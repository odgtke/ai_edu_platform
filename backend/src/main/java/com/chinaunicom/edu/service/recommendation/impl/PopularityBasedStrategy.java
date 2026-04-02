package com.chinaunicom.edu.service.recommendation.impl;

import com.chinaunicom.edu.entity.Course;
import com.chinaunicom.edu.service.recommendation.AbstractRecommendationStrategy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 基于热度的推荐策略
 * 策略模式具体实现：根据课程热度（选课人数、评分）推荐
 */
@Component
public class PopularityBasedStrategy extends AbstractRecommendationStrategy {
    
    @Override
    public String getStrategyName() {
        return "POPULARITY";
    }
    
    @Override
    public double calculateScore(Long userId, Long courseId) {
        Course course = courseService.getById(courseId);
        if (course == null) {
            return 0.0;
        }
        
        // 热度分数 = 基于课程ID生成模拟热度分数
        // 实际项目中从数据库查询真实选课人数和评分
        double mockEnrollmentScore = (courseId % 100) * 0.5; // 模拟选课人数分数
        double mockRatingScore = 20 + (courseId % 30); // 模拟评分分数 (20-50)
        
        return Math.min(mockEnrollmentScore + mockRatingScore, MAX_SCORE);
    }
    
    @Override
    protected String generateReason(Course course, double score) {
        // 基于分数生成推荐理由
        if (score > 70) {
            return "热门课程，已有 " + (int)(score * 2) + " 人学习";
        }
        return "评分较高的优质课程";
    }
    
    @Override
    protected List<Course> getCandidateCourses(Long userId) {
        // 返回所有启用的课程作为候选
        return courseService.list();
    }
}
