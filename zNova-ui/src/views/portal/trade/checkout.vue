<template>
  <div class="common-layout">
    <el-container class="app-container">
      <Header />
      
      <el-main class="main-content">
        <div class="checkout-container">
          <div class="content-wrapper">
            
            <!-- 顶部导航与步骤 -->
            <div class="page-header">
              <el-breadcrumb separator="/" class="custom-breadcrumb">
                <el-breadcrumb-item :to="{ path: '/computer-market' }">首页</el-breadcrumb-item>
                <el-breadcrumb-item :to="{ path: '/computer-market/shopping-cart' }">购物车</el-breadcrumb-item>
                <el-breadcrumb-item>确认订单</el-breadcrumb-item>
              </el-breadcrumb>
              
              <div class="steps-wrapper">
                <el-steps :active="1" finish-status="success" align-center class="custom-steps">
                  <el-step title="查看购物车" icon="ShoppingCart" />
                  <el-step title="确认订单信息" icon="Edit" />
                  <el-step title="支付订单" icon="Money" />
                  <el-step title="完成" icon="CircleCheck" />
                </el-steps>
              </div>
            </div>

            <!-- 1. 收货地址区域 -->
            <div class="section-card address-section">
              <div class="card-header">
                <div class="header-title">
                  <el-icon class="header-icon"><Location /></el-icon>
                  <span>收货信息</span>
                </div>
                <div class="header-actions">
                  <el-button link type="primary" @click="openAddressModal">切换地址</el-button>
                  <el-divider direction="vertical" />
                  <el-button link @click="$router.push('/portal/user/address')">管理地址</el-button>
                </div>
              </div>
              
              <div class="card-body">
                <div v-if="currentAddress" class="selected-address-card">
                  <div class="address-ribbon" v-if="currentAddress.isDefault === '1'">默认</div>
                  <div class="addr-icon">
                    <el-icon><Place /></el-icon>
                  </div>
                  <div class="addr-info">
                    <div class="addr-user">
                      <span class="name">{{ currentAddress.realName }}</span>
                      <span class="phone">{{ currentAddress.phone }}</span>
                    </div>
                    <div class="addr-text">
                      {{ currentAddress.province }} {{ currentAddress.city }} {{ currentAddress.district }} 
                      <span class="detail-text">{{ currentAddress.detailAddress }}</span>
                    </div>
                  </div>
                  <div class="addr-edit" @click="openAddressModal">
                    <el-icon><Edit /></el-icon>
                  </div>
                </div>

                <div v-else class="no-address-state">
                  <el-empty description="您还没有选择收货地址" :image-size="80">
                    <el-button type="primary" icon="Plus" round @click="openAddressModal">选择或添加地址</el-button>
                  </el-empty>
                </div>
              </div>
            </div>

            <!-- 2. 商品清单 -->
            <div class="section-card product-section">
              <div class="card-header">
                <div class="header-title">
                  <el-icon class="header-icon"><Goods /></el-icon>
                  <span>商品清单</span>
                </div>
                <span class="item-count">共 {{ totalCount }} 件商品</span>
              </div>

              <div class="card-body no-padding">
                <el-table 
                  :data="checkoutList" 
                  style="width: 100%" 
                  :header-cell-style="{ background: '#f8f9fa', color: '#606266', fontWeight: '500' }"
                  class="checkout-table"
                >
                  <el-table-column label="商品信息" min-width="320">
                    <template #default="scope">
                      <div class="product-info-cell">
                        <div class="img-wrapper">
                          <img :src="scope.row.productImg || '/default-img.png'" class="product-img" />
                        </div>
                        <div class="product-detail">
                          <div class="name">{{ scope.row.productName }}</div>
                          <div class="tags">
                            <span 
                              class="biz-tag"
                              :class="scope.row.businessType === '1' ? 'tag-rent' : 'tag-sale'"
                            >
                              {{ scope.row.businessType === '1' ? '租赁' : '购买' }}
                            </span>
                          </div>
                        </div>
                      </div>
                    </template>
                  </el-table-column>
                  
                  <el-table-column label="单价" width="160" align="center">
                    <template #default="scope">
                      <div class="price-cell">
                        <span class="currency">¥</span>
                        <span class="num">{{ scope.row.price }}</span>
                        <span v-if="scope.row.businessType === '1'" class="unit">/天</span>
                      </div>
                    </template>
                  </el-table-column>

                  <el-table-column label="数量" width="100" align="center">
                    <template #default="scope">
                      <span class="qty-text">x {{ scope.row.quantity }}</span>
                    </template>
                  </el-table-column>

                  <el-table-column label="租赁周期配置" min-width="300">
                    <template #default="scope">
                      <div v-if="scope.row.businessType === '1'" class="rent-config">
                        <el-date-picker
                          v-model="scope.row.daterange"
                          type="daterange"
                          range-separator="→"
                          start-placeholder="起租"
                          end-placeholder="归还"
                          :disabled-date="disabledDate"
                          @change="(val) => handleDateChange(val, scope.row)"
                          style="width: 100%"
                          size="default"
                          :prefix-icon="Calendar"
                          class="custom-date-picker"
                        />
                        <div v-if="scope.row.rentDays > 0" class="rent-duration">
                          <el-icon><Timer /></el-icon> 租期: <span class="highlight">{{ scope.row.rentDays }}</span> 天
                        </div>
                      </div>
                      <span v-else class="text-gray-light">--</span>
                    </template>
                  </el-table-column>

                  <el-table-column label="小计" width="160" align="center">
                    <template #default="scope">
                      <span class="subtotal-price">¥{{ calculateSubtotal(scope.row) }}</span>
                    </template>
                  </el-table-column>
                </el-table>

                <!-- 备注区域 -->
                <div class="remark-wrapper">
                  <span class="remark-label">订单备注：</span>
                  <el-input
                    v-model="orderRemark"
                    placeholder="选填：给商家留言（温馨提示：请确认好租赁时间）"
                    class="remark-input"
                    maxlength="100"
                    show-word-limit
                  />
                </div>
              </div>
            </div>

          </div>
        </div>

        <!-- 底部结算悬浮栏 -->
        <div class="checkout-footer-bar">
          <div class="bar-content">
            <div class="price-info">
              <span class="label">应付总额:</span>
              <div class="total-price-wrapper">
                <span class="currency">¥</span>
                <span class="amount">{{ finalTotalPrice }}</span>
              </div>
            </div>
            <div class="action-area">
              <span class="item-summary">共 <b>{{ totalCount }}</b> 件商品</span>
              <el-button 
                color="#626aef" 
                size="large" 
                class="submit-btn" 
                @click="submitOrder" 
                :loading="submitting"
                round
              >
                立即支付
              </el-button>
            </div>
          </div>
        </div>

        <!-- 地址选择弹窗 -->
        <el-dialog 
          v-model="addressVisible" 
          title="选择收货地址" 
          width="600px" 
          class="address-dialog"
          append-to-body
        >
          <div class="address-grid-select">
            <div 
              v-for="addr in addressList" 
              :key="addr.addressId" 
              class="addr-option-card"
              :class="{ 'is-selected': selectedAddressId === addr.addressId }"
              @click="selectedAddressId = addr.addressId"
            >
              <div class="card-top">
                <span class="name">{{ addr.realName }}</span>
                <span class="phone">{{ addr.phone }}</span>
                <el-tag v-if="addr.isDefault === '1'" size="small" type="danger" effect="plain" class="default-tag">默认</el-tag>
              </div>
              <div class="card-mid">
                {{ addr.province }}{{ addr.city }}{{ addr.district }} {{ addr.detailAddress }}
              </div>
              <div class="check-mark" v-if="selectedAddressId === addr.addressId">
                <el-icon><Select /></el-icon>
              </div>
            </div>
            
            <div class="add-new-addr" @click="goToAddressMgr">
              <el-icon><Plus /></el-icon>
              <span>管理/新增地址</span>
            </div>
          </div>
          <template #footer>
            <div class="dialog-footer">
              <el-button @click="addressVisible = false">取消</el-button>
              <el-button type="primary" @click="confirmAddressSelection" color="#626aef">确认选择</el-button>
            </div>
          </template>
        </el-dialog>

      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { 
  ShoppingCart, Edit, CircleCheck, Location, Goods, Select, 
  Place, Timer, Calendar, Plus, Money 
} from '@element-plus/icons-vue';
import { listCart } from "@/api/shop/cart";
import { listAddress } from "@/api/portal/address";
import { createOrder } from "@/api/portal/order";
import { getAppToken } from '@/utils/auth';
import Header from '@/views/computerMarket/Header.vue';

const route = useRoute();
const router = useRouter();

// 数据状态
const checkoutList = ref([]);
const addressList = ref([]);
const currentAddress = ref(null);
const selectedAddressId = ref(null);
const orderRemark = ref('');
const addressVisible = ref(false);
const submitting = ref(false);

// 计算属性
const totalCount = computed(() => checkoutList.value.length);

const finalTotalPrice = computed(() => {
  let total = 0;
  checkoutList.value.forEach(item => {
    total += calculateSubtotal(item);
  });
  return total.toFixed(2);
});

// 日期相关
const disabledDate = (time) => {
  return time.getTime() < Date.now() - 8.64e7;
};

const handleDateChange = (val, row) => {
  if (val && val.length === 2) {
    const start = val[0].getTime();
    const end = val[1].getTime();
    let days = Math.ceil((end - start) / (1000 * 3600 * 24));
    if (days <= 0) days = 1;
    row.rentDays = days;
  } else {
    row.rentDays = 0;
  }
};

const calculateSubtotal = (item) => {
  if (item.businessType === '1') {
    const days = item.rentDays || 0;
    return item.price * item.quantity * days;
  } else {
    return item.price * item.quantity;
  }
};

// 导航
const goToAddressMgr = () => {
  addressVisible.value = false;
  router.push('/portal/user/address');
}

// 初始化
const init = async () => {
  const token = localStorage.getItem('app_token');
  if (!token) {
    ElMessage.error("请先登录");
    router.push('/portal/login?redirect=' + encodeURIComponent(route.fullPath));
    return;
  }

  const directBuy = route.query.directBuy === 'true';
  const productStr = route.query.product;
  
  // 检查是否有从build页面传递过来的商品信息
  const productId = route.query.productId;
  const productName = route.query.productName;
  
  // 如果有从build页面传递的商品信息，则使用它
  if (productId && productName) {
    checkoutList.value = [{
      productId: productId,
      productName: productName,
      quantity: 1,
      price: 0, // 需要从后端获取价格
      businessType: '0', // 默认为购买
      daterange: [],
      rentDays: 0
    }];
  } else if (directBuy && productStr) {
    try {
      const productData = JSON.parse(decodeURIComponent(productStr));
      checkoutList.value = [productData];
    } catch (error) {
      ElMessage.error("商品数据错误");
      router.push('/computer-market');
      return;
    }
  } else {
    const cartIdsStr = route.query.ids;
    if (!cartIdsStr) {
      // 检查是否有sessionStorage中存储的商品信息
       const pendingProduct = sessionStorage.getItem('pendingProduct');
       if (pendingProduct) {
         try {
           const productData = JSON.parse(pendingProduct);
           checkoutList.value = [productData];
           // 清除sessionStorage中的商品信息
           sessionStorage.removeItem('pendingProduct');
         } catch (error) {
           ElMessage.error("商品数据错误");
           router.push('/computer-market');
           return;
         }
       } else {
         ElMessage.error("未选择商品");
         router.push('/computer-market/cart');
         return;
       }
    } else {
      const cartIdArr = cartIdsStr.split(',').map(Number);

      try {
        const res = await listCart();
        const allItems = res.rows || res.data || [];
        checkoutList.value = allItems.filter(item => cartIdArr.includes(item.cartId)).map(item => ({
          ...item,
          daterange: [],
          rentDays: 0
        }));
        if (checkoutList.value.length === 0) {
          router.push('/computer-market/cart');
        }
      } catch (error) {
        console.error(error);
      }
    }
  }

  try {
    const addrRes = await listAddress();
    addressList.value = addrRes.data || [];
    const def = addressList.value.find(a => a.isDefault === '1');
    if (def) {
      currentAddress.value = def;
      selectedAddressId.value = def.addressId;
    } else if (addressList.value.length > 0) {
      currentAddress.value = addressList.value[0];
      selectedAddressId.value = addressList.value[0].addressId;
    }
  } catch (error) {
    console.error(error);
  }
};

// 弹窗逻辑
const openAddressModal = () => {
  if (currentAddress.value) {
    selectedAddressId.value = currentAddress.value.addressId;
  }
  addressVisible.value = true;
};

const confirmAddressSelection = () => {
  const selected = addressList.value.find(a => a.addressId === selectedAddressId.value);
  if (selected) {
    currentAddress.value = selected;
  }
  addressVisible.value = false;
};

// 提交订单
const submitOrder = async () => {
  if (!currentAddress.value) {
    ElMessage.warning("请先选择收货地址");
    return;
  }

  for (const item of checkoutList.value) {
    if (item.businessType === '1' && (!item.daterange || item.daterange.length !== 2)) {
      ElMessage.warning(`商品【${item.productName}】是租赁商品，请选择租赁时间`);
      return;
    }
  }

  submitting.value = true;

  const payload = {
    addressId: currentAddress.value.addressId,
    remark: orderRemark.value,
    items: checkoutList.value.map(item => {
      const baseItem = {
        cartId: item.cartId,
        productId: item.productId,
        quantity: item.quantity,
        businessType: item.businessType
      };
      if (item.businessType === '1') {
        baseItem.startDate = item.daterange[0];
        baseItem.endDate = item.daterange[1];
      }
      return baseItem;
    })
  };

  try {
    const res = await createOrder(payload);
    ElMessage.success("订单提交成功！");
    
    // 获取所有订单号
    const orderNos = Array.isArray(res.data) ? res.data : [res.data?.orderNo || res.orderNo];
    const totalAmount = finalTotalPrice.value;
    
    // 将订单号数组转换为逗号分隔的字符串
    const orderNosStr = orderNos.join(',');
    
    router.push({
      path: '/portal/trade/pay',
      query: { orderNos: orderNosStr, amount: totalAmount }
    });
  } catch (error) {
    console.error(error);
  } finally {
    submitting.value = false;
  }
};

onMounted(() => {
  init();
});
</script>

<style scoped>
.common-layout {
  min-height: 100vh;
}

.app-container {
  background-color: #f5f7fa; /* 统一浅灰色背景 */
  min-height: 100vh;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
}

.main-content {
  padding: 0;
  margin-top: 60px;
}

.checkout-container {
  padding-bottom: 100px; /* 留出底部栏空间 */
}

.content-wrapper {
  max-width: 1200px;
  margin: 0 auto;
  padding: 10px 20px;
}

/* --- 顶部导航 --- */
.page-header {
  margin-bottom: 30px;
}

.custom-breadcrumb {
  margin-bottom: 30px;
  font-size: 14px;
}

.steps-wrapper {
  background: #fff;
  padding: 30px 20px;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.02);
}

/* --- 通用卡片样式 --- */
.section-card {
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.03);
  margin-bottom: 25px;
  overflow: hidden;
  border: 1px solid #ebeef5;
}

.card-header {
  padding: 20px 25px;
  border-bottom: 1px solid #f2f3f5;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 8px;
}

.header-icon {
  color: #626aef;
  font-size: 20px;
}

.card-body {
  padding: 25px;
}

.card-body.no-padding {
  padding: 0;
}

/* --- 收货地址 --- */
.selected-address-card {
  position: relative;
  background: linear-gradient(135deg, #fff 0%, #f9faff 100%);
  border: 2px solid #626aef;
  border-radius: 12px;
  padding: 25px;
  display: flex;
  align-items: flex-start;
  gap: 20px;
  overflow: hidden;
  transition: all 0.3s;
}

.address-ribbon {
  position: absolute;
  top: 0;
  right: 0;
  background: #626aef;
  color: #fff;
  font-size: 12px;
  padding: 4px 12px;
  border-bottom-left-radius: 12px;
  font-weight: bold;
}

.addr-icon {
  width: 48px;
  height: 48px;
  background: #eff0ff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #626aef;
  font-size: 24px;
  flex-shrink: 0;
}

.addr-info {
  flex: 1;
}

.addr-user {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 8px;
}

.addr-user .phone {
  margin-left: 10px;
  font-weight: normal;
  color: #606266;
  font-size: 16px;
}

.addr-text {
  color: #606266;
  line-height: 1.5;
  font-size: 14px;
}
.detail-text { color: #303133; font-weight: 500; }

.addr-edit {
  color: #909399;
  cursor: pointer;
  padding: 10px;
  transition: color 0.2s;
}
.addr-edit:hover { color: #626aef; }

/* --- 商品清单 --- */
.product-info-cell {
  display: flex;
  gap: 15px;
  align-items: center;
  padding: 10px 0;
}

.img-wrapper {
  width: 70px;
  height: 70px;
  border-radius: 8px;
  border: 1px solid #eee;
  background: #f9f9f9;
  display: flex;
  align-items: center;
  justify-content: center;
}

.product-img {
  max-width: 100%;
  max-height: 100%;
  border-radius: 6px;
}

.product-detail .name {
  font-weight: 500;
  color: #303133;
  margin-bottom: 8px;
  line-height: 1.4;
}

.biz-tag {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 4px;
  color: #fff;
  display: inline-block;
}
.tag-rent { background: linear-gradient(135deg, #a0cfff 0%, #409eff 100%); }
.tag-sale { background: linear-gradient(135deg, #fab6b6 0%, #f56c6c 100%); }

.price-cell { font-weight: bold; color: #303133; }
.price-cell .unit { font-size: 12px; color: #909399; font-weight: normal; }
.qty-text { color: #909399; font-family: monospace; }
.subtotal-price { color: #f56c6c; font-weight: bold; font-size: 16px; }

/* 租赁配置 */
.rent-config {
  display: flex;
  flex-direction: column;
  gap: 5px;
}
.rent-duration {
  font-size: 12px;
  color: #e6a23c;
  display: flex;
  align-items: center;
  gap: 4px;
}
.text-gray-light { color: #c0c4cc; }

/* 备注 */
.remark-wrapper {
  padding: 20px 25px;
  background: #fdfdfd;
  display: flex;
  align-items: center;
  border-top: 1px solid #f2f3f5;
}

.remark-label {
  width: 90px;
  color: #606266;
}
.remark-input {
  max-width: 600px;
}

/* --- 底部结算悬浮栏 --- */
.checkout-footer-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  background: #fff;
  box-shadow: 0 -4px 20px rgba(0,0,0,0.08);
  z-index: 999;
  animation: slideUp 0.3s ease-out;
}

@keyframes slideUp {
  from { transform: translateY(100%); }
  to { transform: translateY(0); }
}

.bar-content {
  max-width: 1200px;
  margin: 0 auto;
  height: 80px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}

.price-info {
  display: flex;
  align-items: baseline;
  gap: 15px;
}

.price-info .label {
  font-size: 16px;
  color: #303133;
}

.total-price-wrapper {
  color: #f56c6c;
  font-weight: bold;
}
.total-price-wrapper .currency { font-size: 20px; }
.total-price-wrapper .amount { font-size: 32px; letter-spacing: -1px; }

.action-area {
  display: flex;
  align-items: center;
  gap: 20px;
}

.item-summary {
  color: #909399;
  font-size: 14px;
}
.item-summary b { color: #f56c6c; margin: 0 2px; }

.submit-btn {
  width: 180px;
  font-size: 16px;
  font-weight: 600;
  box-shadow: 0 4px 12px rgba(98, 106, 239, 0.4);
}

/* --- 地址选择弹窗 --- */
.address-grid-select {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
  max-height: 400px;
  overflow-y: auto;
  padding: 5px;
}

.addr-option-card {
  border: 1px solid #dcdfe6;
  border-radius: 8px;
  padding: 15px;
  cursor: pointer;
  position: relative;
  transition: all 0.2s;
}

.addr-option-card:hover {
  border-color: #c6e2ff;
  background: #f9faff;
}

.addr-option-card.is-selected {
  border-color: #626aef;
  background: #f2f3ff;
}

.card-top {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}
.card-top .name { font-weight: bold; color: #303133; }
.card-top .phone { color: #606266; font-size: 13px; }

.card-mid {
  font-size: 13px;
  color: #606266;
  line-height: 1.4;
}

.check-mark {
  position: absolute;
  right: 0;
  bottom: 0;
  background: #626aef;
  color: #fff;
  padding: 2px 6px;
  border-top-left-radius: 8px;
  border-bottom-right-radius: 8px;
}

.add-new-addr {
  border: 1px dashed #dcdfe6;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 100px;
  cursor: pointer;
  color: #909399;
  gap: 8px;
}
.add-new-addr:hover {
  border-color: #626aef;
  color: #626aef;
}
</style>