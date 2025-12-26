package com.myweb.service.impl;

import com.myweb.config.RabbitMQConfig;
import com.myweb.dto.NotificationMessageDTO;
import com.myweb.service.MessageProducerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 消息通知Producer服务实现
 */
@Service
@Slf4j
public class MessageProducerServiceImpl implements MessageProducerService {
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    @Override
    public void sendSmsNotification(NotificationMessageDTO message) {
        try {
            message.setType("sms");
            message.setCreateTime(LocalDateTime.now());
            
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.NOTIFICATION_EXCHANGE,
                    RabbitMQConfig.SMS_ROUTING_KEY,
                    message
            );
            
            log.info("SMS notification sent to queue: userId={}, phone={}, title={}", 
                    message.getUserId(), message.getPhone(), message.getTitle());
            
        } catch (Exception e) {
            log.error("Failed to send SMS notification to queue", e);
        }
    }
    
    @Override
    public void sendEmailNotification(NotificationMessageDTO message) {
        try {
            message.setType("email");
            message.setCreateTime(LocalDateTime.now());
            
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.NOTIFICATION_EXCHANGE,
                    RabbitMQConfig.EMAIL_ROUTING_KEY,
                    message
            );
            
            log.info("Email notification sent to queue: userId={}, email={}, title={}", 
                    message.getUserId(), message.getEmail(), message.getTitle());
            
        } catch (Exception e) {
            log.error("Failed to send Email notification to queue", e);
        }
    }
    
    @Override
    public void sendSmsNotificationBatch(List<NotificationMessageDTO> messages) {
        for (NotificationMessageDTO message : messages) {
            sendSmsNotification(message);
        }
    }
}
