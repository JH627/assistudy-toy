package com.assistudy.commonservice.webrtc.controller;

import com.assistudy.commonservice.room.entity.Room;
import com.assistudy.commonservice.room.entity.RoomParticipant;
import com.assistudy.commonservice.room.entity.enums.RoomType;
import com.assistudy.commonservice.room.repository.RoomParticipantRepository;
import com.assistudy.commonservice.room.repository.RoomRepository;
import com.assistudy.commonservice.support.IntegrationTestSupport;
import com.assistudy.commonservice.webrtc.dto.request.CreateTokenRequest;
import com.assistudy.shared.constants.HeaderConstants;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class WebRTCControllerTest extends IntegrationTestSupport {

    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private RoomParticipantRepository roomParticipantRepository;

    private static final Long HOST_ID = 1L;
    private static final Long NON_PARTICIPANT_ID = 999L;

    private Room saveActiveRoom() {
        Room room = roomRepository.save(Room.builder()
                .hostUserId(HOST_ID)
                .name("화상방")
                .type(RoomType.STUDY)
                .isPrivate(false)
                .micActive(true)
                .maxParticipants(4)
                .isActive(true)
                .isDeleted(false)
                .build());
        roomParticipantRepository.save(RoomParticipant.builder().room(room).userId(HOST_ID).isDeleted(false).build());
        return room;
    }

    @Test
    void 참여자가_화상회의_토큰을_발급받는다() throws Exception {
        Room room = saveActiveRoom();

        CreateTokenRequest request = CreateTokenRequest.builder().roomId(room.getId()).build();

        mockMvc.perform(post("/webrtc/tokens")
                        .header(HeaderConstants.USER_ID_HEADER, HOST_ID)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.roomId").value(room.getId()))
                .andExpect(jsonPath("$.result.token").isNotEmpty())
                .andExpect(jsonPath("$.result.participantName").value("user" + HOST_ID));
    }

    @Test
    void 방에_참여하지_않은_사용자는_토큰_발급에_실패한다() throws Exception {
        Room room = saveActiveRoom();

        CreateTokenRequest request = CreateTokenRequest.builder().roomId(room.getId()).build();

        mockMvc.perform(post("/webrtc/tokens")
                        .header(HeaderConstants.USER_ID_HEADER, NON_PARTICIPANT_ID)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("WEBRTC011"));
    }

    @Test
    void 존재하지_않는_방으로_토큰_발급을_요청하면_실패한다() throws Exception {
        CreateTokenRequest request = CreateTokenRequest.builder().roomId(999999L).build();

        mockMvc.perform(post("/webrtc/tokens")
                        .header(HeaderConstants.USER_ID_HEADER, HOST_ID)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("WEBRTC001"));
    }
}
