package com.studentsys.student.feign;

import com.studentsys.common.entity.Major;
import com.studentsys.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 专业服务Feign客户端
 */
@FeignClient(name = "base-service", contextId = "majorFeignClient")
public interface MajorFeignClient {
    
    @GetMapping("/api/major/{id}")
    Result<Major> getById(@PathVariable("id") Long id);
}
