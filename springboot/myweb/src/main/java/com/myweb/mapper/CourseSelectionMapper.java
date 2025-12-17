package com.myweb.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.myweb.entity.CourseSelection;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

/**
 * 选课记录Mapper接口
 */
@Mapper
public interface CourseSelectionMapper extends BaseMapper<CourseSelection> {
    
    /**
     * 根据学生ID查询选课记录
     */
    @Select("SELECT * FROM course_selection WHERE student_id = #{studentId}")
    List<CourseSelection> selectByStudentId(Long studentId);
    
    /**
     * 根据课程ID查询选课记录
     */
    @Select("SELECT * FROM course_selection WHERE course_id = #{courseId}")
    List<CourseSelection> selectByCourseId(Long courseId);
    
    /**
     * 查询学生是否已选该课程（不包括已退选的）
     */
    @Select("SELECT * FROM course_selection WHERE student_id = #{studentId} AND course_id = #{courseId} AND status != 'dropped'")
    CourseSelection selectByStudentAndCourse(Long studentId, Long courseId);
    
    /**
     * 查询学生的该课程选课记录（包括所有状态）
     */
    @Select("SELECT * FROM course_selection WHERE student_id = #{studentId} AND course_id = #{courseId}")
    CourseSelection selectByStudentAndCourseAll(Long studentId, Long courseId);
}
