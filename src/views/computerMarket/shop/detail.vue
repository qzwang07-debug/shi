<template>
  <div class="product-detail-page">
    <Header />

    <div class="breadcrumb-container">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/computer-market' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item :to="{ path: '/computer-market' }">电脑租赁</el-breadcrumb-item>
        <el-breadcrumb-item>商品详情</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <div class="main-container" v-loading="loading">
      <div v-if="product" class="product-wrapper">
        <div class="product-overview">
          <div class="gallery-section">
            <div class="main-image-box">
              <el-image :src="product.imageUrl ? handleImageUrl(product.imageUrl) : defaultImage"
                :preview-src-list="[product.imageUrl ? handleImageUrl(product.imageUrl) : defaultImage]"
                fit="cover"
                class="main-image"
                :initial-index="0"
              >
                <template #placeholder>
                  <div class="image-slot">加载中...</div>
                </template>
              </el-image>
            </div>
          </div>

          <div class="info-section">
            <h1 class="product-title">{{ product.productName }}</h1>
            <p class="product-subtitle">{{ product.description }}</p>

            <div class="price-card">
              <div v-if="isRent" class="price-row rent-mode">
                <div class="main-price">
                  <span class="label">日租金</span>
                  <span class="currency">¥</span>
                  <span class="amount">{{ product.rentPrice }}</span>
                  <span class="unit">/天</span>
                </div>
                <div class="divider-vertical"></div>
                <div class="deposit-info">
                  <span class="label">押金</span>
                  <span class="value">¥{{ product.deposit || 0 }}</span>
                  <el-tooltip content="归还设备无损坏后，押金将全额退还" placement="top">
                    <el-icon class="info-icon"><QuestionFilled /></el-icon>
                  </el-tooltip>
                </div>
              </div>
              
              <div v-else class="price-row sale-mode">
                <div class="main-price">
                  <span class="label">售价</span>
                  <span class="currency">¥</span>
                  <span class="amount">{{ product.salePrice }}</span>
                </div>
              </div>

              <div class="stock-row">
                <span class="label">{{ isRent ? '租赁状态:' : '库存状态:' }}</span>
                
                <span v-if="realStock > 0" class="stock-val in-stock">
                  <el-icon><CircleCheckFilled /></el-icon> 
                  {{ isRent ? '可租' : '现货' }} ({{ realStock }}件)
                </span>
                
                <span v-else class="stock-val out-stock">
                  <el-icon><CircleCloseFilled /></el-icon> 
                  {{ isRent ? '设备已租完' : '暂时缺货' }}
                </span>
              </div>
            </div>

            <div class="specs-summary">
              <div class="spec-item">
                <span class="spec-label">处理器</span>
                <span class="spec-val">{{ product.cpu }}</span>
              </div>
              <div class="spec-item">
                <span class="spec-label">显卡</span>
                <span class="spec-val">{{ product.graphicsCard }}</span>
              </div>
              <div class="spec-item">
                <span class="spec-label">内存</span>
                <span class="spec-val">{{ product.memory }}</span>
              </div>
            </div>

            <div class="action-area">
              <div class="quantity-selector">
                <span class="qty-label">数量</span>
                <el-input-number 
                  v-model="quantity" 
                  :min="1" 
                  :max="realStock || 1" 
                  :disabled="realStock <= 0"
                  size="large"
                />
              </div>
              
              <div class="btn-group">
                <el-button 
                  class="action-btn cart-btn" 
                  size="large" 
                  @click="addToCart"
                  :disabled="realStock <= 0"
                  plain
                >
                  <el-icon style="margin-right: 8px"><ShoppingCart /></el-icon>
                  加入购物车
                </el-button>
                <el-button 
                  type="primary" 
                  class="action-btn buy-btn" 
                  size="large"
                  @click="buyNow"
                  :disabled="realStock <= 0"
                >
                  立即{{ isRent ? '租赁' : '购买' }}
                </el-button>
              </div>
            </div>
            
            <!-- 评分概览 -->
            <div class="meta-item">
              <span class="label">评分</span>
              <el-rate
                v-model="averageRating"
                disabled
                show-score
                text-color="#ff9900"
                score-template="{value}"
              />
            </div>

            <div class="service-tags">
              <span class="tag"><el-icon><CircleCheck /></el-icon> 正品保障</span>
              <span class="tag"><el-icon><Van /></el-icon> 极速发货</span>
              <span class="tag" v-if="isRent"><el-icon><Refresh /></el-icon> 随租随还</span>
            </div>
          </div>
        </div>

        <!-- 商品标签页区域 -->
        <div class="product-tabs-section">
          <el-tabs v-model="activeTab" class="custom-tabs" @tab-change="handleTabChange">
            <!-- 商品详情标签 -->
            <el-tab-pane label="商品详情" name="detail">
              <div class="rich-text-content" v-if="product.detail">
                <div v-html="product.detail"></div>
              </div>
              <div v-else class="empty-tab">
                <el-empty description="暂无图文详情" :image-size="100"></el-empty>
              </div>
            </el-tab-pane>
            
            <!-- 配置清单标签 -->
            <el-tab-pane label="配置清单" name="config">
              <el-descriptions :column="1" border>
                <el-descriptions-item label="CPU">{{ product.cpu }}</el-descriptions-item>
                <el-descriptions-item label="显卡">{{ product.graphicsCard }}</el-descriptions-item>
                <el-descriptions-item label="内存">{{ product.memory }}</el-descriptions-item>
                <el-descriptions-item label="硬盘">{{ product.ssd }}</el-descriptions-item>
                <el-descriptions-item label="主板">{{ product.mainboard || '标准配置' }}</el-descriptions-item>
              </el-descriptions>
            </el-tab-pane>
            
            <!-- 商品评价标签 -->
            <el-tab-pane :label="`商品评价 (${commentTotal})`" name="reviews">
              <div class="reviews-container" v-loading="commentLoading">
                <div v-if="commentList.length > 0">
                  <div v-for="item in commentList" :key="item.id" class="review-item">
                    <div class="review-header">
                      <div class="user-info">
                        <el-avatar :size="32" :src="defaultAvatar" class="user-avatar">
                          {{ item.userName ? item.userName.charAt(0).toUpperCase() : 'U' }}
                        </el-avatar>
                        <span class="user-name">{{ item.userName || '匿名用户' }}</span>
                      </div>
                      <div class="review-meta">
                        <span class="review-date">{{ formatDate(item.createTime) }}</span>
                      </div>
                    </div>
                    <div class="review-rating">
                      <el-rate v-model="item.star" disabled text-color="#ff9900" />
                    </div>
                    <div class="review-content">
                      {{ item.content }}
                    </div>
                  </div>
                  
                  <!-- 评价分页 -->
                  <div class="pagination-box">
                    <el-pagination
                      v-show="commentTotal > 0"
                      :total="commentTotal"
                      v-model:current-page="commentParams.pageNum"
                      v-model:page-size="commentParams.pageSize"
                      layout="prev, pager, next"
                      @current-change="getCommentList"
                    />
                  </div>
                </div>
                <el-empty v-else description="暂无评价，快来抢沙发吧！" :image-size="80" />
              </div>
            </el-tab-pane>
          </el-tabs>
        </div>
      </div>
      
      <el-empty v-else description="商品不存在" :image-size="100" />
    </div>
    
    <el-footer class="simple-footer">
      <p>© 2024 电脑租赁智能平台</p>
    </el-footer>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { getFrontProduct } from "@/api/front/product";
import { addToCart as addToCartAPI } from '@/api/shop/cart';
import { listFrontComment } from '@/api/front/comment';
import { ElMessage } from 'element-plus';
import { handleImageUrl } from '@/utils/ruoyi'
import {
  ZoomIn, QuestionFilled, CircleCheckFilled, CircleCloseFilled,
  ShoppingCart, CircleCheck, Van, Refresh
} from '@element-plus/icons-vue';
import Header from '../Header.vue';
// import defaultImg from '@/assets/images/default-product.jpg';

const route = useRoute();
const router = useRouter();

// 状态
const loading = ref(true);
const product = ref(null);
const quantity = ref(1);
const activeTab = ref('detail');
const commentLoading = ref(false);
const commentList = ref([]);
const commentTotal = ref(0);
const averageRating = ref(0);
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png';
const commentParams = ref({
  pageNum: 1,
  pageSize: 10,
  productId: null
});
// const defaultImage = defaultImg;

// 1. 判断是否租赁 (严格模式：只认 '1')
const isRent = computed(() => {
  if (!product.value) return false;
  return product.value.productType === '1';
});

// 2. 动态获取真实库存
const realStock = computed(() => {
  if (!product.value) return 0;
  if (isRent.value) {
    // 租赁模式：看 availableRent (可租赁数量)
    return product.value.stockQuantity || 0;
  } else {
    // 出售模式 (类型 '2')：看 stockQuantity (库存)
    return product.value.stockQuantity || 0;
  }
});

// 获取详情数据
const fetchDetail = async () => {
  const id = route.params.id;
  if (!id) {
    ElMessage.error('参数错误');
    loading.value = false;
    return;
  }

  try {
    loading.value = true;
    const response = await getFrontProduct(id);
    if (response.data) {
      product.value = response.data;
    } else {
      ElMessage.warning('未找到该商品信息');
    }
  } catch (error) {
    console.error('获取详情失败:', error);
    ElMessage.error('获取商品详情失败');
  } finally {
    loading.value = false;
  }
};

// 加入购物车
const addToCart = async () => {
  if (!product.value) return;

  try {
    // 业务类型: 1=租赁, 2=购买
    const businessType = isRent.value ? '1' : '2'; 
    const price = isRent.value ? product.value.rentPrice : product.value.salePrice;
    
    if (!price || price === 0) {
      ElMessage.warning('该商品价格异常，无法加入购物车');
      return;
    }
    
    const cartData = {
      productId: product.value.id,
      productName: product.value.productName,
      productImg: product.value.imageUrl,
      price: price,
      businessType: businessType,
      quantity: quantity.value
    };
    
    await addToCartAPI(cartData);
    ElMessage.success('商品已成功加入购物车');
  } catch (e) {
    console.error('加入购物车失败:', e);
    // 这里如果拦截器已经报了错，可以不重复报
  }
};

// 立即下单
const buyNow = () => {
  if (!product.value) return;

  // 构建商品数据
  const productData = {
    productId: product.value.id,
    productName: product.value.productName,
    productImg: product.value.imageUrl,
    price: isRent.value ? product.value.rentPrice : product.value.salePrice,
    businessType: isRent.value ? '1' : '2',
    quantity: quantity.value,
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

// 获取评价列表
const getCommentList = async () => {
  if (!product.value) return;
  
  try {
    commentLoading.value = true;
    commentParams.value.productId = product.value.id;
    const response = await listFrontComment(commentParams.value);
    
    if (response.rows) {
      commentList.value = response.rows;
      commentTotal.value = response.total;
      
      // 计算平均评分
      if (commentList.value.length > 0) {
        const totalStars = commentList.value.reduce((sum, item) => sum + (item.star || 0), 0);
        averageRating.value = (totalStars / commentList.value.length).toFixed(1);
      } else {
        averageRating.value = 0;
      }
    }
  } catch (error) {
    console.error('获取评价列表失败:', error);
    ElMessage.error('获取评价列表失败');
  } finally {
    commentLoading.value = false;
  }
};

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
};

// 监听标签页切换
const handleTabChange = (tab) => {
  if (tab === 'reviews' && product.value) {
    getCommentList();
  }
};

onMounted(() => {
  fetchDetail();
});

// 监听product变化，当商品数据加载完成后自动获取评价数据
watch(() => product.value, (newProduct) => {
  if (newProduct && newProduct.id) {
    getCommentList();
  }
}, { immediate: false });
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap');

.product-detail-page {
  min-height: 100vh;
  background-color: #ffffff;
  font-family: 'Inter', sans-serif;
}

/* 面包屑 */
.breadcrumb-container {
  max-width: 1200px;
  margin: 20px auto; 
  padding: 0 20px;
}

/* 主容器 */
.main-container {
  max-width: 1200px;
  margin: 0 auto 60px;
  padding: 0 20px;
  min-height: 600px;
}

/* 上半部分布局 */
.product-overview {
  display: flex;
  gap: 48px;
  margin-bottom: 60px;
}

/* 图片区域 */
.gallery-section {
  flex: 0 0 500px; 
  width: 500px;
}

.main-image-box {
  width: 100%;
  height: 500px;
  border-radius: 16px;
  overflow: hidden;
  border: 1px solid #f1f5f9;
  background: #f8fafc;
  display: flex;
  align-items: center;
  justify-content: center;
}

.main-image {
  width: 100%;
  height: 100%;
  cursor: zoom-in;
}

/* 信息区域 */
.info-section {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.product-title {
  font-size: 28px;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 12px;
  line-height: 1.3;
}

.product-subtitle {
  font-size: 15px;
  color: #64748b;
  margin-bottom: 24px;
  line-height: 1.6;
}

/* 价格卡片 */
.price-card {
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 24px;
}

.price-row {
  display: flex;
  align-items: flex-end;
  margin-bottom: 16px;
}

.main-price {
  color: #ef4444; 
  display: flex;
  align-items: baseline;
}

.main-price .label {
  font-size: 14px;
  color: #64748b;
  margin-right: 8px;
  font-weight: normal;
}

.main-price .currency {
  font-size: 18px;
  font-weight: 600;
}

.main-price .amount {
  font-size: 36px;
  font-weight: 800;
  line-height: 1;
  margin: 0 2px;
}

.main-price .unit {
  font-size: 14px;
  color: #64748b;
}

.divider-vertical {
  width: 1px;
  height: 24px;
  background: #cbd5e1;
  margin: 0 24px;
  align-self: center;
}

.deposit-info {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #475569;
}
.deposit-info .value {
  font-weight: 600;
  color: #334155;
}
.info-icon {
  color: #94a3b8;
  cursor: help;
}

.stock-row {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
}
.stock-row .label { color: #64748b; }
.stock-val.in-stock { color: #059669; font-weight: 500; display: flex; align-items: center; gap: 4px;}
.stock-val.out-stock { color: #dc2626; font-weight: 500; display: flex; align-items: center; gap: 4px;}

/* 规格简述 */
.specs-summary {
  margin-bottom: 32px;
  display: flex;
  gap: 32px;
  border-top: 1px solid #f1f5f9;
  border-bottom: 1px solid #f1f5f9;
  padding: 16px 0;
}
.spec-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.spec-label { font-size: 12px; color: #94a3b8; }
.spec-val { font-size: 15px; color: #334155; font-weight: 500; }

/* 操作区 */
.action-area {
  display: flex;
  gap: 24px;
  margin-bottom: 32px;
  align-items: center;
}
.quantity-selector {
  display: flex;
  align-items: center;
  gap: 12px;
}
.qty-label { font-size: 14px; color: #334155; font-weight: 500; }

.btn-group {
  display: flex;
  gap: 16px;
  flex: 1;
}
.action-btn {
  flex: 1;
  height: 50px;
  font-size: 16px;
  border-radius: 8px;
}
.cart-btn {
  border-color: #3b82f6;
  color: #3b82f6;
}
.cart-btn:hover {
  background-color: #eff6ff;
}
.buy-btn {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  border: none;
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.3);
  color: #fff;
}
.buy-btn:hover {
  box-shadow: 0 6px 16px rgba(37, 99, 235, 0.4);
  transform: translateY(-1px);
}
.buy-btn:disabled {
  background: #cbd5e1;
  box-shadow: none;
  cursor: not-allowed;
  transform: none;
}

/* 服务标签 */
.service-tags {
  display: flex;
  gap: 20px;
  font-size: 13px;
  color: #64748b;
}
.service-tags .tag {
  display: flex;
  align-items: center;
  gap: 6px;
}

/* 底部 Tabs */
.product-tabs-section {
  background: #fff;
}

.custom-tabs :deep(.el-tabs__item) {
  font-size: 16px;
  height: 50px;
  line-height: 50px;
  color: #64748b;
}
.custom-tabs :deep(.el-tabs__item.is-active) {
  color: #3b82f6;
  font-weight: 600;
}
.custom-tabs :deep(.el-tabs__active-bar) {
  background-color: #3b82f6;
  height: 3px;
}

.rich-text-content {
  padding: 20px 0;
  line-height: 1.8;
  color: #334155;
  font-size: 15px;
}
.rich-text-content :deep(img) {
  max-width: 100%;
  height: auto;
}
.empty-tab {
  padding: 40px 0;
}

.simple-footer {
  text-align: center;
  border-top: 1px solid #e2e8f0;
  color: #94a3b8;
  font-size: 14px;
  padding: 20px 0;
  margin-top: auto;
}

/* 评价样式 */
.reviews-container {
  padding: 20px 0;
}

.review-item {
  padding: 20px 0;
  border-bottom: 1px solid #f1f5f9;
}

.review-item:last-child {
  border-bottom: none;
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-avatar {
  background-color: #3b82f6;
}

.user-name {
  font-size: 14px;
  color: #334155;
  font-weight: 500;
}

.review-meta {
  font-size: 13px;
  color: #94a3b8;
}

.review-rating {
  margin-bottom: 12px;
}

.review-content {
  font-size: 15px;
  color: #334155;
  line-height: 1.6;
  padding-left: 44px;
}

.pagination-box {
  display: flex;
  justify-content: center;
  margin-top: 30px;
  padding: 20px 0;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.meta-item .label {
  font-size: 14px;
  color: #64748b;
}

/* 响应式 */
@media (max-width: 1024px) {
  .product-overview { flex-direction: column; gap: 30px; }
  .gallery-section { width: 100%; flex: none; }
  .main-image-box { height: 400px; }
}
</style>