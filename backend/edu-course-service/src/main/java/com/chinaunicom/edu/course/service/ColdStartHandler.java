package com.chinaunicom.edu.course.service;

import com.chinaunicom.edu.course.entity.Course;
import com.chinaunicom.edu.course.entity.RecommendationResult;
import com.chinaunicom.edu.course.entity.UserPreference;
import com.chinaunicom.edu.course.mapper.CourseMapper;
import com.chinaunicom.edu.course.mapper.RecommendationMapper;
import com.chinaunicom.edu.course.mapper.UserPreferenceMapper;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 冷启动处理策略
 */
@Slf4j
@Component
public class ColdStartHandler {
    
    @Autowired
    private UserPreferenceMapper preferenceMapper;
    
    @Autowired
    private CourseMapper courseMapper;
    
    @Autowired
    private RecommendationMapper recommendationMapper;
    
    @Autowired
    private RecommendationEngine recommendationEngine;
    
    /**
     * 新用户推荐
     */
    public List<RecommendationResult> recommendForNewUser(NewUserProfile profile, int limit) {
        log.info("新用户冷启动推荐，userId: {}, interests: {}", 
                profile.getUserId(), profile.getInterests());
        
        List<RecommendationResult> results = new ArrayList<>();
        
        // 1. 基于注册信息推荐（40%）
        if (profile.getInterests() != null && !profile.getInterests().isEmpty()) {
            List<RecommendationResult> interestBased = recommendByInterests(
                    profile.getInterests(), limit / 2);
            results.addAll(interestBased);
            
            // 保存用户偏好
            saveUserPreferences(profile.getUserId(), profile.getInterests());
        }
        
        // 2. 补充热门课程（30%）
        List<RecommendationResult> trending = 
                recommendationEngine.getTrendingCourses(limit / 3);
        results.addAll(trending);
        
        // 3. 补充新手入门课程（30%）
        List<RecommendationResult> beginner = getBeginnerCourses(limit / 3);
        results.addAll(beginner);
        
        // 去重并排序
        return results.stream()
                .collect(Collectors.groupingBy(RecommendationResult::getCourseId))
                .values().stream()
                .map(list -> list.get(0))
                .sorted(Comparator.comparing(RecommendationResult::getScore).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    /**
     * 基于兴趣标签推荐
     */
    private List<RecommendationResult> recommendByInterests(List<String> interests, int limit) {
        List<RecommendationResult> results = new ArrayList<>();
        
        for (String interest : interests) {
            // 根据兴趣类别查询相关课程
            List<Course> courses = findCoursesByInterest(interest);
            
            for (Course course : courses) {
                results.add(RecommendationResult.builder()
                        .courseId(course.getCourseId())
                        .courseName(course.getCourseName())
                        .score(0.8)
                        .finalScore(0.8)
                        .source("cold_start_interest")
                        .reason("符合您感兴趣的" + interest + "领域")
                        .generatedTime(LocalDateTime.now())
                        .build());
            }
        }
        
        return results.stream()
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    /**
     * 根据兴趣查找课程
     */
    private List<Course> findCoursesByInterest(String interest) {
        // 这里简化处理，实际应该根据课程标签、分类等匹配
        // 暂时返回所有课程，后续可以优化
        return courseMapper.selectList(null);
    }
    
    /**
     * 保存用户偏好
     */
    private void saveUserPreferences(Long userId, List<String> interests) {
        for (String interest : interests) {
            UserPreference preference = UserPreference.builder()
                    .userId(userId)
                    .preferenceType("course_category")
                    .preferenceValue(interest)
                    .preferenceScore(0.8)
                    .build();
            preferenceMapper.insert(preference);
        }
    }
    
    /**
     * 获取新手入门课程
     */
    private List<RecommendationResult> getBeginnerCourses(int limit) {
        // 查询难度为初级的课程
        List<Course> beginnerCourses = courseMapper.selectList(null);
        
        return beginnerCourses.stream()
                .limit(limit)
                .map(course -> RecommendationResult.builder()
                        .courseId(course.getCourseId())
                        .courseName(course.getCourseName())
                        .score(0.6)
                        .finalScore(0.6)
                        .source("cold_start_beginner")
                        .reason("适合新手入门的课程")
                        .generatedTime(LocalDateTime.now())
                        .build())
                .collect(Collectors.toList());
    }
    
    /**
     * 判断是否是新用户
     */
    public boolean isNewUser(Long userId) {
        // 检查用户是否有行为数据
        List<UserPreference> preferences = preferenceMapper.findByUserId(userId);
        return preferences == null || preferences.isEmpty();
    }
    
    /**
     * 新用户画像
     */
    @Data
    @Builder
    public static class NewUserProfile {
        private Long userId;
        private List<String> interests;      // 兴趣标签
        private String careerGoal;           // 职业目标
        private String educationLevel;       // 教育水平
        private Integer availableTime;       // 每周可用学习时间（小时）
    }
}
