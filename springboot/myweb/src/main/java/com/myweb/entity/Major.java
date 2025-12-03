package com.myweb.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 专业实体类
 */
@Data
@TableName("major")
public class Major {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 专业名称
     */
    private String majorName;
    
    /**
     * 系名
     */
    private String departmentName;
    
    /**
     * 所属学院ID
     */
    private Long collegeId;
    
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
     * 学生数量（非数据库字段）
     */
    @TableField(exist = false)
    private Integer studentCount;
    
    /**
     * 教师数量（非数据库字段）
     */
    @TableField(exist = false)
    private Integer teacherCount;
}
