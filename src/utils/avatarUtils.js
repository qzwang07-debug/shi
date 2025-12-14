/**
 * 头像处理工具
 */

// 默认头像路径
export const DEFAULT_AVATAR = '/img/profile.jpg'

/**
 * 获取用户头像
 * @param {Object} userInfo - 用户信息对象
 * @param {string} userInfo.avatar - 用户头像路径
 * @returns {string} - 处理后的头像路径
 */
export const getUserAvatar = (userInfo) => {
  if (userInfo && userInfo.avatar) {
    return userInfo.avatar
  }
  return DEFAULT_AVATAR
}

/**
 * 处理头像加载失败
 * @param {Event} event - 图片加载错误事件
 */
export const handleAvatarError = (event) => {
  event.target.src = DEFAULT_AVATAR
}

/**
 * 格式化头像URL
 * @param {string} avatarUrl - 原始头像URL
 * @returns {string} - 格式化后的头像URL
 */
export const formatAvatarUrl = (avatarUrl) => {
  if (!avatarUrl) {
    return DEFAULT_AVATAR
  }
  
  // 检查是否为完整URL
  if (avatarUrl.startsWith('http://') || avatarUrl.startsWith('https://')) {
    return avatarUrl
  }
  
  // 如果是相对路径，确保以/开头
  if (!avatarUrl.startsWith('/')) {
    return `/${avatarUrl}`
  }
  
  return avatarUrl
}
