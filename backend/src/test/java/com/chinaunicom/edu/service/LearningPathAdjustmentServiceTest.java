package com.chinaunicom.edu.service;

import com.chinaunicom.edu.entity.LearningPath;
import com.chinaunicom.edu.mapper.LearningPathMapper;
import com.chinaunicom.edu.mapper.UserBehaviorMapper;
import com.chinaunicom.edu.service.impl.LearningPathAdjustmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 学习路径调整服务测试类
 */
class LearningPathAdjustmentServiceTest {

    @Mock
    private UserBehaviorMapper userBehaviorMapper;

    @Mock
    private LearningPathMapper learningPathMapper;

    @InjectMocks
    private LearningPathAdjustmentServiceImpl pathAdjustmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testEvaluatePerformance_NewUser() {
        // Given
        Long userId = 1L;
        when(userBehaviorMapper.findRecentByUserId(anyLong(), anyInt())).thenReturn(null);

        // When
        Map<String, Object> performance = pathAdjustmentService.evaluatePerformance(userId);

        // Then
        assertNotNull(performance);
        assertEquals("beginner", performance.get("experienceLevel"));
        assertEquals(50.0, performance.get("consistencyScore"));
        assertEquals(50.0, performance.get("engagementScore"));
        assertEquals(0.0, performance.get("progressRate"));
    }

    @Test
    void testCalculateOptimalDifficulty_HighPerformance() {
        // Given
        Map<String, Object> performance = new HashMap<>();
        performance.put("consistencyScore", 90.0);
        performance.put("engagementScore", 85.0);
        performance.put("progressRate", 0.9);
        performance.put("efficiencyIndex", 0.9);
        performance.put("experienceLevel", "intermediate");

        // When
        double difficulty = pathAdjustmentService.calculateOptimalDifficulty(performance);

        // Then
        assertTrue(difficulty > 1.0, "高表现用户应获得更高的难度系数");
        assertTrue(difficulty <= 2.0, "难度系数不应超过上限");
    }

    @Test
    void testCalculateOptimalDifficulty_LowPerformance() {
        // Given
        Map<String, Object> performance = new HashMap<>();
        performance.put("consistencyScore", 30.0);
        performance.put("engagementScore", 25.0);
        performance.put("progressRate", 0.2);
        performance.put("efficiencyIndex", 0.25);
        performance.put("experienceLevel", "beginner");

        // When
        double difficulty = pathAdjustmentService.calculateOptimalDifficulty(performance);

        // Then
        assertTrue(difficulty < 1.0, "低表现用户应获得较低的难度系数");
        assertTrue(difficulty >= 0.5, "难度系数不应低于下限");
    }

    @Test
    void testAdjustPathDifficulty_NoAdjustmentNeeded() {
        // Given
        Long userId = 1L;
        LearningPath path = new LearningPath();
        path.setId(1L);
        path.setUserId(userId);
        path.setDifficultyFactor(BigDecimal.valueOf(1.0));

        // 模拟用户表现刚好适中，不需要调整
        when(userBehaviorMapper.findRecentByUserId(anyLong(), anyInt())).thenReturn(null);

        // When
        LearningPath result = pathAdjustmentService.adjustPathDifficulty(userId, path);

        // Then
        assertEquals(BigDecimal.valueOf(1.0), result.getDifficultyFactor());
        verify(learningPathMapper, never()).updateById(any());
    }

    @Test
    void testGenerateAlternativePaths() {
        // Given
        Long userId = 1L;
        LearningPath currentPath = new LearningPath();
        currentPath.setEstimatedHours(100);
        String difficultyLevel = "normal";

        // When
        Map<String, Object> alternatives = pathAdjustmentService.generateAlternativePaths(
            userId, currentPath, difficultyLevel);

        // Then
        assertNotNull(alternatives);
        assertTrue(alternatives.containsKey("options"));
        assertTrue(alternatives.containsKey("recommendation"));
    }

    @Test
    void testGetPathAdjustmentSuggestions() {
        // Given
        Long userId = 1L;
        LearningPath learningPath = new LearningPath();

        // When
        Map<String, Object> suggestions = pathAdjustmentService.getPathAdjustmentSuggestions(
            userId, learningPath);

        // Then
        assertNotNull(suggestions);
        assertTrue(suggestions.containsKey("currentDifficulty"));
        assertTrue(suggestions.containsKey("recommendedDifficulty"));
        assertTrue(suggestions.containsKey("performanceMetrics"));
        assertTrue(suggestions.containsKey("recommendations"));
    }
}
