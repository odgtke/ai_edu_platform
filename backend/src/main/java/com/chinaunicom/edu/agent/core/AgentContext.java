package com.chinaunicom.edu.agent.core;

import com.chinaunicom.edu.entity.User;
import com.chinaunicom.edu.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 智能体上下文
 * 维护全局状态和共享信息
 */
@Slf4j
@Component
public class AgentContext {
    
    @Autowired
    private UserService userService;
    
    // 会话上下文缓存 (sessionId -> context)
    private final Map<String, Map<String, Object>> sessionContexts = new ConcurrentHashMap<>();
    
    // 用户偏好缓存 (userId -> preferences)
    private final Map<Long, Map<String, Object>> userPreferences = new ConcurrentHashMap<>();
    
    /**
     * 获取会话上下文
     */
    public Map<String, Object> getSessionContext(String sessionId) {
        return sessionContexts.computeIfAbsent(sessionId, k -> new HashMap<>());
    }
    
    /**
     * 设置会话上下文
     */
    public void setSessionContext(String sessionId, Map<String, Object> context) {
        sessionContexts.put(sessionId, new HashMap<>(context));
    }
    
    /**
     * 清除会话上下文
     */
    public void clearSessionContext(String sessionId) {
        sessionContexts.remove(sessionId);
    }
    
    /**
     * 获取用户偏好
     */
    public Map<String, Object> getUserPreferences(Long userId) {
        return userPreferences.computeIfAbsent(userId, k -> loadUserPreferences(userId));
    }
    
    /**
     * 加载用户偏好（从数据库或其他存储）
     */
    private Map<String, Object> loadUserPreferences(Long userId) {
        Map<String, Object> preferences = new HashMap<>();
        
        try {
            User user = userService.getById(userId);
            if (user != null) {
                preferences.put("userName", user.getUsername());
                preferences.put("realName", user.getRealName());
                preferences.put("userType", user.getUserType());
            }
        } catch (Exception e) {
            log.warn("Failed to load user preferences for userId: {}", userId, e);
        }
        
        return preferences;
    }
    
    /**
     * 更新用户偏好
     */
    public void updateUserPreferences(Long userId, Map<String, Object> preferences) {
        userPreferences.put(userId, new HashMap<>(preferences));
    }
    
    /**
     * 在上下文中设置值
     */
    public void setInContext(String sessionId, String key, Object value) {
        getSessionContext(sessionId).put(key, value);
    }
    
    /**
     * 从上下文中获取值
     */
    @SuppressWarnings("unchecked")
    public <T> T getFromContext(String sessionId, String key, Class<T> type) {
        Object value = getSessionContext(sessionId).get(key);
        if (value != null && type.isInstance(value)) {
            return (T) value;
        }
        return null;
    }
    
    /**
     * 获取上下文中的字符串值
     */
    public String getContextString(String sessionId, String key) {
        return getFromContext(sessionId, key, String.class);
    }
    
    /**
     * 获取上下文中的整数值
     */
    public Integer getContextInteger(String sessionId, String key) {
        return getFromContext(sessionId, key, Integer.class);
    }
    
    /**
     * 获取上下文中的长整数值
     */
    public Long getContextLong(String sessionId, String key) {
        return getFromContext(sessionId, key, Long.class);
    }
    
    /**
     * 检查上下文是否存在某个键
     */
    public boolean hasContextKey(String sessionId, String key) {
        return getSessionContext(sessionId).containsKey(key);
    }
}
