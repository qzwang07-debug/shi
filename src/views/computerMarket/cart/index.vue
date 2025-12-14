<template>
  <div class="cart-page">
    <Header />

    <div class="main-container">
      <!-- 页面标题 -->
      <div class="cart-header">
        <h1 class="title">我的购物清单</h1>
        <span class="count">共 {{ cartList.length }} 件商品</span>
      </div>

      <!-- 加载状态 -->
      <div v-if="loading" class="loading-box">
        <el-skeleton :rows="5" animated />
      </div>

      <!-- 空状态 -->
      <div v-else-if="cartList.length === 0" class="empty-cart">
        <el-empty description="购物车空空如也" :image-size="200">
          <el-button type="primary" @click="$router.push('/computer-market')" round>去逛逛</el-button>
        </el-empty>
      </div>

      <!-- 购物车列表 -->
      <div v-else class="cart-list-wrapper">
        <!-- 表头 -->
        <div class="cart-table-header">
          <div class="col-check">
            <el-checkbox 
              v-model="isAllSelected" 
              :indeterminate="isIndeterminate"
              @change="handleSelectAll"
            >全选</el-checkbox>
          </div>
          <div class="col-info">商品信息</div>
          <div class="col-type">类型</div>
          <div class="col-price">单价</div>
          <div class="col-qty">数量</div>
          <div class="col-subtotal">小计</div>
          <div class="col-action">操作</div>
        </div>

        <!-- 商品列表项 -->
        <div 
          v-for="item in cartList" 
          :key="item.cartId" 
          class="cart-item"
          :class="{ 'is-selected': item.isChecked === '1' }"
        >
          <div class="col-check">
            <el-checkbox 
              v-model="item.isChecked" 
              true-label="1" 
              false-label="0"
              @change="(val) => handleCheckChange(item, val)"
            />
          </div>
          
          <div class="col-info product-info">
            <el-image 
              :src="item.productImg ? handleImageUrl(item.productImg) : defaultImg" 
              class="product-img"
              fit="cover"
            />
            <div class="info-text">
              <h3 class="name" @click="$router.push(`/computer-market/product-detail/${item.productId}`)">
                {{ item.productName }}
              </h3>
              <p class="specs">标准配置</p>
            </div>
          </div>

          <div class="col-type">
            <el-tag :type="item.businessType === '1' ? '' : 'warning'" effect="plain" round>
              {{ item.businessType === '1' ? '租赁' : '购买' }}
            </el-tag>
          </div>

          <div class="col-price">
            <span class="currency">¥</span>{{ item.price }}
            <span v-if="item.businessType === '1'" class="unit">/天</span>
          </div>

          <div class="col-qty">
            <el-input-number 
              v-model="item.quantity" 
              :min="1" 
              :max="99"
              size="small"
              @change="(val) => handleQuantityChange(item, val)"
            />
          </div>

          <div class="col-subtotal">
            <span class="amount">¥{{ (item.price * item.quantity).toFixed(2) }}</span>
          </div>

          <div class="col-action">
            <el-button 
              type="danger" 
              link 
              icon="Delete"
              @click="handleDelete(item.cartId)"
            >删除</el-button>
          </div>
        </div>
      </div>

      <!-- 底部结算栏 (Sticky) -->
      <div v-if="cartList.length > 0" class="cart-footer">
        <div class="footer-left">
          <el-checkbox 
            v-model="isAllSelected" 
            :indeterminate="isIndeterminate"
            @change="handleSelectAll"
          >全选</el-checkbox>
          <el-button link @click="handleBatchDelete" :disabled="selectedIds.length === 0" style="margin-left: 20px">
            删除选中的商品
          </el-button>
        </div>
        
        <div class="footer-right">
          <div class="total-info">
            <div class="total-row" v-if="totalStats.rentTotal > 0">
              <span class="label">租赁日租:</span>
              <span class="price rent-price">¥{{ totalStats.rentTotal.toFixed(2) }}<span class="unit">/天</span></span>
            </div>
            <div class="total-row" v-if="totalStats.saleTotal > 0">
              <span class="label">购买总额:</span>
              <span class="price sale-price">¥{{ totalStats.saleTotal.toFixed(2) }}</span>
            </div>
            <div class="total-row selected-count">
              已选 <b>{{ totalStats.count }}</b> 件
            </div>
          </div>
          
          <el-button 
            type="primary" 
            class="checkout-btn" 
            :disabled="totalStats.count === 0"
            @click="handleCheckout"
          >
            去结算
          </el-button>
        </div>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { listCart, updateCart, delCart } from '@/api/shop/cart';
import Header from '../Header.vue';
import { Delete } from '@element-plus/icons-vue';
import { getAppToken } from '@/utils/auth';
import { handleImageUrl } from '@/utils/ruoyi'
const router = useRouter();
const route = useRoute();
const baseUrl = import.meta.env.VITE_APP_BASE_API;
const defaultImg = new URL('@/assets/images/default-product.jpg', import.meta.url).href;

const loading = ref(true);
const cartList = ref([]);

// 计算属性：是否全选
const isAllSelected = computed({
  get: () => cartList.value.length > 0 && cartList.value.every(item => item.isChecked === '1'),
  set: (val) => handleSelectAll(val)
});

// 计算属性：全选框的半选状态
const isIndeterminate = computed(() => {
  const selectedCount = cartList.value.filter(item => item.isChecked === '1').length;
  return selectedCount > 0 && selectedCount < cartList.value.length;
});

// 计算属性：选中的ID集合
const selectedIds = computed(() => {
  return cartList.value
    .filter(item => item.isChecked === '1')
    .map(item => item.cartId);
});

// 计算属性：合计金额（分开计算租赁和购买）
const totalStats = computed(() => {
  let rentTotal = 0;
  let saleTotal = 0;
  let count = 0;

  cartList.value.forEach(item => {
    if (item.isChecked === '1') {
      count++;
      const subtotal = item.price * item.quantity;
      if (item.businessType === '1') {
        rentTotal += subtotal;
      } else {
        saleTotal += subtotal;
      }
    }
  });

  return { rentTotal, saleTotal, count };
});

// 检查登录状态
const checkLogin = () => {
  const token = getAppToken();
  if (!token) {
    ElMessage.error("请先登录");
    router.push('/portal/login?redirect=' + encodeURIComponent(route.fullPath));
    return false;
  }
  return true;
};

// 初始化
const initData = async () => {
  if (!checkLogin()) return;
  
  loading.value = true;
  try {
    const res = await listCart();
    cartList.value = res.data || [];
  } catch (error) {
    console.error(error);
    ElMessage.error('购物车加载失败');
    if (error && error.toString().includes('认证失败')) {
      ElMessage.error("登录状态已过期，请重新登录");
      router.push('/portal/login?redirect=' + encodeURIComponent(route.fullPath));
    }
  } finally {
    loading.value = false;
  }
};

// 修改数量
const handleQuantityChange = async (item, val) => {
  if (!checkLogin()) return;
  
  try {
    await updateCart({
      cartId: item.cartId,
      quantity: val,
      // 保持当前的选中状态
      isChecked: item.isChecked
    });
  } catch (error) {
    // 如果失败，回退（简化处理，实际可刷新列表）
    initData();
    if (error && error.toString().includes('认证失败')) {
      ElMessage.error("登录状态已过期，请重新登录");
      router.push('/portal/login?redirect=' + encodeURIComponent(route.fullPath));
    }
  }
};

// 修改选中状态
const handleCheckChange = async (item, val) => {
  if (!checkLogin()) return;
  
  try {
    await updateCart({
      cartId: item.cartId,
      isChecked: val
      // quantity 不传后端会保持原样
    });
  } catch (error) {
    item.isChecked = val === '1' ? '0' : '1'; // 回退UI
    ElMessage.error('操作失败');
    if (error && error.toString().includes('认证失败')) {
      ElMessage.error("登录状态已过期，请重新登录");
      router.push('/portal/login?redirect=' + encodeURIComponent(route.fullPath));
    }
  }
};

// 全选/取消全选
const handleSelectAll = async (val) => {
  const checkedStr = val ? '1' : '0';
  
  // 前端先变 UI
  cartList.value.forEach(item => item.isChecked = checkedStr);
  
  // 这里如果不希望循环调用接口，后端应该提供一个 batchUpdate 接口
  // 毕设简化处理：循环调用更新，或者只更新当前页面的UI，等点击结算或离开时再同步？
  // 最稳妥的毕设方案：前端循环调用 updateCart (并发可能较多)，或者忽略这里的 API 调用，
  // 仅在“去结算”时把选中的 IDs 传给订单页。
  
  // 方案二（推荐）：循环调用，虽然性能一般但逻辑最简单
  const promises = cartList.value.map(item => updateCart({
    cartId: item.cartId,
    isChecked: checkedStr
  }));
  
  try {
    await Promise.all(promises);
  } catch (e) {
    console.error(e);
  }
};

// 删除单个
const handleDelete = (cartId) => {
  if (!checkLogin()) return;
  
  ElMessageBox.confirm('确定要从购物车移除该商品吗？', '提示', {
    type: 'warning',
    confirmButtonText: '移除',
    cancelButtonText: '取消'
  }).then(async () => {
    await delCart(cartId);
    ElMessage.success('已移除');
    initData(); // 刷新列表
  }).catch(error => {
    if (error && error.toString().includes('认证失败')) {
      ElMessage.error("登录状态已过期，请重新登录");
      router.push('/portal/login?redirect=' + encodeURIComponent(route.fullPath));
    }
  });
};

// 批量删除
const handleBatchDelete = () => {
  if (!checkLogin()) return;
  if (selectedIds.value.length === 0) return;
  
  ElMessageBox.confirm(`确定要移除选中的 ${selectedIds.value.length} 件商品吗？`, '提示', {
    type: 'warning',
    confirmButtonText: '确认移除',
    cancelButtonText: '取消'
  }).then(async () => {
    await delCart(selectedIds.value.join(','));
    ElMessage.success('批量移除成功');
    initData();
  }).catch(error => {
    if (error && error.toString().includes('认证失败')) {
      ElMessage.error("登录状态已过期，请重新登录");
      router.push('/portal/login?redirect=' + encodeURIComponent(route.fullPath));
    }
  });
};

// 去结算
const handleCheckout = () => {
  if (!checkLogin()) return;
  
  // 获取选中的购物车项ID
  const selectedCartIds = cartList.value
    .filter(item => item.isChecked === '1')
    .map(item => item.cartId);
  
  if (selectedCartIds.length === 0) {
    ElMessage.warning('请先选择要结算的商品');
    return;
  }
  
  // 跳转到确认订单页前，验证token
  const token = localStorage.getItem('app_token');
  console.log("购物车页面跳转前，token:", token);
  
  // 跳转到确认订单页，传递选中的购物车ID
  router.push({
    path: '/computer-market/checkout',
    query: {
      ids: selectedCartIds.join(',')
    }
  });
};

onMounted(() => {
  initData();
});
</script>

<style scoped>
.cart-page {
  min-height: 100vh;
  background-color: #f8fafc;
  padding-bottom: 100px; /* 为底部栏留位置 */
}

.main-container {
  max-width: 1200px;
  margin: 80px auto 0;
  padding: 0 20px;
}

.cart-header {
  margin-bottom: 24px;
  display: flex;
  align-items: baseline;
  gap: 12px;
}
.cart-header .title {
  font-size: 28px;
  color: #1e293b;
  font-weight: 700;
}
.cart-header .count {
  color: #64748b;
  font-size: 14px;
}

/* 空状态 */
.empty-cart {
  background: #fff;
  border-radius: 16px;
  padding: 60px 0;
  box-shadow: 0 2px 10px rgba(0,0,0,0.02);
}

/* 表格结构自定义 - Flex布局模拟表格 */
.cart-list-wrapper {
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 2px 10px rgba(0,0,0,0.02);
}

.cart-table-header {
  display: flex;
  padding: 16px 24px;
  background: #f1f5f9;
  color: #64748b;
  font-size: 14px;
  font-weight: 500;
}

/* 列宽定义 */
.col-check { width: 60px; display: flex; align-items: center; }
.col-info { flex: 1; }
.col-type { width: 100px; text-align: center; }
.col-price { width: 140px; text-align: center; }
.col-qty { width: 140px; text-align: center; }
.col-subtotal { width: 140px; text-align: center; font-weight: 700; color: #1e293b; }
.col-action { width: 80px; text-align: center; }

/* 列表项样式 */
.cart-item {
  display: flex;
  align-items: center;
  padding: 24px;
  border-bottom: 1px solid #f1f5f9;
  transition: background-color 0.2s;
}
.cart-item:hover {
  background-color: #fafbfd;
}
.cart-item:last-child {
  border-bottom: none;
}

/* 商品信息列 */
.product-info {
  display: flex;
  gap: 16px;
  align-items: center;
}
.product-img {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  background: #f8fafc;
}
.info-text {
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.info-text .name {
  font-size: 16px;
  color: #1e293b;
  font-weight: 500;
  cursor: pointer;
  transition: color 0.2s;
}
.info-text .name:hover {
  color: #3b82f6;
}
.specs {
  font-size: 12px;
  color: #94a3b8;
}

/* 价格列 */
.col-price, .col-subtotal {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}
.unit {
  font-size: 12px;
  color: #94a3b8;
  font-weight: normal;
}
.currency {
  font-size: 12px;
  margin-right: 2px;
}

/* 底部结算栏 */
.cart-footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #fff;
  border-top: 1px solid #e2e8f0;
  padding: 16px 40px; /* 略微调大两边边距 */
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 -4px 20px rgba(0,0,0,0.05);
  z-index: 100;
}

/* 居中约束（可选，如果希望底部栏内容也居中对齐主容器） */
/* 注意：由于是fixed，这里用简单的padding处理，如果追求完美可以加个内部容器 */

.footer-left {
  display: flex;
  align-items: center;
}

.footer-right {
  display: flex;
  align-items: center;
  gap: 24px;
}

.total-info {
  text-align: right;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.total-row {
  display: flex;
  align-items: baseline;
  justify-content: flex-end;
  gap: 8px;
  font-size: 14px;
  color: #64748b;
}

.total-row .price {
  font-size: 18px;
  font-weight: 700;
}
.rent-price { color: #3b82f6; }
.sale-price { color: #ef4444; }

.checkout-btn {
  height: 44px;
  padding: 0 36px;
  font-size: 16px;
  border-radius: 22px;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}
</style>