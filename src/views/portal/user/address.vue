<template>
  <div class="app-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>我的收货地址</span>
          <el-button type="primary" icon="Plus" @click="handleAdd">新增地址</el-button>
        </div>
      </template>

      <el-empty v-if="addressList.length === 0" description="暂无收货地址" />

      <div v-else class="address-grid">
        <el-card 
          v-for="item in addressList" 
          :key="item.addressId" 
          class="address-item"
          :class="{ 'is-default': item.isDefault === '1' }"
          shadow="hover"
        >
          <div class="address-content">
            <div class="info-row name-row">
              <span class="name">{{ item.realName }}</span>
              <span class="phone">{{ item.phone }}</span>
              <el-tag v-if="item.isDefault === '1'" size="small" type="success" effect="dark">默认</el-tag>
            </div>
            <div class="info-row region-row">
              {{ item.province }} {{ item.city }} {{ item.district }}
            </div>
            <div class="info-row detail-row">
              {{ item.detailAddress }}
            </div>
          </div>
          <div class="address-actions">
            <el-button type="primary" link icon="Edit" @click="handleEdit(item)">编辑</el-button>
            <el-button type="danger" link icon="Delete" @click="handleDelete(item)">删除</el-button>
          </div>
        </el-card>
      </div>
    </el-card>

    <!-- 添加/修改地址对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="addressRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="收货人" prop="realName">
          <el-input v-model="form.realName" placeholder="请输入收货人姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号码" maxlength="11" />
        </el-form-item>
        <el-form-item label="所在地区" prop="region">
           <!-- 简化处理：实际项目中可以使用 el-cascader 配合行政区划数据 -->
           <div class="region-inputs">
             <el-input v-model="form.province" placeholder="省" style="width: 32%" />
             <el-input v-model="form.city" placeholder="市" style="width: 32%" />
             <el-input v-model="form.district" placeholder="区/县" style="width: 32%" />
           </div>
        </el-form-item>
        <el-form-item label="详细地址" prop="detailAddress">
          <el-input v-model="form.detailAddress" type="textarea" placeholder="请输入详细地址" />
        </el-form-item>
        <el-form-item label="默认地址" prop="isDefault">
          <el-switch v-model="form.isDefault" active-value="1" inactive-value="0" />
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

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { listAddress, addAddress, updateAddress, delAddress } from "@/api/portal/address";
import { getAppToken } from '@/utils/auth';

const router = useRouter();
const route = useRoute();

const addressList = ref([]);
const open = ref(false);
const title = ref("");
const addressRef = ref(null);

const form = reactive({
  addressId: undefined,
  realName: '',
  phone: '',
  province: '',
  city: '',
  district: '',
  detailAddress: '',
  isDefault: '0'
});

const rules = {
  realName: [{ required: true, message: "收货人不能为空", trigger: "blur" }],
  phone: [
    { required: true, message: "手机号不能为空", trigger: "blur" },
    { pattern: /^1[3-9]\d{9}$/, message: "请输入正确的手机号码", trigger: "blur" }
  ],
  detailAddress: [{ required: true, message: "详细地址不能为空", trigger: "blur" }]
};

// 检查登录状态
function checkLogin() {
  const token = getAppToken();
  if (!token) {
    ElMessage.error("请先登录");
    router.push('/portal/login?redirect=' + encodeURIComponent(route.fullPath));
    return false;
  }
  return true;
}

function getList() {
  if (!checkLogin()) return;
  
  listAddress().then(response => {
    addressList.value = response.data || [];
  }).catch(error => {
    console.error("Failed to fetch addresses", error);
    if (error && error.toString().includes('认证失败')) {
      ElMessage.error("登录状态已过期，请重新登录");
      router.push('/portal/login?redirect=' + encodeURIComponent(route.fullPath));
    }
  });
}

function cancel() {
  open.value = false;
  reset();
}

function reset() {
  form.addressId = undefined;
  form.realName = '';
  form.phone = '';
  form.province = '';
  form.city = '';
  form.district = '';
  form.detailAddress = '';
  form.isDefault = '0';
}

function handleAdd() {
  if (!checkLogin()) return;
  reset();
  open.value = true;
  title.value = "添加收货地址";
}

function handleEdit(row) {
  if (!checkLogin()) return;
  reset();
  Object.assign(form, row);
  open.value = true;
  title.value = "修改收货地址";
}

function submitForm() {
  if (!checkLogin()) return;
  
  addressRef.value.validate(valid => {
    if (valid) {
      // 简单校验地区
      if(!form.province || !form.city || !form.district) {
        ElMessage.warning("请补全省市区信息");
        return;
      }
      
      if (form.addressId) {
        updateAddress(form).then(() => {
          ElMessage.success("修改成功");
          open.value = false;
          getList();
        }).catch(error => {
          if (error && error.toString().includes('认证失败')) {
            ElMessage.error("登录状态已过期，请重新登录");
            router.push('/portal/login?redirect=' + encodeURIComponent(route.fullPath));
          }
        });
      } else {
        addAddress(form).then(() => {
          ElMessage.success("新增成功");
          open.value = false;
          getList();
        }).catch(error => {
          if (error && error.toString().includes('认证失败')) {
            ElMessage.error("登录状态已过期，请重新登录");
            router.push('/portal/login?redirect=' + encodeURIComponent(route.fullPath));
          }
        });
      }
    }
  });
}

function handleDelete(row) {
  if (!checkLogin()) return;
  
  ElMessageBox.confirm('是否确认删除该地址?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    delAddress(row.addressId).then(() => {
      ElMessage.success("删除成功");
      getList();
    }).catch(error => {
      if (error && error.toString().includes('认证失败')) {
        ElMessage.error("登录状态已过期，请重新登录");
        router.push('/portal/login?redirect=' + encodeURIComponent(route.fullPath));
      }
    });
  });
}

onMounted(() => {
  checkLogin();
  getList();
});
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.address-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.address-item {
  position: relative;
  border: 1px solid #e4e7ed;
  transition: all 0.3s;
}

.address-item.is-default {
  border-color: #67c23a;
  background-color: #f0f9eb;
}

.address-content {
  font-size: 14px;
  color: #606266;
}

.info-row {
  margin-bottom: 8px;
}

.name-row {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 10px;
}

.address-actions {
  border-top: 1px solid #ebeef5;
  margin-top: 15px;
  padding-top: 10px;
  text-align: right;
}

.region-inputs {
  display: flex;
  justify-content: space-between;
}
</style>