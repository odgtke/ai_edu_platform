package com.chinaunicom.edu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 资源实体类
 * 存储上传的视频、文档等资源文件
 */
@Data
@TableName("edu_resource")
public class Resource {

    /**
     * 资源ID
     */
    @TableId(type = IdType.AUTO)
    private Long resourceId;

    /**
     * 资源名称
     */
    private String resourceName;

    /**
     * 资源类型: video-视频, document-文档, image-图片, audio-音频, other-其他
     */
    private String resourceType;

    /**
     * 原始文件名
     */
    private String fileName;

    /**
     * 文件存储路径
     */
    private String filePath;

    /**
     * 文件大小(字节)
     */
    private Long fileSize;

    /**
     * 文件格式(mp4, pdf, doc等)
     */
    private String fileFormat;

    /**
     * 时长(秒), 视频/音频专用
     */
    private Integer duration;

    /**
     * 缩略图URL, 视频/图片专用
     */
    private String thumbnailUrl;

    /**
     * 资源描述
     */
    private String description;

    /**
     * 上传者ID
     */
    private Long uploaderId;

    /**
     * 上传者名称
     */
    private String uploaderName;

    /**
     * 状态: 0-禁用, 1-启用, 2-转码中
     */
    private Integer status;

    /**
     * 下载次数
     */
    private Integer downloadCount;

    /**
     * 浏览次数
     */
    private Integer viewCount;

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
