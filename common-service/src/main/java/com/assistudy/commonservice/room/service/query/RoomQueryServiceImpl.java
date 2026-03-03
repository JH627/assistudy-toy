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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RoomQueryServiceImpl implements RoomQueryService {

	private final RoomRepository roomRepository;
	private final RoomParticipantRepository roomParticipantRepository;
	private final UserServiceClient userServiceClient;
	private final TotalTimeRepository totalTimeRepository;

	@Override
	public List<RoomListResponse> getAllRooms() {
		// 삭제되지 않은 방 조회
		List<Room> rooms = roomRepository.findByIsDeletedFalse();

		return rooms.stream()
				.map(room -> {
					// 현재 방 참여자 수
					int currentParticipants = roomParticipantRepository.countByRoomIdAndIsDeletedFalse(room.getId());
					// 호스트 닉네임
					String hostNickname = "Unknown";
					try {
						UserInfoResponse hostInfo = userServiceClient.getUserInfo(room.getHostUserId()).getResult();
						hostNickname = hostInfo != null ? hostInfo.getNickname() : "Unknown";
					} catch (Exception e) {
						// User service 호출 실패시 기본값 사용
						hostNickname = "Host#" + room.getHostUserId();
					}

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
					String hostProfileImage = hostInfo != null ? hostInfo.getProfileImage() : null;

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
		List<Room> rooms = roomRepository.searchRoomsByKeyword(trimmedKeyword);

		if (rooms.isEmpty()) {
			return RoomConverter.toSearchRoomsResponse(List.of(), trimmedKeyword);
		}

		List<Long> hostUserIds = rooms.stream().map(Room::getHostUserId).distinct().toList();
		List<UserInfoResponse> hostInfos = userServiceClient.getUsersInfo(hostUserIds).getResult();
		Map<Long, UserInfoResponse> hostInfoMap = hostInfos.stream()
				.collect(Collectors.toMap(UserInfoResponse::getId, info -> info));

		List<SearchRoomsResponse.RoomSearchResult> results = rooms.stream()
				.map(room -> {
					int currentParticipants = roomParticipantRepository.countByRoomIdAndIsDeletedFalse(room.getId());
					UserInfoResponse hostInfo = hostInfoMap.get(room.getHostUserId());
					String hostNickname = hostInfo != null ? hostInfo.getNickname() : "Unknown";

					// 사용자가 방에 참가 중인지 확인
					boolean isJoined = roomParticipantRepository.findByRoomIdAndUserIdAndIsDeletedFalse(room.getId(), userId).isPresent();

					return RoomConverter.toRoomSearchResult(room, currentParticipants, hostNickname, isJoined);
				})
				.toList();

		return RoomConverter.toSearchRoomsResponse(results, trimmedKeyword);
	}

	@Override
	public SearchRoomsResponse getRecommendedRooms(Long userId) {
		// totalTime 대비 focusTime 비율이 높은 방 조회
		List<Room> topRooms = totalTimeRepository.findTopRoomsByFocusRatio();

		if (topRooms.isEmpty()) {
			return RoomConverter.toSearchRoomsResponse(List.of(), "추천");
		}
		// 조건에 맞는 방들을 찾기 (최대 10개)
		List<SearchRoomsResponse.RoomSearchResult> availableRooms = new ArrayList<>();

		// 모든 방을 DTO로 변환하면서 조건에 맞는 방 찾기
		for (Room room : topRooms) {
			// 최대 10개까지만 찾기
			if (availableRooms.size() >= 10) {
				break;
			}

			int currentParticipants = roomParticipantRepository.countByRoomIdAndIsDeletedFalse(room.getId());
			UserInfoResponse hostInfo = getUserInfo(room.getHostUserId());
			String hostNickname = hostInfo.getNickname();

			// 사용자가 방에 참가 중인지 확인
			boolean isJoined = roomParticipantRepository.findByRoomIdAndUserIdAndIsDeletedFalse(room.getId(), userId).isPresent();

			// 조건에 맞는 방만 availableRooms에 추가
			if (isJoined || currentParticipants < room.getMaxParticipants()) {
				SearchRoomsResponse.RoomSearchResult roomResult = RoomConverter.toRoomSearchResult(
						room, currentParticipants, hostNickname, isJoined);
				availableRooms.add(roomResult);
			}
		}
		List<SearchRoomsResponse.RoomSearchResult> results;

		if (availableRooms.size() >= 4) {
			// 조건에 맞는 방이 4개 이상인 경우, 랜덤으로 4개 선택
			results = selectRandomRoomsFromResults(availableRooms, 4);
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
