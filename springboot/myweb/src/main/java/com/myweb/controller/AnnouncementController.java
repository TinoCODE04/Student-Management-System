package com.myweb.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.myweb.common.Result;
import com.myweb.entity.Announcement;
import com.myweb.entity.Notification;
import com.myweb.entity.Student;
import com.myweb.entity.Teacher;
import com.myweb.service.AnnouncementService;
import com.myweb.service.NotificationService;
import com.myweb.service.StudentService;
import com.myweb.service.TeacherService;
import com.myweb.websocket.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 系统公告Controller
 */
@RestController
@RequestMapping("/api/announcement")
public class AnnouncementController {
    
    @Autowired
    private AnnouncementService announcementService;
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private StudentService studentService;
    
    @Autowired
    private TeacherService teacherService;
    
    @Autowired
    private WebSocketServer webSocketServer;
    
    /**
     * 分页查询公告(管理员)
     */
    @GetMapping("/page")
    public Result<Page<Announcement>> page(@RequestParam(defaultValue = "1") int page,
                                           @RequestParam(defaultValue = "10") int size,
                                           @RequestAttribute(value = "role", required = false) String role) {
        if (!"admin".equals(role)) {
            return Result.forbidden("无权限访问");
        }
        
        Page<Announcement> pageResult = announcementService.getAnnouncementPage(page, size);
        return Result.success(pageResult);
    }
    
    /**
     * 发布公告(管理员)
     */
    @PostMapping
    public Result<String> publish(@RequestBody Announcement announcement,
                                  @RequestAttribute(value = "userId", required = false) Long userId,
                                  @RequestAttribute(value = "username", required = false) String username,
                                  @RequestAttribute(value = "role", required = false) String role) {
        if (!"admin".equals(role)) {
            return Result.forbidden("无权限操作");
        }
        
        // 设置发布者信息
        announcement.setPublisherId(userId);
        announcement.setPublisherName(username != null ? username : "管理员");
        announcement.setPublisherType("admin");
        
        boolean success = announcementService.publishAnnouncement(announcement);
        
        if (success) {
            // 创建通知推送给目标用户
            createNotificationsForAnnouncement(announcement);
            return Result.success("发布成功");
        }
        
        return Result.error("发布失败");
    }
    
    /**
     * 更新公告(管理员)
     */
    @PutMapping("/{id}")
    public Result<String> update(@PathVariable Long id,
                                 @RequestBody Announcement announcement,
                                 @RequestAttribute(value = "role", required = false) String role) {
        if (!"admin".equals(role)) {
            return Result.forbidden("无权限操作");
        }
        
        announcement.setId(id);
        boolean success = announcementService.updateAnnouncement(announcement);
        
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }
    
    /**
     * 删除公告(管理员)
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id,
                                 @RequestAttribute(value = "role", required = false) String role) {
        if (!"admin".equals(role)) {
            return Result.forbidden("无权限操作");
        }
        
        // 1. 删除公告
        boolean success = announcementService.deleteAnnouncement(id);
        
        if (success) {
            // 2. 删除所有相关的通知
            deleteRelatedNotifications(id);
            return Result.success("删除成功");
        }
        
        return Result.error("删除失败");
    }
    
    /**
     * 删除与公告相关的所有通知
     */
    private void deleteRelatedNotifications(Long announcementId) {
        try {
            // 删除所有 type="announcement" 且 relatedId=公告ID 的通知
            notificationService.lambdaUpdate()
                .eq(Notification::getType, "announcement")
                .eq(Notification::getRelatedId, announcementId)
                .remove();
            
            System.out.println("✅ 已删除公告 ID=" + announcementId + " 的所有相关通知");
        } catch (Exception e) {
            System.err.println("❌ 删除通知失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 为公告创建通知并推送
     */
    private void createNotificationsForAnnouncement(Announcement announcement) {
        try {
            String targetRole = announcement.getTargetRole();
            
            // 1. 根据目标角色查询用户并创建通知记录
            if ("all".equals(targetRole) || "student".equals(targetRole)) {
                // 为所有学生创建通知
                List<Student> students = studentService.list();
                for (Student student : students) {
                    createNotificationForUser(
                        student.getId(), 
                        "student", 
                        announcement.getTitle(), 
                        announcement.getContent(),
                        announcement.getId()  // 传入公告ID
                    );
                }
                System.out.println("✅ 已为 " + students.size() + " 个学生创建通知");
            }
            
            if ("all".equals(targetRole) || "teacher".equals(targetRole)) {
                // 为所有教师创建通知
                List<Teacher> teachers = teacherService.list();
                for (Teacher teacher : teachers) {
                    createNotificationForUser(
                        teacher.getId(), 
                        "teacher", 
                        announcement.getTitle(), 
                        announcement.getContent(),
                        announcement.getId()  // 传入公告ID
                    );
                }
                System.out.println("✅ 已为 " + teachers.size() + " 个教师创建通知");
            }
            
            // 2. 通过WebSocket广播公告给所有在线用户
            Notification wsNotification = new Notification();
            wsNotification.setTitle(announcement.getTitle());
            wsNotification.setContent(announcement.getContent());
            wsNotification.setType("announcement");
            wsNotification.setRelatedId(announcement.getId());  // 设置关联ID
            wsNotification.setCreateTime(LocalDateTime.now());
            
            webSocketServer.sendNotification(wsNotification);
            
            System.out.println("📢 公告已推送: " + announcement.getTitle() + " -> " + targetRole);
            
        } catch (Exception e) {
            System.err.println("❌ 公告推送失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 为单个用户创建通知记录
     */
    private void createNotificationForUser(Long userId, String userType, String title, String content, Long announcementId) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setUserType(userType);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType("announcement");
        notification.setRelatedId(announcementId);  // 关联公告ID
        notification.setStatus(0); // 未读
        notification.setCreateTime(LocalDateTime.now());
        notification.setUpdateTime(LocalDateTime.now());
        
        notificationService.save(notification);
    }
}
