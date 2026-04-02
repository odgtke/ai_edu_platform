package com.chinaunicom.edu.service.resource.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

/**
 * 文档资源处理器
 * 工厂模式具体实现：处理文档类型资源
 */
@Component
public class DocumentResourceHandler extends AbstractResourceTypeHandler {
    
    private static final List<String> SUPPORTED_EXTENSIONS = Arrays.asList(
            "pdf", "doc", "docx", "ppt", "pptx", "xls", "xlsx", 
            "txt", "rtf", "csv", "md", "epub", "mobi"
    );
    
    @Override
    public String getResourceType() {
        return "document";
    }
    
    @Override
    public List<String> getSupportedExtensions() {
        return SUPPORTED_EXTENSIONS;
    }
    
    @Override
    protected ValidationResult doValidate(MultipartFile file) {
        // 文档类型可以添加额外的验证
        // 例如：扫描病毒、检查文件内容等
        return ValidationResult.success();
    }
    
    @Override
    public String getStorageSubdirectory() {
        return "documents";
    }
    
    @Override
    public boolean supportsMetadataExtraction() {
        return true;
    }
    
    @Override
    public Metadata extractMetadata(String filePath) {
        Metadata metadata = new Metadata();
        
        // TODO: 集成文档处理库提取页数等信息
        // Apache POI for Office documents
        // PDFBox for PDF files
        metadata.setPageCount(0);
        metadata.setEncoding("UTF-8");
        
        return metadata;
    }
}
