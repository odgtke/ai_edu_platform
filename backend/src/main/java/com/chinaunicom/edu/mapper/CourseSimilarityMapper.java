package com.chinaunicom.edu.mapper;

import com.chinaunicom.edu.entity.CourseSimilarity;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface CourseSimilarityMapper {
    
    @Insert("INSERT INTO course_similarities (course_id_1, course_id_2, similarity_score, similarity_type) " +
            "VALUES (#{course1Id}, #{course2Id}, #{similarityScore}, #{similarityType})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(CourseSimilarity similarity);
    
    @Select("SELECT * FROM course_similarities WHERE course_id_1 = #{courseId} OR course_id_2 = #{courseId} " +
            "ORDER BY similarity_score DESC LIMIT #{limit}")
    List<CourseSimilarity> findByCourseId(@Param("courseId") Long courseId, @Param("limit") Integer limit);
    
    @Select("SELECT * FROM course_similarities WHERE course_id_1 = #{courseId1} AND course_id_2 = #{courseId2}")
    CourseSimilarity findByCoursePair(@Param("courseId1") Long courseId1, @Param("courseId2") Long courseId2);
    
    @Select("SELECT cs.*, c1.course_name as course1_name, c2.course_name as course2_name " +
            "FROM course_similarities cs " +
            "JOIN courses c1 ON cs.course_id_1 = c1.id " +
            "JOIN courses c2 ON cs.course_id_2 = c2.id " +
            "WHERE cs.course_id_1 = #{courseId} OR cs.course_id_2 = #{courseId} " +
            "ORDER BY cs.similarity_score DESC LIMIT #{limit}")
    List<CourseSimilarity> findWithCourseNames(@Param("courseId") Long courseId, @Param("limit") Integer limit);
}