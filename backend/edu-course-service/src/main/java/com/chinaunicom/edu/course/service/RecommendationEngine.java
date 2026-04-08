package com.chinaunicom.edu.course.service;

import com.chinaunicom.edu.course.entity.RecommendationResult;
import java.util.List;

public interface RecommendationEngine {
    
    /**
     * 生成个性化推荐
     */
    List<RecommendationResult> generateRecommendations(Long userId, int limit);
    
    /**
     * 获取热门课程
     */
    List<RecommendationResult> getTrendingCourses(int limit);
    
    /**
     * 刷新用户推荐
     */
    void refreshRecommendations(Long userId);
}
