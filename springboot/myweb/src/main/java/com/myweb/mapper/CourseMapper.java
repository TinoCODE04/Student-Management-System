package com.myweb.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.myweb.entity.Course;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

/**
 * 课程Mapper接口
 */
@Mapper
public interface CourseMapper extends BaseMapper<Course> {
    
    /**
     * 根据教师ID查询课程列表
     */
    @Select("SELECT * FROM course WHERE teacher_id = #{teacherId}")
    List<Course> selectByTeacherId(Long teacherId);
    
    /**
     * 根据学院ID查询课程列表
     */
    @Select("SELECT * FROM course WHERE college_id = #{collegeId}")
    List<Course> selectByCollegeId(Long collegeId);
}
