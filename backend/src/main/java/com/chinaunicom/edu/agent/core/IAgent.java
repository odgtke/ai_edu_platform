package com.chinaunicom.edu.agent.core;

import com.chinaunicom.edu.agent.core.model.AgentRequest;
import com.chinaunicom.edu.agent.core.model.AgentResponse;
import com.chinaunicom.edu.agent.nlp.Intent;

/**
 * 智能体接口
 * 所有智能体必须实现此接口
 */
public interface IAgent {
    
    /**
     * 获取智能体ID
     */
    String getAgentId();
    
    /**
     * 获取智能体名称
     */
    String getAgentName();
    
    /**
     * 获取智能体描述
     */
    String getAgentDescription();
    
    /**
     * 处理用户请求
     */
    AgentResponse process(AgentRequest request);
    
    /**
     * 判断是否能处理该意图
     */
    boolean canHandle(Intent intent);
    
    /**
     * 获取智能体类型
     */
    AgentType getAgentType();
    
    /**
     * 初始化智能体
     */
    void initialize();
    
    /**
     * 销毁智能体
     */
    void destroy();
}
