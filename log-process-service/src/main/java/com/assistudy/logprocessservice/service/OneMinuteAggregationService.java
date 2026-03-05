package com.assistudy.logprocessservice.service;

import com.assistudy.logprocessservice.client.CommonServiceClient;
import com.assistudy.logprocessservice.dto.OnDeviceLogDto;
import com.assistudy.logprocessservice.dto.request.CreateFocusScoreRequest;
import com.assistudy.logprocessservice.dto.response.FocusScoreResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class OneMinuteAggregationService {

    private final CommonServiceClient commonServiceClient;

    // List<OnDeviceLogDto> 대신 집계 상태만 유지 → 메모리 O(유저-룸 쌍 수)
    private final Map<String, AggregateState> oneMinuteAggregates = new ConcurrentHashMap<>();

    public void collectLog(OnDeviceLogDto logDto, boolean isFocusLog) {
        String key = logDto.getUserId() + "_" + logDto.getRoomId();
        // compute는 원자적으로 실행되어 thread-safe
        oneMinuteAggregates.compute(key, (k, state) -> {
            if (state == null) state = new AggregateState(logDto.getUserId(), logDto.getRoomId());
            state.update(logDto, isFocusLog);
            return state;
        });
        log.debug("Log collected for key={}", key);
    }

    @Scheduled(fixedRate = 60000)
    public void processOneMinuteLogs() {
        if (oneMinuteAggregates.isEmpty()) {
            return;
        }

        log.info("Starting 1-minute log aggregation");

        // 복사본 생성 없이 키별로 drain → 피크 시 메모리 2배 문제 해소
        for (String key : new HashSet<>(oneMinuteAggregates.keySet())) {
            AggregateState state = oneMinuteAggregates.remove(key);
            if (state == null) continue;
            try {
                log.info("Processing aggregated logs for key={}, focusLogs={}, totalLogs={}",
                    key, state.focusLogCount, state.totalLogs);
                processAggregateState(state);
            } catch (Exception e) {
                log.error("Failed to process aggregated logs for key={}", key, e);
            }
        }

        log.info("1-minute log aggregation completed");
    }

    private void processAggregateState(AggregateState state) {
        if (state.focusLogCount == 0) return;

        int averageScore = (int) Math.round(state.scoreSum / state.focusLogCount);
        String evaluationText = generateEvaluation(state, averageScore);

        log.info("user={}, room={}, avgScore={}, yawn={}, gaze={}, head={}",
            state.userId, state.roomId, averageScore,
            state.yawnCount, state.gazeDistractionCount, state.headTurnCount);

        saveFocusScore(state.userId, state.roomId, averageScore, evaluationText);
    }

    private String generateEvaluation(AggregateState state, int averageScore) {
        double maxScore = state.scoreMax;
        double minScore = state.scoreMin == Double.MAX_VALUE ? 0 : state.scoreMin;

        StringBuilder sb = new StringBuilder();

        if (averageScore >= 80) sb.append("매우 우수한 집중력을 보였습니다. ");
        else if (averageScore >= 70) sb.append("양호한 집중 상태를 유지했습니다. ");
        else if (averageScore >= 50) sb.append("보통 수준의 집중력을 보였습니다. ");
        else if (averageScore >= 30) sb.append("집중력이 다소 부족했습니다. ");
        else sb.append("집중력이 매우 낮았습니다. ");

        sb.append(String.format("평균 집중도 %d점. ", averageScore));

        List<String> issues = new ArrayList<>();
        List<String> positives = new ArrayList<>();

        if (state.yawnCount > 0) issues.add(String.format("하품 %d회", state.yawnCount));
        if (state.gazeDistractionCount > 2) issues.add(String.format("시선 이탈 %d회", state.gazeDistractionCount));
        else if (state.gazeDistractionCount <= 1) positives.add("시선 집중 우수");
        if (state.headTurnCount > 3) issues.add(String.format("고개 돌림 %d회", state.headTurnCount));
        if ((maxScore - minScore) > 30) issues.add("집중도 변동 심함");
        else if ((maxScore - minScore) <= 15 && state.focusLogCount > 1) positives.add("집중도 안정적");
        if (state.focusLogCount < 6) issues.add("측정 데이터 부족");

        if (!positives.isEmpty()) sb.append("✅ ").append(String.join(", ", positives)).append(". ");
        if (!issues.isEmpty()) sb.append("⚠️ 개선점: ").append(String.join(", ", issues)).append(". ");

        if (averageScore >= 80 && issues.isEmpty()) sb.append("현재 상태를 유지하세요!");
        else if (averageScore >= 60) sb.append("조금 더 집중하면 더 좋은 결과를 얻을 수 있습니다.");
        else sb.append("휴식을 취하거나 학습 환경을 점검해보세요.");

        return sb.toString();
    }

    private void saveFocusScore(Long userId, Long roomId, int averageScore, String evaluationText) {
        try {
            CreateFocusScoreRequest request = CreateFocusScoreRequest.builder()
                .userId(userId)
                .roomId(roomId)
                .score(averageScore)
                .endTime(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                .evaluationText(evaluationText)
                .build();

            FocusScoreResponse response = commonServiceClient.createFocusScore(request);
            log.info("Focus score saved: id={}, user={}, room={}, score={}",
                response.getId(), userId, roomId, averageScore);
        } catch (feign.FeignException e) {
            log.error("Feign error saving focus score: status={}, body={}", e.status(), e.contentUTF8());
        } catch (Exception e) {
            log.error("Failed to save focus score for user={}, room={}", userId, roomId, e);
        }
    }

    // 로그 객체 전체 대신 집계값만 유지
    static class AggregateState {
        final Long userId;
        final Long roomId;

        double scoreSum = 0;
        double scoreMax = Double.MIN_VALUE;
        double scoreMin = Double.MAX_VALUE;
        int focusLogCount = 0;
        int yawnCount = 0;
        int gazeDistractionCount = 0;
        int headTurnCount = 0;
        int totalLogs = 0;

        AggregateState(Long userId, Long roomId) {
            this.userId = userId;
            this.roomId = roomId;
        }

        void update(OnDeviceLogDto log, boolean isFocusLog) {
            totalLogs++;
            if (isFocusLog && log.getFocusScore() != null) {
                double score = log.getFocusScore();
                scoreSum += score;
                focusLogCount++;
                if (score > scoreMax) scoreMax = score;
                if (score < scoreMin) scoreMin = score;
            }
            String logType = log.getLogType();
            if ("YAWN".equals(logType)) yawnCount++;
            else if ("GAZE_DISTRACTION".equals(logType)) gazeDistractionCount++;
            else if ("HEAD_TURN".equals(logType)) headTurnCount++;
        }
    }
}
