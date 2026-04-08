package com.chinaunicom.edu.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinaunicom.edu.course.entity.Recommendation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface RecommendationMapper extends BaseMapper<Recommendation> {
    
    /**
     * 查询用户的推荐列表
     */
    @Select("SELECT * FROM recommendations WHERE user_id = #{userId} AND expired_time > NOW() ORDER BY recommendation_score DESC")
    List<Recommendation> findByUserId(@Param("userId") Long userId);
    
    /**
     * 更新点击状态
     */
    @Update("UPDATE recommendations SET is_clicked = true WHERE id = #{id}")
    void updateClicked(@Param("id") Long id);
    
    /**
     * 更新报名状态
     */
    @Update("UPDATE recommendations SET is_enrolled = true WHERE id = #{id}")
    void updateEnrolled(@Param("id") Long id);
}
