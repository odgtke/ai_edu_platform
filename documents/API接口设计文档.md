# 智慧教育平台 - API接口设计文档

## 文档信息

- **版本**: v1.0
- **日期**: 2026-03-11
- **服务架构**: Spring Cloud 微服务

## 目录

1. [通用规范](#通用规范)
2. [用户服务 (edu-user-service)](#用户服务-edu-user-service)
3. [课程服务 (edu-course-service)](#课程服务-edu-course-service)
4. [学习中心服务 (edu-learning-service)](#学习中心服务-edu-learning-service)
5. [智能体服务 (edu-agent-service)](#智能体服务-edu-agent-service)

---

## 通用规范

### 基础信息

| 项目 | 值 |
|------|-----|
| API网关地址 | `http://localhost:8080` |
| 响应格式 | JSON |
| 字符编码 | UTF-8 |
| 请求方式 | RESTful |

### 响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

### 状态码说明

| 状态码 | 说明 |
|--------|------|
| 200 | 请求成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

---

## 用户服务 (edu-user-service)

**服务端口**: 8081  
**基础路径**: `/api`

### 1. 用户管理接口

#### 1.1 获取用户列表

- **接口**: `GET /api/users`
- **描述**: 获取所有用户列表
- **响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "userId": 1,
      "username": "admin",
      "realName": "系统管理员",
      "email": "admin@edu.com",
      "phone": "13800138000",
      "userType": 4,
      "status": 1,
      "createTime": "2024-01-01 10:00:00"
    }
  ]
}
```

#### 1.2 分页查询用户

- **接口**: `GET /api/users/page`
- **描述**: 分页查询用户列表
- **请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| current | Integer | 否 | 当前页码，默认1 |
| size | Integer | 否 | 每页大小，默认10 |

- **响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [...],
    "total": 100,
    "size": 10,
    "current": 1,
    "pages": 10
  }
}
```

#### 1.3 获取用户详情

- **接口**: `GET /api/users/{id}`
- **描述**: 根据ID获取用户详情
- **路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 用户ID |

#### 1.4 创建用户

- **接口**: `POST /api/users`
- **描述**: 创建新用户
- **请求体**:

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| username | String | 是 | 用户名 |
| realName | String | 是 | 真实姓名 |
| email | String | 是 | 邮箱 |
| phone | String | 否 | 手机号 |
| password | String | 是 | 密码 |
| userType | Integer | 是 | 用户类型：1-学生, 2-教师, 3-家长, 4-管理员 |
| status | Integer | 否 | 状态：0-禁用, 1-启用，默认1 |

#### 1.5 更新用户

- **接口**: `PUT /api/users/{id}`
- **描述**: 更新用户信息
- **路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 用户ID |

#### 1.6 删除用户

- **接口**: `DELETE /api/users/{id}`
- **描述**: 删除用户
- **路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 用户ID |

#### 1.7 获取用户总数

- **接口**: `GET /api/users/count`
- **描述**: 获取用户总数

---

### 2. 认证接口

#### 2.1 用户登录

- **接口**: `POST /api/auth/login`
- **描述**: 用户登录
- **请求体**:

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| username | String | 是 | 用户名 |
| password | String | 是 | 密码 |

- **响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "userId": 1,
    "username": "admin",
    "realName": "系统管理员",
    "userType": 4
  }
}
```

#### 2.2 用户注册

- **接口**: `POST /api/auth/register`
- **描述**: 用户注册
- **请求体**:

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| username | String | 是 | 用户名 |
| password | String | 是 | 密码 |
| realName | String | 是 | 真实姓名 |
| email | String | 是 | 邮箱 |
| userType | Integer | 是 | 用户类型 |

---

### 3. 班级管理接口

#### 3.1 获取班级列表

- **接口**: `GET /api/classes`
- **描述**: 获取所有班级列表

#### 3.2 获取班级详情

- **接口**: `GET /api/classes/{id}`
- **描述**: 根据ID获取班级详情

#### 3.3 创建班级

- **接口**: `POST /api/classes`
- **描述**: 创建新班级

#### 3.4 更新班级

- **接口**: `PUT /api/classes/{id}`
- **描述**: 更新班级信息

#### 3.5 删除班级

- **接口**: `DELETE /api/classes/{id}`
- **描述**: 删除班级

---

### 4. 年级管理接口

#### 4.1 获取年级列表

- **接口**: `GET /api/grades`
- **描述**: 获取所有年级列表

#### 4.2 获取年级详情

- **接口**: `GET /api/grades/{id}`
- **描述**: 根据ID获取年级详情

#### 4.3 创建年级

- **接口**: `POST /api/grades`
- **描述**: 创建新年级

#### 4.4 更新年级

- **接口**: `PUT /api/grades/{id}`
- **描述**: 更新年级信息

#### 4.5 删除年级

- **接口**: `DELETE /api/grades/{id}`
- **描述**: 删除年级

---

### 5. 用户班级关联接口

#### 5.1 获取关联列表

- **接口**: `GET /api/user-classes`
- **描述**: 获取所有用户班级关联列表

#### 5.2 根据用户查询

- **接口**: `GET /api/user-classes/user/{userId}`
- **描述**: 根据用户ID查询关联班级

#### 5.3 根据班级查询

- **接口**: `GET /api/user-classes/class/{classId}`
- **描述**: 根据班级ID查询关联用户

#### 5.4 创建关联

- **接口**: `POST /api/user-classes`
- **描述**: 创建用户班级关联

#### 5.5 删除关联

- **接口**: `DELETE /api/user-classes/{id}`
- **描述**: 删除用户班级关联

---

## 课程服务 (edu-course-service)

**服务端口**: 8082  
**基础路径**: `/api`

### 1. 课程管理接口

#### 1.1 获取课程列表

- **接口**: `GET /api/courses`
- **描述**: 获取所有课程列表

#### 1.2 分页查询课程

- **接口**: `GET /api/courses/page`
- **描述**: 分页查询课程列表
- **请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| current | Integer | 否 | 当前页码，默认1 |
| size | Integer | 否 | 每页大小，默认10 |
| courseName | String | 否 | 课程名称（模糊查询） |

#### 1.3 获取课程详情

- **接口**: `GET /api/courses/{id}`
- **描述**: 根据ID获取课程详情

#### 1.4 创建课程

- **接口**: `POST /api/courses`
- **描述**: 创建新课程

#### 1.5 更新课程

- **接口**: `PUT /api/courses/{id}`
- **描述**: 更新课程信息

#### 1.6 删除课程

- **接口**: `DELETE /api/courses/{id}`
- **描述**: 删除课程

#### 1.7 获取课程总数

- **接口**: `GET /api/courses/count`
- **描述**: 获取课程总数

---

### 2. 资源管理接口

#### 2.1 获取资源列表

- **接口**: `GET /api/resources`
- **描述**: 获取所有资源列表

#### 2.2 获取资源详情

- **接口**: `GET /api/resources/{id}`
- **描述**: 根据ID获取资源详情

#### 2.3 创建资源

- **接口**: `POST /api/resources`
- **描述**: 创建新资源

#### 2.4 更新资源

- **接口**: `PUT /api/resources/{id}`
- **描述**: 更新资源信息

#### 2.5 删除资源

- **接口**: `DELETE /api/resources/{id}`
- **描述**: 删除资源

---

### 3. 推荐接口

#### 3.1 个性化推荐

- **接口**: `GET /api/recommendations/personalized`
- **描述**: 获取个性化课程推荐
- **请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| userId | Long | 是 | 用户ID |
| limit | Integer | 否 | 推荐数量，默认10 |

#### 3.2 按类型推荐

- **接口**: `GET /api/recommendations/by-type`
- **描述**: 按类型获取推荐
- **请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| userId | Long | 是 | 用户ID |
| type | String | 是 | 推荐类型 |
| limit | Integer | 否 | 推荐数量，默认10 |

#### 3.3 热门课程

- **接口**: `GET /api/recommendations/popular`
- **描述**: 获取热门课程
- **请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| limit | Integer | 否 | 数量，默认10 |

---

## 学习中心服务 (edu-learning-service)

**服务端口**: 8083  
**基础路径**: `/api`

### 1. 学习记录接口

#### 1.1 获取学习记录列表

- **接口**: `GET /api/learning/records`
- **描述**: 获取所有学习记录

#### 1.2 根据学生查询

- **接口**: `GET /api/learning/records/{studentId}`
- **描述**: 根据学生ID查询学习记录

#### 1.3 创建学习记录

- **接口**: `POST /api/learning/records`
- **描述**: 创建学习记录

---

### 2. 作业管理接口

#### 2.1 获取作业列表

- **接口**: `GET /api/assignments`
- **描述**: 获取所有作业列表

#### 2.2 获取作业详情

- **接口**: `GET /api/assignments/{id}`
- **描述**: 根据ID获取作业详情

#### 2.3 根据课程查询

- **接口**: `GET /api/assignments/course/{courseId}`
- **描述**: 根据课程ID查询作业

#### 2.4 创建作业

- **接口**: `POST /api/assignments`
- **描述**: 创建新作业

#### 2.5 更新作业

- **接口**: `PUT /api/assignments/{id}`
- **描述**: 更新作业信息

#### 2.6 删除作业

- **接口**: `DELETE /api/assignments/{id}`
- **描述**: 删除作业

---

### 3. 考试评估接口

#### 3.1 获取评估列表

- **接口**: `GET /api/assessments`
- **描述**: 获取所有评估列表

#### 3.2 获取评估详情

- **接口**: `GET /api/assessments/{id}`
- **描述**: 根据ID获取评估详情

#### 3.3 根据课程查询

- **接口**: `GET /api/assessments/course/{courseId}`
- **描述**: 根据课程ID查询评估

#### 3.4 创建评估

- **接口**: `POST /api/assessments`
- **描述**: 创建新评估

#### 3.5 更新评估

- **接口**: `PUT /api/assessments/{id}`
- **描述**: 更新评估信息

#### 3.6 删除评估

- **接口**: `DELETE /api/assessments/{id}`
- **描述**: 删除评估

---

### 4. 学习分析接口

#### 4.1 学生概览

- **接口**: `GET /api/analytics/student/{studentId}/overview`
- **描述**: 获取学生学习概览
- **响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "studentId": 1,
    "totalLearningHours": 120,
    "completedCourses": 5,
    "averageScore": 85.5,
    "ranking": 10
  }
}
```

#### 4.2 学习日历

- **接口**: `GET /api/analytics/student/{studentId}/calendar`
- **描述**: 获取学生学习日历数据

#### 4.3 课程统计

- **接口**: `GET /api/analytics/course/{courseId}/statistics`
- **描述**: 获取课程学习统计

---

## 智能体服务 (edu-agent-service)

**服务端口**: 8084  
**基础路径**: `/api`

### 1. AI对话接口

#### 1.1 智能对话

- **接口**: `POST /api/agent/chat`
- **描述**: 与AI智能体对话
- **请求体**:

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| message | String | 是 | 用户消息 |

- **响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "reply": "AI回复内容",
    "type": "text"
  }
}
```

#### 1.2 健康检查

- **接口**: `GET /api/agent/health`
- **描述**: 检查智能体服务状态

---

## 附录

### A. 用户类型枚举

| 值 | 说明 |
|----|------|
| 1 | 学生 |
| 2 | 教师 |
| 3 | 家长 |
| 4 | 管理员 |

### B. 状态枚举

| 值 | 说明 |
|----|------|
| 0 | 禁用 |
| 1 | 启用 |

### C. 服务依赖关系

```
API Gateway (8080)
    ├── 用户服务 (8081) → MySQL, Redis
    ├── 课程服务 (8082) → MySQL, Redis
    ├── 学习中心服务 (8083) → MySQL, Redis
    └── 智能体服务 (8084) → MySQL, Redis
```

### D. 测试命令

```bash
# 测试用户服务
curl http://localhost:8080/api/users/page?page=1&size=10

# 测试课程服务
curl http://localhost:8080/api/courses/page?page=1&size=10

# 测试智能体服务
curl -X POST http://localhost:8080/api/agent/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "Hello"}'
```
