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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OneMinuteAggregationService {

    private final CommonServiceClient commonServiceClient;

    private final Map<String, List<OnDeviceLogDto>> oneMinuteLogs = new ConcurrentHashMap<>();

    public void collectLog(OnDeviceLogDto logDto) {
        String key = logDto.getUserId() + "-" + logDto.getRoomId();
        oneMinuteLogs.computeIfAbsent(key, k -> new ArrayList<>()).add(logDto);
        log.debug("Log collected for key={}, total={}", key, oneMinuteLogs.get(key).size());
    }

    @Scheduled(fixedRate = 60000)
    public void processOneMinuteLogs() {
        if (oneMinuteLogs.isEmpty()) {
            return;
        }

        log.info("Starting 1-minute log aggregation");
        Map<String, List<OnDeviceLogDto>> logsToProcess = new HashMap<>(oneMinuteLogs);
        oneMinuteLogs.clear();

        logsToProcess.forEach((key, logs) -> {
            try {
                log.info("Processing logs for key={}, count={}", key, logs.size());
                processUserRoomLogs(logs);
            } catch (Exception e) {
                log.error("Failed to process logs for key={}", key, e);
            }
        });

        log.info("1-minute log aggregation completed");
    }

    private void processUserRoomLogs(List<OnDeviceLogDto> logs) {
        if (logs.isEmpty()) return;

        OnDeviceLogDto firstLog = logs.get(0);
        OneMinuteAnalysisResult analysis = analyzeOneMinuteData(logs);
        saveFocusScore(firstLog.getUserId(), firstLog.getRoomId(), analysis);
    }

    private OneMinuteAnalysisResult analyzeOneMinuteData(List<OnDeviceLogDto> logs) {
        List<Double> focusScores = logs.stream()
            .filter(l -> l.getFocusScore() != null)
            .map(OnDeviceLogDto::getFocusScore)
            .collect(Collectors.toList());

        if (focusScores.isEmpty()) {
            return OneMinuteAnalysisResult.builder()
                .averageScore(0)
                .evaluationText("1분간 집중도 데이터가 측정되지 않았습니다.")
                .confidence(0.0)
                .yawnCount(0).gazeDistractionCount(0).headTurnCount(0)
                .totalLogs(logs.size())
                .build();
        }

        double avgScore = focusScores.stream().mapToDouble(d -> d).average().orElse(0.0);
        double maxScore = focusScores.stream().mapToDouble(d -> d).max().orElse(0.0);
        double minScore = focusScores.stream().mapToDouble(d -> d).min().orElse(0.0);

        long yawnCount = logs.stream().filter(l -> "YAWN".equals(l.getLogType())).count();
        long gazeDistractionCount = logs.stream().filter(l -> "GAZE_DISTRACTION".equals(l.getLogType())).count();
        long headTurnCount = logs.stream().filter(l -> "HEAD_TURN".equals(l.getLogType())).count();

        double confidence = calculateConfidence(logs.size(), focusScores.size());
        String evaluationText = generateEvaluation(avgScore, maxScore, minScore,
            yawnCount, gazeDistractionCount, headTurnCount, logs.size());

        return OneMinuteAnalysisResult.builder()
            .averageScore((int) Math.round(avgScore))
            .evaluationText(evaluationText)
            .confidence(confidence)
            .yawnCount((int) yawnCount)
            .gazeDistractionCount((int) gazeDistractionCount)
            .headTurnCount((int) headTurnCount)
            .totalLogs(logs.size())
            .build();
    }

    private String generateEvaluation(double avgScore, double maxScore, double minScore,
                                      long yawnCount, long gazeDistractionCount, long headTurnCount,
                                      int totalLogs) {
        StringBuilder sb = new StringBuilder();

        if (avgScore >= 80) {
            sb.append("매우 우수한 집중력을 보였습니다. ");
        } else if (avgScore >= 70) {
            sb.append("양호한 집중 상태를 유지했습니다. ");
        } else if (avgScore >= 50) {
            sb.append("보통 수준의 집중력을 보였습니다. ");
        } else if (avgScore >= 30) {
            sb.append("집중력이 다소 부족했습니다. ");
        } else {
            sb.append("집중력이 매우 낮았습니다. ");
        }

        sb.append(String.format("평균 집중도 %.1f점. ", avgScore));

        List<String> issues = new ArrayList<>();
        List<String> positives = new ArrayList<>();

        if (yawnCount > 0) issues.add(String.format("하품 %d회", yawnCount));
        if (gazeDistractionCount > 2) issues.add(String.format("시선 이탈 %d회", gazeDistractionCount));
        else if (gazeDistractionCount <= 1) positives.add("시선 집중 우수");
        if (headTurnCount > 3) issues.add(String.format("고개 돌림 %d회", headTurnCount));
        if ((maxScore - minScore) > 30) issues.add("집중도 변동 심함");
        else if ((maxScore - minScore) <= 15) positives.add("집중도 안정적");
        if (totalLogs < 6) issues.add("측정 데이터 부족");

        if (!positives.isEmpty()) sb.append("✅ ").append(String.join(", ", positives)).append(". ");
        if (!issues.isEmpty()) sb.append("⚠️ 개선점: ").append(String.join(", ", issues)).append(". ");

        if (avgScore >= 80 && issues.isEmpty()) sb.append("현재 상태를 유지하세요!");
        else if (avgScore >= 60) sb.append("조금 더 집중하면 더 좋은 결과를 얻을 수 있습니다.");
        else sb.append("휴식을 취하거나 학습 환경을 점검해보세요.");

        return sb.toString();
    }

    private double calculateConfidence(int totalLogs, int validScoreLogs) {
        if (totalLogs == 0) return 0.0;
        double base = (double) validScoreLogs / totalLogs;
        if (totalLogs >= 10) return Math.min(0.95, base + 0.1);
        if (totalLogs >= 5) return Math.min(0.85, base + 0.05);
        return base;
    }

    private void saveFocusScore(Long userId, Long roomId, OneMinuteAnalysisResult analysis) {
        try {
            CreateFocusScoreRequest request = CreateFocusScoreRequest.builder()
                .userId(userId)
                .roomId(roomId)
                .score(analysis.getAverageScore())
                .endTime(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                .evaluationText(analysis.getEvaluationText())
                .build();

            FocusScoreResponse response = commonServiceClient.createFocusScore(request);
            log.info("Focus score saved: id={}, user={}, room={}, score={}",
                response.getId(), userId, roomId, analysis.getAverageScore());
            log.info("Evaluation: {}", analysis.getEvaluationText());
        } catch (feign.FeignException e) {
            log.error("Feign error saving focus score: status={}, body={}",
                e.status(), e.contentUTF8());
        } catch (Exception e) {
            log.error("Failed to save focus score for user={}, room={}", userId, roomId, e);
        }
    }

    @lombok.Data
    @lombok.Builder
    public static class OneMinuteAnalysisResult {
        private Integer averageScore;
        private String evaluationText;
        private Double confidence;
        private Integer yawnCount;
        private Integer gazeDistractionCount;
        private Integer headTurnCount;
        private Integer totalLogs;
    }
}
