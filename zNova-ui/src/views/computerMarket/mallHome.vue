<template>
  <div class="common-layout">
    <el-container class="app-container">
      <Header />
      
      <el-main class="main-content">
        <!-- 轮播图区域：增加圆角和阴影，使其更像一个展示台 -->
        <div class="carousel-wrapper">
          <el-carousel trigger="click" height="500px" class="main-carousel">
            <el-carousel-item>
              <img src="@/assets/images/1.jpg" alt="轮播图1" class="carousel-img">
            </el-carousel-item>
            <el-carousel-item>
              <div class="carousel-content">
                <img src="@/assets/images/2.jpg" alt="轮播图2" class="carousel-img">
                <div class="carousel-overlay light">
                  <h3>找到你的完美装备</h3>
                  <h1>性能，一触即知</h1>
                </div>
              </div>
            </el-carousel-item>
            <el-carousel-item>
              <div class="carousel-content">
                <img src="@/assets/images/3.jpg" alt="轮播图3" class="carousel-img">
                <div class="carousel-overlay dark">
                  <h3>为创意与游戏而生</h3>
                  <h1>打造极致体验</h1>
                </div>
              </div>
            </el-carousel-item>
          </el-carousel>
        </div>

        <!-- 快捷入口区域：改为卡片式，更符合浅色极简风格 -->
        <div class="service-cards">
          <div class="service-card" @click="goToSale">
            <el-icon class="service-icon"><Goods /></el-icon>
            <div class="service-info">
              <h3>我要购买</h3>
              <p>精选全新/二手优质主机</p>
            </div>
            <el-icon class="arrow-icon"><ArrowRight /></el-icon>
          </div>
          <div class="service-card" @click="goToRental">
            <el-icon class="service-icon"><Timer /></el-icon>
            <div class="service-info">
              <h3>我要租赁</h3>
              <p>低成本体验高性能设备</p>
            </div>
            <el-icon class="arrow-icon"><ArrowRight /></el-icon>
          </div>
          <div class="service-card" @click="goToBuild">
            <el-icon class="service-icon"><Cpu /></el-icon>
            <div class="service-info">
              <h3>DIY 装机</h3>
              <p>定制专属你的强力装备</p>
            </div>
            <el-icon class="arrow-icon"><ArrowRight /></el-icon>
          </div>
        </div>

        <!-- 热门机型标题 -->
        <div class="section-header">
          <h2>热门机型推荐</h2>
          <span class="subtitle">Hot Recommendations</span>
        </div>

        <!-- 商品列表展示区域 -->
        <div class="product-list">
          <div v-for="product in productList" :key="product.id" class="product-card">
            
            <!-- 左侧：图片 -->
            <div class="product-thumb">
              <img 
                :src="product.imageUrl ? handleImageUrl(product.imageUrl) : '@/assets/images/default-product.jpg'" 
                alt="商品图片" 
              >
              <div class="product-tag" :class="product.productType === '1' ? 'tag-rent' : 'tag-sale'">
                {{ product.productType === '1' ? '租赁' : '售卖' }}
              </div>
            </div>

            <!-- 中间：信息详情 (移除 el-form，使用更现代的布局) -->
            <div class="product-info">
              <h3 class="product-title" @click="goToDetail(product)">{{ product.productName }}</h3>
              
              <!-- 硬件参数网格 -->
              <div class="specs-grid">
                <div class="spec-item">
                  <span class="label">CPU</span>
                  <span class="value">{{ product.cpu }}</span>
                </div>
                <div class="spec-item">
                  <span class="label">显卡</span>
                  <span class="value">{{ product.graphicsCard }}</span>
                </div>
                <div class="spec-item">
                  <span class="label">内存</span>
                  <span class="value">{{ product.memory }}</span>
                </div>
                <div class="spec-item">
                  <span class="label">硬盘</span>
                  <span class="value">{{ product.ssd }}</span>
                </div>
                <div class="spec-item full-width">
                  <span class="label">主板</span>
                  <span class="value">{{ product.motherboard }}</span>
                </div>
              </div>

              <!-- 价格显示 -->
              <div class="price-box">
                <template v-if="product.productType === '1'">
                  <span class="currency">¥</span>
                  <span class="amount">{{ product.rentPrice || 0 }}</span>
                  <span class="unit">/天起</span>
                </template>
                <template v-else>
                  <span class="currency">¥</span>
                  <span class="amount">{{ product.salePrice || 0 }}</span>
                </template>
              </div>
            </div>

            <!-- 右侧：评分与操作 -->
            <div class="product-action-zone">
              <div class="score-circle">
                <el-progress 
                  type="dashboard" 
                  :percentage="product.performanceScore ? Math.min(100, Math.round((product.performanceScore / 28387) * 100)) : 0"
                  :color="customColors"
                  :stroke-width="8"
                  :width="100"
                >
                  <template #default>
                    <div class="score-inner">
                      <span class="score-num">{{ product.performanceScore || '暂无数据' }}</span>
                      <span class="score-text">性能分</span>
                    </div>
                  </template>
                </el-progress>
              </div>

              <div class="action-buttons">
                <el-button type="primary" size="large" round class="btn-main" @click.prevent="handlePurchase(product)">
                  {{ product.productType === '1' ? '立即租赁' : '立即购买' }}
                </el-button>
                <div class="secondary-actions">
                  <el-button link @click.prevent="goToDetail(product)">查看详情</el-button>
                  <el-divider direction="vertical" />
                  <el-button link @click.prevent="addToCart(product)">加入购物车</el-button>
                </div>
              </div>
            </div>

          </div>
        </div>
      </el-main>
      
      <el-footer class="footer-content">
        <p>© 2024 电脑租售与硬件性能智能评估平台 | 极致性能 触手可及</p>
      </el-footer>
    </el-container>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
// 引入图标
import { Goods, Timer, Cpu, ArrowRight } from '@element-plus/icons-vue';
import { listFrontProduct } from "@/api/front/product";
import Header from './Header.vue'; 
import { addToCart as addToCartAPI } from '@/api/shop/cart';
import { handleImageUrl } from '@/utils/ruoyi'
import { ElMessage } from 'element-plus';

const router = useRouter();

// 进度条颜色自定义
const customColors = [
  { color: '#f56c6c', percentage: 20 },
  { color: '#e6a23c', percentage: 40 },
  { color: '#5cb87a', percentage: 60 },
  { color: '#1989fa', percentage: 80 },
  { color: '#6f7ad3', percentage: 100 },
];

// 导航方法
const goToRental = () => router.push('/computer-market/rental');
const goToSale = () => router.push('/computer-market/sale');
const goToBuild = () => router.push('/computer-market/build');
const goToDetail = (product) => router.push(`/computer-market/product-detail/${product.id}`);

// 购买/租赁逻辑
const handlePurchase = (product) => {
  const productData = {
    productId: product.id,
    productName: product.productName,
    productImg: product.imageUrl,
    price: product.productType === '1' ? product.rentPrice : product.salePrice,
    businessType: product.productType === '1' ? '1' : '2',
    quantity: 1,
    daterange: [], 
    rentDays: 0    
  };
  router.push({
    path: '/computer-market/checkout',
    query: {
      directBuy: 'true',
      product: encodeURIComponent(JSON.stringify(productData))
    }
  });
};

const addToCart = async (product) => {
  try {
    const cartData = {
      productId: product.id,
      productName: product.productName,
      productImg: product.imageUrl,
      price: product.rentPrice || product.salePrice || 0,
      businessType: product.productType === '1' ? '1' : '2', 
      quantity: 1
    };
    await addToCartAPI(cartData);
    ElMessage.success('已加入购物车');
  } catch (error) {
    console.error('加入购物车失败:', error);
    ElMessage.error('操作失败');
  }
};

const productList = ref([]);



const getProductData = async () => {
  try {
    const response = await listFrontProduct({});
    if (response.rows && response.rows.length > 0) {
      const hotProducts = response.rows.filter(product => [1, 2, 3].includes(product.id));
      productList.value = hotProducts;
      productList.value.sort((a, b) => a.id - b.id);
    } else {
      productList.value = [];
    }
  } catch (error) {
    console.error('获取商品数据失败:', error);
    productList.value = [];
  }
};

onMounted(() => {
  getProductData();
});
</script>

<style scoped>
/* 全局容器：使用柔和的浅灰色背景 */
.app-container {
  background-color: #f5f7fa;
  min-height: 100vh;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
}

.main-content {
  padding: 0;
  margin-top: 60px; /* 适配 Header 高度 */
  max-width: 100%;
  overflow-x: hidden;
}

/* --- 轮播图区域优化 --- */
.carousel-wrapper {
  background: #fff;
  margin-bottom: 20px;
}



.carousel-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.carousel-content {
  position: relative;
  width: 100%;
  height: 100%;
}

.carousel-overlay {
  position: absolute;
  top: 50%;
  left: 15%;
  transform: translateY(-50%);
  z-index: 10;
}

.carousel-overlay h3 {
  font-size: 24px;
  margin-bottom: 10px;
  font-weight: 300;
  opacity: 0.9;
}

.carousel-overlay h1 {
  font-size: 48px;
  font-weight: 700;
  margin: 0;
  letter-spacing: 1px;
}

/* 针对不同背景图的文字颜色适配 */
.carousel-overlay.light { color: #fff; text-shadow: 0 2px 10px rgba(0,0,0,0.3); }
.carousel-overlay.dark { color: #303133; }

/* --- 服务卡片（原按钮区域）优化 --- */
.service-cards {
  display: flex;
  justify-content: center;
  gap: 30px;
  padding: 40px 20px;
  max-width: 1200px;
  margin: -40px auto 0; 
  position: relative;
  z-index: 20;
}

.service-card {
  flex: 1;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  padding: 25px 30px;
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.05);
  display: flex;
  align-items: center;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid #fff;
}

.service-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 40px rgba(111, 122, 211, 0.15);
  border-color: #d9ecff;
}

.service-icon {
  font-size: 46px;
  color: #626aef;
  background: #eff0ff;
  padding: 12px;
  border-radius: 12px;
  margin-right: 20px;
}

.service-info h3 {
  margin: 0 0 5px;
  font-size: 18px;
  color: #303133;
}

.service-info p {
  margin: 0;
  font-size: 13px;
  color: #909399;
}

.arrow-icon {
  margin-left: auto;
  color: #c0c4cc;
}

/* --- 标题区域 --- */
.section-header {
  text-align: center;
  margin: 40px 0 30px; /* 增加与上方内容的距离 */
}

.section-header h2 {
  font-size: 28px;
  color: #303133;
  margin-bottom: 8px;
  font-weight: 600;
}

.section-header .subtitle {
  font-size: 14px;
  color: #909399;
  text-transform: uppercase;
  letter-spacing: 2px;
  position: relative;
  padding-bottom: 10px;
}

.section-header .subtitle::after {
  content: "";
  display: block;
  width: 40px;
  height: 2px;
  background: #626aef;
  margin: 8px auto 0;
}

/* --- 商品列表优化 --- */
.product-list {
  max-width: 1200px;
  margin: 0 auto 60px;
  padding: 0 20px;
  display: flex;
  flex-direction: column;
  gap: 25px;
}

.product-card {
  display: flex;
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.03);
  transition: all 0.3s ease;
  border: 1px solid #ebeef5;
}

.product-card:hover {
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.08);
  border-color: #d9ecff;
}

/* 商品图片 */
.product-thumb {
  width: 280px;
  height: 240px;
  position: relative;
  background: #fcfcfc;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.product-thumb img {
  max-width: 85%;
  max-height: 85%;
  object-fit: contain;
  transition: transform 0.4s;
}

.product-card:hover .product-thumb img {
  transform: scale(1.08);
}

.product-tag {
  position: absolute;
  top: 15px;
  left: 15px;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: bold;
  color: #fff;
}
.tag-rent { background: linear-gradient(135deg, #a0cfff 0%, #409eff 100%); }
.tag-sale { background: linear-gradient(135deg, #fab6b6 0%, #f56c6c 100%); }

/* 商品信息 */
.product-info {
  flex: 1;
  padding: 25px 30px;
  display: flex;
  flex-direction: column;
}

.product-title {
  margin: 0 0 20px;
  font-size: 20px;
  color: #303133;
  cursor: pointer;
  transition: color 0.2s;
}
.product-title:hover { color: #626aef; }

.specs-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px 24px;
  margin-bottom: 20px;
}

.spec-item {
  font-size: 14px;
  display: flex;
  align-items: center;
}

.spec-item .label {
  color: #909399;
  width: 40px;
  margin-right: 10px;
}

.spec-item .value {
  color: #606266;
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.full-width { grid-column: span 2; }

.price-box {
  margin-top: auto;
  color: #f56c6c;
}

.currency { font-size: 18px; font-weight: bold; }
.amount { font-size: 28px; font-weight: bold; margin: 0 4px; }
.unit { font-size: 13px; color: #909399; }

/* 右侧操作区 */
.product-action-zone {
  width: 220px;
  border-left: 1px solid #f0f2f5;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px;
  background: #fafafa;
}

.score-circle {
  margin-bottom: 20px;
}

.score-inner {
  display: flex;
  flex-direction: column;
  align-items: center;
  line-height: 1.2;
}

.score-num {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.score-text {
  font-size: 12px;
  color: #909399;
}

.action-buttons {
  width: 100%;
  text-align: center;
}

.btn-main {
  width: 80%;
  margin-bottom: 12px;
  background-color: #626aef; /* 统一品牌色 */
  border-color: #626aef;
  box-shadow: 0 4px 12px rgba(98, 106, 239, 0.3);
}
.btn-main:hover {
  background-color: #858bf5;
  border-color: #858bf5;
}

.secondary-actions .el-button {
  font-size: 13px;
  color: #909399;
}
.secondary-actions .el-button:hover {
  color: #626aef;
}

/* 页脚 */
.footer-content {
  background: #fff;
  padding: 30px 0;
  color: #909399;
  font-size: 13px;
  border-top: 1px solid #ebeef5;
}
</style>