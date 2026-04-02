package com.chinaunicom.edu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 资源学习记录实体类
 * 记录学生对资源的学习进度
 */
@Data
@TableName("edu_resource_learning")
public class ResourceLearning {

    /**
     * 记录ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 学生ID
     */
    private Long studentId;

    /**
     * 资源ID
     */
    private Long resourceId;

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 学习进度(百分比)
     */
    private Integer progress;

    /**
     * 当前位置(秒), 视频专用
     */
    private Integer currentPosition;

    /**
     * 是否完成: 0-未完成, 1-已完成
     */
    private Integer isCompleted;

    /**
     * 最后学习时间
     */
    private LocalDateTime lastStudyTime;

    /**
     * 学习时长(秒)
     */
    private Integer studyDuration;

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
