package com.studentsys.student;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * 学生服务启动类
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@MapperScan("com.studentsys.student.mapper")
@ComponentScan(basePackages = {"com.studentsys.student", "com.studentsys.common"})
public class StudentApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(StudentApplication.class, args);
    }
}
