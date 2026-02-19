<template>
  <Header />
  <div class="app-container">
    <el-row :gutter="24">
      <el-col :span="14" :xs="24" class="left-panel">
        <el-card class="modern-card main-config-card">
          <template #header>
            <div class="card-header">
              <div class="header-title">
                <el-icon class="header-icon"><Monitor /></el-icon>
                <span>DIY 电脑配置单</span>
              </div>
              <div class="header-actions">
                <el-button text bg @click="resetForm">
                  <el-icon><RefreshRight /></el-icon> 重置
                </el-button>
                <el-button type="primary" color="#6366f1" class="assess-btn" @click="handleAssess">
                  <el-icon><DataAnalysis /></el-icon> 立即评估
                </el-button>
              </div>
            </div>
          </template>
          
          <el-form ref="formRef" :model="form" :rules="rules" label-width="60px" size="default" class="config-form">
            
            <div class="title-section">
              <el-form-item label="标题" prop="title" label-width="50px">
                <el-input 
                  v-model="form.title" 
                  placeholder="给你的爱机起个名字（例如：白色海景房 4090版）" 
                  class="title-input"
                >
                  <template #prefix><el-icon><Edit /></el-icon></template>
                </el-input>
              </el-form-item>
            </div>

            <div class="section-divider">
              <span class="divider-text">核心组件 Core Components</span>
              <span class="divider-line"></span>
            </div>

            <!-- CPU Row -->
            <div class="hardware-row-wrapper">
              <el-row :gutter="12" class="hardware-row" align="middle">
                <el-col :span="2" class="row-icon">
                  <el-tag effect="dark" type="primary" round>CPU</el-tag>
                </el-col>
                <el-col :span="11">
                  <el-form-item prop="cpuModel" label-width="0" class="mb-0">
                    <el-select 
                      v-model="activeHardware.cpu"
                      value-key="id"
                      filterable 
                      placeholder="选择处理器型号 (支持搜索)" 
                      style="width: 100%"
                      @change="handleCpuChange"
                      popper-class="hardware-select-dropdown"
                    >
                      <el-option
                        v-for="item in cpuOptions"
                        :key="item.id"
                        :label="item.model"
                        :value="item"
                      >
                        <div class="option-item">
                          <span class="option-main">{{ item.model }}</span>
                          <span class="option-sub">{{ item.socketType }} · {{ item.tdp }}W</span>
                        </div>
                      </el-option>
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="4" class="info-col" style="text-align: center;">
                   <el-tag v-if="form.cpuTdp" type="warning" effect="plain" size="small" round><el-icon><Lightning /></el-icon> {{ form.cpuTdp }}W</el-tag>
                </el-col>
                <el-col :span="5">
                  <el-form-item prop="cpuPrice" label-width="0" class="mb-0">
                    <el-input-number v-model="form.cpuPrice" :min="0" :precision="2" :controls="false" style="width: 100%" placeholder="价格" class="price-input">
                       <template #prefix>¥</template>
                    </el-input-number>
                  </el-form-item>
                </el-col>
                <el-col :span="2" class="status-col">
                  <StatusIcon :issues="getIssues('cpu')" />
                </el-col>
              </el-row>
            </div>

            <!-- Mobo Row (Merged Brand/Series) -->
            <div class="hardware-row-wrapper">
              <el-row :gutter="12" class="hardware-row" align="middle">
                 <el-col :span="2" class="row-icon">
                  <el-tag effect="dark" type="warning" round>主板</el-tag>
                </el-col>
                <el-col :span="9">
                  <el-form-item prop="moboBrand" label-width="0" class="mb-0">
                    <el-input v-model="form.moboBrand" placeholder="具体型号 (如: 华硕 TUF B760M 重炮手)" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item prop="moboModel" label-width="0" class="mb-0">
                    <el-select 
                      v-model="activeHardware.mobo"
                      value-key="id" 
                      filterable 
                      default-first-option
                      placeholder="芯片组" 
                      style="width: 100%"
                      @change="handleMoboChange"
                      popper-class="hardware-select-dropdown"
                    >
                      <el-option
                        v-for="item in moboOptions"
                        :key="item.id"
                        :label="item.chipset"
                        :value="item"
                      >
                         <div class="option-item">
                           <span class="option-main">{{ item.chipset }}</span>
                           <span class="option-sub">{{ item.socketType }} / {{ item.memoryType }}</span>
                         </div>
                      </el-option>
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="5">
                  <el-form-item prop="moboPrice" label-width="0" class="mb-0">
                    <el-input-number v-model="form.moboPrice" :min="0" :precision="2" :controls="false" style="width: 100%" placeholder="价格" class="price-input">
                      <template #prefix>¥</template>
                    </el-input-number>
                  </el-form-item>
                </el-col>
                <el-col :span="2" class="status-col">
                  <StatusIcon :issues="getIssues('mobo')" />
                </el-col>
              </el-row>
            </div>

            <!-- RAM Row -->
            <div class="hardware-row-wrapper">
              <el-row :gutter="12" class="hardware-row" align="middle">
                 <el-col :span="2" class="row-icon">
                  <el-tag effect="dark" type="danger" round>内存</el-tag>
                </el-col>
                <el-col :span="5">
                  <el-form-item prop="ramBrand" label-width="0" class="mb-0">
                    <el-input v-model="form.ramBrand" placeholder="品牌型号" />
                  </el-form-item>
                </el-col>
                <el-col :span="3">
                  <el-form-item label-width="0" class="mb-0">
                    <el-select v-model="form.ramInterface" placeholder="代数" @change="handleRamTypeChange">
                      <el-option label="DDR4" value="DDR4" />
                      <el-option label="DDR5" value="DDR5" />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="4">
                  <el-form-item label-width="0" class="mb-0">
                    <el-select v-model="form.ramFrequency" filterable placeholder="频率" :disabled="!form.ramInterface">
                      <el-option
                        v-for="item in filteredRamOptions"
                        :key="item.id"
                        :label="item.frequency + ' MHz'"
                        :value="item.frequency.toString()"
                      />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="3">
                  <el-form-item label-width="0" class="mb-0">
                     <el-input v-model="form.ramCapacity" placeholder="容量(16G*2)" />
                  </el-form-item>
                </el-col>
                <el-col :span="5">
                  <el-form-item label-width="0" class="mb-0">
                    <el-input-number v-model="form.ramPrice" :min="0" :precision="2" :controls="false" style="width: 100%" placeholder="价格" class="price-input">
                      <template #prefix>¥</template>
                    </el-input-number>
                  </el-form-item>
                </el-col>
                <el-col :span="2" class="status-col">
                  <StatusIcon :issues="getIssues('ram')" />
                </el-col>
              </el-row>
            </div>

            <!-- GPU Row (Merged Brand/Series) -->
            <div class="hardware-row-wrapper">
              <el-row :gutter="12" class="hardware-row" align="middle">
                 <el-col :span="2" class="row-icon">
                  <el-tag effect="dark" type="success" round>显卡</el-tag>
                </el-col>
                <el-col :span="9">
                  <el-form-item prop="gpuBrand" label-width="0" class="mb-0">
                    <el-input v-model="form.gpuBrand" placeholder="具体型号 (如: 七彩虹 iGame Ultra W)" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item prop="gpuModel" label-width="0" class="mb-0">
                    <el-select 
                      v-model="activeHardware.gpu"
                      value-key="id" 
                      filterable 
                      placeholder="核心型号" 
                      style="width: 100%"
                      @change="handleGpuChange"
                      popper-class="hardware-select-dropdown"
                    >
                      <el-option
                        v-for="item in gpuOptions"
                        :key="item.id"
                        :label="item.model"
                        :value="item"
                      >
                        <div class="option-item">
                           <span class="option-main">{{ item.model }}</span>
                           <span class="option-sub">{{ item.tdp }}W</span>
                        </div>
                      </el-option>
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="5">
                  <el-form-item prop="gpuPrice" label-width="0" class="mb-0">
                    <el-input-number v-model="form.gpuPrice" :min="0" :precision="2" :controls="false" style="width: 100%" placeholder="价格" class="price-input">
                      <template #prefix>¥</template>
                    </el-input-number>
                  </el-form-item>
                </el-col>
                 <el-col :span="2" class="status-col">
                </el-col>
              </el-row>
            </div>

            <div class="section-divider">
              <span class="divider-text">存储与机电 Storage & Power</span>
              <span class="divider-line"></span>
            </div>

            <div class="hardware-row-wrapper">
              <el-row :gutter="12" class="hardware-row" align="middle">
                <el-col :span="2" class="row-icon">
                  <el-icon><WindPower /></el-icon>
                </el-col>
                <el-col :span="9">
                   <el-form-item prop="coolerFullName" label-width="0" class="mb-0">
                    <el-input v-model="form.coolerFullName" placeholder="散热器型号">
                      <template #append>
                        <el-input-number v-model="form.coolerPrice" :min="0" :precision="0" :controls="false" style="width: 60px;" placeholder="¥" />
                      </template>
                    </el-input>
                  </el-form-item>
                </el-col>
                 <el-col :span="2" class="row-icon" style="text-align: right;">
                  <el-icon><Cpu /></el-icon>
                </el-col>
                <el-col :span="9">
                  <el-form-item prop="ssdFullName" label-width="0" class="mb-0">
                    <el-input v-model="form.ssdFullName" placeholder="固态硬盘型号">
                      <template #append>
                        <el-input-number v-model="form.ssdPrice" :min="0" :precision="0" :controls="false" style="width: 60px;" placeholder="¥" />
                      </template>
                    </el-input>
                  </el-form-item>
                </el-col>
                 <el-col :span="2" class="status-col">
                  <StatusIcon :issues="getIssues('cooler')" />
                </el-col>
              </el-row>
            </div>

            <!-- PSU Row (Merged Brand/Series) -->
            <div class="hardware-row-wrapper">
              <el-row :gutter="12" class="hardware-row" align="middle">
                 <el-col :span="2" class="row-icon">
                  <el-tag effect="light" type="warning" round>电源</el-tag>
                </el-col>
                <el-col :span="9">
                  <el-form-item prop="psuBrand" label-width="0" class="mb-0">
                    <el-input v-model="form.psuBrand" placeholder="具体型号 (如: 长城 X6 金牌全模组)" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item prop="psuWattage" label-width="0" class="mb-0">
                    <el-select 
                      v-model="activeHardware.psu"
                      value-key="id" 
                      filterable 
                      placeholder="额定功率" 
                      style="width: 100%"
                      @change="handlePsuChange"
                    >
                      <el-option
                        v-for="item in psuOptions"
                        :key="item.id"
                        :label="item.wattage + ' W'"
                        :value="item"
                      />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="5">
                  <el-form-item prop="psuPrice" label-width="0" class="mb-0">
                    <el-input-number v-model="form.psuPrice" :min="0" :precision="2" :controls="false" style="width: 100%" placeholder="价格" class="price-input">
                      <template #prefix>¥</template>
                    </el-input-number>
                  </el-form-item>
                </el-col>
                 <el-col :span="2" class="status-col">
                  <StatusIcon :issues="getIssues('psu')" />
                </el-col>
              </el-row>
            </div>

            <div class="hardware-row-wrapper">
              <el-row :gutter="12" class="hardware-row" align="middle">
                <el-col :span="2" class="row-icon">
                   <el-icon><Box /></el-icon>
                </el-col>
                <el-col :span="9">
                  <el-form-item prop="caseFullName" label-width="0" class="mb-0">
                    <el-input v-model="form.caseFullName" placeholder="机箱型号">
                      <template #append>
                        <el-input-number v-model="form.casePrice" :min="0" :precision="0" :controls="false" style="width: 60px;" placeholder="¥" />
                      </template>
                    </el-input>
                  </el-form-item>
                </el-col>
                <el-col :span="2" class="row-icon" style="text-align: right;">
                   <el-icon><Help /></el-icon>
                </el-col>
                <el-col :span="9">
                  <el-form-item prop="fanFullName" label-width="0" class="mb-0">
                    <el-input v-model="form.fanFullName" placeholder="风扇/其他">
                      <template #append>
                        <el-input-number v-model="form.fanPrice" :min="0" :precision="0" :controls="false" style="width: 60px;" placeholder="¥" />
                      </template>
                    </el-input>
                  </el-form-item>
                </el-col>
              </el-row>
             </div>

            <div class="footer-bar">
               <div class="price-summary">
                 <span class="label">预计总价</span>
                 <span class="currency">¥</span>
                 <span class="amount">{{ totalPrice }}</span>
               </div>
               <div class="action-buttons">
                  <el-button size="large" round @click="resetForm">清空</el-button>
                  <el-button type="primary" size="large" color="#6366f1" round @click="submitForm" :loading="loading" class="submit-btn">
                    {{ form.buildId ? '更新配置单' : '保存配置单' }}
                  </el-button>
               </div>
            </div>

            <div class="compatibility-report" :class="{'has-issues': compatibilityIssues.length > 0}">
              <div class="report-header">
                <el-icon :class="compatibilityIssues.length === 0 ? 'text-success' : 'text-warning'">
                  <CircleCheckFilled v-if="compatibilityIssues.length === 0" />
                  <WarningFilled v-else />
                </el-icon>
                <span>兼容性分析</span>
              </div>
              
              <div class="report-content">
                <div v-if="compatibilityIssues.length === 0" class="safe-message">
                  <span class="dot"></span> 硬件配置看起来很棒，没有发现明显的兼容性冲突。
                </div>
                
                <template v-else>
                   <div v-for="(issue, index) in compatibilityIssues" :key="index" class="issue-item-card" :class="issue.type">
                      <el-icon class="issue-icon"><Warning /></el-icon>
                      <span>{{ issue.message }}</span>
                   </div>
                </template>

                <div class="notice-text">
                  * 注：请自行核对机箱是否支持所选散热器高度和显卡长度。
                </div>
              </div>
            </div>

          </el-form>
        </el-card>

        <el-card class="modern-card mt-4 ">
          <template #header>
            <div class="card-header">
              <span><el-icon><List /></el-icon> 历史配置</span>
              <el-button type="primary" link @click="getList">刷新</el-button>
            </div>
          </template>

          <div v-if="!isLogin">
             <el-empty description="登录后查看历史" :image-size="100">
                <el-button type="primary" round @click="$router.push('/login')">去登录</el-button>
             </el-empty>
          </div>
          <div v-else>
            <el-table :data="buildList" style="width: 100%" v-loading="listLoading" :header-cell-style="{background:'#f8f9fa', color:'#606266'}">
              <el-table-column prop="title" label="标题" min-width="150" show-overflow-tooltip>
                <template #default="scope">
                  <span style="font-weight: 500;">{{ scope.row.title }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="cpuModel" label="核心" width="180" show-overflow-tooltip>
                 <template #default="scope">
                   <div class="mini-spec">{{ scope.row.cpuModel }}</div>
                   <div class="mini-spec sub">{{ scope.row.gpuModel }}</div>
                 </template>
              </el-table-column>
              <el-table-column prop="totalPrice" label="总价" width="100">
                <template #default="scope">
                  <span class="price-text">¥{{ scope.row.totalPrice }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="createTime" label="时间" width="120">
                 <template #default="scope">
                   <span style="font-size: 12px; color: #909399;">{{ scope.row.createTime?.substring(0,10) }}</span>
                 </template>
              </el-table-column>
              <el-table-column label="操作" width="120" fixed="right">
                <template #default="scope">
                  <el-button size="small" type="primary" link @click="handleLoadBuild(scope.row)">编辑</el-button>
                  <el-button size="small" type="danger" link @click="handleDeleteBuild(scope.row)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
            <div class="pagination-container">
        <el-pagination
          v-show="total > 0"
          background
          layout="prev, pager, next"
          :total="total"
          :page-size="queryParams.pageSize"
          v-model:current-page="queryParams.pageNum"
          @current-change="handlePageChange"
          @size-change="handleSizeChange"
        />
      </div>
    
          </div>
         
        </el-card>

        <!-- 智能商品推荐模块 -->
        <el-card class="modern-card mt-4 recommend-card">
          <template #header>
            <div class="card-header">
              <span>🎯 性能相近的成品推荐</span>
            </div>
          </template>
          
          <div v-if="recommendList.length === 0" class="empty-state">
            <el-empty description="暂无推荐商品" :image-size="120" />
          </div>
          
          <el-row :gutter="20" v-else>
            <el-col :span="6" v-for="product in recommendList" :key="product.productId">
              <div class="product-card">
                <div class="product-image">
                  <img :src="product.imageUrl" :alt="product.productName" />
                  <div class="product-tag" :class="getProductTagClass(product.productType)">
                    {{ getProductTagText(product.productType) }}
                  </div>
                </div>
                <div class="product-info">
                  <div class="product-name">{{ product.productName }}</div>
                  <div class="product-match-rate">
                    <el-tag :type="getMatchRateType(calculateMatchRate(product.performanceScore, assessResult.totalScore))">
                      {{ calculateMatchRate(product.performanceScore, assessResult.totalScore) }}% 相似
                    </el-tag>
                  </div>
                  <div class="product-hardware">{{ product.cpu }} + {{ product.gpuModel }}</div>
                  <div class="product-price">
                    <template v-if="product.productType === '1'">
                      <span class="currency">¥</span>
                      <span class="amount">{{ product.rentPrice || 0 }}</span>
                      <span class="unit">/天</span>
                    </template>
                    <template v-else-if="product.productType === '3'">
                      <div class="price-row">
                        <span class="price-label">租金:</span>
                        <span class="currency">¥</span>
                        <span class="amount">{{ product.rentPrice || 0 }}</span>
                        <span class="unit">/天</span>
                      </div>
                      <div class="price-row">
                        <span class="price-label">售价:</span>
                        <span class="currency">¥</span>
                        <span class="amount">{{ product.salePrice || 0 }}</span>
                      </div>
                    </template>
                    <template v-else>
                      <span class="currency">¥</span>
                      <span class="amount">{{ product.salePrice || 0 }}</span>
                    </template>
                  </div>
                  <el-button type="primary" size="small" @click="viewProductDetail(product)" class="view-detail-btn">
                  立即支付
                  </el-button>
                </div>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>

      <el-col :span="10" :xs="24" class="right-panel">
        <el-card class="modern-card sticky-card performance-card">
          <template #header>
            <div class="card-header perf-header">
              <span class="title">
                <el-icon><DataLine /></el-icon> 性能预测中心
              </span>
              <el-tag size="small" type="success" effect="plain" round>AI 评估 Beta</el-tag>
            </div>
          </template>
          
          <div v-if="!assessResult" class="empty-state-perf">
            <el-empty description="选择 CPU 和显卡后点击「立即评估」" :image-size="120" />
          </div>

          <div v-else class="performance-panel">
            
            <div class="score-card-main">
              <div class="score-ring-bg"></div>
              <div class="score-content">
                <div class="score-label">综合性能评分</div>
                <div class="score-number">{{ assessResult.totalScore }}</div>
                <div class="score-bar-wrapper">
                   <el-progress 
                    :text-inside="false" 
                    :stroke-width="10" 
                    :percentage="calculatePercentage(assessResult.totalScore, 28387)" 
                    color="#6366f1"
                    :show-text="false"
                  />
                </div>
                <div class="score-desc">击败了全国 {{ calculatePercentage(assessResult.totalScore, 28387) }}% 的用户</div>
              </div>
            </div>

            <!-- 雷达图展示区 -->
            <div class="radar-chart-container" v-if="assessResult">
              <div ref="radarChartRef" class="radar-chart"></div>
            </div>

            <div class="resolution-switch">
              <el-radio-group v-model="currentResolution" size="default" fill="#6366f1">
                <el-radio-button label="1080P">1080P</el-radio-button>
                <el-radio-button label="2K">2K</el-radio-button>
                <el-radio-button label="4K">4K</el-radio-button>
              </el-radio-group>
            </div>

            <!-- 瓶颈警告区域 -->
            <div class="bottleneck-warning" :class="{balanced: bottleneckInfo?.type === 'balanced'}" v-if="bottleneckInfo">
              <div class="warning-header">
                <el-icon :class="bottleneckInfo.type === 'balanced' ? 'text-success' : 'text-warning'">
                  <CircleCheckFilled v-if="bottleneckInfo.type === 'balanced'" />
                  <WarningFilled v-else />
                </el-icon>
                <span class="warning-title">{{ bottleneckInfo.message }}</span>
              </div>
              <div class="warning-detail">
                {{ bottleneckInfo.detail }}
              </div>
            </div>
            
            <!-- 显示器推荐区域 (新增) -->
            <div class="monitor-recommend-section" v-if="monitorRecommendation">
              <div class="section-title"><el-icon><Monitor /></el-icon> 智能显示器推荐</div>
              
              <!-- Best Choice Highlight -->
              <div class="best-choice-card">
                <div class="badge">最佳搭配</div>
                <div class="choice-content">
                   <div class="res-hz">
                     <span class="res">{{ monitorRecommendation.bestChoice.resolution }}</span>
                     <span class="divider">/</span>
                     <span class="hz">{{ monitorRecommendation.bestChoice.refreshRate }}</span>
                   </div>
                   <div class="reason">{{ monitorRecommendation.bestChoice.reason }}</div>
                </div>
              </div>

              <!-- Other Options List -->
              <div class="options-grid">
                 <div v-for="opt in monitorRecommendation.options" :key="opt.resolution" 
                      class="option-item" :class="{active: currentResolution === opt.resolution, best: opt.isBest}"
                      @click="currentResolution = opt.resolution">
                    <div class="opt-header">
                       <span class="opt-res">{{ opt.resolution }}</span>
                       <span class="opt-hz">{{ opt.refreshRate }}</span>
                    </div>
                    <div class="opt-desc">{{ opt.desc }}</div>
                    <div v-if="opt.isBest" class="best-mark">推荐</div>
                 </div>
              </div>
            </div>

            <!-- 性价比展示区域优化 -->
            <div class="cp-section" v-if="cpData">
               <div class="cp-header">
                 <div class="cp-title">
                   <span class="label">性价比评估</span>
                   <el-tooltip content="基于3A大作帧率与总价的换算，并包含性能门槛惩罚机制。" placement="top">
                     <el-icon class="info-icon"><Warning /></el-icon>
                   </el-tooltip>
                 </div>
                 <div class="cp-right">
                    <el-tag size="small" :type="cpData.percentage > 75 ? 'success' : 'warning'" effect="dark">{{ cpData.level }}</el-tag>
                    <span class="cp-value" :style="{color: cpColor}">
                      {{ cpData.displayValue }} <span class="unit">FPS/k¥</span>
                    </span>
                 </div>
               </div>
               <div class="cp-bar">
                 <el-progress 
                    :text-inside="true" 
                    :stroke-width="18" 
                    :percentage="Number(cpData.percentage)" 
                    :color="cpColor"
                  />
               </div>
               <!-- 惩罚提示 -->
               <div v-if="cpData.isPenalty" class="penalty-tip">
                 <el-icon><WarningFilled /></el-icon> 警示：该配置在当前分辨率下难以流畅运行核心3A大作，性价比判定已降级。
               </div>
            </div>

            <div class="games-list">
              <div v-for="(game, index) in assessResult.games" :key="index" class="game-row">
                <div class="game-info">
                  <span class="game-name">{{ game.gameName }}</span>
                  <span class="game-fps">
                     {{ getGameFps(game, currentResolution) }} <span class="fps-unit">FPS</span>
                  </span>
                </div>
                <div class="game-bar">
                  <el-progress
                    :text-inside="false"
                    :stroke-width="8"
                    :percentage="calculatePercentage(getGameFps(game, currentResolution), calculateMaxFps(currentResolution))"
                    :color="getFpsColor(getGameFps(game, currentResolution))"
                    :show-text="false"
                  />
                </div>
                <div class="game-description" v-if="game.description">
                  {{ game.description }}
                </div>
              </div>
            </div>

            <div class="ai-review-section">
              <div class="ai-review-header">
                <div class="ai-review-title">
                  <el-icon><DataAnalysis /></el-icon>
                  <span>AI 实时评估</span>
                </div>
                <div class="ai-style-selector">
                  <el-radio-group v-model="aiStyle" size="small">
                    <el-tooltip v-for="opt in aiStyleOptions" :key="opt.value" :content="opt.desc" placement="top">
                      <el-radio-button :label="opt.value">{{ opt.label }}</el-radio-button>
                    </el-tooltip>
                  </el-radio-group>
                </div>
                <el-button
                  type="primary"
                  size="small"
                  :loading="aiReviewLoading"
                  @click="handleAiReview"
                >
                  {{ aiReviewText }}
                </el-button>
              </div>
              <div v-if="aiReviewTypedContent" class="ai-review-body markdown-body" v-html="aiReviewHtml"></div>
              <el-empty v-else description="点击按钮让 AI 实时点评当前配置" :image-size="80" />
            </div>

             <div class="disclaimer">数据基于理论性能推算，实际游戏受场景影响可能波动 ±10%</div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup name="Build">
import { ref, reactive, computed, onMounted, h, watch, nextTick, onUnmounted } from 'vue';
import { ElMessage, ElTooltip, ElIcon, ElMessageBox } from 'element-plus';
import { Warning, CircleCheck, CircleClose, Monitor, RefreshRight, DataAnalysis, Edit, Lightning, WindPower, Cpu, Box, Help, List, DataLine, CircleCheckFilled, WarningFilled } from '@element-plus/icons-vue';
import { 
  listFrontCpu, 
  listFrontGpu, 
  listFrontMemory, 
  listFrontMotherboard, 
  listFrontPower 
} from '@/api/front/hardware';
import { addUserBuild, listUserBuild, updateUserBuild, delUserBuild } from '@/api/front/userBuild';
import { assessPerformance } from '@/api/front/performance';
import { generateAiReview } from '@/api/system/aiReview';
import { listSimilarProducts } from '@/api/market/product';
import useUserStore from '@/store/modules/user'; 
import { useRouter } from 'vue-router';
import Header from '../Header.vue';
import * as echarts from 'echarts';
import { markRaw } from 'vue';
import { marked } from 'marked';

// ---------------------------------------------------------------------
// 组件：状态图标 (StatusIcon)
// ---------------------------------------------------------------------
const StatusIcon = (props) => {
  const issues = props.issues || [];
  if (issues.length === 0) return null;

  const hasError = issues.some(i => i.type === 'error');
  const color = hasError ? '#f56c6c' : '#e6a23c';
  const icon = hasError ? CircleClose : Warning;
  
  const tooltipContent = issues.map(i => i.message).join('; ');

  return h(ElTooltip, { content: tooltipContent, placement: 'top', effect: 'light' }, () => [
    h(ElIcon, { size: 18, color: color, style: 'cursor: pointer; margin-top: 0px;' }, () => h(icon))
  ]);
};

const router = useRouter();
const userStore = useUserStore();
const loading = ref(false);
const formRef = ref(null);
// 雷达图相关
const radarChartRef = ref(null);
let radarChartInstance = null;

// 登录状态检查
const isLogin = computed(() => {
  const adminToken = !!userStore.token;
  const appToken = localStorage.getItem('app_token');
  return adminToken || !!appToken;
});

// 选项数据
const cpuOptions = ref([]);
const gpuOptions = ref([]);
const ramOptions = ref([]);
const filteredRamOptions = ref([]);
const moboOptions = ref([]);
const psuOptions = ref([]);

// 历史记录数据
const buildList = ref([]);
const total = ref(0);
const listLoading = ref(false);
const queryParams = reactive({
  pageNum: 1,
  pageSize: 5,
  title: undefined
});

// 商品推荐数据
const recommendList = ref([]);
const debounceTimer = ref(null);

// 选中的硬件详细对象
const activeHardware = reactive({
  cpu: null,
  mobo: null,
  gpu: null,
  psu: null
});

// 评估结果
const assessResult = ref(null);
const currentResolution = ref('1080P'); 
const aiReviewLoading = ref(false);
const aiReviewContent = ref('');
const aiReviewTypedContent = ref('');
let aiTypewriterTimer = null;
const aiReviewText = computed(() => aiReviewContent.value ? '重新评估' : 'AI 评估');
const aiReviewHtml = computed(() => {
  if (!aiReviewTypedContent.value) return '';
  return marked(aiReviewTypedContent.value);
});
const aiStyle = ref('fun');
const aiStyleOptions = [
  { value: 'fun', label: '老哥吐槽', desc: '江湖味、接地气' },
  { value: 'expert', label: '严谨专家', desc: '专业、数据导向' },
  { value: 'beginner', label: '小白保姆', desc: '温柔、通俗易懂' }
];

const MAX_FPS_REFERENCE = {
  '1080P': 600, 
  '2K': 500,   
  '4K': 300     
};

const form = reactive({
  buildId: null,
  title: '',
  cpuModel: '', cpuPrice: 0, cpuTdp: 0,
  moboBrand: '', moboModel: '', moboSeries: '', moboPrice: 0,
  ramBrand: '', ramInterface: '', ramFrequency: '', ramCapacity: '', ramPrice: 0,
  gpuBrand: '', gpuModel: '', gpuSeries: '', gpuPrice: 0,
  ssdFullName: '', ssdPrice: 0,
  coolerFullName: '', coolerPrice: 0,
  psuBrand: '', psuWattage: '', psuSeries: '', psuPrice: 0,
  caseFullName: '', casePrice: 0,
  fanFullName: '', fanPrice: 0,
  totalPrice: 0
});

const rules = {
  title: [{ required: true, message: '请输入配置单标题', trigger: 'blur' }],
  cpuModel: [{ required: true, message: '请选择CPU型号', trigger: 'change' }],
};

// ---------------------------------------------------------------------
// 核心逻辑：兼容性检查 (Computed)
// ---------------------------------------------------------------------
const compatibilityIssues = computed(() => {
  const issues = [];
  const { cpu, mobo, gpu, psu } = activeHardware;

  if (cpu && mobo) {
    const cpuSocket = cpu.socketType || cpu.socket_type;
    const moboSocket = mobo.socketType || mobo.socket_type;
    
    if (cpuSocket && moboSocket && cpuSocket !== moboSocket) {
      issues.push({
        type: 'error',
        source: 'cpu',
        message: `CPU接口(${cpuSocket})与主板接口(${moboSocket})不兼容`
      });
      issues.push({ type: 'error', source: 'mobo', message: `主板接口(${moboSocket})与CPU接口(${cpuSocket})不兼容` });
    }
  }

  if (mobo && form.ramInterface) {
    const moboRamType = mobo.memoryType || mobo.memory_type; 
    if (moboRamType && form.ramInterface && moboRamType.toUpperCase() !== form.ramInterface.toUpperCase()) {
      issues.push({
        type: 'error',
        source: 'mobo',
        message: `主板仅支持 ${moboRamType}，当前选择了 ${form.ramInterface}`
      });
      issues.push({
        type: 'error',
        source: 'ram',
        message: `内存类型 ${form.ramInterface} 与主板不兼容`
      });
    }
  }

  if (psu && cpu && gpu) {
    const cpuTdp = Number(cpu.tdp) || 0;
    const gpuTdp = Number(gpu.tdp) || 0;
    const psuWattage = Number(psu.wattage) || 0;
    const basePower = cpuTdp + gpuTdp + 50; 
    
    if (psuWattage < basePower) {
      issues.push({
        type: 'error',
        source: 'psu',
        message: `电源功率不足！估算功耗 ${basePower}W，电源仅 ${psuWattage}W`
      });
    } else if (psuWattage < basePower * 1.25) {
      issues.push({
        type: 'warning',
        source: 'psu',
        message: `电源负载较高（余量不足25%），建议升级电源`
      });
    }
  }

  if (cpu) {
    const cpuTdp = Number(cpu.tdp) || 0;
    if (cpuTdp > 200) {
      issues.push({
        type: 'warning',
        source: 'cooler',
        message: `CPU功耗 ${cpuTdp}W 较高，建议搭配 360 水冷`
      });
      issues.push({ type: 'warning', source: 'cpu', message: `CPU功耗较高，注意散热` });
    }
  }
  return issues;
});

function getIssues(sourceName) {
  return compatibilityIssues.value.filter(i => i.source === sourceName);
}

// ---------------------------------------------------------------------
// 业务逻辑
// ---------------------------------------------------------------------
const totalPrice = computed(() => {
  const price = 
    (Number(form.cpuPrice) || 0) +
    (Number(form.moboPrice) || 0) +
    (Number(form.ramPrice) || 0) +
    (Number(form.gpuPrice) || 0) +
    (Number(form.ssdPrice) || 0) +
    (Number(form.coolerPrice) || 0) +
    (Number(form.psuPrice) || 0) +
    (Number(form.casePrice) || 0) +
    (Number(form.fanPrice) || 0);
  form.totalPrice = price; 
  return price.toFixed(2);
});

// 提取共同的性价比计算逻辑
function calculateOptimizedRatio(resolution) {
  if (!assessResult.value || !assessResult.value.games || !form.totalPrice || form.totalPrice <= 0) return null;
  
  // 1. 计算总帧率
  let totalFps = 0;
  let minFpsInAaa = 999; // 记录3A大作的最低帧，用于熔断机制

  assessResult.value.games.forEach(game => {
    const fps = game.fps[resolution] || 0;
    totalFps += fps;
    
    // 如果是 3A 大作（这里根据名字包含来判断，实际项目中可用 gameType 字段）
    if (game.gameName.includes('黑神话') || game.gameName.includes('赛博朋克') || game.gameName.includes('荒野')) {
        minFpsInAaa = Math.min(minFpsInAaa, fps);
    }
  });

  // 2. 原始性价比 (每千元帧数)
  let ratio = totalFps / form.totalPrice;

  // --- 优化点：性能门槛惩罚 ---
  // 如果在当前分辨率下，核心3A游戏低于 20帧，说明这台机器在这个分辨率下"不可用"
  // 强制降低性价比得分，防止"电子垃圾"因为便宜而获得高分
  if (minFpsInAaa < 20) {
      ratio = ratio * 0.5; // 惩罚系数，直接砍半
  } else if (minFpsInAaa < 40) {
      ratio = ratio * 0.8; // 勉强能玩，小幅惩罚
  }
  
  return {
    ratio: ratio,
    minFpsInAaa: minFpsInAaa,
    totalFps: totalFps
  };
}

// START: 优化 - 性价比计算逻辑 (含性能门槛惩罚)
const cpData = computed(() => {
  if (!assessResult.value || !assessResult.value.games || !form.totalPrice || form.totalPrice <= 0) return null;
  
  const result = calculateOptimizedRatio(currentResolution.value);
  if (!result) return null;
  
  const { ratio, minFpsInAaa } = result;

  // 3. 动态基准值 (每千元多少帧算优秀)
  const referenceMax = {
       '1080P': 0.14,
      '2K': 0.10,
      '4K': 0.05
  }[currentResolution.value] || 0.12;

  const percentage = Math.min((ratio / referenceMax) * 100, 100);

  // 4. 生成评价标签 (加分项)
  let level = '';
  if (percentage > 90) level = '极致性价比';
  else if (percentage > 75) level = '物超所值';
  else if (percentage > 50) level = '价格合理';
  else level = '土豪专属'; // 指溢价高（全是外观件）或配置不合理

  return {
    val: ratio,
    displayValue: (ratio * 1000).toFixed(1), // 每千元能买多少帧
    percentage: percentage.toFixed(0),
    level: level,
    isPenalty: minFpsInAaa < 20 // 前端可以据此显示提示："该配置在当前分辨率下难以流畅运行3A大作"
  };
});

const cpColor = computed(() => {
    if(!cpData.value) return '#909399';
    const p = Number(cpData.value.percentage);
    // 性价比高显示绿色，低显示橙/红
    if(p > 80) return '#13ce66'; 
    if(p > 60) return '#409eff'; 
    return '#f56c6c'; 
});

// START: 新增 - 显示器智能推荐逻辑
const monitorRecommendation = computed(() => {
  if (!assessResult.value || !assessResult.value.games || !form.totalPrice || form.totalPrice <= 0) return null;

  const resolutions = ['1080P', '2K', '4K'];
  // 标准档位列表 (High to Low)
  const standardRates = [400, 360, 320, 300, 280, 260, 240, 200, 180, 160, 144];
  // 动态基准值 (与cpData保持一致)
  const referenceMax = {
       '1080P': 0.14,
      '2K': 0.10,
      '4K': 0.05
  };

  let bestRes = '1080P';
  let maxPercentage = -1;

  // 临时计算列表
  const calculatedOptions = resolutions.map(res => {
    // 1. 计算优化后的性价比
    const optimizedResult = calculateOptimizedRatio(res);
    const totalFps = optimizedResult ? optimizedResult.totalFps : 0;
    const ratio = optimizedResult ? optimizedResult.ratio : 0;
    
    // 2. 获取当前分辨率的动态基准值
    const refMax = referenceMax[res] || 0.12;
    
    // 3. 计算性价比百分比（与cpData保持一致）
    const percentage = Math.min((ratio / refMax) * 100, 100);

    // 追踪最佳分辨率（使用性价比百分比）
    if (percentage > maxPercentage) {
        maxPercentage = percentage;
        bestRes = res;
    }

    // 4. 计算推荐刷新率
    // 基准公式: 总帧率 / 3 (模拟竞技向需求)
    const baseFps = totalFps / 3;
    let recommendHz = 120; // 兜底逻辑：默认 120Hz

    // 匹配标准档位
    for (const rate of standardRates) {
        if (baseFps >= rate) {
            recommendHz = rate;
            break;
        }
    }
    // 如果基准帧率 < 144，保持默认的 120Hz (或根据需要调整为60Hz)

    // 生成描述文案
    let desc = '';
    if (res === '1080P') desc = '极致流畅，电竞首选';
    else if (res === '2K') desc = '画质与流畅的平衡';
    else desc = '画质优先，细腻体验';

    if (recommendHz < 144) {
        desc = '画质优先，帧率一般';
    }

    return {
        resolution: res,
        refreshRate: recommendHz + 'Hz',
        ratio: ratio,
        percentage: percentage,
        desc: desc,
        isBest: false // 后续更新
    };
  });

  // 标记最佳选项
  const options = calculatedOptions.map(opt => {
      if (opt.resolution === bestRes) {
          return {
              ...opt,
              isBest: true,
              desc: '性价比最高且帧率极佳'
          };
      }
      return opt;
  });

  const bestChoice = options.find(o => o.isBest);

  return {
      bestChoice: {
          resolution: bestChoice.resolution,
          refreshRate: bestChoice.refreshRate,
          reason: '综合配置性能与预算的最佳搭配'
      },
      options: options
  };
});
// END: 新增 - 显示器智能推荐逻辑

// 计算瓶颈类型：根据当前分辨率下的CPU和GPU理论帧率
const bottleneckInfo = computed(() => {
    if(!assessResult.value || !assessResult.value.games || assessResult.value.games.length === 0) {
        return null;
    }
    
    // 取第一款游戏的数据作为参考（所有游戏应该结果一致）
    const game = assessResult.value.games[0];
    if(!game.cpuFps || !game.gpuFps) {
        return null;
    }
    
    const cpuFps = game.cpuFps[currentResolution.value] || 0;
    const gpuFps = game.gpuFps[currentResolution.value] || 0;
    
    if(cpuFps <= 0 || gpuFps <= 0) {
        return null;
    }
    
    // 计算比例
    const cpuRatio = cpuFps / gpuFps;
    const gpuRatio = gpuFps / cpuFps;
    
    // 30%阈值
    const threshold = 1.3;
    
    if(cpuRatio >= threshold) {
        // CPU帧率比GPU高30%以上，显卡瓶颈
        return {
            type: 'gpu',
            message: '当前配置存在显卡瓶颈',
            // 优化点：增加建议用户降低分辨率的提示
            detail: `CPU理论帧率(${cpuFps} FPS)比显卡理论帧率(${gpuFps} FPS)高${((cpuRatio - 1) * 100).toFixed(0)}%，建议升级显卡以充分发挥CPU性能。您也可以尝试降低游戏分辨率或画质以缓解瓶颈。`
        };
    } else if(gpuRatio >= threshold) {
        // GPU帧率比CPU高30%以上，CPU瓶颈
        return {
            type: 'cpu',
            message: '当前配置存在CPU瓶颈',
            detail: `显卡理论帧率(${gpuFps} FPS)比CPU理论帧率(${cpuFps} FPS)高${((gpuRatio - 1) * 100).toFixed(0)}%，建议升级CPU以充分发挥显卡性能`
        };
    }
    
    // 无明显瓶颈
    return {
        type: 'balanced',
        message: '当前配置较为平衡',
        detail: `CPU理论帧率(${cpuFps} FPS)与显卡理论帧率(${gpuFps} FPS)比例协调，没有明显的性能瓶颈`
    };
});
// 雷达图数据计算
const radarData = computed(() => {
  if(!assessResult.value) return null;
  
  // 获取各维度数据
  const cpuScore = assessResult.value.cpuBaseScore || 0;
  const gpuScore = activeHardware.gpu?.performanceScore || 0;
  const memoryScore = assessResult.value.memoryScore || 0;
  const totalScore = assessResult.value.totalScore || 0;
  
  // 检查是否填写了内存
  const hasMemory = form.ramFrequency && form.ramFrequency > 0;
  
  // 检查是否填写了价格
  const hasPrice = form.totalPrice > 0;
  
  // 优化点：性价比维度直接使用计算好的百分比 (0-100)
  // 如果没有价格，性价比为0
  const costPerfValue = (hasPrice && cpData.value) ? Number(cpData.value.percentage) : 0;
  
  // 固定为五边形雷达图，动态调整显示的维度
  // 优化点：性价比 Max 设为 100，与其他维度统一量纲（虽然其他是绝对值，但性价比我们用百分比展示更好）
  const dimensions = [
    { name: 'CPU性能', value: cpuScore, max: 9907, show: true },
    { name: '显卡性能', value: gpuScore, max: 14480, show: true },
    { name: '内存性能', value: memoryScore, max: 4000, show: hasMemory },
    { name: '整机跑分', value: totalScore, max: 28387, show: true },
    { name: '性价比', value: costPerfValue, max: 100, show: hasPrice }
  ];
  
  return dimensions;
});

// 初始化雷达图
function initRadarChart() {
  if(!radarChartRef.value) return;
  
  // 初始化雷达图实例
  radarChartInstance = markRaw(echarts.init(radarChartRef.value));
  
  // 更新雷达图数据
  updateRadarChart();
}

// 更新雷达图
function updateRadarChart() {
  if(!radarChartInstance || !radarData.value) return;
  
  // 固定为五边形雷达图，提取所有维度配置
  const indicator = radarData.value.map(item => ({
    name: item.name,
    max: item.max
  }));
  
  // 提取数据，根据show属性决定是否显示
  const values = radarData.value.map(item => item.show ? item.value : 0);
  
  // 设置雷达图选项
  const option = {
    backgroundColor: 'transparent',
    radar: {
      indicator: indicator,
      shape: 'polygon',
      splitNumber: 5,
      axisName: {
        color: '#606266',
        fontSize: 12
      },
      splitLine: {
        lineStyle: {
          color: '#e4e7ed'
        }
      },
      splitArea: {
        show: true,
        areaStyle: {
          color: ['#f9f9f9', '#f0f0f0']
        }
      },
      axisLine: {
        lineStyle: {
          color: '#dcdfe6'
        }
      }
    },
    series: [{
      name: '配置评分',
      type: 'radar',
      data: [{
        value: values,
        name: '当前配置',
        areaStyle: {
          color: new echarts.graphic.RadialGradient(0.5, 0.5, 1, [
            { color: 'rgba(99, 102, 241, 0.3)', offset: 0 },
            { color: 'rgba(99, 102, 241, 0.1)', offset: 1 }
          ])
        },
        lineStyle: {
          color: '#6366f1',
          width: 2
        },
        itemStyle: {
          color: '#6366f1',
          borderWidth: 2,
          borderColor: '#fff'
        },
        // 标签显示配置
        label: {
          show: true,
          formatter: (params) => {
            // 只显示有数据的维度标签
            const dimension = radarData.value[params.dataIndex];
            if (!dimension.show) return '';
            
            // 特殊处理性价比标签，加上 %
            if (dimension.name === '性价比') {
                return params.value.toFixed(0) + '分';
            }
            return params.value.toFixed(0);
          }
        }
      }]
    }]
  };
  
  // 更新雷达图
  radarChartInstance.setOption(option);
}

// 监听评估结果变化，更新雷达图
watch(
  [() => assessResult.value, () => currentResolution.value],
  () => {
    if(radarChartInstance) {
      updateRadarChart();
    } else if(assessResult.value) {
      // 使用nextTick确保DOM渲染完成后再初始化雷达图
      nextTick(() => {
        initRadarChart();
      });
    }
  },
  { deep: true }
);

// END: 新增 - 性价比计算逻辑

async function loadData() {
  try {
    const [cpuRes, gpuRes, ramRes, moboRes, psuRes] = await Promise.all([
      listFrontCpu({ pageNum: 1, pageSize: 999 }),
      listFrontGpu({ pageNum: 1, pageSize: 999 }),
      listFrontMemory({ pageNum: 1, pageSize: 999 }),
      listFrontMotherboard({ pageNum: 1, pageSize: 999 }),
      listFrontPower({ pageNum: 1, pageSize: 999 })
    ]);
    cpuOptions.value = cpuRes.rows || [];
    gpuOptions.value = gpuRes.rows || [];
    ramOptions.value = ramRes.rows || [];
    moboOptions.value = moboRes.rows || [];
    psuOptions.value = psuRes.rows || [];
  } catch (error) {
    console.error("Failed to load hardware options:", error);
  }
}

function getList() {
  if (!isLogin.value) return; 
  listLoading.value = true;
  listUserBuild(queryParams).then(response => {
    buildList.value = response.rows;
    total.value = response.total;
    listLoading.value = false;
  }).catch(() => {
    listLoading.value = false;
  });
}

function handleLoadBuild(row) {
  Object.keys(form).forEach(key => {
    if (row[key] !== undefined) form[key] = row[key];
  });
  form.buildId = row.buildId; 
  
  activeHardware.cpu = cpuOptions.value.find(i => i.model === row.cpuModel) || null;
  activeHardware.gpu = gpuOptions.value.find(i => i.model === row.gpuModel) || null;
  activeHardware.mobo = moboOptions.value.find(i => 
    i.chipset === row.moboModel && 
    (row.ramInterface ? i.memoryType === row.ramInterface : true)
  ) || null;
  activeHardware.psu = psuOptions.value.find(i => i.wattage == row.psuWattage) || null;

  // 先调用handleRamTypeChange来设置filteredRamOptions
  handleRamTypeChange(form.ramInterface);
  // 然后再设置ramFrequency，这样就不会被重置了
  form.ramFrequency = row.ramFrequency;
  
  ElMessage.success("已加载配置：" + row.title);
  window.scrollTo({ top: 0, behavior: 'smooth' });
}

function handleDeleteBuild(row) {
  ElMessageBox.confirm('确认删除配置单 "' + row.title + '" 吗?', "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning"
  }).then(() => {
    return delUserBuild(row.buildId);
  }).then(() => {
    getList();
    ElMessage.success("删除成功");
    if (form.buildId === row.buildId) resetForm(); 
  }).catch(() => {});
}
// 新增分页事件处理函数
const handlePageChange = (pageNum) => {
  queryParams.pageNum = pageNum;
  getList();
};

const handleSizeChange = (pageSize) => {
  queryParams.pageSize = pageSize;
  queryParams.pageNum = 1; // 切换每页条数时重置到第一页
  getList();
};
function handleCpuChange(item) {
  form.cpuModel = item ? item.model : '';
  form.cpuTdp = item ? item.tdp : 0;
}

function handleMoboChange(item) {
  form.moboModel = item ? item.chipset : '';
}

function handleGpuChange(item) {
  form.gpuModel = item ? item.model : '';
}

function handlePsuChange(item) {
  form.psuWattage = item ? item.wattage.toString() : '';
}

function handleRamTypeChange(val) {
  form.ramFrequency = ''; 
  if (!val) {
    filteredRamOptions.value = [];
    return;
  }
  filteredRamOptions.value = ramOptions.value.filter(item => {
    if (item.type && item.type.toUpperCase() !== val) return false;
    const freq = Number(item.frequency);
    return val === 'DDR5' ? freq >= 4800 : freq < 4800;
  });
}

function stopAiTypewriter() {
  if (aiTypewriterTimer) {
    clearInterval(aiTypewriterTimer);
    aiTypewriterTimer = null;
  }
}

function startAiTypewriter(content) {
  stopAiTypewriter();
  aiReviewTypedContent.value = '';
  if (!content) return;

  let index = 0;
  aiTypewriterTimer = setInterval(() => {
    aiReviewTypedContent.value += content.charAt(index);
    index += 1;
    if (index >= content.length) {
      stopAiTypewriter();
    }
  }, 12);
}

function buildAiReviewPayload() {
  const bestChoice = monitorRecommendation.value?.bestChoice || {};
  return {
    title: form.title,
    cpuModel: form.cpuModel,
    cpuPrice: Number(form.cpuPrice) || 0,
    cpuTdp: Number(form.cpuTdp || activeHardware.cpu?.tdp) || 0,
    moboBrand: form.moboBrand,
    moboModel: form.moboModel,
    moboSeries: form.moboSeries,
    moboPrice: Number(form.moboPrice) || 0,
    ramBrand: form.ramBrand,
    ramInterface: form.ramInterface,
    ramFrequency: form.ramFrequency,
    ramCapacity: form.ramCapacity,
    ramPrice: Number(form.ramPrice) || 0,
    gpuBrand: form.gpuBrand,
    gpuModel: form.gpuModel,
    gpuSeries: form.gpuSeries,
    gpuPrice: Number(form.gpuPrice) || 0,
    ssdFullName: form.ssdFullName,
    ssdPrice: Number(form.ssdPrice) || 0,
    coolerFullName: form.coolerFullName,
    coolerPrice: Number(form.coolerPrice) || 0,
    psuBrand: form.psuBrand,
    psuWattage: form.psuWattage,
    psuSeries: form.psuSeries,
    psuPrice: Number(form.psuPrice) || 0,
    caseFullName: form.caseFullName,
    casePrice: Number(form.casePrice) || 0,
    fanFullName: form.fanFullName,
    fanPrice: Number(form.fanPrice) || 0,
    totalPrice: Number(form.totalPrice) || 0,
    totalScore: Number(assessResult.value?.totalScore) || 0,
    currentResolution: currentResolution.value,
    bottleneckType: bottleneckInfo.value?.type || '',
    bottleneckMessage: bottleneckInfo.value?.message || '',
    bottleneckDetail: bottleneckInfo.value?.detail || '',
    cpLevel: cpData.value?.level || '',
    cpDisplayValue: cpData.value?.displayValue ? Number(cpData.value.displayValue) : null,
    cpPercentage: cpData.value?.percentage ? Number(cpData.value.percentage) : null,
    monitorBestResolution: bestChoice.resolution || '',
    monitorBestRefreshRate: bestChoice.refreshRate || '',
    monitorBestReason: bestChoice.reason || '',
    monitorOptions: (monitorRecommendation.value?.options || []).map(opt => ({
      resolution: opt.resolution,
      refreshRate: opt.refreshRate,
      desc: opt.desc,
      best: !!opt.isBest
    })),
    games: assessResult.value?.games || [],
    style: aiStyle.value
  };
}

async function handleAiReview() {
  if (!assessResult.value) {
    ElMessage.warning('请先完成性能评估');
    return;
  }

  aiReviewLoading.value = true;
  try {
    const res = await generateAiReview(buildAiReviewPayload());
    const content = res?.data?.content || res?.data?.aiResponse || '';
    if (!content) {
      ElMessage.warning('AI 返回内容为空');
      return;
    }
    aiReviewContent.value = content;
    startAiTypewriter(content);
    ElMessage.success('AI 实时评估完成');
  } catch (error) {
    console.error('AI 实时评估失败:', error);
  } finally {
    aiReviewLoading.value = false;
  }
}

function handleAssess() {
  if (!form.cpuModel || !form.gpuModel) {
    ElMessage.warning("请至少选择 CPU 和 显卡 才能进行性能评估");
    return;
  }
  const reqData = {
    cpuModel: form.cpuModel,
    gpuModel: form.gpuModel,
    ramType: form.ramInterface || 'DDR4',
    ramFrequency: Number(form.ramFrequency) || 3200
  };
  stopAiTypewriter();
  aiReviewContent.value = '';
  aiReviewTypedContent.value = '';
  assessPerformance(reqData).then(res => {
    assessResult.value = res.data; 
    ElMessage.success("评估完成");
  });
}

function getGameFps(gameData, resolution) {
  if (!gameData || !gameData.fps) return 0;
  return gameData.fps[resolution] || 0;
}

function calculateMaxFps(resolution) {
  return MAX_FPS_REFERENCE[resolution] || 300;
}

function calculatePercentage(val, max) {
  if (!val || !max) return 0;
  const pct = (val / max) * 100;
  return pct > 100 ? 100 : Number(pct.toFixed(1));
}

function getFpsColor(fps) {
  if (fps >= 144) return '#67c23a';
  if (fps >= 60) return '#e6a23c';
  return '#f56c6c';
}

// 相似度计算函数
function calculateMatchRate(productScore, userScore) {
  if (userScore <= 0) return 0;
  const rate = 100 - (Math.abs(productScore - userScore) / userScore * 100);
  return Math.min(Math.round(rate), 100);
}

// 获取相似度标签类型
function getMatchRateType(rate) {
  if (rate >= 95) return 'danger';
  if (rate >= 85) return 'primary';
  return 'info';
}

// 获取商品标签类名
function getProductTagClass(productType) {
  if (productType === '1') return 'tag-rent';
  return 'tag-sale';
}

// 获取商品标签文本
function getProductTagText(productType) {
  if (productType === '1') return '租赁';
  if (productType === '3') return '租售';
  return '售卖';
}

// 获取推荐商品
function getRecommendProducts(score) {
  if (debounceTimer.value) {
    clearTimeout(debounceTimer.value);
  }
  debounceTimer.value = setTimeout(async () => {
    if (score > 0) {
      try {
        const res = await listSimilarProducts(score);
        console.log("推荐商品原始数据:", res.data);
        recommendList.value = res.data || [];
        console.log("推荐商品处理后数据:", recommendList.value);
      } catch (error) {
        console.error("获取推荐商品失败:", error);
      }
    }
  }, 300);
}

// 查看商品详情
function viewProductDetail(product) {
  // 检查用户是否登录
  const token = localStorage.getItem('app_token');
  if (!token) {
    ElMessage.warning('请先登录');
    // 将商品信息存储到sessionStorage，登录后可以获取
    sessionStorage.setItem('pendingProduct', JSON.stringify({
      productId: product.productId,
      productName: product.productName,
      productImg: product.imageUrl,
      price: product.price || 0,
      businessType: product.businessType || '0', // 默认为购买
      quantity: 1,
      daterange: [],
      rentDays: 0
    }));
    router.push('/portal/login?redirect=' + encodeURIComponent('/computer-market/checkout'));
    return;
  }
  
  // 构建完整的商品数据，与mallHome.vue保持一致
  const productData = {
    productId: product.productId,
    productName: product.productName,
    productImg: product.imageUrl,
    price: product.price || 0,
    businessType: product.businessType || '0', // 默认为购买
    quantity: 1,
    daterange: [],
    rentDays: 0
  };
  
  // 直接跳转到结算页面，并传递完整的商品信息
  router.push({
    path: '/computer-market/checkout',
    query: {
      directBuy: 'true',
      product: encodeURIComponent(JSON.stringify(productData))
    }
  });
}

// 监听评估结果变化
watch(
  () => assessResult.value,
  (newVal) => {
    if (newVal && newVal.totalScore > 0) {
      getRecommendProducts(newVal.totalScore);
    }
  },
  { deep: true }
);

function submitForm() {
  if (!isLogin.value) {
     ElMessageBox.confirm('登录后即可保存您的配置单，是否前往登录？', '温馨提示', {
       confirmButtonText: '去登录',
       cancelButtonText: '再逛逛',
       type: 'info'
     }).then(() => router.push('/login'));
     return;
  }

  if (compatibilityIssues.value.some(i => i.type === 'error')) {
    ElMessage.error("存在严重的兼容性冲突，请修正后再保存！");
    return;
  }

  formRef.value.validate(valid => {
    if (valid) {
      loading.value = true;
      const api = form.buildId ? updateUserBuild : addUserBuild;
      api(form).then(() => {
        ElMessage.success(form.buildId ? "更新成功" : "保存成功");
        loading.value = false;
        if(!form.buildId) form.buildId = null; // 新增后保持原样或重置视需求而定
        getList();
      }).catch(() => { loading.value = false; });
    }
  });
}

// 修改后的重置逻辑，确保新合并的字段能被清空
function resetForm() {
  if (formRef.value) formRef.value.resetFields();
  form.buildId = null;
  form.cpuTdp = 0;
  form.totalPrice = 0;
  
  // 确保清空合并后的字段
  form.moboBrand = '';
  form.gpuBrand = '';
  form.psuBrand = '';
  form.ramBrand = '';
  form.ramCapacity = '';
  
  assessResult.value = null;
  stopAiTypewriter();
  aiReviewContent.value = '';
  aiReviewTypedContent.value = '';
  currentResolution.value = '1080P';
  Object.keys(activeHardware).forEach(k => activeHardware[k] = null);
}

onMounted(() => {
  loadData();
  // 明确设置每页显示5条
  queryParams.pageSize = 5;
  if (isLogin.value) getList();
  
  // 初始化雷达图
  if(assessResult.value) {
    initRadarChart();
  }
});

// 监听窗口大小变化，重绘雷达图
window.addEventListener('resize', () => {
  if(radarChartInstance) {
    radarChartInstance.resize();
  }
});

// 组件卸载时销毁雷达图实例
onUnmounted(() => {
  stopAiTypewriter();
  if(radarChartInstance) {
    radarChartInstance.dispose();
    radarChartInstance = null;
  }
});
</script>

<style scoped lang="scss">
/* ---------------- 全局容器 ---------------- */
.app-container {
  background-color: #f5f7fa; /* 浅灰底色 */
  min-height: 100vh;
  padding: 20px;
   padding-top: 80px;
   
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
  color: #303133;
}

/* ---------------- 通用卡片样式 ---------------- */
.modern-card {
  border: none;
  border-radius: 16px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.04);
  background: #ffffff;
  transition: all 0.3s ease;
  overflow: hidden;

    overflow-y: auto;
  :deep(.el-card__header) {
    border-bottom: 1px solid #f0f2f5;
    padding: 16px 20px;
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  
  .header-title {
    font-size: 18px;
    font-weight: 600;
    color: #1f2937;
    display: flex;
    align-items: center;
    gap: 8px;
    .header-icon { color: #6366f1; }
  }
}

/* ---------------- 表单区域 ---------------- */
.title-section {
  padding: 10px 10px 10px;
 
  .title-input :deep(.el-input__wrapper) {
    --el-input-height: 44px;
    box-shadow: none;
    background: #f9fafb;
    border: 1px solid #e5e7eb;
    border-radius: 8px;
    padding: 8px 12px;
    &:focus-within {
      background: #fff;
      border-color: #6366f1;
      box-shadow: 0 0 0 2px rgba(99, 102, 241, 0.2);
    }
  }
}



/* 分割线 */
.section-divider {
  display: flex;
  align-items: center;
  margin: 20px 0 15px;
  padding: 0 10px;
  
  .divider-text {
    font-size: 12px;
    font-weight: 600;
    color: #9ca3af;
    text-transform: uppercase;
    letter-spacing: 0.5px;
    margin-right: 12px;
  }
  .divider-line {
    flex: 1;
    height: 1px;
    background: #f0f2f5;
  }
}

/* 硬件行容器优化 */
.hardware-row-wrapper {
  margin-bottom: 8px;
}

.hardware-row {
  padding: 14px 10px; /* 增加一点垂直内边距 */
  border-radius: 12px;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid transparent;
  background-color: #fff; /* 默认白色背景 */
  margin-bottom: 2px;

  &:hover {
    background-color: #f8fafc; /* 悬停时微微变灰，突出显示 */
    border-color: #dbeafe; /* 边框变蓝 */
    box-shadow: 0 4px 12px rgba(99, 102, 241, 0.08); /* 增加投影 */
    transform: translateY(-1px);
    z-index: 1;
  }
  
  /* 针对合并后的输入框样式优化 */
  :deep(.el-input__wrapper), :deep(.el-select__wrapper) {
     box-shadow: none !important;
     background-color: transparent;
     padding-left: 0;
     border-bottom: 1px solid #e5e7eb;
     border-radius: 0;
     transition: border-color 0.3s;
     
     &.is-focus {
       border-color: #6366f1;
     }
  }
  
  /* 去除输入框原本的边框，只保留底部线条，更有科技感 */
  :deep(.el-input__inner) {
    font-weight: 500;
    color: #374151;
    
    &::placeholder {
      font-weight: 400;
      color: #9ca3af;
      font-size: 13px;
    }
  }

  /* 价格输入框样式 */
  .price-input :deep(.el-input__wrapper) {
    border: none;
    background: #f1f5f9;
    border-radius: 6px;
    padding: 0 10px;
    height: 32px;
    box-shadow: none !important;
    
    &:hover {
      background: #e2e8f0;
    }
  }
}

.option-item {
  /* 确保内部容器是块级，并且有合适的间距 */
  width: 100%;
  
  .option-main { font-weight: 500; display: block; line-height: 1.4; }
  .option-sub {
    font-size: 11px;
    color: #9ca3af;
    text-align: right;  /* 添加靠右对齐 */
    display: block;      /* 确保是块级元素 */
    margin-top: 2px;     /* 添加一点间距 */
    line-height: 1.2;
  }
}

.row-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  color: #6b7280;
  font-size: 18px;
}

.tdp-tag {
  font-size: 12px;
  color: #e6a23c;
  background: #fdf6ec;
  padding: 2px 6px;
  border-radius: 4px;
}

.mb-0 { margin-bottom: 0 !important; }

/* 状态图标列居中 */
.status-col {
  display: flex;
  justify-content: center;
  align-items: center;
}

/* 底部结算条 */
.footer-bar {
  margin-top: 30px;
  background: #f8fafc;
  padding: 20px;
  border-radius: 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border: 1px solid #f1f5f9;
  
  .price-summary {
    display: flex;
    align-items: baseline;
    .label { color: #64748b; font-size: 14px; margin-right: 8px; }
    .currency { color: #ef4444; font-size: 18px; font-weight: bold; margin-right: 2px; }
    .amount { color: #ef4444; font-size: 32px; font-weight: 700; letter-spacing: -1px; }
  }
}

/* 兼容性报告 */
.compatibility-report {
  margin-top: 24px;
  background: #f0fdf4;
  border-radius: 12px;
  padding: 16px;
  border: 1px solid #bbf7d0;

  &.has-issues {
    background: #fffbeb;
    border-color: #fde68a;
    
    .report-header { color: #b45309; }
  }

  .report-header {
    display: flex;
    align-items: center;
    gap: 8px;
    font-weight: 600;
    color: #15803d;
    margin-bottom: 12px;
  }

  .safe-message {
    color: #166534;
    font-size: 14px;
    display: flex;
    align-items: center;
    .dot { width: 8px; height: 8px; background: #22c55e; border-radius: 50%; margin-right: 8px; }
  }

  .issue-item-card {
    display: flex;
    align-items: flex-start;
    padding: 8px 12px;
    border-radius: 8px;
    background: #fff;
    margin-bottom: 8px;
    font-size: 13px;
    line-height: 1.5;
    
    &.error { color: #dc2626; border-left: 3px solid #dc2626; }
    &.warning { color: #d97706; border-left: 3px solid #d97706; }

    .issue-icon { margin-right: 8px; margin-top: 2px; }
  }

  .notice-text {
    margin-top: 10px;
    font-size: 12px;
    color: #9ca3af;
  }
}

/* ---------------- 右侧性能面板 ---------------- */
.performance-card {
  position: sticky;
  top: 80px;
  background: linear-gradient(to bottom, #ffffff 0%, #fafafa 100%);
}

.score-card-main {
  background: radial-gradient(circle at center, rgba(99, 102, 241, 0.08) 0%, transparent 70%);
  padding: 30px 0;
  margin: 0;
  border-bottom: 1px dashed #e5e7eb;
  text-align: center;
  
  .score-label { font-size: 14px; color: #64748b; margin-bottom: 4px; }
  .score-number { 
    font-size: 48px; 
    font-weight: 800; 
    color: #3b82f6; 
    letter-spacing: -1px; 
    background: linear-gradient(135deg, #6366f1 0%, #3b82f6 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
  }
  .score-bar-wrapper { width: 60%; margin: 10px auto; }
  .score-desc { font-size: 12px; color: #94a3b8; }
}

/* 雷达图样式 */
.radar-chart-container {
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 20px 0;
  background: #ffffff;
  border-radius: 8px;
  padding: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.radar-chart {
  width: 100%;
  height: 300px;
  min-height: 250px;
}

@media (max-width: 768px) {
  .radar-chart {
    height: 250px;
  }
}

.resolution-switch {
  display: flex;
  justify-content: center;
  margin-bottom: 30px;
}

/* 显示器推荐区域样式 (新增) */
.monitor-recommend-section {
  background: #f8fafc;
  border-radius: 8px;
  padding: 12px;
  margin: 20px 0;
  border: 1px solid #e2e8f0;

  .section-title {
    font-size: 14px;
    font-weight: 600;
    color: #334155;
    margin-bottom: 12px;
    display: flex;
    align-items: center;
    gap: 6px;
  }

  .best-choice-card {
    background: linear-gradient(135deg, #6366f1 0%, #4f46e5 100%);
    border-radius: 8px;
    padding: 12px;
    color: white;
    position: relative;
    overflow: hidden;
    margin-bottom: 12px;
    box-shadow: 0 4px 6px rgba(79, 70, 229, 0.2);

    .badge {
      position: absolute;
      top: 0;
      right: 0;
      background: #facc15;
      color: #92400e;
      font-size: 10px;
      font-weight: 700;
      padding: 2px 8px;
      border-bottom-left-radius: 8px;
    }

    .choice-content {
      .res-hz {
        display: flex;
        align-items: baseline;
        gap: 4px;
        margin-bottom: 4px;
        .res { font-size: 20px; font-weight: 700; }
        .divider { opacity: 0.6; }
        .hz { font-size: 16px; font-weight: 500; }
      }
      .reason { font-size: 12px; opacity: 0.9; }
    }
  }

  .options-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 8px;

    .option-item {
      background: white;
      border: 1px solid #e2e8f0;
      border-radius: 6px;
      padding: 8px;
      cursor: pointer;
      transition: all 0.2s;
      position: relative;

      &:hover {
        border-color: #6366f1;
        transform: translateY(-2px);
      }

      &.active {
        border-color: #6366f1;
        background: #eef2ff;
        .opt-res { color: #4f46e5; }
      }

      .opt-header {
        display: flex;
        flex-direction: column;
        align-items: center;
        margin-bottom: 4px;
        .opt-res { font-weight: 700; font-size: 13px; color: #1e293b; }
        .opt-hz { font-size: 11px; color: #64748b; }
      }
      
      .opt-desc {
        font-size: 10px;
        color: #94a3b8;
        text-align: center;
        line-height: 1.2;
        transform: scale(0.9);
      }

      .best-mark {
        position: absolute;
        top: -4px;
        right: -4px;
        background: #facc15;
        color: #854d0e;
        font-size: 9px;
        padding: 1px 4px;
        border-radius: 4px;
        font-weight: 700;
      }
    }
  }
}

/* 性价比区域样式优化 */
.cp-section {
  padding: 0 10px 20px;
  .cp-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 8px;
    .cp-title {
       display: flex; align-items: center; gap: 4px; color: #606266; font-size: 14px;
       .info-icon { font-size: 14px; color: #909399; cursor: pointer; }
    }
    .cp-right {
        display: flex;
        align-items: center;
        gap: 8px;
    }
    .cp-value { font-weight: 700; font-size: 16px; .unit { font-size: 12px; opacity: 0.8; } }
  }
  
  .penalty-tip {
    margin-top: 8px;
    font-size: 12px;
    color: #f56c6c;
    background: #fef0f0;
    padding: 6px 10px;
    border-radius: 4px;
    display: flex;
    align-items: center;
    gap: 5px;
  }
}

/* 瓶颈警告 */
.bottleneck-warning {
  margin: 0 16px 20px;
  background: #fffbeb;
  border: 1px solid #fcd34d;
  border-radius: 8px;
  padding: 12px;
  
  &.balanced {
    background: #ecfdf5;
    border-color: #6ee7b7;
    .warning-title { color: #047857; }
    .warning-detail { color: #065f46; }
  }
  
  .warning-header {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 6px;
    .warning-title { font-weight: 700; font-size: 14px; color: #b45309; }
  }
  .warning-detail { font-size: 12px; color: #92400e; line-height: 1.5; padding-left: 24px; }
}

.games-list {
  padding: 0 10px;
  .game-row {
    margin-bottom: 20px;
    .game-info {
      display: flex;
      justify-content: space-between;
      margin-bottom: 6px;
      font-size: 14px;
      .game-name { font-weight: 600; color: #374151; }
      .game-fps { font-weight: 700; color: #10b981; .fps-unit { font-size: 10px; color: #9ca3af; font-weight: 400; } }
    }
    .game-description {
      font-size: 11px;
      color: #9ca3af;
      margin-top: 4px;
      line-height: 1.4;
    }
  }
}

.ai-review-section {
  margin: 10px 12px 0;
  padding: 14px;
  border-radius: 10px;
  border: 1px solid #dbeafe;
  background: #f8fbff;

  .ai-review-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 10px;
    margin-bottom: 10px;
    flex-wrap: wrap;
  }

  .ai-review-title {
    display: flex;
    align-items: center;
    gap: 6px;
    font-weight: 700;
    color: #1d4ed8;
  }

  .ai-style-selector {
    flex: 1;
    display: flex;
    justify-content: center;
    
    :deep(.el-radio-group) {
      display: flex;
      gap: 0;
    }
    
    :deep(.el-radio-button__inner) {
      padding: 5px 12px;
      font-size: 12px;
    }
  }

  .ai-review-body {
    white-space: pre-wrap;
    line-height: 1.7;
    font-size: 13px;
    color: #1e293b;
    max-height: 320px;
    overflow-y: auto;
    background: #ffffff;
    border: 1px solid #e5e7eb;
    border-radius: 8px;
    padding: 12px;
  }
}



.disclaimer {
  text-align: center;
  font-size: 11px;
  color: #cbd5e1;
  margin-top: 40px;
}

.empty-state-perf {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
}

/* ---------------- 商品推荐模块样式 ---------------- */
.recommend-card {
  margin-top: 24px;
  
  .empty-state {
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 200px;
    padding: 40px 0;
  }
  
  .product-card {
    background: #f8fafc;
    border: 1px solid #e2e8f0;
    border-radius: 8px;
    overflow: hidden;
    transition: all 0.3s ease;
    
    &:hover {
      transform: translateY(-4px);
      box-shadow: 0 8px 16px rgba(0, 0, 0, 0.08);
      border-color: #cbd5e1;
    }
  }
  
  .product-image {
    width: 100%;
    height: 120px;
    overflow: hidden;
    background: #ffffff;
    position: relative;
    
    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      transition: transform 0.3s ease;
    }
    
    &:hover img {
      transform: scale(1.05);
    }
  }
  
  .product-tag {
    position: absolute;
    top: 10px;
    left: 10px;
    padding: 3px 10px;
    border-radius: 20px;
    font-size: 12px;
    font-weight: bold;
    color: #fff;
    z-index: 1;
  }
  
  .tag-rent { 
    background: linear-gradient(135deg, #a0cfff 0%, #409eff 100%); 
  }
  
  .tag-sale { 
    background: linear-gradient(135deg, #fab6b6 0%, #f56c6c 100%); 
  }
  
  .product-info {
    padding: 12px;
  }
  
  .product-name {
    font-size: 14px;
    font-weight: 600;
    color: #334155;
    margin-bottom: 8px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
  
  .product-match-rate {
    margin-bottom: 8px;
  }
  
  .product-hardware {
    font-size: 12px;
    color: #64748b;
    margin-bottom: 12px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
  
  .product-price {
    margin-bottom: 12px;
    
    .price-row {
      display: flex;
      align-items: baseline;
      margin-bottom: 4px;
      
      &:last-child {
        margin-bottom: 0;
      }
    }
    
    .price-label {
      font-size: 12px;
      color: #6b7280;
      margin-right: 4px;
      min-width: 30px;
    }
    
    .currency {
      font-size: 12px;
      color: #ef4444;
      font-weight: 500;
    }
    
    .amount {
      font-size: 14px;
      font-weight: 700;
      color: #ef4444;
      margin: 0 2px;
    }
    
    .unit {
      font-size: 10px;
      color: #6b7280;
    }
  }
  
  .view-detail-btn {
    width: 100%;
    font-size: 12px;
  }
}

/* ---------------- 表格样式 ---------------- */
.mini-spec { font-size: 13px; font-weight: 500; }
.mini-spec.sub { font-size: 11px; color: #9ca3af; margin-top: 2px; }
.price-text { font-family: monospace; font-weight: 600; }
.pagination-container { padding: 15px 0; display: flex; justify-content: flex-end; }

/* 响应式调整 */
@media (max-width: 768px) {
  .hardware-row { flex-direction: column; align-items: stretch; }
  .footer-bar { flex-direction: column; gap: 15px; text-align: center; }
}
</style>

<!-- 全局样式，用于控制下拉框（popper）的样式 -->
<style lang="scss">
.hardware-select-dropdown {
  .el-select-dropdown__item {
    height: auto !important;
    padding-top: 6px;
    padding-bottom: 6px;
    line-height: normal;
  }
}

.markdown-body {
  color: #334155;
  line-height: 1.6;
  font-size: 14px;

  h1, h2, h3, h4, h5, h6 {
    margin-top: 16px;
    margin-bottom: 8px;
    font-weight: 600;
    color: #1e293b;
  }

  h1 { font-size: 20px; }
  h2 { font-size: 18px; }
  h3 { font-size: 16px; }

  p {
    margin: 8px 0;
  }

  ul, ol {
    margin: 8px 0;
    padding-left: 24px;
  }

  li {
    margin: 4px 0;
  }

  strong {
    color: #1e293b;
    font-weight: 600;
  }

  em {
    color: #64748b;
  }

  code {
    background: #f1f5f9;
    padding: 2px 6px;
    border-radius: 4px;
    font-family: 'Fira Code', monospace;
    font-size: 13px;
    color: #e11d48;
  }

  blockquote {
    border-left: 4px solid #6366f1;
    padding-left: 12px;
    margin: 12px 0;
    color: #64748b;
    background: #f8fafc;
    padding: 8px 12px;
    border-radius: 0 6px 6px 0;
  }

  hr {
    border: none;
    border-top: 1px solid #e2e8f0;
    margin: 16px 0;
  }
}
</style>
