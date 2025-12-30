package com.studentsys.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.studentsys.common.entity.Major;

import java.util.List;

/**
 * 专业Service接口
 */
public interface MajorService extends IService<Major> {
    
    /**
     * 根据学院ID查询专业列表
     */
    List<Major> listByCollegeId(Long collegeId);
    
    /**
     * 获取所有专业（包含统计信息）
     */
    List<Major> listWithStats();
    
    /**
     * 根据名称查询
     */
    Major getByName(String name);
    
    /**
     * 统计学院下专业数量
     */
    long countByCollegeId(Long collegeId);
}
