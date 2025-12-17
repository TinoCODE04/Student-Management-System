USE student_management_sys;

-- ====================================
-- 通知数据检查和修复脚本
-- ====================================

-- 1. 查看所有通知及其对应的用户信息
SELECT 
    n.id AS '通知ID',
    n.user_id AS '用户ID',
    n.user_type AS '用户类型',
    n.title AS '标题',
    n.content AS '内容',
    n.type AS '通知类型',
    n.status AS '状态',
    n.create_time AS '创建时间',
    CASE 
        WHEN s.id IS NOT NULL THEN CONCAT('学生: ', s.name, ' (', s.student_id, ')')
        WHEN t.id IS NOT NULL THEN CONCAT('教师: ', t.name, ' (工号:', t.id, ')')
        ELSE '❌ 未找到用户'
    END AS '用户信息'
FROM notification n
LEFT JOIN student s ON n.user_id = s.id AND n.user_type = 'student'
LEFT JOIN teacher t ON n.user_id = t.id AND n.user_type = 'teacher'
ORDER BY n.create_time DESC
LIMIT 20;

-- 2. 检查是否有错误的user_type
SELECT 
    '错误的通知记录' AS '类型',
    COUNT(*) AS '数量'
FROM notification 
WHERE (user_type = 'student' AND user_id NOT IN (SELECT id FROM student))
   OR (user_type = 'teacher' AND user_id NOT IN (SELECT id FROM teacher));

-- 3. 按通知类型统计
SELECT 
    type AS '通知类型',
    user_type AS '用户类型',
    COUNT(*) AS '数量',
    COUNT(CASE WHEN status = 0 THEN 1 END) AS '未读',
    COUNT(CASE WHEN status = 1 THEN 1 END) AS '已读'
FROM notification
GROUP BY type, user_type
ORDER BY user_type, type;

-- 4. 查看最近的成绩通知
SELECT 
    n.id,
    n.user_id,
    n.user_type,
    s.name AS '学生姓名',
    n.title,
    n.content,
    n.create_time
FROM notification n
LEFT JOIN student s ON n.user_id = s.id AND n.user_type = 'student'
WHERE n.type IN ('grade', 'grade_update')
ORDER BY n.create_time DESC
LIMIT 10;

-- 5. 查看学生的选课相关通知
SELECT 
    n.id,
    n.user_id,
    s.name AS '学生姓名',
    n.title,
    n.content,
    n.type,
    n.create_time
FROM notification n
LEFT JOIN student s ON n.user_id = s.id
WHERE n.type IN ('selection', 'drop', 'reselection')
ORDER BY n.create_time DESC
LIMIT 10;

-- 6. 删除错误的通知数据（user_id不存在的）
-- ⚠️ 取消注释后执行
-- DELETE FROM notification 
-- WHERE (user_type = 'student' AND user_id NOT IN (SELECT id FROM student))
--    OR (user_type = 'teacher' AND user_id NOT IN (SELECT id FROM teacher));

-- 7. 清空所有通知数据（用于重新测试）
-- ⚠️ 慎用！取消注释后执行
-- TRUNCATE TABLE notification;

-- 8. 查看特定学生的所有通知
-- 替换 USER_ID 为实际的学生ID
-- SELECT * FROM notification 
-- WHERE user_id = 1 AND user_type = 'student'
-- ORDER BY create_time DESC;

-- 9. 查看特定教师的所有通知
-- 替换 USER_ID 为实际的教师ID
-- SELECT * FROM notification 
-- WHERE user_id = 1 AND user_type = 'teacher'
-- ORDER BY create_time DESC;

-- 10. 统计各用户的通知数量
SELECT 
    user_type AS '用户类型',
    user_id AS '用户ID',
    CASE 
        WHEN user_type = 'student' THEN (SELECT name FROM student WHERE id = n.user_id)
        WHEN user_type = 'teacher' THEN (SELECT name FROM teacher WHERE id = n.user_id)
    END AS '用户姓名',
    COUNT(*) AS '总通知数',
    COUNT(CASE WHEN status = 0 THEN 1 END) AS '未读',
    COUNT(CASE WHEN status = 1 THEN 1 END) AS '已读'
FROM notification n
GROUP BY user_type, user_id
ORDER BY user_type, user_id;

-- 11. 验证通知是否正确发送给学生
-- 查看最近录入成绩的记录和对应的通知
SELECT 
    cs.id AS '选课记录ID',
    cs.student_id AS '学生ID',
    s.name AS '学生姓名',
    c.course_name AS '课程名',
    cs.score AS '成绩',
    (SELECT COUNT(*) FROM notification 
     WHERE user_id = cs.student_id 
     AND user_type = 'student'
     AND type IN ('grade', 'grade_update')
     AND related_id = cs.course_id) AS '对应通知数'
FROM course_selection cs
JOIN student s ON cs.student_id = s.id
JOIN course c ON cs.course_id = c.id
WHERE cs.score IS NOT NULL
ORDER BY cs.id DESC
LIMIT 10;

SELECT '✅ 检查完成！' AS '结果';
