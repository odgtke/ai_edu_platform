import request from '@/utils/request'

/**
 * AI 智能体 API 模块
 */

/**
 * 与 AI 智能体对话
 * @param {string} message - 用户消息
 */
export function chatWithAgent(message) {
  return request({
    url: '/api/agent/chat',
    method: 'post',
    data: { message }
  })
}

/**
 * 检查智能体服务健康状态
 */
export function checkAgentHealth() {
  return request({
    url: '/api/agent/health',
    method: 'get'
  })
}
