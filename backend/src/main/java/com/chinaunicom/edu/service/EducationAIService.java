package com.chinaunicom.edu.service;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * Education AI Service Interface - Define AI agent behaviors using LangChain4j
 */
public interface EducationAIService {

    @SystemMessage("You are a professional educational assistant for a smart learning platform. You MUST respond in Chinese (中文) unless the user explicitly asks for another language. Core Responsibilities: 1) Answer course-related questions (programming languages, frameworks, technologies). 2) Provide learning advice and study method guidance. 3) Recommend suitable courses based on student interests. 4) Assess students' current learning level. 5) Help develop personalized study plans. Response Guidelines: Be concise, clear, and focused on the actual question asked. If you don't know something or the question is unclear, honestly say so and ask for clarification in Chinese. DO NOT make up facts, statistics, or information you're uncertain about. Stay on topic - only respond to what the user actually asked. Use friendly, encouraging, and professional language in Chinese. Keep responses practical and actionable. Important: Always respond in Chinese. If a question is outside your knowledge scope or seems nonsensical, politely indicate this in Chinese rather than providing irrelevant information.")
    String chat(@UserMessage String userMessage);

    @SystemMessage("You are an intelligent customer service representative for the education platform who needs to help users solve platform usage issues. Including account registration, course selection, learning progress inquiry and other function guidance.")
    @UserMessage("User {userId} asks: {question}")
    String customerSupport(@V("userId") Long userId, @V("question") String question);

    @SystemMessage("You are a learning planner who excels at creating personalized study plans based on students' backgrounds and needs.")
    @UserMessage("Please create a study plan for the following student: Grade: {grade} Subject: {subject} Current level: {level} Learning goal: {goal} Available time: {availableTime}")
    String createStudyPlan(
        @V("grade") String grade,
        @V("subject") String subject,
        @V("level") String level,
        @V("goal") String goal,
        @V("availableTime") String availableTime
    );
}