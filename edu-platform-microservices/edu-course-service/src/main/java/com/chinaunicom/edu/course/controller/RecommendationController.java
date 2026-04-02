package com.chinaunicom.edu.course.controller;

import com.chinaunicom.edu.common.Result;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private static final String[] COURSE_NAMES = {
        "Python编程入门", "Java高级特性", "前端开发实战", "数据分析基础",
        "机器学习入门", "深度学习进阶", "数据库设计", "云计算架构",
        "网络安全基础", "项目管理实战", "人工智能导论", "大数据技术"
    };

    private static final String[] RECOMMEND_REASONS = {
        "基于您的学习历史推荐", "与您学过的课程高度相关", "热门课程，值得学习",
        "拓展知识面的好选择", "符合您的职业规划", "同学都在学的课程"
    };

    @GetMapping("/personalized")
    public Result<List<Map<String, Object>>> getPersonalizedRecommendations(
            @RequestParam Long userId,
            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        List<Map<String, Object>> recommendations = new ArrayList<>();
        int count = Math.min(limit, COURSE_NAMES.length);
        
        for (int i = 0; i < count; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("courseId", (long) (i + 1));
            item.put("courseName", COURSE_NAMES[i]);
            item.put("score", Math.max(0.5, 0.95 - (i * 0.08)));
            item.put("reason", RECOMMEND_REASONS[i % RECOMMEND_REASONS.length]);
            item.put("studentCount", 1000 + (i * 150));
            recommendations.add(item);
        }
        return Result.success(recommendations);
    }

    @GetMapping("/by-type")
    public Result<List<Map<String, Object>>> getRecommendationsByType(
            @RequestParam Long userId,
            @RequestParam String type,
            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        return getPersonalizedRecommendations(userId, limit);
    }

    @GetMapping("/popular")
    public Result<List<Map<String, Object>>> getPopularCourses(
            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        List<Map<String, Object>> courses = new ArrayList<>();
        int count = Math.min(limit, COURSE_NAMES.length);
        
        for (int i = 0; i < count; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("courseId", (long) (i + 1));
            item.put("courseName", COURSE_NAMES[i]);
            item.put("studentCount", 2000 - (i * 200));
            courses.add(item);
        }
        return Result.success(courses);
    }
}
