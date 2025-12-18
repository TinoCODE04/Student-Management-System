package com.myweb.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.myweb.common.Result;
import com.myweb.dto.PasswordDTO;
import com.myweb.dto.StudentQueryDTO;
import com.myweb.entity.College;
import com.myweb.entity.Major;
import com.myweb.entity.Student;
import com.myweb.service.CollegeService;
import com.myweb.service.MajorService;
import com.myweb.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * 学生Controller
 */
@RestController
@RequestMapping("/api/student")
public class StudentController {
    
    @Autowired
    private StudentService studentService;
    
    @Autowired
    private CollegeService collegeService;
    
    @Autowired
    private MajorService majorService;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    /**
     * 分页查询学生列表
     */
    @GetMapping("/page")
    public Result<Page<Student>> page(StudentQueryDTO queryDTO,
                                       @RequestAttribute(value = "userId", required = false) Long userId,
                                       @RequestAttribute(value = "role", required = false) String role) {
        // 如果是教师角色,只查询该教师教授的学生
        if ("teacher".equals(role) && userId != null) {
            queryDTO.setTeacherId(userId);
        }
        
        Page<Student> page = studentService.pageQuery(queryDTO);
        // 清除密码字段
        page.getRecords().forEach(s -> s.setPassword(null));
        return Result.success(page);
    }
    
    /**
     * 获取所有学生列表
     */
    @GetMapping("/list")
    public Result<?> list() {
        return Result.success(studentService.list());
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
     * 新增学生（管理员和教师）
     */
    @PostMapping
    public Result<Void> add(@RequestBody Student student,
                            @RequestAttribute("role") String role) {
        // 管理员和教师可以添加学生
        if (!"teacher".equals(role) && !"admin".equals(role)) {
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
     * 更新学生（管理员和教师可以修改任何学生，学生只能修改自己）
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, 
                               @RequestBody Student student,
                               @RequestAttribute("role") String role,
                               @RequestAttribute("userId") Long userId) {
        // 管理员和教师可以修改任何学生，学生只能修改自己
        if (!"teacher".equals(role) && !"admin".equals(role) && !id.equals(userId)) {
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
     * 删除学生（管理员和教师）
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id,
                               @RequestAttribute("role") String role) {
        // 管理员和教师可以删除学生
        if (!"teacher".equals(role) && !"admin".equals(role)) {
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
                                       @RequestAttribute("userId") Long userId,
                                       @RequestAttribute("role") String role) {
        if (!"student".equals(role)) {
            return Result.forbidden("此接口仅供学生使用");
        }
        
        // if (!passwordDTO.getNewPassword().equals(passwordDTO.getConfirmPassword())) {
        //     return Result.error("两次密码不一致");
        // }
        
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
    public Result<Student> getCurrentInfo(@RequestAttribute("userId") Long userId,
                                          @RequestAttribute("role") String role) {
        if (!"student".equals(role)) {
            return Result.forbidden("此接口仅供学生使用");
        }
        
        Student student = studentService.getById(userId);
        if (student != null) {
            student.setPassword(null);
            // 关联查询学院信息
            if (student.getCollegeId() != null) {
                College college = collegeService.getById(student.getCollegeId());
                student.setCollege(college);
            }
            // 关联查询专业信息
            if (student.getMajorId() != null) {
                Major major = majorService.getById(student.getMajorId());
                student.setMajor(major);
            }
        }
        return Result.success(student);
    }
}
