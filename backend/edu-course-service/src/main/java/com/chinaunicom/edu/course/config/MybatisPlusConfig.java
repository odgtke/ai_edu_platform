package com.chinaunicom.edu.course.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
@MapperScan("com.chinaunicom.edu.course.mapper")
public class MybatisPlusConfig {
    // MyBatis Plus配置
}
