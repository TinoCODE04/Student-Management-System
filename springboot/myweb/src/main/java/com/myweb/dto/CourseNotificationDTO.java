package com.myweb.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 课程通知DTO
 * 教师发送课程通知给选课学生
 */
@Data
public class CourseNotificationDTO {
    
    /**
     * 课程ID
     */
    @NotNull(message = "课程ID不能为空")
    private Long courseId;
    
    /**
     * 通知标题
     */
    @NotBlank(message = "通知标题不能为空")
    private String title;
    
    /**
     * 通知内容
     */
    @NotBlank(message = "通知内容不能为空")
    private String content;
    
    /**
     * 是否发送短信（默认false）
     */
    private Boolean sendSms = false;
    
    /**
     * 是否发送邮件（默认false）
     */
    private Boolean sendEmail = false;
}
