<template>
  <div class="app-container pay-container">
    <el-card class="pay-card">
      <template #header>
        <div class="card-header">
          <span class="title">收银台</span>
          <span class="tips">请在 15:00 分钟内完成支付</span>
        </div>
      </template>

      <div class="pay-content">
        <div class="order-info">
          <div class="info-item">
            <span class="label">订单编号：</span>
            <span class="value">{{ orderNo }}</span>
          </div>
          <div class="info-item">
            <span class="label">支付金额：</span>
            <span class="amount">¥ {{ amount }}</span>
          </div>
        </div>

        <el-divider />

        <div class="pay-type-select">
          <div 
            class="type-item" 
            :class="{ active: payType === 'wechat' }"
            @click="payType = 'wechat'"
          >
            <svg-icon icon-class="wechat" class="pay-icon" />
            <span>微信支付</span>
          </div>
          <div 
            class="type-item" 
            :class="{ active: payType === 'alipay' }"
            @click="payType = 'alipay'"
          >
            <svg-icon icon-class="money" class="pay-icon" /> <span>支付宝</span>
          </div>
        </div>

        <div class="qr-section">
          <div class="qr-box">
            <img :src="handleImageUrl('@/assets/images/pay.png')" alt="支付二维码" class="qr-img" />
            <div class="qr-mask" v-if="loading">
              <el-icon class="is-loading"><Loading /></el-icon>
              <p>支付处理中...</p>
            </div>
          </div>
          <p class="qr-desc">请使用 {{ payType === 'wechat' ? '微信' : '支付宝' }} 扫一扫</p>
          <p class="qr-desc-sub">二维码仅为模拟演示，点击下方按钮直接完成</p>
        </div>

        <div class="action-box">
          <el-button 
            type="success" 
            size="large" 
            class="pay-btn"
            :loading="loading"
            @click="handleSimulatePay"
          >
            模拟支付成功
          </el-button>
          <el-button text @click="cancelPay">稍后支付</el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import request from '@/utils/request'; // 直接使用request调用，也可封装到api文件
import { ElMessage } from 'element-plus';
import { Loading } from '@element-plus/icons-vue';
import { getAppToken } from '@/utils/auth';
import { handleImageUrl } from '@/utils/ruoyi';

const route = useRoute();
const router = useRouter();

// 响应式数据
const orderNo = route.query.orderNo;
const amount = route.query.amount;
const loading = ref(false);
const payType = ref('wechat'); // 默认微信


/** 模拟支付处理 */
function handleSimulatePay() {
  // 检查登录状态
  const token = getAppToken();
  if (!token) {
    ElMessage.error("请先登录");
    router.push('/portal/login?redirect=' + encodeURIComponent(route.fullPath));
    return;
  }

  loading.value = true;
  
  // 模拟网络延迟 1秒，增加真实感
  setTimeout(() => {
    request({
      url: `/app/order/pay/${route.query.orderNo}`,
      method: 'put',
      headers: {
        'Authorization': 'Bearer ' + token
      }
    }).then(res => {
      loading.value = false;
      ElMessage.success('恭喜您，支付成功！');
      
      // 跳转回我的订单页
      router.push({
        path: '/portal/user/order',
        query: { tab: '1' }
      });
    }).catch((error) => {
      loading.value = false;
      if (error && error.toString().includes('认证失败')) {
        ElMessage.error("登录状态已过期，请重新登录");
        router.push('/portal/login?redirect=' + encodeURIComponent(route.fullPath));
      }
    });
  }, 1000);
}

/** 取消/稍后支付 */
function cancelPay() {
  // 路由到订单列表页面，并自动显示"待支付"标签页
  router.push({
    path: '/portal/user/order',
    query: { tab: '0' }
  });
}
</script>

<style scoped lang="scss">
.pay-container {
  display: flex;
  justify-content: center;
  padding-top: 50px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 84px);

  .pay-card {
    width: 600px;
    
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      .title {
        font-size: 18px;
        font-weight: bold;
      }
      .tips {
        font-size: 13px;
        color: #f56c6c;
      }
    }

    .pay-content {
      padding: 20px;

      .order-info {
        text-align: center;
        margin-bottom: 30px;
        
        .info-item {
          margin-bottom: 10px;
          .label {
            color: #606266;
          }
          .value {
            font-weight: 500;
          }
          .amount {
            font-size: 28px;
            color: #f56c6c;
            font-weight: bold;
          }
        }
      }

      .pay-type-select {
        display: flex;
        justify-content: center;
        gap: 20px;
        margin: 30px 0;

        .type-item {
          width: 140px;
          height: 50px;
          border: 1px solid #dcdfe6;
          border-radius: 4px;
          display: flex;
          align-items: center;
          justify-content: center;
          cursor: pointer;
          transition: all 0.3s;

          .pay-icon {
            font-size: 20px;
            margin-right: 8px;
          }

          &:hover {
            border-color: #409eff;
            color: #409eff;
          }

          &.active {
            border-color: #409eff;
            background-color: #ecf5ff;
            color: #409eff;
          }
        }
      }

      .qr-section {
        text-align: center;
        margin-bottom: 30px;

        .qr-box {
          position: relative;
          width: 200px;
          height: 200px;
          margin: 0 auto 15px;
          border: 1px solid #ebeef5;
          padding: 10px;
          border-radius: 8px;

          .qr-img {
            width: 100%;
            height: 100%;
          }

          .qr-mask {
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(255, 255, 255, 0.9);
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
            color: #409eff;
          }
        }

        .qr-desc {
          font-size: 14px;
          color: #303133;
          margin-bottom: 5px;
        }
        .qr-desc-sub {
          font-size: 12px;
          color: #909399;
        }
      }

      .action-box {
        text-align: center;
        .pay-btn {
          width: 200px;
          margin-right: 15px;
        }
      }
    }
  }
}
</style>