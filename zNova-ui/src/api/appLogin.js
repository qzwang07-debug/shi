import request from '@/utils/request'

// C端用户登录方法
export function appLogin(username, password, code, uuid) {
  const data = {
    username,
    password,
    code,
    uuid
  }
  return request({
    url: '/app/login',
    headers: {
      isToken: false,
      repeatSubmit: false
    },
    method: 'post',
    data: data
  })
}

// C端用户注册方法
export function appRegister(data) {
  return request({
    url: '/app/register',
    headers: {
      isToken: false
    },
    method: 'post',
    data: data
  })
}

// 获取C端用户详细信息
export function getAppUserInfo() {
  return request({
    url: '/app/user/info',
    method: 'get'
  })
}