<template>
  <div class="assessment">
    <!-- 页面标题 -->
    <div class="page-header-modern">
      <div class="header-content">
        <div class="title-badge">
          <el-icon class="badge-icon"><TrendCharts /></el-icon>
        </div>
        <div class="title-text">
          <h1 class="page-title">能力评估</h1>
          <p class="page-subtitle">测试和提升您的学习能力，跟踪学习进度</p>
        </div>
      </div>
      <div class="header-stats">
        <div class="stat-item">
          <span class="stat-value">{{ assessments.length }}</span>
          <span class="stat-label">评估总数</span>
        </div>
        <div class="stat-divider"></div>
        <div class="stat-item">
          <span class="stat-value">{{ completedCount }}</span>
          <span class="stat-label">已完成</span>
        </div>
        <div class="stat-divider"></div>
        <div class="stat-item">
          <span class="stat-value">{{ avgScore }}%</span>
          <span class="stat-label">平均分</span>
        </div>
      </div>
      <div class="header-actions">
        <el-button type="primary" class="btn-add" @click="showAddDialog">
          <el-icon><Plus /></el-icon>
          创建评估
        </el-button>
      </div>
    </div>

    <!-- 能力雷达图 -->
    <div class="radar-section">
      <div class="section-header">
        <h2 class="section-title">
          <el-icon><DataAnalysis /></el-icon>
          能力分析
        </h2>
      </div>
      <div class="radar-container">
        <div ref="radarChart" class="radar-chart"></div>
        <div class="ability-list">
          <div v-for="(item, index) in abilityList" :key="index" class="ability-item">
            <div class="ability-name">{{ item.name }}</div>
            <div class="ability-bar">
              <div class="ability-progress" :style="{ width: item.score + '%', background: item.color }"></div>
            </div>
            <div class="ability-score">{{ item.score }}分</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 评估列表 -->
    <div class="assessment-section">
      <div class="section-header">
        <h2 class="section-title">
          <el-icon><DocumentChecked /></el-icon>
          评估列表
        </h2>
        <div class="filter-group">
          <el-select v-model="filterCourse" placeholder="全部课程" clearable @change="handleFilter">
            <el-option
              v-for="course in courseList"
              :key="course.courseId"
              :label="course.courseName"
              :value="course.courseId"
            />
          </el-select>
          <el-select v-model="filterStatus" placeholder="全部状态" clearable @change="handleFilter">
            <el-option label="未开始" value="pending" />
            <el-option label="进行中" value="progress" />
            <el-option label="已完成" value="completed" />
          </el-select>
        </div>
      </div>

      <!-- 加载状态 -->
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="3" animated />
      </div>

      <!-- 空状态 -->
      <div v-else-if="filteredAssessments.length === 0" class="empty-container">
        <el-empty description="暂无评估">
          <el-button type="primary" @click="showAddDialog">创建评估</el-button>
        </el-empty>
      </div>

      <!-- 评估卡片列表 -->
      <div v-else class="assessment-grid">
        <el-card
          v-for="item in filteredAssessments"
          :key="item.assessmentId"
          class="assessment-card"
          shadow="hover"
        >
          <div class="card-header">
            <div class="assessment-status" :class="getStatusClass(item)">
              <el-icon><CircleCheck v-if="item.status === 1" /><Clock v-else /></el-icon>
              {{ getStatusLabel(item) }}
            </div>
            <el-dropdown trigger="click">
              <el-button circle size="small" class="more-btn">
                <el-icon><MoreFilled /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="editAssessment(item)">
                    <el-icon><Edit /></el-icon>编辑
                  </el-dropdown-item>
                  <el-dropdown-item @click="startAssessment(item)" v-if="item.status !== 1">
                    <el-icon><VideoPlay /></el-icon>开始评估
                  </el-dropdown-item>
                  <el-dropdown-item divided type="danger" @click="deleteAssessmentItem(item)">
                    <el-icon><Delete /></el-icon>删除
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>

          <div class="card-content">
            <h3 class="assessment-title">{{ item.assessmentName }}</h3>
            <p class="assessment-desc">{{ item.description || '暂无描述' }}</p>
            
            <div class="assessment-meta">
              <div class="meta-item">
                <el-icon><Reading /></el-icon>
                <span>{{ getCourseName(item.courseId) }}</span>
              </div>
              <div class="meta-item">
                <el-icon><Timer /></el-icon>
                <span>{{ item.duration || 30 }} 分钟</span>
              </div>
              <div class="meta-item">
                <el-icon><Trophy /></el-icon>
                <span>满分 {{ item.totalScore || 100 }} 分</span>
              </div>
            </div>

            <div class="assessment-progress" v-if="item.userScore !== undefined">
              <div class="progress-header">
                <span>您的成绩</span>
                <span class="score" :class="getScoreClass(item.userScore, item.totalScore)">
                  {{ item.userScore }}/{{ item.totalScore }}
                </span>
              </div>
              <el-progress 
                :percentage="(item.userScore / item.totalScore) * 100" 
                :status="getProgressStatus(item.userScore, item.totalScore)"
                :stroke-width="8"
              />
            </div>
          </div>

          <div class="card-footer">
            <el-button 
              :type="item.status === 1 ? 'success' : 'primary'" 
              @click="startAssessment(item)"
              :disabled="item.status === 1"
            >
              {{ item.status === 1 ? '已完成' : '开始评估' }}
            </el-button>
            <el-button v-if="item.status === 1" @click="viewResult(item)">
              查看结果
            </el-button>
          </div>
        </el-card>
      </div>
    </div>

    <!-- 创建/编辑评估对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑评估' : '创建评估'"
      width="500px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="评估名称" prop="assessmentName">
          <el-input v-model="form.assessmentName" placeholder="请输入评估名称" />
        </el-form-item>
        
        <el-form-item label="所属课程" prop="courseId">
          <el-select v-model="form.courseId" placeholder="请选择课程" style="width: 100%">
            <el-option
              v-for="course in courseList"
              :key="course.courseId"
              :label="course.courseName"
              :value="course.courseId"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="评估时长" prop="duration">
          <el-input-number v-model="form.duration" :min="5" :max="180" :step="5" />
          <span class="unit">分钟</span>
        </el-form-item>
        
        <el-form-item label="总分" prop="totalScore">
          <el-input-number v-model="form.totalScore" :min="10" :max="200" :step="10" />
          <span class="unit">分</span>
        </el-form-item>
        
        <el-form-item label="评估描述" prop="description">
          <el-input 
            v-model="form.description" 
            type="textarea" 
            :rows="3"
            placeholder="请输入评估描述"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitting">
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 评估结果抽屉 -->
    <el-drawer
      v-model="resultDrawerVisible"
      title="评估结果"
      size="500px"
    >
      <div class="result-content" v-if="selectedAssessment">
        <div class="result-header">
          <div class="result-score">
            <div class="score-circle" :class="getResultScoreClass()">
              <span class="score-value">{{ selectedAssessment.userScore }}</span>
              <span class="score-total">/{{ selectedAssessment.totalScore }}</span>
            </div>
            <div class="score-label">{{ getResultLabel() }}</div>
          </div>
        </div>

        <div class="result-detail">
          <h4>评估详情</h4>
          <div class="detail-item">
            <span class="label">评估名称</span>
            <span class="value">{{ selectedAssessment.assessmentName }}</span>
          </div>
          <div class="detail-item">
            <span class="label">所属课程</span>
            <span class="value">{{ getCourseName(selectedAssessment.courseId) }}</span>
          </div>
          <div class="detail-item">
            <span class="label">完成时间</span>
            <span class="value">{{ formatDate(selectedAssessment.completeTime) }}</span>
          </div>
          <div class="detail-item">
            <span class="label">用时</span>
            <span class="value">{{ selectedAssessment.duration }} 分钟</span>
          </div>
        </div>

        <div class="result-ability">
          <h4>能力分析</h4>
          <div class="ability-tags">
            <el-tag v-for="(tag, index) in resultAbilities" :key="index" :type="tag.type">
              {{ tag.name }}
            </el-tag>
          </div>
        </div>

        <div class="result-suggestion">
          <h4>学习建议</h4>
          <p>{{ getSuggestion() }}</p>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAssessments, createAssessment, updateAssessment, deleteAssessment as deleteAssessmentApi } from '@/api/learning'
import { getAllCourses } from '@/api/course'

const router = useRouter()

// 数据
const loading = ref(false)
const assessments = ref([])
const courseList = ref([])
const filterCourse = ref('')
const filterStatus = ref('')

// 能力数据（模拟）
const abilityList = ref([
  { name: '逻辑思维', score: 85, color: '#667eea' },
  { name: '编程能力', score: 78, color: '#10b981' },
  { name: '数据分析', score: 72, color: '#f59e0b' },
  { name: '系统设计', score: 68, color: '#ef4444' },
  { name: '沟通协作', score: 88, color: '#8b5cf6' },
  { name: '创新思维', score: 75, color: '#ec4899' }
])

// 计算属性
const completedCount = computed(() => {
  return assessments.value.filter(item => item.status === 1).length
})

const avgScore = computed(() => {
  const completed = assessments.value.filter(item => item.userScore !== undefined)
  if (completed.length === 0) return 0
  const total = completed.reduce((sum, item) => sum + (item.userScore / item.totalScore * 100), 0)
  return Math.round(total / completed.length)
})

const filteredAssessments = computed(() => {
  let result = assessments.value
  if (filterCourse.value) {
    result = result.filter(item => item.courseId === filterCourse.value)
  }
  if (filterStatus.value) {
    const statusMap = { pending: 0, progress: 2, completed: 1 }
    result = result.filter(item => item.status === statusMap[filterStatus.value])
  }
  return result
})

// 雷达图
const radarChart = ref(null)
let radarChartInstance = null

const initRadarChart = () => {
  if (!radarChart.value) return
  radarChartInstance = echarts.init(radarChart.value)
  const option = {
    radar: {
      indicator: abilityList.value.map(item => ({ name: item.name, max: 100 })),
      radius: '65%',
      axisName: {
        color: '#64748b',
        fontSize: 12
      },
      splitArea: {
        areaStyle: {
          color: ['#f8fafc', '#f1f5f9', '#e2e8f0', '#cbd5e1'].reverse()
        }
      }
    },
    series: [{
      type: 'radar',
      data: [{
        value: abilityList.value.map(item => item.score),
        name: '能力评估',
        areaStyle: {
          color: 'rgba(102, 126, 234, 0.3)'
        },
        lineStyle: {
          color: '#667eea',
          width: 2
        },
        itemStyle: {
          color: '#667eea'
        }
      }]
    }]
  }
  radarChartInstance.setOption(option)
}

// 获取课程名称
const getCourseName = (courseId) => {
  const course = courseList.value.find(c => c.courseId === courseId)
  return course ? course.courseName : '未知课程'
}

// 获取状态标签
const getStatusLabel = (item) => {
  if (item.status === 1) return '已完成'
  if (item.userScore !== undefined) return '进行中'
  return '未开始'
}

// 获取状态样式
const getStatusClass = (item) => {
  if (item.status === 1) return 'completed'
  if (item.userScore !== undefined) return 'progress'
  return 'pending'
}

// 获取分数样式
const getScoreClass = (score, total) => {
  const percentage = score / total
  if (percentage >= 0.9) return 'excellent'
  if (percentage >= 0.8) return 'good'
  if (percentage >= 0.6) return 'pass'
  return 'fail'
}

// 获取进度状态
const getProgressStatus = (score, total) => {
  const percentage = score / total
  if (percentage >= 0.9) return 'success'
  if (percentage >= 0.6) return ''
  return 'exception'
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString('zh-CN')
}

// 加载评估列表
const loadAssessments = async () => {
  loading.value = true
  try {
    const res = await getAssessments()
    if (res.code === 200) {
      assessments.value = res.data.map((item, index) => ({
        ...item,
        status: index % 3 === 0 ? 1 : 0, // 模拟状态
        userScore: index % 3 === 0 ? Math.floor(Math.random() * 30) + 70 : undefined
      }))
    }
  } catch (error) {
    console.error('获取评估列表失败:', error)
    ElMessage.error('获取评估列表失败')
  } finally {
    loading.value = false
  }
}

// 加载课程列表
const loadCourses = async () => {
  try {
    const res = await getAllCourses()
    if (res.code === 200) {
      courseList.value = res.data
    }
  } catch (error) {
    console.error('获取课程列表失败:', error)
  }
}

// 筛选
const handleFilter = () => {
  // 筛选逻辑在 computed 中处理
}

// 对话框
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const formRef = ref(null)
const form = ref({
  assessmentName: '',
  courseId: '',
  duration: 30,
  totalScore: 100,
  description: ''
})

const rules = {
  assessmentName: [{ required: true, message: '请输入评估名称', trigger: 'blur' }],
  courseId: [{ required: true, message: '请选择课程', trigger: 'change' }]
}

// 显示添加对话框
const showAddDialog = () => {
  isEdit.value = false
  form.value = {
    assessmentName: '',
    courseId: '',
    duration: 30,
    totalScore: 100,
    description: ''
  }
  dialogVisible.value = true
}

// 编辑评估
const editAssessment = (item) => {
  isEdit.value = true
  form.value = { ...item }
  dialogVisible.value = true
}

// 提交表单
const submitForm = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    const api = isEdit.value ? 
      () => updateAssessment(form.value.assessmentId, form.value) : 
      () => createAssessment(form.value)
    const res = await api()
    if (res.code === 200) {
      ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
      dialogVisible.value = false
      loadAssessments()
    }
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error('提交失败')
  } finally {
    submitting.value = false
  }
}

// 删除评估
const deleteAssessmentItem = async (item) => {
  try {
    await ElMessageBox.confirm(`确定要删除评估 "${item.assessmentName}" 吗？`, '确认删除', {
      type: 'warning'
    })
    const res = await deleteAssessmentApi(item.assessmentId)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadAssessments()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 开始评估
const startAssessment = (item) => {
  if (item.status === 1) {
    ElMessage.warning('该评估已完成')
    return
  }
  ElMessage.success(`开始评估: ${item.assessmentName}`)
  // 实际应跳转到评估页面
}

// 结果抽屉
const resultDrawerVisible = ref(false)
const selectedAssessment = ref(null)
const resultAbilities = ref([])

const viewResult = (item) => {
  selectedAssessment.value = item
  // 模拟能力标签
  const percentage = item.userScore / item.totalScore
  resultAbilities.value = [
    { name: '理解能力', type: percentage > 0.8 ? 'success' : 'info' },
    { name: '应用能力', type: percentage > 0.7 ? 'success' : 'warning' },
    { name: '分析能力', type: percentage > 0.6 ? 'primary' : 'info' }
  ]
  resultDrawerVisible.value = true
}

const getResultScoreClass = () => {
  if (!selectedAssessment.value) return ''
  const percentage = selectedAssessment.value.userScore / selectedAssessment.value.totalScore
  if (percentage >= 0.9) return 'excellent'
  if (percentage >= 0.8) return 'good'
  if (percentage >= 0.6) return 'pass'
  return 'fail'
}

const getResultLabel = () => {
  if (!selectedAssessment.value) return ''
  const percentage = selectedAssessment.value.userScore / selectedAssessment.value.totalScore
  if (percentage >= 0.9) return '优秀'
  if (percentage >= 0.8) return '良好'
  if (percentage >= 0.6) return '及格'
  return '需努力'
}

const getSuggestion = () => {
  if (!selectedAssessment.value) return ''
  const percentage = selectedAssessment.value.userScore / selectedAssessment.value.totalScore
  if (percentage >= 0.9) return '表现优秀！建议继续保持，可以尝试更具挑战性的内容。'
  if (percentage >= 0.8) return '表现良好！建议巩固基础知识，提升应用能力。'
  if (percentage >= 0.6) return '基本达标。建议加强薄弱环节的学习，多做练习。'
  return '需要加强学习。建议重新学习相关课程内容，寻求帮助。'
}

onMounted(() => {
  loadAssessments()
  loadCourses()
  nextTick(() => {
    initRadarChart()
  })
})
</script>

<style scoped>
.assessment {
  padding: 24px;
  background: #f8fafc;
  min-height: 100vh;
}

/* 页面头部 */
.page-header-modern {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  background: white;
  padding: 24px 28px;
  border-radius: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.header-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.title-badge {
  width: 56px;
  height: 56px;
  background: linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%);
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.badge-icon {
  font-size: 28px;
  color: white;
}

.title-text h1 {
  font-size: 24px;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 4px 0;
}

.title-text p {
  font-size: 14px;
  color: #64748b;
  margin: 0;
}

/* 统计信息 */
.header-stats {
  display: flex;
  align-items: center;
  gap: 24px;
}

.stat-item {
  text-align: center;
}

.stat-value {
  display: block;
  font-size: 28px;
  font-weight: 700;
  color: #1e293b;
  line-height: 1;
}

.stat-label {
  font-size: 13px;
  color: #64748b;
  margin-top: 4px;
}

.stat-divider {
  width: 1px;
  height: 40px;
  background: #e2e8f0;
}

/* 操作按钮 */
.btn-add {
  border-radius: 10px;
  padding: 10px 20px;
  background: linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%);
  border: none;
}

/* 雷达图区域 */
.radar-section {
  background: white;
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
  margin: 0;
}

.section-title .el-icon {
  color: #8b5cf6;
}

.filter-group {
  display: flex;
  gap: 12px;
}

.radar-container {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 40px;
  align-items: center;
}

.radar-chart {
  height: 350px;
}

.ability-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.ability-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.ability-name {
  width: 80px;
  font-size: 14px;
  color: #64748b;
}

.ability-bar {
  flex: 1;
  height: 8px;
  background: #e2e8f0;
  border-radius: 4px;
  overflow: hidden;
}

.ability-progress {
  height: 100%;
  border-radius: 4px;
  transition: width 0.3s ease;
}

.ability-score {
  width: 50px;
  text-align: right;
  font-weight: 600;
  color: #1e293b;
}

/* 评估区域 */
.assessment-section {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.assessment-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
}

.assessment-card {
  border-radius: 12px;
  overflow: hidden;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.assessment-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.assessment-status {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.assessment-status.completed {
  background: #dcfce7;
  color: #15803d;
}

.assessment-status.progress {
  background: #dbeafe;
  color: #1d4ed8;
}

.assessment-status.pending {
  background: #f1f5f9;
  color: #64748b;
}

.card-content {
  margin-bottom: 20px;
}

.assessment-title {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 8px 0;
}

.assessment-desc {
  font-size: 13px;
  color: #64748b;
  margin: 0 0 12px 0;
  line-height: 1.5;
}

.assessment-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 16px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #64748b;
}

.assessment-progress {
  padding: 12px;
  background: #f8fafc;
  border-radius: 8px;
}

.progress-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 13px;
}

.score {
  font-weight: 600;
}

.score.excellent {
  color: #15803d;
}

.score.good {
  color: #1d4ed8;
}

.score.pass {
  color: #b45309;
}

.score.fail {
  color: #dc2626;
}

.card-footer {
  display: flex;
  gap: 8px;
}

.card-footer .el-button {
  flex: 1;
}

/* 加载和空状态 */
.loading-container {
  padding: 40px;
}

.empty-container {
  padding: 60px 40px;
  text-align: center;
}

/* 结果抽屉 */
.result-content {
  padding: 20px;
}

.result-header {
  text-align: center;
  margin-bottom: 32px;
}

.result-score {
  display: inline-block;
}

.score-circle {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  margin: 0 auto 12px;
  border: 4px solid;
}

.score-circle.excellent {
  border-color: #22c55e;
  color: #15803d;
}

.score-circle.good {
  border-color: #3b82f6;
  color: #1d4ed8;
}

.score-circle.pass {
  border-color: #f59e0b;
  color: #b45309;
}

.score-circle.fail {
  border-color: #ef4444;
  color: #dc2626;
}

.score-value {
  font-size: 36px;
  font-weight: 700;
}

.score-total {
  font-size: 16px;
  opacity: 0.7;
}

.score-label {
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
}

.result-detail,
.result-ability,
.result-suggestion {
  margin-bottom: 24px;
}

.result-detail h4,
.result-ability h4,
.result-suggestion h4 {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 16px;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  padding: 12px 0;
  border-bottom: 1px solid #f1f5f9;
}

.detail-item .label {
  color: #64748b;
}

.detail-item .value {
  font-weight: 500;
  color: #1e293b;
}

.ability-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.result-suggestion p {
  padding: 16px;
  background: #f8fafc;
  border-radius: 8px;
  color: #64748b;
  line-height: 1.6;
}

/* 表单 */
.unit {
  margin-left: 8px;
  color: #64748b;
}

/* 响应式 */
@media (max-width: 768px) {
  .radar-container {
    grid-template-columns: 1fr;
  }
  
  .radar-chart {
    height: 280px;
  }
  
  .page-header-modern {
    flex-direction: column;
    gap: 16px;
    text-align: center;
  }
  
  .assessment-grid {
    grid-template-columns: 1fr;
  }
  
  .filter-group {
    flex-direction: column;
    width: 100%;
  }
}
</style>
