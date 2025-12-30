package com.studentsys.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 课程实体类
 */
@Data
@TableName("course")
public class Course implements Serializable {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 课程编号
     */
    private String courseNo;
    
    /**
     * 课程名称
     */
    private String courseName;
    
    /**
     * 学期
     */
    private String semester;
    
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
    
    // ========== 非数据库字段 ==========
    
    /**
     * 所属学院
     */
    @TableField(exist = false)
    private College college;
    
    /**
     * 任课教师
     */
    @TableField(exist = false)
    private Teacher teacher;
}
