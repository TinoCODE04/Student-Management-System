package com.myweb.controller;

import com.myweb.common.Result;
import com.myweb.entity.Course;
import com.myweb.entity.CourseSelection;
import com.myweb.entity.Student;
import com.myweb.entity.Teacher;
import com.myweb.service.CourseSelectionService;
import com.myweb.service.CourseService;
import com.myweb.service.StudentService;
import com.myweb.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 选课Controller
 */
@RestController
@RequestMapping("/api/selection")
public class CourseSelectionController {
    
    @Autowired
    private CourseSelectionService courseSelectionService;
    
    @Autowired
    private CourseService courseService;
    
    @Autowired
    private StudentService studentService;
    
    @Autowired
    private TeacherService teacherService;
    
    /**
     * 学生查看自己的选课记录
     */
    @GetMapping("/my")
    public Result<List<CourseSelection>> mySelections(@RequestAttribute("userId") Long userId,
                                                       @RequestAttribute("role") String role) {
        if (!"student".equals(role)) {
            return Result.forbidden("此接口仅供学生使用");
        }
        
        List<CourseSelection> selections = courseSelectionService.listByStudentId(userId);
        // 填充课程信息和教师信息
        for (CourseSelection selection : selections) {
            Course course = courseService.getById(selection.getCourseId());
            if (course != null && course.getTeacherId() != null) {
                Teacher teacher = teacherService.getById(course.getTeacherId());
                if (teacher != null) {
                    teacher.setPassword(null); // 隐藏密码
                }
                course.setTeacher(teacher);
            }
            selection.setCourse(course);
        }
        return Result.success(selections);
    }
    
    /**
     * 教师查看某课程的选课学生
     */
    @GetMapping("/course/{courseId}")
    public Result<List<CourseSelection>> getCourseSelections(@PathVariable Long courseId,
                                                              @RequestAttribute("role") String role) {
        if (!"teacher".equals(role)) {
            return Result.forbidden("此接口仅供教师使用");
        }
        
        List<CourseSelection> selections = courseSelectionService.listByCourseId(courseId);
        // 填充学生信息
        for (CourseSelection selection : selections) {
            Student student = studentService.getById(selection.getStudentId());
            if (student != null) {
                student.setPassword(null);
            }
            selection.setStudent(student);
        }
        return Result.success(selections);
    }
    
    /**
     * 学生选课
     */
    @PostMapping("/select/{courseId}")
    public Result<Void> selectCourse(@PathVariable Long courseId,
                                     @RequestAttribute("userId") Long userId,
                                     @RequestAttribute("role") String role) {
        if (!"student".equals(role)) {
            return Result.forbidden("此接口仅供学生使用");
        }
        
        boolean success = courseSelectionService.selectCourse(userId, courseId);
        if (!success) {
            return Result.error("选课失败，可能已选过该课程或课程已满");
        }
        return Result.success("选课成功", null);
    }
    
    /**
     * 学生退课（只能退待开课的课程，学习中的不能退）
     */
    @PostMapping("/drop/{courseId}")
    public Result<Void> dropCourse(@PathVariable Long courseId,
                                   @RequestAttribute("userId") Long userId,
                                   @RequestAttribute("role") String role) {
        if (!"student".equals(role)) {
            return Result.forbidden("此接口仅供学生使用");
        }
        
        boolean success = courseSelectionService.dropCourse(userId, courseId);
        if (!success) {
            return Result.error("退课失败，只有待开课的课程才能退选");
        }
        return Result.success("退课成功", null);
    }
    
    /**
     * 学生重新选课（退选后重选）
     */
    @PostMapping("/reselect/{courseId}")
    public Result<Void> reselectCourse(@PathVariable Long courseId,
                                       @RequestAttribute("userId") Long userId,
                                       @RequestAttribute("role") String role) {
        if (!"student".equals(role)) {
            return Result.forbidden("此接口仅供学生使用");
        }
        
        boolean success = courseSelectionService.reselectCourse(userId, courseId);
        if (!success) {
            return Result.error("重新选课失败，可能课程已满或状态不允许");
        }
        return Result.success("重新选课成功", null);
    }
    
    /**
     * 教师录入成绩
     */
    @PostMapping("/score")
    public Result<Void> updateScore(@RequestBody Map<String, Object> params,
                                    @RequestAttribute("role") String role) {
        if (!"teacher".equals(role)) {
            return Result.forbidden("此接口仅供教师使用");
        }
        
        Long studentId = Long.valueOf(params.get("studentId").toString());
        Long courseId = Long.valueOf(params.get("courseId").toString());
        Double score = Double.valueOf(params.get("score").toString());
        
        boolean success = courseSelectionService.updateScore(studentId, courseId, score);
        if (!success) {
            return Result.error("成绩录入失败");
        }
        return Result.success("成绩录入成功", null);
    }
}
