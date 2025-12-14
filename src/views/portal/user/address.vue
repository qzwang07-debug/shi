<template>
  <div class="common-layout">
    <Header />
    
    <div class="main-content">
      <div class="content-wrapper">
        <!-- 页面标题区 -->
        <div class="page-header">
          <div class="header-left">
            <el-button link @click="goBack" class="back-btn">
              <el-icon><ArrowLeft /></el-icon> 返回
            </el-button>
            <h2>收货地址</h2>
            <span class="subtitle">管理您的配送信息</span>
          </div>
          <!-- 顶部新增按钮（可选，卡片区域也有新增入口） -->
          <el-button type="primary" icon="Plus" round class="main-add-btn" @click="handleAdd">
            新增地址
          </el-button>
        </div>

        <!-- 地址列表区域 -->
        <div class="address-grid">
          <!-- 新增地址卡片 (快捷入口) -->
          <div class="address-card add-card" @click="handleAdd">
            <div class="add-icon-wrapper">
              <el-icon><Plus /></el-icon>
            </div>
            <span class="add-text">添加新地址</span>
          </div>

          <!-- 现有地址循环 -->
          <div 
            v-for="item in addressList" 
            :key="item.addressId" 
            class="address-card"
            :class="{ 'is-default': item.isDefault === '1' }"
          >
            <!-- 默认地址角标 -->
            <div v-if="item.isDefault === '1'" class="default-badge">
              <span>默认</span>
            </div>

            <div class="card-body">
              <div class="user-info">
                <div class="avatar-placeholder">
                  {{ item.realName ? item.realName.charAt(0) : 'User' }}
                </div>
                <div class="text-info">
                  <div class="name">
                    {{ item.realName }}
                    <span class="phone">{{ item.phone }}</span>
                  </div>
                  <div class="tag-area">
                    <!-- 这里可以根据业务逻辑添加 家/公司 标签，目前仅作占位演示 -->
                    <span class="address-tag">地址</span>
                  </div>
                </div>
              </div>

              <div class="divider"></div>

              <div class="address-details">
                <div class="detail-item">
                  <el-icon><LocationInformation /></el-icon>
                  <span>{{ item.province }} {{ item.city }} {{ item.district }}</span>
                </div>
                <div class="detail-item full-address">
                  <el-icon><OfficeBuilding /></el-icon>
                  <span>{{ item.detailAddress }}</span>
                </div>
              </div>
            </div>

            <!-- 底部操作栏 -->
            <div class="card-actions">
              <div class="action-btn edit" @click.stop="handleEdit(item)">
                <el-icon><Edit /></el-icon> 编辑
              </div>
              <div class="action-btn delete" @click.stop="handleDelete(item)">
                <el-icon><Delete /></el-icon> 删除
              </div>
            </div>
          </div>
        </div>
        
        <!-- 空状态 -->
        <el-empty v-if="addressList.length === 0" description="暂无收货地址，快去添加一个吧" />
      </div>
    </div>

    <!-- 添加/修改地址对话框 (保持原有逻辑，优化样式) -->
    <el-dialog 
      :title="title" 
      v-model="open" 
      width="550px" 
      append-to-body
      class="custom-dialog"
      destroy-on-close
    >
      <el-form ref="addressRef" :model="form" :rules="rules" label-width="90px" label-position="top">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="收货人姓名" prop="realName">
              <el-input v-model="form.realName" placeholder="请填写收货人姓名" prefix-icon="User" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号码" prop="phone">
              <el-input v-model="form.phone" placeholder="请填写11位手机号" maxlength="11" prefix-icon="Iphone" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="所在地区" prop="region" class="region-form-item">
           <div class="region-inputs">
             <el-input v-model="form.province" placeholder="省份" />
             <span class="divider-dash">-</span>
             <el-input v-model="form.city" placeholder="城市" />
             <span class="divider-dash">-</span>
             <el-input v-model="form.district" placeholder="区/县" />
           </div>
        </el-form-item>

        <el-form-item label="详细地址" prop="detailAddress">
          <el-input 
            v-model="form.detailAddress" 
            type="textarea" 
            :rows="3" 
            placeholder="街道、楼牌号等" 
            resize="none"
          />
        </el-form-item>

        <el-form-item>
          <div class="switch-wrapper">
             <span class="switch-label">设为默认收货地址</span>
             <el-switch v-model="form.isDefault" active-value="1" inactive-value="0" />
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="cancel">取 消</el-button>
          <el-button type="primary" @click="submitForm">保存地址</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Plus, Edit, Delete, LocationInformation, OfficeBuilding, User, Iphone, ArrowLeft } from '@element-plus/icons-vue';
import { listAddress, addAddress, updateAddress, delAddress } from "@/api/portal/address";
import { getAppToken } from '@/utils/auth';
import Header from '@/views/computerMarket/Header.vue';

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
    // 排序：默认地址排在最前面
    const data = response.data || [];
    addressList.value = data.sort((a, b) => b.isDefault - a.isDefault);
  }).catch(error => {
    console.error(error);
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
  title.value = "新增收货地址";
}

function handleEdit(row) {
  if (!checkLogin()) return;
  reset();
  Object.assign(form, row);
  open.value = true;
  title.value = "编辑收货地址";
}

function submitForm() {
  if (!checkLogin()) return;
  addressRef.value.validate(valid => {
    if (valid) {
      if(!form.province || !form.city || !form.district) {
        ElMessage.warning("请补全省市区信息");
        return;
      }
      const req = form.addressId ? updateAddress(form) : addAddress(form);
      req.then(() => {
        ElMessage.success(form.addressId ? "修改成功" : "新增成功");
        open.value = false;
        getList();
      });
    }
  });
}

function handleDelete(row) {
  if (!checkLogin()) return;
  ElMessageBox.confirm('确认要删除该地址吗？', '系统提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    delAddress(row.addressId).then(() => {
      ElMessage.success("删除成功");
      getList();
    });
  });
}

// 返回上一页
const goBack = () => {
  router.back();
};

onMounted(() => {
  checkLogin();
  getList();
});
</script>

<style scoped>
/* 全局容器 */
.common-layout {
  min-height: 100vh;
  background-color: #f5f7fa;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
}

.main-content {
  padding-top: 80px; /* Header space */
  padding-bottom: 60px;
}

.content-wrapper {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

/* 面包屑样式 */
.breadcrumb-container {
  padding: 20px 0;
  margin-bottom: 20px;
  background: transparent;
}

/* 页头 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 30px;
}

.page-header h2 {
  font-size: 28px;
  color: #303133;
  margin: 0;
  font-weight: 600;
}

.page-header .subtitle {
  font-size: 14px;
  color: #909399;
  margin-top: 5px;
  display: block;
}

.main-add-btn {
  background-color: #626aef;
  border-color: #626aef;
  box-shadow: 0 4px 12px rgba(98, 106, 239, 0.3);
  padding: 10px 24px;
}

.main-add-btn:hover {
  background-color: #7b83f5;
  border-color: #7b83f5;
}

/* 网格布局 */
.address-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 25px;
}

/* 通用卡片样式 */
.address-card {
  background: #ffffff;
  border-radius: 12px;
  position: relative;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
  border: 1px solid #ebeef5;
  display: flex;
  flex-direction: column;
}

.address-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
  border-color: #d9ecff;
}

/* 新增地址卡片 (虚线框) */
.add-card {
  border: 2px dashed #dcdfe6;
  background-color: #fcfcfc;
  cursor: pointer;
  justify-content: center;
  align-items: center;
  min-height: 220px;
  color: #909399;
}

.add-card:hover {
  border-color: #626aef;
  color: #626aef;
  background-color: #f5f6ff;
}

.add-icon-wrapper {
  font-size: 40px;
  margin-bottom: 15px;
  background: #fff;
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(0,0,0,0.05);
}

.add-text {
  font-size: 16px;
  font-weight: 500;
}

/* 默认地址高亮 */
.address-card.is-default {
  border: 1px solid #626aef;
  background: #fbfbff;
}

/* 默认角标 */
.default-badge {
  position: absolute;
  top: 0;
  right: 0;
  width: 0;
  height: 0;
  border-top: 50px solid #626aef;
  border-left: 50px solid transparent; 
  z-index: 1;
}

.default-badge span {
  position: absolute;
  top: -42px;
  right: 4px;
  color: white;
  font-size: 12px;
  font-weight: bold;
  transform: rotate(45deg);
}

/* 卡片内容区 */
.card-body {
  padding: 24px 24px 0;
  flex: 1;
}

.user-info {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.avatar-placeholder {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #e0eafc, #cfdef3);
  color: #5c6b8f;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  font-weight: bold;
  margin-right: 15px;
}

.address-card.is-default .avatar-placeholder {
  background: linear-gradient(135deg, #626aef, #858bf5);
  color: #fff;
}

.text-info .name {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
  line-height: 1.2;
}

.text-info .phone {
  font-size: 14px;
  color: #909399;
  font-weight: normal;
  margin-left: 8px;
}

.tag-area {
  margin-top: 4px;
}

.address-tag {
  font-size: 11px;
  background: #f0f2f5;
  color: #909399;
  padding: 2px 6px;
  border-radius: 4px;
}

.divider {
  height: 1px;
  background: #f0f2f5;
  margin: 0 0 15px;
}

.address-details {
  color: #606266;
  font-size: 14px;
}

.detail-item {
  display: flex;
  align-items: flex-start;
  margin-bottom: 8px;
  line-height: 1.5;
}

.detail-item .el-icon {
  margin-right: 8px;
  margin-top: 3px;
  color: #aeb2bb;
}

.full-address {
  color: #303133;
  font-weight: 500;
}

/* 底部操作区 */
.card-actions {
  display: flex;
  border-top: 1px solid #f0f2f5;
  margin-top: 20px;
}

.action-btn {
  flex: 1;
  text-align: center;
  padding: 12px 0;
  font-size: 13px;
  cursor: pointer;
  color: #909399;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 5px;
}

.action-btn:hover {
  background-color: #f9fafe;
}

.action-btn.edit:hover {
  color: #626aef;
}

.action-btn.delete:hover {
  color: #f56c6c;
}

.action-btn:not(:last-child) {
  border-right: 1px solid #f0f2f5;
}

/* 对话框样式调整 */
.region-inputs {
  display: flex;
  align-items: center;
  gap: 10px;
  width: 100%;
}

.divider-dash {
  color: #dcdfe6;
}

.switch-wrapper {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  background: #f5f7fa;
  padding: 10px 15px;
  border-radius: 4px;
}

.switch-label {
  color: #606266;
}

/* 响应式调整 */
@media screen and (max-width: 768px) {
  .address-grid {
    grid-template-columns: 1fr;
  }
}
</style>