# 智慧教育平台 - 项目结构优化方案

## 优化后的目录结构

```
AI_code_2026/
├── backend/                          # 后端服务
│   ├── src/main/java/com/chinaunicom/edu/
│   │   ├── EduPlatformApplication.java
│   │   ├── common/                   # 公共类
│   │   │   └── Result.java
│   │   ├── config/                   # 配置类
│   │   │   ├── CorsConfig.java
│   │   │   ├── MybatisPlusConfig.java
│   │   │   ├── SecurityConfig.java
│   │   │   └── WebConfig.java
│   │   ├── controller/               # 控制器层
│   │   │   ├── AgentController.java
│   │   │   ├── AssessmentController.java
│   │   │   ├── AuthController.java
│   │   │   ├── CourseController.java
│   │   │   ├── LearningController.java
│   │   │   ├── RecommendationController.java
│   │   │   └── UserController.java
│   │   ├── dto/                      # 数据传输对象
│   │   │   ├── LearningRecordDTO.java
│   │   │   └── UserDTO.java
│   │   ├── entity/                   # 实体类
│   │   │   ├── Assignment.java
│   │   │   ├── AssignmentSubmission.java
│   │   │   ├── Course.java
│   │   │   ├── LearningRecord.java
│   │   │   └── User.java
│   │   ├── exception/                # 异常处理（新增）
│   │   │   ├── BusinessException.java
│   │   │   └── GlobalExceptionHandler.java
│   │   ├── mapper/                   # MyBatis Mapper
│   │   │   ├── AssignmentMapper.java
│   │   │   ├── CourseMapper.java
│   │   │   └── UserMapper.java
│   │   ├── service/                  # 服务层
│   │   │   ├── AssignmentService.java
│   │   │   ├── CourseService.java
│   │   │   ├── LearningRecordService.java
│   │   │   └── impl/                 # 实现类子包
│   │   │       ├── AssignmentServiceImpl.java
│   │   │       ├── CourseServiceImpl.java
│   │   │       └── LearningRecordServiceImpl.java
│   │   └── utils/                    # 工具类
│   │       └── JwtUtil.java
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   └── mapper/                   # XML映射文件
│   └── pom.xml
│
├── frontend/                         # 前端应用
│   ├── src/
│   │   ├── api/                      # API接口（新增）
│   │   │   ├── agent.js
│   │   │   ├── assessment.js
│   │   │   ├── course.js
│   │   │   ├── learning.js
│   │   │   └── user.js
│   │   ├── components/               # 公共组件
│   │   │   ├── common/               # 通用组件
│   │   │   │   ├── DataTable.vue
│   │   │   │   ├── Pagination.vue
│   │   │   │   └── SearchForm.vue
│   │   │   └── layout/               # 布局组件
│   │   │       ├── AppHeader.vue
│   │   │       ├── AppSidebar.vue
│   │   │       └── PageLayout.vue
│   │   ├── composables/              # 组合式函数（新增）
│   │   │   ├── usePagination.js
│   │   │   └── useTable.js
│   │   ├── router/
│   │   │   └── index.js
│   │   ├── stores/                   # Pinia状态管理（新增）
│   │   │   ├── app.js
│   │   │   └── user.js
│   │   ├── utils/                    # 工具函数
│   │   │   ├── request.js            # axios封装
│   │   │   └── format.js             # 格式化工具
│   │   ├── views/                    # 页面视图
│   │   │   ├── dashboard/            # 仪表盘
│   │   │   │   └── Index.vue
│   │   │   ├── learning/             # 学习中心
│   │   │   │   ├── Index.vue
│   │   │   │   ├── Calendar.vue
│   │   │   │   └── Statistics.vue
│   │   │   ├── course/               # 课程管理
│   │   │   │   ├── Index.vue
│   │   │   │   └── Detail.vue
│   │   │   ├── user/                 # 用户管理
│   │   │   │   └── Index.vue
│   │   │   └── agent/                # 智能体
│   │   │       └── Index.vue
│   │   ├── App.vue
│   │   └── main.js
│   ├── index.html
│   ├── package.json
│   └── vite.config.js
│
├── docs/                             # 项目文档（统一）
│   ├── api/                          # API文档
│   │   ├── 学习中心API文档.md
│   │   └── API接口文档.md
│   ├── database/                     # 数据库脚本
│   │   ├── mysql_tables.sql
│   │   ├── assessment_tables.sql
│   │   └── recommendation_tables.sql
│   ├── deploy/                       # 部署文档
│   │   └── DEPLOYMENT_GUIDE.md
│   ├── manual/                       # 用户手册
│   ├── requirements/                 # 需求文档
│   └── README.md
│
├── scripts/                          # 脚本文件（新增）
│   ├── db/                           # 数据库脚本
│   │   ├── init.sql
│   │   └── seed_data.sql
│   └── deploy/                       # 部署脚本
│       └── start.sh
│
├── .gitignore
├── README.md
└── docker-compose.yml                # Docker编排（新增）
```

## 主要优化点

### 1. 后端优化
- **新增 exception 包**：统一异常处理
- **service 分包**：impl 子包存放实现类
- **config 精简**：删除冗余配置，保留核心配置

### 2. 前端优化
- **新增 api 目录**：集中管理 API 接口
- **新增 composables 目录**：复用组合式函数
- **新增 stores 目录**：Pinia 状态管理
- **views 按模块分组**：dashboard/、learning/、course/、user/、agent/
- **components 分层**：common/ 通用组件、layout/ 布局组件

### 3. 文档优化
- **统一 docs 目录**：合并 docs 和 documents
- **按类型分类**：api/、database/、deploy/、manual/、requirements/

### 4. 新增目录
- **scripts/**：数据库初始化和部署脚本
- **docker-compose.yml**：容器化部署配置

## 待清理文件

### 删除目录
- `documents/` → 已合并到 `docs/`
- `frontend_separated/` → 废弃的旧前端目录
- `src/` → 根目录下的残留文件

### 整理文件
- `docs/` 下的重复 SQL 文件（保留最新版本）
- `backend/target/` 下的构建文件（添加到 .gitignore）
