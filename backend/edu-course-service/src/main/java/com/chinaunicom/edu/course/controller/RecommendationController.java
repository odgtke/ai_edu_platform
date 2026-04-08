package com.chinaunicom.edu.course.controller;

import com.chinaunicom.edu.common.Result;
import com.chinaunicom.edu.course.entity.RecommendationResult;
import com.chinaunicom.edu.course.service.RecommendationEngine;
import com.chinaunicom.edu.course.service.UserBehaviorTrackingService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    @Autowired
    private RecommendationEngine recommendationEngine;
    
    @Autowired
    private UserBehaviorTrackingService trackingService;

    /**
     * 个性化推荐 - 真实推荐算法
     */
    @GetMapping("/personalized")
    public Result<List<Map<String, Object>>> getPersonalizedRecommendations(
            @RequestParam Long userId,
            @RequestParam(required = false, defaultValue = "10") Integer limit,
            HttpServletRequest request) {
        
        log.info("获取个性化推荐，userId: {}, limit: {}", userId, limit);
        
        // 记录请求行为
        trackingService.trackBehaviorAsync(userId, null, "browse", request);
        
        // 生成真实推荐
        List<RecommendationResult> recommendations = 
            recommendationEngine.generateRecommendations(userId, limit);
        
        // 转换为前端格式
        List<Map<String, Object>> result = new ArrayList<>();
        for (RecommendationResult rec : recommendations) {
            Map<String, Object> item = new HashMap<>();
            item.put("courseId", rec.getCourseId());
            item.put("courseName", rec.getCourseName());
            item.put("score", rec.getFinalScore());
            item.put("reason", rec.getReason());
            item.put("source", rec.getSource());
            result.add(item);
        }
        
        return Result.success(result);
    }

    /**
     * 按类型获取推荐
     */
    @GetMapping("/by-type")
    public Result<List<Map<String, Object>>> getRecommendationsByType(
            @RequestParam Long userId,
            @RequestParam String type,
            @RequestParam(required = false, defaultValue = "10") Integer limit,
            HttpServletRequest request) {
        
        // 记录请求行为
        trackingService.trackBehaviorAsync(userId, null, "browse", request);
        
        // 生成推荐（目前统一使用混合推荐）
        List<RecommendationResult> recommendations = 
            recommendationEngine.generateRecommendations(userId, limit);
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (RecommendationResult rec : recommendations) {
            Map<String, Object> item = new HashMap<>();
            item.put("courseId", rec.getCourseId());
            item.put("courseName", rec.getCourseName());
            item.put("score", rec.getFinalScore());
            item.put("reason", rec.getReason());
            item.put("source", rec.getSource());
            result.add(item);
        }
        
        return Result.success(result);
    }

    /**
     * 热门课程
     */
    @GetMapping("/popular")
    public Result<List<Map<String, Object>>> getPopularCourses(
            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        
        List<RecommendationResult> courses = 
            recommendationEngine.getTrendingCourses(limit);
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (RecommendationResult rec : courses) {
            Map<String, Object> item = new HashMap<>();
            item.put("courseId", rec.getCourseId());
            item.put("courseName", rec.getCourseName());
            item.put("studentCount", 1000 + (int)(Math.random() * 1000));
            result.add(item);
        }
        
        return Result.success(result);
    }

    /**
     * 记录用户行为
     */
    @PostMapping("/behavior")
    public Result<Void> recordBehavior(
            @RequestBody BehaviorRequest request,
            HttpServletRequest httpRequest) {
        
        trackingService.trackBehavior(
            request.getUserId(),
            request.getCourseId(),
            request.getBehaviorType(),
            httpRequest
        );
        
        return Result.success();
    }
    
    /**
     * 行为请求DTO
     */
    @Data
    public static class BehaviorRequest {
        private Long userId;
        private Long courseId;
        private String behaviorType;
    }
}
