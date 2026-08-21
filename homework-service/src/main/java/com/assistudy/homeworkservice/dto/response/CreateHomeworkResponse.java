package com.assistudy.homeworkservice.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class CreateHomeworkResponse {

	private Long id;                // 과제ID
	private Long roomId;            // 방ID
	private String roomName;        // 방 이름 (추가 정보)
	private LocalDate date;     // 날짜
	private String comment;         // 과제 내용
}
