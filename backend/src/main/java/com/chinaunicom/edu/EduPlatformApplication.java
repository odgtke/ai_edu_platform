package com.chinaunicom.edu;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 智慧教育平台主应用程序类
 */
@SpringBootApplication(scanBasePackages = {"com.chinaunicom.edu", "com.chinaunicom.edu.agent"})
@MapperScan("com.chinaunicom.edu.mapper")
@EnableScheduling
@EnableCaching
public class EduPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(EduPlatformApplication.class, args);
    }
}