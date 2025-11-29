import request from '@/utils/request'

// 查询显卡硬件列表
export function listGpu(query) {
  return request({
    url: '/system/gpu/list',
    method: 'get',
    params: query
  })
}

// 查询显卡硬件详细
export function getGpu(id) {
  return request({
    url: '/system/gpu/' + id,
    method: 'get'
  })
}

// 新增显卡硬件
export function addGpu(data) {
  return request({
    url: '/system/gpu',
    method: 'post',
    data: data
  })
}

// 修改显卡硬件
export function updateGpu(data) {
  return request({
    url: '/system/gpu',
    method: 'put',
    data: data
  })
}

// 删除显卡硬件
export function delGpu(id) {
  return request({
    url: '/system/gpu/' + id,
    method: 'delete'
  })
}
