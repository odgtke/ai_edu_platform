package com.chinaunicom.edu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 课程实体类
 */
@Data
@TableName("edu_course")
public class Course {
    
    /**
     * 课程ID
     */
    @TableId(type = IdType.AUTO)
    private Long courseId;
    
    /**
     * 课程名称
     */
    private String courseName;
    
    /**
     * 课程编码
     */
    private String courseCode;
    
    /**
     * 课程描述
     */
    private String description;
    
    /**
     * 授课教师ID
     */
    private Long teacherId;
    
    /**
     * 年级
     */
    private Integer gradeLevel;
    
    /**
     * 学科ID
     */
    private Long subjectId;
    
    /**
     * 学分
     */
    private Double credit;
    
    /**
     * 状态：0-停用 1-启用
     */
    private Integer status;
    
    /**
     * 封面图片URL
     */
    private String coverImage;
    
    /**
     * 总课时数
     */
    private Integer totalLessons;
    
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