package com.myweb.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.myweb.entity.Teacher;

/**
 * 教师Service接口
 */
public interface TeacherService extends IService<Teacher> {
    
    /**
     * 根据用户名查询教师
     */
    Teacher getByUsername(String username);
    
    /**
     * 修改密码
     */
    boolean updatePassword(Long id, String oldPassword, String newPassword);
}
