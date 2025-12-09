# Spring Cloud 微服务架构图

## 系统架构

```
┌──────────────────────────────────────────────────────────────┐
│                         浏览器用户                              │
└────────────────────────┬─────────────────────────────────────┘
                         │ HTTP :5173
                         ▼
┌────────────────────────────────────────────────────────────────┐
│                     Vue Frontend (前端)                          │
│                     端口: 5173                                   │
│                     框架: Vue 3 + Element Plus                   │
└────────────────────────┬───────────────────────────────────────┘
                         │ Vite Proxy
                         │ /api → :8080
                         ▼
┌────────────────────────────────────────────────────────────────┐
│              Spring Cloud Gateway (API网关)                      │
│              端口: 8080                                          │
│              功能: 路由转发、负载均衡、CORS                        │
├────────────────────────────────────────────────────────────────┤
│  路由规则:                                                       │
│  • /api/** → lb://STUDENT-SERVICE                               │
│  • CORS 配置: localhost:5173, localhost:3000                   │
│  • 超时配置: 3s 连接, 5s 响应                                    │
└────────────────────────┬───────────────────────────────────────┘
                         │ Load Balance
                         │ 服务调用
                         ▼
        ┌────────────────────────────────────┐
        │    Eureka Server (服务注册中心)      │
        │    端口: 8761                        │
        │    功能: 服务注册与发现               │
        └───────────┬────────────────┬────────┘
                    │                │
         服务注册    │                │ 服务发现
         心跳检测    │                │
                    ▼                ▼
        ┌─────────────────────────────────────────┐
        │   Student Service (学生管理服务)         │
        │   端口: 8081                             │
        │   服务名: student-service                │
        ├─────────────────────────────────────────┤
        │  业务模块:                                │
        │  • AuthController (认证)                │
        │  • StudentController (学生管理)         │
        │  • TeacherController (教师管理)         │
        │  • CourseController (课程管理)          │
        │  • CollegeController (学院管理)         │
        │  • MajorController (专业管理)           │
        │  • CourseSelectionController (选课)    │
        └───────────────────┬─────────────────────┘
                            │ MyBatis-Plus
                            │ JDBC
                            ▼
        ┌─────────────────────────────────────────┐
        │         MySQL Database                   │
        │         端口: 3306                        │
        │         数据库: student_management_sys    │
        ├─────────────────────────────────────────┤
        │  数据表:                                  │
        │  • student (学生)                        │
        │  • teacher (教师)                        │
        │  • course (课程)                         │
        │  • college (学院)                        │
        │  • major (专业)                          │
        │  • course_selection (选课记录)           │
        └─────────────────────────────────────────┘
```

## 服务启动顺序

```
1. MySQL Database (3306)
   ↓
2. Eureka Server (8761) - 服务注册中心
   ↓ 等待 30 秒
3. Student Service (8081) - 业务服务，注册到 Eureka
   ↓ 等待 20 秒
4. Gateway Service (8080) - API 网关，注册到 Eureka
   ↓ 等待 15 秒
5. Vue Frontend (5173) - 前端应用
```

## 请求流程

### 用户登录流程

```
1. 用户在浏览器访问 http://localhost:5173/login
   ↓
2. Vue 前端发起请求 POST /api/auth/login
   ↓
3. Vite Proxy 转发到 http://localhost:8080/api/auth/login
   ↓
4. Gateway 接收请求，查询 Eureka 获取 student-service 实例
   ↓
5. Gateway 转发到 http://localhost:8081/api/auth/login
   ↓
6. Student Service 处理登录逻辑
   ↓
7. 查询 MySQL 验证用户名密码
   ↓
8. 生成 JWT Token 返回
   ↓
9. Gateway 转发响应给前端
   ↓
10. Vue 保存 Token 到 localStorage
```

### 查询学生列表流程

```
1. 用户访问学生管理页面
   ↓
2. Vue 发起请求 GET /api/students?current=1&size=10
   ↓
3. Request Interceptor 添加 Authorization: Bearer {token}
   ↓
4. Vite Proxy → Gateway (8080) → Student Service (8081)
   ↓
5. JWT Interceptor 验证 Token
   ↓
6. StudentController 调用 StudentService
   ↓
7. StudentService 通过 MyBatis-Plus 查询数据库
   ↓
8. 返回分页数据
   ↓
9. Gateway 转发响应
   ↓
10. Vue 接收数据并渲染表格
```

## 技术栈详细说明

### 后端技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Spring Boot | 3.3.0 | 基础框架 |
| Spring Cloud | 2023.0.0 | 微服务框架 |
| Spring Cloud Netflix Eureka | 4.1.0 | 服务注册与发现 |
| Spring Cloud Gateway | 4.1.0 | API 网关 |
| Spring Cloud OpenFeign | 4.1.0 | 服务调用 |
| MyBatis-Plus | 3.5.5 | ORM 框架 |
| MySQL Connector | 8.x | 数据库驱动 |
| JWT (jjwt) | 0.12.5 | 认证授权 |
| Hutool | 5.8.25 | 工具类库 |
| Kaptcha | 2.3.2 | 验证码 |
| Java | 21 | 开发语言 |

### 前端技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Vue | 3.x | 前端框架 |
| Vue Router | 4.x | 路由管理 |
| Pinia | 2.x | 状态管理 |
| Element Plus | 2.x | UI 组件库 |
| Axios | 1.x | HTTP 客户端 |
| Vite | 5.x | 构建工具 |

## Spring Cloud 核心功能

### 1. Eureka - 服务注册与发现

**Eureka Server 配置:**
```properties
server.port=8761
eureka.client.register-with-eureka=false
eureka.client.fetch-registry=false
```

**客户端配置:**
```properties
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
eureka.instance.prefer-ip-address=true
```

**功能特点:**
- 服务自动注册
- 健康检查（心跳机制）
- 服务发现
- 故障自动剔除

### 2. Gateway - API 网关

**路由配置:**
```properties
spring.cloud.gateway.routes[0].id=student-service
spring.cloud.gateway.routes[0].uri=lb://STUDENT-SERVICE
spring.cloud.gateway.routes[0].predicates[0]=Path=/api/**
```

**功能特点:**
- 动态路由
- 负载均衡 (lb://)
- 全局 CORS 配置
- 请求/响应过滤
- 超时控制

### 3. OpenFeign - 声明式服务调用

**使用示例:**
```java
@FeignClient(name = "student-service")
public interface StudentClient {
    @GetMapping("/api/students/{id}")
    Result<Student> getStudent(@PathVariable Long id);
}
```

**功能特点:**
- 声明式 HTTP 客户端
- 自动服务发现
- 负载均衡
- 熔断降级（可集成 Resilience4j）

## 端口分配

| 服务 | 端口 | 说明 |
|------|------|------|
| Vue Frontend | 5173 | 前端开发服务器 (Vite) |
| Gateway Service | 8080 | API 网关 (统一入口) |
| Student Service | 8081 | 学生管理业务服务 |
| Eureka Server | 8761 | 服务注册中心 |
| MySQL | 3306 | 数据库 |

## 安全配置

### JWT 认证流程

1. 用户登录成功后，后端生成 JWT Token
2. 前端将 Token 存储在 localStorage
3. 后续请求自动在 Header 中添加 `Authorization: Bearer {token}`
4. 后端 JwtInterceptor 拦截并验证 Token
5. 验证通过后放行请求

### 排除路径

以下路径不需要 JWT 验证:
- `/api/auth/login` - 登录
- `/api/auth/captcha` - 获取验证码
- `/api/auth/register` - 注册

## 扩展建议

### 1. 服务拆分

当前是单体服务，可以进一步拆分为:

```
auth-service (8081) - 认证服务
├─ 用户登录
├─ 验证码
└─ Token 管理

student-service (8082) - 学生服务
├─ 学生信息管理
└─ 学生选课

teacher-service (8083) - 教师服务
├─ 教师信息管理
└─ 课程管理

course-service (8084) - 课程服务
├─ 课程管理
├─ 选课管理
└─ 成绩管理

college-service (8085) - 学院服务
├─ 学院管理
└─ 专业管理
```

### 2. 添加配置中心

```
Config Server (8888)
├─ 集中管理配置
├─ 动态刷新
└─ 环境隔离
```

### 3. 添加链路追踪

```
Zipkin Server (9411)
├─ 分布式追踪
├─ 性能分析
└─ 问题定位
```

### 4. 添加消息队列

```
RabbitMQ (5672)
├─ 异步消息处理
├─ 服务解耦
└─ 削峰填谷
```

### 5. 添加缓存

```
Redis (6379)
├─ 数据缓存
├─ Session 共享
└─ 分布式锁
```

## 监控与运维

### Eureka 控制台

访问 http://localhost:8761 查看:
- 已注册的服务实例
- 服务健康状态
- 实例详细信息

### Gateway 日志

```properties
logging.level.org.springframework.cloud.gateway=DEBUG
logging.level.reactor.netty=DEBUG
```

查看请求路由和转发详情。

### 服务健康检查

```bash
# 检查 Eureka
curl http://localhost:8761/actuator/health

# 检查 Gateway
curl http://localhost:8080/actuator/health

# 检查 Student Service
curl http://localhost:8081/actuator/health
```

## 常见问题排查

### 1. 服务无法注册

**现象:** Eureka 控制台看不到服务

**排查步骤:**
1. 检查 Eureka Server 是否启动
2. 检查网络连接
3. 查看服务日志中的 Eureka 注册信息
4. 确认配置文件中的 `eureka.client.service-url.defaultZone`

### 2. Gateway 路由失败

**现象:** 404 Not Found

**排查步骤:**
1. 确认服务已注册到 Eureka
2. 检查服务名称大小写
3. 查看 Gateway 日志
4. 确认路由配置正确

### 3. 跨域问题

**现象:** CORS 错误

**解决方案:**
- Gateway 已配置全局 CORS
- 确保 Student Service 中移除了 CORS 配置
- 检查 Gateway 的 CorsConfig

### 4. 请求超时

**现象:** 504 Gateway Timeout

**解决方案:**
```properties
# 调整 Gateway 超时时间
spring.cloud.gateway.httpclient.connect-timeout=5000
spring.cloud.gateway.httpclient.response-timeout=10s
```

## 性能优化

### 1. Gateway 优化

- 启用 HTTP/2
- 调整连接池大小
- 配置合理的超时时间

### 2. Eureka 优化

- 调整心跳间隔
- 配置缓存策略
- 生产环境启用自我保护

### 3. 服务优化

- 数据库连接池配置
- 添加 Redis 缓存
- 使用异步处理

## 部署建议

### 开发环境
- 本地启动所有服务
- 使用 H2 内存数据库（可选）
- 开启热部署

### 测试环境
- Docker Compose 部署
- 独立的数据库实例
- 配置环境变量

### 生产环境
- Kubernetes 部署
- 服务多实例部署
- 使用 Nginx 作为入口
- 配置日志收集
- 监控告警

---

**版本:** 1.0.0  
**更新日期:** 2025-12-09  
**维护者:** 开发团队
