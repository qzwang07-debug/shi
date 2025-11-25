import request from '@/utils/request'

// 查询CPU硬件列表
export function listCpu(query) {
  return request({
    url: '/system/cpu/list',
    method: 'get',
    params: query
  })
}

// 查询CPU硬件详细
export function getCpu(id) {
  return request({
    url: '/system/cpu/' + id,
    method: 'get'
  })
}

// 新增CPU硬件
export function addCpu(data) {
  return request({
    url: '/system/cpu',
    method: 'post',
    data: data
  })
}

// 修改CPU硬件
export function updateCpu(data) {
  return request({
    url: '/system/cpu',
    method: 'put',
    data: data
  })
}

// 删除CPU硬件
export function delCpu(id) {
  return request({
    url: '/system/cpu/' + id,
    method: 'delete'
  })
}
