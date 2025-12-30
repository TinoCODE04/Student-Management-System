package com.studentsys.base.feign;

import com.studentsys.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 教师服务Feign客户端
 */
@FeignClient(name = "teacher-service", contextId = "teacherFeignClient")
public interface TeacherFeignClient {
    
    @GetMapping("/api/teacher/count")
    Result<Long> countByCollegeId(@RequestParam("collegeId") Long collegeId);
    
    @GetMapping("/api/teacher/count/major")
    Result<Long> countByMajorId(@RequestParam("majorId") Long majorId);
}
