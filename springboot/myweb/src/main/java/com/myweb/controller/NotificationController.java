package com.myweb.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.myweb.common.Result;
import com.myweb.entity.Notification;
import com.myweb.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 通知Controller
 */
@RestController
@RequestMapping("/api/notification")
public class NotificationController {
    
    @Autowired
    private NotificationService notificationService;
    
    /**
     * 获取用户通知列表
     */
    @GetMapping("/list")
    public Result<Page<Notification>> list(@RequestParam(defaultValue = "1") int pageNum,
                                            @RequestParam(defaultValue = "10") int pageSize,
                                            @RequestAttribute("userId") Long userId) {
        Page<Notification> page = notificationService.getUserNotifications(userId, pageNum, pageSize);
        return Result.success(page);
    }
    
    /**
     * 获取未读数量
     */
    @GetMapping("/unread-count")
    public Result<Long> unreadCount(@RequestAttribute("userId") Long userId) {
        long count = notificationService.getUnreadCount(userId);
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
     * 全部标记为已读
     */
    @PostMapping("/read-all")
    public Result<Void> markAllAsRead(@RequestAttribute("userId") Long userId) {
        notificationService.markAllAsRead(userId);
        return Result.success("全部标记为已读", null);
    }
}
