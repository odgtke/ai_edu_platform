package com.chinaunicom.edu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 作业实体类
 */
@Data
@TableName("edu_assignment")
public class Assignment {
    
    /**
     * 作业ID
     */
    @TableId(type = IdType.AUTO)
    private Long assignmentId;
    
    /**
     * 课程ID
     */
    private Long courseId;
    
    /**
     * 作业标题
     */
    private String title;
    
    /**
     * 作业描述
     */
    private String description;
    
    /**
     * 作业类型：1-课后练习 2-项目作业 3-考试
     */
    private Integer assignmentType;
    
    /**
     * 总分
     */
    private Integer totalScore;
    
    /**
     * 截止日期
     */
    private LocalDateTime deadline;
    
    /**
     * 状态：0-草稿 1-已发布 2-已结束
     */
    private Integer status;
    
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
