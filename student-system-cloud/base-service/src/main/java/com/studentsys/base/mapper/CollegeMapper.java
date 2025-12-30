package com.studentsys.base.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.studentsys.common.entity.College;
import org.apache.ibatis.annotations.Mapper;

/**
 * 学院Mapper
 */
@Mapper
public interface CollegeMapper extends BaseMapper<College> {
}
