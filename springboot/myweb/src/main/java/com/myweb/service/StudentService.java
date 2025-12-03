package com.myweb.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.myweb.dto.StudentQueryDTO;
import com.myweb.entity.Student;

/**
 * 学生Service接口
 */
public interface StudentService extends IService<Student> {
    
    /**
     * 根据用户名查询学生
     */
    Student getByUsername(String username);
    
    /**
     * 根据学号查询学生
     */
    Student getByStudentNo(String studentNo);
    
    /**
     * 分页查询学生
     */
    Page<Student> pageQuery(StudentQueryDTO queryDTO);
    
    /**
     * 修改密码
     */
    boolean updatePassword(Long id, String oldPassword, String newPassword);
}
