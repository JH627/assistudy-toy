package com.assistudy.commonservice.analysis.controller;

import com.assistudy.commonservice.analysis.doc.AnalysisApiDocumentation;
import com.assistudy.commonservice.analysis.dto.response.FocusScoreResponse;
import com.assistudy.commonservice.analysis.service.query.FocusScoreQueryService;
import com.assistudy.shared.annotation.LoginUser;
import com.assistudy.commonservice.global.annotation.VerifiedUser;
import com.assistudy.shared.response.ApiResponse;
import com.assistudy.commonservice.time.dto.request.GetAnalysisResultRequest;
import com.assistudy.commonservice.time.dto.response.GetAnalysisResultResponse;
import com.assistudy.commonservice.time.service.command.TotalTimeCommandService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/analysis")
@RequiredArgsConstructor
@Tag(name = "Analysis", description = "집중도 분석 관리 API")
public class AnalysisController {

    private final FocusScoreQueryService focusScoreQueryService;
    private final TotalTimeCommandService totalTimeCommandService;

    @GetMapping("/my/focus-scores/date")
    @AnalysisApiDocumentation.GetMyFocusScoresByDateApi
    public ApiResponse<List<FocusScoreResponse>> getMyFocusScoresByDate(@LoginUser Long userId, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<FocusScoreResponse> focusScores = focusScoreQueryService.getFocusScoresByUserAndDate(userId, date);
        return ApiResponse.onSuccess(focusScores);
    }

    @GetMapping("/my/focus-scores/date-room")
    @AnalysisApiDocumentation.GetMyFocusScoresByDateAndRoomApi
    public ApiResponse<List<FocusScoreResponse>> getMyFocusScoresByDateAndRoom(@LoginUser Long userId, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, @RequestParam Long roomId) {
        List<FocusScoreResponse> focusScores = focusScoreQueryService.getFocusScoresByUserAndRoomAndDate(userId, roomId, date);
        return ApiResponse.onSuccess(focusScores);
    }

    @GetMapping("/analysis-results/rooms/{roomId}/dates/{date}")
    public ApiResponse<GetAnalysisResultResponse> getAnalysisResult(
            @VerifiedUser Long userId,
            @PathVariable Long roomId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        GetAnalysisResultRequest request = GetAnalysisResultRequest.builder()
                .roomId(roomId)
                .date(date)
                .build();

        GetAnalysisResultResponse response = totalTimeCommandService.getOrGenerateResult(userId, request);
        return ApiResponse.onSuccess(response);
    }

}