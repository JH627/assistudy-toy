package com.assistudy.homeworkservice.controller;

import com.assistudy.homeworkservice.dto.request.CreateHomeworkRequest;
import com.assistudy.homeworkservice.dto.request.UpdateHomeworkRequest;
import com.assistudy.homeworkservice.entity.Homework;
import com.assistudy.homeworkservice.global.dto.response.ParticipatedRoomResponse;
import com.assistudy.homeworkservice.global.dto.response.RoomSummaryResponse;
import com.assistudy.homeworkservice.repository.HomeworkRepository;
import com.assistudy.homeworkservice.support.IntegrationTestSupport;
import com.assistudy.shared.constants.HeaderConstants;
import com.assistudy.shared.response.ApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class HomeworkControllerTest extends IntegrationTestSupport {

    @Autowired
    private HomeworkRepository homeworkRepository;

    private static final Long HOST_ID = 1L;
    private static final Long PARTICIPANT_ID = 2L;
    private static final Long ROOM_ID = 100L;

    private RoomSummaryResponse classRoom() {
        return new RoomSummaryResponse(ROOM_ID, HOST_ID, "수업방", "CLASS", null, null, false, true, false, 10);
    }

    private void stubRoom() {
        when(roomServiceClient.getRoom(ROOM_ID)).thenReturn(ApiResponse.onSuccess(classRoom()));
    }

    private Homework saveHomework(LocalDate date, String comment) {
        return homeworkRepository.save(Homework.builder()
                .roomId(ROOM_ID)
                .date(date)
                .comment(comment)
                .build());
    }

    @Test
    void 호스트가_과제를_생성한다() throws Exception {
        stubRoom();
        CreateHomeworkRequest request = CreateHomeworkRequest.builder()
                .roomId(ROOM_ID)
                .date(LocalDate.of(2026, 1, 1))
                .comment("1단원 예습")
                .build();

        mockMvc.perform(put("/homeworks")
                        .header(HeaderConstants.USER_ID_HEADER, HOST_ID)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.comment").value("1단원 예습"))
                .andExpect(jsonPath("$.result.roomName").value("수업방"));
    }

    @Test
    void 호스트가_아니면_과제_생성에_실패한다() throws Exception {
        stubRoom();
        CreateHomeworkRequest request = CreateHomeworkRequest.builder()
                .roomId(ROOM_ID)
                .date(LocalDate.of(2026, 1, 1))
                .comment("1단원 예습")
                .build();

        mockMvc.perform(put("/homeworks")
                        .header(HeaderConstants.USER_ID_HEADER, PARTICIPANT_ID)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("HOMEWORK003"));
    }

    @Test
    void 방과_날짜로_과제를_조회한다() throws Exception {
        stubRoom();
        LocalDate date = LocalDate.of(2026, 1, 1);
        saveHomework(date, "1단원 예습");

        mockMvc.perform(get("/homeworks/search")
                        .header(HeaderConstants.USER_ID_HEADER, PARTICIPANT_ID)
                        .param("roomId", ROOM_ID.toString())
                        .param("date", date.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.isHost").value(false))
                .andExpect(jsonPath("$.result.homeworks[0].comment").value("1단원 예습"));
    }

    @Test
    void 과제_내용을_수정한다() throws Exception {
        stubRoom();
        Homework homework = saveHomework(LocalDate.of(2026, 1, 1), "원래 내용");

        UpdateHomeworkRequest request = UpdateHomeworkRequest.builder().comment("수정된 내용").build();

        mockMvc.perform(put("/homeworks/{homeworkId}", homework.getId())
                        .header(HeaderConstants.USER_ID_HEADER, HOST_ID)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.comment").value("수정된 내용"));

        Homework updated = homeworkRepository.findById(homework.getId()).orElseThrow();
        assertThat(updated.getComment()).isEqualTo("수정된 내용");
    }

    @Test
    void 호스트가_아니면_과제_수정에_실패한다() throws Exception {
        stubRoom();
        Homework homework = saveHomework(LocalDate.of(2026, 1, 1), "원래 내용");

        UpdateHomeworkRequest request = UpdateHomeworkRequest.builder().comment("수정된 내용").build();

        mockMvc.perform(put("/homeworks/{homeworkId}", homework.getId())
                        .header(HeaderConstants.USER_ID_HEADER, PARTICIPANT_ID)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("HOMEWORK004"));
    }

    @Test
    void 참여했던_방들의_과제_목록을_조회한다() throws Exception {
        saveHomework(LocalDate.of(2026, 1, 1), "1단원 예습");
        when(roomServiceClient.getParticipatedClassRooms(PARTICIPANT_ID))
                .thenReturn(ApiResponse.onSuccess(List.of(new ParticipatedRoomResponse(classRoom(), false))));

        mockMvc.perform(get("/homeworks/my-participated-rooms")
                        .header(HeaderConstants.USER_ID_HEADER, PARTICIPANT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.rooms[0].roomId").value(ROOM_ID))
                .andExpect(jsonPath("$.result.rooms[0].homeworks[0].comment").value("1단원 예습"));
    }
}
