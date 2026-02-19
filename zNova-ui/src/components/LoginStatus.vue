<template>
  <div class="login-status">
    <!-- 未登录状态 -->
    <transition name="fade">
      <div v-if="!isLoggedIn" key="login">
        <el-button
          type="primary"
          size="small"
          @click="handleLoginClick"
          class="login-btn"
        >
          登录
        </el-button>
      </div>
    </transition>

    <!-- 已登录状态 -->
    <transition name="fade">
      <div v-if="isLoggedIn" key="user" class="user-info" @click="handleUserClick">
        <div class="user-avatar">
          <img 
            :src="userAvatar" 
            :alt="finalUserInfo?.nickname || finalUserInfo?.username"
            @error="handleAvatarError"
            class="avatar-img"
          />
        </div>
        <span class="user-nickname">{{ finalUserInfo?.nickname || finalUserInfo?.username }}</span>
        <el-icon class="arrow-icon"><ArrowDown /></el-icon>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowDown } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import useAppUserStore from '@/store/modules/appUser'

const router = useRouter()
const appUserStore = useAppUserStore()

// 定义props
const props = defineProps({
  // 可以从父组件传入用户信息，优先级高于本地存储
  userInfo: {
    type: Object,
    default: null
  }
})

// 定义事件
const emit = defineEmits(['login', 'logout', 'profile'])

// 默认头像
const defaultAvatar = '/img/profile.jpg'

// 计算登录状态
const isLoggedIn = computed(() => appUserStore.isLoggedIn)

// 计算最终用户信息
const finalUserInfo = computed(() => {
  return props.userInfo || appUserStore.userInfo
})

// 计算用户头像
const userAvatar = computed(() => appUserStore.userAvatar)

// 获取用户信息
const fetchUserInfo = async () => {
  if (appUserStore.isLoggedIn && !appUserStore.userInfo) {
    await appUserStore.fetchUserInfo()
  }
}

// 处理登录按钮点击
const handleLoginClick = () => {
  emit('login')
  router.push('/portal/login')
}

// 处理用户信息点击
const handleUserClick = () => {
  emit('profile')
  router.push('/portal/user/profile')
}

// 处理头像加载失败
const handleAvatarError = (event) => {
  event.target.src = defaultAvatar
}

// 监听登录状态变化
watch(
  () => props.userInfo,
  (newVal) => {
    if (newVal) {
      appUserStore.updateLocalUserInfo(newVal)
    }
  }
)

// 组件挂载时获取用户信息
onMounted(() => {
  if (!props.userInfo) {
    fetchUserInfo()
  }
})
</script>

<style scoped>
.login-status {
  display: flex;
  align-items: center;
  cursor: pointer;
}

/* 登录按钮样式 */
.login-btn {
  border-radius: 20px;
  padding: 6px 16px;
  font-size: 14px;
  transition: all 0.3s ease;
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
}

/* 用户信息样式 */
.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 12px;
  border-radius: 20px;
  background-color: #f5f7fa;
  transition: all 0.3s ease;
  cursor: pointer;
}

.user-info:hover {
  background-color: #ecf5ff;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
}

/* 用户头像样式 */
.user-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  overflow: hidden;
  border: 2px solid #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: all 0.3s ease;
}

/* 用户昵称样式 */
.user-nickname {
  font-size: 14px;
  color: #303133;
  font-weight: 500;
  max-width: 100px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 箭头图标样式 */
.arrow-icon {
  font-size: 12px;
  color: #909399;
  transition: transform 0.3s ease;
}

.user-info:hover .arrow-icon {
  transform: rotate(180deg);
  color: #409eff;
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
</style>