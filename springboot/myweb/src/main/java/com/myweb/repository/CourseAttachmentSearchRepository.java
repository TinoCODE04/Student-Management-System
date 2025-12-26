package com.myweb.repository;

import com.myweb.document.CourseAttachmentDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 课程附件Elasticsearch Repository
 */
@Repository
public interface CourseAttachmentSearchRepository extends ElasticsearchRepository<CourseAttachmentDocument, Long> {
    
    /**
     * 根据课程ID查询
     */
    List<CourseAttachmentDocument> findByCourseId(Long courseId);
    
    /**
     * 根据原始文件名搜索（模糊匹配）
     */
    Page<CourseAttachmentDocument> findByOriginalFilenameContaining(String keyword, Pageable pageable);
    
    /**
     * 根据描述搜索
     */
    Page<CourseAttachmentDocument> findByDescriptionContaining(String keyword, Pageable pageable);
    
    /**
     * 根据文件内容搜索
     */
    Page<CourseAttachmentDocument> findByContentContaining(String keyword, Pageable pageable);
}
