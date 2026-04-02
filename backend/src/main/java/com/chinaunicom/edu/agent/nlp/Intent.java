package com.chinaunicom.edu.agent.nlp;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 意图识别结果
 */
@Data
public class Intent {
    
    /**
     * 意图类型
     */
    private IntentType type;
    
    /**
     * 意图名称
     */
    private String name;
    
    /**
     * 置信度 (0-1)
     */
    private Double confidence;
    
    /**
     * 提取的实体
     */
    private Map<String, Object> entities;
    
    /**
     * 原始文本
     */
    private String rawText;
    
    public Intent() {
        this.entities = new HashMap<>();
    }
    
    public Intent(IntentType type, String name, Double confidence) {
        this();
        this.type = type;
        this.name = name;
        this.confidence = confidence;
    }
    
    public static Intent of(IntentType type, String name, Double confidence) {
        return new Intent(type, name, confidence);
    }
    
    public static Intent unknown() {
        return new Intent(IntentType.UNKNOWN, "unknown", 0.0);
    }
    
    public void addEntity(String key, Object value) {
        this.entities.put(key, value);
    }
    
    public Object getEntity(String key) {
        return this.entities.get(key);
    }
}
