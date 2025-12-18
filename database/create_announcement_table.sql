-- 创建系统公告表
CREATE TABLE IF NOT EXISTS announcement (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '公告ID',
    title VARCHAR(200) NOT NULL COMMENT '公告标题',
    content TEXT NOT NULL COMMENT '公告内容',
    target_role VARCHAR(20) NOT NULL COMMENT '目标角色: all/teacher/student',
    priority VARCHAR(20) DEFAULT 'medium' COMMENT '优先级: high/medium/low',
    publisher_id BIGINT NOT NULL COMMENT '发布者ID',
    publisher_name VARCHAR(50) NOT NULL COMMENT '发布者姓名',
    publisher_type VARCHAR(20) DEFAULT 'admin' COMMENT '发布者类型',
    status INT DEFAULT 1 COMMENT '状态: 0-已删除 1-正常',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_target_role (target_role),
    INDEX idx_create_time (create_time),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统公告表';
