package com.assistudy.commonservice.room.service.query;

import com.assistudy.commonservice.room.dto.response.*;

import java.util.List;

public interface RoomQueryService {
	/**
	 * 전체 방 목록을 조회합니다.
	 * @return 방 목록 응답 리스트
	 */
	List<RoomListResponse> getAllRooms();

	/**
	 * 방 상세 정보를 조회합니다.
	 * @param roomId 방 ID
	 * @return 방 상세 정보
	 */
	RoomDetailResponse getRoomDetail(Long roomId);

	/**
	 * 사용자가 참여 중인 스터디 방 목록을 조회합니다.
	 * @param userId 사용자 ID
	 * @return 스터디 방 목록
	 */
	GetMyStudyRoomsResponse getMyStudyRooms(Long userId);

	/**
	 * 사용자가 참가자로 참여 중인 클래스 방 목록을 조회합니다.
	 * @param userId 사용자 ID
	 * @return 클래스 방 목록 (참가자 관점)
	 */
	GetMyClassRoomsAsParticipantResponse getMyClassRoomsAsParticipant(Long userId);

	/**
	 * 사용자가 호스트로 참여 중인 클래스 방 목록을 조회합니다.
	 * @param userId 사용자 ID
	 * @return 클래스 방 목록 (호스트 관점)
	 */
	GetMyClassRoomsAsHostResponse getMyClassRoomsAsHost(Long userId);

	/**
	 * 키워드로 방을 검색합니다.
	 * @param keyword 검색 키워드
	 * @param userId 사용자 ID (참여 여부 판단용)
	 * @return 검색 결과
	 */
	SearchRoomsResponse searchRooms(String keyword, Long userId);

	/**
	 * 추천 방 목록을 조회합니다. (totalTime 대비 focusTime 비율이 높은 방 중 일부)
	 * @param userId 사용자 ID
	 * @return 추천 방 검색 결과
	 */
	SearchRoomsResponse getRecommendedRooms(Long userId);
}
