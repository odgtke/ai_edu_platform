package com.chinaunicom.edu.course.controller;

import com.chinaunicom.edu.common.Result;
import com.chinaunicom.edu.course.service.ABTestService;
import com.chinaunicom.edu.course.service.RecommendationMetricsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 推荐系统管理接口
 */
@Slf4j
@RestController
@RequestMapping("/api/recommendations/admin")
public class RecommendationAdminController {
    
    @Autowired
    private RecommendationMetricsService metricsService;
    
    @Autowired
    private ABTestService abTestService;
    
    /**
     * 获取系统指标
     */
    @GetMapping("/metrics/system")
    public Result<Map<String, Object>> getSystemMetrics() {
        RecommendationMetricsService.SystemMetrics metrics = metricsService.getSystemMetrics();
        
        Map<String, Object> result = new HashMap<>();
        result.put("totalRequests", metrics.getTotalRequests());
        result.put("successRequests", metrics.getSuccessRequests());
        result.put("successRate", String.format("%.2f%%", metrics.getSuccessRate() * 100));
        result.put("avgResponseTime", String.format("%.2f ms", metrics.getAvgResponseTime()));
        result.put("p95ResponseTime", metrics.getP95ResponseTime() + " ms");
        
        return Result.success(result);
    }
    
    /**
     * 获取业务指标
     */
    @GetMapping("/metrics/business")
    public Result<Map<String, Object>> getBusinessMetrics() {
        RecommendationMetricsService.BusinessMetrics metrics = metricsService.getBusinessMetrics();
        
        Map<String, Object> result = new HashMap<>();
        result.put("totalRecommendations", metrics.getTotalRecommendations());
        result.put("totalClicks", metrics.getTotalClicks());
        result.put("totalEnrolls", metrics.getTotalEnrolls());
        result.put("ctr", String.format("%.2f%%", metrics.getCtr() * 100));
        result.put("cvr", String.format("%.2f%%", metrics.getCvr() * 100));
        
        return Result.success(result);
    }
    
    /**
     * 获取A/B测试结果
     */
    @GetMapping("/abtest/{experimentId}")
    public Result<ABTestService.ExperimentResult> getABTestResult(
            @PathVariable String experimentId) {
        ABTestService.ExperimentResult result = abTestService.calculateResult(experimentId);
        return Result.success(result);
    }
    
    /**
     * 重置指标
     */
    @PostMapping("/metrics/reset")
    public Result<Void> resetMetrics() {
        metricsService.reset();
        return Result.success();
    }
    
    /**
     * 健康检查
     */
    @GetMapping("/health")
    public Result<Map<String, String>> healthCheck() {
        Map<String, String> result = new HashMap<>();
        result.put("status", "UP");
        result.put("service", "recommendation-service");
        return Result.success(result);
    }
}
