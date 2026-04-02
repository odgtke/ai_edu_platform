package com.chinaunicom.edu.service.recommendation.impl;

import com.chinaunicom.edu.entity.Course;
import com.chinaunicom.edu.entity.UserPreference;
import com.chinaunicom.edu.mapper.UserPreferenceMapper;
import com.chinaunicom.edu.service.recommendation.AbstractRecommendationStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 基于内容的推荐策略
 * 策略模式具体实现：根据用户偏好和课程内容匹配度推荐
 */
@Component
public class ContentBasedStrategy extends AbstractRecommendationStrategy {
    
    @Autowired
    private UserPreferenceMapper userPreferenceMapper;
    
    @Override
    public String getStrategyName() {
        return "CONTENT_BASED";
    }
    
    @Override
    public double calculateScore(Long userId, Long courseId) {
        // 获取用户偏好（模拟实现）
        // List<UserPreference> preferences = userPreferenceMapper.selectByUserId(userId);
        List<UserPreference> preferences = new ArrayList<>(); // 模拟空列表
        if (preferences.isEmpty()) {
            return 0.0;
        }
        
        Course course = courseService.getById(courseId);
        if (course == null) {
            return 0.0;
        }
        
        // 计算内容匹配度（简化实现）
        double score = 0.0;
        
        // 类别匹配（使用courseName模拟类别）
        String courseName = course.getCourseName();
        if (courseName != null && courseName.contains("编程")) {
            score += 50.0;
        }
        
        // 难度匹配（使用description模拟难度）
        String description = course.getDescription();
        if (description != null && description.contains("入门")) {
            score += 30.0;
        }
        
        return Math.min(score, MAX_SCORE);
    }
    
    @Override
    protected String generateReason(Course course, double score) {
        return "根据您的学习偏好推荐";
    }
    
    @Override
    public boolean isSupported(Long userId) {
        // 简化实现：始终支持
        return true;
    }
}
