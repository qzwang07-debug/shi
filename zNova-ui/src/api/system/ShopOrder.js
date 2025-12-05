import request from '@/utils/request'

// 查询订单主列表
export function listShopOrder(query) {
  return request({
    url: '/system/ShopOrder/list',
    method: 'get',
    params: query
  })
}

// 查询订单主详细
export function getShopOrder(orderId) {
  return request({
    url: '/system/ShopOrder/' + orderId,
    method: 'get'
  })
}

// 新增订单主
export function addShopOrder(data) {
  return request({
    url: '/system/ShopOrder',
    method: 'post',
    data: data
  })
}

// 修改订单主
export function updateShopOrder(data) {
  return request({
    url: '/system/ShopOrder',
    method: 'put',
    data: data
  })
}

// 删除订单主
export function delShopOrder(orderId) {
  return request({
    url: '/system/ShopOrder/' + orderId,
    method: 'delete'
  })
}

// 审核退款
export function auditRefund(data) {
  return request({
    url: '/merchant/order/auditRefund',
    method: 'post',
    data: data
  })
}