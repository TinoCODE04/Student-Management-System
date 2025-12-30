package com.studentsys.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.studentsys.common.entity.Course;
import org.apache.ibatis.annotations.Mapper;

/**
 * 课程Mapper
 */
@Mapper
public interface CourseMapper extends BaseMapper<Course> {
}
