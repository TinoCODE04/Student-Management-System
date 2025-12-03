package com.myweb.dto;

import lombok.Data;

/**
 * 课程查询DTO
 */
@Data
public class CourseQueryDTO {
    
    /**
     * 课程名称（模糊查询）
     */
    private String courseName;
    
    /**
     * 教师ID
     */
    private Long teacherId;
    
    /**
     * 学院ID
     */
    private Long collegeId;
    
    /**
     * 当前页
     */
    private Integer pageNum = 1;
    
    /**
     * 每页条数
     */
    private Integer pageSize = 10;
}
