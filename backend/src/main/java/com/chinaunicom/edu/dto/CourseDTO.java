package com.chinaunicom.edu.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 课程DTO
 */
@Data
public class CourseDTO {
    
    @NotBlank(message = "课程名称不能为空")
    private String courseName;
    
    @NotBlank(message = "课程编码不能为空")
    private String courseCode;
    
    private String description;
    
    @NotNull(message = "教师ID不能为空")
    private Long teacherId;
    
    @NotNull(message = "年级不能为空")
    private Integer gradeLevel;
    
    @NotNull(message = "学科ID不能为空")
    private Long subjectId;
    
    private Double credit;
    private Integer status;
    private String coverImage;
}