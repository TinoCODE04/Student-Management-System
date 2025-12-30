package com.studentsys.common.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 学生查询DTO
 */
@Data
public class StudentQueryDTO implements Serializable {
    
    /**
     * 当前页码
     */
    private Integer pageNum = 1;
    
    /**
     * 每页大小
     */
    private Integer pageSize = 10;
    
    /**
     * 姓名（模糊查询）
     */
    private String name;
    
    /**
     * 学号（模糊查询）
     */
    private String studentNo;
    
    /**
     * 班级名称
     */
    private String className;
    
    /**
     * 学院ID
     */
    private Long collegeId;
    
    /**
     * 专业ID
     */
    private Long majorId;
    
    /**
     * 年级
     */
    private String grade;
}
