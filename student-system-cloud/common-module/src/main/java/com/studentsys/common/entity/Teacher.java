package com.studentsys.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 教师实体类
 */
@Data
@TableName("teacher")
public class Teacher implements Serializable {
    
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
     * 教工号
     */
    private String teacherNo;
    
    /**
     * 性别
     */
    private String gender;
    
    /**
     * 职称
     */
    private String title;
    
    /**
     * 所属学院ID
     */
    private Long collegeId;
    
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
    
    // ========== 非数据库字段 ==========
    
    /**
     * 所属学院
     */
    @TableField(exist = false)
    private College college;
    
    /**
     * 所属专业
     */
    @TableField(exist = false)
    private Major major;
}
