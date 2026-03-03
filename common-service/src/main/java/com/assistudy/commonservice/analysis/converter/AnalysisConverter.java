package com.assistudy.commonservice.analysis.converter;

import com.assistudy.commonservice.analysis.entity.AnalysisResult;
import com.assistudy.commonservice.analysis.entity.FocusScore;
import com.assistudy.commonservice.analysis.dto.response.AnalysisResultResponse;
import com.assistudy.commonservice.analysis.dto.response.FocusScoreResponse;

public class AnalysisConverter {

    public static AnalysisResultResponse toAnalysisResultResponse(AnalysisResult result) {
        return AnalysisResultResponse.builder()
                .id(result.getId())
                .userId(result.getUserId())
                .roomId(result.getRoom().getId())
                .analysisType(result.getAnalysisType())
                .analysisResult(result.getAnalysisResult())
                .confidence(result.getConfidence())
                .metadata(result.getMetadata())
                .createdAt(result.getCreatedAt())
                .build();
    }

    public static FocusScoreResponse toFocusScoreResponseWithNickname(FocusScore focusScore, String userNickname) {
        return FocusScoreResponse.builder()
                .id(focusScore.getId())
                .roomId(focusScore.getRoom().getId())
                .roomName(focusScore.getRoom().getName())
                .userId(focusScore.getUserId())
                .userNickname(userNickname)
                .date(focusScore.getDate())
                .endTime(focusScore.getEndTime())
                .score(focusScore.getScore())
                .evaluationText(focusScore.getEvaluationText())
                .build();
    }
} 