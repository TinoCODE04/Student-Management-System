package com.myweb.controller;

import com.myweb.common.Result;
import com.myweb.dto.LoginDTO;
import com.myweb.dto.LoginVO;
import com.myweb.dto.PasswordDTO;
import com.myweb.entity.Student;
import com.myweb.entity.Teacher;
import com.myweb.service.AuthService;
import com.myweb.service.StudentService;
import com.myweb.service.TeacherService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证Controller
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private StudentService studentService;
    
    @Autowired
    private TeacherService teacherService;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    /**
     * 登录
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        try {
            LoginVO loginVO = authService.login(loginDTO);
            return Result.success("登录成功", loginVO);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取验证码
     */
    @GetMapping("/captcha")
    public Result<Map<String, String>> getCaptcha() {
        String[] captcha = authService.generateCaptcha();
        Map<String, String> result = new HashMap<>();
        result.put("captchaKey", captcha[0]);
        result.put("captchaImage", captcha[1]);
        return Result.success(result);
    }
    
    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    public Result<Map<String, Object>> getUserInfo(
            @RequestAttribute("userId") Long userId,
            @RequestAttribute("username") String username,
            @RequestAttribute("role") String role) {
        Map<String, Object> info = new HashMap<>();
        info.put("userId", userId);
        info.put("username", username);
        info.put("role", role);
        return Result.success(info);
    }
    
    /**
     * 修改密码
     */
    @PostMapping("/password")
    public Result<Void> changePassword(
            @Valid @RequestBody PasswordDTO passwordDTO,
            @RequestAttribute("userId") Long userId,
            @RequestAttribute("role") String role) {
        
        String currentPassword;
        
        if ("student".equals(role)) {
            Student student = studentService.getById(userId);
            if (student == null) {
                return Result.error("用户不存在");
            }
            currentPassword = student.getPassword();
            
            // 验证旧密码
            if (!passwordEncoder.matches(passwordDTO.getOldPassword(), currentPassword)) {
                return Result.error("原密码错误");
            }
            
            // 更新新密码
            student.setPassword(passwordEncoder.encode(passwordDTO.getNewPassword()));
            studentService.updateById(student);
        } else {
            Teacher teacher = teacherService.getById(userId);
            if (teacher == null) {
                return Result.error("用户不存在");
            }
            currentPassword = teacher.getPassword();
            
            // 验证旧密码
            if (!passwordEncoder.matches(passwordDTO.getOldPassword(), currentPassword)) {
                return Result.error("原密码错误");
            }
            
            // 更新新密码
            teacher.setPassword(passwordEncoder.encode(passwordDTO.getNewPassword()));
            teacherService.updateById(teacher);
        }
        
        return Result.success("密码修改成功", null);
    }
    
    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.success("退出成功", null);
    }
}
