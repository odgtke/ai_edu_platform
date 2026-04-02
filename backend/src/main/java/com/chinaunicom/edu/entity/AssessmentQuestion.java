package com.chinaunicom.edu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 评估题目实体类
 */
@Data
@TableName("edu_assessment_question")
public class AssessmentQuestion {
    
    /**
     * 题目ID
     */
    @TableId(type = IdType.AUTO)
    private Long questionId;
    
    /**
     * 评估ID
     */
    private Long assessmentId;
    
    /**
     * 题目序号
     */
    private Integer questionOrder;
    
    /**
     * 题目类型：1-单选题 2-多选题 3-判断题 4-填空题 5-简答题
     */
    private Integer questionType;
    
    /**
     * 题目内容
     */
    private String content;
    
    /**
     * 题目选项（JSON格式存储）
     */
    private String options;
    
    /**
     * 正确答案
     */
    private String correctAnswer;
    
    /**
     * 题目分数
     */
    private BigDecimal score;
    
    /**
     * 难度等级：1-简单 2-中等 3-困难
     */
    private Integer difficulty;
    
    /**
     * 知识点标签
     */
    private String tags;
    
    /**
     * 解析说明
     */
    private String analysis;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}