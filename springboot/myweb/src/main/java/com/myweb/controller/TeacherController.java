package com.myweb.controller;

import com.myweb.common.Result;
import com.myweb.dto.PasswordDTO;
import com.myweb.entity.Major;
import com.myweb.entity.Teacher;
import com.myweb.service.CollegeService;
import com.myweb.service.MajorService;
import com.myweb.service.TeacherService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
