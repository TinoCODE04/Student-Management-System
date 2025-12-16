package com.myweb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.myweb.entity.CourseAttachment;
import com.myweb.entity.Student;
import com.myweb.entity.Teacher;
import com.myweb.mapper.CourseAttachmentMapper;
import com.myweb.service.CourseAttachmentService;
import com.myweb.service.MinioService;
import com.myweb.service.StudentService;
import com.myweb.service.TeacherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 课程附件Service实现
 */
@Service
@Slf4j
public class CourseAttachmentServiceImpl extends ServiceImpl<CourseAttachmentMapper, CourseAttachment> 
        implements CourseAttachmentService {
    
    @Autowired
    private MinioService minioService;
    
    @Autowired
    private TeacherService teacherService;
    
    @Autowired
    private StudentService studentService;
    
    @Override
    @Transactional
    public CourseAttachment uploadAttachment(Long courseId, MultipartFile file, Long uploaderId, 
                                             String uploaderType, String description) {
        try {
            // 上传到MinIO
            String folder = "course/" + courseId + "/";
            String minioKey = minioService.uploadFile(file, folder);
            
            // 保存附件信息
            CourseAttachment attachment = new CourseAttachment();
            attachment.setCourseId(courseId);
            attachment.setFilename(file.getOriginalFilename());
            attachment.setOriginalFilename(file.getOriginalFilename());
            attachment.setContentType(file.getContentType());
            attachment.setFileSize(file.getSize());
            attachment.setMinioKey(minioKey);
            attachment.setUploaderId(uploaderId);
            attachment.setUploaderType(uploaderType);
            attachment.setDescription(description);
            attachment.setDownloadCount(0);
            
            save(attachment);
            
            log.info("Attachment uploaded successfully: courseId={}, filename={}", courseId, file.getOriginalFilename());
            return attachment;
        } catch (Exception e) {
            log.error("Error uploading attachment", e);
            throw new RuntimeException("附件上传失败: " + e.getMessage());
        }
    }
    
    @Override
    public List<CourseAttachment> listByCourseId(Long courseId) {
        LambdaQueryWrapper<CourseAttachment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseAttachment::getCourseId, courseId)
               .orderByDesc(CourseAttachment::getCreateTime);
        
        List<CourseAttachment> attachments = list(wrapper);
        
        // 填充文件URL和上传者姓名
        for (CourseAttachment attachment : attachments) {
            attachment.setFileUrl(minioService.getFileUrl(attachment.getMinioKey()));
            
            // 获取上传者姓名
            if ("teacher".equals(attachment.getUploaderType())) {
                Teacher teacher = teacherService.getById(attachment.getUploaderId());
                if (teacher != null) {
                    attachment.setUploaderName(teacher.getName());
                }
            } else if ("student".equals(attachment.getUploaderType())) {
                Student student = studentService.getById(attachment.getUploaderId());
                if (student != null) {
                    attachment.setUploaderName(student.getName());
                }
            }
        }
        
        return attachments;
    }
    
    @Override
    @Transactional
    public boolean deleteAttachment(Long id, Long userId, String role) {
        CourseAttachment attachment = getById(id);
        if (attachment == null) {
            return false;
        }
        
        // 权限检查：只有上传者或教师可以删除
        if (!"teacher".equals(role) && !userId.equals(attachment.getUploaderId())) {
            throw new RuntimeException("无权限删除此附件");
        }
        
        // 删除MinIO中的文件
        try {
            minioService.deleteFile(attachment.getMinioKey());
        } catch (Exception e) {
            log.error("Error deleting file from MinIO", e);
        }
        
        // 删除数据库记录
        return removeById(id);
    }
    
    @Override
    public void incrementDownloadCount(Long id) {
        CourseAttachment attachment = getById(id);
        if (attachment != null) {
            attachment.setDownloadCount(attachment.getDownloadCount() + 1);
            updateById(attachment);
        }
    }
}
