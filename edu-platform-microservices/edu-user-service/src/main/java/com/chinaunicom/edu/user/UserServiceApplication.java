package com.chinaunicom.edu.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 用户服务启动类
 * 使用 Nacos 作为服务注册中心
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@MapperScan("com.chinaunicom.edu.user.mapper")
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
