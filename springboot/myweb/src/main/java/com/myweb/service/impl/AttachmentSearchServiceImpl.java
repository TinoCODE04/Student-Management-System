package com.myweb.service.impl;

import com.myweb.document.CourseAttachmentDocument;
import com.myweb.entity.Admin;
import com.myweb.entity.Course;
import com.myweb.entity.CourseAttachment;
import com.myweb.entity.Student;
import com.myweb.entity.Teacher;
import com.myweb.mapper.AdminMapper;
import com.myweb.mapper.CourseAttachmentMapper;
import com.myweb.mapper.CourseMapper;
import com.myweb.mapper.StudentMapper;
import com.myweb.mapper.TeacherMapper;
import com.myweb.repository.CourseAttachmentSearchRepository;
import com.myweb.service.AttachmentSearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 课程附件搜索Service实现
 */
@Service
@Slf4j
public class AttachmentSearchServiceImpl implements AttachmentSearchService {
    
    @Autowired(required = false)
    private CourseAttachmentSearchRepository searchRepository;
    
    @Autowired(required = false)
    private ElasticsearchOperations elasticsearchOperations;
    
    @Autowired
    private CourseAttachmentMapper attachmentMapper;
    
    @Autowired
    private CourseMapper courseMapper;
    
    @Autowired
    private TeacherMapper teacherMapper;
    
    @Autowired
    private StudentMapper studentMapper;
    
    @Autowired
    private AdminMapper adminMapper;
    
    @Override
    public void indexAttachment(CourseAttachmentDocument document) {
        if (searchRepository == null) {
            log.warn("Elasticsearch is not available, skipping indexing");
            return;
        }
        try {
            searchRepository.save(document);
            log.info("Indexed attachment to ES: id={}, filename={}", document.getId(), document.getOriginalFilename());
        } catch (Exception e) {
            log.error("Failed to index attachment to ES: id={}", document.getId(), e);
        }
    }
    
    @Override
    public void deleteAttachment(Long id) {
        if (searchRepository == null) {
            log.warn("Elasticsearch is not available, skipping deletion");
            return;
        }
        try {
            searchRepository.deleteById(id);
            log.info("Deleted attachment from ES: id={}", id);
        } catch (Exception e) {
            log.error("Failed to delete attachment from ES: id={}", id, e);
        }
    }
    
    @Override
    public void updateAttachment(CourseAttachmentDocument document) {
        indexAttachment(document);
    }
    
    @Override
    public Page<CourseAttachmentDocument> searchAttachments(String keyword, Long courseId, int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        
        if (elasticsearchOperations == null) {
            log.warn("Elasticsearch is not available, returning empty page");
            return Page.empty(pageable);
        }
        
        try {
            // 构建搜索条件
            Criteria criteria = new Criteria();
            
            // 如果指定了课程ID，则只搜索该课程的附件
            if (courseId != null) {
                criteria = criteria.and("courseId").is(courseId);
            }
            
            // 如果有关键词，则在多个字段中搜索
            if (keyword != null && !keyword.trim().isEmpty()) {
                Criteria keywordCriteria = new Criteria()
                        .or("originalFilename").contains(keyword)
                        .or("description").contains(keyword)
                        .or("content").contains(keyword)
                        .or("courseName").contains(keyword)
                        .or("uploaderName").contains(keyword);
                
                criteria = criteria.subCriteria(keywordCriteria);
            }
            
            Query query = new CriteriaQuery(criteria).setPageable(pageable);
            SearchHits<CourseAttachmentDocument> searchHits = elasticsearchOperations.search(query, CourseAttachmentDocument.class);
            
            List<CourseAttachmentDocument> content = searchHits.getSearchHits()
                    .stream()
                    .map(SearchHit::getContent)
                    .collect(Collectors.toList());
            
            return PageableExecutionUtils.getPage(content, pageable, searchHits::getTotalHits);
            
        } catch (Exception e) {
            log.error("Search attachments failed: keyword={}, courseId={}", keyword, courseId, e);
            return Page.empty(pageable);
        }
    }
    
    @Override
    public List<CourseAttachmentDocument> getAttachmentsByCourseId(Long courseId) {
        if (searchRepository == null) {
            log.warn("Elasticsearch is not available");
            return List.of();
        }
        return searchRepository.findByCourseId(courseId);
    }
    
    @Override
    public void rebuildIndex() {
        if (searchRepository == null) {
            log.warn("Elasticsearch is not available, cannot rebuild index");
            return;
        }
        
        try {
            log.info("Starting to rebuild Elasticsearch index for course attachments...");
            
            // 清空现有索引
            searchRepository.deleteAll();
            
            // 从数据库获取所有附件
            List<CourseAttachment> attachments = attachmentMapper.selectList(null);
            
            log.info("Found {} attachments in database", attachments.size());
            
            // 批量索引到Elasticsearch
            for (CourseAttachment attachment : attachments) {
                CourseAttachmentDocument document = convertToDocument(attachment);
                searchRepository.save(document);
            }
            
            log.info("Successfully rebuilt Elasticsearch index with {} attachments", attachments.size());
            
        } catch (Exception e) {
            log.error("Failed to rebuild Elasticsearch index", e);
        }
    }
    
    /**
     * 将CourseAttachment实体转换为Elasticsearch文档
     */
    private CourseAttachmentDocument convertToDocument(CourseAttachment attachment) {
        CourseAttachmentDocument document = new CourseAttachmentDocument();
        BeanUtils.copyProperties(attachment, document);
        
        // 补充课程名称
        Course course = courseMapper.selectById(attachment.getCourseId());
        if (course != null) {
            document.setCourseName(course.getCourseName());
        }
        
        // 补充上传者姓名
        if ("teacher".equals(attachment.getUploaderType())) {
            Teacher teacher = teacherMapper.selectById(attachment.getUploaderId());
            if (teacher != null) {
                document.setUploaderName(teacher.getName());
            }
        } else if ("student".equals(attachment.getUploaderType())) {
            Student student = studentMapper.selectById(attachment.getUploaderId());
            if (student != null) {
                document.setUploaderName(student.getName());
            }
        } else if ("admin".equals(attachment.getUploaderType())) {
            Admin admin = adminMapper.selectById(attachment.getUploaderId());
            if (admin != null) {
                document.setUploaderName(admin.getName());
            }
        }
        
        // 注意：这里content字段暂时为空，后续可以集成文档内容提取（如Apache Tika）
        // 目前只支持文件名和描述的搜索
        
        return document;
    }
}
