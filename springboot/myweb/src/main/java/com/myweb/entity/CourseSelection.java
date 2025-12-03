package com.myweb.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 选课记录实体类
 */
@Data
@TableName("course_selection")
public class CourseSelection {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 学生ID
     */
    private Long studentId;
    
    /**
     * 课程ID
     */
    private Long courseId;
    
    /**
     * 成绩
     */
    private BigDecimal score;
    
    /**
     * 状态：selected已选，dropped已退选，completed已完成
     */
    private String status;
    
    /**
     * 选课时间
     */
    private LocalDateTime selectTime;
    
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
     * 学生信息（非数据库字段）
     */
    @TableField(exist = false)
    private Student student;
    
    /**
     * 课程信息（非数据库字段）
     */
    @TableField(exist = false)
    private Course course;
}
