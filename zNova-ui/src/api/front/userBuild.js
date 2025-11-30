import request from '@/utils/request'

// 新增用户装机配置
export function addUserBuild(data) {
  return request({
    url: '/front/build',
    method: 'post',
    data: data
  })
}