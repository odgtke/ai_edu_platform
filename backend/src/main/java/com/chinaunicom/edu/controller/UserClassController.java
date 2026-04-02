package com.chinaunicom.edu.controller;

import com.chinaunicom.edu.common.Result;
import com.chinaunicom.edu.entity.UserClass;
import com.chinaunicom.edu.service.UserClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户班级关联控制器
 */
@RestController
@RequestMapping("/user-classes")
public class UserClassController {

    private final UserClassService userClassService;

    @Autowired
    public UserClassController(UserClassService userClassService) {
        this.userClassService = userClassService;
    }

    /**
     * 为用户分配班级
     */
    @PostMapping("/assign")
    public Result<Boolean> assignClass(
            @RequestParam Long userId,
            @RequestParam Long classId,
            @RequestParam String userType) {
        
        boolean success = userClassService.assignClass(userId, classId, userType);
        return success ? Result.success(true) : Result.error("分配失败，可能已存在关联");
    }

    /**
     * 移除用户的班级
     */
    @PostMapping("/remove")
    public Result<Boolean> removeFromClass(
            @RequestParam Long userId,
            @RequestParam Long classId) {
        
        boolean success = userClassService.removeFromClass(userId, classId);
        return success ? Result.success(true) : Result.error("移除失败");
    }

    /**
     * 获取用户的班级列表
     */
    @GetMapping("/user/{userId}")
    public Result<List<UserClass>> listByUserId(@PathVariable Long userId) {
        List<UserClass> list = userClassService.listByUserId(userId);
        return Result.success(list);
    }

    /**
     * 获取班级的用户列表
     */
    @GetMapping("/class/{classId}")
    public Result<List<UserClass>> listByClassId(@PathVariable Long classId) {
        List<UserClass> list = userClassService.listByClassId(classId);
        return Result.success(list);
    }

    /**
     * 获取用户所在的所有班级ID
     */
    @GetMapping("/user/{userId}/class-ids")
    public Result<List<Long>> getClassIdsByUserId(@PathVariable Long userId) {
        List<Long> classIds = userClassService.getClassIdsByUserId(userId);
        return Result.success(classIds);
    }
}
