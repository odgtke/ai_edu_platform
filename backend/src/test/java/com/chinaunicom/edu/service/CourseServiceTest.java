package com.chinaunicom.edu.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chinaunicom.edu.entity.Course;
import com.chinaunicom.edu.mapper.CourseMapper;
import com.chinaunicom.edu.service.impl.CourseServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseMapper courseMapper;

    @InjectMocks
    private CourseServiceImpl courseService;

    @Test
    void testGetAllCourses() {
        // Given
        List<Course> mockCourses = Arrays.asList(
            createCourse(1L, "数学", "MATH001"),
            createCourse(2L, "语文", "CHINESE001")
        );
        when(courseMapper.selectList(null)).thenReturn(mockCourses);

        // When
        List<Course> result = courseService.list();

        // Then
        assertEquals(2, result.size());
        assertEquals("数学", result.get(0).getCourseName());
        assertEquals("语文", result.get(1).getCourseName());
        verify(courseMapper).selectList(null);
    }

    @Test
    void testCreateCourse() {
        // Given
        Course course = createCourse(1L, "英语", "ENGLISH001");
        when(courseMapper.insert(course)).thenReturn(1);

        // When
        boolean result = courseService.save(course);

        // Then
        assertTrue(result);
        verify(courseMapper).insert(course);
    }

    @Test
    void testUpdateCourse() {
        // Given
        Course course = createCourse(1L, "物理", "PHYSICS001");
        when(courseMapper.updateById(course)).thenReturn(1);

        // When
        boolean result = courseService.updateById(course);

        // Then
        assertTrue(result);
        verify(courseMapper).updateById(course);
    }

    @Test
    void testDeleteCourse() {
        // Given
        Long courseId = 1L;
        when(courseMapper.deleteById(courseId)).thenReturn(1);

        // When
        boolean result = courseService.removeById(courseId);

        // Then
        assertTrue(result);
        verify(courseMapper).deleteById(courseId);
    }

    @Test
    void testGetCourseById() {
        // Given
        Long courseId = 1L;
        Course mockCourse = createCourse(courseId, "化学", "CHEMISTRY001");
        when(courseMapper.selectById(courseId)).thenReturn(mockCourse);

        // When
        Course result = courseService.getById(courseId);

        // Then
        assertNotNull(result);
        assertEquals(courseId, result.getCourseId());
        assertEquals("化学", result.getCourseName());
        verify(courseMapper).selectById(courseId);
    }

    @Test
    void testGetCoursesByTeacherId() {
        // Given
        Long teacherId = 100L;
        List<Course> mockCourses = Arrays.asList(
            createCourse(1L, "数学", "MATH001"),
            createCourse(2L, "物理", "PHYSICS001")
        );
        
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Course::getTeacherId, teacherId);
        when(courseMapper.selectList(wrapper)).thenReturn(mockCourses);

        // When
        List<Course> result = courseService.lambdaQuery()
                .eq(Course::getTeacherId, teacherId)
                .list();

        // Then
        assertEquals(2, result.size());
        verify(courseMapper).selectList(any(LambdaQueryWrapper.class));
    }

    @Test
    void testGetCoursesByGradeLevel() {
        // Given
        Integer gradeLevel = 9;
        List<Course> mockCourses = Arrays.asList(
            createCourse(1L, "数学", "MATH001"),
            createCourse(3L, "英语", "ENGLISH001")
        );
        
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Course::getGradeLevel, gradeLevel);
        when(courseMapper.selectList(wrapper)).thenReturn(mockCourses);

        // When
        List<Course> result = courseService.lambdaQuery()
                .eq(Course::getGradeLevel, gradeLevel)
                .list();

        // Then
        assertEquals(2, result.size());
        verify(courseMapper).selectList(any(LambdaQueryWrapper.class));
    }

    private Course createCourse(Long id, String name, String code) {
        Course course = new Course();
        course.setCourseId(id);
        course.setCourseName(name);
        course.setCourseCode(code);
        course.setTeacherId(100L);
        course.setGradeLevel(9);
        course.setSubjectId(1L);
        course.setStatus(1);
        course.setCredit(2.0);
        return course;
    }
}