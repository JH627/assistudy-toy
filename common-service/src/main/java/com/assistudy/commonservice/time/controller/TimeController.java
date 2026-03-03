package com.assistudy.commonservice.time.controller;

import com.assistudy.commonservice.global.annotation.LoginUser;
import com.assistudy.commonservice.global.dto.response.ApiResponse;
import com.assistudy.commonservice.room.entity.enums.RoomType;
import com.assistudy.commonservice.time.doc.TimeApiDocumentation;
import com.assistudy.commonservice.time.dto.response.RoomsByDateResponse;
import com.assistudy.commonservice.time.dto.response.GetResultResponse;
import com.assistudy.commonservice.time.dto.response.StudyGrassResponse;
import com.assistudy.commonservice.time.dto.response.StudyRankingResponse;
import com.assistudy.commonservice.time.dto.response.TotalTimeResponse;
import com.assistudy.commonservice.time.dto.response.TotalTimeSummaryResponse;
import com.assistudy.commonservice.time.service.query.TotalTimeQueryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;

@RestController
@RequestMapping("/time")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "학습시간 관리", description = "학습시간 기록 및 조회 API")
public class TimeController {

    private final TotalTimeQueryService totalTimeQueryService;

    // 사용자 - 방 특정 날짜 학습시간 조회
    @GetMapping("/room/{roomId}")
    @TimeApiDocumentation.GetTotalTimeByUserAndRoomAndDateApi
    public ApiResponse<List<TotalTimeResponse>> getTotalTimeByUserAndRoomAndDate(@LoginUser Long userId, @PathVariable Long roomId, @RequestParam LocalDate date) {
        List<TotalTimeResponse> responses = totalTimeQueryService.getTotalTimeByUserAndRoomAndDate(userId, roomId, date);
        return ApiResponse.onSuccess(responses);
    }

    // 사용자 특정 날짜, 특정 방타입 학습시간 요약 조회 (총합계 + tagName별 상세)
    @GetMapping("/total/summary")
    @TimeApiDocumentation.GetTotalTimeSummaryByUserAndDateAndRoomTypeApi
    public ApiResponse<TotalTimeSummaryResponse> getTotalTimeSummaryByUserAndDateAndRoomType(
        @LoginUser Long userId,
        @RequestParam(required = true) RoomType roomType,
        @RequestParam(required = false) LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        TotalTimeSummaryResponse response = totalTimeQueryService.getTotalTimeSummaryByUserAndDateAndRoomType(userId, date, roomType);
        return ApiResponse.onSuccess(response);
    }

    // 사용자 특정 연도 공부 잔디 조회
    @GetMapping("/grass")
    @TimeApiDocumentation.GetStudyGrassByUserAndYearApi
    public ApiResponse<StudyGrassResponse> getStudyGrassByUserAndYear(@LoginUser Long userId, @RequestParam(required = false) Integer year) {
        if (year == null) {
            year = Year.now().getValue();
        }
        StudyGrassResponse response = totalTimeQueryService.getStudyGrassByUserAndYear(userId, year);
        return ApiResponse.onSuccess(response);
    }

    // 특정 날짜 기준 순공 시간 상위 6명 랭킹 조회
    @GetMapping("/ranking")
    @TimeApiDocumentation.GetStudyRankingByDateApi
    public ApiResponse<StudyRankingResponse> getStudyRankingByDate(@RequestParam(required = false) LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        StudyRankingResponse response = totalTimeQueryService.getStudyRankingByDate(date);
        return ApiResponse.onSuccess(response);
    }

    // 특정 날짜에 기록이 있는 방 목록 조회
    @GetMapping("/rooms")
    public ApiResponse<List<RoomsByDateResponse>> getRoomsByUserAndDate(@LoginUser Long userId, @RequestParam LocalDate date) {
        List<RoomsByDateResponse> response = totalTimeQueryService.getRoomsByUserAndDate(userId, date);
        return ApiResponse.onSuccess(response);
    }

    // roomId, userId, date로 result 조회
    @GetMapping("/result")
    @TimeApiDocumentation.GetResultByRoomIdAndUserIdAndDateApi
    public ApiResponse<GetResultResponse> getResultByRoomIdAndUserIdAndDate(@RequestParam Long roomId, @LoginUser Long userId, @RequestParam LocalDate date) {
        GetResultResponse response = totalTimeQueryService.getResultByRoomIdAndUserIdAndDate(roomId, userId, date);
        return ApiResponse.onSuccess(response);
    }
}