# 学生管理系统 - Spring Cloud Alibaba 微服务版

## 项目架构

本项目采用 Spring Cloud Alibaba 微服务架构，将原单体应用拆分为多个独立服务。

### 技术栈

- **Spring Boot**: 3.2.5
- **Spring Cloud**: 2023.0.1
- **Spring Cloud Alibaba**: 2023.0.1.0
- **Java**: 21
- **Nacos**: 2.3.0+（服务注册与配置中心）
- **MyBatis-Plus**: 3.5.5
- **MySQL**: 8.0+
- **JWT**: jjwt 0.12.5

### 服务列表

| 服务名称 | 端口 | 说明 |
|---------|------|------|
| gateway-service | 8080 | API网关，统一入口 |
| auth-service | 8081 | 认证服务（登录、验证码） |
| student-service | 8082 | 学生服务（学生CRUD） |
| teacher-service | 8083 | 教师服务（教师CRUD） |
| course-service | 8084 | 课程服务（课程CRUD） |
| selection-service | 8085 | 选课服务（选课、退课、成绩） |
| base-service | 8086 | 基础服务（学院、专业CRUD） |
| common-module | - | 公共模块（实体、工具类） |

### 目录结构

```
student-system-cloud/
├── pom.xml                 # 父POM
├── common-module/          # 公共模块
│   ├── entity/            # 实体类
│   ├── dto/               # 数据传输对象
│   ├── result/            # 统一响应
│   ├── exception/         # 异常处理
│   ├── enums/             # 枚举类
│   └── utils/             # 工具类
├── gateway-service/        # API网关
├── auth-service/           # 认证服务
├── student-service/        # 学生服务
├── teacher-service/        # 教师服务
├── course-service/         # 课程服务
├── selection-service/      # 选课服务
└── base-service/           # 基础服务
```

## 环境要求

1. **JDK 21+**
2. **Maven 3.6+**
3. **MySQL 8.0+**
4. **Nacos 2.3.0+**

## 快速启动

### 1. 启动 Nacos

下载并启动 Nacos：

```bash
# Windows
startup.cmd -m standalone

# Linux/Mac
sh startup.sh -m standalone
```

Nacos 控制台：http://localhost:8848/nacos
默认账号/密码：nacos/nacos

### 2. 初始化数据库

确保 MySQL 已启动，执行数据库初始化脚本：

```sql
-- 使用 database/init.sql 或 mysql脚本.txt
```

### 3. 编译项目

```bash
cd student-system-cloud
mvn clean install -DskipTests
```

### 4. 启动服务

按以下顺序启动各服务：

```bash
# 1. 启动网关服务
cd gateway-service
mvn spring-boot:run

# 2. 启动认证服务
cd auth-service
mvn spring-boot:run

# 3. 启动基础服务（学院、专业）
cd base-service
mvn spring-boot:run

# 4. 启动学生服务
cd student-service
mvn spring-boot:run

# 5. 启动教师服务
cd teacher-service
mvn spring-boot:run

# 6. 启动课程服务
cd course-service
mvn spring-boot:run

# 7. 启动选课服务
cd selection-service
mvn spring-boot:run
```

### 5. 启动前端

```bash
cd vue
npm install
npm run dev
```

前端访问：http://localhost:5173

## API 路由说明

所有请求通过网关（8080端口）统一转发：

| 路由前缀 | 目标服务 |
|---------|---------|
| /api/auth/** | auth-service (8081) |
| /api/student/** | student-service (8082) |
| /api/teacher/** | teacher-service (8083) |
| /api/course/** | course-service (8084) |
| /api/selection/** | selection-service (8085) |
| /api/college/** | base-service (8086) |
| /api/major/** | base-service (8086) |

## 配置说明

### 数据库配置

各服务的 `application.yml` 中配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/student_management_sys
    username: root
    password: "010804"
```

### Nacos 配置

各服务的 `bootstrap.yml` 中配置：

```yaml
spring:
  cloud:
    nacos:
      config:
        server-addr: localhost:8848
        file-extension: yaml
```

### JWT 配置

JWT Secret 配置在 `common-module` 的 `JwtUtil.java` 中。

## 服务间调用

使用 OpenFeign 进行服务间调用：

```java
@FeignClient(name = "base-service")
public interface CollegeFeignClient {
    @GetMapping("/api/college/{id}")
    Result<College> getById(@PathVariable("id") Long id);
}
```

## 功能清单

### 认证模块
- [x] 登录（支持学生/教师）
- [x] 验证码生成与验证
- [x] JWT Token 生成与解析

### 学生模块
- [x] 学生CRUD
- [x] 分页查询
- [x] 个人信息查看
- [x] 密码修改

### 教师模块
- [x] 教师CRUD
- [x] 个人信息查看
- [x] 密码修改

### 课程模块
- [x] 课程CRUD
- [x] 分页查询
- [x] 教师课程列表

### 选课模块
- [x] 学生选课
- [x] 学生退课
- [x] 学生重选
- [x] 成绩录入
- [x] 成绩查询

### 基础模块
- [x] 学院CRUD
- [x] 专业CRUD
- [x] 统计信息

## 注意事项

1. 确保 Nacos 服务先启动
2. 网关服务最先启动
3. 各业务服务可并行启动
4. 前端代理已配置到网关 8080 端口

## 开发建议

1. 使用 IDEA 导入父 POM 即可识别所有子模块
2. 可以使用 Spring Boot Dashboard 批量启动服务
3. 建议使用 Docker Compose 部署生产环境
