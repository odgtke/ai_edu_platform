package com.chinaunicom.edu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinaunicom.edu.entity.Resource;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 资源 Mapper 接口
 */
@Mapper
public interface ResourceMapper extends BaseMapper<Resource> {

    /**
     * 增加下载次数
     */
    @Update("UPDATE edu_resource SET download_count = download_count + 1 WHERE resource_id = #{resourceId}")
    int incrementDownloadCount(@Param("resourceId") Long resourceId);

    /**
     * 增加浏览次数
     */
    @Update("UPDATE edu_resource SET view_count = view_count + 1 WHERE resource_id = #{resourceId}")
    int incrementViewCount(@Param("resourceId") Long resourceId);

    /**
     * 根据课程ID查询资源列表
     */
    @Select("SELECT r.* FROM edu_resource r " +
            "INNER JOIN edu_course_resource cr ON r.resource_id = cr.resource_id " +
            "WHERE cr.course_id = #{courseId} AND r.status = 1 " +
            "ORDER BY cr.resource_order ASC")
    List<Resource> selectByCourseId(@Param("courseId") Long courseId);
}
