import request from '@/utils/request'

// 查询商品评价列表
export function listFrontComment(query) {
  return request({
    url: '/app/comment/list',
    method: 'get',
    params: query
  })
}