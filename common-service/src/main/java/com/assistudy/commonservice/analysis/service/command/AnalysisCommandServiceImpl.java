package com.assistudy.commonservice.analysis.service.command;

import org.springframework.stereotype.Service;

import com.assistudy.commonservice.analysis.dto.request.CreateAnalysisResultRequest;
import com.assistudy.commonservice.analysis.dto.request.CreateLogEntryRequest;
import com.assistudy.commonservice.analysis.dto.response.CreateAnalysisResultResponse;
import com.assistudy.commonservice.analysis.entity.AnalysisResult;
import com.assistudy.commonservice.analysis.entity.LogEntry;
import com.assistudy.commonservice.analysis.exception.AnalysisErrorCode;
import com.assistudy.commonservice.analysis.exception.AnalysisException;
import com.assistudy.commonservice.analysis.repository.AnalysisResultRepository;
import com.assistudy.commonservice.analysis.repository.LogEntryRepository;
import com.assistudy.commonservice.room.entity.Room;
import com.assistudy.commonservice.room.exception.RoomErrorCode;
import com.assistudy.commonservice.room.exception.RoomException;
import com.assistudy.commonservice.room.repository.RoomRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AnalysisCommandServiceImpl implements AnalysisCommandService {

	private final AnalysisResultRepository analysisResultRepository;
	private final LogEntryRepository logEntryRepository;
	private final RoomRepository roomRepository;

	@Override
	public CreateAnalysisResultResponse createAnalysisResult(CreateAnalysisResultRequest request) {
		try {
			log.info("Creating analysis result for user: {}, room: {}, type: {}",
				request.getUserId(), request.getRoomId(), request.getAnalysisType());

			// 방 존재 여부 확인
			Room room = roomRepository.findById(request.getRoomId())
				.orElseThrow(() -> new RoomException(RoomErrorCode.ROOM_NOT_FOUND));

			// 분석 결과 생성
			AnalysisResult analysisResult = AnalysisResult.builder()
				.userId(request.getUserId())
				.room(room)
				.analysisType(request.getAnalysisType())
				.analysisResult(request.getAnalysisResult())
				.confidence(request.getConfidence())
				.metadata(request.getMetadata())
				.build();

			AnalysisResult savedResult = analysisResultRepository.save(analysisResult);

			log.info("Analysis result saved successfully with ID: {} for user: {}",
				savedResult.getId(), request.getUserId());

			return CreateAnalysisResultResponse.builder()
				.id(savedResult.getId())
				.status("SUCCESS")
				.message("Analysis result created successfully")
				.build();

		} catch (RoomException e) {
			log.error("Room not found for analysis result creation: roomId={}, error={}",
				request.getRoomId(), e.getMessage());
			throw e;
		} catch (Exception e) {
			log.error("Failed to create analysis result for user: {}, error: {}",
				request.getUserId(), e.getMessage(), e);
			throw new AnalysisException(AnalysisErrorCode.ANALYSIS_SAVE_FAILED);
		}
	}

	@Override
	public void createLogEntry(CreateLogEntryRequest request) {
		try {
			log.info("Creating log entry for user: {}, room: {}, type: {}",
				request.getUserId(), request.getRoomId(), request.getLogType());

			// 방 존재 여부 확인
			Room room = roomRepository.findById(request.getRoomId())
				.orElseThrow(() -> new RoomException(RoomErrorCode.ROOM_NOT_FOUND));

			// 로그 엔트리 생성
			LogEntry logEntry = LogEntry.builder()
				.userId(request.getUserId())
				.room(room)
				.logType(request.getLogType())
				.logData(request.getLogData())
				.build();

			LogEntry savedEntry = logEntryRepository.save(logEntry);

			log.info("Log entry saved successfully with ID: {} for user: {}",
				savedEntry.getId(), request.getUserId());

		} catch (RoomException e) {
			log.error("Room not found for log entry creation: roomId={}, error={}",
				request.getRoomId(), e.getMessage());
			throw e;
		} catch (Exception e) {
			log.error("Failed to create log entry for user: {}, error: {}",
				request.getUserId(), e.getMessage(), e);
			throw new AnalysisException(AnalysisErrorCode.LOG_SAVE_FAILED);
		}
	}
}
