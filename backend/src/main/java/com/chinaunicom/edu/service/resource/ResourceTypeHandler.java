package com.chinaunicom.edu.service.resource;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 资源类型处理器接口
 * 策略模式：定义不同类型资源的处理规范
 */
public interface ResourceTypeHandler {
    
    /**
     * 获取资源类型名称
     */
    String getResourceType();
    
    /**
     * 获取支持的文件扩展名列表
     */
    List<String> getSupportedExtensions();
    
    /**
     * 验证文件是否有效
     * @param file 上传的文件
     * @return 验证结果
     */
    ValidationResult validate(MultipartFile file);
    
    /**
     * 处理文件上传
     * @param file 上传的文件
     * @param savePath 保存路径
     * @return 处理后的文件信息
     */
    FileProcessResult processUpload(MultipartFile file, String savePath);
    
    /**
     * 获取文件存储子目录
     */
    String getStorageSubdirectory();
    
    /**
     * 是否需要提取元数据
     */
    boolean supportsMetadataExtraction();
    
    /**
     * 提取文件元数据
     * @param filePath 文件路径
     * @return 元数据信息
     */
    Metadata extractMetadata(String filePath);
    
    /**
     * 验证结果
     */
    class ValidationResult {
        private final boolean valid;
        private final String message;
        
        private ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }
        
        public static ValidationResult success() {
            return new ValidationResult(true, null);
        }
        
        public static ValidationResult failure(String message) {
            return new ValidationResult(false, message);
        }
        
        public boolean isValid() { return valid; }
        public String getMessage() { return message; }
    }
    
    /**
     * 文件处理结果
     */
    class FileProcessResult {
        private final String fileName;
        private final String filePath;
        private final long fileSize;
        private final Metadata metadata;
        
        public FileProcessResult(String fileName, String filePath, long fileSize, Metadata metadata) {
            this.fileName = fileName;
            this.filePath = filePath;
            this.fileSize = fileSize;
            this.metadata = metadata;
        }
        
        public String getFileName() { return fileName; }
        public String getFilePath() { return filePath; }
        public long getFileSize() { return fileSize; }
        public Metadata getMetadata() { return metadata; }
    }
    
    /**
     * 元数据
     */
    class Metadata {
        private Integer duration;  // 时长（视频/音频）
        private String resolution; // 分辨率（视频/图片）
        private Integer pageCount; // 页数（文档）
        private String encoding;   // 编码格式
        
        public Integer getDuration() { return duration; }
        public void setDuration(Integer duration) { this.duration = duration; }
        
        public String getResolution() { return resolution; }
        public void setResolution(String resolution) { this.resolution = resolution; }
        
        public Integer getPageCount() { return pageCount; }
        public void setPageCount(Integer pageCount) { this.pageCount = pageCount; }
        
        public String getEncoding() { return encoding; }
        public void setEncoding(String encoding) { this.encoding = encoding; }
    }
}
