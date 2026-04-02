package com.chinaunicom.edu.agent.core;

import com.chinaunicom.edu.agent.core.model.AgentRequest;
import com.chinaunicom.edu.agent.core.model.AgentResponse;
import com.chinaunicom.edu.agent.nlp.Intent;
import com.chinaunicom.edu.agent.nlp.IntentRecognizer;
import com.chinaunicom.edu.agent.nlp.IntentType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 智能体管理器
 * 负责智能体的注册、发现和路由
 */
@Slf4j
@Service
public class AgentManager {
    
    @Autowired
    private IntentRecognizer intentRecognizer;
    
    @Autowired
    private AgentContext agentContext;
    
    // 智能体注册表 (agentId -> agent)
    private final Map<String, IAgent> registeredAgents = new ConcurrentHashMap<>();
    
    // 意图到智能体的映射 (intentType -> agentIds)
    private final Map<String, Set<String>> intentToAgents = new ConcurrentHashMap<>();
    
    // 智能体类型到智能体的映射 (agentType -> agentIds)
    private final Map<AgentType, Set<String>> typeToAgents = new ConcurrentHashMap<>();
    
    /**
     * 注册智能体
     */
    public void registerAgent(IAgent agent) {
        String agentId = agent.getAgentId();
        AgentType agentType = agent.getAgentType();
        
        // 注册到主注册表
        registeredAgents.put(agentId, agent);
        
        // 注册到类型映射
        typeToAgents.computeIfAbsent(agentType, k -> new HashSet<>()).add(agentId);
        
        // 注册意图映射
        Set<IntentType> supportedIntents = getSupportedIntents(agent);
        for (IntentType intent : supportedIntents) {
            intentToAgents.computeIfAbsent(intent.getCode(), k -> new HashSet<>()).add(agentId);
        }
        
        log.info("Registered agent: {} (type: {}, intents: {})", 
                agentId, agentType.getName(), supportedIntents);
    }
    
    /**
     * 获取智能体支持的意图列表
     */
    @SuppressWarnings("unchecked")
    private Set<IntentType> getSupportedIntents(IAgent agent) {
        try {
            // 通过反射调用受保护的方法
            java.lang.reflect.Method method = BaseAgent.class.getDeclaredMethod("getSupportedIntents");
            method.setAccessible(true);
            return (Set<IntentType>) method.invoke(agent);
        } catch (Exception e) {
            log.warn("Failed to get supported intents for agent: {}", agent.getAgentId(), e);
            return Collections.emptySet();
        }
    }
    
    /**
     * 根据意图选择合适的智能体
     */
    public List<IAgent> selectAgents(Intent intent) {
        List<IAgent> candidates = new ArrayList<>();
        
        // 首先尝试精确匹配
        Set<String> agentIds = intentToAgents.get(intent.getType().getCode());
        if (agentIds != null) {
            for (String agentId : agentIds) {
                IAgent agent = registeredAgents.get(agentId);
                if (agent != null && agent.canHandle(intent)) {
                    candidates.add(agent);
                }
            }
        }
        
        // 如果没有精确匹配，尝试类型匹配
        if (candidates.isEmpty()) {
            AgentType fallbackType = getFallbackAgentType(intent.getType());
            if (fallbackType != null) {
                Set<String> fallbackAgentIds = typeToAgents.get(fallbackType);
                if (fallbackAgentIds != null) {
                    for (String agentId : fallbackAgentIds) {
                        IAgent agent = registeredAgents.get(agentId);
                        if (agent != null) {
                            candidates.add(agent);
                        }
                    }
                }
            }
        }
        
        return candidates;
    }
    
    /**
     * 获取回退智能体类型
     */
    private AgentType getFallbackAgentType(IntentType intentType) {
        switch (intentType) {
            case COURSE_QUERY:
            case COURSE_RECOMMEND:
            case COURSE_ENROLL:
                return AgentType.RECOMMEND;
            case QA_ASK:
            case QA_EXPLAIN:
                return AgentType.QA;
            case LEARNING_START:
            case LEARNING_PROGRESS:
            case LEARNING_HISTORY:
                return AgentType.TEACHING;
            case ASSESSMENT_START:
            case ASSESSMENT_RESULT:
                return AgentType.ASSESSMENT;
            default:
                return AgentType.COORDINATOR;
        }
    }
    
    /**
     * 处理用户请求
     */
    public AgentResponse processRequest(AgentRequest request) {
        long startTime = System.currentTimeMillis();
        
        try {
            // 1. 意图识别
            Intent intent = intentRecognizer.recognize(request.getMessage());
            request.setIntent(intent);
            
            log.debug("Recognized intent: {} (confidence: {})", 
                     intent.getName(), intent.getConfidence());
            
            // 2. 选择智能体
            List<IAgent> agents = selectAgents(intent);
            if (agents.isEmpty()) {
                log.warn("No suitable agent found for intent: {}", intent.getType());
                return AgentResponse.error("抱歉，暂时无法处理您的请求");
            }
            
            // 3. 路由到最佳智能体
            IAgent selectedAgent = selectBestAgent(agents, request, intent);
            log.debug("Selected agent: {}", selectedAgent.getAgentId());
            
            // 4. 执行处理
            AgentResponse response = selectedAgent.process(request);
            response.setProcessTime(System.currentTimeMillis() - startTime);
            
            log.debug("Request processed successfully, took {}ms", response.getProcessTime());
            return response;
            
        } catch (Exception e) {
            log.error("Failed to process request", e);
            return AgentResponse.builder()
                    .requestId(request.getRequestId())
                    .content("系统出现错误，请稍后再试")
                    .type("error")
                    .confidence(0.0)
                    .processTime(System.currentTimeMillis() - startTime)
                    .build();
        }
    }
    
    /**
     * 选择最佳智能体
     */
    private IAgent selectBestAgent(List<IAgent> agents, AgentRequest request, Intent intent) {
        // 简单策略：选择第一个可用的智能体
        // 可以扩展为基于负载、性能、用户偏好的复杂策略
        return agents.get(0);
    }
    
    /**
     * 获取所有已注册的智能体
     */
    public Collection<IAgent> getAllAgents() {
        return registeredAgents.values();
    }
    
    /**
     * 根据ID获取智能体
     */
    public IAgent getAgentById(String agentId) {
        return registeredAgents.get(agentId);
    }
    
    /**
     * 根据类型获取智能体
     */
    public List<IAgent> getAgentsByType(AgentType type) {
        Set<String> agentIds = typeToAgents.get(type);
        if (agentIds == null) {
            return Collections.emptyList();
        }
        
        List<IAgent> agents = new ArrayList<>();
        for (String agentId : agentIds) {
            IAgent agent = registeredAgents.get(agentId);
            if (agent != null) {
                agents.add(agent);
            }
        }
        return agents;
    }
    
    /**
     * 初始化时自动注册所有智能体Bean
     */
    @PostConstruct
    public void autoRegisterAgents() {
        // 这里可以通过ApplicationContext获取所有IAgent类型的Bean
        // 但在实际项目中通常通过Spring的自动装配完成
        log.info("Agent manager initialized with {} agents", registeredAgents.size());
    }
}
