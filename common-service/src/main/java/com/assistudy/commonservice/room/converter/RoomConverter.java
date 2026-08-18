package com.assistudy.commonservice.room.converter;

import com.assistudy.commonservice.global.dto.response.UserInfoResponse;
import com.assistudy.commonservice.room.dto.cache.RecommendCandidate;
import com.assistudy.commonservice.room.dto.response.*;
import com.assistudy.commonservice.room.entity.Room;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Room 도메인 변환기
 * Entity와 DTO 간의 변환을 담당합니다.
 */
public class RoomConverter {

	/**
	 * Room 엔티티를 CreateRoomResponse로 변환합니다.
	 */
	public static CreateRoomResponse toCreateRoomResponse(Room room) {
		return CreateRoomResponse.builder()
				.id(room.getId())
				.name(room.getName())
				.type(room.getType())
				.tagName(room.getTagName())
				.description(room.getDescription())
				.isPrivate(room.getIsPrivate())
				.micActive(room.getMicActive())
				.maxParticipants(room.getMaxParticipants())
				.createdAt(room.getCreatedAt())
				.build();
	}

	/**
	 * Room 엔티티와 호스트, 참가자 정보를 받아서 RoomDetailResponse로 변환합니다.
	 */
	public static RoomDetailResponse toRoomDetailResponse(Room room, UserInfoResponse hostInfo, List<UserInfoResponse> participantsInfo) {
		return RoomDetailResponse.builder()
				.id(room.getId())
				.name(room.getName())
				.type(room.getType())
				.tagName(room.getTagName())
				.description(room.getDescription())
				.rules(room.getRules())
				.isPrivate(room.getIsPrivate())
				.micActive(room.getMicActive())
				.maxParticipants(room.getMaxParticipants())
				.currentParticipants(participantsInfo.size())
				.host(RoomDetailResponse.RoomHostInfo.from(hostInfo))
				.participants(participantsInfo.stream()
						.map(RoomDetailResponse.RoomParticipantInfo::from)
						.toList())
				.createdAt(room.getCreatedAt())
				.isJoined(false)
				.build();
	}

	/**
	 * 단일 Room → RoomListResponse (간단 변환)
	 */
	public static RoomListResponse toRoomListResponse(Room room) {
		return RoomListResponse.builder()
				.id(room.getId())
				.name(room.getName())
				.type(room.getType())
				.tagName(room.getTagName())
				.description(room.getDescription())
				.isPrivate(room.getIsPrivate())
				.micActive(room.getMicActive())
				.maxParticipants(room.getMaxParticipants())
				.createdAt(room.getCreatedAt())
				.isJoined(false)
				.build();
	}

	/**
	 * Room + 계산된 필드 → RoomListResponse
	 */
	public static RoomListResponse toRoomListResponse(Room room, Integer currentParticipants, String hostNickname, Boolean isJoined) {
		return RoomListResponse.builder()
				.id(room.getId())
				.name(room.getName())
				.type(room.getType())
				.tagName(room.getTagName())
				.description(room.getDescription())
				.isPrivate(room.getIsPrivate())
				.micActive(room.getMicActive())
				.maxParticipants(room.getMaxParticipants())
				.currentParticipants(currentParticipants)
				.hostNickname(hostNickname)
				.createdAt(room.getCreatedAt())
				.isJoined(isJoined)
				.build();
	}

	/**
	 * Room 엔티티 리스트를 RoomListResponse 리스트로 변환합니다.
	 */
	public static List<RoomListResponse> toRoomListResponseList(List<Room> rooms) {
		return rooms.stream()
				.map(RoomConverter::toRoomListResponse)
				.collect(Collectors.toList());
	}

	/**
	 * StudyRoomInfo 생성
	 */
	public static GetMyStudyRoomsResponse.StudyRoomInfo toStudyRoomInfo(Room room, Integer currentParticipants, String hostNickname, Boolean isJoined) {
		return GetMyStudyRoomsResponse.StudyRoomInfo.builder()
				.id(room.getId())
				.name(room.getName())
				.tagName(room.getTagName())
				.description(room.getDescription())
				.isPrivate(room.getIsPrivate())
				.micActive(room.getMicActive())
				.maxParticipants(room.getMaxParticipants())
				.currentParticipants(currentParticipants)
				.hostNickname(hostNickname)
				.createdAt(room.getCreatedAt())
				.isJoined(isJoined)
				.build();
	}

	public static GetMyStudyRoomsResponse toGetMyStudyRoomsResponse(List<GetMyStudyRoomsResponse.StudyRoomInfo> studyRooms) {
		return GetMyStudyRoomsResponse.builder()
				.studyRooms(studyRooms)
				.build();
	}

	/**
	 * Class 참가자 응답 변환
	 */
	public static GetMyClassRoomsAsParticipantResponse.ClassRoomParticipantInfo toClassRoomParticipantInfo(Room room, Integer currentParticipants,
				String hostNickname, Integer hostProfileImage, Boolean isJoined) {
		return GetMyClassRoomsAsParticipantResponse.ClassRoomParticipantInfo.builder()
				.id(room.getId())
				.name(room.getName())
				.tagName(room.getTagName())
				.description(room.getDescription())
				.rules(room.getRules())
				.isPrivate(room.getIsPrivate())
				.micActive(room.getMicActive())
				.maxParticipants(room.getMaxParticipants())
				.currentParticipants(currentParticipants)
				.hostNickname(hostNickname)
				.hostProfileImage(hostProfileImage)
				.createdAt(room.getCreatedAt())
				.isJoined(isJoined)
				.build();
	}

	public static GetMyClassRoomsAsParticipantResponse toGetMyClassRoomsAsParticipantResponse(List<GetMyClassRoomsAsParticipantResponse.ClassRoomParticipantInfo> classRooms) {
		return GetMyClassRoomsAsParticipantResponse.builder()
				.classRooms(classRooms)
				.build();
	}

	/**
	 * Class 호스트 응답 변환
	 */
	public static GetMyClassRoomsAsHostResponse.ClassRoomHostInfo toClassRoomHostInfo(Room room, Integer currentParticipants, Boolean isJoined) {
		return GetMyClassRoomsAsHostResponse.ClassRoomHostInfo.builder()
				.id(room.getId())
				.name(room.getName())
				.tagName(room.getTagName())
				.description(room.getDescription())
				.rules(room.getRules())
				.isPrivate(room.getIsPrivate())
				.micActive(room.getMicActive())
				.maxParticipants(room.getMaxParticipants())
				.currentParticipants(currentParticipants)
				.createdAt(room.getCreatedAt())
				.isJoined(isJoined)
				.build();
	}

	public static GetMyClassRoomsAsHostResponse toGetMyClassRoomsAsHostResponse(List<GetMyClassRoomsAsHostResponse.ClassRoomHostInfo> classRooms) {
		return GetMyClassRoomsAsHostResponse.builder()
				.classRooms(classRooms)
				.build();
	}

	/**
	 * 검색 결과 변환
	 */
	public static SearchRoomsResponse.RoomSearchResult toRoomSearchResult(Room room, Integer currentParticipants, String hostNickname, Boolean isJoined) {
		return SearchRoomsResponse.RoomSearchResult.builder()
				.id(room.getId())
				.name(room.getName())
				.type(room.getType())
				.tagName(room.getTagName())
				.description(room.getDescription())
				.isPrivate(room.getIsPrivate())
				.micActive(room.getMicActive())
				.maxParticipants(room.getMaxParticipants())
				.currentParticipants(currentParticipants)
				.hostNickname(hostNickname)
				.createdAt(room.getCreatedAt())
				.isJoined(isJoined)
				.build();
	}

	public static SearchRoomsResponse.RoomSearchResult toRoomSearchResult(RecommendCandidate candidate, Integer currentParticipants,
				String hostNickname, Boolean isJoined) {
		return SearchRoomsResponse.RoomSearchResult.builder()
				.id(candidate.id())
				.name(candidate.name())
				.type(candidate.type())
				.tagName(candidate.tagName())
				.description(candidate.description())
				.isPrivate(candidate.isPrivate())
				.micActive(candidate.micActive())
				.maxParticipants(candidate.maxParticipants())
				.currentParticipants(currentParticipants)
				.hostNickname(hostNickname)
				.createdAt(candidate.createdAt())
				.isJoined(isJoined)
				.build();
	}

	public static SearchRoomsResponse toSearchRoomsResponse(List<SearchRoomsResponse.RoomSearchResult> rooms, String keyword) {
		return SearchRoomsResponse.builder()
				.rooms(rooms)
				.totalCount(rooms.size())
				.keyword(keyword)
				.build();
	}
} 