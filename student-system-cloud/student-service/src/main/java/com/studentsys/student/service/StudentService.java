package com.studentsys.student.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.studentsys.common.dto.StudentQueryDTO;
import com.studentsys.common.entity.Student;

/**
 * 学生Service接口
 */
public interface StudentService extends IService<Student> {
    
    /**
     * 分页查询学生
     */
    Page<Student> pageQuery(StudentQueryDTO queryDTO);
    
    /**
     * 根据用户名查询
     */
    Student getByUsername(String username);
    
    /**
     * 根据学号查询
     */
    Student getByStudentNo(String studentNo);
    
    /**
     * 修改密码
     */
    boolean updatePassword(Long userId, String oldPassword, String newPassword);
    
    /**
     * 根据学院ID统计学生数量
     */
    long countByCollegeId(Long collegeId);
    
    /**
     * 根据专业ID统计学生数量
     */
    long countByMajorId(Long majorId);
}
