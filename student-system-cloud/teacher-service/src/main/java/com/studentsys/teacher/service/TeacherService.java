package com.studentsys.teacher.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.studentsys.common.entity.Teacher;

/**
 * 教师Service接口
 */
public interface TeacherService extends IService<Teacher> {
    
    /**
     * 根据用户名查询
     */
    Teacher getByUsername(String username);
    
    /**
     * 根据教工号查询
     */
    Teacher getByTeacherNo(String teacherNo);
    
    /**
     * 修改密码
     */
    boolean updatePassword(Long userId, String oldPassword, String newPassword);
    
    /**
     * 根据学院ID统计教师数量
     */
    long countByCollegeId(Long collegeId);
    
    /**
     * 根据专业ID统计教师数量
     */
    long countByMajorId(Long majorId);
}
