package com.assistudy.logprocessservice.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import com.assistudy.logprocessservice.global.dto.response.ApiResponse;
import com.assistudy.logprocessservice.dto.FocusAnalysisResult;
import com.assistudy.logprocessservice.dto.OnDeviceLogDto;
import org.springframework.http.HttpStatus;

@Slf4j
@Service
public class SparkAnalysisService {

    private JavaSparkContext sparkContext;
    private volatile boolean sparkAvailable = false;

    @PostConstruct
    public void init() {
        try {
            // Embedded mode 설정
            SparkConf conf = new SparkConf()
                .setAppName("Assistudy-LogAnalyzer")
                .setMaster("local[*]")  // 1개 코어만 사용 (더욱 단순화)
                .set("spark.ui.enabled", "true")  // UI 활성화
                .set("spark.sql.shuffle.partitions", "1")
                .set("spark.driver.memory", "512m")  // 메모리 더 늘려
                .set("spark.executor.memory", "512m")
                .set("spark.sql.adaptive.enabled", "true")
                .set("spark.driver.allowMultipleContexts", "true")
                .set("spark.network.timeout", "300s")
                .set("spark.rpc.askTimeout", "300s");

            sparkContext = new JavaSparkContext(conf);
            sparkAvailable = true;
            log.info("✅ Spark embedded mode initialized successfully");

        } catch (Exception e) {
            log.error("❌ Spark initialization failed, using fallback mode", e);
            this.sparkContext = null; // fallback 모드로 전환
        }
    }

    @PreDestroy
    public void cleanup() {
        if (sparkContext != null) {
            try {
                sparkContext.close();
                log.info("Spark context closed");
            } catch (Exception e) {
                log.warn("Error closing Spark context", e);
            }
        }
    }

    public boolean isSparkAvailable() {
        return sparkAvailable;
    }

    /**
     * 로그 분석 - 에러 패턴 분석
     */
    public ApiResponse<Map<String, Long>> analyzeErrorPatterns(String logContent) {
        if (!sparkAvailable) {
            log.warn("Spark context not available, using fallback analysis");
            return ApiResponse.onSuccess(fallbackAnalysis(logContent));
        }

        try {
            // 로그를 라인별로 분리
            JavaRDD<String> logLines = sparkContext.parallelize(
                Arrays.asList(logContent.split("\n"))
            );

            // ERROR 레벨 로그만 필터링하고 카운트
            Map<String, Long> errorPatterns = logLines
                .filter(line -> line.contains("ERROR") || line.contains("Exception"))
                .map(line -> extractErrorType(line))
                .countByValue();

            return ApiResponse.onSuccess(errorPatterns);
        } catch (Exception e) {
            log.error("Spark analysis failed, using fallback", e);
            return ApiResponse.onFailure(HttpStatus.INTERNAL_SERVER_ERROR, "500", "Spark analysis failed", fallbackAnalysis(logContent));
        }
    }

    /**
     * 로그 통계 분석
     */
    public ApiResponse<Map<String, Object>> analyzeLogStatistics(String logContent) {
        if (!sparkAvailable) {
            return ApiResponse.onSuccess(fallbackStatistics(logContent));
        }

        Map<String, Object> stats = new HashMap<>();

        try {
            JavaRDD<String> logLines = sparkContext.parallelize(
                Arrays.asList(logContent.split("\n"))
            );

            // 로그 레벨별 카운트
            Map<String, Long> levelCounts = logLines
                .map(this::extractLogLevel)
                .countByValue();

            stats.put("levelCounts", levelCounts);
            stats.put("totalLines", logLines.count());
            stats.put("errorRate", calculateErrorRate(levelCounts));

            // 시간대별 분포 (간단한 예시)
            Map<Integer, Long> hourlyDistribution = logLines
                .map(this::extractHour)
                .filter(hour -> hour >= 0)
                .countByValue();

            stats.put("hourlyDistribution", hourlyDistribution);
            return ApiResponse.onSuccess(stats);
        } catch (Exception e) {
            log.error("Statistics analysis failed", e);
            return ApiResponse.onFailure(HttpStatus.INTERNAL_SERVER_ERROR, "500", "Statistics analysis failed", fallbackStatistics(logContent));
        }
    }

    /**
     * Spark를 사용한 집중도 분석
     */
    public ApiResponse<FocusAnalysisResult> analyzeFocusData(OnDeviceLogDto logDto) {
        if (!sparkAvailable) {
            log.warn("Spark context not available, using fallback focus analysis");
            return ApiResponse.onSuccess(createFallbackFocusAnalysis(logDto));
        }

        try {
            log.info("🔥 Starting Spark-based focus analysis for user: {}, focusScore: {}",
                logDto.getUserId(), logDto.getFocusScore());

            // 집중도 데이터를 Spark RDD로 변환
            JavaRDD<Double> focusScoreRDD = sparkContext.parallelize(
                Arrays.asList(logDto.getFocusScore())
            );

            // Spark를 통한 통계 계산
            long totalRecords = focusScoreRDD.count();
            double currentScore = logDto.getFocusScore();
            double averageScore = focusScoreRDD.reduce((a, b) -> a + b) / totalRecords;

            // 집중도 분산 계산
            double variance = focusScoreRDD
                .map(score -> Math.pow(score - averageScore, 2))
                .reduce((a, b) -> a + b) / totalRecords;

            // 트렌드 분석 (Spark 기반)
            String trend = analyzeTrendWithSpark(focusScoreRDD, currentScore);

            // 추천사항 생성
            String recommendation = generateRecommendation(currentScore, averageScore);

            // 집중 중단 횟수 계산 (임계값 기반)
            long focusBreakCount = focusScoreRDD
                .filter(score -> score < 60.0)
                .count();

            FocusAnalysisResult result = FocusAnalysisResult.builder()
                .currentScore(currentScore)
                .averageScore(averageScore)
                .trend(trend)
                .confidence(0.9) // Spark 분석이므로 높은 신뢰도
                .summary(String.format("Spark 분석 완료: 현재 집중도 %.1f, 평균 %.1f, 트렌드 %s",
                    currentScore, averageScore, trend))
                .recommendation(recommendation)
                .sessionDuration(30) // 기본값
                .focusBreakCount((int) focusBreakCount)
                .focusVariance(variance)
                .build();

            log.info("✅ Spark focus analysis completed: score={}, trend={}, variance={}",
                currentScore, trend, variance);

            return ApiResponse.onSuccess(result);

        } catch (Exception e) {
            log.error("❌ Spark focus analysis failed, using fallback", e);
            return ApiResponse.onFailure(HttpStatus.INTERNAL_SERVER_ERROR, "500",
                "Spark analysis failed", createFallbackFocusAnalysis(logDto));
        }
    }

    private String analyzeTrendWithSpark(JavaRDD<Double> focusScoreRDD, double currentScore) {
        try {
            // 평균과 현재 점수 비교
            double average = focusScoreRDD.reduce((a, b) -> a + b) / focusScoreRDD.count();

            if (currentScore > average + 10) {
                return "INCREASING";
            } else if (currentScore < average - 10) {
                return "DECREASING";
            } else {
                return "STABLE";
            }
        } catch (Exception e) {
            log.warn("Trend analysis failed, using stable", e);
            return "STABLE";
        }
    }

    private String generateRecommendation(double currentScore, double averageScore) {
        if (currentScore >= 80) {
            return "훌륭한 집중력을 보이고 있습니다! 이 상태를 유지해보세요.";
        } else if (currentScore >= 60) {
            return "양호한 집중 상태입니다. 조금 더 집중해보세요.";
        } else if (currentScore >= 40) {
            return "집중력이 떨어지고 있습니다. 잠시 휴식을 취하거나 환경을 점검해보세요.";
        } else {
            return "집중력이 매우 낮습니다. 휴식을 취하거나 학습 환경을 개선해보세요.";
        }
    }

    private FocusAnalysisResult createFallbackFocusAnalysis(OnDeviceLogDto logDto) {
        double focusScore = logDto.getFocusScore() != null ? logDto.getFocusScore() : 50.0;

        String trend = "STABLE";
        if (focusScore >= 80) {
            trend = "INCREASING";
        } else if (focusScore <= 40) {
            trend = "DECREASING";
        }

        return FocusAnalysisResult.builder()
            .currentScore(focusScore)
            .averageScore(focusScore)
            .trend(trend)
            .confidence(0.6) // 낮은 신뢰도 (fallback)
            .summary("Fallback 분석 완료")
            .recommendation("지속적인 모니터링 권장")
            .sessionDuration(30)
            .focusBreakCount(0)
            .focusVariance(5.0)
            .build();
    }


    // 헬퍼 메서드들
    private String extractErrorType(String line) {
        if (line.contains("NullPointerException")) return "NullPointerException";
        if (line.contains("SQLException")) return "SQLException";
        if (line.contains("IOException")) return "IOException";
        if (line.contains("IllegalArgumentException")) return "IllegalArgumentException";
        if (line.contains("Exception")) return "GenericException";
        return "ERROR";
    }

    private String extractLogLevel(String line) {
        if (line.contains("ERROR")) return "ERROR";
        if (line.contains("WARN")) return "WARN";
        if (line.contains("INFO")) return "INFO";
        if (line.contains("DEBUG")) return "DEBUG";
        return "UNKNOWN";
    }

    private Integer extractHour(String line) {
        // 간단한 시간 추출 (HH:mm:ss 패턴)
        try {
            int timeIndex = line.indexOf(":");
            if (timeIndex > 2) {
                String hourStr = line.substring(timeIndex - 2, timeIndex);
                return Integer.parseInt(hourStr.trim());
            }
        } catch (Exception e) {
            // 파싱 실패시 무시
        }
        return -1;
    }

    private double calculateErrorRate(Map<String, Long> levelCounts) {
        long totalLogs = levelCounts.values().stream().mapToLong(Long::longValue).sum();
        long errorLogs = levelCounts.getOrDefault("ERROR", 0L);
        return totalLogs > 0 ? (double) errorLogs / totalLogs * 100 : 0;
    }

    // Spark 없이 기본 분석 (Fallback)
    private Map<String, Long> fallbackAnalysis(String logContent) {
        Map<String, Long> patterns = new HashMap<>();
        String[] lines = logContent.split("\n");

        for (String line : lines) {
            if (line.contains("ERROR") || line.contains("Exception")) {
                String errorType = extractErrorType(line);
                patterns.merge(errorType, 1L, Long::sum);
            }
        }

        return patterns;
    }

    private Map<String, Object> fallbackStatistics(String logContent) {
        Map<String, Object> stats = new HashMap<>();
        String[] lines = logContent.split("\n");

        Map<String, Long> levelCounts = new HashMap<>();
        for (String line : lines) {
            String level = extractLogLevel(line);
            levelCounts.merge(level, 1L, Long::sum);
        }

        stats.put("levelCounts", levelCounts);
        stats.put("totalLines", lines.length);
        stats.put("errorRate", calculateErrorRate(levelCounts));

        return stats;
    }
}