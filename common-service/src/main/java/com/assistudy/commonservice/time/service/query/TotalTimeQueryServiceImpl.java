package com.assistudy.commonservice.time.service.query;

import com.assistudy.commonservice.global.client.UserServiceClient;
import com.assistudy.commonservice.global.dto.response.UserInfoResponse;
import com.assistudy.commonservice.room.entity.enums.RoomType;
import com.assistudy.commonservice.room.repository.RoomRepository;
import com.assistudy.commonservice.time.converter.TotalTimeConverter;
import com.assistudy.commonservice.time.dto.response.*;
import com.assistudy.commonservice.time.entity.TotalTime;
import com.assistudy.commonservice.time.exception.TimeErrorCode;
import com.assistudy.commonservice.time.exception.TimeException;
import com.assistudy.commonservice.time.repository.TotalTimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TotalTimeQueryServiceImpl implements TotalTimeQueryService {

    private final TotalTimeRepository totalTimeRepository;
    private final RoomRepository roomRepository;
    private final UserServiceClient userServiceClient;

    @Override
    public List<TotalTimeResponse> getTotalTimeByUserAndRoomAndDate(Long userId, Long roomId, LocalDate date) {
        // 사용자 존재 확인
        UserInfoResponse userInfo = userServiceClient.getUserInfo(userId).getResult();
        if (userInfo == null) {
            throw new TimeException(TimeErrorCode.USER_NOT_FOUND);
        }

        // 방 존재 확인
        if (!roomRepository.existsById(roomId)) {
            throw new TimeException(TimeErrorCode.ROOM_NOT_FOUND);
        }

        // 데이터베이스에서 직접 날짜 조건을 포함하여 조회
        List<TotalTime> totalTimes = totalTimeRepository.findByUserIdAndRoomIdAndDate(userId, roomId, date);

        return totalTimes.stream()
                .map(totalTime -> TotalTimeConverter.toTotalTimeResponse(totalTime, userInfo.getNickname()))
                .toList();
    }

    @Override
    public TotalTimeSummaryResponse getTotalTimeSummaryByUserAndDateAndRoomType(Long userId, LocalDate date, RoomType roomType) {
        // 사용자 존재 확인
        UserInfoResponse userInfo = userServiceClient.getUserInfo(userId).getResult();
        if (userInfo == null) {
            throw new TimeException(TimeErrorCode.USER_NOT_FOUND);
        }

        // 해당 날짜, 방타입의 모든 학습시간 조회
        List<TotalTime> totalTimes = totalTimeRepository.findByUserIdAndDateAndRoomType(userId, date, roomType);

        // tagName별 집계 조회
        List<Object[]> tagSummaryResults = totalTimeRepository.findTagTimeSummaryByUserIdAndDateAndRoomType(userId, date, roomType);

        return TotalTimeConverter.toTotalTimeSummaryResponse(totalTimes, tagSummaryResults, userId, date, roomType);
    }

    @Override
    public StudyGrassResponse getStudyGrassByUserAndYear(Long userId, Integer year) {
        // 사용자 존재 확인
        UserInfoResponse userInfo = userServiceClient.getUserInfo(userId).getResult();
        if (userInfo == null) {
            throw new TimeException(TimeErrorCode.USER_NOT_FOUND);
        }

        // YEAR(t.date) 대신 [yearStart, yearEndExclusive) 범위로 조회 (인덱스 사용 위함)
        LocalDate yearStart = LocalDate.of(year, 1, 1);
        LocalDate yearEndExclusive = LocalDate.of(year + 1, 1, 1);

        // 해당 연도의 일별 focusTime 데이터 조회
        List<Object[]> dailyFocusTimeResults = totalTimeRepository.findDailyFocusTimeByUserIdAndYear(userId, yearStart, yearEndExclusive);

        // 해당 연도의 최대 focusTime 조회
        Integer maxFocusTime = totalTimeRepository.findMaxDailyFocusTimeByUserIdAndYear(userId, yearStart, yearEndExclusive);

        // 1년간의 모든 날짜에 대해 공부 데이터 생성
        List<StudyGrassResponse.DailyStudyData> dailyData = generateYearlyData(year, dailyFocusTimeResults, maxFocusTime);

        return TotalTimeConverter.toStudyGrassResponse(dailyData, maxFocusTime, year);
    }

    @Override
    public StudyRankingResponse getStudyRankingByDate(LocalDate date) {
        // 해당 날짜의 focusTime 상위 6명 조회
        List<Object[]> top6Results = totalTimeRepository.findTop6UsersByFocusTimeOnDate(date);

        if (top6Results.isEmpty()) {
            // 데이터가 없는 경우 빈 랭킹 반환
            return TotalTimeConverter.toStudyRankingResponse(List.of(), date);
        }

        // 상위 6명의 userId 목록 추출
        List<Long> userIds = top6Results.stream()
                .map(result -> ((Number) result[0]).longValue())
                .collect(Collectors.toList());

        // 사용자 정보 일괄 조회
        List<UserInfoResponse> userInfos = userServiceClient.getUsersInfo(userIds).getResult();
        Map<Long, String> userNicknameMap = userInfos.stream()
                .collect(Collectors.toMap(UserInfoResponse::getId, UserInfoResponse::getNickname));

        // 랭킹 데이터 생성
        List<StudyRankingResponse.RankingUser> rankingUsers = generateRankingUsers(top6Results, userNicknameMap);
        return TotalTimeConverter.toStudyRankingResponse(rankingUsers, date);
    }

    @Override
    public List<RoomsByDateResponse> getRoomsByUserAndDate(Long userId, LocalDate date) {
        // 해당 날짜에 사용자가 기록한 방 ID와 이름 조회
        List<Object[]> results = totalTimeRepository.findDistinctRoomsByUserIdAndDate(userId, date);
        return TotalTimeConverter.toRoomsByDateResponseList(results);
    }

    @Override
    public GetResultResponse getResultByRoomIdAndUserIdAndDate(Long roomId, Long userId, LocalDate date) {
        // 해당 사용자의 해당 방, 해당 날짜 TotalTime 조회
        TotalTime totalTime = totalTimeRepository.findByRoomIdAndUserIdAndDate(roomId, userId, date).orElse(null);
        return TotalTimeConverter.toGetResultResponse(totalTime);
    }

    // ================= 내부 비즈니스 로직 메서드 =================

    /**
     * 1년간의 모든 날짜에 대해 공부 데이터를 생성합니다.
     * GitHub 잔디처럼 0-4점으로 점수를 매깁니다.
     */
    private List<StudyGrassResponse.DailyStudyData> generateYearlyData(Integer year, List<Object[]> dailyFocusTimeResults, Integer maxFocusTime) {
        // 일별 데이터를 Map으로 변환 (날짜 -> focusTime)
        Map<LocalDate, Integer> dailyFocusTimeMap = dailyFocusTimeResults.stream()
                .collect(Collectors.toMap(
                        result -> (LocalDate) result[0],
                        result -> ((Number) result[1]).intValue()
                ));

        List<StudyGrassResponse.DailyStudyData> dailyData = new java.util.ArrayList<>();

        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);

        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            Integer focusTime = dailyFocusTimeMap.getOrDefault(currentDate, 0);
            Integer score = calculateScore(focusTime, maxFocusTime);

            dailyData.add(StudyGrassResponse.DailyStudyData.builder()
                    .date(currentDate)
                    .focusTime(focusTime)
                    .score(score)
                    .build());

            currentDate = currentDate.plusDays(1);
        }

        return dailyData;
    }

    /**
     * focusTime을 기반으로 0-4점을 계산합니다.
     * 0분: 0점
     * 1분 이상: 최대 focusTime을 4등분하여 1,2,3,4점 부여
     */
    private Integer calculateScore(Integer focusTime, Integer maxFocusTime) {
        if (focusTime == 0 || maxFocusTime == 0) {
            return 0;
        }

        double quarter = maxFocusTime / 4.0;
        int level = (int) Math.ceil(focusTime / quarter);
        return Math.min(level, 4);
    }

    /**
     * 랭킹 결과와 사용자 정보를 기반으로 랭킹 사용자 목록을 생성합니다.
     */
    private List<StudyRankingResponse.RankingUser> generateRankingUsers(List<Object[]> top6Results, Map<Long, String> userNicknameMap) {
        List<StudyRankingResponse.RankingUser> rankingUsers = new ArrayList<>();
        
        for (int i = 0; i < top6Results.size(); i++) {
            Object[] result = top6Results.get(i);
            Long userId = ((Number) result[0]).longValue();
            Integer focusTime = ((Number) result[1]).intValue();

            String nickname = userNicknameMap.getOrDefault(userId, "Unknown");

            rankingUsers.add(StudyRankingResponse.RankingUser.builder()
                    .userId(userId)
                    .nickname(nickname)
                    .focusTime(focusTime)
                    .rank(i + 1)  // 1부터 시작하는 순위
                    .build());
        }

        return rankingUsers;
    }
}
