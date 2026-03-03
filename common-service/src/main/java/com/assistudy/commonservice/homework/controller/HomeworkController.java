package com.assistudy.commonservice.homework.controller;

import com.assistudy.commonservice.global.annotation.LoginUser;
import com.assistudy.commonservice.global.dto.response.ApiResponse;
import com.assistudy.commonservice.homework.doc.HomeworkApiDocumentation;
import com.assistudy.commonservice.homework.dto.request.CreateHomeworkRequest;
import com.assistudy.commonservice.homework.dto.request.UpdateHomeworkRequest;
import com.assistudy.commonservice.homework.dto.response.CreateHomeworkResponse;
import com.assistudy.commonservice.homework.dto.response.GetHomeworksByRoomAndDateResponse;
import com.assistudy.commonservice.homework.dto.response.UserParticipatedRoomsWithHomeworkResponse;
import com.assistudy.commonservice.homework.service.command.HomeworkCommandService;
import com.assistudy.commonservice.homework.service.query.HomeworkQueryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
@Tag(name = "Homework", description = "과제 관리 API")
public class HomeworkController {

    private final HomeworkQueryService homeworkQueryService;
    private final HomeworkCommandService homeworkCommandService;

    // 방별 날짜별 과제 조회 (한 방에 한 날짜에 여러 과제가 있을 수 있음)
    @GetMapping("/homeworks/search")
    @HomeworkApiDocumentation.GetHomeworksByRoomAndDateApi
    public ApiResponse<GetHomeworksByRoomAndDateResponse> getHomeworksByRoomAndDate(
        @LoginUser Long userId, @RequestParam Long roomId, @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        GetHomeworksByRoomAndDateResponse homework = homeworkQueryService.getHomeworksByRoomAndDate(roomId, date, userId);
        return ApiResponse.onSuccess(homework);
    }

    // 과제 생성 (한 방에 한 날짜에 여러 과제가 있을 수 있음)
    @PutMapping("/homeworks")
    @HomeworkApiDocumentation.CreateHomeworkApi
    public ApiResponse<CreateHomeworkResponse> createHomework(@LoginUser Long userId, @Valid @RequestBody CreateHomeworkRequest request) {
        CreateHomeworkResponse response = homeworkCommandService.createHomework(request, userId);
        return ApiResponse.onSuccess(response);
    }

    // 사용자가 참여했던 방들의 과제 목록 조회
    @GetMapping("/homeworks/my-participated-rooms")
    @HomeworkApiDocumentation.GetMyParticipatedRoomsWithHomeworkApi
    public ApiResponse<UserParticipatedRoomsWithHomeworkResponse> getMyParticipatedRoomsWithHomework(@LoginUser Long userId) {
        UserParticipatedRoomsWithHomeworkResponse response = homeworkQueryService.getUserParticipatedRoomsWithHomework(userId);
        return ApiResponse.onSuccess(response);
    }

    // 과제 내용 수정 (날짜는 변경 불가)
    @PutMapping("/homeworks/{homeworkId}")
    @HomeworkApiDocumentation.UpdateHomeworkApi
    public ApiResponse<CreateHomeworkResponse> updateHomework(@PathVariable Long homeworkId, @Valid @RequestBody UpdateHomeworkRequest request, @LoginUser Long userId) {
        CreateHomeworkResponse response = homeworkCommandService.updateHomework(homeworkId, request, userId);
        return ApiResponse.onSuccess(response);
    }
}