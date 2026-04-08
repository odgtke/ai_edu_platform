package com.chinaunicom.edu.course.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.Builder;
import java.time.LocalDateTime;

@Data
@Builder
@TableName("course_similarities")
public class CourseSimilarity {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long courseId1;
    
    private Long courseId2;
    
    /**
     * 相似度分数 0-1
     */
    private Double similarityScore;
    
    /**
     * 相似度类型：category(类别), content(内容), tag(标签), instructor(讲师)
     */
    private String similarityType;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime calculatedTime;
}
