import request from '@/utils/request'

// 查询收货地址列表
export function listAppAddress(query) {
  return request({
    url: '/system/AppAddress/list',
    method: 'get',
    params: query
  })
}

// 查询收货地址详细
export function getAppAddress(addressId) {
  return request({
    url: '/system/AppAddress/' + addressId,
    method: 'get'
  })
}

// 新增收货地址
export function addAppAddress(data) {
  return request({
    url: '/system/AppAddress',
    method: 'post',
    data: data
  })
}

// 修改收货地址
export function updateAppAddress(data) {
  return request({
    url: '/system/AppAddress',
    method: 'put',
    data: data
  })
}

// 删除收货地址
export function delAppAddress(addressId) {
  return request({
    url: '/system/AppAddress/' + addressId,
    method: 'delete'
  })
}
