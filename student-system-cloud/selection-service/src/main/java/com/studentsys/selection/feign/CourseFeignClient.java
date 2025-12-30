package com.studentsys.selection.feign;

import com.studentsys.common.entity.Course;
import com.studentsys.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * 课程服务Feign客户端
 */
@FeignClient(name = "course-service", contextId = "courseFeignClient")
public interface CourseFeignClient {
    
    @GetMapping("/api/course/{id}")
    Result<Course> getById(@PathVariable("id") Long id);
    
    @GetMapping("/api/course/list")
    Result<List<Course>> list();
    
    @GetMapping("/api/course/teacher/{teacherId}")
    Result<List<Course>> listByTeacher(@PathVariable("teacherId") Long teacherId);
}
