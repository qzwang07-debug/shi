import request from '@/utils/request'

// 获取商家后台首页统计数据
export function getDashboardStats() {
  return request({
    url: '/merchant/dashboard/stats',
    method: 'get'
  })
}
export function getDashboardTrend(params) {
  return request({
    url: '/merchant/dashboard/trend',
    method: 'get',
    params
  })
}