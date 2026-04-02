package com.chinaunicom.edu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinaunicom.edu.entity.AssignmentSubmission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 作业提交记录Mapper接口
 */
@Mapper
public interface AssignmentSubmissionMapper extends BaseMapper<AssignmentSubmission> {
    
    /**
     * 获取学生的作业提交统计
     */
    @Select("SELECT COUNT(*) FROM edu_student_assignment WHERE student_id = #{studentId} AND status > 0")
    int countSubmittedByStudent(@Param("studentId") Long studentId);
    
    /**
     * 获取学生需要完成的作业总数
     */
    @Select("SELECT COUNT(*) FROM edu_assignment a " +
            "INNER JOIN edu_learning_record lr ON a.course_id = lr.course_id " +
            "WHERE lr.student_id = #{studentId} AND a.status = 1")
    int countTotalAssignmentsByStudent(@Param("studentId") Long studentId);
}
