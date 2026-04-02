package com.chinaunicom.edu.controller;

import com.chinaunicom.edu.common.Result;
import com.chinaunicom.edu.service.RecommendationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 个性推荐API控制器
 */
@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {
    
    private static final Logger logger = LoggerFactory.getLogger(RecommendationController.class);
    
    @Autowired
    private RecommendationService recommendationService;
    
    /**
     * 获取个性化推荐
     */
    @GetMapping("/personalized")
    public Result<List<Map<String, Object>>> getPersonalizedRecommendations(
            @RequestParam Long userId,
            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        try {
            List<Map<String, Object>> recommendations = recommendationService.generateRecommendations(userId, limit);
            return Result.success(recommendations);
        } catch (Exception e) {
            logger.error("Failed to get personalized recommendations: {}", e.getMessage());
            return Result.error("获取个性化推荐失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据推荐类型获取推荐
     */
    @GetMapping("/by-type")
    public Result<List<Map<String, Object>>> getRecommendationsByType(
            @RequestParam Long userId,
            @RequestParam String type,
            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        try {
            List<Map<String, Object>> recommendations = recommendationService.generateRecommendationsByType(userId, type, limit);
            return Result.success(recommendations);
        } catch (Exception e) {
            logger.error("Failed to get {} recommendations: {}", type, e.getMessage());
            return Result.error("获取" + type + "推荐失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取热门课程推荐
     */
    @GetMapping("/trending")
    public Result<List<Map<String, Object>>> getTrendingCourses(
            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        try {
            List<Map<String, Object>> trendingCourses = recommendationService.getTrendingCourses(limit);
            return Result.success(trendingCourses);
        } catch (Exception e) {
            logger.error("Failed to get trending courses: {}", e.getMessage());
            return Result.error("获取热门课程失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取学习路径推荐
     */
    @GetMapping("/learning-paths")
    public Result<List<Map<String, Object>>> getLearningPathRecommendations(
            @RequestParam Long userId) {
        try {
            List<Map<String, Object>> paths = recommendationService.getLearningPathRecommendations(userId);
            return Result.success(paths);
        } catch (Exception e) {
            logger.error("Failed to get learning path recommendations: {}", e.getMessage());
            return Result.error("获取学习路径推荐失败: " + e.getMessage());
        }
    }
    
    /**
     * 记录用户行为
     */
    @PostMapping("/behavior")
    public Result<Void> recordUserBehavior(
            @RequestParam Long userId,
            @RequestParam Long courseId,
            @RequestParam String behaviorType,
            @RequestParam(defaultValue = "1.0") Double behaviorValue) {
        try {
            recommendationService.recordUserBehavior(userId, courseId, behaviorType, behaviorValue);
            return Result.success();
        } catch (Exception e) {
            logger.error("Failed to record user behavior: {}", e.getMessage());
            return Result.error("记录用户行为失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新用户偏好
     */
    @PostMapping("/preference")
    public Result<Void> updateUserPreference(
            @RequestParam Long userId,
            @RequestParam String preferenceType,
            @RequestParam String preferenceValue,
            @RequestParam(defaultValue = "1.0") Double score) {
        try {
            recommendationService.updateUserPreference(userId, preferenceType, preferenceValue, score);
            return Result.success();
        } catch (Exception e) {
            logger.error("Failed to update user preference: {}", e.getMessage());
            return Result.error("更新用户偏好失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取推荐统计信息
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getRecommendationStatistics(
            @RequestParam Long userId) {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("userId", userId);
            stats.put("totalRecommendations", 50); // 示例数据
            stats.put("clickedRecommendations", 15);
            stats.put("enrolledRecommendations", 8);
            stats.put("clickThroughRate", "30%");
            return Result.success(stats);
        } catch (Exception e) {
            logger.error("Failed to get recommendation statistics: {}", e.getMessage());
            return Result.error("获取推荐统计失败: " + e.getMessage());
        }
    }
    
    /**
     * 测试推荐系统连通性
     */
    @GetMapping("/health")
    public Result<String> healthCheck() {
        return Result.success("Recommendation system is running");
    }
}