-- =============================================
-- 添加短信日志表
-- =============================================

USE student_management_sys;

-- 创建短信日志表
CREATE TABLE IF NOT EXISTS sms_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '短信日志ID',
    user_id BIGINT NOT NULL COMMENT '接收者ID',
    user_type VARCHAR(20) NOT NULL COMMENT '接收者类型：student-学生, teacher-教师',
    phone VARCHAR(20) NOT NULL COMMENT '接收者手机号',
    title VARCHAR(200) NOT NULL COMMENT '短信标题',
    content TEXT NOT NULL COMMENT '短信内容',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '发送状态：pending-待发送, success-成功, failed-失败',
    error_msg VARCHAR(500) COMMENT '失败原因',
    related_type VARCHAR(50) COMMENT '相关业务类型：course-课程, selection-选课, drop-退课',
    related_id BIGINT COMMENT '相关业务ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user (user_id, user_type),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='短信日志表';

-- 添加索引以提高查询性能
ALTER TABLE sms_log ADD INDEX idx_phone (phone);
ALTER TABLE sms_log ADD INDEX idx_related (related_type, related_id);

COMMIT;
