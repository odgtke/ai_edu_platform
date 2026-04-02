package com.chinaunicom.edu.agent.core.model;

import com.chinaunicom.edu.agent.nlp.Intent;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 智能体请求
 */
@Data
public class AgentRequest {
    
    /**
     * 请求ID
     */
    private String requestId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 会话ID
     */
    private String sessionId;
    
    /**
     * 用户输入消息
     */
    private String message;
    
    /**
     * 识别的意图
     */
    private Intent intent;
    
    /**
     * 上下文信息
     */
    private Map<String, Object> context;
    
    /**
     * 时间戳
     */
    private Long timestamp;
    
    /**
     * 请求来源 (web, app, wechat等)
     */
    private String source;
    
    public AgentRequest() {
        this.context = new HashMap<>();
        this.timestamp = System.currentTimeMillis();
    }
    
    public static AgentRequestBuilder builder() {
        return new AgentRequestBuilder();
    }
    
    public static class AgentRequestBuilder {
        private AgentRequest request = new AgentRequest();
        
        public AgentRequestBuilder requestId(String requestId) {
            request.setRequestId(requestId);
            return this;
        }
        
        public AgentRequestBuilder userId(Long userId) {
            request.setUserId(userId);
            return this;
        }
        
        public AgentRequestBuilder sessionId(String sessionId) {
            request.setSessionId(sessionId);
            return this;
        }
        
        public AgentRequestBuilder message(String message) {
            request.setMessage(message);
            return this;
        }
        
        public AgentRequestBuilder intent(Intent intent) {
            request.setIntent(intent);
            return this;
        }
        
        public AgentRequestBuilder context(Map<String, Object> context) {
            request.setContext(context);
            return this;
        }
        
        public AgentRequestBuilder addContext(String key, Object value) {
            request.getContext().put(key, value);
            return this;
        }
        
        public AgentRequestBuilder source(String source) {
            request.setSource(source);
            return this;
        }
        
        public AgentRequest build() {
            return request;
        }
    }
}
