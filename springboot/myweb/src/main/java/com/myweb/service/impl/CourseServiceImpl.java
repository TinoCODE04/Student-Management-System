package com.myweb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.myweb.dto.CourseQueryDTO;
import com.myweb.entity.Course;
import com.myweb.mapper.CourseMapper;
import com.myweb.service.CourseService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.util.List;

/**
 * 课程Service实现类
 */
@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {
    
    @Override
    public List<Course> listByTeacherId(Long teacherId) {
        return baseMapper.selectByTeacherId(teacherId);
    }
    
    @Override
    public List<Course> listByCollegeId(Long collegeId) {
        return baseMapper.selectByCollegeId(collegeId);
    }
    
    @Override
    public Page<Course> pageQuery(CourseQueryDTO queryDTO) {
        Page<Course> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        
        // 课程名称模糊查询
        if (StringUtils.hasText(queryDTO.getCourseName())) {
            wrapper.like(Course::getCourseName, queryDTO.getCourseName());
        }
        
        // 教师ID查询
        if (queryDTO.getTeacherId() != null) {
            wrapper.eq(Course::getTeacherId, queryDTO.getTeacherId());
        }
        
        // 学院ID查询
        if (queryDTO.getCollegeId() != null) {
            wrapper.eq(Course::getCollegeId, queryDTO.getCollegeId());
        }
        
        // 按创建时间倒序
        wrapper.orderByDesc(Course::getCreateTime);
        
        return this.page(page, wrapper);
    }
}
