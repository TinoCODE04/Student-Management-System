package com.studentsys.base.controller;

import com.studentsys.base.service.CollegeService;
import com.studentsys.common.entity.College;
import com.studentsys.common.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学院Controller
 */
@RestController
@RequestMapping("/api/college")
@RequiredArgsConstructor
public class CollegeController {
    
    private final CollegeService collegeService;
    
    /**
     * 获取所有学院列表（包含统计信息）
     */
    @GetMapping("/list")
    public Result<List<College>> list() {
        List<College> colleges = collegeService.listWithStats();
        return Result.success(colleges);
    }
    
    /**
     * 获取所有学院列表（简单列表，用于下拉选择）
     */
    @GetMapping("/simple")
    public Result<List<College>> simpleList() {
        List<College> colleges = collegeService.list();
        return Result.success(colleges);
    }
    
    /**
     * 根据ID获取学院
     */
    @GetMapping("/{id}")
    public Result<College> getById(@PathVariable Long id) {
        College college = collegeService.getById(id);
        return Result.success(college);
    }
    
    /**
     * 新增学院
     */
    @PostMapping
    public Result<Void> add(@RequestBody College college,
                            @RequestHeader(value = "X-Role", required = false) String role) {
        // 只有教师可以添加学院
        if (!"teacher".equals(role)) {
            return Result.forbidden("无权限操作");
        }
        
        // 检查名称是否已存在
        if (collegeService.getByName(college.getCollegeName()) != null) {
            return Result.error("学院名称已存在");
        }
        
        collegeService.save(college);
        return Result.success();
    }
    
    /**
     * 更新学院
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id,
                               @RequestBody College college,
                               @RequestHeader(value = "X-Role", required = false) String role) {
        // 只有教师可以修改学院
        if (!"teacher".equals(role)) {
            return Result.forbidden("无权限操作");
        }
        
        college.setId(id);
        collegeService.updateById(college);
        return Result.success();
    }
    
    /**
     * 删除学院
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id,
                               @RequestHeader(value = "X-Role", required = false) String role) {
        // 只有教师可以删除学院
        if (!"teacher".equals(role)) {
            return Result.forbidden("无权限操作");
        }
        
        collegeService.removeById(id);
        return Result.success();
    }
}
