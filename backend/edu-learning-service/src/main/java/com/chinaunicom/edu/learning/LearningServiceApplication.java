package com.chinaunicom.edu.learning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 学习中心服务启动类
 * 使用 Nacos 作为服务注册中心
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class LearningServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LearningServiceApplication.class, args);
    }
}
