package com.myweb.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.myweb.entity.CourseAttachment;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 课程附件Service
 */
public interface CourseAttachmentService extends IService<CourseAttachment> {
    
    /**
     * 上传课程附件
     */
    CourseAttachment uploadAttachment(Long courseId, MultipartFile file, Long uploaderId, 
                                      String uploaderType, String description);
    
    /**
     * 获取课程的所有附件
     */
    List<CourseAttachment> listByCourseId(Long courseId);
    
    /**
     * 删除附件（同时删除MinIO中的文件）
     */
    boolean deleteAttachment(Long id, Long userId, String role);
    
    /**
     * 增加下载次数
     */
    void incrementDownloadCount(Long id);
}
