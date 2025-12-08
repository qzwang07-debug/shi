<template>
  <div class="app-container">
    <!-- Row 1: 顶部数据统计卡片 -->
    <el-row :gutter="20" class="stat-row">
      <el-col :xs="24" :sm="12" :lg="6" v-for="(item, index) in statList" :key="index">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <!-- 左侧：文字和数字 -->
            <div class="stat-info">
              <div class="stat-title">{{ item.title }}</div>
              <div class="stat-value">
                <!-- 使用 countUp 动画效果的数字占位 -->
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
        <el-card shadow="never" class="chart-card">
          <template #header>
            <div class="chart-header">
              <span class="chart-title">近七日租售趋势</span>
              <el-tag type="info" size="small">数据实时更新</el-tag>
            </div>
          </template>
          
          <!-- 图表占位符 -->
          <div class="echarts-placeholder">
            <el-icon class="placeholder-icon"><DataLine /></el-icon>
            <p>ECharts 图表区域</p>
            <p class="sub-text">（待接入后端数据 API）</p>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { 
  List, 
  Box, 
  Timer, 
  Monitor, 
  DataLine 
} from '@element-plus/icons-vue';

// 顶部统计卡片的数据配置
const statList = ref([
  {
    title: '今日订单',
    value: 12,
    unit: '单',
    icon: List,
    type: 'blue' //用于控制图标背景色
  },
  {
    title: '待发货',
    value: 5,
    unit: '单',
    icon: Box,
    type: 'red'
  },
  {
    title: '租赁中/待归还',
    value: 8,
    unit: '单',
    icon: Timer,
    type: 'orange'
  },
  {
    title: '上架商品总数',
    value: 120,
    unit: '件',
    icon: Monitor,
    type: 'green'
  }
]);
</script>

<style scoped lang="scss">
.app-container {
  padding: 20px;
  background-color: #f5f7fa; /* 浅灰底色，凸显卡片 */
  min-height: 100vh;
}

/* --- Row 1: 统计卡片样式 --- */
.stat-row {
  margin-bottom: 20px;
}

.stat-card {
  border: none;
  border-radius: 8px;
  margin-bottom: 20px; /* 移动端换行间距 */
  
  :deep(.el-card__body) {
    padding: 20px;
  }
}

.stat-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 10px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.stat-unit {
  font-size: 14px;
  font-weight: normal;
  color: #909399;
  margin-left: 4px;
}

/* 图标区域样式 */
.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  transition: all 0.3s;
}

/* 图标颜色主题 */
.stat-icon.blue {
  color: #409EFF;
  background-color: rgba(64, 158, 255, 0.1);
}
.stat-icon.red {
  color: #F56C6C;
  background-color: rgba(245, 108, 108, 0.1);
}
.stat-icon.orange {
  color: #E6A23C;
  background-color: rgba(230, 162, 60, 0.1);
}
.stat-icon.green {
  color: #67C23A;
  background-color: rgba(103, 194, 58, 0.1);
}

.stat-card:hover .stat-icon {
  transform: scale(1.1); /* 鼠标悬浮微效 */
}

/* --- Row 2: 图表卡片样式 --- */
.chart-card {
  border: none;
  border-radius: 8px;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  border-left: 4px solid #409EFF;
  padding-left: 10px;
}

/* 占位符样式 */
.echarts-placeholder {
  height: 350px;
  background-color: #f8f9fa;
  border-radius: 4px;
  border: 2px dashed #e4e7ed;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: #909399;
  transition: background-color 0.3s;
}

.echarts-placeholder:hover {
  background-color: #f2f6fc;
  border-color: #c0c4cc;
}

.placeholder-icon {
  font-size: 48px;
  margin-bottom: 15px;
  color: #c0c4cc;
}

.sub-text {
  font-size: 12px;
  margin-top: 5px;
  opacity: 0.7;
}
</style>