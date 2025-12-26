package com.myweb.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

/**
 * 课程附件Elasticsearch文档
 * 用于全文搜索课程附件（支持文件名、描述、内容搜索）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "course_attachments")
public class CourseAttachmentDocument {
    
    @Id
    private Long id;
    
    /**
     * 课程ID
     */
    @Field(type = FieldType.Long)
    private Long courseId;
    
    /**
     * 课程名称（用于搜索显示）
     */
    @Field(type = FieldType.Text)
    private String courseName;
    
    /**
     * 文件名（存储名）
     */
    @Field(type = FieldType.Keyword)
    private String filename;
    
    /**
     * 原始文件名（支持中文搜索）
     */
    @Field(type = FieldType.Text)
    private String originalFilename;
    
    /**
     * 文件类型
     */
    @Field(type = FieldType.Keyword)
    private String contentType;
    
    /**
     * 文件大小
     */
    @Field(type = FieldType.Long)
    private Long fileSize;
    
    /**
     * MinIO存储路径
     */
    @Field(type = FieldType.Keyword)
    private String minioKey;
    
    /**
     * 上传者ID
     */
    @Field(type = FieldType.Long)
    private Long uploaderId;
    
    /**
     * 上传者类型（teacher/admin）
     */
    @Field(type = FieldType.Keyword)
    private String uploaderType;
    
    /**
     * 上传者姓名
     */
    @Field(type = FieldType.Text)
    private String uploaderName;
    
    /**
     * 文件描述（支持全文搜索）
     */
    @Field(type = FieldType.Text)
    private String description;
    
    /**
     * 文件内容（提取的文本内容，支持全文搜索）
     * 用于Word、PDF、PPT等文档的内容搜索
     */
    @Field(type = FieldType.Text)
    private String content;
    
    /**
     * 下载次数
     */
    @Field(type = FieldType.Integer)
    private Integer downloadCount;
    
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
