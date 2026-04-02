import request from '@/utils/request'

/**
 * 课程管理 API 模块
 */

/**
 * 分页查询课程
 * @param {Object} params - 查询参数
 */
export function getCoursePage(params) {
  return request({
    url: '/api/courses/page',
    method: 'get',
    params
  })
}

/**
 * 获取所有课程
 */
export function getAllCourses() {
  return request({
    url: '/api/courses',
    method: 'get'
  })
}

/**
 * 根据ID获取课程
 * @param {number} id - 课程ID
 */
export function getCourseById(id) {
  return request({
    url: `/api/courses/${id}`,
    method: 'get'
  })
}

/**
 * 创建课程
 * @param {Object} data - 课程数据
 */
export function createCourse(data) {
  return request({
    url: '/api/courses',
    method: 'post',
    data
  })
}

/**
 * 更新课程
 * @param {Object} data - 课程数据
 */
export function updateCourse(data) {
  return request({
    url: `/api/courses/${data.courseId}`,
    method: 'put',
    data
  })
}

/**
 * 删除课程
 * @param {number} id - 课程ID
 */
export function deleteCourse(id) {
  return request({
    url: `/api/courses/${id}`,
    method: 'delete'
  })
}

/**
 * 获取课程总数
 */
export function getCourseCount() {
  return request({
    url: '/api/courses/count',
    method: 'get'
  })
}

/**
 * 获取推荐课程
 * @param {number} limit - 数量限制
 */
export function getRecommendations(limit = 10) {
  return request({
    url: '/api/recommendations/popular',
    method: 'get',
    params: { limit }
  })
}

/**
 * 获取个性化推荐
 * @param {number} userId - 用户ID
 * @param {number} limit - 数量限制
 */
export function getPersonalizedRecommendations(userId, limit = 10) {
  return request({
    url: '/api/recommendations/personalized',
    method: 'get',
    params: { userId, limit }
  })
}
