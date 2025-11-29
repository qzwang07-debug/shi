import request from '@/utils/request'

// 查询CPU列表（前台专用）
export function listFrontCpu(query) {
  return request({
    url: '/front/hardware/cpu/list',
    method: 'get',
    params: query
  })
}

// 查询GPU列表（前台专用）
export function listFrontGpu(query) {
  return request({
    url: '/front/hardware/gpu/list',
    method: 'get',
    params: query
  })
}

// 查询内存列表（前台专用）
export function listFrontMemory(query) {
  return request({
    url: '/front/hardware/memory/list',
    method: 'get',
    params: query
  })
}