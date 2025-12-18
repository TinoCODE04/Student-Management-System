package com.myweb.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.myweb.common.Result;
import com.myweb.dto.PasswordDTO;
import com.myweb.entity.Major;
import com.myweb.entity.Teacher;
import com.myweb.service.CollegeService;
import com.myweb.service.MajorService;
import com.myweb.service.TeacherService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 教师Controller
 */
@RestController
@RequestMapping("/api/teacher")
public class TeacherController {
    
    @Autowired
    private TeacherService teacherService;
    
    @Autowired
    private MajorService majorService;
    
    @Autowired
    private CollegeService collegeService;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    /**
     * 分页查询教师
     */
    @GetMapping("/page")
    public Result<Page<Teacher>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Long majorId) {
        
        Page<Teacher> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Teacher> wrapper = new LambdaQueryWrapper<>();
        
        if (name != null && !name.isEmpty()) {
            wrapper.like(Teacher::getName, name);
        }
        if (username != null && !username.isEmpty()) {
            wrapper.like(Teacher::getUsername, username);
        }
        if (majorId != null) {
            wrapper.eq(Teacher::getMajorId, majorId);
        }
        
        Page<Teacher> result = teacherService.page(page, wrapper);
        // 清空密码
        result.getRecords().forEach(teacher -> teacher.setPassword(null));
        
        return Result.success(result);
    }
    
    /**
     * 获取所有教师列表
     */
    @GetMapping("/list")
    public Result<List<Teacher>> list() {
        List<Teacher> teachers = teacherService.list();
        teachers.forEach(t -> t.setPassword(null));
        return Result.success(teachers);
    }
    
    /**
     * 根据ID获取教师
     */
    @GetMapping("/{id}")
    public Result<Teacher> getById(@PathVariable Long id) {
        Teacher teacher = teacherService.getById(id);
        if (teacher != null) {
            teacher.setPassword(null);
        }
        return Result.success(teacher);
    }
    
    /**
     * 新增教师（仅管理员）
     */
    @PostMapping
    public Result<Void> add(@RequestBody Teacher teacher,
                           @RequestAttribute(value = "role", required = false) String role) {
        // 只有管理员可以新增教师
        if (!"admin".equals(role)) {
            return Result.forbidden("无权限操作");
        }
        
        // 检查用户名是否已存在
        LambdaQueryWrapper<Teacher> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Teacher::getUsername, teacher.getUsername());
        if (teacherService.count(wrapper) > 0) {
            return Result.error("用户名已存在");
        }
        
        // 加密密码
        if (teacher.getPassword() == null || teacher.getPassword().isEmpty()) {
            return Result.error("密码不能为空");
        }
        teacher.setPassword(passwordEncoder.encode(teacher.getPassword()));
        teacher.setRole("teacher");
        if (teacher.getStatus() == null) {
            teacher.setStatus("active");
        }
        
        teacherService.save(teacher);
        return Result.success("添加成功", null);
    }
    
    /**
     * 更新教师（仅管理员）
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, 
                              @RequestBody Teacher teacher,
                              @RequestAttribute(value = "role", required = false) String role) {
        // 只有管理员可以更新教师
        if (!"admin".equals(role)) {
            return Result.forbidden("无权限操作");
        }
        
        Teacher existTeacher = teacherService.getById(id);
        if (existTeacher == null) {
            return Result.error("教师不存在");
        }
        
        teacher.setId(id);
        
        // 如果有密码，则加密
        if (teacher.getPassword() != null && !teacher.getPassword().isEmpty()) {
            teacher.setPassword(passwordEncoder.encode(teacher.getPassword()));
        } else {
            teacher.setPassword(null); // 不修改密码
        }
        
        teacherService.updateById(teacher);
        return Result.success("更新成功", null);
    }
    
    /**
     * 删除教师（仅管理员）
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id,
                              @RequestAttribute(value = "role", required = false) String role) {
        // 只有管理员可以删除教师
        if (!"admin".equals(role)) {
            return Result.forbidden("无权限操作");
        }
        
        teacherService.removeById(id);
        return Result.success("删除成功", null);
    }
    
    /**
     * 修改密码
     */
    @PutMapping("/password")
    public Result<Void> updatePassword(@Valid @RequestBody PasswordDTO passwordDTO,
                                       @RequestAttribute("userId") Long userId,
                                       @RequestAttribute("role") String role) {
        if (!"teacher".equals(role)) {
            return Result.forbidden("此接口仅供教师使用");
        }
        
        // if (!passwordDTO.getNewPassword().equals(passwordDTO.getConfirmPassword())) {
        //     return Result.error("两次密码不一致");
        // }
        
        boolean success = teacherService.updatePassword(userId, passwordDTO.getOldPassword(), passwordDTO.getNewPassword());
        if (!success) {
            return Result.error("旧密码错误");
        }
        return Result.success("密码修改成功", null);
    }
    
    /**
     * 获取当前登录教师信息
     */
    @GetMapping("/info")
    public Result<Teacher> getCurrentInfo(@RequestAttribute("userId") Long userId,
                                          @RequestAttribute("role") String role) {
        if (!"teacher".equals(role)) {
            return Result.forbidden("此接口仅供教师使用");
        }
        
        Teacher teacher = teacherService.getById(userId);
        if (teacher != null) {
            teacher.setPassword(null);
            // 关联查询专业和学院信息
            if (teacher.getMajorId() != null) {
                Major major = majorService.getById(teacher.getMajorId());
                if (major != null) {
                    // 关联查询学院信息
                    if (major.getCollegeId() != null) {
                        major.setCollege(collegeService.getById(major.getCollegeId()));
                    }
                    teacher.setMajor(major);
                }
            }
        }
        return Result.success(teacher);
    }
    
    /**
     * 更新教师个人信息
     */
    @PutMapping("/info")
    public Result<Void> updateInfo(@RequestBody Teacher teacher,
                                   @RequestAttribute("userId") Long userId,
                                   @RequestAttribute("role") String role) {
        if (!"teacher".equals(role)) {
            return Result.forbidden("此接口仅供教师使用");
        }
        
        teacher.setId(userId);
        // 不允许修改密码和角色
        teacher.setPassword(null);
        teacher.setRole(null);
        teacherService.updateById(teacher);
        return Result.success();
    }
}
