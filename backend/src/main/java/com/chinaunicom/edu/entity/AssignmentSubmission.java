package com.chinaunicom.edu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 作业提交记录实体类
 */
@Data
@TableName("edu_student_assignment")
public class AssignmentSubmission {
    
    /**
     * 提交ID
     */
    @TableId(value = "submit_id", type = IdType.AUTO)
    private Long submissionId;
    
    /**
     * 作业ID
     */
    private Long assignmentId;
    
    /**
     * 学生ID
     */
    private Long studentId;
    
    /**
     * 提交内容
     */
    private String content;
    
    /**
     * 附件URL
     */
    private String attachmentUrl;
    
    /**
     * 得分
     */
    private Integer score;
    
    /**
     * 状态：0-未提交 1-已提交 2-已批改
     */
    private Integer status;
    
    /**
     * 提交时间
     */
    private LocalDateTime submitTime;
    
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
