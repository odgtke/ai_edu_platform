package com.chinaunicom.edu.agent.core.model;

import com.chinaunicom.edu.agent.core.AgentType;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 智能体响应
 */
@Data
public class AgentResponse {
    
    /**
     * 响应ID
     */
    private String responseId;
    
    /**
     * 请求ID
     */
    private String requestId;
    
    /**
     * 响应内容
     */
    private String content;
    
    /**
     * 响应类型 (text, rich_text, card, action等)
     */
    private String type;
    
    /**
     * 处理该请求的智能体类型
     */
    private AgentType agentType;
    
    /**
     * 相关资源列表
     */
    private List<Resource> resources;
    
    /**
     * 推荐动作列表
     */
    private List<Action> actions;
    
    /**
     * 置信度 (0-1)
     */
    private Double confidence;
    
    /**
     * 是否需要人工介入
     */
    private Boolean needHuman;
    
    /**
     * 上下文信息
     */
    private Map<String, Object> context;
    
    /**
     * 处理时间 (毫秒)
     */
    private Long processTime;
    
    /**
     * 时间戳
     */
    private Long timestamp;
    
    public AgentResponse() {
        this.resources = new ArrayList<>();
        this.actions = new ArrayList<>();
        this.context = new HashMap<>();
        this.confidence = 0.0;
        this.needHuman = false;
        this.timestamp = System.currentTimeMillis();
    }
    
    public static AgentResponseBuilder builder() {
        return new AgentResponseBuilder();
    }
    
    public static AgentResponse success(String content) {
        return builder().content(content).type("text").confidence(1.0).build();
    }
    
    public static AgentResponse error(String errorMessage) {
        return builder().content(errorMessage).type("error").confidence(0.0).build();
    }
    
    public static class AgentResponseBuilder {
        private AgentResponse response = new AgentResponse();
        
        public AgentResponseBuilder responseId(String responseId) {
            response.setResponseId(responseId);
            return this;
        }
        
        public AgentResponseBuilder requestId(String requestId) {
            response.setRequestId(requestId);
            return this;
        }
        
        public AgentResponseBuilder content(String content) {
            response.setContent(content);
            return this;
        }
        
        public AgentResponseBuilder type(String type) {
            response.setType(type);
            return this;
        }
        
        public AgentResponseBuilder agentType(AgentType agentType) {
            response.setAgentType(agentType);
            return this;
        }
        
        public AgentResponseBuilder addResource(Resource resource) {
            response.getResources().add(resource);
            return this;
        }
        
        public AgentResponseBuilder addAction(Action action) {
            response.getActions().add(action);
            return this;
        }
        
        public AgentResponseBuilder confidence(Double confidence) {
            response.setConfidence(confidence);
            return this;
        }
        
        public AgentResponseBuilder needHuman(Boolean needHuman) {
            response.setNeedHuman(needHuman);
            return this;
        }
        
        public AgentResponseBuilder processTime(Long processTime) {
            response.setProcessTime(processTime);
            return this;
        }
        
        public AgentResponse build() {
            return response;
        }
    }
}
