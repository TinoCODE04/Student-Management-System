package com.studentsys.teacher.feign;

import com.studentsys.common.entity.College;
import com.studentsys.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 学院服务Feign客户端
 */
@FeignClient(name = "base-service", contextId = "collegeFeignClient")
public interface CollegeFeignClient {
    
    @GetMapping("/api/college/{id}")
    Result<College> getById(@PathVariable("id") Long id);
}
