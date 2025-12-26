package com.myweb.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import java.time.LocalDateTime;

/**
 * 课程 Elasticsearch 文档
 */
@Data
@Document(indexName = "courses")
public class CourseDocument {
    
    @Id
    private Long id;
    
    /**
     * 课程名称
     */
    @Field(type = FieldType.Text)
    private String courseName;
    
    /**
     * 学分
     */
    @Field(type = FieldType.Integer)
    private Integer credit;
    
    /**
     * 所属学院ID
     */
    @Field(type = FieldType.Long)
    private Long collegeId;
    
    /**
     * 所属学院名称
     */
    @Field(type = FieldType.Text)
    private String collegeName;
    
    /**
     * 授课教师ID
     */
    @Field(type = FieldType.Long)
    private Long teacherId;
    
    /**
     * 授课教师姓名
     */
    @Field(type = FieldType.Text)
    private String teacherName;
    
    /**
     * 上课时段
     */
    @Field(type = FieldType.Keyword)
    private String schedule;
    
    /**
     * 上课地点
     */
    @Field(type = FieldType.Text)
    private String location;
    
    /**
     * 最大选课人数
     */
    @Field(type = FieldType.Integer)
    private Integer maxStudents;
    
    /**
     * 已选人数
     */
    @Field(type = FieldType.Integer)
    private Integer selectedCount;
    
    /**
     * 课程描述
     */
    @Field(type = FieldType.Text)
    private String description;
    
    /**
     * 创建时间
     */
    @Field(type = FieldType.Date)
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @Field(type = FieldType.Date)
    private LocalDateTime updateTime;
}
