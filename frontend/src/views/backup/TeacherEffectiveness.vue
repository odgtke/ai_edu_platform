<template>
  <div class="teacher-effectiveness">
    <div class="page-header">
      <div class="header-content">
        <div class="header-breadcrumb">
          <el-button link @click="$router.push('/analytics')">
            <el-icon><ArrowLeft /></el-icon>
            杩涙柨娲栧垎鏋愪笁锟界妇
          </el-button>
        </div>
        <h1 class="page-title">鏁欏笀鏁欏︽晥鏋滅粺璁</h1>
        <p class="page-subtitle">璇勪及鏁欏笀鏁欏﹁川閲忋佸︾敓婊℃剰搴﹀拰璇剧▼鏁堟灉</p>
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
      <div class="chart-card">
        <h3>鏁版瑥绗璇峰嫬鍨庡垹鍡楃</h3>
        <div ref="teacherRatingChart" class="chart-container"></div>
      </div>
      <div class="chart-card">
        <h3>璇剧▼鈻煎娾剝鍓板害锕佺Ъ鍔?/h3>
        <div ref="satisfactionChart" class="chart-container"></div>
      </div>
    </div>

    <div class="teacher-ranking">
      <h3>浼犳硷拷鏁版瑥绗閹烘帟锟</h3>
      <el-table :data="teachers" style="width: 100%">
        <el-table-column type="index" label="鎺掑悕" width="80" />
        <el-table-column prop="name" label="鏁欏笀濮撳悕" />
        <el-table-column prop="course" label="涓夋槒锟借剧▼鈻" />
        <el-table-column prop="rating" label="璇峰嫬鍨">
          <template #default="scope">
            <el-rate v-model="scope.row.rating" disabled show-score />
          </template>
        </el-table-column>
        <el-table-column prop="students" label="瀛︾敓鏁? />
        <el-table-column prop="completion" label="瀹氬矁锟介悳?>
          <template #default="scope">
            <el-progress :percentage="scope.row.completion" />
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script>
import { ArrowLeft, Star, User, Reading } from '@element-plus/icons-vue'
import * as echarts from 'echarts'

export default {
  name: 'TeacherEffectiveness',
  components: { ArrowLeft, Star, User, Reading },
  data() {
    return {
      metrics: [
        { icon: 'Star', iconClass: 'orange', label: '骞冲潎璇勫垎', value: '4.6' },
        { icon: 'User', iconClass: 'blue', label: '鏁版瑥绗鎬荤粯鏆', value: '48' },
        { icon: 'Reading', iconClass: 'green', label: '骞村啿娼庡畾宀冿拷閻?, value: '78.5%' }
      ],
      teachers: [
        { name: '寮鐘虫殌閹?, course: 'Python缂栨牜鈻', rating: 4.9, students: 523, completion: 92 },
        { name: '鏉熷氦鈧浣哥瑎', course: 'Java閸╄櫣锟', rating: 4.8, students: 486, completion: 88 },
        { name: '閻滃宕ユ竟?, course: '鏁版嵁缁鎾寸', rating: 4.7, students: 412, completion: 85 }
      ],
      charts: {}
    }
  },
  mounted() {
    this.$nextTick(() => this.initCharts())
  },
  methods: {
    initCharts() {
      this.initTeacherRatingChart()
      this.initSatisfactionChart()
    },
    initTeacherRatingChart() {
      const chart = echarts.init(this.$refs.teacherRatingChart)
      chart.setOption({
        tooltip: { trigger: 'item' },
        series: [{
          type: 'pie',
          radius: ['40%', '70%'],
          data: [
            { value: 12, name: '5閺?, itemStyle: { color: '#10b981' } },
            { value: 18, name: '4閺?, itemStyle: { color: '#3b82f6' } },
            { value: 10, name: '3閺?, itemStyle: { color: '#f59e0b' } },
            { value: 5, name: '2閺?, itemStyle: { color: '#ef4444' } },
            { value: 3, name: '1閺?, itemStyle: { color: '#94a3b8' } }
          ]
        }]
      })
    },
    initSatisfactionChart() {
      const chart = echarts.init(this.$refs.satisfactionChart)
      chart.setOption({
        tooltip: { trigger: 'axis' },
        xAxis: { type: 'category', data: ['1鏈?, '2鏈?, '3鏈?, '4鏈?, '5鏈?, '6鏈?] },
        yAxis: { type: 'value', max: 100 },
        series: [{
          data: [85, 87, 89, 88, 91, 93],
          type: 'line',
          smooth: true,
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: 'rgba(59, 130, 246, 0.3)' },
              { offset: 1, color: 'rgba(59, 130, 246, 0.05)' }
            ])
          }
        }]
      })
    }
  }
}
</script>

<style scoped>
.teacher-effectiveness {
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
.metric-icon.orange { background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%); color: white; }
.metric-icon.blue { background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%); color: white; }
.metric-icon.green { background: linear-gradient(135deg, #10b981 0%, #059669 100%); color: white; }
.metric-icon .el-icon { font-size: 28px; }
.metric-content { display: flex; flex-direction: column; gap: 4px; }
.metric-label { font-size: var(--text-sm); color: var(--text-secondary); }
.metric-value { font-size: var(--text-2xl); font-weight: var(--font-bold); color: var(--text-primary); }
.charts-row {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
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
.chart-container { height: 300px; }
.teacher-ranking {
  background: white;
  border-radius: var(--radius-xl);
  padding: var(--space-5);
  box-shadow: var(--shadow-card);
}
.teacher-ranking h3 {
  font-size: var(--text-lg);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin: 0 0 var(--space-4);
}
@media (max-width: 768px) {
  .metrics-grid, .charts-row { grid-template-columns: 1fr; }
}
</style>
