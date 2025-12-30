package com.studentsys.base.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.studentsys.common.entity.Major;
import org.apache.ibatis.annotations.Mapper;

/**
 * 专业Mapper
 */
@Mapper
public interface MajorMapper extends BaseMapper<Major> {
}
