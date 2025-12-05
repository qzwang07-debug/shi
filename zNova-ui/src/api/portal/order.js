import request from '@/utils/request'

// 创建订单
// data结构: { addressId, remark, items: [{cartId, productId, quantity, businessType, startDate, endDate}] }
export function createOrder(data) {
  return request({
    url: '/app/order/create',
    method: 'post',
    data: data
  })
}

// 查询我的订单列表
export function listOrder(query) {
  return request({
    url: '/app/order/list', // 确保后端 AppOrderController 有这个映射
    method: 'get',
    params: query
  })
}

// 用户取消订单
export function cancelOrder(data) {
  return request({
    url: '/app/order/cancel', // 改为 /app/ 开头
    method: 'post',
    data: data
  })
}

// 用户申请退款
export function applyRefund(data) {
  return request({
    url: '/app/order/applyRefund', // 改为 /app/ 开头
    method: 'post',
    data: data
  })
}
