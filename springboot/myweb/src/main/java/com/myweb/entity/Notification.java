package com.myweb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 通知实体
 */
@Data
@TableName("notification")
public class Notification {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private String userType;
    
    private String title;
    
    private String content;
    
    private String type;
    
    private Long relatedId;
    
    private String url;
    
    private Integer status;  // 0-未读, 1-已读
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
}
