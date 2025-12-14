import request from '@/utils/request'

// 查询商品列表
export function listProduct(query) {
  return request({
    url: '/system/product/list',
    method: 'get',
    params: query
  })
}

// 查询商品详细
export function getProduct(id) {
  return request({
    url: '/system/product/' + id,
    method: 'get'
  })
}

// 新增商品
export function addProduct(data) {
  return request({
    url: '/system/product',
    method: 'post',
    data: data
  })
}

// 修改商品
export function updateProduct(data) {
  return request({
    url: '/system/product',
    method: 'put',
    data: data
  })
}

// 删除商品
export function delProduct(id) {
  return request({
    url: '/system/product/' + id,
    method: 'delete'
  })
}

export function getPerformanceScore(id) {
  return request({
    url: `/system/computer/performance/${id}`,
    method: 'get'
  });
}
// 获取CPU列表
export function listCpu(query) {
  return request({
    url: '/front/hardware/cpu/list',  // 修正为前台硬件接口路径
    method: 'get',
    params: query
  })
}

export function listGpu(query) {
  return request({
    url: '/front/hardware/gpu/list',  // 修正为前台硬件接口路径
    method: 'get',
    params: query
  })
}

export function listMemory(query) {
  return request({
    url: '/front/hardware/memory/list',  // 修正为前台硬件接口路径
    method: 'get',
    params: query
  })
}
