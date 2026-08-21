package com.assistudy.homeworkservice.service.command;

import com.assistudy.homeworkservice.dto.request.CreateHomeworkRequest;
import com.assistudy.homeworkservice.dto.request.UpdateHomeworkRequest;
import com.assistudy.homeworkservice.dto.response.CreateHomeworkResponse;

public interface HomeworkCommandService {
	CreateHomeworkResponse createHomework(CreateHomeworkRequest request, Long userId);

	CreateHomeworkResponse updateHomework(Long homeworkId, UpdateHomeworkRequest request, Long userId);
}
