<template>
  <div class="learning">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">学习中心</h1>
        <p class="page-subtitle">查看学习进度、管理学习记录、制定学习计划</p>
      </div>
    </div>

    <!-- 学习统计 -->
    <div class="stats-section">
      <div class="stat-card">
        <div class="stat-icon blue">
          <el-icon><Clock /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.totalHours }}小时</div>
          <div class="stat-label">总学习时长</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon green">
          <el-icon><Check /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.completedCourses }}门</div>
          <div class="stat-label">已完成课程</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon orange">
          <el-icon><Document /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.totalRecords }}条</div>
          <div class="stat-label">学习记录数</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon purple">
          <el-icon><TrendCharts /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.averageProgress }}%</div>
          <div class="stat-label">平均进度</div>
        </div>
      </div>
    </div>

    <!-- 主体内容区：日历 + 学习记录 -->
    <div class="main-content">
      <!-- 左侧：学习记录列表 -->
      <div class="records-section">
        <div class="section-header">
          <h3>学习记录</h3>
          <el-button type="primary" link @click="showAddRecordDialog">
            <el-icon><Plus /></el-icon>
            添加记录
          </el-button>
        </div>
        
        <el-table :data="learningRecords" stripe border v-loading="loading" size="small">
          <el-table-column type="index" width="45" />
          
          <el-table-column label="课程名称" min-width="160">
            <template #default="{ row }">
              <div class="course-name">{{ row.courseName }}</div>
            </template>
          </el-table-column>
          
          <el-table-column label="时长" width="80">
            <template #default="{ row }">
              <span class="duration">{{ row.duration }}分钟</span>
            </template>
          </el-table-column>
          
          <el-table-column label="进度" width="120">
            <template #default="{ row }">
              <el-progress :percentage="row.progress" :status="getProgressStatus(row.progress)" :stroke-width="6" />
            </template>
          </el-table-column>
          
          <el-table-column label="日期" width="100">
            <template #default="{ row }">
              {{ row.studyDate }}
            </template>
          </el-table-column>
          
          <el-table-column label="状态" width="70">
            <template #default="{ row }">
              <el-tag :type="row.status === 'completed' ? 'success' : 'primary'" size="small">
                {{ row.status === 'completed' ? '已完成' : '学习中' }}
              </el-tag>
            </template>
          </el-table-column>
          
          <el-table-column label="操作" width="100" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" link size="small" @click="editRecord(row)">编辑</el-button>
              <el-button type="danger" link size="small" @click="deleteRecord(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <div class="pagination">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :total="total"
            :page-sizes="[10, 20, 50]"
            layout="total, prev, pager, next"
            @current-change="handlePageChange"
            @size-change="handlePageChange"
            small
          />
        </div>
      </div>

      <!-- 右侧：学习日历 -->
      <div class="calendar-section">
        <div class="section-header">
          <h3>学习日历</h3>
          <el-radio-group v-model="calendarView" size="small">
            <el-radio-button label="week">周</el-radio-button>
            <el-radio-button label="month">月</el-radio-button>
          </el-radio-group>
        </div>
        <div class="calendar-content">
          <div class="calendar-grid">
            <div 
              v-for="day in calendarDays" 
              :key="day.date"
              class="calendar-day"
              :class="{ 
                'today': day.isToday, 
                'has-study': day.studyHours > 0,
                'current-month': day.isCurrentMonth 
              }"
            >
              <div class="day-number">{{ day.day }}</div>
              <div v-if="day.studyHours > 0" class="study-hours">{{ day.studyHours }}h</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 学习记录列表（占位，保持原有结构） -->
    <div class="records-section" style="display:none;">
      <div class="section-header">
        <h3>学习记录</h3>
        <el-button type="primary" link @click="showAddRecordDialog">
          <el-icon><Plus /></el-icon>
          添加记录
        </el-button>
      </div>
      
      <el-table :data="learningRecords" stripe border v-loading="loading">
        <el-table-column type="index" width="50" />
        
        <el-table-column label="课程名称" min-width="200">
          <template #default="{ row }">
            <div class="course-name">{{ row.courseName }}</div>
          </template>
        </el-table-column>
        
        <el-table-column label="学习时长" width="120">
          <template #default="{ row }">
            <span class="duration">{{ row.duration }} 分钟</span>
          </template>
        </el-table-column>
        
        <el-table-column label="学习进度" width="200">
          <template #default="{ row }">
            <el-progress :percentage="row.progress" :status="getProgressStatus(row.progress)" />
          </template>
        </el-table-column>
        
        <el-table-column label="学习日期" width="160">
          <template #default="{ row }">
            {{ row.studyDate }}
          </template>
        </el-table-column>
        
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'completed' ? 'success' : 'primary'">
              {{ row.status === 'completed' ? '已完成' : '学习中' }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="editRecord(row)">编辑</el-button>
            <el-button type="danger" link size="small" @click="deleteRecord(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, prev, pager, next"
          @current-change="handlePageChange"
          @size-change="handlePageChange"
          small
        />
      </div>
    </div>

    <!-- 添加/编辑学习记录对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑学习记录' : '添加学习记录'"
      width="500px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="课程名称" prop="courseName">
          <el-input v-model="form.courseName" placeholder="请输入课程名称" />
        </el-form-item>
        
        <el-form-item label="学习时长" prop="duration">
          <el-input-number v-model="form.duration" :min="1" :max="480" style="width: 200px" />
          <span class="unit">分钟</span>
        </el-form-item>
        
        <el-form-item label="学习进度" prop="progress">
          <el-slider v-model="form.progress" :max="100" show-input />
        </el-form-item>
        
        <el-form-item label="学习日期" prop="studyDate">
          <el-date-picker
            v-model="form.studyDate"
            type="date"
            placeholder="选择日期"
            style="width: 100%"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        
        <el-form-item label="完成状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="learning">学习中</el-radio>
            <el-radio label="completed">已完成</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitting">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getStatistics, getCalendar, getRecordsPage } from '@/api/learning'
    // 统计数据
    const stats = ref({
      totalHours: 0,
      completedCourses: 0,
      totalRecords: 0,
      averageProgress: 0
    })

    // 日历视图
    const calendarView = ref('month')
    const calendarData = ref({})
    
    // 生成日历数据
    const calendarDays = computed(() => {
      const days = []
      const today = new Date()
      const year = today.getFullYear()
      const month = today.getMonth()
      
      const firstDay = new Date(year, month, 1)
      const lastDay = new Date(year, month + 1, 0)
      const startDate = new Date(firstDay)
      startDate.setDate(startDate.getDate() - firstDay.getDay())
      
      for (let i = 0; i < 42; i++) {
        const date = new Date(startDate)
        date.setDate(startDate.getDate() + i)
        
        const isToday = date.toDateString() === today.toDateString()
        const isCurrentMonth = date.getMonth() === month
        const dateStr = date.toISOString().split('T')[0]
        const studyHours = calendarData.value[dateStr] || 0
        
        days.push({
          date: dateStr,
          day: date.getDate(),
          isToday,
          isCurrentMonth,
          studyHours
        })
      }
      
      return days
    })

    // 学习记录
    const learningRecords = ref([])
    const currentPage = ref(1)
    const pageSize = ref(10)
    const total = ref(0)
    const loading = ref(false)

    // 获取进度状态
    const getProgressStatus = (progress) => {
      if (progress === 100) return 'success'
      if (progress >= 60) return ''
      return 'exception'
    }

    // 对话框
    const dialogVisible = ref(false)
    const isEdit = ref(false)
    const submitting = ref(false)
    const formRef = ref(null)
    const form = ref({
      courseName: '',
      duration: 30,
      progress: 0,
      studyDate: '',
      status: 'learning'
    })

    const rules = {
      courseName: [
        { required: true, message: '请输入课程名称', trigger: 'blur' }
      ],
      duration: [
        { required: true, message: '请输入学习时长', trigger: 'blur' }
      ],
      studyDate: [
        { required: true, message: '请选择学习日期', trigger: 'change' }
      ]
    }

    // 显示添加对话框
    const showAddRecordDialog = () => {
      isEdit.value = false
      form.value = {
        courseName: '',
        duration: 30,
        progress: 0,
        studyDate: new Date().toISOString().split('T')[0],
        status: 'learning'
      }
      dialogVisible.value = true
    }

    // 编辑记录
    const editRecord = (row) => {
      isEdit.value = true
      form.value = { ...row }
      dialogVisible.value = true
    }

    // 提交表单
    const submitForm = async () => {
      const valid = await formRef.value?.validate().catch(() => false)
      if (!valid) return
      
      submitting.value = true
      
      setTimeout(() => {
        if (isEdit.value) {
          const index = learningRecords.value.findIndex(r => r.id === form.value.id)
          if (index !== -1) {
            learningRecords.value[index] = { ...form.value }
          }
          ElMessage.success('更新成功')
        } else {
          const newRecord = {
            ...form.value,
            id: Date.now()
          }
          learningRecords.value.unshift(newRecord)
          total.value++
          ElMessage.success('添加成功')
        }
        
        submitting.value = false
        dialogVisible.value = false
      }, 500)
    }

    // 删除记录
    const deleteRecord = (row) => {
      ElMessageBox.confirm(
        '确定要删除这条学习记录吗？',
        '确认删除',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      ).then(() => {
        const index = learningRecords.value.findIndex(r => r.id === row.id)
        if (index !== -1) {
          learningRecords.value.splice(index, 1)
          total.value--
          ElMessage.success('删除成功')
        }
      }).catch(() => {})
    }

    // 加载统计数据
    const loadStatistics = async () => {
      try {
        const res = await getStatistics()
        if (res.code === 200) {
          stats.value = res.data
        }
      } catch (error) {
        console.error('加载统计数据失败:', error)
      }
    }

    // 加载日历数据
    const loadCalendar = async () => {
      try {
        const res = await getCalendar()
        if (res.code === 200) {
          calendarData.value = res.data.dailyHours || {}
        }
      } catch (error) {
        console.error('加载日历数据失败:', error)
      }
    }

    // 加载学习记录
    const loadRecords = async () => {
      loading.value = true
      try {
        const res = await getRecordsPage({
          current: currentPage.value,
          size: pageSize.value
        })
        if (res.code === 200) {
          // 转换后端数据为前端格式
          learningRecords.value = res.data.records.map(record => ({
            id: record.recordId,
            courseName: `课程ID: ${record.courseId}`,
            duration: record.studyDuration,
            progress: record.progress || 0,
            studyDate: record.lastStudyTime ? record.lastStudyTime.split('T')[0] : '',
            status: record.isCompleted ? 'completed' : 'learning'
          }))
          total.value = res.data.total
        }
      } catch (error) {
        console.error('加载学习记录失败:', error)
        ElMessage.error('加载学习记录失败')
      } finally {
        loading.value = false
      }
    }

    // 分页变化
    const handlePageChange = () => {
      loadRecords()
    }

    onMounted(() => {
      loadStatistics()
      loadCalendar()
      loadRecords()
    })
</script>

<style scoped>
.learning {
  padding: 24px;
}

.page-header {
  margin-bottom: 24px;
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

/* 统计区域 */
.stats-section {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  font-size: 24px;
  color: white;
}

.stat-icon.blue {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-icon.green {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-icon.orange {
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
}

.stat-icon.purple {
  background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #1a1a1a;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

/* 主体内容区 */
.main-content {
  display: grid;
  grid-template-columns: 1fr 320px;
  gap: 20px;
  margin-bottom: 24px;
}

/* 日历区域 */
.calendar-section {
  background: white;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  height: fit-content;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.section-header h3 {
  font-size: 16px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0;
}

.calendar-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 4px;
}

.calendar-day {
  aspect-ratio: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border-radius: 6px;
  background: #f5f7fa;
  cursor: pointer;
  transition: all 0.3s ease;
  min-height: 36px;
}

.calendar-day:hover {
  background: #e4e7ed;
}

.calendar-day.today {
  background: #667eea;
  color: white;
}

.calendar-day.has-study {
  background: #e6f7ff;
  border: 1px solid #1890ff;
}

.calendar-day:not(.current-month) {
  opacity: 0.4;
}

.day-number {
  font-size: 12px;
  font-weight: 500;
}

.study-hours {
  font-size: 10px;
  color: #1890ff;
  margin-top: 1px;
}

.calendar-day.today .study-hours {
  color: white;
}

/* 记录区域 */
.records-section {
  background: white;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.course-name {
  font-weight: 500;
  color: #1a1a1a;
}

.duration {
  color: #666;
}

.unit {
  margin-left: 8px;
  color: #666;
}

.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #e8e8e8;
}

/* 响应式 */
@media (max-width: 1200px) {
  .stats-section {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .main-content {
    grid-template-columns: 1fr;
  }
  
  .calendar-section {
    order: -1;
  }
}

@media (max-width: 768px) {
  .stats-section {
    grid-template-columns: 1fr;
  }
  
  .calendar-grid {
    gap: 3px;
  }
  
  .calendar-day {
    font-size: 11px;
    min-height: 32px;
  }
}
</style>
