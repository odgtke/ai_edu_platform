import request from '@/utils/request'

/**
 * 班级管理 API 模块
 */

export function getClassPage(params) {
  return request({
    url: '/api/classes/page',
    method: 'get',
    params
  })
}

export function getClassesByGrade(gradeId) {
  return request({
    url: `/api/classes/by-grade/${gradeId}`,
    method: 'get'
  })
}

export function getClassesByTeacher(teacherId) {
  return request({
    url: `/api/classes/by-teacher/${teacherId}`,
    method: 'get'
  })
}

export function getClassById(id) {
  return request({
    url: `/api/classes/${id}`,
    method: 'get'
  })
}

export function createClass(data) {
  return request({
    url: '/api/classes',
    method: 'post',
    data
  })
}

export function updateClass(id, data) {
  return request({
    url: `/api/classes/${id}`,
    method: 'put',
    data
  })
}

export function deleteClass(id) {
  return request({
    url: `/api/classes/${id}`,
    method: 'delete'
  })
}
