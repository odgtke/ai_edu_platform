package com.chinaunicom.edu.service.impl;

import com.chinaunicom.edu.service.MetricsService;
import org.springframework.stereotype.Service;

import java.lang.management.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 系统监控指标服务实现类
 */
@Service
public class MetricsServiceImpl implements MetricsService {

    @Override
    public Map<String, Object> getApplicationInfo() {
        Map<String, Object> info = new HashMap<>();
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        
        info.put("applicationName", "智慧教育平台");
        info.put("javaVersion", System.getProperty("java.version"));
        info.put("javaVendor", System.getProperty("java.vendor"));
        info.put("startTime", runtimeMXBean.getStartTime());
        info.put("uptime", runtimeMXBean.getUptime());
        info.put("pid", runtimeMXBean.getName().split("@")[0]);
        
        return info;
    }

    @Override
    public Map<String, Object> getJvmMemoryMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapUsage = memoryMXBean.getHeapMemoryUsage();
        MemoryUsage nonHeapUsage = memoryMXBean.getNonHeapMemoryUsage();
        
        // 堆内存
        metrics.put("heapInit", heapUsage.getInit() / 1024 / 1024); // MB
        metrics.put("heapUsed", heapUsage.getUsed() / 1024 / 1024);
        metrics.put("heapCommitted", heapUsage.getCommitted() / 1024 / 1024);
        metrics.put("heapMax", heapUsage.getMax() / 1024 / 1024);
        
        // 非堆内存
        metrics.put("nonHeapInit", nonHeapUsage.getInit() / 1024 / 1024);
        metrics.put("nonHeapUsed", nonHeapUsage.getUsed() / 1024 / 1024);
        metrics.put("nonHeapCommitted", nonHeapUsage.getCommitted() / 1024 / 1024);
        
        // 内存使用率
        double heapUsagePercent = (double) heapUsage.getUsed() / heapUsage.getMax() * 100;
        metrics.put("heapUsagePercent", String.format("%.2f%%", heapUsagePercent));
        
        return metrics;
    }

    @Override
    public Map<String, Object> getCpuMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        OperatingSystemMXBean osMXBean = ManagementFactory.getOperatingSystemMXBean();
        
        metrics.put("availableProcessors", osMXBean.getAvailableProcessors());
        metrics.put("systemLoadAverage", osMXBean.getSystemLoadAverage());
        metrics.put("osName", osMXBean.getName());
        metrics.put("osVersion", osMXBean.getVersion());
        metrics.put("osArch", osMXBean.getArch());
        
        return metrics;
    }

    @Override
    public Map<String, Object> getThreadMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        
        metrics.put("threadCount", threadMXBean.getThreadCount());
        metrics.put("peakThreadCount", threadMXBean.getPeakThreadCount());
        metrics.put("daemonThreadCount", threadMXBean.getDaemonThreadCount());
        metrics.put("totalStartedThreadCount", threadMXBean.getTotalStartedThreadCount());
        
        return metrics;
    }

    @Override
    public Map<String, Object> getGcMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        for (GarbageCollectorMXBean gcMXBean : ManagementFactory.getGarbageCollectorMXBeans()) {
            Map<String, Object> gcInfo = new HashMap<>();
            gcInfo.put("collectionCount", gcMXBean.getCollectionCount());
            gcInfo.put("collectionTime", gcMXBean.getCollectionTime());
            metrics.put(gcMXBean.getName(), gcInfo);
        }
        
        return metrics;
    }

    @Override
    public Map<String, Object> getAllMetrics() {
        Map<String, Object> allMetrics = new HashMap<>();
        
        allMetrics.put("application", getApplicationInfo());
        allMetrics.put("memory", getJvmMemoryMetrics());
        allMetrics.put("cpu", getCpuMetrics());
        allMetrics.put("threads", getThreadMetrics());
        allMetrics.put("gc", getGcMetrics());
        allMetrics.put("timestamp", System.currentTimeMillis());
        
        return allMetrics;
    }
}
