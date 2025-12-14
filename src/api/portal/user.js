import request from '@/utils/request'

// 获取用户信息
export function getInfo() {
  return request({
    url: '/app/user/info',
    method: 'get'
  })
}

// 修改个人信息
export function updateProfile(data) {
  return request({
    url: '/app/user/profile',
    method: 'put',
    data
  })
}

// 修改密码
export function updatePassword(data) {
  return request({
    url: '/app/user/pwd',
    method: 'put',
    data
  })
}

// 头像上传
export function uploadAvatar(data) {
  return request({
    url: '/app/user/avatar',
    method: 'post',
    data
  })
}