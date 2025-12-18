package com.myweb.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.myweb.common.Result;
import com.myweb.entity.Notification;
import com.myweb.service.NotificationService;
import com.myweb.websocket.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 通知Controller
 */
@RestController
@RequestMapping("/api/notification")
public class NotificationController {
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private WebSocketServer webSocketServer;
    
    /**
     * 分页查询所有公告(管理员)
     */
    @GetMapping("/page")
    public Result<Page<Notification>> page(@RequestParam(defaultValue = "1") int page,
                                           @RequestParam(defaultValue = "10") int size,
                                           @RequestAttribute(value = "role", required = false) String role) {
        if (!"admin".equals(role)) {
            return Result.forbidden("无权限访问");
        }
        
        Page<Notification> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Notification::getCreateTime);
        Page<Notification> result = notificationService.page(pageObj, wrapper);
        return Result.success(result);
    }
    
    /**
     * 新增公告(管理员)
     */
    @PostMapping
    public Result<Void> add(@RequestBody Notification notification,
                           @RequestAttribute(value = "role", required = false) String role) {
        if (!"admin".equals(role)) {
            return Result.forbidden("无权限操作");
        }
        
        notification.setCreateTime(LocalDateTime.now());
        notification.setUpdateTime(LocalDateTime.now());
        notificationService.save(notification);
        
        // 通过WebSocket推送通知给在线用户
        webSocketServer.sendNotification(notification);
        
        return Result.success("发布成功", null);
    }
    
    /**
     * 更新公告(管理员)
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id,
                              @RequestBody Notification notification,
                              @RequestAttribute(value = "role", required = false) String role) {
        if (!"admin".equals(role)) {
            return Result.forbidden("无权限操作");
        }
        
        notification.setId(id);
        notification.setUpdateTime(LocalDateTime.now());
        notificationService.updateById(notification);
        
        return Result.success("更新成功", null);
    }
    
    /**
     * 删除公告(管理员)
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id,
                              @RequestAttribute(value = "role", required = false) String role) {
        if (!"admin".equals(role)) {
            return Result.forbidden("无权限操作");
        }
        
        notificationService.removeById(id);
        return Result.success("删除成功", null);
    }
    
    /**
     * 获取用户通知列表（按用户ID和用户类型过滤）
     */
    @GetMapping("/list")
    public Result<Page<Notification>> list(@RequestParam(defaultValue = "1") int pageNum,
                                            @RequestParam(defaultValue = "10") int pageSize,
                                            @RequestAttribute("userId") Long userId,
                                            @RequestAttribute("role") String role) {
        // 将 role (student/teacher) 转换为 userType
        String userType = role;
        Page<Notification> page = notificationService.getUserNotifications(userId, userType, pageNum, pageSize);
        return Result.success(page);
    }
    
    /**
     * 获取未读数量（按用户ID和用户类型过滤）
     */
    @GetMapping("/unread-count")
    public Result<Long> unreadCount(@RequestAttribute("userId") Long userId,
                                     @RequestAttribute("role") String role) {
        String userType = role;
        long count = notificationService.getUnreadCount(userId, userType);
        return Result.success(count);
    }
    
    /**
     * 标记为已读
     */
    @PostMapping("/read/{id}")
    public Result<Void> markAsRead(@PathVariable Long id,
                                   @RequestAttribute("userId") Long userId) {
        notificationService.markAsRead(id, userId);
        return Result.success("标记成功", null);
    }
    
    /**
     * 全部标记为已读（按用户ID和用户类型过滤）
     */
    @PostMapping("/read-all")
    public Result<Void> markAllAsRead(@RequestAttribute("userId") Long userId,
                                       @RequestAttribute("role") String role) {
        String userType = role;
        notificationService.markAllAsRead(userId, userType);
        return Result.success("全部标记为已读", null);
    }
}
