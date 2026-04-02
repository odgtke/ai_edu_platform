package com.chinaunicom.edu.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chinaunicom.edu.common.Result;
import com.chinaunicom.edu.entity.Course;
import com.chinaunicom.edu.entity.User;
import com.chinaunicom.edu.service.CourseService;
import com.chinaunicom.edu.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final CourseService courseService;
    
    public UserController(UserService userService, CourseService courseService) {
        this.userService = userService;
        this.courseService = courseService;
    }

    /**
     * 分页查询用户
     */
    @GetMapping("/page")
    public Result<Page<User>> getPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String realName,
            @RequestParam(required = false) Integer userType,
            @RequestParam(required = false) Integer status) {
        
        Page<User> pageObj = new Page<>(page, size);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        
        // 添加查询条件
        if (username != null && !username.isEmpty()) {
            queryWrapper.like("username", username);
        }
        if (realName != null && !realName.isEmpty()) {
            queryWrapper.like("real_name", realName);
        }
        if (userType != null) {
            queryWrapper.eq("user_type", userType);
        }
        if (status != null) {
            queryWrapper.eq("status", status);
        }
        
        // 按创建时间降序排列
        queryWrapper.orderByDesc("create_time");
        
        Page<User> result = userService.page(pageObj, queryWrapper);
        return Result.success(result);
    }

    /**
     * 获取所有用户
     */
    @GetMapping
    public Result<List<User>> getAll() {
        List<User> list = userService.list();
        return Result.success(list);
    }

    /**
     * 根据ID获取用户
     */
    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable Long id) {
        User user = userService.getById(id);
        return Result.success(user);
    }

    /**
     * 创建用户
     */
    @PostMapping
    public Result<Boolean> create(@RequestBody User user) {
        boolean result = userService.save(user);
        return Result.success(result);
    }

    /**
     * 更新用户
     */
    @PutMapping
    public Result<Boolean> update(@RequestBody User user) {
        boolean result = userService.updateById(user);
        return Result.success(result);
    }

    /**
     * 获取用户总数
     */
    @GetMapping("/count")
    public Result<Long> getCount() {
        long count = userService.count();
        return Result.success(count);
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        // 检查用户是否有关联的课程（作为教师）
        QueryWrapper<Course> courseWrapper = new QueryWrapper<>();
        courseWrapper.eq("teacher_id", id);
        long courseCount = courseService.count(courseWrapper);
        
        if (courseCount > 0) {
            return Result.validateFailed("该用户是 " + courseCount + " 门课程的教师，请先转移或删除这些课程后再删除用户");
        }
        
        boolean result = userService.removeById(id);
        return Result.success(result);
    }
}