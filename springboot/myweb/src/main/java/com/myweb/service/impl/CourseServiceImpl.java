package com.myweb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.myweb.document.CourseDocument;
import com.myweb.dto.CourseQueryDTO;
import com.myweb.entity.Course;
import com.myweb.mapper.CourseMapper;
import com.myweb.service.CourseSearchService;
import com.myweb.service.CourseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.io.Serializable;
import java.util.List;

/**
 * 课程Service实现类
 */
@Slf4j
@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {
    
    @Autowired
    private CourseSearchService courseSearchService;
    
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
    
    /**
     * 保存课程（重写以同步ES）
     */
    @Override
    public boolean save(Course course) {
        // 1. 保存到 MySQL
        boolean result = super.save(course);
        
        // 2. 同步到 Elasticsearch
        if (result) {
            try {
                CourseDocument document = courseSearchService.convertToDocument(course);
                courseSearchService.indexCourse(document);
            } catch (Exception e) {
                // ES 索引失败不影响主流程
                log.error("课程索引失败", e);
            }
        }
        
        return result;
    }
    
    /**
     * 更新课程（重写以同步ES）
     */
    @Override
    public boolean updateById(Course course) {
        // 1. 更新 MySQL
        boolean result = super.updateById(course);
        
        // 2. 同步到 Elasticsearch
        if (result) {
            try {
                Course updatedCourse = this.getById(course.getId());
                CourseDocument document = courseSearchService.convertToDocument(updatedCourse);
                courseSearchService.indexCourse(document);
            } catch (Exception e) {
                // ES 索引失败不影响主流程
                log.error("课程索引更新失败", e);
            }
        }
        
        return result;
    }
    
    /**
     * 删除课程（重写以同步ES）
     */
    @Override
    public boolean removeById(Serializable id) {
        // 1. 从 MySQL 删除
        boolean result = super.removeById(id);
        
        // 2. 从 Elasticsearch 删除
        if (result) {
            try {
                courseSearchService.deleteCourse((Long) id);
            } catch (Exception e) {
                // ES 删除失败不影响主流程
                log.error("课程索引删除失败", e);
            }
        }
        
        return result;
    }
}
