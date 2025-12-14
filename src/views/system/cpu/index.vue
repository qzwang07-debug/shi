<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="品牌" prop="brand">
        <el-select v-model="queryParams.brand" placeholder="请选择品牌" clearable>
          <el-option
            v-for="dict in cpu_brand"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="型号" prop="model">
        <el-input
          v-model="queryParams.model"
          placeholder="请输入型号"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="接口" prop="socketType">
        <el-select v-model="queryParams.socketType" placeholder="请选择接口" clearable>
          <el-option
            v-for="dict in cpu_socket_type"
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
          v-hasPermi="['system:cpu:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['system:cpu:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['system:cpu:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['system:cpu:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="cpuList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="${comment}" align="center" prop="id" />
      <el-table-column label="品牌" align="center" prop="brand">
        <template #default="scope">
          <dict-tag :options="cpu_brand" :value="scope.row.brand"/>
        </template>
      </el-table-column>
      <el-table-column label="型号" align="center" prop="model" />
      <el-table-column label="接口" align="center" prop="socketType">
        <template #default="scope">
          <dict-tag :options="cpu_socket_type" :value="scope.row.socketType"/>
        </template>
      </el-table-column>
      <el-table-column label="单核性能分" align="center" prop="singleCoreScore" />
      <el-table-column label="多核性能分" align="center" prop="multiCoreScore" />
      <el-table-column label="功耗(W)" align="center" prop="tdp" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['system:cpu:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['system:cpu:remove']">删除</el-button>
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

    <!-- 添加或修改CPU硬件对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="cpuRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="品牌" prop="brand">
          <el-select v-model="form.brand" placeholder="请选择品牌">
            <el-option
              v-for="dict in cpu_brand"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="型号" prop="model">
          <el-input v-model="form.model" placeholder="请输入型号" />
        </el-form-item>
        <el-form-item label="接口" prop="socketType">
          <el-select v-model="form.socketType" placeholder="请选择接口">
            <el-option
              v-for="dict in cpu_socket_type"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="单核性能分" prop="singleCoreScore">
          <el-input v-model="form.singleCoreScore" placeholder="请输入单核性能分" />
        </el-form-item>
        <el-form-item label="多核性能分" prop="multiCoreScore">
          <el-input v-model="form.multiCoreScore" placeholder="请输入多核性能分" />
        </el-form-item>
        <el-form-item label="功耗(W)" prop="tdp">
          <el-input v-model="form.tdp" placeholder="请输入功耗(W)" />
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

<script setup name="Cpu">
import { listCpu, getCpu, delCpu, addCpu, updateCpu } from "@/api/system/cpu"

const { proxy } = getCurrentInstance()
const { cpu_socket_type, cpu_brand } = proxy.useDict('cpu_socket_type', 'cpu_brand')

const cpuList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    brand: null,
    model: null,
    socketType: null,
  },
  rules: {
    brand: [
      { required: true, message: "品牌不能为空", trigger: "change" }
    ],
    model: [
      { required: true, message: "型号不能为空", trigger: "blur" }
    ],
    socketType: [
      { required: true, message: "接口不能为空", trigger: "change" }
    ],
    singleCoreScore: [
      { required: true, message: "单核性能分不能为空", trigger: "blur" }
    ],
    multiCoreScore: [
      { required: true, message: "多核性能分不能为空", trigger: "blur" }
    ],
    tdp: [
      { required: true, message: "功耗(W)不能为空", trigger: "blur" }
    ],
  }
})

const { queryParams, form, rules } = toRefs(data)

/** 查询CPU硬件列表 */
function getList() {
  loading.value = true
  listCpu(queryParams.value).then(response => {
    cpuList.value = response.rows
    total.value = response.total
    loading.value = false
  })
}

// 取消按钮
function cancel() {
  open.value = false
  reset()
}

// 表单重置
function reset() {
  form.value = {
    id: null,
    brand: null,
    model: null,
    socketType: null,
    singleCoreScore: null,
    multiCoreScore: null,
    tdp: null,
    createTime: null,
    updateTime: null
  }
  proxy.resetForm("cpuRef")
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef")
  handleQuery()
}

// 多选框选中数据
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  open.value = true
  title.value = "添加CPU硬件"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const _id = row.id || ids.value
  getCpu(_id).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改CPU硬件"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["cpuRef"].validate(valid => {
    if (valid) {
      if (form.value.id != null) {
        updateCpu(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addCpu(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功")
          open.value = false
          getList()
        })
      }
    }
  })
}

/** 删除按钮操作 */
function handleDelete(row) {
  const _ids = row.id || ids.value
  proxy.$modal.confirm('是否确认删除CPU硬件编号为"' + _ids + '"的数据项？').then(function() {
    return delCpu(_ids)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('system/cpu/export', {
    ...queryParams.value
  }, `cpu_${new Date().getTime()}.xlsx`)
}

getList()
</script>
