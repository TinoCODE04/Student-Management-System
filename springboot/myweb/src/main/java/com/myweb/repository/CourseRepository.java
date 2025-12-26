package com.myweb.repository;

import com.myweb.document.CourseDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 课程 Elasticsearch 仓库
 */
@Repository
public interface CourseRepository extends ElasticsearchRepository<CourseDocument, Long> {
    
    /**
     * 根据课程名称查询
     */
    List<CourseDocument> findByCourseName(String courseName);
    
    /**
     * 根据学院ID查询
     */
    List<CourseDocument> findByCollegeId(Long collegeId);
    
    /**
     * 根据教师ID查询
     */
    List<CourseDocument> findByTeacherId(Long teacherId);
}
