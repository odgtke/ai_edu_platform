<template>
  <div class="assessment-page">
    <div class="page-header">
      <div class="header-content">
        <div>
          <h2>鑳藉姏璇勪及</h2>
          <p class="subtitle">娴嬭瘯鍜屾彁鍗囨偍鐨勫︿範鑳藉?/p>
        </div>
        <div class="header-stats">
          <div class="quick-stat">
            <div class="quick-stat-value">{{ statistics.totalCount || 0 }}</div>
            <div class="quick-stat-label">宸插畬鎴愯瘎浼?/div>
          </div>
          <div class="quick-stat">
            <div class="quick-stat-value">{{ statistics.passRate || 0 }}%</div>
            <div class="quick-stat-label">閫氳繃鐜?/div>
          </div>
        </div>
      </div>
    </div>
    
    <el-row :gutter="20">
      <!-- 鍙鍙傚姞鐨勮瘎浼板垪琛 -->
      <el-col :span="16">
        <el-card class="main-card">
          <template #header>
            <div class="card-header">
              <span class="header-title">馃搵 鍙鍙傚姞鐨勮瘎浼</span>
              <el-radio-group v-model="filterType" size="small">
                <el-radio-button label="all">鍏ㄩ儴</el-radio-button>
                <el-radio-button label="subject">鎸夊︾?/el-radio-button>
                <el-radio-button label="grade">鎸夊勾绾?/el-radio-button>
              </el-radio-group>
            </div>
          </template>
          
          <!-- 绛涢夋爣绛?-->
          <div v-if="filterType !== 'all'" class="filter-tags">
            <el-tag
              v-for="tag in filterOptions"
              :key="tag.value"
              :type="selectedFilter === tag.value ? 'primary' : 'info'"
              class="filter-tag"
              @click="selectedFilter = tag.value"
            >
              {{ tag.label }}
            </el-tag>
          </div>
          
          <div v-if="loading" class="loading-container">
            <el-skeleton :rows="5" animated />
          </div>
          
          <div v-else-if="filteredAssessments.length === 0" class="empty-container">
            <el-empty description="鏆傛棤鍙鍙傚姞鐨勮瘎浼">
              <template #image>
                <div class="empty-icon">馃摎</div>
              </template>
            </el-empty>
          </div>
          
          <div v-else class="assessment-list">
            <el-card
              v-for="item in filteredAssessments"
              :key="item.assessmentId"
              class="assessment-item"
              shadow="hover"
            >
              <div class="assessment-info">
                <div class="assessment-header">
                  <div class="assessment-title">{{ item.assessmentName }}</div>
                  <el-tag v-if="item.isNew" type="success" size="small" effect="dark">NEW</el-tag>
                </div>
                <div class="assessment-meta">
                  <el-tag size="small" type="info">{{ getSubjectName(item.subjectId) }}</el-tag>
                  <el-tag size="small" type="warning">{{ getGradeLabel(item.gradeLevel) }}</el-tag>
                  <el-tag size="small" :type="getDifficultyType(item.difficulty)">
                    {{ getDifficultyLabel(item.difficulty) }}
                  </el-tag>
                  <span class="meta-item">
                    <el-icon><Timer /></el-icon>
                    {{ item.duration }}鍒嗛挓
                  </span>
                  <span class="meta-item">
                    <el-icon><Document /></el-icon>
                    {{ item.totalQuestions }}棰?
                  </span>
                  <span class="meta-item">
                    <el-icon><Trophy /></el-icon>
                    鍙婃牸鍒? {{ item.passScore }}
                  </span>
                </div>
                <div class="assessment-desc" v-if="item.description">
                  {{ item.description }}
                </div>
                <div class="assessment-progress" v-if="item.userProgress">
                  <el-progress 
                    :percentage="item.userProgress" 
                    :status="item.userProgress >= 100 ? 'success' : ''"
                    :stroke-width="8"
                  />
                  <span class="progress-text">涓婃″仛鍒扮?{{ item.lastQuestion }} 棰?/span>
                </div>
              </div>
              <div class="assessment-action">
                <el-button 
                  :type="item.userProgress > 0 ? 'warning' : 'primary'" 
                  @click="startAssessment(item)"
                >
                  {{ item.userProgress > 0 ? '缁х画娴嬭瘎' : '寮濮嬫祴璇? }}
                </el-button>
                <el-button text @click="viewAssessmentDetail(item)">
                  璇︽儏
                </el-button>
              </div>
            </el-card>
          </div>
        </el-card>
        
        <!-- 鑳藉姏闆疯揪鍥?-->
        <el-card class="capability-card" style="margin-top: 20px;">
          <template #header>
            <div class="card-header">
              <span>馃幆 鎴戠殑鑳藉姏鍒嗘瀽</span>
              <el-link type="primary" @click="refreshCapabilityData">鍒锋柊</el-link>
            </div>
          </template>
          <div ref="capabilityChart" class="capability-chart"></div>
        </el-card>
      </el-col>
      
      <!-- 璇勪及鍘嗗彶鍜岀粺璁?-->
      <el-col :span="8">
        <el-card class="side-card">
          <template #header>
            <div class="card-header">
              <span>馃搳 鏈杩戣瘎浼?/span>
              <el-link type="primary" @click="viewAllHistory">鍏ㄩ儴</el-link>
            </div>
          </template>
          
          <div v-if="historyLoading" class="loading-container">
            <el-skeleton :rows="3" animated />
          </div>
          
          <div v-else-if="history.length === 0" class="empty-container">
            <el-empty description="鏆傛棤璇勪及璁板綍" :image-size="80" />
          </div>
          
          <div v-else class="history-list">
            <div
              v-for="(record, index) in history.slice(0, 5)"
              :key="index"
              class="history-item"
              @click="viewHistoryDetail(record)"
            >
              <div class="history-info">
                <div class="history-title">{{ record.assessmentName }}</div>
                <div class="history-meta">
                  <span>{{ getSubjectName(record.subjectId) }}</span>
                  <span class="dot">路</span>
                  <span>{{ formatDate(record.completeTime) }}</span>
                </div>
              </div>
              <div class="history-score-section">
                <el-progress
                  type="circle"
                  :percentage="Math.round(record.scorePercentage || (record.score / record.totalScore * 100))"
                  :status="record.isPassed ? 'success' : 'exception'"
                  :width="50"
                  :stroke-width="4"
                />
              </div>
            </div>
          </div>
        </el-card>
        
        <!-- 缁熻′俊鎭 -->
        <el-card class="side-card" style="margin-top: 20px;">
          <template #header>
            <div class="card-header">
              <span>馃搱 璇勪及缁熻</span>
            </div>
          </template>
          <div class="statistics">
            <div class="stat-item">
              <div class="stat-value">{{ statistics.totalCount || 0 }}</div>
              <div class="stat-label">鎬绘祴璇勬℃?/div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ statistics.passCount || 0 }}</div>
              <div class="stat-label">閫氳繃娆℃暟</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ formatScore(statistics.averageScore) }}</div>
              <div class="stat-label">骞冲潎鍒?/div>
            </div>
          </div>
          <div class="pass-rate-section" v-if="statistics.passRate !== undefined">
            <div class="pass-rate-label">鎬讳綋閫氳繃鐜?/div>
            <el-progress 
              :percentage="statistics.passRate" 
              :color="getPassRateColor"
              :stroke-width="12"
              striped
            />
          </div>
        </el-card>
        
        <!-- 瀛︾戝垎甯 -->
        <el-card class="side-card" style="margin-top: 20px;">
          <template #header>
            <div class="card-header">
              <span>馃摎 瀛︾戝垎甯</span>
            </div>
          </template>
          <div ref="subjectChart" class="subject-chart"></div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 寮濮嬭瘎浼板硅瘽妗 -->
    <el-dialog
      v-model="startDialogVisible"
      title="寮濮嬭瘎浼?
      width="500px"
      :close-on-click-modal="false"
    >
      <div v-if="selectedAssessment" class="start-dialog-content">
        <div class="dialog-icon">馃摑</div>
        <h3>{{ selectedAssessment.assessmentName }}</h3>
        <p class="dialog-desc">{{ selectedAssessment.description || '璇疯ょ湡绛旈橈紝娴嬭瘯鎮ㄧ殑鑳藉姏姘村钩銆? }}</p>
        <div class="dialog-info">
          <div class="info-item">
            <span class="info-label">棰樼洰鏁伴噺锛?/span>
            <span class="info-value">{{ selectedAssessment.totalQuestions }} 棰?/span>
          </div>
          <div class="info-item">
            <span class="info-label">鑰冭瘯鏃堕棿锛?/span>
            <span class="info-value">{{ selectedAssessment.duration }} 鍒嗛挓</span>
          </div>
          <div class="info-item">
            <span class="info-label">鍙婃牸鍒嗘暟锛?/span>
            <span class="info-value">{{ selectedAssessment.passScore }} 鍒?/span>
          </div>
          <div class="info-item">
            <span class="info-label">璇勪及绫诲瀷锛?/span>
            <span class="info-value">{{ getAssessmentTypeLabel(selectedAssessment.assessmentType) }}</span>
          </div>
        </div>
        <el-alert
          v-if="selectedAssessment.userProgress > 0"
          title="鎮ㄤ笂娆″凡瀹屾垚閮ㄥ垎棰樼洰锛岀偣鍑荤户缁灏嗕粠涓婃¤繘搴﹀紑濮?
          type="warning"
          :closable="false"
          show-icon
          style="margin-top: 15px;"
        />
      </div>
      <template #footer>
        <el-button @click="startDialogVisible = false">鍙栨秷</el-button>
        <el-button type="primary" @click="confirmStart" :loading="starting">
          {{ selectedAssessment?.userProgress > 0 ? '缁х画娴嬭瘎' : '寮濮嬫祴璇? }}
        </el-button>
      </template>
    </el-dialog>
    
    <!-- 璇勪及璇︽儏瀵硅瘽妗?-->
    <el-dialog
      v-model="detailDialogVisible"
      title="璇勪及璇︽儏"
      width="600px"
    >
      <div v-if="selectedAssessment" class="detail-dialog-content">
        <div class="detail-header">
          <h3>{{ selectedAssessment.assessmentName }}</h3>
          <el-tag :type="selectedAssessment.status === 1 ? 'success' : 'info'">
            {{ selectedAssessment.status === 1 ? '杩涜屼? : '宸茬粨鏉? }}
          </el-tag>
        </div>
        <div class="detail-section">
          <h4>璇勪及淇℃伅</h4>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="瀛︾">{{ getSubjectName(selectedAssessment.subjectId) }}</el-descriptions-item>
            <el-descriptions-item label="骞寸骇">{{ getGradeLabel(selectedAssessment.gradeLevel) }}</el-descriptions-item>
            <el-descriptions-item label="鏃堕暱">{{ selectedAssessment.duration }} 鍒嗛挓</el-descriptions-item>
            <el-descriptions-item label="棰樻暟">{{ selectedAssessment.totalQuestions }} 棰?/el-descriptions-item>
            <el-descriptions-item label="鎬诲垎">{{ selectedAssessment.totalScore }} 鍒?/el-descriptions-item>
            <el-descriptions-item label="鍙婃牸鍒?>{{ selectedAssessment.passScore }} 鍒?/el-descriptions-item>
          </el-descriptions>
        </div>
        <div class="detail-section" v-if="selectedAssessment.description">
          <h4>璇勪及璇存槑</h4>
          <p class="detail-desc">{{ selectedAssessment.description }}</p>
        </div>
      </div>
    </el-dialog>
    
    <!-- 鍘嗗彶璇︽儏瀵硅瘽妗?-->
    <el-dialog
      v-model="historyDialogVisible"
      title="璇勪及璇︽儏"
      width="700px"
    >
      <div v-if="selectedHistory" class="history-detail-content">
        <div class="history-detail-header">
          <h3>{{ selectedHistory.assessmentName }}</h3>
          <el-tag :type="selectedHistory.isPassed ? 'success' : 'danger'">
            {{ selectedHistory.isPassed ? '宸查氳繃' : '鏈閫氳繃' }}
          </el-tag>
        </div>
        <div class="history-detail-score">
          <div class="score-circle">
            <div class="score-value">{{ selectedHistory.score }}</div>
            <div class="score-total">/ {{ selectedHistory.totalScore }}</div>
          </div>
          <div class="score-info">
            <div class="score-percentage">
              寰楀垎鐜? {{ Math.round(selectedHistory.scorePercentage || (selectedHistory.score / selectedHistory.totalScore * 100)) }}%
            </div>
            <div class="score-time">
              鐢ㄦ椂: {{ formatTime(selectedHistory.timeSpent) }}
            </div>
            <div class="score-date">
              瀹屾垚鏃堕棿: {{ formatDateTime(selectedHistory.completeTime) }}
            </div>
          </div>
        </div>
        <div class="history-detail-actions">
          <el-button type="primary" @click="viewReport(selectedHistory)">
            鏌ョ湅鑳藉姏鍒嗘瀽鎶ュ憡
          </el-button>
          <el-button @click="retakeAssessment(selectedHistory)">
            閲嶆柊娴嬭瘎
          </el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { Timer, Document, Trophy } from '@element-plus/icons-vue'
import * as echarts from 'echarts'

export default {
  name: 'Assessment',
  components: { Timer, Document, Trophy },
  data() {
    return {
      userId: 1,
      loading: false,
      historyLoading: false,
      assessments: [],
      history: [],
      statistics: {},
      startDialogVisible: false,
      detailDialogVisible: false,
      historyDialogVisible: false,
      starting: false,
      selectedAssessment: null,
      selectedHistory: null,
      filterType: 'all',
      selectedFilter: null,
      capabilityChart: null,
      subjectChart: null,
      filterOptions: []
    }
  },
  computed: {
    filteredAssessments() {
      let result = this.assessments
      if (this.filterType === 'subject' && this.selectedFilter) {
        result = result.filter(a => a.subjectId === parseInt(this.selectedFilter))
      } else if (this.filterType === 'grade' && this.selectedFilter) {
        result = result.filter(a => a.gradeLevel === parseInt(this.selectedFilter))
      }
      return result
    },
    getPassRateColor() {
      const rate = this.statistics.passRate || 0
      if (rate >= 80) return '#67C23A'
      if (rate >= 60) return '#E6A23C'
      return '#F56C6C'
    }
  },
  watch: {
    filterType(newVal) {
      if (newVal === 'subject') {
        this.filterOptions = [
          { label: '鏁板', value: '1' },
          { label: '璇鏂', value: '2' },
          { label: '鑻辫', value: '3' },
          { label: '鐗╃悊', value: '4' },
          { label: '鍖栧', value: '5' }
        ]
      } else if (newVal === 'grade') {
        this.filterOptions = [
          { label: '涓骞寸骇', value: '1' },
          { label: '浜屽勾绾?, value: '2' },
          { label: '涓夊勾绾?, value: '3' },
          { label: '鍥涘勾绾?, value: '4' },
          { label: '浜斿勾绾?, value: '5' },
          { label: '鍏骞寸?, value: '6' }
        ]
      }
      this.selectedFilter = null
    }
  },
  mounted() {
    this.loadAssessments()
    this.loadHistory()
    this.loadStatistics()
    this.$nextTick(() => {
      this.initCharts()
    })
  },
  beforeUnmount() {
    if (this.capabilityChart) {
      this.capabilityChart.dispose()
    }
    if (this.subjectChart) {
      this.subjectChart.dispose()
    }
  },
  methods: {
    async loadAssessments() {
      this.loading = true
      try {
        const response = await fetch(/assessments/available?userId=${this.userId}`)
        const result = await response.json()
        if (result.code === 200 && result.data) {
          this.assessments = result.data.map(item => ({
            ...item,
            isNew: this.isNewAssessment(item.createTime)
          }))
        }
      } catch (error) {
        console.error('鍔犺浇璇勪及鍒楄〃澶辫触:', error)
        this.$message.error('鍔犺浇璇勪及鍒楄〃澶辫触')
      } finally {
        this.loading = false
      }
    },
    async loadHistory() {
      this.historyLoading = true
      try {
        const response = await fetch(/assessments/history/${this.userId}`)
        const result = await response.json()
        if (result.code === 200 && result.data) {
          this.history = result.data
          this.$nextTick(() => {
            this.updateSubjectChart()
          })
        }
      } catch (error) {
        console.error('鍔犺浇璇勪及鍘嗗彶澶辫触:', error)
      } finally {
        this.historyLoading = false
      }
    },
    async loadStatistics() {
      try {
        const response = await fetch(/assessments/statistics/${this.userId}`)
        const result = await response.json()
        if (result.code === 200 && result.data) {
          this.statistics = result.data
          this.$nextTick(() => {
            this.updateCapabilityChart()
          })
        }
      } catch (error) {
        console.error('鍔犺浇璇勪及缁熻″け璐:', error)
      }
    },
    isNewAssessment(createTime) {
      if (!createTime) return false
      const create = new Date(createTime)
      const now = new Date()
      const diffDays = (now - create) / (1000 * 60 * 60 * 24)
      return diffDays <= 7
    },
    getSubjectName(subjectId) {
      const subjects = { 1: '鏁板', 2: '璇鏂', 3: '鑻辫', 4: '鐗╃悊', 5: '鍖栧', 6: '鐢熺墿', 7: '鍘嗗彶', 8: '鍦扮悊' }
      return subjects[subjectId] || '缁煎悎'
    },
    getGradeLabel(grade) {
      const grades = { 1: '涓骞寸骇', 2: '浜屽勾绾?, 3: '涓夊勾绾?, 4: '鍥涘勾绾?, 5: '浜斿勾绾?, 6: '鍏骞寸?, 7: '鍒濅腑', 8: '楂樹腑' }
      return grades[grade] || '閫氱敤'
    },
    getDifficultyType(difficulty) {
      const types = { 1: 'success', 2: 'warning', 3: 'danger' }
      return types[difficulty] || 'info'
    },
    getDifficultyLabel(difficulty) {
      const labels = { 1: '绠鍗?, 2: '涓绛', 3: '鍥伴毦' }
      return labels[difficulty] || '鏅閫?
    },
    getAssessmentTypeLabel(type) {
      const types = { 1: '鍏ュ︽祴璇', 2: '闃舵垫ф祴璇?, 3: '鏈熸湯娴嬭瘯', 4: '涓撻」鑳藉姏娴嬭瘯' }
      return types[type] || '鏅閫氭祴璇?
    },
    formatDate(dateStr) {
      if (!dateStr) return '-'
      const date = new Date(dateStr)
      return date.toLocaleDateString('zh-CN')
    },
    formatDateTime(dateStr) {
      if (!dateStr) return '-'
      const date = new Date(dateStr)
      return date.toLocaleString('zh-CN')
    },
    formatTime(seconds) {
      if (!seconds) return '-'
      const mins = Math.floor(seconds / 60)
      const secs = seconds % 60
      return `${mins}鍒?{secs}绉抈
    },
    formatScore(score) {
      if (score === undefined || score === null) return '0'
      return parseFloat(score).toFixed(1)
    },
    startAssessment(assessment) {
      this.selectedAssessment = assessment
      this.startDialogVisible = true
    },
    viewAssessmentDetail(assessment) {
      this.selectedAssessment = assessment
      this.detailDialogVisible = true
    },
    viewHistoryDetail(record) {
      this.selectedHistory = record
      this.historyDialogVisible = true
    },
    async confirmStart() {
      if (!this.selectedAssessment) return
      
      this.starting = true
      try {
        const response = await fetch(
          /assessments/${this.selectedAssessment.assessmentId}/start?userId=${this.userId}`,
          { method: 'POST' }
        )
        const result = await response.json()
        if (result.code === 200 && result.data) {
          this.$message.success('璇勪及寮濮嬶紒')
          this.startDialogVisible = false
          this.$router.push(`/assessment/take/${result.data.userAssessmentId}`)
        } else {
          this.$message.error(result.message || '寮濮嬭瘎浼板け璐?)
        }
      } catch (error) {
        console.error('寮濮嬭瘎浼板け璐?', error)
        this.$message.error('寮濮嬭瘎浼板け璐?)
      } finally {
        this.starting = false
      }
    },
    viewAllHistory() {
      this.$router.push('/assessment/history')
    },
    viewReport(history) {
      this.$router.push(`/assessment/report/${history.resultId}`)
    },
    retakeAssessment(history) {
      const assessment = this.assessments.find(a => a.assessmentId === history.assessmentId)
      if (assessment) {
        this.startAssessment(assessment)
      } else {
        this.$message.warning('璇ヨ瘎浼板綋鍓嶄笉鍙鍙傚?)
      }
    },
    initCharts() {
      this.initCapabilityChart()
      this.initSubjectChart()
    },
    initCapabilityChart() {
      const chartDom = this.$refs.capabilityChart
      if (!chartDom) return
      this.capabilityChart = echarts.init(chartDom)
      this.updateCapabilityChart()
    },
    initSubjectChart() {
      const chartDom = this.$refs.subjectChart
      if (!chartDom) return
      this.subjectChart = echarts.init(chartDom)
      this.updateSubjectChart()
    },
    updateCapabilityChart() {
      if (!this.capabilityChart) return
      
      const option = {
        radar: {
          indicator: [
            { name: '閫昏緫鎬濈淮', max: 100 },
            { name: '璇瑷琛ㄨ揪', max: 100 },
            { name: '鏁板﹁繍绠', max: 100 },
            { name: '鐭ヨ瘑璁板繂', max: 100 },
            { name: '鍒嗘瀽鑳藉姏', max: 100 },
            { name: '鍒涙柊鎬濈淮', max: 100 }
          ],
          radius: '65%',
          axisName: {
            color: '#606266'
          }
        },
        series: [{
          type: 'radar',
          data: [{
            value: [
              this.statistics.logicScore || 75,
              this.statistics.languageScore || 80,
              this.statistics.mathScore || 70,
              this.statistics.memoryScore || 85,
              this.statistics.analysisScore || 78,
              this.statistics.creativeScore || 72
            ],
            name: '鑳藉姏璇勪及',
            areaStyle: {
              color: 'rgba(64, 158, 255, 0.3)'
            },
            lineStyle: {
              color: '#409EFF'
            },
            itemStyle: {
              color: '#409EFF'
            }
          }]
        }]
      }
      this.capabilityChart.setOption(option)
    },
    updateSubjectChart() {
      if (!this.subjectChart) return
      
      const subjectCount = {}
      this.history.forEach(h => {
        const subject = this.getSubjectName(h.subjectId)
        subjectCount[subject] = (subjectCount[subject] || 0) + 1
      })
      
      const data = Object.entries(subjectCount).map(([name, value]) => ({ name, value }))
      
      const option = {
        tooltip: {
          trigger: 'item'
        },
        series: [{
          type: 'pie',
          radius: ['40%', '70%'],
          avoidLabelOverlap: false,
          itemStyle: {
            borderRadius: 10,
            borderColor: '#fff',
            borderWidth: 2
          },
          label: {
            show: true,
            formatter: '{b}: {c}'
          },
          data: data.length > 0 ? data : [
            { name: '鏁板', value: 3 },
            { name: '璇鏂', value: 2 },
            { name: '鑻辫', value: 2 }
          ]
        }]
      }
      this.subjectChart.setOption(option)
    },
    refreshCapabilityData() {
      this.loadStatistics()
      this.$message.success('鏁版嵁宸插埛鏂?)
    }
  }
}
</script>

<style scoped>
.assessment-page {
  padding: 20px;
}

.page-header {
  margin-bottom: 24px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.page-header h2 {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}

.subtitle {
  color: #909399;
  font-size: 14px;
}

.header-stats {
  display: flex;
  gap: 32px;
}

.quick-stat {
  text-align: center;
}

.quick-stat-value {
  font-size: 32px;
  font-weight: 700;
  color: #409EFF;
  line-height: 1;
}

.quick-stat-label {
  font-size: 13px;
  color: #909399;
  margin-top: 4px;
}

.main-card,
.side-card {
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  color: #303133;
}

.header-title {
  font-size: 16px;
}

.filter-tags {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid #ebeef5;
}

.filter-tag {
  cursor: pointer;
  transition: all 0.3s;
}

.loading-container,
.empty-container {
  padding: 40px 0;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 16px;
}

.assessment-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.assessment-item {
  border-radius: 10px;
  transition: all 0.3s;
}

.assessment-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.assessment-item :deep(.el-card__body) {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
}

.assessment-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.assessment-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.assessment-meta {
  display: flex;
  gap: 8px;
  align-items: center;
  flex-wrap: wrap;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #606266;
}

.assessment-desc {
  margin-top: 10px;
  font-size: 13px;
  color: #909399;
  line-height: 1.5;
}

.assessment-progress {
  margin-top: 12px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.progress-text {
  font-size: 12px;
  color: #909399;
  white-space: nowrap;
}

.assessment-action {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.history-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.history-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.history-item:hover {
  background: #f5f7fa;
}

.history-info {
  flex: 1;
}

.history-title {
  font-size: 14px;
  color: #303133;
  margin-bottom: 6px;
  font-weight: 500;
}

.history-meta {
  font-size: 12px;
  color: #909399;
  display: flex;
  align-items: center;
  gap: 6px;
}

.dot {
  color: #c0c4cc;
}

.history-score-section {
  margin-left: 12px;
}

.statistics {
  display: flex;
  justify-content: space-around;
  text-align: center;
  margin-bottom: 20px;
}

.stat-item {
  padding: 10px;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #409EFF;
  margin-bottom: 6px;
}

.stat-label {
  font-size: 12px;
  color: #909399;
}

.pass-rate-section {
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
}

.pass-rate-label {
  font-size: 13px;
  color: #606266;
  margin-bottom: 10px;
}

.capability-chart,
.subject-chart {
  height: 280px;
  width: 100%;
}

/* 瀵硅瘽妗嗘牱寮?*/
.start-dialog-content {
  text-align: center;
}

.dialog-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.start-dialog-content h3 {
  margin-bottom: 12px;
  color: #303133;
  font-size: 20px;
}

.dialog-desc {
  color: #606266;
  margin-bottom: 24px;
  line-height: 1.6;
  font-size: 14px;
}

.dialog-info {
  background: #f5f7fa;
  padding: 20px;
  border-radius: 10px;
  text-align: left;
}

.info-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
}

.info-item:last-child {
  margin-bottom: 0;
}

.info-label {
  color: #909399;
  font-size: 14px;
}

.info-value {
  color: #303133;
  font-weight: 600;
  font-size: 14px;
}

/* 璇︽儏瀵硅瘽妗嗘牱寮?*/
.detail-dialog-content,
.history-detail-content {
  padding: 10px 0;
}

.detail-header,
.history-detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.detail-header h3,
.history-detail-header h3 {
  font-size: 20px;
  color: #303133;
  margin: 0;
}

.detail-section {
  margin-bottom: 24px;
}

.detail-section h4 {
  font-size: 16px;
  color: #303133;
  margin-bottom: 12px;
  font-weight: 600;
}

.detail-desc {
  color: #606266;
  line-height: 1.8;
  font-size: 14px;
}

/* 鍘嗗彶璇︽儏鍒嗘暟鏍峰紡 */
.history-detail-score {
  display: flex;
  align-items: center;
  gap: 32px;
  padding: 24px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e7ed 100%);
  border-radius: 12px;
  margin-bottom: 24px;
}

.score-circle {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background: linear-gradient(135deg, #409EFF 0%, #67C23A 100%);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: white;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

.score-value {
  font-size: 32px;
  font-weight: 700;
  line-height: 1;
}

.score-total {
  font-size: 14px;
  opacity: 0.9;
}

.score-info {
  flex: 1;
}

.score-percentage {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}

.score-time,
.score-date {
  font-size: 14px;
  color: #606266;
  margin-bottom: 4px;
}

.history-detail-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
}

.view-all {
  text-align: center;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
}
</style>
