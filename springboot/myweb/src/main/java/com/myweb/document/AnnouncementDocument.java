package com.myweb.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import java.time.LocalDateTime;

/**
 * 公告 Elasticsearch 文档
 */
@Data
@Document(indexName = "announcements")
public class AnnouncementDocument {
    
    @Id
    private Long id;
    
    /**
     * 公告标题
     */
    @Field(type = FieldType.Text)
    private String title;
    
    /**
     * 公告内容
     */
    @Field(type = FieldType.Text)
    private String content;
    
    /**
     * 目标角色: all/teacher/student
     */
    @Field(type = FieldType.Keyword)
    private String targetRole;
    
    /**
     * 优先级: high/medium/low
     */
    @Field(type = FieldType.Keyword)
    private String priority;
    
    /**
     * 发布者ID
     */
    @Field(type = FieldType.Long)
    private Long publisherId;
    
    /**
     * 发布者姓名
     */
    @Field(type = FieldType.Text)
    private String publisherName;
    
    /**
     * 发布者类型
     */
    @Field(type = FieldType.Keyword)
    private String publisherType;
    
    /**
     * 浏览次数（预留字段，可以后续添加到数据库）
     */
    @Field(type = FieldType.Integer)
    private Integer viewCount;
    
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
