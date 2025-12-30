package com.studentsys.selection.feign;

import com.studentsys.common.entity.Student;
import com.studentsys.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 学生服务Feign客户端
 */
@FeignClient(name = "student-service", contextId = "studentFeignClient")
public interface StudentFeignClient {
    
    @GetMapping("/api/student/{id}")
    Result<Student> getById(@PathVariable("id") Long id);
}
