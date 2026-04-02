package com.chinaunicom.edu.service.recommendation.impl;

import com.chinaunicom.edu.entity.Course;
import com.chinaunicom.edu.service.recommendation.RecommendationStrategy;
import com.chinaunicom.edu.service.recommendation.RecommendationStrategyRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 混合推荐策略
 * 策略模式 + 组合模式：组合多个基础策略，加权融合结果
 */
@Component
public class HybridRecommendationStrategy implements RecommendationStrategy {
    
    @Autowired
    private RecommendationStrategyRegistry strategyRegistry;
    
    // 策略权重配置
    private final Map<String, Double> strategyWeights = new HashMap<>();
    
    public HybridRecommendationStrategy() {
        // 默认权重配置
        strategyWeights.put("CONTENT_BASED", 0.35);
        strategyWeights.put("COLLABORATIVE", 0.30);
        strategyWeights.put("POPULARITY", 0.20);
        strategyWeights.put("KNOWLEDGE", 0.15);
    }
    
    @Override
    public String getStrategyName() {
        return "HYBRID";
    }
    
    @Override
    public List<Map<String, Object>> generateRecommendations(Long userId, Integer limit) {
        Map<Long, Double> aggregatedScores = new HashMap<>();
        Map<Long, String> courseReasons = new HashMap<>();
        
        // 获取所有支持该用户的策略
        List<RecommendationStrategy> strategies = strategyRegistry.getSupportedStrategies(userId);
        
        // 聚合各策略的分数
        for (RecommendationStrategy strategy : strategies) {
            String strategyName = strategy.getStrategyName();
            double weight = strategyWeights.getOrDefault(strategyName, 0.25);
            
            List<Map<String, Object>> recommendations = strategy.generateRecommendations(userId, limit * 2);
            
            for (Map<String, Object> rec : recommendations) {
                Long courseId = (Long) rec.get("itemId");
                Double score = (Double) rec.get("score");
                String reason = (String) rec.get("reason");
                
                aggregatedScores.merge(courseId, score * weight, Double::sum);
                
                // 记录最高分的推荐理由
                if (!courseReasons.containsKey(courseId) || 
                    aggregatedScores.get(courseId) < score * weight) {
                    courseReasons.put(courseId, reason + " [" + strategyName + "]");
                }
            }
        }
        
        // 排序并限制数量
        return aggregatedScores.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(limit)
                .map(entry -> {
                    Long courseId = entry.getKey();
                    Course course = strategyRegistry.getAllStrategies().iterator().next()
                            .getClass().getName().contains("CourseService") ? null : null;
                    
                    // 获取课程信息
                    course = getCourseInfo(courseId);
                    
                    Map<String, Object> item = new HashMap<>();
                    item.put("itemId", courseId);
                    if (course != null) {
                        item.put("itemName", course.getCourseName());
                    }
                    item.put("itemType", "course");
                    item.put("score", Math.min(entry.getValue(), 99.99));
                    item.put("reason", courseReasons.getOrDefault(courseId, "综合推荐"));
                    item.put("strategy", "HYBRID");
                    return item;
                })
                .collect(Collectors.toList());
    }
    
    @Override
    public double calculateScore(Long userId, Long courseId) {
        double totalScore = 0.0;
        double totalWeight = 0.0;
        
        List<RecommendationStrategy> strategies = strategyRegistry.getSupportedStrategies(userId);
        
        for (RecommendationStrategy strategy : strategies) {
            String strategyName = strategy.getStrategyName();
            double weight = strategyWeights.getOrDefault(strategyName, 0.25);
            
            double score = strategy.calculateScore(userId, courseId);
            totalScore += score * weight;
            totalWeight += weight;
        }
        
        return totalWeight > 0 ? totalScore / totalWeight : 0.0;
    }
    
    @Override
    public boolean isSupported(Long userId) {
        return true; // 混合策略始终支持
    }
    
    /**
     * 设置策略权重
     */
    public void setStrategyWeight(String strategyName, double weight) {
        strategyWeights.put(strategyName, weight);
    }
    
    /**
     * 获取当前权重配置
     */
    public Map<String, Double> getStrategyWeights() {
        return new HashMap<>(strategyWeights);
    }
    
    private Course getCourseInfo(Long courseId) {
        // 通过任意策略获取课程服务
        return strategyRegistry.getAllStrategies().stream()
                .findFirst()
                .map(s -> {
                    try {
                        java.lang.reflect.Field field = s.getClass().getSuperclass().getDeclaredField("courseService");
                        field.setAccessible(true);
                        Object courseService = field.get(s);
                        java.lang.reflect.Method method = courseService.getClass().getMethod("getById", Long.class);
                        return (Course) method.invoke(courseService, courseId);
                    } catch (Exception e) {
                        return null;
                    }
                })
                .orElse(null);
    }
}
