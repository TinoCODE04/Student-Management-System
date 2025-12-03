package com.myweb.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.myweb.entity.CourseSelection;
import java.util.List;

/**
 * 选课记录Service接口
 */
public interface CourseSelectionService extends IService<CourseSelection> {
    
    /**
     * 根据学生ID查询选课记录
     */
    List<CourseSelection> listByStudentId(Long studentId);
    
    /**
     * 根据课程ID查询选课记录
     */
    List<CourseSelection> listByCourseId(Long courseId);
    
    /**
     * 学生选课
     */
    boolean selectCourse(Long studentId, Long courseId);
    
    /**
     * 学生退课
     */
    boolean dropCourse(Long studentId, Long courseId);
    
    /**
     * 学生重新选课（退选后重选）
     */
    boolean reselectCourse(Long studentId, Long courseId);
    
    /**
     * 教师录入成绩
     */
    boolean updateScore(Long studentId, Long courseId, Double score);
}
