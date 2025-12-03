<template>
  <div class="app-container" style="padding-top: 60px;">
    <Header />
    <!-- 状态切换栏 -->
    <el-tabs v-model="activeTab" @tab-click="handleTabClick">
      <el-tab-pane label="全部订单" name="all"></el-tab-pane>
      <el-tab-pane label="待支付" name="0"></el-tab-pane>
      <el-tab-pane label="待发货" name="1"></el-tab-pane>
      <el-tab-pane label="进行中" name="2"></el-tab-pane>
      <el-tab-pane label="已完成" name="3"></el-tab-pane>
    </el-tabs>

    <!-- 订单列表 -->
    <div v-loading="loading">
      <div v-if="orderList.length === 0" class="empty-block">
        <el-empty description="暂无订单数据" />
      </div>

      <el-card v-for="order in orderList" :key="order.orderId" shadow="hover" class="order-card">
        <!-- 卡片头部 -->
        <template #header>
          <div class="card-header">
            <span>订单号：{{ order.orderNo }}</span>
            <div>
              <!-- 状态标签 -->
              <el-tag :type="statusTypeMap[order.status]">{{ statusText(order.status) }}</el-tag>
            </div>
          </div>
        </template>

        <!-- 商品明细列表 (正常情况) -->
        <div v-if="order.shopOrderItemList && order.shopOrderItemList.length > 0">
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
              </div>
              <div v-else class="sale-info">
                <span class="tag-sale">购买</span>
              </div>
              <div class="item-price">
                <span>¥{{ item.price }}</span>
                <span class="quantity">x{{ item.quantity }}</span>
              </div>
            </div>
            
            <!-- 评价按钮：仅当 status == 3 显示 -->
            <div class="item-action">
               <el-button 
                 v-if="order.status == '3'" 
                 type="primary" 
                 link 
                 size="small"
                 icon="ChatDotRound" 
                 @click="openCommentDialog(order, item)"
               >
                 评价
               </el-button>
            </div>
          </div>
        </div>

        <!-- 数据异常兜底显示：如果没有商品明细，显示通用占位 -->
        <div v-else class="order-item">
           <div class="item-info">
             <div class="item-name" style="color: #909399; font-style: italic;">
               (暂无商品明细数据)
             </div>
           </div>
           <div class="item-action">
             <!-- 即使没数据，如果是已完成状态，允许对订单整体进行评价 -->
             <el-button 
               v-if="order.status == '3'" 
               type="primary" 
               link 
               size="small"
               icon="ChatDotRound" 
               @click="openCommentDialog(order, { productId: 0, productName: '整单评价' })"
             >
               评价
             </el-button>
           </div>
        </div>

        <!-- 底部合计与操作 -->
        <div class="card-footer">
          <div class="total-amount">
            实付: <span class="price-text">¥{{ order.totalAmount }}</span>
          </div>
          <div class="action-btns">
            <el-button v-if="order.status == '0'" type="danger" size="small" @click="handlePay(order)">去支付</el-button>
            
            <!-- 购买且已发货 -> 确认收货 -->
            <el-button 
                v-if="order.status == '2' && !isRentOrder(order)" 
                type="primary" size="small" 
                @click="handleConfirm(order)"
            >确认收货</el-button>
            
            <!-- 租赁且进行中 -> 申请归还 -->
            <el-button 
                v-if="order.status == '2' && isRentOrder(order)" 
                type="warning" size="small" 
                @click="handleReturn(order)"
            >申请归还</el-button>
            
            <el-button v-if="order.status == '4'" type="info" plain size="small" disabled>归还中(待商家确认)</el-button>
          </div>
        </div>
      </el-card>
    </div>
    
    <!-- 评价对话框 -->
    <el-dialog v-model="commentOpen" title="发表评价" width="500px" append-to-body>
      <el-form :model="commentForm" label-width="80px">
        <el-form-item label="评分">
          <el-rate v-model="commentForm.star" />
        </el-form-item>
        <el-form-item label="评价内容">
          <el-input v-model="commentForm.content" type="textarea" rows="4" placeholder="请输入您的使用体验..." />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="commentOpen = false">取 消</el-button>
          <el-button type="primary" @click="submitComment">确 定</el-button>
        </div>
      </template>
    </el-dialog>

    <pagination v-show="total>0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import request from '@/utils/request';
import { ElMessage, ElMessageBox } from 'element-plus';
import { parseTime } from '@/utils/ruoyi';
import Header from '@/views/computerMarket/Header.vue';

const router = useRouter();
const route = useRoute();
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
  star: 5,
  content: ''
});

const statusTypeMap = { '0':'danger', '1':'warning', '2':'primary', '3':'success', '4':'info' };

// 辅助函数：获取状态文本
function statusText(status) {
  const map = { '0':'待支付', '1':'待发货', '2':'进行中', '3':'已完成', '4':'归还中' };
  return map[String(status)] || status;
}

// 判断是否包含租赁商品
const isRentOrder = (order) => {
  return order.shopOrderItemList && order.shopOrderItemList.some(item => item.businessType === '1');
};

function getList() {
  loading.value = true;
  
  // 【调试日志】打印查询参数
  console.log('【前端订单列表】查询参数:', JSON.parse(JSON.stringify(queryParams)));
  
  request({ url: '/app/order/list', method: 'get', params: queryParams }).then(res => {
    // 【调试日志】打印返回的订单数据
    console.log('【前端订单列表】返回数据:', res);
    console.log('【前端订单列表】订单数量:', res.rows ? res.rows.length : 0);
    
    if (res.rows && res.rows.length > 0) {
      res.rows.forEach((order, index) => {
        console.log(`【前端订单列表】订单 ${index} - orderId: ${order.orderId}, 状态: ${order.status}, 商品数量: ${order.shopOrderItemList ? order.shopOrderItemList.length : 0}`);
        if (order.shopOrderItemList && order.shopOrderItemList.length > 0) {
          order.shopOrderItemList.forEach((item, itemIndex) => {
            console.log(`【前端订单列表】  商品 ${itemIndex} - productId: ${item.productId}, productName: ${item.productName}`);
          });
        }
      });
    }
    
    orderList.value = res.rows;
    total.value = res.total;
    loading.value = false;
  }).catch(error => {
    console.error('【前端订单列表】查询失败:', error);
    loading.value = false;
  });
}

function handleTabClick(tab) {
  queryParams.status = tab.props.name === 'all' ? undefined : tab.props.name;
  queryParams.pageNum = 1;
  
  // 【调试日志】打印标签切换信息
  console.log('【前端标签切换】切换到:', tab.props.name, '查询状态:', queryParams.status);
  
  getList();
}

function handlePay(order) {
  router.push({ path: '/portal/trade/pay', query: { orderNo: order.orderNo, amount: order.totalAmount } });
}

function handleConfirm(order) {
  ElMessageBox.confirm('确认收到商品吗?', '提示', { type: 'success' }).then(() => {
    request({ url: `/app/order/confirm/${order.orderNo}`, method: 'put' }).then(() => {
      ElMessage.success('交易完成');
      getList();
    });
  });
}

function handleReturn(order) {
  ElMessageBox.confirm('确认已将设备寄回/归还给商家吗?', '归还确认', { type: 'warning' }).then(() => {
    request({ url: `/app/order/return/${order.orderId}`, method: 'put' }).then(() => {
      ElMessage.success('归还申请已提交，等待商家确认');
      getList();
    });
  });
}

function openCommentDialog(order, item) {
  // 【调试日志】打印打开评论对话框时的数据
  console.log('【前端评论】打开评论对话框 - 订单数据:', order);
  console.log('【前端评论】打开评论对话框 - 商品数据:', item);
  console.log('【前端评论】设置评论表单 - orderId:', order.orderId, 'productId:', item.productId);
  
  commentForm.orderId = order.orderId;
  commentForm.productId = item.productId;
  commentForm.star = 5;
  commentForm.content = '';
  commentOpen.value = true;
}

function submitComment() {
  // 【调试日志】打印提交前的表单数据
  console.log('【前端评论】准备提交评论 - 表单数据:', JSON.parse(JSON.stringify(commentForm)));
  
  if (!commentForm.content) return ElMessage.warning("请输入评价内容");
  request({
    url: '/app/comment/add',
    method: 'post',
    data: commentForm
  }).then(() => {
    ElMessage.success('评价成功');
    commentOpen.value = false;
  }).catch(error => {
    console.error('【前端评论】提交失败:', error);
  });
}

onMounted(() => {
  // 从路由参数中获取activeTab
  const tabIndex = route.query.activeTab;
  if (tabIndex !== undefined) {
    // 设置activeTab
    activeTab.value = tabIndex;
    // 触发对应标签的查询
    handleTabClick({ props: { name: tabIndex } });
  } else {
    // 默认查询全部订单
    getList();
  }
});
</script>

<style scoped lang="scss">
.order-card { margin-bottom: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; font-weight: bold; }
.order-item { display: flex; padding: 10px 0; border-bottom: 1px solid #f0f0f0; align-items: center; }
.item-img { width: 80px; height: 80px; object-fit: cover; border-radius: 4px; margin-right: 15px; }
.item-info { flex: 1; }
.item-name { font-size: 15px; color: #333; margin-bottom: 5px; }
.rent-info .tag-rent { background: #e8f4ff; color: #1890ff; padding: 2px 5px; border-radius: 2px; margin-right: 5px; font-size: 12px; }
.sale-info .tag-sale { background: #fff1f0; color: #f5222d; padding: 2px 5px; border-radius: 2px; font-size: 12px; }
.item-price { display: flex; justify-content: space-between; color: #333; font-weight: 500; }
.item-action { margin-left: 15px; } /* 确保按钮有间距 */
.card-footer { display: flex; justify-content: space-between; align-items: center; margin-top: 10px; padding-top: 10px; border-top: 1px solid #eee; }
.price-text { color: #f56c6c; font-size: 18px; font-weight: bold; }
</style>