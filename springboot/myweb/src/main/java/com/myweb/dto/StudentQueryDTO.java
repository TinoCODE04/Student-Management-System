package com.myweb.dto;

import lombok.Data;

/**
 * 学生查询DTO
 */
@Data
public class StudentQueryDTO {
    
    /**
     * 姓名（模糊查询）
     */
    private String name;
    
    /**
     * 学号
     */
    private String studentNo;
    
    /**
     * 班级
     */
    private String className;
    
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
