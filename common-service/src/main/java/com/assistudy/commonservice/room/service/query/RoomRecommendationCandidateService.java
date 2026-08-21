package com.assistudy.commonservice.room.service.query;

import com.assistudy.commonservice.room.dto.cache.RecommendCandidate;
import com.assistudy.commonservice.room.dto.cache.RecommendCandidateList;
import com.assistudy.commonservice.room.entity.Room;
import com.assistudy.commonservice.time.repository.TotalTimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * GET /rooms/recommend의 후보 방 목록(집계 쿼리 결과)만 따로 떼서 캐싱하는 서비스.
 * isJoined/정원 필터링 등 사용자별로 달라지는 부분은 RoomQueryServiceImpl에서 매 요청 새로
 * 계산하고, 여기서는 모든 사용자에게 동일한 "상위 후보 목록" 계산만 담당한다.
 *
 * 별도 빈으로 분리한 이유: @Cacheable은 Spring AOP 프록시를 거쳐야 동작하는데,
 * 같은 클래스 안에서 자기 자신의 메서드를 호출하면(self-invocation) 프록시를 안 거쳐서
 * 캐싱이 적용되지 않는다.
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RoomRecommendationCandidateService {

    private final TotalTimeRepository totalTimeRepository;

    private static final int RECOMMEND_LOOKBACK_DAYS = 30;
    private static final int RECOMMEND_CANDIDATE_LIMIT = 50;

    // 전 사용자 공통 결과라 파라미터가 없고, 키도 고정 문자열 하나만 씀
    @Cacheable(value = "recommend-candidates", key = "'all'")
    public RecommendCandidateList getCandidates() {
        // 최근 30일 totalTime 대비 focusTime 비율이 높은 방 후보를 최대 50개까지만 조회
        List<Room> topRooms = totalTimeRepository.findTopRoomsByFocusRatio(
                LocalDate.now().minusDays(RECOMMEND_LOOKBACK_DAYS),
                PageRequest.of(0, RECOMMEND_CANDIDATE_LIMIT));

        List<RecommendCandidate> candidates = topRooms.stream()
                .map(room -> new RecommendCandidate(
                        room.getId(),
                        room.getHostUserId(),
                        room.getName(),
                        room.getType(),
                        room.getTagName(),
                        room.getDescription(),
                        room.getIsPrivate(),
                        room.getMicActive(),
                        room.getMaxParticipants(),
                        room.getCreatedAt()))
                .toList();
        return new RecommendCandidateList(candidates);
    }
}
