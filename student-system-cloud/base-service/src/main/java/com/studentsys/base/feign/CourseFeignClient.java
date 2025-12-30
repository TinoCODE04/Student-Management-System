package com.studentsys.base.feign;

import com.studentsys.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 课程服务Feign客户端
 */
@FeignClient(name = "course-service", contextId = "courseFeignClient")
public interface CourseFeignClient {
    
    @GetMapping("/api/course/count")
    Result<Long> countByCollegeId(@RequestParam("collegeId") Long collegeId);
}
