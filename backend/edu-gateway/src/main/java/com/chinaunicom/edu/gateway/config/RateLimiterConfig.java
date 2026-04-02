package com.chinaunicom.edu.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

/**
 * 限流配置
 */
@Configuration
public class RateLimiterConfig {

    /**
     * 基于IP的限流Key解析器（默认）
     */
    @Bean
    @Primary
    public KeyResolver ipKeyResolver() {
        return exchange -> Mono.just(
            exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
        );
    }

    /**
     * 基于用户的限流Key解析器
     */
    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> Mono.just(
            exchange.getRequest().getHeaders().getFirst("X-User-Id") != null 
                ? exchange.getRequest().getHeaders().getFirst("X-User-Id")
                : "anonymous"
        );
    }

    /**
     * 基于API路径的限流Key解析器
     */
    @Bean
    public KeyResolver apiKeyResolver() {
        return exchange -> Mono.just(
            exchange.getRequest().getPath().value()
        );
    }
}
