package com.myweb.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.myweb.entity.Announcement;

/**
 * 系统公告Service
 */
public interface AnnouncementService extends IService<Announcement> {
    
    /**
     * 分页查询公告
     */
    Page<Announcement> getAnnouncementPage(int page, int size);
    
    /**
     * 发布公告
     */
    boolean publishAnnouncement(Announcement announcement);
    
    /**
     * 更新公告
     */
    boolean updateAnnouncement(Announcement announcement);
    
    /**
     * 删除公告(软删除)
     */
    boolean deleteAnnouncement(Long id);
}
