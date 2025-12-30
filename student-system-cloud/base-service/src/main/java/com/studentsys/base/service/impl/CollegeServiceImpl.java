package com.studentsys.base.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.studentsys.base.feign.CourseFeignClient;
import com.studentsys.base.feign.StudentFeignClient;
import com.studentsys.base.mapper.CollegeMapper;
import com.studentsys.base.service.CollegeService;
import com.studentsys.base.service.MajorService;
import com.studentsys.common.entity.College;
import com.studentsys.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 学院Service实现
 */
@Service
public class CollegeServiceImpl extends ServiceImpl<CollegeMapper, College> implements CollegeService {
    
    @Lazy
    @Autowired
    private MajorService majorService;
    
    @Autowired
    private StudentFeignClient studentFeignClient;
    
    @Autowired
    private CourseFeignClient courseFeignClient;
    
    @Override
    public List<College> listWithStats() {
        List<College> colleges = list();
        
        colleges.forEach(college -> {
            // 统计专业数量
            long majorCount = majorService.countByCollegeId(college.getId());
            college.setMajorCount((int) majorCount);
            
            // 统计学生数量
            try {
                Result<Long> studentResult = studentFeignClient.countByCollegeId(college.getId());
                if (studentResult.getCode() == 200) {
                    college.setStudentCount(studentResult.getData().intValue());
                }
            } catch (Exception e) {
                college.setStudentCount(0);
            }
            
            // 统计课程数量
            try {
                Result<Long> courseResult = courseFeignClient.countByCollegeId(college.getId());
                if (courseResult.getCode() == 200) {
                    college.setCourseCount(courseResult.getData().intValue());
                }
            } catch (Exception e) {
                college.setCourseCount(0);
            }
        });
        
        return colleges;
    }
    
    @Override
    public College getByName(String name) {
        return lambdaQuery().eq(College::getCollegeName, name).one();
    }
}
