<template>
  <div class="checkout-container">
    <div class="main-content">
      <!-- 步骤条 -->
      <el-steps :active="1" finish-status="success" simple class="mb-20">
        <el-step title="我的购物车" icon="ShoppingCart" />
        <el-step title="填写核对订单信息" icon="Edit" />
        <el-step title="成功提交订单" icon="CircleCheck" />
      </el-steps>

      <!-- 1. 收货地址选择 -->
      <el-card class="section-card mb-20">
        <template #header>
          <div class="section-header">
            <span class="title"><el-icon><Location /></el-icon> 收货人信息</span>
            <el-button link type="primary" @click="openAddressModal">切换地址</el-button>
            <el-button link type="primary" @click="$router.push('/portal/user/address')">管理地址</el-button>
          </div>
        </template>
        
        <div v-if="currentAddress" class="current-address-box">
          <div class="addr-name">{{ currentAddress.realName }} <span class="phone">{{ currentAddress.phone }}</span></div>
          <div class="addr-detail">
            <el-tag v-if="currentAddress.isDefault === '1'" size="small" type="danger">默认</el-tag>
            {{ currentAddress.province }} {{ currentAddress.city }} {{ currentAddress.district }} {{ currentAddress.detailAddress }}
          </div>
        </div>
        <div v-else class="no-address">
          <el-empty description="您还没有选择收货地址" :image-size="60">
            <el-button type="primary" @click="openAddressModal">选择或添加地址</el-button>
          </el-empty>
        </div>
      </el-card>

      <!-- 2. 商品清单 -->
      <el-card class="section-card mb-20">
        <template #header>
          <div class="section-header">
            <span class="title"><el-icon><Goods /></el-icon> 商品清单</span>
          </div>
        </template>

        <el-table :data="checkoutList" border style="width: 100%">
          <el-table-column label="商品信息" min-width="300">
            <template #default="scope">
              <div class="product-info">
                <img :src="scope.row.productImg || '/default-img.png'" class="product-img" />
                <div class="product-detail">
                  <div class="name">{{ scope.row.productName }}</div>
                  <div class="tags">
                    <el-tag v-if="scope.row.businessType === '1'" type="warning" size="small">租赁</el-tag>
                    <el-tag v-else type="success" size="small">购买</el-tag>
                  </div>
                </div>
              </div>
            </template>
          </el-table-column>
          
          <el-table-column label="单价/日租金" width="150" align="center">
            <template #default="scope">
              <span class="price">¥{{ scope.row.price }}</span>
              <span v-if="scope.row.businessType === '1'" class="unit">/天</span>
            </template>
          </el-table-column>

          <el-table-column label="数量" width="100" align="center" prop="quantity" />

          <el-table-column label="租赁配置 (仅租赁商品)" min-width="320">
            <template #default="scope">
              <div v-if="scope.row.businessType === '1'">
                <el-date-picker
                  v-model="scope.row.daterange"
                  type="daterange"
                  range-separator="至"
                  start-placeholder="起租日期"
                  end-placeholder="归还日期"
                  :disabled-date="disabledDate"
                  @change="(val) => handleDateChange(val, scope.row)"
                  style="width: 100%"
                  size="default"
                />
                <div v-if="scope.row.rentDays > 0" class="rent-days-tip">
                  共租 {{ scope.row.rentDays }} 天
                </div>
              </div>
              <span v-else class="text-gray">无须配置</span>
            </template>
          </el-table-column>

          <el-table-column label="小计" width="150" align="center">
            <template #default="scope">
              <span class="subtotal">¥{{ calculateSubtotal(scope.row) }}</span>
            </template>
          </el-table-column>
        </el-table>

        <div class="remark-section mt-20">
          <el-input
            v-model="orderRemark"
            type="textarea"
            :rows="2"
            placeholder="订单备注：如有特殊需求，请在此处留言"
          />
        </div>
      </el-card>
    </div>

    <!-- 底部结算栏 -->
    <div class="checkout-footer">
      <div class="footer-content">
        <div class="total-info">
          <span>共 <span class="highlight">{{ totalCount }}</span> 件商品</span>
          <span class="ml-20">应付总额：</span>
          <span class="final-price">¥{{ finalTotalPrice }}</span>
        </div>
        <el-button 
          type="danger" 
          size="large" 
          class="submit-btn" 
          @click="submitOrder" 
          :loading="submitting"
        >
          提交订单
        </el-button>
      </div>
    </div>

    <!-- 地址选择弹窗 -->
    <el-dialog v-model="addressVisible" title="选择收货地址" width="600px">
      <div class="address-list">
        <div 
          v-for="addr in addressList" 
          :key="addr.addressId" 
          class="address-option"
          :class="{ active: selectedAddressId === addr.addressId }"
          @click="selectedAddressId = addr.addressId"
        >
          <div class="opt-name">{{ addr.realName }} {{ addr.phone }}</div>
          <div class="opt-detail">{{ addr.province }}{{ addr.city }}{{ addr.district }}{{ addr.detailAddress }}</div>
          <el-icon v-if="selectedAddressId === addr.addressId" class="check-icon"><Select /></el-icon>
        </div>
      </div>
      <div v-if="addressList.length === 0" class="text-center py-4">
        <el-button type="primary" @click="$router.push('/portal/user/address')">去管理地址</el-button>
      </div>
      <template #footer>
        <el-button @click="addressVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmAddressSelection">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { ShoppingCart, Edit, CircleCheck, Location, Goods, Select } from '@element-plus/icons-vue';
import { listCart } from "@/api/shop/cart";
import { listAddress } from "@/api/portal/address";
import { createOrder } from "@/api/portal/order";
import { getAppToken } from '@/utils/auth';

const route = useRoute();
const router = useRouter();

// 数据
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

// 禁用过期日期（今天之前）
const disabledDate = (time) => {
  return time.getTime() < Date.now() - 8.64e7;
};

// 监听日期变化计算天数
const handleDateChange = (val, row) => {
  if (val && val.length === 2) {
    const start = val[0].getTime();
    const end = val[1].getTime();
    // 向上取整计算天数，至少算1天
    let days = Math.ceil((end - start) / (1000 * 3600 * 24));
    if (days <= 0) days = 1;
    row.rentDays = days;
  } else {
    row.rentDays = 0;
  }
};

// 计算单项小计
const calculateSubtotal = (item) => {
  if (item.businessType === '1') {
    // 租赁：单价 * 数量 * 天数
    const days = item.rentDays || 0;
    return item.price * item.quantity * days;
  } else {
    // 购买：单价 * 数量
    return item.price * item.quantity;
  }
};

// 初始化加载
const init = async () => {
  // 检查登录状态 - 直接使用localStorage避免任何中间函数问题
  const token = localStorage.getItem('app_token');
  console.log("订单页面init，从localStorage获取token:", token);
  
  if (!token) {
    ElMessage.error("请先登录");
    router.push('/portal/login?redirect=' + encodeURIComponent(route.fullPath));
    return;
  }

  const cartIdsStr = route.query.ids;
  if (!cartIdsStr) {
    ElMessage.error("参数错误，未选择商品");
    router.push('/computer-market/cart');
    return;
  }
  const cartIdArr = cartIdsStr.split(',').map(Number);

  // 1. 获取购物车数据并过滤
  try {
    const res = await listCart(); // 假设这个接口返回该用户所有购物车项
    const allItems = res.rows || res.data || [];
    
    // 过滤出选中的商品
    checkoutList.value = allItems.filter(item => cartIdArr.includes(item.cartId)).map(item => ({
      ...item,
      daterange: [], // 初始化日期范围
      rentDays: 0    // 初始化租期
    }));

    if (checkoutList.value.length === 0) {
      ElMessage.warning("未找到指定商品，请重新选择");
      router.push('/computer-market/cart');
    }
  } catch (error) {
    console.error("Failed to fetch cart items", error);
    if (error && error.toString().includes('认证失败')) {
      ElMessage.error("登录状态已过期，请重新登录");
      router.push('/portal/login?redirect=' + encodeURIComponent(route.fullPath));
    }
  }

  // 2. 获取地址列表并设置默认
  try {
    console.log("准备调用listAddress，当前token:", token);
    console.log("当前路由完整路径:", route.fullPath);
    console.log("当前路由query参数:", route.query);
    
    const addrRes = await listAddress();
    console.log("listAddress返回结果:", addrRes);
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
    console.error("Failed to fetch addresses", error);
    if (error && error.toString().includes('认证失败')) {
      ElMessage.error("登录状态已过期，请重新登录");
      router.push('/portal/login?redirect=' + encodeURIComponent(route.fullPath));
    }
  }
};

// 地址相关操作
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
  // 检查登录状态
  const token = getAppToken();
  if (!token) {
    ElMessage.error("请先登录");
    router.push('/portal/login?redirect=' + encodeURIComponent(route.fullPath));
    return;
  }

  if (!currentAddress.value) {
    ElMessage.warning("请先选择收货地址");
    return;
  }

  // 校验租赁商品是否选了时间
  for (const item of checkoutList.value) {
    if (item.businessType === '1' && (!item.daterange || item.daterange.length !== 2)) {
      ElMessage.warning(`商品【${item.productName}】是租赁商品，请选择起租和归还日期`);
      return;
    }
  }

  submitting.value = true;

  // 构造后端需要的入参格式
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
      // 如果是租赁，加入时间
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
    // 跳转到支付页，传递订单号和金额
    // 后端返回的是订单号数组，取第一个订单号
    const orderNo = res.data?.[0] || res.data?.orderNo || res.orderNo;
    const totalAmount = finalTotalPrice.value;
    
    router.push({
      path: '/portal/trade/pay',
      query: {
        orderNo: orderNo,
        amount: totalAmount
      }
    });
  } catch (error) {
    console.error("Order creation failed", error);
    if (error && error.toString().includes('认证失败')) {
      ElMessage.error("登录状态已过期，请重新登录");
      router.push('/portal/login?redirect=' + encodeURIComponent(route.fullPath));
    }
  } finally {
    submitting.value = false;
  }
};

onMounted(() => {
  init();
});
</script>

<style scoped>
.checkout-container {
  background-color: #f5f7fa;
  min-height: 100vh;
  padding-bottom: 80px; /* 留出底部栏空间 */
}

.main-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.mb-20 { margin-bottom: 20px; }
.mt-20 { margin-top: 20px; }
.text-gray { color: #909399; font-size: 12px; }

.section-card {
  border-radius: 8px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.section-header .title {
  font-size: 16px;
  font-weight: bold;
  display: flex;
  align-items: center;
  gap: 5px;
}

/* 地址样式 */
.current-address-box {
  padding: 10px;
  border: 1px solid #e4e7ed;
  background: #fcfcfc;
  border-radius: 4px;
}
.addr-name { font-weight: bold; font-size: 16px; margin-bottom: 5px; }
.addr-name .phone { margin-left: 10px; font-weight: normal; }
.addr-detail { color: #606266; line-height: 1.5; }

.address-option {
  padding: 15px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  margin-bottom: 10px;
  cursor: pointer;
  position: relative;
  transition: all 0.2s;
}
.address-option:hover { border-color: #409eff; }
.address-option.active { border-color: #409eff; background-color: #ecf5ff; }
.opt-name { font-weight: bold; margin-bottom: 5px; }
.check-icon { position: absolute; right: 15px; top: 50%; transform: translateY(-50%); color: #409eff; font-weight: bold; }

/* 商品列表样式 */
.product-info {
  display: flex;
  align-items: center;
  gap: 10px;
}
.product-img {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 4px;
  border: 1px solid #eee;
}
.product-detail .name {
  font-size: 14px;
  margin-bottom: 5px;
  line-height: 1.4;
}
.price { font-weight: bold; color: #303133; }
.unit { font-size: 12px; color: #909399; }
.subtotal { color: #f56c6c; font-weight: bold; font-size: 16px; }
.rent-days-tip { font-size: 12px; color: #e6a23c; margin-top: 4px; }

/* 底部结算栏 */
.checkout-footer {
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  background: #fff;
  box-shadow: 0 -2px 10px rgba(0,0,0,0.05);
  z-index: 100;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  height: 70px;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  padding: 0 20px;
}

.total-info {
  font-size: 14px;
  margin-right: 20px;
  display: flex;
  align-items: baseline;
}

.ml-20 { margin-left: 20px; }
.highlight { color: #f56c6c; font-weight: bold; margin: 0 4px; }
.final-price { font-size: 28px; color: #f56c6c; font-weight: bold; }

.submit-btn {
  width: 160px;
  font-size: 18px;
}
</style>