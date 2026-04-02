package com.chinaunicom.edu.config;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.model.output.Response;
import java.time.Duration;
import java.util.List;

/**
 * LangChain4j 配置类
 */
@Configuration
public class LangChainConfig {

    @Value("${langchain4j.openai.api-key:#{null}}")
    private String openAiApiKey;

    @Value("${langchain4j.openai.base-url:https://api.openai.com/v1}")
    private String openAiBaseUrl;

    @Value("${langchain4j.openai.model:gpt-3.5-turbo}")
    private String openAiModel;

    @Value("${langchain4j.openai.temperature:0.3}")
    private Double temperature;

    @Value("${langchain4j.openai.top-p:0.8}")
    private Double topP;

    @Value("${langchain4j.openai.max-tokens:500}")
    private Integer maxTokens;

    @Value("${langchain4j.chat-memory.max-messages:10}")
    private Integer maxMessages;

    /**
     * 配置 OpenAI 聊天模型
     */
    @Bean
    public ChatLanguageModel chatLanguageModel() {
        if (openAiApiKey == null || openAiApiKey.isEmpty()) {
            // 如果没有配置 API key，则使用模拟模型
            return new MockChatModel();
        }

        return OpenAiChatModel.builder()
                .apiKey(openAiApiKey)
                .baseUrl(openAiBaseUrl)
                .modelName(openAiModel)
                .temperature(temperature)
                .topP(topP)
                .maxTokens(maxTokens)
                .timeout(Duration.ofSeconds(60))
                .logRequests(true)
                .logResponses(true)
                .build();
    }

    /**
     * 配置聊天记忆
     */
    @Bean
    public ChatMemory chatMemory() {
        return MessageWindowChatMemory.withMaxMessages(maxMessages);
    }

    /**
     * 模拟聊天模型 - 用于开发和测试
     */
    public static class MockChatModel implements ChatLanguageModel {
        @Override
        public String generate(String userMessage) {
            // 简单的模拟响应
            if (userMessage.contains("你好") || userMessage.contains("hello")) {
                return "您好！我是您的学习助手，有什么我可以帮助您的吗？";
            } else if (userMessage.contains("课程")) {
                return "我们有丰富的课程资源，包括编程、数学、英语等各个学科。您想了解哪方面的课程呢？";
            } else if (userMessage.contains("学习")) {
                return "学习是一个循序渐进的过程。建议您制定合理的学习计划，保持规律的学习习惯。";
            } else {
                return "感谢您的提问！我会尽力为您提供帮助。请问您还有其他问题吗？";
            }
        }
        
        @Override
        public Response<AiMessage> generate(List<ChatMessage> messages) {
            // 获取最后一条用户消息
            String userMessage = "";
            for (int i = messages.size() - 1; i >= 0; i--) {
                ChatMessage msg = messages.get(i);
                if (msg instanceof dev.langchain4j.data.message.UserMessage) {
                    userMessage = ((dev.langchain4j.data.message.UserMessage) msg).text();
                    break;
                }
            }
            
            String response = generate(userMessage);
            return Response.from(AiMessage.aiMessage(response));
        }
    }
}