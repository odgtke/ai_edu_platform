package com.chinaunicom.edu.agent;

import com.chinaunicom.edu.agent.agents.LangChainQAAgent;
import com.chinaunicom.edu.agent.core.AgentManager;
import com.chinaunicom.edu.agent.core.model.AgentRequest;
import com.chinaunicom.edu.agent.core.model.AgentResponse;
import com.chinaunicom.edu.agent.nlp.Intent;
import com.chinaunicom.edu.agent.nlp.IntentRecognizer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * 智能体服务
 * 封装智能体调用逻辑，处理智能体注册和请求分发
 */
@Slf4j
@Service
public class AgentService {
    
    @Autowired
    private AgentManager agentManager;
    
    @Autowired
    private IntentRecognizer intentRecognizer;
    
    @Autowired
    private LangChainQAAgent langChainQAAgent;
    
    /**
     * 初始化时注册所有智能体
     */
    @PostConstruct
    public void init() {
        log.info("Initializing AgentService and registering agents...");
        
        // LangChain智能体不需要通过AgentManager注册
        log.info("Using LangChain-based QA agent");
        
        log.info("AgentService initialized successfully. Registered {} agents", 
                agentManager.getAllAgents().size());
    }
    
    /**
     * 处理用户消息
     */
    public AgentResponse processMessage(Long userId, String sessionId, String message) {
        log.debug("Processing message from user {}: {}", userId, message);
        
        try {
            // 1. 识别意图
            Intent intent = intentRecognizer.recognize(message);
            log.debug("Recognized intent: {} (confidence: {})", 
                    intent.getType().getName(), intent.getConfidence());
            
            // 2. 构建请求
            AgentRequest request = AgentRequest.builder()
                    .userId(userId)
                    .sessionId(sessionId)
                    .message(message)
                    .intent(intent)
                    .addContext("timestamp", System.currentTimeMillis())
                    .build();
            
            // 3. 通过AgentManager处理请求
            return agentManager.processRequest(request);
            
        } catch (Exception e) {
            log.error("Failed to process message: {}", message, e);
            return AgentResponse.error("处理消息时出现错误，请稍后再试");
        }
    }
    
    /**
     * 获取智能体状态
     */
    public AgentStatus getStatus() {
        AgentStatus status = new AgentStatus();
        status.setAgentCount(agentManager.getAllAgents().size());
        status.setStatus("running");
        status.setVersion("1.0.0");
        return status;
    }
    
    /**
     * 智能体状态类
     */
    public static class AgentStatus {
        private int agentCount;
        private String status;
        private String version;
        
        public int getAgentCount() { return agentCount; }
        public void setAgentCount(int agentCount) { this.agentCount = agentCount; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
    }
}
