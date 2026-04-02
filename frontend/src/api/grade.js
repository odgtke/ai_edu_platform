import request from '@/utils/request'

/**
 * 年级管理 API 模块
 */

export function getGradeList() {
  return request({
    url: '/api/grades/list',
    method: 'get'
  })
}

export function getActiveGrades() {
  return request({
    url: '/api/grades/active',
    method: 'get'
  })
}

export function getGradeById(id) {
  return request({
    url: `/api/grades/${id}`,
    method: 'get'
  })
}

export function createGrade(data) {
  return request({
    url: '/api/grades',
    method: 'post',
    data
  })
}

export function updateGrade(id, data) {
  return request({
    url: `/api/grades/${id}`,
    method: 'put',
    data
  })
}

export function deleteGrade(id) {
  return request({
    url: `/api/grades/${id}`,
    method: 'delete'
  })
}
