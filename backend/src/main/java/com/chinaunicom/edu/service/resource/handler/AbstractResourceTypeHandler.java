package com.chinaunicom.edu.service.resource.handler;

import com.chinaunicom.edu.service.resource.ResourceTypeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * 资源类型处理器抽象基类
 * 模板方法模式：定义资源处理的通用流程
 */
public abstract class AbstractResourceTypeHandler implements ResourceTypeHandler {
    
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    
    // 最大文件大小 500MB
    protected static final long MAX_FILE_SIZE = 500 * 1024 * 1024;
    
    @Override
    public ValidationResult validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ValidationResult.failure("文件不能为空");
        }
        
        // 检查文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            return ValidationResult.failure("文件大小不能超过500MB");
        }
        
        // 检查文件扩展名
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        
        if (!getSupportedExtensions().contains(extension.toLowerCase())) {
            return ValidationResult.failure("不支持的文件类型: " + extension);
        }
        
        // 子类特定的验证
        return doValidate(file);
    }
    
    /**
     * 子类特定的验证逻辑
     */
    protected abstract ValidationResult doValidate(MultipartFile file);
    
    @Override
    public FileProcessResult processUpload(MultipartFile file, String savePath) {
        try {
            String originalFilename = file.getOriginalFilename();
            String extension = getFileExtension(originalFilename);
            
            // 生成新文件名
            String newFileName = generateUniqueFileName(extension);
            String fullPath = savePath + File.separator + newFileName;
            
            // 确保目录存在
            Path path = Paths.get(savePath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
            
            // 保存文件
            File destFile = new File(fullPath);
            file.transferTo(destFile);
            
            // 提取元数据（如果支持）
            Metadata metadata = null;
            if (supportsMetadataExtraction()) {
                metadata = extractMetadata(fullPath);
            }
            
            logger.info("文件上传成功: {}, 类型: {}, 大小: {}", 
                    originalFilename, getResourceType(), formatFileSize(file.getSize()));
            
            return new FileProcessResult(newFileName, fullPath, file.getSize(), metadata);
            
        } catch (IOException e) {
            logger.error("文件上传失败", e);
            throw new RuntimeException("文件上传失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 生成唯一文件名
     */
    protected String generateUniqueFileName(String extension) {
        return UUID.randomUUID().toString().replace("-", "") + "." + extension.toLowerCase();
    }
    
    /**
     * 获取文件扩展名
     */
    protected String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }
    
    /**
     * 格式化文件大小
     */
    protected String formatFileSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.2f KB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", size / (1024.0 * 1024));
        } else {
            return String.format("%.2f GB", size / (1024.0 * 1024 * 1024));
        }
    }
}
