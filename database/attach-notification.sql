USE student_management_sys;

-- 删除旧表（如果存在）
DROP TABLE IF EXISTS course_attachment;

-- 创建附件表（完整版本，匹配后端实体类）
CREATE TABLE course_attachment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    filename VARCHAR(255) NOT NULL COMMENT '文件名（MinIO中的名称）',
    original_filename VARCHAR(255) NOT NULL COMMENT '原始文件名',
    content_type VARCHAR(100) COMMENT '文件类型',
    file_size BIGINT COMMENT '文件大小（字节）',
    minio_key VARCHAR(500) NOT NULL COMMENT 'MinIO存储路径',
    uploader_id BIGINT NOT NULL COMMENT '上传者ID',
    uploader_type VARCHAR(20) NOT NULL COMMENT '上传者类型（teacher/student）',
    description VARCHAR(500) COMMENT '附件描述',
    download_count INT DEFAULT 0 COMMENT '下载次数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_course_id (course_id),
    INDEX idx_uploader (uploader_id, uploader_type),
    FOREIGN KEY (course_id) REFERENCES course(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程附件表';


-- 删除旧通知表（如果存在）
DROP TABLE IF EXISTS notification;

-- 创建通知表
CREATE TABLE notification (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    title VARCHAR(200) NOT NULL COMMENT '通知标题',
    content TEXT COMMENT '通知内容',
    type VARCHAR(50) COMMENT '通知类型',
    is_read TINYINT DEFAULT 0 COMMENT '是否已读（0未读，1已读）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_is_read (is_read),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知表';

-- 验证表结构
SHOW TABLES LIKE 'course_attachment';
SHOW TABLES LIKE 'notification';
DESC course_attachment;
DESC notification;