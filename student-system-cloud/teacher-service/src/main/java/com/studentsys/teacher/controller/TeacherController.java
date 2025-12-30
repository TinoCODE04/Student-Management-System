package com.studentsys.teacher.controller;

import com.studentsys.common.dto.PasswordDTO;
import com.studentsys.common.entity.College;
import com.studentsys.common.entity.Teacher;
import com.studentsys.common.result.Result;
import com.studentsys.teacher.feign.CollegeFeignClient;
import com.studentsys.teacher.service.TeacherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 教师Controller
 */
@RestController
@RequestMapping("/api/teacher")
@RequiredArgsConstructor
public class TeacherController {
    
    private final TeacherService teacherService;
    private final CollegeFeignClient collegeFeignClient;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
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
     * 新增教师
     */
    @PostMapping
    public Result<Void> add(@RequestBody Teacher teacher,
                            @RequestHeader(value = "X-Role", required = false) String role) {
        // 只有教师可以添加教师（管理员权限）
        if (!"teacher".equals(role)) {
            return Result.forbidden("无权限操作");
        }
        
        // 检查用户名是否已存在
        if (teacherService.getByUsername(teacher.getUsername()) != null) {
            return Result.error("用户名已存在");
        }
        
        // 检查教工号是否已存在
        if (teacherService.getByTeacherNo(teacher.getTeacherNo()) != null) {
            return Result.error("教工号已存在");
        }
        
        // 加密密码
        if (teacher.getPassword() == null || teacher.getPassword().isEmpty()) {
            teacher.setPassword(passwordEncoder.encode("123456"));
        } else {
            teacher.setPassword(passwordEncoder.encode(teacher.getPassword()));
        }
        
        teacher.setRole("teacher");
        teacherService.save(teacher);
        return Result.success();
    }
    
    /**
     * 更新教师
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id,
                               @RequestBody Teacher teacher,
                               @RequestHeader(value = "X-Role", required = false) String role,
                               @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        // 只有教师可以修改（自己或管理员）
        if (!"teacher".equals(role)) {
            return Result.forbidden("无权限操作");
        }
        
        teacher.setId(id);
        teacher.setPassword(null);
        teacher.setRole(null);
        teacherService.updateById(teacher);
        return Result.success();
    }
    
    /**
     * 删除教师
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id,
                               @RequestHeader(value = "X-Role", required = false) String role,
                               @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        // 只有教师可以删除（管理员权限），且不能删除自己
        if (!"teacher".equals(role)) {
            return Result.forbidden("无权限操作");
        }
        if (id.equals(userId)) {
            return Result.error("不能删除自己");
        }
        
        teacherService.removeById(id);
        return Result.success();
    }
    
    /**
     * 修改密码
     */
    @PutMapping("/password")
    public Result<Void> updatePassword(@Valid @RequestBody PasswordDTO passwordDTO,
                                       @RequestHeader(value = "X-User-Id", required = false) Long userId,
                                       @RequestHeader(value = "X-Role", required = false) String role) {
        if (!"teacher".equals(role)) {
            return Result.forbidden("此接口仅供教师使用");
        }
        
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
    public Result<Teacher> getCurrentInfo(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                          @RequestHeader(value = "X-Role", required = false) String role) {
        if (!"teacher".equals(role)) {
            return Result.forbidden("此接口仅供教师使用");
        }
        
        Teacher teacher = teacherService.getById(userId);
        if (teacher != null) {
            teacher.setPassword(null);
            // 通过Feign获取学院信息
            if (teacher.getCollegeId() != null) {
                try {
                    Result<College> collegeResult = collegeFeignClient.getById(teacher.getCollegeId());
                    if (collegeResult.getCode() == 200) {
                        teacher.setCollege(collegeResult.getData());
                    }
                } catch (Exception e) {
                    // 忽略Feign调用异常
                }
            }
        }
        return Result.success(teacher);
    }
    
    /**
     * 统计学院教师数量（供其他服务调用）
     */
    @GetMapping("/count")
    public Result<Long> countByCollegeId(@RequestParam("collegeId") Long collegeId) {
        return Result.success(teacherService.countByCollegeId(collegeId));
    }
    
    /**
     * 统计专业教师数量（供其他服务调用）
     */
    @GetMapping("/count/major")
    public Result<Long> countByMajorId(@RequestParam("majorId") Long majorId) {
        return Result.success(teacherService.countByMajorId(majorId));
    }
}
