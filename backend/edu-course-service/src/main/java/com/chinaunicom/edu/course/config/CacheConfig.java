package com.chinaunicom.edu.course.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * 缓存配置 - 使用ConcurrentMapCacheManager（Spring内置）
 */
@Configuration
@EnableCaching
public class CacheConfig {
    
    /**
     * 本地缓存管理器
     */
    @Bean
    @Primary
    public CacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
        cacheManager.setCacheNames(java.util.Arrays.asList(
                "personalized",
                "trending",
                "userPreferences",
                "courseSimilarities"
        ));
        return cacheManager;
    }
}
