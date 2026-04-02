/**
 * 格式化工具函数
 */

/**
 * 格式化日期
 * @param {string|Date} date - 日期
 * @param {string} format - 格式
 */
export function formatDate(date, format = 'YYYY-MM-DD') {
  if (!date) return '-'
  const d = new Date(date)
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const hour = String(d.getHours()).padStart(2, '0')
  const minute = String(d.getMinutes()).padStart(2, '0')
  
  return format
    .replace('YYYY', year)
    .replace('MM', month)
    .replace('DD', day)
    .replace('HH', hour)
    .replace('mm', minute)
}

/**
 * 格式化时长（分钟转换为小时分钟）
 * @param {number} minutes - 分钟数
 */
export function formatDuration(minutes) {
  if (!minutes || minutes <= 0) return '0分钟'
  const hours = Math.floor(minutes / 60)
  const mins = minutes % 60
  if (hours > 0) {
    return `${hours}小时${mins > 0 ? mins + '分钟' : ''}`
  }
  return `${mins}分钟`
}

/**
 * 格式化百分比
 * @param {number} value - 数值
 * @param {number} decimals - 小数位数
 */
export function formatPercent(value, decimals = 0) {
  if (value === null || value === undefined) return '-'
  return value.toFixed(decimals) + '%'
}

/**
 * 格式化数字（添加千分位分隔符）
 * @param {number} num - 数字
 */
export function formatNumber(num) {
  if (num === null || num === undefined) return '-'
  return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}
