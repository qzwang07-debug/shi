import request from '@/utils/request'

// 查询商品列表（前台专用，不应用权限控制）
export function listFrontProduct(query) {
  return request({
    url: '/front/product/list',
    method: 'get',
    params: query
  })
}

// 查询商品详细（前台专用）
export function getFrontProduct(id) {
  return request({
    url: '/front/product/' + id,
    method: 'get'
  })
}