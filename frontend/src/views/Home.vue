<template>
  <div class="home">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">数据概览</h1>
        <p class="page-subtitle">实时监控平台核心数据指标</p>
      </div>
      <div class="header-actions">
        <el-button type="primary" :loading="loading" @click="refreshData">
          <el-icon><Refresh /></el-icon>
          刷新数据
        </el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div 
        v-for="(stat, index) in statsData" 
        :key="index"
        class="stat-card"
        :style="{ background: stat.gradient }"
      >
        <div class="stat-icon">
          <el-icon :size="32"><component :is="stat.icon" /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ stat.value }}{{ stat.suffix }}</div>
          <div class="stat-label">{{ stat.label }}</div>
          <div class="stat-trend" :class="stat.trendType">
            <el-icon v-if="stat.trendType === 'positive'"><ArrowUp /></el-icon>
            <el-icon v-else><ArrowDown /></el-icon>
            {{ stat.trend }}%
          </div>
        </div>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="charts-section">
      <div class="chart-row">
        <!-- 学习趋势图 -->
        <div class="chart-card large">
          <div class="chart-header">
            <h3>学习趋势分析</h3>
            <el-radio-group v-model="trendPeriod" size="small">
              <el-radio-button label="week">本周</el-radio-button>
              <el-radio-button label="month">本月</el-radio-button>
              <el-radio-button label="year">全年</el-radio-button>
            </el-radio-group>
          </div>
          <div ref="trendChart" class="chart-container"></div>
        </div>

        <!-- 课程分类占比 -->
        <div class="chart-card">
          <div class="chart-header">
            <h3>课程分类占比</h3>
          </div>
          <div ref="categoryChart" class="chart-container"></div>
        </div>
      </div>

      <div class="chart-row">
        <!-- 用户活跃度 -->
        <div class="chart-card">
          <div class="chart-header">
            <h3>用户活跃度</h3>
          </div>
          <div ref="activityChart" class="chart-container"></div>
        </div>

        <!-- 能力评估分布 -->
        <div class="chart-card">
          <div class="chart-header">
            <h3>能力评估分布</h3>
          </div>
          <div ref="assessmentChart" class="chart-container"></div>
        </div>
      </div>
    </div>

    <!-- 快速操作 -->
    <div class="quick-actions">
      <h3>快速操作</h3>
      <div class="actions-grid">
        <el-button 
          v-for="action in quickActions" 
          :key="action.path"
          class="action-btn"
          @click="$router.push(action.path)"
        >
          <el-icon :size="20"><component :is="action.icon" /></el-icon>
          <span>{{ action.label }}</span>
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'
import { getUserCount } from '@/api/user'
import { getCourseCount, getAllCourses } from '@/api/course'
import { getAllRecords } from '@/api/learning'
    const router = useRouter()
    const trendPeriod = ref('week')
    const loading = ref(false)
    
    // 模拟数据（作为fallback）
    const mockStatsData = [
      {
        icon: 'User',
        label: '总用户数',
        value: 1256,
        suffix: '人',
        trend: 12,
        trendType: 'positive',
        gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
      },
      {
        icon: 'Reading',
        label: '课程总数',
        value: 48,
        suffix: '门',
        trend: 8,
        trendType: 'positive',
        gradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)'
      },
      {
        icon: 'School',
        label: '活跃学习者',
        value: 892,
        suffix: '人',
        trend: 15,
        trendType: 'positive',
        gradient: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)'
      },
      {
        icon: 'Star',
        label: '满意度',
        value: 96,
        suffix: '%',
        trend: 3,
        trendType: 'positive',
        gradient: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)'
      }
    ]
    
    // 统计数据（动态）
    const statsData = ref(JSON.parse(JSON.stringify(mockStatsData)))

    // 快速操作
    const quickActions = ref([
      { icon: 'User', label: '用户管理', path: '/users' },
      { icon: 'Reading', label: '课程管理', path: '/courses' },
      { icon: 'School', label: '学习中心', path: '/learning' },
      { icon: 'ChatDotRound', label: '智能助手', path: '/agent' },
      { icon: 'TrendCharts', label: '能力评估', path: '/assessment' },
      { icon: 'DataAnalysis', label: '数据分析', path: '/analytics' }
    ])

    // 图表引用
    const trendChart = ref(null)
    const categoryChart = ref(null)
    const activityChart = ref(null)
    const assessmentChart = ref(null)

    let trendChartInstance = null
    let categoryChartInstance = null
    let activityChartInstance = null
    let assessmentChartInstance = null

    // 初始化学习趋势图
    const initTrendChart = () => {
      if (!trendChart.value) return
      trendChartInstance = echarts.init(trendChart.value)
      const option = {
        tooltip: { trigger: 'axis' },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: {
          type: 'category',
          data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
          axisLine: { lineStyle: { color: '#ddd' } },
          axisLabel: { color: '#666' }
        },
        yAxis: {
          type: 'value',
          axisLine: { lineStyle: { color: '#ddd' } },
          axisLabel: { color: '#666' },
          splitLine: { lineStyle: { color: '#f0f0f0' } }
        },
        series: [
          {
            name: '学习时长',
            type: 'line',
            smooth: true,
            data: [120, 132, 101, 134, 90, 230, 210],
            itemStyle: { color: '#667eea' },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(102, 126, 234, 0.3)' },
                { offset: 1, color: 'rgba(102, 126, 234, 0.05)' }
              ])
            }
          },
          {
            name: '活跃人数',
            type: 'line',
            smooth: true,
            data: [220, 182, 191, 234, 290, 330, 310],
            itemStyle: { color: '#f5576c' }
          }
        ]
      }
      trendChartInstance.setOption(option)
    }

    // 初始化课程分类图
    const initCategoryChart = () => {
      if (!categoryChart.value) return
      categoryChartInstance = echarts.init(categoryChart.value)
      const option = {
        tooltip: { trigger: 'item' },
        legend: { bottom: '5%', left: 'center' },
        series: [
          {
            name: '课程分类',
            type: 'pie',
            radius: ['40%', '70%'],
            avoidLabelOverlap: false,
            itemStyle: {
              borderRadius: 10,
              borderColor: '#fff',
              borderWidth: 2
            },
            label: { show: false, position: 'center' },
            emphasis: {
              label: { show: true, fontSize: 20, fontWeight: 'bold' }
            },
            labelLine: { show: false },
            data: [
              { value: 1048, name: '编程开发', itemStyle: { color: '#667eea' } },
              { value: 735, name: '数据科学', itemStyle: { color: '#f5576c' } },
              { value: 580, name: '人工智能', itemStyle: { color: '#4facfe' } },
              { value: 484, name: '前端开发', itemStyle: { color: '#43e97b' } },
              { value: 300, name: '其他', itemStyle: { color: '#fa709a' } }
            ]
          }
        ]
      }
      categoryChartInstance.setOption(option)
    }

    // 初始化用户活跃度图
    const initActivityChart = () => {
      if (!activityChart.value) return
      activityChartInstance = echarts.init(activityChart.value)
      const option = {
        tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: {
          type: 'value',
          axisLine: { lineStyle: { color: '#ddd' } },
          axisLabel: { color: '#666' },
          splitLine: { lineStyle: { color: '#f0f0f0' } }
        },
        yAxis: {
          type: 'category',
          data: ['00:00', '04:00', '08:00', '12:00', '16:00', '20:00'],
          axisLine: { lineStyle: { color: '#ddd' } },
          axisLabel: { color: '#666' }
        },
        series: [
          {
            name: '活跃人数',
            type: 'bar',
            data: [120, 80, 350, 480, 420, 380],
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
                { offset: 0, color: '#4facfe' },
                { offset: 1, color: '#00f2fe' }
              ])
            }
          }
        ]
      }
      activityChartInstance.setOption(option)
    }

    // 初始化能力评估图
    const initAssessmentChart = () => {
      if (!assessmentChart.value) return
      assessmentChartInstance = echarts.init(assessmentChart.value)
      const option = {
        tooltip: { trigger: 'axis' },
        radar: {
          indicator: [
            { name: '编程能力', max: 100 },
            { name: '逻辑思维', max: 100 },
            { name: '数学基础', max: 100 },
            { name: '创新能力', max: 100 },
            { name: '团队协作', max: 100 },
            { name: '沟通表达', max: 100 }
          ],
          radius: '65%'
        },
        series: [
          {
            name: '能力评估',
            type: 'radar',
            data: [
              {
                value: [85, 90, 78, 88, 82, 80],
                name: '平均水平',
                areaStyle: { color: 'rgba(102, 126, 234, 0.3)' },
                lineStyle: { color: '#667eea' },
                itemStyle: { color: '#667eea' }
              }
            ]
          }
        ]
      }
      assessmentChartInstance.setOption(option)
    }

    // 获取统计数据
    const fetchStatsData = async () => {
      loading.value = true
      try {
        const [userRes, courseRes, recordsRes] = await Promise.all([
          getUserCount().catch(() => ({ data: 0 })),
          getCourseCount().catch(() => ({ data: 0 })),
          getAllRecords().catch(() => ({ data: [] }))
        ])
        
        // 更新用户数
        if (userRes.data > 0) {
          statsData.value[0].value = userRes.data
        }
        
        // 更新课程数
        if (courseRes.data > 0) {
          statsData.value[1].value = courseRes.data
        }
        
        // 计算活跃学习者（有学习记录的唯一学生数）
        if (recordsRes.data && recordsRes.data.length > 0) {
          const uniqueStudents = new Set(recordsRes.data.map(r => r.studentId))
          statsData.value[2].value = uniqueStudents.size
        }
        
        // 满意度保持模拟数据（后端暂无此接口）
        // 可以基于学习记录计算完成率作为满意度参考
        
      } catch (error) {
        console.warn('获取统计数据失败，使用模拟数据:', error)
        // 使用模拟数据
        statsData.value = JSON.parse(JSON.stringify(mockStatsData))
      } finally {
        loading.value = false
      }
    }
    
    // 刷新数据
    const refreshData = async () => {
      await fetchStatsData()
      await fetchChartData()
      ElMessage.success('数据刷新成功')
    }
    
    // 获取图表数据
    const fetchChartData = async () => {
      try {
        // 获取课程数据用于分类统计
        const courseRes = await getAllCourses().catch(() => ({ data: [] }))
        if (courseRes.data && courseRes.data.length > 0) {
          updateCategoryChart(courseRes.data)
        }
        
        // 获取学习记录用于趋势分析
        const recordsRes = await getAllRecords().catch(() => ({ data: [] }))
        if (recordsRes.data && recordsRes.data.length > 0) {
          updateTrendChart(recordsRes.data)
          updateActivityChart(recordsRes.data)
        }
      } catch (error) {
        console.warn('获取图表数据失败:', error)
      }
    }
    
    // 更新课程分类图表
    const updateCategoryChart = (courses) => {
      if (!categoryChartInstance) return
      
      // 按分类统计
      const categoryCount = {}
      courses.forEach(course => {
        const category = course.category || '其他'
        categoryCount[category] = (categoryCount[category] || 0) + 1
      })
      
      const data = Object.entries(categoryCount).map(([name, value]) => ({
        name,
        value
      }))
      
      categoryChartInstance.setOption({
        series: [{ data }]
      })
    }
    
    // 更新学习趋势图表
    const updateTrendChart = (records) => {
      if (!trendChartInstance) return
      
      // 按日期统计学习时长
      const dailyStats = {}
      const days = ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
      
      // 初始化一周数据
      days.forEach(day => {
        dailyStats[day] = { duration: 0, count: 0 }
      })
      
      // 统计学习记录
      records.forEach(record => {
        if (record.createTime) {
          const date = new Date(record.createTime)
          const dayIndex = date.getDay() || 7
          const dayName = days[dayIndex - 1]
          dailyStats[dayName].duration += record.duration || 0
          dailyStats[dayName].count += 1
        }
      })
      
      const durationData = days.map(day => Math.round(dailyStats[day].duration / 60)) // 转换为小时
      const countData = days.map(day => dailyStats[day].count)
      
      trendChartInstance.setOption({
        series: [
          { data: durationData },
          { data: countData }
        ]
      })
    }
    
    // 更新用户活跃度图表
    const updateActivityChart = (records) => {
      if (!activityChartInstance) return
      
      // 按时间段统计活跃人数
      const hourStats = new Array(6).fill(0)
      const timeSlots = ['00:00', '04:00', '08:00', '12:00', '16:00', '20:00']
      
      records.forEach(record => {
        if (record.createTime) {
          const hour = new Date(record.createTime).getHours()
          const slotIndex = Math.floor(hour / 4)
          if (slotIndex < 6) {
            hourStats[slotIndex]++
          }
        }
      })
      
      activityChartInstance.setOption({
        series: [{ data: hourStats }]
      })
    }

    // 监听窗口大小变化
    const handleResize = () => {
      trendChartInstance?.resize()
      categoryChartInstance?.resize()
      activityChartInstance?.resize()
      assessmentChartInstance?.resize()
    }

    onMounted(() => {
      initTrendChart()
      initCategoryChart()
      initActivityChart()
      initAssessmentChart()
      window.addEventListener('resize', handleResize)
      
      // 加载数据
      fetchStatsData()
      fetchChartData()
    })

    onUnmounted(() => {
      window.removeEventListener('resize', handleResize)
      trendChartInstance?.dispose()
      categoryChartInstance?.dispose()
      activityChartInstance?.dispose()
      assessmentChartInstance?.dispose()
    })

    watch(trendPeriod, () => {
      // 根据周期切换数据
      if (trendChartInstance) {
        const newData = trendPeriod.value === 'week' 
          ? [120, 132, 101, 134, 90, 230, 210]
          : trendPeriod.value === 'month'
          ? [820, 932, 901, 934, 1290, 1330, 1320]
          : [3200, 3500, 3800, 4200, 4500, 4800, 5100]
        trendChartInstance.setOption({
          series: [{ data: newData }]
        })
      }
    })

</script>

<style scoped>
.home {
  padding: 24px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.header-content h1 {
  font-size: 28px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0 0 8px 0;
}

.header-content p {
  font-size: 14px;
  color: #666;
  margin: 0;
}

/* 统计卡片 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 24px;
  border-radius: 16px;
  color: white;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-4px);
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  opacity: 0.9;
  margin-bottom: 8px;
}

.stat-trend {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  padding: 4px 8px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.2);
}

/* 图表区域 */
.charts-section {
  margin-bottom: 24px;
}

.chart-row {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 20px;
  margin-bottom: 20px;
}

.chart-row:last-child {
  grid-template-columns: 1fr 1fr;
  margin-bottom: 0;
}

.chart-card {
  background: white;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.chart-header h3 {
  font-size: 16px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0;
}

.chart-container {
  height: 280px;
}

/* 快速操作 */
.quick-actions {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.quick-actions h3 {
  font-size: 18px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0 0 16px 0;
}

.actions-grid {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 12px;
}

.action-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 20px;
  border: 1px solid #e8e8e8;
  border-radius: 12px;
  background: white;
  transition: all 0.3s ease;
}

.action-btn:hover {
  border-color: #667eea;
  color: #667eea;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
}

/* 响应式 */
@media (max-width: 1200px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .chart-row,
  .chart-row:last-child {
    grid-template-columns: 1fr;
  }
  
  .actions-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .actions-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .page-header {
    flex-direction: column;
    gap: 16px;
    text-align: center;
  }
}
</style>
