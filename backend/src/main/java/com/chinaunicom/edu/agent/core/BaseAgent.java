package com.chinaunicom.edu.agent.core;

import com.chinaunicom.edu.agent.core.model.AgentRequest;
import com.chinaunicom.edu.agent.core.model.AgentResponse;
import com.chinaunicom.edu.agent.nlp.Intent;
import com.chinaunicom.edu.agent.nlp.IntentType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * 智能体基类
 * 提供通用的功能实现
 */
@Slf4j
public abstract class BaseAgent implements IAgent {
    
    @Autowired
    protected AgentContext agentContext;
    
    private boolean initialized = false;
    
    @PostConstruct
    @Override
    public void initialize() {
        if (!initialized) {
            log.info("Initializing agent: {} ({})", getAgentName(), getAgentId());
            doInitialize();
            initialized = true;
            log.info("Agent {} initialized successfully", getAgentId());
        }
    }
    
    @PreDestroy
    @Override
    public void destroy() {
        if (initialized) {
            log.info("Destroying agent: {} ({})", getAgentName(), getAgentId());
            doDestroy();
            initialized = false;
            log.info("Agent {} destroyed successfully", getAgentId());
        }
    }
    
    /**
     * 子类实现具体的初始化逻辑
     */
    protected void doInitialize() {
        // 默认空实现，子类可重写
    }
    
    /**
     * 子类实现具体的销毁逻辑
     */
    protected void doDestroy() {
        // 默认空实现，子类可重写
    }
    
    @Override
    public final AgentResponse process(AgentRequest request) {
        long startTime = System.currentTimeMillis();
        
        try {
            log.debug("Agent {} processing request: {}", getAgentId(), request.getMessage());
            
            // 验证请求
            validateRequest(request);
            
            // 执行具体处理逻辑
            AgentResponse response = doProcess(request);
            
            // 设置响应元信息
            response.setRequestId(request.getRequestId());
            response.setAgentType(getAgentType());
            response.setProcessTime(System.currentTimeMillis() - startTime);
            
            log.debug("Agent {} processed request successfully, took {}ms", 
                     getAgentId(), response.getProcessTime());
            
            return response;
            
        } catch (Exception e) {
            log.error("Agent {} failed to process request", getAgentId(), e);
            return handleError(request, e);
        }
    }
    
    /**
     * 验证请求参数
     */
    protected void validateRequest(AgentRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }
        if (request.getMessage() == null || request.getMessage().trim().isEmpty()) {
            throw new IllegalArgumentException("Message cannot be empty");
        }
    }
    
    /**
     * 子类实现具体的处理逻辑
     */
    protected abstract AgentResponse doProcess(AgentRequest request);
    
    /**
     * 处理错误情况
     */
    protected AgentResponse handleError(AgentRequest request, Exception e) {
        return AgentResponse.builder()
                .requestId(request.getRequestId())
                .content("抱歉，处理您的请求时出现了问题：" + e.getMessage())
                .type("error")
                .confidence(0.0)
                .build();
    }
    
    /**
     * 检查是否能处理该意图
     */
    @Override
    public boolean canHandle(Intent intent) {
        return getSupportedIntents().contains(intent.getType());
    }
    
    /**
     * 获取该智能体支持的意图列表
     */
    protected abstract java.util.Set<IntentType> getSupportedIntents();
    
    @Override
    public String toString() {
        return String.format("%s(id=%s, type=%s)", 
                           getAgentName(), getAgentId(), getAgentType().getName());
    }
}
