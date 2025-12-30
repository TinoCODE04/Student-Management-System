package com.studentsys.selection;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 选课服务启动类
 */
@SpringBootApplication(scanBasePackages = {"com.studentsys.selection", "com.studentsys.common"})
@EnableDiscoveryClient
@EnableFeignClients
@MapperScan("com.studentsys.selection.mapper")
public class SelectionApplication {
    public static void main(String[] args) {
        SpringApplication.run(SelectionApplication.class, args);
    }
}
