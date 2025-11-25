import Cookies from 'js-cookie'

const TokenKey = 'Admin-Token'
const AppTokenKey = 'app_token'

// 获取后台管理token
export function getToken() {
  return Cookies.get(TokenKey)
}

// 设置后台管理token
export function setToken(token) {
  return Cookies.set(TokenKey, token)
}

// 删除后台管理token
export function removeToken() {
  return Cookies.remove(TokenKey)
}

// 获取C端用户token
export function getAppToken() {
  const token = localStorage.getItem(AppTokenKey)
  console.log("getAppToken从localStorage获取:", AppTokenKey, "值:", token);
  return token;
}

// 设置C端用户token
export function setAppToken(token) {
  return localStorage.setItem(AppTokenKey, token)
}

// 删除C端用户token
export function removeAppToken() {
  return localStorage.removeItem(AppTokenKey)
}
