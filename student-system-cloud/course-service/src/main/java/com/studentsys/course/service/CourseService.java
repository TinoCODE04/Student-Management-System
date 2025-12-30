package com.studentsys.course.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.studentsys.common.dto.CourseQueryDTO;
import com.studentsys.common.entity.Course;

import java.util.List;

/**
 * 课程Service接口
 */
public interface CourseService extends IService<Course> {
    
    /**
     * 分页查询课程
     */
    Page<Course> pageQuery(CourseQueryDTO queryDTO);
    
    /**
     * 根据教师ID查询课程
     */
    List<Course> listByTeacherId(Long teacherId);
    
    /**
     * 根据学院ID统计课程数量
     */
    long countByCollegeId(Long collegeId);
}
