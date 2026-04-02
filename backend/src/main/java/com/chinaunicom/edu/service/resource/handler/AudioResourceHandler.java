package com.chinaunicom.edu.service.resource.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

/**
 * 音频资源处理器
 * 工厂模式具体实现：处理音频类型资源
 */
@Component
public class AudioResourceHandler extends AbstractResourceTypeHandler {
    
    private static final List<String> SUPPORTED_EXTENSIONS = Arrays.asList(
            "mp3", "wav", "wma", "aac", "flac", "ogg", "m4a", "wma"
    );
    
    // 音频文件大小限制 500MB
    private static final long MAX_AUDIO_SIZE = 500 * 1024 * 1024;
    
    @Override
    public String getResourceType() {
        return "audio";
    }
    
    @Override
    public List<String> getSupportedExtensions() {
        return SUPPORTED_EXTENSIONS;
    }
    
    @Override
    protected ValidationResult doValidate(MultipartFile file) {
        if (file.getSize() > MAX_AUDIO_SIZE) {
            return ValidationResult.failure("音频文件大小不能超过500MB");
        }
        
        return ValidationResult.success();
    }
    
    @Override
    public String getStorageSubdirectory() {
        return "audios";
    }
    
    @Override
    public boolean supportsMetadataExtraction() {
        return true;
    }
    
    @Override
    public Metadata extractMetadata(String filePath) {
        Metadata metadata = new Metadata();
        
        // TODO: 集成音频处理库（如 JLayer 或 JavaSound）提取元数据
        metadata.setDuration(0);
        metadata.setEncoding("MP3");
        
        return metadata;
    }
}
