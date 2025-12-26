package com.myweb.consumer;

import com.myweb.config.RabbitMQConfig;
import com.myweb.dto.NotificationMessageDTO;
import com.myweb.entity.SmsLog;
import com.myweb.mapper.SmsLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 通知消息消费者
 * 监听RabbitMQ队列并处理短信/邮件发送
 */
@Component
@Slf4j
public class NotificationConsumer {
    
    @Autowired
    private SmsLogMapper smsLogMapper;
    
    /**
     * 消费短信队列
     * 处理短信发送逻辑
     */
    @RabbitListener(queues = RabbitMQConfig.SMS_QUEUE)
    public void consumeSmsMessage(NotificationMessageDTO message) {
        log.info("Received SMS message: userId={}, phone={}, title={}", 
                message.getUserId(), message.getPhone(), message.getTitle());
        
        SmsLog smsLog = new SmsLog();
        smsLog.setUserId(message.getUserId());
        smsLog.setUserType(message.getUserType());
        smsLog.setPhone(message.getPhone());
        smsLog.setTitle(message.getTitle());
        smsLog.setContent(message.getContent());
        smsLog.setRelatedType(message.getRelatedType());
        smsLog.setRelatedId(message.getRelatedId());
        smsLog.setStatus("pending");
        smsLog.setCreateTime(LocalDateTime.now());
        smsLog.setUpdateTime(LocalDateTime.now());
        
        try {
            // TODO: 这里集成真实的短信服务API（如阿里云短信、腾讯云短信等）
            // 目前只是模拟发送短信
            boolean sendSuccess = sendSms(message);
            
            if (sendSuccess) {
                smsLog.setStatus("success");
                log.info("✅ SMS sent successfully: phone={}, title={}", message.getPhone(), message.getTitle());
            } else {
                smsLog.setStatus("failed");
                smsLog.setErrorMsg("短信发送失败");
                log.error("❌ SMS send failed: phone={}", message.getPhone());
            }
            
        } catch (Exception e) {
            smsLog.setStatus("failed");
            smsLog.setErrorMsg(e.getMessage());
            log.error("❌ SMS send error: phone={}", message.getPhone(), e);
        }
        
        // 保存短信日志
        smsLogMapper.insert(smsLog);
    }
    
    /**
     * 消费邮件队列
     * 处理邮件发送逻辑
     */
    @RabbitListener(queues = RabbitMQConfig.EMAIL_QUEUE)
    public void consumeEmailMessage(NotificationMessageDTO message) {
        log.info("Received Email message: userId={}, email={}, title={}", 
                message.getUserId(), message.getEmail(), message.getTitle());
        
        try {
            // TODO: 这里集成真实的邮件服务（如JavaMail、阿里云邮件推送等）
            // 目前只是模拟发送邮件
            boolean sendSuccess = sendEmail(message);
            
            if (sendSuccess) {
                log.info("✅ Email sent successfully: email={}, title={}", message.getEmail(), message.getTitle());
            } else {
                log.error("❌ Email send failed: email={}", message.getEmail());
            }
            
        } catch (Exception e) {
            log.error("❌ Email send error: email={}", message.getEmail(), e);
        }
    }
    
    /**
     * 模拟发送短信
     * TODO: 集成真实短信服务API
     */
    private boolean sendSms(NotificationMessageDTO message) {
        // 模拟短信发送逻辑
        log.info("📱 [模拟短信发送]");
        log.info("   接收者: {} ({})", message.getUserName(), message.getPhone());
        log.info("   标题: {}", message.getTitle());
        log.info("   内容: {}", message.getContent());
        
        // 模拟90%的成功率
        return Math.random() > 0.1;
    }
    
    /**
     * 模拟发送邮件
     * TODO: 集成真实邮件服务
     */
    private boolean sendEmail(NotificationMessageDTO message) {
        // 模拟邮件发送逻辑
        log.info("📧 [模拟邮件发送]");
        log.info("   接收者: {} ({})", message.getUserName(), message.getEmail());
        log.info("   标题: {}", message.getTitle());
        log.info("   内容: {}", message.getContent());
        
        // 模拟90%的成功率
        return Math.random() > 0.1;
    }
}
