package com.studentsys.teacher;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 教师服务启动类
 */
@SpringBootApplication(scanBasePackages = {"com.studentsys.teacher", "com.studentsys.common"})
@EnableDiscoveryClient
@EnableFeignClients
@MapperScan("com.studentsys.teacher.mapper")
public class TeacherApplication {
    public static void main(String[] args) {
        SpringApplication.run(TeacherApplication.class, args);
    }
}
