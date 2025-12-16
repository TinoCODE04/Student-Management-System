package com.myweb.service;

import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * MinIO文件存储服务
 */
@Service
@Slf4j
public class MinioService {
    
    @Autowired
    private MinioClient minioClient;
    
    @Value("${minio.bucket-name}")
    private String bucketName;
    
    /**
     * 初始化bucket（如果不存在则创建）
     */
    public void ensureBucketExists() {
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(bucketName)
                    .build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(bucketName)
                        .build());
                log.info("Bucket {} created successfully", bucketName);
            }
        } catch (Exception e) {
            log.error("Error creating bucket", e);
            throw new RuntimeException("创建存储桶失败", e);
        }
    }
    
    /**
     * 上传文件
     * @param file 文件
     * @param folder 文件夹（如 "course/123/"）
     * @return MinIO对象名称（路径）
     */
    public String uploadFile(MultipartFile file, String folder) {
        try {
            ensureBucketExists();
            
            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String objectName = folder + UUID.randomUUID().toString() + extension;
            
            // 上传文件
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
            
            log.info("File uploaded successfully: {}", objectName);
            return objectName;
        } catch (Exception e) {
            log.error("Error uploading file", e);
            throw new RuntimeException("文件上传失败", e);
        }
    }
    
    /**
     * 删除文件
     * @param objectName 对象名称
     */
    public void deleteFile(String objectName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
            log.info("File deleted successfully: {}", objectName);
        } catch (Exception e) {
            log.error("Error deleting file", e);
            throw new RuntimeException("文件删除失败", e);
        }
    }
    
    /**
     * 获取文件访问URL（有效期7天）
     * @param objectName 对象名称
     * @return 访问URL
     */
    public String getFileUrl(String objectName) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(objectName)
                            .expiry(7, TimeUnit.DAYS)
                            .build()
            );
        } catch (Exception e) {
            log.error("Error getting file URL", e);
            throw new RuntimeException("获取文件URL失败", e);
        }
    }
    
    /**
     * 下载文件流
     * @param objectName 对象名称
     * @return 文件输入流
     */
    public InputStream downloadFile(String objectName) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
        } catch (Exception e) {
            log.error("Error downloading file", e);
            throw new RuntimeException("文件下载失败", e);
        }
    }
}
