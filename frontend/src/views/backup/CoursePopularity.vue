<template>
  <div class="course-popularity">
    <div class="page-header">
      <div class="header-content">
        <div class="header-breadcrumb">
          <el-button link @click="$router.push('/analytics')">
            <el-icon><ArrowLeft /></el-icon>
            杩涙柨娲栧垎鏋愪笁锟界妇
          </el-button>
        </div>
        <h1 class="page-title">璇剧▼鐑搴﹀垎鏋</h1>
        <p class="page-subtitle">鍒嗘瀽璇剧▼鍙楁㈣繋绋嬪害銆佸畬璇剧巼鍜屽︿範瓒嬪娍</p>
      </div>
    </div>

    <div class="metrics-grid">
      <div class="metric-card" v-for="(metric, index) in metrics" :key="index">
        <div class="metric-icon" :class="metric.iconClass">
          <el-icon><component :is="metric.icon" /></el-icon>
        </div>
        <div class="metric-content">
          <span class="metric-label">{{ metric.label }}</span>
          <span class="metric-value">{{ metric.value }}</span>
        </div>
      </div>
    </div>

    <div class="charts-row">
      <div class="chart-card large">
        <h3>璇剧▼鈻奸悜锟藉抽幒鎺曪拷</h3>
        <div ref="popularityChart" class="chart-container"></div>
      </div>
      <div class="chart-card">
        <h3>璇剧▼鈻煎垹鍡欒鍒犲棗绔</h3>
        <div ref="categoryChart" class="chart-container"></div>
      </div>
    </div>

    <div class="course-list">
      <h3>閻戯拷妫璇剧▼鈻艰凤附鍎</h3>
      <el-table :data="courses" style="width: 100%">
        <el-table-column prop="name" label="璇剧▼鈻煎悗宥囆" />
        <el-table-column prop="category" label="鍒犲棛琚" />
        <el-table-column prop="students" label="瀛︿範浜虹儤鏆" sortable />
        <el-table-column prop="rating" label="璇峰嫬鍨" sortable>
          <template #default="scope">
            <el-rate v-model="scope.row.rating" disabled show-score />
          </template>
        </el-table-column>
        <el-table-column prop="completion" label="瀹氬矁锟介悳? sortable>
          <template #default="scope">
            <el-progress :percentage="scope.row.completion" :color="getProgressColor(scope.row.completion)" />
          </template>
        </el-table-column>
        <el-table-column prop="trend" label="鐡掑濞">
          <template #default="scope">
            <el-tag :type="scope.row.trend > 0 ? 'success' : 'danger'">
              <el-icon><ArrowUp v-if="scope.row.trend > 0" /><ArrowDown v-else /></el-icon>
              {{ Math.abs(scope.row.trend) }}%
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script>
import { ArrowLeft, ArrowUp, ArrowDown, Star, User, Collection } from '@element-plus/icons-vue'
import * as echarts from 'echarts'

export default {
  name: 'CoursePopularity',
  components: { ArrowLeft, ArrowUp, ArrowDown, Star, User, Collection },
  data() {
    return {
      metrics: [
        { icon: 'Collection', iconClass: 'red', label: '閻戯拷妫璇剧▼鈻', value: '156' },
        { icon: 'User', iconClass: 'blue', label: '鎬昏诧拷娑旂姳姹夊▎?, value: '12.5K' },
        { icon: 'Star', iconClass: 'orange', label: '骞冲潎璇勫垎', value: '4.5' }
      ],
      courses: [
        { name: 'Python缂栨牜鈻煎叚銉╂，', category: '缂栨牜鈻煎紑鈧鍙?, students: 2341, rating: 4.8, completion: 92, trend: 15 },
        { name: 'Java鏍忕跨妇閹垛偓鏈?, category: '缂栨牜鈻煎紑鈧鍙?, students: 1892, rating: 4.7, completion: 88, trend: 8 },
        { name: 'Web鍓嶅秶锟藉紑鈧鍙栨垵鐤勬埅?, category: '鍓嶅秶锟藉紑鈧鍙?, students: 1654, rating: 4.6, completion: 85, trend: -3 },
        { name: '鏁版嵁鍒嗘瀽涓夊骸褰茬憴鍡楀', category: '鏁版嵁绉戝︼拷', students: 1432, rating: 4.5, completion: 78, trend: 22 },
        { name: '浜哄搫浼愭櫤鑳介崺铏癸拷', category: '浜哄搫浼愭櫤鑳', students: 1289, rating: 4.7, completion: 72, trend: 35 }
      ]
    }
  },
  mounted() {
    this.$nextTick(() => this.initCharts())
  },
  methods: {
    initCharts() {
      this.initPopularityChart()
      this.initCategoryChart()
    },
    initPopularityChart() {
      const chart = echarts.init(this.$refs.popularityChart)
      chart.setOption({
        tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: { type: 'value' },
        yAxis: {
          type: 'category',
          data: ['AI閸╄櫣锟', '鏁版嵁鍒嗘瀽', 'Web鍓嶅秶锟', 'Java', 'Python'],
          axisLabel: { color: '#64748b' }
        },
        series: [{
          type: 'bar',
          data: [1289, 1432, 1654, 1892, 2341],
          itemStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
              { offset: 0, color: '#3b82f6' },
              { offset: 1, color: '#8b5cf6' }
            ]),
            borderRadius: [0, 4, 4, 0]
          }
        }]
      })
    },
    initCategoryChart() {
      const chart = echarts.init(this.$refs.categoryChart)
      chart.setOption({
        tooltip: { trigger: 'item' },
        series: [{
          type: 'pie',
          radius: ['40%', '70%'],
          data: [
            { value: 45, name: '缂栨牜鈻煎紑鈧鍙?, itemStyle: { color: '#3b82f6' } },
            { value: 28, name: '鏁版嵁绉戝︼拷', itemStyle: { color: '#10b981' } },
            { value: 22, name: '浜哄搫浼愭櫤鑳', itemStyle: { color: '#8b5cf6' } },
            { value: 18, name: '鍓嶅秶锟藉紑鈧鍙?, itemStyle: { color: '#f59e0b' } },
            { value: 15, name: '鍏鏈电铂', itemStyle: { color: '#94a3b8' } }
          ]
        }]
      })
    },
    getProgressColor(value) {
      if (value >= 90) return '#10b981'
      if (value >= 70) return '#3b82f6'
      return '#f59e0b'
    }
  }
}
</script>

<style scoped>
.course-popularity {
  max-width: 1600px;
  margin: 0 auto;
}
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
  margin: var(--space-3) 0 var(--space-2);
}
.page-subtitle {
  font-size: var(--text-sm);
  color: var(--text-secondary);
  margin: 0;
}
.metrics-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--space-5);
  margin-bottom: var(--space-6);
}
.metric-card {
  background: white;
  border-radius: var(--radius-xl);
  padding: var(--space-5);
  box-shadow: var(--shadow-card);
  display: flex;
  align-items: center;
  gap: var(--space-4);
}
.metric-icon {
  width: 56px;
  height: 56px;
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
}
.metric-icon.red { background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%); color: white; }
.metric-icon .el-icon { font-size: 28px; }
.metric-icon.blue { background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%); color: white; }
.metric-icon.orange { background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%); color: white; }
.metric-icon .el-icon { font-size: 28px; }
.metric-content { display: flex; flex-direction: column; gap: 4px; }
.metric-label { font-size: var(--text-sm); color: var(--text-secondary); }
.metric-value { font-size: var(--text-2xl); font-weight: var(--font-bold); color: var(--text-primary); }
.charts-row {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: var(--space-5);
  margin-bottom: var(--space-6);
}
.chart-card {
  background: white;
  border-radius: var(--radius-xl);
  padding: var(--space-5);
  box-shadow: var(--shadow-card);
}
.chart-card h3 {
  font-size: var(--text-base);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin: 0 0 var(--space-4);
}
.chart-container { height: 350px; }
.course-list {
  background: white;
  border-radius: var(--radius-xl);
  padding: var(--space-5);
  box-shadow: var(--shadow-card);
}
.course-list h3 {
  font-size: var(--text-lg);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin: 0 0 var(--space-4);
}
@media (max-width: 768px) {
  .metrics-grid, .charts-row { grid-template-columns: 1fr; }
}
</style>
