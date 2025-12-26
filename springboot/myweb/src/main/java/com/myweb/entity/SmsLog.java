package com.myweb.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 短信日志实体
 */
@Data
@TableName("sms_log")
public class SmsLog {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 接收者ID
     */
    private Long userId;
    
    /**
     * 接收者类型
     */
    private String userType;
    
    /**
     * 接收者手机号
     */
    private String phone;
    
    /**
     * 短信标题
     */
    private String title;
    
    /**
     * 短信内容
     */
    private String content;
    
    /**
     * 发送状态：pending-待发送, success-成功, failed-失败
     */
    private String status;
    
    /**
     * 失败原因
     */
    private String errorMsg;
    
    /**
     * 相关业务类型
     */
    private String relatedType;
    
    /**
     * 相关业务ID
     */
    private Long relatedId;
    
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
}
