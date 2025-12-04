# Postman 接口测试指南

本文档详细介绍如何使用 Postman 测试学生管理系统的后端 API 接口。

---

## 📥 快速开始

### 第一步：启动后端服务

确保 Spring Boot 后端已启动，运行在 `http://localhost:8080`

### 第二步：下载并安装 Postman

- 下载地址：https://www.postman.com/downloads/
- 或使用 Web 版：https://web.postman.co/

---

## 🔧 前置配置

### 1. 创建 Postman 环境

1. 打开 Postman
2. 点击右上角的 **眼睛图标** (Environment Quick Look)
3. 点击 **Add** 创建新环境
4. 环境名称填写：`学生管理系统`
5. 添加以下变量：

| 变量名 | 初始值 | 当前值 | 说明 |
|--------|--------|--------|------|
| `baseUrl` | `http://localhost:8080/api` | `http://localhost:8080/api` | 后端API基础地址 |
| `token` | (空) | (空) | 登录后自动填充 |
| `captchaKey` | (空) | (空) | 验证码Key |

6. 点击 **Save** 保存环境
7. 在右上角下拉菜单中选择 `学生管理系统` 环境

### 2. 设置全局请求头

对于需要认证的接口，在 Headers 中添加：
```
Authorization: Bearer {{token}}
Content-Type: application/json
```

### 3. 创建 Collection（接口集合）

1. 点击左侧 **Collections** 标签
2. 点击 **+** 创建新 Collection
3. 命名为 `学生管理系统 API`
4. 在 Collection 中创建文件夹分类：
   - 🔐 认证接口
   - 👨‍🎓 学生管理
   - 📚 课程管理
   - 🏫 学院管理
   - 📖 专业管理
   - ✏️ 选课管理
   - 👨‍🏫 教师管理

---

## 📝 接口测试用例

---

## 一、认证接口 (Auth)

### 1.1 获取验证码

**操作步骤：**
1. 新建请求，选择 **GET** 方法
2. 输入 URL：`{{baseUrl}}/auth/captcha`
3. 点击 **Send**

```
GET {{baseUrl}}/auth/captcha
```

**响应示例：**
```json
{
    "code": 200,
    "message": "操作成功",
    "data": {
        "captchaKey": "a1b2c3d4-xxxx-xxxx-xxxx",
        "captchaImage": "data:image/png;base64,iVBORw0KGgo..."
    }
}
```

> 💡 **重要提示**：
> 1. 将响应中的 `captchaKey` 保存到环境变量中
> 2. `captchaImage` 是 Base64 编码的图片，可以在浏览器中打开查看验证码
> 3. 在浏览器地址栏粘贴 `captchaImage` 的完整内容即可看到验证码图片

**自动保存 captchaKey 脚本**（在 Tests 标签中添加）：
```javascript
if (pm.response.code === 200) {
    var jsonData = pm.response.json();
    if (jsonData.data && jsonData.data.captchaKey) {
        pm.environment.set("captchaKey", jsonData.data.captchaKey);
        console.log("验证码Key已保存: " + jsonData.data.captchaKey);
    }
}
```

---

### 1.2 用户登录

**操作步骤：**
1. 新建请求，选择 **POST** 方法
2. 输入 URL：`{{baseUrl}}/auth/login`
3. 选择 **Body** 标签 → **raw** → **JSON**
4. 输入登录信息
5. 点击 **Send**

```
POST {{baseUrl}}/auth/login
```

**Headers：**
```
Content-Type: application/json
```

**Body (raw JSON)：**

教师账号登录：
```json
{
    "username": "admin",
    "password": "123456",
    "captchaKey": "{{captchaKey}}",
    "captchaCode": "xxxx"
}
```

学生账号登录：
```json
{
    "username": "2024001",
    "password": "123456",
    "captchaKey": "{{captchaKey}}",
    "captchaCode": "xxxx"
}
```

> ⚠️ 注意：`captchaCode` 需要查看验证码图片后手动填写

**响应示例：**
```json
{
    "code": 200,
    "message": "登录成功",
    "data": {
        "token": "eyJhbGciOiJIUzI1NiJ9...",
        "userId": 1,
        "username": "admin",
        "name": "管理员",
        "role": "teacher"
    }
}
```

> 💡 **重要**：登录成功后，Token 会自动保存到环境变量中

**Postman 自动保存 Token 脚本**（在 Tests 标签中添加）：
```javascript
if (pm.response.code === 200) {
    var jsonData = pm.response.json();
    if (jsonData.code === 200 && jsonData.data && jsonData.data.token) {
        pm.environment.set("token", jsonData.data.token);
        console.log("Token 已保存成功！");
        console.log("用户角色: " + jsonData.data.role);
    }
}
```

---

### 1.3 获取当前用户信息
```
GET {{baseUrl}}/auth/info
```

**Headers：**
```
Authorization: Bearer {{token}}
```

**响应示例：**
```json
{
    "code": 200,
    "message": "操作成功",
    "data": {
        "userId": 1,
        "username": "admin",
        "role": "teacher"
    }
}
```

---

### 1.4 修改密码
```
POST {{baseUrl}}/auth/password
```

**Headers：**
```
Content-Type: application/json
Authorization: Bearer {{token}}
```

**Body：**
```json
{
    "oldPassword": "123456",
    "newPassword": "654321"
}
```

**响应示例：**
```json
{
    "code": 200,
    "message": "密码修改成功",
    "data": null
}
```

---

## 二、学生管理 (Student)

### 2.1 分页查询学生列表
```
GET {{baseUrl}}/student/page?pageNum=1&pageSize=10
```

**可选查询参数：**
| 参数 | 类型 | 说明 |
|------|------|------|
| pageNum | int | 页码，默认1 |
| pageSize | int | 每页条数，默认10 |
| studentNo | string | 学号（模糊搜索） |
| name | string | 姓名（模糊搜索） |
| majorId | long | 专业ID |
| collegeId | long | 学院ID |

**完整示例：**
```
GET {{baseUrl}}/student/page?pageNum=1&pageSize=10&name=张
```

**Headers：**
```
Authorization: Bearer {{token}}
```

**响应示例：**
```json
{
    "code": 200,
    "message": "操作成功",
    "data": {
        "records": [
            {
                "id": 1,
                "studentNo": "2024001",
                "username": "2024001",
                "name": "张三",
                "gender": "M",
                "birthDate": "2002-05-15",
                "majorId": 1,
                "major": {
                    "id": 1,
                    "majorName": "计算机科学与技术",
                    "collegeId": 1
                },
                "phone": "13800138001",
                "email": "zhangsan@example.com",
                "status": "active"
            }
        ],
        "total": 1,
        "size": 10,
        "current": 1,
        "pages": 1
    }
}
```

---

### 2.2 根据ID获取学生
```
GET {{baseUrl}}/student/1
```

**Headers：**
```
Authorization: Bearer {{token}}
```

**响应示例：**
```json
{
    "code": 200,
    "message": "操作成功",
    "data": {
        "id": 1,
        "studentNo": "2024001",
        "username": "2024001",
        "name": "张三",
        "gender": "M",
        "majorId": 1,
        "status": "active"
    }
}
```

---

### 2.3 新增学生
```
POST {{baseUrl}}/student
```

**Headers：**
```
Content-Type: application/json
Authorization: Bearer {{token}}
```

**Body：**
```json
{
    "studentNo": "2024002",
    "username": "2024002",
    "password": "123456",
    "name": "李四",
    "gender": "M",
    "birthDate": "2003-08-20",
    "majorId": 1,
    "grade": "2024",
    "className": "计科1班",
    "phone": "13800138002",
    "email": "lisi@example.com",
    "enrollDate": "2024-09-01",
    "status": "active"
}
```

**响应示例：**
```json
{
    "code": 200,
    "message": "操作成功",
    "data": null
}
```

---

### 2.4 更新学生信息
```
PUT {{baseUrl}}/student/2
```

**Headers：**
```
Content-Type: application/json
Authorization: Bearer {{token}}
```

**Body：**
```json
{
    "name": "李四（已修改）",
    "phone": "13900139002",
    "email": "lisi_new@example.com",
    "className": "计科2班"
}
```

**响应示例：**
```json
{
    "code": 200,
    "message": "操作成功",
    "data": null
}
```

---

### 2.5 删除学生
```
DELETE {{baseUrl}}/student/2
```

**Headers：**
```
Authorization: Bearer {{token}}
```

**响应示例：**
```json
{
    "code": 200,
    "message": "操作成功",
    "data": null
}
```

---

## 三、课程管理 (Course)

### 3.1 获取所有课程列表
```
GET {{baseUrl}}/course/list
```

**Headers：**
```
Authorization: Bearer {{token}}
```

**响应示例：**
```json
{
    "code": 200,
    "message": "操作成功",
    "data": [
        {
            "id": 1,
            "courseName": "Java程序设计",
            "teacherId": 1,
            "credit": 4.0,
            "schedule": "周一 1-2节",
            "location": "教学楼A101",
            "maxStudents": 50,
            "selectedCount": 10,
            "description": "Java编程基础课程"
        }
    ]
}
```

---

### 3.2 分页查询课程
```
GET {{baseUrl}}/course/page?pageNum=1&pageSize=10&courseName=Java
```

**Headers：**
```
Authorization: Bearer {{token}}
```

---

### 3.3 新增课程
```
POST {{baseUrl}}/course
```

**Headers：**
```
Content-Type: application/json
Authorization: Bearer {{token}}
```

**Body：**
```json
{
    "courseName": "Python数据分析",
    "teacherId": 1,
    "credit": 3.0,
    "schedule": "周三 3-4节",
    "location": "实验楼B201",
    "maxStudents": 40,
    "description": "使用Python进行数据分析"
}
```

---

### 3.4 更新课程
```
PUT {{baseUrl}}/course/1
```

**Headers：**
```
Content-Type: application/json
Authorization: Bearer {{token}}
```

**Body：**
```json
{
    "courseName": "Java程序设计（进阶）",
    "credit": 5.0,
    "maxStudents": 60
}
```

---

### 3.5 删除课程
```
DELETE {{baseUrl}}/course/1
```

**Headers：**
```
Authorization: Bearer {{token}}
```

---

## 四、学院管理 (College)

### 4.1 获取所有学院
```
GET {{baseUrl}}/college/list
```

---

### 4.2 新增学院
```
POST {{baseUrl}}/college
```

**Body：**
```json
{
    "collegeName": "人工智能学院",
    "minCredit": 160,
    "description": "专注于AI技术研究与应用"
}
```

---

### 4.3 更新学院
```
PUT {{baseUrl}}/college/1
```

**Body：**
```json
{
    "collegeName": "计算机与信息学院",
    "minCredit": 170
}
```

---

### 4.4 删除学院
```
DELETE {{baseUrl}}/college/1
```

---

## 五、专业管理 (Major)

### 5.1 获取所有专业
```
GET {{baseUrl}}/major/list
```

---

### 5.2 根据学院获取专业
```
GET {{baseUrl}}/major/list?collegeId=1
```

---

### 5.3 新增专业
```
POST {{baseUrl}}/major
```

**Body：**
```json
{
    "majorName": "人工智能",
    "collegeId": 1,
    "description": "人工智能技术与应用"
}
```

---

### 5.4 更新专业
```
PUT {{baseUrl}}/major/1
```

**Body：**
```json
{
    "majorName": "计算机科学与技术（新工科）"
}
```

---

### 5.5 删除专业
```
DELETE {{baseUrl}}/major/1
```

---

## 六、选课管理 (Selection)

### 6.1 学生选课
```
POST {{baseUrl}}/selection/select/1
```

> URL中的 `1` 是课程ID

**Headers：**
```
Authorization: Bearer {{token}}
```
> ⚠️ 需要使用学生账号的Token

**响应示例：**
```json
{
    "code": 200,
    "message": "选课成功",
    "data": null
}
```

---

### 6.2 学生退课
```
POST {{baseUrl}}/selection/drop/1
```

**Headers：**
```
Authorization: Bearer {{token}}
```

---

### 6.3 获取我的选课记录（学生）
```
GET {{baseUrl}}/selection/my
```

**Headers：**
```
Authorization: Bearer {{token}}
```
> 使用学生账号Token

**响应示例：**
```json
{
    "code": 200,
    "message": "操作成功",
    "data": [
        {
            "id": 1,
            "studentId": 1,
            "courseId": 1,
            "course": {
                "id": 1,
                "courseName": "Java程序设计",
                "credit": 4.0
            },
            "status": "selected",
            "score": null,
            "selectTime": "2024-09-15 10:30:00"
        }
    ]
}
```

---

### 6.4 获取课程的选课学生（教师）
```
GET {{baseUrl}}/selection/course/1
```

**Headers：**
```
Authorization: Bearer {{token}}
```
> 使用教师账号Token

---

### 6.5 录入成绩（教师）
```
POST {{baseUrl}}/selection/score
```

**Headers：**
```
Content-Type: application/json
Authorization: Bearer {{token}}
```

**Body：**
```json
{
    "studentId": 1,
    "courseId": 1,
    "score": 85.5
}
```

**响应示例：**
```json
{
    "code": 200,
    "message": "成绩录入成功",
    "data": null
}
```

---

## 七、教师管理 (Teacher)

### 7.1 获取教师列表
```
GET {{baseUrl}}/teacher/list
```

---

### 7.2 根据ID获取教师
```
GET {{baseUrl}}/teacher/1
```

---

## 📋 测试顺序建议

### 🚀 完整测试流程

#### 阶段一：教师账号测试（管理功能）

| 步骤 | 操作 | 说明 |
|------|------|------|
| 1 | 获取验证码 | GET `/api/auth/captcha` |
| 2 | 教师登录 | POST `/api/auth/login`（用户名：admin） |
| 3 | 查看学院列表 | GET `/api/college/list` |
| 4 | 新增学院 | POST `/api/college` |
| 5 | 查看专业列表 | GET `/api/major/list` |
| 6 | 新增专业 | POST `/api/major` |
| 7 | 查看学生列表 | GET `/api/student/page` |
| 8 | 新增学生 | POST `/api/student` |
| 9 | 查看课程列表 | GET `/api/course/list` |
| 10 | 新增课程 | POST `/api/course` |

#### 阶段二：学生账号测试（选课功能）

| 步骤 | 操作 | 说明 |
|------|------|------|
| 1 | 获取验证码 | GET `/api/auth/captcha` |
| 2 | 学生登录 | POST `/api/auth/login`（用户名：学号） |
| 3 | 查看可选课程 | GET `/api/course/list` |
| 4 | 选择课程 | POST `/api/selection/select/{courseId}` |
| 5 | 查看我的选课 | GET `/api/selection/my` |
| 6 | 退选课程 | POST `/api/selection/drop/{courseId}` |

#### 阶段三：教师录入成绩

| 步骤 | 操作 | 说明 |
|------|------|------|
| 1 | 教师登录 | 使用教师账号登录 |
| 2 | 查看课程学生 | GET `/api/selection/course/{courseId}` |
| 3 | 录入成绩 | POST `/api/selection/score` |

---

## ⚠️ 常见错误码

| 状态码 | 说明 | 解决方案 |
|--------|------|----------|
| 200 | 成功 | - |
| 400 | 请求参数错误 | 检查请求体JSON格式和字段 |
| 401 | 未授权 | Token过期或无效，重新登录 |
| 403 | 禁止访问 | 角色权限不足（如学生调用教师接口） |
| 500 | 服务器错误 | 检查请求参数或后端日志 |

---

## �️ Postman 操作详解

### 一、如何创建请求

1. **打开 Postman**
2. 点击 **New** → **HTTP Request**
3. 在 URL 栏左侧选择请求方法（GET/POST/PUT/DELETE）
4. 在 URL 栏输入接口地址
5. 如果是 POST/PUT 请求：
   - 点击 **Body** 标签
   - 选择 **raw**
   - 右侧下拉菜单选择 **JSON**
   - 输入 JSON 格式的请求体
6. 点击 **Send** 发送请求

### 二、如何设置 Headers

1. 点击 **Headers** 标签
2. 添加键值对：

| Key | Value |
|-----|-------|
| `Content-Type` | `application/json` |
| `Authorization` | `Bearer {{token}}` |

### 三、如何使用环境变量

1. 在 URL 或 Headers 中使用 `{{变量名}}` 格式
2. 例如：`{{baseUrl}}/auth/login`
3. Postman 会自动替换为环境变量的值

### 四、如何查看响应

发送请求后，在下方可以看到：
- **Body**：响应内容（JSON格式）
- **Headers**：响应头
- **Status**：HTTP状态码（如 200 OK）
- **Time**：响应时间

### 五、如何保存请求

1. 点击 **Save** 按钮
2. 选择保存到的 Collection
3. 输入请求名称
4. 点击 **Save**

---

## 📦 导入 Postman Collection

如果你想快速开始，可以创建一个 `collection.json` 文件，包含所有预定义的请求。

### 手动创建 Collection 的步骤：

1. 在 Postman 左侧点击 **Import**
2. 选择 **Raw text** 标签
3. 粘贴 JSON 格式的 Collection 定义
4. 点击 **Import**

---

## 🔍 调试技巧

### 1. 使用 Console 查看日志

- 点击 Postman 底部的 **Console** 按钮
- 可以看到所有请求和响应的详细信息
- Tests 脚本中的 `console.log()` 输出也会显示在这里

### 2. Pre-request Script（请求前脚本）

在发送请求前执行，例如：
```javascript
// 在登录前自动获取验证码
pm.sendRequest({
    url: pm.environment.get("baseUrl") + "/auth/captcha",
    method: 'GET'
}, function (err, res) {
    if (!err) {
        var data = res.json();
        pm.environment.set("captchaKey", data.data.captchaKey);
    }
});
```

### 3. 批量运行测试

1. 点击 Collection 旁边的 **Run** 按钮
2. 选择要运行的请求
3. 设置迭代次数
4. 点击 **Run** 执行所有请求

---

## 📌 测试账号信息

| 角色 | 用户名 | 密码 | 说明 |
|------|--------|------|------|
| 教师/管理员 | admin | 123456 | 拥有所有权限 |
| 学生 | 2024001 | 123456 | 学生账号（以学号登录） |

> ⚠️ 默认密码都是 `123456`，实际环境请修改

---

## 🔗 相关资源

- [Postman 官方文档](https://learning.postman.com/docs/getting-started/introduction/)
- [Postman 中文教程](https://www.postman.com/downloads/)
- [HTTP 状态码参考](https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Status)

---

## ✅ 接口清单汇总

### 认证接口 (/api/auth)
| 方法 | 路径 | 说明 | 需要Token |
|------|------|------|-----------|
| GET | /captcha | 获取验证码 | ❌ |
| POST | /login | 用户登录 | ❌ |
| GET | /info | 获取当前用户信息 | ✅ |
| POST | /password | 修改密码 | ✅ |

### 学生接口 (/api/student)
| 方法 | 路径 | 说明 | 需要Token | 角色限制 |
|------|------|------|-----------|----------|
| GET | /page | 分页查询学生 | ✅ | - |
| GET | /list | 获取所有学生 | ✅ | - |
| GET | /{id} | 根据ID获取学生 | ✅ | - |
| POST | / | 新增学生 | ✅ | 教师 |
| PUT | /{id} | 更新学生 | ✅ | 教师/本人 |
| DELETE | /{id} | 删除学生 | ✅ | 教师 |

### 课程接口 (/api/course)
| 方法 | 路径 | 说明 | 需要Token | 角色限制 |
|------|------|------|-----------|----------|
| GET | /list | 获取所有课程 | ✅ | - |
| GET | /page | 分页查询课程 | ✅ | - |
| GET | /teacher | 获取教师的课程 | ✅ | 教师 |
| GET | /{id} | 根据ID获取课程 | ✅ | - |
| POST | / | 新增课程 | ✅ | 教师 |
| PUT | /{id} | 更新课程 | ✅ | 教师 |
| DELETE | /{id} | 删除课程 | ✅ | 教师 |

### 选课接口 (/api/selection)
| 方法 | 路径 | 说明 | 需要Token | 角色限制 |
|------|------|------|-----------|----------|
| GET | /my | 学生查看选课记录 | ✅ | 学生 |
| GET | /course/{courseId} | 查看课程选课学生 | ✅ | 教师 |
| POST | /select/{courseId} | 学生选课 | ✅ | 学生 |
| POST | /drop/{courseId} | 学生退课 | ✅ | 学生 |
| POST | /reselect/{courseId} | 重新选课 | ✅ | 学生 |
| POST | /score | 录入成绩 | ✅ | 教师 |

### 学院接口 (/api/college)
| 方法 | 路径 | 说明 | 需要Token | 角色限制 |
|------|------|------|-----------|----------|
| GET | /list | 获取所有学院 | ✅ | - |
| GET | /{id} | 根据ID获取学院 | ✅ | - |
| POST | / | 新增学院 | ✅ | 教师 |
| PUT | /{id} | 更新学院 | ✅ | 教师 |
| DELETE | /{id} | 删除学院 | ✅ | 教师 |

### 专业接口 (/api/major)
| 方法 | 路径 | 说明 | 需要Token | 角色限制 |
|------|------|------|-----------|----------|
| GET | /list | 获取所有专业 | ✅ | - |
| GET | /college/{collegeId} | 根据学院获取专业 | ✅ | - |
| GET | /{id} | 根据ID获取专业 | ✅ | - |
| POST | / | 新增专业 | ✅ | 教师 |
| PUT | /{id} | 更新专业 | ✅ | 教师 |
| DELETE | /{id} | 删除专业 | ✅ | 教师 |

### 教师接口 (/api/teacher)
| 方法 | 路径 | 说明 | 需要Token | 角色限制 |
|------|------|------|-----------|----------|
| GET | /list | 获取所有教师 | ✅ | - |
| GET | /{id} | 根据ID获取教师 | ✅ | - |
| GET | /info | 获取当前教师信息 | ✅ | 教师 |
| PUT | /info | 更新教师信息 | ✅ | 教师 |
| PUT | /password | 修改密码 | ✅ | 教师 |

---

**文档版本**：v1.0  
**最后更新**：2024年

