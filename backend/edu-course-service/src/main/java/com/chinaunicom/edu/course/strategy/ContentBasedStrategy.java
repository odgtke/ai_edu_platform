package com.chinaunicom.edu.course.strategy;

import com.chinaunicom.edu.course.entity.Course;
import com.chinaunicom.edu.course.entity.RecommendationResult;
import com.chinaunicom.edu.course.entity.UserBehavior;
import com.chinaunicom.edu.course.entity.UserPreference;
import com.chinaunicom.edu.course.mapper.CourseMapper;
import com.chinaunicom.edu.course.mapper.UserBehaviorMapper;
import com.chinaunicom.edu.course.mapper.UserPreferenceMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ContentBasedStrategy implements RecommendationStrategy {
    
    @Autowired
    private UserPreferenceMapper preferenceMapper;
    
    @Autowired
    private UserBehaviorMapper behaviorMapper;
    
    @Autowired
    private CourseMapper courseMapper;
    
    @Override
    public String getStrategyName() {
        return "content";
    }
    
    @Override
    public List<RecommendationResult> recommend(Long userId, int limit) {
        log.info("内容推荐，userId: {}, limit: {}", userId, limit);
        
        // 1. 获取用户偏好
        List<UserPreference> preferences = preferenceMapper.findByUserId(userId);
        
        if (preferences.isEmpty()) {
            log.warn("用户 {} 无偏好数据", userId);
            return Collections.emptyList();
        }
        
        // 2. 构建偏好向量
        Map<String, Double> preferenceVector = buildPreferenceVector(preferences);
        
        // 3. 获取已学课程
        Set<Long> learnedCourses = getLearnedCourses(userId);
        
        // 4. 计算候选课程匹配度
        List<Course> candidates = courseMapper.selectList(null); // 查询所有课程
        List<RecommendationResult> results = new ArrayList<>();
        
        for (Course course : candidates) {
            if (learnedCourses.contains(course.getCourseId())) {
                continue; // 跳过已学课程
            }
            
            double score = calculateContentScore(course, preferenceVector);
            if (score > 0.3) { // 阈值过滤
                results.add(RecommendationResult.builder()
                        .courseId(course.getCourseId())
                        .courseName(course.getCourseName())
                        .score(score)
                        .finalScore(score)
                        .source(getStrategyName())
                        .reason("符合您的" + getTopPreference(preferences) + "偏好")
                        .generatedTime(LocalDateTime.now())
                        .build());
            }
        }
        
        return results.stream()
                .sorted(Comparator.comparing(RecommendationResult::getScore).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    /**
     * 构建偏好向量
     */
    private Map<String, Double> buildPreferenceVector(List<UserPreference> preferences) {
        Map<String, Double> vector = new HashMap<>();
        for (UserPreference pref : preferences) {
            String key = pref.getPreferenceType() + ":" + pref.getPreferenceValue();
            vector.put(key, pref.getPreferenceScore());
        }
        return vector;
    }
    
    /**
     * 获取已学课程
     */
    private Set<Long> getLearnedCourses(Long userId) {
        List<UserBehavior> behaviors = behaviorMapper.findByUserId(userId);
        return behaviors.stream()
                .filter(b -> Arrays.asList("study", "complete").contains(b.getBehaviorType()))
                .map(UserBehavior::getCourseId)
                .collect(Collectors.toSet());
    }
    
    /**
     * 计算内容匹配分数
     */
    private double calculateContentScore(Course course, Map<String, Double> preferences) {
        double score = 0.0;
        
        // 类别匹配（40%权重）
        if (course.getCategory() != null) {
            String categoryKey = "course_category:" + course.getCategory();
            if (preferences.containsKey(categoryKey)) {
                score += preferences.get(categoryKey) * 0.4;
            }
        }
        
        // 这里可以根据实际课程字段扩展更多匹配逻辑
        
        return Math.min(score, 1.0);
    }
    
    /**
     * 获取用户最高偏好
     */
    private String getTopPreference(List<UserPreference> preferences) {
        return preferences.stream()
                .max(Comparator.comparing(UserPreference::getPreferenceScore))
                .map(UserPreference::getPreferenceValue)
                .orElse("学习");
    }
}
