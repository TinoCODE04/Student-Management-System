package com.myweb.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.myweb.entity.Student;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 学生Mapper接口
 */
@Mapper
public interface StudentMapper extends BaseMapper<Student> {
    
    /**
     * 根据用户名查询学生
     */
    @Select("SELECT * FROM student WHERE username = #{username}")
    Student selectByUsername(String username);
    
    /**
     * 根据学号查询学生
     */
    @Select("SELECT * FROM student WHERE student_no = #{studentNo}")
    Student selectByStudentNo(String studentNo);
}
