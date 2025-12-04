# 学生信息管理系统

基于 **Vue3 + Spring Boot 3** 的前后端分离学生信息管理系统。

## 📋 项目概述

本系统是一个完整的教务管理平台，支持**教师**和**学生**两种角色，提供学生信息管理、课程管理、选课管理等功能。

### 技术栈

| 层级 | 技术 |
|------|------|
| 前端框架 | Vue 3.5 (Composition API + `<script setup>`) |
| 构建工具 | Vite 7.x |
| UI组件库 | Element Plus 2.10 |
| 状态管理 | Pinia |
| 路由 | Vue Router 4 |
| HTTP客户端 | Axios |
| 后端框架 | Spring Boot 3.5.0 |
| ORM框架 | MyBatis-Plus 3.5.7 |
| 数据库 | MySQL 8.0 |
| 认证方式 | JWT (jjwt 0.12.5) |
| 密码加密 | BCrypt |
| 验证码 | Kaptcha |

---

## 🏗️ 项目结构

```
Java EE-学生管理系统/
├── database/                    # 数据库脚本
│   └── init.sql                # 初始化SQL（建库建表）
├── springboot/                  # 后端项目
│   └── myweb/
│       ├── pom.xml             # Maven依赖配置
│       └── src/main/java/com/myweb/
│           ├── common/         # 通用类（Result响应封装）
│           ├── config/         # 配置类（JWT、MyBatis、跨域）
│           ├── controller/     # 控制器层
│           ├── dto/            # 数据传输对象
│           ├── entity/         # 实体类
│           ├── interceptor/    # 拦截器（JWT认证）
│           ├── mapper/         # MyBatis Mapper
│           ├── service/        # 业务逻辑层
│           └── utils/          # 工具类（JwtUtil）
└── vue/                         # 前端项目
    ├── src/
    │   ├── api/                # API接口封装
    │   ├── router/             # 路由配置
    │   ├── stores/             # Pinia状态管理
    │   ├── utils/              # 工具类（axios封装）
    │   └── views/              # 页面组件
    │       ├── teacher/        # 教师端页面
    │       └── student/        # 学生端页面
    └── vite.config.js          # Vite配置（代理）
```

---

## 🗄️ 数据库设计

### ER关系图

```
┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│  sys_college │────<│  sys_major   │     │  sys_teacher │
│    (学院)    │     │    (专业)    │     │    (教师)    │
└──────────────┘     └──────────────┘     └──────┬───────┘
                            │                     │
                            │                     │
                     ┌──────┴───────┐     ┌───────┴───────┐
                     │  sys_student │     │  sys_course   │
                     │    (学生)    │     │    (课程)     │
                     └──────┬───────┘     └───────┬───────┘
                            │                     │
                            └──────────┬──────────┘
                                       │
                            ┌──────────┴──────────┐
                            │ sys_course_selection │
                            │      (选课记录)      │
                            └─────────────────────┘
```

### 数据表说明

| 表名 | 说明 | 主要字段 |
|------|------|----------|
| sys_college | 学院表 | id, college_name, min_credit, description |
| sys_major | 专业表 | id, major_name, college_id, description |
| sys_teacher | 教师表 | id, username, password, name, role |
| sys_student | 学生表 | id, student_no, username, password, name, major_id |
| sys_course | 课程表 | id, course_name, teacher_id, credit, max_students |
| sys_course_selection | 选课表 | id, student_id, course_id, score, status |

---

## 🔐 系统功能

### 角色权限

| 功能模块 | 教师 | 学生 |
|----------|:----:|:----:|
| 学生管理（CRUD） | ✅ | ❌ |
| 课程管理（CRUD） | ✅ | ❌ |
| 学院管理（CRUD） | ✅ | ❌ |
| 专业管理（CRUD） | ✅ | ❌ |
| 选课管理/成绩录入 | ✅ | ❌ |
| 查看学院/专业信息 | ✅ | ✅ |
| 选课/退课 | ❌ | ✅ |
| 查看我的课程 | ❌ | ✅ |
| 查看我的成绩 | ❌ | ✅ |
| 个人信息管理 | ✅ | ✅ |
| 修改密码 | ✅ | ✅ |

### 功能特性

- 🔒 **JWT认证**：无状态Token认证，24小时有效期
- 🖼️ **验证码登录**：Kaptcha生成图形验证码
- 📱 **响应式布局**：Element Plus组件自适应
- 🔄 **实时统计**：首页展示各类统计数据
- 📝 **分页查询**：支持条件筛选和分页
- 🎨 **角色菜单**：根据角色动态显示菜单

---

## 🚀 快速开始

### 环境要求

- JDK 21+
- Maven 3.8+
- MySQL 8.0+
- Node.js 18+
- npm 9+

### 1. 初始化数据库

```sql
-- 使用MySQL命令行或图形工具执行
mysql -u root -p010804 < database/init.sql
```

或者手动执行 `database/init.sql` 文件内容。

### 2. 启动后端

```powershell
cd springboot/myweb

# 安装依赖并运行
mvn spring-boot:run

# 或者先打包再运行
mvn clean package -DskipTests
java -jar target/myweb-0.0.1-SNAPSHOT.jar
```

后端启动成功后访问：http://localhost:8080

### 3. 启动前端

```powershell
cd vue

# 安装依赖
npm install

# 开发模式运行
npm run dev
```

前端启动成功后访问：http://localhost:5173

### 4. 默认账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 教师 | admin | 123456 |
| 学生 | 2024001 | 123456 |

---

## 📁 开发过程详解

### 第一步：数据库设计

1. 分析需求，确定实体：学院、专业、教师、学生、课程、选课
2. 设计表结构，确定字段和约束
3. 建立外键关系：专业→学院，学生→专业，课程→教师，选课→学生+课程
4. 创建触发器：选课时自动更新课程已选人数

```sql
-- 核心表结构示例
CREATE TABLE sys_student (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_no VARCHAR(20) UNIQUE NOT NULL,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(50) NOT NULL,
    major_id BIGINT,
    -- ... 其他字段
    FOREIGN KEY (major_id) REFERENCES sys_major(id)
);
```

### 第二步：Spring Boot后端开发

#### 2.1 项目配置

**pom.xml** - 核心依赖：
```xml
<dependencies>
    <!-- Web -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <!-- MyBatis-Plus -->
    <dependency>
        <groupId>com.baomidou</groupId>
        <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
        <version>3.5.7</version>
    </dependency>
    
    <!-- JWT -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.12.5</version>
    </dependency>
</dependencies>
```

**application.properties** - 数据库配置：
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/student_management_sys
spring.datasource.username=root
spring.datasource.password=010804

# MyBatis-Plus配置
mybatis-plus.mapper-locations=classpath*:/mapper/**/*.xml
mybatis-plus.configuration.map-underscore-to-camel-case=true
```

#### 2.2 分层架构

```
请求流程：
Client → Controller → Service → Mapper → Database
           ↓
       JwtInterceptor (认证拦截)
```

**实体类示例** (`Student.java`)：
```java
@Data
@TableName("sys_student")
public class Student {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String studentNo;
    private String username;
    private String password;
    private String name;
    private Long majorId;
    // ...
    
    @TableField(exist = false)
    private Major major;  // 关联对象
}
```

**Mapper接口**：使用MyBatis-Plus，继承`BaseMapper`即可获得CRUD方法
```java
@Mapper
public interface StudentMapper extends BaseMapper<Student> {
    // 自定义复杂查询
    IPage<Student> selectPageWithDetails(Page<Student> page, @Param("query") StudentQueryDTO query);
}
```

**Service层**：业务逻辑处理
```java
@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> 
    implements StudentService {
    
    @Override
    public Page<Student> pageQuery(StudentQueryDTO query) {
        return baseMapper.selectPageWithDetails(
            new Page<>(query.getPageNum(), query.getPageSize()), query);
    }
}
```

**Controller层**：接收请求，返回统一Result
```java
@RestController
@RequestMapping("/api/student")
public class StudentController {
    
    @GetMapping("/page")
    public Result<Page<Student>> page(StudentQueryDTO queryDTO) {
        return Result.success(studentService.pageQuery(queryDTO));
    }
}
```

#### 2.3 JWT认证流程

```
1. 登录请求 → AuthController.login()
2. 验证用户名密码 → AuthService.login()
3. 生成JWT Token → JwtUtil.generateToken(userId, username, role)
4. 返回Token给前端

5. 后续请求携带Token → Authorization: Bearer <token>
6. JwtInterceptor拦截 → 解析Token → 设置RequestAttribute
7. Controller通过@RequestAttribute获取用户信息
```

### 第三步：Vue3前端开发

#### 3.1 项目配置

**vite.config.js** - 代理配置：
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

**axios封装** (`request.js`)：
```javascript
const service = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截：自动添加Token
service.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// 响应拦截：统一错误处理
service.interceptors.response.use(
  response => response.data,
  error => {
    if (error.response?.status === 401) {
      router.push('/login')
    }
    return Promise.reject(error)
  }
)
```

#### 3.2 路由与权限

```javascript
// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const role = userInfo.role
  
  // 未登录跳转登录页
  if (!token && !to.meta.public) {
    next('/login')
    return
  }
  
  // 角色权限检查
  if (to.meta.role && to.meta.role !== role) {
    next('/dashboard')
    return
  }
  
  next()
})
```

#### 3.3 页面组件（Composition API）

```vue
<script setup>
import { ref, onMounted } from 'vue'
import { getStudentPage, deleteStudent } from '@/api/student'

const tableData = ref([])
const loading = ref(false)

const loadData = async () => {
  loading.value = true
  try {
    const res = await getStudentPage(queryForm)
    tableData.value = res.data.records
  } finally {
    loading.value = false
  }
}

onMounted(() => loadData())
</script>

<template>
  <el-table :data="tableData" v-loading="loading">
    <!-- 表格列 -->
  </el-table>
</template>
```

---

## 📝 API接口文档

### 认证接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/auth/captcha | 获取验证码 |
| POST | /api/auth/login | 用户登录 |
| GET | /api/auth/info | 获取当前用户信息 |
| POST | /api/auth/password | 修改密码 |
| POST | /api/auth/logout | 退出登录 |

### 学生接口

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | /api/student/page | 分页查询学生 | 教师 |
| GET | /api/student/{id} | 获取学生详情 | 教师 |
| POST | /api/student | 新增学生 | 教师 |
| PUT | /api/student/{id} | 更新学生 | 教师/本人 |
| DELETE | /api/student/{id} | 删除学生 | 教师 |
| GET | /api/student/info | 获取当前学生信息 | 学生 |

### 课程接口

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | /api/course/list | 获取所有课程 | 全部 |
| GET | /api/course/page | 分页查询课程 | 全部 |
| POST | /api/course | 新增课程 | 教师 |
| PUT | /api/course/{id} | 更新课程 | 教师 |
| DELETE | /api/course/{id} | 删除课程 | 教师 |

### 选课接口

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | /api/selection/my | 获取我的选课 | 学生 |
| POST | /api/selection/select/{courseId} | 选课 | 学生 |
| POST | /api/selection/drop/{courseId} | 退课 | 学生 |
| GET | /api/selection/course/{courseId} | 获取课程选课学生 | 教师 |
| POST | /api/selection/score | 录入成绩 | 教师 |

---

## ⚠️ 常见问题

### Q1: 后端启动报数据库连接失败？
确保MySQL服务已启动，并且已执行`init.sql`创建数据库。检查`application.properties`中的连接信息。

### Q2: 前端请求报跨域错误？
开发模式下Vite已配置代理，确保请求路径以`/api`开头。生产环境需配置Nginx反向代理。

### Q3: 登录后跳转回登录页？
检查Token是否正确保存到localStorage，以及后端JWT配置的密钥是否一致。

### Q4: MyBatis-Plus字段映射问题？
确保开启了驼峰转下划线：`map-underscore-to-camel-case=true`

---

## 📄 License

MIT License

---

## 👨‍💻 作者

学生信息管理系统 - 基于Vue3 + Spring Boot的教务管理平台
