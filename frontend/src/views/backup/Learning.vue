<template>
  <div class="learning-page">
    <div class="page-header">
      <div class="header-main">
        <div>
          <h2>学习中心</h2>
          <p class="subtitle">管理您的学习进度和记录</p>
        </div>
        <div class="header-actions">
          <el-button type="primary" @click="goToKnowledgeDashboard">
            <el-icon><DataAnalysis /></el-icon>
            知识掌握度分析
          </el-button>
        </div>
      </div>
    </div>
    
    <el-row :gutter="20">
      <!-- 学习统计 -->
      <el-col :span="24">
        <el-row :gutter="20" class="stats-row">
          <el-col :span="6">
            <el-card class="stat-card" shadow="hover">
              <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
                <el-icon><Clock /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ formatDuration(statistics.totalStudyTime) }}</div>
                <div class="stat-label">总学习时长</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="stat-card" shadow="hover">
              <div class="stat-icon" style="background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);">
                <el-icon><CircleCheck /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ statistics.completedCoursesCount || 0 }}</div>
                <div class="stat-label">已完成课程</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="stat-card" shadow="hover">
              <div class="stat-icon" style="background: linear-gradient(135deg, #fc5c7d 0%, #6a82fb 100%);">
                <el-icon><Reading /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ statistics.learningRecordsCount || 0 }}</div>
                <div class="stat-label">学习记录数</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="stat-card" shadow="hover">
              <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);">
                <el-icon><TrendCharts /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ statistics.averageProgress || 0 }}%</div>
                <div class="stat-label">平均进度</div>
              </div>
            </el-card>
          </el-col>
        </el-row>
        
        <!-- 第二行统计：本周学习时长和作业提交率 -->
        <el-row :gutter="20" class="stats-row" style="margin-top: 15px;">
          <el-col :span="12">
            <el-card class="stat-card-wide" shadow="hover">
              <div class="weekly-stats">
                <div class="weekly-header">
                  <span class="weekly-title">本周学习时长</span>
                  <span class="weekly-value">{{ weeklyStats.weeklyStudyHours || 0 }} 小时</span>
                </div>
                <el-progress 
                  :percentage="weeklyStats.completionRate || 0" 
                  :stroke-width="12"
                  :show-text="false"
                  class="weekly-progress"
                />
                <div class="weekly-target">目标: {{ weeklyStats.targetHours || 15 }} 小时/周</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card class="stat-card-wide" shadow="hover">
              <div class="assignment-stats">
                <div class="assignment-header">
                  <span class="assignment-title">作业提交率</span>
                  <span class="assignment-value">{{ statistics.assignmentSubmissionRate || 0 }}%</span>
                </div>
                <el-progress 
                  :percentage="statistics.assignmentSubmissionRate || 0" 
                  :stroke-width="12"
                  :status="getSubmissionStatus(statistics.assignmentSubmissionRate)"
                  :show-text="false"
                  class="assignment-progress"
                />
                <div class="assignment-desc">及时提交作业有助于巩固学习成果</div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" class="content-row">
      <!-- 学习记录列表 -->
      <el-col :span="16">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>📚 我的学习记录</span>
              <el-button type="primary" @click="$router.push('/courses')">
                <el-icon><Plus /></el-icon> 添加课程
              </el-button>
            </div>
          </template>
          
          <div v-if="loading" class="loading-container">
            <el-skeleton :rows="5" animated />
          </div>
          
          <div v-else-if="learningRecords.length === 0" class="empty-container">
            <el-empty description="暂无学习记录">
              <el-button type="primary" @click="$router.push('/courses')">去浏览课程</el-button>
            </el-empty>
          </div>
          
          <div v-else class="record-list">
            <el-card
              v-for="record in learningRecords"
              :key="record.recordId"
              class="record-item"
              shadow="hover"
            >
              <div class="record-main">
                <div class="record-info">
                  <div class="record-title">{{ record.courseName || `课程 #${record.courseId}` }}</div>
                  <div class="record-meta">
                    <span class="meta-item">
                      <el-icon><Timer /></el-icon>
                      学习时长: {{ formatDuration(record.studyDuration) }}
                    </span>
                    <span class="meta-item">
                      <el-icon><Calendar /></el-icon>
                      最近学习: {{ formatDate(record.lastStudyTime) }}
                    </span>
                  </div>
                </div>
                <div class="record-progress">
                  <el-progress
                    :percentage="record.progress"
                    :status="record.isCompleted ? 'success' : ''"
                    :stroke-width="10"
                  />
                  <el-tag v-if="record.isCompleted" type="success" size="small">已完成</el-tag>
                  <el-tag v-else type="warning" size="small">学习中</el-tag>
                </div>
              </div>
              <div class="record-actions">
                <el-button type="primary" size="small" @click="continueLearning(record)">
                  {{ record.isCompleted ? '复习' : '继续学习' }}
                </el-button>
                <el-button size="small" @click="updateProgress(record)">更新进度</el-button>
              </div>
            </el-card>
          </div>
          
          <!-- 分页 -->
          <div class="pagination-container" v-if="learningRecords.length > 0">
            <el-pagination
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :page-sizes="[5, 10, 20]"
              :total="total"
              layout="total, sizes, prev, pager, next"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
            />
          </div>
        </el-card>
      </el-col>
      
      <!-- 最近学习动态 -->
      <el-col :span="8">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>🕐 最近学习</span>
            </div>
          </template>
          
          <div v-if="recentRecords.length === 0" class="empty-container">
            <el-empty description="暂无最近学习记录 />
          </div>
          
          <div v-else class="recent-list">
            <div
              v-for="(record, index) in recentRecords"
              :key="index"
              class="recent-item"
            >
              <div class="recent-dot"></div>
              <div class="recent-content">
                <div class="recent-title">{{ record.courseName || `课程 #${record.courseId}` }}</div>
                <div class="recent-time">{{ formatRelativeTime(record.lastStudyTime) }}</div>
                <div class="recent-progress">
                  进度: {{ record.progress }}%
                </div>
              </div>
            </div>
          </div>
        </el-card>
        
        <!-- 学习日历 -->
        <el-card style="margin-top: 20px;">
          <template #header>
            <div class="card-header">
              <span>📅 学习日历</span>
              <el-link type="primary" @click="showFullCalendar = true">全月学习记录</el-link>
            </div>
          </template>
          <div class="learning-calendar">
            <div class="calendar-header">
              <span v-for="day in ['日', '一', '二', '三', '四', '五', '六']" :key="day" class="calendar-weekday">{{ day }}</span>
            </div>
            <div class="calendar-body">
              <div 
                v-for="(day, index) in calendarData" 
                :key="index"
                class="calendar-day"
                :class="{ 'has-study': day.hasStudy, 'today': day.isToday }"
              >
                <div class="day-number">{{ day.dayOfMonth }}</div>
                <div v-if="day.hasStudy" class="day-hours">{{ day.studyHours }}h</div>
              </div>
            </div>
          </div>
        </el-card>
        
        <!-- 学习建议 -->
        <el-card style="margin-top: 20px;">
          <template #header>
            <div class="card-header">
              <span>💡 学习建议</span>
            </div>
          </template>
          <div class="suggestions">
            <div class="suggestion-item">
              <el-icon><InfoFilled /></el-icon>
              <span>保持每天学习30分钟，效果更好</span>
            </div>
            <div class="suggestion-item">
              <el-icon><InfoFilled /></el-icon>
              <span>完成课程后记得做练习巩固</span>
            </div>
            <div class="suggestion-item">
              <el-icon><InfoFilled /></el-icon>
              <span>遇到困难可以使用AI助手</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 更新学习进度对话框 -->
    <el-dialog
      v-model="progressDialogVisible"
      title="更新学习进度"
      width="400px"
    >
      <el-form :model="progressForm" label-width="100px">
        <el-form-item label="课程">
          <span>{{ progressForm.courseName }}</span>
        </el-form-item>
        <el-form-item label="学习进度">
          <el-slider v-model="progressForm.progress" :max="100" show-stops />
        </el-form-item>
        <el-form-item label="学习时长(分)">
          <el-input-number v-model="progressForm.studyDuration" :min="0" :max="480" />
        </el-form-item>
        <el-form-item label="完成状态">
          <el-switch
            v-model="progressForm.isCompleted"
            active-text="已完成"
            inactive-text="学习中"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="progressDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveProgress" :loading="saveLoading">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { Clock, CircleCheck, Reading, TrendCharts, Plus, Timer, Calendar, InfoFilled } from '@element-plus/icons-vue'

export default {
  name: 'Learning',
  components: { Clock, CircleCheck, Reading, TrendCharts, Plus, Timer, Calendar, InfoFilled },
  data() {
    return {
      studentId: 1, // 实际应从登录状态获取
      loading: false,
      learningRecords: [],
      statistics: {},
      recentRecords: [],
      weeklyStats: {},
      calendarData: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      progressDialogVisible: false,
      saveLoading: false,
      showFullCalendar: false,
      progressForm: {
        recordId: null,
        courseId: null,
        courseName: '',
        progress: 0,
        studyDuration: 0,
        isCompleted: false
      }
    }
  },
  mounted() {
    this.loadLearningRecords()
    this.loadStatistics()
    this.loadWeeklyStats()
    this.loadCalendarData()
  },
  methods: {
    async loadLearningRecords() {
      this.loading = true
      try {
        const response = await fetch(`/learning/records/student/${this.studentId}`)
        const result = await response.json()
        if (result.code === 200 && result.data) {
          this.learningRecords = result.data
          this.total = result.data.length
          // 获取最近5条记录
          this.recentRecords = result.data.slice(0, 5)
        }
      } catch (error) {
        console.error('加载学习记录失败:', error)
        this.$message.error('加载学习记录失败')
      } finally {
        this.loading = false
      }
    },
    async loadStatistics() {
      try {
        const response = await fetch(`/learning/student/${this.studentId}/progress`)
        const result = await response.json()
        if (result.code === 200 && result.data) {
          this.statistics = result.data
        }
      } catch (error) {
        console.error('加载学习统计失败:', error)
      }
    },
    async loadWeeklyStats() {
      try {
        const response = await fetch(`/learning/student/${this.studentId}/weekly`)
        const result = await response.json()
        if (result.code === 200 && result.data) {
          this.weeklyStats = result.data
        }
      } catch (error) {
        console.error('加载本周统计失败:', error)
      }
    },
    async loadCalendarData() {
      try {
        const response = await fetch(`/learning/student/${this.studentId}/calendar`)
        const result = await response.json()
        if (result.code === 200 && result.data) {
          // 标记今天
          const today = new Date().toISOString().split('T')[0]
          this.calendarData = result.data.map(day => ({
            ...day,
            isToday: day.date === today
          }))
        }
      } catch (error) {
        console.error('加载学习日历失败:', error)
      }
    },
    getSubmissionStatus(rate) {
      if (rate >= 90) return 'success'
      if (rate >= 60) return ''
      return 'exception'
    },
    formatDuration(minutes) {
      if (!minutes) return '0分钟'
      if (minutes < 60) return `${minutes}分钟`
      const hours = Math.floor(minutes / 60)
      const mins = minutes % 60
      if (mins === 0) return `${hours}小时`
      return `${hours}小时${mins}分钟`
    },
    formatDate(dateStr) {
      if (!dateStr) return '-'
      const date = new Date(dateStr)
      return date.toLocaleString('zh-CN')
    },
    formatRelativeTime(dateStr) {
      if (!dateStr) return '-'
      const date = new Date(dateStr)
      const now = new Date()
      const diff = now - date
      const minutes = Math.floor(diff / 60000)
      const hours = Math.floor(diff / 3600000)
      const days = Math.floor(diff / 86400000)
      
      if (minutes < 1) return '刚刚'
      if (minutes < 60) return `${minutes}分钟前`
      if (hours < 24) return `${hours}小时前`
      if (days < 7) return `${days}天前`
      return date.toLocaleDateString('zh-CN')
    },
    continueLearning(record) {
      this.$message.info(`继续学习: ${record.courseName || '课程 #' + record.courseId}`)
      // 可以跳转到课程详情页
      // this.$router.push(`/course/${record.courseId}/learn`)
    },
    updateProgress(record) {
      this.progressForm = {
        recordId: record.recordId,
        courseId: record.courseId,
        courseName: record.courseName || `课程 #${record.courseId}`,
        progress: record.progress || 0,
        studyDuration: 0, // 新增学习时长
        isCompleted: record.isCompleted || false
      }
      this.progressDialogVisible = true
    },
    async saveProgress() {
      this.saveLoading = true
      try {
        const recordData = {
          recordId: this.progressForm.recordId,
          studentId: this.studentId,
          courseId: this.progressForm.courseId,
          progress: this.progressForm.progress,
          studyDuration: this.progressForm.studyDuration,
          isCompleted: this.progressForm.isCompleted,
          lastStudyTime: new Date().toISOString()
        }
        
        const response = await fetch('/learning/record', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(recordData)
        })
        const result = await response.json()
        
        if (result.code === 200) {
          this.$message.success('学习进度更新成功')
          this.progressDialogVisible = false
          // 重新加载数据
          this.loadLearningRecords()
          this.loadStatistics()
        } else {
          this.$message.error(result.message || '更新失败')
        }
      } catch (error) {
        console.error('更新学习进度失败:', error)
        this.$message.error('更新学习进度失败')
      } finally {
        this.saveLoading = false
      }
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.currentPage = 1
    },
    handleCurrentChange(val) {
      this.currentPage = val
    },
    goToKnowledgeDashboard() {
      this.$router.push('/knowledge')
    }
  }
}
</script>

<style scoped>
.page-header {
  margin-bottom: 20px;
}

.header-main {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 15px;
}

.page-header h2 {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 5px;
}

.subtitle {
  color: #909399;
  font-size: 14px;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card :deep(.el-card__body) {
  display: flex;
  align-items: center;
  padding: 20px;
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
}

.stat-icon .el-icon {
  font-size: 24px;
  color: #fff;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 22px;
  font-weight: 700;
  color: #303133;
  line-height: 1.2;
}

.stat-label {
  font-size: 13px;
  color: #909399;
  margin-top: 4px;
}

.content-row {
  margin-top: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
}

.loading-container,
.empty-container {
  padding: 40px 0;
}

.record-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.record-item :deep(.el-card__body) {
  padding: 20px;
}

.record-main {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.record-info {
  flex: 1;
}

.record-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}

.record-meta {
  display: flex;
  gap: 20px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 13px;
  color: #909399;
}

.record-progress {
  width: 200px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.record-actions {
  display: flex;
  justify-content: flex-end;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}

.recent-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.recent-item {
  display: flex;
  gap: 12px;
  padding-bottom: 15px;
  border-bottom: 1px solid #ebeef5;
}

.recent-item:last-child {
  border-bottom: none;
  padding-bottom: 0;
}

.recent-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: #409EFF;
  margin-top: 5px;
  flex-shrink: 0;
}

.recent-content {
  flex: 1;
}

.recent-title {
  font-size: 14px;
  color: #303133;
  margin-bottom: 4px;
}

.recent-time {
  font-size: 12px;
  color: #909399;
  margin-bottom: 4px;
}

.recent-progress {
  font-size: 12px;
  color: #409EFF;
}

.suggestions {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.suggestion-item {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  font-size: 13px;
  color: #606266;
  line-height: 1.5;
}

.suggestion-item .el-icon {
  color: #e6a23c;
  margin-top: 2px;
}

/* 统计卡片宽版 */
.stat-card-wide :deep(.el-card__body) {
  padding: 20px;
}

.weekly-stats, .assignment-stats {
  width: 100%;
}

.weekly-header, .assignment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.weekly-title, .assignment-title {
  font-size: 14px;
  color: #606266;
}

.weekly-value, .assignment-value {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.weekly-progress, .assignment-progress {
  margin-bottom: 8px;
}

.weekly-target, .assignment-desc {
  font-size: 12px;
  color: #909399;
}

/* 学习日历样式 */
.learning-calendar {
  padding: 10px 0;
}

.calendar-header {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 8px;
  margin-bottom: 10px;
}

.calendar-weekday {
  text-align: center;
  font-size: 13px;
  color: #909399;
  padding: 8px 0;
}

.calendar-body {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 8px;
}

.calendar-day {
  aspect-ratio: 1;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  transition: all 0.3s;
  cursor: pointer;
}

.calendar-day:hover {
  background: #e4e7ed;
}

.calendar-day.has-study {
  background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
  color: #fff;
}

.calendar-day.today {
  box-shadow: 0 0 0 2px #409eff;
}

.day-number {
  font-size: 14px;
  font-weight: 500;
}

.day-hours {
  font-size: 11px;
  margin-top: 2px;
  opacity: 0.9;
}
</style>
