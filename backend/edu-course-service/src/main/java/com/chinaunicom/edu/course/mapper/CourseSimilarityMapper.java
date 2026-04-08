package com.chinaunicom.edu.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinaunicom.edu.course.entity.CourseSimilarity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CourseSimilarityMapper extends BaseMapper<CourseSimilarity> {
    
    /**
     * 查询课程的相似课程
     */
    @Select("SELECT * FROM course_similarities WHERE course_id_1 = #{courseId} ORDER BY similarity_score DESC")
    List<CourseSimilarity> findByCourseId(@Param("courseId") Long courseId);
    
    /**
     * 查询两个课程的相似度
     */
    @Select("SELECT * FROM course_similarities WHERE (course_id_1 = #{id1} AND course_id_2 = #{id2}) OR (course_id_1 = #{id2} AND course_id_2 = #{id1})")
    CourseSimilarity findByCoursePair(@Param("id1") Long id1, @Param("id2") Long id2);
}
