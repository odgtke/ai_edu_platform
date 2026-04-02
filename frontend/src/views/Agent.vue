<template>
  <div class="agent">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">AI智能助手</h1>
        <p class="page-subtitle">基于大语言模型的智能教育助手，为您解答学习问题</p>
      </div>
    </div>

    <div class="agent-container">
      <!-- 左侧对话区域 -->
      <div class="chat-section">
        <!-- 消息列表 -->
        <div class="messages-container" ref="messagesContainer">
          <div v-if="messages.length === 0" class="welcome-screen">
            <div class="welcome-icon">
              <el-icon :size="64"><ChatDotRound /></el-icon>
            </div>
            <h2>您好！我是智慧教育AI助手</h2>
            <p>我可以帮您解答课程相关问题、提供学习建议、推荐适合的课程</p>
            <div class="quick-questions">
              <el-button 
                v-for="question in quickQuestions" 
                :key="question"
                class="question-btn"
                @click="sendQuickQuestion(question)"
              >
                {{ question }}
              </el-button>
            </div>
          </div>
          
          <template v-else>
            <div 
              v-for="(message, index) in messages" 
              :key="index"
              class="message"
              :class="message.type"
            >
              <div class="message-avatar">
                <el-avatar :size="40" :icon="message.type === 'user' ? UserFilled : ChatDotRound" />
              </div>
              <div class="message-content">
                <div class="message-header">
                  <span class="sender">{{ message.type === 'user' ? '您' : 'AI助手' }}</span>
                  <span class="time">{{ message.time }}</span>
                </div>
                <div class="message-body" v-html="formatMessage(message.content)"></div>
              </div>
            </div>
          </template>
          
          <!-- 加载状态 -->
          <div v-if="loading" class="loading-message">
            <el-icon class="loading-icon"><Loading /></el-icon>
            <span>AI正在思考中...</span>
          </div>
        </div>

        <!-- 输入区域 -->
        <div class="input-section">
          <el-input
            v-model="inputMessage"
            type="textarea"
            :rows="3"
            placeholder="输入您的问题，按Enter发送，Shift+Enter换行..."
            @keydown.enter.prevent="handleEnter"
          />
          <div class="input-actions">
            <el-button type="primary" @click="sendMessage" :loading="loading" :disabled="!inputMessage.trim()">
              <el-icon><Promotion /></el-icon>
              发送
            </el-button>
            <el-button @click="clearChat">
              <el-icon><Delete /></el-icon>
              清空
            </el-button>
          </div>
        </div>
      </div>

      <!-- 右侧功能区域 -->
      <div class="sidebar-section">
        <!-- 功能卡片 -->
        <div class="feature-card">
          <h3>智能功能</h3>
          <div class="feature-list">
            <div class="feature-item">
              <el-icon><Reading /></el-icon>
              <div class="feature-info">
                <div class="feature-title">课程答疑</div>
                <div class="feature-desc">解答课程学习中的疑问</div>
              </div>
            </div>
            <div class="feature-item">
              <el-icon><TrendCharts /></el-icon>
              <div class="feature-info">
                <div class="feature-title">学习建议</div>
                <div class="feature-desc">提供个性化学习路径</div>
              </div>
            </div>
            <div class="feature-item">
              <el-icon><Star /></el-icon>
              <div class="feature-info">
                <div class="feature-title">课程推荐</div>
                <div class="feature-desc">推荐适合您的课程</div>
              </div>
            </div>
            <div class="feature-item">
              <el-icon><Document /></el-icon>
              <div class="feature-info">
                <div class="feature-title">知识总结</div>
                <div class="feature-desc">总结重点知识点</div>
              </div>
            </div>
          </div>
        </div>

        <!-- 历史记录 -->
        <div class="history-card">
          <h3>对话历史</h3>
          <div class="history-list">
            <div 
              v-for="(chat, index) in chatHistory" 
              :key="index"
              class="history-item"
              @click="loadChat(chat)"
            >
              <el-icon><ChatLineRound /></el-icon>
              <span class="history-title">{{ chat.title }}</span>
              <span class="history-time">{{ chat.time }}</span>
            </div>
          </div>
          <el-button type="primary" link class="new-chat-btn" @click="clearChat">
            <el-icon><Plus /></el-icon>
            新建对话
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
    const messages = ref([])
    const inputMessage = ref('')
    const loading = ref(false)
    const messagesContainer = ref(null)

    // 快捷问题
    const quickQuestions = ref([
      'Python如何入门学习？',
      '推荐一些数据分析课程',
      '如何提高编程能力？',
      '机器学习的 prerequisites 是什么？'
    ])

    // 对话历史
    const chatHistory = ref([
      { title: 'Python学习咨询', time: '10:30' },
      { title: '数据分析课程推荐', time: '昨天' },
      { title: '前端开发学习路径', time: '前天' }
    ])

    // 格式化消息（简单的 Markdown 支持）
    const formatMessage = (content) => {
      return content
        .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
        .replace(/\*(.*?)\*/g, '<em>$1</em>')
        .replace(/`(.*?)`/g, '<code>$1</code>')
        .replace(/\n/g, '<br>')
    }

    // 发送消息
    const sendMessage = async () => {
      const content = inputMessage.value.trim()
      if (!content || loading.value) return

      // 添加用户消息
      messages.value.push({
        type: 'user',
        content: content,
        time: new Date().toLocaleTimeString()
      })

      inputMessage.value = ''
      loading.value = true

      // 滚动到底部
      await nextTick()
      scrollToBottom()

      // 模拟 AI 回复
      setTimeout(() => {
        const responses = [
          '这是一个很好的问题！让我为您详细解答...',
          '根据您的需求，我建议您可以：\n\n1. 先学习基础知识\n2. 通过实践项目巩固\n3. 参与社区讨论',
          '我理解您的困惑。对于这个问题，您可以参考以下资源：\n\n- 官方文档\n- 在线教程\n- 实践项目',
          '感谢您的提问！基于您的情况，我推荐您：\n\n**第一步**：了解基础概念\n**第二步**：动手实践\n**第三步**：深入学习'
        ]
        
        const randomResponse = responses[Math.floor(Math.random() * responses.length)]
        
        messages.value.push({
          type: 'assistant',
          content: randomResponse,
          time: new Date().toLocaleTimeString()
        })

        loading.value = false
        
        nextTick(() => {
          scrollToBottom()
        })
      }, 1500)
    }

    // 发送快捷问题
    const sendQuickQuestion = (question) => {
      inputMessage.value = question
      sendMessage()
    }

    // 处理 Enter 键
    const handleEnter = (e) => {
      if (!e.shiftKey) {
        sendMessage()
      }
    }

    // 清空对话
    const clearChat = () => {
      messages.value = []
      ElMessage.success('对话已清空')
    }

    // 加载历史对话
    const loadChat = (chat) => {
      ElMessage.info(`加载对话：${chat.title}`)
      // 这里可以实现加载历史对话的逻辑
    }

    // 滚动到底部
    const scrollToBottom = () => {
      if (messagesContainer.value) {
        messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
      }
    }

    onMounted(() => {
      // 可以在这里加载历史消息
    })

</script>

<style scoped>
.agent {
  padding: 24px;
  height: calc(100vh - 120px);
}

.page-header {
  margin-bottom: 20px;
}

.page-header h1 {
  font-size: 28px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0 0 8px 0;
}

.page-header p {
  font-size: 14px;
  color: #666;
  margin: 0;
}

/* 主容器 */
.agent-container {
  display: grid;
  grid-template-columns: 1fr 320px;
  gap: 20px;
  height: calc(100% - 80px);
}

/* 对话区域 */
.chat-section {
  display: flex;
  flex-direction: column;
  background: white;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}

.messages-container {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
}

/* 欢迎界面 */
.welcome-screen {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  text-align: center;
  padding: 40px;
}

.welcome-icon {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  margin-bottom: 24px;
}

.welcome-screen h2 {
  font-size: 24px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0 0 12px 0;
}

.welcome-screen p {
  font-size: 14px;
  color: #666;
  margin: 0 0 32px 0;
}

.quick-questions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  justify-content: center;
}

.question-btn {
  padding: 12px 20px;
  border-radius: 24px;
  background: #f5f7fa;
  border: 1px solid #e4e7ed;
  color: #606266;
  transition: all 0.3s ease;
}

.question-btn:hover {
  background: #667eea;
  border-color: #667eea;
  color: white;
}

/* 消息样式 */
.message {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.message.user {
  flex-direction: row-reverse;
}

.message-avatar {
  flex-shrink: 0;
}

.message-content {
  max-width: 70%;
}

.message-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}

.message.user .message-header {
  flex-direction: row-reverse;
}

.sender {
  font-size: 13px;
  font-weight: 500;
  color: #1a1a1a;
}

.time {
  font-size: 12px;
  color: #999;
}

.message-body {
  padding: 12px 16px;
  border-radius: 12px;
  font-size: 14px;
  line-height: 1.6;
}

.message.assistant .message-body {
  background: #f5f7fa;
  color: #1a1a1a;
}

.message.user .message-body {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

/* 加载状态 */
.loading-message {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  color: #666;
  font-size: 14px;
}

.loading-icon {
  animation: rotate 1s linear infinite;
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

/* 输入区域 */
.input-section {
  padding: 20px;
  border-top: 1px solid #e8e8e8;
}

.input-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 12px;
}

/* 侧边栏 */
.sidebar-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.feature-card,
.history-card {
  background: white;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.feature-card h3,
.history-card h3 {
  font-size: 16px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0 0 16px 0;
}

.feature-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: 8px;
  background: #f5f7fa;
  transition: all 0.3s ease;
}

.feature-item:hover {
  background: #e4e7ed;
}

.feature-item .el-icon {
  font-size: 24px;
  color: #667eea;
}

.feature-info {
  flex: 1;
}

.feature-title {
  font-size: 14px;
  font-weight: 500;
  color: #1a1a1a;
  margin-bottom: 2px;
}

.feature-desc {
  font-size: 12px;
  color: #999;
}

/* 历史记录 */
.history-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.history-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.history-item:hover {
  background: #f5f7fa;
}

.history-item .el-icon {
  font-size: 16px;
  color: #999;
}

.history-title {
  flex: 1;
  font-size: 14px;
  color: #1a1a1a;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.history-time {
  font-size: 12px;
  color: #999;
}

.new-chat-btn {
  margin-top: 12px;
  width: 100%;
}

/* 响应式 */
@media (max-width: 992px) {
  .agent-container {
    grid-template-columns: 1fr;
  }
  
  .sidebar-section {
    display: none;
  }
}

@media (max-width: 768px) {
  .agent {
    padding: 16px;
  }
  
  .message-content {
    max-width: 85%;
  }
  
  .welcome-screen {
    padding: 20px;
  }
  
  .quick-questions {
    flex-direction: column;
    align-items: stretch;
  }
}
</style>
