package com.myweb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统公告实体
 */
@Data
@TableName("announcement")
public class Announcement {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 公告标题
     */
    private String title;
    
    /**
     * 公告内容
     */
    private String content;
    
    /**
     * 目标角色: all/teacher/student
     */
    private String targetRole;
    
    /**
     * 优先级: high/medium/low
     */
    private String priority;
    
    /**
     * 发布者ID
     */
    private Long publisherId;
    
    /**
     * 发布者姓名
     */
    private String publisherName;
    
    /**
     * 发布者类型
     */
    private String publisherType;
    
    /**
     * 状态: 0-已删除 1-正常
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
