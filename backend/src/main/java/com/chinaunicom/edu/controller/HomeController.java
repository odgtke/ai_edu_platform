package com.chinaunicom.edu.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 主页控制器
 * 处理根路径请求，返回主页视图
 */
@Controller
public class HomeController {

    /**
     * 处理根路径请求，返回主页
     */
    @RequestMapping("/")
    public String home() {
        return "forward:/index.html";  // 转发到静态index.html页面
    }

    /**
     * 处理主页路径请求
     */
    @RequestMapping("/home")
    public String homePage() {
        return "forward:/index.html";  // 转发到静态index.html页面
    }
}