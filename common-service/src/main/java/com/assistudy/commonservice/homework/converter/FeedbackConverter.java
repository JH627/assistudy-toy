package com.assistudy.commonservice.homework.converter;

import com.assistudy.commonservice.homework.entity.Feedback;
import com.assistudy.commonservice.homework.dto.response.CreateFeedbackResponse;

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