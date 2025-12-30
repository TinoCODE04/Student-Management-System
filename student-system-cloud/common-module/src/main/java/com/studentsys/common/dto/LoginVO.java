package com.studentsys.common.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 登录响应VO
 */
@Data
public class LoginVO implements Serializable {
    
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
     * 角色
     */
    private String role;
    
    /**
     * 头像
     */
    private String avatar;
}
