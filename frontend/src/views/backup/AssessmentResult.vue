<template>
  <div class="assessment-result-page">
    <div class="result-container">
      <!-- 鍔犺浇鐘舵佲偓?-->
      <div v-if="loading" class="loading-wrapper">
        <el-skeleton :rows="10" animated />
      </div>
      
      <!-- 缁鎾寸亯鍐呭癸拷 -->
      <template v-else-if="result">
        <!-- 鎴愮哗澶撮儴 -->
        <div class="result-header" :class="{ 'passed': result.isPassed, 'failed': !result.isPassed }">
          <div class="result-icon">
            {{ result.isPassed ? '鉁' : '鉁' }}
          </div>
          <h2 class="result-title">{{ result.isPassed ? '鎭鍠滈氳繃锛' : '鏈閫氳繃璇勪及' }}</h2>
          <p class="result-subtitle">{{ result.assessmentName }}</p>
          
          <div class="score-display">
            <div class="score-circle">
              <div class="score-value">{{ result.score }}</div>
              <div class="score-total">/ {{ result.totalScore }}</div>
            </div>
            <div class="score-percentage">{{ scorePercentage }}%</div>
          </div>
          
          <div class="result-meta">
            <el-tag :type="result.isPassed ? 'success' : 'danger'" size="large" effect="dark">
              {{ result.isPassed ? '宸叉煡鈧姘崇箖' : '鏈燂拷鈧姘崇箖' }}
            </el-tag>
            <span class="meta-item">
              <el-icon><Timer /></el-icon>
              閻銊︽ {{ formatTime(result.timeSpent) }}
            </span>
            <span class="meta-item">
              <el-icon><Calendar /></el-icon>
              {{ formatDate(result.completeTime) }}
            </span>
          </div>
        </div>
        
        <!-- 鑳藉姏鍒嗘瀽 -->
        <div class="result-section">
          <h3 class="section-title">
            <el-icon><DataAnalysis /></el-icon>
            鑳藉姏鍒嗘瀽
          </h3>
          <div class="capability-chart-wrapper">
            <div ref="capabilityChart" class="capability-chart"></div>
          </div>
          <div class="capability-tags">
            <div 
              v-for="(capability, index) in capabilityList" 
              :key="index"
              class="capability-tag"
              :class="getCapabilityLevel(capability.score)"
            >
              <span class="tag-name">{{ capability.name }}</span>
              <el-progress 
                :percentage="capability.score" 
                :color="getCapabilityColor(capability.score)"
                :stroke-width="8"
                :show-text="false"
              />
              <span class="tag-score">{{ capability.score }}鍒?/span>
            </div>
          </div>
        </div>
        
        <!-- 缁涙棃锟借凤附鍎 -->
        <div class="result-section">
          <h3 class="section-title">
            <el-icon><Document /></el-icon>
            缁涙棃锟借凤附鍎
          </h3>
          <div class="answer-stats">
            <div class="stat-card correct">
              <div class="stat-icon">閴?/div>
              <div class="stat-info">
                <div class="stat-num">{{ correctCount }}</div>
                <div class="stat-label">姝锝団</div>
              </div>
            </div>
            <div class="stat-card wrong">
              <div class="stat-icon">閴?/div>
              <div class="stat-info">
                <div class="stat-num">{{ wrongCount }}</div>
                <div class="stat-label">闁挎瑨锟</div>
              </div>
            </div>
            <div class="stat-card unanswered">
              <div class="stat-icon">-</div>
              <div class="stat-info">
                <div class="stat-num">{{ unansweredCount }}</div>
                <div class="stat-label">鏈燂拷鐡</div>
              </div>
            </div>
          </div>
          
          <!-- 妫版兼窗鍒犳勩 -->
          <div class="question-list">
            <div
              v-for="(detail, index) in answerDetails"
              :key="index"
              class="question-item"
              :class="{ 'correct': detail.isCorrect, 'wrong': !detail.isCorrect && detail.userAnswer, 'unanswered': !detail.userAnswer }"
            >
              <div class="question-header">
                <span class="question-num">缁?{{ detail.questionOrder }} 妫?/span>
                <el-tag :type="getQuestionTypeTag(detail.questionType)" size="small">
                  {{ getQuestionTypeLabel(detail.questionType) }}
                </el-tag>
                <span class="question-score">{{ detail.score }}鍒?/span>
              </div>
              <div class="question-content">{{ detail.content }}</div>
              <div class="answer-row">
                <div class="answer-item">
                  <span class="answer-label">閹銊ф畱缁涙梹锟介敍?/span>
                  <span class="answer-value" :class="{ 'correct': detail.isCorrect, 'wrong': !detail.isCorrect }">
                    {{ detail.userAnswer || '鏈燂拷缍旂粵? }}
                  </span>
                </div>
                <div class="answer-item">
                  <span class="answer-label">姝锝団樼粵鏃锟介敍?/span>
                  <span class="answer-value correct">{{ detail.correctAnswer }}</span>
                </div>
              </div>
              <div v-if="detail.analysis" class="question-analysis">
                <el-icon><InfoFilled /></el-icon>
                <span>{{ detail.analysis }}</span>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 瀛︿範寤洪缚锟 -->
        <div v-if="suggestions.length > 0" class="result-section">
          <h3 class="section-title">
            <el-icon><InfoFilled /></el-icon>
            瀛︿範寤洪缚锟
          </h3>
          <div class="suggestion-list">
            <div
              v-for="(suggestion, index) in suggestions"
              :key="index"
              class="suggestion-item"
            >
              <div class="suggestion-icon">{{ index + 1 }}</div>
              <div class="suggestion-content">{{ suggestion }}</div>
            </div>
          </div>
        </div>
        
        <!-- 閹垮秳缍旀寜閽 -->
        <div class="result-actions">
          <el-button type="primary" size="large" @click="viewReport">
            <el-icon><DocumentChecked /></el-icon>
            鏌ャ儳婀呰凤妇绮忛幎銉ユ啞
          </el-button>
          <el-button size="large" @click="retakeAssessment">
            <el-icon><RefreshRight /></el-icon>
            閲嶆柊濞村鐦
          </el-button>
          <el-button size="large" @click="goBack">
            <el-icon><HomeFilled /></el-icon>
            杩涙柨娲栬瘎浼颁笁锟界妇
          </el-button>
        </div>
      </template>
      
      <!-- 闁挎瑨锟界姸鎬佲偓?-->
      <div v-else class="error-wrapper">
        <el-empty description="鍔犺浇缁鎾寸亯澶辫触">
          <el-button type="primary" @click="loadResult">閲嶆柊鍔犺浇</el-button>
        </el-empty>
      </div>
    </div>
  </div>
</template>

<script>
import { Timer, Calendar, DataAnalysis, Document, InfoFilled, Opportunity, DocumentChecked, RefreshRight, HomeFilled } from '@element-plus/icons-vue'
import * as echarts from 'echarts'

export default {
  name: 'AssessmentResult',
  components: { Timer, Calendar, DataAnalysis, Document, InfoFilled, Opportunity, DocumentChecked, RefreshRight, HomeFilled },
  data() {
    return {
      userId: 1,
      userAssessmentId: null,
      loading: true,
      result: null,
      capabilityChart: null,
      capabilityList: [
        { name: '閫夋槒绶鎬绘繄娣', score: 75 },
        { name: '璇凤拷鈻堢悰銊ㄦ彧', score: 80 },
        { name: '鏁版澘锟借繘鎰鐣', score: 70 },
        { name: '鐭ヨ瘑璁版澘绻', score: 85 },
        { name: '鍒嗘瀽鑳藉姏', score: 78 },
        { name: '鍒犳稒鏌婃绘繄娣', score: 72 }
      ],
      suggestions: [
        '鍔熺姴宸遍崺铏癸拷鐭ヨ瘑閻ㄥ嫬锟芥稊鐙呯礉寤洪缚锟藉儚姘锟芥稊鐘垫祲鍏铏鏆鏉熸劕鍞村畾?,
        '鎻愭劙鐝鐟欙綁锟介夌喎瀹抽敍灞界紦璁帮拷绻樼悰宀勬烘棩鍓佺矊娑?,
        '濞夈劑鍣搁柨娆擄拷鏁板告倞閿涘苯缂撶粩瀣╅嚋浜烘椽鏁婃０妯绘拱',
        '澶氬弬鍔犳ā鎷熸祴璇曟洩绱濈啛鎮夎冭瘯鑺傚忥拷'
      ]
    }
  },
  computed: {
    scorePercentage() {
      if (!this.result || !this.result.totalScore) return 0
      return Math.round((this.result.score / this.result.totalScore) * 100)
    },
    answerDetails() {
      return this.result?.answerDetails || []
    },
    correctCount() {
      return this.answerDetails.filter(d => d.isCorrect).length
    },
    wrongCount() {
      return this.answerDetails.filter(d => !d.isCorrect && d.userAnswer).length
    },
    unansweredCount() {
      return this.answerDetails.filter(d => !d.userAnswer).length
    }
  },
  mounted() {
    this.userAssessmentId = this.$route.params.userAssessmentId
    if (!this.userAssessmentId) {
      this.$message.error('鍙栧倹鏆熼柨娆掞拷')
      this.$router.push('/assessment')
      return
    }
    this.loadResult()
  },
  beforeUnmount() {
    if (this.capabilityChart) {
      this.capabilityChart.dispose()
    }
  },
  methods: {
    async loadResult() {
      this.loading = true
      try {
        const response = await fetch(/assessments/report/${this.userAssessmentId}`)
        const data = await response.json()
        if (data.code === 200 && data.data) {
          this.result = data.data
          this.$nextTick(() => {
            this.initCapabilityChart()
          })
        } else {
          this.$message.error(data.message || '鍔犺浇缁鎾寸亯澶辫触')
        }
      } catch (error) {
        console.error('鍔犺浇缁鎾寸亯澶辫触:', error)
        this.$message.error('缂冩垹绮堕柨娆掞拷閿涘矁锟界粙宥呮倵闁插秷鐦')
      } finally {
        this.loading = false
      }
    },
    initCapabilityChart() {
      const chartDom = this.$refs.capabilityChart
      if (!chartDom) return
      
      this.capabilityChart = echarts.init(chartDom)
      
      const option = {
        radar: {
          indicator: this.capabilityList.map(c => ({
            name: c.name,
            max: 100
          })),
          radius: '65%',
          axisName: {
            color: '#606266',
            fontSize: 12
          }
        },
        series: [{
          type: 'radar',
          data: [{
            value: this.capabilityList.map(c => c.score),
            name: '鑳藉姏璇勪及',
            areaStyle: {
              color: this.result?.isPassed 
                ? 'rgba(103, 194, 58, 0.3)' 
                : 'rgba(245, 108, 108, 0.3)'
            },
            lineStyle: {
              color: this.result?.isPassed ? '#67C23A' : '#F56C6C'
            },
            itemStyle: {
              color: this.result?.isPassed ? '#67C23A' : '#F56C6C'
            }
          }]
        }]
      }
      
      this.capabilityChart.setOption(option)
    },
    getCapabilityLevel(score) {
      if (score >= 80) return 'excellent'
      if (score >= 60) return 'good'
      return 'needs-improvement'
    },
    getCapabilityColor(score) {
      if (score >= 80) return '#67C23A'
      if (score >= 60) return '#E6A23C'
      return '#F56C6C'
    },
    getQuestionTypeLabel(type) {
      const types = {
        1: '鍗℃洟鈧澶愶拷',
        2: '鍍忔岸鈧澶愶拷',
        3: '鍒犮倖鏌囨０?,
        4: '婵夛拷鈹栨０?,
        5: '绠扁偓缁涙棃锟'
      }
      return types[type] || '鏈燂拷鐓℃０妯虹'
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
    formatTime(seconds) {
      if (!seconds) return '-'
      const mins = Math.floor(seconds / 60)
      const secs = seconds % 60
      return `${mins}鍒?{secs}缁夋妶
    },
    formatDate(dateStr) {
      if (!dateStr) return '-'
      const date = new Date(dateStr)
      return date.toLocaleString('zh-CN')
    },
    viewReport() {
      this.$message.info('璇凤妇绮忛幎銉ユ啞鍔熺喕鍏樺紑鈧鍙栨垳鑵')
    },
    retakeAssessment() {
      if (this.result?.assessmentId) {
        this.$router.push(`/assessment`)
      }
    },
    goBack() {
      this.$router.push('/assessment')
    }
  }
}
</script>

<style scoped>
.assessment-result-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 20px;
}

.result-container {
  max-width: 900px;
  margin: 0 auto;
}

.loading-wrapper,
.error-wrapper {
  background: #fff;
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.result-header {
  background: #fff;
  border-radius: 12px;
  padding: 40px;
  text-align: center;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  margin-bottom: 20px;
  border-top: 4px solid #dcdfe6;
}

.result-header.passed {
  border-top-color: #67C23A;
}

.result-header.failed {
  border-top-color: #F56C6C;
}

.result-icon {
  font-size: 64px;
  margin-bottom: 16px;
}

.result-title {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}

.result-header.passed .result-title {
  color: #67C23A;
}

.result-header.failed .result-title {
  color: #F56C6C;
}

.result-subtitle {
  font-size: 16px;
  color: #909399;
  margin-bottom: 24px;
}

.score-display {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 24px;
}

.score-circle {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  background: linear-gradient(135deg, #409EFF 0%, #67C23A 100%);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: white;
  box-shadow: 0 4px 16px rgba(64, 158, 255, 0.3);
}

.score-value {
  font-size: 36px;
  font-weight: 700;
  line-height: 1;
}

.score-total {
  font-size: 16px;
  opacity: 0.9;
}

.score-percentage {
  margin-top: 12px;
  font-size: 24px;
  font-weight: 600;
  color: #409EFF;
}

.result-meta {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 24px;
  flex-wrap: wrap;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #606266;
  font-size: 14px;
}

.result-section {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  margin-bottom: 20px;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.capability-chart-wrapper {
  height: 300px;
  margin-bottom: 24px;
}

.capability-chart {
  width: 100%;
  height: 100%;
}

.capability-tags {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

.capability-tag {
  padding: 12px 16px;
  border-radius: 8px;
  background: #f5f7fa;
}

.capability-tag.excellent {
  background: #f0f9eb;
}

.capability-tag.good {
  background: #fdf6ec;
}

.capability-tag.needs-improvement {
  background: #fef0f0;
}

.tag-name {
  display: block;
  font-size: 14px;
  color: #606266;
  margin-bottom: 8px;
}

.tag-score {
  display: block;
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.answer-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  border-radius: 10px;
  background: #f5f7fa;
}

.stat-card.correct {
  background: #f0f9eb;
}

.stat-card.wrong {
  background: #fef0f0;
}

.stat-card.unanswered {
  background: #f4f4f5;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  font-weight: 600;
}

.stat-card.correct .stat-icon {
  background: #67C23A;
  color: white;
}

.stat-card.wrong .stat-icon {
  background: #F56C6C;
  color: white;
}

.stat-card.unanswered .stat-icon {
  background: #909399;
  color: white;
}

.stat-num {
  font-size: 24px;
  font-weight: 700;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.question-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.question-item {
  padding: 16px;
  border-radius: 8px;
  border: 2px solid #e4e7ed;
  transition: all 0.3s;
}

.question-item.correct {
  border-color: #67C23A;
  background: #f0f9eb;
}

.question-item.wrong {
  border-color: #F56C6C;
  background: #fef0f0;
}

.question-item.unanswered {
  border-color: #909399;
  background: #f4f4f5;
}

.question-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.question-num {
  font-weight: 600;
  color: #303133;
}

.question-score {
  margin-left: auto;
  font-size: 14px;
  color: #909399;
}

.question-content {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  margin-bottom: 12px;
}

.answer-row {
  display: flex;
  gap: 24px;
  flex-wrap: wrap;
}

.answer-item {
  font-size: 14px;
}

.answer-label {
  color: #909399;
}

.answer-value {
  font-weight: 600;
}

.answer-value.correct {
  color: #67C23A;
}

.answer-value.wrong {
  color: #F56C6C;
}

.question-analysis {
  margin-top: 12px;
  padding: 12px;
  background: #ecf5ff;
  border-radius: 6px;
  font-size: 13px;
  color: #409EFF;
  display: flex;
  align-items: flex-start;
  gap: 8px;
}

.suggestion-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.suggestion-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
}

.suggestion-icon {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: #409EFF;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 600;
  flex-shrink: 0;
}

.suggestion-content {
  flex: 1;
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
}

.result-actions {
  display: flex;
  justify-content: center;
  gap: 16px;
  padding: 20px 0;
  flex-wrap: wrap;
}

@media (max-width: 768px) {
  .result-header {
    padding: 24px;
  }
  
  .result-title {
    font-size: 22px;
  }
  
  .score-circle {
    width: 100px;
    height: 100px;
  }
  
  .score-value {
    font-size: 28px;
  }
  
  .result-meta {
    flex-direction: column;
    gap: 12px;
  }
  
  .answer-stats {
    grid-template-columns: 1fr;
  }
  
  .answer-row {
    flex-direction: column;
    gap: 8px;
  }
  
  .result-actions {
    flex-direction: column;
  }
  
  .result-actions .el-button {
    width: 100%;
  }
}
</style>
