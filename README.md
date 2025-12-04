# 🎓 学生信息管理系统

<p align="center">
  <img src="https://img.shields.io/badge/Vue-3.5-brightgreen" alt="Vue">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.3.0-green" alt="Spring Boot">
  <img src="https://img.shields.io/badge/MyBatis--Plus-3.5.5-blue" alt="MyBatis-Plus">
  <img src="https://img.shields.io/badge/MySQL-8.0-orange" alt="MySQL">
  <img src="https://img.shields.io/badge/License-MIT-yellow" alt="License">
</p>

基于 **Vue 3 + Spring Boot 3** 的前后端分离学生信息管理系统，支持教师和学生两种角色，实现完整的教务管理功能。

## ✨ 功能特性

### 🧑‍🏫 教师端
- **个人信息** - 查看/修改个人资料、修改密码
- **学生管理** - 学生信息的增删改查、筛选导出
- **课程管理** - 课程的创建、编辑、删除
- **选课管理** - 查看选课记录、录入学生成绩
- **学院管理** - 学院信息维护
- **专业管理** - 专业信息维护

### 👨‍🎓 学生端
- **个人信息** - 查看/修改个人资料、修改密码
- **选课中心** - 浏览可选课程、在线选课/退课
- **我的课程** - 查看已选课程详情
- **我的成绩** - 查看课程成绩和学分统计
- **学院信息** - 浏览学院介绍
- **专业信息** - 浏览专业介绍

### 🔒 安全特性
- JWT Token 认证
- BCrypt 密码加密
- 基于角色的权限控制
- 图形验证码登录

## 🛠️ 技术栈

### 前端
| 技术 | 版本 | 说明 |
|------|------|------|
| Vue.js | 3.5 | 渐进式 JavaScript 框架 |
| Vite | 6.0 | 下一代前端构建工具 |
| Element Plus | 2.9 | Vue 3 组件库 |
| Pinia | 2.2 | Vue 状态管理 |
| Vue Router | 4.4 | Vue 官方路由 |
| Axios | 1.7 | HTTP 请求库 |

### 后端
| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.3.0 | Java 应用框架 |
| MyBatis-Plus | 3.5.5 | MyBatis 增强工具 |
| MySQL | 8.0 | 关系型数据库 |
| JWT (jjwt) | 0.12.5 | Token 认证 |
| Lombok | - | Java 代码简化 |
| Kaptcha | - | 验证码生成 |

## 📁 项目结构

```
Student-Management-System/
├── 📂 database/                     # 数据库脚本
│   └── init.sql                    # 初始化SQL（建库建表+测试数据）
│
├── 📂 springboot/myweb/             # 后端项目
│   ├── pom.xml                     # Maven 依赖配置
│   └── src/main/java/com/myweb/
│       ├── common/                 # 通用响应封装
│       ├── config/                 # 配置类（JWT、MyBatis、CORS）
│       ├── controller/             # 控制器层
│       │   ├── AuthController      # 认证接口
│       │   ├── StudentController   # 学生接口
│       │   ├── TeacherController   # 教师接口
│       │   ├── CourseController    # 课程接口
│       │   ├── CourseSelectionController  # 选课接口
│       │   ├── CollegeController   # 学院接口
│       │   └── MajorController     # 专业接口
│       ├── dto/                    # 数据传输对象
│       ├── entity/                 # 实体类
│       ├── interceptor/            # JWT 拦截器
│       ├── mapper/                 # MyBatis Mapper
│       ├── service/                # 业务逻辑层
│       └── utils/                  # 工具类
│
└── 📂 vue/                          # 前端项目
    ├── package.json                # 依赖配置
    ├── vite.config.js              # Vite 配置
    └── src/
        ├── api/                    # API 接口封装
        ├── assets/                 # 静态资源（图片、CSS）
        ├── router/                 # 路由配置
        ├── stores/                 # Pinia 状态管理
        ├── utils/                  # 工具类（Axios 封装）
        └── views/                  # 页面组件
            ├── teacher/            # 教师端页面
            └── student/            # 学生端页面
```

## 🗄️ 数据库设计

### ER 关系图

```
┌─────────────┐      ┌─────────────┐      ┌─────────────┐
│   College   │──1:N─│    Major    │──1:N─│   Student   │
│   (学院)    │      │   (专业)    │      │   (学生)    │
└─────────────┘      └──────┬──────┘      └──────┬──────┘
                            │                     │
                       1:N  │                     │ N:M
                            ▼                     │
                     ┌─────────────┐              │
                     │   Teacher   │              │
                     │   (教师)    │              │
                     └──────┬──────┘              │
                            │                     │
                       1:N  │                     │
                            ▼                     │
                     ┌─────────────┐              │
                     │   Course    │◄─────────────┘
                     │   (课程)    │
                     └──────┬──────┘
                            │
                       1:N  │
                            ▼
                ┌───────────────────┐
                │ CourseSelection   │
                │   (选课记录)      │
                └───────────────────┘
```

### 数据表

| 表名 | 说明 | 核心字段 |
|------|------|----------|
| `college` | 学院表 | id, college_name, min_credit |
| `major` | 专业表 | id, major_name, college_id |
| `teacher` | 教师表 | id, username, password, name, title, major_id |
| `student` | 学生表 | id, student_no, username, password, name, major_id, college_id |
| `course` | 课程表 | id, course_name, teacher_id, credit, max_students |
| `course_selection` | 选课表 | id, student_id, course_id, score, status |

## 🚀 快速开始

### 环境要求

- **JDK** 21+
- **Maven** 3.8+
- **Node.js** 18+
- **MySQL** 8.0+

### 1️⃣ 克隆项目

```bash
git clone https://github.com/TinoCODE04/Student-Management-System.git
cd Student-Management-System
```

### 2️⃣ 初始化数据库

```bash
# 登录 MySQL 并执行初始化脚本
mysql -u root -p < database/init.sql
```

> 💡 默认创建数据库 `student_management_sys`，包含测试数据

### 3️⃣ 启动后端

```bash
cd springboot/myweb

# 修改数据库配置（如需要）
# vim src/main/resources/application.properties

# 运行项目
mvn spring-boot:run
```

后端启动后访问：http://localhost:8080

### 4️⃣ 启动前端

```bash
cd vue

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端启动后访问：http://localhost:5173

## 👤 测试账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 教师 | `teacher1` | `123456` |
| 教师 | `teacher2` | `123456` |
| 学生 | `student1` | `123456` |
| 学生 | `student2` | `123456` |

## 📸 系统截图

### 登录页面
- 支持教师/学生统一登录
- 图形验证码验证

### 教师端
- 学生管理：支持条件筛选、批量操作
- 选课管理：查看选课记录、录入成绩
- 课程管理：课程信息的 CRUD 操作

### 学生端
- 选课中心：浏览课程、一键选课
- 我的成绩：成绩查询、学分统计
- 个人信息：资料查看与修改

## 📝 API 接口

### 认证模块
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/auth/login` | 用户登录 |
| GET | `/api/auth/captcha` | 获取验证码 |

### 学生模块
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/student/list` | 获取学生列表 |
| GET | `/api/student/info` | 获取当前学生信息 |
| PUT | `/api/student/{id}` | 更新学生信息 |
| DELETE | `/api/student/{id}` | 删除学生 |

### 课程模块
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/course/list` | 获取课程列表 |
| POST | `/api/course` | 创建课程 |
| PUT | `/api/course/{id}` | 更新课程 |
| DELETE | `/api/course/{id}` | 删除课程 |

### 选课模块
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/selection/list` | 获取选课记录 |
| POST | `/api/selection/select` | 选课 |
| DELETE | `/api/selection/{id}` | 退课 |
| PUT | `/api/selection/score` | 录入成绩 |

## ⚙️ 配置说明

### 后端配置 (`application.properties`)

```properties
# 服务端口
server.port=8080

# 数据库配置
spring.datasource.url=jdbc:mysql://localhost:3306/student_management_sys
spring.datasource.username=root
spring.datasource.password=your_password

# JWT 密钥
jwt.secret=your_jwt_secret_key
jwt.expiration=86400000
```

### 前端配置 (`vite.config.js`)

```javascript
export default defineConfig({
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
```

## 🤝 贡献指南

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 提交 Pull Request

## 📄 开源协议

本项目采用 [MIT](LICENSE) 协议开源。

## 🙏 致谢

- [Vue.js](https://vuejs.org/)
- [Element Plus](https://element-plus.org/)
- [Spring Boot](https://spring.io/projects/spring-boot)
- [MyBatis-Plus](https://baomidou.com/)

---

<p align="center">
  如果这个项目对你有帮助，请给个 ⭐ Star 支持一下！
</p>
