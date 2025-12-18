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
@RequestMapping("/api/course")
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
    public Result<Page<Course>> page(CourseQueryDTO queryDTO,
                                     @RequestAttribute(value = "userId", required = false) Long userId,
                                     @RequestAttribute(value = "role", required = false) String role) {
        // 如果是教师角色，只能查看自己的课程
        if ("teacher".equals(role) && userId != null) {
            queryDTO.setTeacherId(userId);
        }
        
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
     * 新增课程（管理员和教师）
     */
    @PostMapping
    public Result<Void> add(@RequestBody Course course,
                            @RequestAttribute("userId") Long userId,
                            @RequestAttribute("role") String role) {
        if (!"teacher".equals(role) && !"admin".equals(role)) {
            return Result.forbidden("无权限操作");
        }
        // 管理员可以指定任何教师，教师只能设置自己为授课教师
        if ("teacher".equals(role)) {
            course.setTeacherId(userId);
        }
        course.setSelectedCount(0);
        courseService.save(course);
        return Result.success();
    }
    
    /**
     * 更新课程（管理员和教师）
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, 
                               @RequestBody Course course,
                               @RequestAttribute("userId") Long userId,
                               @RequestAttribute("role") String role) {
        if (!"teacher".equals(role) && !"admin".equals(role)) {
            return Result.forbidden("无权限操作");
        }
        
        // 验证课程是否存在
        Course existingCourse = courseService.getById(id);
        if (existingCourse == null) {
            return Result.error("课程不存在");
        }
        
        // 教师只能修改自己的课程，管理员可以修改所有课程
        if ("teacher".equals(role) && !existingCourse.getTeacherId().equals(userId)) {
            return Result.forbidden("您只能修改自己的课程");
        }
        
        course.setId(id);
        // 教师不能修改授课教师，管理员可以
        if ("teacher".equals(role)) {
            course.setTeacherId(userId);
        }
        courseService.updateById(course);
        return Result.success();
    }
    
    /**
     * 删除课程（管理员和教师）
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id,
                               @RequestAttribute("userId") Long userId,
                               @RequestAttribute("role") String role) {
        if (!"teacher".equals(role) && !"admin".equals(role)) {
            return Result.forbidden("无权限操作");
        }
        
        // 验证课程是否存在
        Course existingCourse = courseService.getById(id);
        if (existingCourse == null) {
            return Result.error("课程不存在");
        }
        
        // 教师只能删除自己的课程，管理员可以删除所有课程
        if ("teacher".equals(role) && !existingCourse.getTeacherId().equals(userId)) {
            return Result.forbidden("您只能删除自己的课程");
        }
        
        courseService.removeById(id);
        return Result.success();
    }
}
