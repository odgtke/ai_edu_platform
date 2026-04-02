package com.chinaunicom.edu.service.resource.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

/**
 * 图片资源处理器
 * 工厂模式具体实现：处理图片类型资源
 */
@Component
public class ImageResourceHandler extends AbstractResourceTypeHandler {
    
    private static final List<String> SUPPORTED_EXTENSIONS = Arrays.asList(
            "jpg", "jpeg", "png", "gif", "bmp", "webp", "svg", "ico", "tiff"
    );
    
    // 图片文件大小限制 50MB
    private static final long MAX_IMAGE_SIZE = 50 * 1024 * 1024;
    
    @Override
    public String getResourceType() {
        return "image";
    }
    
    @Override
    public List<String> getSupportedExtensions() {
        return SUPPORTED_EXTENSIONS;
    }
    
    @Override
    protected ValidationResult doValidate(MultipartFile file) {
        if (file.getSize() > MAX_IMAGE_SIZE) {
            return ValidationResult.failure("图片文件大小不能超过50MB");
        }
        
        // TODO: 可以添加图片格式深度检测
        // 检查文件头魔数（Magic Number）
        
        return ValidationResult.success();
    }
    
    @Override
    public String getStorageSubdirectory() {
        return "images";
    }
    
    @Override
    public boolean supportsMetadataExtraction() {
        return true;
    }
    
    @Override
    public Metadata extractMetadata(String filePath) {
        Metadata metadata = new Metadata();
        
        // TODO: 集成图片处理库（如 Thumbnailator 或 ImageIO）提取元数据
        metadata.setResolution("1920x1080");
        metadata.setEncoding("RGB");
        
        return metadata;
    }
}
