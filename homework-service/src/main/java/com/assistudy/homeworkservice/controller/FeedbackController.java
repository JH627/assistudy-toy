package com.assistudy.homeworkservice.controller;

import com.assistudy.shared.annotation.LoginUser;
import com.assistudy.shared.response.ApiResponse;
import com.assistudy.homeworkservice.doc.FeedbackApiDocumentation;
import com.assistudy.homeworkservice.dto.request.CreateFeedbackRequest;
import com.assistudy.homeworkservice.dto.request.DeleteFeedbackRequest;
import com.assistudy.homeworkservice.dto.request.GetFeedbackListRequest;
import com.assistudy.homeworkservice.dto.request.UpdateFeedbackByHostRequest;
import com.assistudy.homeworkservice.dto.response.CreateFeedbackResponse;
import com.assistudy.homeworkservice.dto.response.HostFeedbackResponse;
import com.assistudy.homeworkservice.service.command.FeedbackCommandService;
import com.assistudy.homeworkservice.service.query.FeedbackQueryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
@Tag(name = "Feedback", description = "피드백 관리 API")
public class FeedbackController {

    private final FeedbackQueryService feedbackQueryService;
    private final FeedbackCommandService feedbackCommandService;

    // 날짜별 사용자 피드백 조회
    @GetMapping("/rooms/{roomId}/feedback")
    @FeedbackApiDocumentation.GetFeedbacksByRoomAndDateAndUserApi
    public ApiResponse<List<CreateFeedbackResponse>> getFeedbacksByRoomAndDateAndUser(
        @LoginUser Long userId,
        @PathVariable Long roomId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<CreateFeedbackResponse> feedbacks = feedbackQueryService.getFeedbacksByRoomAndDateAndUser(roomId, date, userId);
        return ApiResponse.onSuccess(feedbacks);
    }

    // 피드백 작성
    @PostMapping("/feedback")
    @FeedbackApiDocumentation.CreateFeedbackApi
    public ApiResponse<CreateFeedbackResponse> createFeedback(@LoginUser Long userId, @Valid @RequestBody CreateFeedbackRequest request) {
        CreateFeedbackResponse response = feedbackCommandService.createFeedback(request, userId);
        return ApiResponse.onSuccess(response);
    }

    // 피드백 삭제
    @DeleteMapping("/feedback")
    @FeedbackApiDocumentation.DeleteFeedbackApi
    public ApiResponse<Void> deleteFeedback(@LoginUser Long userId, @Valid @RequestBody DeleteFeedbackRequest request) {
        feedbackCommandService.deleteFeedback(request.getFeedbackId(), userId);
        return ApiResponse.onSuccess();
    }

    // 사용자별 피드백 조회
    @GetMapping("/rooms/{roomId}/feedback/user")
    @FeedbackApiDocumentation.GetFeedbacksByRoomAndUserApi
    public ApiResponse<List<CreateFeedbackResponse>> getFeedbacksByRoomAndUser(@LoginUser Long userId, @PathVariable Long roomId) {
        List<CreateFeedbackResponse> feedbacks = feedbackQueryService.getFeedbacksByRoomAndUser(roomId, userId);
        return ApiResponse.onSuccess(feedbacks);
    }

    // 날짜별 모든 피드백 조회
    @GetMapping("/rooms/{roomId}/feedback/date")
    @FeedbackApiDocumentation.GetFeedbacksByRoomAndDateApi
    public ApiResponse<List<CreateFeedbackResponse>> getFeedbacksByRoomAndDate(
        @LoginUser Long userId,
        @PathVariable Long roomId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<CreateFeedbackResponse> feedbacks = feedbackQueryService.getFeedbacksByRoomAndDate(roomId, date);
        return ApiResponse.onSuccess(feedbacks);
    }

    // 피드백 목록 조회
    @PostMapping("/feedback/list")
    @FeedbackApiDocumentation.GetFeedbackListApi
    public ApiResponse<List<CreateFeedbackResponse>> getFeedbacks(@Valid @RequestBody GetFeedbackListRequest request) {
        List<CreateFeedbackResponse> feedbacks = feedbackQueryService.getFeedbacksByHomework(request.getHomeworkId());
        return ApiResponse.onSuccess(feedbacks);
    }

    // 호스트가 특정 날짜의 피드백 현황 조회
    @GetMapping("/feedback/host")
    @FeedbackApiDocumentation.GetHostFeedbackByRoomAndDateApi
    public ApiResponse<HostFeedbackResponse> getHostFeedbackByRoomAndDate(
        @LoginUser Long userId,
        @RequestParam Long roomId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        HostFeedbackResponse response = feedbackQueryService.getHostFeedbackByRoomAndDate(roomId, date, userId);
        return ApiResponse.onSuccess(response);
    }

    // 호스트가 피드백 수정
    @PutMapping("/feedback/host")
    @FeedbackApiDocumentation.UpdateFeedbackByHostApi
    public ApiResponse<Void> updateFeedbackByHost(@LoginUser Long userId, @Valid @RequestBody UpdateFeedbackByHostRequest request) {
        feedbackCommandService.updateFeedbackByHost(request, userId);
        return ApiResponse.onSuccess();
    }
}
