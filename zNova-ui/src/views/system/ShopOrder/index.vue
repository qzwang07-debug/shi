<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="订单编号(唯一)" prop="orderNo">
        <el-input
          v-model="queryParams.orderNo"
          placeholder="请输入订单编号(唯一)"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="用户ID" prop="userId">
        <el-input
          v-model="queryParams.userId"
          placeholder="请输入用户ID"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="订单总金额" prop="totalAmount">
        <el-input
          v-model="queryParams.totalAmount"
          placeholder="请输入订单总金额"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="支付时间" prop="payTime">
        <el-date-picker clearable
          v-model="queryParams.payTime"
          type="date"
          value-format="YYYY-MM-DD"
          placeholder="请选择支付时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="收货人" prop="receiverName">
        <el-input
          v-model="queryParams.receiverName"
          placeholder="请输入收货人"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="收货电话" prop="receiverPhone">
        <el-input
          v-model="queryParams.receiverPhone"
          placeholder="请输入收货电话"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="部门ID" prop="deptId">
        <el-input
          v-model="queryParams.deptId"
          placeholder="请输入部门ID"
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
          v-hasPermi="['system:ShopOrder:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['system:ShopOrder:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['system:ShopOrder:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['system:ShopOrder:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="ShopOrderList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="${comment}" align="center" prop="orderId" />
      <el-table-column label="订单编号(唯一)" align="center" prop="orderNo" />
      <el-table-column label="用户ID" align="center" prop="userId" />
      <el-table-column label="订单总金额" align="center" prop="totalAmount" />
      <el-table-column label="状态(0待支付 1待发货 2租赁中/已发货 3已完成/已归还 4已取消)" align="center" prop="status" />
      <el-table-column label="支付方式(1微信 2支付宝)" align="center" prop="payType" />
      <el-table-column label="支付时间" align="center" prop="payTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.payTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="收货人" align="center" prop="receiverName" />
      <el-table-column label="收货电话" align="center" prop="receiverPhone" />
      <el-table-column label="收货完整地址" align="center" prop="receiverAddress" />
      <el-table-column label="用户备注" align="center" prop="remark" />
      <el-table-column label="部门ID" align="center" prop="deptId" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['system:ShopOrder:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['system:ShopOrder:remove']">删除</el-button>
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

    <!-- 添加或修改订单主对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="ShopOrderRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="订单编号(唯一)" prop="orderNo">
          <el-input v-model="form.orderNo" placeholder="请输入订单编号(唯一)" />
        </el-form-item>
        <el-form-item label="用户ID" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入用户ID" />
        </el-form-item>
        <el-form-item label="订单总金额" prop="totalAmount">
          <el-input v-model="form.totalAmount" placeholder="请输入订单总金额" />
        </el-form-item>
        <el-form-item label="支付时间" prop="payTime">
          <el-date-picker clearable
            v-model="form.payTime"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="请选择支付时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="收货人" prop="receiverName">
          <el-input v-model="form.receiverName" placeholder="请输入收货人" />
        </el-form-item>
        <el-form-item label="收货电话" prop="receiverPhone">
          <el-input v-model="form.receiverPhone" placeholder="请输入收货电话" />
        </el-form-item>
        <el-form-item label="收货完整地址" prop="receiverAddress">
          <el-input v-model="form.receiverAddress" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="用户备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="部门ID" prop="deptId">
          <el-input v-model="form.deptId" placeholder="请输入部门ID" />
        </el-form-item>
        <el-divider content-position="center">订单明细信息</el-divider>
        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button type="primary" icon="Plus" @click="handleAddShopOrderItem">添加</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" icon="Delete" @click="handleDeleteShopOrderItem">删除</el-button>
          </el-col>
        </el-row>
        <el-table :data="shopOrderItemList" :row-class-name="rowShopOrderItemIndex" @selection-change="handleShopOrderItemSelectionChange" ref="shopOrderItem">
          <el-table-column type="selection" width="50" align="center" />
          <el-table-column label="序号" align="center" prop="index" width="50"/>
          <el-table-column label="商品ID" prop="productId" width="150">
            <template #default="scope">
              <el-input v-model="scope.row.productId" placeholder="请输入商品ID" />
            </template>
          </el-table-column>
          <el-table-column label="商品名称快照" prop="productName" width="150">
            <template #default="scope">
              <el-input v-model="scope.row.productName" placeholder="请输入商品名称快照" />
            </template>
          </el-table-column>
          <el-table-column label="类型(1租赁 2购买)" prop="businessType" width="150">
            <template #default="scope">
              <el-select v-model="scope.row.businessType" placeholder="请选择类型(1租赁 2购买)">
                <el-option label="请选择字典生成" value="" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="成交单价(日租金或售价)" prop="price" width="150">
            <template #default="scope">
              <el-input v-model="scope.row.price" placeholder="请输入成交单价(日租金或售价)" />
            </template>
          </el-table-column>
          <el-table-column label="数量" prop="quantity" width="150">
            <template #default="scope">
              <el-input v-model="scope.row.quantity" placeholder="请输入数量" />
            </template>
          </el-table-column>
          <el-table-column label="起租时间" prop="rentStartTime" width="240">
            <template #default="scope">
              <el-date-picker clearable
                v-model="scope.row.rentStartTime"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="请选择起租时间">
              </el-date-picker>
            </template>
          </el-table-column>
          <el-table-column label="归还时间" prop="rentEndTime" width="240">
            <template #default="scope">
              <el-date-picker clearable
                v-model="scope.row.rentEndTime"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="请选择归还时间">
              </el-date-picker>
            </template>
          </el-table-column>
          <el-table-column label="租期(天)" prop="rentDays" width="150">
            <template #default="scope">
              <el-input v-model="scope.row.rentDays" placeholder="请输入租期(天)" />
            </template>
          </el-table-column>
          <el-table-column label="部门ID" prop="deptId" width="150">
            <template #default="scope">
              <el-input v-model="scope.row.deptId" placeholder="请输入部门ID" />
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

<script setup name="ShopOrder">
import { listShopOrder, getShopOrder, delShopOrder, addShopOrder, updateShopOrder } from "@/api/system/ShopOrder"

const { proxy } = getCurrentInstance()

const ShopOrderList = ref([])
const shopOrderItemList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const checkedShopOrderItem = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    orderNo: null,
    userId: null,
    totalAmount: null,
    status: null,
    payType: null,
    payTime: null,
    receiverName: null,
    receiverPhone: null,
    receiverAddress: null,
    deptId: null
  },
  rules: {
    orderNo: [
      { required: true, message: "订单编号(唯一)不能为空", trigger: "blur" }
    ],
    userId: [
      { required: true, message: "用户ID不能为空", trigger: "blur" }
    ],
    totalAmount: [
      { required: true, message: "订单总金额不能为空", trigger: "blur" }
    ],
  }
})

const { queryParams, form, rules } = toRefs(data)

/** 查询订单主列表 */
function getList() {
  loading.value = true
  listShopOrder(queryParams.value).then(response => {
    ShopOrderList.value = response.rows
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
    orderId: null,
    orderNo: null,
    userId: null,
    totalAmount: null,
    status: null,
    payType: null,
    payTime: null,
    receiverName: null,
    receiverPhone: null,
    receiverAddress: null,
    remark: null,
    createTime: null,
    updateTime: null,
    deptId: null
  }
  shopOrderItemList.value = []
  proxy.resetForm("ShopOrderRef")
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
  ids.value = selection.map(item => item.orderId)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  open.value = true
  title.value = "添加订单主"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const _orderId = row.orderId || ids.value
  getShopOrder(_orderId).then(response => {
    form.value = response.data
    shopOrderItemList.value = response.data.shopOrderItemList
    open.value = true
    title.value = "修改订单主"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["ShopOrderRef"].validate(valid => {
    if (valid) {
      form.value.shopOrderItemList = shopOrderItemList.value
      if (form.value.orderId != null) {
        updateShopOrder(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addShopOrder(form.value).then(response => {
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
  const _orderIds = row.orderId || ids.value
  proxy.$modal.confirm('是否确认删除订单主编号为"' + _orderIds + '"的数据项？').then(function() {
    return delShopOrder(_orderIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

/** 订单明细序号 */
function rowShopOrderItemIndex({ row, rowIndex }) {
  row.index = rowIndex + 1
}

/** 订单明细添加按钮操作 */
function handleAddShopOrderItem() {
  let obj = {}
  obj.productId = ""
  obj.productName = ""
  obj.productImg = ""
  obj.businessType = ""
  obj.price = ""
  obj.quantity = ""
  obj.rentStartTime = ""
  obj.rentEndTime = ""
  obj.rentDays = ""
  obj.deptId = ""
  shopOrderItemList.value.push(obj)
}

/** 订单明细删除按钮操作 */
function handleDeleteShopOrderItem() {
  if (checkedShopOrderItem.value.length == 0) {
    proxy.$modal.msgError("请先选择要删除的订单明细数据")
  } else {
    const shopOrderItems = shopOrderItemList.value
    const checkedShopOrderItems = checkedShopOrderItem.value
    shopOrderItemList.value = shopOrderItems.filter(function(item) {
      return checkedShopOrderItems.indexOf(item.index) == -1
    })
  }
}

/** 复选框选中数据 */
function handleShopOrderItemSelectionChange(selection) {
  checkedShopOrderItem.value = selection.map(item => item.index)
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('system/ShopOrder/export', {
    ...queryParams.value
  }, `ShopOrder_${new Date().getTime()}.xlsx`)
}

getList()
</script>
