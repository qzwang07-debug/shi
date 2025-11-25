<template>
  <div class="app-container home">
    <el-row :gutter="20">
      <el-col :sm="24" :lg="24">
        <hr />
      </el-col>
    </el-row>
    
    <el-row :gutter="20">
      <el-col :sm="24" :lg="12" style="padding-left: 20px">
        <h2>电脑租售与硬件性能智能评估平台</h2>
        <p>
          随着数字化转型加速，企业短期办公、个人临时用机需求激增，电脑租售市场规模持续扩大，但行业现存诸多痛点：传统硬件性能评估依赖人工检测效率低下；租售流程以线下操作为主，存在资产管理混乱问题在此背景下，构建“智能评估-线上租售-动态管理”一体化平台，成为解决行业痛点的关键。
        </p>
        
        <p>
          <el-button
            type="primary"
            icon="Cloudy"
            plain
            @click="goTarget('https://gitee.com/y_project/RuoYi-Vue')"
          >
            访问前台
          </el-button>
          
        </p>
      </el-col>
    </el-row>
    
    <el-divider />
    
    <el-row :gutter="20">
      <el-col :xs="24" :sm="24" :md="12" :lg="8">
        <el-card class="update-log">
          <template #header>
            <div class="clearfix">
              <span>联系信息</span>
            </div>
          </template>
          <div class="body">
            <p>
              <i class="el-icon-s-promotion"></i> QQ： 1179630166 <span style="margin-left: 10px;">手机号：13763837981</span>
              
              
            </p>
            
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
const goTarget = (url) => {
  window.open(url, '_blank')
}
</script>

<style scoped>
.home {
  padding: 20px;
}

.update-log {
  margin-bottom: 20px;
}

.body p {
  margin-bottom: 10px;
}

.el-col {
  margin-bottom: 20px;
}
</style>