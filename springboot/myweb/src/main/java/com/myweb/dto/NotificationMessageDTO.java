package com.myweb.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 通知消息DTO
 * 用于RabbitMQ消息传递
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMessageDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 消息类型：sms-短信, email-邮件
     */
    private String type;
    
    /**
     * 接收者ID
     */
    private Long userId;
    
    /**
     * 接收者类型：student-学生, teacher-教师
     */
    private String userType;
    
    /**
     * 接收者姓名
     */
    private String userName;
    
    /**
     * 接收者手机号
     */
    private String phone;
    
    /**
     * 接收者邮箱
     */
    private String email;
    
    /**
     * 通知标题
     */
    private String title;
    
    /**
     * 通知内容
     */
    private String content;
    
    /**
     * 相关业务类型：course-课程, selection-选课
     */
    private String relatedType;
    
    /**
     * 相关业务ID
     */
    private Long relatedId;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
