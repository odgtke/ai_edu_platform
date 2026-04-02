package com.chinaunicom.edu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 年级实体类
 */
@Data
@TableName("edu_grade")
public class Grade {

    /**
     * 年级ID
     */
    @TableId(type = IdType.AUTO)
    private Long gradeId;

    /**
     * 年级名称
     */
    private String gradeName;

    /**
     * 年级级别(1-12)
     */
    private Integer gradeLevel;

    /**
     * 描述
     */
    private String description;

    /**
     * 状态: 0-禁用, 1-启用
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
