package com.assistudy.commonservice.homework.controller;

import com.assistudy.commonservice.homework.dto.request.CreateHomeworkRequest;
import com.assistudy.commonservice.homework.dto.request.UpdateHomeworkRequest;
import com.assistudy.commonservice.homework.entity.Homework;
import com.assistudy.commonservice.homework.repository.HomeworkRepository;
import com.assistudy.commonservice.room.entity.Room;
import com.assistudy.commonservice.room.entity.RoomParticipant;
import com.assistudy.commonservice.room.entity.enums.RoomType;
import com.assistudy.commonservice.room.repository.RoomParticipantRepository;
import com.assistudy.commonservice.room.repository.RoomRepository;
import com.assistudy.commonservice.support.IntegrationTestSupport;
import com.assistudy.shared.constants.HeaderConstants;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class HomeworkControllerTest extends IntegrationTestSupport {

    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private RoomParticipantRepository roomParticipantRepository;
    @Autowired
    private HomeworkRepository homeworkRepository;

    private static final Long HOST_ID = 1L;
    private static final Long PARTICIPANT_ID = 2L;

    private Room saveClassRoom() {
        Room room = Room.builder()
                .hostUserId(HOST_ID)
                .name("수업방")
                .type(RoomType.CLASS)
                .isPrivate(false)
                .micActive(true)
                .maxParticipants(10)
                .isActive(true)
                .isDeleted(false)
                .build();
        Room saved = roomRepository.save(room);
        roomParticipantRepository.save(RoomParticipant.builder().room(saved).userId(HOST_ID).isDeleted(false).build());
        roomParticipantRepository.save(RoomParticipant.builder().room(saved).userId(PARTICIPANT_ID).isDeleted(false).build());
        return saved;
    }

    private Homework saveHomework(Room room, LocalDate date, String comment) {
        return homeworkRepository.save(Homework.builder()
                .room(room)
                .date(date)
                .comment(comment)
                .build());
    }

    @Test
    void 호스트가_과제를_생성한다() throws Exception {
        Room room = saveClassRoom();
        CreateHomeworkRequest request = CreateHomeworkRequest.builder()
                .roomId(room.getId())
                .date(LocalDate.of(2026, 1, 1))
                .comment("1단원 예습")
                .build();

        mockMvc.perform(put("/homeworks")
                        .header(HeaderConstants.USER_ID_HEADER, HOST_ID)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.comment").value("1단원 예습"));
    }

    @Test
    void 호스트가_아니면_과제_생성에_실패한다() throws Exception {
        Room room = saveClassRoom();
        CreateHomeworkRequest request = CreateHomeworkRequest.builder()
                .roomId(room.getId())
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
        Room room = saveClassRoom();
        LocalDate date = LocalDate.of(2026, 1, 1);
        saveHomework(room, date, "1단원 예습");

        mockMvc.perform(get("/homeworks/search")
                        .header(HeaderConstants.USER_ID_HEADER, PARTICIPANT_ID)
                        .param("roomId", room.getId().toString())
                        .param("date", date.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.isHost").value(false))
                .andExpect(jsonPath("$.result.homeworks[0].comment").value("1단원 예습"));
    }

    @Test
    void 과제_내용을_수정한다() throws Exception {
        Room room = saveClassRoom();
        Homework homework = saveHomework(room, LocalDate.of(2026, 1, 1), "원래 내용");

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
        Room room = saveClassRoom();
        Homework homework = saveHomework(room, LocalDate.of(2026, 1, 1), "원래 내용");

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
        Room room = saveClassRoom();
        saveHomework(room, LocalDate.of(2026, 1, 1), "1단원 예습");

        mockMvc.perform(get("/homeworks/my-participated-rooms")
                        .header(HeaderConstants.USER_ID_HEADER, PARTICIPANT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.rooms[0].roomId").value(room.getId()))
                .andExpect(jsonPath("$.result.rooms[0].homeworks[0].comment").value("1단원 예습"));
    }
}
