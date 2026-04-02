package com.chinaunicom.edu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户评估结果实体类
 */
@Data
@TableName("edu_user_assessment")
public class UserAssessment {
    
    /**
     * 结果ID
     */
    @TableId(type = IdType.AUTO)
    private Long resultId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 评估ID
     */
    private Long assessmentId;
    
    /**
     * 开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 结束时间
     */
    private LocalDateTime endTime;
    
    /**
     * 用时（秒）
     */
    private Integer timeSpent;
    
    /**
     * 总得分
     */
    private BigDecimal totalScore;
    
    /**
     * 得分百分比
     */
    private BigDecimal scorePercentage;
    
    /**
     * 是否及格：0-不及格 1-及格
     */
    private Integer isPassed;
    
    /**
     * 答题详情（JSON格式存储）
     */
    private String answerDetails;
    
    /**
     * 评估状态：1-进行中 2-已完成 3-已批改
     */
    private Integer status;
    
    /**
     * 教师评语
     */
    private String teacherComment;
    
    /**
     * 能力分析报告（JSON格式）
     */
    private String capabilityReport;
    
    /**
     * 推荐学习路径
     */
    private String recommendedPath;
    
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