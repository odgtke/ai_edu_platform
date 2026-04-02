package com.chinaunicom.edu.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chinaunicom.edu.entity.LearningRecord;
import com.chinaunicom.edu.mapper.LearningRecordMapper;
import com.chinaunicom.edu.service.impl.LearningRecordServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LearningRecordServiceTest {

    @Mock
    private LearningRecordMapper learningRecordMapper;

    @InjectMocks
    private LearningRecordServiceImpl learningRecordService;

    @Test
    void testSaveLearningRecord() {
        // Given
        LearningRecord record = createLearningRecord(1L, 100L, 1L, 30);
        when(learningRecordMapper.insert(record)).thenReturn(1);

        // When
        boolean result = learningRecordService.save(record);

        // Then
        assertTrue(result);
        verify(learningRecordMapper).insert(record);
    }

    @Test
    void testUpdateLearningRecord() {
        // Given
        LearningRecord record = createLearningRecord(1L, 100L, 1L, 60);
        when(learningRecordMapper.updateById(record)).thenReturn(1);

        // When
        boolean result = learningRecordService.updateById(record);

        // Then
        assertTrue(result);
        verify(learningRecordMapper).updateById(record);
    }

    @Test
    void testGetStudentLearningRecords() {
        // Given
        Long studentId = 100L;
        List<LearningRecord> mockRecords = Arrays.asList(
            createLearningRecord(1L, studentId, 1L, 30),
            createLearningRecord(2L, studentId, 2L, 45)
        );
        
        LambdaQueryWrapper<LearningRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LearningRecord::getStudentId, studentId);
        when(learningRecordMapper.selectList(wrapper)).thenReturn(mockRecords);

        // When
        List<LearningRecord> result = learningRecordService.lambdaQuery()
                .eq(LearningRecord::getStudentId, studentId)
                .orderByDesc(LearningRecord::getLastStudyTime)
                .list();

        // Then
        assertEquals(2, result.size());
        verify(learningRecordMapper).selectList(any(LambdaQueryWrapper.class));
    }

    @Test
    void testGetCourseLearningRecords() {
        // Given
        Long courseId = 1L;
        List<LearningRecord> mockRecords = Arrays.asList(
            createLearningRecord(1L, 100L, courseId, 30),
            createLearningRecord(2L, 101L, courseId, 45)
        );
        
        LambdaQueryWrapper<LearningRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LearningRecord::getCourseId, courseId);
        when(learningRecordMapper.selectList(wrapper)).thenReturn(mockRecords);

        // When
        List<LearningRecord> result = learningRecordService.lambdaQuery()
                .eq(LearningRecord::getCourseId, courseId)
                .orderByDesc(LearningRecord::getLastStudyTime)
                .list();

        // Then
        assertEquals(2, result.size());
        verify(learningRecordMapper).selectList(any(LambdaQueryWrapper.class));
    }

    @Test
    void testGetStudentCourseProgress() {
        // Given
        Long studentId = 100L;
        Long courseId = 1L;
        LearningRecord mockRecord = createLearningRecord(1L, studentId, courseId, 75);
        mockRecord.setProgress(75);
        mockRecord.setIsCompleted(false);
        
        LambdaQueryWrapper<LearningRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LearningRecord::getStudentId, studentId)
               .eq(LearningRecord::getCourseId, courseId);
        when(learningRecordMapper.selectOne(wrapper)).thenReturn(mockRecord);

        // When
        LearningRecord result = learningRecordService.lambdaQuery()
                .eq(LearningRecord::getStudentId, studentId)
                .eq(LearningRecord::getCourseId, courseId)
                .one();

        // Then
        assertNotNull(result);
        assertEquals(studentId, result.getStudentId());
        assertEquals(courseId, result.getCourseId());
        assertEquals(75, result.getProgress().intValue());
        verify(learningRecordMapper).selectOne(any(LambdaQueryWrapper.class));
    }

    @Test
    void testCalculateStudentStatistics() {
        // Given
        Long studentId = 100L;
        List<LearningRecord> mockRecords = Arrays.asList(
            createLearningRecord(1L, studentId, 1L, 30),
            createLearningRecord(2L, studentId, 2L, 45),
            createLearningRecord(3L, studentId, 3L, 60)
        );
        
        // 设置其中一条记录为已完成
        mockRecords.get(2).setIsCompleted(true);
        
        LambdaQueryWrapper<LearningRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LearningRecord::getStudentId, studentId);
        when(learningRecordMapper.selectList(wrapper)).thenReturn(mockRecords);

        // When
        List<LearningRecord> records = learningRecordService.lambdaQuery()
                .eq(LearningRecord::getStudentId, studentId)
                .list();

        // 计算统计数据
        int totalDuration = records.stream()
                .mapToInt(LearningRecord::getStudyDuration)
                .sum();
        
        long completedCourses = records.stream()
                .filter(LearningRecord::getIsCompleted)
                .count();

        // Then
        assertEquals(3, records.size());
        assertEquals(135, totalDuration); // 30 + 45 + 60
        assertEquals(1, completedCourses);
        verify(learningRecordMapper).selectList(any(LambdaQueryWrapper.class));
    }

    @Test
    void testSaveOrUpdateRecord() {
        // Given
        LearningRecord record = createLearningRecord(1L, 100L, 1L, 90);
        record.setIsCompleted(true);
        when(learningRecordMapper.updateById(record)).thenReturn(1);

        // When
        boolean result = learningRecordService.saveOrUpdate(record);

        // Then
        assertTrue(result);
        verify(learningRecordMapper).updateById(record);
    }

    private LearningRecord createLearningRecord(Long recordId, Long studentId, Long courseId, Integer duration) {
        LearningRecord record = new LearningRecord();
        record.setRecordId(recordId);
        record.setStudentId(studentId);
        record.setCourseId(courseId);
        record.setLessonId(1L);
        record.setStudyDuration(duration);
        record.setProgress(duration); // 简化处理，假设进度等于学习时长
        record.setIsCompleted(duration >= 60); // 60分钟以上算完成
        record.setLastStudyTime(LocalDateTime.now());
        return record;
    }
}