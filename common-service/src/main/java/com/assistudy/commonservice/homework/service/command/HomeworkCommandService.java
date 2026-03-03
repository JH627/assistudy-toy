package com.assistudy.commonservice.homework.service.command;

import com.assistudy.commonservice.homework.dto.request.CreateHomeworkRequest;
import com.assistudy.commonservice.homework.dto.request.UpdateHomeworkRequest;
import com.assistudy.commonservice.homework.dto.response.CreateHomeworkResponse;

public interface HomeworkCommandService {
	/**
	 * 과제를 생성합니다. (한 방의 같은 날짜에 여러 과제 가능)
	 * @param request 과제 생성 요청 정보
	 * @param userId 요청 사용자 ID (권한 확인)
	 * @return 생성된 과제 정보
	 */
	CreateHomeworkResponse createHomework(CreateHomeworkRequest request, Long userId);

	/**
	 * 과제를 수정합니다. (날짜는 변경 불가)
	 * @param homeworkId 과제 ID
	 * @param request 수정 요청 정보
	 * @param userId 요청 사용자 ID (권한 확인)
	 * @return 수정된 과제 정보
	 */
	CreateHomeworkResponse updateHomework(Long homeworkId, UpdateHomeworkRequest request, Long userId);
}
