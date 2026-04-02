package com.chinaunicom.edu.user.controller;

import com.chinaunicom.edu.common.Result;
import com.chinaunicom.edu.user.entity.User;
import com.chinaunicom.edu.user.service.UserService;
import com.chinaunicom.edu.user.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody User loginUser) {
        User user = userService.login(loginUser.getUsername(), loginUser.getPassword());

        // 生成 JWT Token
        String accessToken = jwtUtil.generateToken(user.getUsername(), String.valueOf(user.getUserId()));
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername(), String.valueOf(user.getUserId()));

        Map<String, Object> response = new HashMap<>();
        response.put("accessToken", accessToken);
        response.put("refreshToken", refreshToken);
        response.put("expiresIn", 86400); // 24小时，单位秒
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("userId", user.getUserId());
        userInfo.put("username", user.getUsername());
        userInfo.put("realName", user.getRealName());
        userInfo.put("userType", user.getUserType());
        response.put("userInfo", userInfo);

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

    /**
     * 刷新 Token
     */
    @PostMapping("/refresh")
    public Result<Map<String, Object>> refresh(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        if (refreshToken == null || refreshToken.isEmpty()) {
            return Result.error(400, "Refresh token is required");
        }

        // 验证 refresh token
        if (!jwtUtil.validateToken(refreshToken)) {
            return Result.error(401, "Invalid or expired refresh token");
        }

        // 从 token 中获取用户信息
        String username = jwtUtil.getUsernameFromToken(refreshToken);
        String userId = jwtUtil.parseToken(refreshToken).get("userId", String.class);

        // 生成新的 token
        String newAccessToken = jwtUtil.generateToken(username, userId);
        String newRefreshToken = jwtUtil.generateRefreshToken(username, userId);

        Map<String, Object> response = new HashMap<>();
        response.put("accessToken", newAccessToken);
        response.put("refreshToken", newRefreshToken);
        response.put("expiresIn", 86400);

        return Result.success(response);
    }
}
