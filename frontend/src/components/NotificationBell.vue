<template>
  <div class="notification-bell">
    <el-dropdown trigger="click" @visible-change="handleVisibleChange">
      <div class="bell-wrapper">
        <el-icon :size="20"><Bell /></el-icon>
        <el-badge v-if="unreadCount > 0" :value="unreadCount" class="badge" />
      </div>
      <template #dropdown>
        <div class="notification-dropdown">
          <div class="dropdown-header">
            <span>消堟伅通知</span>
            <el-button type="text" size="small" @click="goToNotificationCenter">
              查ョ湅六ㄩ儴
            </el-button>
          </div>
          <div class="notification-list" v-if="notifications.length > 0">
            <div
              v-for="item in notifications.slice(0, 5)"
              :key="item.notificationId"
              class="notification-item"
              :class="{ unread: item.isRead === 0 }"
              @click="handleClick(item)"
            >
              <div class="item-icon" :class="item.type">
                <el-icon v-if="item.type === 'system'"><Bell /></el-icon>
                <el-icon v-else-if="item.type === 'course'"><Reading /></el-icon>
                <el-icon v-else-if="item.type === 'assignment'"><Document /></el-icon>
                <el-icon v-else-if="item.type === 'exam'"><Timer /></el-icon>
                <el-icon v-else><InfoFilled /></el-icon>
              </div>
              <div class="item-content">
                <div class="item-title">{{ item.title }}</div>
                <div class="item-time">{{ formatTime(item.sendTime) }}</div>
              </div>
              <div v-if="item.isRead === 0" class="unread-dot"></div>
            </div>
          </div>
          <el-empty v-else description="暂无消堟伅" :image-size="60" />
          <div class="dropdown-footer">
            <el-button type="text" size="small" @click="markAllAsRead" :disabled="unreadCount === 0">
              六ㄩ儴标记�三哄凡请?
            </el-button>
          </div>
        </div>
      </template>
    </el-dropdown>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Bell, Reading, Document, Timer, InfoFilled } from '@element-plus/icons-vue'
import { getUserNotifications, getUnreadCount, markAsRead, markAllAsRead as markAllRead } from '@/notification'

const router = useRouter()
const currentUser = JSON.parse(localStorage.getItem('user') || '{}')
const userId = currentUser.userId || 1

const notifications = ref([])
const unreadCount = ref(0)
let refreshInterval = null

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
  return date.toLocaleDateString('zh-CN')
}

const fetchNotifications = async () => {
  try {
    const res = await getUserNotifications(userId)
    if (res.code === 200) {
      notifications.value = res.data
    }
  } catch (error) {
    console.error('获取消息列表失败:', error)
  }
}

const fetchUnreadCount = async () => {
  try {
    const res = await getUnreadCount(userId)
    if (res.code === 200) {
      unreadCount.value = res.data
    }
  } catch (error) {
    console.error('鑾峰彇期��数伴噺失败:', error)
  }
}

const handleVisibleChange = (visible) => {
  if (visible) {
    fetchNotifications()
  }
}

const handleClick = async (item) => {
  if (item.isRead === 0) {
    try {
      await markAsRead(item.notificationId)
      item.isRead = 1
      unreadCount.value--
    } catch (error) {
      console.error('标记�已茶�失败:', error)
    }
  }
  // 栏规嵁消堟伅类型跳宠浆
  switch (item.type) {
    case 'course':
      router.push('/learning')
      break
    case 'assignment':
      router.push('/assignments')
      break
    default:
      router.push('/notifications')
  }
}

const markAllAsRead = async () => {
  try {
    const res = await markAllRead(userId)
    if (res.code === 200) {
      ElMessage.success('全部标记为已读')
      notifications.value.forEach(item => item.isRead = 1)
      unreadCount.value = 0
    }
  } catch (error) {
    console.error('标记全部已读失败:', error)
    ElMessage.error('操作失败')
  }
}

const goToNotificationCenter = () => {
  router.push('/notifications')
}

// 定氭椂删锋柊期��数伴噺
const startRefresh = () => {
  refreshInterval = setInterval(() => {
    fetchUnreadCount()
  }, 30000) // 30秒刷新一次?
}

const stopRefresh = () => {
  if (refreshInterval) {
    clearInterval(refreshInterval)
    refreshInterval = null
  }
}

onMounted(() => {
  fetchUnreadCount()
  startRefresh()
})

onUnmounted(() => {
  stopRefresh()
})
</script>

<style scoped>
.notification-bell {
  margin-right: 15px;
}

.bell-wrapper {
  position: relative;
  cursor: pointer;
  padding: 8px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.bell-wrapper:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.badge {
  position: absolute;
  top: 0;
  right: 0;
}

.notification-dropdown {
  width: 350px;
  max-height: 400px;
}

.dropdown-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 15px;
  border-bottom: 1px solid #e4e7ed;
  font-weight: bold;
}

.notification-list {
  max-height: 300px;
  overflow-y: auto;
}

.notification-item {
  display: flex;
  align-items: center;
  padding: 12px 15px;
  cursor: pointer;
  transition: background-color 0.3s;
  border-bottom: 1px solid #f0f2f5;
}

.notification-item:hover {
  background-color: #f5f7fa;
}

.notification-item.unread {
  background-color: #ecf5ff;
}

.item-icon {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
  color: #fff;
  font-size: 16px;
}

.item-icon.system { background: #409eff; }
.item-icon.course { background: #67c23a; }
.item-icon.assignment { background: #e6a23c; }
.item-icon.exam { background: #f56c6c; }
.item-icon.default { background: #909399; }

.item-content {
  flex: 1;
  min-width: 0;
}

.item-title {
  font-size: 14px;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.item-time {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.unread-dot {
  width: 8px;
  height: 8px;
  background: #f56c6c;
  border-radius: 50%;
  margin-left: 8px;
}

.dropdown-footer {
  padding: 10px 15px;
  border-top: 1px solid #e4e7ed;
  text-align: center;
}
</style>
