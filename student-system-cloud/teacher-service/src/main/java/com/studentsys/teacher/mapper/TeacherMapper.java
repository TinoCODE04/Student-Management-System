package com.studentsys.teacher.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.studentsys.common.entity.Teacher;
import org.apache.ibatis.annotations.Mapper;

/**
 * 教师Mapper
 */
@Mapper
public interface TeacherMapper extends BaseMapper<Teacher> {
}
