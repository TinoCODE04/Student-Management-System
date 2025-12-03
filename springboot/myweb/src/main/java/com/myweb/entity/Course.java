package com.myweb.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 课程实体类
 */
@Data
@TableName("course")
public class Course {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 课程名称
     */
    private String courseName;
    
    /**
     * 学分
     */
    private Integer credit;
    
    /**
     * 所属学院ID
     */
    private Long collegeId;
    
    /**
     * 上课教师ID
     */
    private Long teacherId;
    
    /**
     * 上课时段
     */
    private String schedule;
    
    /**
     * 上课地点
     */
    private String location;
    
    /**
     * 最大选课人数
     */
    private Integer maxStudents;
    
    /**
     * 已选人数
     */
    private Integer selectedCount;
    
    /**
     * 课程描述
     */
    private String description;
    
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
     * 授课教师（非数据库字段）
     */
    @TableField(exist = false)
    private Teacher teacher;
}
