package com.studentsys.selection.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.studentsys.common.entity.Course;
import com.studentsys.common.entity.CourseSelection;
import com.studentsys.common.entity.Student;
import com.studentsys.common.result.Result;
import com.studentsys.selection.feign.CourseFeignClient;
import com.studentsys.selection.feign.StudentFeignClient;
import com.studentsys.selection.mapper.CourseSelectionMapper;
import com.studentsys.selection.service.CourseSelectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 选课Service实现
 */
@Service
@RequiredArgsConstructor
public class CourseSelectionServiceImpl extends ServiceImpl<CourseSelectionMapper, CourseSelection> implements CourseSelectionService {
    
    private final CourseFeignClient courseFeignClient;
    private final StudentFeignClient studentFeignClient;
    
    @Override
    public Page<CourseSelection> pageQuery(Integer pageNum, Integer pageSize, Long courseId, Long studentId, Integer status) {
        Page<CourseSelection> page = new Page<>(pageNum, pageSize);
        
        LambdaQueryWrapper<CourseSelection> wrapper = new LambdaQueryWrapper<>();
        
        if (courseId != null) {
            wrapper.eq(CourseSelection::getCourseId, courseId);
        }
        if (studentId != null) {
            wrapper.eq(CourseSelection::getStudentId, studentId);
        }
        if (status != null) {
            wrapper.eq(CourseSelection::getStatus, status);
        }
        
        wrapper.orderByDesc(CourseSelection::getSelectTime);
        
        Page<CourseSelection> result = baseMapper.selectPage(page, wrapper);
        
        // 填充课程和学生信息
        result.getRecords().forEach(this::fillInfo);
        
        return result;
    }
    
    @Override
    @Transactional
    public boolean selectCourse(Long studentId, Long courseId) {
        // 检查是否已选
        CourseSelection existing = lambdaQuery()
                .eq(CourseSelection::getStudentId, studentId)
                .eq(CourseSelection::getCourseId, courseId)
                .one();
        
        if (existing != null) {
            // 如果是退课状态，允许重选
            if ("dropped".equals(existing.getStatus())) {
                existing.setStatus("studying"); // 已选
                existing.setSelectTime(LocalDateTime.now());
                return updateById(existing);
            }
            return false; // 已选，不能重复选
        }
        
        // 检查课程容量
        Result<Course> courseResult = courseFeignClient.getById(courseId);
        if (courseResult.getCode() != 200 || courseResult.getData() == null) {
            return false;
        }
        
        Course course = courseResult.getData();
        long selectedCount = lambdaQuery()
                .eq(CourseSelection::getCourseId, courseId)
                .ne(CourseSelection::getStatus, "dropped") // 非退课状态
                .count();
        
        if (course.getMaxStudents() != null && selectedCount >= course.getMaxStudents()) {
            return false; // 课程已满
        }
        
        // 新增选课记录
        CourseSelection selection = new CourseSelection();
        selection.setStudentId(studentId);
        selection.setCourseId(courseId);
        selection.setStatus("studying"); // 已选
        selection.setSelectTime(LocalDateTime.now());
        
        return save(selection);
    }
    
    @Override
    @Transactional
    public boolean dropCourse(Long studentId, Long courseId) {
        CourseSelection existing = lambdaQuery()
                .eq(CourseSelection::getStudentId, studentId)
                .eq(CourseSelection::getCourseId, courseId)
                .in(CourseSelection::getStatus, "pending", "studying") // 可退状态
                .one();
        
        if (existing == null) {
            return false;
        }
        
        // 如果已有成绩，不允许退课
        if (existing.getScore() != null) {
            return false;
        }
        
        existing.setStatus("dropped"); // 退课
        return updateById(existing);
    }
    
    @Override
    @Transactional
    public boolean reselectCourse(Long studentId, Long courseId) {
        CourseSelection existing = lambdaQuery()
                .eq(CourseSelection::getStudentId, studentId)
                .eq(CourseSelection::getCourseId, courseId)
                .eq(CourseSelection::getStatus, "dropped") // 退课状态
                .one();
        
        if (existing == null) {
            return false;
        }
        
        // 检查课程容量
        Result<Course> courseResult = courseFeignClient.getById(courseId);
        if (courseResult.getCode() != 200 || courseResult.getData() == null) {
            return false;
        }
        
        Course course = courseResult.getData();
        long selectedCount = lambdaQuery()
                .eq(CourseSelection::getCourseId, courseId)
                .ne(CourseSelection::getStatus, "dropped") // 非退课状态
                .count();
        
        if (course.getMaxStudents() != null && selectedCount >= course.getMaxStudents()) {
            return false; // 课程已满
        }
        
        existing.setStatus("studying"); // 已选
        existing.setSelectTime(LocalDateTime.now());
        return updateById(existing);
    }
    
    @Override
    public List<CourseSelection> listByStudentId(Long studentId) {
        List<CourseSelection> list = lambdaQuery()
                .eq(CourseSelection::getStudentId, studentId)
                .ne(CourseSelection::getStatus, "dropped") // 非退课
                .orderByDesc(CourseSelection::getSelectTime)
                .list();
        list.forEach(this::fillInfo);
        return list;
    }
    
    @Override
    public List<CourseSelection> listByCourseId(Long courseId) {
        List<CourseSelection> list = lambdaQuery()
                .eq(CourseSelection::getCourseId, courseId)
                .ne(CourseSelection::getStatus, "dropped") // 非退课
                .orderByDesc(CourseSelection::getSelectTime)
                .list();
        list.forEach(this::fillInfo);
        return list;
    }
    
    @Override
    @Transactional
    public boolean enterScore(Long selectionId, Double score) {
        CourseSelection selection = getById(selectionId);
        if (selection == null || "dropped".equals(selection.getStatus())) {
            return false;
        }
        
        selection.setScore(BigDecimal.valueOf(score));
        return updateById(selection);
    }
    
    @Override
    @Transactional
    public boolean batchEnterScore(List<Long> selectionIds, List<Double> scores) {
        if (selectionIds.size() != scores.size()) {
            return false;
        }
        
        for (int i = 0; i < selectionIds.size(); i++) {
            CourseSelection selection = getById(selectionIds.get(i));
            if (selection != null && !"dropped".equals(selection.getStatus())) {
                selection.setScore(BigDecimal.valueOf(scores.get(i)));
                updateById(selection);
            }
        }
        return true;
    }
    
    @Override
    public List<CourseSelection> getStudentGrades(Long studentId) {
        List<CourseSelection> list = lambdaQuery()
                .eq(CourseSelection::getStudentId, studentId)
                .isNotNull(CourseSelection::getScore)
                .orderByDesc(CourseSelection::getUpdateTime)
                .list();
        list.forEach(this::fillInfo);
        return list;
    }
    
    @Override
    public boolean hasSelected(Long studentId, Long courseId) {
        return lambdaQuery()
                .eq(CourseSelection::getStudentId, studentId)
                .eq(CourseSelection::getCourseId, courseId)
                .ne(CourseSelection::getStatus, "dropped") // 非退课
                .count() > 0;
    }
    
    /**
     * 填充课程和学生信息
     */
    private void fillInfo(CourseSelection selection) {
        // 填充课程信息
        if (selection.getCourseId() != null) {
            try {
                Result<Course> courseResult = courseFeignClient.getById(selection.getCourseId());
                if (courseResult.getCode() == 200) {
                    selection.setCourse(courseResult.getData());
                }
            } catch (Exception e) {
                // 忽略异常
            }
        }
        // 填充学生信息
        if (selection.getStudentId() != null) {
            try {
                Result<Student> studentResult = studentFeignClient.getById(selection.getStudentId());
                if (studentResult.getCode() == 200) {
                    Student student = studentResult.getData();
                    if (student != null) {
                        student.setPassword(null);
                    }
                    selection.setStudent(student);
                }
            } catch (Exception e) {
                // 忽略异常
            }
        }
    }
}
