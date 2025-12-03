package com.myweb.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.myweb.dto.CourseQueryDTO;
import com.myweb.entity.Course;
import java.util.List;

/**
 * 课程Service接口
 */
public interface CourseService extends IService<Course> {
    
    /**
     * 根据教师ID查询课程列表
     */
    List<Course> listByTeacherId(Long teacherId);
    
    /**
     * 根据学院ID查询课程列表
     */
    List<Course> listByCollegeId(Long collegeId);
    
    /**
     * 分页查询课程
     */
    Page<Course> pageQuery(CourseQueryDTO queryDTO);
}
