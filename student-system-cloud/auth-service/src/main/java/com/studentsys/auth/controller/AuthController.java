package com.studentsys.auth.controller;

import com.studentsys.auth.service.AuthService;
import com.studentsys.common.dto.LoginDTO;
import com.studentsys.common.dto.LoginVO;
import com.studentsys.common.result.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证Controller
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    /**
     * 登录
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        LoginVO loginVO = authService.login(loginDTO);
        return Result.success("登录成功", loginVO);
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
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestHeader(value = "X-Username", required = false) String username,
            @RequestHeader(value = "X-Role", required = false) String role) {
        Map<String, Object> info = new HashMap<>();
        info.put("userId", userId);
        info.put("username", username);
        info.put("role", role);
        return Result.success(info);
    }
    
    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.success("退出成功", null);
    }
}
