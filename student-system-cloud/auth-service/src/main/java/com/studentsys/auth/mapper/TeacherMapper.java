package com.studentsys.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.studentsys.common.entity.Teacher;
import org.apache.ibatis.annotations.Mapper;

/**
 * 教师Mapper（用于认证查询）
 */
@Mapper
public interface TeacherMapper extends BaseMapper<Teacher> {
}
