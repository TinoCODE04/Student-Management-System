package com.studentsys.course.feign;

import com.studentsys.common.entity.Teacher;
import com.studentsys.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 教师服务Feign客户端
 */
@FeignClient(name = "teacher-service", contextId = "teacherFeignClient")
public interface TeacherFeignClient {
    
    @GetMapping("/api/teacher/{id}")
    Result<Teacher> getById(@PathVariable("id") Long id);
}
