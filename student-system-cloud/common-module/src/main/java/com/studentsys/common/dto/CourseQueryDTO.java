package com.studentsys.common.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 课程查询DTO
 */
@Data
public class CourseQueryDTO implements Serializable {
    
    /**
     * 当前页码
     */
    private Integer pageNum = 1;
    
    /**
     * 每页大小
     */
    private Integer pageSize = 10;
    
    /**
     * 课程名称（模糊查询）
     */
    private String courseName;
    
    /**
     * 课程编号（精确查询）
     */
    private String courseNo;
    
    /**
     * 学期（精确查询）
     */
    private String semester;
    
    /**
     * 学院ID
     */
    private Long collegeId;
    
    /**
     * 教师ID
     */
    private Long teacherId;
}
