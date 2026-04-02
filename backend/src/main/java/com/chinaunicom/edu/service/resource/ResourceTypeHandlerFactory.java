package com.chinaunicom.edu.service.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 资源类型处理器工厂
 * 工厂模式 + 注册表模式：统一管理资源类型处理器的创建和获取
 */
@Component
public class ResourceTypeHandlerFactory {
    
    // 扩展名 -> 处理器映射
    private final Map<String, ResourceTypeHandler> extensionToHandler = new ConcurrentHashMap<>();
    
    // 资源类型 -> 处理器映射
    private final Map<String, ResourceTypeHandler> typeToHandler = new ConcurrentHashMap<>();
    
    @Autowired
    private List<ResourceTypeHandler> handlers;
    
    /**
     * 初始化时注册所有处理器
     */
    @PostConstruct
    public void initialize() {
        for (ResourceTypeHandler handler : handlers) {
            registerHandler(handler);
        }
    }
    
    /**
     * 注册处理器
     */
    public void registerHandler(ResourceTypeHandler handler) {
        String resourceType = handler.getResourceType();
        typeToHandler.put(resourceType.toLowerCase(), handler);
        
        // 注册扩展名映射
        for (String ext : handler.getSupportedExtensions()) {
            extensionToHandler.put(ext.toLowerCase(), handler);
        }
    }
    
    /**
     * 根据扩展名获取处理器
     * @param extension 文件扩展名（不含点）
     * @return 处理器，如果不支持则返回null
     */
    public Optional<ResourceTypeHandler> getHandlerByExtension(String extension) {
        if (extension == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(extensionToHandler.get(extension.toLowerCase()));
    }
    
    /**
     * 根据资源类型获取处理器
     * @param resourceType 资源类型（video/document/image/audio）
     * @return 处理器
     */
    public Optional<ResourceTypeHandler> getHandlerByType(String resourceType) {
        if (resourceType == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(typeToHandler.get(resourceType.toLowerCase()));
    }
    
    /**
     * 判断扩展名是否受支持
     */
    public boolean isExtensionSupported(String extension) {
        return extension != null && extensionToHandler.containsKey(extension.toLowerCase());
    }
    
    /**
     * 获取所有支持的扩展名
     */
    public Set<String> getAllSupportedExtensions() {
        return new HashSet<>(extensionToHandler.keySet());
    }
    
    /**
     * 获取所有资源类型
     */
    public Set<String> getAllResourceTypes() {
        return new HashSet<>(typeToHandler.keySet());
    }
    
    /**
     * 获取所有处理器
     */
    public Collection<ResourceTypeHandler> getAllHandlers() {
        return new ArrayList<>(typeToHandler.values());
    }
    
    /**
     * 根据文件名判断资源类型
     * @param filename 文件名
     * @return 资源类型，如果不支持则返回null
     */
    public Optional<String> determineResourceType(String filename) {
        if (filename == null || !filename.contains(".")) {
            return Optional.empty();
        }
        
        String extension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
        return getHandlerByExtension(extension)
                .map(ResourceTypeHandler::getResourceType);
    }
    
    /**
     * 获取指定类型的所有扩展名
     */
    public List<String> getExtensionsByType(String resourceType) {
        return getHandlerByType(resourceType)
                .map(ResourceTypeHandler::getSupportedExtensions)
                .orElse(Collections.emptyList());
    }
    
    /**
     * 移除处理器
     */
    public void unregisterHandler(String resourceType) {
        ResourceTypeHandler removed = typeToHandler.remove(resourceType.toLowerCase());
        if (removed != null) {
            // 移除扩展名映射
            for (String ext : removed.getSupportedExtensions()) {
                extensionToHandler.remove(ext.toLowerCase());
            }
        }
    }
}
