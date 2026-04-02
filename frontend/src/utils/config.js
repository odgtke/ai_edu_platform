// API 闁板秶鐤嗛弬鍥︽
const API_CONFIG = {
  // 瀵崣鎴犲箚婢?
  development: {
    baseURL: 'http://localhost:8080',
    timeout: 10000
  },
  // 閻㈢喍楠囬悳婢?
  production: {
    baseURL: 'http://your-production-domain.com',
    timeout: 15000
  }
}

// 閼惧嘲褰囪ぐ鎾冲閻滄晶鍐帳缂?
const getConfig = () => {
  const env = process.env.NODE_ENV || 'development'
  return API_CONFIG[env] || API_CONFIG.development
}

export default getConfig()