package com.myweb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.myweb.dto.StudentQueryDTO;
import com.myweb.entity.Student;
import com.myweb.mapper.StudentMapper;
import com.myweb.service.StudentService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 学生Service实现类
 */
@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentService {
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    @Override
    public Student getByUsername(String username) {
        return baseMapper.selectByUsername(username);
    }
    
    @Override
    public Student getByStudentNo(String studentNo) {
        return baseMapper.selectByStudentNo(studentNo);
    }
    
    @Override
    public Page<Student> pageQuery(StudentQueryDTO queryDTO) {
        Page<Student> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        
        // 姓名模糊查询
        if (StringUtils.hasText(queryDTO.getName())) {
            wrapper.like(Student::getName, queryDTO.getName());
        }
        
        // 学号精确查询
        if (StringUtils.hasText(queryDTO.getStudentNo())) {
            wrapper.eq(Student::getStudentNo, queryDTO.getStudentNo());
        }
        
        // 班级模糊查询
        if (StringUtils.hasText(queryDTO.getClassName())) {
            wrapper.like(Student::getClassName, queryDTO.getClassName());
        }
        
        // 学院ID查询
        if (queryDTO.getCollegeId() != null) {
            wrapper.eq(Student::getCollegeId, queryDTO.getCollegeId());
        }
        
        // 排序处理
        if (StringUtils.hasText(queryDTO.getOrderBy())) {
            boolean isAsc = "asc".equalsIgnoreCase(queryDTO.getOrderType());
            
            // 根据排序字段进行排序
            if ("studentNo".equals(queryDTO.getOrderBy())) {
                wrapper.orderBy(true, isAsc, Student::getStudentNo);
            } else {
                // 默认按创建时间倒序
                wrapper.orderByDesc(Student::getCreateTime);
            }
        } else {
            // 默认按创建时间倒序
            wrapper.orderByDesc(Student::getCreateTime);
        }
        
        return this.page(page, wrapper);
    }
    
    @Override
    public boolean updatePassword(Long id, String oldPassword, String newPassword) {
        Student student = this.getById(id);
        if (student == null) {
            return false;
        }
        
        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, student.getPassword())) {
            return false;
        }
        
        // 更新新密码
        student.setPassword(passwordEncoder.encode(newPassword));
        return this.updateById(student);
    }
}
