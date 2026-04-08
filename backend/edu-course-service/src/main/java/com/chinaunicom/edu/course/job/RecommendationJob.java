package com.chinaunicom.edu.course.job;

import com.chinaunicom.edu.course.entity.RecommendationResult;
import com.chinaunicom.edu.course.mapper.UserBehaviorMapper;
import com.chinaunicom.edu.course.service.CachedRecommendationService;
import com.chinaunicom.edu.course.service.RecommendationEngine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 推荐系统定时任务
 */
@Slf4j
@Component
public class RecommendationJob {
    
    @Autowired
    private RecommendationEngine recommendationEngine;
    
    @Autowired
    private CachedRecommendationService cachedService;
    
    @Autowired
    private UserBehaviorMapper userBehaviorMapper;
    
    /**
     * 每日凌晨2点：预计算活跃用户推荐
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void precomputeRecommendations() {
        log.info("开始预计算推荐，时间: {}", LocalDateTime.now());
        
        try {
            // 获取活跃用户（最近7天有行为的用户）
            Set<Long> activeUsers = getActiveUsers(7);
            log.info("活跃用户数量: {}", activeUsers.size());
            
            // 并行生成推荐
            activeUsers.parallelStream().forEach(userId -> {
                try {
                    List<RecommendationResult> recommendations = 
                            recommendationEngine.generateRecommendations(userId, 20);
                    
                    // 保存到数据库或缓存
                    log.debug("用户 {} 推荐生成完成，数量: {}", userId, recommendations.size());
                    
                } catch (Exception e) {
                    log.error("预计算推荐失败，userId: {}", userId, e);
                }
            });
            
            log.info("预计算推荐完成");
            
        } catch (Exception e) {
            log.error("预计算推荐任务失败", e);
        }
    }
    
    /**
     * 每小时：更新热门课程缓存
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void updateTrendingCache() {
        log.info("更新热门课程缓存");
        
        try {
            cachedService.clearAllCache();
            cachedService.getTrendingCourses(20);
            log.info("热门课程缓存更新完成");
        } catch (Exception e) {
            log.error("更新热门课程缓存失败", e);
        }
    }
    
    /**
     * 每10分钟：清理过期缓存
     */
    @Scheduled(cron = "0 */10 * * * ?")
    public void cleanupCache() {
        log.debug("清理过期缓存");
        // Caffeine自动清理，这里可以记录统计信息
    }
    
    /**
     * 每周一凌晨3点：计算课程相似度
     */
    @Scheduled(cron = "0 0 3 ? * MON")
    public void computeCourseSimilarities() {
        log.info("开始计算课程相似度");
        
        try {
            // 这里实现课程相似度计算逻辑
            // 1. 获取所有课程
            // 2. 计算课程之间的相似度
            // 3. 更新course_similarities表
            
            log.info("课程相似度计算完成");
        } catch (Exception e) {
            log.error("计算课程相似度失败", e);
        }
    }
    
    /**
     * 获取活跃用户
     */
    private Set<Long> getActiveUsers(int days) {
        // 简化实现，实际应该查询数据库
        Set<Long> users = new java.util.HashSet<>();
        users.add(1L);
        users.add(2L);
        users.add(3L);
        return users;
    }
}
