package com.assistudy.commonservice.analysis.service.query;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.assistudy.commonservice.analysis.converter.AnalysisConverter;
import com.assistudy.commonservice.analysis.dto.response.AnalysisResultResponse;
import com.assistudy.commonservice.analysis.entity.AnalysisResult;
import com.assistudy.commonservice.analysis.exception.AnalysisErrorCode;
import com.assistudy.commonservice.analysis.exception.AnalysisException;
import com.assistudy.commonservice.analysis.repository.AnalysisResultRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AnalysisQueryServiceImpl implements AnalysisQueryService {

	private final AnalysisResultRepository analysisResultRepository;

	@Override
	public List<AnalysisResultResponse> getAnalysisResultsByUser(Long userId) {
		try {
			log.info("Retrieving analysis results for user: {}", userId);

			List<AnalysisResult> results = analysisResultRepository.findByUserIdOrderByCreatedAtDesc(userId);

			return results.stream()
				.map(AnalysisConverter::toAnalysisResultResponse)
				.collect(Collectors.toList());

		} catch (Exception e) {
			log.error("Failed to retrieve analysis results for user: {}, error: {}",
				userId, e.getMessage(), e);
			throw new AnalysisException(AnalysisErrorCode.ANALYSIS_RETRIEVAL_FAILED);
		}
	}

	@Override
	public List<AnalysisResultResponse> getAnalysisResultsByUserAndRoom(Long userId, Long roomId) {
		try {
			log.info("Retrieving analysis results for user: {}, room: {}", userId, roomId);

			List<AnalysisResult> results = analysisResultRepository.findByUserIdAndRoom_IdOrderByCreatedAtDesc(userId, roomId);

			return results.stream()
				.map(AnalysisConverter::toAnalysisResultResponse)
				.collect(Collectors.toList());

		} catch (Exception e) {
			log.error("Failed to retrieve analysis results for user: {}, room: {}, error: {}",
				userId, roomId, e.getMessage(), e);
			throw new AnalysisException(AnalysisErrorCode.ANALYSIS_RETRIEVAL_FAILED);
		}
	}
}
