package com.chinaunicom.edu.service.impl;

import com.chinaunicom.edu.entity.LearningPath;
import com.chinaunicom.edu.entity.UserBehavior;
import com.chinaunicom.edu.mapper.UserBehaviorMapper;
import com.chinaunicom.edu.mapper.LearningPathMapper;
import com.chinaunicom.edu.service.LearningPathAdjustmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 学习路径动态调整服务实现类
 */
@Service
public class LearningPathAdjustmentServiceImpl implements LearningPathAdjustmentService {

    private static final Logger logger = LoggerFactory.getLogger(LearningPathAdjustmentServiceImpl.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final UserBehaviorMapper userBehaviorMapper;
    private final LearningPathMapper learningPathMapper;

    public LearningPathAdjustmentServiceImpl(UserBehaviorMapper userBehaviorMapper,
                                           LearningPathMapper learningPathMapper) {
        this.userBehaviorMapper = userBehaviorMapper;
        this.learningPathMapper = learningPathMapper;
    }

    @Override
    public LearningPath adjustPathDifficulty(Long userId, LearningPath learningPath) {
        try {
            // 1. 评估用户当前表现
            Map<String, Object> performance = evaluatePerformance(userId);
            
            // 2. 计算最优难度系数
            double optimalDifficulty = calculateOptimalDifficulty(performance);
            
            // 3. 判断是否需要调整
            BigDecimal currentDifficulty = learningPath.getDifficultyFactor() != null ? 
                learningPath.getDifficultyFactor() : BigDecimal.ONE;
            
            double currentDiffValue = currentDifficulty.doubleValue();
            double diffRatio = Math.abs(optimalDifficulty - currentDiffValue) / currentDiffValue;
            
            // 如果差异超过15%，则进行调整
            if (diffRatio > 0.15) {
                // 4. 更新路径难度
                learningPath.setDifficultyFactor(BigDecimal.valueOf(optimalDifficulty));
                learningPath.setLastAdjustmentTime(LocalDateTime.now());
                
                // 5. 设置适应性等级
                String adaptiveLevel = determineAdaptiveLevel(optimalDifficulty);
                learningPath.setAdaptiveLevel(adaptiveLevel);
                
                // 6. 记录调整原因
                String reason = generateAdjustmentReason(performance, optimalDifficulty);
                learningPath.setAdjustmentReason(reason);
                
                // 7. 更新性能指标
                learningPath.setPerformanceMetrics(objectMapper.writeValueAsString(performance));
                
                // 8. 保存到数据库
                learningPathMapper.updateById(learningPath);
                
                logger.info("Adjusted learning path {} for user {}: difficulty factor {} -> {}, reason: {}", 
                    learningPath.getId(), userId, currentDiffValue, optimalDifficulty, reason);
            }
            
            return learningPath;
            
        } catch (Exception e) {
            logger.error("Failed to adjust learning path for user {}: {}", userId, e.getMessage(), e);
            return learningPath;
        }
    }

    @Override
    public Map<String, Object> evaluatePerformance(Long userId) {
        Map<String, Object> performance = new HashMap<>();
        
        try {
            // 获取用户最近30天的行为数据
            List<UserBehavior> recentBehaviors = userBehaviorMapper.findRecentByUserId(userId, 100);
            
            if (recentBehaviors.isEmpty()) {
                // 新用户，返回默认表现
                performance.put("experienceLevel", "beginner");
                performance.put("consistencyScore", 50.0);
                performance.put("engagementScore", 50.0);
                performance.put("progressRate", 0.0);
                performance.put("averageSessionTime", 0);
                return performance;
            }
            
            // 1. 学习一致性评分 (0-100)
            double consistencyScore = calculateConsistencyScore(recentBehaviors);
            performance.put("consistencyScore", consistencyScore);
            
            // 2. 参与度评分 (0-100)
            double engagementScore = calculateEngagementScore(recentBehaviors);
            performance.put("engagementScore", engagementScore);
            
            // 3. 进度速率
            double progressRate = calculateProgressRate(recentBehaviors);
            performance.put("progressRate", progressRate);
            
            // 4. 平均学习时长
            int avgSessionTime = calculateAverageSessionTime(recentBehaviors);
            performance.put("averageSessionTime", avgSessionTime);
            
            // 5. 经验等级判定
            String experienceLevel = determineExperienceLevel(consistencyScore, engagementScore, avgSessionTime);
            performance.put("experienceLevel", experienceLevel);
            
            // 6. 学习效率指数
            double efficiencyIndex = (consistencyScore * 0.3 + engagementScore * 0.4 + progressRate * 0.3) / 100;
            performance.put("efficiencyIndex", efficiencyIndex);
            
        } catch (Exception e) {
            logger.error("Error evaluating performance for user {}: {}", userId, e.getMessage(), e);
            // 返回基础评分
            performance.put("consistencyScore", 50.0);
            performance.put("engagementScore", 50.0);
            performance.put("progressRate", 0.5);
            performance.put("averageSessionTime", 30);
            performance.put("experienceLevel", "intermediate");
        }
        
        return performance;
    }

    @Override
    public double calculateOptimalDifficulty(Map<String, Object> performanceMetrics) {
        double consistencyScore = (Double) performanceMetrics.get("consistencyScore");
        double engagementScore = (Double) performanceMetrics.get("engagementScore");
        double progressRate = (Double) performanceMetrics.get("progressRate");
        double efficiencyIndex = (Double) performanceMetrics.get("efficiencyIndex");
        String experienceLevel = (String) performanceMetrics.get("experienceLevel");
        
        // 基础难度系数 (0.8-1.2)
        double baseDifficulty = 1.0;
        
        // 根据不同指标调整难度
        if (consistencyScore > 80 && engagementScore > 80) {
            baseDifficulty *= 1.2; // 表现优秀，适当提高难度
        } else if (consistencyScore < 40 || engagementScore < 40) {
            baseDifficulty *= 0.8; // 表现较差，降低难度
        }
        
        if (progressRate > 0.8) {
            baseDifficulty *= 1.1; // 进度很快，可以挑战更高难度
        } else if (progressRate < 0.3) {
            baseDifficulty *= 0.9; // 进度缓慢，需要降低难度
        }
        
        // 经验等级影响
        switch (experienceLevel) {
            case "beginner":
                baseDifficulty *= 0.7;
                break;
            case "advanced":
                baseDifficulty *= 1.3;
                break;
            default: // intermediate
                baseDifficulty *= 1.0;
        }
        
        // 效率指数微调
        if (efficiencyIndex > 0.8) {
            baseDifficulty *= 1.05;
        } else if (efficiencyIndex < 0.4) {
            baseDifficulty *= 0.95;
        }
        
        // 限制在合理范围内
        return Math.max(0.5, Math.min(2.0, baseDifficulty));
    }

    @Override
    public Map<String, Object> generateAlternativePaths(Long userId, LearningPath currentPath, String difficultyLevel) {
        Map<String, Object> alternatives = new HashMap<>();
        
        try {
            // 这里应该根据难度等级生成不同的学习路径
            // 简化实现：基于当前路径调整课程顺序和难度
            
            List<Map<String, Object>> pathOptions = new ArrayList<>();
            
            // 选项1：更容易的路径
            if (!"easy".equals(difficultyLevel)) {
                Map<String, Object> easyPath = createPathVariant(currentPath, 0.7, "轻松入门版");
                pathOptions.add(easyPath);
            }
            
            // 选项2：标准难度路径
            Map<String, Object> normalPath = createPathVariant(currentPath, 1.0, "标准版");
            pathOptions.add(normalPath);
            
            // 选项3：更具挑战性的路径
            if (!"hard".equals(difficultyLevel)) {
                Map<String, Object> hardPath = createPathVariant(currentPath, 1.4, "挑战版");
                pathOptions.add(hardPath);
            }
            
            alternatives.put("options", pathOptions);
            alternatives.put("recommendation", selectBestOption(pathOptions));
            
        } catch (Exception e) {
            logger.error("Error generating alternative paths for user {}: {}", userId, e.getMessage(), e);
        }
        
        return alternatives;
    }

    @Override
    public Map<String, Object> getPathAdjustmentSuggestions(Long userId, LearningPath learningPath) {
        Map<String, Object> suggestions = new HashMap<>();
        
        try {
            Map<String, Object> performance = evaluatePerformance(userId);
            double optimalDifficulty = calculateOptimalDifficulty(performance);
            
            BigDecimal currentDifficulty = learningPath.getDifficultyFactor() != null ? 
                learningPath.getDifficultyFactor() : BigDecimal.ONE;
            
            suggestions.put("currentDifficulty", currentDifficulty.doubleValue());
            suggestions.put("recommendedDifficulty", optimalDifficulty);
            suggestions.put("performanceMetrics", performance);
            
            // 生成具体建议
            List<String> recommendations = new ArrayList<>();
            
            if (optimalDifficulty > currentDifficulty.doubleValue() * 1.1) {
                recommendations.add("检测到您的学习表现优异，建议适当提高学习难度");
                recommendations.add("可以尝试挑战更高级别的课程内容");
            } else if (optimalDifficulty < currentDifficulty.doubleValue() * 0.9) {
                recommendations.add("当前学习难度可能偏高，建议调整到更适合的水平");
                recommendations.add("可以从基础知识开始巩固，循序渐进");
            } else {
                recommendations.add("当前学习难度设置合理，继续保持");
            }
            
            // 添加学习建议
            String experienceLevel = (String) performance.get("experienceLevel");
            switch (experienceLevel) {
                case "beginner":
                    recommendations.add("建议增加学习频次，建立良好的学习习惯");
                    recommendations.add("重点关注基础知识的掌握");
                    break;
                case "intermediate":
                    recommendations.add("可以尝试更多实践项目来加深理解");
                    recommendations.add("建议参与学习小组讨论交流");
                    break;
                case "advanced":
                    recommendations.add("可以挑战更高阶的学习目标");
                    recommendations.add("建议担任学习小组的指导者角色");
                    break;
            }
            
            suggestions.put("recommendations", recommendations);
            
        } catch (Exception e) {
            logger.error("Error generating path adjustment suggestions for user {}: {}", userId, e.getMessage(), e);
        }
        
        return suggestions;
    }

    // 私有辅助方法
    
    private double calculateConsistencyScore(List<UserBehavior> behaviors) {
        if (behaviors.size() < 5) return 50.0;
        
        // 计算学习频率的一致性
        Map<String, Long> dailyCounts = behaviors.stream()
            .filter(b -> b.getCreatedTime() != null)
            .collect(Collectors.groupingBy(
                b -> b.getCreatedTime().toLocalDate().toString(),
                Collectors.counting()
            ));
        
        if (dailyCounts.isEmpty()) return 50.0;
        
        // 计算方差来衡量一致性
        double avg = dailyCounts.values().stream().mapToLong(Long::longValue).average().orElse(0);
        double variance = dailyCounts.values().stream()
            .mapToLong(Long::longValue)
            .mapToDouble(x -> Math.pow(x - avg, 2))
            .average()
            .orElse(0);
        
        // 方差越小，一致性越高 (转换为0-100分)
        double consistency = Math.max(0, 100 - variance * 10);
        return Math.min(100, consistency);
    }
    
    private double calculateEngagementScore(List<UserBehavior> behaviors) {
        if (behaviors.isEmpty()) return 50.0;
        
        // 基于行为类型和频率计算参与度
        long totalActions = behaviors.size();
        long studyActions = behaviors.stream()
            .map(UserBehavior::getBehaviorType)
            .filter(type -> "study".equals(type) || "complete".equals(type))
            .count();
        
        // 参与度 = 学习行为占比 * 100
        return (double) studyActions / totalActions * 100;
    }
    
    private double calculateProgressRate(List<UserBehavior> behaviors) {
        // 简化实现：基于行为数量的增长趋势
        if (behaviors.size() < 10) return 0.5;
        
        // 模拟进度增长 (实际应该基于具体的学习完成情况)
        return Math.min(1.0, behaviors.size() / 50.0);
    }
    
    private int calculateAverageSessionTime(List<UserBehavior> behaviors) {
        // 简化：假设每次行为代表一定学习时间
        return Math.min(120, Math.max(10, behaviors.size() * 2));
    }
    
    private String determineExperienceLevel(double consistency, double engagement, int avgTime) {
        double score = (consistency + engagement) / 2;
        
        if (score > 80 && avgTime > 60) {
            return "advanced";
        } else if (score > 60) {
            return "intermediate";
        } else {
            return "beginner";
        }
    }
    
    private String determineAdaptiveLevel(double difficulty) {
        if (difficulty < 0.8) return "easy";
        else if (difficulty > 1.2) return "hard";
        else return "normal";
    }
    
    private String generateAdjustmentReason(Map<String, Object> performance, double newDifficulty) {
        StringBuilder reason = new StringBuilder();
        String experienceLevel = (String) performance.get("experienceLevel");
        double consistency = (Double) performance.get("consistencyScore");
        double engagement = (Double) performance.get("engagementScore");
        
        reason.append("基于");
        reason.append(experienceLevel);
        reason.append("级别的学习表现(");
        reason.append(String.format("一致性%.0f%%, 参与度%.0f%%", consistency, engagement));
        reason.append(")，调整难度系数至").append(String.format("%.2f", newDifficulty));
        
        return reason.toString();
    }
    
    private Map<String, Object> createPathVariant(LearningPath original, double difficultyMultiplier, String variantName) {
        Map<String, Object> variant = new HashMap<>();
        variant.put("name", variantName);
        variant.put("difficultyFactor", difficultyMultiplier);
        variant.put("estimatedHours", Math.round(original.getEstimatedHours() * difficultyMultiplier));
        variant.put("description", "基于原路径的" + variantName);
        return variant;
    }
    
    private Map<String, Object> selectBestOption(List<Map<String, Object>> options) {
        // 简化实现：选择中间难度选项
        return options.size() > 1 ? options.get(1) : options.get(0);
    }
}
