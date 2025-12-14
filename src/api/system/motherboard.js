import request from '@/utils/request'

// 查询主板规格列表
export function listMotherboard(query) {
  return request({
    url: '/system/motherboard/list',
    method: 'get',
    params: query
  })
}

// 查询主板规格详细
export function getMotherboard(id) {
  return request({
    url: '/system/motherboard/' + id,
    method: 'get'
  })
}

// 新增主板规格
export function addMotherboard(data) {
  return request({
    url: '/system/motherboard',
    method: 'post',
    data: data
  })
}

// 修改主板规格
export function updateMotherboard(data) {
  return request({
    url: '/system/motherboard',
    method: 'put',
    data: data
  })
}

// 删除主板规格
export function delMotherboard(id) {
  return request({
    url: '/system/motherboard/' + id,
    method: 'delete'
  })
}
