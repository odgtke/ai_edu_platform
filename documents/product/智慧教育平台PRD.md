# 智慧教育平台产品需求文档 (PRD)

## 文档信息

| 项目 | 内容 |
|------|------|
| 产品名称 | 智慧教育平台 |
| 版本号 | v2.0 |
| 编写日期 | 2026-03-24 |
| 目标用户 | 学生、教师、管理员 |

---

## 1. 产品概述

### 1.1 产品背景
智慧教育平台是一个面向教育行业的综合管理系统，旨在通过数字化手段提升教学管理效率，为学生提供个性化学习体验，为教师提供便捷的教学工具，为管理员提供全面的数据管理能力。

### 1.2 产品目标
- 构建完整的在线教育生态系统
- 提供个性化学习推荐服务
- 实现教学资源的数字化管理
- 支持多角色协同工作（学生/教师/管理员）

### 1.3 用户角色

| 角色 | 职责 | 核心功能 |
|------|------|----------|
| 学生 | 学习参与者 | 课程学习、资源浏览、能力评估、个性推荐 |
| 教师 | 教学实施者 | 课程管理、作业批改、学生管理、资源上传 |
| 管理员 | 系统管理者 | 用户管理、资源配置、数据统计、系统配置 |

---

## 2. 功能模块

### 2.1 用户管理模块

#### 2.1.1 功能描述
提供完整的用户生命周期管理，支持多角色用户体系，包括JWT认证、密码加密、关联课程检查等安全机制。

#### 2.1.2 功能列表
- **用户注册/登录**：支持用户名密码登录，JWT Token认证，密码强度验证
- **用户信息管理**：增删改查用户基本信息，支持分页查询和条件筛选
- **角色权限管理**：学生(1)、教师(2)、管理员(3)三种角色
- **用户状态管理**：启用(1)/禁用(0)账户
- **安全删除检查**：删除教师用户前检查关联课程，防止数据不一致

#### 2.1.3 数据模型
```java
User {
    userId: Long          // 用户ID
    username: String      // 用户名
    realName: String      // 真实姓名
    password: String      // 密码（BCrypt加密存储）
    email: String         // 邮箱
    phone: String         // 电话
    userType: Integer     // 用户类型：1-学生 2-教师 3-管理员
    status: Integer       // 状态：0-禁用 1-启用
    avatar: String        // 头像URL
    createTime: DateTime  // 创建时间
    updateTime: DateTime  // 更新时间
}
```

#### 2.1.4 接口清单
| 接口 | 方法 | 描述 |
|------|------|------|
| /auth/login | POST | 用户登录，返回JWT Token |
| /auth/register | POST | 用户注册，密码强度验证 |
| /users/page | GET | 分页查询用户，支持多条件筛选 |
| /users | GET/POST/PUT | 用户CRUD操作 |
| /users/{id} | GET/DELETE | 查询/删除用户 |
| /users/count | GET | 获取用户总数 |

---

### 2.2 课程管理模块

#### 2.2.1 功能描述
管理课程信息，支持按年级、学科分类，关联教师和学生，支持多维度筛选查询。

#### 2.2.2 功能列表
- **课程CRUD**：创建、查询、更新、删除课程
- **课程分类**：按年级（1-小学/2-初中/3-高中/4-大学）、学科分类
- **教师关联**：指定授课教师，支持根据教师查询课程
- **年级筛选**：支持根据年级级别查询课程
- **课程统计**：查看课程总数

#### 2.2.3 数据模型
```java
Course {
    courseId: Long        // 课程ID
    courseName: String    // 课程名称
    courseCode: String    // 课程编码
    description: String   // 课程描述
    teacherId: Long       // 教师ID
    gradeLevel: Integer   // 年级：1-小学 2-初中 3-高中 4-大学
    subjectId: Long       // 学科ID
    credit: Double        // 学分
    status: Integer       // 状态：0-停用 1-启用
    coverImage: String    // 封面图片
    totalLessons: Integer // 总课时数
    createTime: DateTime
    updateTime: DateTime
}
```

#### 2.2.4 接口清单
| 接口 | 方法 | 描述 |
|------|------|------|
| /courses/page | GET | 分页查询课程，支持6种筛选条件 |
| /courses | GET/POST/PUT | 课程CRUD操作 |
| /courses/{id} | GET/DELETE | 查询/删除课程 |
| /courses/count | GET | 获取课程总数 |
| /courses/teacher/{teacherId} | GET | 根据教师查询课程 |
| /courses/grade/{gradeLevel} | GET | 根据年级查询课程 |

---

### 2.3 资源管理模块

#### 2.3.1 功能描述
管理教学资源，支持多种资源类型（视频、文档、音频、图片）的上传、下载、分类和课程关联。

#### 2.3.2 功能列表
- **资源上传**：支持文件上传，自动识别资源类型，记录上传者信息
- **资源分类**：按类型（video/document/image/audio/other）分类管理
- **资源检索**：按名称、类型、关键词搜索，支持分页
- **资源下载**：支持资源下载，自动统计下载次数
- **资源关联**：关联资源到具体课程，支持必修/选修设置
- **浏览统计**：自动记录资源浏览次数

#### 2.3.3 数据模型
```java
Resource {
    resourceId: Long      // 资源ID
    resourceName: String  // 资源名称
    resourceType: String  // 资源类型：video/document/image/audio/other
    fileName: String      // 原始文件名
    filePath: String      // 文件存储路径
    fileSize: Long        // 文件大小(字节)
    fileFormat: String    // 文件格式(mp4, pdf, doc等)
    duration: Integer     // 时长(秒), 视频/音频专用
    thumbnailUrl: String  // 缩略图URL
    description: String   // 描述
    uploaderId: Long      // 上传者ID
    uploaderName: String  // 上传者名称
    downloadCount: Integer// 下载次数
    viewCount: Integer    // 浏览次数
    status: Integer       // 状态: 0-禁用 1-启用 2-转码中
    createTime: DateTime
    updateTime: DateTime
}

CourseResource {
    id: Long
    courseId: Long        // 课程ID
    resourceId: Long      // 资源ID
    resourceOrder: Integer// 资源顺序
    isRequired: Integer   // 是否必修: 0-选修 1-必修
}
```

#### 2.3.4 接口清单
| 接口 | 方法 | 描述 |
|------|------|------|
| /resources/upload | POST | 上传资源文件(multipart/form-data) |
| /resources/page | GET | 分页查询资源，支持类型和关键词筛选 |
| /resources/types | GET | 获取所有资源类型列表 |
| /resources/{resourceId} | GET | 获取资源详情(自动增加浏览次数) |
| /resources/{resourceId}/download | GET | 下载资源文件 |
| /resources/{resourceId} | DELETE | 删除资源 |
| /resources/course/{courseId} | GET | 获取课程的资源列表 |
| /resources/course/{courseId}/resource/{resourceId} | POST/DELETE | 关联/移除课程资源 |
| /resources/course/{courseId}/check/{resourceId} | GET | 检查资源是否已关联课程 |

---

### 2.4 班级与年级管理模块

#### 2.4.1 功能描述
管理学校的年级和班级结构，支持学生分班管理，建立完整的学校组织架构。

#### 2.4.2 功能列表
- **年级管理**：创建和管理年级（一年级、二年级等），支持启用/禁用
- **班级管理**：在年级下创建班级，指定班主任
- **学生分班**：将学生分配到班级
- **班级统计**：查看班级人数、课程完成情况
- **年级筛选**：根据年级查询班级列表
- **教师关联**：根据教师查询管理的班级

#### 2.4.3 数据模型
```java
Grade {
    gradeId: Long         // 年级ID
    gradeName: String     // 年级名称（如：一年级）
    gradeLevel: Integer   // 年级级别：1-小学 2-初中 3-高中 4-大学
    description: String   // 描述
    status: Integer       // 状态: 0-禁用 1-启用
    createTime: DateTime
    updateTime: DateTime
}

Clazz {
    classId: Long         // 班级ID
    className: String     // 班级名称
    gradeId: Long         // 所属年级ID
    gradeName: String     // 年级名称(冗余字段)
    teacherId: Long       // 班主任ID
    studentCount: Integer // 学生人数
    description: String   // 描述
    status: Integer       // 状态
    createTime: DateTime
    updateTime: DateTime
}

UserClass {
    id: Long
    userId: Long          // 学生ID
    classId: Long         // 班级ID
    joinTime: DateTime    // 加入时间
}
```

#### 2.4.4 接口清单
| 接口 | 方法 | 描述 |
|------|------|------|
| /grades/list | GET | 获取所有年级列表 |
| /grades/active | GET | 获取启用的年级列表 |
| /grades/{id} | GET/PUT/DELETE | 年级查询/更新/删除 |
| /grades | POST | 新增年级 |
| /classes/page | GET | 分页查询班级，支持年级和关键词筛选 |
| /classes/by-grade/{gradeId} | GET | 根据年级查询班级 |
| /classes/by-teacher/{teacherId} | GET | 根据教师查询班级 |
| /classes/{id} | GET/PUT/DELETE | 班级查询/更新/删除 |
| /classes | POST | 新增班级 |

---

### 2.5 个性推荐模块（核心功能）

#### 2.5.1 功能描述
基于混合推荐算法，为用户提供个性化的课程和学习资源推荐，是平台的核心智能化功能。

#### 2.5.2 推荐算法
采用**加权混合推荐算法**，权重分配：
- **协同过滤推荐**（40%）：基于相似用户行为模式
- **内容推荐**（30%）：基于用户偏好和课程标签匹配
- **热门推荐**（20%）：基于用户行为统计的热门课程
- **知识推荐**（10%）：基于课程关联性和学习路径

#### 2.5.3 功能列表
- **个性化推荐**：综合算法生成推荐列表，计算匹配度分数
- **分类推荐**：按推荐类型查看（协同/内容/热门/知识）
- **热门课程**：查看当前最受欢迎的课程
- **学习路径**：基于已完成课程推荐进阶学习路线
- **偏好设置**：用户可调整个人偏好（学科/年级/难度）
- **行为记录**：记录用户浏览、学习、收藏行为，用于优化推荐
- **推荐统计**：查看推荐点击率、报名率等统计数据

#### 2.5.4 数据模型
```java
// 用户偏好
UserPreference {
    id: Long
    userId: Long
    preferenceType: String    // 偏好类型：subject/grade_level/difficulty
    preferenceValue: String   // 偏好值
    preferenceScore: Decimal  // 偏好分数
    createTime: DateTime
    updateTime: DateTime
}

// 用户行为
UserBehavior {
    id: Long
    userId: Long
    courseId: Long
    behaviorType: String      // 行为类型：view/study/complete/favorite
    behaviorValue: Decimal    // 行为值
    ipAddress: String         // IP地址
    userAgent: String         // 用户代理
    createdTime: DateTime
}

// 推荐结果
Recommendation {
    recommendationId: Long
    userId: Long
    courseId: Long
    recommendationType: String // 推荐类型：hybrid/content/collaborative/trending/knowledge
    recommendationScore: Decimal // 推荐分数
    recommendationReason: String // 推荐理由
    isClicked: Boolean        // 是否点击
    isEnrolled: Boolean       // 是否报名
    createdTime: DateTime
    expiredTime: DateTime     // 过期时间
}

// 课程相似度
CourseSimilarity {
    id: Long
    course1Id: Long
    course2Id: Long
    similarityScore: Decimal  // 相似度分数(0-1)
}

// 学习路径
LearningPath {
    id: Long
    userId: Long
    pathName: String
    pathDescription: String
    courseIds: String         // JSON格式课程ID数组
    pathStatus: String        // 状态：active/completed/archived
    completionRate: Decimal   // 完成率
    estimatedHours: Integer   // 预估学习小时数
}
```

#### 2.5.5 接口清单
| 接口 | 方法 | 描述 |
|------|------|------|
| /api/recommendations/personalized | GET | 获取个性化推荐列表 |
| /api/recommendations/by-type | GET | 按类型获取推荐(content/collaborative/trending/knowledge) |
| /api/recommendations/trending | GET | 获取热门课程推荐 |
| /api/recommendations/learning-paths | GET | 获取学习路径推荐 |
| /api/recommendations/behavior | POST | 记录用户行为 |
| /api/recommendations/preference | POST | 更新用户偏好 |
| /api/recommendations/statistics | GET | 获取推荐统计信息 |
| /api/recommendations/health | GET | 推荐系统健康检查 |

---

### 2.6 学习中心模块

#### 2.6.1 功能描述
学生学习的核心区域，记录学习进度，管理学习资源，提供全面的学习数据统计。

#### 2.6.2 功能列表
- **我的课程**：查看已选课程和学习进度，包含课程名称和课时信息
- **学习记录**：记录学习时长、完成章节，支持批量查询优化
- **资源学习**：在线查看课程资源，记录学习进度
- **学习统计**：
  - 总学习时长统计
  - 已完成课程数
  - 平均学习进度
  - 作业提交率
  - 最近学习记录
- **学习日历**：按日期统计学习时长，支持最近30天数据
- **周统计**：本周学习时长、目标完成率、按天统计
- **班级统计**：班级整体学习情况

#### 2.6.3 数据模型
```java
LearningRecord {
    recordId: Long
    studentId: Long         // 学生ID
    courseId: Long          // 课程ID
    lessonId: Long          // 课时ID
    studyDuration: Integer  // 学习时长（分钟）
    progress: Decimal       // 学习进度(0-100)
    isCompleted: Boolean    // 是否完成
    lastStudyTime: DateTime // 最后学习时间
    createTime: DateTime
    updateTime: DateTime
}

// DTO包含课程信息
LearningRecordDTO {
    recordId: Long
    studentId: Long
    courseId: Long
    courseName: String      // 课程名称
    totalLessons: Integer   // 总课时数
    completedLessons: Integer // 已完成课时数
    studyDuration: Integer
    progress: Decimal
    isCompleted: Boolean
    lastStudyTime: DateTime
}
```

#### 2.6.4 接口清单
| 接口 | 方法 | 描述 |
|------|------|------|
| /learning/records/student/{studentId} | GET | 获取学生学习记录(含课程名称) |
| /learning/records/course/{courseId} | GET | 获取课程学习记录 |
| /learning/record | POST | 创建或更新学习记录 |
| /learning/progress/student/{studentId}/course/{courseId} | GET | 获取某课程学习进度 |
| /learning/student/{studentId}/progress | GET | 获取学生学习进度统计 |
| /learning/class/{classId}/statistics | GET | 获取班级统计信息 |
| /learning/statistics/student/{studentId} | GET | 获取学生总体统计 |
| /learning/student/{studentId}/calendar | GET | 获取学习日历(最近30天) |
| /learning/student/{studentId}/weekly | GET | 获取本周学习统计 |

---

### 2.7 作业与评估模块

#### 2.7.1 功能描述
支持作业布置、提交、批改，以及学生能力评估测试，形成完整的教学评估闭环。

#### 2.7.2 功能列表
- **作业管理**：教师布置作业，设置截止时间、总分
- **作业提交**：学生在线提交作业，支持多次提交
- **作业批改**：教师批改作业，给出评分和反馈
- **提交率统计**：统计学生作业提交情况
- **能力评估**：
  - 多维度评估（知识/技能/能力）
  - 按学科/年级筛选评估
  - 开始评估和提交答案
  - 评估历史记录
  - 能力分析报告生成
- **权限检查**：检查用户是否有权限参加评估

#### 2.7.3 数据模型
```java
Assignment {
    assignmentId: Long
    courseId: Long
    title: String            // 作业标题
    description: String      // 作业描述
    teacherId: Long          // 布置教师ID
    deadline: DateTime       // 截止时间
    totalScore: Integer      // 总分
    status: Integer          // 状态
    createTime: DateTime
    updateTime: DateTime
}

AssignmentSubmission {
    submissionId: Long
    assignmentId: Long
    studentId: Long
    content: String          // 提交内容
    attachments: String      // 附件JSON
    submitTime: DateTime
    score: Integer           // 得分
    feedback: String         // 教师反馈
    status: Integer          // 状态：0-未提交 1-已提交 2-已批改
}

Assessment {
    assessmentId: Long
    assessmentName: String   // 评估名称
    assessmentType: String   // 评估类型：knowledge/skill/comprehensive
    description: String
    subjectId: Long          // 学科ID
    gradeLevel: Integer      // 年级
    totalScore: Integer
    timeLimit: Integer       // 时间限制(分钟)
    status: Integer
}

AssessmentQuestion {
    questionId: Long
    assessmentId: Long
    questionType: String     // 题型：single_choice/multiple_choice/fill_blank/essay
    content: String          // 题目内容
    options: String          // 选项JSON
    correctAnswer: String    // 正确答案
    score: Integer           // 分值
    orderNum: Integer        // 顺序
}

UserAssessment {
    id: Long
    userId: Long
    assessmentId: Long
    score: Decimal           // 得分
    answers: String          // 答案JSON
    abilityAnalysis: String  // 能力分析JSON
    status: String           // 状态：in_progress/completed
    startTime: DateTime
    completedTime: DateTime
}
```

#### 2.7.4 接口清单
| 接口 | 方法 | 描述 |
|------|------|------|
| /api/assessments/available | GET | 获取可参加的评估列表 |
| /api/assessments/subject/{subjectId} | GET | 根据学科获取评估 |
| /api/assessments/grade/{gradeLevel} | GET | 根据年级获取评估 |
| /api/assessments/{assessmentId} | GET | 获取评估详情(含题目) |
| /api/assessments/{assessmentId}/start | POST | 开始评估 |
| /api/assessments/{assessmentId}/submit | POST | 提交答案 |
| /api/assessments/history/{userId} | GET | 获取评估历史 |
| /api/assessments/statistics/{userId} | GET | 获取评估统计 |
| /api/assessments/report/{userAssessmentId} | GET | 生成能力分析报告 |
| /api/assessments/{assessmentId}/permission/{userId} | GET | 检查评估权限 |
| /api/assessments/in-progress/{userId} | GET | 获取进行中的评估 |

---

### 2.8 消息通知模块

#### 2.8.1 功能描述
系统消息推送中心，支持多种通知类型和发送方式，确保信息及时触达。

#### 2.8.2 功能列表
- **系统通知**：平台公告、更新提醒
- **个人消息**：私信、一对一沟通
- **班级广播**：向整个班级发送通知
- **未读计数**：实时获取未读消息数量
- **消息管理**：
  - 标记单条消息已读
  - 标记所有消息已读
  - 消息列表查询
- **优先级设置**：支持消息优先级（1-普通 2-重要 3-紧急）

#### 2.8.3 数据模型
```java
Notification {
    notificationId: Long
    senderId: Long           // 发送者ID（0表示系统）
    senderName: String       // 发送者名称
    receiverId: Long         // 接收者ID
    title: String            // 通知标题
    content: String          // 通知内容
    notificationType: String // 类型：system/course/assignment/message
    relatedId: Long          // 关联业务ID
    priority: Integer        // 优先级：1-普通 2-重要 3-紧急
    isRead: Boolean          // 是否已读
    createTime: DateTime
}
```

#### 2.8.4 接口清单
| 接口 | 方法 | 描述 |
|------|------|------|
| /notifications/unread-count/{userId} | GET | 获取未读消息数量 |
| /notifications/user/{userId} | GET | 获取用户消息列表 |
| /notifications/send-to-user | POST | 发送消息给个人 |
| /notifications/send-to-class | POST | 发送消息给班级 |
| /notifications/send-system | POST | 发送系统消息 |
| /notifications/mark-read/{notificationId} | POST | 标记消息已读 |
| /notifications/mark-all-read/{userId} | POST | 标记所有消息已读 |

---

### 2.9 AI智能助手模块

#### 2.9.1 功能描述
基于LangChain4j的智能问答助手，提供教育相关的智能服务，支持上下文记忆。

#### 2.9.2 功能列表
- **智能问答**：回答教育相关问题，基于LangChain4j实现
- **上下文记忆**：维护用户会话历史，支持连续对话
- **历史记录**：查看用户交互历史
- **历史清除**：清除用户历史记录
- **健康检查**：服务状态监控

#### 2.9.3 数据模型
```java
// 交互记录
InteractionRecord {
    question: String         // 问题
    answer: String           // 答案
    confidence: Decimal      // 置信度
    sources: List<String>    // 参考来源
    timestamp: DateTime      // 时间戳
}

// 问答响应
QAResponse {
    answer: String           // 回答内容
    confidence: Decimal      // 置信度(0-1)
    sources: List<String>    // 知识来源
    responseTime: Long       // 响应时间(ms)
}
```

#### 2.9.4 接口清单
| 接口 | 方法 | 描述 |
|------|------|------|
| /api/langchain/qa | POST | 智能问答 |
| /api/langchain/history/{userId} | GET | 获取用户交互历史 |
| /api/langchain/history/{userId} | DELETE | 清除用户历史 |
| /api/langchain/health | GET | 健康检查 |

---

## 3. 技术架构

### 3.1 后端技术栈
| 技术 | 版本 | 用途 |
|------|------|------|
| Spring Boot | 2.7.10 | 核心框架 |
| MyBatis Plus | 3.5.x | ORM框架 |
| MySQL | 8.0 | 主数据库 |
| Redis | 6.x | 缓存 |
| Spring Security | 5.x | 安全认证 |
| JWT | 0.11.x | Token认证 |
| LangChain4j | 0.27.x | AI智能助手 |
| Lombok | 1.18.x | 代码简化 |

### 3.2 前端技术栈
| 技术 | 版本 | 用途 |
|------|------|------|
| Vue | 3.3.x | 前端框架 |
| Vite | 4.x | 构建工具 |
| Element Plus | 2.3.x | UI组件库 |
| Vue Router | 4.x | 路由管理 |
| Axios | 1.4.x | HTTP客户端 |
| Element Plus Icons | - | 图标库 |

### 3.3 系统架构图

```
┌─────────────────────────────────────────────────────────────┐
│                        前端层 (Vue 3)                        │
│  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐│
│  │ 仪表盘  │ │用户管理 │ │课程管理 │ │资源管理 │ │个性推荐 ││
│  └─────────┘ └─────────┘ └─────────┘ └─────────┘ └─────────┘│
│  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐│
│  │学习中心 │ │班级年级 │ │能力评估 │ │消息通知 │ │AI助手  ││
│  └─────────┘ └─────────┘ └─────────┘ └─────────┘ └─────────┘│
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                      API网关层 (Vite Proxy)                  │
│                      http://localhost:8081                   │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                      后端层 (Spring Boot)                    │
│  ┌─────────────────────────────────────────────────────────┐│
│  │                    Controller 层                        ││
│  │  AuthController | UserController | CourseController    ││
│  │  ResourceController | RecommendationController         ││
│  │  LearningController | AssessmentController             ││
│  │  NotificationController | LangChainAgentController     ││
│  └─────────────────────────────────────────────────────────┘│
│  ┌─────────────────────────────────────────────────────────┐│
│  │                    Service 层                           ││
│  │  业务逻辑 | 推荐算法引擎 | AI智能助手                    ││
│  └─────────────────────────────────────────────────────────┘│
│  ┌─────────────────────────────────────────────────────────┐│
│  │                    Mapper 层                            ││
│  │  MyBatis Plus 数据访问层                                ││
│  └─────────────────────────────────────────────────────────┘│
│  ┌─────────────────────────────────────────────────────────┐│
│  │                    Entity 层                            ││
│  │  21个实体类，覆盖完整业务领域                            ││
│  └─────────────────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                      数据层                                  │
│  ┌─────────┐ ┌─────────┐ ┌─────────┐                       │
│  │  MySQL  │ │  Redis  │ │ 文件存储 │                       │
│  │  30+表  │ │  缓存   │ │ uploads │                       │
│  └─────────┘ └─────────┘ └─────────┘                       │
└─────────────────────────────────────────────────────────────┘
```

---

## 4. 数据库设计

### 4.1 数据库表清单

| 表名 | 说明 | 核心字段 |
|------|------|----------|
| edu_user | 用户表 | user_id, username, user_type, status |
| edu_course | 课程表 | course_id, course_name, teacher_id, grade_level |
| edu_resource | 资源表 | resource_id, resource_type, file_path, uploader_id |
| edu_course_resource | 课程资源关联表 | course_id, resource_id, is_required |
| edu_grade | 年级表 | grade_id, grade_name, grade_level |
| edu_class | 班级表 | class_id, class_name, grade_id, teacher_id |
| edu_user_class | 用户班级关联表 | user_id, class_id |
| edu_learning_record | 学习记录表 | student_id, course_id, progress, study_duration |
| edu_assignment | 作业表 | assignment_id, course_id, teacher_id, deadline |
| edu_assignment_submission | 作业提交表 | submission_id, assignment_id, student_id, score |
| edu_assessment | 评估表 | assessment_id, assessment_name, subject_id, grade_level |
| edu_assessment_question | 评估题目表 | question_id, assessment_id, question_type, content |
| edu_user_assessment | 用户评估记录表 | user_id, assessment_id, score, ability_analysis |
| edu_notification | 通知表 | notification_id, sender_id, receiver_id, type, is_read |
| user_preferences | 用户偏好表 | user_id, preference_type, preference_value |
| user_behaviors | 用户行为表 | user_id, course_id, behavior_type, behavior_value |
| recommendations | 推荐结果表 | user_id, course_id, recommendation_type, score |
| course_similarities | 课程相似度表 | course1_id, course2_id, similarity_score |
| learning_paths | 学习路径表 | user_id, path_name, course_ids, completion_rate |

### 4.2 核心表关系图

```
edu_user (1) ────────< (N) edu_learning_record
    │                        │
    │                        ▼
    │                   (N) edu_course
    │                        │
    │                        ▼
    │                   (N) edu_resource
    │                        │
    │                        ▼
    │                   (N) edu_assignment
    │                        │
    │                        ▼
    │                   (N) edu_assignment_submission
    │
    ├───────< (N) edu_user_class >────── (N) edu_class
    │                                        │
    │                                        ▼
    │                                   (N) edu_grade
    │
    ├───────< (N) edu_user_assessment >── (N) edu_assessment
    │                                        │
    │                                        ▼
    │                                   (N) edu_assessment_question
    │
    ├───────< (N) user_preferences
    ├───────< (N) user_behaviors
    ├───────< (N) recommendations
    └───────< (N) edu_notification (receiver)
              (N) edu_notification (sender)
```

---

## 5. 非功能性需求

### 5.1 性能需求
| 指标 | 目标值 |
|------|--------|
| API平均响应时间 | < 500ms |
| 推荐算法计算时间 | < 2s |
| 文件上传速度 | > 2MB/s |
| 并发用户支持 | 100+ |
| 系统可用性 | 99.5% |

### 5.2 安全需求
- **认证鉴权**：JWT Token认证，支持Token刷新，24小时过期
- **密码加密**：BCrypt加密存储，密码强度要求（8位以上，含大小写字母、数字、特殊字符）
- **接口权限**：基于角色的接口访问控制
- **数据验证**：服务端参数校验，防止SQL注入
- **外键检查**：删除操作前检查关联数据，防止数据不一致

### 5.3 可靠性需求
- **数据备份**：定期数据库备份
- **事务管理**：关键操作使用数据库事务
- **错误处理**：全局异常处理，统一错误响应
- **日志记录**：操作日志和错误日志记录

---

## 6. 开发状态

### 6.1 已完成功能 ✅

| 模块 | 功能点 | 状态 |
|------|--------|------|
| 用户管理 | 注册/登录/JWT认证 | ✅ |
| 用户管理 | 用户CRUD/分页/筛选 | ✅ |
| 用户管理 | 关联课程删除检查 | ✅ |
| 课程管理 | 课程CRUD/分页 | ✅ |
| 课程管理 | 年级/学科分类 | ✅ |
| 课程管理 | 教师关联 | ✅ |
| 资源管理 | 文件上传/下载 | ✅ |
| 资源管理 | 资源分类/搜索 | ✅ |
| 资源管理 | 课程资源关联 | ✅ |
| 年级管理 | 年级CRUD | ✅ |
| 班级管理 | 班级CRUD/分页 | ✅ |
| 班级管理 | 年级关联/教师关联 | ✅ |
| 个性推荐 | 混合推荐算法 | ✅ |
| 个性推荐 | 四种推荐类型 | ✅ |
| 个性推荐 | 学习路径推荐 | ✅ |
| 个性推荐 | 用户行为记录 | ✅ |
| 学习中心 | 学习记录管理 | ✅ |
| 学习中心 | 学习进度统计 | ✅ |
| 学习中心 | 学习日历/周统计 | ✅ |
| 能力评估 | 评估CRUD | ✅ |
| 能力评估 | 题目管理 | ✅ |
| 能力评估 | 开始/提交/报告 | ✅ |
| 作业管理 | 作业CRUD | ✅ |
| 作业管理 | 提交/批改 | ✅ |
| 消息通知 | 个人/班级/系统消息 | ✅ |
| 消息通知 | 已读/未读管理 | ✅ |
| AI助手 | LangChain问答 | ✅ |
| AI助手 | 历史记录管理 | ✅ |
| 前端页面 | 12个功能页面 | ✅ |

### 6.2 待优化功能 📋

| 模块 | 功能点 | 优先级 |
|------|--------|--------|
| 前端 | UI统一优化 | 中 |
| 推荐 | 算法性能优化 | 中 |
| 系统 | 缓存策略完善 | 低 |
| 前端 | 移动端适配 | 低 |
| 系统 | 数据分析报表 | 低 |

---

## 7. 附录

### 7.1 项目结构

```
智慧教育平台/
├── backend/                          # 后端项目
│   ├── src/main/java/com/chinaunicom/edu/
│   │   ├── agent/                    # AI智能体
│   │   │   └── agents/
│   │   │       └── LangChainQAAgent.java
│   │   ├── common/                   # 公共类
│   │   │   └── Result.java           # 统一响应
│   │   ├── config/                   # 配置类
│   │   │   ├── SecurityConfig.java   # 安全配置
│   │   │   └── MybatisPlusConfig.java
│   │   ├── controller/               # 控制器(13个)
│   │   │   ├── AuthController.java
│   │   │   ├── UserController.java
│   │   │   ├── CourseController.java
│   │   │   ├── ResourceController.java
│   │   │   ├── GradeController.java
│   │   │   ├── ClazzController.java
│   │   │   ├── RecommendationController.java
│   │   │   ├── LearningController.java
│   │   │   ├── AssessmentController.java
│   │   │   ├── NotificationController.java
│   │   │   └── LangChainAgentController.java
│   │   ├── dto/                      # DTO
│   │   │   └── LearningRecordDTO.java
│   │   ├── entity/                   # 实体类(21个)
│   │   ├── mapper/                   # Mapper接口
│   │   ├── service/                  # Service层
│   │   │   └── impl/                 # 实现类
│   │   │       └── RecommendationServiceImpl.java
│   │   └── utils/                    # 工具类
│   │       ├── JwtUtil.java
│   │       └── PasswordUtil.java
│   └── pom.xml
├── frontend/                         # 前端项目
│   ├── src/
│   │   ├── views/                    # 页面(12个)
│   │   │   ├── Home.vue              # 仪表盘
│   │   │   ├── Users.vue             # 用户管理
│   │   │   ├── Courses.vue           # 课程管理
│   │   │   ├── Resources.vue         # 资源管理
│   │   │   ├── Classes.vue           # 班级管理
│   │   │   ├── Recommendation.vue    # 个性推荐
│   │   │   ├── Learning.vue          # 学习中心
│   │   │   ├── Assessment.vue        # 能力评估
│   │   │   ├── NotificationCenter.vue# 消息通知
│   │   │   ├── Agent.vue             # AI助手
│   │   │   ├── Settings.vue          # 设置
│   │   │   └── Help.vue              # 帮助
│   │   ├── App.vue                   # 主布局
│   │   └── main.js                   # 入口
│   └── package.json
└── documents/                        # 文档
    ├── API接口文档.md
    ├── 智慧教育平台PRD.md            # 本文档
    ├── requirements/                 # 需求文档
    ├── product/                      # 产品文档
    └── manual/                       # 使用手册
```

### 7.2 核心接口统计

| 模块 | 接口数量 |
|------|----------|
| 认证授权 | 2 |
| 用户管理 | 7 |
| 课程管理 | 9 |
| 资源管理 | 10 |
| 年级管理 | 6 |
| 班级管理 | 7 |
| 个性推荐 | 8 |
| 学习中心 | 9 |
| 消息通知 | 7 |
| 能力评估 | 11 |
| AI助手 | 4 |
| **总计** | **80+** |

### 7.3 版本历史

| 版本 | 日期 | 修改内容 | 作者 |
|------|------|----------|------|
| v1.0 | 2026-03-01 | 初始需求规格 | 需求分析师AI |
| v2.0 | 2026-03-24 | 根据实际开发功能更新 | AI助手 |

---

**文档结束**