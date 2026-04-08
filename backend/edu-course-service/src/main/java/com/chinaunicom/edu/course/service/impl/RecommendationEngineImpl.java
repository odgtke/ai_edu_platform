package com.chinaunicom.edu.course.service.impl;

import com.chinaunicom.edu.course.entity.RecommendationResult;
import com.chinaunicom.edu.course.service.RecommendationEngine;
import com.chinaunicom.edu.course.strategy.CollaborativeFilteringStrategy;
import com.chinaunicom.edu.course.strategy.ContentBasedStrategy;
import com.chinaunicom.edu.course.strategy.TrendingStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RecommendationEngineImpl implements RecommendationEngine {
    
    // 权重配置
    private static final double WEIGHT_COLLABORATIVE = 0.40;
    private static final double WEIGHT_CONTENT = 0.30;
    private static final double WEIGHT_TRENDING = 0.30;
    
    @Autowired
    private CollaborativeFilteringStrategy collaborativeStrategy;
    
    @Autowired
    private ContentBasedStrategy contentStrategy;
    
    @Autowired
    private TrendingStrategy trendingStrategy;
    
    @Override
    public List<RecommendationResult> generateRecommendations(Long userId, int limit) {
        log.info("生成推荐，userId: {}, limit: {}", userId, limit);
        
        try {
            // 并行执行各策略
            CompletableFuture<List<RecommendationResult>> collaborativeFuture = 
                CompletableFuture.supplyAsync(() -> 
                    collaborativeStrategy.recommend(userId, limit * 2));
            
            CompletableFuture<List<RecommendationResult>> contentFuture = 
                CompletableFuture.supplyAsync(() -> 
                    contentStrategy.recommend(userId, limit * 2));
            
            CompletableFuture<List<RecommendationResult>> trendingFuture = 
                CompletableFuture.supplyAsync(() -> 
                    trendingStrategy.recommend(userId, limit));
            
            // 等待所有策略完成
            CompletableFuture.allOf(collaborativeFuture, contentFuture, trendingFuture).join();
            
            List<RecommendationResult> collaborative = collaborativeFuture.get();
            List<RecommendationResult> content = contentFuture.get();
            List<RecommendationResult> trending = trendingFuture.get();
            
            // 加权融合
            Map<Long, RecommendationResult> merged = new HashMap<>();
            
            mergeResults(merged, collaborative, WEIGHT_COLLABORATIVE, "collaborative");
            mergeResults(merged, content, WEIGHT_CONTENT, "content");
            mergeResults(merged, trending, WEIGHT_TRENDING, "trending");
            
            // 排序并返回Top N
            return merged.values().stream()
                    .sorted(Comparator.comparing(RecommendationResult::getFinalScore).reversed())
                    .limit(limit)
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            log.error("生成推荐失败", e);
            // 降级到热门推荐
            return getTrendingCourses(limit);
        }
    }
    
    /**
     * 合并推荐结果
     */
    private void mergeResults(Map<Long, RecommendationResult> merged,
                             List<RecommendationResult> results, 
                             double weight,
                             String source) {
        for (RecommendationResult result : results) {
            Long courseId = result.getCourseId();
            
            if (merged.containsKey(courseId)) {
                // 已存在，累加分数
                RecommendationResult existing = merged.get(courseId);
                existing.addScore(result.getScore() * weight);
                existing.addSource(source);
            } else {
                // 新加入
                result.setFinalScore(result.getScore() * weight);
                result.addSource(source);
                merged.put(courseId, result);
            }
        }
    }
    
    @Override
    public List<RecommendationResult> getTrendingCourses(int limit) {
        return trendingStrategy.recommend(null, limit);
    }
    
    @Override
    public void refreshRecommendations(Long userId) {
        // 清除缓存并重新生成
        log.info("刷新推荐，userId: {}", userId);
        generateRecommendations(userId, 20);
    }
}
