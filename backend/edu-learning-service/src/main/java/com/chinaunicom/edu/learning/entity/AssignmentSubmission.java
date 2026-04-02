package com.chinaunicom.edu.learning.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("edu_assignment_submission")
public class AssignmentSubmission {
    
    @TableId(type = IdType.AUTO)
    private Long submissionId;
    
    private Long assignmentId;
    
    private Long studentId;
    
    private String content;
    
    private Integer score;
    
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime submitTime;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
