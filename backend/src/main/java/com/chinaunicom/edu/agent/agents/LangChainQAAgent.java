package com.chinaunicom.edu.agent.agents;

import com.chinaunicom.edu.service.EducationAIService;
import com.chinaunicom.edu.tools.EducationTools;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 问答智能体 - 使用LangChain4j实现
 * 负责回答用户问题、提供帮助信息
 */
@Component
public class LangChainQAAgent {
    
    @Autowired
    private ChatLanguageModel chatLanguageModel;
    
    @Autowired
    private ChatMemory chatMemory;
    
    @Autowired
    private EducationTools educationTools;
    
    // LangChain4j AI服务
    private EducationAIService educationAIService;
    
    // 交互历史记录
    private final ConcurrentHashMap<Long, List<InteractionRecord>> interactionHistory = new ConcurrentHashMap<>();
    
    /**
     * 初始化智能体
     */
    @PostConstruct
    public void init() {
        // 初始化LangChain4j服务（不使用tools，避免Qwen3服务不支持tool_choice）
        this.educationAIService = AiServices.builder(EducationAIService.class)
                .chatLanguageModel(chatLanguageModel)
                .chatMemory(chatMemory)
                // .tools(educationTools)  // 暂时禁用，Qwen3服务不支持tool_choice
                .build();
    }
    
    /**
     * 处理用户提问
     */
    public QAResponse processQuestion(Long userId, String question) {
        try {
            // 使用LangChain4j AI服务处理问题
            String aiResponse = educationAIService.chat(question);
            
            // 记录交互历史
            recordInteraction(userId, question, aiResponse);
            
            return QAResponse.builder()
                    .success(true)
                    .answer(aiResponse)
                    .timestamp(LocalDateTime.now())
                    .confidence(0.85)
                    .build();
                    
        } catch (Exception e) {
            // 异常处理
            String fallbackResponse = getFallbackResponse(question);
            
            recordInteraction(userId, question, "ERROR: " + e.getMessage());
            
            return QAResponse.builder()
                    .success(false)
                    .answer(fallbackResponse)
                    .errorMessage(e.getMessage())
                    .timestamp(LocalDateTime.now())
                    .confidence(0.3)
                    .build();
        }
    }
    
    /**
     * 获取用户交互历史
     */
    public List<InteractionRecord> getUserHistory(Long userId) {
        return interactionHistory.getOrDefault(userId, new ArrayList<>());
    }
    
    /**
     * 清除用户历史
     */
    public void clearUserHistory(Long userId) {
        interactionHistory.remove(userId);
    }
    
    /**
     * 记录交互历史
     */
    private void recordInteraction(Long userId, String question, String answer) {
        InteractionRecord record = new InteractionRecord();
        record.setUserId(userId);
        record.setQuestion(question);
        record.setAnswer(answer);
        record.setTimestamp(LocalDateTime.now());
        
        interactionHistory.computeIfAbsent(userId, k -> new ArrayList<>()).add(record);
        
        // 限制历史记录数量
        List<InteractionRecord> records = interactionHistory.get(userId);
        if (records.size() > 50) {
            records.remove(0);
        }
    }
    
    /**
     * 兜底响应
     */
    private String getFallbackResponse(String question) {
        question = question.toLowerCase();
        
        if (question.contains("你好") || question.contains("hello")) {
            return "您好！我是您的学习助手，请问有什么可以帮助您的吗？";
        }
        
        if (question.contains("课程")) {
            return "您可以在课程管理页面查看所有课程信息，或者告诉我您感兴趣的学习方向，我可以为您推荐合适的课程。";
        }
        
        if (question.contains("学习")) {
            return "建议您制定学习计划，循序渐进地学习。您可以在学习中心查看学习进度和相关资源。";
        }
        
        return "很抱歉，我暂时无法回答这个问题。您可以尝试换个方式提问，或者联系我们的客服老师获得帮助。";
    }
    
    /**
     * 问答响应类
     */
    public static class QAResponse {
        private boolean success;
        private String answer;
        private String errorMessage;
        private LocalDateTime timestamp;
        private double confidence;
        
        public static Builder builder() {
            return new Builder();
        }
        
        public static class Builder {
            private QAResponse response = new QAResponse();
            
            public Builder success(boolean success) {
                response.success = success;
                return this;
            }
            
            public Builder answer(String answer) {
                response.answer = answer;
                return this;
            }
            
            public Builder errorMessage(String errorMessage) {
                response.errorMessage = errorMessage;
                return this;
            }
            
            public Builder timestamp(LocalDateTime timestamp) {
                response.timestamp = timestamp;
                return this;
            }
            
            public Builder confidence(double confidence) {
                response.confidence = confidence;
                return this;
            }
            
            public QAResponse build() {
                return response;
            }
        }
        
        // Getters
        public boolean isSuccess() { return success; }
        public String getAnswer() { return answer; }
        public String getErrorMessage() { return errorMessage; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public double getConfidence() { return confidence; }
    }
    
    /**
     * 交互记录类
     */
    public static class InteractionRecord {
        private Long userId;
        private String question;
        private String answer;
        private LocalDateTime timestamp;
        
        // Getters and Setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        
        public String getQuestion() { return question; }
        public void setQuestion(String question) { this.question = question; }
        
        public String getAnswer() { return answer; }
        public void setAnswer(String answer) { this.answer = answer; }
        
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    }
}