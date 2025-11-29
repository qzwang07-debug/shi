<template>
  <div class="portal-home">
    <h1>欢迎来到电脑租售与硬件性能智能评估平台</h1>
    <p>这是C端用户前台首页</p>
    <div v-if="userInfo">
      <p>当前登录用户：{{ userInfo.nickname || userInfo.username }}</p>
      <el-button @click="logout">退出登录</el-button>
    </div>
    <div v-else>
      <el-button @click="$router.push('/portal/login')">去登录</el-button>
    </div>
  </div>
</template>

<script>
import { getAppUserInfo } from '@/api/appLogin'

export default {
  name: 'PortalHome',
  data() {
    return {
      userInfo: null
    }
  },
  created() {
    this.getUserInfo()
  },
  methods: {
    getUserInfo() {
      const token = localStorage.getItem('app_token')
      if (token) {
        getAppUserInfo().then(res => {
          this.userInfo = res.user
        }).catch(err => {
          console.error('获取用户信息失败:', err)
          localStorage.removeItem('app_token')
          this.$message.error('登录状态已过期，请重新登录')
          this.$router.push('/portal/login')
        })
      }
    },
    logout() {
      localStorage.removeItem('app_token')
      this.userInfo = null
      this.$message.success('已退出登录')
      this.$router.push('/portal/login')
    }
  }
}
</script>

<style scoped>
.portal-home {
  padding: 50px;
  text-align: center;
}

h1 {
  color: #333;
  margin-bottom: 20px;
}

p {
  color: #666;
  margin-bottom: 30px;
}
</style>