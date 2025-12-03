package com.myweb.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.myweb.entity.Teacher;
import com.myweb.mapper.TeacherMapper;
import com.myweb.service.TeacherService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 教师Service实现类
 */
@Service
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, Teacher> implements TeacherService {
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    @Override
    public Teacher getByUsername(String username) {
        return baseMapper.selectByUsername(username);
    }
    
    @Override
    public boolean updatePassword(Long id, String oldPassword, String newPassword) {
        Teacher teacher = this.getById(id);
        if (teacher == null) {
            return false;
        }
        
        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, teacher.getPassword())) {
            return false;
        }
        
        // 更新新密码
        teacher.setPassword(passwordEncoder.encode(newPassword));
        return this.updateById(teacher);
    }
}
