package com.chinaunicom.edu.user.controller;

import com.chinaunicom.edu.common.Result;
import com.chinaunicom.edu.user.entity.UserClass;
import com.chinaunicom.edu.user.service.UserClassService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-classes")
public class UserClassController {

    private final UserClassService userClassService;

    public UserClassController(UserClassService userClassService) {
        this.userClassService = userClassService;
    }

    @GetMapping
    public Result<List<UserClass>> list() {
        return Result.success(userClassService.list());
    }

    @GetMapping("/user/{userId}")
    public Result<List<UserClass>> getByUserId(@PathVariable Long userId) {
        List<UserClass> userClasses = userClassService.lambdaQuery()
                .eq(UserClass::getUserId, userId)
                .list();
        return Result.success(userClasses);
    }

    @GetMapping("/class/{classId}")
    public Result<List<UserClass>> getByClassId(@PathVariable Long classId) {
        List<UserClass> userClasses = userClassService.lambdaQuery()
                .eq(UserClass::getClassId, classId)
                .list();
        return Result.success(userClasses);
    }

    @PostMapping
    public Result<Boolean> save(@RequestBody UserClass userClass) {
        return Result.success(userClassService.save(userClass));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(userClassService.removeById(id));
    }
}
