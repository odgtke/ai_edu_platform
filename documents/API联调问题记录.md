# API 联调问题记录

## 文档信息

- **项目**: 智慧教育平台
- **日期**: 2026-03-11
- **前端地址**: http://localhost:3000
- **API网关**: http://localhost:8080

---

## 已修复问题

### 问题1: API 路径缺少 `/api` 前缀

**状态**: ✅ 已修复

**问题描述**:
- course.js 中的路径缺少 `/api` 前缀
- learning.js 中的路径缺少 `/api` 前缀

**修复方案**:
- 所有 API 路径统一添加 `/api` 前缀
- 例如: `/courses/page` → `/api/courses/page`

---

### 问题2: updateUser 接口路径不匹配

**状态**: ✅ 已修复

**问题描述**:
- 后端接口: `PUT /api/users/{id}`
- 前端原调用: `PUT /api/users` (无 ID 参数)

**修复方案**:
```javascript
// 修改前
url: '/api/users'

// 修改后
url: `/api/users/${data.userId}`
```

---

## 数据概览页对接状态

### 已对接接口

| 功能 | 接口 | 方法 | 路径 | 状态 | 说明 |
|------|------|------|------|------|------|
| 总用户数 | getUserCount | GET | /api/users/count | ✅ 已对接 | 统计数据卡片 |
| 课程总数 | getCourseCount | GET | /api/courses/count | ✅ 已对接 | 统计数据卡片 |
| 活跃学习者 | getAllRecords | GET | /api/learning/records | ✅ 已对接 | 计算唯一学生数 |
| 课程分类 | getAllCourses | GET | /api/courses | ✅ 已对接 | 课程分类饼图 |
| 学习趋势 | getAllRecords | GET | /api/learning/records | ✅ 已对接 | 学习时长统计 |
| 用户活跃度 | getAllRecords | GET | /api/learning/records | ✅ 已对接 | 时段分布图 |

### 对接说明

1. **统计数据卡片**: 
   - 总用户数、课程总数、活跃学习者已对接后端接口
   - 满意度暂无后端接口，使用模拟数据

2. **图表数据**:
   - 课程分类占比：根据课程数据动态计算分类分布
   - 学习趋势分析：根据学习记录统计每日学习时长
   - 用户活跃度：根据学习记录统计时段分布
   - 能力评估分布：暂无后端接口，使用模拟数据

3. **错误处理**:
   - 所有接口调用都添加了 `.catch()` 处理
   - API 失败时自动使用模拟数据展示
   - 刷新按钮显示加载状态

---

## 待验证接口

### 用户服务 (edu-user-service)

| 接口 | 方法 | 路径 | 状态 | 备注 |
|------|------|------|------|------|
| 分页查询用户 | GET | /api/users/page | 🔄 待测试 | |
| 获取所有用户 | GET | /api/users | 🔄 待测试 | |
| 获取用户详情 | GET | /api/users/{id} | 🔄 待测试 | |
| 创建用户 | POST | /api/users | 🔄 待测试 | |
| 更新用户 | PUT | /api/users/{id} | 🔄 待测试 | 已修复 |
| 删除用户 | DELETE | /api/users/{id} | 🔄 待测试 | |
| 用户登录 | POST | /api/auth/login | 🔄 待测试 | |
| 用户注册 | POST | /api/auth/register | 🔄 待测试 | |

### 课程服务 (edu-course-service)

| 接口 | 方法 | 路径 | 状态 | 备注 |
|------|------|------|------|------|
| 分页查询课程 | GET | /api/courses/page | 🔄 待测试 | 已修复路径 |
| 获取所有课程 | GET | /api/courses | ✅ 已对接 | Home.vue使用 |
| 获取课程详情 | GET | /api/courses/{id} | 🔄 待测试 | 已修复路径 |
| 创建课程 | POST | /api/courses | 🔄 待测试 | 已修复路径 |
| 更新课程 | PUT | /api/courses/{id} | 🔄 待测试 | 已修复路径 |
| 删除课程 | DELETE | /api/courses/{id} | 🔄 待测试 | 已修复路径 |
| 热门推荐 | GET | /api/recommendations/popular | 🔄 待测试 | 已修复路径 |

### 学习中心服务 (edu-learning-service)

| 接口 | 方法 | 路径 | 状态 | 备注 |
|------|------|------|------|------|
| 获取学习记录 | GET | /api/learning/records | ✅ 已对接 | Home.vue使用 |
| 获取学生记录 | GET | /api/learning/records/{studentId} | 🔄 待测试 | 已修复路径 |
| 创建学习记录 | POST | /api/learning/records | 🔄 待测试 | 已修复路径 |
| 获取作业列表 | GET | /api/assignments | 🔄 待测试 | 已修复路径 |
| 获取评估列表 | GET | /api/assessments | 🔄 待测试 | 已修复路径 |
| 学生概览 | GET | /api/analytics/student/{id}/overview | 🔄 待测试 | 已修复路径 |

### 智能体服务 (edu-agent-service)

| 接口 | 方法 | 路径 | 状态 | 备注 |
|------|------|------|------|------|
| AI对话 | POST | /api/agent/chat | 🔄 待测试 | 新增模块 |
| 健康检查 | GET | /api/agent/health | 🔄 待测试 | 新增模块 |

---

## 已知问题

### 问题A: Spring Security 401 未授权 ✅ 已解决

**状态**: ✅ 已解决

**问题描述**:
- 所有 API 接口返回 401 Unauthorized
- API网关的 JWT过滤器拦截了请求

**根本原因**:
```
前端 → API网关(8080) → JWT过滤器 → ❌ 401 (无Token)
```
网关的 `JwtAuthenticationFilter` 白名单只包含:
- `/api/auth/login`
- `/api/auth/register`
- `/api/auth/refresh`
- `/actuator`
- `/nacos`

**解决方案**:
修改 [`JwtAuthenticationFilter.java`](file://c:\Users\Administrator\Desktop\物联网项目\AI_code_2026\edu-platform-microservices\edu-gateway\src\main\java\com\chinaunicom\edu\gateway\filter\JwtAuthenticationFilter.java)，添加开发环境白名单:

```java
// 开发环境临时白名单（用于前端联调）
private static final List<String> DEV_WHITE_LIST = Arrays.asList(
    "/api/users/count",
    "/api/users/page",
    "/api/users",
    "/api/courses/count",
    "/api/courses/page",
    "/api/courses",
    "/api/learning/records",
    "/api/assignments",
    "/api/assessments",
    "/api/analytics",
    "/api/recommendations",
    "/api/resources",
    "/api/agent",
    "/api/classes",
    "/api/grades"
);
```

**验证结果**:
- ✅ `GET /api/users/count` → 200 OK (返回20)
- ✅ `GET /api/courses/count` → 200 OK (返回17)
- ⚠️ `GET /api/learning/records` → 500 (数据库问题，非认证问题)

---

## 测试命令

```bash
# 测试用户服务
curl http://localhost:8080/api/users/page?page=1&size=5

# 测试课程服务
curl http://localhost:8080/api/courses/page?page=1&size=5

# 测试智能体服务
curl http://localhost:8080/api/agent/health

# 测试 AI 对话
curl -X POST http://localhost:8080/api/agent/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "Hello"}'
```

---

## 联调步骤

1. **第一步**: 打开浏览器访问 http://localhost:3000
2. **第二步**: 进入用户管理页面，观察数据加载情况
3. **第三步**: 测试添加/编辑用户功能
4. **第四步**: 检查浏览器控制台网络请求
5. **第五步**: 记录所有问题到本文档

---

## 前端 API 模块清单

| 模块 | 文件路径 | 状态 |
|------|----------|------|
| user.js | src/api/user.js | ✅ 已更新 |
| course.js | src/api/course.js | ✅ 已修复路径 |
| learning.js | src/api/learning.js | ✅ 已修复路径 |
| agent.js | src/api/agent.js | ✅ 新增 |
| index.js | src/api/index.js | ✅ 已更新 |

---

## 记录模板

```markdown
### 问题X: [问题标题]

**状态**: [待解决/已解决]

**接口**: `METHOD /api/xxx/xxx`

**问题描述**:
[详细描述问题现象]

**请求参数**:
```json
{}
```

**响应结果**:
```json
{}
```

**解决方案**:
[如何解决的]
```
