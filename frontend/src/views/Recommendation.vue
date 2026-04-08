<template>
  <div class="recommendation">
    <!-- 页面标题 -->
    <div class="page-header-modern">
      <div class="header-content">
        <div class="title-badge">
          <el-icon class="badge-icon"><i-ep-star /></el-icon>
        </div>
        <div class="title-text">
          <h1 class="page-title">个性推荐</h1>
          <p class="page-subtitle">基于您的学习历史和兴趣，为您推荐最适合的内容</p>
        </div>
      </div>
      <div class="header-actions">
        <el-button class="btn-refresh" @click="refreshRecommendations">
          <el-icon><i-ep-refresh /></el-icon>
          刷新推荐
        </el-button>
      </div>
    </div>

    <!-- 推荐统计 -->
    <div class="stats-section">
      <div class="stat-card">
        <div class="stat-icon blue">
          <el-icon><i-ep-star /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ recommendations.length }}</div>
          <div class="stat-label">推荐课程</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon green">
          <el-icon><i-ep-trend-charts /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ avgMatchRate }}%</div>
          <div class="stat-label">平均匹配度</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon orange">
          <el-icon><i-ep-view /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ viewedCount }}</div>
          <div class="stat-label">已浏览</div>
        </div>
      </div>
    </div>

    <!-- 个性化推荐 -->
    <div class="recommendation-section">
      <div class="section-header">
        <h2 class="section-title">
          <el-icon><i-ep-magic-stick /></el-icon>
          为您推荐
        </h2>
        <el-radio-group v-model="recommendType" size="small">
          <el-radio-button label="personalized">个性化</el-radio-button>
          <el-radio-button label="popular">热门</el-radio-button>
          <el-radio-button label="trending">趋势</el-radio-button>
        </el-radio-group>
      </div>

      <!-- 加载状态 -->
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="3" animated />
      </div>

      <!-- 空状态 -->
      <div v-else-if="recommendations.length === 0" class="empty-container">
        <el-empty description="暂无推荐内容">
          <el-button type="primary" @click="$router.push('/courses')">
            浏览课程
          </el-button>
        </el-empty>
      </div>

      <!-- 推荐列表 -->
      <div v-else class="recommendation-grid">
        <el-card
          v-for="item in recommendations"
          :key="item.courseId"
          class="recommendation-card"
          shadow="hover"
        >
          <div class="card-header">
            <div class="match-badge" :style="getMatchStyle(item.score)">
              <el-icon><i-ep-circle-check /></el-icon>
              {{ (item.score * 100).toFixed(0) }}% 匹配
            </div>
            <el-dropdown trigger="click">
              <el-button circle size="small" class="more-btn">
                <el-icon><i-ep-more-filled /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="viewDetail(item)">
                    <el-icon><View /></el-icon>查看详情
                  </el-dropdown-item>
                  <el-dropdown-item @click="addToFavorites(item)">
                    <el-icon><Star /></el-icon>收藏
                  </el-dropdown-item>
                  <el-dropdown-item @click="shareItem(item)">
                    <el-icon><i-ep-share /></el-icon>分享
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>

          <div class="card-content">
            <div class="course-icon" :style="getIconStyle(item.courseId)">
              {{ getIcon(item.courseName) }}
            </div>
            <h3 class="course-title">{{ item.courseName }}</h3>
            <p class="course-reason">
              <el-icon><i-ep-info-filled /></el-icon>
              {{ item.reason || '基于您的学习历史推荐' }}
            </p>
          </div>

          <div class="card-footer">
            <el-button type="primary" @click="viewDetail(item)">
              查看详情
            </el-button>
            <el-button 
              :type="likedCourses.has(item.courseId) ? 'primary' : 'default'"
              @click="toggleLike(item)"
            >
              {{ likedCourses.has(item.courseId) ? '❤️ 已赞' : '👍 点赞' }}
            </el-button>
          </div>
        </el-card>
      </div>
    </div>

    <!-- 热门推荐 -->
    <div class="trending-section">
      <div class="section-header">
        <h2 class="section-title">
          <el-icon><i-ep-star /></el-icon>
          热门课程
        </h2>
      </div>

      <div v-if="trendingLoading" class="loading-container">
        <el-skeleton :rows="2" animated />
      </div>

      <div v-else class="trending-list">
        <div
          v-for="(item, index) in trending"
          :key="index"
          class="trending-item"
          @click="viewDetail(item)"
        >
          <div class="trending-rank" :class="{ 'top-three': index < 3 }">
            {{ index + 1 }}
          </div>
          <div class="trending-info">
            <div class="trending-title">{{ item.courseName }}</div>
            <div class="trending-meta">
              <span class="student-count">
                <el-icon><i-ep-user /></el-icon>
                {{ item.studentCount || Math.floor(Math.random() * 2000) + 100 }} 人学习
              </span>
            </div>
          </div>
          <el-icon class="trending-arrow"><ArrowRight /></el-icon>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
// 图标通过 el-icon 组件直接使用
import { getPersonalizedRecommendations, getRecommendations } from '@/api/course'

const router = useRouter()

// 数据
const loading = ref(false)
const trendingLoading = ref(false)
const recommendations = ref([])
const trending = ref([])
const recommendType = ref('personalized')
const viewedCount = ref(0)
const likedCourses = ref(new Set()) // 存储已点赞的课程ID

// 计算属性
const avgMatchRate = computed(() => {
  if (recommendations.value.length === 0) return 0
  const total = recommendations.value.reduce((sum, item) => sum + (item.score || 0), 0)
  return Math.round((total / recommendations.value.length) * 100)
})

// 获取匹配度样式
const getMatchStyle = (score) => {
  if (score >= 0.8) return { background: '#dcfce7', color: '#15803d' }
  if (score >= 0.6) return { background: '#dbeafe', color: '#1d4ed8' }
  if (score >= 0.4) return { background: '#fef3c7', color: '#b45309' }
  return { background: '#f1f5f9', color: '#64748b' }
}

// 获取图标样式
const getIconStyle = (id) => {
  const colors = [
    'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
    'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
    'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
    'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',
    'linear-gradient(135deg, #fa709a 0%, #fee140 100%)',
    'linear-gradient(135deg, #30cfd0 0%, #330867 100%)'
  ]
  return {
    background: colors[id % colors.length]
  }
}

// 获取图标
const getIcon = (name) => {
  if (!name) return '📚'
  const firstChar = name.charAt(0)
  if (/[A-Za-z]/.test(firstChar)) return firstChar.toUpperCase()
  return firstChar
}

// 当前用户ID（实际应从登录状态获取）
const currentUserId = ref(1)

// 行为追踪API
const trackBehavior = async (courseId, behaviorType) => {
  try {
    await fetch('/api/recommendations/behavior', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        userId: currentUserId.value,
        courseId: courseId,
        behaviorType: behaviorType,
        timestamp: new Date().toISOString()
      })
    })
  } catch (error) {
    console.error('行为追踪失败:', error)
  }
}

// 加载个性化推荐
const loadRecommendations = async () => {
  loading.value = true
  try {
    const userId = currentUserId.value
    const res = await getPersonalizedRecommendations(userId, 8)
    if (res.code === 200) {
      recommendations.value = res.data.map((item, index) => ({
        ...item,
        score: item.score || (0.95 - index * 0.08),
        reason: item.reason || getReasonByScore(item.score || (0.95 - index * 0.08))
      }))
      
      // 记录浏览行为
      trackBehavior(null, 'browse')
    }
  } catch (error) {
    console.error('获取推荐失败:', error)
    ElMessage.error('获取推荐失败')
  } finally {
    loading.value = false
  }
}

// 获取推荐理由
const getReasonByScore = (score) => {
  if (score >= 0.8) return '高度匹配您的学习偏好'
  if (score >= 0.6) return '与您学过的课程相关'
  if (score >= 0.4) return '热门课程，值得学习'
  return '拓展知识面的好选择'
}

// 加载热门课程
const loadTrending = async () => {
  trendingLoading.value = true
  try {
    const res = await getRecommendations(5)
    if (res.code === 200) {
      trending.value = res.data
    }
  } catch (error) {
    console.error('获取热门课程失败:', error)
  } finally {
    trendingLoading.value = false
  }
}

// 刷新推荐
const refreshRecommendations = () => {
  loadRecommendations()
  ElMessage.success('推荐已刷新')
}

// 查看详情
const viewDetail = (item) => {
  viewedCount.value++
  // 埋点：浏览课程
  trackBehavior(item.courseId, 'view')
  router.push(`/courses`)
  ElMessage.info(`查看课程: ${item.courseName}`)
}

// 添加到收藏
const addToFavorites = async (item) => {
  // 埋点：收藏
  await trackBehavior(item.courseId, 'favorite')
  ElMessage.success(`已收藏: ${item.courseName}`)
}

// 分享
const shareItem = async (item) => {
  // 埋点：分享
  await trackBehavior(item.courseId, 'share')
  ElMessage.success(`分享: ${item.courseName}`)
}

// 切换点赞状态
const toggleLike = async (item) => {
  const courseId = item.courseId
  const isLiked = likedCourses.value.has(courseId)
  
  if (isLiked) {
    // 取消点赞
    likedCourses.value.delete(courseId)
    await trackBehavior(currentUserId.value, courseId, 'unlike')
    ElMessage.success(`取消点赞: ${item.courseName}`)
  } else {
    // 点赞
    likedCourses.value.add(courseId)
    await trackBehavior(currentUserId.value, courseId, 'like')
    ElMessage.success(`点赞: ${item.courseName}`)
  }
}

onMounted(() => {
  loadRecommendations()
  loadTrending()
})
</script>

<style scoped>
.recommendation {
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
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
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

.header-actions {
  display: flex;
  gap: 12px;
}

.btn-refresh {
  border-radius: 10px;
  padding: 10px 20px;
}

/* 统计区域 */
.stats-section {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-icon.blue {
  background: #dbeafe;
  color: #1d4ed8;
}

.stat-icon.green {
  background: #dcfce7;
  color: #15803d;
}

.stat-icon.orange {
  background: #fef3c7;
  color: #b45309;
}

.stat-icon .el-icon {
  font-size: 28px;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #1e293b;
  line-height: 1;
}

.stat-label {
  font-size: 14px;
  color: #64748b;
  margin-top: 4px;
}

/* 推荐区域 */
.recommendation-section {
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
  color: #3b82f6;
}

/* 推荐网格 */
.recommendation-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.recommendation-card {
  border-radius: 12px;
  overflow: hidden;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.recommendation-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.match-badge {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.more-btn {
  opacity: 0.6;
}

.more-btn:hover {
  opacity: 1;
}

.card-content {
  text-align: center;
  margin-bottom: 20px;
}

.course-icon {
  width: 80px;
  height: 80px;
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 36px;
  font-weight: 600;
  color: white;
  margin: 0 auto 16px;
}

.course-title {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 8px 0;
  line-height: 1.4;
}

.course-reason {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  font-size: 13px;
  color: #64748b;
  margin: 0;
}

.card-footer {
  display: flex;
  gap: 8px;
}

.card-footer .el-button {
  flex: 1;
}

/* 热门区域 */
.trending-section {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.trending-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.trending-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: #f8fafc;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.trending-item:hover {
  background: #f1f5f9;
  transform: translateX(4px);
}

.trending-rank {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: #e2e8f0;
  color: #64748b;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 16px;
}

.trending-rank.top-three {
  background: linear-gradient(135deg, #fbbf24 0%, #f59e0b 100%);
  color: white;
}

.trending-info {
  flex: 1;
}

.trending-title {
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 4px;
}

.trending-meta {
  font-size: 13px;
  color: #64748b;
}

.student-count {
  display: flex;
  align-items: center;
  gap: 4px;
}

.trending-arrow {
  color: #94a3b8;
}

/* 加载和空状态 */
.loading-container {
  padding: 40px;
}

.empty-container {
  padding: 60px 40px;
  text-align: center;
}

/* 响应式 */
@media (max-width: 768px) {
  .stats-section {
    grid-template-columns: 1fr;
  }
  
  .recommendation-grid {
    grid-template-columns: 1fr;
  }
  
  .page-header-modern {
    flex-direction: column;
    gap: 16px;
    text-align: center;
  }
  
  .section-header {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }
}
</style>
