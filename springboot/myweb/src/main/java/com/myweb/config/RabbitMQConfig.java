package com.myweb.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ配置类
 * 用于课程通知的消息队列配置
 */
@Configuration
public class RabbitMQConfig {
    
    // 交换机名称
    public static final String NOTIFICATION_EXCHANGE = "notification.exchange";
    
    // 队列名称
    public static final String SMS_QUEUE = "sms.queue";
    public static final String EMAIL_QUEUE = "email.queue";
    
    // 路由键
    public static final String SMS_ROUTING_KEY = "notification.sms";
    public static final String EMAIL_ROUTING_KEY = "notification.email";
    
    /**
     * 声明通知交换机（Topic类型）
     */
    @Bean
    public TopicExchange notificationExchange() {
        return new TopicExchange(NOTIFICATION_EXCHANGE, true, false);
    }
    
    /**
     * 声明短信队列
     */
    @Bean
    public Queue smsQueue() {
        return QueueBuilder.durable(SMS_QUEUE)
                .withArgument("x-message-ttl", 3600000) // 消息1小时过期
                .withArgument("x-max-length", 10000) // 队列最大长度
                .build();
    }
    
    /**
     * 声明邮件队列
     */
    @Bean
    public Queue emailQueue() {
        return QueueBuilder.durable(EMAIL_QUEUE)
                .withArgument("x-message-ttl", 3600000)
                .withArgument("x-max-length", 10000)
                .build();
    }
    
    /**
     * 绑定短信队列到交换机
     */
    @Bean
    public Binding smsBinding(Queue smsQueue, TopicExchange notificationExchange) {
        return BindingBuilder.bind(smsQueue)
                .to(notificationExchange)
                .with(SMS_ROUTING_KEY);
    }
    
    /**
     * 绑定邮件队列到交换机
     */
    @Bean
    public Binding emailBinding(Queue emailQueue, TopicExchange notificationExchange) {
        return BindingBuilder.bind(emailQueue)
                .to(notificationExchange)
                .with(EMAIL_ROUTING_KEY);
    }
    
    /**
     * 消息转换器（JSON格式）
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    
    /**
     * RabbitTemplate配置
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        
        // 设置消息确认回调
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (!ack) {
                System.err.println("消息发送失败: " + cause);
            }
        });
        
        return rabbitTemplate;
    }
}
