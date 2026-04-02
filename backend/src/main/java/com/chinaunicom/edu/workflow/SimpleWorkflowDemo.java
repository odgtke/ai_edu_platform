package com.chinaunicom.edu.workflow;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

/**
 * 简化版LangChain4j工作流演示
 * 展示核心工作流编排能力
 */
@Component
public class SimpleWorkflowDemo {

    @Autowired
    private ChatLanguageModel chatLanguageModel;

    private WorkflowAssistant workflowAssistant;

    @PostConstruct
    public void init() {
        this.workflowAssistant = AiServices.builder(WorkflowAssistant.class)
                .chatLanguageModel(chatLanguageModel)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(50))
                .tools(new DemoTools())
                .build();
    }

    /**
     * 执行教育工作流
     */
    public String executeEducationWorkflow(String userInput) {
        String instruction = "请按照以下步骤处理用户的教育需求：\n" +
                "1. 分析用户的学习目标\n" +
                "2. 评估所需的知识基础\n" +
                "3. 推荐合适的学习路径\n" +
                "4. 制定具体的学习计划\n\n" +
                "用户输入：" + userInput;
        
        return workflowAssistant.processEducationRequest(instruction);
    }

    /**
     * LangChain4j AI服务接口
     */
    interface WorkflowAssistant {
        String processEducationRequest(String request);
    }

    /**
     * 演示工具类
     */
    public static class DemoTools {
        
        @Tool("分析学习目标")
        public String analyzeGoal(String goal) {
            return "已分析学习目标：" + goal + "，识别出关键技能需求。";
        }

        @Tool("评估知识基础")
        public String assessKnowledge(String background) {
            return "知识基础评估完成，当前水平为中级。";
        }

        @Tool("推荐学习路径")
        public String recommendPath(String domain) {
            List<String> path = Arrays.asList("基础理论学习", "实践项目训练", "高级应用开发");
            return "推荐学习路径：" + String.join(" -> ", path);
        }

        @Tool("制定学习计划")
        public String createPlan(String requirements) {
            return "学习计划制定完成：建议8周完成，每周投入10小时。";
        }
    }
}