package com.chinaunicom.edu.agent.controller;

import com.chinaunicom.edu.common.Result;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/agent")
public class LangChainAgentController {

    @PostMapping("/chat")
    public Result<Map<String, Object>> chat(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        Map<String, Object> response = new HashMap<>();
        response.put("reply", "This is a mock response for: " + message);
        response.put("type", "text");
        return Result.success(response);
    }

    @GetMapping("/health")
    public Result<String> health() {
        return Result.success("Agent service is running");
    }
}
