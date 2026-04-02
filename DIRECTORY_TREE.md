# 项目目录结构

## 根目录

```
AI_code_2026/
├── .gitignore              # Git 忽略配置
├── PROJECT_STRUCTURE.md    # 项目结构优化方案
├── DIRECTORY_TREE.md       # 本文件 - 目录结构说明
├── README.md               # 项目说明文档
├── backend/                # 后端服务
├── documents/              # 项目文档
├── frontend/               # 前端应用
├── src/                    # ⚠️ 待删除 - 残留文件
├── .idea/                  # IDE 配置
├── .lingma/                # Lingma 配置
├── .qoder/                 # Qoder 配置
└── .vscode/                # VSCode 配置
```

## 后端目录 (backend/)

```
backend/
├── pom.xml                 # Maven 配置
├── api-test.bat            # API 测试脚本
├── scripts/                # 脚本目录
│   └── ...
├── src/
│   └── main/
│       ├── java/com/chinaunicom/edu/
│       │   ├── EduPlatformApplication.java    # 启动类
│       │   ├── agent/          # 智能体模块
│       │   │   ├── AgentService.java
│       │   │   ├── core/       # 核心类
│       │   │   ├── impl/       # 实现类
│       │   │   └── ...
│       │   ├── common/         # 公共类
│       │   │   └── Result.java # 统一响应
│       │   ├── config/         # 配置类 (10个)
│       │   │   ├── CorsConfig.java
│       │   │   ├── MybatisPlusConfig.java
│       │   │   ├── SecurityConfig.java
│       │   │   └── ...
│       │   ├── controller/     # 控制器层 (8个)
│       │   │   ├── AgentController.java
│       │   │   ├── AssessmentController.java
│       │   │   ├── AuthController.java
│       │   │   ├── CourseController.java
│       │   │   ├── LearningController.java
│       │   │   ├── RecommendationController.java
│       │   │   └── UserController.java
│       │   ├── dto/            # 数据传输对象 (3个)
│       │   │   ├── LearningRecordDTO.java
│       │   │   └── ...
│       │   ├── entity/         # 实体类 (14个)
│       │   │   ├── Assignment.java
│       │   │   ├── Course.java
│       │   │   ├── LearningRecord.java
│       │   │   ├── User.java
│       │   │   └── ...
│       │   ├── exception/      # ✅ 新增 - 异常处理 (2个)
│       │   │   ├── BusinessException.java
│       │   │   └── GlobalExceptionHandler.java
│       │   ├── mapper/         # MyBatis Mapper (14个)
│       │   │   ├── CourseMapper.java
│       │   │   └── ...
│       │   ├── nlp/            # NLP 模块
│       │   │   └── ...
│       │   ├── service/        # 服务层
│       │   │   ├── AssessmentService.java      # 接口
│       │   │   ├── CourseService.java          # 接口
│       │   │   ├── LearningRecordService.java  # 接口
│       │   │   ├── UserService.java            # 接口
│       │   │   └── impl/       # ✅ 实现类子包 (7个)
│       │   │       ├── AssessmentServiceImpl.java
│       │   │       ├── CourseServiceImpl.java
│       │   │       ├── LearningRecordServiceImpl.java
│       │   │       ├── RecommendationServiceImpl.java
│       │   │       └── ...
│       │   ├── tools/          # 工具类
│       │   ├── utils/          # 工具类 (3个)
│       │   │   └── JwtUtil.java
│       │   ├── vo/             # 视图对象 (2个)
│       │   └── workflow/       # 工作流
│       └── resources/
│           ├── application.yml # 配置文件
│           └── ...
└── target/                 # 编译输出 (添加到 .gitignore)
```

## 前端目录 (frontend/)

```
frontend/
├── index.html              # 入口 HTML
├── package.json            # NPM 配置
├── package-lock.json       # 锁定依赖版本
├── vite.config.js          # Vite 配置
├── README.md               # 前端说明
├── DEPLOYMENT_GUIDE.md     # 部署指南
├── public/                 # 静态资源
└── src/
    ├── App.vue             # 根组件
    ├── main.js             # 入口文件
    ├── api/                # ✅ 新增 - API 接口 (4个)
    │   ├── index.js        # 统一导出
    │   ├── course.js       # 课程 API
    │   ├── learning.js     # 学习 API
    │   └── user.js         # 用户 API
    ├── assets/             # 资源文件
    ├── components/         # 公共组件
    ├── composables/        # ✅ 新增 - 组合式函数 (2个)
    │   ├── usePagination.js
    │   └── useTable.js
    ├── router/             # 路由配置
    │   └── index.js
    ├── stores/             # 状态管理 (待添加)
    ├── utils/              # 工具函数 (5个)
    │   ├── request.js      # ✅ Axios 封装
    │   ├── format.js       # ✅ 格式化工具
    │   └── ...
    └── views/              # 页面视图 (9个)
        ├── Home.vue        # 首页/仪表盘
        ├── Users.vue       # 用户管理
        ├── Courses.vue     # 课程管理
        ├── Learning.vue    # 学习中心
        ├── Agent.vue       # 智能体
        ├── Assessment.vue  # 能力评估
        ├── Recommendation.vue  # 个性推荐
        ├── Learning.vue    # 学习中心
        └── ...
```

## 文档目录 (documents/)

```
documents/
├── API接口文档.md              # 通用 API 文档
├── 学习中心API文档.md          # 学习中心 API
├── LangChain4j工作流实现说明.md
├── 智慧教育平台项目总结报告.md
├── 能力评估功能模块设计说明.md
├── 能力评估模块实施总结.md
├── 修复说明.md
├── recommendation_system_summary.md
├── mysql_tables.sql            # 数据库表结构
├── assessment_tables.sql       # 评估表
├── recommendation_tables.sql   # 推荐表
├── insert_questions.sql        # 初始化数据
├── architecture/               # 架构文档
├── manual/                     # 用户手册 (3个)
├── product/                    # 产品文档
├── requirements/               # 需求文档
└── security/                   # 安全文档
```

## 目录结构评估

### ✅ 合理的部分

1. **后端结构清晰**
   - 按功能分层：controller/service/mapper/entity
   - exception 包独立，职责明确
   - service/impl 分离接口和实现

2. **前端结构优化**
   - api/ 目录集中管理接口
   - composables/ 复用逻辑
   - utils/ 工具函数分类

3. **文档分类明确**
   - 按类型分目录：architecture/manual/requirements/security

### ⚠️ 建议优化

1. **根目录 src/** 
   - 状态：残留文件，建议删除
   - 操作：`rm -rf src/`

2. **前端 views/**
   - 当前：9个文件平铺
   - 建议：按模块分组
     ```
     views/
     ├── dashboard/
     │   └── Index.vue
     ├── user/
     │   └── Index.vue
     ├── course/
     │   ├── Index.vue
     │   └── Detail.vue
     ├── learning/
     │   └── Index.vue
     └── agent/
         └── Index.vue
     ```

3. **前端 components/**
   - 当前：空目录
   - 建议：添加通用组件
     ```
     components/
     ├── common/           # 通用组件
     │   ├── DataTable.vue
     │   ├── Pagination.vue
     │   └── SearchForm.vue
     └── layout/           # 布局组件
         ├── AppHeader.vue
         ├── AppSidebar.vue
         └── PageLayout.vue
     ```

4. **后端 agent/ 模块**
   - 当前：与主业务混合
   - 建议：作为独立模块或微服务

### 📊 统计

| 目录 | 文件数 | 状态 |
|------|--------|------|
| backend/controller | 8 | ✅ 合理 |
| backend/service | 8+7 | ✅ 合理 |
| backend/entity | 14 | ✅ 合理 |
| backend/mapper | 14 | ✅ 合理 |
| frontend/api | 4 | ✅ 新增 |
| frontend/composables | 2 | ✅ 新增 |
| frontend/views | 9 | ⚠️ 建议分组 |
| documents | 20 | ✅ 合理 |

## 结论

当前项目目录结构**基本合理**，主要优化已完成：
- ✅ 后端 exception 包已添加
- ✅ 前端 api/ composables/ 已添加
- ✅ 文档统一在 documents/

**剩余建议**（可选）：
1. 删除根目录 `src/` 残留
2. 前端 views/ 按模块分组
3. 添加前端通用组件
