package com.studentsys.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.studentsys.common.dto.CourseQueryDTO;
import com.studentsys.common.entity.Course;
import com.studentsys.course.mapper.CourseMapper;
import com.studentsys.course.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 课程Service实现
 */
@Service
@RequiredArgsConstructor
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {
    
    @Override
    public Page<Course> pageQuery(CourseQueryDTO queryDTO) {
        Page<Course> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        
        // 课程名模糊查询
        if (StringUtils.hasText(queryDTO.getCourseName())) {
            wrapper.like(Course::getCourseName, queryDTO.getCourseName());
        }
        // 课程编号
        if (StringUtils.hasText(queryDTO.getCourseNo())) {
            wrapper.like(Course::getCourseNo, queryDTO.getCourseNo());
        }
        // 教师
        if (queryDTO.getTeacherId() != null) {
            wrapper.eq(Course::getTeacherId, queryDTO.getTeacherId());
        }
        // 学院
        if (queryDTO.getCollegeId() != null) {
            wrapper.eq(Course::getCollegeId, queryDTO.getCollegeId());
        }
        // 学期
        if (StringUtils.hasText(queryDTO.getSemester())) {
            wrapper.eq(Course::getSemester, queryDTO.getSemester());
        }
        
        wrapper.orderByDesc(Course::getCreateTime);
        
        return baseMapper.selectPage(page, wrapper);
    }
    
    @Override
    public List<Course> listByTeacherId(Long teacherId) {
        return lambdaQuery().eq(Course::getTeacherId, teacherId).list();
    }
    
    @Override
    public long countByCollegeId(Long collegeId) {
        return lambdaQuery().eq(Course::getCollegeId, collegeId).count();
    }
}
