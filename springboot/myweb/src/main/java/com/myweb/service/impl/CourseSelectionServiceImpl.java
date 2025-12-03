package com.myweb.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.myweb.entity.Course;
import com.myweb.entity.CourseSelection;
import com.myweb.mapper.CourseMapper;
import com.myweb.mapper.CourseSelectionMapper;
import com.myweb.service.CourseSelectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 选课记录Service实现类
 */
@Service
public class CourseSelectionServiceImpl extends ServiceImpl<CourseSelectionMapper, CourseSelection> implements CourseSelectionService {
    
    @Autowired
    private CourseMapper courseMapper;
    
    @Override
    public List<CourseSelection> listByStudentId(Long studentId) {
        return baseMapper.selectByStudentId(studentId);
    }
    
    @Override
    public List<CourseSelection> listByCourseId(Long courseId) {
        return baseMapper.selectByCourseId(courseId);
    }
    
    @Override
    @Transactional
    public boolean selectCourse(Long studentId, Long courseId) {
        // 检查是否已选
        CourseSelection existing = baseMapper.selectByStudentAndCourse(studentId, courseId);
        if (existing != null) {
            return false;
        }
        
        // 检查课程是否已满
        Course course = courseMapper.selectById(courseId);
        if (course == null || course.getSelectedCount() >= course.getMaxStudents()) {
            return false;
        }
        
        // 创建选课记录
        CourseSelection selection = new CourseSelection();
        selection.setStudentId(studentId);
        selection.setCourseId(courseId);
        selection.setStatus("pending");  // 新选课默认为待开课状态（可退课）
        selection.setSelectTime(LocalDateTime.now());
        
        int result = baseMapper.insert(selection);
        
        // 更新已选人数
        if (result > 0) {
            course.setSelectedCount(course.getSelectedCount() + 1);
            courseMapper.updateById(course);
        }
        
        return result > 0;
    }
    
    @Override
    @Transactional
    public boolean dropCourse(Long studentId, Long courseId) {
        CourseSelection selection = baseMapper.selectByStudentAndCourse(studentId, courseId);
        // 只有 pending(待开课) 状态才能退课，studying(学习中) 不能退
        if (selection == null || !"pending".equals(selection.getStatus())) {
            return false;
        }
        
        // 更新状态为已退选
        selection.setStatus("dropped");
        int result = baseMapper.updateById(selection);
        
        // 更新已选人数
        if (result > 0) {
            Course course = courseMapper.selectById(courseId);
            if (course != null && course.getSelectedCount() > 0) {
                course.setSelectedCount(course.getSelectedCount() - 1);
                courseMapper.updateById(course);
            }
        }
        
        return result > 0;
    }
    
    @Override
    @Transactional
    public boolean reselectCourse(Long studentId, Long courseId) {
        // 查找已退选的记录
        CourseSelection selection = baseMapper.selectByStudentAndCourse(studentId, courseId);
        if (selection == null || !"dropped".equals(selection.getStatus())) {
            return false;
        }
        
        // 检查课程是否已满
        Course course = courseMapper.selectById(courseId);
        if (course == null || course.getSelectedCount() >= course.getMaxStudents()) {
            return false;
        }
        
        // 更新状态为待开课
        selection.setStatus("pending");
        selection.setSelectTime(LocalDateTime.now());
        int result = baseMapper.updateById(selection);
        
        // 更新已选人数
        if (result > 0) {
            course.setSelectedCount(course.getSelectedCount() + 1);
            courseMapper.updateById(course);
        }
        
        return result > 0;
    }
    
    @Override
    public boolean updateScore(Long studentId, Long courseId, Double score) {
        CourseSelection selection = baseMapper.selectByStudentAndCourse(studentId, courseId);
        if (selection == null) {
            return false;
        }
        
        selection.setScore(BigDecimal.valueOf(score));
        selection.setStatus("completed");
        
        return baseMapper.updateById(selection) > 0;
    }
}
