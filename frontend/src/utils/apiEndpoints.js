// 閻劍鍩涢惄绋垮彠 API
import apiClient from './api'

export const userAPI = {
  // 閻劍鍩涢惂璇茬秿
  login: (credentials) => apiClient.post('/auth/login', credentials),
  
  // 閻劍鍩涘▔銊ュ斀
  register: (userData) => apiClient.post('/auth/register', userData),
  
  // 閼惧嘲褰囬悽銊﹀煕娣団剝浼?
  getUserInfo: () => apiClient.get('/users/profile'),
  
  // 閺囧瓨鏌婇悽銊﹀煕娣団剝浼?
  updateUserInfo: (userData) => apiClient.put('/users/profile', userData),
  
  // 閼惧嘲褰囬悽銊﹀煕閸掓銆?
  getUserList: (params) => apiClient.get('/users', { params })
}

// 鐠囧墽鈻奸惄绋垮彠 API
export const courseAPI = {
  // 閼惧嘲褰囩拠鍓р柤閸掓銆?
  getCourseList: (params) => apiClient.get('/courses', { params }),
  
  // 閼惧嘲褰囩拠鍓р柤鐠囷附鍎?
  getCourseDetail: (id) => apiClient.get(`/courses/${id}`),
  
  // 閸掓稑缂撶拠鍓р柤
  createCourse: (courseData) => apiClient.post('/courses', courseData),
  
  // 閺囧瓨鏌婄拠鍓р柤
  updateCourse: (id, courseData) => apiClient.put(`/courses/${id}`, courseData),
  
  // 閸掔娀娅庣拠鍓р柤
  deleteCourse: (id) => apiClient.delete(`/courses/${id}`)
}

// 鐎涳缚绡勭拋鏉跨秿閻╃鍙?API
export const learningAPI = {
  // 閼惧嘲褰囩€涳缚绡勭拋鏉跨秿
  getLearningRecords: (params) => apiClient.get('/learning/records', { params }),
  
  // 閸掓稑缂撶€涳缚绡勭拋鏉跨秿
  createLearningRecord: (recordData) => apiClient.post('/learning/records', recordData),
  
  // 閺囧瓨鏌婄€涳缚绡勬潻娑樺
  updateProgress: (id, progress) => apiClient.put(`/learning/records/${id}/progress`, { progress })
}

// 閼宠棄濮忕拠鍕強閻╃鍙?API
export const assessmentAPI = {
  // 閼惧嘲褰囩拠鍕強閸掓銆?
  getAssessments: (params) => apiClient.get('/assessments', { params }),
  
  // 瀵慨瀣槑娴?
  startAssessment: (assessmentId) => apiClient.post(/assessments/${assessmentId}/start`),
  
  // 閹绘劒姘︾粵鏃€
  submitAnswer: (data) => apiClient.post('/assessments/submit', data),
  
  // 閼惧嘲褰囩拠鍕強缂佹挻鐏?
  getAssessmentResult: (attemptId) => apiClient.get(/assessments/result/${attemptId}`)
}

// 娑撻幀褎甯归懡鎰祲閸?API
export const recommendationAPI = {
  // 閼惧嘲褰囨稉閹冨閹恒劏宕?
  getPersonalized: (userId, limit = 10) => 
    apiClient.get(/recommendations/personalized?userId=${userId}&limit=${limit}`),
  
  // 閹稿琚崹瀣箯閸欐牗甯归懡?
  getByType: (userId, type, limit = 10) => 
    apiClient.get(/recommendations/by-type?userId=${userId}&type=${type}&limit=${limit}`),
  
  // 閼惧嘲褰囬悜闂傘劏鍓р柤
  getTrending: (limit = 10) => 
    apiClient.get(/recommendations/trending?limit=${limit}`),
  
  // 閼惧嘲褰囩€涳缚绡勭捄瀵?
  getLearningPaths: (userId) => 
    apiClient.get(/recommendations/learning-paths?userId=${userId}`),
  
  // 鐠佹澘缍嶉悽銊﹀煕鐞涘奔璐?
  recordBehavior: (data) => 
    apiClient.post('/recommendations/behavior', data),
  
  // 閺囧瓨鏌婇悽銊﹀煕閸嬪繐銈?
  updatePreference: (data) => 
    apiClient.post('/recommendations/preference', data)
}