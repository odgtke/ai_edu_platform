package com.chinaunicom.edu.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        // 配置序列化
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(1))  // 默认1小时过期
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .disableCachingNullValues();

        return RedisCacheManager.builder(factory)
                .cacheDefaults(config)
                .withCacheConfiguration("users", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(30)))
                .withCacheConfiguration("courses", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(2)))
                .withCacheConfiguration("recommendations", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(15)))
                .withCacheConfiguration("resources", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(1)))
                .build();
    }
}