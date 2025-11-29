<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="商品名称" prop="productName">
        <el-input
          v-model="queryParams.productName"
          placeholder="请输入商品名称"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="租售类型" prop="productType">
        <el-select v-model="queryParams.productType" placeholder="请选择租售类型" clearable>
          <el-option
            v-for="dict in product_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="销售价格" prop="salePrice">
        <el-input
          v-model="queryParams.salePrice"
          placeholder="请输入销售价格"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="CPU" prop="cpu">
        <el-input
          v-model="queryParams.cpu"
          placeholder="请输入CPU"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="显卡" prop="graphicsCard">
        <el-input
          v-model="queryParams.graphicsCard"
          placeholder="请输入显卡"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option
            v-for="dict in sys_normal_disable"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="Plus"
          @click="handleAdd"
          v-hasPermi="['system:product:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['system:product:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['system:product:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['system:product:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="productList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="商家名称" prop="deptName" align="center" />
      <el-table-column label="商品ID" align="center" prop="id" />
      <el-table-column label="商品名称" align="center" prop="productName" />
      <el-table-column label="租售类型" align="center" prop="productType">
        <template #default="scope">
          <dict-tag :options="product_type" :value="scope.row.productType"/>
        </template>
      </el-table-column>
      <el-table-column label="日租金" align="center" prop="rentPrice" />
      <el-table-column label="销售价格" align="center" prop="salePrice" />
      <el-table-column label="库存数量" align="center" prop="stockQuantity" />
      <el-table-column label="可租赁数量" align="center" prop="availableRent" />
      <el-table-column label="CPU" align="center" prop="cpu" />
      <el-table-column label="主板" align="center" prop="motherboard" />
      <el-table-column label="内存" align="center" prop="memory" />
      <el-table-column label="固态硬盘" align="center" prop="ssd" />
      <el-table-column label="显卡" align="center" prop="graphicsCard" />
      <el-table-column label="散热器" align="center" prop="cooler" />
      <el-table-column label="电源" align="center" prop="powerSupply" />
      <el-table-column label="机箱" align="center" prop="chassis" />
      <el-table-column label="图片URL" align="center" prop="imageUrl" width="100">
        <template #default="scope">
          <image-preview :src="scope.row.imageUrl" :width="50" :height="50"/>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status">
        <template #default="scope">
          <dict-tag :options="sys_normal_disable" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['system:product:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['system:product:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <pagination
      v-show="total>0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改商品对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="productRef" :model="form" :rules="rules" label-width="80px">
        <!-- 部门选择（平台管理员可见，部门用户隐藏） -->
        <el-form-item label="部门名称" prop="deptId" v-if="isAdmin">
          <el-select v-model="form.deptId" placeholder="请选择部门">
            <el-option
                v-for="dept in deptList"
                :key="dept.deptId"
                :label="dept.deptName"
                :value="dept.deptId">
            </el-option>
          </el-select>
        </el-form-item>
        <!-- 部门用户显示部门名称（只读） -->
        <el-form-item label="部门名称" v-if="!isAdmin">
          <el-input v-model="form.deptName" disabled placeholder="自动填入当前部门" />
        </el-form-item>
        <el-form-item label="商品名称" prop="productName">
          <el-input v-model="form.productName" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="租售类型" prop="productType">
          <el-select v-model="form.productType" placeholder="请选择租售类型">
            <el-option
              v-for="dict in product_type"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="日租金" prop="rentPrice">
          <el-input v-model="form.rentPrice" placeholder="请输入日租金" />
        </el-form-item>
        <el-form-item label="销售价格" prop="salePrice">
          <el-input v-model="form.salePrice" placeholder="请输入销售价格" />
        </el-form-item>
        <el-form-item label="库存数量" prop="stockQuantity">
          <el-input v-model="form.stockQuantity" placeholder="请输入库存数量" />
        </el-form-item>
        <el-form-item label="可租赁数量" prop="availableRent">
          <el-input v-model="form.availableRent" placeholder="请输入可租赁数量" />
        </el-form-item>
       <!-- CPU组件：可输入模糊匹配的下拉框 -->
<el-form-item label="CPU" prop="cpu">
  <el-select 
    v-model="form.cpu" 
    placeholder="请选择CPU（支持模糊搜索）"
    filterable
    remote
    :remote-method="remoteCpuMethod"
    :loading="cpuLoading"
  >
    <el-option
      v-for="item in cpuList"
      :key="item.model"
      :label="`${item.brand} ${item.model}`"
      :value="item.model"
    />
  </el-select>
</el-form-item>
        <el-form-item label="主板" prop="motherboard">
          <el-input v-model="form.motherboard" placeholder="请输入主板" />
        </el-form-item>
        <!-- 内存组件：品牌（文本框）+ 类型（下拉框）+ 频率（下拉框） -->
<el-form-item label="内存" prop="memory">
  <el-row :gutter="10">
    <el-col :span="8">
      <el-input
        v-model="form.memoryBrandAndModel"
        placeholder="请输入内存品牌型号（如：宏碁冰刃）"
      />
    </el-col>
    <el-col :span="8">
      <el-select v-model="form.memoryType" placeholder="请选择内存类型">
        <el-option
          v-for="item in memoryTypeList"
          :key="item.type"
          :label="item.type"
          :value="item.type"
        />
      </el-select>
    </el-col>
    <el-col :span="8">
      <el-select v-model="form.memoryFrequency" placeholder="请选择内存频率">
        <el-option
          v-for="item in memoryFrequencyList"
          :key="item.frequency"
          :label="`${item.frequency} MHz`"
          :value="item.frequency"
        />
      </el-select>
    </el-col>
  </el-row>
</el-form-item>
<el-form-item label="固态硬盘" prop="ssd">
  <el-input v-model="form.ssd" placeholder="请输入固态硬盘型号" />
</el-form-item>
     <!-- 显卡组件：品牌（文本框）+ 型号（下拉框）+ 具体型号（文本框） -->
<el-form-item label="显卡品牌" prop="graphicsCardBrand">
  <el-input v-model="form.graphicsCardBrand" placeholder="请输入显卡品牌（如：七彩虹）" />
</el-form-item>
<el-form-item label="显卡型号" prop="graphicsCardModel">
  <el-select 
    v-model="form.graphicsCardModel" 
    placeholder="请选择显卡型号（支持模糊搜索）"
    filterable
    remote
    :remote-method="remoteGpuMethod"
    :loading="gpuLoading"
  >
    <el-option
      v-for="item in gpuList"
      :key="item.model"
      :label="`${item.brand} ${item.model}`"
      :value="item.model"
    />
  </el-select>
</el-form-item>
<el-form-item label="显卡具体型号" prop="graphicsCardSpecific">
  <el-input v-model="form.graphicsCardSpecific" placeholder="请输入具体型号（如：Ultra W OC）" />
</el-form-item>
        <el-form-item label="散热器" prop="cooler">
          <el-input v-model="form.cooler" placeholder="请输入散热器" />
        </el-form-item>
        <el-form-item label="电源" prop="powerSupply">
          <el-input v-model="form.powerSupply" placeholder="请输入电源" />
        </el-form-item>
        <el-form-item label="机箱" prop="chassis">
          <el-input v-model="form.chassis" placeholder="请输入机箱" />
        </el-form-item>
        <el-form-item label="图片URL" prop="imageUrl">
          <image-upload v-model="form.imageUrl"/>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio
              v-for="dict in sys_normal_disable"
              :key="dict.value"
              :label="dict.value"
            >{{dict.label}}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Product">
import { ref, reactive, toRefs, getCurrentInstance, onMounted } from 'vue'
import { listProduct, getProduct, delProduct, addProduct, updateProduct, listCpu, listGpu, listMemory } from "@/api/system/product"
import useUserStore from '@/store/modules/user'
import { listDept, getDept } from "@/api/system/dept"

// 获取proxy（优化生产环境兼容性）
const { proxy } = getCurrentInstance() || {}
const { product_type, sys_normal_disable } = proxy?.useDict('product_type', 'sys_normal_disable') || { product_type: [], sys_normal_disable: [] }

// 响应式数据定义
const productList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")

// 硬件列表相关
const cpuList = ref([])
const gpuList = ref([])
const gpuLoading = ref(false)
const cpuLoading = ref(false)
const memoryTypeList = ref([])
const memoryFrequencyList = ref([])

// 部门相关
const isAdmin = ref(false)
const deptList = ref([])
const userStore = useUserStore()

// 查询参数（仅保留列表筛选字段）
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  productName: null,
  productType: null,
  salePrice: null,
  cpu: null,
  graphicsCard: null,
  status: null
})

// 表单初始值定义
const initialForm = {
  id: null,
  productName: null,
  productType: null,
  rentPrice: null,
  salePrice: null,
  stockQuantity: null,
  availableRent: null,
  cpu: null,
  motherboard: null,
  memory: null,
  ssd: null,
  graphicsCard: null,
  cooler: null,
  powerSupply: null,
  chassis: null,
  imageUrl: null,
  status: "0", // 默认状态（启用）
  createBy: null,
  createTime: null,
  updateBy: null,
  updateTime: null,
  remark: null,
  // 关联字段
  gpuModel: null,
  memoryType: null,
  memoryFrequency: null,
  // 显卡细分字段
  graphicsCardBrand: null,
  graphicsCardModel: null,
  graphicsCardSpecific: null,
  // 内存细分字段
  memoryBrandAndModel: null,
  // 部门相关
  deptId: null,
  deptName: null
}

// 表单数据（仅保留一份定义）
const form = reactive({ ...initialForm })

// 表单校验规则
const rules = reactive({
  productName: [
    { required: true, message: "商品名称不能为空", trigger: "blur" }
  ]
})

/** 查询商品列表 */
function getList() {
  loading.value = true
  listProduct(queryParams).then(response => {
    productList.value = response.rows || []
    total.value = response.total || 0
    loading.value = false
  }).catch(error => {
    console.error('获取商品列表失败:', error)
    loading.value = false
    proxy?.$modal?.msgError('获取商品列表失败，请稍后重试')
  })
}

/** 取消按钮 */
function cancel() {
  open.value = false
  resetForm()
}

/** 表单重置 */
function resetForm() {
  // 1. 清除reactive对象上的所有属性（防止残留后端返回的多余字段）
  for (const key in form) {
    delete form[key]
  }
  
  // 2. 重新赋值初始状态
  Object.assign(form, initialForm)
  
  // 3. 处理特殊逻辑：非管理员自动回填部门ID
  if (!isAdmin.value && userStore.deptId) {
    form.deptId = userStore.deptId
    form.deptName = userStore.deptName || '当前部门'
  }

  // 4. 重置校验状态
  proxy?.resetForm("productRef")
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.pageNum = 1
  getList()
}

/** 重置按钮操作 */
function resetQuery() {
  Object.assign(queryParams, {
    productName: null,
    productType: null,
    salePrice: null,
    cpu: null,
    graphicsCard: null,
    status: null
  })
  handleQuery()
}

/** 多选框选中事件 */
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id)
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

/** 获取部门列表（修正：去掉分页参数，后端不支持分页） */
async function getDeptList() {
  try {
    console.log('开始获取部门列表（无分页参数）')
    // 后端listDept方法不接收分页参数，直接传空对象
    const response = await listDept({})
    console.log('部门列表接口返回完整响应:', response)
    
    // 后端直接返回部门列表数组，不是分页对象
    if (response.code === 200) {
      deptList.value = response.data || []
      console.log('部门列表获取成功，共', deptList.value.length, '个部门')
    } else {
      console.error('部门列表接口返回错误码:', response.code, '消息:', response.msg)
      deptList.value = []
    }
    
    // 非管理员自动填充部门信息
    if (!isAdmin.value && userStore.deptId) {
      form.deptId = userStore.deptId
      form.deptName = userStore.deptName || '当前部门'
      console.log('自动填充部门信息:', { deptId: form.deptId, deptName: form.deptName })
    }
  } catch (error) {
    console.error('获取部门列表失败 - 详细错误信息:', error)
    deptList.value = []
    
    // 非管理员直接用用户信息，管理员提示错误
    if (!isAdmin.value && userStore.deptId) {
      form.deptId = userStore.deptId
      form.deptName = userStore.deptName || '当前部门'
    } else if (isAdmin.value) {
      proxy?.$modal?.msgWarning('获取部门列表失败，部分功能可能受限，请联系管理员')
    }
  }
}

/** 新增按钮操作 */
function handleAdd() {
  resetForm()
  open.value = true
  title.value = "添加商品"

  // 管理员加载部门列表，非管理员直接用用户部门信息
  if (isAdmin.value && deptList.value.length === 0) {
    getDeptList()
  }
}

/** 修改按钮操作 */
async function handleUpdate(row) {
  console.log('开始修改商品，传入行数据:', row)
  resetForm()
  const targetId = row?.id || ids.value[0]
  if (!targetId) {
    console.warn('未找到目标商品ID')
    return
  }

  try {
    console.log('获取商品详情，ID:', targetId)
    const response = await getProduct(targetId)
    console.log('商品详情接口返回:', response)
    
    const productData = response.data || {}
    console.log('商品数据:', productData)
    Object.assign(form, productData)

    // 解析显卡信息（增加空值保护）
    const fullGraphicsInfo = form.graphicsCard || ''
    if (fullGraphicsInfo) {
      // 预处理字符串
      let processedInfo = fullGraphicsInfo
        .replace(/([\u4e00-\u9fa5])([A-Z])/g, '$1 $2') // 中文后加空格
        .replace(/(RTX\s*\d+)(Ti)/gi, '$1 $2')
        .replace(/(RTX|RX|GT)\s*(\d+)/gi, '$1 $2')
      
      const cardParts = processedInfo.trim().split(/\s+/)
      if (cardParts.length) {
        form.graphicsCardBrand = cardParts[0]
        
        // 提取型号部分
        const modelEndIndex = cardParts.findIndex((part, index) => {
          if (index === 0) return false
          const upperPart = part.toUpperCase()
          return !["NVIDIA", "AMD", "INTEL", "GEFORCE", "RADEON", "ARC", "RTX", "RX", "GT", "TI", "SUPER"].includes(upperPart) && !/^\d+$/.test(part)
        })
        
        const modelParts = modelEndIndex > 0 ? cardParts.slice(1, modelEndIndex) : cardParts.slice(1)
        form.graphicsCardModel = modelParts.join(' ')
        form.graphicsCardSpecific = modelEndIndex > 0 ? cardParts.slice(modelEndIndex).join(' ') : ''
      }
    }

    // 解析内存信息（增加正则匹配容错）
    const memoryText = form.memory || ''
    const memoryRegex = /^(.*?)(?:\s+(DDR\d+))?(?:\s+(\d+))?(\s*MHz)?$/i
    const match = memoryRegex.exec(memoryText.trim())
    if (match) {
      form.memoryBrandAndModel = match[1]?.trim() || ''
      form.memoryType = match[2] || null
      form.memoryFrequency = match[3] ? parseInt(match[3], 10) : null
    }

    // 部门信息回显
    if (form.deptId && !isAdmin.value) {
      form.deptName = userStore.deptName || '当前部门'
    } else if (form.deptId && isAdmin.value && deptList.value.length === 0) {
      await getDeptList()
      const currentDept = deptList.value.find(d => d.deptId === form.deptId)
      form.deptName = currentDept?.deptName
    }

    open.value = true
    title.value = "修改商品"
    console.log('修改对话框已打开')
  } catch (error) {
    console.error('获取商品详情失败 - 详细错误:', error)
    proxy?.$modal?.msgError(`获取商品详情失败: ${error.message || '未知错误'}`)
  }
}

/** 获取CPU列表 */
async function getCpuList() {
  try {
    const response = await listCpu({})
    cpuList.value = response.rows || []
  } catch (error) {
    console.error('获取CPU列表失败:', error)
  }
}

/** 获取GPU列表 */
async function getGpuList() {
  try {
    const response = await listGpu({})
    gpuList.value = (response.rows || []).map(item => ({
      brand: item.brand,
      model: item.model
    }))
  } catch (error) {
    console.error('获取GPU列表失败:', error)
  }
}

/** 获取内存类型和频率列表 */
async function getMemoryList() {
  try {
    const response = await listMemory({ pageNum: 1, pageSize: 1000 })
    const memoryList = response.rows || []
    
    // 去重并格式化类型和频率
    const types = [...new Set(memoryList.map(item => item.type).filter(Boolean))]
    const frequencies = [...new Set(memoryList.map(item => item.frequency).filter(Boolean))]
    
    memoryTypeList.value = types.map(type => ({ type }))
    memoryFrequencyList.value = frequencies.map(freq => ({ frequency: freq }))
  } catch (error) {
    console.error('获取内存列表失败:', error)
  }
}

/** GPU模糊搜索 */
async function remoteGpuMethod(query) {
  if (!query) return
  gpuLoading.value = true
  try {
    const response = await listGpu({ model: query })
    gpuList.value = (response.rows || []).map(item => ({
      brand: item.brand,
      model: item.model
    }))
  } catch (error) {
    console.error('搜索GPU失败:', error)
  } finally {
    gpuLoading.value = false
  }
}

/** CPU模糊搜索 */
async function remoteCpuMethod(query) {
  if (!query) return
  cpuLoading.value = true
  try {
    const response = await listCpu({ model: query })
    cpuList.value = response.rows || []
  } catch (error) {
    console.error('搜索CPU失败:', error)
  } finally {
    cpuLoading.value = false
  }
}

/** 提交表单 */
async function submitForm() {
  try {
    const valid = await proxy?.$refs?.productRef?.validate()
    if (!valid) return

    // 拼接显卡信息（处理空值）
    const graphicsCardParts = [
      form.graphicsCardBrand,
      form.graphicsCardModel,
      form.graphicsCardSpecific
    ].filter(Boolean)
    form.graphicsCard = graphicsCardParts.join(' ')
    form.gpuModel = form.graphicsCardModel // 关联GPU型号

    // 拼接内存信息（处理空值）
    const memoryParts = [
      form.memoryBrandAndModel,
      form.memoryType,
      form.memoryFrequency ? `${form.memoryFrequency} MHz` : ''
    ].filter(Boolean)
    form.memory = memoryParts.join(' ')

    // 提交数据
    if (form.id) {
      await updateProduct(form)
      proxy?.$modal?.msgSuccess("修改成功")
    } else {
      await addProduct(form)
      proxy?.$modal?.msgSuccess("新增成功")
    }
    
    open.value = false
    getList()
  } catch (error) {
    console.error('提交表单失败:', error)
    proxy?.$modal?.msgError(form.id ? '修改失败' : '新增失败')
  }
}

/** 删除操作 */
async function handleDelete(row) {
  const targetIds = row?.id ? [row.id] : ids.value
  if (!targetIds.length) return

  try {
    await proxy?.$modal?.confirm('是否确认删除选中的数据项？')
    await delProduct(targetIds)
    proxy?.$modal?.msgSuccess("删除成功")
    getList()
  } catch (error) {
    console.error('删除失败:', error)
    if (error !== 'cancel') proxy?.$modal?.msgError('删除失败')
  }
}

/** 导出操作 */
function handleExport() {
  proxy?.download('system/product/export', { ...queryParams }, `product_${new Date().getTime()}.xlsx`)
}

/** 初始化函数 */
async function init() {
  // 判断是否为管理员
  isAdmin.value = userStore.roles?.includes('admin') || false
  console.log('初始化 - 用户角色:', userStore.roles, '是否为管理员:', isAdmin.value)
  console.log('初始化 - 用户部门信息:', { deptId: userStore.deptId, deptName: userStore.deptName })
  
  // 预加载基础数据
  Promise.all([
    getCpuList(),
    getGpuList(),
    getMemoryList()
  ]).catch(error => console.error('预加载硬件数据失败:', error))

  // 管理员加载部门列表，非管理员直接设置部门信息
  if (isAdmin.value) {
    console.log('管理员用户，加载部门列表')
    getDeptList()
  } else if (userStore.deptId) {
    console.log('非管理员用户，直接使用自身部门信息')
    form.deptId = userStore.deptId
    form.deptName = userStore.deptName || '当前部门'
  }
}

// 页面挂载时初始化
onMounted(() => {
  init()
  getList()
})
</script>