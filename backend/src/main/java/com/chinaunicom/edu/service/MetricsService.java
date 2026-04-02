package com.chinaunicom.edu.service;

import java.util.Map;

/**
 * 系统监控指标服务
 */
public interface MetricsService {

    /**
     * 获取应用基本信息
     */
    Map<String, Object> getApplicationInfo();

    /**
     * 获取JVM内存使用情况
     */
    Map<String, Object> getJvmMemoryMetrics();

    /**
     * 获取系统CPU使用情况
     */
    Map<String, Object> getCpuMetrics();

    /**
     * 获取线程信息
     */
    Map<String, Object> getThreadMetrics();

    /**
     * 获取GC信息
     */
    Map<String, Object> getGcMetrics();

    /**
     * 获取所有指标
     */
    Map<String, Object> getAllMetrics();
}
