package com.myweb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.myweb.entity.Announcement;
import com.myweb.mapper.AnnouncementMapper;
import com.myweb.service.AnnouncementService;
import org.springframework.stereotype.Service;

/**
 * 系统公告Service实现
 */
@Service
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, Announcement> implements AnnouncementService {
    
    @Override
    public Page<Announcement> getAnnouncementPage(int page, int size) {
        Page<Announcement> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Announcement::getStatus, 1)
               .orderByDesc(Announcement::getCreateTime);
        return this.page(pageParam, wrapper);
    }
    
    @Override
    public boolean publishAnnouncement(Announcement announcement) {
        announcement.setStatus(1);
        return this.save(announcement);
    }
    
    @Override
    public boolean updateAnnouncement(Announcement announcement) {
        return this.updateById(announcement);
    }
    
    @Override
    public boolean deleteAnnouncement(Long id) {
        Announcement announcement = new Announcement();
        announcement.setId(id);
        announcement.setStatus(0);
        return this.updateById(announcement);
    }
}
