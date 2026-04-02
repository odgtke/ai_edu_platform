// 通知相关 API 接口

export const getUserNotifications = async (userId) => {
  // 模拟数据
  return {
    code: 200,
    data: []
  }
}

export const getUnreadCount = async (userId) => {
  return {
    code: 200,
    data: 0
  }
}

export const markAsRead = async (notificationId) => {
  return {
    code: 200,
    data: true
  }
}

export const markAllAsRead = async (userId) => {
  return {
    code: 200,
    data: true
  }
}
