package com.myweb.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.myweb.common.Result;
import com.myweb.dto.CourseQueryDTO;
import com.myweb.entity.Course;
import com.myweb.entity.CourseSelection;
import com.myweb.entity.Teacher;
import com.myweb.service.CourseSelectionService;
import com.myweb.service.CourseService;
import com.myweb.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 课程Controller
 */
@RestController
@RequestMapping("/api/courses")
public class CourseController {
    
    @Autowired
    private CourseService courseService;
    
    @Autowired
    private TeacherService teacherService;
    
    @Autowired
    private CourseSelectionService courseSelectionService;
    
    /**
     * 计算课程的有效选课人数（不包含dropped状态）
     */
    private int countValidSelections(Long courseId) {
        LambdaQueryWrapper<CourseSelection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseSelection::getCourseId, courseId)
               .in(CourseSelection::getStatus, Arrays.asList("pending", "studying", "completed"));
        return (int) courseSelectionService.count(wrapper);
    }
    
    /**
     * 分页查询课程
     */
    @GetMapping("/page")
    public Result<Page<Course>> page(CourseQueryDTO queryDTO) {
        Page<Course> pageResult = courseService.pageQuery(queryDTO);
        // 填充教师信息和实时选课人数
        for (Course course : pageResult.getRecords()) {
            if (course.getTeacherId() != null) {
                Teacher teacher = teacherService.getById(course.getTeacherId());
                if (teacher != null) {
                    teacher.setPassword(null);
                }
                course.setTeacher(teacher);
            }
            // 实时计算有效选课人数
            course.setSelectedCount(countValidSelections(course.getId()));
        }
        return Result.success(pageResult);
    }
    
    /**
     * 获取所有课程列表（包含教师信息和实时选课人数）
     */
    @GetMapping("/list")
    public Result<List<Course>> list() {
        List<Course> courses = courseService.list();
        // 填充教师信息和实时选课人数
        for (Course course : courses) {
            if (course.getTeacherId() != null) {
                Teacher teacher = teacherService.getById(course.getTeacherId());
                if (teacher != null) {
                    teacher.setPassword(null);
                }
                course.setTeacher(teacher);
            }
            // 实时计算有效选课人数
            course.setSelectedCount(countValidSelections(course.getId()));
        }
        return Result.success(courses);
    }
    
    /**
     * 根据教师ID获取课程列表（教师查看自己的课程）
     */
    @GetMapping("/teacher")
    public Result<List<Course>> listByTeacher(@RequestAttribute("userId") Long userId,
                                              @RequestAttribute("role") String role) {
        if (!"teacher".equals(role)) {
            return Result.forbidden("此接口仅供教师使用");
        }
        return Result.success(courseService.listByTeacherId(userId));
    }
    
    /**
     * 根据ID获取课程
     */
    @GetMapping("/{id}")
    public Result<Course> getById(@PathVariable Long id) {
        return Result.success(courseService.getById(id));
    }
    
    /**
     * 新增课程（仅教师）
     */
    @PostMapping
    public Result<Void> add(@RequestBody Course course,
                            @RequestAttribute("role") String role) {
        if (!"teacher".equals(role)) {
            return Result.forbidden("无权限操作");
        }
        course.setSelectedCount(0);
        courseService.save(course);
        return Result.success();
    }
    
    /**
     * 更新课程（仅教师）
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, 
                               @RequestBody Course course,
                               @RequestAttribute("role") String role) {
        if (!"teacher".equals(role)) {
            return Result.forbidden("无权限操作");
        }
        course.setId(id);
        courseService.updateById(course);
        return Result.success();
    }
    
    /**
     * 删除课程（仅教师）
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id,
                               @RequestAttribute("role") String role) {
        if (!"teacher".equals(role)) {
            return Result.forbidden("无权限操作");
        }
        courseService.removeById(id);
        return Result.success();
    }
}
