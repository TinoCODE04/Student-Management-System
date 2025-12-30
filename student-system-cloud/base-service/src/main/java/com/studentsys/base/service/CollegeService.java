package com.studentsys.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.studentsys.common.entity.College;

import java.util.List;

/**
 * 学院Service接口
 */
public interface CollegeService extends IService<College> {
    
    /**
     * 获取所有学院（包含统计信息）
     */
    List<College> listWithStats();
    
    /**
     * 根据名称查询
     */
    College getByName(String name);
}
