import request from '@/utils/request'

// 查询电脑商品列表
export function listComputer(query) {
  return request({
    url: '/system/computer/list',
    method: 'get',
    params: query
  })
}

// 获取电脑商品详细信息
export function getInfo(id) {
  return request({
    url: `/system/computer/${id}`,
    method: 'get'
  })
}

// 新增电脑商品
export function addComputer(data) {
  return request({
    url: '/system/computer',
    method: 'post',
    data: data
  })
}

// 修改电脑商品
export function updateComputer(data) {
  return request({
    url: '/system/computer',
    method: 'put',
    data: data
  })
}

// 删除电脑商品
export function delComputer(ids) {
  return request({
    url: `/system/computer/${ids}`,
    method: 'delete'
  })
}

// 导出电脑商品
export function exportComputer(query) {
  return request({
    url: '/system/computer/export',
    method: 'post',
    data: query,
    responseType: 'blob'
  })
}