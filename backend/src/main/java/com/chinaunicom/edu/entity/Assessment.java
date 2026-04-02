package com.chinaunicom.edu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 能力评估实体类
 */
@Data
@TableName("edu_assessment")
public class Assessment {
    
    /**
     * 评估ID
     */
    @TableId(type = IdType.AUTO)
    private Long assessmentId;
    
    /**
     * 评估名称
     */
    private String assessmentName;
    
    /**
     * 评估编码
     */
    private String assessmentCode;
    
    /**
     * 学科ID
     */
    private Long subjectId;
    
    /**
     * 适用年级
     */
    private Integer gradeLevel;
    
    /**
     * 评估类型：1-入学测试 2-阶段性测试 3-期末测试 4-专项能力测试
     */
    private Integer assessmentType;
    
    /**
     * 评估描述
     */
    private String description;
    
    /**
     * 总分
     */
    private BigDecimal totalScore;
    
    /**
     * 及格分数
     */
    private BigDecimal passingScore;
    
    /**
     * 考试时长（分钟）
     */
    private Integer duration;
    
    /**
     * 题目数量
     */
    private Integer questionCount;
    
    /**
     * 状态：0-草稿 1-发布 2-进行中 3-已结束
     */
    private Integer status;
    
    /**
     * 创建者ID
     */
    private Long creatorId;
    
    /**
     * 开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 结束时间
     */
    private LocalDateTime endTime;
    
    /**
     * 是否允许重复参加：0-否 1-是
     */
    private Integer allowRepeat;
    
    /**
     * 重考间隔（小时）
     */
    private Integer repeatInterval;
    
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