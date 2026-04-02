package com.chinaunicom.edu.service.recommendation;

import java.util.List;
import java.util.Map;

/**
 * 推荐策略接口
 * 策略模式：定义推荐算法的统一接口
 */
public interface RecommendationStrategy {
    
    /**
     * 获取策略名称
     */
    String getStrategyName();
    
    /**
     * 生成推荐列表
     * @param userId 用户ID
     * @param limit 推荐数量限制
     * @return 推荐结果列表
     */
    List<Map<String, Object>> generateRecommendations(Long userId, Integer limit);
    
    /**
     * 计算推荐分数
     * @param userId 用户ID
     * @param courseId 课程ID
     * @return 推荐分数 (0-100)
     */
    double calculateScore(Long userId, Long courseId);
    
    /**
     * 是否支持该用户
     * @param userId 用户ID
     * @return 是否支持
     */
    boolean isSupported(Long userId);
}
