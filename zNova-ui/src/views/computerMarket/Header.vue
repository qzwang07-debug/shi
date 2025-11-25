<template>
  <el-header class="header-container">
    <div class="header-content">
      <!-- 左侧区域：logo + 文字 -->
      <div class="header-left">
        <span class="platform-text">电脑租售与硬件性能智能评估平台</span>
      </div>
      
      <!-- 中间占位 -->
      <div class="header-center"></div>
      
      <!-- 右侧导航 -->
      <div class="header-right">
        <div class="nav-item" @click="goToHome">首页</div>
        <div class="nav-item" @click="goToSale">出售</div>
        <div class="nav-item" @click="goToRental">租赁</div>
        <div class="nav-item" @click="goToBuild">装机</div> <!-- 新增装机按钮 -->
        <div class="nav-item" @click="goToCart">购物车</div>
        <div class="nav-item">商家</div>
        <div class="nav-item">登录</div>
      </div>
    </div>
  </el-header>
</template>

<script setup>
import { useRouter } from 'vue-router';

const router = useRouter();

// 导航方法
const goToHome = () => {
  router.push('/computer-market');
};

const goToSale = () => {
  router.push('/computer-market/sale');
};

const goToRental = () => {
  router.push('/computer-market/rental');
};

// 新增装机页面导航
const goToBuild = () => {
  router.push('/computer-market/build');
};

const goToCart = () => {
  router.push('/computer-market/shopping-cart');
};
</script>

<style scoped>
.header-container {
  background-color: #ffffff;
  padding: 0 20px;
  border-bottom: 1px solid #e0e0e0;
  height: 60px;
  border-radius: 20px;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
}

.header-content {
  display: flex;
  width: 100%;
  height: 100%;
  padding: 0 20px;
  align-items: center;
  border-radius: 20px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 15px;
  font-weight: 500;
  color: #000000;
  width: 33.333%;
}

.platform-text {
  font-size: 16px;
  white-space: nowrap;
}

.header-center {
  width: 33.333%;
}

.header-right {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 30px; /* 减小间距（从50px改为30px，避免间距过大） */
  width: 60%; /* 增加宽度（从33.333%改为60%，提供足够空间） */
  font-size: 14px;
}

.nav-item {
  position: relative;
  cursor: pointer;
  padding: 5px 0;
  transition: color 0.2s;
}

.nav-item:hover {
  color: #0071c5;
}

.nav-item::after {
  content: '';
  position: absolute;
  left: 0;
  bottom: -5px;
  width: 0;
  height: 2px;
  background-color: #0071c5;
  transition: width 0.3s ease;
}

.nav-item:hover::after {
  width: 100%;
}
</style>