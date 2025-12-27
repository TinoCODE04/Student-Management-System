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
        log.info("================================================");
        log.info("📩 收到短信通知任务");
        log.info("   接收者ID: {}", message.getUserId());
        log.info("   接收者类型: {}", message.getUserType());
        log.info("   接收者姓名: {}", message.getUserName());
        log.info("   手机号: {}", message.getPhone());
        log.info("   标题: {}", message.getTitle());
        log.info("   内容: {}", message.getContent());
        log.info("   业务类型: {}", message.getRelatedType());
        log.info("   业务ID: {}", message.getRelatedId());
        log.info("================================================");
        
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
                log.info("✅ 短信发送成功!");
                log.info("   手机号: {}", message.getPhone());
                log.info("   标题: {}", message.getTitle());
            } else {
                smsLog.setStatus("failed");
                smsLog.setErrorMsg("短信发送失败");
                log.error("❌ 短信发送失败: phone={}", message.getPhone());
            }
            
        } catch (Exception e) {
            smsLog.setStatus("failed");
            smsLog.setErrorMsg(e.getMessage());
            log.error("❌ 短信发送异常: phone={}, error={}", message.getPhone(), e.getMessage(), e);
        }
        
        // 保存短信日志到数据库
        try {
            smsLogMapper.insert(smsLog);
            log.info("💾 短信发送记录已保存到数据库: smsLogId={}, status={}", 
                    smsLog.getId(), smsLog.getStatus());
        } catch (Exception e) {
            log.error("❌ 保存短信日志失败", e);
        }
        
        log.info("================================================");
    }
    
    /**
     * 消费邮件队列
     * 处理邮件发送逻辑
     * 注意：这是预留的邮件功能，目前只打印日志
     */
    @RabbitListener(queues = RabbitMQConfig.EMAIL_QUEUE)
    public void consumeEmailMessage(NotificationMessageDTO message) {
        log.info("================================================");
        log.info("📧 收到邮件通知任务");
        log.info("   接收者ID: {}", message.getUserId());
        log.info("   接收者类型: {}", message.getUserType());
        log.info("   接收者姓名: {}", message.getUserName());
        log.info("   邮箱: {}", message.getEmail());
        log.info("   标题: {}", message.getTitle());
        log.info("   内容: {}", message.getContent());
        log.info("   业务类型: {}", message.getRelatedType());
        log.info("   业务ID: {}", message.getRelatedId());
        log.info("================================================");
        
        try {
            // TODO: 这里集成真实的邮件服务（如JavaMail、阿里云邮件推送等）
            // 目前只是模拟发送邮件
            boolean sendSuccess = sendEmail(message);
            
            if (sendSuccess) {
                log.info("✅ 邮件发送成功!");
                log.info("   邮箱: {}", message.getEmail());
                log.info("   标题: {}", message.getTitle());
            } else {
                log.error("❌ 邮件发送失败: email={}", message.getEmail());
            }
            
        } catch (Exception e) {
            log.error("❌ 邮件发送异常: email={}, error={}", message.getEmail(), e.getMessage(), e);
        }
        
        log.info("================================================");
    }
    
    /**
     * 模拟发送短信
     * TODO: 集成真实短信服务API（阿里云SMS、腾讯云SMS等）
     */
    private boolean sendSms(NotificationMessageDTO message) {
        // 模拟短信发送逻辑
        log.info("📱 [模拟短信发送中...]");
        log.info("   API调用: 阿里云短信服务（待集成）");
        log.info("   接收者: {} ({})", message.getUserName(), message.getPhone());
        log.info("   标题: {}", message.getTitle());
        log.info("   内容: {}", message.getContent());
        
        try {
            // 模拟API调用延迟
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // 模拟95%的成功率
        boolean success = Math.random() > 0.05;
        
        if (success) {
            log.info("   ✓ 短信服务商返回: SUCCESS");
        } else {
            log.error("   ✗ 短信服务商返回: FAILED");
        }
        
        return success;
    }
    
    /**
     * 模拟发送邮件
     * TODO: 集成真实邮件服务（JavaMail、Spring Mail、阿里云邮件推送等）
     * 
     * 邮件Consumer说明：
     * 1. 当前状态：仅打印日志，不保存数据库（因为未实现email_log表）
     * 2. 用途：预留功能，教师可选择发送邮件通知
     * 3. 待实现：
     *    - 创建 email_log 表
     *    - 集成真实邮件服务（JavaMail/Spring Mail）
     *    - 添加邮件模板功能
     */
    private boolean sendEmail(NotificationMessageDTO message) {
        // 模拟邮件发送逻辑
        log.info("📧 [模拟邮件发送中...]");
        log.info("   SMTP服务器: 待配置（Spring Mail）");
        log.info("   接收者: {} ({})", message.getUserName(), message.getEmail());
        log.info("   标题: {}", message.getTitle());
        log.info("   内容: {}", message.getContent());
        
        try {
            // 模拟SMTP发送延迟
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // 模拟95%的成功率
        boolean success = Math.random() > 0.05;
        
        if (success) {
            log.info("   ✓ SMTP服务器返回: 250 OK");
        } else {
            log.error("   ✗ SMTP服务器返回: 550 Failed");
        }
        
        return success;
    }
}
