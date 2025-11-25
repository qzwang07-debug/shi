<template>
  <div class="common-layout">
    <el-container class="app-container">
      <Header />
      
       <el-main class="main-content">
        <!-- 页面标题 -->
        <div class="text-center mb-12 page-header">
          <h1 class="main-title">电脑租赁服务</h1>
          <p class="sub-title">高性能设备 · 极速租赁 · 智能评估</p>
          <div class="title-decoration"></div>
        </div>

        <!-- 筛选和搜索区域 -->
        <el-card class="filter-card mb-8" :body-style="{ padding: '0' }">
          <div class="filter-container">
            <!-- 左侧：硬件筛选 + 租金范围 + 排序 -->
            <div class="left-filters">
              <el-select 
                v-model="selectedCPU" 
                placeholder="CPU型号"
                filterable
                clearable
                @change="handleFilterChange"
                class="custom-select hardware-select"
                :loading="cpuLoading"
                remote
                :remote-method="remoteCpuMethod"
                effect="light"
              >
                <template #prefix><el-icon><Cpu /></el-icon></template>
                <el-option
                  v-for="cpu in cpuOptions"
                  :key="cpu.id"
                  :label="`${cpu.brand} ${cpu.model}`"
                  :value="cpu.model"
                >
                  <div class="option-item">
                    <span class="brand-tag" :class="cpu.brand.toLowerCase()">{{ cpu.brand }}</span>
                    <span class="model-text">{{ cpu.model }}</span>
                  </div>
                </el-option>
              </el-select>

              <el-select 
                v-model="selectedGPU" 
                placeholder="显卡型号"
                filterable
                clearable
                @change="handleFilterChange"
                class="custom-select hardware-select"
                :loading="gpuLoading"
                remote
                :remote-method="remoteGpuMethod"
              >
                <template #prefix><el-icon><Monitor /></el-icon></template>
                <el-option
                  v-for="gpu in gpuOptions"
                  :key="gpu.id"
                  :label="`${gpu.brand} ${gpu.model}`"
                  :value="gpu.model"
                >
                   <div class="option-item">
                    <span class="brand-tag" :class="gpu.brand.toLowerCase()">{{ gpu.brand }}</span>
                    <span class="model-text">{{ gpu.model }}</span>
                  </div>
                </el-option>
              </el-select>

              <el-select 
                v-model="priceRange" 
                placeholder="租金范围"
                clearable
                @change="handleFilterChange"
                class="custom-select price-range-select"
              >
                <template #prefix><el-icon><Money /></el-icon></template>
                <el-option label="0-25元/天" value="0-25"></el-option>
                <el-option label="25-50元/天" value="25-50"></el-option>
                <el-option label="50-100元/天" value="50-100"></el-option>
              </el-select>

              <el-select 
                v-model="sortType" 
                placeholder="排序方式"
                @change="handleFilterChange"
                class="custom-select sort-select"
              >
                <template #prefix><el-icon><Sort /></el-icon></template>
                <el-option label="默认排序" value="default"></el-option>
                <el-option label="价格从低到高" value="price-low"></el-option>
                <el-option label="价格从高到低" value="price-high"></el-option>
                <el-option label="性能排序" value="performance"></el-option>
              </el-select>
            </div>

            <!-- 右侧：搜索框和搜索按钮 -->
            <div class="right-search">
              <div class="search-wrapper">
                <el-input 
                  v-model="searchQuery" 
                  placeholder="搜索想租赁的设备..." 
                  class="custom-search-input"
                  clearable
                  @keyup.enter="handleSearch"
                >
                  <template #prefix>
                    <el-icon class="search-icon"><Search /></el-icon>
                  </template>
                </el-input>
                <el-button 
                  type="primary" 
                  class="search-button"
                  @click="handleSearch"
                >
                  搜索
                </el-button>
              </div>
            </div>
          </div>
        </el-card>

        <!-- 商品网格 -->
        <div v-if="loading" class="loading-container">
          <el-skeleton v-for="i in 6" :key="i" animated class="skeleton-card">
            <template #template>
              <el-skeleton-item variant="image" style="width: 100%; height: 220px" />
              <div style="padding: 14px">
                <el-skeleton-item variant="h1" style="width: 50%" />
                <el-skeleton-item variant="text" style="margin-top: 16px" />
                <el-skeleton-item variant="text" style="width: 30%" />
              </div>
            </template>
          </el-skeleton>
        </div>
        
        <div v-else-if="filteredProducts.length === 0" class="empty-container">
          <div class="empty-wrapper">
            <el-empty description="暂无符合条件的商品" :image-size="220"></el-empty>
            <el-button @click="clearFilters" plain round>清空筛选条件</el-button>
          </div>
        </div>
        
        <div v-else class="product-grid">
          <el-card 
            v-for="product in displayedProducts" 
            :key="product.id"
            class="product-card"
            :body-style="{ padding: '0' }"
          >
            <div class="card-image-wrapper">
              <div class="image-overlay"></div>
              <el-image 
                :src="product.imageUrl ? '/dev-api' + product.imageUrl : '@/assets/images/default-product.jpg'" 
                alt="商品图片"
                class="product-image"
                fit="cover"
                loading="lazy"
              ></el-image>
              
              <!-- 价格标签 (悬浮) -->
              <div class="floating-price">
                <span class="currency">¥</span>
                <span class="amount">{{ product.rentPrice }}</span>
                <span class="unit">/天</span>
              </div>

              <div 
                v-if="product.originalRentPrice && product.originalRentPrice > product.rentPrice"
                class="discount-tag"
              >
                特惠
              </div>
            </div>
            
            <div class="product-body">
              <div class="title-section">
                <h3 class="product-name" :title="product.productName">{{ product.productName }}</h3>
                <div class="stock-badge" :class="{ 'low-stock': product.availableRent < 3 }">
                  {{ product.availableRent > 0 ? `余 ${product.availableRent}` : '缺货' }}
                </div>
              </div>

              <!-- 性能条 -->
              <div class="performance-section">
                <div class="perf-label">
                  <el-icon color="#6366f1"><Odometer /></el-icon>
                  <span>性能评分</span>
                </div>
                <div class="perf-content">
                  <el-progress 
                    :percentage="product.performanceScore.percentage" 
                    :stroke-width="8"
                    class="performance-bar"
                    :color="getPerformanceColor(product.performanceScore.percentage)"
                    :format="(p) => product.performanceScore.score"
                  ></el-progress>
                </div>
              </div>
              
             <!-- 参数标签化展示 -->
              <div class="specs-chips">
                <div class="spec-chip" title="处理器">
                  <el-icon><Cpu /></el-icon>
                  <span>{{ product.cpu }}</span>
                </div>
                <div class="spec-chip" title="显卡">
                  <el-icon><Monitor /></el-icon>
                  <span>{{ product.graphicsCard }}</span>
                </div>
                <!-- 合并内存和硬盘到同一行 -->
                <div class="spec-chip memory-storage-chip" title="内存/存储">
                  <span>{{ formatMemoryStorage(product) }}</span>
                </div>
              </div>
              <div class="price-section"> 
              </div>
              <div class="card-divider"></div>
              
              <div class="action-buttons">
                <el-button 
                  class="action-btn detail-btn"
                  icon="View"
                  @click="viewDetails(product.id)"
                  circle
                  plain
                ></el-button>
                <el-button 
                  class="action-btn rent-btn"
                  icon="ShoppingCart"
                  @click="addToCart(product, 'rent')"
                  :disabled="!product.availableRent"
                >
                  {{ product.availableRent ? '立即租赁' : '暂时缺货' }}
                </el-button>
              </div>
            </div>
          </el-card>
        </div>

        <!-- 加载更多按钮 -->
        <div v-if="hasMoreProducts" class="pagination-wrapper">
          <el-button 
            class="load-more-btn"
            @click="loadMore"
            :loading="loading"
          >
            探索更多设备
            <el-icon class="el-icon--right"><ArrowDown /></el-icon>
          </el-button>
        </div>
      </el-main>
      
      <!-- 页脚 -->
      <el-footer class="footer-content">
        <div class="footer-inner">
          <p>© 2024 电脑租赁智能平台</p>
          <span class="divider">|</span>
          <p>极速响应 · 信用免押 · 随租随还</p>
        </div>
      </el-footer>
    </el-container>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { listFrontProduct } from "@/api/front/product";
import { listFrontCpu, listFrontGpu } from "@/api/front/hardware";
import {
  Box, ShoppingCart, View, Search, Cpu, Monitor,
  Money, Sort, Odometer, Connection, Files, ArrowDown
} from '@element-plus/icons-vue';
import Header from '../Header.vue';
import { addToCart as addToCartAPI } from '@/api/shop/cart';

const router = useRouter();

// 状态管理
const products = ref([]);
const filteredProducts = ref([]);
const displayedProducts = ref([]);
const loading = ref(true);
const currentPage = ref(0);
const productsPerPage = ref(12); // 增加每页显示数量
const searchQuery = ref('');
const sortType = ref('default');

// 筛选状态
const cpuOptions = ref([]);
const gpuOptions = ref([]);
const selectedCPU = ref('');
const selectedGPU = ref('');
const priceRange = ref('');
const cpuLoading = ref(false);
const gpuLoading = ref(false);

// 计算属性
const hasMoreProducts = computed(() => {
  return displayedProducts.value.length < filteredProducts.value.length;
});

// 性能颜色映射 (调整为更清新的颜色)
const getPerformanceColor = (percentage) => {
  if (percentage >= 85) return '#8b5cf6'; // 紫色
  if (percentage >= 70) return '#3b82f6'; // 蓝色
  if (percentage >= 50) return '#10b981'; // 绿色
  return '#f59e0b'; // 橙色
};

// 清空筛选
const clearFilters = () => {
  selectedCPU.value = '';
  selectedGPU.value = '';
  priceRange.value = '';
  searchQuery.value = '';
  sortType.value = 'default';
  filterAndSortProducts();
};
const formatMemoryStorage = (product) => {
  // 处理内存：提取品牌和容量（根据实际数据格式调整）
  let memoryText = "";
  if (product.memory) {
    // 假设内存格式如 "宏碁 DDR5 32GB 3600MHz" 或 "金士顿 16GB DDR4"
    const memoryParts = product.memory.split(/\s+/);
    // 取第一个词作为品牌，找到包含 GB 的部分作为容量
    const brand = memoryParts[0];
    const capacity = memoryParts.find(part => part.includes("GB")) || "";
    memoryText = `${brand}${capacity}`;
  }

  // 处理硬盘：提取品牌和容量（根据实际数据格式调整）
  let storageText = "";
  if (product.ssd) {
    // 假设硬盘格式如 "西数 SN850X 1TB" 或 "三星 980PRO 512GB"
    const storageParts = product.ssd.split(/\s+/);
    const brand = storageParts[0];
    const capacity = storageParts.find(part => part.includes("TB") || part.includes("GB")) || "";
    storageText = `${brand}${capacity}`;
  }

  return `${memoryText} ${storageText}`;
};
// 计算性能评分（综合性能）
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
    memoryScore = product.memoryFrequency * 2;
  } else if (product.memoryType === 'DDR5') {
    memoryScore = product.memoryFrequency;
  }

  const totalScore = cpuBaseScore + memoryScore + gpuScore;
  const maxScore = 32387;
  const percentage = Math.min(100, Math.round((totalScore / maxScore) * 100));

  return {
    score: totalScore,
    percentage
  };
};

const getProductData = async () => {
  try {
    loading.value = true;
    const response = await listFrontProduct({
      minId: 4,
      hasRent: true,
      pageSize: 1000  // 请求1000条数据，确保拿回所有商品
    });
    
    if (response.rows && response.rows.length > 0) {
      const validProducts = response.rows.filter(product => product.id >= 4);
      products.value = validProducts.map(product => ({
        ...product,
        performanceScore: calculateScore(product)
      }));
      filterAndSortProducts();
    } else {
      products.value = [];
      filteredProducts.value = [];
    }
  } catch (error) {
    console.error('获取商品数据失败:', error);
    ElMessage.error('数据加载异常，请稍后重试');
  } finally {
    loading.value = false;
  }
};

const getHardwareData = async () => {
  try {
    cpuLoading.value = true;
    gpuLoading.value = true;
    const [cpuRes, gpuRes] = await Promise.all([
      listFrontCpu({ pageSize: 100 }),
      listFrontGpu({ pageSize: 100 })
    ]);
    cpuOptions.value = cpuRes.rows || [];
    gpuOptions.value = gpuRes.rows || [];
  } catch (error) {
    console.error('硬件数据失败:', error);
  } finally {
    cpuLoading.value = false;
    gpuLoading.value = false;
  }
};

const remoteCpuMethod = async (query) => {
  if (!query) { await getHardwareData(); return; }
  cpuLoading.value = true;
  try {
    const response = await listFrontCpu({ model: query });
    cpuOptions.value = response.rows;
  } finally { cpuLoading.value = false; }
};

const remoteGpuMethod = async (query) => {
  if (!query) { await getHardwareData(); return; }
  gpuLoading.value = true;
  try {
    const response = await listFrontGpu({ model: query });
    gpuOptions.value = response.rows;
  } finally { gpuLoading.value = false; }
};

const filterAndSortProducts = () => {
  let result = [...products.value].filter(product => {
    const type = String(product.productType || '').trim();
    return type === '1' || type === '3';
  });
  
  if (selectedCPU.value) {
    result = result.filter(p => p.cpu?.toLowerCase().includes(selectedCPU.value.toLowerCase()));
  }
  if (selectedGPU.value) {
    result = result.filter(p => p.graphicsCard?.toLowerCase().includes(selectedGPU.value.toLowerCase()));
  }
  if (priceRange.value) {
    const [min, max] = priceRange.value.split('-').map(Number);
    result = result.filter(p => (p.rentPrice || 0) >= min && (p.rentPrice || 0) < max);
  }
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase();
    result = result.filter(p => 
      p.productName.toLowerCase().includes(query) ||
      p.cpu.toLowerCase().includes(query) ||
      p.graphicsCard.toLowerCase().includes(query)
    );
  }
  
  switch (sortType.value) {
    case 'price-low': result.sort((a, b) => (a.rentPrice || 0) - (b.rentPrice || 0)); break;
    case 'price-high': result.sort((a, b) => (b.rentPrice || 0) - (a.rentPrice || 0)); break;
    case 'performance': result.sort((a, b) => b.performanceScore.percentage - a.performanceScore.percentage); break;
    default: result.sort((a, b) => a.id - b.id);
  }
  
  filteredProducts.value = result;
  currentPage.value = 0;
  displayedProducts.value = filteredProducts.value.slice(0, productsPerPage.value);
};

const handleFilterChange = () => filterAndSortProducts();
const handleSearch = () => filterAndSortProducts();

const loadMore = () => {
  currentPage.value++;
  const startIndex = currentPage.value * productsPerPage.value;
  const endIndex = startIndex + productsPerPage.value;
  displayedProducts.value = [...displayedProducts.value, ...filteredProducts.value.slice(startIndex, endIndex)];
};

const viewDetails = (productId) => router.push(`/computer-market/product-detail/${productId}`);

const addToCart = async (product, type) => {
  try {
    const cartData = {
      productId: product.id,
      productName: product.productName,
      productImg: product.imageUrl,
      price: product.rentPrice,
      businessType: '1', // 1=租赁
      quantity: 1
    };
    
    await addToCartAPI(cartData);
    ElMessage.success({ message: '已加入租赁清单', type: 'success', plain: true });
  } catch (error) {
    console.error('加入租赁清单失败:', error);
    ElMessage.error('操作失败');
  }
};

onMounted(() => {
  getProductData();
  getHardwareData();
});
</script>

<style scoped>
/* 引入字体，建议在全局 index.html 中也引入 Inter 或类似无衬线字体 */
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap');

.common-layout {
  min-height: 100vh;
  /* 极浅的灰蓝渐变背景，清新护眼 */
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
}

.app-container {
  width: 100%;
}

.main-content {
  padding: 40px 24px;
  margin-top: 60px;
  max-width: 1360px; /* 略微收窄最大宽度，视觉更聚拢 */
  margin-left: auto;
  margin-right: auto;
}

/* --- 页面头部 --- */
.page-header {
  margin-bottom: 48px;
  position: relative;
}

.main-title {
  font-size: 36px;
  color: #1e293b;
  font-weight: 800;
  margin-bottom: 12px;
  letter-spacing: -0.5px;
}

.sub-title {
  color: #64748b;
  font-size: 16px;
  font-weight: 400;
}

.title-decoration {
  width: 60px;
  height: 4px;
  background: #3b82f6;
  margin: 20px auto 0;
  border-radius: 2px;
  opacity: 0.8;
}

/* --- 筛选区域 --- */
.filter-card {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(12px);
  border: 1px solid #fff;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(148, 163, 184, 0.1); /* 极淡的阴影 */
  margin-bottom: 40px;
  overflow: visible; /* 允许下拉框溢出 */
}

.filter-container {
  padding: 20px 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
  flex-wrap: wrap;
}

.left-filters {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  flex: 1;
}

.custom-select {
  width: 160px;
}

.hardware-select {
  width: 180px;
}

/* 深度定制 Element Plus 输入框样式 - 扁平化 */
.custom-select :deep(.el-input__wrapper),
.custom-search-input :deep(.el-input__wrapper) {
  background-color: #f8fafc;
  border: 1px solid transparent; /* 默认无边框 */
  box-shadow: none;
  border-radius: 8px;
  padding: 4px 12px;
  transition: all 0.3s ease;
}

.custom-select :deep(.el-input__wrapper):hover,
.custom-search-input :deep(.el-input__wrapper):hover {
  background-color: #f1f5f9;
}

.custom-select :deep(.el-input__wrapper.is-focus),
.custom-search-input :deep(.el-input__wrapper.is-focus) {
  background-color: #fff;
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.2); /* 聚焦时光晕 */
  border-color: #3b82f6;
}

/* 下拉选项美化 */
.option-item {
  display: flex;
  align-items: center;
  gap: 8px;
}
.brand-tag {
  font-size: 10px;
  padding: 2px 6px;
  border-radius: 4px;
  color: #fff;
  font-weight: bold;
}
.brand-tag.intel { background: #0068b5; }
.brand-tag.amd { background: #ed1c24; }
.brand-tag.nvidia { background: #76b900; }
.model-text { color: #334155; }

/* 右侧搜索 */
.right-search {
  width: 320px;
}

.search-wrapper {
  display: flex;
  gap: 10px;
}

.custom-search-input {
  flex: 1;
}

.search-button {
  border-radius: 8px;
  padding: 0 20px;
  background-color: #3b82f6;
  border: none;
  font-weight: 500;
  transition: all 0.3s;
}

.search-button:hover {
  background-color: #2563eb;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.3);
}

/* --- 商品网格 --- */
.product-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr); /* 固定一行4列 */
  gap: 32px;
}

/* --- 商品卡片 (核心样式) --- */
.product-card {
  border: none;
  border-radius: 20px;
  background: #fff;
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  position: relative;
  overflow: hidden;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.03);
  display: flex;
  flex-direction: column;
  height: 100%; /* 【新增】确保卡片高度占满网格高度 */
}

.product-card:hover {
  transform: translateY(-8px);
  /* 悬停阴影加深 */
  box-shadow: 0 20px 40px -10px rgba(0, 0, 0, 0.1);
}

.card-image-wrapper {
  position: relative;
  height: 220px;
  overflow: hidden;
  background: #f8fafc;
}

.product-image {
  width: 100%;
  height: 100%;
  transition: transform 0.6s ease;
}

.product-card:hover .product-image {
  transform: scale(1.08);
}

/* 悬浮价格标签 */
.floating-price {
  position: absolute;
  bottom: 16px;
  right: 16px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(8px);
  padding: 6px 16px;
  border-radius: 30px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
  display: flex;
  align-items: baseline;
  z-index: 2;
  color: #0f172a;
}

.currency { font-size: 14px; font-weight: 600; margin-right: 2px; }
.amount { font-size: 22px; font-weight: 800; color: #2563eb; letter-spacing: -0.5px; }
.unit { font-size: 12px; color: #64748b; margin-left: 2px; }

.discount-tag {
  position: absolute;
  top: 16px;
  left: 16px;
  background: #ef4444;
  color: white;
  font-size: 12px;
  font-weight: 700;
  padding: 4px 10px;
  border-radius: 6px;
  box-shadow: 0 2px 8px rgba(239, 68, 68, 0.4);
}

/* 商品主体内容 */
.product-body {
  padding: 24px;
  flex: 1; /* 占满剩余空间 */
  display: flex;
  flex-direction: column; /* 垂直排列 */
  /* 确保内容区不会被压缩 */
  min-height: 0;
}

.title-section {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
  gap: 10px;
}

.product-name {
  font-size: 18px;
  font-weight: 700;
  color: #1e293b;
  line-height: 1.4;
  margin: 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.stock-badge {
  font-size: 12px;
  background: #ecfdf5;
  color: #059669;
  padding: 2px 8px;
  border-radius: 4px;
  white-space: nowrap;
  font-weight: 600;
}
.stock-badge.low-stock {
  background: #fef2f2;
  color: #dc2626;
}

/* 性能部分 */
.performance-section {
  margin-bottom: 20px;
  flex-shrink: 0; /* 不压缩 */
}
.perf-label {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #64748b;
  margin-bottom: 6px;
}
.performance-bar :deep(.el-progress-bar__outer) {
  background-color: #f1f5f9;
  border-radius: 10px;
}
.performance-bar :deep(.el-progress-bar__inner) {
  border-radius: 10px;
  transition: width 1s ease;
}

/* 规格 Chips */
.specs-chips {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 16px;
  flex-shrink: 0;
  align-items: flex-start; /* 防止单行时被拉伸 */
  min-height: 72px; /* 核心修改：预留约两行标签的高度，强行对齐 */
  align-content: flex-start; /* 确保内容从上往下排 */
  justify-content: flex-start; /* 确保内容从左往右排 */
}
.spec-chip {
  display: flex;
  align-items: center;
  gap: 6px;
  background: #f8fafc; /* 统一浅灰背景 */
  border: 1px solid #e2e8f0; /* 统一边框色 */
  padding: 6px 10px;
  border-radius: 8px;
  font-size: 12px;
  color: #475569; /* 统一文字色 */
  transition: all 0.2s;
}

.spec-chip:hover {
  background: #fff;
  border-color: #cbd5e1;
  transform: translateY(-1px);
}

.spec-chip .el-icon {
  color: #94a3b8;
}

.card-divider {
  height: 1px;
  background: #f1f5f9;
  margin: 0 -24px 20px; /* 负边距拉通分割线 */
}

/* 操作按钮组 */
.action-buttons {
  display: flex;
  gap: 12px;
  margin-top: auto; /* 自动推到最底部 */
  padding-top: 16px; /* 与上方内容保持距离 */
  flex-shrink: 0; /* 防止按钮被压缩 */
}
.action-btn {
  height: 44px;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 600;
}

.detail-btn {
  width: 44px;
  padding: 0;
  border-color: #e2e8f0;
  color: #64748b;
}

.detail-btn:hover {
  background-color: #f8fafc;
  color: #3b82f6;
  border-color: #bfdbfe;
}

.rent-btn {
  flex: 1;
  background: #1e293b; /* 深色按钮显高级 */
  border: none;
  color: #fff;
  box-shadow: 0 4px 12px rgba(30, 41, 59, 0.2);
  transition: all 0.3s;
}

.rent-btn:hover {
  background: #334155;
  box-shadow: 0 6px 16px rgba(30, 41, 59, 0.3);
  transform: translateY(-1px);
}

.rent-btn.is-disabled {
  background: #e2e8f0;
  color: #94a3b8;
  box-shadow: none;
}

/* --- 其他 --- */
.pagination-wrapper {
  text-align: center;
  margin-top: 60px;
}

.load-more-btn {
  background: white;
  border: 1px solid #e2e8f0;
  color: #475569;
  padding: 12px 32px;
  height: auto;
  border-radius: 30px;
  font-size: 15px;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.load-more-btn:hover {
  color: #3b82f6;
  border-color: #3b82f6;
  background: #eff6ff;
}

.empty-container {
  padding: 60px 0;
  display: flex;
  justify-content: center;
}
.empty-wrapper {
  text-align: center;
}

.footer-content {
  margin-top: 80px;
  border-top: 1px solid #e2e8f0;
  background: #fff;
}

.footer-inner {
  max-width: 1200px;
  margin: 0 auto;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  color: #94a3b8;
  font-size: 14px;
}

.divider { margin: 0 16px; color: #e2e8f0; }

/* 响应式 */
@media (max-width: 1200px) {
  .product-grid {
    grid-template-columns: repeat(3, 1fr); /* 中等屏幕一行3列 */
  }
  .filter-container { flex-direction: column; align-items: stretch; }
  .left-filters { width: 100%; }
  .right-search { width: 100%; margin-left: 0; }
  .custom-select, .hardware-select, .price-range-select, .sort-select { flex: 1; min-width: 140px; }
}

@media (max-width: 992px) {
  .product-grid {
    grid-template-columns: repeat(2, 1fr); /* 小屏幕一行2列 */
  }
}

@media (max-width: 640px) {
  .product-grid {
    grid-template-columns: 1fr; /* 手机屏幕一行1列 */
  }
  .main-title { font-size: 28px; }
}
/* 内存硬盘合并标签样式 */
.memory-storage-chip {
  display: flex;
  align-items: center;
  gap: 12px; /* 内存和硬盘之间的间距 */
  /* 移除特殊背景和边框色，继承通用样式 */
}

/* 调整标签整体布局，避免换行 */
.specs-chips {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 24px;
  align-items: center;
}

/* 确保合并后的标签不会过长 */
.memory-storage-chip span {
  white-space: nowrap;
  font-size: 12px;
  color: #334155;
}
</style>