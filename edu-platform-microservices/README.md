# 智慧教育平台 - Spring Cloud 微服务架构

## 项目概述

本项目是智慧教育平台的 Spring Cloud 微服务改造版本，采用分布式架构设计，将单体应用拆分为多个独立的微服务。

## 服务架构

```
┌─────────────────────────────────────────────────────────────┐
│                        前端应用 (Vue 3)                       │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    API Gateway (8080)                       │
│              - 路由转发  - 限流控制  - JWT认证                │
└─────────────────────────────────────────────────────────────┘
                              │
              ┌───────────────┼───────────────┐
              ▼               ▼               ▼
┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐
│     Nacos       │ │  edu-user-svc   │ │  edu-course-svc │
│   (8848)        │ │  (8081)         │ │  (8082)         │
│  服务注册中心    │ │  用户/认证服务   │ │  课程/资源服务   │
└─────────────────┘ └─────────────────┘ └─────────────────┘
                                              │
                              ┌───────────────┴───────────────┐
                              ▼                               ▼
                    ┌─────────────────┐             ┌─────────────────┐
                    │ edu-learning-svc│             │  edu-agent-svc  │
                    │    (8083)       │             │    (8084)       │
                    │  学习中心服务    │             │  智能体服务      │
                    └─────────────────┘             └─────────────────┘
```

## 服务清单

| 服务名称 | 端口 | 职责 | 依赖 |
|---------|------|------|------|
| Nacos | 8848 | 服务注册与发现、配置中心 | - |
| edu-gateway | 8080 | API网关、路由、限流 | Nacos, Redis |
| edu-user-service | 8081 | 用户管理、认证授权 | Nacos, MySQL, Redis |
| edu-course-service | 8082 | 课程管理、资源管理 | Nacos, MySQL, Redis |
| edu-learning-service | 8083 | 学习记录、作业考试 | Nacos, MySQL, Redis |
| edu-agent-service | 8084 | AI智能体、NLP | Nacos, MySQL, Redis |

## 技术栈

- **Spring Boot 2.7.10**
- **Spring Cloud 2021.0.8**
- **Spring Cloud Gateway** - API网关
- **Nacos** - 服务注册中心、配置中心
- **OpenFeign** - 服务间调用
- **MyBatis Plus** - ORM框架
- **MySQL 8.0** - 关系数据库
- **Redis** - 缓存和会话
- **JWT** - 认证授权

## 快速开始

### 1. 环境准备

- JDK 1.8+
- Maven 3.6+
- MySQL 8.0
- Redis 6.0+

### 2. 数据库初始化

```sql
-- 创建数据库
create database edu_platform character set utf8mb4 collate utf8mb4_unicode_ci;
```

### 3. 启动服务

#### Windows 环境

```bash
# 使用启动脚本
start-all.bat

# 或手动启动（按顺序）
cd edu-gateway && mvn spring-boot:run
cd edu-user-service && mvn spring-boot:run
cd edu-course-service && mvn spring-boot:run
cd edu-learning-service && mvn spring-boot:run
cd edu-agent-service && mvn spring-boot:run
```

#### Docker 部署

```bash
# 构建并启动所有服务
docker-compose up -d

# 查看服务状态
docker-compose ps

# 停止服务
docker-compose down
```

### 4. 访问服务

- Nacos 控制台: http://localhost:8848/nacos (nacos/nacos)
- API 网关: http://localhost:8080
- 用户服务: http://localhost:8081
- 课程服务: http://localhost:8082
- 学习中心服务: http://localhost:8083
- 智能体服务: http://localhost:8084

## API 路由规则

| 路径前缀 | 目标服务 |
|---------|---------|
| /api/user/** | edu-user-service |
| /api/auth/** | edu-user-service |
| /api/class/** | edu-user-service |
| /api/grade/** | edu-user-service |
| /api/course/** | edu-course-service |
| /api/resource/** | edu-course-service |
| /api/recommendation/** | edu-course-service |
| /api/learning/** | edu-learning-service |
| /api/assignment/** | edu-learning-service |
| /api/assessment/** | edu-learning-service |
| /api/agent/** | edu-agent-service |
| /api/nlp/** | edu-agent-service |
| /api/ai/** | edu-agent-service |

## 配置说明

### 1. Nacos 配置

```yaml
spring:
  cloud:
    nacos:
      discovery:
        server-addr: 127.0.0.1:8848
        username: nacos
        password: nacos
      config:
        server-addr: 127.0.0.1:8848
        username: nacos
        password: nacos
        file-extension: yml
```

### 2. 数据库配置

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/edu_platform
    username: root
    password: your_password
```

### 3. Redis 配置

```yaml
spring:
  redis:
    host: localhost
    port: 6379
```

## 服务间调用示例

```java
@FeignClient(name = "edu-user-service", path = "/api/user")
public interface UserFeignClient {
    
    @GetMapping("/{id}")
    Result<UserDTO> getUserById(@PathVariable Long id);
    
    @PostMapping("/validate")
    Result<Boolean> validateToken(@RequestParam String token);
}
```

## 监控与运维

### 健康检查

每个服务都提供健康检查端点：
- http://localhost:{port}/actuator/health

### 指标监控

- http://localhost:{port}/actuator/metrics

## 开发规范

1. **服务命名**: edu-{功能}-service
2. **端口分配**: 8081-8089 用于业务服务
3. **API路径**: /api/{功能}/**
4. **配置中心**: 使用 Spring Cloud Config（可选）
5. **日志规范**: 统一使用 SLF4J + Logback

## 后续优化

- [ ] 集成 Nacos 配置中心
- [ ] 添加 SkyWalking 链路追踪
- [ ] 集成 Sentinel 熔断限流
- [ ] 添加 ELK 日志收集
- [ ] 实现分布式事务（Seata）
