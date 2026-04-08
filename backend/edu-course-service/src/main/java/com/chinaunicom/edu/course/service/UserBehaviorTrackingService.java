package com.chinaunicom.edu.course.service;

import com.chinaunicom.edu.course.entity.UserBehavior;
import com.chinaunicom.edu.course.mapper.UserBehaviorMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class UserBehaviorTrackingService {
    
    @Autowired
    private UserBehaviorMapper behaviorMapper;
    
    /**
     * 行为权重定义
     */
    private static final Map<String, Double> BEHAVIOR_WEIGHTS = createBehaviorWeights();
    
    private static Map<String, Double> createBehaviorWeights() {
        Map<String, Double> map = new HashMap<>();
        map.put("view", 1.0);
        map.put("browse", 0.5);
        map.put("study", 2.0);
        map.put("complete", 5.0);
        map.put("favorite", 3.0);
        map.put("share", 2.5);
        map.put("rate", 4.0);
        map.put("enroll", 4.5);
        return map;
    }
    
    /**
     * 同步记录行为
     */
    public void trackBehavior(Long userId, Long courseId, String behaviorType, 
                             HttpServletRequest request) {
        try {
            UserBehavior behavior = UserBehavior.builder()
                    .userId(userId)
                    .courseId(courseId)
                    .behaviorType(behaviorType)
                    .behaviorValue(BEHAVIOR_WEIGHTS.getOrDefault(behaviorType, 1.0))
                    .ipAddress(getClientIp(request))
                    .userAgent(request.getHeader("User-Agent"))
                    .createdTime(LocalDateTime.now())
                    .build();
            
            behaviorMapper.insert(behavior);
            log.debug("行为记录成功: userId={}, courseId={}, type={}", 
                     userId, courseId, behaviorType);
                     
        } catch (Exception e) {
            log.error("行为记录失败", e);
        }
    }
    
    /**
     * 异步记录行为
     */
    @Async
    public void trackBehaviorAsync(Long userId, Long courseId, String behaviorType,
                                   HttpServletRequest request) {
        trackBehavior(userId, courseId, behaviorType, request);
    }
    
    /**
     * 获取客户端IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
