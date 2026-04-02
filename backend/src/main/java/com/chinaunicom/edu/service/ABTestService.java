package com.chinaunicom.edu.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A/B测试服务
 * 支持推荐算法的多版本对比测试
 */
public interface ABTestService {
    
    /**
     * 获取用户所属的测试组
     * @param userId 用户ID
     * @return 测试组标识 (A/B/C等)
     */
    String getUserGroup(Long userId);
    
    /**
     * 记录推荐展示
     * @param userId 用户ID
     * @param group 测试组
     * @param recommendationId 推荐ID
     */
    void recordImpression(Long userId, String group, Long recommendationId);
    
    /**
     * 记录推荐点击
     * @param userId 用户ID
     * @param group 测试组
     * @param recommendationId 推荐ID
     */
    void recordClick(Long userId, String group, Long recommendationId);
    
    /**
     * 记录课程报名
     * @param userId 用户ID
     * @param group 测试组
     * @param courseId 课程ID
     */
    void recordEnrollment(Long userId, String group, Long courseId);
    
    /**
     * 获取A/B测试统计数据
     * @return 各组统计指标
     */
    Map<String, GroupStats> getTestStatistics();
    
    /**
     * 重置统计数据
     */
    void resetStatistics();
    
    /**
     * 测试组统计信息
     */
    class GroupStats {
        private final String groupName;
        private final AtomicLong impressions = new AtomicLong(0);
        private final AtomicLong clicks = new AtomicLong(0);
        private final AtomicLong enrollments = new AtomicLong(0);
        
        public GroupStats(String groupName) {
            this.groupName = groupName;
        }
        
        public void incrementImpression() {
            impressions.incrementAndGet();
        }
        
        public void incrementClick() {
            clicks.incrementAndGet();
        }
        
        public void incrementEnrollment() {
            enrollments.incrementAndGet();
        }
        
        public String getGroupName() {
            return groupName;
        }
        
        public long getImpressions() {
            return impressions.get();
        }
        
        public long getClicks() {
            return clicks.get();
        }
        
        public long getEnrollments() {
            return enrollments.get();
        }
        
        public double getClickThroughRate() {
            long imp = impressions.get();
            return imp > 0 ? (double) clicks.get() / imp * 100 : 0.0;
        }
        
        public double getConversionRate() {
            long imp = impressions.get();
            return imp > 0 ? (double) enrollments.get() / imp * 100 : 0.0;
        }
        
        @Override
        public String toString() {
            return String.format("Group %s: impressions=%d, clicks=%d, CTR=%.2f%%, enrollments=%d, CVR=%.2f%%",
                    groupName, getImpressions(), getClicks(), getClickThroughRate(), 
                    getEnrollments(), getConversionRate());
        }
    }
}
