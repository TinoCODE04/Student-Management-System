package com.myweb.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 教师实体类
 */
@Data
@TableName("teacher")
public class Teacher {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户名（登录账号）
     */
    private String username;
    
    /**
     * 密码（BCrypt加密）
     */
    private String password;
    
    /**
     * 姓名
     */
    private String name;
    
    /**
     * 性别
     */
    private String gender;
    
    /**
     * 职称
     */
    private String title;
    
    /**
     * 所属专业ID
     */
    private Long majorId;
    
    /**
     * 角色
     */
    private String role;
    
    /**
     * 头像URL
     */
    private String avatar;
    
    /**
     * 联系电话
     */
    private String phone;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    /**
     * 所属专业（非数据库字段）
     */
    @TableField(exist = false)
    private Major major;
}
