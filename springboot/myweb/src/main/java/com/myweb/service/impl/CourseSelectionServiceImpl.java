package com.myweb.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.myweb.entity.Course;
import com.myweb.entity.CourseSelection;
import com.myweb.entity.Student;
import com.myweb.mapper.CourseMapper;
import com.myweb.mapper.CourseSelectionMapper;
import com.myweb.service.CourseSelectionService;
import com.myweb.service.NotificationService;
import com.myweb.service.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 选课记录Service实现类
 */
@Service
@Slf4j
public class CourseSelectionServiceImpl extends ServiceImpl<CourseSelectionMapper, CourseSelection> implements CourseSelectionService {
    
    @Autowired
    private CourseMapper courseMapper;
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private StudentService studentService;
    
    @Override
    public List<CourseSelection> listByStudentId(Long studentId) {
        return baseMapper.selectByStudentId(studentId);
    }
    
    @Override
    public List<CourseSelection> listByCourseId(Long courseId) {
        return baseMapper.selectByCourseId(courseId);
    }
    
    @Override
    @Transactional
    public boolean selectCourse(Long studentId, Long courseId) {
        // 检查是否已选（不包括已退选的）
        CourseSelection existing = baseMapper.selectByStudentAndCourse(studentId, courseId);
        if (existing != null) {
            return false; // 已经选过且未退选
        }
        
        // 检查课程是否存在且未满
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            log.warn("Course not found: {}", courseId);
            return false;
        }
        
        if (course.getSelectedCount() >= course.getMaxStudents()) {
            log.warn("Course is full: courseId={}, selectedCount={}, maxStudents={}", 
                courseId, course.getSelectedCount(), course.getMaxStudents());
            return false;
        }
        
        // 查询是否有历史退选记录
        CourseSelection historicalSelection = baseMapper.selectByStudentAndCourseAll(studentId, courseId);
        
        int result;
        if (historicalSelection != null && "dropped".equals(historicalSelection.getStatus())) {
            // 如果有退选记录，则更新状态为pending
            historicalSelection.setStatus("pending");
            historicalSelection.setSelectTime(LocalDateTime.now());
            historicalSelection.setScore(null); // 清空之前的成绩
            result = baseMapper.updateById(historicalSelection);
            log.info("Student {} re-selected course {}", studentId, courseId);
        } else {
            // 创建新的选课记录
            CourseSelection selection = new CourseSelection();
            selection.setStudentId(studentId);
            selection.setCourseId(courseId);
            selection.setStatus("pending");  // 新选课默认为待开课状态（可退课）
            selection.setSelectTime(LocalDateTime.now());
            result = baseMapper.insert(selection);
            log.info("Student {} selected course {}", studentId, courseId);
        }
        
        // 更新已选人数
        if (result > 0) {
            course.setSelectedCount(course.getSelectedCount() + 1);
            courseMapper.updateById(course);
            log.info("Course {} selected count updated to {}", courseId, course.getSelectedCount());
            
            // 发送选课成功通知
            log.info("🔔 准备发送选课通知给学生 studentId={}, courseId={}, courseName={}", 
                    studentId, courseId, course.getCourseName());
            try {
                if (notificationService == null) {
                    log.error("❌ NotificationService is NULL!");
                } else {
                    log.info("✅ NotificationService is available, calling createNotification...");
                    notificationService.createNotification(
                        studentId, 
                        "student", 
                        "选课成功", 
                        "您已成功选择课程：" + course.getCourseName(),
                        "selection",
                        courseId,
                        "/student/courses"
                    );
                    log.info("✅ Notification creation method called successfully");
                }
            } catch (Exception e) {
                log.error("❌ Failed to create notification for student {}: {}", studentId, e.getMessage(), e);
                // 通知失败不影响选课
            }
        }
        
        return result > 0;
    }
    
    @Override
    @Transactional
    public boolean dropCourse(Long studentId, Long courseId) {
        // 查询当前有效的选课记录（不包括已退选的）
        CourseSelection selection = baseMapper.selectByStudentAndCourse(studentId, courseId);
        // 只有 pending(待开课) 状态才能退课，studying(学习中) 和 completed(已完成) 不能退
        if (selection == null || !"pending".equals(selection.getStatus())) {
            log.warn("Cannot drop course: studentId={}, courseId={}, selection={}", 
                studentId, courseId, selection == null ? "null" : selection.getStatus());
            return false;
        }
        
        // 获取课程信息（用于通知）
        Course course = courseMapper.selectById(courseId);
        String courseName = course != null ? course.getCourseName() : "课程";
        
        // 更新状态为已退选
        selection.setStatus("dropped");
        int result = baseMapper.updateById(selection);
        
        // 更新已选人数并发送通知
        if (result > 0) {
            if (course != null && course.getSelectedCount() > 0) {
                course.setSelectedCount(course.getSelectedCount() - 1);
                courseMapper.updateById(course);
                log.info("Student {} dropped course {}, selected count: {}", 
                    studentId, courseId, course.getSelectedCount());
            }
            
            // 发送退课成功通知
            log.info("🔔 准备发送退课通知给学生 studentId={}, courseId={}, courseName={}", 
                    studentId, courseId, courseName);
            try {
                if (notificationService == null) {
                    log.error("❌ NotificationService is NULL!");
                } else {
                    log.info("✅ NotificationService is available, calling createNotification for drop course...");
                    notificationService.createNotification(
                        studentId, 
                        "student", 
                        "退课成功", 
                        "您已成功退选课程：" + courseName,
                        "drop",
                        courseId,
                        "/student/courses"
                    );
                    log.info("✅ Drop course notification created successfully");
                }
            } catch (Exception e) {
                log.error("❌ Failed to create drop notification for student {}: {}", studentId, e.getMessage(), e);
                // 通知失败不影响退课
            }
        }
        
        return result > 0;
    }
    
    @Override
    @Transactional
    public boolean reselectCourse(Long studentId, Long courseId) {
        // 查找已退选的记录（需要查询所有状态）
        CourseSelection selection = baseMapper.selectByStudentAndCourseAll(studentId, courseId);
        if (selection == null || !"dropped".equals(selection.getStatus())) {
            return false;
        }
        
        // 检查课程是否已满
        Course course = courseMapper.selectById(courseId);
        if (course == null || course.getSelectedCount() >= course.getMaxStudents()) {
            return false;
        }
        
        String courseName = course.getCourseName();
        
        // 更新状态为待开课
        selection.setStatus("pending");
        selection.setSelectTime(LocalDateTime.now());
        int result = baseMapper.updateById(selection);
        
        // 更新已选人数并发送通知
        if (result > 0) {
            course.setSelectedCount(course.getSelectedCount() + 1);
            courseMapper.updateById(course);
            log.info("Student {} reselected course {}, selected count: {}", 
                    studentId, courseId, course.getSelectedCount());
            
            // 发送重新选课成功通知
            log.info("🔔 准备发送重选课程通知给学生 studentId={}, courseId={}, courseName={}", 
                    studentId, courseId, courseName);
            try {
                if (notificationService == null) {
                    log.error("❌ NotificationService is NULL!");
                } else {
                    log.info("✅ NotificationService is available, calling createNotification for reselect course...");
                    notificationService.createNotification(
                        studentId, 
                        "student", 
                        "重选成功", 
                        "您已成功重新选择课程：" + courseName,
                        "reselection",
                        courseId,
                        "/student/courses"
                    );
                    log.info("✅ Reselect course notification created successfully");
                }
            } catch (Exception e) {
                log.error("❌ Failed to create reselect notification for student {}: {}", studentId, e.getMessage(), e);
                // 通知失败不影响重选
            }
        }
        
        return result > 0;
    }
    
    @Override
    public boolean updateScore(Long studentId, Long courseId, Double score) {
        // 查询有效的选课记录（不包括已退选的）
        CourseSelection selection = baseMapper.selectByStudentAndCourse(studentId, courseId);
        if (selection == null) {
            log.warn("No valid course selection found for student {} and course {}", studentId, courseId);
            return false;
        }
        
        // 保存旧成绩，用于判断是新增还是修改
        BigDecimal oldScore = selection.getScore();
        boolean isUpdate = (oldScore != null);
        
        selection.setScore(BigDecimal.valueOf(score));
        selection.setStatus("completed");
        
        boolean success = baseMapper.updateById(selection) > 0;
        if (success) {
            log.info("Score updated for student {} in course {}: {}", studentId, courseId, score);
            
            // 获取课程和学生信息
            try {
                Course course = courseMapper.selectById(courseId);
                String courseName = course != null ? course.getCourseName() : "课程";
                Long teacherId = course != null ? course.getTeacherId() : null;
                
                Student student = studentService.getById(studentId);
                String studentName = student != null ? student.getName() : "学生";
                
                // === 1. 发送通知给学生 ===
                String studentTitle;
                String studentContent;
                String studentNotificationType;
                
                if (isUpdate) {
                    // 修改成绩
                    studentTitle = "成绩更新";
                    studentContent = String.format("您的课程《%s》成绩已更新：%.1f分 → %.1f分", 
                                          courseName, oldScore.doubleValue(), score);
                    studentNotificationType = "grade_update";
                    log.info("🔄 Grade updated for student {}: {} → {}", studentId, oldScore, score);
                } else {
                    // 新增成绩
                    studentTitle = "成绩发布";
                    studentContent = String.format("您的课程《%s》成绩已发布：%.1f分", courseName, score);
                    studentNotificationType = "grade";
                    log.info("🆕 New grade published for student {}: {}", studentId, score);
                }
                
                // 发送通知给学生
                notificationService.createNotification(
                    studentId,
                    "student",
                    studentTitle,
                    studentContent,
                    studentNotificationType,
                    courseId,
                    "/student/grades"
                );
                log.info("✅ Grade notification sent to student {}", studentId);
                
                // === 2. 发送操作确认通知给教师 ===
                if (teacherId != null) {
                    String teacherTitle;
                    String teacherContent;
                    String teacherNotificationType;
                    
                    if (isUpdate) {
                        // 修改成绩
                        teacherTitle = "成绩修改确认";
                        teacherContent = String.format("您已为学生【%s】修改课程《%s》成绩：%.1f分 → %.1f分", 
                                              studentName, courseName, oldScore.doubleValue(), score);
                        teacherNotificationType = "teacher_grade_update";
                    } else {
                        // 新增成绩
                        teacherTitle = "成绩录入确认";
                        teacherContent = String.format("您已为学生【%s】录入课程《%s》成绩：%.1f分", 
                                              studentName, courseName, score);
                        teacherNotificationType = "teacher_grade_add";
                    }
                    
                    // 发送通知给教师
                    notificationService.createNotification(
                        teacherId,
                        "teacher",
                        teacherTitle,
                        teacherContent,
                        teacherNotificationType,
                        courseId,
                        "/teacher/selections"
                    );
                    log.info("✅ Grade confirmation notification sent to teacher {}", teacherId);
                }
                
            } catch (Exception e) {
                log.error("❌ Failed to send grade notifications: {}", e.getMessage(), e);
                // 通知失败不影响成绩录入
            }
        }
        return success;
    }
}
