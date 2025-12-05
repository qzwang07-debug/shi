<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="订单编号" prop="orderNo">
        <el-input
          v-model="queryParams.orderNo"
          placeholder="请输入订单编号"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option label="待支付" value="0" />
          <el-option label="待发货" value="1" />
          <el-option label="已发货/租赁中" value="2" />
          <el-option label="已完成" value="3" />
          <el-option label="已取消" value="4" />
          <el-option label="退款审核中" value="5" />
          <el-option label="已退款" value="6" />
          <el-option label="归还中" value="7" />
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
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['system:order:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="shopOrderList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="订单编号" align="center" prop="orderNo" width="180" />
      <el-table-column label="总金额" align="center" prop="totalAmount" />
      <el-table-column label="状态" align="center" prop="status">
        <template #default="scope">
           <el-tag :type="statusType(scope.row.status)">{{ statusText(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="收货人" align="center" prop="receiverName" />
      <el-table-column label="电话" align="center" prop="receiverPhone" />
      <el-table-column label="下单时间" align="center" prop="createTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Search" @click="handleDetail(scope.row)">详情</el-button>
          
          <!-- 发货按钮：仅状态1显示 (待发货) -->
          <el-button
            v-if="scope.row.status === '1'"
            link
            type="success"
            icon="Van"
            @click="handleShip(scope.row)"
          >发货</el-button>

          <!-- 确认收货按钮：仅状态7显示 (归还中) -->
          <el-button
            v-if="scope.row.status === '7'"
            link
            type="warning"
            icon="Checked"
            @click="handleConfirmReturn(scope.row)"
          >确认收货</el-button>
          
          <!-- 退款审核按钮：仅状态5显示 (退款审核中) -->
          <template v-if="scope.row.status === '5'">
            <el-button
              link type="success" icon="Check"
              @click="handleAudit(scope.row, true)"
            >同意退款</el-button>
            <el-button
              link type="danger" icon="Close"
              @click="handleAudit(scope.row, false)"
            >拒绝退款</el-button>
          </template>
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
  </div>
</template>

<script setup name="ShopOrder">
import { listShopOrder, getShopOrder, delShopOrder, addShopOrder, updateShopOrder } from "@/api/system/ShopOrder";
import request from '@/utils/request';
import { ElMessage, ElMessageBox } from 'element-plus';
import { auditRefund } from "@/api/system/ShopOrder";
const { proxy } = getCurrentInstance();

const shopOrderList = ref([]);
const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref("");

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    orderNo: null,
    userId: null,
    status: null,
    receiverName: null,
    receiverPhone: null,
    deptId: null
  },
  rules: {
  }
});

const { queryParams, form, rules } = toRefs(data);

function statusText(status) {
  const map = {
    '0': '待支付',
    '1': '待发货',
    '2': '进行中',
    '3': '已完成',
    '4': '已取消',
    '5': '退款审核中',
    '6': '已退款',
    '7': '归还中'
  };
  return map[status] || status;
}

function statusType(status) {
  const map = {
    '0': 'danger',
    '1': 'warning',
    '2': 'primary',
    '3': 'success',
    '4': 'info',
    '5': 'warning',
    '6': 'info',
    '7': 'info'
  };
  return map[status] || '';
}

/** 查询列表 - 记得后端接口路径已改为 /merchant/order/list，需确认 api.js */
function getList() {
  loading.value = true;
  // 临时直接调用，防止 api.js 未更新
  request({
    url: '/merchant/order/list',
    method: 'get',
    params: queryParams.value
  }).then(response => {
    shopOrderList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

/** 搜索 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

/** 重置 */
function resetQuery() {
  proxy.resetForm("queryRef");
  handleQuery();
}

/** 选中 */
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.orderId);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 详情 */
function handleDetail(row) {
  proxy.$alert("功能开发中，订单ID：" + row.orderId);
}

/** 发货操作 */
function handleShip(row) {
  ElMessageBox.confirm('确认要为订单 "' + row.orderNo + '" 执行发货操作吗？', "系统提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning"
  }).then(() => {
    return request({
        url: `/merchant/order/ship/${row.orderId}`,
        method: 'put'
    });
  }).then(() => {
    getList();
    ElMessage.success("发货成功");
  }).catch(() => {});
}

/** 确认归还 */
function handleConfirmReturn(row) {
  ElMessageBox.confirm('确认收到该订单的归还物品吗？', "系统提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning"
  }).then(() => {
    return request({
        url: `/merchant/order/confirmReturn/${row.orderId}`,
        method: 'put'
    });
  }).then(() => {
    getList();
    ElMessage.success("确认收货成功");
  }).catch(() => {});
}

/** 导出 */
function handleExport() {
  proxy.download('merchant/order/export', {
    ...queryParams.value
  }, `shop_order_${new Date().getTime()}.xlsx`)
}
// 审核处理函数
function handleAudit(row, pass) {
  const actionText = pass ? "同意退款" : "拒绝退款";
  proxy.$modal.confirm(`确认要"${actionText}"订单 ${row.orderNo} 吗？`).then(function() {
    // 构造参数，注意这里传给后端的对象结构
    return auditRefund({ orderId: row.orderId, pass: pass });
  }).then(() => {
    proxy.$modal.msgSuccess("操作成功");
    getList();
  }).catch(() => {});
}
getList();
</script>