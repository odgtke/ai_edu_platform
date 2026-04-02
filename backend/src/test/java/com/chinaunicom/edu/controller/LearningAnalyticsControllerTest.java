package com.chinaunicom.edu.controller;

import com.chinaunicom.edu.common.Result;
import com.chinaunicom.edu.service.KnowledgeMasteryAnalysisService;
import com.chinaunicom.edu.service.LearningPathAdjustmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 学习分析控制器测试类
 */
class LearningAnalyticsControllerTest {

    private MockMvc mockMvc;

    @Mock
    private LearningPathAdjustmentService pathAdjustmentService;

    @Mock
    private KnowledgeMasteryAnalysisService masteryAnalysisService;

    @InjectMocks
    private LearningAnalyticsController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void testGetPathAdjustmentSuggestions_Success() throws Exception {
        // Given
        Long userId = 1L;
        Map<String, Object> mockSuggestions = new HashMap<>();
        mockSuggestions.put("currentDifficulty", 1.0);
        mockSuggestions.put("recommendedDifficulty", 1.2);
        mockSuggestions.put("recommendations", new String[]{"建议增加难度"});

        when(pathAdjustmentService.getPathAdjustmentSuggestions(eq(userId), any()))
            .thenReturn(mockSuggestions);

        // When & Then
        mockMvc.perform(get("/api/analytics/path-adjustment/{userId}", userId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.currentDifficulty").value(1.0))
            .andExpect(jsonPath("$.data.recommendedDifficulty").value(1.2));
    }

    @Test
    void testAdjustLearningPath_Success() throws Exception {
        // Given
        Long userId = 1L;
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("targetLevel", "hard");

        Map<String, Object> mockAlternatives = new HashMap<>();
        mockAlternatives.put("options", new String[]{"选项1", "选项2"});

        when(pathAdjustmentService.generateAlternativePaths(eq(userId), any(), anyString()))
            .thenReturn(mockAlternatives);

        // When & Then
        mockMvc.perform(post("/api/analytics/adjust-path/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"targetLevel\":\"hard\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.options").isArray());
    }

    @Test
    void testGetKnowledgeOverview_Success() throws Exception {
        // Given
        Long userId = 1L;
        Map<String, Object> mockOverview = new HashMap<>();
        mockOverview.put("totalPoints", 45);
        mockOverview.put("avgMastery", 72.5);

        when(masteryAnalysisService.getUserKnowledgeOverview(userId))
            .thenReturn(mockOverview);

        // When & Then
        mockMvc.perform(get("/api/analytics/knowledge-overview/{userId}", userId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.totalPoints").value(45))
            .andExpect(jsonPath("$.data.avgMastery").value(72.5));
    }

    @Test
    void testGetCategoryMastery_Success() throws Exception {
        // Given
        Long userId = 1L;
        Map<String, Object> mockRadarData = new HashMap<>();
        mockRadarData.put("categories", new String[]{"编程基础", "数据结构"});
        mockRadarData.put("values", new double[]{85.0, 78.0});

        when(masteryAnalysisService.generateRadarChartData(userId))
            .thenReturn(mockRadarData);

        // When & Then
        mockMvc.perform(get("/api/analytics/category-mastery/{userId}", userId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.categories").isArray());
    }

    @Test
    void testUpdateKnowledgeMastery_Success() throws Exception {
        // Given
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("userId", "1");
        requestBody.put("knowledgePointId", "1");
        requestBody.put("score", "95.5");
        requestBody.put("studyDuration", "60");
        requestBody.put("learningMethod", "practice");

        // When & Then
        mockMvc.perform(post("/api/analytics/update-mastery")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"userId\":\"1\",\"knowledgePointId\":\"1\",\"score\":\"95.5\",\"studyDuration\":\"60\",\"learningMethod\":\"practice\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("知识点掌握度更新成功"));
    }

    @Test
    void testPredictLearningOutcome_Success() throws Exception {
        // Given
        Long userId = 1L;
        Map<String, Object> studyPlan = new HashMap<>();
        studyPlan.put("hoursPerWeek", 10);
        studyPlan.put("durationWeeks", 8);

        Map<String, Object> mockPrediction = new HashMap<>();
        mockPrediction.put("predictedMastery", 85.0);
        mockPrediction.put("confidence", 0.9);

        when(masteryAnalysisService.predictLearningOutcome(eq(userId), anyMap()))
            .thenReturn(mockPrediction);

        // When & Then
        mockMvc.perform(post("/api/analytics/predict-outcome/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"hoursPerWeek\":10,\"durationWeeks\":8}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.predictedMastery").value(85.0));
    }
}
