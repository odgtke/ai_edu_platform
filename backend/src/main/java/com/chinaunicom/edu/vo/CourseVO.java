package com.chinaunicom.edu.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 课程VO
 */
@Data
public class CourseVO {
    
    private Long courseId;
    private String courseName;
    private String courseCode;
    private String description;
    private Long teacherId;
    private String teacherName;
    private Integer gradeLevel;
    private Long subjectId;
    private String subjectName;
    private Double credit;
    private Integer status;
    private String coverImage;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}