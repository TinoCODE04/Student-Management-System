package com.myweb.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.myweb.entity.College;
import org.apache.ibatis.annotations.Mapper;

/**
 * 学院Mapper接口
 */
@Mapper
public interface CollegeMapper extends BaseMapper<College> {
}
