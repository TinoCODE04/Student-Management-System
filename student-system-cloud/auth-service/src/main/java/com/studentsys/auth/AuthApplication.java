package com.studentsys.auth;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * 认证服务启动类
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.studentsys.auth.mapper")
@ComponentScan(basePackages = {"com.studentsys.auth", "com.studentsys.common"})
public class AuthApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }
}
