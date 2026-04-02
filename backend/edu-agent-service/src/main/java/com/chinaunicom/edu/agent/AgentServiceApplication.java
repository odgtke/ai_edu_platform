package com.chinaunicom.edu.agent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 智能体服务启动类
 * 使用 Nacos 作为服务注册中心
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class AgentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgentServiceApplication.class, args);
    }
}
