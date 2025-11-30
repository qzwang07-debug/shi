<template>
  <div class="app-container">
    <el-row :gutter="20">
      <el-col :span="14" :xs="24">
        <el-card class="box-card">
          <template #header>
            <div class="clearfix">
              <span>DIY 电脑配置单</span>
              <el-button style="float: right; padding: 3px 0" type="text" @click="resetForm">重置</el-button>
            </div>
          </template>
          
          <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" size="default">
            
            <el-form-item label="配置标题" prop="title">
              <el-input v-model="form.title" placeholder="请输入配置单标题（如：我的吃鸡神机）" />
            </el-form-item>

            <el-divider content-position="left">核心组件</el-divider>

            <el-row :gutter="10">
              <el-col :span="12">
                <el-form-item label="CPU 型号" prop="cpuModel">
                  <el-select 
                    v-model="form.cpuModel" 
                    filterable 
                    placeholder="请选择/搜索 CPU" 
                    style="width: 100%"
                    @change="handleCpuChange"
                  >
                    <el-option
                      v-for="item in cpuOptions"
                      :key="item.id"
                      :label="item.model"
                      :value="item.model"
                    >
                      <span style="float: left">{{ item.model }}</span>
                      <span style="float: right; color: #8492a6; font-size: 13px">{{ item.tdp }}W</span>
                    </el-option>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="CPU 价格" prop="cpuPrice">
                  <el-input-number v-model="form.cpuPrice" :min="0" :precision="2" controls-position="right" style="width: 100%" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item label="功耗展示">
              <el-input v-model="form.cpuTdp" readonly placeholder="自动填入">
                <template #append>W</template>
              </el-input>
            </el-form-item>

            <el-row :gutter="10">
              <el-col :span="8">
                <el-form-item label="主板品牌" prop="moboBrand">
                  <el-input v-model="form.moboBrand" placeholder="品牌" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="主板系列" prop="moboSeries">
                  <el-input v-model="form.moboSeries" placeholder="系列" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="芯片组/型号" prop="moboModel">
                  <el-select v-model="form.moboModel" filterable placeholder="选择芯片组" style="width: 100%">
                    <el-option
                      v-for="item in moboOptions"
                      :key="item.id"
                      :label="item.chipset"
                      :value="item.chipset"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item label="主板价格" prop="moboPrice">
              <el-input-number v-model="form.moboPrice" :min="0" :precision="2" controls-position="right" style="width: 100%" />
            </el-form-item>

            <el-row :gutter="10">
              <el-col :span="8">
                <el-form-item label="内存品牌" prop="ramBrand">
                  <el-input v-model="form.ramBrand" placeholder="品牌" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="接口类型" prop="ramInterface">
                  <el-select v-model="form.ramInterface" placeholder="类型" @change="handleRamTypeChange">
                    <el-option label="DDR4" value="DDR4" />
                    <el-option label="DDR5" value="DDR5" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="频率" prop="ramFrequency">
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
            </el-row>
            <el-row :gutter="10">
              <el-col :span="12">
                <el-form-item label="容量" prop="ramCapacity">
                  <el-input v-model="form.ramCapacity" placeholder="如 16GB" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="内存价格" prop="ramPrice">
                  <el-input-number v-model="form.ramPrice" :min="0" :precision="2" controls-position="right" style="width: 100%" />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="10">
              <el-col :span="8">
                <el-form-item label="显卡品牌" prop="gpuBrand">
                  <el-input v-model="form.gpuBrand" placeholder="品牌" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="显卡系列" prop="gpuSeries">
                  <el-input v-model="form.gpuSeries" placeholder="系列" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="显卡型号" prop="gpuModel">
                  <el-select v-model="form.gpuModel" filterable placeholder="型号" style="width: 100%">
                    <el-option
                      v-for="item in gpuOptions"
                      :key="item.id"
                      :label="item.model"
                      :value="item.model"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item label="显卡价格" prop="gpuPrice">
              <el-input-number v-model="form.gpuPrice" :min="0" :precision="2" controls-position="right" style="width: 100%" />
            </el-form-item>

            <el-divider content-position="left">存储与外设</el-divider>

            <el-form-item label="固态硬盘" prop="ssdFullName">
              <el-input v-model="form.ssdFullName" placeholder="请输入固态硬盘全称">
                <template #append>
                  <el-input-number v-model="form.ssdPrice" :min="0" :precision="2" placeholder="价格" :controls="false" style="width: 100px" />
                  <span style="margin-left: 5px">元</span>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item label="散热器" prop="coolerFullName">
              <el-input v-model="form.coolerFullName" placeholder="请输入散热器全称">
                <template #append>
                  <el-input-number v-model="form.coolerPrice" :min="0" :precision="2" placeholder="价格" :controls="false" style="width: 100px" />
                  <span style="margin-left: 5px">元</span>
                </template>
              </el-input>
            </el-form-item>

            <el-row :gutter="10">
              <el-col :span="8">
                <el-form-item label="电源品牌" prop="psuBrand">
                  <el-input v-model="form.psuBrand" placeholder="品牌" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="电源系列" prop="psuSeries">
                  <el-input v-model="form.psuSeries" placeholder="系列" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="瓦数" prop="psuWattage">
                  <el-select v-model="form.psuWattage" filterable placeholder="瓦数" style="width: 100%">
                    <el-option
                      v-for="item in psuOptions"
                      :key="item.id"
                      :label="item.wattage + ' W'"
                      :value="item.wattage.toString()"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item label="电源价格" prop="psuPrice">
              <el-input-number v-model="form.psuPrice" :min="0" :precision="2" controls-position="right" style="width: 100%" />
            </el-form-item>

            <el-form-item label="机箱" prop="caseFullName">
              <el-input v-model="form.caseFullName" placeholder="请输入机箱全称">
                <template #append>
                  <el-input-number v-model="form.casePrice" :min="0" :precision="2" placeholder="价格" :controls="false" style="width: 100px" />
                  <span style="margin-left: 5px">元</span>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item label="风扇" prop="fanFullName">
              <el-input v-model="form.fanFullName" placeholder="请输入风扇全称">
                <template #append>
                  <el-input-number v-model="form.fanPrice" :min="0" :precision="2" placeholder="价格" :controls="false" style="width: 100px" />
                  <span style="margin-left: 5px">元</span>
                </template>
              </el-input>
            </el-form-item>

            <el-divider />

            <el-form-item label="总金额" class="total-price-item">
              <span style="color: #f56c6c; font-size: 20px; font-weight: bold">¥ {{ totalPrice }}</span>
            </el-form-item>

            <el-form-item>
              <el-button type="primary" @click="submitForm" :loading="loading">保存配置单</el-button>
              <el-button @click="resetForm">清空</el-button>
            </el-form-item>

          </el-form>
        </el-card>
      </el-col>

      <el-col :span="10" :xs="24">
        <el-card class="box-card" style="height: 100%; min-height: 500px;">
          <template #header>
            <div class="clearfix">
              <span>性能可视化 (开发中)</span>
            </div>
          </template>
          <div style="text-align: center; margin-top: 100px; color: #909399;">
            <el-icon :size="50"><DataLine /></el-icon>
            <p>此处后续放置可视化图表</p>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup name="Build">
import { ref, reactive, computed, onMounted, watch } from 'vue';
import { ElMessage } from 'element-plus';
import { 
  listFrontCpu, 
  listFrontGpu, 
  listFrontMemory, 
  listFrontMotherboard, 
  listFrontPower 
} from '@/api/front/hardware';
import { addUserBuild } from '@/api/front/userBuild';

const loading = ref(false);
const formRef = ref(null);

// 选项数据
const cpuOptions = ref([]);
const gpuOptions = ref([]);
const ramOptions = ref([]); // 原始内存数据
const filteredRamOptions = ref([]); // 过滤后的内存数据
const moboOptions = ref([]);
const psuOptions = ref([]);

// 表单数据
const form = reactive({
  title: '',
  // CPU
  cpuModel: '',
  cpuPrice: 0,
  cpuTdp: 0,
  // 主板
  moboBrand: '',
  moboModel: '',
  moboSeries: '',
  moboPrice: 0,
  // 内存
  ramBrand: '',
  ramInterface: '',
  ramFrequency: '',
  ramCapacity: '',
  ramPrice: 0,
  // 显卡
  gpuBrand: '',
  gpuModel: '',
  gpuSeries: '',
  gpuPrice: 0,
  // 存储与外设
  ssdFullName: '',
  ssdPrice: 0,
  coolerFullName: '',
  coolerPrice: 0,
  psuBrand: '',
  psuWattage: '',
  psuSeries: '',
  psuPrice: 0,
  caseFullName: '',
  casePrice: 0,
  fanFullName: '',
  fanPrice: 0,
  // 统计
  totalPrice: 0
});

// 校验规则
const rules = {
  title: [{ required: true, message: '请输入配置单标题', trigger: 'blur' }],
  cpuModel: [{ required: true, message: '请选择CPU型号', trigger: 'change' }],
};

// 计算总价
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
  form.totalPrice = price; // 同步写入 form
  return price.toFixed(2);
});

// 加载后端数据
async function loadData() {
  try {
    // 1. CPU
    const cpuRes = await listFrontCpu({ pageNum: 1, pageSize: 999 });
    cpuOptions.value = cpuRes.rows || [];
    
    // 2. GPU
    const gpuRes = await listFrontGpu({ pageNum: 1, pageSize: 999 });
    gpuOptions.value = gpuRes.rows || [];

    // 3. Memory
    const ramRes = await listFrontMemory({ pageNum: 1, pageSize: 999 });
    ramOptions.value = ramRes.rows || [];

    // 4. Motherboard
    const moboRes = await listFrontMotherboard({ pageNum: 1, pageSize: 999 });
    moboOptions.value = moboRes.rows || [];

    // 5. Power Supply
    const psuRes = await listFrontPower({ pageNum: 1, pageSize: 999 });
    psuOptions.value = psuRes.rows || [];
  } catch (error) {
    console.error("Failed to load hardware options:", error);
  }
}

// 处理 CPU 变更：自动填入 TDP
function handleCpuChange(val) {
  const selectedCpu = cpuOptions.value.find(item => item.model === val);
  if (selectedCpu) {
    form.cpuTdp = selectedCpu.tdp;
  } else {
    form.cpuTdp = 0;
  }
}

// 处理内存类型变更：过滤频率
function handleRamTypeChange(val) {
  form.ramFrequency = ''; // 重置频率选择
  if (!val) {
    filteredRamOptions.value = [];
    return;
  }
  
  filteredRamOptions.value = ramOptions.value.filter(item => {
    // 假设后端返回的数据中有 type 字段 (DDR4/DDR5) 和 frequency (Long)
    // 根据需求：DDR5 >= 4800, DDR4 < 4800
    // 如果后端数据本身就已经区分了 type 字段，优先用 type 过滤
    if (item.type && item.type.toUpperCase() !== val) {
      return false;
    }
    
    // 辅助逻辑：通过频率判断
    const freq = Number(item.frequency);
    if (val === 'DDR5') {
      return freq >= 4800;
    } else if (val === 'DDR4') {
      return freq < 4800;
    }
    return true;
  });
}

// 提交表单
function submitForm() {
  formRef.value.validate(valid => {
    if (valid) {
      loading.value = true;
      addUserBuild(form).then(response => {
        ElMessage.success("保存成功");
        loading.value = false;
        // 可以选择跳转到列表页或清空
      }).catch(() => {
        loading.value = false;
      });
    }
  });
}

// 重置表单
function resetForm() {
  if (formRef.value) {
    formRef.value.resetFields();
  }
  form.cpuTdp = 0;
  form.totalPrice = 0;
}

onMounted(() => {
  loadData();
});
</script>

<style scoped>
.box-card {
  margin-bottom: 20px;
}
</style>