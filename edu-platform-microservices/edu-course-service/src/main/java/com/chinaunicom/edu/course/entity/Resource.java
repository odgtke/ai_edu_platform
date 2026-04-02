package com.chinaunicom.edu.course.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("edu_resource")
public class Resource {
    
    @TableId(type = IdType.AUTO)
    private Long resourceId;
    
    private String resourceName;
    
    private String resourceType;
    
    @TableField("file_name")
    private String fileName;
    
    @TableField("file_path")
    private String filePath;
    
    @TableField("file_size")
    private Long fileSize;
    
    @TableField("file_format")
    private String fileFormat;
    
    private Integer duration;
    
    @TableField("thumbnail_url")
    private String thumbnailUrl;
    
    private String description;
    
    @TableField("uploader_id")
    private Long uploaderId;
    
    @TableField("uploader_name")
    private String uploaderName;
    
    private Integer status;
    
    @TableField("download_count")
    private Integer downloadCount;
    
    @TableField("view_count")
    private Integer viewCount;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
