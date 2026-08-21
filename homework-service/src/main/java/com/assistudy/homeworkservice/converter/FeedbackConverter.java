package com.assistudy.homeworkservice.converter;

import com.assistudy.homeworkservice.entity.Feedback;
import com.assistudy.homeworkservice.dto.response.CreateFeedbackResponse;

public class FeedbackConverter {

	public static CreateFeedbackResponse toCreateFeedbackResponse(Feedback feedback) {
		return CreateFeedbackResponse.builder()
				.id(feedback.getId())
				.homeworkId(feedback.getHomework().getId())
				.userId(feedback.getUserId())
				.userNickname(null)
				.feedback(feedback.getFeedback())
				.date(feedback.getDate())
				.build();
	}
}
