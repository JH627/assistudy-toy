package com.assistudy.logprocessservice.service;

import com.assistudy.logprocessservice.dto.FocusAnalysisResult;
import com.assistudy.logprocessservice.dto.OnDeviceLogDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AnalysisService {

    /**
     * 집중도 분석
     */
    public FocusAnalysisResult analyzeFocusData(OnDeviceLogDto logDto) {
        double focusScore = logDto.getFocusScore() != null ? logDto.getFocusScore() : 50.0;

        String trend;
        if (focusScore >= 80) {
            trend = "INCREASING";
        } else if (focusScore <= 40) {
            trend = "DECREASING";
        } else {
            trend = "STABLE";
        }

        return FocusAnalysisResult.builder()
            .currentScore(focusScore)
            .averageScore(focusScore)
            .trend(trend)
            .confidence(0.85)
            .summary(String.format("분석 완료: 현재 집중도 %.1f, 트렌드 %s", focusScore, trend))
            .recommendation(generateRecommendation(focusScore))
            .sessionDuration(30)
            .focusBreakCount(focusScore < 60.0 ? 1 : 0)
            .focusVariance(5.0)
            .build();
    }

    /**
     * 로그 에러 패턴 분석
     */
    public Map<String, Long> analyzeErrorPatterns(String logContent) {
        if (logContent == null || logContent.isBlank()) {
            return new HashMap<>();
        }

        return Arrays.stream(logContent.split("\n"))
            .filter(line -> line.contains("ERROR") || line.contains("Exception"))
            .collect(Collectors.groupingBy(this::extractErrorType, Collectors.counting()));
    }

    /**
     * 로그 통계 분석
     */
    public Map<String, Object> analyzeLogStatistics(String logContent) {
        Map<String, Object> stats = new HashMap<>();

        if (logContent == null || logContent.isBlank()) {
            stats.put("levelCounts", new HashMap<>());
            stats.put("totalLines", 0);
            stats.put("errorRate", 0.0);
            return stats;
        }

        String[] lines = logContent.split("\n");

        Map<String, Long> levelCounts = Arrays.stream(lines)
            .collect(Collectors.groupingBy(this::extractLogLevel, Collectors.counting()));

        Map<Integer, Long> hourlyDistribution = Arrays.stream(lines)
            .map(this::extractHour)
            .filter(hour -> hour >= 0)
            .collect(Collectors.groupingBy(h -> h, Collectors.counting()));

        stats.put("levelCounts", levelCounts);
        stats.put("totalLines", lines.length);
        stats.put("errorRate", calculateErrorRate(levelCounts));
        stats.put("hourlyDistribution", hourlyDistribution);

        return stats;
    }

    private String generateRecommendation(double currentScore) {
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
        try {
            int colonIdx = line.indexOf(":");
            if (colonIdx > 2) {
                return Integer.parseInt(line.substring(colonIdx - 2, colonIdx).trim());
            }
        } catch (Exception ignored) {
        }
        return -1;
    }

    private double calculateErrorRate(Map<String, Long> levelCounts) {
        long total = levelCounts.values().stream().mapToLong(Long::longValue).sum();
        long errors = levelCounts.getOrDefault("ERROR", 0L);
        return total > 0 ? (double) errors / total * 100 : 0;
    }
}
