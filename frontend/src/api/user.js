import request from '@/utils/request'

/**
 * 用户管理 API 模块
 */

/**
 * 分页查询用户
 * @param {Object} params - 查询参数
 */
export function getUserPage(params) {
  return request({
    url: '/api/users/page',
    method: 'get',
    params
  })
}

/**
 * 获取所有用户
 */
export function getAllUsers() {
  return request({
    url: '/api/users',
    method: 'get'
  })
}

/**
 * 根据ID获取用户
 * @param {number} id - 用户ID
 */
export function getUserById(id) {
  return request({
    url: `/api/users/${id}`,
    method: 'get'
  })
}

/**
 * 创建用户
 * @param {Object} data - 用户数据
 */
export function createUser(data) {
  return request({
    url: '/api/users',
    method: 'post',
    data
  })
}

/**
 * 更新用户
 * @param {Object} data - 用户数据
 */
export function updateUser(data) {
  return request({
    url: `/api/users/${data.userId}`,
    method: 'put',
    data
  })
}

/**
 * 删除用户
 * @param {number} id - 用户ID
 */
export function deleteUser(id) {
  return request({
    url: `/api/users/${id}`,
    method: 'delete'
  })
}

/**
 * 获取用户总数
 */
export function getUserCount() {
  return request({
    url: '/api/users/count',
    method: 'get'
  })
}
