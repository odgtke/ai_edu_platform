<template>
  <div class="analytics-dashboard">
    <!-- 椤甸潰鏍囬 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">鏁版嵁鍒嗘瀽涓蹇</h1>
        <p class="page-subtitle">鍏ㄦ柟浣嶆暀鑲叉暟鎹娲炲療涓庡彲瑙嗗寲鍒嗘瀽</p>
      </div>
      <div class="header-actions">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="鑷
          start-placeholder="寮濮嬫棩鏈
          end-placeholder="缁撴潫鏃ユ湡"
          size="default"
        />
        <el-button type="primary" class="btn-gradient">
          <el-icon><Download /></el-icon>
          瀵煎嚭閹躲儱鎲
        </el-button>
      </div>
    </div>

    <!-- 鍒嗘瀽濡鈥虫健鍏銉ュ經 -->
    <div class="analytics-modules">
      <div 
        class="module-card" 
        v-for="module in modules" 
        :key="module.path"
        @click="$router.push(module.path)"
      >
        <div class="module-icon" :class="module.iconClass">
          <el-icon><component :is="module.icon" /></el-icon>
        </div>
        <div class="module-content">
          <h3>{{ module.title }}</h3>
          <p>{{ module.description }}</p>
        </div>
        <div class="module-stats">
          <div class="stat-item">
            <span class="stat-value">{{ module.statValue }}</span>
            <span class="stat-label">{{ module.statLabel }}</span>
          </div>
        </div>
        <el-icon class="module-arrow"><ArrowRight /></el-icon>
      </div>
    </div>

    <!-- 鏁版嵁姒傝堬拷鍗★紕澧 -->
    <div class="overview-section">
      <div class="section-header">
        <h2>鏍忕跨妇鎸佸洦鐖ｅ掑倽锟</h2>
        <el-radio-group v-model="timeRange" size="small">
          <el-radio-button label="day">浠ュ﹥妫</el-radio-button>
          <el-radio-button label="week">鏈燂拷鎳</el-radio-button>
          <el-radio-button label="month">鏈燂拷婀</el-radio-button>
          <el-radio-button label="year">鍏銊ュ嬀</el-radio-button>
        </el-radio-group>
      </div>
      
      <div class="overview-grid">
        <div class="overview-card" v-for="(item, index) in overviewData" :key="index">
          <div class="overview-icon" :class="item.iconClass">
            <el-icon><component :is="item.icon" /></el-icon>
          </div>
          <div class="overview-info">
            <span class="overview-label">{{ item.label }}</span>
            <span class="overview-value">{{ item.value }}</span>
            <div class="overview-trend" :class="item.trend > 0 ? 'up' : 'down'">
              <el-icon><ArrowUp v-if="item.trend > 0" /><ArrowDown v-else /></el-icon>
              <span>{{ Math.abs(item.trend) }}%</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 鍥涙崘銆冮崠鍝勭厵 -->
    <div class="charts-section">
      <div class="chart-row">
        <div class="chart-card large">
          <div class="chart-header">
            <h3>骞村啿褰村ú鏄忕┈搴︼箒绉煎姛?/h3>
            <el-radio-group v-model="activityPeriod" size="small">
              <el-radio-button label="7">杩?鍍?/el-radio-button>
              <el-radio-button label="30">杩?0鍍?/el-radio-button>
            </el-radio-group>
          </div>
          <div ref="activityChart" class="chart-container"></div>
        </div>
        
        <div class="chart-card">
          <div class="chart-header">
            <h3>鐢ㄦ埛绫诲瀷鍒犲棗绔</h3>
          </div>
          <div ref="userTypeChart" class="chart-container"></div>
        </div>
      </div>
      
      <div class="chart-row">
        <div class="chart-card">
          <div class="chart-header">
            <h3>璇剧▼鈻煎垹鍡欒閻戯拷瀹</h3>
          </div>
          <div ref="courseCategoryChart" class="chart-container"></div>
        </div>
        
        <div class="chart-card">
          <div class="chart-header">
            <h3>瀛︿範鏃ヨ埖锟藉垹鍡楃</h3>
          </div>
          <div ref="learningTimeChart" class="chart-container"></div>
        </div>
        
        <div class="chart-card">
          <div class="chart-header">
            <h3>Top 5 閻戯拷妫璇剧▼鈻</h3>
          </div>
          <div class="top-courses">
            <div class="course-item" v-for="(course, index) in topCourses" :key="index">
              <div class="course-rank" :class="'rank-' + (index + 1)">{{ index + 1 }}</div>
              <div class="course-info">
                <span class="course-name">{{ course.name }}</span>
                <span class="course-students">{{ course.students }} 浜哄搫婀瀛?/span>
              </div>
              <div class="course-trend">
                <el-icon :class="course.trend > 0 ? 'up' : 'down'">
                  <ArrowUp v-if="course.trend > 0" /><ArrowDown v-else />
                </el-icon>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { 
  Download, ArrowRight, ArrowUp, ArrowDown,
  User, School, Reading, TrendCharts,
  DataAnalysis, Star, Timer, Collection
} from '@element-plus/icons-vue'
import * as echarts from 'echarts'

export default {
  name: 'AnalyticsDashboard',
  components: {
    Download, ArrowRight, ArrowUp, ArrowDown,
    User, School, Reading, TrendCharts,
    DataAnalysis, Star, Timer, Collection
  },
  data() {
    return {
      dateRange: [],
      timeRange: 'week',
      activityPeriod: '7',
      modules: [
        {
          title: '瀛︼妇鏁撳︿範鐞涘奔璐',
          description: '瀛︿範鏃ュ爼鏆遍妴浣规た璺冲啫瀹抽妴浣虹叀璇峰棙甯夋彁鈥冲冲垎鏋',
          icon: 'User',
          iconClass: 'blue',
          path: '/analytics/student-behavior',
          statValue: '2,847',
          statLabel: '濞叉槒绌瀛︼妇鏁'
        },
        {
          title: '鏁版瑥绗鏁版瑥锟芥暟鍫熺亯',
          description: '璇剧▼鈻艰触銊╁櫤閵嗕礁锟介悽鐔稿姬閹板繐瀹抽妴浣规殌瀛︼附鏅ラ悳鍥╃埠璁?,
          icon: 'School',
          iconClass: 'green',
          path: '/analytics/teacher-effectiveness',
          statValue: '96%',
          statLabel: '濠娾剝鍓板害?
        },
        {
          title: '璇剧▼鈻奸悜锟藉冲垎鏋',
          description: '璇剧▼鈻煎彇妤侊拷杩涘海鈻煎害锔衡偓浣哥暚璇剧▼宸奸妴浣界Ъ鍔熷灝鍨庨弸?,
          icon: 'Reading',
          iconClass: 'orange',
          path: '/analytics/course-popularity',
          statValue: '156',
          statLabel: '閻戯拷妫璇剧▼鈻'
        }
      ],
      overviewData: [
        { icon: 'User', iconClass: 'blue', label: '鎬昏崵鏁ゆ埅閿嬫殶', value: '12,580', trend: 12.5 },
        { icon: 'School', iconClass: 'green', label: '濞叉槒绌瀛︿範閼?, value: '8,920', trend: 8.3 },
        { icon: 'Reading', iconClass: 'orange', label: '璇剧▼鈻兼荤粯鏆', value: '486', trend: 15.2 },
        { icon: 'TrendCharts', iconClass: 'purple', label: '瀛︿範瀹屾垚閻?, value: '78.5%', trend: 5.7 }
      ],
      topCourses: [
        { name: 'Python 缂栨牜鈻煎叚銉╂，', students: 2341, trend: 15 },
        { name: 'Java 鏍忕跨妇閹垛偓鏈?, students: 1892, trend: 8 },
        { name: 'Web 鍓嶅秶锟藉紑鈧鍙栨垵鐤勬埅?, students: 1654, trend: -3 },
        { name: '鏁版嵁鍒嗘瀽涓夊骸褰茬憴鍡楀', students: 1432, trend: 22 },
        { name: '浜哄搫浼愭櫤鑳介崺铏癸拷', students: 1289, trend: 35 }
      ],
      charts: {}
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.initCharts()
    })
    window.addEventListener('resize', this.handleResize)
  },
  beforeUnmount() {
    window.removeEventListener('resize', this.handleResize)
    Object.values(this.charts).forEach(chart => chart.dispose())
  },
  methods: {
    initCharts() {
      this.initActivityChart()
      this.initUserTypeChart()
      this.initCourseCategoryChart()
      this.initLearningTimeChart()
    },
    initActivityChart() {
      const chart = echarts.init(this.$refs.activityChart)
      this.charts.activity = chart
      
      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'cross' }
        },
        legend: {
          data: ['鏃ャ儲妞胯烦鍐鏁ゆ埅?, '鏂板烇拷鐢ㄦ埛', '瀛︿範鏃ュ爼鏆(灏忔椂)'],
          bottom: 0
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '15%',
          top: '10%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: ['鍛ㄣ劋绔', '鍛ㄣ劋绨', '鍛ㄣ劋绗', '鍛ㄣ劌娲', '鍛ㄣ劋绨', '鍛ㄣ劌鍙', '鍛ㄣ劍妫'],
          axisLine: { lineStyle: { color: '#e2e8f0' } },
          axisLabel: { color: '#64748b' }
        },
        yAxis: [
          {
            type: 'value',
            name: '鐢ㄦ埛鏁?,
            axisLine: { show: false },
            axisTick: { show: false },
            axisLabel: { color: '#64748b' },
            splitLine: { lineStyle: { color: '#f1f5f9' } }
          },
          {
            type: 'value',
            name: '鏃ュ爼鏆',
            axisLine: { show: false },
            axisTick: { show: false },
            axisLabel: { color: '#64748b', formatter: '{value}h' },
            splitLine: { show: false }
          }
        ],
        series: [
          {
            name: '鏃ャ儲妞胯烦鍐鏁ゆ埅?,
            type: 'line',
            smooth: true,
            data: [1200, 1320, 1010, 1340, 900, 2300, 2100],
            itemStyle: { color: '#3b82f6' },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(59, 130, 246, 0.3)' },
                { offset: 1, color: 'rgba(59, 130, 246, 0.05)' }
              ])
            }
          },
          {
            name: '鏂板烇拷鐢ㄦ埛',
            type: 'line',
            smooth: true,
            data: [220, 182, 191, 234, 290, 330, 310],
            itemStyle: { color: '#10b981' }
          },
          {
            name: '瀛︿範鏃ュ爼鏆(灏忔椂)',
            type: 'bar',
            yAxisIndex: 1,
            data: [150, 180, 120, 200, 90, 280, 250],
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: '#f59e0b' },
                { offset: 1, color: '#fbbf24' }
              ]),
              borderRadius: [4, 4, 0, 0]
            }
          }
        ]
      }
      
      chart.setOption(option)
    },
    initUserTypeChart() {
      const chart = echarts.init(this.$refs.userTypeChart)
      this.charts.userType = chart
      
      const option = {
        tooltip: {
          trigger: 'item',
          formatter: '{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          right: '5%',
          top: 'center',
          textStyle: { color: '#64748b' }
        },
        series: [
          {
            type: 'pie',
            radius: ['40%', '70%'],
            center: ['35%', '50%'],
            avoidLabelOverlap: false,
            itemStyle: {
              borderRadius: 8,
              borderColor: '#fff',
              borderWidth: 2
            },
            label: { show: false },
            data: [
              { value: 8234, name: '瀛︼妇鏁', itemStyle: { color: '#3b82f6' } },
              { value: 1240, name: '鏁版瑥绗', itemStyle: { color: '#10b981' } },
              { value: 86, name: '绠＄悊鍛?, itemStyle: { color: '#f59e0b' } },
              { value: 3020, name: '璁板灝锟', itemStyle: { color: '#94a3b8' } }
            ]
          }
        ]
      }
      
      chart.setOption(option)
    },
    initCourseCategoryChart() {
      const chart = echarts.init(this.$refs.courseCategoryChart)
      this.charts.courseCategory = chart
      
      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'shadow' }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          top: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'value',
          axisLine: { show: false },
          axisTick: { show: false },
          axisLabel: { color: '#64748b' },
          splitLine: { lineStyle: { color: '#f1f5f9' } }
        },
        yAxis: {
          type: 'category',
          data: ['浜哄搫浼愭櫤鑳', '鏁版嵁绉戝︼拷', '鍓嶅秶锟藉紑鈧鍙?, '鍚庡海锟藉紑鈧鍙?, '缁夎插З寮鈧鍙?, '浜烘垼锟界?, '缂冩垹绮跺畾澶婂弿'],
          axisLine: { show: false },
          axisTick: { show: false },
          axisLabel: { color: '#64748b' }
        },
        series: [
          {
            type: 'bar',
            data: [320, 280, 250, 220, 180, 150, 120],
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
                { offset: 0, color: '#667eea' },
                { offset: 1, color: '#764ba2' }
              ]),
              borderRadius: [0, 4, 4, 0]
            },
            barWidth: '60%'
          }
        ]
      }
      
      chart.setOption(option)
    },
    initLearningTimeChart() {
      const chart = echarts.init(this.$refs.learningTimeChart)
      this.charts.learningTime = chart
      
      const option = {
        tooltip: {
          trigger: 'axis'
        },
        radar: {
          indicator: [
            { name: '鏃モ晜娅抃n6-9閻?, max: 100 },
            { name: '涓夊﹤宕峔n9-12閻?, max: 100 },
            { name: '涓夊宕峔n12-18閻?, max: 100 },
            { name: '閺呮矮绗俓n18-22閻?, max: 100 },
            { name: '濞ｅ崬锟絓n22-24閻?, max: 100 },
            { name: '閸戝本娅抃n0-6閻?, max: 100 }
          ],
          shape: 'circle',
          splitNumber: 4,
          axisName: {
            color: '#64748b',
            fontSize: 11
          },
          splitLine: {
            lineStyle: {
              color: [
                'rgba(59, 130, 246, 0.1)',
                'rgba(59, 130, 246, 0.2)',
                'rgba(59, 130, 246, 0.3)',
                'rgba(59, 130, 246, 0.4)'
              ].reverse()
            }
          },
          splitArea: { show: false },
          axisLine: {
            lineStyle: { color: 'rgba(59, 130, 246, 0.3)' }
          }
        },
        series: [
          {
            type: 'radar',
            data: [
              {
                value: [30, 85, 70, 95, 45, 10],
                name: '瀛︿範濞叉槒绌搴?,
                areaStyle: {
                  color: new echarts.graphic.RadialGradient(0.5, 0.5, 1, [
                    { offset: 0, color: 'rgba(59, 130, 246, 0.1)' },
                    { offset: 1, color: 'rgba(59, 130, 246, 0.5)' }
                  ])
                },
                lineStyle: { color: '#3b82f6', width: 2 },
                itemStyle: { color: '#3b82f6' }
              }
            ]
          }
        ]
      }
      
      chart.setOption(option)
    },
    handleResize() {
      Object.values(this.charts).forEach(chart => chart.resize())
    }
  }
}
</script>

<style scoped>
.analytics-dashboard {
  max-width: 1600px;
  margin: 0 auto;
}

/* 椤甸潰鏍囬橈拷 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: var(--space-6);
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

.header-actions {
  display: flex;
  gap: var(--space-3);
  align-items: center;
}

.btn-gradient {
  background: linear-gradient(135deg, var(--primary-500) 0%, var(--primary-600) 100%);
  border: none;
  display: flex;
  align-items: center;
  gap: var(--space-2);
}

/* 鍒嗘瀽濡鈥虫健鍏銉ュ經 */
.analytics-modules {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--space-5);
  margin-bottom: var(--space-6);
}

.module-card {
  background: white;
  border-radius: var(--radius-xl);
  padding: var(--space-6);
  box-shadow: var(--shadow-card);
  display: flex;
  align-items: center;
  gap: var(--space-4);
  cursor: pointer;
  transition: all var(--transition-normal);
  position: relative;
}

.module-card:hover {
  box-shadow: var(--shadow-card-hover);
  transform: translateY(-2px);
}

.module-icon {
  width: 60px;
  height: 60px;
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.module-icon.blue {
  background: linear-gradient(135deg, #dbeafe 0%, #bfdbfe 100%);
  color: #2563eb;
}

.module-icon.green {
  background: linear-gradient(135deg, #d1fae5 0%, #a7f3d0 100%);
  color: #059669;
}

.module-icon.orange {
  background: linear-gradient(135deg, #ffedd5 0%, #fed7aa 100%);
  color: #ea580c;
}

.module-icon .el-icon {
  font-size: 28px;
}

.module-content {
  flex: 1;
}

.module-content h3 {
  font-size: var(--text-lg);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin: 0 0 var(--space-1);
}

.module-content p {
  font-size: var(--text-sm);
  color: var(--text-secondary);
  margin: 0;
}

.module-stats {
  text-align: right;
  padding-right: var(--space-6);
}

.stat-value {
  display: block;
  font-size: var(--text-xl);
  font-weight: var(--font-bold);
  color: var(--primary-600);
}

.stat-label {
  font-size: var(--text-xs);
  color: var(--text-tertiary);
}

.module-arrow {
  color: var(--text-tertiary);
  font-size: 20px;
  transition: transform var(--transition-fast);
}

.module-card:hover .module-arrow {
  transform: translateX(4px);
  color: var(--primary-500);
}

/* 濮掑倽锟介崠鍝勭厵 */
.overview-section {
  background: white;
  border-radius: var(--radius-xl);
  padding: var(--space-6);
  box-shadow: var(--shadow-card);
  margin-bottom: var(--space-6);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-5);
}

.section-header h2 {
  font-size: var(--text-lg);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin: 0;
}

.overview-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--space-5);
}

.overview-card {
  display: flex;
  align-items: center;
  gap: var(--space-4);
  padding: var(--space-5);
  background: var(--bg-secondary);
  border-radius: var(--radius-lg);
  transition: all var(--transition-fast);
}

.overview-card:hover {
  background: var(--primary-50);
}

.overview-icon {
  width: 48px;
  height: 48px;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
}

.overview-icon.blue {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  color: white;
}

.overview-icon.green {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: white;
}

.overview-icon.orange {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
  color: white;
}

.overview-icon.purple {
  background: linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%);
  color: white;
}

.overview-icon .el-icon {
  font-size: 24px;
}

.overview-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.overview-label {
  font-size: var(--text-sm);
  color: var(--text-secondary);
}

.overview-value {
  font-size: var(--text-xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
}

.overview-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: var(--text-xs);
  font-weight: var(--font-medium);
}

.overview-trend.up {
  color: var(--success-600);
}

.overview-trend.down {
  color: var(--error-600);
}

/* 鍥涙崘銆冮崠鍝勭厵 */
.charts-section {
  display: flex;
  flex-direction: column;
  gap: var(--space-5);
}

.chart-row {
  display: grid;
  gap: var(--space-5);
}

.chart-row:first-child {
  grid-template-columns: 2fr 1fr;
}

.chart-row:last-child {
  grid-template-columns: repeat(3, 1fr);
}

.chart-card {
  background: white;
  border-radius: var(--radius-xl);
  padding: var(--space-5);
  box-shadow: var(--shadow-card);
}

.chart-card.large {
  min-height: 400px;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-4);
}

.chart-header h3 {
  font-size: var(--text-base);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin: 0;
}

.chart-container {
  height: 300px;
}

/* Top 璇剧▼鈻煎垹妤勩 */
.top-courses {
  display: flex;
  flex-direction: column;
  gap: var(--space-3);
}

.course-item {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-3);
  background: var(--bg-secondary);
  border-radius: var(--radius-md);
  transition: all var(--transition-fast);
}

.course-item:hover {
  background: var(--primary-50);
}

.course-rank {
  width: 28px;
  height: 28px;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: var(--text-sm);
  font-weight: var(--font-bold);
  color: white;
  flex-shrink: 0;
}

.rank-1 { background: linear-gradient(135deg, #fbbf24 0%, #f59e0b 100%); }
.rank-2 { background: linear-gradient(135deg, #94a3b8 0%, #64748b 100%); }
.rank-3 { background: linear-gradient(135deg, #cd7f32 0%, #a0522d 100%); }
.rank-4, .rank-5 { background: var(--gray-300); color: var(--gray-600); }

.course-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.course-name {
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  color: var(--text-primary);
}

.course-students {
  font-size: var(--text-xs);
  color: var(--text-tertiary);
}

.course-trend .el-icon {
  font-size: 16px;
}

.course-trend .el-icon.up {
  color: var(--success-500);
}

.course-trend .el-icon.down {
  color: var(--error-500);
}

/* 閸濆秴绨插紑?*/
@media (max-width: 1200px) {
  .analytics-modules {
    grid-template-columns: 1fr;
  }
  
  .overview-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .chart-row:first-child,
  .chart-row:last-child {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    gap: var(--space-4);
  }
  
  .overview-grid {
    grid-template-columns: 1fr;
  }
}
</style>
