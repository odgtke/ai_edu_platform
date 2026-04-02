package com.chinaunicom.edu.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * 分布式限流组件
 * 基于Redis实现滑动窗口限流
 */
@Component
public class RateLimiterComponent {

    private static final Logger logger = LoggerFactory.getLogger(RateLimiterComponent.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // Lua脚本：滑动窗口限流
    private static final String SLIDE_WINDOW_SCRIPT = 
        "local key = KEYS[1] " +
        "local limit = tonumber(ARGV[1]) " +
        "local window = tonumber(ARGV[2]) " +
        "local now = tonumber(ARGV[3]) " +
        "local member = ARGV[4] " +
        "local clearBefore = now - window " +
        "redis.call('ZREMRANGEBYSCORE', key, 0, clearBefore) " +
        "local current = redis.call('ZCARD', key) " +
        "if current < limit then " +
        "    redis.call('ZADD', key, now, member) " +
        "    redis.call('EXPIRE', key, window / 1000) " +
        "    return 1 " +
        "else " +
        "    return 0 " +
        "end";

    private DefaultRedisScript<Long> slideWindowScript;

    @PostConstruct
    public void init() {
        slideWindowScript = new DefaultRedisScript<>();
        slideWindowScript.setScriptText(SLIDE_WINDOW_SCRIPT);
        slideWindowScript.setResultType(Long.class);
    }

    /**
     * 滑动窗口限流
     * @param key 限流key
     * @param limit 限制次数
     * @param windowSeconds 窗口大小（秒）
     * @return true: 允许通过, false: 被限流
     */
    public boolean tryAcquire(String key, int limit, int windowSeconds) {
        try {
            long now = System.currentTimeMillis();
            String member = now + "-" + Thread.currentThread().getId();
            
            Long result = redisTemplate.execute(
                slideWindowScript,
                Collections.singletonList(key),
                String.valueOf(limit),
                String.valueOf(windowSeconds * 1000),
                String.valueOf(now),
                member
            );
            
            return result != null && result == 1;
        } catch (Exception e) {
            logger.error("Rate limiter error for key {}: {}", key, e.getMessage());
            // 限流组件异常时，默认允许通过（降级策略）
            return true;
        }
    }

    /**
     * 令牌桶限流（简单实现）
     * @param key 限流key
     * @param maxTokens 最大令牌数
     * @param refillRate 每秒补充令牌数
     * @return true: 允许通过, false: 被限流
     */
    public boolean tokenBucketTryAcquire(String key, int maxTokens, int refillRate) {
        try {
            String tokenKey = "rate:token:" + key;
            String lastRefillKey = "rate:refill:" + key;
            
            // 获取当前令牌数和上次补充时间
            Integer tokens = (Integer) redisTemplate.opsForValue().get(tokenKey);
            Long lastRefill = (Long) redisTemplate.opsForValue().get(lastRefillKey);
            
            long now = System.currentTimeMillis();
            
            if (tokens == null) {
                tokens = maxTokens;
            }
            
            if (lastRefill == null) {
                lastRefill = now;
            }
            
            // 计算需要补充的令牌
            long elapsed = (now - lastRefill) / 1000;
            int tokensToAdd = (int) (elapsed * refillRate);
            
            if (tokensToAdd > 0) {
                tokens = Math.min(maxTokens, tokens + tokensToAdd);
                redisTemplate.opsForValue().set(lastRefillKey, now, 1, TimeUnit.HOURS);
            }
            
            // 尝试获取令牌
            if (tokens > 0) {
                redisTemplate.opsForValue().set(tokenKey, tokens - 1, 1, TimeUnit.HOURS);
                return true;
            }
            
            return false;
        } catch (Exception e) {
            logger.error("Token bucket error for key {}: {}", key, e.getMessage());
            return true;
        }
    }

    /**
     * 考试提交限流 - 每个用户每秒最多提交1次
     */
    public boolean trySubmitAnswer(Long userId, Long assessmentId) {
        String key = String.format("rate:submit:%d:%d", userId, assessmentId);
        return tryAcquire(key, 5, 10); // 10秒内最多5次提交
    }

    /**
     * 考试开始限流 - 防止考试开始时瞬间高并发
     */
    public boolean tryStartAssessment(Long assessmentId) {
        String key = String.format("rate:start:%d", assessmentId);
        // 每秒最多100人开始考试
        return tryAcquire(key, 100, 1);
    }

    /**
     * 全局考试限流 - 保护系统整体负载
     */
    public boolean tryGlobalAssessment() {
        String key = "rate:global:assessment";
        // 每秒最多500个考试相关操作
        return tryAcquire(key, 500, 1);
    }

    /**
     * 获取限流统计信息
     */
    public long getCurrentCount(String key) {
        try {
            Long count = redisTemplate.opsForZSet().zCard(key);
            return count != null ? count : 0;
        } catch (Exception e) {
            return 0;
        }
    }
}
