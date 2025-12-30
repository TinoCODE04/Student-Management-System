package com.studentsys.selection.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.studentsys.common.entity.CourseSelection;

import java.util.List;

/**
 * 选课Service接口
 */
public interface CourseSelectionService extends IService<CourseSelection> {
    
    /**
     * 分页查询选课记录（教师视角）
     */
    Page<CourseSelection> pageQuery(Integer pageNum, Integer pageSize, Long courseId, Long studentId, Integer status);
    
    /**
     * 学生选课
     */
    boolean selectCourse(Long studentId, Long courseId);
    
    /**
     * 学生退课
     */
    boolean dropCourse(Long studentId, Long courseId);
    
    /**
     * 学生重选（退课后再选）
     */
    boolean reselectCourse(Long studentId, Long courseId);
    
    /**
     * 查询学生已选课程
     */
    List<CourseSelection> listByStudentId(Long studentId);
    
    /**
     * 查询课程已选学生
     */
    List<CourseSelection> listByCourseId(Long courseId);
    
    /**
     * 录入成绩
     */
    boolean enterScore(Long selectionId, Double score);
    
    /**
     * 批量录入成绩
     */
    boolean batchEnterScore(List<Long> selectionIds, List<Double> scores);
    
    /**
     * 获取学生成绩列表
     */
    List<CourseSelection> getStudentGrades(Long studentId);
    
    /**
     * 检查学生是否已选某课程
     */
    boolean hasSelected(Long studentId, Long courseId);
}
