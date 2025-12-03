package com.myweb.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 学生实体类
 */
@Data
@TableName("student")
public class Student {
    
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
     * 学号
     */
    private String studentNo;
    
    /**
     * 总学分
     */
    private Integer totalCredit;
    
    /**
     * 所属学院ID
     */
    private Long collegeId;
    
    /**
     * 班级名称
     */
    private String className;
    
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
     * 年龄
     */
    private Integer age;
    
    /**
     * 出生日期
     */
    private LocalDateTime birthDate;
    
    /**
     * 年级（如：2021）
     */
    private String grade;
    
    /**
     * 入学日期
     */
    private LocalDateTime enrollDate;
    
    /**
     * 所属专业ID
     */
    private Long majorId;
    
    /**
     * 账号状态
     */
    private String status;
    
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
     * 所属学院（非数据库字段）
     */
    @TableField(exist = false)
    private College college;
    
    /**
     * 所属专业（非数据库字段）
     */
    @TableField(exist = false)
    private Major major;
}
