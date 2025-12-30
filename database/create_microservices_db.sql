-- =============================================
-- 学生信息管理系统 - 微服务版数据库脚本
-- 数据库名：student_management_sys_cloud
-- 用户：root  密码：010804
-- 版本：Microservices v1.0
-- 说明：基于 init.sql，增加微服务所需字段
-- =============================================

-- 创建数据库
DROP DATABASE IF EXISTS student_management_sys_cloud;
CREATE DATABASE student_management_sys_cloud DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE student_management_sys_cloud;

-- =============================================
-- 1. 学院表 (college)
-- =============================================
CREATE TABLE college (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '学院ID',
    college_name VARCHAR(100) NOT NULL COMMENT '学院名称',
    min_credit INT DEFAULT 0 COMMENT '最低学分要求',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学院信息表';

-- =============================================
-- 2. 专业表 (major)
-- =============================================
CREATE TABLE major (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '专业ID',
    major_name VARCHAR(100) NOT NULL COMMENT '专业名称',
    department_name VARCHAR(100) COMMENT '系名',
    college_id BIGINT NOT NULL COMMENT '所属学院ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (college_id) REFERENCES college(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='专业信息表';

-- =============================================
-- 3. 教师表 (teacher) - 微服务版增强
-- =============================================
CREATE TABLE teacher (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '教师ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名（登录账号）',
    password VARCHAR(255) NOT NULL COMMENT '密码（BCrypt加密）',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    teacher_no VARCHAR(50) UNIQUE COMMENT '教工号',  -- 微服务新增
    gender VARCHAR(10) DEFAULT '男' COMMENT '性别',
    title VARCHAR(50) COMMENT '职称',
    college_id BIGINT COMMENT '所属学院ID',  -- 微服务新增
    major_id BIGINT COMMENT '所属专业ID',
    role VARCHAR(20) DEFAULT 'teacher' COMMENT '角色',
    avatar VARCHAR(255) COMMENT '头像URL',
    phone VARCHAR(20) COMMENT '联系电话',
    email VARCHAR(100) COMMENT '邮箱',
    status VARCHAR(20) DEFAULT 'active' COMMENT '账号状态：active正常，disabled禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (college_id) REFERENCES college(id) ON DELETE SET NULL,
    FOREIGN KEY (major_id) REFERENCES major(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教师信息表';

-- =============================================
-- 4. 学生表 (student)
-- =============================================
CREATE TABLE student (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '学生ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名（登录账号）',
    password VARCHAR(255) NOT NULL COMMENT '密码（BCrypt加密）',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    gender VARCHAR(10) DEFAULT '男' COMMENT '性别',
    student_no VARCHAR(50) NOT NULL UNIQUE COMMENT '学号',
    total_credit INT DEFAULT 0 COMMENT '总学分',
    college_id BIGINT COMMENT '所属学院ID',
    major_id BIGINT COMMENT '所属专业ID',
    class_name VARCHAR(50) COMMENT '班级名称',
    grade VARCHAR(10) COMMENT '年级（如2021）',
    birth_date DATETIME COMMENT '出生日期',
    enroll_date DATETIME COMMENT '入学日期',
    status VARCHAR(20) DEFAULT 'active' COMMENT '账号状态：active正常，disabled禁用',
    role VARCHAR(20) DEFAULT 'student' COMMENT '角色',
    avatar VARCHAR(255) COMMENT '头像URL',
    phone VARCHAR(20) COMMENT '联系电话',
    email VARCHAR(100) COMMENT '邮箱',
    age INT COMMENT '年龄',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (college_id) REFERENCES college(id) ON DELETE SET NULL,
    FOREIGN KEY (major_id) REFERENCES major(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生信息表';

-- =============================================
-- 5. 课程表 (course) - 微服务版增强
-- =============================================
CREATE TABLE course (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '课程ID',
    course_no VARCHAR(50) COMMENT '课程编号',  -- 微服务新增
    course_name VARCHAR(100) NOT NULL COMMENT '课程名称',
    semester VARCHAR(20) DEFAULT '2024-2025-1' COMMENT '学期',  -- 微服务新增
    credit INT DEFAULT 0 COMMENT '学分',
    college_id BIGINT COMMENT '所属学院ID',
    teacher_id BIGINT COMMENT '上课教师ID',
    schedule VARCHAR(100) COMMENT '上课时段',
    location VARCHAR(100) COMMENT '上课地点',
    max_students INT DEFAULT 100 COMMENT '最大选课人数',
    selected_count INT DEFAULT 0 COMMENT '已选人数',
    description TEXT COMMENT '课程描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (college_id) REFERENCES college(id) ON DELETE SET NULL,
    FOREIGN KEY (teacher_id) REFERENCES teacher(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程信息表';

-- =============================================
-- 6. 选课表 (course_selection) - 微服务版
-- =============================================
-- 状态说明：
--   pending  - 待开课（可退课）
--   studying - 学习中（已选）
--   completed - 已完成（有成绩）
--   dropped  - 已退选
-- =============================================
CREATE TABLE course_selection (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '选课记录ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    score DECIMAL(5,2) COMMENT '成绩',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '状态：pending待开课，studying学习中，completed已完成，dropped已退选',  -- 微服务：VARCHAR类型
    select_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '选课时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_student_course (student_id, course_id),
    FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES course(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='选课信息表';

-- =============================================
-- 插入初始数据
-- =============================================

-- 插入学院数据 (4个学院)
INSERT INTO college (college_name, min_credit) VALUES
('计算机科学与技术学院', 160),
('电子信息工程学院', 155),
('经济管理学院', 145),
('外国语学院', 140);

-- 插入专业数据 (8个专业)
INSERT INTO major (major_name, department_name, college_id) VALUES
('软件工程', '软件工程系', 1),
('计算机科学与技术', '计算机系', 1),
('电子信息工程', '电子系', 2),
('通信工程', '通信系', 2),
('工商管理', '管理系', 3),
('会计学', '会计系', 3),
('英语', '英语系', 4),
('商务英语', '商务英语系', 4);

-- 插入教师数据 (6位教师，密码: 123456)
-- BCrypt加密后的123456: $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi
INSERT INTO teacher (username, password, name, teacher_no, gender, title, college_id, major_id, role, phone, email, status) VALUES
('teacher1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '张明华', 'T000001', '男', '教授', 1, 1, 'teacher', '13800138001', 'zhangmh@edu.com', 'active'),
('teacher2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '李晓红', 'T000002', '女', '副教授', 1, 2, 'teacher', '13800138002', 'lixh@edu.com', 'active'),
('teacher3', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '王建国', 'T000003', '男', '讲师', 2, 3, 'teacher', '13800138003', 'wangjg@edu.com', 'active'),
('teacher4', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '刘芳', 'T000004', '女', '教授', 3, 5, 'teacher', '13800138004', 'liufang@edu.com', 'active'),
('teacher5', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '陈伟', 'T000005', '男', '副教授', 3, 6, 'teacher', '13800138005', 'chenwei@edu.com', 'active'),
('teacher6', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '杨丽娜', 'T000006', '女', '讲师', 4, 7, 'teacher', '13800138006', 'yangln@edu.com', 'active');

-- 插入学生数据 (8位学生，密码: 123456)
INSERT INTO student (username, password, name, gender, student_no, total_credit, college_id, major_id, class_name, grade, birth_date, enroll_date, status, role, avatar, phone, email, age) VALUES
('student1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '赵子龙', '男', '2021001001', 68, 1, 1, '软件2101', '2021', '2003-05-15', '2021-09-01', 'active', 'student', 'p1.jpeg', '13900139001', 'zhaozl@stu.edu.com', 20),
('student2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '钱雨萱', '女', '2021001002', 72, 1, 1, '软件2101', '2021', '2002-08-22', '2021-09-01', 'active', 'student', 'p2.png', '13900139002', 'qianyx@stu.edu.com', 21),
('student3', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '孙浩然', '男', '2021002001', 55, 1, 2, '计科2101', '2021', '2003-03-10', '2021-09-01', 'active', 'student', 'p3.jpg', '13900139003', 'sunhr@stu.edu.com', 20),
('student4', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '李思琪', '女', '2021003001', 63, 2, 3, '电子2101', '2021', '2004-01-28', '2021-09-01', 'active', 'student', 'p4.jpg', '13900139004', 'lisq@stu.edu.com', 19),
('student5', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '周杰伦', '男', '2021004001', 48, 2, 4, '通信2101', '2021', '2002-11-05', '2021-09-01', 'active', 'student', 'p5.jpg', '13900139005', 'zhoujl@stu.edu.com', 21),
('student6', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '吴美玲', '女', '2021005001', 76, 3, 5, '工商2101', '2021', '2003-07-18', '2021-09-01', 'active', 'student', 'p6.jpg', '13900139006', 'wuml@stu.edu.com', 20),
('student7', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '郑凯文', '男', '2021006001', 52, 3, 6, '会计2101', '2021', '2003-09-25', '2021-09-01', 'active', 'student', 'p7.jpg', '13900139007', 'zhengkw@stu.edu.com', 20),
('student8', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '王小燕', '女', '2021007001', 61, 4, 7, '英语2101', '2021', '2002-12-12', '2021-09-01', 'active', 'student', 'p8.jpg', '13900139008', 'wangxy@stu.edu.com', 21);

-- 插入课程数据 (12门课程) - 带课程编号和学期
INSERT INTO course (course_no, course_name, semester, credit, college_id, teacher_id, schedule, location, max_students, selected_count, description) VALUES
-- 计算机学院课程
('CS0001', 'Java程序设计', '2024-2025-1', 4, 1, 1, '周一 1-2节', '教学楼A101', 60, 4, 'Java编程基础与面向对象设计'),
('CS0002', '数据结构与算法', '2024-2025-1', 4, 1, 1, '周二 3-4节', '教学楼A102', 55, 3, '常用数据结构与算法分析'),
('CS0003', '数据库原理', '2024-2025-1', 3, 1, 2, '周三 5-6节', '教学楼B201', 50, 3, '关系数据库设计与SQL语言'),
('CS0004', '计算机网络', '2024-2025-1', 3, 1, 2, '周四 1-2节', '教学楼B202', 50, 2, 'TCP/IP协议与网络编程'),
-- 电子信息学院课程
('EE0001', '电路分析', '2024-2025-1', 4, 2, 3, '周一 3-4节', '教学楼D101', 50, 2, '电路基础理论与分析方法'),
('EE0002', '数字电子技术', '2024-2025-1', 4, 2, 3, '周三 1-2节', '教学楼D103', 45, 2, '数字逻辑与组合电路'),
-- 经济管理学院课程
('BM0001', '管理学原理', '2024-2025-1', 3, 3, 4, '周二 7-8节', '教学楼F101', 60, 2, '现代管理理论与实践'),
('BM0002', '财务会计', '2024-2025-1', 4, 3, 5, '周四 5-6节', '教学楼F103', 50, 2, '会计核算与财务报表'),
('BM0003', '市场营销', '2024-2025-1', 3, 3, 4, '周三 7-8节', '教学楼F102', 55, 1, '营销策略与消费者行为'),
-- 外国语学院课程
('EN0001', '大学英语', '2024-2025-1', 2, 4, 6, '周五 5-6节', '教学楼G101', 80, 4, '英语听说读写综合训练'),
('EN0002', '英语口语', '2024-2025-1', 2, 4, 6, '周一 9-10节', '教学楼G102', 40, 2, '英语口语交际能力训练'),
('EN0003', '商务英语写作', '2024-2025-1', 3, 4, 6, '周三 9-10节', '教学楼G103', 45, 1, '商务英语文书写作技巧');

-- =============================================
-- 7. 管理员表 (admin)
-- =============================================
CREATE TABLE admin (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '管理员ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名（登录账号）',
    password VARCHAR(255) NOT NULL COMMENT '密码（BCrypt加密）',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    gender VARCHAR(10) DEFAULT '男' COMMENT '性别',
    role VARCHAR(20) DEFAULT 'admin' COMMENT '角色',
    avatar VARCHAR(255) COMMENT '头像URL',
    phone VARCHAR(20) COMMENT '联系电话',
    email VARCHAR(100) COMMENT '邮箱',
    status VARCHAR(20) DEFAULT 'active' COMMENT '账号状态：active正常，disabled禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员信息表';

-- =============================================
-- 8. 系统通知表 (notification)
-- =============================================
CREATE TABLE notification (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '通知ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    user_type VARCHAR(20) NOT NULL COMMENT '用户类型（student/teacher/admin）',
    title VARCHAR(200) NOT NULL COMMENT '通知标题',
    content TEXT COMMENT '通知内容',
    type VARCHAR(50) COMMENT '通知类型（selection=选课, grade=成绩, system=系统, drop=退课）',
    related_id BIGINT COMMENT '关联ID（如课程ID）',
    url VARCHAR(500) COMMENT '跳转链接',
    status INT DEFAULT 0 COMMENT '状态（0=未读, 1=已读）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_user_type (user_type),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统通知表';

-- =============================================
-- 9. 系统公告表 (announcement)
-- =============================================
CREATE TABLE announcement (
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

-- =============================================
-- 10. 课程附件表 (course_attachment)
-- =============================================
CREATE TABLE course_attachment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '附件ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    filename VARCHAR(255) NOT NULL COMMENT '存储文件名',
    original_filename VARCHAR(255) NOT NULL COMMENT '原始文件名',
    content_type VARCHAR(100) COMMENT '文件类型（MIME类型）',
    file_size BIGINT COMMENT '文件大小（字节）',
    minio_key VARCHAR(500) NOT NULL COMMENT 'MinIO存储键',
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

-- =============================================
-- 11. 短信日志表 (sms_log)
-- =============================================
CREATE TABLE sms_log (
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
    INDEX idx_phone (phone),
    INDEX idx_status (status),
    INDEX idx_related (related_type, related_id),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='短信日志表';

-- =============================================
-- 插入初始数据
-- =============================================

-- 插入管理员数据 (3位管理员，密码: admin123)
-- BCrypt加密后的 admin123: $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi
INSERT INTO admin (username, password, name, gender, role, phone, email, status) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '超级管理员', '男', 'admin', '13800000000', 'admin@system.com', 'active'),
('admin001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '张三丰', '男', 'admin', '13800000001', 'zhangsf@system.com', 'active'),
('admin002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '李四海', '女', 'admin', '13800000002', 'lish@system.com', 'active');

-- 插入系统公告数据
INSERT INTO announcement (title, content, target_role, priority, publisher_id, publisher_name, publisher_type, status) VALUES
('欢迎使用学生信息管理系统', '本系统已全面升级为微服务架构，现已支持管理员、教师、学生三级用户体系。请各位用户及时更新个人信息。', 'all', 'high', 1, '超级管理员', 'admin', 1),
('关于选课的重要通知', '本学期选课将于下周一开始，请各位同学提前了解课程信息，按时选课。', 'student', 'high', 1, '超级管理员', 'admin', 1),
('教师考核通知', '本学期教师考核工作即将开展，请各位教师做好准备。', 'teacher', 'medium', 1, '超级管理员', 'admin', 1);

-- 插入选课数据 (微服务版：status 为 VARCHAR 类型)
INSERT INTO course_selection (student_id, course_id, score, status, select_time) VALUES
-- 学生1: 赵子龙
(1, 1, 92.5, 'completed', '2024-09-01 10:00:00'),
(1, 2, 88.0, 'completed', '2024-09-01 10:05:00'),
(1, 3, NULL, 'studying', '2024-09-01 10:10:00'),
(1, 10, 85.0, 'completed', '2024-09-01 10:15:00'),
(1, 4, NULL, 'pending', '2024-09-01 10:16:00'),
-- 学生2: 钱雨萱
(2, 1, 95.0, 'completed', '2024-09-01 10:20:00'),
(2, 2, 91.5, 'completed', '2024-09-01 10:25:00'),
(2, 4, NULL, 'studying', '2024-09-01 10:30:00'),
(2, 10, 78.0, 'completed', '2024-09-01 10:35:00'),
(2, 11, NULL, 'pending', '2024-09-01 10:36:00'),
-- 学生3: 孙浩然
(3, 1, 78.5, 'completed', '2024-09-01 10:40:00'),
(3, 3, NULL, 'studying', '2024-09-01 10:45:00'),
(3, 10, 72.0, 'completed', '2024-09-01 10:50:00'),
(3, 2, NULL, 'pending', '2024-09-01 10:51:00'),
-- 学生4: 李思琪
(4, 5, 86.0, 'completed', '2024-09-01 11:00:00'),
(4, 6, NULL, 'studying', '2024-09-01 11:05:00'),
(4, 10, 88.5, 'completed', '2024-09-01 11:10:00'),
(4, 1, NULL, 'pending', '2024-09-01 11:11:00'),
-- 学生5: 周杰伦
(5, 5, 70.0, 'completed', '2024-09-01 11:20:00'),
(5, 6, NULL, 'studying', '2024-09-01 11:25:00'),
(5, 10, 76.0, 'completed', '2024-09-01 11:30:00'),
(5, 2, NULL, 'dropped', '2024-09-01 11:31:00'),
-- 学生6: 吴美玲
(6, 7, 98.0, 'completed', '2024-09-01 11:40:00'),
(6, 8, NULL, 'studying', '2024-09-01 11:45:00'),
(6, 9, NULL, 'pending', '2024-09-01 11:50:00'),
(6, 10, 95.0, 'completed', '2024-09-01 11:55:00'),
(6, 11, NULL, 'pending', '2024-09-01 11:56:00'),
-- 学生7: 郑凯文
(7, 7, 83.0, 'completed', '2024-09-01 12:00:00'),
(7, 8, NULL, 'studying', '2024-09-01 12:05:00'),
(7, 10, 81.0, 'completed', '2024-09-01 12:10:00'),
(7, 1, NULL, 'pending', '2024-09-01 12:11:00'),
-- 学生8: 王小燕
(8, 10, NULL, 'studying', '2024-09-01 12:20:00'),
(8, 11, NULL, 'studying', '2024-09-01 12:25:00'),
(8, 12, NULL, 'pending', '2024-09-01 12:30:00'),
(8, 7, 87.0, 'completed', '2024-09-01 12:35:00'),
(8, 1, NULL, 'pending', '2024-09-01 12:36:00');

-- =============================================
-- 创建索引优化查询性能
-- =============================================
-- 管理员表索引
CREATE INDEX idx_admin_username ON admin(username);
CREATE INDEX idx_admin_status ON admin(status);

-- 学生表索引
CREATE INDEX idx_student_college ON student(college_id);
CREATE INDEX idx_student_major ON student(major_id);
CREATE INDEX idx_student_class ON student(class_name);
CREATE INDEX idx_student_status ON student(status);
CREATE INDEX idx_student_no ON student(student_no);

-- 教师表索引
CREATE INDEX idx_teacher_college ON teacher(college_id);
CREATE INDEX idx_teacher_major ON teacher(major_id);
CREATE INDEX idx_teacher_status ON teacher(status);
CREATE INDEX idx_teacher_no ON teacher(teacher_no);

-- 课程表索引
CREATE INDEX idx_course_no ON course(course_no);
CREATE INDEX idx_course_semester ON course(semester);
CREATE INDEX idx_course_college ON course(college_id);
CREATE INDEX idx_course_teacher ON course(teacher_id);

-- 选课表索引
CREATE INDEX idx_selection_student ON course_selection(student_id);
CREATE INDEX idx_selection_course ON course_selection(course_id);
CREATE INDEX idx_selection_status ON course_selection(status);

-- 系统公告表索引
CREATE INDEX idx_announcement_target ON announcement(target_role);
CREATE INDEX idx_announcement_status ON announcement(status);

-- 短信日志表索引 (已在创建表时定义)

-- =============================================
-- 数据统计
-- =============================================
SELECT '=== 微服务数据库初始化完成 ===' AS info;

SELECT 
    '学院' AS item, COUNT(*) AS count FROM college
UNION ALL
SELECT 
    '专业', COUNT(*) FROM major
UNION ALL
SELECT 
    '管理员', COUNT(*) FROM admin
UNION ALL
SELECT 
    '教师', COUNT(*) FROM teacher
UNION ALL
SELECT 
    '学生', COUNT(*) FROM student
UNION ALL
SELECT 
    '课程', COUNT(*) FROM course
UNION ALL
SELECT 
    '选课记录', COUNT(*) FROM course_selection
UNION ALL
SELECT 
    '系统公告', COUNT(*) FROM announcement;

-- =============================================
-- 微服务新增字段说明
-- =============================================
-- teacher 表：
--   - teacher_no: 教工号（T000001格式）
--   - college_id: 所属学院ID
--
-- course 表：
--   - course_no: 课程编号（CS0001, EE0001等）
--   - semester: 学期（2024-2025-1格式）
--
-- course_selection 表：
--   - status: 改为VARCHAR类型，值为字符串
--             pending/studying/completed/dropped
--
-- 新增表：
--   - admin: 管理员表（3位管理员）
--   - notification: 系统通知表（支持个人消息推送）
--   - announcement: 系统公告表（支持全局公告）
--   - course_attachment: 课程附件表（支持MinIO文件上传）
--   - sms_log: 短信日志表（记录短信发送历史）
-- =============================================

-- =============================================
-- 默认账号信息
-- =============================================
-- 管理员账号: admin / admin123
--            admin001 / admin123
--            admin002 / admin123
-- 教师账号: teacher1-teacher6 / 123456
-- 学生账号: student1-student8 / 123456
-- =============================================

COMMIT;

-- 显示创建的数据库
SHOW DATABASES LIKE 'student_management_sys_cloud';
