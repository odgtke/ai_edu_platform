package com.chinaunicom.edu.course.service;

import com.chinaunicom.edu.course.entity.RecommendationResult;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A/B测试服务
 */
@Slf4j
@Service
public class ABTestService {
    
    /**
     * 实验配置
     */
    private static final Map<String, ExperimentConfig> EXPERIMENTS = new ConcurrentHashMap<>();
    
    /**
     * 实验日志
     */
    private final Map<Long, ExperimentLog> experimentLogs = new ConcurrentHashMap<>();
    
    static {
        // 初始化实验配置
        EXPERIMENTS.put("algorithm_v2", ExperimentConfig.builder()
                .experimentId("algorithm_v2")
                .experimentName("推荐算法V2测试")
                .controlGroupRatio(0.5)
                .treatmentGroupRatio(0.5)
                .build());
    }
    
    /**
     * 分配实验组
     */
    public String assignExperimentGroup(Long userId, String experimentId) {
        ExperimentConfig config = EXPERIMENTS.get(experimentId);
        if (config == null) {
            return "control";
        }
        
        // 基于用户ID哈希分配
        int hash = Math.abs(userId.hashCode()) % 100;
        if (hash < config.getTreatmentGroupRatio() * 100) {
            return "treatment"; // 实验组
        }
        return "control"; // 对照组
    }
    
    /**
     * 记录实验曝光
     */
    public void logExposure(Long userId, String experimentId, String group, 
                           RecommendationResult recommendation) {
        ExperimentLog experimentLog = ExperimentLog.builder()
                .userId(userId)
                .experimentId(experimentId)
                .group(group)
                .recommendationId(recommendation.getCourseId())
                .exposureTime(new Date())
                .isClicked(false)
                .isEnrolled(false)
                .build();
        
        experimentLogs.put(System.currentTimeMillis(), experimentLog);
        log.debug("记录实验曝光: userId={}, experiment={}, group={}", 
                 userId, experimentId, group);
    }
    
    /**
     * 记录点击
     */
    public void logClick(Long userId, Long recommendationId) {
        experimentLogs.values().stream()
                .filter(log -> log.getUserId().equals(userId) 
                        && log.getRecommendationId().equals(recommendationId))
                .forEach(log -> log.setIsClicked(true));
    }
    
    /**
     * 记录报名
     */
    public void logEnroll(Long userId, Long recommendationId) {
        experimentLogs.values().stream()
                .filter(log -> log.getUserId().equals(userId) 
                        && log.getRecommendationId().equals(recommendationId))
                .forEach(log -> log.setIsEnrolled(true));
    }
    
    /**
     * 计算实验结果
     */
    public ExperimentResult calculateResult(String experimentId) {
        List<ExperimentLog> logs = experimentLogs.values().stream()
                .filter(log -> log.getExperimentId().equals(experimentId))
                .collect(java.util.stream.Collectors.toList());
        
        // 对照组统计
        List<ExperimentLog> controlLogs = logs.stream()
                .filter(log -> "control".equals(log.getGroup()))
                .collect(java.util.stream.Collectors.toList());
        
        // 实验组统计
        List<ExperimentLog> treatmentLogs = logs.stream()
                .filter(log -> "treatment".equals(log.getGroup()))
                .collect(java.util.stream.Collectors.toList());
        
        return ExperimentResult.builder()
                .experimentId(experimentId)
                .controlGroup(calculateMetrics(controlLogs))
                .treatmentGroup(calculateMetrics(treatmentLogs))
                .build();
    }
    
    /**
     * 计算指标
     */
    private GroupMetrics calculateMetrics(List<ExperimentLog> logs) {
        if (logs.isEmpty()) {
            return GroupMetrics.builder().build();
        }
        
        long total = logs.size();
        long clicked = logs.stream().filter(ExperimentLog::getIsClicked).count();
        long enrolled = logs.stream().filter(ExperimentLog::getIsEnrolled).count();
        
        return GroupMetrics.builder()
                .totalExposures(total)
                .clicks(clicked)
                .enrolls(enrolled)
                .ctr((double) clicked / total)
                .cvr((double) enrolled / total)
                .build();
    }
    
    /**
     * 实验配置
     */
    @Data
    @Builder
    public static class ExperimentConfig {
        private String experimentId;
        private String experimentName;
        private double controlGroupRatio;
        private double treatmentGroupRatio;
    }
    
    /**
     * 实验日志
     */
    @Data
    @Builder
    public static class ExperimentLog {
        private Long userId;
        private String experimentId;
        private String group;
        private Long recommendationId;
        private Date exposureTime;
        private Boolean isClicked;
        private Boolean isEnrolled;
    }
    
    /**
     * 实验结果
     */
    @Data
    @Builder
    public static class ExperimentResult {
        private String experimentId;
        private GroupMetrics controlGroup;
        private GroupMetrics treatmentGroup;
    }
    
    /**
     * 分组指标
     */
    @Data
    @Builder
    public static class GroupMetrics {
        private long totalExposures;
        private long clicks;
        private long enrolls;
        private double ctr;
        private double cvr;
    }
}
