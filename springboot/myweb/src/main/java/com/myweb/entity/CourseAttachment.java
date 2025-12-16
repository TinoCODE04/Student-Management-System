package com.myweb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 课程附件实体
 */
@Data
@TableName("course_attachment")
public class CourseAttachment {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long courseId;
    
    private String filename;
    
    private String originalFilename;
    
    private String contentType;
    
    private Long fileSize;
    
    private String minioKey;
    
    private Long uploaderId;
    
    private String uploaderType;
    
    private String description;
    
    private Integer downloadCount;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
    
    // 非数据库字段
    @TableField(exist = false)
    private String fileUrl;
    
    @TableField(exist = false)
    private String uploaderName;
}
