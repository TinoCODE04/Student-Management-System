package com.myweb.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
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
    
    // 死信队列
    public static final String SMS_DLX_QUEUE = "sms.dlx.queue";
    public static final String EMAIL_DLX_QUEUE = "email.dlx.queue";
    public static final String DLX_EXCHANGE = "dlx.exchange";
    
    // 路由键
    public static final String SMS_ROUTING_KEY = "notification.sms";
    public static final String EMAIL_ROUTING_KEY = "notification.email";
    public static final String SMS_DLX_ROUTING_KEY = "dlx.sms";
    public static final String EMAIL_DLX_ROUTING_KEY = "dlx.email";
    
    /**
     * 声明通知交换机（Topic类型）
     */
    @Bean
    public TopicExchange notificationExchange() {
        return new TopicExchange(NOTIFICATION_EXCHANGE, true, false);
    }
    
    /**
     * 声明死信交换机
     */
    @Bean
    public DirectExchange dlxExchange() {
        return new DirectExchange(DLX_EXCHANGE, true, false);
    }
    
    /**
     * 声明短信队列（带死信队列配置）
     */
    @Bean
    public Queue smsQueue() {
        return QueueBuilder.durable(SMS_QUEUE)
                .withArgument("x-message-ttl", 3600000) // 消息1小时过期
                .withArgument("x-max-length", 10000) // 队列最大长度
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE) // 死信交换机
                .withArgument("x-dead-letter-routing-key", SMS_DLX_ROUTING_KEY) // 死信路由键
                .build();
    }
    
    /**
     * 声明邮件队列（带死信队列配置）
     */
    @Bean
    public Queue emailQueue() {
        return QueueBuilder.durable(EMAIL_QUEUE)
                .withArgument("x-message-ttl", 3600000)
                .withArgument("x-max-length", 10000)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", EMAIL_DLX_ROUTING_KEY)
                .build();
    }
    
    /**
     * 声明短信死信队列
     */
    @Bean
    public Queue smsDlxQueue() {
        return QueueBuilder.durable(SMS_DLX_QUEUE).build();
    }
    
    /**
     * 声明邮件死信队列
     */
    @Bean
    public Queue emailDlxQueue() {
        return QueueBuilder.durable(EMAIL_DLX_QUEUE).build();
    }
    
    /**
     * 绑定短信队列到交换机
     */
    @Bean
    public Binding smsBinding(Queue smsQueue, TopicExchange notificationExchange) {
        return BindingBuilder
                .bind(smsQueue)
                .to(notificationExchange)
                .with(SMS_ROUTING_KEY);
    }
    
    /**
     * 绑定邮件队列到交换机
     */
    @Bean
    public Binding emailBinding(Queue emailQueue, TopicExchange notificationExchange) {
        return BindingBuilder
                .bind(emailQueue)
                .to(notificationExchange)
                .with(EMAIL_ROUTING_KEY);
    }
    
    /**
     * 绑定短信死信队列
     */
    @Bean
    public Binding smsDlxBinding(Queue smsDlxQueue, DirectExchange dlxExchange) {
        return BindingBuilder
                .bind(smsDlxQueue)
                .to(dlxExchange)
                .with(SMS_DLX_ROUTING_KEY);
    }
    
    /**
     * 绑定邮件死信队列
     */
    @Bean
    public Binding emailDlxBinding(Queue emailDlxQueue, DirectExchange dlxExchange) {
        return BindingBuilder
                .bind(emailDlxQueue)
                .to(dlxExchange)
                .with(EMAIL_DLX_ROUTING_KEY);
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
        
        // 启用发布者确认模式
        rabbitTemplate.setMandatory(true);
        
        // 设置消息确认回调（消息到达交换机）
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                System.out.println("✅ 消息成功发送到交换机");
            } else {
                System.err.println("❌ 消息发送到交换机失败: " + cause);
            }
        });
        
        // 设置消息返回回调（消息从交换机路由到队列失败）
        rabbitTemplate.setReturnsCallback(returned -> {
            System.err.println("❌ 消息路由失败: " + returned.getMessage());
            System.err.println("   Reply Code: " + returned.getReplyCode());
            System.err.println("   Reply Text: " + returned.getReplyText());
            System.err.println("   Exchange: " + returned.getExchange());
            System.err.println("   Routing Key: " + returned.getRoutingKey());
        });
        
        return rabbitTemplate;
    }
    
    /**
     * 配置消息监听容器工厂
     * 启用手动ACK模式，确保消息被正确处理后才从队列删除
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            SimpleRabbitListenerContainerFactoryConfigurer configurer) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setMessageConverter(messageConverter());
        // 设置为自动确认模式（默认），也可以改为手动确认
        // factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        return factory;
    }
}
