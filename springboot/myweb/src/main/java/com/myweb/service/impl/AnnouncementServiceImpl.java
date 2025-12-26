package com.myweb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.myweb.document.AnnouncementDocument;
import com.myweb.entity.Announcement;
import com.myweb.mapper.AnnouncementMapper;
import com.myweb.service.AnnouncementSearchService;
import com.myweb.service.AnnouncementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;

/**
 * 系统公告Service实现
 */
@Slf4j
@Service
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, Announcement> implements AnnouncementService {
    
    @Autowired
    private AnnouncementSearchService announcementSearchService;
    
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
        boolean result = this.save(announcement);
        
        // 同步到 Elasticsearch
        if (result) {
            try {
                AnnouncementDocument document = announcementSearchService.convertToDocument(announcement);
                announcementSearchService.indexAnnouncement(document);
            } catch (Exception e) {
                log.error("公告索引失败", e);
            }
        }
        
        return result;
    }
    
    @Override
    public boolean updateAnnouncement(Announcement announcement) {
        boolean result = this.updateById(announcement);
        
        // 同步到 Elasticsearch
        if (result) {
            try {
                Announcement updatedAnnouncement = this.getById(announcement.getId());
                AnnouncementDocument document = announcementSearchService.convertToDocument(updatedAnnouncement);
                announcementSearchService.indexAnnouncement(document);
            } catch (Exception e) {
                log.error("公告索引更新失败", e);
            }
        }
        
        return result;
    }
    
    @Override
    public boolean deleteAnnouncement(Long id) {
        Announcement announcement = new Announcement();
        announcement.setId(id);
        announcement.setStatus(0);
        boolean result = this.updateById(announcement);
        
        // 从 Elasticsearch 删除
        if (result) {
            try {
                announcementSearchService.deleteAnnouncement(id);
            } catch (Exception e) {
                log.error("公告索引删除失败", e);
            }
        }
        
        return result;
    }
}
