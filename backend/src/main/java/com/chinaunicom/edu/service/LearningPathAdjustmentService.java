package com.chinaunicom.edu.service;

import com.chinaunicom.edu.entity.LearningPath;
import java.util.Map;

/**
 * 学习路径动态调整服务
 * 根据用户学习表现自动调整学习路径难度和内容
 */
public interface LearningPathAdjustmentService {

    /**
     * 分析用户学习表现并调整路径
     * @param userId 用户ID
     * @param learningPath 当前学习路径
     * @return 调整后的学习路径
     */
    LearningPath adjustPathDifficulty(Long userId, LearningPath learningPath);

    /**
     * 评估用户当前学习表现
     * @param userId 用户ID
     * @return 表现评估结果
     */
    Map<String, Object> evaluatePerformance(Long userId);

    /**
     * 计算适合的难度系数
     * @param performanceMetrics 性能指标
     * @return 难度系数 (0.5-2.0)
     */
    double calculateOptimalDifficulty(Map<String, Object> performanceMetrics);

    /**
     * 生成备选学习路径
     * @param userId 用户ID
     * @param currentPath 当前路径
     * @param difficultyLevel 目标难度等级
     * @return 备选路径列表
     */
    Map<String, Object> generateAlternativePaths(Long userId, LearningPath currentPath, String difficultyLevel);

    /**
     * 获取路径调整建议
     * @param userId 用户ID
     * @param learningPath 学习路径
     * @return 调整建议
     */
    Map<String, Object> getPathAdjustmentSuggestions(Long userId, LearningPath learningPath);
}
