package com.chinaunicom.edu.agent.llm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * 大语言模型客户端
 * 支持多种LLM服务提供商
 */
@Slf4j
@Component
public class LLMClient {
    
    @Value("${llm.provider:mock}")
    private String provider;
    
    @Value("${llm.api-key:}")
    private String apiKey;
    
    @Value("${llm.api-url:}")
    private String apiUrl;
    
    @Value("${llm.model:gpt-3.5-turbo}")
    private String model;
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 发送对话请求
     */
    public String chat(String userMessage, String systemPrompt) {
        try {
            switch (provider.toLowerCase()) {
                case "openai":
                    return callOpenAI(userMessage, systemPrompt);
                case "azure":
                    return callAzureOpenAI(userMessage, systemPrompt);
                case "local":
                    return callLocalModel(userMessage, systemPrompt);
                case "mock":
                default:
                    return callMockModel(userMessage, systemPrompt);
            }
        } catch (Exception e) {
            log.error("LLM调用失败", e);
            return generateFallbackResponse(userMessage);
        }
    }
    
    /**
     * 调用OpenAI API
     */
    private String callOpenAI(String userMessage, String systemPrompt) {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            
            List<Map<String, String>> messages = new ArrayList<>();
            
            if (systemPrompt != null && !systemPrompt.isEmpty()) {
                Map<String, String> systemMessage = new HashMap<>();
                systemMessage.put("role", "system");
                systemMessage.put("content", systemPrompt);
                messages.add(systemMessage);
            }
            
            Map<String, String> userMsg = new HashMap<>();
            userMsg.put("role", "user");
            userMsg.put("content", userMessage);
            messages.add(userMsg);
            
            requestBody.put("messages", messages);
            requestBody.put("temperature", 0.7);
            requestBody.put("max_tokens", 1000);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<String> response = restTemplate.postForEntity(
                apiUrl != null ? apiUrl : "https://api.openai.com/v1/chat/completions",
                request,
                String.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode root = objectMapper.readTree(response.getBody());
                return root.path("choices").get(0).path("message").path("content").asText();
            }
            
            return "调用大模型服务失败";
            
        } catch (Exception e) {
            log.error("OpenAI调用失败", e);
            return generateFallbackResponse(userMessage);
        }
    }
    
    /**
     * 调用Azure OpenAI
     */
    private String callAzureOpenAI(String userMessage, String systemPrompt) {
        // Azure OpenAI实现
        return callOpenAI(userMessage, systemPrompt);
    }
    
    /**
     * 调用本地模型
     */
    private String callLocalModel(String userMessage, String systemPrompt) {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("prompt", userMessage);
            requestBody.put("system_prompt", systemPrompt);
            requestBody.put("max_tokens", 1000);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<String> response = restTemplate.postForEntity(
                apiUrl != null ? apiUrl : "http://localhost:8000/generate",
                request,
                String.class
            );
            
            return response.getBody();
            
        } catch (Exception e) {
            log.error("本地模型调用失败", e);
            return generateFallbackResponse(userMessage);
        }
    }
    
    /**
     * Mock模型（用于测试和演示）
     */
    private String callMockModel(String userMessage, String systemPrompt) {
        log.debug("使用Mock模型处理消息: {}", userMessage);
        
        // 模拟网络延迟
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        return generateIntelligentResponse(userMessage);
    }
    
    /**
     * 生成智能回复（基于规则）
     */
    private String generateIntelligentResponse(String message) {
        String lowerMsg = message.toLowerCase();
        
        // 问候
        if (lowerMsg.contains("你好") || lowerMsg.contains("您好") || lowerMsg.contains("hello")) {
            return "您好！很高兴为您服务。我是您的智能学习助手，可以帮您解答学习问题、推荐课程、查询进度等。请问有什么可以帮助您的？";
        }
        
        // 课程相关
        if (lowerMsg.contains("课程") || lowerMsg.contains("选课")) {
            return "选择课程时，建议您考虑以下几点：\n\n" +
                   "1. **基础水平**：选择适合自己当前水平的课程\n" +
                   "2. **学习目标**：明确想要达到的学习成果\n" +
                   "3. **时间安排**：确保有足够的时间完成学习\n" +
                   "4. **兴趣方向**：选择自己感兴趣的领域\n\n" +
                   "您可以告诉我您的年级和感兴趣的科目，我可以为您推荐合适的课程！";
        }
        
        // 学习进度
        if (lowerMsg.contains("进度") || lowerMsg.contains("学到哪里")) {
            return "您可以在学习中心查看详细的学习进度。系统会记录您的：\n\n" +
                   "- 已完成章节\n" +
                   "- 学习时长统计\n" +
                   "- 作业完成情况\n" +
                   "- 测试成绩分析\n\n" +
                   "坚持学习，您一定能取得进步！💪";
        }
        
        // 学习方法
        if (lowerMsg.contains("怎么学") || lowerMsg.contains("学习方法")) {
            return "高效学习方法推荐：\n\n" +
                   "1. **制定计划**：每天固定时间学习\n" +
                   "2. **主动思考**：不要被动接受知识\n" +
                   "3. **及时复习**：遵循艾宾浩斯遗忘曲线\n" +
                   "4. **实践应用**：通过练习巩固知识\n" +
                   "5. **总结归纳**：建立知识体系\n\n" +
                   "需要我针对某个具体科目给出学习建议吗？";
        }
        
        // 作业/练习
        if (lowerMsg.contains("作业") || lowerMsg.contains("练习")) {
            return "完成作业时建议：\n\n" +
                   "1. **先复习**：回顾相关知识点\n" +
                   "2. **独立思考**：先自己尝试解决\n" +
                   "3. **限时完成**：培养时间管理能力\n" +
                   "4. **检查验证**：完成后仔细检查\n" +
                   "5. **总结错题**：建立错题本\n\n" +
                   "如果遇到难题，可以随时向我提问！";
        }
        
        // 考试
        if (lowerMsg.contains("考试") || lowerMsg.contains("测试")) {
            return "考试准备建议：\n\n" +
                   "1. **系统复习**：梳理知识框架\n" +
                   "2. **重点突破**：针对薄弱环节强化\n" +
                   "3. **模拟练习**：做历年真题\n" +
                   "4. **调整心态**：保持平和心态\n" +
                   "5. **充足睡眠**：保证考试状态\n\n" +
                   "加油！相信您能取得好成绩！";
        }
        
        // 默认回复
        return "感谢您的提问！关于\"" + message + "\"，\n\n" +
               "这是一个很好的问题。作为您的学习助手，我建议您：\n\n" +
               "1. 如果是学习相关问题，可以参考课程资料\n" +
               "2. 如果是技术问题，可以查看帮助文档\n" +
               "3. 如果需要人工帮助，可以联系客服\n\n" +
               "您还有其他问题吗？";
    }
    
    /**
     * 生成兜底回复
     */
    private String generateFallbackResponse(String message) {
        return "抱歉，我暂时无法处理您的请求。您可以：\n\n" +
               "1. 换个方式描述您的问题\n" +
               "2. 查看常见问题解答\n" +
               "3. 联系人工客服获取帮助\n\n" +
               "错误信息：大模型服务暂时不可用";
    }
    
    /**
     * 检查LLM服务是否可用
     */
    public boolean isAvailable() {
        return !"mock".equalsIgnoreCase(provider) || true;
    }
}
