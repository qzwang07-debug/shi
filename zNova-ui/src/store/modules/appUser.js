import { defineStore } from 'pinia'
import { getAppUserInfo } from '@/api/appLogin'

const useAppUserStore = defineStore(
  'appUser',
  {
    state: () => ({
      token: localStorage.getItem('app_token'),
      userInfo: null,
      loading: false
    }),
    actions: {
      // 获取/刷新用户信息
      async fetchUserInfo() {
        if (!this.token) return null
        
        this.loading = true
        try {
          const res = await getAppUserInfo()
          if (res.code === 200 && res.user) {
            this.userInfo = {
              ...res.user,
              creditScore: res.user.creditScore || 500,
              frozenDeposit: res.user.frozenDeposit || 0
            }
            // 同时更新本地缓存，方便非响应式场景使用
            localStorage.setItem('app_user_info', JSON.stringify(this.userInfo))
            return this.userInfo
          }
        } catch (error) {
          console.error('Fetch AppUser Info Error:', error)
          // 如果 401，则清除 token
          if (error.response && error.response.status === 401) {
            this.clearUserInfo()
          }
        } finally {
          this.loading = false
        }
        return null
      },
      
      // 设置 Token
      setToken(token) {
        this.token = token
        localStorage.setItem('app_token', token)
      },
      
      // 清除用户信息（登出）
      clearUserInfo() {
        this.token = null
        this.userInfo = null
        localStorage.removeItem('app_token')
        localStorage.removeItem('app_user_info')
      },
      
      // 实时更新本地缓存的用户数据（不通过接口，仅同步 UI）
      updateLocalUserInfo(data) {
        if (this.userInfo) {
          this.userInfo = { ...this.userInfo, ...data }
          localStorage.setItem('app_user_info', JSON.stringify(this.userInfo))
        }
      }
    },
    getters: {
      isLoggedIn: (state) => !!state.token,
      userAvatar: (state) => {
        if (state.userInfo && state.userInfo.avatar) {
          // 如果是绝对路径则直接返回，否则拼接基础 API
          if (state.userInfo.avatar.startsWith('http')) {
            return state.userInfo.avatar
          }
          return import.meta.env.VITE_APP_BASE_API + state.userInfo.avatar
        }
        return '/img/profile.jpg'
      }
    }
  }
)

export default useAppUserStore
