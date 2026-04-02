import request from '@/utils/request'

/**
 * 资源管理 API 模块
 */

/**
 * 分页查询资源列表
 * @param {Object} params - 查询参数
 */
export function getResourcePage(params) {
  return request({
    url: '/api/resources/page',
    method: 'get',
    params
  })
}

/**
 * 获取所有资源
 */
export function getAllResources() {
  return request({
    url: '/api/resources',
    method: 'get'
  })
}

/**
 * 根据ID获取资源
 * @param {number} id - 资源ID
 */
export function getResourceById(id) {
  return request({
    url: `/api/resources/${id}`,
    method: 'get'
  })
}

/**
 * 删除资源
 * @param {number} id - 资源ID
 */
export function deleteResource(id) {
  return request({
    url: `/api/resources/${id}`,
    method: 'delete'
  })
}

/**
 * 上传资源
 * @param {FormData} formData - 表单数据
 */
export function uploadResource(formData) {
  return request({
    url: '/api/resources/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
