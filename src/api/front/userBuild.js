import request from '@/utils/request'

// 查询用户装机配置列表
export function listUserBuild(query) {
  return request({
    url: '/front/build/list',
    method: 'get',
    params: query
  })
}

// 新增用户装机配置
export function addUserBuild(data) {
  return request({
    url: '/front/build',
    method: 'post',
    data: data
  })
}

// 修改用户装机配置
export function updateUserBuild(data) {
  return request({
    url: '/front/build',
    method: 'put',
    data: data
  })
}

// 删除用户装机配置
export function delUserBuild(buildId) {
  return request({
    url: '/front/build/' + buildId,
    method: 'delete'
  })
}