package com.chinaunicom.edu.agent.core;

/**
 * 智能体类型枚举
 */
public enum AgentType {
    
    TEACHING("teaching", "教学智能体", "负责课程内容讲解、知识点解析、学习路径规划"),
    QA("qa", "问答智能体", "负责智能答疑、FAQ匹配、多轮对话"),
    RECOMMEND("recommend", "推荐智能体", "负责课程推荐、学习资源推荐、个性化内容推送"),
    ASSESSMENT("assessment", "评估智能体", "负责作业批改、学习效果评估、能力测评"),
    TUTOR("tutor", "辅导智能体", "负责一对一辅导、错题分析、学习建议"),
    COORDINATOR("coordinator", "协调智能体", "负责智能体之间的协调和任务分配");
    
    private final String code;
    private final String name;
    private final String description;
    
    AgentType(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static AgentType fromCode(String code) {
        for (AgentType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        return null;
    }
}
