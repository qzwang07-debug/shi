import request from '@/utils/request'

// 查询内存规格列表
export function listMemory(query) {
  return request({
    url: '/system/memory/list',
    method: 'get',
    params: query
  })
}

// 查询内存规格详细
export function getMemory(id) {
  return request({
    url: '/system/memory/' + id,
    method: 'get'
  })
}

// 新增内存规格
export function addMemory(data) {
  return request({
    url: '/system/memory',
    method: 'post',
    data: data
  })
}

// 修改内存规格
export function updateMemory(data) {
  return request({
    url: '/system/memory',
    method: 'put',
    data: data
  })
}

// 删除内存规格
export function delMemory(id) {
  return request({
    url: '/system/memory/' + id,
    method: 'delete'
  })
}
