package com.chinaunicom.edu.config;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * Spring Boot Actuator 健康检查配置
 */
@Configuration
public class ActuatorConfig {

    /**
     * 数据库健康检查
     */
    @Bean
    public HealthIndicator databaseHealthIndicator(DataSource dataSource) {
        return () -> {
            try (Connection connection = dataSource.getConnection()) {
                if (connection.isValid(1)) {
                    return Health.up()
                            .withDetail("database", "MySQL")
                            .withDetail("status", "Connected")
                            .build();
                }
            } catch (Exception e) {
                return Health.down()
                        .withDetail("database", "MySQL")
                        .withDetail("error", e.getMessage())
                        .build();
            }
            return Health.down().build();
        };
    }

    /**
     * Redis健康检查
     */
    @Bean
    public HealthIndicator redisHealthIndicator(RedisTemplate<String, Object> redisTemplate) {
        return () -> {
            try {
                redisTemplate.opsForValue().get("health:check");
                return Health.up()
                        .withDetail("redis", "Connected")
                        .build();
            } catch (Exception e) {
                return Health.down()
                        .withDetail("redis", "Disconnected")
                        .withDetail("error", e.getMessage())
                        .build();
            }
        };
    }
}
