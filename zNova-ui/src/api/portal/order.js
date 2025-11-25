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