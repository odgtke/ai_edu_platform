<template>
  <div class="assessment-take-page">
    <!-- 椤堕儴瀵艰埅鏍?-->
    <div class="assessment-header">
      <div class="header-left">
        <el-button text @click="confirmExit">
          <el-icon><ArrowLeft /></el-icon>
          閫鍑?
        </el-button>
        <span class="assessment-title">{{ assessment.assessmentName || '鑳藉姏璇勪及' }}</span>
      </div>
      <div class="header-center">
        <div class="timer" :class="{ 'warning': remainingTime < 300, 'danger': remainingTime < 60 }">
          <el-icon><Timer /></el-icon>
          <span>{{ formatTime(remainingTime) }}</span>
        </div>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="showSubmitConfirm" :disabled="submitting">
          鎻愪氦绛斿嵎
        </el-button>
      </div>
    </div>

    <!-- 涓讳綋鍐呭 -->
    <div class="assessment-body">
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="10" animated />
      </div>
      
      <div v-else-if="error" class="error-container">
        <el-empty :description="error">
          <el-button type="primary" @click="loadAssessment">閲嶆柊鍔犺浇</el-button>
        </el-empty>
      </div>
      
      <template v-else>
        <!-- 宸︿晶棰樼洰鍖哄煙 -->
        <div class="question-area">
          <div class="question-card">
            <div class="question-header">
              <el-tag :type="getQuestionTypeTag(currentQuestion.questionType)">
                {{ getQuestionTypeLabel(currentQuestion.questionType) }}
              </el-tag>
              <span class="question-score">{{ currentQuestion.score }}鍒?/span>
            </div>
            
            <div class="question-content">
              <h3 class="question-title">
                <span class="question-number">绗?{{ currentIndex + 1 }} 棰?/span>
                {{ currentQuestion.content }}
              </h3>
              
              <!-- 鍗曢夐 -->
              <div v-if="currentQuestion.questionType === 1" class="options-list">
                <div
                  v-for="(option, key) in parsedOptions"
                  :key="key"
                  class="option-item"
                  :class="{ 'selected': userAnswers[currentQuestion.questionOrder] === key }"
                  @click="selectSingleOption(key)"
                >
                  <el-radio
                    :model-value="userAnswers[currentQuestion.questionOrder]"
                    :label="key"
                    @click.stop
                  >
                    <span class="option-key">{{ key }}.</span>
                    <span class="option-text">{{ option }}</span>
                  </el-radio>
                </div>
              </div>
              
              <!-- 澶氶夐 -->
              <div v-else-if="currentQuestion.questionType === 2" class="options-list">
                <div
                  v-for="(option, key) in parsedOptions"
                  :key="key"
                  class="option-item"
                  :class="{ 'selected': isMultiSelected(key) }"
                  @click="toggleMultiOption(key)"
                >
                  <el-checkbox
                    :model-value="isMultiSelected(key)"
                    @click.stop
                  >
                    <span class="option-key">{{ key }}.</span>
                    <span class="option-text">{{ option }}</span>
                  </el-checkbox>
                </div>
              </div>
              
              <!-- 鍒ゆ柇棰?-->
              <div v-else-if="currentQuestion.questionType === 3" class="options-list">
                <div
                  v-for="option in judgeOptions"
                  :key="option.value"
                  class="option-item"
                  :class="{ 'selected': userAnswers[currentQuestion.questionOrder] === option.value }"
                  @click="selectSingleOption(option.value)"
                >
                  <el-radio
                    :model-value="userAnswers[currentQuestion.questionOrder]"
                    :label="option.value"
                    @click.stop
                  >
                    {{ option.label }}
                  </el-radio>
                </div>
              </div>
              
              <!-- 濉绌洪?-->
              <div v-else-if="currentQuestion.questionType === 4" class="fill-blank-area">
                <el-input
                  v-model="userAnswers[currentQuestion.questionOrder]"
                  type="textarea"
                  :rows="3"
                  placeholder="璇疯緭鍏ユ偍鐨勭瓟妗?
                  maxlength="500"
                  show-word-limit
                />
              </div>
              
              <!-- 绠绛旈 -->
              <div v-else-if="currentQuestion.questionType === 5" class="essay-area">
                <el-input
                  v-model="userAnswers[currentQuestion.questionOrder]"
                  type="textarea"
                  :rows="6"
                  placeholder="璇疯緭鍏ユ偍鐨勭瓟妗?
                  maxlength="2000"
                  show-word-limit
                />
              </div>
            </div>
          </div>
          
          <!-- 搴曢儴瀵艰埅 -->
          <div class="question-navigation">
            <el-button
              :disabled="currentIndex === 0"
              @click="prevQuestion"
            >
              <el-icon><ArrowLeft /></el-icon>
              涓婁竴棰?
            </el-button>
            
            <div class="nav-dots">
              <el-tooltip
                v-for="(q, index) in questions"
                :key="index"
                :content="`绗?${index + 1} 棰?{getAnswerStatus(index)}`"
                placement="top"
              >
                <span
                  class="nav-dot"
                  :class="{
                    'active': index === currentIndex,
                    'answered': hasAnswered(index)
                  }"
                  @click="jumpToQuestion(index)"
                >
                  {{ index + 1 }}
                </span>
              </el-tooltip>
            </div>
            
            <el-button
              v-if="currentIndex < questions.length - 1"
              type="primary"
              @click="nextQuestion"
            >
              涓嬩竴棰?
              <el-icon><ArrowRight /></el-icon>
            </el-button>
            <el-button
              v-else
              type="success"
              @click="showSubmitConfirm"
            >
              鎻愪氦绛斿嵎
            </el-button>
          </div>
        </div>
        
        <!-- 鍙充晶淇℃伅闈㈡澘 -->
        <div class="info-panel">
          <el-card class="progress-card">
            <template #header>
              <span>绛旈樿繘搴</span>
            </template>
            <div class="progress-stats">
              <div class="stat-row">
                <span>鎬婚樻?/span>
                <span class="stat-value">{{ questions.length }} 棰?/span>
              </div>
              <div class="stat-row">
                <span>宸蹭綔绛?/span>
                <span class="stat-value answered">{{ answeredCount }} 棰?/span>
              </div>
              <div class="stat-row">
                <span>鏈浣滅?/span>
                <span class="stat-value unanswered">{{ questions.length - answeredCount }} 棰?/span>
              </div>
            </div>
            <el-progress
              :percentage="progressPercentage"
              :stroke-width="10"
              :status="progressStatus"
            />
          </el-card>
          
          <el-card class="question-nav-card">
            <template #header>
              <span>棰樼洰瀵艰埅</span>
            </template>
            <div class="question-grid">
              <div
                v-for="(q, index) in questions"
                :key="index"
                class="question-num"
                :class="{
                  'current': index === currentIndex,
                  'answered': hasAnswered(index)
                }"
                @click="jumpToQuestion(index)"
              >
                {{ index + 1 }}
              </div>
            </div>
          </el-card>
        </div>
      </template>
    </div>

    <!-- 鎻愪氦纭璁ゅ硅瘽妗?-->
    <el-dialog
      v-model="submitDialogVisible"
      title="纭璁ゆ彁浜"
      width="400px"
      :close-on-click-modal="false"
    >
      <div class="submit-confirm-content">
        <el-icon class="confirm-icon" :size="48" color="#E6A23C"><WarningFilled /></el-icon>
        <p class="confirm-text">纭瀹氳佹彁浜ょ瓟鍗峰悧锛?/p>
        <div class="confirm-stats">
          <div class="stat-item">
            <span>鎬婚樻暟锛</span>
            <strong>{{ questions.length }} 棰?/strong>
          </div>
          <div class="stat-item">
            <span>宸蹭綔绛旓細</span>
            <strong class="answered">{{ answeredCount }} 棰?/strong>
          </div>
          <div class="stat-item">
            <span>鏈浣滅瓟锛</span>
            <strong class="unanswered">{{ questions.length - answeredCount }} 棰?/strong>
          </div>
        </div>
        <el-alert
          v-if="answeredCount < questions.length"
          title="杩樻湁鏈浣滅瓟鐨勯樼洰锛屾彁浜ゅ悗灏嗘棤娉曚慨鏀?
          type="warning"
          :closable="false"
          show-icon
        />
      </div>
      <template #footer>
        <el-button @click="submitDialogVisible = false">缁х画绛旈</el-button>
        <el-button type="primary" @click="submitAssessment" :loading="submitting">
          纭璁ゆ彁浜
        </el-button>
      </template>
    </el-dialog>

    <!-- 閫鍑虹‘璁ゅ硅瘽妗 -->
    <el-dialog
      v-model="exitDialogVisible"
      title="纭璁ら鍑?
      width="400px"
    >
      <div class="exit-confirm-content">
        <el-icon class="confirm-icon" :size="48" color="#F56C6C"><CircleCloseFilled /></el-icon>
        <p class="confirm-text">纭瀹氳侀鍑鸿瘎浼板悧锛?/p>
        <p class="confirm-subtext">閫鍑哄悗绛旈樿繘搴﹀皢涓嶄細淇濆?/p>
      </div>
      <template #footer>
        <el-button @click="exitDialogVisible = false">缁х画绛旈</el-button>
        <el-button type="danger" @click="exitAssessment">纭璁ら鍑?/el-button>
      </template>
    </el-dialog>

    <!-- 鑷鍔ㄦ彁浜ゆ彁绀 -->
    <el-dialog
      v-model="autoSubmitDialogVisible"
      title="鏃堕棿鍒?
      width="400px"
      :close-on-click-modal="false"
      :show-close="false"
    >
      <div class="auto-submit-content">
        <el-icon class="confirm-icon" :size="48" color="#409EFF"><InfoFilled /></el-icon>
        <p class="confirm-text">鑰冭瘯鏃堕棿宸茬粨鏉?/p>
        <p class="confirm-subtext">绯荤粺灏嗚嚜鍔ㄦ彁浜ゆ偍鐨勭瓟鍗?/p>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { ArrowLeft, ArrowRight, Timer, WarningFilled, CircleCloseFilled, InfoFilled } from '@element-plus/icons-vue'

export default {
  name: 'AssessmentTake',
  components: { ArrowLeft, ArrowRight, Timer, WarningFilled, CircleCloseFilled, InfoFilled },
  data() {
    return {
      userId: 1,
      assessmentId: null,
      userAssessmentId: null,
      loading: true,
      error: null,
      assessment: {},
      questions: [],
      currentIndex: 0,
      userAnswers: {},
      remainingTime: 0,
      timerInterval: null,
      submitting: false,
      submitDialogVisible: false,
      exitDialogVisible: false,
      autoSubmitDialogVisible: false,
      judgeOptions: [
        { value: 'A', label: '鉁?姝ｇ‘' },
        { value: 'B', label: '鉁?閿欒' }
      ]
    }
  },
  computed: {
    currentQuestion() {
      return this.questions[this.currentIndex] || {}
    },
    parsedOptions() {
      if (!this.currentQuestion.options) return {}
      try {
        return JSON.parse(this.currentQuestion.options)
      } catch {
        return {}
      }
    },
    answeredCount() {
      return Object.values(this.userAnswers).filter(answer => 
        answer !== undefined && answer !== null && answer !== ''
      ).length
    },
    progressPercentage() {
      if (this.questions.length === 0) return 0
      return Math.round((this.answeredCount / this.questions.length) * 100)
    },
    progressStatus() {
      if (this.answeredCount === this.questions.length) return 'success'
      if (this.answeredCount > 0) return ''
      return ''
    }
  },
  mounted() {
    this.userAssessmentId = this.$route.params.userAssessmentId
    this.assessmentId = this.$route.query.assessmentId
    if (!this.userAssessmentId || !this.assessmentId) {
      this.error = '鍙傛暟閿欒锛屾棤娉曞姞杞借瘎浼?
      this.loading = false
      return
    }
    this.loadAssessment()
    // 娣诲姞椤甸潰鍏抽棴鎻愮ず
    window.addEventListener('beforeunload', this.handleBeforeUnload)
  },
  beforeUnmount() {
    this.clearTimer()
    window.removeEventListener('beforeunload', this.handleBeforeUnload)
  },
  methods: {
    async loadAssessment() {
      this.loading = true
      this.error = null
      try {
        const response = await fetch(/assessments/${this.assessmentId}`)
        const result = await response.json()
        if (result.code === 200 && result.data) {
          this.assessment = result.data.assessment || {}
          this.questions = result.data.questions || []
          this.remainingTime = (this.assessment.duration || 60) * 60
          this.startTimer()
        } else {
          this.error = result.message || '鍔犺浇璇勪及澶辫触'
        }
      } catch (error) {
        console.error('鍔犺浇璇勪及澶辫触:', error)
        this.error = '缃戠粶閿欒锛岃风◢鍚庨噸璇'
      } finally {
        this.loading = false
      }
    },
    startTimer() {
      this.clearTimer()
      this.timerInterval = setInterval(() => {
        if (this.remainingTime > 0) {
          this.remainingTime--
          if (this.remainingTime === 0) {
            this.handleTimeUp()
          }
        }
      }, 1000)
    },
    clearTimer() {
      if (this.timerInterval) {
        clearInterval(this.timerInterval)
        this.timerInterval = null
      }
    },
    handleTimeUp() {
      this.clearTimer()
      this.autoSubmitDialogVisible = true
      setTimeout(() => {
        this.submitAssessment()
      }, 2000)
    },
    formatTime(seconds) {
      const hours = Math.floor(seconds / 3600)
      const mins = Math.floor((seconds % 3600) / 60)
      const secs = seconds % 60
      if (hours > 0) {
        return `${hours}:${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
      }
      return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
    },
    getQuestionTypeLabel(type) {
      const types = {
        1: '鍗曢夐',
        2: '澶氶夐',
        3: '鍒ゆ柇棰?,
        4: '濉绌洪?,
        5: '绠绛旈'
      }
      return types[type] || '鏈鐭ラ樺瀷'
    },
    getQuestionTypeTag(type) {
      const tags = {
        1: 'primary',
        2: 'success',
        3: 'warning',
        4: 'info',
        5: 'danger'
      }
      return tags[type] || 'info'
    },
    selectSingleOption(key) {
      this.userAnswers[this.currentQuestion.questionOrder] = key
    },
    isMultiSelected(key) {
      const currentAnswer = this.userAnswers[this.currentQuestion.questionOrder]
      if (!currentAnswer) return false
      return currentAnswer.split(',').includes(key)
    },
    toggleMultiOption(key) {
      const currentAnswer = this.userAnswers[this.currentQuestion.questionOrder] || ''
      const selected = currentAnswer ? currentAnswer.split(',') : []
      const index = selected.indexOf(key)
      if (index > -1) {
        selected.splice(index, 1)
      } else {
        selected.push(key)
      }
      this.userAnswers[this.currentQuestion.questionOrder] = selected.sort().join(',')
    },
    prevQuestion() {
      if (this.currentIndex > 0) {
        this.currentIndex--
      }
    },
    nextQuestion() {
      if (this.currentIndex < this.questions.length - 1) {
        this.currentIndex++
      }
    },
    jumpToQuestion(index) {
      this.currentIndex = index
    },
    hasAnswered(index) {
      const question = this.questions[index]
      if (!question) return false
      const answer = this.userAnswers[question.questionOrder]
      return answer !== undefined && answer !== null && answer !== ''
    },
    getAnswerStatus(index) {
      return this.hasAnswered(index) ? '锛堝凡绛旓級' : '锛堟湭绛旓級'
    },
    showSubmitConfirm() {
      this.submitDialogVisible = true
    },
    async submitAssessment() {
      this.submitting = true
      this.submitDialogVisible = false
      try {
        const response = await fetch(
          /assessments/${this.assessmentId}/submit?userId=${this.userId}`,
          {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(this.userAnswers)
          }
        )
        const result = await response.json()
        if (result.code === 200) {
          this.$message.success('鎻愪氦鎴愬姛锛?)
          this.$router.push(`/assessment/result/${result.data.userAssessmentId}`)
        } else {
          this.$message.error(result.message || '鎻愪氦澶辫触')
        }
      } catch (error) {
        console.error('鎻愪氦澶辫触:', error)
        this.$message.error('鎻愪氦澶辫触锛岃风◢鍚庨噸璇')
      } finally {
        this.submitting = false
      }
    },
    confirmExit() {
      this.exitDialogVisible = true
    },
    exitAssessment() {
      this.exitDialogVisible = false
      this.$router.push('/assessment')
    },
    handleBeforeUnload(e) {
      if (this.answeredCount > 0 && !this.submitting) {
        e.preventDefault()
        e.returnValue = ''
      }
    }
  }
}
</script>

<style scoped>
.assessment-take-page {
  min-height: 100vh;
  background: #f5f7fa;
  display: flex;
  flex-direction: column;
}

.assessment-header {
  background: #fff;
  padding: 16px 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.assessment-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.timer {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 20px;
  font-weight: 600;
  color: #409EFF;
  padding: 8px 16px;
  background: #ecf5ff;
  border-radius: 8px;
}

.timer.warning {
  color: #E6A23C;
  background: #fdf6ec;
}

.timer.danger {
  color: #F56C6C;
  background: #fef0f0;
  animation: pulse 1s infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.7; }
}

.assessment-body {
  flex: 1;
  display: flex;
  padding: 20px;
  gap: 20px;
  max-width: 1400px;
  margin: 0 auto;
  width: 100%;
}

.loading-container,
.error-container {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
}

.question-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.question-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  flex: 1;
}

.question-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #ebeef5;
}

.question-score {
  font-size: 14px;
  color: #909399;
}

.question-content {
  padding: 10px 0;
}

.question-title {
  font-size: 16px;
  color: #303133;
  line-height: 1.8;
  margin-bottom: 24px;
}

.question-number {
  display: inline-block;
  background: #409EFF;
  color: #fff;
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 14px;
  margin-right: 12px;
}

.options-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.option-item {
  padding: 16px 20px;
  border: 2px solid #e4e7ed;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.option-item:hover {
  border-color: #409EFF;
  background: #f5f7fa;
}

.option-item.selected {
  border-color: #409EFF;
  background: #ecf5ff;
}

.option-key {
  font-weight: 600;
  margin-right: 8px;
  color: #409EFF;
}

.option-text {
  color: #606266;
}

.fill-blank-area,
.essay-area {
  padding: 10px 0;
}

.question-navigation {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.nav-dots {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  justify-content: center;
  max-width: 400px;
}

.nav-dot {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: #f5f7fa;
  border: 2px solid #e4e7ed;
  cursor: pointer;
  font-size: 13px;
  color: #606266;
  transition: all 0.3s;
}

.nav-dot:hover {
  border-color: #409EFF;
}

.nav-dot.active {
  background: #409EFF;
  border-color: #409EFF;
  color: #fff;
}

.nav-dot.answered {
  background: #67C23A;
  border-color: #67C23A;
  color: #fff;
}

.info-panel {
  width: 300px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.progress-card :deep(.el-card__header) {
  font-weight: 600;
}

.progress-stats {
  margin-bottom: 16px;
}

.stat-row {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
}

.stat-row:last-child {
  border-bottom: none;
}

.stat-value {
  font-weight: 600;
  color: #303133;
}

.stat-value.answered {
  color: #67C23A;
}

.stat-value.unanswered {
  color: #F56C6C;
}

.question-nav-card :deep(.el-card__header) {
  font-weight: 600;
}

.question-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 8px;
}

.question-num {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  background: #f5f7fa;
  border: 2px solid #e4e7ed;
  cursor: pointer;
  font-size: 14px;
  color: #606266;
  transition: all 0.3s;
}

.question-num:hover {
  border-color: #409EFF;
}

.question-num.current {
  background: #409EFF;
  border-color: #409EFF;
  color: #fff;
}

.question-num.answered {
  background: #67C23A;
  border-color: #67C23A;
  color: #fff;
}

.submit-confirm-content,
.exit-confirm-content,
.auto-submit-content {
  text-align: center;
  padding: 20px 0;
}

.confirm-icon {
  margin-bottom: 16px;
}

.confirm-text {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}

.confirm-subtext {
  font-size: 14px;
  color: #909399;
}

.confirm-stats {
  background: #f5f7fa;
  border-radius: 8px;
  padding: 16px;
  margin: 20px 0;
  text-align: left;
}

.stat-item {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
}

.stat-item .answered {
  color: #67C23A;
}

.stat-item .unanswered {
  color: #F56C6C;
}

@media (max-width: 1024px) {
  .assessment-body {
    flex-direction: column;
  }
  
  .info-panel {
    width: 100%;
    flex-direction: row;
  }
  
  .progress-card,
  .question-nav-card {
    flex: 1;
  }
}

@media (max-width: 768px) {
  .assessment-header {
    flex-wrap: wrap;
    gap: 10px;
  }
  
  .header-center {
    order: 3;
    width: 100%;
    justify-content: center;
  }
  
  .info-panel {
    flex-direction: column;
  }
  
  .nav-dots {
    display: none;
  }
}
</style>
