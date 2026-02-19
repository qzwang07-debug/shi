import request from '@/utils/request'

export function generateAiReview(data) {
  return request({
    url: '/system/ai/review',
    method: 'post',
    headers: {
      repeatSubmit: false
    },
    timeout: 60000,
    data
  })
}
