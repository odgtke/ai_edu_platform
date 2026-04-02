package com.chinaunicom.edu.controller;

import com.chinaunicom.edu.common.Result;
import com.chinaunicom.edu.config.ThreadPoolMonitorConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 线程池监控控制器
 * 提供线程池状态查询和动态调整接口
 */
@RestController
@RequestMapping("/api/monitor/threadpool")
public class ThreadPoolMonitorController {

    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolMonitorController.class);

    @Autowired
    private ThreadPoolMonitorConfig threadPoolMonitorConfig;

    /**
     * 获取所有线程池状态
     */
    @GetMapping("/status")
    public Result<Map<String, Map<String, Object>>> getThreadPoolStatus() {
        try {
            Map<String, Map<String, Object>> status = threadPoolMonitorConfig.getThreadPoolStatus();
            return Result.success(status);
        } catch (Exception e) {
            logger.error("Failed to get thread pool status: {}", e.getMessage());
            return Result.error("获取线程池状态失败");
        }
    }

    /**
     * 获取线程池统计信息
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getThreadPoolStatistics() {
        try {
            Map<String, Object> statistics = threadPoolMonitorConfig.getThreadPoolStatistics();
            return Result.success(statistics);
        } catch (Exception e) {
            logger.error("Failed to get thread pool statistics: {}", e.getMessage());
            return Result.error("获取线程池统计失败");
        }
    }

    /**
     * 动态调整线程池大小
     * @param poolName 线程池名称
     * @param corePoolSize 核心线程数
     * @param maxPoolSize 最大线程数
     */
    @PostMapping("/adjust/{poolName}")
    public Result<String> adjustThreadPoolSize(
            @PathVariable String poolName,
            @RequestParam int corePoolSize,
            @RequestParam int maxPoolSize) {
        try {
            // 参数校验
            if (corePoolSize < 1 || maxPoolSize < corePoolSize) {
                return Result.error("参数错误：核心线程数必须>=1，最大线程数必须>=核心线程数");
            }
            
            // 限制最大线程数，防止配置错误
            if (maxPoolSize > 200) {
                return Result.error("最大线程数不能超过200");
            }
            
            boolean success = threadPoolMonitorConfig.adjustThreadPoolSize(poolName, corePoolSize, maxPoolSize);
            if (success) {
                logger.info("Thread pool {} adjusted to core: {}, max: {}", poolName, corePoolSize, maxPoolSize);
                return Result.success("线程池调整成功");
            } else {
                return Result.error("线程池调整失败，请检查线程池名称");
            }
        } catch (Exception e) {
            logger.error("Failed to adjust thread pool: {}", e.getMessage());
            return Result.error("调整线程池失败: " + e.getMessage());
        }
    }

    /**
     * 批量调整线程池（用于紧急扩容）
     */
    @PostMapping("/adjust-all")
    public Result<String> adjustAllThreadPools(@RequestParam double scaleFactor) {
        try {
            if (scaleFactor < 0.5 || scaleFactor > 3.0) {
                return Result.error("缩放因子必须在0.5-3.0之间");
            }
            
            Map<String, Map<String, Object>> status = threadPoolMonitorConfig.getThreadPoolStatus();
            int adjustedCount = 0;
            
            for (String poolName : status.keySet()) {
                Map<String, Object> poolStatus = status.get(poolName);
                int currentCore = (int) poolStatus.get("corePoolSize");
                int currentMax = (int) poolStatus.get("maximumPoolSize");
                
                int newCore = Math.max(1, (int) (currentCore * scaleFactor));
                int newMax = Math.max(newCore, (int) (currentMax * scaleFactor));
                newMax = Math.min(newMax, 200); // 上限保护
                
                if (threadPoolMonitorConfig.adjustThreadPoolSize(poolName, newCore, newMax)) {
                    adjustedCount++;
                }
            }
            
            logger.info("Adjusted {} thread pools with scale factor {}", adjustedCount, scaleFactor);
            return Result.success("成功调整 " + adjustedCount + " 个线程池");
        } catch (Exception e) {
            logger.error("Failed to adjust all thread pools: {}", e.getMessage());
            return Result.error("批量调整失败: " + e.getMessage());
        }
    }
}
