import request from '@/utils/request'

/**
 * 通知管理 API 模块
 */

export function getUnreadCount(userId) {
  return request({
    url: `/api/notifications/unread-count/${userId}`,
    method: 'get'
  })
}

export function getUserNotifications(userId) {
  return request({
    url: `/api/notifications/user/${userId}`,
    method: 'get'
  })
}

export function sendToUser(data) {
  return request({
    url: '/api/notifications/send-to-user',
    method: 'post',
    params: data
  })
}

export function sendToClass(data) {
  return request({
    url: '/api/notifications/send-to-class',
    method: 'post',
    params: data
  })
}

export function sendSystemNotification(data) {
  return request({
    url: '/api/notifications/send-system',
    method: 'post',
    params: data
  })
}

export function markAsRead(notificationId) {
  return request({
    url: `/api/notifications/mark-read/${notificationId}`,
    method: 'post'
  })
}

export function markAllAsRead(userId) {
  return request({
    url: `/api/notifications/mark-all-read/${userId}`,
    method: 'post'
  })
}
