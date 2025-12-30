package com.studentsys.base;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 基础服务启动类
 */
@SpringBootApplication(scanBasePackages = {"com.studentsys.base", "com.studentsys.common"})
@EnableDiscoveryClient
@EnableFeignClients
@MapperScan("com.studentsys.base.mapper")
public class BaseApplication {
    public static void main(String[] args) {
        SpringApplication.run(BaseApplication.class, args);
    }
}
