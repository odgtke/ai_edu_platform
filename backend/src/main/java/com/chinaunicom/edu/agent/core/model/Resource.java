package com.chinaunicom.edu.agent.core.model;

import lombok.Data;

/**
 * 资源信息
 */
@Data
public class Resource {
    
    /**
     * 资源ID
     */
    private String resourceId;
    
    /**
     * 资源类型 (course, video, document, link等)
     */
    private String type;
    
    /**
     * 资源标题
     */
    private String title;
    
    /**
     * 资源描述
     */
    private String description;
    
    /**
     * 资源链接
     */
    private String url;
    
    /**
     * 封面图片
     */
    private String coverImage;
    
    /**
     * 相关度评分 (0-1)
     */
    private Double relevance;
    
    public Resource() {}
    
    public Resource(String type, String title, String description, String url) {
        this.type = type;
        this.title = title;
        this.description = description;
        this.url = url;
    }
    
    public static ResourceBuilder builder() {
        return new ResourceBuilder();
    }
    
    public static class ResourceBuilder {
        private Resource resource = new Resource();
        
        public ResourceBuilder resourceId(String resourceId) {
            resource.setResourceId(resourceId);
            return this;
        }
        
        public ResourceBuilder type(String type) {
            resource.setType(type);
            return this;
        }
        
        public ResourceBuilder title(String title) {
            resource.setTitle(title);
            return this;
        }
        
        public ResourceBuilder description(String description) {
            resource.setDescription(description);
            return this;
        }
        
        public ResourceBuilder url(String url) {
            resource.setUrl(url);
            return this;
        }
        
        public ResourceBuilder coverImage(String coverImage) {
            resource.setCoverImage(coverImage);
            return this;
        }
        
        public ResourceBuilder relevance(Double relevance) {
            resource.setRelevance(relevance);
            return this;
        }
        
        public Resource build() {
            return resource;
        }
    }
}
