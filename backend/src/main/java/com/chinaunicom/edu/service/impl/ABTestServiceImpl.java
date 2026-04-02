package com.chinaunicom.edu.service.impl;

import com.chinaunicom.edu.service.ABTestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A/B测试服务实现
 * 基于用户ID哈希分配测试组
 */
@Service
public class ABTestServiceImpl implements ABTestService {
    
    private static final Logger logger = LoggerFactory.getLogger(ABTestServiceImpl.class);
    
    // 测试组分配比例：A组50%，B组30%，C组20%
    private static final int GROUP_A_THRESHOLD = 50;
    private static final int GROUP_B_THRESHOLD = 80; // 50 + 30
    
    // 各组统计数据
    private final Map<String, GroupStats> statistics = new ConcurrentHashMap<>();
    
    public ABTestServiceImpl() {
        // 初始化统计对象
        statistics.put("A", new GroupStats("A"));
        statistics.put("B", new GroupStats("B"));
        statistics.put("C", new GroupStats("C"));
    }
    
    @Override
    public String getUserGroup(Long userId) {
        // 基于用户ID的哈希值分配测试组，确保同一用户始终在同一组
        int hash = Math.abs(userId.hashCode() % 100);
        
        if (hash < GROUP_A_THRESHOLD) {
            return "A"; // 50% 用户 - 基础推荐算法
        } else if (hash < GROUP_B_THRESHOLD) {
            return "B"; // 30% 用户 - 改进推荐算法
        } else {
            return "C"; // 20% 用户 - 实验推荐算法
        }
    }
    
    @Override
    public void recordImpression(Long userId, String group, Long recommendationId) {
        GroupStats stats = statistics.get(group);
        if (stats != null) {
            stats.incrementImpression();
            logger.debug("Recorded impression: user={}, group={}, recommendation={}", 
                    userId, group, recommendationId);
        }
    }
    
    @Override
    public void recordClick(Long userId, String group, Long recommendationId) {
        GroupStats stats = statistics.get(group);
        if (stats != null) {
            stats.incrementClick();
            logger.info("Recorded click: user={}, group={}, recommendation={}", 
                    userId, group, recommendationId);
        }
    }
    
    @Override
    public void recordEnrollment(Long userId, String group, Long courseId) {
        GroupStats stats = statistics.get(group);
        if (stats != null) {
            stats.incrementEnrollment();
            logger.info("Recorded enrollment: user={}, group={}, course={}", 
                    userId, group, courseId);
        }
    }
    
    @Override
    public Map<String, GroupStats> getTestStatistics() {
        return new ConcurrentHashMap<>(statistics);
    }
    
    @Override
    public void resetStatistics() {
        statistics.clear();
        statistics.put("A", new GroupStats("A"));
        statistics.put("B", new GroupStats("B"));
        statistics.put("C", new GroupStats("C"));
        logger.info("A/B test statistics reset");
    }
    
    /**
     * 打印统计报告
     */
    public void printStatisticsReport() {
        logger.info("========== A/B Test Statistics Report ==========");
        for (GroupStats stats : statistics.values()) {
            logger.info(stats.toString());
        }
        logger.info("================================================");
    }
}
