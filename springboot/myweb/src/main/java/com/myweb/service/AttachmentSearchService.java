package com.myweb.service;

import com.myweb.document.CourseAttachmentDocument;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 课程附件搜索Service
 */
public interface AttachmentSearchService {
    
    /**
     * 索引附件到Elasticsearch
     */
    void indexAttachment(CourseAttachmentDocument document);
    
    /**
     * 从Elasticsearch删除附件索引
     */
    void deleteAttachment(Long id);
    
    /**
     * 更新附件索引
     */
    void updateAttachment(CourseAttachmentDocument document);
    
    /**
     * 全文搜索附件（搜索文件名、描述、内容）
     * @param keyword 搜索关键词
     * @param courseId 课程ID（可选，为null则搜索所有课程）
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 搜索结果分页
     */
    Page<CourseAttachmentDocument> searchAttachments(String keyword, Long courseId, int pageNum, int pageSize);
    
    /**
     * 根据课程ID获取所有附件
     */
    List<CourseAttachmentDocument> getAttachmentsByCourseId(Long courseId);
    
    /**
     * 重建索引（将数据库中的附件全部同步到Elasticsearch）
     */
    void rebuildIndex();
}
