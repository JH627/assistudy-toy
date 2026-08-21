package com.assistudy.webrtcservice.controller;

import com.assistudy.shared.constants.HeaderConstants;
import com.assistudy.shared.exception.CustomException;
import com.assistudy.shared.exception.code.GeneralErrorCode;
import com.assistudy.shared.response.ApiResponse;
import com.assistudy.webrtcservice.dto.request.CreateTokenRequest;
import com.assistudy.webrtcservice.global.client.RoomServiceClient;
import com.assistudy.webrtcservice.global.client.UserServiceClient;
import com.assistudy.webrtcservice.global.dto.response.RoomSummaryResponse;
import com.assistudy.webrtcservice.global.dto.response.UserInfoResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class WebRTCControllerTest {

    private static final Long HOST_ID = 1L;
    private static final Long ROOM_ID = 10L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RoomServiceClient roomServiceClient;

    @MockitoBean
    private UserServiceClient userServiceClient;

    private RoomSummaryResponse activeRoom() {
        return new RoomSummaryResponse(ROOM_ID, HOST_ID, "화상방", "STUDY", null, null, false, true, false, 4);
    }

    @Test
    void 참여자가_화상회의_토큰을_발급받는다() throws Exception {
        when(roomServiceClient.getRoom(ROOM_ID)).thenReturn(ApiResponse.onSuccess(activeRoom()));
        when(roomServiceClient.checkParticipant(ROOM_ID, HOST_ID)).thenReturn(ApiResponse.onSuccess(true));
        when(roomServiceClient.countParticipants(ROOM_ID)).thenReturn(ApiResponse.onSuccess(1));
        when(userServiceClient.getUserInfo(HOST_ID))
                .thenReturn(ApiResponse.onSuccess(new UserInfoResponse(HOST_ID, "host@test.com", "host", null)));

        CreateTokenRequest request = CreateTokenRequest.builder().roomId(ROOM_ID).build();

        mockMvc.perform(post("/webrtc/tokens")
                        .header(HeaderConstants.USER_ID_HEADER, HOST_ID)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.roomId").value(ROOM_ID))
                .andExpect(jsonPath("$.result.token").isNotEmpty())
                .andExpect(jsonPath("$.result.participantName").value("host"));
    }

    @Test
    void 방에_참여하지_않은_사용자는_토큰_발급에_실패한다() throws Exception {
        when(roomServiceClient.getRoom(ROOM_ID)).thenReturn(ApiResponse.onSuccess(activeRoom()));
        when(roomServiceClient.checkParticipant(ROOM_ID, HOST_ID)).thenReturn(ApiResponse.onSuccess(false));
        when(userServiceClient.getUserInfo(anyLong()))
                .thenReturn(ApiResponse.onSuccess(new UserInfoResponse(HOST_ID, "host@test.com", "host", null)));

        CreateTokenRequest request = CreateTokenRequest.builder().roomId(ROOM_ID).build();

        mockMvc.perform(post("/webrtc/tokens")
                        .header(HeaderConstants.USER_ID_HEADER, HOST_ID)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("WEBRTC011"));
    }

    @Test
    void 방_조회에서_업스트림_예외가_나면_그대로_전파된다() throws Exception {
        // common-service가 방 없음(400)을 던지면 RemoteErrorDecoder가 복원한 CustomException이
        // 그대로 전파되는지 확인 (webrtc-service 자체 에러코드로 뭉개지 않음)
        when(roomServiceClient.getRoom(ROOM_ID))
                .thenThrow(new CustomException(GeneralErrorCode.INVALID_INPUT_VALUE));

        CreateTokenRequest request = CreateTokenRequest.builder().roomId(ROOM_ID).build();

        mockMvc.perform(post("/webrtc/tokens")
                        .header(HeaderConstants.USER_ID_HEADER, HOST_ID)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(GeneralErrorCode.INVALID_INPUT_VALUE.getCode()));
    }
}
