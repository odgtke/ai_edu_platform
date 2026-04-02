package com.chinaunicom.edu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户班级关联实体类
 */
@Data
@TableName("edu_user_class")
public class UserClass {

    /**
     * 关联ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 班级ID
     */
    private Long classId;

    /**
     * 用户类型: student-学生, teacher-教师
     */
    private String userType;

    /**
     * 加入时间
     */
    private LocalDateTime joinTime;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 用户姓名（非数据库字段）
     */
    @TableField(exist = false)
    private String userName;

    /**
     * 班级名称（非数据库字段）
     */
    @TableField(exist = false)
    private String className;

    /**
     * 年级名称（非数据库字段）
     */
    @TableField(exist = false)
    private String gradeName;
}
