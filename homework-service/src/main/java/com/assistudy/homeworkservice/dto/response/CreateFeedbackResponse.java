package com.assistudy.homeworkservice.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class CreateFeedbackResponse {

	private Long id;
	private LocalDate date;
	private String feedback;
	private Long userId;
	private String userNickname;
	private Long homeworkId;
}
