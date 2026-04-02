package com.chinaunicom.edu.service.recommendation;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 推荐策略注册表
 * 注册中心模式：管理所有推荐策略的注册和发现
 */
@Component
public class RecommendationStrategyRegistry {
    
    // 策略名称 -> 策略实例
    private final Map<String, RecommendationStrategy> strategies = new ConcurrentHashMap<>();
    
    // 策略类型 -> 策略列表
    private final Map<StrategyType, List<RecommendationStrategy>> typeToStrategies = new ConcurrentHashMap<>();
    
    /**
     * 策略类型枚举
     */
    public enum StrategyType {
        CONTENT_BASED,      // 基于内容
        COLLABORATIVE,      // 协同过滤
        KNOWLEDGE_BASED,    // 基于知识
        POPULARITY_BASED,   // 基于热度
        HYBRID              // 混合策略
    }
    
    /**
     * 注册策略
     */
    public void registerStrategy(RecommendationStrategy strategy) {
        strategies.put(strategy.getStrategyName(), strategy);
    }
    
    /**
     * 注册策略（带类型）
     */
    public void registerStrategy(RecommendationStrategy strategy, StrategyType type) {
        registerStrategy(strategy);
        typeToStrategies.computeIfAbsent(type, k -> new ArrayList<>()).add(strategy);
    }
    
    /**
     * 根据名称获取策略
     */
    public Optional<RecommendationStrategy> getStrategy(String name) {
        return Optional.ofNullable(strategies.get(name));
    }
    
    /**
     * 根据类型获取策略列表
     */
    public List<RecommendationStrategy> getStrategiesByType(StrategyType type) {
        return typeToStrategies.getOrDefault(type, Collections.emptyList());
    }
    
    /**
     * 获取所有策略
     */
    public Collection<RecommendationStrategy> getAllStrategies() {
        return strategies.values();
    }
    
    /**
     * 获取所有策略名称
     */
    public Set<String> getAllStrategyNames() {
        return strategies.keySet();
    }
    
    /**
     * 检查策略是否存在
     */
    public boolean hasStrategy(String name) {
        return strategies.containsKey(name);
    }
    
    /**
     * 移除策略
     */
    public void unregisterStrategy(String name) {
        RecommendationStrategy removed = strategies.remove(name);
        if (removed != null) {
            // 从类型映射中移除
            typeToStrategies.values().forEach(list -> list.remove(removed));
        }
    }
    
    /**
     * 获取支持指定用户的策略列表
     */
    public List<RecommendationStrategy> getSupportedStrategies(Long userId) {
        return strategies.values().stream()
                .filter(s -> s.isSupported(userId))
                .collect(java.util.stream.Collectors.toList());
    }
}
