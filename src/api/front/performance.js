import request from '@/utils/request'

// 评估配置单性能
export function assessPerformance(data) {
  return request({
    url: '/front/performance/assess',
    method: 'post',
    data: data
  })
}