<template>
  <div class="common-layout">
    <el-container class="app-container">
      <!-- 导入的头部导航栏组件 -->
      <Header />
      
      <!-- 主要内容区域 -->
      <el-main class="main-content">
        <div class="carousel-container">
          <!-- 轮播图 -->
          <el-carousel trigger="click" height="520px" indicator-position="bottom">
            <el-carousel-item>
              <img 
                src="@/assets/images/1.jpg" 
                alt="轮播图1" 
                class="carousel-img"
              >
            </el-carousel-item>
            <el-carousel-item>
              <img 
                src="@/assets/images/2.jpg" 
                alt="轮播图2" 
                class="carousel-img"
              >
              <div class="carousel-text carousel-text-white">
                找到你的完美装备<br>
                <h1>性能，一触即知</h1>
              </div>
            </el-carousel-item>
            <el-carousel-item>
              <img 
                src="@/assets/images/3.jpg" 
                alt="轮播图3" 
                class="carousel-img"
              >
              <div class="carousel-text carousel-text-black">
                为游戏玩家、直播用户和创意人士<br>
                <h2>精心打造出色的电脑体验</h2>
              </div>
            </el-carousel-item>
          </el-carousel>

          <!-- 按钮区域 -->
          <div class="buttons-container">
             <a href="#" class="custom-btn" @click.prevent="goToSale">我要购买</a>
            <a href="#" class="custom-btn" @click.prevent="goToRental">我要租赁</a>
            <a href="#" class="custom-btn" @click.prevent="goToBuild">DIY装机</a>
          </div>
        </div>

        <!-- 热门机型区域 -->
        <div class="hot-model-section">
          <span class="hot-model-text"><h2>热门机型</h2></span>
          <div class="hot-model-line"></div>
        </div>

        <!-- 商品列表展示区域 -->
        <div class="product-section">
          <div v-for="product in productList" :key="product.id" class="product-item">
            <h3 class="product-name">{{ product.productName }}</h3>

            <div class="product-detail">
              <div class="product-image">
                <img 
                  :src="product.imageUrl ? handleImageUrl(product.imageUrl) : '@/assets/images/default-product.jpg'" 
                  alt="商品图片" 
                  class="product-img"
                >
              </div>

              <div class="product-form">
                <el-form :model="product" label-width="80px" class="hardware-form">
                  <el-form-item label="CPU">
                    <el-input v-model="product.cpu" readonly />
                  </el-form-item>
                  <el-form-item label="内存">
                    <el-input v-model="product.memory" readonly />
                  </el-form-item>
                  <el-form-item label="硬盘">
                    <el-input v-model="product.ssd" readonly />
                  </el-form-item>
                  <el-form-item label="显卡">
                    <el-input v-model="product.graphicsCard" readonly />
                  </el-form-item>
                  <el-form-item label="主板">
                    <el-input v-model="product.motherboard" readonly />
                  </el-form-item>
                  <el-form-item label="价格">
                    <template v-if="product.productType === '1'">
                      <el-input :value="`租赁: ${product.rentPrice || 0}元/天`" readonly />
                    </template>
                    <template v-else-if="product.productType === '2'">
                      <el-input :value="`销售: ${product.salePrice || 0}元`" readonly />
                    </template>
                    <template v-else>
                      <el-input :value="`销售: ${product.salePrice || 0}元 / 租赁: ${product.rentPrice || 0}元/天`" readonly />
                    </template>
                  </el-form-item>
                </el-form>
              </div>

              <div class="performance-score">
                <h3>性能评分</h3>
                <el-progress 
                  type="dashboard" 
                  :percentage="product.performanceScore.percentage"
                  :stroke-width="15"
                  :width="180"
                >
                  <template #default>
                    <span class="percentage-value">{{ product.performanceScore.score }}</span>
                    <span class="percentage-label">综合性能</span>
                  </template>
                </el-progress>
              </div>

              <div class="service-buttons">
                <a href="#" class="action-btn detail" @click.prevent="goToDetail(product)">商品详情</a>
                <a href="#" class="action-btn" :class="product.productType === '1' ? 'rent' : 'buy'" @click.prevent="handlePurchase(product)">
                  {{ product.productType === '1' ? '立即租赁' : '立即购买' }}
                </a>
                <a href="#" class="action-btn consult" @click.prevent="addToCart(product)">加入购物车</a>
              </div>
            </div>
          </div>
        </div>
      </el-main>
      
      <!-- 页脚 -->
      <el-footer class="footer-content">
        <p>电脑租售与硬件性能智能评估平台</p>
      </el-footer>
    </el-container>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElContainer, ElMain, ElFooter, ElCarousel, ElCarouselItem, ElForm, ElFormItem, ElInput, ElProgress, ElMessage } from 'element-plus';
import { listFrontProduct } from "@/api/front/product";
import Header from './Header.vue'; // 导入导航栏组件
import { addToCart as addToCartAPI } from '@/api/shop/cart';
import { handleImageUrl } from '@/utils/ruoyi'
const router = useRouter();

// 导航方法
const goToRental = () => {
  router.push('/computer-market/rental');
};

const goToSale = () => {
  router.push('/computer-market/sale');
};

// 新增装机页面导航
const goToBuild = () => {
  router.push('/computer-market/build');
};

// 跳转到商品详情页
const goToDetail = (product) => {
  router.push(`/computer-market/product-detail/${product.id}`);
};

// 处理立即租赁/购买
const handlePurchase = (product) => {
  // 构建商品数据
  const productData = {
    productId: product.id,
    productName: product.productName,
    productImg: product.imageUrl,
    price: product.productType === '1' ? product.rentPrice : product.salePrice,
    businessType: product.productType === '1' ? '1' : '2',
    quantity: 1,
    daterange: [], // 租赁日期范围
    rentDays: 0    // 租赁天数
  };

  // 跳转到订单结算页面，携带商品数据
  router.push({
    path: '/computer-market/checkout',
    query: {
      directBuy: 'true',
      product: encodeURIComponent(JSON.stringify(productData))
    }
  });
};

// 添加到购物车
const addToCart = async (product) => {
  try {
    // 首页商品数据结构与后端商品不同，需要映射
    const cartData = {
      productId: product.id,
      productName: product.productName,
      productImg: product.imageUrl,
      price: product.rentPrice || product.salePrice || 0,
      businessType: product.productType === '1' ? '1' : '2', // 根据商品类型判断
      quantity: 1
    };
    
    await addToCartAPI(cartData);
    ElMessage.success('已加入购物车');
  } catch (error) {
    console.error('加入购物车失败:', error);
    ElMessage.error('操作失败');
  }
};

// 商品数据
const productList = ref([]);

// 性能分数计算
const calculateScore = (product) => {
  let cpuBaseScore = 0;
  let memoryScore = 0;
  const gpuScore = product.gpuScore || 0;

  if (product.cpuBrand === 'AMD') {
    cpuBaseScore = Math.round((product.cpuMultiCoreScore * 0.5) + product.cpuSingleCoreScore);
  } else if (product.cpuBrand === 'Intel') {
    cpuBaseScore = Math.round((product.cpuMultiCoreScore * 0.3) + product.cpuSingleCoreScore);
  }

  if (product.memoryType === 'DDR4') {
    memoryScore = product.memoryFrequency ;
  } else if (product.memoryType === 'DDR5') {
    memoryScore = product.memoryFrequency/2;
  }

  const totalScore = cpuBaseScore + memoryScore + gpuScore;
  const maxScore = 28387;
  const percentage = Math.min(100, Math.round((totalScore / maxScore) * 100));

  return {
    score: totalScore,
    percentage
  };
};

// 获取商品数据
const getProductData = async () => {
  try {
    const response = await listFrontProduct({});
    if (response.rows && response.rows.length > 0) {
      const hotProducts = response.rows.filter(product => 
        [1, 2, 3].includes(product.id)
      );

      productList.value = hotProducts.map(product => ({
        ...product,
        performanceScore: calculateScore(product)
      }));

      productList.value.sort((a, b) => a.id - b.id);
    } else {
      console.warn('未获取到商品数据');
      productList.value = [];
    }
  } catch (error) {
    console.error('获取商品数据失败:', error);
    productList.value = [];
    ElMessage.error('获取热门商品失败，请重试');
  }
};

// 页面挂载时获取数据
onMounted(() => {
  getProductData();
});
</script>

<style scoped>
/* 保留所有原有样式，移除了导航栏相关样式（已移至Header组件） */
.common-layout {
  min-height: 100vh;
}

.app-container {
  background-color: #FAFAF8;
  color: #000000;
  min-height: 100vh;
}

.main-content {
  background-color: #FAFAF8;
  padding: 20px;
  margin-top: 70px; /* 为固定导航栏留出空间 */
}

/* 轮播图样式 */
.carousel-container {
  width: 100%;
  max-width: 1500px;
  margin: 0 auto;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: relative;
}

.carousel-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.carousel-text {
  position: absolute;
  left: 200px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 28px;
  font-weight: 600;
  line-height: 1.6;
  z-index: 10;
  text-align: left;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
}

.carousel-text-white {
  color: #ffffff;
}

.carousel-text-black {
  color: #000000;
  text-shadow: 0 2px 4px rgba(255, 255, 255, 0.3);
}

.carousel-text h1,
.carousel-text h2 {
  font-size: 2.5rem;
  margin: 10px 0;
}

/* 按钮样式 */
.buttons-container {
  display: flex;
  justify-content: center;
  gap: 40px;
  padding: 40px 0;
}

.custom-btn {
  position: relative;
  width: 200px;
  height: 60px;
  text-align: center;
  line-height: 60px;
  color: #fff;
  font-size: 24px;
  text-decoration: none;
  font-family: sans-serif;
  box-sizing: border-box;
  background: linear-gradient(
    135deg,
    #7b61ff 0%,
    #9a72ff 20%,
    #b883ff 40%,
    #d694ff 50%,
    #b883ff 60%,
    #9a72ff 80%,
    #7b61ff 100%
  );
  background-size: 400%;
  border-radius: 30px;
  z-index: 1;
  transition: all 0.3s;
}

.custom-btn::before {
  content: '';
  position: absolute;
  inset: -5px;
  z-index: -1;
  background: linear-gradient(
    135deg,
    #7b61ff 0%,
    #9a72ff 20%,
    #b883ff 40%,
    #d694ff 50%,
    #b883ff 60%,
    #9a72ff 80%,
    #7b61ff 100%
  );
  background-size: 400%;
  border-radius: 40px;
  opacity: 0;
  filter: blur(15px);
  transition: opacity 0.5s;
}

.custom-btn:hover::before {
  opacity: 1;
  animation: glow 15s linear infinite;
}
.custom-btn:hover {
  animation: glow 15s linear infinite;
}

@keyframes glow {
  0% { background-position: 0% 0; }
  100% { background-position: 400% 0; }
}

.custom-btn:hover::before {
  opacity: 1;
  filter: blur(20px);
  animation: animate 8s linear infinite;
}

.custom-btn:hover {
  animation: animate 8s linear infinite;
}

@keyframes animate {
  0% {
    background-position: 0%;
  }
  100% {
    background-position: 400%;
  }
}

/* 热门机型+横线容器 */
.hot-model-section {
  display: flex;
  align-items: center;
  width: 100%;
  margin-top: 20px;
  padding-left: 20px;
}

.hot-model-text {
  font-size: 20px;
  font-weight: 600;
  color: #9a72ff;
  white-space: nowrap;
}

.hot-model-line {
  height: 2px;
  background-color: #e0e0e0;
  flex-grow: 1;
  margin-left: 15px;
  max-width: 1300px;
}

/* 商品展示区域样式 */
.product-section {
  padding: 30px 20px;
  max-width: 1600px;
  margin: 0 auto;
}

.product-name {
  font-size: 22px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 20px;
  padding-left: 12px;
  border-left: 4px solid #9a72ff;
  display: flex;
  align-items: center;
}
.product-name::after {
  content: '';
  flex-grow: 1;
  height: 1px;
  background-color: #f0f0f0;
  margin-left: 15px;
}
.product-detail {
  display: flex;
  gap: 10px;
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

.product-image {
flex: 0 0 350px;
  height: 300px;
  border-radius: 8px;
  overflow: hidden;
  background-color: #f9f9f9;
  border: 1px solid #f0f0f0;
  position: relative;
}

.product-img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  transition: transform 0.5s ease;
}

.product-img:hover {
  transform: scale(1.2);
}

.product-form {
  flex: 1;
  max-width: 400px; 
}

.hardware-form {
  max-width: 600px;
}

.el-form-item .el-input {
  width: 100%;
  max-width: 400px;
}

/* 性能评分样式 */
.performance-score {
  flex: 0 0 220px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 25px;
  background-color: #f9f9f9;
  border-radius: 10px;
  margin-left: 10px;
  min-height: 280px;
}

.performance-score h3 {
  margin-bottom: 20px;
  color: #333;
  font-size: 18px;
}

.percentage-value {
  display: block;
  margin-top: 10px;
  font-size: 28px;
  font-weight: bold;
}

.percentage-label {
  display: block;
  margin-top: 5px;
  font-size: 14px;
  color: #666;
}

.footer-content {
  background-color: #FAFAF8;
  text-align: center;
  padding: 15px 0;
  border-top: 1px solid #e0e0e0;
}

/* 按钮容器样式 */
.service-buttons {
  flex: 0 0 220px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: 15px;
  padding: 20px;
}

/* 按钮样式 */
.action-btn {
  width: 180px;
  padding: 12px 0;
  border-radius: 8px;
  color: #fff;
  font-size: 16px;
  font-weight: 500;
  text-decoration: none;
  text-align: center;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  position: relative;
  overflow: hidden;
  margin-bottom: 20px;
  margin-left: 20px;
}

.action-btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.15);
}

.action-btn:active {
  transform: translateY(-1px);
}

.action-btn.rent {
  background: linear-gradient(135deg, #7b61ff 0%, #9a72ff 100%);
}

.action-btn.buy {
  background: linear-gradient(135deg, #0071c5 0%, #00a0e9 100%);
}

.action-btn.consult {
  background: linear-gradient(135deg, #f59e0b 0%, #fbbf24 100%);
}

.action-btn.detail {
  background: linear-gradient(135deg, #10b981 0%, #34d399 100%);
}

.action-btn::before {
  content: '';
  display: inline-block;
  width: 16px;
  height: 16px;
  margin-right: 8px;
  vertical-align: middle;
  background-size: contain;
  background-repeat: no-repeat;
}

.product-item {
 background-color: #fff;
  border-radius: 12px;
  padding: 25px;
  margin-bottom: 30px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
  overflow: hidden;
}

.product-item:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.action-btn.rent::before {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' fill='none' viewBox='0 0 24 24' stroke='%23ffffff'%3E%3Cpath stroke-linecap='round' stroke-linejoin='round' stroke-width='2' d='M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z' /%3E%3C/svg%3E");
}

.action-btn.buy::before {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' fill='none' viewBox='0 0 24 24' stroke='%23ffffff'%3E%3Cpath stroke-linecap='round' stroke-linejoin='round' stroke-width='2' d='M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z' /%3E%3C/svg%3E");
}

.action-btn.consult::before {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' fill='none' viewBox='0 0 24 24' stroke='%23ffffff'%3E%3Cpath stroke-linecap='round' stroke-linejoin='round' stroke-width='2' d='M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.97-4.03 9-9 9s-9-4.03-9-9 4.03-9 9-9 9 4.03 9 9z' /%3E%3C/svg%3E");
}

.action-btn.detail::before {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' fill='none' viewBox='0 0 24 24' stroke='%23ffffff'%3E%3Cpath stroke-linecap='round' stroke-linejoin='round' stroke-width='2' d='M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z' /%3E%3C/svg%3E");
}
</style>