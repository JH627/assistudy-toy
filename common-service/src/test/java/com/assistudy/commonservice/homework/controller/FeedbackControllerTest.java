package com.assistudy.commonservice.homework.controller;

import com.assistudy.commonservice.homework.dto.request.CreateFeedbackRequest;
import com.assistudy.commonservice.homework.dto.request.DeleteFeedbackRequest;
import com.assistudy.commonservice.homework.dto.request.GetFeedbackListRequest;
import com.assistudy.commonservice.homework.dto.request.UpdateFeedbackByHostRequest;
import com.assistudy.commonservice.homework.entity.Feedback;
import com.assistudy.commonservice.homework.entity.Homework;
import com.assistudy.commonservice.homework.repository.FeedbackRepository;
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

class FeedbackControllerTest extends IntegrationTestSupport {

    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private RoomParticipantRepository roomParticipantRepository;
    @Autowired
    private HomeworkRepository homeworkRepository;
    @Autowired
    private FeedbackRepository feedbackRepository;

    private static final Long HOST_ID = 1L;
    private static final Long PARTICIPANT_ID = 2L;
    private static final Long OTHER_PARTICIPANT_ID = 3L;

    private Room room;
    private Homework homework;

    private void setUpRoomAndHomework() {
        Room saved = roomRepository.save(Room.builder()
                .hostUserId(HOST_ID)
                .name("수업방")
                .type(RoomType.CLASS)
                .isPrivate(false)
                .micActive(true)
                .maxParticipants(10)
                .isActive(true)
                .isDeleted(false)
                .build());
        roomParticipantRepository.save(RoomParticipant.builder().room(saved).userId(HOST_ID).isDeleted(false).build());
        roomParticipantRepository.save(RoomParticipant.builder().room(saved).userId(PARTICIPANT_ID).isDeleted(false).build());
        this.room = saved;
        this.homework = homeworkRepository.save(Homework.builder()
                .room(saved)
                .date(LocalDate.of(2026, 1, 1))
                .comment("1단원 예습")
                .build());
    }

    @Test
    void 참여자가_피드백을_작성한다() throws Exception {
        setUpRoomAndHomework();
        CreateFeedbackRequest request = CreateFeedbackRequest.builder()
                .homeworkId(homework.getId())
                .userId(PARTICIPANT_ID)
                .feedback("잘했어요")
                .build();

        mockMvc.perform(post("/feedback")
                        .header(HeaderConstants.USER_ID_HEADER, PARTICIPANT_ID)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.feedback").value("잘했어요"));
    }

    @Test
    void 방에_참여하지_않은_사용자는_피드백_작성에_실패한다() throws Exception {
        setUpRoomAndHomework();
        CreateFeedbackRequest request = CreateFeedbackRequest.builder()
                .homeworkId(homework.getId())
                .userId(999L)
                .feedback("잘했어요")
                .build();

        mockMvc.perform(post("/feedback")
                        .header(HeaderConstants.USER_ID_HEADER, 999L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("FEEDBACK003"));
    }

    @Test
    void 같은_과제에_중복_피드백은_실패한다() throws Exception {
        setUpRoomAndHomework();
        feedbackRepository.save(Feedback.builder()
                .homework(homework)
                .userId(PARTICIPANT_ID)
                .date(LocalDate.now())
                .feedback("먼저 씀")
                .build());

        CreateFeedbackRequest request = CreateFeedbackRequest.builder()
                .homeworkId(homework.getId())
                .userId(PARTICIPANT_ID)
                .feedback("다시 씀")
                .build();

        mockMvc.perform(post("/feedback")
                        .header(HeaderConstants.USER_ID_HEADER, PARTICIPANT_ID)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("FEEDBACK002"));
    }

    @Test
    void 작성자가_피드백을_삭제한다() throws Exception {
        setUpRoomAndHomework();
        Feedback feedback = feedbackRepository.save(Feedback.builder()
                .homework(homework)
                .userId(PARTICIPANT_ID)
                .date(LocalDate.now())
                .feedback("삭제될 피드백")
                .build());

        DeleteFeedbackRequest request = DeleteFeedbackRequest.builder().feedbackId(feedback.getId()).build();

        mockMvc.perform(delete("/feedback")
                        .header(HeaderConstants.USER_ID_HEADER, PARTICIPANT_ID)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        assertThat(feedbackRepository.findById(feedback.getId())).isEmpty();
    }

    @Test
    void 작성자가_아니면_피드백_삭제에_실패한다() throws Exception {
        setUpRoomAndHomework();
        Feedback feedback = feedbackRepository.save(Feedback.builder()
                .homework(homework)
                .userId(PARTICIPANT_ID)
                .date(LocalDate.now())
                .feedback("삭제될 피드백")
                .build());

        DeleteFeedbackRequest request = DeleteFeedbackRequest.builder().feedbackId(feedback.getId()).build();

        mockMvc.perform(delete("/feedback")
                        .header(HeaderConstants.USER_ID_HEADER, OTHER_PARTICIPANT_ID)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("FEEDBACK005"));
    }

    @Test
    void 과제ID로_피드백_목록을_조회한다() throws Exception {
        setUpRoomAndHomework();
        feedbackRepository.save(Feedback.builder()
                .homework(homework).userId(PARTICIPANT_ID).date(LocalDate.now()).feedback("피드백1").build());

        GetFeedbackListRequest request = GetFeedbackListRequest.builder().homeworkId(homework.getId()).build();

        mockMvc.perform(post("/feedback/list")
                        .header(HeaderConstants.USER_ID_HEADER, PARTICIPANT_ID)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result[0].feedback").value("피드백1"));
    }

    @Test
    void 호스트가_날짜별_피드백_현황을_조회한다() throws Exception {
        setUpRoomAndHomework();
        feedbackRepository.save(Feedback.builder()
                .homework(homework).userId(PARTICIPANT_ID).date(LocalDate.now()).feedback("피드백1").build());

        mockMvc.perform(get("/feedback/host")
                        .header(HeaderConstants.USER_ID_HEADER, HOST_ID)
                        .param("roomId", room.getId().toString())
                        .param("date", homework.getDate().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.homeworks[0].userFeedbacks[0].userId").value(PARTICIPANT_ID))
                .andExpect(jsonPath("$.result.homeworks[0].userFeedbacks[0].feedback").value("피드백1"));
    }

    @Test
    void 호스트가_아니면_피드백_현황_조회에_실패한다() throws Exception {
        setUpRoomAndHomework();

        mockMvc.perform(get("/feedback/host")
                        .header(HeaderConstants.USER_ID_HEADER, PARTICIPANT_ID)
                        .param("roomId", room.getId().toString())
                        .param("date", homework.getDate().toString()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("FEEDBACK003"));
    }

    @Test
    void 호스트가_피드백을_수정한다() throws Exception {
        setUpRoomAndHomework();
        Feedback feedback = feedbackRepository.save(Feedback.builder()
                .homework(homework).userId(PARTICIPANT_ID).date(LocalDate.now()).feedback("원본").build());

        UpdateFeedbackByHostRequest request = UpdateFeedbackByHostRequest.builder()
                .feedbackId(feedback.getId())
                .feedback("호스트가 수정함")
                .build();

        mockMvc.perform(put("/feedback/host")
                        .header(HeaderConstants.USER_ID_HEADER, HOST_ID)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        Feedback updated = feedbackRepository.findById(feedback.getId()).orElseThrow();
        assertThat(updated.getFeedback()).isEqualTo("호스트가 수정함");
    }
}
