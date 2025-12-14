<template>
  <div class="dashboard-container">
    <!-- Row 1: 顶部数据统计卡片 -->
    <el-row :gutter="20" class="stat-row">
      <el-col :xs="24" :sm="12" :lg="6" v-for="(item, index) in statList" :key="index">
        <!-- 修改：shadow="never" 去除阴影 -->
        <el-card shadow="never" class="stat-card">
          <div class="stat-content">
            <!-- 左侧：文字和数字 -->
            <div class="stat-info">
              <div class="stat-title">{{ item.title }}</div>
              <div class="stat-value">
                {{ item.value }}
                <span class="stat-unit">{{ item.unit }}</span>
              </div>
            </div>
            <!-- 右侧：图标 -->
            <div class="stat-icon" :class="item.type">
              <el-icon><component :is="item.icon" /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- Row 2: 图表区域 -->
    <el-row>
      <el-col :span="24">
        <!-- 修改：shadow="never" 去除阴影 -->
        <el-card shadow="never" class="chart-card">
          <template #header>
            <div class="chart-header">
              <span class="chart-title">近七日趋势分析</span>
              <!-- 单选框：切换销售/租赁 -->
              <el-radio-group v-model="chartRadio" size="small" @change="handleRadioChange">
                <el-radio-button label="销售" />
                <el-radio-button label="租赁" />
              </el-radio-group>
            </div>
          </template>
          <!-- ECharts 容器 -->
          <div 
            ref="chartRef" 
            style="height: 350px; width: 100%;" 
            v-loading="chartLoading"
          ></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, markRaw } from 'vue';
import { getDashboardStats, getDashboardTrend } from "@/api/merchant/dashboard";
import * as echarts from 'echarts';

// 状态定义
const chartRef = ref(null);
let chartInstance = null;
const chartRadio = ref('销售');
const chartLoading = ref(false);
const serverTrendData = ref({ categories: [], salesData: [], rentalData: [] });
const statList = ref([
  { title: "今日订单", key: "todayOrderCount", value: 0, unit: "单", type: "blue", icon: "Tickets" },
  { title: "待发货", key: "pendingShipmentCount", value: 0, unit: "单", type: "red", icon: "Box" },
  { title: "租赁中", key: "rentingCount", value: 0, unit: "单", type: "orange", icon: "Timer" },
  { title: "商品总数", key: "totalProductCount", value: 0, unit: "件", type: "green", icon: "Goods" },
]);

const getStats = async () => {
  try {
    // 模拟数据或调用真实接口
    const response = await getDashboardStats();
    if (response && response.data) {
      statList.value.forEach(item => {
        if (response.data[item.key] !== undefined) item.value = response.data[item.key];
      });
    }
  } catch (error) { console.error(error); }
};

const getTrend = async () => {
  chartLoading.value = true;
  try {
    const res = await getDashboardTrend();
    if (res && res.code === 200 && res.data) {
      serverTrendData.value = {
        categories: res.data.categories || [],
        salesData: res.data.salesData || [],
        rentalData: res.data.rentalData || []
      };
      updateChartOption();
    }
  } catch (err) { console.error(err); } finally { chartLoading.value = false; }
};

const initChart = () => {
  if (chartRef.value) {
    chartInstance = markRaw(echarts.init(chartRef.value));
    updateChartOption();
    // 使用 ResizeObserver 替代 window resize 以支持侧边栏伸缩
    initResizeObserver();
  }
};

let resizeObserver = null;
const initResizeObserver = () => {
  if (resizeObserver) resizeObserver.disconnect();
  if (chartRef.value) {
    resizeObserver = new ResizeObserver(() => {
       chartInstance?.resize();
    });
    resizeObserver.observe(chartRef.value);
  }
}

const hexToRgba = (hex, opacity) => {
  return `rgba(${parseInt("0x" + hex.slice(1, 3))},${parseInt("0x" + hex.slice(3, 5))},${parseInt("0x" + hex.slice(5, 7))},${opacity})`;
}

const updateChartOption = () => {
  if (!chartInstance) return;
  const isSales = chartRadio.value === '销售';
  const currentSeriesData = isSales ? serverTrendData.value.salesData : serverTrendData.value.rentalData;
  const color = isSales ? '#409EFF' : '#E6A23C';

  const option = {
    tooltip: { trigger: 'axis', axisPointer: { type: 'cross' }, padding: [5, 10] },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: serverTrendData.value.categories,
      axisTick: { show: false },
      axisLine: { show: false },
      axisLabel: { color: '#606266' }
    },
    yAxis: {
      type: 'value',
      axisTick: { show: false },
      axisLine: { show: false },
      splitLine: { lineStyle: { type: 'dashed', color: '#E0E3E9' } },
      minInterval: 1
    },
    series: [
      {
        name: chartRadio.value + '量',
        type: 'line',
        smooth: true,
        showSymbol: false,
        lineStyle: { width: 2, color: color },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: color },
            { offset: 1, color: hexToRgba(color, 0.1) }
          ]),
          opacity: 0.8
        },
        data: currentSeriesData,
        animationDuration: 1000,
        animationEasing: 'cubicInOut'
      }
    ]
  };
  chartInstance.setOption(option);
};

const handleRadioChange = () => { updateChartOption(); };

onMounted(() => { getStats(); initChart(); getTrend(); });
onBeforeUnmount(() => { 
  if (resizeObserver) resizeObserver.disconnect();
  chartInstance?.dispose(); 
});
</script>

<style scoped lang="scss">
.dashboard-container {
  /* 基础容器 */
}

.stat-row { margin-bottom: 20px; }

/* 修改：移除了 transition, hover, transform 等动画样式 */
.stat-card {
  border: none;
  border-radius: 8px;
  margin-bottom: 10px;
}

.stat-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 5px;
}

.stat-info { display: flex; flex-direction: column; }
.stat-title { font-size: 14px; color: #909399; margin-bottom: 8px; }
.stat-value { font-size: 28px; font-weight: bold; color: #303133; }
.stat-unit { font-size: 12px; font-weight: normal; color: #909399; margin-left: 4px; }

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  /* 移除了 transition */
  
  &.blue { color: #409EFF; background-color: rgba(64, 158, 255, 0.1); }
  &.red { color: #F56C6C; background-color: rgba(245, 108, 108, 0.1); }
  &.orange { color: #E6A23C; background-color: rgba(230, 162, 60, 0.1); }
  &.green { color: #67C23A; background-color: rgba(103, 194, 58, 0.1); }
}

/* 移除了 hover 放大效果 */

.chart-card { border: none; border-radius: 8px; }
.chart-header { display: flex; justify-content: space-between; align-items: center; }
.chart-title { font-size: 16px; font-weight: bold; color: #303133; }
</style>