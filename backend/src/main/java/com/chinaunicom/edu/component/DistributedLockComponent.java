package com.chinaunicom.edu.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 分布式锁组件
 * 基于Redis实现，防止重复提交和并发问题
 */
@Component
public class DistributedLockComponent {

    private static final Logger logger = LoggerFactory.getLogger(DistributedLockComponent.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // Lua脚本：释放锁（确保只能释放自己加的锁）
    private static final String RELEASE_LOCK_SCRIPT = 
        "if redis.call('get', KEYS[1]) == ARGV[1] then " +
        "    return redis.call('del', KEYS[1]) " +
        "else " +
        "    return 0 " +
        "end";

    private DefaultRedisScript<Long> releaseLockScript;

    @PostConstruct
    public void init() {
        releaseLockScript = new DefaultRedisScript<>();
        releaseLockScript.setScriptText(RELEASE_LOCK_SCRIPT);
        releaseLockScript.setResultType(Long.class);
    }

    /**
     * 获取分布式锁
     * @param lockKey 锁的key
     * @param expireSeconds 锁过期时间（秒）
     * @return 锁标识（用于释放锁），获取失败返回null
     */
    public String tryLock(String lockKey, int expireSeconds) {
        try {
            String lockValue = UUID.randomUUID().toString() + "-" + Thread.currentThread().getId();
            Boolean success = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, lockValue, expireSeconds, TimeUnit.SECONDS);
            
            if (Boolean.TRUE.equals(success)) {
                return lockValue;
            }
            return null;
        } catch (Exception e) {
            logger.error("Try lock error for key {}: {}", lockKey, e.getMessage());
            return null;
        }
    }

    /**
     * 释放分布式锁
     * @param lockKey 锁的key
     * @param lockValue 锁标识（tryLock返回的值）
     * @return true: 释放成功, false: 释放失败
     */
    public boolean unlock(String lockKey, String lockValue) {
        try {
            Long result = redisTemplate.execute(
                releaseLockScript,
                Collections.singletonList(lockKey),
                lockValue
            );
            return result != null && result == 1;
        } catch (Exception e) {
            logger.error("Unlock error for key {}: {}", lockKey, e.getMessage());
            return false;
        }
    }

    /**
     * 带自动释放的锁执行
     * @param lockKey 锁的key
     * @param expireSeconds 锁过期时间
     * @param task 要执行的任务
     * @return true: 执行成功, false: 获取锁失败
     */
    public boolean executeWithLock(String lockKey, int expireSeconds, Runnable task) {
        String lockValue = tryLock(lockKey, expireSeconds);
        if (lockValue == null) {
            return false;
        }
        
        try {
            task.run();
            return true;
        } finally {
            unlock(lockKey, lockValue);
        }
    }

    /**
     * 考试提交锁 - 防止重复提交
     */
    public String trySubmitLock(Long userId, Long assessmentId) {
        String lockKey = String.format("lock:submit:%d:%d", userId, assessmentId);
        // 5秒内只能提交一次
        return tryLock(lockKey, 5);
    }

    /**
     * 考试开始锁 - 防止重复开始考试
     */
    public String tryStartLock(Long userId) {
        String lockKey = String.format("lock:start:%d", userId);
        // 10秒内只能开始一次考试
        return tryLock(lockKey, 10);
    }

    /**
     * 答案保存锁 - 确保答案保存的原子性
     */
    public String trySaveAnswerLock(Long userAssessmentId, Integer questionOrder) {
        String lockKey = String.format("lock:answer:%d:%d", userAssessmentId, questionOrder);
        // 3秒内只能保存一次
        return tryLock(lockKey, 3);
    }

    /**
     * 释放考试提交锁
     */
    public void releaseSubmitLock(Long userId, Long assessmentId, String lockValue) {
        String lockKey = String.format("lock:submit:%d:%d", userId, assessmentId);
        unlock(lockKey, lockValue);
    }

    /**
     * 释放考试开始锁
     */
    public void releaseStartLock(Long userId, String lockValue) {
        String lockKey = String.format("lock:start:%d", userId);
        unlock(lockKey, lockValue);
    }

    /**
     * 检查是否已加锁
     */
    public boolean isLocked(String lockKey) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(lockKey));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 延长锁过期时间
     */
    public boolean extendLock(String lockKey, String lockValue, int additionalSeconds) {
        try {
            String currentValue = (String) redisTemplate.opsForValue().get(lockKey);
            if (lockValue.equals(currentValue)) {
                redisTemplate.expire(lockKey, additionalSeconds, TimeUnit.SECONDS);
                return true;
            }
            return false;
        } catch (Exception e) {
            logger.error("Extend lock error for key {}: {}", lockKey, e.getMessage());
            return false;
        }
    }
}
