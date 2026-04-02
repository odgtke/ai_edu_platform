package com.chinaunicom.edu.controller;

import com.chinaunicom.edu.common.Result;
import com.chinaunicom.edu.service.ABTestService;
import com.chinaunicom.edu.service.ABTestService.GroupStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 推荐系统监控控制器
 * 用于查看推荐效果指标和A/B测试统计
 */
@RestController
@RequestMapping("/api/monitor/recommendation")
public class RecommendationMonitorController {
    
    private static final Logger logger = LoggerFactory.getLogger(RecommendationMonitorController.class);
    
    private final ABTestService abTestService;
    private final RedisTemplate<String, Object> redisTemplate;
    
    public RecommendationMonitorController(ABTestService abTestService, 
                                           RedisTemplate<String, Object> redisTemplate) {
        this.abTestService = abTestService;
        this.redisTemplate = redisTemplate;
    }
    
    /**
     * 获取A/B测试统计数据
     */
    @GetMapping("/ab-test-stats")
    public Result<Map<String, Object>> getABTestStatistics() {
        try {
            Map<String, GroupStats> stats = abTestService.getTestStatistics();
            Map<String, Object> result = new HashMap<>();
            
            for (Map.Entry<String, GroupStats> entry : stats.entrySet()) {
                Map<String, Object> groupData = new HashMap<>();
                GroupStats groupStats = entry.getValue();
                groupData.put("group", groupStats.getGroupName());
                groupData.put("impressions", groupStats.getImpressions());
                groupData.put("clicks", groupStats.getClicks());
                groupData.put("enrollments", groupStats.getEnrollments());
                groupData.put("ctr", groupStats.getClickThroughRate());
                groupData.put("cvr", groupStats.getConversionRate());
                result.put(entry.getKey(), groupData);
            }
            
            return Result.success(result);
        } catch (Exception e) {
            logger.error("Error getting A/B test statistics: {}", e.getMessage());
            return Result.error("获取A/B测试统计失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取缓存统计信息
     */
    @GetMapping("/cache-stats")
    public Result<Map<String, Object>> getCacheStatistics() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            // 获取Redis中推荐缓存的数量
            Set<String> keys = redisTemplate.keys("recommendation:*");
            int cacheCount = keys != null ? keys.size() : 0;
            
            stats.put("recommendationCacheCount", cacheCount);
            stats.put("cacheKeyPattern", "recommendation:{userId}:{limit}");
            stats.put("cacheTtlHours", 24);
            
            return Result.success(stats);
        } catch (Exception e) {
            logger.error("Error getting cache statistics: {}", e.getMessage());
            return Result.error("获取缓存统计失败: " + e.getMessage());
        }
    }
    
    /**
     * 重置A/B测试统计
     */
    @PostMapping("/reset-stats")
    public Result<String> resetStatistics() {
        try {
            abTestService.resetStatistics();
            return Result.success("A/B测试统计已重置");
        } catch (Exception e) {
            logger.error("Error resetting statistics: {}", e.getMessage());
            return Result.error("重置统计失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取推荐系统整体指标
     */
    @GetMapping("/metrics")
    public Result<Map<String, Object>> getRecommendationMetrics() {
        try {
            Map<String, Object> metrics = new HashMap<>();
            
            // A/B测试指标
            Map<String, GroupStats> abStats = abTestService.getTestStatistics();
            long totalImpressions = abStats.values().stream()
                .mapToLong(GroupStats::getImpressions)
                .sum();
            long totalClicks = abStats.values().stream()
                .mapToLong(GroupStats::getClicks)
                .sum();
            long totalEnrollments = abStats.values().stream()
                .mapToLong(GroupStats::getEnrollments)
                .sum();
            
            metrics.put("totalImpressions", totalImpressions);
            metrics.put("totalClicks", totalClicks);
            metrics.put("totalEnrollments", totalEnrollments);
            metrics.put("overallCTR", totalImpressions > 0 ? 
                (double) totalClicks / totalImpressions * 100 : 0.0);
            metrics.put("overallCVR", totalImpressions > 0 ? 
                (double) totalEnrollments / totalImpressions * 100 : 0.0);
            
            // 缓存指标
            Set<String> keys = redisTemplate.keys("recommendation:*");
            metrics.put("cachedRecommendations", keys != null ? keys.size() : 0);
            
            // 算法版本分布
            Map<String, Object> algorithmDistribution = new HashMap<>();
            algorithmDistribution.put("A", "基础混合推荐算法 (50%)");
            algorithmDistribution.put("B", "协同过滤增强算法 (30%)");
            algorithmDistribution.put("C", "知识图谱增强算法 (20%)");
            metrics.put("algorithmVersions", algorithmDistribution);
            
            return Result.success(metrics);
        } catch (Exception e) {
            logger.error("Error getting recommendation metrics: {}", e.getMessage());
            return Result.error("获取推荐指标失败: " + e.getMessage());
        }
    }
    
    /**
     * 手动清除推荐缓存
     */
    @PostMapping("/clear-cache")
    public Result<String> clearRecommendationCache() {
        try {
            Set<String> keys = redisTemplate.keys("recommendation:*");
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
            }
            return Result.success("推荐缓存已清除，共清除 " + (keys != null ? keys.size() : 0) + " 条");
        } catch (Exception e) {
            logger.error("Error clearing recommendation cache: {}", e.getMessage());
            return Result.error("清除缓存失败: " + e.getMessage());
        }
    }
}
