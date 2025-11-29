import request from '@/utils/request'

// 查询购物车列表
export function listCart() {
  return request({
    url: '/app/cart/list',
    method: 'get'
  })
}

// 添加购物车 (已在详情页使用，这里保留以备不时之需)
export function addToCart(data) {
  return request({
    url: '/app/cart',
    method: 'post',
    data: data
  })
}

// 修改购物车 (数量、选中状态)
export function updateCart(data) {
  return request({
    url: '/app/cart',
    method: 'put',
    data: data
  })
}

// 删除购物车 (支持批量: 101,102)
export function delCart(cartIds) {
  return request({
    url: '/app/cart/' + cartIds,
    method: 'delete'
  })
}