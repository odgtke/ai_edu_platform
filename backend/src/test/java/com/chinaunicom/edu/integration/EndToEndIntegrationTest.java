package com.chinaunicom.edu.integration;

import com.chinaunicom.edu.EduPlatformApplication;
import com.chinaunicom.edu.common.Result;
import com.chinaunicom.edu.entity.Course;
import com.chinaunicom.edu.entity.LearningRecord;
import com.chinaunicom.edu.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 智慧教育平台端到端集成测试
 */
@SpringBootTest(classes = EduPlatformApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class EndToEndIntegrationTest {

    @LocalServerPort
    private int port;

    private TestRestTemplate restTemplate;
    private String baseUrl;

    @BeforeEach
    void setUp() {
        restTemplate = new TestRestTemplate();
        baseUrl = "http://localhost:" + port + "/api";
    }

    @Test
    void testCompleteUserFlow() {
        // 1. 创建学生用户
        User student = new User();
        student.setUsername("student001");
        student.setPassword("password123");
        student.setRealName("张三");
        student.setPhone("13800138001");
        student.setEmail("student@example.com");
        student.setUserType(1); // 学生
        student.setStatus(1);   // 启用

        ResponseEntity<Result> createUserResponse = restTemplate.postForEntity(
                baseUrl + "/users", student, Result.class);
        
        assertEquals(HttpStatus.OK, createUserResponse.getStatusCode());
        assertTrue((Boolean) createUserResponse.getBody().getData());

        // 2. 创建教师用户
        User teacher = new User();
        teacher.setUsername("teacher001");
        teacher.setPassword("password123");
        teacher.setRealName("李老师");
        teacher.setPhone("13800138002");
        teacher.setEmail("teacher@example.com");
        teacher.setUserType(2); // 教师
        teacher.setStatus(1);   // 启用

        restTemplate.postForEntity(baseUrl + "/users", teacher, Result.class);

        // 3. 教师创建课程
        Course course = new Course();
        course.setCourseName("高等数学");
        course.setCourseCode("MATH101");
        course.setDescription("大学一年级高等数学课程");
        course.setTeacherId(2L); // 假设教师ID为2
        course.setGradeLevel(1);
        course.setSubjectId(1L);
        course.setCredit(4.0);
        course.setStatus(1);

        ResponseEntity<Result> createCourseResponse = restTemplate.postForEntity(
                baseUrl + "/courses", course, Result.class);
        
        assertEquals(HttpStatus.OK, createCourseResponse.getStatusCode());
        assertTrue((Boolean) createCourseResponse.getBody().getData());

        // 4. 学生开始学习课程
        LearningRecord record = new LearningRecord();
        record.setStudentId(1L); // 学生ID
        record.setCourseId(1L);  // 课程ID
        record.setLessonId(1L);
        record.setStudyDuration(45);
        record.setProgress(75);
        record.setIsCompleted(false);
        record.setLastStudyTime(java.time.LocalDateTime.now());

        ResponseEntity<Result> saveRecordResponse = restTemplate.postForEntity(
                baseUrl + "/learning/record", record, Result.class);
        
        assertEquals(HttpStatus.OK, saveRecordResponse.getStatusCode());
        assertTrue((Boolean) saveRecordResponse.getBody().getData());

        // 5. 查询学生学习记录
        ResponseEntity<Result> getRecordsResponse = restTemplate.getForEntity(
                baseUrl + "/learning/records/student/1", Result.class);
        
        assertEquals(HttpStatus.OK, getRecordsResponse.getStatusCode());
        assertNotNull(getRecordsResponse.getBody().getData());
        
        // 6. 查询学生学习统计
        ResponseEntity<Result> getStatsResponse = restTemplate.getForEntity(
                baseUrl + "/learning/statistics/student/1", Result.class);
        
        assertEquals(HttpStatus.OK, getStatsResponse.getStatusCode());
        assertNotNull(getStatsResponse.getBody().getData());
    }

    @Test
    void testCourseManagementFlow() {
        // 1. 获取所有课程
        ResponseEntity<Result> getAllCoursesResponse = restTemplate.getForEntity(
                baseUrl + "/courses", Result.class);
        
        assertEquals(HttpStatus.OK, getAllCoursesResponse.getStatusCode());
        
        // 2. 根据教师ID查询课程
        ResponseEntity<Result> getTeacherCoursesResponse = restTemplate.getForEntity(
                baseUrl + "/courses/teacher/2", Result.class);
        
        assertEquals(HttpStatus.OK, getTeacherCoursesResponse.getStatusCode());
        
        // 3. 根据年级查询课程
        ResponseEntity<Result> getGradeCoursesResponse = restTemplate.getForEntity(
                baseUrl + "/courses/grade/1", Result.class);
        
        assertEquals(HttpStatus.OK, getGradeCoursesResponse.getStatusCode());
    }

    @Test
    void testUserManagementFlow() {
        // 1. 获取所有用户
        ResponseEntity<Result> getAllUsersResponse = restTemplate.getForEntity(
                baseUrl + "/users", Result.class);
        
        assertEquals(HttpStatus.OK, getAllUsersResponse.getStatusCode());
        
        // 2. 根据ID获取特定用户
        ResponseEntity<Result> getUserResponse = restTemplate.getForEntity(
                baseUrl + "/users/1", Result.class);
        
        assertEquals(HttpStatus.OK, getUserResponse.getStatusCode());
        assertNotNull(getUserResponse.getBody().getData());
    }

    @Test
    void testDataConsistency() {
        // 1. 创建用户
        User user = new User();
        user.setUsername("testuser_consistency");
        user.setPassword("password123");
        user.setRealName("测试用户");
        user.setUserType(1);
        user.setStatus(1);

        ResponseEntity<Result> createUserResponse = restTemplate.postForEntity(
                baseUrl + "/users", user, Result.class);
        
        assertTrue((Boolean) createUserResponse.getBody().getData());

        // 2. 验证用户可以通过ID查询到
        ResponseEntity<Result> getUserResponse = restTemplate.getForEntity(
                baseUrl + "/users/3", Result.class); // 假设这是新创建的用户
        
        assertEquals(HttpStatus.OK, getUserResponse.getStatusCode());
        User retrievedUser = (User) getUserResponse.getBody().getData();
        assertEquals("testuser_consistency", retrievedUser.getUsername());
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}