package com.chinaunicom.edu.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池监控配置
 * 定期输出线程池状态，支持动态调整
 */
@Configuration
@EnableScheduling
public class ThreadPoolMonitorConfig {

    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolMonitorConfig.class);

    @Autowired
    @Qualifier("taskExecutor")
    private Executor taskExecutor;

    @Autowired
    @Qualifier("recommendationExecutor")
    private Executor recommendationExecutor;

    @Autowired
    @Qualifier("dataProcessingExecutor")
    private Executor dataProcessingExecutor;

    @Autowired
    @Qualifier("scheduledExecutor")
    private Executor scheduledExecutor;

    @Autowired
    @Qualifier("highPriorityExecutor")
    private Executor highPriorityExecutor;

    private Map<String, ThreadPoolTaskExecutor> executors;

    @PostConstruct
    public void init() {
        executors = new HashMap<>();
        executors.put("taskExecutor", (ThreadPoolTaskExecutor) taskExecutor);
        executors.put("recommendationExecutor", (ThreadPoolTaskExecutor) recommendationExecutor);
        executors.put("dataProcessingExecutor", (ThreadPoolTaskExecutor) dataProcessingExecutor);
        executors.put("scheduledExecutor", (ThreadPoolTaskExecutor) scheduledExecutor);
        executors.put("highPriorityExecutor", (ThreadPoolTaskExecutor) highPriorityExecutor);
        
        logger.info("Thread pool monitor initialized with {} executors", executors.size());
    }

    /**
     * 每30秒输出一次线程池状态
     */
    @Scheduled(fixedRate = 30000)
    public void logThreadPoolStatus() {
        executors.forEach((name, executor) -> {
            ThreadPoolExecutor threadPool = executor.getThreadPoolExecutor();
            
            int activeCount = threadPool.getActiveCount();
            int poolSize = threadPool.getPoolSize();
            int corePoolSize = threadPool.getCorePoolSize();
            int maximumPoolSize = threadPool.getMaximumPoolSize();
            long taskCount = threadPool.getTaskCount();
            long completedTaskCount = threadPool.getCompletedTaskCount();
            int queueSize = threadPool.getQueue().size();
            int remainingCapacity = threadPool.getQueue().remainingCapacity();
            
            // 计算使用率
            double usageRate = poolSize > 0 ? (double) activeCount / poolSize * 100 : 0;
            double queueUsageRate = (queueSize + remainingCapacity) > 0 
                ? (double) queueSize / (queueSize + remainingCapacity) * 100 : 0;
            
            // 根据使用率输出不同级别的日志
            if (usageRate > 80 || queueUsageRate > 80) {
                logger.warn("[ThreadPool-{}] HIGH LOAD - Active: {}/{}, Queue: {}/{}, Completed: {}",
                    name, activeCount, poolSize, queueSize, queueSize + remainingCapacity, completedTaskCount);
            } else {
                logger.debug("[ThreadPool-{}] Active: {}/{}, Queue: {}/{}, Completed: {}",
                    name, activeCount, poolSize, queueSize, queueSize + remainingCapacity, completedTaskCount);
            }
        });
    }

    /**
     * 获取线程池状态（用于API接口）
     */
    public Map<String, Map<String, Object>> getThreadPoolStatus() {
        Map<String, Map<String, Object>> status = new HashMap<>();
        
        executors.forEach((name, executor) -> {
            ThreadPoolExecutor threadPool = executor.getThreadPoolExecutor();
            Map<String, Object> poolStatus = new HashMap<>();
            
            poolStatus.put("activeCount", threadPool.getActiveCount());
            poolStatus.put("poolSize", threadPool.getPoolSize());
            poolStatus.put("corePoolSize", threadPool.getCorePoolSize());
            poolStatus.put("maximumPoolSize", threadPool.getMaximumPoolSize());
            poolStatus.put("taskCount", threadPool.getTaskCount());
            poolStatus.put("completedTaskCount", threadPool.getCompletedTaskCount());
            poolStatus.put("queueSize", threadPool.getQueue().size());
            poolStatus.put("remainingCapacity", threadPool.getQueue().remainingCapacity());
            poolStatus.put("largestPoolSize", threadPool.getLargestPoolSize());
            
            status.put(name, poolStatus);
        });
        
        return status;
    }

    /**
     * 动态调整线程池大小（用于运行时扩容/缩容）
     */
    public boolean adjustThreadPoolSize(String poolName, int corePoolSize, int maxPoolSize) {
        ThreadPoolTaskExecutor executor = executors.get(poolName);
        if (executor == null) {
            logger.error("Thread pool {} not found", poolName);
            return false;
        }
        
        try {
            ThreadPoolExecutor threadPool = executor.getThreadPoolExecutor();
            threadPool.setCorePoolSize(corePoolSize);
            threadPool.setMaximumPoolSize(maxPoolSize);
            logger.info("Adjusted thread pool {} - core: {}, max: {}", poolName, corePoolSize, maxPoolSize);
            return true;
        } catch (Exception e) {
            logger.error("Failed to adjust thread pool {}: {}", poolName, e.getMessage());
            return false;
        }
    }

    /**
     * 获取线程池统计信息（用于监控大屏）
     */
    public Map<String, Object> getThreadPoolStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        long totalActive = 0;
        long totalPoolSize = 0;
        long totalCompleted = 0;
        long totalQueueSize = 0;
        
        for (Map.Entry<String, ThreadPoolTaskExecutor> entry : executors.entrySet()) {
            ThreadPoolExecutor threadPool = entry.getValue().getThreadPoolExecutor();
            totalActive += threadPool.getActiveCount();
            totalPoolSize += threadPool.getPoolSize();
            totalCompleted += threadPool.getCompletedTaskCount();
            totalQueueSize += threadPool.getQueue().size();
        }
        
        statistics.put("totalActiveThreads", totalActive);
        statistics.put("totalPoolSize", totalPoolSize);
        statistics.put("totalCompletedTasks", totalCompleted);
        statistics.put("totalQueueSize", totalQueueSize);
        statistics.put("poolCount", executors.size());
        statistics.put("utilizationRate", totalPoolSize > 0 ? 
            (double) totalActive / totalPoolSize * 100 : 0);
        
        return statistics;
    }
}
