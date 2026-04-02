<template>
  <div class="knowledge-dashboard">
    <div class="page-header">
      <h2>鐭ヨ瘑鎺屾彙搴﹀垎鏋</h2>
      <p class="subtitle">鍏ㄩ潰浜嗚В鎮ㄧ殑鐭ヨ瘑鎺屾彙鎯呭喌鍜屽彂灞曡秼鍔</p>
    </div>

    <!-- 姒傝堝崱鐗 -->
    <el-row :gutter="20" class="overview-cards">
      <el-col :span="6">
        <div class="stat-card" :style="{ background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' }">
          <div class="stat-icon">
            <el-icon><DataAnalysis /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ overview.totalPoints || 0 }}</div>
            <div class="stat-label">鐭ヨ瘑閻愯勨偓缁樻殶</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card" :style="{ background: 'linear-gradient(135deg, #11998e 0%, #38ef7d 100%)' }">
          <div class="stat-icon">
            <el-icon><TrendCharts /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ overview.avgMastery || 0 }}%</div>
            <div class="stat-label">骞村啿娼庢帉鎻″害?/div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card" :style="{ background: 'linear-gradient(135deg, #fc5c7d 0%, #6a82fb 100%)' }">
          <div class="stat-icon">
            <el-icon><Warning /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ overview.weakPoints || 0 }}</div>
            <div class="stat-label">闂団偓鍔熺姴宸遍悙?/div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card" :style="{ background: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)' }">
          <div class="stat-icon">
            <el-icon><Lightning /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ overview.strengthPoints || 0 }}</div>
            <div class="stat-label">浼犳ê濞嶆０鍡楃厵</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 涓夋槒锟藉垎鏋愰崠鍝勭厵 -->
    <el-row :gutter="20" class="analysis-section">
      <!-- 宸诧缚鏅堕敍姘舵祫杈撴儳娴 -->
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>棣冩惓 鍒犲棛琚鎺屾彙搴︼箓娴勮緭鎯ф禈</span>
              <el-button type="primary" size="small" @click="refreshRadarChart">
                <el-icon><Refresh /></el-icon> 鍒犻攱鏌
              </el-button>
            </div>
          </template>
          
          <div v-if="radarLoading" class="chart-placeholder">
            <el-skeleton :rows="8" animated />
          </div>
          <div v-else class="radar-chart-container">
            <div id="radarChart" class="radar-chart"></div>
          </div>
        </el-card>
      </el-col>

      <!-- 鍙栧厖鏅堕敍姘虫澖寮杈╃叀璇峰棛鍋 -->
      <el-col :span="12">
        <el-card class="weak-points-card">
          <template #header>
            <div class="card-header">
              <span>閳跨媴绗 閽栧嫬鎬ョ煡璇嗛悙?/span>
              <el-tag type="warning">{{ weakPoints.length }} 涓?/el-tag>
            </div>
          </template>
          
          <div v-if="weakPointsLoading" class="loading-container">
            <el-skeleton :rows="6" animated />
          </div>
          <div v-else-if="weakPoints.length === 0" class="empty-state">
            <el-empty description="鏆傛棤閽栧嫬鎬ョ煡璇嗛悙? />
          </div>
          <div v-else class="weak-points-list">
            <div 
              v-for="point in weakPoints" 
              :key="point.id"
              class="weak-point-item"
              @click="viewPointDetail(point)"
            >
              <div class="point-header">
                <span class="point-name">{{ point.name }}</span>
                <el-progress 
                  :percentage="point.mastery" 
                  :stroke-width="8"
                  status="exception"
                  class="mastery-progress"
                />
              </div>
              <div class="point-meta">
                <el-tag size="small" :type="getCategoryTagType(point.category)">
                  {{ point.category }}
                </el-tag>
                <span class="improvement-suggestion">{{ point.suggestion }}</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 璇凤妇绮忔帉鎻″害锕併冩爮?-->
    <el-card class="detail-table-card">
      <template #header>
        <div class="card-header">
          <span>棣冩惖 鐭ヨ瘑閻愮咃拷缁鍡樺笁鎻愨剝鍎忛崘?/span>
          <div class="table-controls">
            <el-select 
              v-model="selectedCategory" 
              placeholder="閫夊嬪ㄥ垹鍡欒" 
              clearable
              style="width: 150px; margin-right: 10px;"
              @change="filterByCategory"
            >
              <el-option 
                v-for="category in categories" 
                :key="category.value"
                :label="category.label"
                :value="category.value"
              />
            </el-select>
            <el-input
              v-model="searchKeyword"
              placeholder="鎼滄粎鍌ㄧ煡璇嗛悙?
              clearable
              style="width: 200px;"
              @input="searchPoints"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
          </div>
        </div>
      </template>

      <el-table 
        :data="filteredPoints" 
        stripe 
        style="width: 100%"
        v-loading="tableLoading"
      >
        <el-table-column prop="name" label="鐭ヨ瘑閻? width="200" />
        <el-table-column prop="category" label="鍒犲棛琚" width="120">
          <template #default="scope">
            <el-tag :type="getCategoryTagType(scope.row.category)">
              {{ scope.row.category }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="mastery" label="鎺屾彙搴? width="150">
          <template #default="scope">
            <el-progress 
              :percentage="scope.row.mastery" 
              :status="getMasteryStatus(scope.row.mastery)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="attempts" label="缁鍐х瘎濞嗏剝鏆" width="100" />
        <el-table-column prop="avgScore" label="骞冲潎寰楀垎" width="100">
          <template #default="scope">
            <span :class="getScoreClass(scope.row.avgScore)">
              {{ scope.row.avgScore }}鍒?
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="lastStudy" label="鏈熲偓鍚庡骸锟芥稊? width="150" />
        <el-table-column label="閹垮秳缍" width="120">
          <template #default="scope">
            <el-button 
              type="primary" 
              size="small" 
              text
              @click="studyPoint(scope.row)"
            >
              閲嶆柊瀛︿範
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
import { 
  DataAnalysis, TrendCharts, Warning, Lightning, 
  Refresh, Search 
} from '@element-plus/icons-vue'
import * as echarts from 'echarts'

export default {
  name: 'KnowledgeDashboard',
  components: {
    DataAnalysis, TrendCharts, Warning, Lightning, Refresh, Search
  },
  data() {
    return {
      // 鏁版嵁鐘舵佲偓?
      overview: {},
      radarData: [],
      weakPoints: [],
      allPoints: [],
      categories: [],
      
      // 鍔犺浇鐘舵佲偓?
      radarLoading: false,
      weakPointsLoading: false,
      tableLoading: false,
      
      // 绛涢夆偓澶嬫蒋浠?
      selectedCategory: '',
      searchKeyword: '',
      
      // 鍥涙崘銆冨紑鏇犳暏
      radarChart: null
    }
  },
  
  computed: {
    filteredPoints() {
      let result = [...this.allPoints]
      
      // 鍒嗙被绛涢夆偓?
      if (this.selectedCategory) {
        result = result.filter(point => point.category === this.selectedCategory)
      }
      
      // 鍏鎶芥暛璇峰秵鎮崇储?
      if (this.searchKeyword) {
        const keyword = this.searchKeyword.toLowerCase()
        result = result.filter(point => 
          point.name.toLowerCase().includes(keyword) ||
          point.category.toLowerCase().includes(keyword)
        )
      }
      
      return result
    }
  },
  
  mounted() {
    this.loadData()
    this.initRadarChart()
  },
  
  beforeUnmount() {
    if (this.radarChart) {
      this.radarChart.dispose()
    }
  },
  
  methods: {
    async loadData() {
      const userId = 1 // 瀹氱偤妾搴︽柧绮犻惂璇茬秿鐘舵佲偓浣藉箯鍙?
      
      // 骞存儼锟藉姞杞介幍鈧鏈熷嬫殶閹?
      Promise.all([
        this.loadOverview(userId),
        this.loadRadarData(userId),
        this.loadWeakPoints(userId),
        this.loadAllPoints(userId)
      ]).then(() => {
        this.updateRadarChart()
      })
    },
    
    async loadOverview(userId) {
      try {
        // 濡鈩冨珯API鐠嬪啰鏁
        this.overview = {
          totalPoints: 45,
          avgMastery: 72,
          weakPoints: 8,
          strengthPoints: 12
        }
      } catch (error) {
        console.error('鍔犺浇濮掑倽锟芥暟鎹澶辫触:', error)
      }
    },
    
    async loadRadarData(userId) {
      this.radarLoading = true
      try {
        // 濡鈩冨珯闂嗙柉鎻鍥涚偓鏆熼幑?
        this.radarData = [
          { name: '缂栨牜鈻奸崺铏癸拷', value: 85 },
          { name: '鏁版嵁缁鎾寸', value: 78 },
          { name: '绠辨佺《璁版崘锟', value: 65 },
          { name: '鍓嶅秶锟藉紑鈧鍙?, value: 92 },
          { name: '鍚庡海锟藉紑鈧鍙?, value: 70 },
          { name: '鏁版嵁搴?, value: 68 }
        ]
      } catch (error) {
        console.error('鍔犺浇闂嗙柉鎻鍥涚偓鏆熼幑锟姐亼璐?', error)
      } finally {
        this.radarLoading = false
      }
    },
    
    async loadWeakPoints(userId) {
      this.weakPointsLoading = true
      try {
        // 濡鈩冨珯閽栧嫬鎬ョ煡璇嗛悙瑙勬殶閹?
        this.weakPoints = [
          {
            id: 1,
            name: '鍔熴劍鈧浣斤拷鍒犳帞鐣诲▔?,
            category: '绠辨佺《璁版崘锟',
            mastery: 35,
            suggestion: '寤洪缚锟戒互搴ｇ病鍏闀愮伐妫版ê绱戝嬪绮屾稊?
          },
          {
            id: 2,
            name: '鏁版嵁搴︽挾鍌ㄥ紑鏇氱喘閸?,
            category: '鏁版嵁搴?,
            mastery: 42,
            suggestion: '闂団偓鐟曚礁濮炲紑铏规倞璁板搫锟芥稊鐘叉嫲瀹氱偠杩'
          },
          {
            id: 3,
            name: 'React Hooks杩涙盯妯',
            category: '鍓嶅秶锟藉紑鈧鍙?,
            mastery: 55,
            suggestion: '鍙栵拷浜掗夋俺绻冩い鍦娲板畾鐐村灛鎻愭劕宕'
          }
        ]
      } catch (error) {
        console.error('鍔犺浇閽栧嫬鎬ョ煡璇嗛悙鐟般亼璐?', error)
      } finally {
        this.weakPointsLoading = false
      }
    },
    
    async loadAllPoints(userId) {
      this.tableLoading = true
      try {
        // 濡鈩冨珯閹碘偓鏈熷岀叀璇峰棛鍋ｆ暟鎹
        this.allPoints = [
          { id: 1, name: 'JavaScript閸╄櫣锟', category: '缂栨牜鈻奸崺铏癸拷', mastery: 95, attempts: 12, avgScore: 92, lastStudy: '2024-03-15' },
          { id: 2, name: 'CSS鐢鍐ㄧ湰', category: '鍓嶅秶锟藉紑鈧鍙?, mastery: 88, attempts: 8, avgScore: 85, lastStudy: '2024-03-14' },
          { id: 3, name: 'Vue缁鍕娆㈤夋矮淇', category: '鍓嶅秶锟藉紑鈧鍙?, mastery: 92, attempts: 10, avgScore: 89, lastStudy: '2024-03-13' },
          { id: 4, name: '闁炬崘銆冮幙宥勭稊', category: '鏁版嵁缁鎾寸', mastery: 75, attempts: 6, avgScore: 78, lastStudy: '2024-03-12' },
          { id: 5, name: '浜哄苯寮舵爮鎴︿憾閸?, category: '鏁版嵁缁鎾寸', mastery: 82, attempts: 7, avgScore: 85, lastStudy: '2024-03-11' }
        ]
        
        // 鎻愭劕褰囧垹鍡欒
        this.categories = [...new Set(this.allPoints.map(p => p.category))].map(cat => ({
          label: cat,
          value: cat
        }))
      } catch (error) {
        console.error('鍔犺浇鐭ヨ瘑閻愮咃拷鎯呭懎銇戣触?', error)
      } finally {
        this.tableLoading = false
      }
    },
    
    initRadarChart() {
      this.$nextTick(() => {
        const chartDom = document.getElementById('radarChart')
        if (chartDom) {
          this.radarChart = echarts.init(chartDom)
          this.updateRadarChart()
        }
      })
    },
    
    updateRadarChart() {
      if (!this.radarChart || !this.radarData.length) return
      
      const option = {
        color: ['#667eea'],
        title: {
          text: '鐭ヨ瘑鎺屾彙搴︼箑鍨庣敮?,
          left: 'center',
          top: 10,
          textStyle: {
            fontSize: 16,
            fontWeight: 'bold',
            color: '#303133'
          }
        },
        tooltip: {
          trigger: 'item',
          formatter: function(params) {
            let result = `<strong>${params.name}</strong><br/>`
            params.value.forEach((val, idx) => {
              result += `${params.indicator[idx].name}: ${val}%<br/>`
            })
            return result
          }
        },
        radar: {
          indicator: this.radarData.map(item => ({
            name: item.name,
            max: 100
          })),
          radius: '55%',
          center: ['50%', '55%'],
          splitNumber: 5,
          axisName: {
            color: '#606266',
            fontSize: 12
          },
          splitLine: {
            lineStyle: {
              color: 'rgba(102, 126, 234, 0.2)'
            }
          },
          splitArea: {
            areaStyle: {
              color: ['rgba(102, 126, 234, 0.05)', 'rgba(102, 126, 234, 0.1)']
            }
          },
          axisLine: {
            lineStyle: {
              color: 'rgba(102, 126, 234, 0.3)'
            }
          }
        },
        series: [{
          type: 'radar',
          data: [{
            value: this.radarData.map(item => item.value),
            name: '鎺屾彙搴?,
            symbol: 'circle',
            symbolSize: 6,
            itemStyle: {
              color: '#667eea'
            },
            areaStyle: {
              color: 'rgba(102, 126, 234, 0.3)'
            },
            lineStyle: {
              width: 2,
              color: '#667eea'
            }
          }]
        }]
      }
      
      this.radarChart.setOption(option)
      
      // 娣诲姞閸濆秴绨插紑?
      window.addEventListener('resize', () => {
        this.radarChart && this.radarChart.resize()
      })
    },
    
    refreshRadarChart() {
      this.loadRadarData(1).then(() => {
        this.updateRadarChart()
      })
    },
    
    filterByCategory() {
      // 绛涢夆偓澶愨偓鏄忕帆宸叉彃婀猚omputed涓夛拷鐤勯悳?
    },
    
    searchPoints() {
      // 鎼滄粎鍌ㄩ夋槒绶宸叉彃婀猚omputed涓夛拷鐤勯悳?
    },
    
    viewPointDetail(point) {
      this.$message.info(`鏌ャ儳婀 ${point.name} 璇凤附鍎廯)
      // 鍙栵拷浜掕烦瀹犳祮鍒犳媽锟芥儏鍛淬夐棃?
    },
    
    studyPoint(point) {
      this.$message.success(`寮鈧濮嬪鍣告柊澧烇拷娑?${point.name}`)
      // 鍙栵拷浜掕烦瀹犳祮鍒犳澘锟芥稊鐘汇夐棃?
    },
    
    getCategoryTagType(category) {
      const typeMap = {
        '缂栨牜鈻奸崺铏癸拷': 'primary',
        '鏁版嵁缁鎾寸': 'success',
        '绠辨佺《璁版崘锟': 'warning',
        '鍓嶅秶锟藉紑鈧鍙?: 'danger',
        '鍚庡海锟藉紑鈧鍙?: '',
        '鏁版嵁搴?: 'info'
      }
      return typeMap[category] || ''
    },
    
    getMasteryStatus(mastery) {
      if (mastery >= 80) return 'success'
      if (mastery >= 60) return ''
      return 'exception'
    },
    
    getScoreClass(score) {
      if (score >= 90) return 'score-excellent'
      if (score >= 80) return 'score-good'
      if (score >= 60) return 'score-average'
      return 'score-poor'
    }
  }
}
</script>

<style scoped>
.knowledge-dashboard {
  padding: 20px;
}

.page-header {
  margin-bottom: 30px;
}

.page-header h2 {
  font-size: 28px;
  font-weight: 600;
  margin-bottom: 8px;
  color: #303133;
}

.subtitle {
  color: #909399;
  font-size: 14px;
}

.overview-cards {
  margin-bottom: 30px;
}

.stat-card {
  height: 120px;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  color: white;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  cursor: pointer;
}

.stat-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  flex-shrink: 0;
}

.stat-icon .el-icon {
  font-size: 28px;
  color: white;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  margin-bottom: 4px;
  color: white;
  line-height: 1;
}

.stat-label {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.9);
  font-weight: 500;
}

.analysis-section {
  margin-bottom: 30px;
}

.chart-card, .weak-points-card {
  height: 420px;
}

.chart-card :deep(.el-card__body) {
  height: calc(100% - 50px);
  padding: 15px;
}

.weak-points-card :deep(.el-card__body) {
  height: calc(100% - 50px);
  padding: 15px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
}

.chart-placeholder, .loading-container {
  padding: 20px;
}

.radar-chart-container {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.radar-chart {
  width: 100%;
  height: 100%;
  min-height: 340px;
}

.empty-state {
  padding: 40px 0;
  text-align: center;
}

.weak-points-list {
  max-height: 340px;
  overflow-y: auto;
}

.weak-point-item {
  padding: 15px;
  border-bottom: 1px solid #eee;
  cursor: pointer;
  transition: background-color 0.2s;
}

.weak-point-item:hover {
  background-color: #f5f7fa;
}

.point-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.point-name {
  font-weight: 500;
  font-size: 15px;
}

.mastery-progress {
  width: 120px;
}

.point-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.improvement-suggestion {
  font-size: 13px;
  color: #909399;
}

.detail-table-card {
  margin-bottom: 20px;
}

.table-controls {
  display: flex;
  align-items: center;
}

.score-excellent { color: #67c23a; font-weight: 500; }
.score-good { color: #409eff; }
.score-average { color: #e6a23c; }
.score-poor { color: #f56c6c; font-weight: 500; }
</style>
