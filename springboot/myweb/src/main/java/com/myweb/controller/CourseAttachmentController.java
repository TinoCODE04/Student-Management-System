package com.myweb.controller;

import com.myweb.common.Result;
import com.myweb.entity.CourseAttachment;
import com.myweb.service.CourseAttachmentService;
import com.myweb.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
}
