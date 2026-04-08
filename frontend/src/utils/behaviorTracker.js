/**
 * 用户行为追踪工具
 * 用于个性化推荐系统的行为数据采集
 */

const API_BASE_URL = '/api'

/**
 * 追踪用户行为
 * @param {number} userId - 用户ID
 * @param {number} courseId - 课程ID（可选）
 * @param {string} behaviorType - 行为类型
 * @param {object} metadata - 额外元数据（可选）
 */
export async function trackBehavior(userId, courseId, behaviorType, metadata = {}) {
  try {
    const response = await fetch(`${API_BASE_URL}/recommendations/behavior`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        userId,
        courseId,
        behaviorType,
        metadata,
        timestamp: new Date().toISOString(),
        pageUrl: window.location.href,
        userAgent: navigator.userAgent
      })
    })
    
    if (!response.ok) {
      console.warn('行为追踪请求失败:', response.status)
    }
  } catch (error) {
    console.error('行为追踪失败:', error)
  }
}

/**
 * 追踪页面浏览
 * @param {number} userId - 用户ID
 * @param {number} courseId - 课程ID（可选）
 */
export function trackView(userId, courseId = null) {
  return trackBehavior(userId, courseId, 'view')
}

/**
 * 追踪课程浏览（从搜索或列表）
 * @param {number} userId - 用户ID
 * @param {number} courseId - 课程ID
 */
export function trackBrowse(userId, courseId) {
  return trackBehavior(userId, courseId, 'browse')
}

/**
 * 追踪开始学习
 * @param {number} userId - 用户ID
 * @param {number} courseId - 课程ID
 */
export function trackStudy(userId, courseId) {
  return trackBehavior(userId, courseId, 'study')
}

/**
 * 追踪完成学习
 * @param {number} userId - 用户ID
 * @param {number} courseId - 课程ID
 * @param {object} metadata - 额外信息（如学习时长）
 */
export function trackComplete(userId, courseId, metadata = {}) {
  return trackBehavior(userId, courseId, 'complete', metadata)
}

/**
 * 追踪收藏
 * @param {number} userId - 用户ID
 * @param {number} courseId - 课程ID
 */
export function trackFavorite(userId, courseId) {
  return trackBehavior(userId, courseId, 'favorite')
}

/**
 * 追踪分享
 * @param {number} userId - 用户ID
 * @param {number} courseId - 课程ID
 * @param {string} shareChannel - 分享渠道
 */
export function trackShare(userId, courseId, shareChannel = '') {
  return trackBehavior(userId, courseId, 'share', { shareChannel })
}

/**
 * 追踪评分
 * @param {number} userId - 用户ID
 * @param {number} courseId - 课程ID
 * @param {number} rating - 评分（1-5）
 */
export function trackRate(userId, courseId, rating) {
  return trackBehavior(userId, courseId, 'rate', { rating })
}

/**
 * 追踪报名
 * @param {number} userId - 用户ID
 * @param {number} courseId - 课程ID
 */
export function trackEnroll(userId, courseId) {
  return trackBehavior(userId, courseId, 'enroll')
}

/**
 * 追踪推荐点击
 * @param {number} userId - 用户ID
 * @param {number} courseId - 课程ID
 * @param {string} source - 推荐来源
 */
export function trackRecommendationClick(userId, courseId, source) {
  return trackBehavior(userId, courseId, 'recommendation_click', { source })
}

/**
 * 页面停留时间追踪
 */
export class PageStayTracker {
  constructor(userId, courseId = null) {
    this.userId = userId
    this.courseId = courseId
    this.startTime = Date.now()
    this.isActive = true
    
    // 页面可见性变化监听
    document.addEventListener('visibilitychange', this.handleVisibilityChange.bind(this))
    
    // 页面卸载时发送数据
    window.addEventListener('beforeunload', this.sendStayData.bind(this))
  }
  
  handleVisibilityChange() {
    if (document.hidden) {
      this.isActive = false
      this.sendStayData()
    } else {
      this.isActive = true
      this.startTime = Date.now()
    }
  }
  
  sendStayData() {
    const stayTime = Date.now() - this.startTime
    // 只记录超过5秒的停留
    if (stayTime > 5000) {
      trackBehavior(this.userId, this.courseId, 'stay', {
        duration: stayTime,
        durationSeconds: Math.round(stayTime / 1000)
      })
    }
  }
  
  destroy() {
    this.sendStayData()
    document.removeEventListener('visibilitychange', this.handleVisibilityChange)
    window.removeEventListener('beforeunload', this.sendStayData)
  }
}

/**
 * 滚动深度追踪
 */
export class ScrollDepthTracker {
  constructor(userId, courseId = null) {
    this.userId = userId
    this.courseId = courseId
    this.maxScrollDepth = 0
    this.trackedDepths = new Set()
    
    this.throttledScroll = this.throttle(this.trackScroll.bind(this), 500)
    window.addEventListener('scroll', this.throttledScroll)
  }
  
  trackScroll() {
    const scrollTop = window.pageYOffset || document.documentElement.scrollTop
    const scrollHeight = document.documentElement.scrollHeight - window.innerHeight
    const scrollPercent = Math.round((scrollTop / scrollHeight) * 100)
    
    if (scrollPercent > this.maxScrollDepth) {
      this.maxScrollDepth = scrollPercent
    }
    
    // 记录关键深度点（25%, 50%, 75%, 100%）
    const keyDepths = [25, 50, 75, 90, 100]
    keyDepths.forEach(depth => {
      if (scrollPercent >= depth && !this.trackedDepths.has(depth)) {
        this.trackedDepths.add(depth)
        trackBehavior(this.userId, this.courseId, 'scroll_depth', { depth })
      }
    })
  }
  
  throttle(func, limit) {
    let inThrottle
    return function() {
      const args = arguments
      const context = this
      if (!inThrottle) {
        func.apply(context, args)
        inThrottle = true
        setTimeout(() => inThrottle = false, limit)
      }
    }
  }
  
  destroy() {
    window.removeEventListener('scroll', this.throttledScroll)
  }
}

export default {
  trackBehavior,
  trackView,
  trackBrowse,
  trackStudy,
  trackComplete,
  trackFavorite,
  trackShare,
  trackRate,
  trackEnroll,
  trackRecommendationClick,
  PageStayTracker,
  ScrollDepthTracker
}
