<template>
  <div class="app-container">
    <!-- 使用动态组件标签渲染当前组件 -->
    <transition name="fade-transform" mode="out-in">
      <component :is="currentDashboardComponent" />
    </transition>
  </div>
</template>

<script setup name="Index">
import { computed } from 'vue';
import useUserStore from '@/store/modules/user';
import AdminDashboard from './dashboard/components/AdminDashboard.vue';
import MerchantDashboard from './dashboard/components/MerchantDashboard.vue';

const userStore = useUserStore();

// 定义计算属性，根据角色返回对应的组件
const currentDashboardComponent = computed(() => {
  const isAdmin = userStore.roles.includes('admin');
  return isAdmin ? AdminDashboard : MerchantDashboard;
});
</script>

<style scoped lang="scss">
.app-container {
  padding: 20px;
  background-color: #f6f8f9;
  min-height: calc(100vh - 84px);
  position: relative;
}

/* 简单的过渡动画，让切换身份时更丝滑 */
.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: all 0.3s;
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(-30px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(30px);
}
</style>