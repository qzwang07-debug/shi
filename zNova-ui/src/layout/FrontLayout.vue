<template>
  <div class="front-layout">
    <!-- 全局导航栏 -->
    <header class="navbar">
      <div class="navbar-container">
        <!-- Logo -->
        <div class="navbar-logo" @click="goToHome">
          <h1 class="logo-text">电脑租售平台</h1>
        </div>
        
        <!-- 导航菜单 -->
        <nav class="navbar-menu">
          <router-link to="/computer-market" class="menu-item">首页</router-link>
          <router-link to="/computer-market/rental" class="menu-item">租赁</router-link>
          <router-link to="/computer-market/sale" class="menu-item">出售</router-link>
          <router-link to="/computer-market/Build" class="menu-item">装机</router-link>
        </nav>
        
        <!-- 登录状态 -->
        <div class="navbar-login">
          <LoginStatus />
        </div>
      </div>
    </header>
    
    <!-- 页面内容 -->
    <main class="main-content">
      <router-view />
    </main>
    
    <!-- 路由加载状态 -->
    <transition name="fade">
      <div v-if="isLoading" class="loading-overlay">
        <el-spinner type="dots" size="60px" />
        <p class="loading-text">加载中...</p>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import LoginStatus from '@/components/LoginStatus.vue'

const router = useRouter()
const route = useRoute()

// 路由加载状态
const isLoading = ref(false)

// 跳转到首页
const goToHome = () => {
  router.push('/computer-market')
}

// 监听路由变化，显示加载状态
watch(
  () => route.path,
  (newPath, oldPath) => {
    if (newPath !== oldPath) {
      isLoading.value = true
      // 模拟加载延迟，实际项目中可以根据路由组件的加载状态来控制
      const timer = setTimeout(() => {
        isLoading.value = false
        clearTimeout(timer)
      }, 300)
    }
  }
)

// 组件挂载时
onMounted(() => {
  // 初始加载时不显示加载状态
  isLoading.value = false
})
</script>

<style scoped>
.front-layout {
  min-height: 100vh;
  background-color: #f5f5f5;
  display: flex;
  flex-direction: column;
}

/* 导航栏样式 */
.navbar {
  background-color: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.navbar-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 60px;
}

/* Logo样式 */
.navbar-logo {
  cursor: pointer;
}

.logo-text {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #409eff;
}

/* 导航菜单样式 */
.navbar-menu {
  display: flex;
  gap: 30px;
}

.menu-item {
  text-decoration: none;
  color: #303133;
  font-size: 16px;
  font-weight: 500;
  padding: 8px 0;
  position: relative;
  transition: color 0.3s ease;
}

.menu-item:hover {
  color: #409eff;
}

.menu-item::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 0;
  height: 2px;
  background-color: #409eff;
  transition: width 0.3s ease;
}

.menu-item:hover::after {
  width: 100%;
}

/* 登录状态容器 */
.navbar-login {
  display: flex;
  align-items: center;
}

/* 主内容区域 */
.main-content {
  flex: 1;
  padding: 20px 0;
}

/* 加载状态样式 */
.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(255, 255, 255, 0.8);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  z-index: 9999;
}

.loading-text {
  margin-top: 20px;
  font-size: 16px;
  color: #606266;
}

/* 过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .navbar-container {
    padding: 0 10px;
  }
  
  .navbar-menu {
    gap: 15px;
  }
  
  .menu-item {
    font-size: 14px;
  }
  
  .logo-text {
    font-size: 18px;
  }
}
</style>