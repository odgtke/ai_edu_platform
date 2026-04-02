<template>
  <div class="student-behavior">
    <!-- 椤甸潰鏍囬橈拷 -->
    <div class="page-header">
      <div class="header-content">
        <div class="header-breadcrumb">
          <el-button link @click="$router.push('/analytics')">
            <el-icon><ArrowLeft /></el-icon>
            杩涙柨娲栧垎鏋愪笁锟界妇
          </el-button>
        </div>
        <h1 class="page-title">瀛︾敓瀛︿範琛屼负鍒嗘瀽</h1>
        <p class="page-subtitle">娣卞叆浜嗚В瀛︾敓瀛︿範涔犳儻銆佹椿璺冨害鍜岀煡璇嗘帉鎻℃儏鍐</p>
      </div>
      <div class="header-actions">
        <el-select v-model="selectedClass" placeholder="閫夋嫨鐝绾" style="width: 150px">
          <el-option label="鍏ㄩ儴鐝绾" value="" />
          <el-option label="杞浠跺伐绋1鐝" value="1" />
          <el-option label="杞浠跺伐绋2鐝" value="2" />
          <el-option label="璁＄畻鏈虹戝1鐝" value="3" />
        </el-select>
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="鑷?
          start-placeholder="寮鈧濮嬪妫╂湡?
          end-placeholder="缁鎾存将鏃ャ儲婀"
        />
        <el-button type="primary" class="btn-gradient">
          <el-icon><Download /></el-icon>
          瀵煎嚭閹躲儱鎲
        </el-button>
      </div>
    </div>

    <!-- 鏍忕跨妇鎸佸洦鐖 -->
    <div class="metrics-grid">
      <div class="metric-card" v-for="(metric, index) in coreMetrics" :key="index">
        <div class="metric-icon" :class="metric.iconClass">
          <el-icon><component :is="metric.icon" /></el-icon>
        </div>
        <div class="metric-content">
          <span class="metric-label">{{ metric.label }}</span>
          <span class="metric-value">{{ metric.value }}</span>
          <div class="metric-trend" :class="metric.trend > 0 ? 'up' : 'down'">
            <el-icon><ArrowUp v-if="metric.trend > 0" /><ArrowDown v-else /></el-icon>
            <span>杈撳啩绗傚懆?{{ Math.abs(metric.trend) }}%</span>
          </div>
        </div>
        <div class="metric-chart">
          <div ref="`sparkline${index}`" class="sparkline"></div>
        </div>
      </div>
    </div>

    <!-- 鍥涙崘銆冮崠鍝勭厵 -->
    <div class="charts-grid">
      <!-- 瀛︿範鏃ュ爼鏆辩搾瀣濞 -->
      <div class="chart-card large">
        <div class="chart-header">
          <div class="chart-title">
            <h3>瀛︿範鏃ュ爼鏆辩搾瀣濞嶅垎鏋</h3>
            <p>瀛︼妇鏁撳В蹇旀）瀛︿範鏃ュ爼鏆卞彇妯哄茬搾瀣濞</p>
          </div>
          <el-radio-group v-model="timeRange" size="small">
            <el-radio-button label="week">鏈燂拷鎳</el-radio-button>
            <el-radio-button label="month">鏈燂拷婀</el-radio-button>
            <el-radio-button label="semester">鏈燂拷锟芥湡?/el-radio-button>
          </el-radio-group>
        </div>
        <div ref="learningTrendChart" class="chart-container"></div>
      </div>

      <!-- 瀛︿範鏃ヨ埖锟藉垹鍡楃 -->
      <div class="chart-card">
        <div class="chart-header">
          <div class="chart-title">
            <h3>瀛︿範鏃ヨ埖锟介崑蹇撱偨</h3>
            <p>瀛︼妇鏁撲笁鈧鍍忊晙鑵戝︿範濞叉槒绌搴︼箑鍨庣敮?/p>
          </div>
        </div>
        <div ref="timePreferenceChart" class="chart-container"></div>
      </div>

      <!-- 鐭ヨ瘑鎺屾彙搴︼箓娴勮緭鎯ф禈 -->
      <div class="chart-card">
        <div class="chart-header">
          <div class="chart-title">
            <h3>鐭ヨ瘑鎺屾彙搴︼箒鐦庝紶?/h3>
            <p>鍚庡嫮鐓¤峰棛鍋ｆ帉鎻℃儏鍛鍠岄梿鐤鎻鍥?/p>
          </div>
        </div>
        <div ref="knowledgeRadarChart" class="chart-container"></div>
      </div>

      <!-- 瀛︿範鐞涘奔璐熷曞繑鏋 -->
      <div class="chart-card">
        <div class="chart-header">
          <div class="chart-title">
            <h3>瀛︿範鐞涘奔璐熷曞繑鏋</h3>
            <p>浠ュ氦锟介梻锟藉煂瀹氬矁锟介惃鍕娴嗛崠鏍у瀻閺?/p>
          </div>
        </div>
        <div ref="funnelChart" class="chart-container"></div>
      </div>

      <!-- 瀛︼妇鏁撳ú鏄忕┈搴︼附甯撶悰?-->
      <div class="chart-card">
        <div class="chart-header">
          <div class="chart-title">
            <h3>瀛︿範濞叉槒绌搴︼附甯撶悰?/h3>
            <p>鏈燂拷鎳嗗︿範鏃ュ爼鏆盩OP10瀛︼妇鏁</p>
          </div>
        </div>
        <div class="student-ranking">
          <div class="rank-item" v-for="(student, index) in topStudents" :key="index">
            <div class="rank-number" :class="'rank-' + (index + 1)">{{ index + 1 }}</div>
            <el-avatar :size="36" :src="student.avatar" />
            <div class="rank-info">
              <span class="student-name">{{ student.name }}</span>
              <span class="student-class">{{ student.class }}</span>
            </div>
            <div class="rank-stats">
              <span class="study-time">{{ student.hours }}灏忔椂</span>
              <el-progress 
                :percentage="student.progress" 
                :color="getProgressColor(index)"
                :show-text="false"
                style="width: 80px"
              />
            </div>
          </div>
        </div>
      </div>

      <!-- 璇剧▼鈻煎畾宀冿拷閻?-->
      <div class="chart-card">
        <div class="chart-header">
          <div class="chart-title">
            <h3>璇剧▼鈻煎畾宀冿拷閻滃洨绮鸿?/h3>
            <p>鍚庡嫯锟界粙瀣锟介悽鐔风暚璇风偓鍎忛崘闈涘瀻鐢?/p>
          </div>
        </div>
        <div ref="completionChart" class="chart-container"></div>
      </div>
    </div>

    <!-- 瀛︿範鐞涘奔璐熷ú鐐茬檪 -->
    <div class="insights-section">
      <div class="section-header">
        <h2>鏅鸿兘濞茬偛鐧</h2>
        <el-tag type="primary">AI 閻㈢喐鍨</el-tag>
      </div>
      <div class="insights-grid">
        <div class="insight-card" v-for="(insight, index) in insights" :key="index" :class="insight.type">
          <div class="insight-icon">
            <el-icon><component :is="insight.icon" /></el-icon>
          </div>
          <div class="insight-content">
            <h4>{{ insight.title }}</h4>
            <p>{{ insight.description }}</p>
            <div class="insight-action">
              <el-button link type="primary">鏌ョ湅璇︽儏</el-button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import {
  ArrowLeft, Download, ArrowUp, ArrowDown,
  User, Clock, Reading, TrendCharts, Star, Warning, SuccessFilled
} from '@element-plus/icons-vue'
import * as echarts from 'echarts'

export default {
  name: 'StudentBehavior',
  components: {
    ArrowLeft, Download, ArrowUp, ArrowDown,
    User, Clock, Reading, TrendCharts, Star, Warning, SuccessFilled
  },
  data() {
    return {
      selectedClass: '',
      dateRange: [],
      timeRange: 'week',
      coreMetrics: [
        { icon: 'User', iconClass: 'blue', label: '濞叉槒绌瀛︾敓鏁?, value: '1,248', trend: 8.5 },
        { icon: 'Clock', iconClass: 'green', label: '浜哄搫娼庡︿範鏃ュ爼鏆', value: '4.2h', trend: 12.3 },
        { icon: 'Reading', iconClass: 'orange', label: '璇剧▼鈻煎彇鍌欑瑢閻?, value: '86.5%', trend: -2.1 },
        { icon: 'TrendCharts', iconClass: 'purple', label: '瀹氬矁锟介悳?, value: '72.8%', trend: 5.7 }
      ],
      topStudents: [
        { name: '寮鐘辩瑏', class: '杞斤拷娆㈠凡銉р柤1閻?, hours: 42.5, progress: 95, avatar: '' },
        { name: '鏉熷骸娲', class: '杞斤拷娆㈠凡銉р柤1閻?, hours: 38.2, progress: 88, avatar: '' },
        { name: '閻滃╃安', class: '璁帮紕鐣绘湡铏癸拷瀛?閻?, hours: 35.7, progress: 82, avatar: '' },
        { name: '鐠ч潧鍙', class: '杞斤拷娆㈠凡銉р柤2閻?, hours: 33.4, progress: 78, avatar: '' },
        { name: '閽熷彉绔', class: '璁帮紕鐣绘湡铏癸拷瀛?閻?, hours: 31.8, progress: 75, avatar: '' },
        { name: '瀛︽瑥鍙', class: '杞斤拷娆㈠凡銉р柤1閻?, hours: 29.5, progress: 70, avatar: '' },
        { name: '鍛ㄣ劋绡', class: '杞斤拷娆㈠凡銉р柤2閻?, hours: 28.3, progress: 68, avatar: '' },
        { name: '鍚庢潙宕', class: '璁帮紕鐣绘湡铏癸拷瀛?閻?, hours: 26.7, progress: 65, avatar: '' }
      ],
      insights: [
        {
          type: 'success',
          icon: 'SuccessFilled',
          title: '瀛︿範缁夛拷鐎鎬谎勫絹鍗?,
          description: '鏈燂拷鎳嗗︼妇鏁撳勾鍐叉綆瀛︿範鏃ュ爼鏆辫緭鍐х瑐鍛ㄣ劍褰佸崱?2.3%閿涘本娅勯梻鏉戯拷娑旂姵妞傚▓鍨妞胯烦鍐ㄥ抽弰搴㈡▔澧炵偛濮為妴?
        },
        {
          type: 'warning',
          icon: 'Warning',
          title: '瀹氬矁锟介悳鍥娓跺叚铏鏁',
          description: '閵嗗﹪鐝缁狙呯暬濞夋洝锟借奥扳偓瀣锟界粙瀣鐣璇剧▼宸间互鍛璐58%閿涘苯缂撹帮拷鏆鐢鍫濆彠濞夈劌锟介悽鐔峰冀妫ｅ牆鑻熺拫鍐╂殻鏁版瑥锟介懞鍌氾拷閵?
        },
        {
          type: 'info',
          icon: 'Star',
          title: '浼犳硷拷瀛︿範濮掓粍鐗',
          description: '寮鐘辩瑏鍚庡苯锟芥湡锟芥噯瀛︿範鏃ュ爼鏆42.5灏忔椂閿涘矁绻涚画?鍛ㄣ劋缍呯仦鍛锟芥＃鏍电礉鍙栵拷缍斾笁鍝勶拷娑旂姵鐖ｅ叚浣冪箻鐞涘矁銆冨綍鑸鈧?
        }
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
      this.initLearningTrendChart()
      this.initTimePreferenceChart()
      this.initKnowledgeRadarChart()
      this.initFunnelChart()
      this.initCompletionChart()
    },
    initLearningTrendChart() {
      const chart = echarts.init(this.$refs.learningTrendChart)
      this.charts.learningTrend = chart

      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'cross' }
        },
        legend: {
          data: ['骞村啿娼庡︿範鏃ュ爼鏆', '濞叉槒绌瀛︾敓鏁?],
          bottom: 0
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '12%',
          top: '8%',
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
            name: '鏃ュ爼鏆(灏忔椂)',
            axisLine: { show: false },
            axisTick: { show: false },
            axisLabel: { color: '#64748b' },
            splitLine: { lineStyle: { color: '#f1f5f9' } }
          },
          {
            type: 'value',
            name: '瀛︾敓鏁?,
            axisLine: { show: false },
            axisTick: { show: false },
            axisLabel: { color: '#64748b' },
            splitLine: { show: false }
          }
        ],
        series: [
          {
            name: '骞村啿娼庡︿範鏃ュ爼鏆',
            type: 'line',
            smooth: true,
            data: [3.2, 3.8, 4.1, 3.5, 4.5, 5.2, 4.8],
            itemStyle: { color: '#3b82f6' },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(59, 130, 246, 0.3)' },
                { offset: 1, color: 'rgba(59, 130, 246, 0.05)' }
              ])
            }
          },
          {
            name: '濞叉槒绌瀛︾敓鏁?,
            type: 'bar',
            yAxisIndex: 1,
            data: [980, 1050, 1120, 1080, 1150, 1248, 1180],
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: '#10b981' },
                { offset: 1, color: '#34d399' }
              ]),
              borderRadius: [4, 4, 0, 0]
            }
          }
        ]
      }

      chart.setOption(option)
    },
    initTimePreferenceChart() {
      const chart = echarts.init(this.$refs.timePreferenceChart)
      this.charts.timePreference = chart

      const option = {
        tooltip: { trigger: 'axis' },
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
          axisName: { color: '#64748b', fontSize: 11 },
          splitLine: {
            lineStyle: {
              color: ['rgba(59, 130, 246, 0.1)', 'rgba(59, 130, 246, 0.2)', 'rgba(59, 130, 246, 0.3)', 'rgba(59, 130, 246, 0.4)'].reverse()
            }
          },
          splitArea: { show: false },
          axisLine: { lineStyle: { color: 'rgba(59, 130, 246, 0.3)' } }
        },
        series: [{
          type: 'radar',
          data: [{
            value: [25, 85, 65, 95, 40, 8],
            name: '瀛︿範濞叉槒绌搴?,
            areaStyle: {
              color: new echarts.graphic.RadialGradient(0.5, 0.5, 1, [
                { offset: 0, color: 'rgba(59, 130, 246, 0.1)' },
                { offset: 1, color: 'rgba(59, 130, 246, 0.5)' }
              ])
            },
            lineStyle: { color: '#3b82f6', width: 2 },
            itemStyle: { color: '#3b82f6' }
          }]
        }]
      }

      chart.setOption(option)
    },
    initKnowledgeRadarChart() {
      const chart = echarts.init(this.$refs.knowledgeRadarChart)
      this.charts.knowledgeRadar = chart

      const option = {
        tooltip: {},
        legend: {
          data: ['閻濓拷楠囧勾鍐叉綆', '骞寸骇骞村啿娼'],
          bottom: 0
        },
        radar: {
          indicator: [
            { name: '閸╄櫣锟界煡璇', max: 100 },
            { name: '缂栨牜鈻艰兘鍔', max: 100 },
            { name: '绠辨佺《鎬绘繄娣', max: 100 },
            { name: '妞ゅ湱娲板畾鐐村灛', max: 100 },
            { name: '鍥涖垽妲﹀崱蹇庣稊', max: 100 },
            { name: '鍒犳稒鏌婃绘繄娣', max: 100 }
          ],
          shape: 'polygon',
          splitNumber: 4,
          axisName: { color: '#64748b' },
          splitLine: { lineStyle: { color: '#e2e8f0' } },
          splitArea: {
            areaStyle: {
              color: ['rgba(59, 130, 246, 0.05)', 'rgba(59, 130, 246, 0.1)']
            }
          }
        },
        series: [{
          type: 'radar',
          data: [
            {
              value: [85, 78, 72, 80, 88, 75],
              name: '閻濓拷楠囧勾鍐叉綆',
              areaStyle: { color: 'rgba(59, 130, 246, 0.3)' },
              lineStyle: { color: '#3b82f6', width: 2 },
              itemStyle: { color: '#3b82f6' }
            },
            {
              value: [80, 75, 70, 75, 82, 70],
              name: '骞寸骇骞村啿娼',
              areaStyle: { color: 'rgba(16, 185, 129, 0.2)' },
              lineStyle: { color: '#10b981', width: 2, type: 'dashed' },
              itemStyle: { color: '#10b981' }
            }
          ]
        }]
      }

      chart.setOption(option)
    },
    initFunnelChart() {
      const chart = echarts.init(this.$refs.funnelChart)
      this.charts.funnel = chart

      const option = {
        tooltip: {
          trigger: 'item',
          formatter: '{b}: {c} ({d}%)'
        },
        series: [{
          type: 'funnel',
          left: '10%',
          top: '10%',
          bottom: '10%',
          width: '80%',
          min: 0,
          max: 100,
          minSize: '0%',
          maxSize: '100%',
          sort: 'descending',
          gap: 2,
          label: {
            show: true,
            position: 'inside',
            formatter: '{b}\n{c}%'
          },
          itemStyle: {
            borderColor: '#fff',
            borderWidth: 1
          },
          emphasis: {
            label: { fontSize: 14 }
          },
          data: [
            { value: 100, name: '璁板潡妫惰剧▼鈻', itemStyle: { color: '#3b82f6' } },
            { value: 85, name: '寮鈧濮嬪锟芥稊?, itemStyle: { color: '#60a5fa' } },
            { value: 65, name: '瀹屾垚50%', itemStyle: { color: '#93c5fd' } },
            { value: 45, name: '瀹屾垚80%', itemStyle: { color: '#bfdbfe' } },
            { value: 32, name: '瀹氬矁锟', itemStyle: { color: '#dbeafe' } }
          ]
        }]
      }

      chart.setOption(option)
    },
    initCompletionChart() {
      const chart = echarts.init(this.$refs.completionChart)
      this.charts.completion = chart

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
          max: 100,
          axisLabel: { formatter: '{value}%', color: '#64748b' },
          splitLine: { lineStyle: { color: '#f1f5f9' } }
        },
        yAxis: {
          type: 'category',
          data: ['Python鍏銉╂，', 'Java閸╄櫣锟', 'Web鍓嶅秶锟', '鏁版嵁搴?, '绠辨佺《璁版崘锟', 'AI閸╄櫣锟'],
          axisLine: { show: false },
          axisTick: { show: false },
          axisLabel: { color: '#64748b' }
        },
        series: [{
          type: 'bar',
          data: [92, 88, 85, 78, 65, 82],
          itemStyle: {
            color: (params) => {
              const colors = ['#10b981', '#10b981', '#3b82f6', '#3b82f6', '#f59e0b', '#3b82f6']
              return colors[params.dataIndex]
            },
            borderRadius: [0, 4, 4, 0]
          },
          barWidth: '60%',
          label: {
            show: true,
            position: 'right',
            formatter: '{c}%',
            color: '#64748b'
          }
        }]
      }

      chart.setOption(option)
    },
    getProgressColor(index) {
      const colors = ['#f59e0b', '#94a3b8', '#cd7f32', '#3b82f6', '#3b82f6', '#3b82f6', '#3b82f6', '#3b82f6']
      return colors[index] || '#3b82f6'
    },
    handleResize() {
      Object.values(this.charts).forEach(chart => chart.resize())
    }
  }
}
</script>

<style scoped>
.student-behavior {
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

.header-breadcrumb {
  margin-bottom: var(--space-3);
}

.header-breadcrumb .el-button {
  color: var(--text-secondary);
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
}

/* 鏍忕跨妇鎸佸洦鐖 */
.metrics-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
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

.metric-icon.blue {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  color: white;
}

.metric-icon.green {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: white;
}

.metric-icon.orange {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
  color: white;
}

.metric-icon.purple {
  background: linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%);
  color: white;
}

.metric-icon .el-icon {
  font-size: 28px;
}

.metric-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.metric-label {
  font-size: var(--text-sm);
  color: var(--text-secondary);
}

.metric-value {
  font-size: var(--text-2xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
}

.metric-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: var(--text-xs);
}

.metric-trend.up {
  color: var(--success-600);
}

.metric-trend.down {
  color: var(--error-600);
}

.metric-chart {
  width: 80px;
  height: 40px;
}

.sparkline {
  width: 100%;
  height: 100%;
}

/* 鍥涙崘銆冪純鎴炵壐 */
.charts-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--space-5);
  margin-bottom: var(--space-6);
}

.chart-card {
  background: white;
  border-radius: var(--radius-xl);
  padding: var(--space-5);
  box-shadow: var(--shadow-card);
}

.chart-card.large {
  grid-column: span 2;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: var(--space-4);
}

.chart-title h3 {
  font-size: var(--text-base);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin: 0 0 4px;
}

.chart-title p {
  font-size: var(--text-xs);
  color: var(--text-tertiary);
  margin: 0;
}

.chart-container {
  height: 280px;
}

/* 瀛︼妇鏁撻幒鎺曪拷 */
.student-ranking {
  display: flex;
  flex-direction: column;
  gap: var(--space-3);
  max-height: 280px;
  overflow-y: auto;
}

.rank-item {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-2);
  border-radius: var(--radius-md);
  transition: all var(--transition-fast);
}

.rank-item:hover {
  background: var(--bg-secondary);
}

.rank-number {
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
.rank-4, .rank-5, .rank-6, .rank-7, .rank-8 { background: var(--gray-200); color: var(--gray-600); }

.rank-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.student-name {
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  color: var(--text-primary);
}

.student-class {
  font-size: var(--text-xs);
  color: var(--text-tertiary);
}

.rank-stats {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
}

.study-time {
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  color: var(--primary-600);
}

/* 濞茬偛鐧傞崠鍝勭厵 */
.insights-section {
  background: white;
  border-radius: var(--radius-xl);
  padding: var(--space-6);
  box-shadow: var(--shadow-card);
}

.section-header {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  margin-bottom: var(--space-5);
}

.section-header h2 {
  font-size: var(--text-lg);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin: 0;
}

.insights-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--space-5);
}

.insight-card {
  display: flex;
  gap: var(--space-4);
  padding: var(--space-5);
  border-radius: var(--radius-lg);
  border-left: 4px solid;
}

.insight-card.success {
  background: var(--success-50);
  border-color: var(--success-500);
}

.insight-card.warning {
  background: var(--warning-50);
  border-color: var(--warning-500);
}

.insight-card.info {
  background: var(--primary-50);
  border-color: var(--primary-500);
}

.insight-icon {
  width: 40px;
  height: 40px;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.insight-card.success .insight-icon {
  background: var(--success-100);
  color: var(--success-600);
}

.insight-card.warning .insight-icon {
  background: var(--warning-100);
  color: var(--warning-600);
}

.insight-card.info .insight-icon {
  background: var(--primary-100);
  color: var(--primary-600);
}

.insight-icon .el-icon {
  font-size: 20px;
}

.insight-content {
  flex: 1;
}

.insight-content h4 {
  font-size: var(--text-base);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin: 0 0 var(--space-2);
}

.insight-content p {
  font-size: var(--text-sm);
  color: var(--text-secondary);
  margin: 0 0 var(--space-3);
  line-height: 1.6;
}

/* 閸濆秴绨插紑?*/
@media (max-width: 1200px) {
  .metrics-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .charts-grid {
    grid-template-columns: 1fr;
  }
  
  .chart-card.large {
    grid-column: span 1;
  }
  
  .insights-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .metrics-grid {
    grid-template-columns: 1fr;
  }
  
  .page-header {
    flex-direction: column;
    gap: var(--space-4);
  }
  
  .header-actions {
    flex-wrap: wrap;
  }
}
</style>
