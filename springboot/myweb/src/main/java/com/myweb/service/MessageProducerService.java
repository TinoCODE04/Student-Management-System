package com.myweb.service;

import com.myweb.dto.NotificationMessageDTO;

/**
 * 消息通知Producer服务
 * 用于发送消息到RabbitMQ队列
 */
public interface MessageProducerService {
    
    /**
     * 发送短信通知到队列
     * @param message 通知消息
     */
    void sendSmsNotification(NotificationMessageDTO message);
    
    /**
     * 发送邮件通知到队列
     * @param message 通知消息
     */
    void sendEmailNotification(NotificationMessageDTO message);
    
    /**
     * 批量发送短信通知
     * @param messages 通知消息列表
     */
    void sendSmsNotificationBatch(java.util.List<NotificationMessageDTO> messages);
}
