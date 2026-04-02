package com.chinaunicom.edu.learning.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("edu_learning_record")
public class LearningRecord {
    
    @TableId(type = IdType.AUTO)
    private Long recordId;
    
    private Long studentId;
    
    private Long courseId;
    
    @TableField("lesson_id")
    private Long lessonId;
    
    @TableField("study_duration")
    private Integer studyDuration;
    
    private Integer progress;
    
    @TableField("is_completed")
    private Boolean isCompleted;
    
    @TableField("last_study_time")
    private LocalDateTime lastStudyTime;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
