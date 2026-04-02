import axios from 'axios'
import config from './config'

// 閸掓稑缂?axios 鐎圭偘绶?
const apiClient = axios.create({
  baseURL: config.baseURL,
  timeout: config.timeout,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 鐠囬攱鐪伴幏锔藉焻閸?
apiClient.interceptors.request.use(
  config => {
    // 濞ｈ濮炵拋銈堢槈 token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 閸濆秴绨查幏锔藉焻閸?
apiClient.interceptors.response.use(
  response => {
    return response.data
  },
  error => {
    if (error.response) {
      switch (error.response.status) {
        case 401:
          // 閺堥幒鍫熸綀閿涘本绔婚梽token楠炴儼鐑︽潪閻ц?
          localStorage.removeItem('token')
          window.location.href = '/login'
          break
        case 403:
          console.error('閺夊啴妾烘稉宥堝喕')
          break
        case 500:
          console.error('閺堝秴濮熼崳銊ュ敶闁劑鏁婄拠?)
          break
        default:
          console.error('鐠囬攱鐪版径杈Е:', error.response.data.message)
      }
    }
    return Promise.reject(error)
  }
)

export default apiClient