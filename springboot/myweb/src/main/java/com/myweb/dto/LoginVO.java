package com.myweb.dto;

import lombok.Data;

/**
 * 登录响应VO
 */
@Data
public class LoginVO {
    
    /**
     * JWT Token
     */
    private String token;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 姓名
     */
    private String name;
    
    /**
     * 角色：student/teacher
     */
    private String role;
    
    /**
     * 头像URL
     */
    private String avatar;
}
