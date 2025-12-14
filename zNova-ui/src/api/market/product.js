import request from '@/utils/request'

// 根据评分获取相似商品列表
export function listSimilarProducts(score) {
  return request({
    url: '/front/product/similar',
    method: 'get',
    params: { score }
  })
}