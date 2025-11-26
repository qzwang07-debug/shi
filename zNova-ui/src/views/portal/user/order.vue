<template>
  <div class="app-container">
    <el-tabs v-model="activeTab" @tab-click="handleTabClick">
      <el-tab-pane label="全部订单" name="all"></el-tab-pane>
      <el-tab-pane label="待支付" name="0"></el-tab-pane>
      <el-tab-pane label="待发货" name="1"></el-tab-pane>
      <el-tab-pane label="待收货/租赁中" name="2"></el-tab-pane>
      <el-tab-pane label="已完成" name="3"></el-tab-pane>
    </el-tabs>

    <div v-loading="loading">
      <div v-if="orderList.length === 0" class="empty-block">
        <el-empty description="暂无订单数据" />
      </div>

      <el-card v-for="order in orderList" :key="order.orderId" shadow="hover" class="order-card">
        <template #header>
          <div class="card-header">
            <span>订单号：{{ order.orderNo }}</span>
            <el-tag :type="statusTypeMap[order.status]">{{ statusTextMap[order.status] }}</el-tag>
          </div>
        </template>

        <div v-for="item in order.shopOrderItemList" :key="item.id" class="order-item">
          <img :src="item.productImg" class="item-img" alt="商品图片" />
          <div class="item-info">
            <div class="item-name">{{ item.productName }}</div>
            
            <div v-if="item.businessType === '1'" class="rent-info">
              <span class="tag-rent">租赁</span>
              <span class="date-range">
                {{ parseTime(item.rentStartTime, '{y}-{m}-{d}') }} 至 
                {{ parseTime(item.rentEndTime, '{y}-{m}-{d}') }}
              </span>
              <span class="days">(共 {{ item.rentDays }} 天)</span>
            </div>
            <div v-else class="sale-info">
              <span class="tag-sale">购买</span>
            </div>
            
            <div class="item-price">
              <span v-if="item.businessType === '1'">日租金: ¥{{ item.price }}</span>
              <span v-else>单价: ¥{{ item.price }}</span>
              <span class="quantity">x{{ item.quantity }}</span>
            </div>
          </div>
        </div>

        <div class="card-footer">
          <div class="total-amount">
            实付金额: <span class="price-text">¥{{ order.totalAmount }}</span>
          </div>
          <div class="action-btns">
            <el-button 
              v-if="order.status === '0'" 
              type="danger" 
              size="small" 
              @click="handlePay(order)"
            >
              去支付
            </el-button>

            <el-button 
              v-if="order.status === '2'" 
              type="primary" 
              size="small" 
              @click="handleConfirm(order)"
            >
              确认收货
            </el-button>
            
            <el-button v-if="order.status === '1'" type="info" plain size="small" disabled>等待商家发货</el-button>
          </div>
        </div>
      </el-card>
    </div>
    
    <pagination
      v-show="total>0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import request from '@/utils/request'; // 假设你没有封装单独的api文件，直接用request演示
import { ElMessage, ElMessageBox } from 'element-plus';
import { parseTime } from '@/utils/ruoyi';
import { getAppToken } from '@/utils/auth';
const router = useRouter();
const route = useRoute();
const loading = ref(false);
const orderList = ref([]);
const total = ref(0);
const activeTab = ref('all');

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  status: undefined
});

// 状态字典映射
const statusTextMap = {
  '0': '待支付',
  '1': '待发货',
  '2': '租赁中/已发货',
  '3': '已完成',
  '4': '已取消'
};
const statusTypeMap = {
  '0': 'danger',
  '1': 'warning',
  '2': 'primary',
  '3': 'success',
  '4': 'info'
};

// 获取订单列表
function getList() {
  loading.value = true;
  const token = getAppToken();
  request({
    url: '/app/order/list',
    method: 'get',
    params: queryParams,
    headers: {
      'Authorization': 'Bearer ' + token
    }
  }).then(response => {
    orderList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  }).catch((error) => {
    loading.value = false;
    if (error && error.toString().includes('认证失败')) {
      ElMessage.error("登录状态已过期，请重新登录");
      router.push('/portal/login?redirect=' + encodeURIComponent(router.currentRoute.value.fullPath));
    }
  });
}

// 切换Tab
function handleTabClick(tab) {
  if (tab.props.name === 'all') {
    queryParams.status = undefined;
  } else {
    queryParams.status = tab.props.name;
  }
  queryParams.pageNum = 1;
  getList();
}

// 跳转去支付页
function handlePay(order) {
  router.push({
    path: '/portal/trade/pay',
    query: {
      orderNo: order.orderNo,
      amount: order.totalAmount
    }
  });
}

// 确认收货
function handleConfirm(order) {
  ElMessageBox.confirm('确认已收到商品或已完成租赁归还吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    const token = getAppToken();
    request({
      url: `/app/order/confirm/${order.orderNo}`,
      method: 'put',
      headers: {
        'Authorization': 'Bearer ' + token
      }
    }).then(() => {
      ElMessage.success('操作成功');
      getList();
    }).catch((error) => {
      if (error && error.toString().includes('认证失败')) {
        ElMessage.error("登录状态已过期，请重新登录");
        router.push('/portal/login?redirect=' + encodeURIComponent(router.currentRoute.value.fullPath));
      }
    });
  });
}

onMounted(() => {
  // 检查路由参数中是否有tab参数，如果有则切换到对应标签
  if (route.query.tab) {
    activeTab.value = route.query.tab;
    if (route.query.tab === 'all') {
      queryParams.status = undefined;
    } else {
      queryParams.status = route.query.tab;
    }
  }
  getList();
});
</script>

<style scoped lang="scss">
.order-card {
  margin-bottom: 20px;
  
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-weight: bold;
  }

  .order-item {
    display: flex;
    padding: 10px 0;
    border-bottom: 1px solid #f0f0f0;
    
    &:last-child {
      border-bottom: none;
    }

    .item-img {
      width: 80px;
      height: 80px;
      object-fit: cover;
      border-radius: 4px;
      margin-right: 15px;
    }

    .item-info {
      flex: 1;
      
      .item-name {
        font-size: 15px;
        color: #333;
        margin-bottom: 5px;
      }

      .rent-info {
        font-size: 13px;
        color: #666;
        margin-bottom: 5px;
        .tag-rent {
          background: #e8f4ff;
          color: #1890ff;
          padding: 2px 5px;
          border-radius: 2px;
          margin-right: 5px;
          font-size: 12px;
        }
      }
      
      .sale-info {
        margin-bottom: 5px;
        .tag-sale {
          background: #fff1f0;
          color: #f5222d;
          padding: 2px 5px;
          border-radius: 2px;
          font-size: 12px;
        }
      }

      .item-price {
        display: flex;
        justify-content: space-between;
        color: #333;
        font-weight: 500;
        .quantity {
          color: #999;
        }
      }
    }
  }

  .card-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-top: 10px;
    padding-top: 10px;
    border-top: 1px solid #eee;

    .total-amount {
      font-size: 14px;
      .price-text {
        color: #f56c6c;
        font-size: 18px;
        font-weight: bold;
      }
    }
  }
}
</style>