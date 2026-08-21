package com.assistudy.homeworkservice.service.query;

import com.assistudy.homeworkservice.dto.response.CreateFeedbackResponse;
import com.assistudy.homeworkservice.dto.response.HostFeedbackResponse;

import java.time.LocalDate;
import java.util.List;

public interface FeedbackQueryService {

	List<CreateFeedbackResponse> getFeedbacksByHomework(Long homeworkId);

	List<CreateFeedbackResponse> getFeedbacksByRoomAndDateAndUser(Long roomId, LocalDate date, Long userId);

	List<CreateFeedbackResponse> getFeedbacksByRoomAndDate(Long roomId, LocalDate date);

	List<CreateFeedbackResponse> getFeedbacksByRoomAndUser(Long roomId, Long userId);

	HostFeedbackResponse getHostFeedbackByRoomAndDate(Long roomId, LocalDate date, Long userId);
}
