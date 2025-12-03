package com.myweb.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.myweb.entity.Major;
import java.util.List;

/**
 * 专业Service接口
 */
public interface MajorService extends IService<Major> {
    
    /**
     * 根据学院ID查询专业列表
     */
    List<Major> listByCollegeId(Long collegeId);
}
