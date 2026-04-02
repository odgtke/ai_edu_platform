package com.chinaunicom.edu.user.controller;

import com.chinaunicom.edu.common.Result;
import com.chinaunicom.edu.user.entity.User;
import com.chinaunicom.edu.user.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody User loginUser) {
        User user = userService.login(loginUser.getUsername(), loginUser.getPassword());
        if (user == null) {
            return Result.error(401, "Invalid credentials");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("userId", user.getUserId());
        response.put("username", user.getUsername());
        response.put("realName", user.getRealName());
        response.put("userType", user.getUserType());

        return Result.success(response);
    }

    @PostMapping("/register")
    public Result<String> register(@RequestBody User user) {
        User existingUser = userService.lambdaQuery()
                .eq(User::getUsername, user.getUsername())
                .one();

        if (existingUser != null) {
            return Result.error(400, "Username already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getStatus() == null) {
            user.setStatus(1);
        }

        boolean saved = userService.save(user);
        if (saved) {
            return Result.success("Registration successful");
        } else {
            return Result.error(500, "Registration failed");
        }
    }
}
