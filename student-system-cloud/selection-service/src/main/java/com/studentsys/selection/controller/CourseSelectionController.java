package com.studentsys.selection.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.studentsys.common.entity.Course;
import com.studentsys.common.entity.CourseSelection;
import com.studentsys.common.result.Result;
import com.studentsys.selection.feign.CourseFeignClient;
import com.studentsys.selection.service.CourseSelectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 选课Controller
 */
@RestController
@RequestMapping("/api/selection")
@RequiredArgsConstructor
public class CourseSelectionController {
    
    private final CourseSelectionService selectionService;
    private final CourseFeignClient courseFeignClient;
    
    /**
     * 分页查询选课记录（教师视角）
     */
    @GetMapping("/page")
    public Result<Page<CourseSelection>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Integer status,
            @RequestHeader(value = "X-Role", required = false) String role) {
        
        Page<CourseSelection> page = selectionService.pageQuery(pageNum, pageSize, courseId, studentId, status);
        return Result.success(page);
    }
    
    /**
     * 学生选课
     */
    @PostMapping("/select/{courseId}")
    public Result<Void> selectCourse(@PathVariable Long courseId,
                                     @RequestHeader(value = "X-User-Id", required = false) Long userId,
                                     @RequestHeader(value = "X-Role", required = false) String role) {
        if (!"student".equals(role)) {
            return Result.forbidden("只有学生可以选课");
        }
        
        // 检查是否已选
        if (selectionService.hasSelected(userId, courseId)) {
            return Result.error("已经选择过该课程");
        }
        
        boolean success = selectionService.selectCourse(userId, courseId);
        if (!success) {
            return Result.error("选课失败，课程可能已满或不存在");
        }
        return Result.success("选课成功", null);
    }
    
    /**
     * 学生退课
     */
    @DeleteMapping("/drop/{courseId}")
    public Result<Void> dropCourse(@PathVariable Long courseId,
                                   @RequestHeader(value = "X-User-Id", required = false) Long userId,
                                   @RequestHeader(value = "X-Role", required = false) String role) {
        if (!"student".equals(role)) {
            return Result.forbidden("只有学生可以退课");
        }
        
        boolean success = selectionService.dropCourse(userId, courseId);
        if (!success) {
            return Result.error("退课失败，可能未选该课程或已有成绩");
        }
        return Result.success("退课成功", null);
    }
    
    /**
     * 学生重选课程
     */
    @PostMapping("/reselect/{courseId}")
    public Result<Void> reselectCourse(@PathVariable Long courseId,
                                       @RequestHeader(value = "X-User-Id", required = false) Long userId,
                                       @RequestHeader(value = "X-Role", required = false) String role) {
        if (!"student".equals(role)) {
            return Result.forbidden("只有学生可以重选");
        }
        
        boolean success = selectionService.reselectCourse(userId, courseId);
        if (!success) {
            return Result.error("重选失败，课程可能已满");
        }
        return Result.success("重选成功", null);
    }
    
    /**
     * 获取学生已选课程
     */
    @GetMapping("/my")
    public Result<List<CourseSelection>> myCourses(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                                   @RequestHeader(value = "X-Role", required = false) String role) {
        if (!"student".equals(role)) {
            return Result.forbidden("此接口仅供学生使用");
        }
        
        List<CourseSelection> list = selectionService.listByStudentId(userId);
        return Result.success(list);
    }
    
    /**
     * 获取学生成绩
     */
    @GetMapping("/grades")
    public Result<List<CourseSelection>> myGrades(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                                  @RequestHeader(value = "X-Role", required = false) String role) {
        if (!"student".equals(role)) {
            return Result.forbidden("此接口仅供学生使用");
        }
        
        List<CourseSelection> list = selectionService.getStudentGrades(userId);
        return Result.success(list);
    }
    
    /**
     * 获取课程已选学生列表（教师视角）
     */
    @GetMapping("/course/{courseId}")
    public Result<List<CourseSelection>> listByCourse(@PathVariable Long courseId,
                                                      @RequestHeader(value = "X-User-Id", required = false) Long userId,
                                                      @RequestHeader(value = "X-Role", required = false) String role) {
        if (!"teacher".equals(role)) {
            return Result.forbidden("此接口仅供教师使用");
        }
        
        // 验证是否是该课程的任课教师
        Result<Course> courseResult = courseFeignClient.getById(courseId);
        if (courseResult.getCode() != 200 || courseResult.getData() == null) {
            return Result.error("课程不存在");
        }
        
        Course course = courseResult.getData();
        if (!userId.equals(course.getTeacherId())) {
            return Result.forbidden("只能查看自己任课的学生");
        }
        
        List<CourseSelection> list = selectionService.listByCourseId(courseId);
        return Result.success(list);
    }
    
    /**
     * 录入成绩
     */
    @PutMapping("/score/{selectionId}")
    public Result<Void> enterScore(@PathVariable Long selectionId,
                                   @RequestBody Map<String, Double> body,
                                   @RequestHeader(value = "X-User-Id", required = false) Long userId,
                                   @RequestHeader(value = "X-Role", required = false) String role) {
        if (!"teacher".equals(role)) {
            return Result.forbidden("只有教师可以录入成绩");
        }
        
        Double score = body.get("score");
        if (score == null || score < 0 || score > 100) {
            return Result.error("成绩必须在0-100之间");
        }
        
        // 验证是否是该课程的任课教师
        CourseSelection selection = selectionService.getById(selectionId);
        if (selection == null) {
            return Result.error("选课记录不存在");
        }
        
        Result<Course> courseResult = courseFeignClient.getById(selection.getCourseId());
        if (courseResult.getCode() != 200 || courseResult.getData() == null) {
            return Result.error("课程不存在");
        }
        
        Course course = courseResult.getData();
        if (!userId.equals(course.getTeacherId())) {
            return Result.forbidden("只能录入自己任课课程的成绩");
        }
        
        boolean success = selectionService.enterScore(selectionId, score);
        if (!success) {
            return Result.error("录入失败");
        }
        return Result.success("成绩录入成功", null);
    }
    
    /**
     * 批量录入成绩
     */
    @PutMapping("/score/batch")
    public Result<Void> batchEnterScore(@RequestBody Map<String, Object> body,
                                        @RequestHeader(value = "X-User-Id", required = false) Long userId,
                                        @RequestHeader(value = "X-Role", required = false) String role) {
        if (!"teacher".equals(role)) {
            return Result.forbidden("只有教师可以录入成绩");
        }
        
        @SuppressWarnings("unchecked")
        List<Long> selectionIds = (List<Long>) body.get("selectionIds");
        @SuppressWarnings("unchecked")
        List<Double> scores = (List<Double>) body.get("scores");
        
        if (selectionIds == null || scores == null || selectionIds.size() != scores.size()) {
            return Result.error("参数错误");
        }
        
        boolean success = selectionService.batchEnterScore(selectionIds, scores);
        if (!success) {
            return Result.error("批量录入失败");
        }
        return Result.success("批量录入成功", null);
    }
}
