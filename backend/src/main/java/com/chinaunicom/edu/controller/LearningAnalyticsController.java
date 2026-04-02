package com.chinaunicom.edu.controller;

import com.chinaunicom.edu.common.Result;
import com.chinaunicom.edu.service.KnowledgeMasteryAnalysisService;
import com.chinaunicom.edu.service.LearningPathAdjustmentService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 学习分析控制器
 * 提供学习路径调整和知识掌握度分析功能
 */
@RestController
@RequestMapping("/api/analytics")
public class LearningAnalyticsController {

    private final LearningPathAdjustmentService pathAdjustmentService;
    private final KnowledgeMasteryAnalysisService masteryAnalysisService;

    public LearningAnalyticsController(LearningPathAdjustmentService pathAdjustmentService,
                                     KnowledgeMasteryAnalysisService masteryAnalysisService) {
        this.pathAdjustmentService = pathAdjustmentService;
        this.masteryAnalysisService = masteryAnalysisService;
    }

    /**
     * 获取学习路径调整建议
     */
    @GetMapping("/path-adjustment/{userId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public Result<Map<String, Object>> getPathAdjustmentSuggestions(@PathVariable Long userId) {
        try {
            // 获取用户当前激活的学习路径
            // 这里简化处理，实际应该查询数据库
            Map<String, Object> suggestions = pathAdjustmentService.getPathAdjustmentSuggestions(userId, null);
            return Result.success(suggestions);
        } catch (Exception e) {
            return Result.error("获取路径调整建议失败: " + e.getMessage());
        }
    }

    /**
     * 手动调整学习路径难度
     */
    @PostMapping("/adjust-path/{userId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public Result<Map<String, Object>> adjustLearningPath(@PathVariable Long userId,
                                                         @RequestBody Map<String, Object> request) {
        try {
            // 获取调整参数
            String targetLevel = (String) request.get("targetLevel");
            
            // 生成备选路径
            Map<String, Object> alternatives = pathAdjustmentService.generateAlternativePaths(
                userId, null, targetLevel);
            
            return Result.success(alternatives);
        } catch (Exception e) {
            return Result.error("路径调整失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户知识掌握度概览
     */
    @GetMapping("/knowledge-overview/{userId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public Result<Map<String, Object>> getKnowledgeOverview(@PathVariable Long userId) {
        try {
            Map<String, Object> overview = masteryAnalysisService.getUserKnowledgeOverview(userId);
            return Result.success(overview);
        } catch (Exception e) {
            return Result.error("获取知识概览失败: " + e.getMessage());
        }
    }

    /**
     * 获取分类掌握度分析（雷达图数据）
     */
    @GetMapping("/category-mastery/{userId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public Result<Map<String, Object>> getCategoryMastery(@PathVariable Long userId) {
        try {
            Map<String, Object> radarData = masteryAnalysisService.generateRadarChartData(userId);
            return Result.success(radarData);
        } catch (Exception e) {
            return Result.error("获取分类掌握度失败: " + e.getMessage());
        }
    }

    /**
     * 获取详细知识点掌握情况
     */
    @GetMapping("/knowledge-details/{userId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public Result<Map<String, Object>> getKnowledgeDetails(@PathVariable Long userId,
                                                          @RequestParam(required = false) Long categoryId) {
        try {
            Map<String, Object> details = masteryAnalysisService.analyzeWeakPoints(userId);
            return Result.success(details);
        } catch (Exception e) {
            return Result.error("获取知识点详情失败: " + e.getMessage());
        }
    }

    /**
     * 更新知识点掌握度
     */
    @PostMapping("/update-mastery")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public Result<String> updateKnowledgeMastery(@RequestBody Map<String, Object> requestData) {
        try {
            Long userId = Long.valueOf(requestData.get("userId").toString());
            Long knowledgePointId = Long.valueOf(requestData.get("knowledgePointId").toString());
            Double score = Double.valueOf(requestData.get("score").toString());
            Integer studyDuration = Integer.valueOf(requestData.get("studyDuration").toString());
            String learningMethod = (String) requestData.get("learningMethod");

            masteryAnalysisService.updateKnowledgeMastery(
                userId, knowledgePointId, score, studyDuration, learningMethod);

            return Result.success("知识点掌握度更新成功");
        } catch (Exception e) {
            return Result.error("更新失败: " + e.getMessage());
        }
    }

    /**
     * 预测学习效果
     */
    @PostMapping("/predict-outcome/{userId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public Result<Map<String, Object>> predictLearningOutcome(@PathVariable Long userId,
                                                             @RequestBody Map<String, Object> studyPlan) {
        try {
            Map<String, Object> prediction = masteryAnalysisService.predictLearningOutcome(userId, studyPlan);
            return Result.success(prediction);
        } catch (Exception e) {
            return Result.error("预测失败: " + e.getMessage());
        }
    }
}
