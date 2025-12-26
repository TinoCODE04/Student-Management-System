package com.myweb.controller;

import com.myweb.common.Result;
import com.myweb.document.CourseAttachmentDocument;
import com.myweb.entity.CourseAttachment;
import com.myweb.service.AttachmentSearchService;
import com.myweb.service.CourseAttachmentService;
import com.myweb.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 课程附件Controller
 */
@RestController
@RequestMapping("/api/course/attachment")
@Slf4j
public class CourseAttachmentController {
    
    @Autowired
    private CourseAttachmentService courseAttachmentService;
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private AttachmentSearchService attachmentSearchService;
    
    /**
     * 上传课程附件
     */
    @PostMapping("/upload")
    public Result<CourseAttachment> upload(@RequestParam("courseId") Long courseId,
                                          @RequestParam("file") MultipartFile file,
                                          @RequestParam(value = "description", required = false) String description,
                                          @RequestAttribute("userId") Long userId,
                                          @RequestAttribute("role") String role) {
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }
        
        // 文件大小限制（10MB）
        if (file.getSize() > 10 * 1024 * 1024) {
            return Result.error("文件大小不能超过10MB");
        }
        
        try {
            CourseAttachment attachment = courseAttachmentService.uploadAttachment(
                    courseId, file, userId, role, description);
            
            log.info("Attachment uploaded: courseId={}, userId={}, filename={}", 
                    courseId, userId, file.getOriginalFilename());
            
            return Result.success("上传成功", attachment);
        } catch (Exception e) {
            log.error("Error uploading attachment", e);
            return Result.error("上传失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取课程的所有附件
     */
    @GetMapping("/list/{courseId}")
    public Result<List<CourseAttachment>> list(@PathVariable Long courseId) {
        List<CourseAttachment> attachments = courseAttachmentService.listByCourseId(courseId);
        return Result.success(attachments);
    }
    
    /**
     * 删除附件
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id,
                               @RequestAttribute("userId") Long userId,
                               @RequestAttribute("role") String role) {
        try {
            boolean success = courseAttachmentService.deleteAttachment(id, userId, role);
            if (success) {
                return Result.success("删除成功", null);
            } else {
                return Result.error("附件不存在");
            }
        } catch (Exception e) {
            log.error("Error deleting attachment", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 下载附件（增加下载次数）
     */
    @PostMapping("/download/{id}")
    public Result<Void> download(@PathVariable Long id) {
        courseAttachmentService.incrementDownloadCount(id);
        return Result.success();
    }
    
    /**
     * 搜索课程附件（支持全文搜索）
     * @param keyword 搜索关键词（文件名、描述、内容）
     * @param courseId 课程ID（可选，为null则搜索所有课程）
     * @param pageNum 页码
     * @param pageSize 每页大小
     */
    @GetMapping("/search")
    public Result<Page<CourseAttachmentDocument>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long courseId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        try {
            Page<CourseAttachmentDocument> result = attachmentSearchService.searchAttachments(
                    keyword, courseId, pageNum, pageSize);
            return Result.success(result);
        } catch (Exception e) {
            log.error("Error searching attachments", e);
            return Result.error("搜索失败: " + e.getMessage());
        }
    }
    
    /**
     * 重建Elasticsearch索引（管理员功能）
     */
    @PostMapping("/rebuild-index")
    public Result<Void> rebuildIndex(@RequestAttribute("role") String role) {
        if (!"admin".equals(role) && !"teacher".equals(role)) {
            return Result.error("无权限执行此操作");
        }
        
        try {
            attachmentSearchService.rebuildIndex();
            return Result.success("索引重建成功", null);
        } catch (Exception e) {
            log.error("Error rebuilding index", e);
            return Result.error("索引重建失败: " + e.getMessage());
        }
    }
}
