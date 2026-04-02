package com.chinaunicom.edu.agent;

import com.chinaunicom.edu.agent.core.model.AgentResponse;
import com.chinaunicom.edu.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * 智能体控制器
 * 提供智能体相关的API接口
 */
@Slf4j
@RestController
@RequestMapping("/agent")
public class AgentController {
    
    @Autowired
    private AgentService agentService;
    
    /**
     * 发送消息给智能体
     */
    @PostMapping("/chat")
    public Result<AgentResponse> chat(@RequestBody ChatRequest chatRequest) {
        try {
            String sessionId = chatRequest.getSessionId() != null ? 
                    chatRequest.getSessionId() : UUID.randomUUID().toString();
            
            // 使用AgentService处理消息
            AgentResponse response = agentService.processMessage(
                    chatRequest.getUserId(), 
                    sessionId, 
                    chatRequest.getMessage()
            );
            
            return Result.success(response);
            
        } catch (Exception e) {
            log.error("Agent chat failed", e);
            return Result.error("智能体服务暂时不可用: " + e.getMessage());
        }
    }
    
    /**
     * 创建新的会话
     */
    @PostMapping("/session")
    public Result<SessionResponse> createSession() {
        try {
            String sessionId = UUID.randomUUID().toString();
            
            SessionResponse response = new SessionResponse();
            response.setSessionId(sessionId);
            response.setStatus("active");
            response.setCreateTime(System.currentTimeMillis());
            
            return Result.success(response);
            
        } catch (Exception e) {
            log.error("Create session failed", e);
            return Result.error("创建会话失败");
        }
    }
    
    /**
     * 获取智能体状态
     */
    @GetMapping("/status")
    public Result<AgentService.AgentStatus> getStatus() {
        try {
            AgentService.AgentStatus status = agentService.getStatus();
            return Result.success(status);
            
        } catch (Exception e) {
            log.error("Get agent status failed", e);
            return Result.error("获取状态失败");
        }
    }
    
    // 内部请求类
    public static class ChatRequest {
        private Long userId;
        private String sessionId;
        private String message;
        
        // getters and setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        
        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
    
    // 内部响应类
    public static class SessionResponse {
        private String sessionId;
        private String status;
        private Long createTime;
        
        // getters and setters
        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public Long getCreateTime() { return createTime; }
        public void setCreateTime(Long createTime) { this.createTime = createTime; }
    }
    
    public static class AgentStatus {
        private int agentCount;
        private String status;
        private String version;
        
        // getters and setters
        public int getAgentCount() { return agentCount; }
        public void setAgentCount(int agentCount) { this.agentCount = agentCount; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
    }
}
