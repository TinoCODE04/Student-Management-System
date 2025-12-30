package com.studentsys.student.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.studentsys.common.dto.PasswordDTO;
import com.studentsys.common.dto.StudentQueryDTO;
import com.studentsys.common.entity.College;
import com.studentsys.common.entity.Major;
import com.studentsys.common.entity.Student;
import com.studentsys.common.result.Result;
import com.studentsys.student.feign.CollegeFeignClient;
import com.studentsys.student.feign.MajorFeignClient;
import com.studentsys.student.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学生Controller
 */
@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentController {
    
    private final StudentService studentService;
    private final CollegeFeignClient collegeFeignClient;
    private final MajorFeignClient majorFeignClient;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    /**
     * 分页查询学生列表
     */
    @GetMapping("/page")
    public Result<Page<Student>> page(StudentQueryDTO queryDTO) {
        Page<Student> page = studentService.pageQuery(queryDTO);
        // 清除密码字段
        page.getRecords().forEach(s -> s.setPassword(null));
        return Result.success(page);
    }
    
    /**
     * 获取所有学生列表
     */
    @GetMapping("/list")
    public Result<List<Student>> list() {
        List<Student> students = studentService.list();
        students.forEach(s -> s.setPassword(null));
        return Result.success(students);
    }
    
    /**
     * 根据ID获取学生
     */
    @GetMapping("/{id}")
    public Result<Student> getById(@PathVariable Long id) {
        Student student = studentService.getById(id);
        if (student != null) {
            student.setPassword(null);
        }
        return Result.success(student);
    }
    
    /**
     * 新增学生
     */
    @PostMapping
    public Result<Void> add(@RequestBody Student student,
                            @RequestHeader(value = "X-Role", required = false) String role) {
        // 只有教师可以添加学生
        if (!"teacher".equals(role)) {
            return Result.forbidden("无权限操作");
        }
        
        // 检查用户名是否已存在
        if (studentService.getByUsername(student.getUsername()) != null) {
            return Result.error("用户名已存在");
        }
        
        // 检查学号是否已存在
        if (studentService.getByStudentNo(student.getStudentNo()) != null) {
            return Result.error("学号已存在");
        }
        
        // 加密密码（默认密码123456）
        if (student.getPassword() == null || student.getPassword().isEmpty()) {
            student.setPassword(passwordEncoder.encode("123456"));
        } else {
            student.setPassword(passwordEncoder.encode(student.getPassword()));
        }
        
        student.setRole("student");
        studentService.save(student);
        return Result.success();
    }
    
    /**
     * 更新学生
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, 
                               @RequestBody Student student,
                               @RequestHeader(value = "X-Role", required = false) String role,
                               @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        // 教师可以修改任何学生，学生只能修改自己
        if (!"teacher".equals(role) && !id.equals(userId)) {
            return Result.forbidden("无权限操作");
        }
        
        student.setId(id);
        // 不允许通过此接口修改密码和角色
        student.setPassword(null);
        student.setRole(null);
        studentService.updateById(student);
        return Result.success();
    }
    
    /**
     * 删除学生
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id,
                               @RequestHeader(value = "X-Role", required = false) String role) {
        // 只有教师可以删除学生
        if (!"teacher".equals(role)) {
            return Result.forbidden("无权限操作");
        }
        
        studentService.removeById(id);
        return Result.success();
    }
    
    /**
     * 修改密码
     */
    @PutMapping("/password")
    public Result<Void> updatePassword(@Valid @RequestBody PasswordDTO passwordDTO,
                                       @RequestHeader(value = "X-User-Id", required = false) Long userId,
                                       @RequestHeader(value = "X-Role", required = false) String role) {
        if (!"student".equals(role)) {
            return Result.forbidden("此接口仅供学生使用");
        }
        
        boolean success = studentService.updatePassword(userId, passwordDTO.getOldPassword(), passwordDTO.getNewPassword());
        if (!success) {
            return Result.error("旧密码错误");
        }
        return Result.success("密码修改成功", null);
    }
    
    /**
     * 获取当前登录学生信息
     */
    @GetMapping("/info")
    public Result<Student> getCurrentInfo(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                          @RequestHeader(value = "X-Role", required = false) String role) {
        if (!"student".equals(role)) {
            return Result.forbidden("此接口仅供学生使用");
        }
        
        Student student = studentService.getById(userId);
        if (student != null) {
            student.setPassword(null);
            // 通过Feign获取学院信息
            if (student.getCollegeId() != null) {
                try {
                    Result<College> collegeResult = collegeFeignClient.getById(student.getCollegeId());
                    if (collegeResult.getCode() == 200) {
                        student.setCollege(collegeResult.getData());
                    }
                } catch (Exception e) {
                    // 忽略Feign调用异常
                }
            }
            // 通过Feign获取专业信息
            if (student.getMajorId() != null) {
                try {
                    Result<Major> majorResult = majorFeignClient.getById(student.getMajorId());
                    if (majorResult.getCode() == 200) {
                        student.setMajor(majorResult.getData());
                    }
                } catch (Exception e) {
                    // 忽略Feign调用异常
                }
            }
        }
        return Result.success(student);
    }
    
    /**
     * 统计学院学生数量（供其他服务调用）
     */
    @GetMapping("/count")
    public Result<Long> countByCollegeId(@RequestParam("collegeId") Long collegeId) {
        return Result.success(studentService.countByCollegeId(collegeId));
    }
    
    /**
     * 统计专业学生数量（供其他服务调用）
     */
    @GetMapping("/count/major")
    public Result<Long> countByMajorId(@RequestParam("majorId") Long majorId) {
        return Result.success(studentService.countByMajorId(majorId));
    }
}
