package com.studentsys.base.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.studentsys.base.feign.StudentFeignClient;
import com.studentsys.base.feign.TeacherFeignClient;
import com.studentsys.base.mapper.MajorMapper;
import com.studentsys.base.service.CollegeService;
import com.studentsys.base.service.MajorService;
import com.studentsys.common.entity.College;
import com.studentsys.common.entity.Major;
import com.studentsys.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 专业Service实现
 */
@Service
public class MajorServiceImpl extends ServiceImpl<MajorMapper, Major> implements MajorService {
    
    @Lazy
    @Autowired
    private CollegeService collegeService;
    
    @Autowired
    private StudentFeignClient studentFeignClient;
    
    @Autowired
    private TeacherFeignClient teacherFeignClient;
    
    @Override
    public List<Major> listByCollegeId(Long collegeId) {
        return lambdaQuery().eq(Major::getCollegeId, collegeId).list();
    }
    
    @Override
    public List<Major> listWithStats() {
        List<Major> majors = list();
        
        majors.forEach(major -> {
            // 填充学院信息
            if (major.getCollegeId() != null) {
                College college = collegeService.getById(major.getCollegeId());
                major.setCollege(college);
            }
            
            // 统计学生数量
            try {
                Result<Long> studentResult = studentFeignClient.countByMajorId(major.getId());
                if (studentResult.getCode() == 200) {
                    major.setStudentCount(studentResult.getData().intValue());
                }
            } catch (Exception e) {
                major.setStudentCount(0);
            }
            
            // 统计教师数量
            try {
                Result<Long> teacherResult = teacherFeignClient.countByMajorId(major.getId());
                if (teacherResult.getCode() == 200) {
                    major.setTeacherCount(teacherResult.getData().intValue());
                }
            } catch (Exception e) {
                major.setTeacherCount(0);
            }
        });
        
        return majors;
    }
    
    @Override
    public Major getByName(String name) {
        return lambdaQuery().eq(Major::getMajorName, name).one();
    }
    
    @Override
    public long countByCollegeId(Long collegeId) {
        return lambdaQuery().eq(Major::getCollegeId, collegeId).count();
    }
}
