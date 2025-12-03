package com.myweb.service;

import com.myweb.dto.LoginDTO;
import com.myweb.dto.LoginVO;

/**
 * 认证Service接口
 */
public interface AuthService {
    
    /**
     * 用户登录
     */
    LoginVO login(LoginDTO loginDTO);
    
    /**
     * 生成验证码
     * @return [验证码key, 验证码图片Base64]
     */
    String[] generateCaptcha();
    
    /**
     * 验证验证码
     */
    boolean verifyCaptcha(String key, String code);
}
