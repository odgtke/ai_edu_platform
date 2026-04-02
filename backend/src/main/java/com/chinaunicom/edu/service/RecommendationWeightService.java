package com.chinaunicom.edu.service;

import java.util.Map;

/**
 * 推荐权重配置服务
 * 提供基于用户类型和活跃度的动态权重配置
 */
public interface RecommendationWeightService {

    /**
     * 获取用户类型的推荐权重
     *
     * @param userType 用户类型：1-学生 2-教师 3-管理员
     * @return 权重配置Map：content, collaborative, trending, knowledge
     */
    Map<String, Double> getWeightsByUserType(Integer userType);

    /**
     * 获取新用户的冷启动权重
     *
     * @return 权重配置Map
     */
    Map<String, Double> getColdStartWeights();

    /**
     * 根据用户活跃度获取权重
     *
     * @param activityScore 活跃度分数(0-100)
     * @return 权重配置Map
     */
    Map<String, Double> getWeightsByActivity(Integer activityScore);
}
