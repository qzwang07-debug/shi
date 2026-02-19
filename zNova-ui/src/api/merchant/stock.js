import request from '@/utils/request'

// 获取库存预警商品列表
export function getStockWarningProducts() {
  return request({
    url: '/merchant/stock/warning/products',
    method: 'get'
  })
}
