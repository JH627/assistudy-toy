package com.assistudy.commonservice.time.service.command;

import com.assistudy.commonservice.analysis.entity.FocusScore;
import com.assistudy.commonservice.analysis.repository.FocusScoreRepository;
import com.assistudy.commonservice.gms.dto.response.GmsChatResponse;
import com.assistudy.commonservice.gms.service.GmsService;
import com.assistudy.commonservice.room.entity.Room;
import com.assistudy.commonservice.room.repository.RoomRepository;
import com.assistudy.commonservice.time.converter.TotalTimeConverter;
import com.assistudy.commonservice.time.dto.request.GetAnalysisResultRequest;
import com.assistudy.commonservice.time.dto.response.GetAnalysisResultResponse;
import com.assistudy.commonservice.time.entity.TotalTime;
import com.assistudy.commonservice.time.exception.TimeErrorCode;
import com.assistudy.commonservice.time.exception.TimeException;
import com.assistudy.commonservice.time.repository.TotalTimeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TotalTimeCommandServiceImpl implements TotalTimeCommandService {

    private final TotalTimeRepository totalTimeRepository;
    private final RoomRepository roomRepository;
    private final FocusScoreRepository focusScoreRepository;
    private final GmsService gmsService;

    @Override
    public void updateOrCreateTotalTime(Long userId, Long roomId, LocalDate date, Integer additionalTotalTime, Integer additionalFocusTime) {
        // 기존 TotalTime 데이터 조회
        TotalTime existingTotalTime = totalTimeRepository.findByRoomIdAndUserIdAndDate(roomId, userId, date).orElse(null);

        if (existingTotalTime != null) {
            // 기존 데이터가 있으면 업데이트
            existingTotalTime.updateTime(additionalTotalTime, additionalFocusTime);
            totalTimeRepository.save(existingTotalTime);
        }
        else {
            // 기존 데이터가 없으면 새로 생성
            Room room = roomRepository.findById(roomId)
                    .orElseThrow(() -> new TimeException(TimeErrorCode.ROOM_NOT_FOUND));

            TotalTime newTotalTime = TotalTime.builder()
                    .room(room)
                    .userId(userId)
                    .date(date)
                    .totalTime(additionalTotalTime)
                    .focusTime(additionalFocusTime)
                    .build();

            totalTimeRepository.save(newTotalTime);
        }
    }

    @Override
    public GetAnalysisResultResponse getOrGenerateResult(Long userId, GetAnalysisResultRequest request) {
        // 해당 사용자의 해당 방, 해당 날짜 TotalTime 조회
        TotalTime totalTime = totalTimeRepository.findByRoomIdAndUserIdAndDate(
                request.getRoomId(), userId, request.getDate()).orElse(null);

        if (totalTime == null) {
            throw new TimeException(TimeErrorCode.TOTAL_TIME_NOT_FOUND);
        }

        // 1시간 이내에 업데이트된 결과가 있는지 확인
        if (totalTime.getResult() != null && totalTime.getUpdatedAt() != null) {
            LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
            if (totalTime.getUpdatedAt().isAfter(oneHourAgo)) {
                return TotalTimeConverter.toGetAnalysisResultResponse(totalTime, true);
            }
        }

        // 해당 날짜의 FocusScore 데이터들을 조회하여 GMS에 전송할 프롬프트 생성
        List<FocusScore> focusScores = focusScoreRepository.findByUserIdAndRoomIdAndEndTimeDate(
                userId, request.getRoomId(), request.getDate());

        if (focusScores.isEmpty()) {
            throw new TimeException(TimeErrorCode.FOCUS_SCORE_NOT_FOUND);
        }

        // GMS에 전송할 프롬프트 생성
        String prompt = generateAnalysisPrompt(focusScores);
        String systemPrompt = "당신은 학습 분석 전문가입니다. 주어진 집중도 데이터를 바탕으로 하루 학습을 종합적으로 분석하고 개선점을 제시해주세요. 한국어로 답변해주세요.";

        try {
            GmsChatResponse gmsResponse = gmsService.sendChatMessage(prompt, systemPrompt);
            String analysisResult = gmsResponse.getResponse();

            totalTime.updateResult(analysisResult);
            totalTimeRepository.save(totalTime);

            return TotalTimeConverter.toGetAnalysisResultResponse(totalTime, false);
        } catch (Exception e) {
            throw new TimeException(TimeErrorCode.ANALYSIS_GENERATION_FAILED);
        }
    }

    /**
     * FocusScore 데이터를 기반으로 GMS에 전송할 프롬프트를 생성합니다.
     */
    private String generateAnalysisPrompt(List<FocusScore> focusScores) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("다음은 하루 동안의 집중도 데이터입니다. 각 시간대별 집중도 점수와 평가를 바탕으로 종합적인 학습 분석을 제공해주세요.\n\n");

        for (FocusScore focusScore : focusScores) {
            prompt.append("시간: ").append(focusScore.getEndTime()).append("\n");
            prompt.append("집중도 점수: ").append(focusScore.getScore()).append("/100\n");
            if (focusScore.getEvaluationText() != null && !focusScore.getEvaluationText().isEmpty()) {
                prompt.append("평가: ").append(focusScore.getEvaluationText()).append("\n");
            }
            prompt.append("---\n");
        }

        prompt.append("\n위 데이터를 바탕으로 다음을 분석해주세요:\n");
        prompt.append("1. 전반적인 집중도 패턴\n");
        prompt.append("2. 집중도가 높았던/낮았던 시간대 분석\n");
        prompt.append("3. 개선점 및 권장사항\n");
        prompt.append("4. 다음 학습 세션을 위한 조언\n");

        return prompt.toString();
    }

}
