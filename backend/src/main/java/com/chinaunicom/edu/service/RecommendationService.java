package com.chinaunicom.edu.service;

import java.util.List;
import java.util.Map;

/**
 * 个性推荐服务接口
 */
public interface RecommendationService {
    
    /**
     * 生成个性化推荐
     * @param userId 用户ID
     * @param limit 返回推荐数量
     * @return 推荐课程列表
     */
    List<Map<String, Object>> generateRecommendations(Long userId, Integer limit);
    
    /**
     * 根据推荐类型生成推荐
     * @param userId 用户ID
     * @param type 推荐类型
     * @param limit 返回推荐数量
     * @return 推荐课程列表
     */
    List<Map<String, Object>> generateRecommendationsByType(Long userId, String type, Integer limit);
    
    /**
     * 获取热门课程推荐
     * @param limit 返回数量
     * @return 热门课程列表
     */
    List<Map<String, Object>> getTrendingCourses(Integer limit);
    
    /**
     * 记录用户行为
     * @param userId 用户ID
     * @param courseId 课程ID
     * @param behaviorType 行为类型
     * @param behaviorValue 行为权重
     */
    void recordUserBehavior(Long userId, Long courseId, String behaviorType, Double behaviorValue);
    
    /**
     * 更新用户偏好
     * @param userId 用户ID
     * @param preferenceType 偏好类型
     * @param preferenceValue 偏好值
     * @param score 偏好分数
     */
    void updateUserPreference(Long userId, String preferenceType, String preferenceValue, Double score);
    
    /**
     * 获取用户学习路径推荐
     * @param userId 用户ID
     * @return 学习路径列表
     */
    List<Map<String, Object>> getLearningPathRecommendations(Long userId);
}