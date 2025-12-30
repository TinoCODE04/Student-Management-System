package com.studentsys.base.controller;

import com.studentsys.base.service.CollegeService;
import com.studentsys.base.service.MajorService;
import com.studentsys.common.entity.College;
import com.studentsys.common.entity.Major;
import com.studentsys.common.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 专业Controller
 */
@RestController
@RequestMapping("/api/major")
@RequiredArgsConstructor
public class MajorController {
    
    private final MajorService majorService;
    private final CollegeService collegeService;
    
    /**
     * 获取所有专业列表（包含统计信息）
     */
    @GetMapping("/list")
    public Result<List<Major>> list() {
        List<Major> majors = majorService.listWithStats();
        return Result.success(majors);
    }
    
    /**
     * 获取所有专业列表（简单列表，用于下拉选择）
     */
    @GetMapping("/simple")
    public Result<List<Major>> simpleList() {
        List<Major> majors = majorService.list();
        // 填充学院信息
        majors.forEach(major -> {
            if (major.getCollegeId() != null) {
                College college = collegeService.getById(major.getCollegeId());
                major.setCollege(college);
            }
        });
        return Result.success(majors);
    }
    
    /**
     * 根据学院ID获取专业列表
     */
    @GetMapping("/college/{collegeId}")
    public Result<List<Major>> listByCollege(@PathVariable Long collegeId) {
        List<Major> majors = majorService.listByCollegeId(collegeId);
        return Result.success(majors);
    }
    
    /**
     * 根据ID获取专业
     */
    @GetMapping("/{id}")
    public Result<Major> getById(@PathVariable Long id) {
        Major major = majorService.getById(id);
        if (major != null && major.getCollegeId() != null) {
            College college = collegeService.getById(major.getCollegeId());
            major.setCollege(college);
        }
        return Result.success(major);
    }
    
    /**
     * 新增专业
     */
    @PostMapping
    public Result<Void> add(@RequestBody Major major,
                            @RequestHeader(value = "X-Role", required = false) String role) {
        // 只有教师可以添加专业
        if (!"teacher".equals(role)) {
            return Result.forbidden("无权限操作");
        }
        
        // 检查名称是否已存在
        if (majorService.getByName(major.getMajorName()) != null) {
            return Result.error("专业名称已存在");
        }
        
        majorService.save(major);
        return Result.success();
    }
    
    /**
     * 更新专业
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id,
                               @RequestBody Major major,
                               @RequestHeader(value = "X-Role", required = false) String role) {
        // 只有教师可以修改专业
        if (!"teacher".equals(role)) {
            return Result.forbidden("无权限操作");
        }
        
        major.setId(id);
        majorService.updateById(major);
        return Result.success();
    }
    
    /**
     * 删除专业
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id,
                               @RequestHeader(value = "X-Role", required = false) String role) {
        // 只有教师可以删除专业
        if (!"teacher".equals(role)) {
            return Result.forbidden("无权限操作");
        }
        
        majorService.removeById(id);
        return Result.success();
    }
}
