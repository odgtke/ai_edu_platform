package com.chinaunicom.edu.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CacheService 单元测试
 */
@SpringBootTest
public class CacheServiceTest {

    @Autowired
    private CacheService cacheService;

    @Test
    public void testSetAndGet() {
        String key = "test:key";
        String value = "testValue";
        
        cacheService.set(key, value, 5, TimeUnit.MINUTES);
        Object cached = cacheService.get(key);
        
        assertEquals(value, cached);
        
        // 清理
        cacheService.delete(key);
    }

    @Test
    public void testDelete() {
        String key = "test:delete";
        cacheService.set(key, "value", 5, TimeUnit.MINUTES);
        
        cacheService.delete(key);
        
        assertNull(cacheService.get(key));
    }

    @Test
    public void testHasKey() {
        String key = "test:exists";
        cacheService.set(key, "value", 5, TimeUnit.MINUTES);
        
        assertTrue(cacheService.hasKey(key));
        assertFalse(cacheService.hasKey("test:notexists"));
        
        // 清理
        cacheService.delete(key);
    }

    @Test
    public void testIncrement() {
        String key = "test:increment";
        cacheService.set(key, 10L, 5, TimeUnit.MINUTES);
        
        Long result = cacheService.increment(key, 5);
        
        assertEquals(15L, result);
        
        // 清理
        cacheService.delete(key);
    }
}
