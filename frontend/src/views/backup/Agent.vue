<template>
  <div class="agent-page">
    <!-- 椤甸潰鏍囬 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">AI 鏅鸿兘鍔╂墜</h1>
        <p class="page-subtitle">鍩轰簬澶фā鍨嬬殑鏅鸿兘鏁欒偛闂绛斿姪鎵</p>
      </div>
      <div class="header-status">
        <span class="status-indicator online"></span>
        <span class="status-text">鏈嶅姟鍦ㄧ嚎</span>
      </div>
    </div>

    <!-- 涓昏亰澶╁尯鍩?-->
    <div class="chat-wrapper">
      <!-- 宸︿晶锛氬揩鎹烽棶棰?-->
      <div class="quick-questions">
        <div class="section-title">
          <el-icon><MagicStick /></el-icon>
          <span>鎺ㄨ崘闂棰</span>
        </div>
        <div class="question-list">
          <div 
            v-for="(q, index) in suggestedQuestions" 
            :key="index"
            class="question-item"
            @click="askQuestion(q)"
          >
            <el-icon><ChatDotRound /></el-icon>
            <span>{{ q }}</span>
          </div>
        </div>
        
        <div class="section-title" style="margin-top: 24px;">
          <el-icon><Clock /></el-icon>
          <span>瀵硅瘽鍘嗗彶</span>
        </div>
        <div class="history-list">
          <div 
            v-for="(session, index) in chatHistory" 
            :key="index"
            class="history-item"
            :class="{ active: currentSession === session.id }"
            @click="switchSession(session.id)"
          >
            <el-icon><ChatLineRound /></el-icon>
            <span class="history-title">{{ session.title }}</span>
            <span class="history-time">{{ session.time }}</span>
          </div>
        </div>
        
        <el-button 
          class="new-chat-btn" 
          type="primary" 
          plain
          @click="startNewChat"
        >
          <el-icon><Plus /></el-icon>
          鏂板缓瀵硅瘽
        </el-button>
      </div>

      <!-- 鍙充晶锛氳亰澶╃獥鍙?-->
      <div class="chat-main">
        <div class="chat-messages" ref="messageContainer">
          <!-- 娆㈣繋娑堟伅 -->
          <div v-if="messages.length === 1" class="welcome-section">
            <div class="welcome-icon">
              <el-icon><ChatDotSquare /></el-icon>
            </div>
            <h2>鎮ㄥソ锛佹垜鏄鏅烘収鏁欒偛AI鍔╂墜</h2>
            <p>鎴戝彲浠ュ府鎮ㄨВ绛旇剧▼鐩稿叧闂棰樸佹彁渚涘︿範寤鸿銆佹帹鑽愰傚悎鐨勮剧?/p>
            <div class="welcome-features">
              <div class="feature-item">
                <el-icon><Reading /></el-icon>
                <span>璇剧▼鍜ㄨ</span>
              </div>
              <div class="feature-item">
                <el-icon><TrendCharts /></el-icon>
                <span>瀛︿範瑙勫垝</span>
              </div>
              <div class="feature-item">
                <el-icon><Star /></el-icon>
                <span>璧勬簮鎺ㄨ崘</span>
              </div>
            </div>
          </div>
          
          <!-- 娑堟伅鍒楄〃 -->
          <div 
            v-for="(msg, index) in messages" 
            :key="index" 
            :class="['message-wrapper', msg.role === 'user' ? 'user' : 'ai']"
          >
            <div class="message-avatar">
              <div v-if="msg.role === 'ai'" class="avatar ai-avatar">
                <el-icon><Cpu /></el-icon>
              </div>
              <div v-else class="avatar user-avatar">
                <el-icon><User /></el-icon>
              </div>
            </div>
            <div class="message-content">
              <div class="message-bubble">
                <div v-if="msg.role === 'ai' && index === messages.length - 1 && loading" class="typing-indicator">
                  <span></span>
                  <span></span>
                  <span></span>
                </div>
                <div v-else class="message-text">{{ msg.content }}</div>
              </div>
              <div class="message-time">{{ msg.time || '鍒氬垰' }}</div>
            </div>
          </div>
        </div>
        
        <!-- 杈撳叆鍖哄煙 -->
        <div class="chat-input-area">
          <div class="input-wrapper">
            <el-input
              v-model="inputMessage"
              type="textarea"
              :rows="2"
              placeholder="杈撳叆鎮ㄧ殑闂棰橈紝鎸 Enter 鍙戦侊紝Shift + Enter 鎹㈣..."
              :disabled="loading"
              resize="none"
              @keydown="handleKeydown"
            />
            <div class="input-actions">
              <el-tooltip content="娓呯┖瀵硅瘽">
                <el-button circle size="small" @click="clearChat">
                  <el-icon><Delete /></el-icon>
                </el-button>
              </el-tooltip>
              <el-button 
                type="primary" 
                class="send-btn"
                :loading="loading"
                :disabled="!inputMessage.trim()"
                @click="sendMessage"
              >
                <el-icon><Promotion /></el-icon>
                鍙戦?
              </el-button>
            </div>
          </div>
          <div class="input-hint">
            <el-icon><InfoFilled /></el-icon>
            <span>AI鐢熸垚鍐呭逛粎渚涘弬鑰冿紝璇蜂互瀹為檯鎯呭喌涓哄噯</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { 
  MagicStick, ChatDotRound, Clock, ChatLineRound, Plus,
  ChatDotSquare, Reading, TrendCharts, Star, Cpu, User,
  Delete, Promotion, InfoFilled 
} from '@element-plus/icons-vue'

export default {
  name: 'Agent',
  components: {
    MagicStick, ChatDotRound, Clock, ChatLineRound, Plus,
    ChatDotSquare, Reading, TrendCharts, Star, Cpu, User,
    Delete, Promotion, InfoFilled
  },
  data() {
    return {
      inputMessage: '',
      messages: [
        { role: 'ai', content: '鎮ㄥソ锛佹垜鏄鏅烘収鏁欒偛骞冲彴鐨凙I鍔╂墜锛屾湁浠涔堝彲浠ュ府鍔╂偍鐨勫悧锛?, time: this.formatTime() }
      ],
      loading: false,
      sessionId: this.generateSessionId(),
      userId: 1,
      currentSession: 'current',
      suggestedQuestions: [
        'Java 鍜?Python 鍝涓鏇撮傚悎鍒濆﹁咃紵',
        '濡備綍鍒跺畾楂樻晥鐨勫︿範璁″垝锛',
        '鎺ㄨ崘涓浜涘墠绔寮鍙戠殑浼樿川璇剧▼',
        '濡備綍璇勪及鑷宸辩殑缂栫▼姘村钩锛',
        '瀛︿範鏁版嵁鍒嗘瀽闇瑕佹帉鎻″摢浜涙妧鑳斤紵'
      ],
      chatHistory: [
        { id: '1', title: 'Java 瀛︿範鍜ㄨ', time: '10:30' },
        { id: '2', title: '鍓嶇寮鍙戣矾绾?, time: '鏄ㄥぉ' },
        { id: '3', title: '鏁版嵁鍒嗘瀽鍏ラ棬', time: '3澶╁墠' }
      ]
    }
  },
  mounted() {
    this.loadHistory()
  },
  methods: {
    generateSessionId() {
      return 'session_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9)
    },
    async loadHistory() {
      try {
        const response = await fetch(/langchain/history/${this.userId}`)
        const result = await response.json()
        if (result.code === 200 && result.data && result.data.length > 0) {
          // 灏嗗巻鍙茶板綍杞鎹涓烘秷鎭鏍煎紡
          const history = []
          result.data.slice(-10).forEach(item => {
            history.push({ role: 'user', content: item.question })
            history.push({ role: 'ai', content: item.answer })
          })
          this.messages = history
        }
      } catch (error) {
        console.error('鍔犺浇鍘嗗彶璁板綍澶辫触:', error)
      }
    },
    handleKeydown(e) {
      if (e.key === 'Enter' && !e.shiftKey) {
        e.preventDefault()
        this.sendMessage()
      }
    },
    askQuestion(question) {
      this.inputMessage = question
      this.sendMessage()
    },
    startNewChat() {
      this.messages = [
        { role: 'ai', content: '鎮ㄥソ锛佹垜鏄鏅烘収鏁欒偛骞冲彴鐨凙I鍔╂墜锛屾湁浠涔堝彲浠ュ府鍔╂偍鐨勫悧锛?, time: this.formatTime() }
      ]
      this.sessionId = this.generateSessionId()
      this.currentSession = 'current'
    },
    clearChat() {
      this.$confirm('纭瀹氳佹竻绌哄綋鍓嶅硅瘽鍚楋?, '鎻愮ず', {
        confirmButtonText: '纭瀹',
        cancelButtonText: '鍙栨秷',
        type: 'warning'
      }).then(() => {
        this.startNewChat()
        this.$message.success('瀵硅瘽宸叉竻绌?)
      }).catch(() => {})
    },
    switchSession(sessionId) {
      this.currentSession = sessionId
      // 瀹為檯搴斿姞杞藉瑰簲浼氳瘽鍘嗗?
    },
    formatTime() {
      const now = new Date()
      return `${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}`
    },
    async sendMessage() {
      if (!this.inputMessage.trim() || this.loading) return
      
      const userQuestion = this.inputMessage.trim()
      const currentTime = this.formatTime()
      this.messages.push({ role: 'user', content: userQuestion, time: currentTime })
      this.inputMessage = ''
      this.loading = true
      this.scrollToBottom()
      
      try {
        console.log('鍙戦佽锋眰鍒板悗绔 API...')
        const response = await fetch('/langchain/qa', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            userId: this.userId,
            question: userQuestion,
            sessionId: this.sessionId
          })
        })
        
        console.log('鏀跺埌鍝嶅簲:', response.status)
        const result = await response.json()
        console.log('瑙ｆ瀽缁撴灉:', result)
        if (result.code === 200 && result.data) {
          console.log('澶фā鍨嬪洖澶?', result.data.answer)
          this.messages.push({
            role: 'ai',
            content: result.data.answer || '鎶辨瓑锛屾垜鏃犳硶鐞嗚В鎮ㄧ殑闂棰樸?,
            time: this.formatTime()
          })
        } else {
          this.messages.push({
            role: 'ai',
            content: '鎶辨瓑锛屾湇鍔℃殏鏃朵笉鍙鐢锛岃风◢鍚庡啀璇曘?,
            time: this.formatTime()
          })
        }
      } catch (error) {
        console.error('鍙戦佹秷鎭澶辫?', error)
        this.messages.push({
          role: 'ai',
          content: '鎶辨瓑锛岀綉缁滆繛鎺ュ紓甯革紝璇锋鏌ョ綉缁滃悗閲嶈瘯銆?,
          time: this.formatTime()
        })
      } finally {
        this.loading = false
        this.scrollToBottom()
      }
    },
    scrollToBottom() {
      this.$nextTick(() => {
        const container = this.$refs.messageContainer
        if (container) {
          container.scrollTop = container.scrollHeight
        }
      })
    }
  }
}
</script>

<style scoped>
/* ============================================
   AI 鏅鸿兘鍔╂墜椤甸潰 - 鏂拌捐¤勮?
   ============================================ */

.agent-page {
  height: calc(100vh - var(--header-height) - 48px);
  display: flex;
  flex-direction: column;
}

/* 椤甸潰鏍囬 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-5);
}

.page-title {
  font-size: var(--text-2xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  margin: 0 0 var(--space-2);
}

.page-subtitle {
  font-size: var(--text-sm);
  color: var(--text-secondary);
  margin: 0;
}

.header-status {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-2) var(--space-4);
  background: var(--success-50);
  border-radius: var(--radius-full);
}

.status-indicator {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--success-500);
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

.status-text {
  font-size: var(--text-sm);
  color: var(--success-600);
  font-weight: var(--font-medium);
}

/* 鑱婂ぉ甯冨眬 */
.chat-wrapper {
  flex: 1;
  display: grid;
  grid-template-columns: 280px 1fr;
  gap: var(--space-5);
  min-height: 0;
}

/* 宸︿晶蹇鎹烽棶棰 */
.quick-questions {
  background: white;
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-card);
  padding: var(--space-5);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.section-title {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin-bottom: var(--space-4);
}

.section-title .el-icon {
  color: var(--primary-500);
}

.question-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}

.question-item {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-3);
  background: var(--bg-secondary);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all var(--transition-fast);
  font-size: var(--text-sm);
  color: var(--text-secondary);
}

.question-item:hover {
  background: var(--primary-50);
  color: var(--primary-600);
}

.question-item .el-icon {
  font-size: 16px;
  color: var(--primary-500);
}

.history-list {
  flex: 1;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}

.history-item {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-3);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all var(--transition-fast);
}

.history-item:hover,
.history-item.active {
  background: var(--primary-50);
}

.history-item .el-icon {
  font-size: 16px;
  color: var(--text-tertiary);
}

.history-title {
  flex: 1;
  font-size: var(--text-sm);
  color: var(--text-primary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.history-time {
  font-size: var(--text-xs);
  color: var(--text-tertiary);
}

.new-chat-btn {
  margin-top: auto;
  width: 100%;
}

/* 涓昏亰澶╁尯鍩?*/
.chat-main {
  background: white;
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-card);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: var(--space-6);
  background: linear-gradient(180deg, var(--gray-50) 0%, white 100%);
}

/* 娆㈣繋鍖哄煙 */
.welcome-section {
  text-align: center;
  padding: var(--space-10) var(--space-6);
}

.welcome-icon {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, var(--primary-500) 0%, var(--primary-600) 100%);
  border-radius: var(--radius-xl);
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto var(--space-5);
  box-shadow: 0 8px 24px rgba(14, 165, 233, 0.3);
}

.welcome-icon .el-icon {
  font-size: 40px;
  color: white;
}

.welcome-section h2 {
  font-size: var(--text-xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  margin: 0 0 var(--space-3);
}

.welcome-section p {
  font-size: var(--text-base);
  color: var(--text-secondary);
  margin: 0 0 var(--space-6);
}

.welcome-features {
  display: flex;
  justify-content: center;
  gap: var(--space-6);
}

.feature-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-4);
  background: white;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
  min-width: 100px;
}

.feature-item .el-icon {
  font-size: 24px;
  color: var(--primary-500);
}

.feature-item span {
  font-size: var(--text-sm);
  color: var(--text-secondary);
}

/* 娑堟伅鏍峰紡 */
.message-wrapper {
  display: flex;
  gap: var(--space-3);
  margin-bottom: var(--space-5);
  animation: messageSlide 0.3s ease;
}

@keyframes messageSlide {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.message-wrapper.user {
  flex-direction: row-reverse;
}

.message-avatar {
  flex-shrink: 0;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.ai-avatar {
  background: linear-gradient(135deg, var(--primary-500) 0%, var(--primary-600) 100%);
  color: white;
}

.user-avatar {
  background: var(--gray-200);
  color: var(--gray-600);
}

.avatar .el-icon {
  font-size: 20px;
}

.message-content {
  max-width: 70%;
  display: flex;
  flex-direction: column;
  gap: var(--space-1);
}

.message-wrapper.user .message-content {
  align-items: flex-end;
}

.message-bubble {
  padding: var(--space-4);
  border-radius: var(--radius-lg);
  line-height: 1.6;
  font-size: var(--text-base);
}

.message-wrapper.ai .message-bubble {
  background: white;
  color: var(--text-primary);
  border-bottom-left-radius: var(--space-1);
  box-shadow: var(--shadow-sm);
}

.message-wrapper.user .message-bubble {
  background: linear-gradient(135deg, var(--primary-500) 0%, var(--primary-600) 100%);
  color: white;
  border-bottom-right-radius: var(--space-1);
}

.message-time {
  font-size: var(--text-xs);
  color: var(--text-tertiary);
}

/* 杈撳叆鎵撳瓧鍔ㄧ敾 */
.typing-indicator {
  display: flex;
  gap: 4px;
  padding: var(--space-2);
}

.typing-indicator span {
  width: 8px;
  height: 8px;
  background: var(--primary-400);
  border-radius: 50%;
  animation: typing 1.4s infinite ease-in-out both;
}

.typing-indicator span:nth-child(1) { animation-delay: -0.32s; }
.typing-indicator span:nth-child(2) { animation-delay: -0.16s; }

@keyframes typing {
  0%, 80%, 100% { transform: scale(0.6); opacity: 0.5; }
  40% { transform: scale(1); opacity: 1; }
}

/* 杈撳叆鍖哄煙 */
.chat-input-area {
  padding: var(--space-5);
  background: white;
  border-top: 1px solid var(--border-light);
}

.input-wrapper {
  display: flex;
  gap: var(--space-3);
  align-items: flex-end;
}

.input-wrapper :deep(.el-textarea__inner) {
  border-radius: var(--radius-lg);
  padding: var(--space-3) var(--space-4);
  resize: none;
  min-height: 60px !important;
}

.input-actions {
  display: flex;
  gap: var(--space-2);
  align-items: center;
}

.send-btn {
  background: linear-gradient(135deg, var(--primary-500) 0%, var(--primary-600) 100%);
  border: none;
  padding: 12px 24px;
  display: flex;
  align-items: center;
  gap: var(--space-2);
}

.send-btn:hover:not(:disabled) {
  background: linear-gradient(135deg, var(--primary-400) 0%, var(--primary-500) 100%);
  transform: translateY(-1px);
  box-shadow: var(--shadow-md);
}

.send-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.input-hint {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-2);
  margin-top: var(--space-3);
  font-size: var(--text-xs);
  color: var(--text-tertiary);
}

.input-hint .el-icon {
  font-size: 14px;
}

/* 鍝嶅簲寮忛傞厤 */
@media (max-width: 1024px) {
  .chat-wrapper {
    grid-template-columns: 1fr;
  }
  
  .quick-questions {
    display: none;
  }
}

@media (max-width: 768px) {
  .welcome-features {
    flex-direction: column;
    align-items: center;
  }
  
  .message-content {
    max-width: 85%;
  }
  
  .input-wrapper {
    flex-direction: column;
  }
  
  .input-actions {
    width: 100%;
    justify-content: flex-end;
  }
}
</style>
