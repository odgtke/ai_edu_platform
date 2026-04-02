<template>
  <div class="recommendation-page">
    <div class="page-header">
      <div class="header-main">
        <div>
          <h2>涓鎬ф帹鑽</h2>
          <p class="subtitle">鍩轰簬鎮ㄧ殑瀛︿範鍘嗗彶鍜屽叴瓒ｏ紝涓烘偍鎺ㄨ崘鏈閫傚悎鐨勫唴瀹</p>
        </div>
        <div class="header-actions">
          <el-button @click="goToKnowledgeDashboard">
            <el-icon><DataAnalysis /></el-icon>
            鎺屾彙搴﹀垎鏋
          </el-button>
        </div>
      </div>
    </div>
    
    <el-row :gutter="20">
      <!-- 涓鎬у寲鎺ㄨ崘 -->
      <el-col :span="16">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>棣冨箚 涓夌儤鍋嶆帹鑽</span>
              <el-button type="primary" text @click="refreshRecommendations">
                <el-icon><Refresh /></el-icon> 鍒犻攱鏌婃帹鑽
              </el-button>
            </div>
          </template>
          
          <div v-if="loading" class="loading-container">
            <el-skeleton :rows="5" animated />
          </div>
          
          <div v-else-if="recommendations.length === 0" class="empty-container">
            <el-empty description="鏆傛棤鎺ㄨ崘鍐呭癸拷">
              <el-button type="primary" @click="$router.push('/courses')">濞村繗锟借剧▼鈻</el-button>
            </el-empty>
          </div>
          
          <div v-else class="recommendation-list">
            <el-card
              v-for="item in recommendations"
              :key="item.recommendationId"
              class="recommendation-item"
              shadow="hover"
            >
              <div class="recommendation-main">
                <div class="recommendation-icon" :style="getIconStyle(item.itemType)">
                  {{ getIcon(item.itemType) }}
                </div>
                <div class="recommendation-info">
                  <div class="recommendation-title">{{ item.itemName }}</div>
                  <div class="recommendation-reason">
                    <el-icon><InfoFilled /></el-icon>
                    {{ item.reason }}
                  </div>
                  <div class="recommendation-score">
                    <el-rate
                      :model-value="item.score * 5"
                      disabled
                      :max="5"
                      show-score
                      :colors="['#99A9BF', '#F7BA2A', '#FF9900']"
                    />
                    <span class="match-rate">閸栧綊鍘ゅ害?{{ (item.score * 100).toFixed(0) }}%</span>
                  </div>
                </div>
              </div>
              <div class="recommendation-actions">
                <el-button type="primary" size="small" @click="viewDetail(item)">
                  鏌ョ湅璇︽儏
                </el-button>
                <el-button size="small" @click="recordBehavior(item, 'click')">
                  <el-icon><Star /></el-icon> 鏀惰棌
                </el-button>
              </div>
            </el-card>
          </div>
        </el-card>
        
        <!-- 閻戯拷妫鎺ㄨ崘 -->
        <el-card style="margin-top: 20px;">
          <template #header>
            <div class="card-header">
              <span>棣冩暉 閻戯拷妫璇剧▼鈻</span>
            </div>
          </template>
          
          <div v-if="trendingLoading" class="loading-container">
            <el-skeleton :rows="3" animated />
          </div>
          
          <div v-else class="trending-list">
            <div
              v-for="(item, index) in trending"
              :key="index"
              class="trending-item"
              @click="viewDetail(item)"
            >
              <div class="trending-rank" :class="{ 'top-three': index < 3 }">{{ index + 1 }}</div>
              <div class="trending-info">
                <div class="trending-title">{{ item.itemName }}</div>
                <div class="trending-meta">
                  <el-tag size="small" type="info">{{ item.itemType === 'course' ? '璇剧▼鈻' : '璧勬簮' }}</el-tag>
                  <span class="trending-score">閻戯拷瀹 {{ (item.score * 100).toFixed(0) }}</span>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <!-- 渚ц竟鏍?-->
      <el-col :span="8">
        <!-- 瀛︿範璺筹拷绶炴帹鑽 -->
        <el-card>
          <template #header>
            <div class="card-header">
              <span>棣冩础閿?瀛︿範璺筹拷绶</span>
            </div>
          </template>
          
          <div v-if="pathsLoading" class="loading-container">
            <el-skeleton :rows="3" animated />
          </div>
          
          <div v-else-if="learningPaths.length === 0" class="empty-container">
            <el-empty description="鏆傛棤瀛︿範璺筹拷绶炴帹鑽" />
          </div>
          
          <div v-else class="path-list">
            <el-timeline>
              <el-timeline-item
                v-for="(path, index) in learningPaths"
                :key="index"
                :type="index === 0 ? 'primary' : ''"
                :icon="index === 0 ? 'Star' : ''"
              >
                <div class="path-item">
                  <div class="path-title">{{ path.name || `瀛︿範璺筹拷绶 ${index + 1}` }}</div>
                  <div class="path-desc">{{ path.description || '绯荤粺鎺ㄨ崘閻ㄥ嫬锟芥稊鐘虹熅瀵? }}</div>
                  <el-button type="primary" size="small" text @click="startPath(path)">
                    寮鈧濮嬪锟芥稊?
                  </el-button>
                </div>
              </el-timeline-item>
            </el-timeline>
          </div>
        </el-card>
        
        <!-- 鎺ㄨ崘缁鐔伙拷 -->
        <el-card style="margin-top: 20px;">
          <template #header>
            <div class="card-header">
              <span>棣冩惓 鎺ㄨ崘缁鐔伙拷</span>
            </div>
          </template>
          <div class="statistics">
            <div class="stat-item">
              <div class="stat-value">{{ recommendationStats.totalRecommendations || 0 }}</div>
              <div class="stat-label">鎬荤粯甯归懡鎰鏆</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ recommendationStats.acceptedCount || 0 }}</div>
              <div class="stat-label">宸叉煡鍣扮痪?/div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ recommendationStats.clickRate || 0 }}%</div>
              <div class="stat-label">閻愮懓鍤閻?/div>
            </div>
          </div>
        </el-card>
        
        <!-- 閸嬪繐銈借剧疆 -->
        <el-card style="margin-top: 20px;">
          <template #header>
            <div class="card-header">
              <span>閳挎瑱绗 閸嬪繐銈借剧疆</span>
            </div>
          </template>
          <div class="preferences">
            <div class="preference-item">
              <span class="preference-label">閹扮喎鍙寸搾锝囨畱瀛︼妇锟</span>
              <el-select
                v-model="preferences.subjects"
                multiple
                placeholder="閫夊嬪ㄥ︼妇锟"
                style="width: 100%;"
                size="small"
              >
                <el-option label="鏁版澘锟" value="math" />
                <el-option label="璇凤拷鏋" value="chinese" />
                <el-option label="閼昏精锟" value="english" />
                <el-option label="鐗団晝鎮" value="physics" />
                <el-option label="閸栨牕锟" value="chemistry" />
              </el-select>
            </div>
            <div class="preference-item">
              <span class="preference-label">闅惧害閸嬪繐銈</span>
              <el-rate v-model="preferences.difficulty" :max="5" />
            </div>
            <el-button type="primary" size="small" @click="savePreferences" style="width: 100%;">
              淇濆瓨閸嬪繐銈
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { Refresh, InfoFilled, Star } from '@element-plus/icons-vue'

export default {
  name: 'Recommendation',
  components: { Refresh, InfoFilled, Star },
  data() {
    return {
      userId: 1, // 瀹氱偤妾搴︽柧绮犻惂璇茬秿鐘舵佲偓浣藉箯鍙?
      loading: false,
      trendingLoading: false,
      pathsLoading: false,
      recommendations: [],
      trending: [],
      learningPaths: [],
      recommendationStats: {},
      preferences: {
        subjects: [],
        difficulty: 3
      }
    }
  },
  mounted() {
    this.loadRecommendations()
    this.loadTrending()
    this.loadLearningPaths()
    this.loadStatistics()
  },
  methods: {
    async loadRecommendations() {
      this.loading = true
      try {
        const response = await fetch(/recommendations/personalized?userId=${this.userId}&limit=10`)
        const result = await response.json()
        if (result.code === 200 && result.data) {
          this.recommendations = result.data
        }
      } catch (error) {
        console.error('鍔犺浇鎺ㄨ崘澶辫触:', error)
        this.$message.error('鍔犺浇鎺ㄨ崘澶辫触')
      } finally {
        this.loading = false
      }
    },
    async loadTrending() {
      this.trendingLoading = true
      try {
        const response = await fetch('/recommendations/trending?limit=5')
        const result = await response.json()
        if (result.code === 200 && result.data) {
          this.trending = result.data
        }
      } catch (error) {
        console.error('鍔犺浇閻戯拷妫鎺ㄨ崘澶辫触:', error)
      } finally {
        this.trendingLoading = false
      }
    },
    async loadLearningPaths() {
      this.pathsLoading = true
      try {
        const response = await fetch(/recommendations/learning-paths?userId=${this.userId}`)
        const result = await response.json()
        if (result.code === 200 && result.data) {
          this.learningPaths = result.data
        }
      } catch (error) {
        console.error('鍔犺浇瀛︿範璺筹拷绶炲け璐:', error)
      } finally {
        this.pathsLoading = false
      }
    },
    async loadStatistics() {
      try {
        const response = await fetch(/recommendations/statistics?userId=${this.userId}`)
        const result = await response.json()
        if (result.code === 200 && result.data) {
          this.recommendationStats = result.data
        }
      } catch (error) {
        console.error('鍔犺浇鎺ㄨ崘缁鐔伙拷澶辫触:', error)
      }
    },
    async recordBehavior(item, behaviorType) {
      try {
        const response = await fetch(
          /recommendations/behavior?userId=${this.userId}&courseId=${item.itemId}&behaviorType=${behaviorType}&behaviorValue=1.0`,
          { method: 'POST' }
        )
        const result = await response.json()
        if (result.code === 200) {
          this.$message.success('宸茶尪锟藉綍鏇熷亶閻ㄥ嫬浜告總?)
        }
      } catch (error) {
        console.error('璁板綍鐞涘奔璐熷け璐:', error)
      }
    },
    async savePreferences() {
      try {
        // 淇濆瓨瀛︼妇锟介崑蹇撱偨
        for (const subject of this.preferences.subjects) {
          await fetch(
            /recommendations/preference?userId=${this.userId}&preferenceType=subject&preferenceValue=${subject}&score=0.8`,
            { method: 'POST' }
          )
        }
        this.$message.success('閸嬪繐銈借剧疆宸茶弓绻氬?)
        this.loadRecommendations()
      } catch (error) {
        console.error('淇濆瓨閸嬪繐銈藉け璐:', error)
        this.$message.error('淇濆瓨閸嬪繐銈藉け璐')
      }
    },
    refreshRecommendations() {
      this.loadRecommendations()
      this.$message.success('鎺ㄨ崘宸叉彃鍩涙柊?)
    },
    getIcon(type) {
      const icons = {
        course: '棣冩憥',
        video: '棣冨箑',
        article: '棣冩惈',
        exercise: '閴佸骏绗',
        assessment: '棣冩憫'
      }
      return icons[type] || '棣冩惗'
    },
    getIconStyle(type) {
      const colors = {
        course: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
        video: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
        article: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
        exercise: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',
        assessment: 'linear-gradient(135deg, #fa709a 0%, #fee140 100%)'
      }
      return {
        background: colors[type] || 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
      }
    },
    viewDetail(item) {
      this.recordBehavior(item, 'view')
      this.$message.info(`鏌ョ湅璇︽儏: ${item.itemName}`)
      // 鍙栵拷浜掕烦瀹犳祮鍒犳媽锟芥儏鍛淬
      // this.$router.push(`/course/${item.itemId}`)
    },
    startPath(path) {
      this.$message.info(`寮鈧濮嬪锟芥稊鐘虹熅瀵? ${path.name || '瀛︿範璺筹拷绶'}`)
      // 鍙栵拷浜掕烦瀹犳祮鍒犳澘锟芥稊鐘虹熅瀵板嫯锟芥儏鍛淬
      // this.$router.push(`/learning-path/${path.id}`)
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

.recommendation-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.recommendation-item :deep(.el-card__body) {
  padding: 20px;
}

.recommendation-main {
  display: flex;
  gap: 15px;
  margin-bottom: 15px;
}

.recommendation-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  flex-shrink: 0;
}

.recommendation-info {
  flex: 1;
}

.recommendation-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}

.recommendation-reason {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 13px;
  color: #909399;
  margin-bottom: 10px;
}

.recommendation-score {
  display: flex;
  align-items: center;
  gap: 15px;
}

.match-rate {
  font-size: 13px;
  color: #409EFF;
  font-weight: 600;
}

.recommendation-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
}

.trending-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.trending-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.3s;
}

.trending-item:hover {
  background: #f5f7fa;
}

.trending-rank {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: #e0e0e0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 600;
  color: #606266;
  flex-shrink: 0;
}

.trending-rank.top-three {
  background: #ff6b6b;
  color: #fff;
}

.trending-info {
  flex: 1;
}

.trending-title {
  font-size: 14px;
  color: #303133;
  margin-bottom: 4px;
}

.trending-meta {
  display: flex;
  gap: 10px;
  align-items: center;
}

.trending-score {
  font-size: 12px;
  color: #909399;
}

.path-list {
  padding: 10px 0;
}

.path-item {
  padding: 10px 0;
}

.path-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 5px;
}

.path-desc {
  font-size: 12px;
  color: #909399;
  margin-bottom: 10px;
  line-height: 1.4;
}

.statistics {
  display: flex;
  justify-content: space-around;
  text-align: center;
}

.stat-item {
  padding: 10px;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #409EFF;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 12px;
  color: #909399;
}

.preferences {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.preference-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.preference-label {
  font-size: 13px;
  color: #606266;
}
</style>
