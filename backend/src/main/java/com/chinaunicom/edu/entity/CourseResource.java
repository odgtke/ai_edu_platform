package com.chinaunicom.edu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 课程资源关联实体类
 * 建立课程与资源的关联关系
 */
@Data
@TableName("edu_course_resource")
public class CourseResource {

    /**
     * 关联ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 资源ID
     */
    private Long resourceId;

    /**
     * 章节ID(可选)
     */
    private Long chapterId;

    /**
     * 资源排序
     */
    private Integer resourceOrder;

    /**
     * 是否必学: 0-选学, 1-必学
     */
    private Integer isRequired;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
