package com.chinaunicom.edu.nlp;

import lombok.Data;

/**
 * 意图识别结果
 */
@Data
public class IntentResult {
    /**
     * 识别出的意图类型
     */
    private IntentType intentType;
    
    /**
     * 置信度(0-1)
     */
    private double confidence;
    
    /**
     * 提取出的实体信息
     */
    private String entities;
    
    /**
     * 原始输入文本
     */
    private String inputText;
    
    public IntentResult() {
        this.confidence = 0.0;
    }
    
    public IntentResult(IntentType intentType, double confidence) {
        this.intentType = intentType;
        this.confidence = confidence;
    }
    
    public IntentResult(IntentType intentType, double confidence, String entities) {
        this.intentType = intentType;
        this.confidence = confidence;
        this.entities = entities;
    }
}