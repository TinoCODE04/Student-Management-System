package com.studentsys.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.studentsys.common.entity.Student;
import org.apache.ibatis.annotations.Mapper;

/**
 * 学生Mapper（用于认证查询）
 */
@Mapper
public interface StudentMapper extends BaseMapper<Student> {
}
