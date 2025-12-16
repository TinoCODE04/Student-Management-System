package com.myweb.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.myweb.entity.Notification;

/**
 * 通知Service
 */
public interface NotificationService extends IService<Notification> {
    
    /**
     * 创建通知
     */
    void createNotification(Long userId, String userType, String title, String content, 
                           String type, Long relatedId, String url);
    
    /**
     * 获取用户通知列表（分页）
     */
    Page<Notification> getUserNotifications(Long userId, int pageNum, int pageSize);
    
    /**
     * 标记为已读
     */
    void markAsRead(Long id, Long userId);
    
    /**
     * 标记全部已读
     */
    void markAllAsRead(Long userId);
    
    /**
     * 获取未读数量
     */
    long getUnreadCount(Long userId);
}
