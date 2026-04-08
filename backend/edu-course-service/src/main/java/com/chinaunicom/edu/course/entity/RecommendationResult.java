package com.chinaunicom.edu.course.entity;

import lombok.Data;
import lombok.Builder;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class RecommendationResult {
    
    private Long courseId;
    
    private String courseName;
    
    /**
     * 原始分数
     */
    private Double score;
    
    /**
     * 加权后最终分数
     */
    private Double finalScore;
    
    /**
     * 主要来源策略
     */
    private String source;
    
    /**
     * 所有来源策略（混合推荐时）
     */
    @Builder.Default
    private Set<String> sources = new HashSet<>();
    
    /**
     * 推荐理由
     */
    private String reason;
    
    /**
     * 生成时间
     */
    private LocalDateTime generatedTime;
    
    /**
     * 添加分数
     */
    public void addScore(double delta) {
        if (this.finalScore == null) {
            this.finalScore = 0.0;
        }
        this.finalScore += delta;
    }
    
    /**
     * 添加来源
     */
    public void addSource(String source) {
        if (this.sources == null) {
            this.sources = new HashSet<>();
        }
        this.sources.add(source);
    }
}
