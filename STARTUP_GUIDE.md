# 智慧教育平台 - 启动指南

## 快速启动

### 方式一：一键启动（推荐）

双击运行 `start-all.bat`，自动检测并启动前后端服务。

```
start-all.bat
```

### 方式二：独立启动

#### 启动后端服务

```bash
# 方法1: 使用脚本
start-backend.bat

# 方法2: 手动启动
cd backend
java -jar target/ai-edu-platform-1.0.0.jar
```

**后端服务信息：**
- 地址：http://localhost:8081
- 端口：8081
- 日志：控制台实时输出

#### 启动前端服务

```bash
# 方法1: 使用脚本
start-frontend.bat

# 方法2: 手动启动
cd frontend
npm run dev
```

**前端服务信息：**
- 地址：http://localhost:3000
- 端口：3000
- 热更新：支持

## 停止服务

### 方式一：一键停止

双击运行 `stop-all.bat`，停止所有 Java 和 Node 进程。

```
stop-all.bat
```

### 方式二：手动停止

- **后端**：在运行后端的终端按 `Ctrl+C`
- **前端**：在运行前端的终端按 `Ctrl+C`

## 服务状态检查

### 检查后端服务

```bash
# Windows
netstat -ano | findstr :8081

# 或使用浏览器访问
http://localhost:8081
```

### 检查前端服务

```bash
# Windows
netstat -ano | findstr :3000

# 或使用浏览器访问
http://localhost:3000
```

## 启动脚本说明

| 脚本 | 功能 | 使用场景 |
|------|------|----------|
| `start-all.bat` | 一键启动前后端 | 快速开始开发 |
| `start-backend.bat` | 仅启动后端 | 单独调试后端 |
| `start-frontend.bat` | 仅启动前端 | 单独调试前端 |
| `stop-all.bat` | 停止所有服务 | 结束开发 |

## 常见问题

### 1. 端口被占用

**现象**：启动时报错 `Port 8081 was already in use`

**解决**：
```bash
# 查找占用端口的进程
netstat -ano | findstr :8081

# 结束进程（将 <PID> 替换为实际的进程ID）
taskkill /PID <PID> /F

# 或直接使用停止脚本
stop-all.bat
```

### 2. 后端 JAR 文件不存在

**现象**：`Unable to access jarfile target/ai-edu-platform-1.0.0.jar`

**解决**：
```bash
cd backend
mvn clean package -DskipTests
```

### 3. 前端依赖未安装

**现象**：`Cannot find module 'vue'`

**解决**：
```bash
cd frontend
npm install
```

### 4. 数据库连接失败

**现象**：后端启动时报数据库连接错误

**解决**：
1. 检查 MySQL 服务是否启动
2. 检查 `backend/src/main/resources/application.yml` 配置
3. 确认数据库 `edu_platform` 已创建

## 开发工作流

### 日常开发

1. **启动服务**：双击 `start-all.bat`
2. **访问应用**：浏览器打开 http://localhost:3000
3. **修改代码**：前端代码修改后自动热更新，后端修改需重启
4. **停止服务**：双击 `stop-all.bat` 或在终端按 `Ctrl+C`

### 前后端独立开发

**仅开发前端**（后端已部署）：
```bash
start-frontend.bat
```

**仅开发后端**（使用 API 测试工具）：
```bash
start-backend.bat
```

## 技术栈版本

| 技术 | 版本 |
|------|------|
| Java | 1.8 |
| Spring Boot | 2.7.10 |
| MySQL | 8.0 |
| Node.js | 18+ |
| Vue | 3.3 |
| Vite | 5.4 |

## 服务依赖关系

```
前端 (localhost:3000)
    ↓ HTTP 请求
后端 (localhost:8081)
    ↓ SQL 查询
数据库 (MySQL:3306)
```

## 注意事项

1. **启动顺序**：建议先启动后端，再启动前端
2. **端口冲突**：确保 8081 和 3000 端口未被占用
3. **编码问题**：脚本使用 UTF-8 编码，Windows 控制台需支持中文
4. **防火墙**：确保防火墙允许上述端口通信

## 联系支持

如有问题，请查看：
- [项目README](README.md)
- [项目结构说明](PROJECT_STRUCTURE.md)
- [API文档](documents/学习中心API文档.md)
