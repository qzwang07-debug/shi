<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="游戏名称(如: Black Myth: Wukong)" prop="gameName">
        <el-input
          v-model="queryParams.gameName"
          placeholder="请输入游戏名称(如: Black Myth: Wukong)"
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
          v-hasPermi="['system:gameInfo:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['system:gameInfo:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['system:gameInfo:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['system:gameInfo:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="gameInfoList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="游戏ID" align="center" prop="gameId" />
      <el-table-column label="游戏名称(如: Black Myth: Wukong)" align="center" prop="gameName" />
      <el-table-column label="游戏描述" align="center" prop="description" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['system:gameInfo:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['system:gameInfo:remove']">删除</el-button>
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

    <!-- 添加或修改游戏性能管理对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="gameInfoRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="游戏名称(如: Black Myth: Wukong)" prop="gameName">
          <el-input v-model="form.gameName" placeholder="请输入游戏名称(如: Black Myth: Wukong)" />
        </el-form-item>
        <el-form-item label="游戏描述" prop="description">
          <el-input v-model="form.description" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-divider content-position="center">游戏性能基准信息</el-divider>
        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button type="primary" icon="Plus" @click="handleAddSysGameBenchmark">添加</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" icon="Delete" @click="handleDeleteSysGameBenchmark">删除</el-button>
          </el-col>
        </el-row>
        <el-table :data="sysGameBenchmarkList" :row-class-name="rowSysGameBenchmarkIndex" @selection-change="handleSysGameBenchmarkSelectionChange" ref="sysGameBenchmark">
          <el-table-column type="selection" width="50" align="center" />
          <el-table-column label="序号" align="center" prop="index" width="50"/>
          <el-table-column label="基准类型(CPU_INTEL, CPU_AMD, GPU_NVIDIA, GPU_AMD)" prop="platformType" width="150">
            <template #default="scope">
              <el-select v-model="scope.row.platformType" placeholder="请选择基准类型(CPU_INTEL, CPU_AMD, GPU_NVIDIA, GPU_AMD)">
                <el-option
                  v-for="dict in sys_platform_type"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                ></el-option>
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="基准硬件跑分(分母)" prop="baseScore" width="150">
            <template #default="scope">
              <el-input v-model="scope.row.baseScore" placeholder="请输入基准硬件跑分(分母)" />
            </template>
          </el-table-column>
          <el-table-column label="基准测试帧率(分子)" prop="baseFps" width="150">
            <template #default="scope">
              <el-input v-model="scope.row.baseFps" placeholder="请输入基准测试帧率(分子)" />
            </template>
          </el-table-column>
          <el-table-column label="分辨率基准" prop="resolution" width="150">
            <template #default="scope">
              <el-input v-model="scope.row.resolution" placeholder="请输入分辨率基准" />
            </template>
          </el-table-column>
        </el-table>
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

<script setup name="GameInfo">
import { listGameInfo, getGameInfo, delGameInfo, addGameInfo, updateGameInfo } from "@/api/system/gameInfo"

const { proxy } = getCurrentInstance()
const { sys_platform_type } = proxy.useDict('sys_platform_type')

const gameInfoList = ref([])
const sysGameBenchmarkList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const checkedSysGameBenchmark = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    gameName: null,
    description: null
  },
  rules: {
    gameName: [
      { required: true, message: "游戏名称(如: Black Myth: Wukong)不能为空", trigger: "blur" }
    ],
  }
})

const { queryParams, form, rules } = toRefs(data)

/** 查询游戏性能管理列表 */
function getList() {
  loading.value = true
  listGameInfo(queryParams.value).then(response => {
    gameInfoList.value = response.rows
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
    gameId: null,
    gameName: null,
    description: null
  }
  sysGameBenchmarkList.value = []
  proxy.resetForm("gameInfoRef")
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
  ids.value = selection.map(item => item.gameId)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  open.value = true
  title.value = "添加游戏性能管理"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const _gameId = row.gameId || ids.value
  getGameInfo(_gameId).then(response => {
    form.value = response.data
    sysGameBenchmarkList.value = response.data.sysGameBenchmarkList
    open.value = true
    title.value = "修改游戏性能管理"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["gameInfoRef"].validate(valid => {
    if (valid) {
      form.value.sysGameBenchmarkList = sysGameBenchmarkList.value
      if (form.value.gameId != null) {
        updateGameInfo(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addGameInfo(form.value).then(response => {
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
  const _gameIds = row.gameId || ids.value
  proxy.$modal.confirm('是否确认删除游戏性能管理编号为"' + _gameIds + '"的数据项？').then(function() {
    return delGameInfo(_gameIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

/** 游戏性能基准序号 */
function rowSysGameBenchmarkIndex({ row, rowIndex }) {
  row.index = rowIndex + 1
}

/** 游戏性能基准添加按钮操作 */
function handleAddSysGameBenchmark() {
  let obj = {}
  obj.platformType = ""
  obj.baseScore = ""
  obj.baseFps = ""
  obj.resolution = ""
  sysGameBenchmarkList.value.push(obj)
}

/** 游戏性能基准删除按钮操作 */
function handleDeleteSysGameBenchmark() {
  if (checkedSysGameBenchmark.value.length == 0) {
    proxy.$modal.msgError("请先选择要删除的游戏性能基准数据")
  } else {
    const sysGameBenchmarks = sysGameBenchmarkList.value
    const checkedSysGameBenchmarks = checkedSysGameBenchmark.value
    sysGameBenchmarkList.value = sysGameBenchmarks.filter(function(item) {
      return checkedSysGameBenchmarks.indexOf(item.index) == -1
    })
  }
}

/** 复选框选中数据 */
function handleSysGameBenchmarkSelectionChange(selection) {
  checkedSysGameBenchmark.value = selection.map(item => item.index)
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('system/gameInfo/export', {
    ...queryParams.value
  }, `gameInfo_${new Date().getTime()}.xlsx`)
}

getList()
</script>
