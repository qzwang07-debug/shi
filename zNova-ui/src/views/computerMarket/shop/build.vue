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

            <div class="hardware-row-wrapper">
              <el-row :gutter="12" class="hardware-row" align="middle">
                <el-col :span="2" class="row-icon">
                  <el-tag effect="dark" type="primary" round>CPU</el-tag>
                </el-col>
                <el-col :span="9">
                  <el-form-item prop="cpuModel" label-width="0" class="mb-0">
                    <el-select 
                      v-model="activeHardware.cpu"
                      value-key="id"
                      filterable 
                      placeholder="选择处理器型号" 
                      style="width: 100%"
                      @change="handleCpuChange"
                      popper-class="modern-select-dropdown"
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
                <el-col :span="7">
                  <el-form-item prop="cpuPrice" label-width="0" class="mb-0">
                    <el-input-number v-model="form.cpuPrice" :min="0" :precision="2" :controls="false" style="width: 100%" placeholder="价格" class="price-input">
                       <template #prefix>¥</template>
                    </el-input-number>
                  </el-form-item>
                </el-col>
                <el-col :span="4" class="info-col">
                   <span class="tdp-tag" v-if="form.cpuTdp"><el-icon><Lightning /></el-icon> {{ form.cpuTdp }}W</span>
                </el-col>
                <el-col :span="2" class="status-col">
                  <StatusIcon :issues="getIssues('cpu')" />
                </el-col>
              </el-row>
            </div>

            <div class="hardware-row-wrapper">
              <el-row :gutter="12" class="hardware-row" align="middle">
                 <el-col :span="2" class="row-icon">
                  <el-tag effect="dark" type="warning" round>主板</el-tag>
                </el-col>
                <el-col :span="4">
                  <el-form-item prop="moboBrand" label-width="0" class="mb-0">
                    <el-input v-model="form.moboBrand" placeholder="品牌" />
                  </el-form-item>
                </el-col>
                <el-col :span="4">
                  <el-form-item prop="moboSeries" label-width="0" class="mb-0">
                    <el-input v-model="form.moboSeries" placeholder="系列" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item prop="moboModel" label-width="0" class="mb-0">
                    <el-select 
                      v-model="activeHardware.mobo"
                      value-key="id" 
                      filterable 
                      default-first-option
                      placeholder="芯片组型号" 
                      style="width: 100%"
                      @change="handleMoboChange"
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
                <el-col :span="6">
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

            <div class="hardware-row-wrapper">
              <el-row :gutter="12" class="hardware-row" align="middle">
                 <el-col :span="2" class="row-icon">
                  <el-tag effect="dark" type="danger" round>内存</el-tag>
                </el-col>
                <el-col :span="4">
                  <el-form-item prop="ramBrand" label-width="0" class="mb-0">
                    <el-input v-model="form.ramBrand" placeholder="品牌" />
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
                <el-col :span="4">
                  <el-form-item label-width="0" class="mb-0">
                     <el-input v-model="form.ramCapacity" placeholder="容量 (如16G*2)" />
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

            <div class="hardware-row-wrapper">
              <el-row :gutter="12" class="hardware-row" align="middle">
                 <el-col :span="2" class="row-icon">
                  <el-tag effect="dark" type="success" round>显卡</el-tag>
                </el-col>
                <el-col :span="4">
                  <el-form-item prop="gpuBrand" label-width="0" class="mb-0">
                    <el-input v-model="form.gpuBrand" placeholder="品牌" />
                  </el-form-item>
                </el-col>
                <el-col :span="4">
                  <el-form-item prop="gpuSeries" label-width="0" class="mb-0">
                    <el-input v-model="form.gpuSeries" placeholder="系列" />
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
                <el-col :span="6">
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

            <div class="hardware-row-wrapper">
              <el-row :gutter="12" class="hardware-row" align="middle">
                 <el-col :span="2" class="row-icon">
                  <el-tag effect="light" type="warning" round>电源</el-tag>
                </el-col>
                <el-col :span="4">
                  <el-form-item prop="psuBrand" label-width="0" class="mb-0">
                    <el-input v-model="form.psuBrand" placeholder="品牌" />
                  </el-form-item>
                </el-col>
                <el-col :span="4">
                  <el-form-item prop="psuSeries" label-width="0" class="mb-0">
                    <el-input v-model="form.psuSeries" placeholder="系列" />
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
                <el-col :span="6">
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

            <div class="resolution-switch">
              <el-radio-group v-model="currentResolution" size="default" fill="#6366f1">
                <el-radio-button label="1080P">1080P</el-radio-button>
                <el-radio-button label="2K">2K</el-radio-button>
                <el-radio-button label="4K">4K</el-radio-button>
              </el-radio-group>
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
             <div class="disclaimer">数据基于理论性能推算，实际游戏受场景影响可能波动 ±10%</div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup name="Build">
import { ref, reactive, computed, onMounted, h } from 'vue';
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
import useUserStore from '@/store/modules/user'; 
import { useRouter } from 'vue-router';
import Header from '../Header.vue';
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

  handleRamTypeChange(form.ramInterface);
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

function resetForm() {
  if (formRef.value) formRef.value.resetFields();
  form.buildId = null;
  form.cpuTdp = 0;
  form.totalPrice = 0;
  assessResult.value = null;
  currentResolution.value = '1080P';
  Object.keys(activeHardware).forEach(k => activeHardware[k] = null);
}

onMounted(() => {
  loadData();
  // 明确设置每页显示5条
  queryParams.pageSize = 5;
  if (isLogin.value) getList();
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

/* 硬件行容器 */
.hardware-row-wrapper {
  margin-bottom: 8px;
}

.hardware-row {
  padding: 12px 10px;
  border-radius: 12px;
  transition: all 0.25s ease;
  border: 1px solid transparent;

  &:hover {
    background-color: #fff;
    border-color: #e5e7eb;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.03);
    transform: translateY(-1px);
  }

  /* 输入框微调 */
  :deep(.el-input__wrapper), :deep(.el-select__wrapper) {
    box-shadow: none;
    background-color: transparent;
    border-bottom: 1px solid #e5e7eb;
    border-radius: 0;
    padding-left: 0;
    
    &.is-focus {
       box-shadow: none !important;
       border-color: #6366f1;
    }
  }

  /* 价格输入框特殊处理 */
  .price-input :deep(.el-input__wrapper) {
    border-bottom: none;
    background: #f3f4f6;
    border-radius: 6px;
    padding: 0 8px;
    height: 32px;
  }
}

.option-item {
  .option-main { font-weight: 500; display: block; }
  .option-sub { font-size: 11px; color: #9ca3af; }
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

.perf-header .title { font-size: 16px; font-weight: 600; display: flex; align-items: center; gap: 6px; }

.score-card-main {
  text-align: center;
  margin: 30px 0;
  position: relative;
  
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

.resolution-switch {
  display: flex;
  justify-content: center;
  margin-bottom: 30px;
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