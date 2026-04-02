# 智慧教育平台前端项目

这是一个基于 Vue 3 + Vite 构建的现代化前端项目，专为智慧教育平台设计。

## 📁 项目结构

```
frontend_separated/
├── public/                 # 静态资源目录
├── src/                   # 源代码目录
│   ├── assets/           # 静态资源文件
│   ├── components/       # Vue 组件
│   ├── views/           # 页面视图组件
│   ├── utils/           # 工具函数和配置
│   │   ├── api.js       # Axios 配置和拦截器
│   │   ├── apiEndpoints.js # API 接口定义
│   │   └── config.js    # 环境配置
│   ├── App.vue          # 根组件
│   └── main.js          # 入口文件
├── index.html           # 主页面模板
├── package.json         # 项目依赖配置
├── vite.config.js       # Vite 构建配置
└── README.md           # 项目说明文档
```

## 🚀 快速开始

### 1. 安装依赖
```bash
npm install
```

### 2. 启动开发服务器
```bash
npm run dev
```
默认访问地址：http://localhost:3000

### 3. 构建生产版本
```bash
npm run build
```

### 4. 预览生产构建
```bash
npm run preview
```

## 🛠 技术栈

- **Vue 3** - 渐进式 JavaScript 框架
- **Vite** - 新一代前端构建工具
- **Element Plus** - Vue 3 组件库
- **Axios** - HTTP 客户端
- **ES6+** - 现代 JavaScript 语法

## ⚙️ 配置说明

### 环境配置
在 `src/utils/config.js` 中配置不同环境的 API 地址：

```javascript
const API_CONFIG = {
  development: {
    baseURL: 'http://localhost:8081',  // 开发环境后端地址
    timeout: 10000
  },
  production: {
    baseURL: 'http://your-production-domain.com',  // 生产环境后端地址
    timeout: 15000
  }
}
```

### 代理配置
在 `vite.config.js` 中配置开发服务器代理：

```javascript
server: {
  port: 3000,
  proxy: {
    '/api': {
      target: 'http://localhost:8081',
      changeOrigin: true,
      secure: false
    }
  }
}
```

## 📡 API 接口

项目已封装了完整的 API 调用模块：

### 用户相关
- `userAPI.login()` - 用户登录
- `userAPI.register()` - 用户注册
- `userAPI.getUserInfo()` - 获取用户信息

### 课程相关
- `courseAPI.getCourseList()` - 获取课程列表
- `courseAPI.getCourseDetail()` - 获取课程详情

### 学习记录
- `learningAPI.getLearningRecords()` - 获取学习记录
- `learningAPI.createLearningRecord()` - 创建学习记录

### 能力评估
- `assessmentAPI.getAssessments()` - 获取评估列表
- `assessmentAPI.startAssessment()` - 开始评估

### 个性推荐
- `recommendationAPI.getPersonalized()` - 获取个性化推荐
- `recommendationAPI.getByType()` - 按类型获取推荐

## 🎨 组件开发规范

### 组件命名
- 使用 PascalCase 命名法
- 组件文件名与组件名保持一致

### 组件结构
```vue
<template>
  <!-- 模板内容 -->
</template>

<script>
export default {
  name: 'ComponentName',
  props: {},
  data() {
    return {}
  },
  methods: {}
}
</script>

<style scoped>
/* 样式代码 */
</style>
```

## 📱 响应式设计

项目采用 Element Plus 组件库，天然支持响应式布局。可根据需要添加自定义媒体查询：

```css
@media (max-width: 768px) {
  /* 移动端样式 */
}

@media (min-width: 769px) and (max-width: 1024px) {
  /* 平板样式 */
}

@media (min-width: 1025px) {
  /* 桌面端样式 */
}
```

## 🔧 开发工具推荐

- **VS Code** - 推荐安装 Volar 插件
- **Chrome DevTools** - 调试工具
- **Vue DevTools** - Vue 开发者工具

## 🚨 注意事项

1. **跨域问题**：开发环境下通过 Vite 代理解决，生产环境需要后端配置 CORS
2. **Token 管理**：JWT Token 存储在 localStorage 中
3. **错误处理**：全局错误拦截器会自动处理常见 HTTP 错误
4. **性能优化**：生产构建会自动进行代码分割和压缩

## 📚 学习资源

- [Vue 3 官方文档](https://v3.vuejs.org/)
- [Vite 官方文档](https://vitejs.dev/)
- [Element Plus 文档](https://element-plus.org/)

## 🤝 贡献指南

1. Fork 项目
2. 创建功能分支
3. 提交更改
4. 发起 Pull Request

## 📄 许可证

MIT License