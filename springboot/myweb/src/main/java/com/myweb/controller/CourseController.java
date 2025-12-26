package com.myweb.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.myweb.common.Result;
import com.myweb.dto.CourseNotificationDTO;
import com.myweb.dto.CourseQueryDTO;
import com.myweb.dto.NotificationMessageDTO;
import com.myweb.entity.Course;
import com.myweb.entity.CourseSelection;
import com.myweb.entity.Student;
import com.myweb.entity.Teacher;
import com.myweb.service.*;
import com.myweb.websocket.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 课程Controller
 */
@RestController
@RequestMapping("/api/course")
@Slf4j
public class CourseController {
    
    @Autowired
    private CourseService courseService;
    
    @Autowired
    private TeacherService teacherService;
    
    @Autowired
    private CourseSelectionService courseSelectionService;
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private MessageProducerService messageProducerService;
    
    @Autowired
    private StudentService studentService;
    
    @Autowired
    private WebSocketServer webSocketServer;
    
    @Autowired
    private CourseSearchService courseSearchService;
    
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
    
    /**
     * 教师发送课程通知给选课学生
     * 支持站内信（WebSocket）+ 短信（RabbitMQ）+ 邮件
     */
    @PostMapping("/notify")
    public Result<Map<String, Object>> sendCourseNotification(
            @Valid @RequestBody CourseNotificationDTO notificationDTO,
            @RequestAttribute("userId") Long userId,
            @RequestAttribute("role") String role) {
        
        // 只有教师可以发送课程通知
        if (!"teacher".equals(role)) {
            return Result.forbidden("只有教师可以发送课程通知");
        }
        
        try {
            // 1. 验证课程是否存在且属于该教师
            Course course = courseService.getById(notificationDTO.getCourseId());
            if (course == null) {
                return Result.error("课程不存在");
            }
            
            if (!course.getTeacherId().equals(userId)) {
                return Result.forbidden("您只能向自己的课程发送通知");
            }
            
            // 2. 获取该课程的所有选课学生（不包括已退选的）
            LambdaQueryWrapper<CourseSelection> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(CourseSelection::getCourseId, notificationDTO.getCourseId())
                   .in(CourseSelection::getStatus, Arrays.asList("pending", "studying", "completed"));
            List<CourseSelection> selections = courseSelectionService.list(wrapper);
            
            if (selections.isEmpty()) {
                return Result.error("该课程暂无选课学生");
            }
            
            int successCount = 0;
            int smsCount = 0;
            int emailCount = 0;
            
            // 3. 向每个学生发送通知
            for (CourseSelection selection : selections) {
                Long studentId = selection.getStudentId();
                Student student = studentService.getById(studentId);
                
                if (student == null) {
                    continue;
                }
                
                // 3.1 发送站内信通知（WebSocket + 数据库）
                try {
                    notificationService.createNotification(
                            studentId,
                            "student",
                            notificationDTO.getTitle(),
                            notificationDTO.getContent(),
                            "course",
                            course.getId(),
                            "/student/courses"
                    );
                    
                    // WebSocket实时推送
                    Map<String, Object> wsMessage = new HashMap<>();
                    wsMessage.put("type", "course_notification");
                    wsMessage.put("title", notificationDTO.getTitle());
                    wsMessage.put("content", notificationDTO.getContent());
                    wsMessage.put("courseId", course.getId());
                    wsMessage.put("courseName", course.getCourseName());
                    webSocketServer.sendMessageToUser(String.valueOf(studentId), wsMessage);
                    
                    successCount++;
                } catch (Exception e) {
                    log.error("Failed to send notification to student {}", studentId, e);
                }
                
                // 3.2 发送短信通知（如果开启）
                if (Boolean.TRUE.equals(notificationDTO.getSendSms()) && student.getPhone() != null) {
                    try {
                        NotificationMessageDTO smsMessage = new NotificationMessageDTO();
                        smsMessage.setUserId(studentId);
                        smsMessage.setUserType("student");
                        smsMessage.setUserName(student.getName());
                        smsMessage.setPhone(student.getPhone());
                        smsMessage.setTitle(notificationDTO.getTitle());
                        smsMessage.setContent(String.format("【学生管理系统】课程《%s》通知：%s",
                                course.getCourseName(), notificationDTO.getContent()));
                        smsMessage.setRelatedType("course");
                        smsMessage.setRelatedId(course.getId());
                        
                        messageProducerService.sendSmsNotification(smsMessage);
                        smsCount++;
                    } catch (Exception e) {
                        log.error("Failed to send SMS to student {}", studentId, e);
                    }
                }
                
                // 3.3 发送邮件通知（如果开启）
                if (Boolean.TRUE.equals(notificationDTO.getSendEmail()) && student.getEmail() != null) {
                    try {
                        NotificationMessageDTO emailMessage = new NotificationMessageDTO();
                        emailMessage.setUserId(studentId);
                        emailMessage.setUserType("student");
                        emailMessage.setUserName(student.getName());
                        emailMessage.setEmail(student.getEmail());
                        emailMessage.setTitle(notificationDTO.getTitle());
                        emailMessage.setContent(notificationDTO.getContent());
                        emailMessage.setRelatedType("course");
                        emailMessage.setRelatedId(course.getId());
                        
                        messageProducerService.sendEmailNotification(emailMessage);
                        emailCount++;
                    } catch (Exception e) {
                        log.error("Failed to send Email to student {}", studentId, e);
                    }
                }
            }
            
            // 4. 返回发送结果统计
            Map<String, Object> result = new HashMap<>();
            result.put("totalStudents", selections.size());
            result.put("notificationsSent", successCount);
            result.put("smsSent", smsCount);
            result.put("emailSent", emailCount);
            
            log.info("Course notification sent: courseId={}, students={}, notifications={}, sms={}, email={}",
                    notificationDTO.getCourseId(), selections.size(), successCount, smsCount, emailCount);
            
            return Result.success("通知发送成功", result);
            
        } catch (Exception e) {
            log.error("Error sending course notification", e);
            return Result.error("通知发送失败: " + e.getMessage());
        }
    }
    
    /**
     * 搜索课程（Elasticsearch）
     */
    @GetMapping("/search")
    public Result<?> searchCourses(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long collegeId,
            @RequestParam(required = false) Integer minCredit,
            @RequestParam(required = false) Integer maxCredit,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        
        try {
            org.springframework.data.domain.Page<com.myweb.document.CourseDocument> page = 
                courseSearchService.searchCourses(keyword, collegeId, minCredit, maxCredit, pageNum, pageSize);
            return Result.success(page);
        } catch (Exception e) {
            log.error("搜索课程失败", e);
            return Result.error("搜索失败: " + e.getMessage());
        }
    }
    
    /**
     * 重建课程索引（管理员/教师权限）
     */
    @PostMapping("/rebuild-index")
    public Result<?> rebuildCourseIndex(@RequestAttribute(value = "role", required = false) String role) {
        // 权限检查
        if (!"admin".equals(role) && !"teacher".equals(role)) {
            return Result.error("无权限执行此操作");
        }
        
        try {
            String message = courseSearchService.rebuildIndex();
            return Result.success(message);
        } catch (Exception e) {
            log.error("重建课程索引失败", e);
            return Result.error("重建索引失败: " + e.getMessage());
        }
    }
}


