package com.myweb.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.myweb.common.Result;
import com.myweb.entity.College;
import com.myweb.entity.Major;
import com.myweb.entity.Student;
import com.myweb.entity.Teacher;
import com.myweb.service.CollegeService;
import com.myweb.service.MajorService;
import com.myweb.service.StudentService;
import com.myweb.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 专业Controller
 */
@RestController
@RequestMapping("/api/major")
public class MajorController {
    
    @Autowired
    private MajorService majorService;
    
    @Autowired
    private CollegeService collegeService;
    
    @Autowired
    private StudentService studentService;
    
    @Autowired
    private TeacherService teacherService;
    
    /**
     * 获取所有专业列表（包含统计信息）
     */
    @GetMapping("/list")
    public Result<List<Major>> list() {
        List<Major> majors = majorService.list();
        // 填充统计信息
        for (Major major : majors) {
            // 填充学院信息
            if (major.getCollegeId() != null) {
                College college = collegeService.getById(major.getCollegeId());
                if (college != null) {
                    major.setCollege(college);
                }
            }
            
            // 统计学生数量
            LambdaQueryWrapper<Student> studentWrapper = new LambdaQueryWrapper<>();
            studentWrapper.eq(Student::getMajorId, major.getId());
            major.setStudentCount((int) studentService.count(studentWrapper));
            
            // 统计教师数量（根据major_id）
            LambdaQueryWrapper<Teacher> teacherWrapper = new LambdaQueryWrapper<>();
            teacherWrapper.eq(Teacher::getMajorId, major.getId());
            major.setTeacherCount((int) teacherService.count(teacherWrapper));
        }
        return Result.success(majors);
    }
    
    /**
     * 根据学院ID获取专业列表
     */
    @GetMapping("/college/{collegeId}")
    public Result<List<Major>> listByCollege(@PathVariable Long collegeId) {
        return Result.success(majorService.listByCollegeId(collegeId));
    }
    
    /**
     * 根据ID获取专业
     */
    @GetMapping("/{id}")
    public Result<Major> getById(@PathVariable Long id) {
        return Result.success(majorService.getById(id));
    }
    
    /**
     * 新增专业（管理员和教师）
     */
    @PostMapping
    public Result<Void> add(@RequestBody Major major,
                            @RequestAttribute("role") String role) {
        if (!"teacher".equals(role) && !"admin".equals(role)) {
            return Result.forbidden("无权限操作");
        }
        majorService.save(major);
        return Result.success();
    }
    
    /**
     * 更新专业（管理员和教师）
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, 
                               @RequestBody Major major,
                               @RequestAttribute("role") String role) {
        if (!"teacher".equals(role) && !"admin".equals(role)) {
            return Result.forbidden("无权限操作");
        }
        major.setId(id);
        majorService.updateById(major);
        return Result.success();
    }
    
    /**
     * 删除专业（管理员和教师）
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id,
                               @RequestAttribute("role") String role) {
        if (!"teacher".equals(role) && !"admin".equals(role)) {
            return Result.forbidden("无权限操作");
        }
        majorService.removeById(id);
        return Result.success();
    }
}
