USE student_management_sys;

-- 删除旧的通知表
DROP TABLE IF EXISTS notification;

-- 创建正确的通知表（匹配 Notification.java 实体类）
CREATE TABLE notification (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    user_type VARCHAR(20) NOT NULL COMMENT '用户类型（student/teacher）',
    title VARCHAR(200) NOT NULL COMMENT '通知标题',
    content TEXT COMMENT '通知内容',
    type VARCHAR(50) COMMENT '通知类型（selection=选课, grade=成绩, system=系统）',
    related_id BIGINT COMMENT '关联ID（如课程ID）',
    url VARCHAR(500) COMMENT '跳转链接',
    status INT DEFAULT 0 COMMENT '状态（0=未读, 1=已读）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_user_type (user_type),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知表';

-- 验证表结构
DESC notification;

-- 查看表是否创建成功
SELECT 'Notification table created successfully!' AS message;
