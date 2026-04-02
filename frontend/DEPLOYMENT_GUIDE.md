# 前端分离部署指南

## 📋 分离步骤

### 1. 创建独立前端项目目录
```bash
# 在项目根目录外创建新的前端项目
mkdir AI_frontend_project
cd AI_frontend_project
```

### 2. 复制前端文件
将 `frontend_separated` 目录下的所有文件复制到新的项目目录：

```bash
# 复制配置文件
cp ../AI_code_2026/frontend_separated/package.json .
cp ../AI_code_2026/frontend_separated/vite.config.js .
cp ../AI_code_2026/frontend_separated/index.html .

# 复制源代码
cp -r ../AI_code_2026/frontend_separated/src/ .

# 复制静态资源（如果有）
cp -r ../AI_code_2026/src/main/resources/static/* public/
```

### 3. 初始化 Node.js 项目
```bash
npm init -y
npm install
```

### 4. 安装依赖包
```bash
# 核心依赖
npm install vue@^3.4.0 axios@^1.6.0

# UI 组件库
npm install element-plus@^2.4.0 @element-plus/icons-vue@^2.3.0

# 开发依赖
npm install -D @vitejs/plugin-vue@^5.0.0 vite@^5.0.0
```

## 🚀 启动方式

### 开发模式
```bash
npm run dev
```
- 默认端口：3000
- 自动打开浏览器
- 热重载功能

### 生产构建
```bash
npm run build
```
- 输出目录：`dist/`
- 包含优化后的静态文件

### 预览生产版本
```bash
npm run preview
```

## ⚙️ 环境配置

### 开发环境配置
文件：`src/utils/config.js`
```javascript
development: {
  baseURL: 'http://localhost:8081',  // 后端服务地址
  timeout: 10000
}
```

### 生产环境配置
```javascript
production: {
  baseURL: 'https://your-domain.com',  // 生产环境后端地址
  timeout: 15000
}
```

## 🔧 后端配合

### CORS 配置（后端）
确保后端允许前端域名访问：
```java
@Configuration
public class CorsConfig {
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:3000");  // 前端地址
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);
        // ...
    }
}
```

### API 地址统一管理
所有 API 调用通过 `src/utils/apiEndpoints.js` 统一管理，便于维护。

## 📁 目录迁移对照表

| 原路径 | 新路径 | 说明 |
|--------|--------|------|
| `src/main/resources/static/*.html` | `public/` | 静态 HTML 文件 |
| `src/main/resources/static/js/` | `public/js/` | JavaScript 文件 |
| `src/main/resources/static/css/` | `public/css/` | CSS 样式文件 |
| `src/main/resources/static/images/` | `public/images/` | 图片资源 |

## 🎯 部署建议

### 1. 静态文件部署
- 将 `dist/` 目录内容部署到 Nginx/Apache
- 配置静态文件服务器

### 2. 反向代理配置
Nginx 配置示例：
```nginx
server {
    listen 80;
    server_name your-frontend-domain.com;
    
    location / {
        root /path/to/dist;
        try_files $uri $uri/ /index.html;
    }
    
    location /api {
        proxy_pass http://your-backend-domain.com;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

### 3. Docker 部署
创建 `Dockerfile`：
```dockerfile
FROM node:18-alpine as build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf
```

## 🔍 调试技巧

### 1. 网络请求调试
- 使用浏览器开发者工具 Network 面板
- 检查请求头、响应状态码
- 验证 API 地址是否正确

### 2. 组件调试
- 使用 Vue DevTools
- 查看组件状态和 props
- 监控事件触发

### 3. 性能优化
- 使用 Lighthouse 进行性能分析
- 优化图片资源大小
- 合理使用代码分割

## 🆘 常见问题

### 1. 跨域问题
**问题**：开发时出现 CORS 错误
**解决**：检查 vite.config.js 代理配置是否正确

### 2. API 请求失败
**问题**：接口返回 404 或连接失败
**解决**：确认后端服务已启动，检查 baseURL 配置

### 3. 静态资源加载失败
**问题**：CSS、JS 文件 404
**解决**：检查构建输出路径，确认资源引用路径正确

### 4. 路由刷新 404
**问题**：刷新页面出现 404
**解决**：配置服务器支持 SPA 路由回退到 index.html

## 📞 技术支持

如有问题请联系开发团队或查看相关文档。