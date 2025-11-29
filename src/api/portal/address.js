import request from '@/utils/request'

// 查询我的收货地址列表
export function listAddress() {
  console.log("调用listAddress，request配置:", {
    url: '/app/address/list',
    method: 'get',
    headers: request.defaults.headers
  });
  return request({
    url: '/app/address/list',
    method: 'get'
  })
}

// 新增收货地址
export function addAddress(data) {
  return request({
    url: '/app/address/add',
    method: 'post',
    data: data
  })
}

// 修改收货地址
export function updateAddress(data) {
  return request({
    url: '/app/address/edit',
    method: 'put',
    data: data
  })
}

// 删除收货地址
export function delAddress(addressId) {
  return request({
    url: '/app/address/del/' + addressId,
    method: 'delete'
  })
}

// 获取地址详情
export function getAddress(addressId) {
  return request({
    url: '/app/address/' + addressId,
    method: 'get'
  })
}