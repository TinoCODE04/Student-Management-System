package com.myweb.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.myweb.entity.College;
import com.myweb.mapper.CollegeMapper;
import com.myweb.service.CollegeService;
import org.springframework.stereotype.Service;

/**
 * 学院Service实现类
 */
@Service
public class CollegeServiceImpl extends ServiceImpl<CollegeMapper, College> implements CollegeService {
}
