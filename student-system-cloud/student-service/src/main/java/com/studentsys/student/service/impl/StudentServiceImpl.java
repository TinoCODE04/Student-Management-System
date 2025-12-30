package com.studentsys.student.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.studentsys.common.dto.StudentQueryDTO;
import com.studentsys.common.entity.Student;
import com.studentsys.student.mapper.StudentMapper;
import com.studentsys.student.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 学生Service实现
 */
@Service
@RequiredArgsConstructor
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentService {
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    @Override
    public Page<Student> pageQuery(StudentQueryDTO queryDTO) {
        Page<Student> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        
        // 姓名模糊查询
        if (StringUtils.hasText(queryDTO.getName())) {
            wrapper.like(Student::getName, queryDTO.getName());
        }
        // 学号模糊查询
        if (StringUtils.hasText(queryDTO.getStudentNo())) {
            wrapper.like(Student::getStudentNo, queryDTO.getStudentNo());
        }
        // 班级
        if (StringUtils.hasText(queryDTO.getClassName())) {
            wrapper.eq(Student::getClassName, queryDTO.getClassName());
        }
        // 学院
        if (queryDTO.getCollegeId() != null) {
            wrapper.eq(Student::getCollegeId, queryDTO.getCollegeId());
        }
        // 专业
        if (queryDTO.getMajorId() != null) {
            wrapper.eq(Student::getMajorId, queryDTO.getMajorId());
        }
        // 年级
        if (StringUtils.hasText(queryDTO.getGrade())) {
            wrapper.eq(Student::getGrade, queryDTO.getGrade());
        }
        
        wrapper.orderByDesc(Student::getCreateTime);
        
        return baseMapper.selectPage(page, wrapper);
    }
    
    @Override
    public Student getByUsername(String username) {
        return lambdaQuery().eq(Student::getUsername, username).one();
    }
    
    @Override
    public Student getByStudentNo(String studentNo) {
        return lambdaQuery().eq(Student::getStudentNo, studentNo).one();
    }
    
    @Override
    public boolean updatePassword(Long userId, String oldPassword, String newPassword) {
        Student student = getById(userId);
        if (student == null) {
            return false;
        }
        
        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, student.getPassword())) {
            return false;
        }
        
        // 更新密码
        student.setPassword(passwordEncoder.encode(newPassword));
        return updateById(student);
    }
    
    @Override
    public long countByCollegeId(Long collegeId) {
        return lambdaQuery().eq(Student::getCollegeId, collegeId).count();
    }
    
    @Override
    public long countByMajorId(Long majorId) {
        return lambdaQuery().eq(Student::getMajorId, majorId).count();
    }
}
