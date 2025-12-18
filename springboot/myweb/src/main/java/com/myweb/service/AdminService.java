package com.myweb.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.myweb.entity.Admin;

/**
 * 管理员Service接口
 */
public interface AdminService extends IService<Admin> {
    
    /**
     * 根据用户名查询管理员
     */
    Admin getByUsername(String username);
}
