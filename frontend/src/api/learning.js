import request from '@/utils/request'

/**
 * 学习中心 API 模块
 */

/**
 * 获取学习统计信息
 */
export function getStatistics() {
  return request({
    url: '/api/learning/statistics',
    method: 'get'
  })
}

/**
 * 获取学习日历数据
 * @param {Object} params - 查询参数
 */
export function getCalendar(params) {
  return request({
    url: '/api/learning/calendar',
    method: 'get',
    params
  })
}

/**
 * 分页查询学习记录
 * @param {Object} params - 查询参数
 */
export function getRecordsPage(params) {
  return request({
    url: '/api/learning/records/page',
    method: 'get',
    params
  })
}

/**
 * 获取所有学习记录
 */
export function getAllRecords() {
  return request({
    url: '/api/learning/records',
    method: 'get'
  })
}

/**
 * 获取学生学习记录
 * @param {number} studentId - 学生 ID
 */
export function getStudentRecords(studentId) {
  return request({
    url: `/api/learning/records/${studentId}`,
    method: 'get'
  })
}

/**
 * 创建学习记录
 * @param {Object} data - 学习记录数据
 */
export function createLearningRecord(data) {
  return request({
    url: '/api/learning/records',
    method: 'post',
    data
  })
}

/**
 * 获取作业列表
 */
export function getAssignments() {
  return request({
    url: '/api/assignments',
    method: 'get'
  })
}

/**
 * 根据课程获取作业
 * @param {number} courseId - 课程 ID
 */
export function getAssignmentsByCourse(courseId) {
  return request({
    url: `/api/assignments/course/${courseId}`,
    method: 'get'
  })
}

/**
 * 创建作业
 * @param {Object} data - 作业数据
 */
export function createAssignment(data) {
  return request({
    url: '/api/assignments',
    method: 'post',
    data
  })
}

/**
 * 更新作业
 * @param {Object} data - 作业数据
 */
export function updateAssignment(data) {
  return request({
    url: `/api/assignments/${data.assignmentId}`,
    method: 'put',
    data
  })
}

/**
 * 删除作业
 * @param {number} id - 作业 ID
 */
export function deleteAssignment(id) {
  return request({
    url: `/api/assignments/${id}`,
    method: 'delete'
  })
}

/**
 * 获取评估列表
 */
export function getAssessments() {
  return request({
    url: '/api/assessments',
    method: 'get'
  })
}

/**
 * 根据课程获取评估
 * @param {number} courseId - 课程 ID
 */
export function getAssessmentsByCourse(courseId) {
  return request({
    url: `/api/assessments/course/${courseId}`,
    method: 'get'
  })
}

/**
 * 创建评估
 * @param {Object} data - 评估数据
 */
export function createAssessment(data) {
  return request({
    url: '/api/assessments',
    method: 'post',
    data
  })
}

/**
 * 更新评估
 * @param {number} id - 评估 ID
 * @param {Object} data - 评估数据
 */
export function updateAssessment(id, data) {
  return request({
    url: `/api/assessments/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除评估
 * @param {number} id - 评估 ID
 */
export function deleteAssessment(id) {
  return request({
    url: `/api/assessments/${id}`,
    method: 'delete'
  })
}

/**
 * 获取学生学习概览
 * @param {number} studentId - 学生 ID
 */
export function getStudentOverview(studentId) {
  return request({
    url: `/api/analytics/student/${studentId}/overview`,
    method: 'get'
  })
}

/**
 * 获取学生学习日历
 * @param {number} studentId - 学生 ID
 */
export function getLearningCalendar(studentId) {
  return request({
    url: `/api/analytics/student/${studentId}/calendar`,
    method: 'get'
  })
}
