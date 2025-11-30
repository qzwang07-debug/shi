import request from '@/utils/request'

// 查询内存性能曲线列表
export function listRam(query) {
  return request({
    url: '/system/ram/list',
    method: 'get',
    params: query
  })
}

// 查询内存性能曲线详细
export function getRam(id) {
  return request({
    url: '/system/ram/' + id,
    method: 'get'
  })
}

// 新增内存性能曲线
export function addRam(data) {
  return request({
    url: '/system/ram',
    method: 'post',
    data: data
  })
}

// 修改内存性能曲线
export function updateRam(data) {
  return request({
    url: '/system/ram',
    method: 'put',
    data: data
  })
}

// 删除内存性能曲线
export function delRam(id) {
  return request({
    url: '/system/ram/' + id,
    method: 'delete'
  })
}
