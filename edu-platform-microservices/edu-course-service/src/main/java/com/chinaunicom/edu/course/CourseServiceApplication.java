package com.chinaunicom.edu.course;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 课程服务启动类
 * 使用 Nacos 作为服务注册中心
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@MapperScan("com.chinaunicom.edu.course.mapper")
public class CourseServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CourseServiceApplication.class, args);
    }
}
