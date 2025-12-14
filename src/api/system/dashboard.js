import request from '@/utils/request'

// 获取管理员后台首页统计数据
export function getAdminStats() {
  return request({
    url: '/system/dashboard/admin-stats',
    method: 'get'
  })
}

// 获取系统访问趋势数据
export function getVisitTrend() {
  return request({
    url: '/system/dashboard/visit-trend',
    method: 'get'
  })
}