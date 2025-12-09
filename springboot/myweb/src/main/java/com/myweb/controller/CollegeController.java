package com.myweb.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.myweb.common.Result;
import com.myweb.entity.College;
import com.myweb.entity.Course;
import com.myweb.entity.Major;
import com.myweb.entity.Student;
import com.myweb.service.CollegeService;
import com.myweb.service.CourseService;
import com.myweb.service.MajorService;
import com.myweb.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学院Controller
 */
@RestController
@RequestMapping("/api/colleges")
public class CollegeController {
    
    @Autowired
    private CollegeService collegeService;
    
    @Autowired
    private MajorService majorService;
    
    @Autowired
    private StudentService studentService;
    
    @Autowired
    private CourseService courseService;
    
    /**
     * 获取所有学院列表（包含统计信息）
     */
    @GetMapping("/list")
    public Result<List<College>> list() {
        List<College> colleges = collegeService.list();
        // 填充统计信息
        for (College college : colleges) {
            // 统计专业数量
            LambdaQueryWrapper<Major> majorWrapper = new LambdaQueryWrapper<>();
            majorWrapper.eq(Major::getCollegeId, college.getId());
            college.setMajorCount((int) majorService.count(majorWrapper));
            
            // 统计学生数量
            LambdaQueryWrapper<Student> studentWrapper = new LambdaQueryWrapper<>();
            studentWrapper.eq(Student::getCollegeId, college.getId());
            college.setStudentCount((int) studentService.count(studentWrapper));
            
            // 统计课程数量
            LambdaQueryWrapper<Course> courseWrapper = new LambdaQueryWrapper<>();
            courseWrapper.eq(Course::getCollegeId, college.getId());
            college.setCourseCount((int) courseService.count(courseWrapper));
        }
        return Result.success(colleges);
    }
    
    /**
     * 根据ID获取学院
     */
    @GetMapping("/{id}")
    public Result<College> getById(@PathVariable Long id) {
        return Result.success(collegeService.getById(id));
    }
    
    /**
     * 新增学院（仅教师）
     */
    @PostMapping
    public Result<Void> add(@RequestBody College college,
                            @RequestAttribute("role") String role) {
        if (!"teacher".equals(role)) {
            return Result.forbidden("无权限操作");
        }
        collegeService.save(college);
        return Result.success();
    }
    
    /**
     * 更新学院（仅教师）
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, 
                               @RequestBody College college,
                               @RequestAttribute("role") String role) {
        if (!"teacher".equals(role)) {
            return Result.forbidden("无权限操作");
        }
        college.setId(id);
        collegeService.updateById(college);
        return Result.success();
    }
    
    /**
     * 删除学院（仅教师）
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id,
                               @RequestAttribute("role") String role) {
        if (!"teacher".equals(role)) {
            return Result.forbidden("无权限操作");
        }
        collegeService.removeById(id);
        return Result.success();
    }
}
