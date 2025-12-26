package com.myweb.service;

import com.myweb.document.AnnouncementDocument;
import com.myweb.entity.Announcement;
import com.myweb.mapper.AnnouncementMapper;
import com.myweb.repository.AnnouncementRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 公告搜索服务
 */
@Slf4j
@Service
public class AnnouncementSearchService {
    
    @Autowired(required = false)
    private AnnouncementRepository announcementRepository;
    
    @Autowired(required = false)
    private ElasticsearchOperations elasticsearchOperations;
    
    @Autowired
    private AnnouncementMapper announcementMapper;
    
    /**
     * 索引公告
     */
    public void indexAnnouncement(AnnouncementDocument document) {
        if (announcementRepository != null) {
            try {
                announcementRepository.save(document);
                log.info("成功索引公告: {}", document.getTitle());
            } catch (Exception e) {
                log.error("索引公告失败: {}", e.getMessage());
            }
        }
    }
    
    /**
     * 删除公告索引
     */
    public void deleteAnnouncement(Long announcementId) {
        if (announcementRepository != null) {
            try {
                announcementRepository.deleteById(announcementId);
                log.info("成功删除公告索引: {}", announcementId);
            } catch (Exception e) {
                log.error("删除公告索引失败: {}", e.getMessage());
            }
        }
    }
    
    /**
     * 搜索公告
     */
    public Page<AnnouncementDocument> searchAnnouncements(
            String keyword, 
            String targetRole, 
            String priority, 
            int pageNum, 
            int pageSize) {
        
        if (announcementRepository == null || elasticsearchOperations == null) {
            log.warn("Elasticsearch 未配置，无法搜索");
            return Page.empty();
        }
        
        try {
            // 构建查询
            var queryBuilder = co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders.bool();
            
            // 关键词搜索（标题和内容）
            if (keyword != null && !keyword.trim().isEmpty()) {
                queryBuilder.must(m -> m.multiMatch(mm -> mm
                    .query(keyword)
                    .fields("title^2", "content", "publisherName") // title 权重更高
                ));
            }
            
            // 按目标角色筛选
            if (targetRole != null && !targetRole.trim().isEmpty()) {
                queryBuilder.filter(f -> f.term(t -> t
                    .field("targetRole")
                    .value(targetRole)
                ));
            }
            
            // 按优先级筛选
            if (priority != null && !priority.trim().isEmpty()) {
                queryBuilder.filter(f -> f.term(t -> t
                    .field("priority")
                    .value(priority)
                ));
            }
            
            // 如果没有任何条件，使用 match_all
            var finalQuery = queryBuilder.build().must().isEmpty() && queryBuilder.build().filter().isEmpty()
                ? co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders.matchAll().build()._toQuery()
                : queryBuilder.build()._toQuery();
            
            // 构建查询
            Query query = NativeQuery.builder()
                .withQuery(finalQuery)
                .withPageable(PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime")))
                .build();
            
            SearchHits<AnnouncementDocument> searchHits = elasticsearchOperations.search(query, AnnouncementDocument.class);
            
            List<AnnouncementDocument> announcements = searchHits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
            
            return new org.springframework.data.domain.PageImpl<>(
                announcements,
                PageRequest.of(pageNum - 1, pageSize),
                searchHits.getTotalHits()
            );
            
        } catch (Exception e) {
            log.error("搜索公告失败: {}", e.getMessage(), e);
            return Page.empty();
        }
    }
    
    /**
     * 重建公告索引
     */
    public String rebuildIndex() {
        if (announcementRepository == null || elasticsearchOperations == null) {
            return "Elasticsearch 未配置，无法重建索引";
        }
        
        try {
            log.info("开始重建公告索引...");
            
            // 删除所有现有索引
            announcementRepository.deleteAll();
            log.info("已清空现有公告索引");
            
            // 从数据库读取所有公告
            List<Announcement> announcements = announcementMapper.selectList(null);
            log.info("从数据库读取到 {} 条公告记录", announcements.size());
            
            // 批量索引
            List<AnnouncementDocument> documents = new ArrayList<>();
            for (Announcement announcement : announcements) {
                AnnouncementDocument document = convertToDocument(announcement);
                documents.add(document);
            }
            
            announcementRepository.saveAll(documents);
            log.info("公告索引重建完成，共索引 {} 条记录", documents.size());
            
            return "成功重建公告索引，共 " + documents.size() + " 条记录";
            
        } catch (Exception e) {
            log.error("重建公告索引失败: {}", e.getMessage(), e);
            return "重建公告索引失败: " + e.getMessage();
        }
    }
    
    /**
     * 将 Announcement 实体转换为 AnnouncementDocument
     */
    public AnnouncementDocument convertToDocument(Announcement announcement) {
        AnnouncementDocument document = new AnnouncementDocument();
        document.setId(announcement.getId());
        document.setTitle(announcement.getTitle());
        document.setContent(announcement.getContent());
        document.setTargetRole(announcement.getTargetRole());
        document.setPriority(announcement.getPriority());
        document.setPublisherId(announcement.getPublisherId());
        document.setPublisherName(announcement.getPublisherName());
        document.setPublisherType(announcement.getPublisherType());
        document.setViewCount(0); // 预留字段，可以后续添加
        document.setCreateTime(announcement.getCreateTime());
        document.setUpdateTime(announcement.getUpdateTime());
        
        return document;
    }
}
