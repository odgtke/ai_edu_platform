<template>
  <div class="notification-container">
    <div class="page-header">
      <h2>娑堝牊浼呴氱煡涓夛拷绺</h2>
      <el-button type="primary" @click="showSendDialog = true">
        <el-icon><Plus /></el-icon>鍙戦佲偓浣圭Х閹?
      </el-button>
    </div>

    <!-- 娑堝牊浼呯画鐔伙拷 -->
    <div class="stats-bar">
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon" style="background: #f56c6c;">
              <el-icon><Bell /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ unreadCount }}</div>
              <div class="stat-label">鏈燂拷锟芥秷鍫熶紖</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon" style="background: #67c23a;">
              <el-icon><Message /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ totalCount }}</div>
              <div class="stat-label">鍏銊╁劥娑堝牊浼</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 娑堝牊浼呭垹妤勩 -->
    <div class="notification-list">
      <div class="list-header">
        <el-radio-group v-model="filterType" @change="handleFilterChange">
          <el-radio-button label="all">鍏銊╁劥</el-radio-button>
          <el-radio-button label="unread">鏈燂拷锟</el-radio-button>
          <el-radio-button label="system">绯荤粺</el-radio-button>
          <el-radio-button label="course">璇剧▼鈻</el-radio-button>
          <el-radio-button label="assignment">浣滀笟</el-radio-button>
        </el-radio-group>
        <el-button type="text" @click="markAllAsRead" :disabled="unreadCount === 0">
          鍏銊╁劥鏍囪帮拷涓夊搫鍑¤?
        </el-button>
      </div>

      <el-empty v-if="filteredNotifications.length === 0" description="鏆傛棤娑堝牊浼" />
      
      <div v-else class="notification-items">
        <div
          v-for="item in filteredNotifications"
          :key="item.notificationId"
          class="notification-item"
          :class="{ unread: item.isRead === 0 }"
          @click="handleNotificationClick(item)"
        >
          <div class="notification-icon" :class="item.type">
            <el-icon v-if="item.type === 'system'"><Bell /></el-icon>
            <el-icon v-else-if="item.type === 'course'"><Reading /></el-icon>
            <el-icon v-else-if="item.type === 'assignment'"><Document /></el-icon>
            <el-icon v-else-if="item.type === 'exam'"><Timer /></el-icon>
            <el-icon v-else><InfoFilled /></el-icon>
          </div>
          <div class="notification-content">
            <div class="notification-header">
              <span class="title">{{ item.title }}</span>
              <el-tag :type="getPriorityType(item.priority)" size="small">
                {{ item.priorityLabel }}
              </el-tag>
              <span class="time">{{ formatTime(item.sendTime) }}</span>
            </div>
            <div class="notification-body">{{ item.content }}</div>
            <div class="notification-footer">
              <span class="sender">鏉熴儴鍤: {{ item.senderName }}</span>
              <span class="type-tag">{{ item.typeLabel }}</span>
            </div>
          </div>
          <div class="notification-status">
            <el-tag v-if="item.isRead === 0" type="danger" size="small">鏈燂拷锟</el-tag>
            <el-tag v-else type="info" size="small">宸茶尪锟</el-tag>
          </div>
        </div>
      </div>
    </div>

    <!-- 鍙戦佹秷鎭瀵硅瘽妗 -->
    <el-dialog v-model="showSendDialog" title="鍙戦佹秷鎭" width="500px">
      <el-form :model="sendForm" :rules="sendRules" ref="sendFormRef" label-width="100px">
        <el-form-item label="鍙戦佸硅薄" prop="receiverType">
          <el-radio-group v-model="sendForm.receiverType">
            <el-radio label="user">涓浜</el-radio>
            <el-radio label="class">鐝绾</el-radio>
            <el-radio label="all">鍏ㄩ儴</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="sendForm.receiverType === 'user'" label="閹恒儲鏁圭敤鎴" prop="receiverId">
          <el-input-number v-model="sendForm.receiverId" :min="1" placeholder="鐢ㄦ埛ID" />
        </el-form-item>
        <el-form-item v-if="sendForm.receiverType === 'class'" label="閫夊嬪ㄩ悵锟介獓" prop="classId">
          <el-select v-model="sendForm.classId" placeholder="閫夊嬪ㄩ悵锟介獓" style="width: 100%">
            <el-option
              v-for="cls in classList"
              :key="cls.classId"
              :label="cls.className"
              :value="cls.classId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="娑堝牊浼呯被鍨" prop="type">
          <el-select v-model="sendForm.type" placeholder="閫夊嬪ㄧ被鍨" style="width: 100%">
            <el-option label="绯荤粺娑堝牊浼" value="system" />
            <el-option label="璇剧▼鈻奸氱煡" value="course" />
            <el-option label="浣滀笟鎻愭劙鍟" value="assignment" />
            <el-option label="閼板啳鐦閫氱煡" value="exam" />
          </el-select>
        </el-form-item>
        <el-form-item label="浼犳ê鍘涚痪? prop="priority">
          <el-radio-group v-model="sendForm.priority">
            <el-radio :label="1">閺咃拷鈧?/el-radio>
            <el-radio :label="2">闁插秷锟</el-radio>
            <el-radio :label="3">绱⒀勨偓?/el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="鏍囬橈拷" prop="title">
          <el-input v-model="sendForm.title" placeholder="璇风柉绶鍏銉︾Х閹锟界垼妫? />
        </el-form-item>
        <el-form-item label="鍐呭癸拷" prop="content">
          <el-input v-model="sendForm.content" type="textarea" :rows="4" placeholder="璇风柉绶鍏銉︾Х閹锟藉敶瀹? />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showSendDialog = false">鍙栨秷</el-button>
        <el-button type="primary" @click="handleSend">鍙戦佲偓?/el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Bell, Message, Reading, Document, Timer, InfoFilled } from '@element-plus/icons-vue'
import { getUserNotifications, getUnreadCount, markAsRead, markAllAsRead as markAllRead, sendToUser, sendToClass, sendSystemNotification } from '@/notification'
import { getClassPage } from '@/clazz'

const currentUser = JSON.parse(localStorage.getItem('user') || '{}')
const userId = currentUser.userId || 1

const notifications = ref([])
const unreadCount = ref(0)
const filterType = ref('all')
const showSendDialog = ref(false)
const classList = ref([])

const sendFormRef = ref(null)
const sendForm = reactive({
  receiverType: 'user',
  receiverId: null,
  classId: null,
  type: 'system',
  priority: 1,
  title: '',
  content: ''
})

const sendRules = {
  title: [{ required: true, message: '璇风柉绶鍏銉︾垼妫?, trigger: 'blur' }],
  content: [{ required: true, message: '璇风柉绶鍏銉ュ敶瀹?, trigger: 'blur' }],
  receiverId: [{ required: true, message: '璇风柉绶鍏銉﹀复閺鍓佹暏鎴绋〥', trigger: 'blur' }],
  classId: [{ required: true, message: '璇风兘鈧澶嬪ㄩ悵锟介獓', trigger: 'change' }]
}

const totalCount = computed(() => notifications.value.length)

const filteredNotifications = computed(() => {
  let list = notifications.value
  if (filterType.value === 'unread') {
    list = list.filter(item => item.isRead === 0)
  } else if (filterType.value !== 'all') {
    list = list.filter(item => item.type === filterType.value)
  }
  return list
})

const getPriorityType = (priority) => {
  switch (priority) {
    case 3: return 'danger'
    case 2: return 'warning'
    default: return 'info'
  }
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleString('zh-CN')
}

// 閼惧嘲褰囨秷鍫熶紖鍒犳勩
const fetchNotifications = async () => {
  try {
    const res = await getUserNotifications(userId)
    if (res.code === 200) {
      notifications.value = res.data
    }
  } catch (error) {
    console.error('閼惧嘲褰囨秷鍫熶紖鍒犳勩冨け璐:', error)
  }
}

// 閼惧嘲褰囨湡锟斤拷鏁颁即鍣
const fetchUnreadCount = async () => {
  try {
    const res = await getUnreadCount(userId)
    if (res.code === 200) {
      unreadCount.value = res.data
    }
  } catch (error) {
    console.error('閼惧嘲褰囨湡锟斤拷鏁颁即鍣哄け璐:', error)
  }
}

// 閼惧嘲褰囬悵锟介獓鍒犳勩
const fetchClasses = async () => {
  try {
    const res = await getClassPage({ page: 1, size: 100 })
    if (res.code === 200) {
      classList.value = res.data.records
    }
  } catch (error) {
    console.error('閼惧嘲褰囬悵锟介獓鍒犳勩冨け璐:', error)
  }
}

// 娑堝牊浼呴悙鐟板毊
const handleNotificationClick = async (item) => {
  if (item.isRead === 0) {
    try {
      const res = await markAsRead(item.notificationId)
      if (res.code === 200) {
        item.isRead = 1
        unreadCount.value--
      }
    } catch (error) {
      console.error('鏍囪帮拷宸茶尪锟藉け璐:', error)
    }
  }
}

// 鍏銊╁劥鏍囪帮拷宸茶尪锟
const markAllAsRead = async () => {
  try {
    const res = await markAllRead(userId)
    if (res.code === 200) {
      ElMessage.success('鍏銊╁劥鏍囪帮拷涓夊搫鍑¤?)
      notifications.value.forEach(item => item.isRead = 1)
      unreadCount.value = 0
    }
  } catch (error) {
    console.error('鏍囪帮拷鍏銊╁劥宸茶尪锟藉け璐:', error)
    ElMessage.error('閹垮秳缍斿け璐')
  }
}

// 鍙戦佲偓浣圭Х閹?
const handleSend = async () => {
  try {
    await sendFormRef.value.validate()
    
    let res
    if (sendForm.receiverType === 'user') {
      res = await sendToUser({
        senderId: userId,
        senderName: currentUser.username || '绠＄悊鍛?,
        receiverId: sendForm.receiverId,
        title: sendForm.title,
        content: sendForm.content,
        type: sendForm.type,
        priority: sendForm.priority
      })
    } else if (sendForm.receiverType === 'class') {
      res = await sendToClass({
        senderId: userId,
        senderName: currentUser.username || '绠＄悊鍛?,
        classId: sendForm.classId,
        title: sendForm.title,
        content: sendForm.content,
        type: sendForm.type,
        priority: sendForm.priority
      })
    } else {
      res = await sendSystemNotification({
        title: sendForm.title,
        content: sendForm.content
      })
    }
    
    if (res.code === 200) {
      ElMessage.success('鍙戦佲偓浣瑰灇鍔?)
      showSendDialog.value = false
      // 閲嶇疆琛ㄥ崟
      Object.assign(sendForm, {
        receiverType: 'user',
        receiverId: null,
        classId: null,
        type: 'system',
        priority: 1,
        title: '',
        content: ''
      })
    }
  } catch (error) {
    console.error('鍙戦佲偓浣搞亼璐?', error)
    ElMessage.error('鍙戦佲偓浣搞亼璐?)
  }
}

const handleFilterChange = () => {
  // 绛涢夆偓澶婂綁閸栨牗妞備笁宥夋付鐟曚線鍣告柊鎷屽箯鍙栨牗鏆熼幑?
}

onMounted(() => {
  fetchNotifications()
  fetchUnreadCount()
  fetchClasses()
})
</script>

<style scoped>
.notification-container {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  color: #303133;
}

.stats-bar {
  margin-bottom: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 24px;
  margin-right: 15px;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 5px;
}

.notification-list {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.notification-items {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.notification-item {
  display: flex;
  padding: 15px;
  border-radius: 8px;
  background: #f5f7fa;
  cursor: pointer;
  transition: all 0.3s;
}

.notification-item:hover {
  background: #e4e7ed;
}

.notification-item.unread {
  background: #ecf5ff;
  border-left: 4px solid #409eff;
}

.notification-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  font-size: 20px;
  color: #fff;
}

.notification-icon.system { background: #409eff; }
.notification-icon.course { background: #67c23a; }
.notification-icon.assignment { background: #e6a23c; }
.notification-icon.exam { background: #f56c6c; }
.notification-icon.default { background: #909399; }

.notification-content {
  flex: 1;
}

.notification-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}

.notification-header .title {
  font-weight: bold;
  font-size: 16px;
  color: #303133;
}

.notification-header .time {
  margin-left: auto;
  font-size: 12px;
  color: #909399;
}

.notification-body {
  color: #606266;
  font-size: 14px;
  line-height: 1.5;
  margin-bottom: 8px;
}

.notification-footer {
  display: flex;
  gap: 15px;
  font-size: 12px;
  color: #909399;
}

.type-tag {
  background: #e4e7ed;
  padding: 2px 8px;
  border-radius: 4px;
}

.notification-status {
  display: flex;
  align-items: center;
  margin-left: 15px;
}
</style>
