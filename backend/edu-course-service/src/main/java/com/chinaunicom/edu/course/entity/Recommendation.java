package com.chinaunicom.edu.course.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.Builder;
import java.time.LocalDateTime;

@Data
@Builder
@TableName("recommendations")
public class Recommendation {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private Long courseId;
    
    /**
     * 推荐类型：content_based, collaborative_filtering, trending, knowledge_based
     */
    private String recommendationType;
    
    /**
     * 推荐分数 0-100
     */
    private Double recommendationScore;
    
    /**
     * 推荐理由
     */
    private String recommendationReason;
    
    /**
     * 是否点击
     */
    private Boolean isClicked;
    
    /**
     * 是否报名
     */
    private Boolean isEnrolled;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
    
    /**
     * 过期时间
     */
    private LocalDateTime expiredTime;
}
