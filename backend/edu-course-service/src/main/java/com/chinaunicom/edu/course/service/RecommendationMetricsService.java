package com.chinaunicom.edu.course.service;

import com.chinaunicom.edu.course.entity.RecommendationResult;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 推荐系统指标监控服务
 */
@Slf4j
@Service
public class RecommendationMetricsService {
    
    /**
     * 推荐请求计数
     */
    private final AtomicLong recommendationRequests = new AtomicLong(0);
    
    /**
     * 推荐成功计数
     */
    private final AtomicLong recommendationSuccess = new AtomicLong(0);
    
    /**
     * 点击计数
     */
    private final Map<Long, AtomicLong> clickCounts = new ConcurrentHashMap<>();
    
    /**
     * 报名计数
     */
    private final Map<Long, AtomicLong> enrollCounts = new ConcurrentHashMap<>();
    
    /**
     * 响应时间记录
     */
    private final List<Long> responseTimes = Collections.synchronizedList(new ArrayList<>());
    
    /**
     * 记录推荐请求
     */
    public void recordRequest() {
        recommendationRequests.incrementAndGet();
    }
    
    /**
     * 记录推荐成功
     */
    public void recordSuccess() {
        recommendationSuccess.incrementAndGet();
    }
    
    /**
     * 记录响应时间
     */
    public void recordResponseTime(long millis) {
        responseTimes.add(millis);
        // 只保留最近1000条记录
        if (responseTimes.size() > 1000) {
            responseTimes.remove(0);
        }
    }
    
    /**
     * 记录点击
     */
    public void recordClick(Long userId) {
        clickCounts.computeIfAbsent(userId, k -> new AtomicLong(0)).incrementAndGet();
    }
    
    /**
     * 记录报名
     */
    public void recordEnroll(Long userId) {
        enrollCounts.computeIfAbsent(userId, k -> new AtomicLong(0)).incrementAndGet();
    }
    
    /**
     * 获取系统指标
     */
    public SystemMetrics getSystemMetrics() {
        long total = recommendationRequests.get();
        long success = recommendationSuccess.get();
        
        return SystemMetrics.builder()
                .totalRequests(total)
                .successRequests(success)
                .successRate(total > 0 ? (double) success / total : 0)
                .avgResponseTime(calculateAvgResponseTime())
                .p95ResponseTime(calculateP95ResponseTime())
                .build();
    }
    
    /**
     * 获取业务指标
     */
    public BusinessMetrics getBusinessMetrics() {
        long totalClicks = clickCounts.values().stream()
                .mapToLong(AtomicLong::get)
                .sum();
        long totalEnrolls = enrollCounts.values().stream()
                .mapToLong(AtomicLong::get)
                .sum();
        long totalRequests = recommendationRequests.get();
        
        return BusinessMetrics.builder()
                .totalRecommendations(totalRequests)
                .totalClicks(totalClicks)
                .totalEnrolls(totalEnrolls)
                .ctr(totalRequests > 0 ? (double) totalClicks / totalRequests : 0)
                .cvr(totalRequests > 0 ? (double) totalEnrolls / totalRequests : 0)
                .build();
    }
    
    /**
     * 计算平均响应时间
     */
    private double calculateAvgResponseTime() {
        if (responseTimes.isEmpty()) {
            return 0;
        }
        return responseTimes.stream()
                .mapToLong(Long::longValue)
                .average()
                .orElse(0);
    }
    
    /**
     * 计算P95响应时间
     */
    private long calculateP95ResponseTime() {
        if (responseTimes.isEmpty()) {
            return 0;
        }
        List<Long> sorted = new ArrayList<>(responseTimes);
        Collections.sort(sorted);
        int index = (int) (sorted.size() * 0.95);
        return sorted.get(Math.min(index, sorted.size() - 1));
    }
    
    /**
     * 重置指标
     */
    public void reset() {
        recommendationRequests.set(0);
        recommendationSuccess.set(0);
        clickCounts.clear();
        enrollCounts.clear();
        responseTimes.clear();
    }
    
    /**
     * 系统指标
     */
    @Data
    @Builder
    public static class SystemMetrics {
        private long totalRequests;
        private long successRequests;
        private double successRate;
        private double avgResponseTime;
        private long p95ResponseTime;
    }
    
    /**
     * 业务指标
     */
    @Data
    @Builder
    public static class BusinessMetrics {
        private long totalRecommendations;
        private long totalClicks;
        private long totalEnrolls;
        private double ctr; // 点击率
        private double cvr; // 转化率
    }
}
