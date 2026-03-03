package com.assistudy.commonservice.homework.service.command;

import com.assistudy.commonservice.homework.converter.HomeworkConverter;
import com.assistudy.commonservice.homework.dto.request.CreateHomeworkRequest;
import com.assistudy.commonservice.homework.dto.request.UpdateHomeworkRequest;
import com.assistudy.commonservice.homework.dto.response.CreateHomeworkResponse;
import com.assistudy.commonservice.homework.entity.Homework;
import com.assistudy.commonservice.homework.exception.HomeworkErrorCode;
import com.assistudy.commonservice.homework.exception.HomeworkException;
import com.assistudy.commonservice.homework.repository.HomeworkRepository;
import com.assistudy.commonservice.room.entity.Room;
import com.assistudy.commonservice.room.repository.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class HomeworkCommandServiceImpl implements HomeworkCommandService {

    private final HomeworkRepository homeworkRepository;
    private final RoomRepository roomRepository;

    @Override
    public CreateHomeworkResponse createHomework(CreateHomeworkRequest request, Long userId) {
        // 과제 생성 (이제 한 방에 한 날짜에 여러 과제가 있을 수 있음)
        Room room = getRoomById(request.getRoomId());
        validateHomeworkCreatePermission(room, userId);

        // 새로운 과제 생성
        Homework newHomework = Homework.builder()
                .room(room)
                .date(request.getDate())
                .comment(request.getComment())
                .build();

        Homework savedHomework = homeworkRepository.save(newHomework);

        return HomeworkConverter.toCreateHomeworkResponse(savedHomework);
    }



    @Override
    public CreateHomeworkResponse updateHomework(Long homeworkId, UpdateHomeworkRequest request, Long userId) {
        Homework homework = getHomeworkById(homeworkId);
        validateHomeworkUpdatePermission(homework, userId);

        Homework updatedHomework = Homework.builder()
                .id(homework.getId())
                .room(homework.getRoom())
                .date(homework.getDate())  // 기존 날짜 유지
                .comment(request.getComment() != null ? request.getComment() : homework.getComment())
                .build();

        Homework savedHomework = homeworkRepository.save(updatedHomework);

        return HomeworkConverter.toCreateHomeworkResponse(savedHomework);
    }

    // ================= 내부 유틸 메서드 =================

    private Room getRoomById(Long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new HomeworkException(HomeworkErrorCode.ROOM_NOT_FOUND));
    }

    private Homework getHomeworkById(Long homeworkId) {
        return homeworkRepository.findById(homeworkId)
                .orElseThrow(() -> new HomeworkException(HomeworkErrorCode.HOMEWORK_NOT_FOUND));
    }

    private void validateHomeworkCreatePermission(Room room, Long userId) {
        if (!room.getHostUserId().equals(userId)) {
            throw new HomeworkException(HomeworkErrorCode.HOMEWORK_CREATE_PERMISSION_DENIED);
        }
    }

    private void validateHomeworkUpdatePermission(Homework homework, Long userId) {
        if (!homework.getRoom().getHostUserId().equals(userId)) {
            throw new HomeworkException(HomeworkErrorCode.HOMEWORK_UPDATE_PERMISSION_DENIED);
        }
    }
}
