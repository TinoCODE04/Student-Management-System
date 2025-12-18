package com.myweb.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.myweb.common.Result;
import com.myweb.entity.Admin;
import com.myweb.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员Controller
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {
    
    @Autowired
    private AdminService adminService;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    /**
     * 分页查询管理员
     */
    @GetMapping("/page")
    public Result<Page<Admin>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        
        Page<Admin> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>();
        
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Admin::getUsername, keyword)
                   .or().like(Admin::getName, keyword)
                   .or().like(Admin::getPhone, keyword);
        }
        
        Page<Admin> result = adminService.page(page, wrapper);
        // 清空密码
        result.getRecords().forEach(admin -> admin.setPassword(null));
        
        return Result.success(result);
    }
    
    /**
     * 根据ID查询管理员
     */
    @GetMapping("/{id}")
    public Result<Admin> getById(@PathVariable Long id) {
        Admin admin = adminService.getById(id);
        if (admin != null) {
            admin.setPassword(null);
        }
        return Result.success(admin);
    }
    
    /**
     * 新增管理员
     */
    @PostMapping
    public Result<Void> add(@RequestBody Admin admin) {
        // 检查用户名是否已存在
        if (adminService.getByUsername(admin.getUsername()) != null) {
            return Result.error("用户名已存在");
        }
        
        // 加密密码
        if (admin.getPassword() == null || admin.getPassword().isEmpty()) {
            return Result.error("密码不能为空");
        }
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        admin.setRole("admin");
        if (admin.getStatus() == null) {
            admin.setStatus("active");
        }
        
        adminService.save(admin);
        return Result.success("添加成功", null);
    }
    
    /**
     * 更新管理员
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Admin admin) {
        Admin existAdmin = adminService.getById(id);
        if (existAdmin == null) {
            return Result.error("管理员不存在");
        }
        
        admin.setId(id);
        
        // 如果有密码，则加密
        if (admin.getPassword() != null && !admin.getPassword().isEmpty()) {
            admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        } else {
            // 不更新密码
            admin.setPassword(null);
        }
        
        adminService.updateById(admin);
        return Result.success("更新成功", null);
    }
    
    /**
     * 删除管理员
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        Admin admin = adminService.getById(id);
        if (admin == null) {
            return Result.error("管理员不存在");
        }
        
        adminService.removeById(id);
        return Result.success("删除成功", null);
    }
    
    /**
     * 重置密码
     */
    @PostMapping("/{id}/reset-password")
    public Result<Void> resetPassword(@PathVariable Long id) {
        Admin admin = adminService.getById(id);
        if (admin == null) {
            return Result.error("管理员不存在");
        }
        
        // 重置为默认密码 admin123
        admin.setPassword(passwordEncoder.encode("admin123"));
        adminService.updateById(admin);
        
        return Result.success("密码已重置为：admin123", null);
    }
    
    /**
     * 启用/禁用管理员
     */
    @PostMapping("/{id}/status")
    public Result<Void> toggleStatus(@PathVariable Long id, @RequestParam String status) {
        Admin admin = adminService.getById(id);
        if (admin == null) {
            return Result.error("管理员不存在");
        }
        
        if (!"active".equals(status) && !"disabled".equals(status)) {
            return Result.error("状态值错误");
        }
        
        admin.setStatus(status);
        adminService.updateById(admin);
        
        return Result.success("状态更新成功", null);
    }
    
    /**
     * 获取当前管理员信息
     */
    @GetMapping("/me")
    public Result<Admin> getCurrentAdmin(@RequestAttribute("userId") Long userId) {
        Admin admin = adminService.getById(userId);
        if (admin != null) {
            admin.setPassword(null);
        }
        return Result.success(admin);
    }
}
