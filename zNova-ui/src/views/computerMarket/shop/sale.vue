<template>
  <div class="common-layout">
    <el-container class="app-container">
      <Header />
      
       <el-main class="main-content">
        <!-- 页面标题 -->
        <div class="text-center mb-12 page-header">
          <h1 class="main-title">电脑销售中心</h1>
          <p class="sub-title">全新正品 · 质量保证 · 售后无忧</p>
          <div class="title-decoration"></div>
        </div>

        <!-- 筛选和搜索区域 -->
        <el-card class="filter-card mb-8" :body-style="{ padding: '0' }">
          <div class="filter-container">
            <!-- 左侧：硬件筛选 + 售价范围 + 排序 -->
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
                placeholder="售价范围"
                clearable
                @change="handleFilterChange"
                class="custom-select price-range-select"
              >
                <template #prefix><el-icon><Money /></el-icon></template>
                <el-option label="0-5000元" value="0-5000"></el-option>
                <el-option label="5000-10000元" value="5000-10000"></el-option>
                <el-option label="10000-20000元" value="10000-20000"></el-option>
                <el-option label="20000元以上" value="20000+"></el-option>
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
                <el-option label="优惠力度" value="discount"></el-option>
              </el-select>
            </div>

            <!-- 右侧：搜索框和搜索按钮 -->
            <div class="right-search">
              <div class="search-wrapper">
                <el-input 
                  v-model="searchQuery" 
                  placeholder="搜索想购买的设备..." 
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
                :src="product.imageUrl ? handleImageUrl(product.imageUrl) : handleImageUrl('@/assets/images/default-product.jpg')"
                alt="商品图片"
                class="product-image"
                fit="cover"
                loading="lazy"
              ></el-image>
              
              <!-- 价格标签 (悬浮) -->
              <div class="floating-price">
                <span class="currency">¥</span>
                <span class="amount">{{ product.salePrice }}</span>
              </div>

              <div 
                v-if="product.originalSalePrice && product.originalSalePrice > product.salePrice"
                class="discount-tag"
              >
                特惠
              </div>
            </div>
            
            <div class="product-body">
              <div class="title-section">
                <h3 class="product-name" :title="product.productName">{{ product.productName }}</h3>
                <div class="stock-badge" :class="{ 'low-stock': product.availableSale < 3 }">
                  {{ product.availableSale > 0 ? `余 ${product.availableSale}` : '缺货' }}
                </div>
              </div>

              <!-- 性能条 -->
              <div class="performance-section">
                <div class="perf-label">
                  <el-icon color="#6366f1"><Odometer /></el-icon>
                  <span>综合性能评分</span>
                </div>
                <div class="perf-content">
                  <el-progress 
                    :percentage="product.performanceScore ? Math.min(100, Math.round((product.performanceScore / 28387) * 100)) : 0" 
                    :stroke-width="8"
                    class="performance-bar"
                    :color="getPerformanceColor(product.performanceScore ? Math.min(100, Math.round((product.performanceScore / 28387) * 100)) : 0)"
                    :format="(p) => product.performanceScore || '暂无数据'"
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
              
              <div 
                v-if="product.originalSalePrice && product.originalSalePrice > product.salePrice"
                class="original-price-tag"
              >
                原价: ¥{{ product.originalSalePrice }}
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
                  @click="addToCart(product, 'sale')"
                  :disabled="!product.availableSale"
                >
                  {{ product.availableSale ? '立即购买' : '暂时缺货' }}
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
          <p>© 2024 电脑销售智能平台</p>
          <span class="divider">|</span>
          <p>正品保障 · 专业售后 · 无忧退换</p>
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
import { getPersonalRecommend } from "@/api/front/recommend";
import { listFrontCpu, listFrontGpu } from "@/api/front/hardware";
import {
  Box, ShoppingCart, View, Search, Cpu, Monitor,
  Money, Sort, Odometer, ArrowDown
} from '@element-plus/icons-vue';
import Header from '../Header.vue';
import { addToCart as addToCartAPI } from '@/api/shop/cart';
import { handleImageUrl } from '@/utils/ruoyi';

const router = useRouter();

// 状态管理 - 采用租赁页面的分页逻辑
const products = ref([]);
const filteredProducts = ref([]);
const displayedProducts = ref([]);
const loading = ref(true);
const currentPage = ref(0);
const productsPerPage = ref(12); // 每页显示12个商品（与租赁页面一致）
const searchQuery = ref('');
const sortType = ref('default');

// 硬件筛选状态
const cpuOptions = ref([]);
const gpuOptions = ref([]);
const selectedCPU = ref('');
const selectedGPU = ref('');
const cpuLoading = ref(false);
const gpuLoading = ref(false);

// 销售筛选状态
const priceRange = ref('');

// 计算属性 - 与租赁页面逻辑一致
const hasMoreProducts = computed(() => {
  return displayedProducts.value.length < filteredProducts.value.length;
});

// 性能颜色映射
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

// 格式化内存存储展示
const formatMemoryStorage = (product) => {
  let memoryText = "";
  if (product.memory) {
    const memoryParts = product.memory.split(/\s+/);
    const brand = memoryParts[0];
    const capacity = memoryParts.find(part => part.includes("GB")) || "";
    memoryText = `${brand}${capacity}`;
  }

  let storageText = "";
  if (product.ssd) {
    const storageParts = product.ssd.split(/\s+/);
    const brand = storageParts[0];
    const capacity = storageParts.find(part => part.includes("TB") || part.includes("GB")) || "";
    storageText = `${brand}${capacity}`;
  }

  return `${memoryText} ${storageText}`;
};

const shuffleInPlace = (list) => {
  for (let i = list.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1))
    const temp = list[i]
    list[i] = list[j]
    list[j] = temp
  }
  return list
}

const recommendFirst = (allProducts, recommendedIds = []) => {
  const recommendedIdSet = new Set(recommendedIds)
  const productById = new Map(allProducts.map(p => [p.id, p]))

  const recommended = []
  const added = new Set()
  for (const id of recommendedIds) {
    if (added.has(id)) continue
    const product = productById.get(id)
    if (!product) continue
    recommended.push(product)
    added.add(id)
  }

  const others = allProducts.filter(p => !recommendedIdSet.has(p.id))
  shuffleInPlace(others)
  return [...recommended, ...others]
}
// 获取商品数据
const getProductData = async () => {
  try {
    loading.value = true;
    const response = await listFrontProduct({
      minId: 4,
      hasSale: true,
      pageSize: 1000  // 请求1000条数据，确保拿回所有商品
    });
    
    if (response.rows && response.rows.length > 0) {
      const validProducts = response.rows.filter(product => product.id >= 4);
      let recommendedIds = [];
      try {
        const recommendRes = await getPersonalRecommend(50);
        recommendedIds = (recommendRes.data || []).map(p => p.id).filter(Boolean);
      } catch (e) {
        console.warn('获取推荐商品失败，将随机展示', e);
      }

      const orderedProducts = recommendFirst(validProducts, recommendedIds);
      products.value = orderedProducts.map(product => ({
        ...product,
        availableSale: product.stockQuantity || 0
      }));
      filterAndSortProducts(); // 初始化时执行筛选排序
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

// 获取硬件数据
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

// CPU远程搜索
const remoteCpuMethod = async (query) => {
  if (!query) { await getHardwareData(); return; }
  cpuLoading.value = true;
  try {
    const response = await listFrontCpu({ model: query });
    cpuOptions.value = response.rows;
  } finally { cpuLoading.value = false; }
};

// GPU远程搜索
const remoteGpuMethod = async (query) => {
  if (!query) { await getHardwareData(); return; }
  gpuLoading.value = true;
  try {
    const response = await listFrontGpu({ model: query });
    gpuOptions.value = response.rows;
  } finally { gpuLoading.value = false; }
};

// 筛选和排序商品 - 采用租赁页面的分页初始化逻辑
const filterAndSortProducts = () => {
  // 基础筛选：只显示销售商品
  let result = [...products.value].filter(product => {
    const type = String(product.productType || '').trim();
    return type === '2' || type === '3';
  });
  
  // CPU筛选
  if (selectedCPU.value) {
    result = result.filter(p => p.cpu?.toLowerCase().includes(selectedCPU.value.toLowerCase()));
  }
  
  // GPU筛选
  if (selectedGPU.value) {
    result = result.filter(p => p.graphicsCard?.toLowerCase().includes(selectedGPU.value.toLowerCase()));
  }
  
  // 价格范围筛选
  if (priceRange.value) {
    if (priceRange.value === '20000+') {
      result = result.filter(p => (p.salePrice || 0) >= 20000);
    } else {
      const [min, max] = priceRange.value.split('-').map(Number);
      result = result.filter(p => (p.salePrice || 0) >= min && (p.salePrice || 0) < max);
    }
  }
  
  // 搜索筛选
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase();
    result = result.filter(p => 
      p.productName.toLowerCase().includes(query) ||
      p.cpu.toLowerCase().includes(query) ||
      p.graphicsCard.toLowerCase().includes(query) ||
      (p.features && p.features.some(f => f.toLowerCase().includes(query)))
    );
  }
  
  // 排序
  switch (sortType.value) {
    case 'price-low': 
      result.sort((a, b) => (a.salePrice || 0) - (b.salePrice || 0)); 
      break;
    case 'price-high': 
      result.sort((a, b) => (b.salePrice || 0) - (a.salePrice || 0)); 
      break;
    case 'performance': 
      result.sort((a, b) => (b.performanceScore || 0) - (a.performanceScore || 0)); 
      break;
    case 'discount':
      result.sort((a, b) => {
        const discountA = a.originalSalePrice ? (a.originalSalePrice - a.salePrice) / a.originalSalePrice : 0;
        const discountB = b.originalSalePrice ? (b.originalSalePrice - b.salePrice) / b.originalSalePrice : 0;
        return discountB - discountA;
      });
      break;
    default:
      break;
  }
  
  filteredProducts.value = result;
  currentPage.value = 0; // 重置页码（与租赁页面一致）
  displayedProducts.value = filteredProducts.value.slice(0, productsPerPage.value); // 初始化显示第一页
};

// 加载更多 - 与租赁页面逻辑完全一致
const loadMore = () => {
  currentPage.value++;
  const startIndex = currentPage.value * productsPerPage.value;
  const endIndex = startIndex + productsPerPage.value;
  displayedProducts.value = [...displayedProducts.value, ...filteredProducts.value.slice(startIndex, endIndex)];
};

const handleFilterChange = () => filterAndSortProducts();
const handleSearch = () => filterAndSortProducts();

const viewDetails = (productId) => {
  router.push(`/computer-market/product-detail/${productId}?type=sale`);
};

const addToCart = async (product, type) => {
  try {
    if (!product.salePrice || product.salePrice === 0) {
      ElMessage.warning('该商品暂时不能购买');
      return;
    }
    
    const cartData = {
      productId: product.id,
      productName: product.productName,
      productImg: product.imageUrl,
      price: product.salePrice,
      businessType: '2', // 2=购买
      quantity: 1
    };
    
    await addToCartAPI(cartData);
    ElMessage.success('已加入购物车');
  } catch (error) {
    console.error('加入购物车失败:', error);
    ElMessage.error('操作失败');
  }
};

onMounted(() => {
  getProductData();
  getHardwareData();
});
</script>

<style scoped>
/* 引入字体 */
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap');

.common-layout {
  min-height: 100vh;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
}

.app-container {
  width: 100%;
}

.main-content {
  padding: 40px 24px;
  margin-top: 60px;
  max-width: 1360px;
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
  box-shadow: 0 4px 20px rgba(148, 163, 184, 0.1);
  margin-bottom: 40px;
  overflow: visible;
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

/* 深度定制Element Plus输入框样式 */
.custom-select :deep(.el-input__wrapper),
.custom-search-input :deep(.el-input__wrapper) {
  background-color: #f8fafc;
  border: 1px solid transparent;
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
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.2);
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
  grid-template-columns: repeat(4, 1fr);
  gap: 32px;
}

/* --- 商品卡片 --- */
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
  height: 100%;
}

.product-card:hover {
  transform: translateY(-8px);
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
  flex: 1;
  display: flex;
  flex-direction: column;
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
  flex-shrink: 0;
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

/* 规格Chips */
.specs-chips {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 16px;
  flex-shrink: 0;
  min-height: 72px;
  align-content: flex-start;
  justify-content: flex-start;
}
.spec-chip {
  display: flex;
  align-items: center;
  gap: 6px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  padding: 6px 10px;
  border-radius: 8px;
  font-size: 12px;
  color: #475569;
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

/* 原价标签 */
.original-price-tag {
  font-size: 12px;
  color: #94a3b8;
  text-decoration: line-through;
  margin-bottom: 16px;
}

.card-divider {
  height: 1px;
  background: #f1f5f9;
  margin: 0 -24px 20px;
}

/* 操作按钮组 */
.action-buttons {
  display: flex;
  gap: 12px;
  margin-top: auto;
  padding-top: 16px;
  flex-shrink: 0;
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
  background: #1e293b;
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

/* --- 分页相关样式 - 与租赁页面保持一致 --- */
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

/* 内存硬盘合并标签样式 */
.memory-storage-chip {
  display: flex;
  align-items: center;
  gap: 12px;
}

.memory-storage-chip span {
  white-space: nowrap;
  font-size: 12px;
  color: #334155;
}

/* 响应式 */
@media (max-width: 1200px) {
  .product-grid {
    grid-template-columns: repeat(3, 1fr);
  }
  .filter-container { flex-direction: column; align-items: stretch; }
  .left-filters { width: 100%; }
  .right-search { width: 100%; margin-left: 0; }
  .custom-select, .hardware-select, .price-range-select, .sort-select { flex: 1; min-width: 140px; }
}

@media (max-width: 992px) {
  .product-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 640px) {
  .product-grid {
    grid-template-columns: 1fr;
  }
  .main-title { font-size: 28px; }
}
</style>