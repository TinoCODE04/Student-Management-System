package com.myweb.repository;

import com.myweb.document.AnnouncementDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 公告 Elasticsearch 仓库
 */
@Repository
public interface AnnouncementRepository extends ElasticsearchRepository<AnnouncementDocument, Long> {
    
    /**
     * 根据标题查询
     */
    List<AnnouncementDocument> findByTitle(String title);
    
    /**
     * 根据目标角色查询
     */
    List<AnnouncementDocument> findByTargetRole(String targetRole);
    
    /**
     * 根据优先级查询
     */
    List<AnnouncementDocument> findByPriority(String priority);
}
