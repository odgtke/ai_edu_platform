# 智慧教育平台

基于 Spring Boot + Vue 3 的前后端分离教育管理系统。

## 技术栈

### 后端
- **框架**: Spring Boot 2.7.10
- **数据库**: MySQL 8.0
- **ORM**: MyBatis Plus 3.5.3
- **安全**: Spring Security + JWT
- **缓存**: Redis
- **AI**: LangChain4j

### 前端
- **框架**: Vue 3 + Vite
- **UI**: Element Plus
- **状态管理**: Pinia
- **路由**: Vue Router 4
- **HTTP**: Axios

## 项目结构

```
AI_code_2026/
├── backend/           # 后端服务
│   ├── src/main/java/com/chinaunicom/edu/
│   │   ├── common/    # 公共类
│   │   ├── config/    # 配置类
│   │   ├── controller/# 控制器层
│   │   ├── dto/       # 数据传输对象
│   │   ├── entity/    # 实体类
│   │   ├── exception/ # 异常处理
│   │   ├── mapper/    # MyBatis Mapper
│   │   ├── service/   # 服务层
│   │   │   └── impl/  # 实现类
│   │   └── utils/     # 工具类
│   └── pom.xml
│
├── frontend/          # 前端应用
│   ├── src/
│   │   ├── api/       # API接口
│   │   ├── components/# 公共组件
│   │   ├── composables/# 组合式函数
│   │   ├── router/    # 路由配置
│   │   ├── stores/    # Pinia状态管理
│   │   ├── utils/     # 工具函数
│   │   └── views/     # 页面视图
│   └── package.json
│
└── docs/              # 项目文档
    ├── api/           # API文档
    ├── database/      # 数据库脚本
    └── manual/        # 用户手册
```

## 快速开始

### 后端启动

```bash
cd backend
mvn clean package -DskipTests
java -jar target/ai-edu-platform-1.0.0.jar
```

### 前端启动

```bash
cd frontend
npm install
npm run dev
```

## 功能模块

- **用户管理**: 学生、教师、管理员账号管理
- **课程管理**: 课程信息、课时管理
- **学习中心**: 学习记录、进度追踪、学习日历
- **智能体**: AI问答、智能推荐
- **能力评估**: 在线测评、成绩分析

## 文档

- [学习中心API文档](docs/api/学习中心API文档.md)
- [API接口文档](docs/api/API接口文档.md)
- [项目结构说明](PROJECT_STRUCTURE.md)

## 开发规范

### 后端
- 使用构造函数注入，避免 `@Autowired` 字段注入
- Service 层添加 `@Transactional` 注解
- Controller 统一返回 `Result<T>` 包装结果
- 异常统一在 `GlobalExceptionHandler` 处理

### 前端
- API 接口统一放在 `api/` 目录
- 使用组合式函数复用逻辑
- 工具函数统一放在 `utils/` 目录

## 许可证

MIT License
