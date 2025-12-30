package com.studentsys.course.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.studentsys.common.dto.CourseQueryDTO;
import com.studentsys.common.entity.Course;
import com.studentsys.common.entity.Teacher;
import com.studentsys.common.result.Result;
import com.studentsys.course.feign.TeacherFeignClient;
import com.studentsys.course.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 课程Controller
 */
@RestController
@RequestMapping("/api/course")
@RequiredArgsConstructor
public class CourseController {
    
    private final CourseService courseService;
    private final TeacherFeignClient teacherFeignClient;
    
    /**
     * 分页查询课程列表
     */
    @GetMapping("/page")
    public Result<Page<Course>> page(CourseQueryDTO queryDTO) {
        Page<Course> page = courseService.pageQuery(queryDTO);
        // 填充教师信息
        page.getRecords().forEach(this::fillTeacherInfo);
        return Result.success(page);
    }
    
    /**
     * 获取所有课程列表
     */
    @GetMapping("/list")
    public Result<List<Course>> list() {
        List<Course> courses = courseService.list();
        courses.forEach(this::fillTeacherInfo);
        return Result.success(courses);
    }
    
    /**
     * 根据ID获取课程
     */
    @GetMapping("/{id}")
    public Result<Course> getById(@PathVariable Long id) {
        Course course = courseService.getById(id);
        if (course != null) {
            fillTeacherInfo(course);
        }
        return Result.success(course);
    }
    
    /**
     * 新增课程
     */
    @PostMapping
    public Result<Void> add(@RequestBody Course course,
                            @RequestHeader(value = "X-Role", required = false) String role) {
        // 只有教师可以添加课程
        if (!"teacher".equals(role)) {
            return Result.forbidden("无权限操作");
        }
        
        courseService.save(course);
        return Result.success();
    }
    
    /**
     * 更新课程
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id,
                               @RequestBody Course course,
                               @RequestHeader(value = "X-Role", required = false) String role) {
        // 只有教师可以修改课程
        if (!"teacher".equals(role)) {
            return Result.forbidden("无权限操作");
        }
        
        course.setId(id);
        courseService.updateById(course);
        return Result.success();
    }
    
    /**
     * 删除课程
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id,
                               @RequestHeader(value = "X-Role", required = false) String role) {
        // 只有教师可以删除课程
        if (!"teacher".equals(role)) {
            return Result.forbidden("无权限操作");
        }
        
        courseService.removeById(id);
        return Result.success();
    }
    
    /**
     * 获取教师的课程列表
     */
    @GetMapping("/teacher/{teacherId}")
    public Result<List<Course>> listByTeacher(@PathVariable Long teacherId) {
        List<Course> courses = courseService.listByTeacherId(teacherId);
        return Result.success(courses);
    }
    
    /**
     * 获取当前教师的课程列表
     */
    @GetMapping("/my")
    public Result<List<Course>> myCourses(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                          @RequestHeader(value = "X-Role", required = false) String role) {
        if (!"teacher".equals(role)) {
            return Result.forbidden("此接口仅供教师使用");
        }
        
        List<Course> courses = courseService.listByTeacherId(userId);
        return Result.success(courses);
    }
    
    /**
     * 统计学院课程数量（供其他服务调用）
     */
    @GetMapping("/count")
    public Result<Long> countByCollegeId(@RequestParam("collegeId") Long collegeId) {
        return Result.success(courseService.countByCollegeId(collegeId));
    }
    
    /**
     * 填充教师信息
     */
    private void fillTeacherInfo(Course course) {
        if (course.getTeacherId() != null) {
            try {
                Result<Teacher> result = teacherFeignClient.getById(course.getTeacherId());
                if (result.getCode() == 200 && result.getData() != null) {
                    course.setTeacher(result.getData());
                }
            } catch (Exception e) {
                // 忽略Feign调用异常
            }
        }
    }
}
