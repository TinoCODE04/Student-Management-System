package com.myweb.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 学院实体类
 */
@Data
@TableName("college")
public class College {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 学院名称
     */
    private String collegeName;
    
    /**
     * 最低学分要求
     */
    private Integer minCredit;
    
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
     * 专业数量（非数据库字段）
     */
    @TableField(exist = false)
    private Integer majorCount;
    
    /**
     * 学生数量（非数据库字段）
     */
    @TableField(exist = false)
    private Integer studentCount;
    
    /**
     * 课程数量（非数据库字段）
     */
    @TableField(exist = false)
    private Integer courseCount;
}
