package com.chinaunicom.edu.course.strategy;

import com.chinaunicom.edu.course.entity.RecommendationResult;
import java.util.List;

/**
 * 推荐策略接口
 */
public interface RecommendationStrategy {
    
    /**
     * 生成推荐
     * @param userId 用户ID
     * @param limit 推荐数量
     * @return 推荐结果列表
     */
    List<RecommendationResult> recommend(Long userId, int limit);
    
    /**
     * 获取策略名称
     */
    String getStrategyName();
}
