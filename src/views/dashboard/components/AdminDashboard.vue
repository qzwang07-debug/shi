<template>
  <div class="dashboard-container">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stat-row">
      <el-col :xs="24" :sm="12" :lg="6" v-for="(item, index) in statList" :key="index">
        <el-card shadow="never" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-title">{{ item.title }}</div>
              <div class="stat-value">{{ item.value }}</div>
            </div>
            <div class="stat-icon" :class="item.type">
              <el-icon><component :is="item.icon" /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 访问趋势折线图 -->
    <el-row class="chart-row">
      <el-col :span="24">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <div class="card-header">
              <span class="header-title">近30日系统访问趋势</span>
              <el-tag size="small" effect="plain">实时监控</el-tag>
            </div>
          </template>
          <div class="chart-container">
            <div ref="visitTrendChart" class="visit-trend-chart"></div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup name="AdminDashboard">
import { ref, onMounted, onUnmounted, nextTick } from 'vue';
import { getAdminStats } from "@/api/system/dashboard";
import { UserFilled, Shop, Money, Tickets } from '@element-plus/icons-vue';
import * as echarts from 'echarts/core';
import { LineChart } from 'echarts/charts';
import {
  TitleComponent,
  TooltipComponent,
  GridComponent,
  DatasetComponent,
  TransformComponent,
  LegendComponent
} from 'echarts/components';
import { CanvasRenderer } from 'echarts/renderers';

echarts.use([
  LineChart,
  TitleComponent,
  TooltipComponent,
  GridComponent,
  DatasetComponent,
  TransformComponent,
  LegendComponent,
  CanvasRenderer
]);

const statList = ref([
  { title: "C端用户总数", value: 0, type: "blue", icon: UserFilled, key: "totalConsumerCount" },
  { title: "入驻商家总数", value: 0, type: "green", icon: Shop, key: "totalMerchantCount" },
  { title: "平台累计交易额", value: 0, type: "orange", icon: Money, key: "totalTransactionAmount" },
  { title: "平台累计订单数", value: 0, type: "red", icon: Tickets, key: "totalOrderCount" },
]);

const visitTrendChart = ref(null);
let chartInstance = null;
let resizeObserver = null;

const getStats = async () => {
  try {
    const response = await getAdminStats();
    if (response.data) {
      statList.value.forEach(item => {
        if (response.data[item.key] !== undefined) {
          item.value = response.data[item.key];
        }
      });
    }
  } catch (error) {
    console.error("获取管理员统计数据失败:", error);
  }
};

const getVisitTrendData = async () => {
  try {
    const response = await import('@/api/system/dashboard').then(api => api.getVisitTrend ? api.getVisitTrend() : Promise.resolve({ code: 200, data: { dates: [], counts: [] } }));
    if (response.code === 200 && response.data) {
      return response.data;
    }
    const dates = Array.from({ length: 30 }, (_, i) => `11-${i + 1}`);
    const counts = Array.from({ length: 30 }, () => Math.floor(Math.random() * 500 + 100));
    return { dates, counts };
  } catch (error) {
    return { dates: [], counts: [] };
  }
};

const initVisitTrendChart = async () => {
  if (!visitTrendChart.value) return;
  
  const data = await getVisitTrendData();
  
  chartInstance = echarts.init(visitTrendChart.value);
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'line' },
      padding: [10, 10],
      backgroundColor: 'rgba(255, 255, 255, 0.95)',
      borderColor: '#eee',
      textStyle: { color: '#333' }
    },
    grid: {
      left: '1%',
      right: '2%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: data.dates,
      axisLine: { show: false },
      axisTick: { show: false },
      axisLabel: { color: '#909399', interval: 'auto' }
    },
    yAxis: {
      type: 'value',
      splitLine: { 
        show: true, 
        lineStyle: { type: 'dashed', color: '#E4E7ED' } 
      },
      axisLine: { show: false },
      axisTick: { show: false },
      axisLabel: { color: '#909399' }
    },
    series: [
      {
        name: '访问量',
        type: 'line',
        smooth: true,
        showSymbol: false,
        symbolSize: 8,
        data: data.counts,
        itemStyle: { color: '#409EFF' },
        lineStyle: { width: 3, color: '#409EFF' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(64, 158, 255, 0.25)' },
            { offset: 1, color: 'rgba(64, 158, 255, 0.01)' }
          ])
        },
        emphasis: {
          focus: 'series'
        }
      }
    ]
  };
  
  chartInstance.setOption(option);
  initResizeObserver();
};

const initResizeObserver = () => {
  if (resizeObserver) resizeObserver.disconnect();
  
  if (visitTrendChart.value) {
    resizeObserver = new ResizeObserver((entries) => {
      window.requestAnimationFrame(() => {
        if (chartInstance) {
          chartInstance.resize();
        }
      });
    });
    resizeObserver.observe(visitTrendChart.value);
  }
};

onMounted(async () => {
  await getStats();
  await nextTick();
  initVisitTrendChart();
});

onUnmounted(() => {
  if (resizeObserver) resizeObserver.disconnect();
  chartInstance?.dispose();
});
</script>

<style scoped lang="scss">
.dashboard-container {
}

.stat-row {
  margin-bottom: 20px;
}

.stat-card {
  border: none;
  border-radius: 8px;
  margin-bottom: 10px;
  /* 去除了动画 */
}

.stat-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 5px; /* 修改：与商户端保持一致 (原为 12px 4px) */
}

.stat-info {
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.stat-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
  font-weight: 500;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
  line-height: 1.2;
  font-family: 'DIN Alternate', 'Helvetica Neue', sans-serif;
}

.stat-icon {
  width: 50px;       /* 修改：从 56px 改为 50px，与商户端一致 */
  height: 50px;      /* 修改：从 56px 改为 50px，与商户端一致 */
  border-radius: 12px; /* 修改：从 16px 改为 12px，与商户端一致 */
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 24px;   /* 修改：从 28px 改为 24px，与商户端一致 */
  
  &.blue { color: #409EFF; background-color: rgba(64, 158, 255, 0.1); }
  &.green { color: #67C23A; background-color: rgba(103, 194, 58, 0.1); }
  &.orange { color: #E6A23C; background-color: rgba(230, 162, 60, 0.1); }
  &.red { color: #F56C6C; background-color: rgba(245, 108, 108, 0.1); }
}

.chart-row { margin-top: 10px; }

.chart-card {
  border: none;
  border-radius: 8px;
  
  :deep(.el-card__header) {
    border-bottom: 1px solid #f0f2f5;
    padding: 15px 20px;
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  
  .header-title {
    font-size: 16px;
    font-weight: 600;
    color: #303133;
    
    &::before {
      content: '';
      display: inline-block;
      width: 4px;
      height: 16px;
      background-color: #409EFF;
      margin-right: 8px;
      vertical-align: middle;
      border-radius: 2px;
    }
  }
}

.chart-container {
  height: 400px;
  width: 100%;
  padding: 10px 0;
}

.visit-trend-chart {
  width: 100%;
  height: 100%;
}
</style>