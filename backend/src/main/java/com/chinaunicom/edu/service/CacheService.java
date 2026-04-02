package com.chinaunicom.edu.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 缓存服务类
 * 提供统一的Redis缓存操作接口
 */
@Service
public class CacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    public CacheService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 设置缓存
     *
     * @param key     缓存键
     * @param value   缓存值
     * @param timeout 过期时间
     * @param unit    时间单位
     */
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /**
     * 获取缓存
     *
     * @param key 缓存键
     * @return 缓存值
     */
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 删除缓存
     *
     * @param key 缓存键
     */
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 判断key是否存在
     *
     * @param key 缓存键
     * @return true-存在 false-不存在
     */
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * 设置过期时间
     *
     * @param key     缓存键
     * @param timeout 过期时间
     * @param unit    时间单位
     * @return true-设置成功 false-设置失败
     */
    public boolean expire(String key, long timeout, TimeUnit unit) {
        return Boolean.TRUE.equals(redisTemplate.expire(key, timeout, unit));
    }

    /**
     * 自增
     *
     * @param key   缓存键
     * @param delta 增量
     * @return 增加后的值
     */
    public Long increment(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 自减
     *
     * @param key   缓存键
     * @param delta 减量
     * @return 减少后的值
     */
    public Long decrement(String key, long delta) {
        return redisTemplate.opsForValue().decrement(key, delta);
    }
}
