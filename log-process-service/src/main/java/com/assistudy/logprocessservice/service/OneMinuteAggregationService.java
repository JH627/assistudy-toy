package com.assistudy.logprocessservice.service;

import com.assistudy.logprocessservice.dto.OnDeviceLogDto;
import com.assistudy.logprocessservice.client.CommonServiceClient;
import com.assistudy.logprocessservice.dto.request.CreateFocusScoreRequest;
import com.assistudy.logprocessservice.dto.response.FocusScoreResponse;
import com.assistudy.logprocessservice.dto.request.CreateAnalysisResultRequest;
import com.assistudy.logprocessservice.dto.response.CreateAnalysisResultResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OneMinuteAggregationService {
    
    private final CommonServiceClient commonServiceClient;
    private final SparkAnalysisService sparkAnalysisService;
    
    // 1분간 수집된 로그들을 저장 (userId-roomId를 키로 사용)
    private final Map<String, List<OnDeviceLogDto>> oneMinuteLogs = new ConcurrentHashMap<>();
    
    /**
     * 로그 수집
     */
    public void collectLog(OnDeviceLogDto logDto) {
        String key = generateKey(logDto.getUserId(), logDto.getRoomId());
        oneMinuteLogs.computeIfAbsent(key, k -> new ArrayList<>()).add(logDto);
        
        log.debug("Log collected for key: {}, total logs: {}", key, oneMinuteLogs.get(key).size());
    }
    
    /**
     * 매 1분마다 실행되는 스케줄러
     */
    @Scheduled(fixedRate = 60000) // 60초마다 실행
    public void processOneMinuteLogs() {
        log.info("🔄 Starting 1-minute log aggregation...");
        
        if (oneMinuteLogs.isEmpty()) {
            log.debug("No logs to process");
            return;
        }
        
        // 현재 수집된 로그들을 복사하고 원본 클리어
        Map<String, List<OnDeviceLogDto>> logsToProcess = new HashMap<>(oneMinuteLogs);
        oneMinuteLogs.clear();
        
        for (Map.Entry<String, List<OnDeviceLogDto>> entry : logsToProcess.entrySet()) {
            try {
                String key = entry.getKey();
                List<OnDeviceLogDto> logs = entry.getValue();
                
                log.info("📊 Processing logs for key: {}, count: {}", key, logs.size());
                processUserRoomLogs(logs);
                
            } catch (Exception e) {
                log.error("Failed to process logs for key: {}", entry.getKey(), e);
            }
        }
        
        log.info("✅ 1-minute log aggregation completed");
    }
    
    /**
     * 특정 사용자-방의 1분간 로그 처리
     */
    private void processUserRoomLogs(List<OnDeviceLogDto> logs) {
        if (logs.isEmpty()) return;
        
        OnDeviceLogDto firstLog = logs.get(0);
        Long userId = firstLog.getUserId();
        Long roomId = firstLog.getRoomId();
        
        // Spark를 통한 1분간 종합 분석
        OneMinuteAnalysisResult analysis = analyzeOneMinuteData(logs);
        
        // FocusScore DB에 저장
        saveFocusScore(userId, roomId, analysis);
    }
    
    /**
     * Spark를 통한 1분간 종합 분석
     */
    private OneMinuteAnalysisResult analyzeOneMinuteData(List<OnDeviceLogDto> logs) {
        try {
            // 로그가 없는 경우
            if (logs.isEmpty()) {
                return OneMinuteAnalysisResult.builder()
                    .averageScore(0)
                    .evaluationText("1분간 로그가 수집되지 않았습니다. 카메라 앞에 계시지 않았거나 시스템 오류가 발생했을 수 있습니다.")
                    .confidence(0.0)
                    .build();
            }
            
            log.info("🔥 Starting Spark-based 1-minute analysis with {} logs", logs.size());
            
            // Spark 사용 가능한지 확인
            if (!sparkAnalysisService.isSparkAvailable()) {
                log.warn("Spark not available, using fallback analysis");
                return fallbackAnalysis(logs);
            }
            
            // 실제 Spark 분석 호출
            return performSparkBasedAnalysis(logs);
            
        } catch (Exception e) {
            log.error("Failed to analyze one minute data with Spark, using fallback", e);
            return fallbackAnalysis(logs);
        }
    }
    
    /**
     * 실제 Spark를 사용한 분석
     */
    private OneMinuteAnalysisResult performSparkBasedAnalysis(List<OnDeviceLogDto> logs) {
        // 집중도 데이터를 위한 가상의 OnDeviceLogDto 생성 (Spark 분석용)
        OnDeviceLogDto aggregatedLog = createAggregatedLog(logs);
        
        // 기존 SparkAnalysisService의 analyzeFocusData 활용
        var sparkResult = sparkAnalysisService.analyzeFocusData(aggregatedLog);
        
        if (sparkResult.getResult() == null) {
            log.warn("Spark analysis failed, using fallback");
            return fallbackAnalysis(logs);
        }
        
        var focusAnalysis = sparkResult.getResult();
        
        // Spark 결과를 기반으로 추가 분석
        var behaviorStats = analyzeUserBehaviorWithSpark(logs);
        
        // 종합 평가 생성
        String evaluationText = generateSparkBasedEvaluation(
            focusAnalysis, 
            behaviorStats, 
            logs.size()
        );
        
        log.info("✅ Spark 1-minute analysis completed: score={}, confidence={}", 
                focusAnalysis.getCurrentScore(), focusAnalysis.getConfidence());
        
        return OneMinuteAnalysisResult.builder()
            .averageScore((int) Math.round(focusAnalysis.getCurrentScore()))
            .evaluationText(evaluationText)
            .confidence(focusAnalysis.getConfidence())
            .yawnCount(behaviorStats.getYawnCount())
            .gazeDistractionCount(behaviorStats.getGazeDistractionCount())
            .headTurnCount(behaviorStats.getHeadTurnCount())
            .totalLogs(logs.size())
            .build();
    }
    
    /**
     * Spark를 사용한 행동 패턴 분석
     */
    private BehaviorStats analyzeUserBehaviorWithSpark(List<OnDeviceLogDto> logs) {
        if (!sparkAnalysisService.isSparkAvailable()) {
            return BehaviorStats.builder()
                .yawnCount(0)
                .gazeDistractionCount(0)
                .headTurnCount(0)
                .build();
        }
        
        try {
            // Spark RDD로 로그 데이터 변환
            List<String> logTypes = logs.stream()
                .filter(log -> log.getLogType() != null)
                .map(OnDeviceLogDto::getLogType)
                .collect(Collectors.toList());
            
            // Spark 컨텍스트를 통한 행동 패턴 분석 (추후 SparkAnalysisService에 메서드 추가)
            long yawnCount = logTypes.stream().filter(type -> "YAWN".equals(type)).count();
            long gazeCount = logTypes.stream().filter(type -> "GAZE_DISTRACTION".equals(type)).count();
            long headCount = logTypes.stream().filter(type -> "HEAD_TURN".equals(type)).count();
            
            log.info("🔍 Spark behavior analysis: YAWN={}, GAZE={}, HEAD={}", yawnCount, gazeCount, headCount);
            
            return BehaviorStats.builder()
                .yawnCount((int) yawnCount)
                .gazeDistractionCount((int) gazeCount)
                .headTurnCount((int) headCount)
                .build();
                
        } catch (Exception e) {
            log.error("Spark behavior analysis failed", e);
            return BehaviorStats.builder().yawnCount(0).gazeDistractionCount(0).headTurnCount(0).build();
        }
    }
    
    /**
     * 로그들을 집계해서 하나의 대표 로그 생성 (Spark 분석용)
     */
    private OnDeviceLogDto createAggregatedLog(List<OnDeviceLogDto> logs) {
        if (logs.isEmpty()) {
            return OnDeviceLogDto.builder()
                .userId(1L)
                .roomId(999L)
                .focusScore(0.0)
                .build();
        }
        
        // 집중도 점수들의 평균 계산
        List<Double> focusScores = logs.stream()
            .filter(log -> log.getFocusScore() != null)
            .map(OnDeviceLogDto::getFocusScore)
            .collect(Collectors.toList());
        
        double avgFocusScore = focusScores.isEmpty() ? 0.0 : 
            focusScores.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        
        OnDeviceLogDto firstLog = logs.get(0);
        
        return OnDeviceLogDto.builder()
            .userId(firstLog.getUserId())
            .roomId(firstLog.getRoomId())
            .focusScore(avgFocusScore)
            .logType("AGGREGATED_1MIN")
            .behaviorText("1분간 " + logs.size() + "개 로그 집계")
            .timestamp(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
            .build();
    }
    
    /**
     * Spark 분석 결과 기반 평가 텍스트 생성
     */
    private String generateSparkBasedEvaluation(
            com.assistudy.logprocessservice.dto.FocusAnalysisResult focusAnalysis,
            BehaviorStats behaviorStats,
            int totalLogs) {
        
        StringBuilder evaluation = new StringBuilder();
        
        // Spark 분석 결과 활용
        evaluation.append("🔥 Spark 분석 결과: ");
        evaluation.append(focusAnalysis.getSummary()).append(" ");
        
        // 행동 패턴 분석 추가
        List<String> issues = new ArrayList<>();
        List<String> positives = new ArrayList<>();
        
        if (behaviorStats.getYawnCount() > 0) {
            issues.add(String.format("하품 %d회", behaviorStats.getYawnCount()));
        }
        if (behaviorStats.getGazeDistractionCount() > 2) {
            issues.add(String.format("시선 이탈 %d회", behaviorStats.getGazeDistractionCount()));
        } else if (behaviorStats.getGazeDistractionCount() <= 1) {
            positives.add("시선 집중 우수");
        }
        if (behaviorStats.getHeadTurnCount() > 3) {
            issues.add(String.format("고개 돌림 %d회", behaviorStats.getHeadTurnCount()));
        }
        
        // 데이터 충분성
        if (totalLogs < 6) {
            issues.add("측정 데이터 부족");
        } else {
            positives.add(String.format("충분한 데이터 수집 (%d개)", totalLogs));
        }
        
        // 긍정적 요소
        if (!positives.isEmpty()) {
            evaluation.append("✅ ").append(String.join(", ", positives)).append(". ");
        }
        
        // 개선점
        if (!issues.isEmpty()) {
            evaluation.append("⚠️ 개선점: ").append(String.join(", ", issues)).append(". ");
        }
        
        // Spark 추천사항 활용
        evaluation.append(focusAnalysis.getRecommendation());
        
        return evaluation.toString();
    }
    
    /**
     * Spark를 사용할 수 없을 때의 fallback 분석
     */
    private OneMinuteAnalysisResult fallbackAnalysis(List<OnDeviceLogDto> logs) {
        // 기존 Java Stream 기반 분석 유지
        List<Double> focusScores = logs.stream()
            .filter(log -> log.getFocusScore() != null)
            .map(OnDeviceLogDto::getFocusScore)
            .collect(Collectors.toList());
        
        if (focusScores.isEmpty()) {
            return OneMinuteAnalysisResult.builder()
                .averageScore(0)
                .evaluationText("1분간 집중도 데이터가 측정되지 않았습니다.")
                .confidence(0.0)
                .build();
        }
        
        double averageScore = focusScores.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        
        long yawnCount = logs.stream()
            .filter(log -> "YAWN".equals(log.getLogType()))
            .count();
        
        return OneMinuteAnalysisResult.builder()
            .averageScore((int) Math.round(averageScore))
            .evaluationText("⚙️ Fallback 분석: 평균 집중도 " + Math.round(averageScore) + "점")
            .confidence(0.6)
            .yawnCount((int) yawnCount)
            .gazeDistractionCount(0)
            .headTurnCount(0)
            .totalLogs(logs.size())
            .build();
    }
    
    /**
     * 행동 통계 DTO
     */
    @lombok.Data
    @lombok.Builder
    public static class BehaviorStats {
        private Integer yawnCount;
        private Integer gazeDistractionCount;
        private Integer headTurnCount;
    }
    
    /**
     * 종합 평가 텍스트 생성
     */
    private String generateComprehensiveEvaluation(
            double avgScore, double maxScore, double minScore,
            long yawnCount, long gazeDistractionCount, long headTurnCount,
            int totalLogs) {
        
        StringBuilder evaluation = new StringBuilder();
        
        // 전체 집중도 평가
        if (avgScore >= 80) {
            evaluation.append("🟢 매우 우수한 집중력을 보였습니다. ");
        } else if (avgScore >= 70) {
            evaluation.append("🔵 양호한 집중 상태를 유지했습니다. ");
        } else if (avgScore >= 50) {
            evaluation.append("🟡 보통 수준의 집중력을 보였습니다. ");
        } else if (avgScore >= 30) {
            evaluation.append("🟠 집중력이 다소 부족했습니다. ");
        } else {
            evaluation.append("🔴 집중력이 매우 낮았습니다. ");
        }
        
        evaluation.append(String.format("평균 집중도는 %.1f점입니다. ", avgScore));
        
        // 행동 패턴 분석
        List<String> issues = new ArrayList<>();
        List<String> positives = new ArrayList<>();
        
        if (yawnCount > 0) {
            issues.add(String.format("하품 %d회", yawnCount));
        }
        if (gazeDistractionCount > 2) {
            issues.add(String.format("시선 이탈 %d회", gazeDistractionCount));
        } else if (gazeDistractionCount <= 1) {
            positives.add("시선 집중이 우수함");
        }
        if (headTurnCount > 3) {
            issues.add(String.format("고개 돌림 %d회", headTurnCount));
        }
        
        // 안정성 평가
        double scoreVariance = maxScore - minScore;
        if (scoreVariance <= 15) {
            positives.add("집중도가 안정적으로 유지됨");
        } else if (scoreVariance > 30) {
            issues.add("집중도 변동이 심함");
        }
        
        // 데이터 충분성 체크
        if (totalLogs < 6) { // 1분에 5초마다면 12개 정도 예상
            issues.add("측정 데이터 부족 (카메라 이탈 가능성)");
        }
        
        // 긍정적 요소
        if (!positives.isEmpty()) {
            evaluation.append("✅ ").append(String.join(", ", positives)).append(". ");
        }
        
        // 개선 필요 요소
        if (!issues.isEmpty()) {
            evaluation.append("⚠️ 개선점: ").append(String.join(", ", issues)).append(". ");
        }
        
        // 추천사항
        if (avgScore >= 80 && issues.isEmpty()) {
            evaluation.append("현재 상태를 그대로 유지하세요!");
        } else if (avgScore >= 60) {
            evaluation.append("조금 더 집중하면 더 좋은 결과를 얻을 수 있습니다.");
        } else {
            evaluation.append("휴식을 취하거나 학습 환경을 점검해보세요.");
        }
        
        return evaluation.toString();
    }
    
    /**
     * 신뢰도 계산
     */
    private double calculateConfidence(int totalLogs, int validScoreLogs) {
        if (totalLogs == 0) return 0.0;
        
        // 기본 신뢰도는 유효한 로그 비율
        double baseConfidence = (double) validScoreLogs / totalLogs;
        
        // 로그 수가 충분하면 신뢰도 증가
        if (totalLogs >= 10) {
            baseConfidence = Math.min(0.95, baseConfidence + 0.1);
        } else if (totalLogs >= 5) {
            baseConfidence = Math.min(0.85, baseConfidence + 0.05);
        }
        
        return baseConfidence;
    }
    
    /**
     * FocusScore DB에 저장
     */
    private void saveFocusScore(Long userId, Long roomId, OneMinuteAnalysisResult analysis) {
        try {
            CreateFocusScoreRequest request = CreateFocusScoreRequest.builder()
                .userId(userId)
                .roomId(roomId)
                .score(analysis.getAverageScore())
                .endTime(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                .evaluationText(analysis.getEvaluationText())
                .build();
            
            // CommonService API 호출
            log.info("💾 Saving 1-minute focus score for user: {}, room: {}, score: {}", 
                    userId, roomId, analysis.getAverageScore());
            
            try {
                FocusScoreResponse response = commonServiceClient.createFocusScore(request);
                log.info("✅ Focus score saved successfully with ID: {} for user: {}, room: {}", 
                        response.getId(), userId, roomId);
                log.info("📊 1-minute analysis: {}", analysis.getEvaluationText());
            } catch (feign.FeignException e) {
                log.error("Feign error saving focus score: status={}, body={}", 
                        e.status(), e.contentUTF8());
                throw e;
            }
            
        } catch (Exception e) {
            log.error("Failed to save focus score for user: {}, room: {}", userId, roomId, e);
        }
    }
    
    private String generateKey(Long userId, Long roomId) {
        return userId + "-" + roomId;
    }
    
    /**
     * 1분간 분석 결과 DTO
     */
    @lombok.Data
    @lombok.Builder
    public static class OneMinuteAnalysisResult {
        private Integer averageScore;
        private String evaluationText;
        private Double confidence;
        private Integer yawnCount;
        private Integer gazeDistractionCount;
        private Integer headTurnCount;
        private Integer totalLogs;
    }
}