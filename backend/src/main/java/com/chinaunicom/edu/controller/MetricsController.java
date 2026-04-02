package com.chinaunicom.edu.controller;

import com.chinaunicom.edu.common.Result;
import com.chinaunicom.edu.service.MetricsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 系统监控指标控制器
 */
@RestController
@RequestMapping("/api/metrics")
public class MetricsController {

    private final MetricsService metricsService;

    public MetricsController(MetricsService metricsService) {
        this.metricsService = metricsService;
    }

    /**
     * 获取所有监控指标
     */
    @GetMapping
    public Result<Map<String, Object>> getAllMetrics() {
        return Result.success(metricsService.getAllMetrics());
    }

    /**
     * 获取应用信息
     */
    @GetMapping("/application")
    public Result<Map<String, Object>> getApplicationInfo() {
        return Result.success(metricsService.getApplicationInfo());
    }

    /**
     * 获取JVM内存指标
     */
    @GetMapping("/memory")
    public Result<Map<String, Object>> getMemoryMetrics() {
        return Result.success(metricsService.getJvmMemoryMetrics());
    }

    /**
     * 获取CPU指标
     */
    @GetMapping("/cpu")
    public Result<Map<String, Object>> getCpuMetrics() {
        return Result.success(metricsService.getCpuMetrics());
    }

    /**
     * 获取线程指标
     */
    @GetMapping("/threads")
    public Result<Map<String, Object>> getThreadMetrics() {
        return Result.success(metricsService.getThreadMetrics());
    }

    /**
     * 获取GC指标
     */
    @GetMapping("/gc")
    public Result<Map<String, Object>> getGcMetrics() {
        return Result.success(metricsService.getGcMetrics());
    }
}
