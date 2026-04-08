package com.chinaunicom.edu.course.service;

import com.chinaunicom.edu.course.entity.RecommendationResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 带缓存的推荐服务
 */
@Slf4j
@Service
public class CachedRecommendationService {
    
    @Autowired
    private RecommendationEngine recommendationEngine;
    
    /**
     * 获取个性化推荐（带缓存）
     */
    @Cacheable(value = "personalized", key = "#userId + '_' + #limit", unless = "#result == null || #result.isEmpty()")
    public List<RecommendationResult> getPersonalizedRecommendations(Long userId, int limit) {
        log.info("缓存未命中，生成个性化推荐，userId: {}", userId);
        return recommendationEngine.generateRecommendations(userId, limit);
    }
    
    /**
     * 获取热门课程（带缓存）
     */
    @Cacheable(value = "trending", key = "#limit", unless = "#result == null || #result.isEmpty()")
    public List<RecommendationResult> getTrendingCourses(int limit) {
        log.info("缓存未命中，获取热门课程");
        return recommendationEngine.getTrendingCourses(limit);
    }
    
    /**
     * 刷新推荐（更新缓存）
     */
    @CachePut(value = "personalized", key = "#userId + '_' + #limit")
    public List<RecommendationResult> refreshRecommendations(Long userId, int limit) {
        log.info("刷新推荐缓存，userId: {}", userId);
        return recommendationEngine.generateRecommendations(userId, limit);
    }
    
    /**
     * 清除用户推荐缓存
     */
    @CacheEvict(value = "personalized", key = "#userId + '_*'")
    public void clearUserCache(Long userId) {
        log.info("清除用户缓存，userId: {}", userId);
    }
    
    /**
     * 清除所有推荐缓存
     */
    @CacheEvict(value = {"personalized", "trending"}, allEntries = true)
    public void clearAllCache() {
        log.info("清除所有推荐缓存");
    }
    
    /**
     * 行为更新时清除缓存
     */
    @Caching(evict = {
        @CacheEvict(value = "personalized", key = "#userId + '_*'"),
        @CacheEvict(value = "userPreferences", key = "#userId")
    })
    public void onBehaviorUpdate(Long userId, Long courseId) {
        log.info("用户行为更新，清除相关缓存，userId: {}, courseId: {}", userId, courseId);
    }
}
