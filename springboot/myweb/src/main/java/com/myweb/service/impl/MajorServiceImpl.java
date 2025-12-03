package com.myweb.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.myweb.entity.Major;
import com.myweb.mapper.MajorMapper;
import com.myweb.service.MajorService;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 专业Service实现类
 */
@Service
public class MajorServiceImpl extends ServiceImpl<MajorMapper, Major> implements MajorService {
    
    @Override
    public List<Major> listByCollegeId(Long collegeId) {
        return baseMapper.selectByCollegeId(collegeId);
    }
}
