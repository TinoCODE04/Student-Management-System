package com.myweb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.myweb.entity.Notification;
import com.myweb.mapper.NotificationMapper;
import com.myweb.service.NotificationService;
import com.myweb.websocket.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 通知Service实现
 */
@Service
@Slf4j
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification> 
        implements NotificationService {
    
    @Override
    public void createNotification(Long userId, String userType, String title, String content, 
                                   String type, Long relatedId, String url) {
        log.info("📝 Creating notification: userId={}, userType={}, title={}, content={}", 
                userId, userType, title, content);
        
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setUserType(userType);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType(type);
        notification.setRelatedId(relatedId);
        notification.setUrl(url);
        notification.setStatus(0); // 未读
        
        log.info("💾 Saving notification to database...");
        boolean saved = save(notification);
        log.info("💾 Notification saved to database: {}, notification id: {}", saved, notification.getId());
        
        // 通过WebSocket实时推送 - 使用静态方法调用
        try {
            log.info("📡 Attempting to send WebSocket message to userId: {}", userId);
            WebSocketServer.sendMessageToUserStatic(userId.toString(), notification);
            log.info("✅ Notification created and sent for user {}: {}", userId, title);
        } catch (Exception e) {
            log.error("❌ Failed to send WebSocket notification for user {}: {}", userId, e.getMessage(), e);
            log.info("💾 Notification saved in database for user {}: {}", userId, title);
        }
    }
    
    @Override
    public Page<Notification> getUserNotifications(Long userId, String userType, int pageNum, int pageSize) {
        Page<Notification> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getUserId, userId)
               .eq(Notification::getUserType, userType)  // 重要：同时过滤用户类型
               .orderByDesc(Notification::getCreateTime);
        
        return page(page, wrapper);
    }
    
    @Override
    public void markAsRead(Long id, Long userId) {
        Notification notification = getById(id);
        if (notification != null && notification.getUserId().equals(userId)) {
            notification.setStatus(1);
            updateById(notification);
        }
    }
    
    @Override
    public void markAllAsRead(Long userId, String userType) {
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getUserId, userId)
               .eq(Notification::getUserType, userType)  // 重要：同时过滤用户类型
               .eq(Notification::getStatus, 0);
        
        Notification update = new Notification();
        update.setStatus(1);
        update(update, wrapper);
    }
    
    @Override
    public long getUnreadCount(Long userId, String userType) {
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getUserId, userId)
               .eq(Notification::getUserType, userType)  // 重要：同时过滤用户类型
               .eq(Notification::getStatus, 0);
        
        return count(wrapper);
    }
}
