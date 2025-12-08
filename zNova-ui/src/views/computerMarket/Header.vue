<template>
  <!-- 头部容器：毛玻璃效果 -->
  <el-header class="header-wrapper">
    <div class="header-content">
      
      <!-- 左侧：Logo区域 -->
      <div class="header-left" @click="goToHome">
        <div class="logo-icon">
          <el-icon><Monitor /></el-icon>
        </div>
        <span class="platform-text">新星硬件 · 智能评估平台</span>
      </div>
      
      <!-- 右侧：导航与用户中心 -->
      <div class="header-right">
        <!-- 导航菜单 -->
        <nav class="nav-menu">
          <div class="nav-item" :class="{ active: currentPath === '/computer-market' }" @click="goToHome">首页</div>
          <div class="nav-item" :class="{ active: currentPath.includes('/sale') }" @click="goToSale">出售</div>
          <div class="nav-item" :class="{ active: currentPath.includes('/rental') }" @click="goToRental">租赁</div>
          <div class="nav-item" :class="{ active: currentPath.includes('/build') }" @click="goToBuild">装机</div>
          <div class="nav-item" :class="{ active: currentPath.includes('/shopping-cart') }" @click="goToCart">
            购物车
          </div>
          <div class="nav-item merchant-link" @click="goToMerchant">商家入驻</div>
        </nav>

        <el-divider direction="vertical" class="header-divider" />

        <!-- 用户登录状态 / 头像下拉菜单 -->
        <div class="user-area">
          <template v-if="isLogin">
            <!-- trigger="hover" 实现鼠标移入弹出 -->
            <el-dropdown trigger="hover" @command="handleCommand">
              
              <!-- 头像包裹区：添加点击事件跳转个人中心 -->
              <div class="user-avatar-wrapper" @click="goToProfile">
                <el-avatar :size="32" :src="userAvatarUrl" class="user-avatar">
                  {{ userInfo.nickname ? userInfo.nickname.charAt(0) : 'User' }}
                </el-avatar>
                <span class="username">{{ userInfo.nickname || userInfo.username || '用户' }}</span>
                <el-icon class="el-icon--right"><CaretBottom /></el-icon>
              </div>
              
              <!-- 下拉菜单内容 -->
              <template #dropdown>
                <el-dropdown-menu class="custom-dropdown">
                  <el-dropdown-item command="profile" icon="User">个人中心</el-dropdown-item>
                  <el-dropdown-item command="orders" icon="List">我的订单</el-dropdown-item>
                  <el-dropdown-item command="address" icon="Location">地址管理</el-dropdown-item>
                  <el-dropdown-item command="logout" divided icon="SwitchButton" style="color: #f56c6c;">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>

          <template v-else>
            <div class="auth-buttons">
              <el-button link class="login-btn" @click="router.push('/portal/login')">登录</el-button>
              <el-button type="primary" round size="small" class="register-btn" @click="router.push('/portal/register')">注册</el-button>
            </div>
          </template>
        </div>
      </div>
    </div>
  </el-header>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Monitor, CaretBottom, List, SwitchButton, Location, User } from '@element-plus/icons-vue';
import { getAppToken, removeAppToken } from '@/utils/auth';
import { getAppUserInfo } from '@/api/appLogin'; // 导入获取用户信息的API
import { formatAvatarUrl } from '@/utils/avatarUtils'; // 导入头像处理工具

const router = useRouter();
const route = useRoute();

// 获取当前路由路径，用于高亮导航
const currentPath = computed(() => route.path);

// 用户状态
const isLogin = ref(false);
const userInfo = ref({});

// 计算头像 URL（处理相对路径/OSS路径）
const userAvatarUrl = computed(() => {
  if (userInfo.value && userInfo.value.avatar) {
    return formatAvatarUrl(userInfo.value.avatar);
  }
  return ''; // 返回空字符串让 el-avatar 显示文字 fallback
});

// 检查登录状态并获取最新用户信息
const checkLogin = async () => {
  const token = getAppToken();
  if (token) {
    isLogin.value = true;
    try {
      // 调用接口获取最新用户信息（确保头像是最新的）
      const res = await getAppUserInfo();
      if (res && res.user) {
        userInfo.value = res.user;
        // 可选：更新本地缓存
        localStorage.setItem('user_info', JSON.stringify(res.user));
      }
    } catch (error) {
      console.error("获取用户信息失败", error);
      // 如果接口调用失败，尝试从本地缓存读取兜底
      const storedUser = localStorage.getItem('user_info');
      if (storedUser) {
        userInfo.value = JSON.parse(storedUser);
      }
    }
  } else {
    isLogin.value = false;
    userInfo.value = {};
  }
};

// 导航跳转方法
const goToHome = () => router.push('/computer-market');
const goToSale = () => router.push('/computer-market/sale');
const goToRental = () => router.push('/computer-market/rental');
const goToBuild = () => router.push('/computer-market/build');
const goToCart = () => router.push('/computer-market/shopping-cart');
const goToMerchant = () => router.push('/login');
const goToProfile = () => router.push('/portal/user/profile'); // 跳转个人中心

// 下拉菜单指令处理
const handleCommand = (command) => {
  switch (command) {
    case 'profile':
      goToProfile();
      break;
    case 'orders':
      router.push({ path: '/portal/user/order', query: { activeTab: 'all' } });
      break;
    case 'address':
      router.push('/portal/user/address');
      break;
    case 'logout':
      handleLogout();
      break;
  }
};

// 退出登录
const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    removeAppToken(); // 清除 Token
    localStorage.removeItem('user_info'); // 清除用户信息
    isLogin.value = false;
    userInfo.value = {};
    ElMessage.success('已退出登录');
    router.push('/portal/login');
  }).catch(() => {});
};

onMounted(() => {
  checkLogin();
});
</script>

<style scoped>
/* 头部整体容器 */
.header-wrapper {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 64px;
  padding: 0;
  z-index: 1000;
  background: rgba(255, 255, 255, 0.9); /* 白色半透明 */
  backdrop-filter: blur(12px); /* 毛玻璃模糊 */
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.03);
  transition: all 0.3s ease;
}

.header-content {
  max-width: 1400px; /* 限制最大宽度，大屏更好看 */
  margin: 0 auto;
  height: 100%;
  padding: 0 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

/* 左侧 Logo */
.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  transition: opacity 0.3s;
}

.header-left:hover {
  opacity: 0.8;
}

.logo-icon {
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, #626aef 0%, #a0cfff 100%);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 20px;
}

.platform-text {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  letter-spacing: 0.5px;
}

/* 右侧区域 */
.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

/* 导航菜单 */
.nav-menu {
  display: flex;
  align-items: center;
  gap: 30px;
}

.nav-item {
  position: relative;
  font-size: 15px;
  color: #606266;
  cursor: pointer;
  padding: 6px 0;
  font-weight: 500;
  transition: all 0.3s;
}

.nav-item:hover, .nav-item.active {
  color: #626aef; /* 主题色 */
}

/* 导航下划线动画 */
.nav-item::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  width: 0;
  height: 2px;
  background-color: #626aef;
  transition: all 0.3s ease;
  transform: translateX(-50%);
  border-radius: 2px;
  opacity: 0;
}

.nav-item:hover::after, .nav-item.active::after {
  width: 80%;
  opacity: 1;
}

.merchant-link {
  color: #909399;
  font-size: 14px;
}

.header-divider {
  height: 24px;
  border-color: #dcdfe6;
}

/* 用户区域 */
.user-area {
  min-width: 120px;
  display: flex;
  justify-content: flex-end;
}

/* 头像下拉触发区 */
.user-avatar-wrapper {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer; /* 关键：显示手型，表示可点击 */
  padding: 4px 8px;
  border-radius: 20px;
  transition: background 0.2s;
}

.user-avatar-wrapper:hover {
  background: rgba(0, 0, 0, 0.05);
}

.user-avatar {
  background: #626aef;
  color: #fff;
  border: 2px solid #fff;
  box-shadow: 0 2px 8px rgba(98, 106, 239, 0.2);
}

.username {
  font-size: 14px;
  color: #303133;
  max-width: 80px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 未登录按钮组 */
.auth-buttons {
  display: flex;
  align-items: center;
  gap: 10px;
}

.login-btn {
  color: #606266;
}
.login-btn:hover {
  color: #626aef;
}

.register-btn {
  background-color: #626aef;
  border-color: #626aef;
}
.register-btn:hover {
  background-color: #7b83f5;
  border-color: #7b83f5;
}
</style>