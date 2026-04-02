package com.chinaunicom.edu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 班级实体类
 * 注意：类名用 Clazz 避免与 java.lang.Class 冲突
 */
@Data
@TableName("edu_class")
public class Clazz {

    /**
     * 班级ID
     */
    @TableId(type = IdType.AUTO)
    private Long classId;

    /**
     * 班级名称
     */
    private String className;

    /**
     * 年级ID
     */
    private Long gradeId;

    /**
     * 班主任ID
     */
    private Long teacherId;

    /**
     * 班主任名称
     */
    private String teacherName;

    /**
     * 学生人数
     */
    private Integer studentCount;

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

    /**
     * 年级名称（非数据库字段）
     */
    @TableField(exist = false)
    private String gradeName;
}
