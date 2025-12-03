package com.myweb.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.myweb.entity.Teacher;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 教师Mapper接口
 */
@Mapper
public interface TeacherMapper extends BaseMapper<Teacher> {
    
    /**
     * 根据用户名查询教师
     */
    @Select("SELECT * FROM teacher WHERE username = #{username}")
    Teacher selectByUsername(String username);
}
