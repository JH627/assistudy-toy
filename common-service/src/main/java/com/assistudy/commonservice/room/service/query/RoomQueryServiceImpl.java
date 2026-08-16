package com.assistudy.commonservice.room.service.query;

import com.assistudy.commonservice.global.client.UserServiceClient;
import com.assistudy.commonservice.global.dto.response.UserInfoResponse;
import com.assistudy.commonservice.room.converter.RoomConverter;
import com.assistudy.commonservice.room.dto.response.*;
import com.assistudy.commonservice.room.entity.Room;
import com.assistudy.commonservice.room.entity.RoomParticipant;
import com.assistudy.commonservice.room.entity.enums.RoomType;
import com.assistudy.commonservice.room.exception.RoomErrorCode;
import com.assistudy.commonservice.room.exception.RoomException;
import com.assistudy.commonservice.room.repository.RoomParticipantRepository;
import com.assistudy.commonservice.room.repository.RoomRepository;
import com.assistudy.commonservice.time.repository.TotalTimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RoomQueryServiceImpl implements RoomQueryService {

	private final RoomRepository roomRepository;
	private final RoomParticipantRepository roomParticipantRepository;
	private final UserServiceClient userServiceClient;
	private final TotalTimeRepository totalTimeRepository;

	private static final int RECOMMEND_LOOKBACK_DAYS = 30;
	private static final int RECOMMEND_CANDIDATE_LIMIT = 50;
	private static final int RECOMMEND_MAX_AVAILABLE = 10;
	private static final int RECOMMEND_RESULT_COUNT = 4;

	@Override
	public List<RoomListResponse> getAllRooms() {
		// 삭제되지 않은 방 조회
		List<Room> rooms = roomRepository.findByIsDeletedFalse();

		if (rooms.isEmpty()) {
			return List.of();
		}

		List<Long> roomIds = rooms.stream().map(Room::getId).toList();
		List<Long> hostUserIds = rooms.stream().map(Room::getHostUserId).distinct().toList();

		// 참가자 수를 방 N개당 N번이 아니라 한 번에 조회
		Map<Long, Integer> participantCountMap = roomParticipantRepository.countGroupedByRoomIdIn(roomIds).stream()
				.collect(Collectors.toMap(
						row -> (Long) row[0],
						row -> ((Number) row[1]).intValue()
				));

		// 호스트 닉네임도 개별 호출 대신 벌크 조회
		List<UserInfoResponse> hostInfos;
		try {
			hostInfos = userServiceClient.getUsersInfo(hostUserIds).getResult();
		} catch (Exception e) {
			hostInfos = List.of();
		}
		Map<Long, String> hostNicknames = (hostInfos == null ? List.<UserInfoResponse>of() : hostInfos).stream()
				.collect(Collectors.toMap(UserInfoResponse::getId, UserInfoResponse::getNickname));

		return rooms.stream()
				.map(room -> {
					int currentParticipants = participantCountMap.getOrDefault(room.getId(), 0);
					String hostNickname = hostNicknames.getOrDefault(room.getHostUserId(), "Host#" + room.getHostUserId());
					return RoomConverter.toRoomListResponse(room, currentParticipants, hostNickname, false);
				})
				.toList();
	}

	@Override
	public RoomDetailResponse getRoomDetail(Long roomId) {
		Room room = getRoomById(roomId);
		// 방 삭제 여부 확인
		validateRoomNotDeleted(room);
		// 방 참가자 정보
		UserInfoResponse hostInfo = getUserInfo(room.getHostUserId());
		List<Long> participantIds = roomParticipantRepository.findUserIdsByRoomIdAndIsDeletedFalse(roomId);
		List<UserInfoResponse> participantsInfo = userServiceClient.getUsersInfo(participantIds).getResult();

		return RoomConverter.toRoomDetailResponse(room, hostInfo, participantsInfo);
	}

	@Override
	public GetMyStudyRoomsResponse getMyStudyRooms(Long userId) {
		// 내 방 목록 조회
		List<RoomParticipant> participantList = roomParticipantRepository.findMyStudyRooms(userId);

		// 참가한 방이 없는 경우 빈 리스트 반환
		if (participantList.isEmpty()) {
			return RoomConverter.toGetMyStudyRoomsResponse(List.of());
		}

		// 방 호스트 정보 추가
		List<GetMyStudyRoomsResponse.StudyRoomInfo> studyRoomInfos = participantList.stream()
				.map(participant -> {
					Room room = participant.getRoom();
					int currentParticipants = roomParticipantRepository.countByRoomIdAndIsDeletedFalse(room.getId());
					UserInfoResponse hostInfo = userServiceClient.getUserInfo(room.getHostUserId()).getResult();
					String hostNickname = hostInfo != null ? hostInfo.getNickname() : "Unknown";

					return RoomConverter.toStudyRoomInfo(room, currentParticipants, hostNickname, true);
				})
				.toList();

		return RoomConverter.toGetMyStudyRoomsResponse(studyRoomInfos);
	}

	@Override
	public GetMyClassRoomsAsParticipantResponse getMyClassRoomsAsParticipant(Long userId) {
		List<RoomParticipant> participantList = roomParticipantRepository.findMyClassRoomsAsParticipant(userId);

		if (participantList.isEmpty()) {
			return RoomConverter.toGetMyClassRoomsAsParticipantResponse(List.of());
		}

		List<Long> hostUserIds = participantList.stream()
				.map(participant -> participant.getRoom().getHostUserId())
				.distinct()
				.toList();

		List<UserInfoResponse> hostInfos = userServiceClient.getUsersInfo(hostUserIds).getResult();
		Map<Long, UserInfoResponse> hostInfoMap = hostInfos.stream()
				.collect(Collectors.toMap(UserInfoResponse::getId, info -> info));

		List<GetMyClassRoomsAsParticipantResponse.ClassRoomParticipantInfo> classRoomInfos = participantList.stream()
				.map(participant -> {
					Room room = participant.getRoom();
					int currentParticipants = roomParticipantRepository.countByRoomIdAndIsDeletedFalse(room.getId());
					UserInfoResponse hostInfo = hostInfoMap.get(room.getHostUserId());
					String hostNickname = hostInfo != null ? hostInfo.getNickname() : "Unknown";
					Integer hostProfileImage = hostInfo != null ? hostInfo.getProfileImage() : null;

					return RoomConverter.toClassRoomParticipantInfo(room, currentParticipants, hostNickname, hostProfileImage, true);
				})
				.toList();

		return RoomConverter.toGetMyClassRoomsAsParticipantResponse(classRoomInfos);
	}

	@Override
	public GetMyClassRoomsAsHostResponse getMyClassRoomsAsHost(Long userId) {
		List<Room> hostClassRooms = roomRepository.findByHostUserIdAndTypeAndIsDeletedFalse(userId, RoomType.CLASS);

		if (hostClassRooms.isEmpty()) {
			return RoomConverter.toGetMyClassRoomsAsHostResponse(List.of());
		}

		List<GetMyClassRoomsAsHostResponse.ClassRoomHostInfo> classRoomInfos = hostClassRooms.stream()
				.map(room -> {
					int currentParticipants = roomParticipantRepository.countByRoomIdAndIsDeletedFalse(room.getId());

					return RoomConverter.toClassRoomHostInfo(room, currentParticipants, true);
				})
				.toList();

		return RoomConverter.toGetMyClassRoomsAsHostResponse(classRoomInfos);
	}

	@Override
	public SearchRoomsResponse searchRooms(String keyword, Long userId) {
		String trimmedKeyword = keyword == null ? "" : keyword.trim();
		// BOOLEAN MODE에서 phrase(큰따옴표)로 감싸 보낼 거라, 키워드 안에 큰따옴표가 있으면
		// phrase가 조기 종료돼 구문 오류가 날 수 있어 제거
		String safeKeyword = trimmedKeyword.replace("\"", "");
		List<Room> rooms = roomRepository.searchRoomsByKeyword(safeKeyword);

		if (rooms.isEmpty()) {
			return RoomConverter.toSearchRoomsResponse(List.of(), trimmedKeyword);
		}

		List<Long> roomIds = rooms.stream().map(Room::getId).toList();
		List<Long> hostUserIds = rooms.stream().map(Room::getHostUserId).distinct().toList();
		List<UserInfoResponse> hostInfos = userServiceClient.getUsersInfo(hostUserIds).getResult();
		Map<Long, UserInfoResponse> hostInfoMap = hostInfos.stream()
				.collect(Collectors.toMap(UserInfoResponse::getId, info -> info));

		// 참가자 수 / 내가 참가 중인지 여부를 방마다 개별 조회하는 대신 한 번씩만 조회
		Map<Long, Integer> participantCountMap = roomParticipantRepository.countGroupedByRoomIdIn(roomIds).stream()
				.collect(Collectors.toMap(
						row -> (Long) row[0],
						row -> ((Number) row[1]).intValue()
				));
		Set<Long> joinedRoomIds = new HashSet<>(roomParticipantRepository.findJoinedRoomIdsIn(roomIds, userId));

		List<SearchRoomsResponse.RoomSearchResult> results = rooms.stream()
				.map(room -> {
					int currentParticipants = participantCountMap.getOrDefault(room.getId(), 0);
					UserInfoResponse hostInfo = hostInfoMap.get(room.getHostUserId());
					String hostNickname = hostInfo != null ? hostInfo.getNickname() : "Unknown";

					boolean isJoined = joinedRoomIds.contains(room.getId());

					return RoomConverter.toRoomSearchResult(room, currentParticipants, hostNickname, isJoined);
				})
				.toList();

		return RoomConverter.toSearchRoomsResponse(results, trimmedKeyword);
	}

	@Override
	public SearchRoomsResponse getRecommendedRooms(Long userId) {
		// 최근 30일 totalTime 대비 focusTime 비율이 높은 방 후보를 최대 50개까지만 조회
		// (기존엔 전체 이력 전체 방을 다 집계해서 total_time이 쌓일수록 느려졌음)
		List<Room> topRooms = totalTimeRepository.findTopRoomsByFocusRatio(
				LocalDate.now().minusDays(RECOMMEND_LOOKBACK_DAYS),
				PageRequest.of(0, RECOMMEND_CANDIDATE_LIMIT));

		if (topRooms.isEmpty()) {
			return RoomConverter.toSearchRoomsResponse(List.of(), "추천");
		}

		List<Long> roomIds = topRooms.stream().map(Room::getId).toList();
		List<Long> hostUserIds = topRooms.stream().map(Room::getHostUserId).distinct().toList();

		// 후보 방들의 참가자 수 / 내 참가 여부 / 호스트 정보를 방마다 개별 조회하는 대신 한 번씩만 조회
		Map<Long, Integer> participantCountMap = roomParticipantRepository.countGroupedByRoomIdIn(roomIds).stream()
				.collect(Collectors.toMap(
						row -> (Long) row[0],
						row -> ((Number) row[1]).intValue()
				));
		Set<Long> joinedRoomIds = new HashSet<>(roomParticipantRepository.findJoinedRoomIdsIn(roomIds, userId));
		List<UserInfoResponse> hostInfos;
		try {
			hostInfos = userServiceClient.getUsersInfo(hostUserIds).getResult();
		} catch (Exception e) {
			hostInfos = List.of();
		}
		Map<Long, String> hostNicknames = (hostInfos == null ? List.<UserInfoResponse>of() : hostInfos).stream()
				.collect(Collectors.toMap(UserInfoResponse::getId, UserInfoResponse::getNickname));

		// 조건에 맞는 방들을 찾기 (최대 10개) - 후보 목록은 이미 다 메모리에 있으니 추가 쿼리 없이 순회만
		List<SearchRoomsResponse.RoomSearchResult> availableRooms = new ArrayList<>();
		for (Room room : topRooms) {
			if (availableRooms.size() >= RECOMMEND_MAX_AVAILABLE) {
				break;
			}

			int currentParticipants = participantCountMap.getOrDefault(room.getId(), 0);
			boolean isJoined = joinedRoomIds.contains(room.getId());
			String hostNickname = hostNicknames.getOrDefault(room.getHostUserId(), "Host#" + room.getHostUserId());

			// 조건에 맞는 방만 availableRooms에 추가
			if (isJoined || currentParticipants < room.getMaxParticipants()) {
				SearchRoomsResponse.RoomSearchResult roomResult = RoomConverter.toRoomSearchResult(
						room, currentParticipants, hostNickname, isJoined);
				availableRooms.add(roomResult);
			}
		}
		List<SearchRoomsResponse.RoomSearchResult> results;

		if (availableRooms.size() >= RECOMMEND_RESULT_COUNT) {
			// 조건에 맞는 방이 4개 이상인 경우, 랜덤으로 4개 선택
			results = selectRandomRoomsFromResults(availableRooms, RECOMMEND_RESULT_COUNT);
		}
		else {
			results = availableRooms;
		}
		return RoomConverter.toSearchRoomsResponse(results, "추천");
	}

	// ================= 내부 유틸 메서드 =================

	private Room getRoomById(Long roomId) {
		return roomRepository.findById(roomId)
				.orElseThrow(() -> new RoomException(RoomErrorCode.ROOM_NOT_FOUND));
	}

	private UserInfoResponse getUserInfo(Long userId) {
		UserInfoResponse userInfo = userServiceClient.getUserInfo(userId).getResult();
		if (userInfo == null) {
			throw new RoomException(RoomErrorCode.ROOM_ACCESS_DENIED);
		}
		return userInfo;
	}

	// 랜덤으로 방을 선택하는 헬퍼 메서드
	private List<SearchRoomsResponse.RoomSearchResult> selectRandomRoomsFromResults(List<SearchRoomsResponse.RoomSearchResult> results, int count) {
		if (results.size() <= count) {
			return results;
		}

		List<SearchRoomsResponse.RoomSearchResult> shuffled = new ArrayList<>(results);
		Collections.shuffle(shuffled);
		return shuffled.subList(0, count);
	}

	private void validateRoomNotDeleted(Room room) {
		if (room.getIsDeleted()) {
			throw new RoomException(RoomErrorCode.ROOM_DELETED);
		}
	}
}
