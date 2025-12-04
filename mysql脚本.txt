-- =============================================
-- 学生信息管理系统数据库脚本（精简版）
-- 数据库名：student_management_sys
-- 用户：root  密码：010804
-- =============================================

-- 创建数据库
DROP DATABASE IF EXISTS student_management_sys;
CREATE DATABASE student_management_sys DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE student_management_sys;

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
-- 3. 教师表 (teacher)
-- =============================================
CREATE TABLE teacher (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '教师ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名（登录账号）',
    password VARCHAR(255) NOT NULL COMMENT '密码（BCrypt加密）',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    gender VARCHAR(10) DEFAULT '男' COMMENT '性别',
    title VARCHAR(50) COMMENT '职称',
    major_id BIGINT COMMENT '所属专业ID',
    role VARCHAR(20) DEFAULT 'teacher' COMMENT '角色',
    avatar VARCHAR(255) COMMENT '头像URL',
    phone VARCHAR(20) COMMENT '联系电话',
    email VARCHAR(100) COMMENT '邮箱',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
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
-- 5. 课程表 (course)
-- =============================================
CREATE TABLE course (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '课程ID',
    course_name VARCHAR(100) NOT NULL COMMENT '课程名称',
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
-- 6. 选课表 (course_selection)
-- =============================================
-- 选课状态说明：
--   pending  - 待开课（可退课）
--   studying - 学习中（不可退课）
--   completed - 已完成（有成绩）
--   dropped  - 已退选
-- =============================================
CREATE TABLE course_selection (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '选课记录ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    score DECIMAL(5,2) COMMENT '成绩',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '状态：pending待开课(可退)，studying学习中(不可退)，completed已完成，dropped已退选',
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
INSERT INTO teacher (username, password, name, gender, title, major_id, role, phone, email) VALUES
('teacher001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '张明华', '男', '教授', 1, 'teacher', '13800138001', 'zhangmh@edu.com'),
('teacher002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '李晓红', '女', '副教授', 2, 'teacher', '13800138002', 'lixh@edu.com'),
('teacher003', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '王建国', '男', '讲师', 3, 'teacher', '13800138003', 'wangjg@edu.com'),
('teacher004', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '刘芳', '女', '教授', 5, 'teacher', '13800138004', 'liufang@edu.com'),
('teacher005', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '陈伟', '男', '副教授', 6, 'teacher', '13800138005', 'chenwei@edu.com'),
('teacher006', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '杨丽娜', '女', '讲师', 7, 'teacher', '13800138006', 'yangln@edu.com');

-- 插入学生数据 (8位学生，密码: 123456)
-- 字段: username, password, name, gender, student_no, total_credit, college_id, major_id, class_name, grade, birth_date, enroll_date, status, role, avatar, phone, email, age
INSERT INTO student (username, password, name, gender, student_no, total_credit, college_id, major_id, class_name, grade, birth_date, enroll_date, status, role, avatar, phone, email, age) VALUES
('student001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '赵子龙', '男', '2021001001', 68, 1, 1, '软件2101', '2021', '2003-05-15', '2021-09-01', 'active', 'student', 'p1.jpeg', '13900139001', 'zhaozl@stu.edu.com', 20),
('student002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '钱雨萱', '女', '2021001002', 72, 1, 1, '软件2101', '2021', '2002-08-22', '2021-09-01', 'active', 'student', 'p2.png', '13900139002', 'qianyx@stu.edu.com', 21),
('student003', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '孙浩然', '男', '2021002001', 55, 1, 2, '计科2101', '2021', '2003-03-10', '2021-09-01', 'active', 'student', 'p3.jpg', '13900139003', 'sunhr@stu.edu.com', 20),
('student004', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '李思琪', '女', '2021003001', 63, 2, 3, '电子2101', '2021', '2004-01-28', '2021-09-01', 'active', 'student', 'p4.jpg', '13900139004', 'lisq@stu.edu.com', 19),
('student005', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '周杰伦', '男', '2021004001', 48, 2, 4, '通信2101', '2021', '2002-11-05', '2021-09-01', 'active', 'student', 'p5.jpg', '13900139005', 'zhoujl@stu.edu.com', 21),
('student006', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '吴美玲', '女', '2021005001', 76, 3, 5, '工商2101', '2021', '2003-07-18', '2021-09-01', 'active', 'student', 'p6.jpg', '13900139006', 'wuml@stu.edu.com', 20),
('student007', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '郑凯文', '男', '2021006001', 52, 3, 6, '会计2101', '2021', '2003-09-25', '2021-09-01', 'active', 'student', 'p7.jpg', '13900139007', 'zhengkw@stu.edu.com', 20),
('student008', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '王小燕', '女', '2021007001', 61, 4, 7, '英语2101', '2021', '2002-12-12', '2021-09-01', 'active', 'student', 'p8.jpg', '13900139008', 'wangxy@stu.edu.com', 21);

-- 插入课程数据 (12门课程)
INSERT INTO course (course_name, credit, college_id, teacher_id, schedule, location, max_students, selected_count, description) VALUES
-- 计算机学院课程
('Java程序设计', 4, 1, 1, '周一 1-2节', '教学楼A101', 60, 4, 'Java编程基础与面向对象设计'),
('数据结构与算法', 4, 1, 1, '周二 3-4节', '教学楼A102', 55, 3, '常用数据结构与算法分析'),
('数据库原理', 3, 1, 2, '周三 5-6节', '教学楼B201', 50, 3, '关系数据库设计与SQL语言'),
('计算机网络', 3, 1, 2, '周四 1-2节', '教学楼B202', 50, 2, 'TCP/IP协议与网络编程'),
-- 电子信息学院课程
('电路分析', 4, 2, 3, '周一 3-4节', '教学楼D101', 50, 2, '电路基础理论与分析方法'),
('数字电子技术', 4, 2, 3, '周三 1-2节', '教学楼D103', 45, 2, '数字逻辑与组合电路'),
-- 经济管理学院课程
('管理学原理', 3, 3, 4, '周二 7-8节', '教学楼F101', 60, 2, '现代管理理论与实践'),
('财务会计', 4, 3, 5, '周四 5-6节', '教学楼F103', 50, 2, '会计核算与财务报表'),
('市场营销', 3, 3, 4, '周三 7-8节', '教学楼F102', 55, 1, '营销策略与消费者行为'),
-- 外国语学院课程
('大学英语', 2, 4, 6, '周五 5-6节', '教学楼G101', 80, 4, '英语听说读写综合训练'),
('英语口语', 2, 4, 6, '周一 9-10节', '教学楼G102', 40, 2, '英语口语交际能力训练'),
('商务英语写作', 3, 4, 6, '周三 9-10节', '教学楼G103', 45, 1, '商务英语文书写作技巧');

-- 插入选课数据 (约40条记录)
-- pending: 待开课(可退课), studying: 学习中(不可退), completed: 已完成, dropped: 已退选
INSERT INTO course_selection (student_id, course_id, score, status) VALUES
-- 学生1: 赵子龙 (软件2101)
(1, 1, 92.5, 'completed'),
(1, 2, 88.0, 'completed'),
(1, 3, NULL, 'studying'),
(1, 10, 85.0, 'completed'),
(1, 4, NULL, 'pending'),

-- 学生2: 钱雨萱 (软件2101)
(2, 1, 95.0, 'completed'),
(2, 2, 91.5, 'completed'),
(2, 4, NULL, 'studying'),
(2, 10, 78.0, 'completed'),
(2, 11, NULL, 'pending'),

-- 学生3: 孙浩然 (计科2101)
(3, 1, 78.5, 'completed'),
(3, 3, NULL, 'studying'),
(3, 10, 72.0, 'completed'),
(3, 2, NULL, 'pending'),

-- 学生4: 李思琪 (电子2101)
(4, 5, 86.0, 'completed'),
(4, 6, NULL, 'studying'),
(4, 10, 88.5, 'completed'),
(4, 1, NULL, 'pending'),

-- 学生5: 周杰伦 (通信2101)
(5, 5, 70.0, 'completed'),
(5, 6, NULL, 'studying'),
(5, 10, 76.0, 'completed'),
(5, 2, NULL, 'dropped'),

-- 学生6: 吴美玲 (工商2101)
(6, 7, 98.0, 'completed'),
(6, 8, NULL, 'studying'),
(6, 9, NULL, 'pending'),
(6, 10, 95.0, 'completed'),
(6, 11, NULL, 'pending'),

-- 学生7: 郑凯文 (会计2101)
(7, 7, 83.0, 'completed'),
(7, 8, NULL, 'studying'),
(7, 10, 81.0, 'completed'),
(7, 1, NULL, 'pending'),

-- 学生8: 王小燕 (英语2101)
(8, 10, NULL, 'studying'),
(8, 11, NULL, 'studying'),
(8, 12, NULL, 'pending'),
(8, 7, 87.0, 'completed'),
(8, 1, NULL, 'pending');

-- 创建索引优化查询性能
CREATE INDEX idx_student_college ON student(college_id);
CREATE INDEX idx_student_major ON student(major_id);
CREATE INDEX idx_student_class ON student(class_name);
CREATE INDEX idx_teacher_major ON teacher(major_id);
CREATE INDEX idx_course_college ON course(college_id);
CREATE INDEX idx_course_teacher ON course(teacher_id);
CREATE INDEX idx_selection_student ON course_selection(student_id);
CREATE INDEX idx_selection_course ON course_selection(course_id);
CREATE INDEX idx_selection_status ON course_selection(status);

-- 更新学生头像
UPDATE student SET avatar = 'p1.jpeg' WHERE id = 1;
UPDATE student SET avatar = 'p2.png' WHERE id = 2;
UPDATE student SET avatar = 'p3.jpg' WHERE id = 3;
UPDATE student SET avatar = 'p4.jpg' WHERE id = 4;
UPDATE student SET avatar = 'p5.jpg' WHERE id = 5;
UPDATE student SET avatar = 'p6.jpg' WHERE id = 6;
UPDATE student SET avatar = 'p7.jpg' WHERE id = 7;
UPDATE student SET avatar = 'p8.jpg' WHERE id = 8;

-- =============================================
-- 数据统计
-- =============================================
-- 学院: 4个
-- 专业: 8个
-- 教师: 6位
-- 学生: 8位
-- 课程: 12门
-- 选课记录: 约40条
-- =============================================