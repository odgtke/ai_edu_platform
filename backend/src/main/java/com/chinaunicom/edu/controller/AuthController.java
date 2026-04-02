package com.chinaunicom.edu.controller;

import com.chinaunicom.edu.common.Result;
import com.chinaunicom.edu.config.UserDetailsImpl;
import com.chinaunicom.edu.entity.User;
import com.chinaunicom.edu.service.UserService;
import com.chinaunicom.edu.utils.JwtUtil;
import com.chinaunicom.edu.utils.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordUtil passwordUtil;

    @PostMapping("/login")
    public Result<Map<String, String>> login(@RequestBody User loginUser) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginUser.getUsername(),
                            loginUser.getPassword()
                    )
            );

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            String accessToken = jwtUtil.generateAccessToken(userDetails.getUsername(), 
                String.valueOf(userDetails.getUser().getUserId()));
            String refreshToken = jwtUtil.generateRefreshToken(userDetails.getUsername(), 
                String.valueOf(userDetails.getUser().getUserId()));

            Map<String, String> response = new HashMap<>();
            response.put("accessToken", accessToken);
            response.put("refreshToken", refreshToken);
            response.put("tokenType", "Bearer");
            response.put("expiresIn", String.valueOf(86400)); // 24小时
            response.put("username", userDetails.getUsername());
            response.put("userId", String.valueOf(userDetails.getUser().getUserId()));

            return Result.success(response);
        } catch (Exception e) {
            return Result.error("Invalid credentials");
        }
    }

    @PostMapping("/register")
    public Result<String> register(@RequestBody User user) {
        // 检查用户名是否已存在
        User existingUser = userService.lambdaQuery()
                .eq(User::getUsername, user.getUsername())
                .one();

        if (existingUser != null) {
            return Result.error("Username already exists");
        }

        // 验证并加密密码
        if (!passwordUtil.validatePasswordStrength(user.getPassword())) {
            return Result.error("密码不符合安全要求：至少8位，包含大小写字母、数字和特殊字符");
        }
        user.setPassword(passwordUtil.encodePassword(user.getPassword()));
        
        // 设置默认状态
        if (user.getStatus() == null) {
            user.setStatus(1); // 默认启用
        }

        boolean saved = userService.save(user);
        if (saved) {
            return Result.success("Registration successful");
        } else {
            return Result.error("Registration failed");
        }
    }
}