package com.chinaunicom.edu.service.resource.handler;

import com.chinaunicom.edu.service.resource.ResourceTypeHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

/**
 * 视频资源处理器
 * 工厂模式具体实现：处理视频类型资源
 */
@Component
public class VideoResourceHandler extends AbstractResourceTypeHandler {
    
    private static final List<String> SUPPORTED_EXTENSIONS = Arrays.asList(
            "mp4", "avi", "mov", "wmv", "flv", "mkv", "webm", "m4v", "3gp"
    );
    
    // 视频文件大小限制 2GB
    private static final long MAX_VIDEO_SIZE = 2L * 1024 * 1024 * 1024;
    
    @Override
    public String getResourceType() {
        return "video";
    }
    
    @Override
    public List<String> getSupportedExtensions() {
        return SUPPORTED_EXTENSIONS;
    }
    
    @Override
    protected ValidationResult doValidate(MultipartFile file) {
        // 视频文件大小限制更宽松
        if (file.getSize() > MAX_VIDEO_SIZE) {
            return ValidationResult.failure("视频文件大小不能超过2GB");
        }
        
        // TODO: 可以添加视频格式深度检测（通过文件头）
        
        return ValidationResult.success();
    }
    
    @Override
    public String getStorageSubdirectory() {
        return "videos";
    }
    
    @Override
    public boolean supportsMetadataExtraction() {
        return true;
    }
    
    @Override
    public Metadata extractMetadata(String filePath) {
        Metadata metadata = new Metadata();
        
        // TODO: 集成视频处理库（如 FFmpeg）提取真实元数据
        // 目前返回默认值
        metadata.setDuration(0);
        metadata.setResolution("1920x1080");
        metadata.setEncoding("H.264");
        
        return metadata;
    }
}
