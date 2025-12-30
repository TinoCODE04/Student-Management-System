package com.studentsys.base.feign;

import com.studentsys.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 学生服务Feign客户端
 */
@FeignClient(name = "student-service", contextId = "studentFeignClient")
public interface StudentFeignClient {
    
    @GetMapping("/api/student/count")
    Result<Long> countByCollegeId(@RequestParam("collegeId") Long collegeId);
    
    @GetMapping("/api/student/count/major")
    Result<Long> countByMajorId(@RequestParam("majorId") Long majorId);
}
