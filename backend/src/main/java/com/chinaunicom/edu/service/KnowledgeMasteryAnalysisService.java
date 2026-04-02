package com.chinaunicom.edu.service;

import com.chinaunicom.edu.entity.KnowledgePoint;
import com.chinaunicom.edu.entity.UserKnowledgeMastery;
import java.util.List;
import java.util.Map;

/**
 * 知识掌握度分析服务
 * 提供知识点掌握情况的分析和可视化数据
 */
public interface KnowledgeMasteryAnalysisService {

    /**
     * 获取用户知识点掌握度概览
     * @param userId 用户ID
     * @return 掌握度分析数据
     */
    Map<String, Object> getUserKnowledgeOverview(Long userId);

    /**
     * 获取分类掌握度分析
     * @param userId 用户ID
     * @return 各分类掌握度数据
     */
    List<Map<String, Object>> getCategoryMasteryAnalysis(Long userId);

    /**
     * 获取知识点掌握度详情
     * @param userId 用户ID
     * @param categoryId 分类ID (可选)
     * @return 知识点详细掌握情况
     */
    List<Map<String, Object>> getKnowledgePointDetails(Long userId, Long categoryId);

    /**
     * 生成掌握度雷达图数据
     * @param userId 用户ID
     * @return 雷达图所需数据
     */
    Map<String, Object> generateRadarChartData(Long userId);

    /**
     * 分析薄弱知识点
     * @param userId 用户ID
     * @return 薄弱知识点列表和改进建议
     */
    Map<String, Object> analyzeWeakPoints(Long userId);

    /**
     * 预测学习效果
     * @param userId 用户ID
     * @param studyPlan 学习计划
     * @return 预测结果
     */
    Map<String, Object> predictLearningOutcome(Long userId, Map<String, Object> studyPlan);

    /**
     * 更新知识点掌握度
     * @param userId 用户ID
     * @param knowledgePointId 知识点ID
     * @param score 本次学习得分
     * @param studyDuration 学习时长(分钟)
     * @param learningMethod 学习方式
     */
    void updateKnowledgeMastery(Long userId, Long knowledgePointId, 
                               Double score, Integer studyDuration, String learningMethod);
}
