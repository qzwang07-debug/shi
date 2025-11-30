<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="关联游戏ID" prop="gameId">
        <el-input
          v-model="queryParams.gameId"
          placeholder="请输入关联游戏ID"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="内存代数(DDR4, DDR5)" prop="ramType">
        <el-select v-model="queryParams.ramType" placeholder="请选择内存代数(DDR4, DDR5)" clearable>
          <el-option
            v-for="dict in motherboard_memory_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="内存频率(MHz)" prop="frequency">
        <el-input
          v-model="queryParams.frequency"
          placeholder="请输入内存频率(MHz)"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="帧率比系数(如 1.208)" prop="ratio">
        <el-input
          v-model="queryParams.ratio"
          placeholder="请输入帧率比系数(如 1.208)"
          clearable
          @keyup.enter="handleQuery"
        />
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
          v-hasPermi="['system:ram:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['system:ram:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['system:ram:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['system:ram:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="ramList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="${comment}" align="center" prop="id" />
      <el-table-column label="关联游戏ID" align="center" prop="gameId" />
      <el-table-column label="内存代数(DDR4, DDR5)" align="center" prop="ramType">
        <template #default="scope">
          <dict-tag :options="motherboard_memory_type" :value="scope.row.ramType"/>
        </template>
      </el-table-column>
      <el-table-column label="内存频率(MHz)" align="center" prop="frequency" />
      <el-table-column label="帧率比系数(如 1.208)" align="center" prop="ratio" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['system:ram:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['system:ram:remove']">删除</el-button>
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

    <!-- 添加或修改内存性能曲线对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="ramRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="关联游戏ID" prop="gameId">
          <el-input v-model="form.gameId" placeholder="请输入关联游戏ID" />
        </el-form-item>
        <el-form-item label="内存代数(DDR4, DDR5)" prop="ramType">
          <el-select v-model="form.ramType" placeholder="请选择内存代数(DDR4, DDR5)">
            <el-option
              v-for="dict in motherboard_memory_type"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="内存频率(MHz)" prop="frequency">
          <el-input v-model="form.frequency" placeholder="请输入内存频率(MHz)" />
        </el-form-item>
        <el-form-item label="帧率比系数(如 1.208)" prop="ratio">
          <el-input v-model="form.ratio" placeholder="请输入帧率比系数(如 1.208)" />
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

<script setup name="Ram">
import { listRam, getRam, delRam, addRam, updateRam } from "@/api/system/ram"

const { proxy } = getCurrentInstance()
const { motherboard_memory_type } = proxy.useDict('motherboard_memory_type')

const ramList = ref([])
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
    gameId: null,
    ramType: null,
    frequency: null,
    ratio: null
  },
  rules: {
    gameId: [
      { required: true, message: "关联游戏ID不能为空", trigger: "blur" }
    ],
    ramType: [
      { required: true, message: "内存代数(DDR4, DDR5)不能为空", trigger: "change" }
    ],
    frequency: [
      { required: true, message: "内存频率(MHz)不能为空", trigger: "blur" }
    ],
    ratio: [
      { required: true, message: "帧率比系数(如 1.208)不能为空", trigger: "blur" }
    ]
  }
})

const { queryParams, form, rules } = toRefs(data)

/** 查询内存性能曲线列表 */
function getList() {
  loading.value = true
  listRam(queryParams.value).then(response => {
    ramList.value = response.rows
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
    gameId: null,
    ramType: null,
    frequency: null,
    ratio: null
  }
  proxy.resetForm("ramRef")
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
  title.value = "添加内存性能曲线"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const _id = row.id || ids.value
  getRam(_id).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改内存性能曲线"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["ramRef"].validate(valid => {
    if (valid) {
      if (form.value.id != null) {
        updateRam(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addRam(form.value).then(response => {
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
  proxy.$modal.confirm('是否确认删除内存性能曲线编号为"' + _ids + '"的数据项？').then(function() {
    return delRam(_ids)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('system/ram/export', {
    ...queryParams.value
  }, `ram_${new Date().getTime()}.xlsx`)
}

getList()
</script>
