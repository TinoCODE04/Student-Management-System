package com.studentsys.teacher.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.studentsys.common.entity.Teacher;
import com.studentsys.teacher.mapper.TeacherMapper;
import com.studentsys.teacher.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 教师Service实现
 */
@Service
@RequiredArgsConstructor
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, Teacher> implements TeacherService {
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    @Override
    public Teacher getByUsername(String username) {
        return lambdaQuery().eq(Teacher::getUsername, username).one();
    }
    
    @Override
    public Teacher getByTeacherNo(String teacherNo) {
        return lambdaQuery().eq(Teacher::getTeacherNo, teacherNo).one();
    }
    
    @Override
    public boolean updatePassword(Long userId, String oldPassword, String newPassword) {
        Teacher teacher = getById(userId);
        if (teacher == null) {
            return false;
        }
        
        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, teacher.getPassword())) {
            return false;
        }
        
        // 更新密码
        teacher.setPassword(passwordEncoder.encode(newPassword));
        return updateById(teacher);
    }
    
    @Override
    public long countByCollegeId(Long collegeId) {
        return lambdaQuery().eq(Teacher::getCollegeId, collegeId).count();
    }
    
    @Override
    public long countByMajorId(Long majorId) {
        return lambdaQuery().eq(Teacher::getMajorId, majorId).count();
    }
}
