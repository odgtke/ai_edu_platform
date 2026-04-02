package com.chinaunicom.edu.nlp;

/**
 * 意图类型枚举
 */
public enum IntentType {
    /**
     * 问候意图
     */
    GREETING,
    
    /**
     * 课程查询意图
     */
    COURSE_QUERY,
    
    /**
     * 学习进度查询意图
     */
    PROGRESS_QUERY,
    
    /**
     * 作业查询意图
     */
    ASSIGNMENT_QUERY,
    
    /**
     * 考试查询意图
     */
    EXAM_QUERY,
    
    /**
     * 推荐意图
     */
    RECOMMENDATION,
    
    /**
     * 其他意图
     */
    OTHER
}