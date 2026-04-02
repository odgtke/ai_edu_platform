# 智慧教育平台后端接口文档

## 文档概述

本文档提供了智慧教育平台所有后端API接口的详细说明，供前端开发人员进行接口联调使用。

**基础URL**: `http://localhost:8081`

**统一响应格式**:
```json
{
  "code": 200,          // 状态码：200成功，其他为错误码
  "message": "success", // 提示信息
  "data": {}            // 响应数据
}
```

**错误码说明**:
- `200`: 请求成功
- `400`: 请求参数错误
- `401`: 未授权/登录过期
- `403`: 无权限访问
- `404`: 资源不存在
- `500`: 服务器内部错误

---

## 1. 认证授权接口 `/auth`

### 1.1 用户登录
**POST** `/auth/login`

**请求参数**:
```json
{
  "username": "string",  // 用户名
  "password": "string"   // 密码
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": "86400",
    "username": "testuser",
    "userId": "1"
  }
}
```

### 1.2 用户注册
**POST** `/auth/register`

**请求参数**:
```json
{
  "username": "string",   // 用户名（必填）
  "password": "string",   // 密码（必填，至少8位，包含大小写字母、数字和特殊字符）
  "realName": "string",   // 真实姓名
  "phone": "string",      // 手机号
  "email": "string",      // 邮箱
  "userType": 1,          // 用户类型：1-学生 2-教师 3-管理员
  "status": 1             // 状态：0-禁用 1-启用
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": "Registration successful"
}
```

---

## 2. 用户管理接口 `/users`

### 2.1 分页查询用户
**GET** `/users/page`

**请求参数**:
- `page`: 页码，默认1
- `size`: 每页大小，默认10
- `username`: 用户名模糊查询（可选）
- `realName`: 真实姓名模糊查询（可选）
- `userType`: 用户类型筛选：1-学生 2-教师 3-管理员（可选）
- `status`: 状态筛选：0-禁用 1-启用（可选）

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "userId": 1,
        "username": "testuser",
        "realName": "测试用户",
        "phone": "13800138000",
        "email": "test@example.com",
        "userType": 1,
        "status": 1,
        "avatar": "头像URL",
        "createTime": "2024-01-01T10:00:00",
        "updateTime": "2024-01-01T10:00:00"
      }
    ],
    "total": 100,
    "size": 10,
    "current": 1,
    "pages": 10
  }
}
```

### 2.2 获取所有用户
**GET** `/users`

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "userId": 1,
      "username": "testuser",
      "realName": "测试用户",
      "phone": "13800138000",
      "email": "test@example.com",
      "userType": 1,
      "status": 1
    }
  ]
}
```

### 2.3 根据ID获取用户
**GET** `/users/{id}`

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "userId": 1,
    "username": "testuser",
    "realName": "测试用户",
    "userType": 1,
    "status": 1
  }
}
```

### 2.4 创建用户
**POST** `/users`

**请求参数**:
```json
{
  "username": "string",
  "password": "string",
  "realName": "string",
  "phone": "string",
  "email": "string",
  "userType": 1,
  "status": 1
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

### 2.5 更新用户
**PUT** `/users`

**请求参数**:
```json
{
  "userId": 1,
  "username": "string",
  "realName": "string",
  "phone": "string",
  "email": "string",
  "userType": 1,
  "status": 1
}
```

### 2.6 删除用户
**DELETE** `/users/{id}`

**说明**: 删除用户前会检查是否有关联的课程（作为教师），如果有则返回错误提示。

**响应示例（有关联课程）**:
```json
{
  "code": 400,
  "message": "该用户是 3 门课程的教师，请先转移或删除这些课程后再删除用户",
  "data": null
}
```

### 2.7 获取用户总数
**GET** `/users/count`

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": 150
}
```

---

## 3. 课程管理接口 `/courses`

### 3.1 分页查询课程
**GET** `/courses/page`

**请求参数**:
- `page`: 页码，默认1
- `size`: 每页大小，默认10
- `courseName`: 课程名称模糊查询（可选）
- `courseCode`: 课程编码模糊查询（可选）
- `teacherId`: 教师ID筛选（可选）
- `gradeLevel`: 年级筛选：1-小学 2-初中 3-高中 4-大学（可选）
- `subjectId`: 学科ID筛选（可选）
- `status`: 状态筛选：0-停用 1-启用（可选）

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "courseId": 1,
        "courseName": "Java程序设计",
        "courseCode": "CS101",
        "description": "Java基础课程",
        "teacherId": 1,
        "gradeLevel": 4,
        "subjectId": 1,
        "credit": 3.0,
        "status": 1,
        "coverImage": "cover.jpg",
        "totalLessons": 20,
        "createTime": "2024-01-01T10:00:00"
      }
    ],
    "total": 50,
    "size": 10,
    "current": 1,
    "pages": 5
  }
}
```

### 3.2 获取所有课程
**GET** `/courses`

### 3.3 根据ID获取课程
**GET** `/courses/{id}`

### 3.4 创建课程
**POST** `/courses`

**请求参数**:
```json
{
  "courseName": "string",
  "courseCode": "string",
  "description": "string",
  "teacherId": 1,
  "gradeLevel": 4,
  "subjectId": 1,
  "credit": 3.0,
  "status": 1,
  "totalLessons": 20
}
```

### 3.5 更新课程
**PUT** `/courses`

### 3.6 删除课程
**DELETE** `/courses/{id}`

### 3.7 获取课程总数
**GET** `/courses/count`

### 3.8 根据教师ID获取课程列表
**GET** `/courses/teacher/{teacherId}`

### 3.9 根据年级获取课程列表
**GET** `/courses/grade/{gradeLevel}`

---

## 4. 资源管理接口 `/resources`

### 4.1 上传资源文件
**POST** `/resources/upload`

**Content-Type**: `multipart/form-data`

**请求参数**:
- `file`: 文件（必填）
- `resourceName`: 资源名称（可选，默认使用文件名）
- `description`: 资源描述（可选）
- `uploaderId`: 上传者ID（必填）
- `uploaderName`: 上传者名称（必填）

**响应示例**:
```json
{
  "code": 200,
  "message": "上传成功",
  "data": {
    "resourceId": 1,
    "resourceName": "Java教程.pdf",
    "resourceType": "document",
    "fileUrl": "/uploads/2024/01/java_tutorial.pdf",
    "fileSize": 2048000,
    "uploaderId": 1,
    "status": 1,
    "createTime": "2024-01-01T10:00:00"
  }
}
```

### 4.2 分页查询资源列表
**GET** `/resources/page`

**请求参数**:
- `page`: 页码，默认1
- `size`: 每页大小，默认10
- `resourceType`: 资源类型筛选：video/document/image/audio/other（可选）
- `keyword`: 资源名称关键词搜索（可选）

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "resourceId": 1,
        "resourceName": "Java教程.pdf",
        "resourceType": "document",
        "fileUrl": "/uploads/2024/01/java_tutorial.pdf",
        "fileSize": 2048000,
        "description": "Java基础教程",
        "uploaderId": 1,
        "downloadCount": 100,
        "viewCount": 500,
        "status": 1,
        "createTime": "2024-01-01T10:00:00"
      }
    ],
    "total": 50,
    "size": 10,
    "current": 1,
    "pages": 5
  }
}
```

### 4.3 获取所有资源类型
**GET** `/resources/types`

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {"value": "video", "label": "视频"},
    {"value": "document", "label": "文档"},
    {"value": "image", "label": "图片"},
    {"value": "audio", "label": "音频"},
    {"value": "other", "label": "其他"}
  ]
}
```

### 4.4 根据ID获取资源详情
**GET** `/resources/{resourceId}`

**说明**: 获取详情时会自动增加浏览次数

### 4.5 下载资源
**GET** `/resources/{resourceId}/download`

**说明**: 直接返回文件流，浏览器会自动触发下载

### 4.6 删除资源
**DELETE** `/resources/{resourceId}`

### 4.7 获取课程的资源列表
**GET** `/resources/course/{courseId}`

### 4.8 关联资源到课程
**POST** `/resources/course/{courseId}/resource/{resourceId}`

**请求参数**:
- `isRequired`: 是否必修，0-选修 1-必修，默认1

### 4.9 从课程中移除资源
**DELETE** `/resources/course/{courseId}/resource/{resourceId}`

### 4.10 检查资源是否已关联课程
**GET** `/resources/course/{courseId}/check/{resourceId}`

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": true  // true表示已关联，false表示未关联
}
```

---

## 5. 年级管理接口 `/grades`

### 5.1 获取所有年级列表
**GET** `/grades/list`

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "gradeId": 1,
      "gradeName": "一年级",
      "gradeLevel": 1,
      "description": "小学一年级",
      "status": 1
    }
  ]
}
```

### 5.2 获取启用的年级列表
**GET** `/grades/active`

### 5.3 根据ID获取年级
**GET** `/grades/{id}`

### 5.4 新增年级
**POST** `/grades`

**请求参数**:
```json
{
  "gradeName": "string",
  "gradeLevel": 1,
  "description": "string",
  "status": 1
}
```

### 5.5 更新年级
**PUT** `/grades/{id}`

### 5.6 删除年级
**DELETE** `/grades/{id}`

---

## 6. 班级管理接口 `/classes`

### 6.1 分页查询班级列表
**GET** `/classes/page`

**请求参数**:
- `pageNum`: 页码，默认1
- `pageSize`: 每页大小，默认10
- `gradeId`: 年级ID筛选（可选）
- `keyword`: 班级名称关键词搜索（可选）

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "classId": 1,
        "className": "一班",
        "gradeId": 1,
        "gradeName": "一年级",
        "teacherId": 1,
        "studentCount": 30,
        "status": 1,
        "createTime": "2024-01-01T10:00:00"
      }
    ],
    "total": 10,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

### 6.2 根据年级ID获取班级列表
**GET** `/classes/by-grade/{gradeId}`

### 6.3 根据教师ID获取班级列表
**GET** `/classes/by-teacher/{teacherId}`

### 6.4 根据ID获取班级详情
**GET** `/classes/{id}`

### 6.5 新增班级
**POST** `/classes`

**请求参数**:
```json
{
  "className": "一班",
  "gradeId": 1,
  "teacherId": 1,
  "description": "string"
}
```

**说明**: 创建时studentCount默认为0，status默认为1

### 6.6 更新班级
**PUT** `/classes/{id}`

### 6.7 删除班级
**DELETE** `/classes/{id}`

---

## 7. 个性推荐接口 `/api/recommendations`

### 7.1 获取个性化推荐
**GET** `/api/recommendations/personalized`

**请求参数**:
- `userId`: 用户ID（必填）
- `limit`: 推荐数量，默认10（可选）

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "courseId": 1,
      "courseName": "Java程序设计",
      "matchScore": 85,
      "totalScore": 85.5,
      "recommendationType": "hybrid",
      "recommendationReason": "基于您的学习偏好推荐"
    }
  ]
}
```

### 7.2 根据推荐类型获取推荐
**GET** `/api/recommendations/by-type`

**请求参数**:
- `userId`: 用户ID（必填）
- `type`: 推荐类型（必填）
  - `content`: 内容推荐
  - `collaborative`: 协同过滤推荐
  - `trending`: 热门推荐
  - `knowledge`: 知识推荐
- `limit`: 推荐数量，默认10（可选）

### 7.3 获取热门课程推荐
**GET** `/api/recommendations/trending`

**请求参数**:
- `limit`: 推荐数量，默认10（可选）

### 7.4 获取学习路径推荐
**GET** `/api/recommendations/learning-paths`

**请求参数**:
- `userId`: 用户ID（必填）

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "pathId": 1,
      "pathName": "Java开发工程师",
      "description": "从入门到精通的Java学习路径",
      "courses": [
        {"courseId": 1, "courseName": "Java基础"},
        {"courseId": 2, "courseName": "Java高级特性"}
      ],
      "estimatedHours": 120,
      "completionRate": 0
    }
  ]
}
```

### 7.5 记录用户行为
**POST** `/api/recommendations/behavior`

**请求参数**:
- `userId`: 用户ID（必填）
- `courseId`: 课程ID（必填）
- `behaviorType`: 行为类型（必填）
  - `view`: 浏览
  - `study`: 学习
  - `complete`: 完成
  - `favorite`: 收藏
- `behaviorValue`: 行为值，默认1.0（可选）

### 7.6 更新用户偏好
**POST** `/api/recommendations/preference`

**请求参数**:
- `userId`: 用户ID（必填）
- `preferenceType`: 偏好类型（必填）
  - `subject`: 学科偏好
  - `grade_level`: 年级偏好
  - `difficulty`: 难度偏好
- `preferenceValue`: 偏好值（必填）
- `score`: 偏好分数，默认1.0（可选）

### 7.7 获取推荐统计信息
**GET** `/api/recommendations/statistics`

**请求参数**:
- `userId`: 用户ID（必填）

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "userId": 1,
    "totalRecommendations": 50,
    "clickedRecommendations": 15,
    "enrolledRecommendations": 8,
    "clickThroughRate": "30%"
  }
}
```

### 7.8 健康检查
**GET** `/api/recommendations/health`

---

## 8. 学习中心接口 `/learning`

### 8.1 获取学生学习记录
**GET** `/learning/records/student/{studentId}`

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "recordId": 1,
      "studentId": 1,
      "courseId": 1,
      "courseName": "Java程序设计",
      "totalLessons": 20,
      "completedLessons": 10,
      "studyDuration": 120,
      "progress": 50.0,
      "isCompleted": false,
      "lastStudyTime": "2024-01-15T10:00:00"
    }
  ]
}
```

### 8.2 获取课程学习记录
**GET** `/learning/records/course/{courseId}`

### 8.3 创建或更新学习记录
**POST** `/learning/record`

**请求参数**:
```json
{
  "recordId": 1,
  "studentId": 1,
  "courseId": 1,
  "lessonId": 1,
  "studyDuration": 30,
  "progress": 50.0,
  "isCompleted": false
}
```

### 8.4 获取学生某课程的学习进度
**GET** `/learning/progress/student/{studentId}/course/{courseId}`

### 8.5 获取学生学习进度统计
**GET** `/learning/student/{studentId}/progress`

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalStudyTime": 360,
    "completedCoursesCount": 2,
    "learningRecordsCount": 5,
    "averageProgress": 65.5,
    "assignmentSubmissionRate": 80.0,
    "recentRecords": [
      {
        "recordId": 1,
        "courseName": "Java程序设计",
        "progress": 50.0,
        "lastStudyTime": "2024-01-15T10:00:00"
      }
    ]
  }
}
```

### 8.6 获取班级统计信息
**GET** `/learning/class/{classId}/statistics`

### 8.7 获取学生的总体学习统计
**GET** `/learning/statistics/student/{studentId}`

### 8.8 获取学习日历
**GET** `/learning/student/{studentId}/calendar`

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "date": "2024-01-15",
      "dayOfMonth": 15,
      "dayOfWeek": 1,
      "studyMinutes": 120,
      "studyHours": 2.0,
      "hasStudy": true
    }
  ]
}
```

### 8.9 获取本周学习统计
**GET** `/learning/student/{studentId}/weekly`

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "weeklyStudyMinutes": 360,
    "weeklyStudyHours": 6.0,
    "targetHours": 15,
    "completionRate": 40,
    "dailyBreakdown": {
      "周一": 60,
      "周二": 120,
      "周三": 0,
      "周四": 90,
      "周五": 90,
      "周六": 0,
      "周日": 0
    }
  }
}
```

---

## 9. 消息通知接口 `/notifications`

### 9.1 获取用户的未读消息数量
**GET** `/notifications/unread-count/{userId}`

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": 5
}
```

### 9.2 获取用户的消息列表
**GET** `/notifications/user/{userId}`

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "notificationId": 1,
      "senderId": 0,
      "receiverId": 1,
      "title": "系统通知",
      "content": "您有一门新课程待学习",
      "notificationType": "system",
      "priority": 1,
      "isRead": false,
      "createTime": "2024-01-15T10:00:00"
    }
  ]
}
```

### 9.3 发送消息给个人
**POST** `/notifications/send-to-user`

**请求参数**:
- `senderId`: 发送者ID（必填）
- `senderName`: 发送者名称（必填）
- `receiverId`: 接收者ID（必填）
- `title`: 消息标题（必填）
- `content`: 消息内容（必填）
- `type`: 消息类型，默认system（可选）
- `priority`: 优先级，默认1（可选）

### 9.4 发送消息给班级
**POST** `/notifications/send-to-class`

**请求参数**:
- `senderId`: 发送者ID（必填）
- `senderName`: 发送者名称（必填）
- `classId`: 班级ID（必填）
- `title`: 消息标题（必填）
- `content`: 消息内容（必填）
- `type`: 消息类型，默认system（可选）
- `priority`: 优先级，默认1（可选）

### 9.5 发送系统消息
**POST** `/notifications/send-system`

**请求参数**:
- `title`: 消息标题（必填）
- `content`: 消息内容（必填）

### 9.6 标记消息为已读
**POST** `/notifications/mark-read/{notificationId}`

### 9.7 标记用户所有消息为已读
**POST** `/notifications/mark-all-read/{userId}`

---

## 10. 能力评估接口 `/api/assessments`

### 10.1 获取可参加的评估列表
**GET** `/api/assessments/available`

**请求参数**:
- `userId`: 用户ID（必填）

### 10.2 根据学科获取评估列表
**GET** `/api/assessments/subject/{subjectId}`

### 10.3 根据年级获取评估列表
**GET** `/api/assessments/grade/{gradeLevel}`

### 10.4 获取评估详情
**GET** `/api/assessments/{assessmentId}`

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "assessment": {
      "assessmentId": 1,
      "assessmentName": "Java基础测试",
      "assessmentType": "knowledge",
      "description": "测试Java基础知识掌握情况",
      "totalScore": 100,
      "timeLimit": 60
    },
    "questions": [
      {
        "questionId": 1,
        "questionType": "single_choice",
        "content": "Java的基本数据类型有哪些？",
        "options": ["int", "String", "boolean", "double"],
        "score": 10
      }
    ]
  }
}
```

### 10.5 开始评估
**POST** `/api/assessments/{assessmentId}/start`

**请求参数**:
- `userId`: 用户ID（必填）

### 10.6 提交答案
**POST** `/api/assessments/{assessmentId}/submit`

**请求参数**:
- `userId`: 用户ID（必填）
- `answers`: 答案Map，格式为 `{"题号": "答案"}`（必填）

**请求示例**:
```json
{
  "userId": 1,
  "answers": {
    "1": "A",
    "2": "B,C",
    "3": "Java是一种编程语言"
  }
}
```

### 10.7 获取用户评估历史
**GET** `/api/assessments/history/{userId}`

### 10.8 获取评估统计信息
**GET** `/api/assessments/statistics/{userId}`

### 10.9 生成能力分析报告
**GET** `/api/assessments/report/{userAssessmentId}`

### 10.10 检查用户是否有权限参加评估
**GET** `/api/assessments/{assessmentId}/permission/{userId}`

### 10.11 获取用户当前进行中的评估
**GET** `/api/assessments/in-progress/{userId}`

---

## 11. LangChain智能助手接口 `/api/langchain`

### 11.1 智能问答
**POST** `/api/langchain/qa`

**请求参数**:
```json
{
  "userId": 1,
  "question": "Java中的继承是什么？",
  "sessionId": "optional-session-id"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "answer": "继承是面向对象编程的三大特性之一...",
    "confidence": 0.95,
    "sources": ["Java编程思想", "官方文档"],
    "responseTime": 1200
  }
}
```

### 11.2 获取用户交互历史
**GET** `/api/langchain/history/{userId}`

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "question": "Java中的继承是什么？",
      "answer": "继承是面向对象编程的三大特性之一...",
      "timestamp": "2024-01-15T10:00:00"
    }
  ]
}
```

### 11.3 清除用户历史
**DELETE** `/api/langchain/history/{userId}`

### 11.4 健康检查
**GET** `/api/langchain/health`

---

## 12. 数据字典

### 12.1 用户类型
| 值 | 说明 |
|----|------|
| 1 | 学生 |
| 2 | 教师 |
| 3 | 管理员 |

### 12.2 用户状态
| 值 | 说明 |
|----|------|
| 0 | 禁用 |
| 1 | 启用 |

### 12.3 年级级别
| 值 | 说明 |
|----|------|
| 1 | 小学 |
| 2 | 初中 |
| 3 | 高中 |
| 4 | 大学 |

### 12.4 资源类型
| 值 | 说明 |
|----|------|
| video | 视频 |
| document | 文档 |
| image | 图片 |
| audio | 音频 |
| other | 其他 |

### 12.5 通知类型
| 值 | 说明 |
|----|------|
| system | 系统通知 |
| course | 课程通知 |
| assignment | 作业通知 |
| message | 个人消息 |

### 12.6 推荐类型
| 值 | 说明 |
|----|------|
| content | 内容推荐 |
| collaborative | 协同过滤推荐 |
| trending | 热门推荐 |
| knowledge | 知识推荐 |

### 12.7 用户行为类型
| 值 | 说明 |
|----|------|
| view | 浏览 |
| study | 学习 |
| complete | 完成 |
| favorite | 收藏 |

---

## 13. 附录

### 13.1 接口调用示例（JavaScript）

```javascript
// GET请求示例
async function getUsers() {
  const response = await fetch('http://localhost:8081/users/page?page=1&size=10', {
    method: 'GET',
    headers: {
      'Authorization': 'Bearer ' + localStorage.getItem('token')
    }
  });
  return await response.json();
}

// POST请求示例
async function createUser(userData) {
  const response = await fetch('http://localhost:8081/users', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': 'Bearer ' + localStorage.getItem('token')
    },
    body: JSON.stringify(userData)
  });
  return await response.json();
}

// 文件上传示例
async function uploadResource(file, uploaderId, uploaderName) {
  const formData = new FormData();
  formData.append('file', file);
  formData.append('uploaderId', uploaderId);
  formData.append('uploaderName', uploaderName);
  
  const response = await fetch('http://localhost:8081/resources/upload', {
    method: 'POST',
    headers: {
      'Authorization': 'Bearer ' + localStorage.getItem('token')
    },
    body: formData
  });
  return await response.json();
}
```

### 13.2 版本历史

| 版本 | 日期 | 修改内容 | 作者 |
|------|------|----------|------|
| v1.0 | 2026-03-24 | 初始版本，包含所有已开发接口 | AI助手 |

---

**文档结束**
