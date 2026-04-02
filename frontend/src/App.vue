<template>
  <div id="app" class="app-container">
    <!-- 左侧侧边栏 -->
    <aside class="sidebar" :class="{ collapsed: isCollapsed }">
      <div class="logo">
        <div class="logo-icon">
          <el-icon><School /></el-icon>
        </div>
        <div class="logo-text" v-show="!isCollapsed">
          <h2>智慧教育</h2>
          <p>AI-Edu Platform</p>
        </div>
      </div>
      
      <nav class="sidebar-nav">
        <!-- 主要功能 -->
        <div class="nav-section" v-show="!isCollapsed">
          <span class="nav-section-title">主要功能</span>
        </div>
        <router-link 
          v-for="item in mainMenuItems" 
          :key="item.path"
          :to="item.path" 
          class="nav-item"
          :class="{ active: $route.path === item.path }"
          :title="isCollapsed ? item.label : ''"
        >
          <el-icon class="nav-icon"><component :is="item.icon" /></el-icon>
          <span class="nav-label" v-show="!isCollapsed">{{ item.label }}</span>
          <div v-show="isCollapsed" class="nav-tooltip">{{ item.label }}</div>
        </router-link>
        
        <!-- 智能服务 -->
        <div class="nav-section" v-show="!isCollapsed">
          <span class="nav-section-title">智能服务</span>
        </div>
        <router-link 
          v-for="item in aiMenuItems" 
          :key="item.path"
          :to="item.path" 
          class="nav-item"
          :class="{ active: $route.path === item.path }"
          :title="isCollapsed ? item.label : ''"
        >
          <el-icon class="nav-icon"><component :is="item.icon" /></el-icon>
          <span class="nav-label" v-show="!isCollapsed">{{ item.label }}</span>
          <div v-show="isCollapsed" class="nav-tooltip">{{ item.label }}</div>
        </router-link>
        
        <!-- 系统设置 -->
        <div class="nav-section" v-show="!isCollapsed">
          <span class="nav-section-title">系统</span>
        </div>
        <router-link 
          v-for="item in systemMenuItems" 
          :key="item.path"
          :to="item.path" 
          class="nav-item"
          :class="{ active: $route.path === item.path }"
          :title="isCollapsed ? item.label : ''"
        >
          <el-icon class="nav-icon"><component :is="item.icon" /></el-icon>
          <span class="nav-label" v-show="!isCollapsed">{{ item.label }}</span>
          <div v-show="isCollapsed" class="nav-tooltip">{{ item.label }}</div>
        </router-link>
      </nav>
      
      <!-- 折叠按钮 -->
      <div class="sidebar-footer">
        <button class="collapse-btn" @click="toggleSidebar">
          <el-icon><Fold v-if="!isCollapsed" /><Expand v-else /></el-icon>
        </button>
      </div>
    </aside>
    
    <!-- 主内容区 -->
    <main class="main-wrapper">
      <!-- 顶部导航栏 -->
      <header class="top-header">
        <div class="breadcrumb">
          <el-icon class="breadcrumb-icon"><HomeFilled /></el-icon>
          <span class="breadcrumb-current">{{ currentPageTitle }}</span>
        </div>
        
        <div class="header-actions">
          <!-- 搜索框 -->
          <div class="search-box">
            <el-icon class="search-icon"><Search /></el-icon>
            <input 
              type="text" 
              placeholder="搜索功能、课程、用户..." 
              class="search-input"
            />
          </div>
          
          <!-- 用户菜单 -->
          <div class="user-menu">
            <el-avatar :size="36" class="user-avatar">
              <el-icon><UserFilled /></el-icon>
            </el-avatar>
            <div class="user-info" v-if="!isMobile">
              <span class="user-name">管理员</span>
              <span class="user-role">系统管理员</span>
            </div>
            <el-icon class="dropdown-icon"><ArrowDown /></el-icon>
          </div>
        </div>
      </header>
      
      <!-- 页面内容 -->
      <div class="page-content">
        <router-view v-slot="{ Component }">
          <transition name="page" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>
    </main>
  </div>
</template>

<script>
import { 
  DataLine, User, Reading, School, Folder,
  ChatDotRound, Setting, QuestionFilled, 
  Search, Fold, Expand, HomeFilled, UserFilled, ArrowDown
} from '@element-plus/icons-vue'

export default {
  name: 'App',
  components: {
    DataLine, User, Reading, School, Folder,
    ChatDotRound, Setting, QuestionFilled, 
    Search, Fold, Expand, HomeFilled, UserFilled, ArrowDown
  },
  data() {
    return {
      isCollapsed: false,
      isMobile: false,
      mainMenuItems: [
        { path: '/', label: '数据概览', icon: 'DataLine' },
        { path: '/users', label: '用户管理', icon: 'User' },
        { path: '/courses', label: '课程管理', icon: 'Reading' },
        { path: '/learning', label: '学习中心', icon: 'Notebook' },
        { path: '/recommendation', label: '个性推荐', icon: 'Star' },
        { path: '/clazz', label: '班级管理', icon: 'School' },
        { path: '/assessment', label: '能力评估', icon: 'TrendCharts' },
        { path: '/resources', label: '资源管理', icon: 'Folder' }
      ],
      aiMenuItems: [
        { path: '/agent', label: '智能助手', icon: 'ChatDotRound' }
      ],
      systemMenuItems: [
        { path: '/settings', label: '系统设置', icon: 'Setting' },
        { path: '/help', label: '帮助中心', icon: 'QuestionFilled' }
      ]
    }
  },
  computed: {
    currentPageTitle() {
      const allItems = [...this.mainMenuItems, ...this.aiMenuItems, ...this.systemMenuItems]
      const current = allItems.find(item => item.path === this.$route.path)
      return current ? current.label : '数据概览'
    }
  },
  mounted() {
    this.checkMobile()
    window.addEventListener('resize', this.checkMobile)
  },
  beforeUnmount() {
    window.removeEventListener('resize', this.checkMobile)
  },
  methods: {
    toggleSidebar() {
      this.isCollapsed = !this.isCollapsed
    },
    checkMobile() {
      this.isMobile = window.innerWidth < 768
      if (this.isMobile) {
        this.isCollapsed = true
      }
    }
  }
}
</script>

<style>
@import './styles/design-system.css';

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

#app {
  font-family: var(--font-sans);
  height: 100vh;
}

.app-container {
  display: flex;
  height: 100vh;
  background-color: var(--bg-secondary);
}

/* 侧边栏样式 */
.sidebar {
  width: var(--sidebar-width);
  background: var(--sidebar-bg);
  display: flex;
  flex-direction: column;
  transition: width var(--transition-normal);
  position: relative;
  z-index: 100;
}

.sidebar.collapsed {
  width: var(--sidebar-collapsed-width);
}

/* Logo区域 */
.logo {
  height: var(--header-height);
  display: flex;
  align-items: center;
  padding: 0 var(--space-4);
  border-bottom: 1px solid var(--sidebar-border);
  gap: var(--space-3);
}

.logo-icon {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, var(--primary-500) 0%, var(--primary-600) 100%);
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.logo-icon .el-icon {
  font-size: 24px;
  color: white;
}

.logo-text h2 {
  color: white;
  font-size: var(--text-lg);
  font-weight: var(--font-bold);
  margin: 0;
  line-height: 1.2;
}

.logo-text p {
  color: var(--sidebar-text);
  font-size: var(--text-xs);
  margin: 0;
}

/* 导航菜单 */
.sidebar-nav {
  flex: 1;
  overflow-y: auto;
  padding: var(--space-4) 0;
}

.nav-section {
  padding: var(--space-4) var(--space-4) var(--space-2);
}

.nav-section-title {
  color: var(--sidebar-text);
  font-size: var(--text-xs);
  font-weight: var(--font-semibold);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.nav-item {
  display: flex;
  align-items: center;
  padding: var(--space-3) var(--space-4);
  margin: 2px var(--space-3);
  border-radius: var(--radius-md);
  color: var(--sidebar-text);
  text-decoration: none;
  transition: all var(--transition-fast);
  position: relative;
}

.nav-item:hover {
  background-color: var(--sidebar-bg-hover);
  color: white;
}

.nav-item.active {
  background: linear-gradient(135deg, rgba(14, 165, 233, 0.2) 0%, rgba(2, 132, 199, 0.1) 100%);
  color: var(--primary-400);
}

.nav-item.active::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 20px;
  background: var(--primary-500);
  border-radius: 0 2px 2px 0;
}

.nav-icon {
  font-size: 20px;
  margin-right: var(--space-3);
  flex-shrink: 0;
}

.nav-label {
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  white-space: nowrap;
}

/* 折叠时的工具提示 */
.nav-tooltip {
  position: absolute;
  left: 100%;
  top: 50%;
  transform: translateY(-50%);
  background: var(--gray-800);
  color: white;
  padding: var(--space-2) var(--space-3);
  border-radius: var(--radius-md);
  font-size: var(--text-sm);
  white-space: nowrap;
  opacity: 0;
  visibility: hidden;
  transition: all var(--transition-fast);
  margin-left: var(--space-2);
  z-index: 200;
}

.nav-item:hover .nav-tooltip {
  opacity: 1;
  visibility: visible;
}

/* 侧边栏底部 */
.sidebar-footer {
  padding: var(--space-4);
  border-top: 1px solid var(--sidebar-border);
  display: flex;
  justify-content: center;
}

.collapse-btn {
  width: 36px;
  height: 36px;
  border: none;
  background: var(--sidebar-bg-hover);
  color: var(--sidebar-text);
  border-radius: var(--radius-md);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all var(--transition-fast);
}

.collapse-btn:hover {
  background: var(--primary-500);
  color: white;
}

/* 主内容区 */
.main-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 顶部导航栏 */
.top-header {
  height: var(--header-height);
  background: white;
  border-bottom: 1px solid var(--border-light);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 var(--space-6);
  flex-shrink: 0;
}

.breadcrumb {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  color: var(--text-secondary);
}

.breadcrumb-icon {
  font-size: 18px;
  color: var(--primary-500);
}

.breadcrumb-current {
  font-size: var(--text-lg);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
}

.header-actions {
  display: flex;
  align-items: center;
  gap: var(--space-5);
}

/* 搜索框 */
.search-box {
  position: relative;
  width: 280px;
}

.search-icon {
  position: absolute;
  left: var(--space-3);
  top: 50%;
  transform: translateY(-50%);
  color: var(--text-tertiary);
  font-size: 16px;
}

.search-input {
  width: 100%;
  height: 40px;
  padding: 0 var(--space-4) 0 var(--space-10);
  border: 1px solid var(--border-light);
  border-radius: var(--radius-full);
  background: var(--bg-secondary);
  font-size: var(--text-sm);
  color: var(--text-primary);
  transition: all var(--transition-fast);
}

.search-input:focus {
  outline: none;
  border-color: var(--primary-400);
  background: white;
  box-shadow: 0 0 0 3px rgba(14, 165, 233, 0.1);
}

.search-input::placeholder {
  color: var(--text-tertiary);
}

/* 用户菜单 */
.user-menu {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  cursor: pointer;
  padding: var(--space-2);
  border-radius: var(--radius-lg);
  transition: background var(--transition-fast);
}

.user-menu:hover {
  background: var(--bg-secondary);
}

.user-avatar {
  background: linear-gradient(135deg, var(--primary-500) 0%, var(--primary-600) 100%);
  color: white;
}

.user-info {
  display: flex;
  flex-direction: column;
  line-height: 1.3;
}

.user-name {
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
}

.user-role {
  font-size: var(--text-xs);
  color: var(--text-tertiary);
}

.dropdown-icon {
  color: var(--text-tertiary);
  font-size: 12px;
}

/* 页面内容区 */
.page-content {
  flex: 1;
  overflow-y: auto;
  padding: var(--space-6);
  background: var(--bg-secondary);
}

/* 页面过渡动画 */
.page-enter-active,
.page-leave-active {
  transition: all 0.3s ease;
}

.page-enter-from {
  opacity: 0;
  transform: translateX(10px);
}

.page-leave-to {
  opacity: 0;
  transform: translateX(-10px);
}

/* 响应式适配 */
@media (max-width: 1024px) {
  .sidebar {
    position: fixed;
    left: 0;
    top: 0;
    bottom: 0;
  }
  
  .sidebar.collapsed {
    transform: translateX(-100%);
  }
  
  .main-wrapper {
    margin-left: var(--sidebar-collapsed-width);
  }
}

@media (max-width: 768px) {
  .search-box {
    display: none;
  }
  
  .user-info {
    display: none;
  }
  
  .page-content {
    padding: var(--space-4);
  }
}
</style>
