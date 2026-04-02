package com.chinaunicom.edu.controller;

import com.chinaunicom.edu.agent.agents.LangChainQAAgent;
import com.chinaunicom.edu.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * LangChain智能体控制器
 * 专门用于LangChain4j驱动的智能体服务
 */
@Slf4j
@RestController
@RequestMapping("/api/langchain")
public class LangChainAgentController {
    
    @Autowired
    private LangChainQAAgent langChainQAAgent;
    
    /**
     * 使用LangChain处理问答
     */
    @PostMapping("/qa")
    public Result<LangChainQAAgent.QAResponse> askQuestion(@RequestBody QuestionRequest request) {
        try {
            Long userId = request.getUserId() != null ? request.getUserId() : 1L;
            String question = request.getQuestion();
            
            if (question == null || question.trim().isEmpty()) {
                return Result.error("问题不能为空");
            }
            
            LangChainQAAgent.QAResponse response = langChainQAAgent.processQuestion(userId, question);
            
            return Result.success(response);
            
        } catch (Exception e) {
            log.error("LangChain QA processing failed", e);
            return Result.error("问答服务暂时不可用: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户交互历史
     */
    @GetMapping("/history/{userId}")
    public Result<List<LangChainQAAgent.InteractionRecord>> getUserHistory(@PathVariable Long userId) {
        try {
            List<LangChainQAAgent.InteractionRecord> history = langChainQAAgent.getUserHistory(userId);
            return Result.success(history);
        } catch (Exception e) {
            log.error("Get user history failed", e);
            return Result.error("获取历史记录失败");
        }
    }
    
    /**
     * 清除用户历史
     */
    @DeleteMapping("/history/{userId}")
    public Result<String> clearUserHistory(@PathVariable Long userId) {
        try {
            langChainQAAgent.clearUserHistory(userId);
            return Result.success("历史记录已清除");
        } catch (Exception e) {
            log.error("Clear user history failed", e);
            return Result.error("清除历史记录失败");
        }
    }
    
    /**
     * 健康检查
     */
    @GetMapping("/health")
    public Result<String> healthCheck() {
        return Result.success("LangChain智能体服务运行正常");
    }
    
    /**
     * 问题请求类
     */
    public static class QuestionRequest {
        private Long userId;
        private String question;
        private String sessionId;
        
        // Getters and Setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        
        public String getQuestion() { return question; }
        public void setQuestion(String question) { this.question = question; }
        
        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    }
}