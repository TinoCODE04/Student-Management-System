package com.myweb.service;

import com.myweb.document.CourseDocument;
import com.myweb.entity.*;
import com.myweb.mapper.*;
import com.myweb.repository.CourseRepository;
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
 * 课程搜索服务
 */
@Slf4j
@Service
public class CourseSearchService {
    
    @Autowired(required = false)
    private CourseRepository courseRepository;
    
    @Autowired(required = false)
    private ElasticsearchOperations elasticsearchOperations;
    
    @Autowired
    private CourseMapper courseMapper;
    
    @Autowired
    private CollegeMapper collegeMapper;
    
    @Autowired
    private TeacherMapper teacherMapper;
    
    /**
     * 索引课程
     */
    public void indexCourse(CourseDocument document) {
        if (courseRepository != null) {
            try {
                courseRepository.save(document);
                log.info("成功索引课程: {}", document.getCourseName());
            } catch (Exception e) {
                log.error("索引课程失败: {}", e.getMessage());
            }
        }
    }
    
    /**
     * 删除课程索引
     */
    public void deleteCourse(Long courseId) {
        if (courseRepository != null) {
            try {
                courseRepository.deleteById(courseId);
                log.info("成功删除课程索引: {}", courseId);
            } catch (Exception e) {
                log.error("删除课程索引失败: {}", e.getMessage());
            }
        }
    }
    
    /**
     * 搜索课程
     */
    public Page<CourseDocument> searchCourses(String keyword, Long collegeId, Integer minCredit, Integer maxCredit, int pageNum, int pageSize) {
        if (courseRepository == null || elasticsearchOperations == null) {
            log.warn("Elasticsearch 未配置，无法搜索");
            return Page.empty();
        }
        
        try {
            // 构建查询
            var queryBuilder = co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders.bool();
            
            // 关键词搜索（课程名、教师名、学院名、描述、地点）
            if (keyword != null && !keyword.trim().isEmpty()) {
                queryBuilder.must(m -> m.multiMatch(mm -> mm
                    .query(keyword)
                    .fields("courseName", "teacherName", "collegeName", "description", "location")
                ));
            }
            
            // 按学院筛选
            if (collegeId != null) {
                queryBuilder.filter(f -> f.term(t -> t
                    .field("collegeId")
                    .value(collegeId)
                ));
            }
            
            // 按学分范围筛选
            if (minCredit != null || maxCredit != null) {
                queryBuilder.filter(f -> f.range(r -> {
                    var rangeQuery = r.field("credit");
                    if (minCredit != null) {
                        rangeQuery.gte(co.elastic.clients.json.JsonData.of(minCredit));
                    }
                    if (maxCredit != null) {
                        rangeQuery.lte(co.elastic.clients.json.JsonData.of(maxCredit));
                    }
                    return rangeQuery;
                }));
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
            
            SearchHits<CourseDocument> searchHits = elasticsearchOperations.search(query, CourseDocument.class);
            
            List<CourseDocument> courses = searchHits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
            
            return new org.springframework.data.domain.PageImpl<>(
                courses,
                PageRequest.of(pageNum - 1, pageSize),
                searchHits.getTotalHits()
            );
            
        } catch (Exception e) {
            log.error("搜索课程失败: {}", e.getMessage(), e);
            return Page.empty();
        }
    }
    
    /**
     * 重建课程索引
     */
    public String rebuildIndex() {
        if (courseRepository == null || elasticsearchOperations == null) {
            return "Elasticsearch 未配置，无法重建索引";
        }
        
        try {
            log.info("开始重建课程索引...");
            
            // 删除所有现有索引
            courseRepository.deleteAll();
            log.info("已清空现有课程索引");
            
            // 从数据库读取所有课程
            List<Course> courses = courseMapper.selectList(null);
            log.info("从数据库读取到 {} 条课程记录", courses.size());
            
            // 批量索引
            List<CourseDocument> documents = new ArrayList<>();
            for (Course course : courses) {
                CourseDocument document = convertToDocument(course);
                documents.add(document);
            }
            
            courseRepository.saveAll(documents);
            log.info("课程索引重建完成，共索引 {} 条记录", documents.size());
            
            return "成功重建课程索引，共 " + documents.size() + " 条记录";
            
        } catch (Exception e) {
            log.error("重建课程索引失败: {}", e.getMessage(), e);
            return "重建课程索引失败: " + e.getMessage();
        }
    }
    
    /**
     * 将 Course 实体转换为 CourseDocument
     */
    public CourseDocument convertToDocument(Course course) {
        CourseDocument document = new CourseDocument();
        document.setId(course.getId());
        document.setCourseName(course.getCourseName());
        document.setCredit(course.getCredit());
        document.setCollegeId(course.getCollegeId());
        document.setTeacherId(course.getTeacherId());
        document.setSchedule(course.getSchedule());
        document.setLocation(course.getLocation());
        document.setMaxStudents(course.getMaxStudents());
        document.setSelectedCount(course.getSelectedCount());
        document.setDescription(course.getDescription());
        document.setCreateTime(course.getCreateTime());
        document.setUpdateTime(course.getUpdateTime());
        
        // 获取学院名称
        if (course.getCollegeId() != null) {
            College college = collegeMapper.selectById(course.getCollegeId());
            if (college != null) {
                document.setCollegeName(college.getCollegeName());
            }
        }
        
        // 获取教师姓名
        if (course.getTeacherId() != null) {
            Teacher teacher = teacherMapper.selectById(course.getTeacherId());
            if (teacher != null) {
                document.setTeacherName(teacher.getName());
            }
        }
        
        return document;
    }
}
