<template>
  <div class="common-layout">
    <Header />
    
    <div class="main-content">
      <div class="content-wrapper">
        
        <!-- 顶部 Tab 导航 -->
        <div class="tabs-header">
          <el-tabs v-model="activeTab" @tab-click="handleTabClick" class="custom-tabs">
            <el-tab-pane label="全部" name="all"></el-tab-pane>
            <el-tab-pane label="待支付" name="0"></el-tab-pane>
            <el-tab-pane label="待发货" name="1"></el-tab-pane>
            <el-tab-pane label="进行中" name="2"></el-tab-pane>
            <el-tab-pane label="已完成" name="3"></el-tab-pane>
          </el-tabs>
        </div>

        <!-- 订单列表区域 -->
        <div class="order-list-container" v-loading="loading">
          
          <!-- 空状态 -->
          <el-empty v-if="orderList.length === 0" description="暂无相关订单" :image-size="120" class="custom-empty" />

          <!-- 订单卡片 -->
          <div v-for="order in orderList" :key="order.orderId" class="order-card">
            
            <!-- 卡片头部：订单号 + 状态 -->
            <div class="card-header">
              <div class="header-left">
                <span class="order-label">订单号</span>
                <span class="order-no">{{ order.orderNo }}</span>
                <span class="create-time">{{ parseTime(order.createTime, '{y}-{m}-{d} {h}:{i}') }}</span>
              </div>
              <div class="header-right">
                <span class="status-tag" :class="`status-${order.status}`">
                  {{ statusText(order.status) }}
                </span>
              </div>
            </div>

            <!-- 卡片中间：商品列表 -->
            <div class="card-body">
              <template v-if="order.shopOrderItemList && order.shopOrderItemList.length > 0">
                <div v-for="item in order.shopOrderItemList" :key="item.id" class="product-item">
                  <div class="img-wrapper">
                    <img :src="item.productImg || '/default-img.png'" alt="商品" />
                  </div>
                  
                  <div class="info-wrapper">
                    <div class="info-main">
                      <h4 class="product-name">{{ item.productName }}</h4>
                      
                      <!-- 业务类型标签 -->
                      <div class="biz-info">
                        <span 
                          class="biz-tag"
                          :class="item.businessType === '1' ? 'tag-rent' : 'tag-sale'"
                        >
                          {{ item.businessType === '1' ? '租赁' : '购买' }}
                        </span>
                        
                        <span v-if="item.businessType === '1'" class="rent-date">
                           {{ parseTime(item.rentStartTime, '{m}/{d}') }} - {{ parseTime(item.rentEndTime, '{m}/{d}') }}
                        </span>
                      </div>
                    </div>
                    
                    <div class="price-qty">
                      <div class="price">¥{{ item.price }}</div>
                      <div class="qty">x{{ item.quantity }}</div>
                      
                      <!-- 单商品评价按钮 (仅完成状态显示) -->
                      <el-button 
                         v-if="order.status == '3'" 
                         type="primary" 
                         link 
                         size="small"
                         icon="ChatLineSquare" 
                         class="comment-btn-sm"
                         @click="openCommentDialog(order, item)"
                       >
                         评价商品
                       </el-button>
                    </div>
                  </div>
                </div>
              </template>

              <!-- 异常兜底 -->
              <div v-else class="empty-detail">
                <span>暂无商品明细数据</span>
                <el-button 
                   v-if="order.status == '3'" 
                   link type="primary" 
                   @click="openCommentDialog(order, { productId: 0, productName: '整单评价' })"
                >
                  评价订单
                </el-button>
              </div>
            </div>

            <!-- 卡片底部：合计 + 操作按钮 -->
            <div class="card-footer">
              <div class="total-section">
                <span>实付款</span>
                <span class="currency">¥</span>
                <span class="amount">{{ order.totalAmount }}</span>
              </div>

              <div class="actions-section">
                <!-- 待支付: 红色主要按钮 -->
                <template v-if="order.status == '0'">
                  <el-button round class="btn-cancel" @click="handleCancel(order)">取消订单</el-button>
                  <el-button type="danger" round class="btn-pay" @click="handlePay(order)">立即支付</el-button>
                </template>

                <!-- 待发货: 退款 -->
                <template v-if="order.status === '1'">
                  <el-button round @click="handleRefundApply(order)">申请退款</el-button>
                  <el-button type="primary" plain round disabled>等待发货</el-button>
                </template>

                <!-- 进行中: 确认收货 / 归还 -->
                <template v-if="order.status == '2'">
                  <!-- 购买订单 -->
                  <el-button 
                    v-if="!isRentOrder(order)" 
                    type="primary" 
                    round 
                    @click="handleConfirm(order)"
                  >
                    确认收货
                  </el-button>
                  
                  <!-- 租赁订单 -->
                  <template v-else>
                     <el-button round disabled>租赁中</el-button>
                     <el-button type="warning" plain round @click="handleReturn(order)">申请归还</el-button>
                  </template>
                </template>

                <!-- 已完成 -->
                <template v-if="order.status == '3'">
                   <el-button round disabled>已完成</el-button>
                </template>

                 <!-- 归还中 -->
                <template v-if="order.status == '7'">
                   <el-button type="info" plain round disabled>归还处理中</el-button>
                </template>
                
                <!-- 已取消/已退款 -->
                <template v-if="['4', '6'].includes(order.status)">
                   <el-button round disabled>交易关闭</el-button>
                </template>

              </div>
            </div>

          </div>
        </div>
      </div>
    </div>
    
    <!-- 评价对话框 -->
    <el-dialog v-model="commentOpen" title="商品评价" width="480px" align-center append-to-body class="comment-dialog">
      <div class="comment-product-name" v-if="commentForm.productName">
        评价商品：{{ commentForm.productName }}
      </div>
      <el-form :model="commentForm" label-position="top">
        <el-form-item label="整体评分">
          <el-rate 
            v-model="commentForm.star" 
            :colors="['#99A9BF', '#F7BA2A', '#FF9900']"
            show-text
            :texts="['极差', '失望', '一般', '满意', '惊喜']"
          />
        </el-form-item>
        <el-form-item label="详细体验">
          <el-input 
            v-model="commentForm.content" 
            type="textarea" 
            :rows="4" 
            placeholder="分享您的使用体验，帮助其他小伙伴..." 
            resize="none"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="commentOpen = false">暂不评价</el-button>
          <el-button type="primary" @click="submitComment" color="#626aef">提交评价</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 分页 -->
    <div class="pagination-container" v-show="total>0">
      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :total="total"
        layout="prev, pager, next"
        @current-change="getList"
        background
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, getCurrentInstance } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import request from '@/utils/request';
import { ElMessage, ElMessageBox } from 'element-plus';
import { parseTime } from '@/utils/ruoyi';
import { ChatLineSquare } from '@element-plus/icons-vue';
import Header from '@/views/computerMarket/Header.vue';
import { cancelOrder, applyRefund } from "@/api/portal/order"; 

const router = useRouter();
const route = useRoute();
const { proxy } = getCurrentInstance();

const loading = ref(false);
const orderList = ref([]);
const total = ref(0);
const activeTab = ref('all');
const commentOpen = ref(false);

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  status: undefined
});

const commentForm = reactive({
  orderId: null,
  productId: null,
  productName: '',
  star: 5,
  content: ''
});

// 状态文字映射
function statusText(status) {
  const map = {
    '0': '待支付',
    '1': '待发货',
    '2': '进行中',
    '3': '已完成',
    '4': '已取消',
    '5': '退款中',
    '6': '已退款',
    '7': '归还中'
  };
  return map[String(status)] || status;
}

const isRentOrder = (order) => {
  return order.shopOrderItemList && order.shopOrderItemList.some(item => item.businessType === '1');
};

function getList() {
  loading.value = true;
  request({ url: '/app/order/list', method: 'get', params: queryParams }).then(res => {
    orderList.value = res.rows || [];
    total.value = res.total;
  }).catch(error => {
    console.error(error);
  }).finally(() => {
    loading.value = false;
  });
}

function handleTabClick(tab) {
  queryParams.status = tab.props.name === 'all' ? undefined : tab.props.name;
  queryParams.pageNum = 1;
  getList();
}

function handlePay(order) {
  router.push({ path: '/portal/trade/pay', query: { orderNo: order.orderNo, amount: order.totalAmount } });
}

function handleConfirm(order) {
  ElMessageBox.confirm('确认收到商品且商品无误吗?', '收货确认', { 
    confirmButtonText: '确认收货',
    cancelButtonText: '取消',
    type: 'success' 
  }).then(() => {
    request({ url: `/app/order/confirm/${order.orderNo}`, method: 'put' }).then(() => {
      ElMessage.success('交易完成');
      getList();
    });
  });
}

function handleReturn(order) {
  ElMessageBox.confirm('确认已将设备寄回给商家吗?', '归还确认', { 
    confirmButtonText: '已寄出，确认归还',
    cancelButtonText: '再想想',
    type: 'warning' 
  }).then(() => {
    request({ url: `/app/order/return/${order.orderId}`, method: 'put' }).then(() => {
      ElMessage.success('归还申请已提交');
      getList();
    });
  });
}

function handleCancel(order) {
  ElMessageBox.confirm('确认要取消该订单吗？', '取消提示', {
    confirmButtonText: '确定取消',
    cancelButtonText: '再看看',
    type: 'info'
  }).then(() => {
    cancelOrder({ orderId: order.orderId }).then(() => {
      ElMessage.success("订单已取消");
      getList();
    });
  });
}

function handleRefundApply(order) {
  ElMessageBox.prompt('请输入退款原因', '申请退款', {
    confirmButtonText: '提交申请',
    cancelButtonText: '取消',
  }).then(({ value }) => {
    applyRefund({ orderId: order.orderId, reason: value }).then(() => {
      ElMessage.success("申请提交成功，请等待审核");
      getList();
    });
  }).catch(() => {});
}

function openCommentDialog(order, item) {
  commentForm.orderId = order.orderId;
  commentForm.productId = item.productId;
  commentForm.productName = item.productName || '未知商品';
  commentForm.star = 5;
  commentForm.content = '';
  commentOpen.value = true;
}

function submitComment() {
  if (!commentForm.content) return ElMessage.warning("请输入评价内容");
  request({
    url: '/app/comment/add',
    method: 'post',
    data: commentForm
  }).then(() => {
    ElMessage.success('评价成功');
    commentOpen.value = false;
  }).catch(error => {
    console.error(error);
  });
}

onMounted(() => {
  const tabIndex = route.query.activeTab;
  if (tabIndex !== undefined) {
    activeTab.value = tabIndex;
    handleTabClick({ props: { name: tabIndex } });
  } else {
    getList();
  }
});
</script>

<style scoped>
/* 全局布局 */
.common-layout {
  min-height: 100vh;
  background-color: #f5f7fa; /* 统一浅灰背景 */
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
}

.main-content {
  padding-top: 80px; /* Header space */
  padding-bottom: 40px;
}

.content-wrapper {
  max-width: 1000px;
  margin: 0 auto;
  padding: 0 20px;
}

/* Tabs 样式优化 */
.tabs-header {
  background: #fff;
  padding: 10px 20px 0;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.03);
  margin-bottom: 20px;
}

:deep(.el-tabs__nav-wrap::after) {
  height: 1px;
  background-color: #f2f3f5;
}

:deep(.el-tabs__item) {
  font-size: 15px;
  height: 48px;
  line-height: 48px;
  color: #606266;
}

:deep(.el-tabs__item.is-active) {
  color: #626aef; /* 主色调 */
  font-weight: 600;
}

:deep(.el-tabs__active-bar) {
  background-color: #626aef;
  height: 3px;
  border-radius: 2px;
}

/* 订单卡片容器 */
.order-list-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.custom-empty {
  background: #fff;
  border-radius: 12px;
  padding: 40px 0;
}

/* 单个订单卡片 */
.order-card {
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.04);
  overflow: hidden;
  transition: transform 0.2s, box-shadow 0.2s;
  border: 1px solid #ebeef5;
}

.order-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
}

/* 卡片头部 */
.card-header {
  padding: 15px 24px;
  background-color: #fff;
  border-bottom: 1px solid #f7f8fa;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  font-size: 13px;
  color: #909399;
}

.order-label { margin-right: 8px; }
.order-no { color: #303133; font-weight: 600; font-family: monospace; font-size: 14px; margin-right: 15px; }
.create-time { color: #c0c4cc; border-left: 1px solid #ebeef5; padding-left: 15px; }

/* 状态标签样式 */
.status-tag {
  font-size: 13px;
  font-weight: 600;
  padding: 4px 12px;
  border-radius: 20px;
}
.status-0 { color: #f56c6c; background: #fef0f0; } /* 待支付 */
.status-1 { color: #e6a23c; background: #fdf6ec; } /* 待发货 */
.status-2 { color: #626aef; background: #f0f2ff; } /* 进行中 */
.status-3 { color: #67c23a; background: #f0f9eb; } /* 已完成 */
.status-4, .status-6 { color: #909399; background: #f4f4f5; } /* 关闭/退款 */
.status-7 { color: #409eff; background: #ecf5ff; } /* 归还中 */

/* 卡片内容区 */
.card-body {
  padding: 0 24px;
}

.product-item {
  display: flex;
  padding: 20px 0;
  border-bottom: 1px dashed #f0f2f5;
}
.product-item:last-child {
  border-bottom: none;
}

.img-wrapper {
  width: 90px;
  height: 90px;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #f2f3f5;
  background: #fbfbfb;
  flex-shrink: 0;
  margin-right: 16px;
}

.img-wrapper img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.info-wrapper {
  flex: 1;
  display: flex;
  justify-content: space-between;
}

.info-main {
  flex: 1;
  margin-right: 20px;
}

.product-name {
  font-size: 15px;
  color: #303133;
  margin: 0 0 10px 0;
  line-height: 1.4;
  font-weight: 500;
}

.biz-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.biz-tag {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 4px;
  font-weight: 500;
}
.tag-rent { color: #409eff; background: #ecf5ff; border: 1px solid #d9ecff; }
.tag-sale { color: #f56c6c; background: #fef0f0; border: 1px solid #fde2e2; }

.rent-date {
  font-size: 12px;
  color: #909399;
  background: #f5f7fa;
  padding: 2px 8px;
  border-radius: 4px;
}

.price-qty {
  text-align: right;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.price { font-size: 16px; color: #303133; font-weight: 600; margin-bottom: 4px; }
.qty { font-size: 13px; color: #909399; }

.comment-btn-sm {
  margin-top: 8px;
  font-size: 12px;
}

/* 卡片底部 */
.card-footer {
  padding: 15px 24px;
  background: #fdfdfe;
  border-top: 1px solid #f2f3f5;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.total-section {
  font-size: 14px;
  color: #606266;
  display: flex;
  align-items: baseline;
}
.total-section .currency { font-size: 14px; color: #f56c6c; font-weight: bold; margin-left: 5px; }
.total-section .amount { font-size: 22px; color: #f56c6c; font-weight: bold; margin-left: 2px; }

.actions-section {
  display: flex;
  gap: 10px;
}

/* 按钮样式微调 */
.btn-pay {
  background: linear-gradient(135deg, #ff7875 0%, #f56c6c 100%);
  border: none;
  box-shadow: 0 4px 10px rgba(245, 108, 108, 0.3);
}
.btn-pay:hover { opacity: 0.9; }

/* 分页 */
.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}

/* 评价框 */
.comment-product-name {
  background: #f5f7fa;
  padding: 10px;
  border-radius: 6px;
  font-size: 13px;
  color: #606266;
  margin-bottom: 15px;
}

@media (max-width: 768px) {
  .info-wrapper { flex-direction: column; }
  .price-qty { flex-direction: row; justify-content: space-between; align-items: center; margin-top: 10px; }
  .card-footer { flex-direction: column; align-items: flex-end; gap: 10px; }
}
</style>