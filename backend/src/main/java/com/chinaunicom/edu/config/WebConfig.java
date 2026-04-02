package com.chinaunicom.edu.config;

import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.boot.web.server.ErrorPageRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

/**
 * Web配置类 - 用于配置静态资源映射和前端History模式支持
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置静态资源映射
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(3600)
                .resourceChain(true)
                .addResolver(new PathResourceResolver());
    }
    
    /**
     * 配置错误页面，支持前端History模式
     * 当访问不存在的路径时，返回index.html让前端路由处理
     */
    @Bean
    public ErrorPageRegistrar errorPageRegistrar() {
        return new ErrorPageRegistrar() {
            @Override
            public void registerErrorPages(ErrorPageRegistry registry) {
                // 404错误时返回index.html，让前端路由处理
                registry.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/index.html"));
            }
        };
    }
}