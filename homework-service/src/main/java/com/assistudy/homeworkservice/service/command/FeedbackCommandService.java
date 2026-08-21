package com.assistudy.homeworkservice.service.command;

import com.assistudy.homeworkservice.dto.request.CreateFeedbackRequest;
import com.assistudy.homeworkservice.dto.request.UpdateFeedbackByHostRequest;
import com.assistudy.homeworkservice.dto.response.CreateFeedbackResponse;

public interface FeedbackCommandService {
	CreateFeedbackResponse createFeedback(CreateFeedbackRequest request, Long userId);

	void deleteFeedback(Long feedbackId, Long userId);

	void updateFeedbackByHost(UpdateFeedbackByHostRequest request, Long userId);
}
