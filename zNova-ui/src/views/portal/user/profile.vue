<template>
  <div class="common-layout">
    <el-container class="app-container">
      <!-- 导入的头部导航栏组件 -->
      <Header />
      
      <!-- 主要内容区域 -->
      <el-main class="main-content">
        <div class="profile-container">
          <!-- 头部信息区域 -->
          <el-card class="profile-header" shadow="hover">
      <div class="header-content">
        <!-- 左侧：头像 -->
        <div class="avatar-section">
          <el-upload
            class="avatar-uploader"
            action="/app/user/avatar"
            :headers="{ 'App-Token': appToken }"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
            :before-upload="beforeAvatarUpload"
          >
            <img v-if="userInfo.avatar" :src="userAvatar" @error="handleAvatarError" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
          <div class="avatar-hint">点击更换头像</div>
        </div>
        
        <!-- 中间：用户基本信息 -->
        <div class="user-info-section">
          <div class="user-main-info">
            <h2 class="nickname">{{ userInfo.nickname || '未设置昵称' }}</h2>
            <span class="username">账号：{{ userInfo.username }}</span>
            <el-tag type="success" size="small" class="auth-tag">已实名认证</el-tag>
          </div>
          <div class="user-detail-info">
            <div class="info-item">
              <el-icon><Phone /></el-icon>
              <span>{{ userInfo.phonenumber || '未设置手机号' }}</span>
            </div>
            <div class="info-item">
              <el-icon><User /></el-icon>
              <span>{{ userInfo.sex === '0' ? '男' : userInfo.sex === '1' ? '女' : '未知' }}</span>
            </div>
          </div>
        </div>
        
        <!-- 右侧：信用分和余额 -->
        <div class="credit-section">
          <div class="credit-score">
            <el-progress
              type="dashboard"
              :percentage="userInfo.creditScore || 700"
              :color="creditColor"
              :width="120"
              :stroke-width="10"
            >
              <template #default>
                <div class="progress-content">
                  <div class="progress-text">信用分</div>
                  <div class="progress-value">{{ userInfo.creditScore || 700 }}</div>
                </div>
              </template>
            </el-progress>
          </div>
          <div class="balance-info">
            <div class="balance-label">账户余额</div>
            <div class="balance-amount">¥{{ userInfo.balance || 0 }}</div>
          </div>
        </div>
      </div>
    </el-card>
    
    <!-- 订单快捷入口区域 -->
    <el-card class="order-shortcuts" shadow="hover">
      <h3 class="section-title">订单管理</h3>
      <div class="shortcuts-grid">
        <div class="shortcut-item" @click="goToOrder(0)">
          <el-icon class="shortcut-icon"><Timer /></el-icon>
          <div class="shortcut-text">待支付</div>
        </div>
        <div class="shortcut-item" @click="goToOrder(1)">
          <el-icon class="shortcut-icon"><Van /></el-icon>
          <div class="shortcut-text">待发货</div>
        </div>
        <div class="shortcut-item" @click="goToOrder(2)">
          <el-icon class="shortcut-icon"><Box /></el-icon>
          <div class="shortcut-text">租赁中</div>
        </div>
        <div class="shortcut-item" @click="goToOrder(3)">
          <el-icon class="shortcut-icon"><List /></el-icon>
          <div class="shortcut-text">全部订单</div>
        </div>
      </div>
    </el-card>
    
    <!-- 功能列表区域 -->
    <el-card class="function-list" shadow="hover">
      <h3 class="section-title">账户设置</h3>
      <el-menu
        default-active="1"
        class="function-menu"
        mode="vertical"
        @select="handleMenuSelect"
      >
        <el-menu-item index="1">
          <template #title>
            <el-icon><User /></el-icon>
            <span>基本资料</span>
          </template>
        </el-menu-item>
        <el-menu-item index="2">
          <template #title>
            <el-icon><Location /></el-icon>
            <span>收货地址</span>
          </template>
        </el-menu-item>
        <el-menu-item index="3">
          <template #title>
            <el-icon><Lock /></el-icon>
            <span>修改密码</span>
          </template>
        </el-menu-item>
        <el-menu-item index="4">
          <template #title>
            <el-icon><SwitchButton /></el-icon>
            <span>退出登录</span>
          </template>
        </el-menu-item>
      </el-menu>
    </el-card>
    
    <!-- 基本资料弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      title="编辑基本资料"
      width="500px"
    >
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="昵称">
          <el-input v-model="editForm.nickname" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="editForm.sex">
            <el-radio label="0">男</el-radio>
            <el-radio label="1">女</el-radio>
            <el-radio label="2">未知</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="editForm.phonenumber" placeholder="请输入手机号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveProfile">保存</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 修改密码弹窗 -->
    <el-dialog
      v-model="pwdDialogVisible"
      title="修改密码"
      width="500px"
    >
      <el-form :model="pwdForm" label-width="100px" :rules="pwdRules" ref="pwdFormRef">
        <el-form-item label="旧密码" prop="oldPassword">
          <el-input type="password" v-model="pwdForm.oldPassword" placeholder="请输入旧密码" />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input type="password" v-model="pwdForm.newPassword" placeholder="请输入新密码" />
        </el-form-item>
        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input type="password" v-model="pwdForm.confirmPassword" placeholder="请确认新密码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="pwdDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="savePassword">保存</el-button>
        </span>
      </template>
    </el-dialog>
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Plus, Phone, User, Location, Lock, SwitchButton, Timer, Van, Box, List } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getInfo } from '@/api/portal/user'
import { updateProfile } from '@/api/portal/user'
import { updatePassword } from '@/api/portal/user'
import { getAppUserInfo } from '@/api/appLogin'
import Header from '@/views/computerMarket/Header.vue'

const router = useRouter()

// 用户信息
const userInfo = ref({
  username: '',
  nickname: '',
  avatar: '',
  phonenumber: '',
  sex: '2',
  creditScore: 700,
  balance: 0
})

// 默认头像
const defaultAvatar = '/img/profile.jpg'

// 编辑资料弹窗
const dialogVisible = ref(false)
const editForm = reactive({
  nickname: '',
  sex: '2',
  phonenumber: ''
})

// 修改密码弹窗
const pwdDialogVisible = ref(false)
const pwdFormRef = ref()
const pwdForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 密码验证规则
const pwdRules = {
  oldPassword: [{ required: true, message: '请输入旧密码', trigger: 'blur' }],
  newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: (rule, value, callback) => {
        if (value !== pwdForm.newPassword) {
          callback(new Error('两次输入密码不一致'))
        } else {
          callback()
        }
      }, trigger: 'blur' }
  ]
}

// 计算信用分颜色
const creditColor = computed(() => {
  const score = userInfo.value.creditScore || 700
  if (score >= 800) return '#67c23a'
  if (score >= 700) return '#e6a23c'
  return '#f56c6c'
})

// 计算用户头像
const userAvatar = computed(() => {
  if (userInfo.value && userInfo.value.avatar) {
    return userInfo.value.avatar
  }
  return defaultAvatar
})

// 获取App-Token
const appToken = computed(() => {
  return localStorage.getItem('app_token')
})

// 获取用户信息
const getUserInfo = async () => {
  const token = localStorage.getItem('app_token')
  if (token) {
    try {
      const res = await getAppUserInfo()
      userInfo.value = {
        ...userInfo.value,
        ...res.user,
        creditScore: res.user.creditScore || 700,
        balance: res.user.balance || 0
      }
    } catch (err) {
      console.error('获取用户信息失败:', err)
      localStorage.removeItem('app_token')
      ElMessage.error('登录状态已过期，请重新登录')
      router.push('/portal/login')
    }
  }
}

// 头像上传成功处理
const handleAvatarSuccess = (response) => {
  if (response.code === 200) {
    userInfo.value.avatar = response.data.avatar
    ElMessage.success('头像上传成功')
  } else {
    ElMessage.error('头像上传失败')
  }
}

// 处理头像加载失败
const handleAvatarError = (event) => {
  event.target.src = defaultAvatar
}

// 头像上传前验证
const beforeAvatarUpload = (file) => {
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPG) {
    ElMessage.error('上传头像图片只能是 JPG/PNG 格式!')
  }
  if (!isLt2M) {
    ElMessage.error('上传头像图片大小不能超过 2MB!')
  }
  return isJPG && isLt2M
}

// 菜单选择处理
const handleMenuSelect = (index) => {
  switch (index) {
    case '1':
      // 编辑基本资料
      editForm.nickname = userInfo.value.nickname
      editForm.sex = userInfo.value.sex
      editForm.phonenumber = userInfo.value.phonenumber
      dialogVisible.value = true
      break
    case '2':
      // 跳转到收货地址
      router.push('/portal/user/address')
      break
    case '3':
      // 修改密码
      pwdDialogVisible.value = true
      break
    case '4':
      // 退出登录
      handleLogout()
      break
  }
}

// 保存基本资料
const saveProfile = async () => {
  try {
    await updateProfile(editForm)
    await getUserInfo()
    dialogVisible.value = false
    ElMessage.success('基本资料修改成功')
  } catch (error) {
    ElMessage.error('基本资料修改失败')
  }
}

// 保存密码
const savePassword = async () => {
  if (!pwdFormRef.value) return
  await pwdFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await updatePassword(pwdForm)
        pwdDialogVisible.value = false
        pwdForm.oldPassword = ''
        pwdForm.newPassword = ''
        pwdForm.confirmPassword = ''
        ElMessage.success('密码修改成功')
      } catch (error) {
        ElMessage.error('密码修改失败')
      }
    }
  })
}

// 退出登录
const handleLogout = () => {
  localStorage.removeItem('app_token')
  userInfo.value = {
    username: '',
    nickname: '',
    avatar: '',
    phonenumber: '',
    sex: '2',
    creditScore: 700,
    balance: 0
  }
  ElMessage.success('退出登录成功')
  router.push('/computer-market')
}

// 跳转到订单页面
const goToOrder = (tabIndex) => {
  router.push({
    path: '/portal/user/order',
    query: { activeTab: tabIndex }
  })
}

// 页面挂载时获取用户信息
onMounted(() => {
  getUserInfo()
})
</script>

<style scoped>
/* 布局样式 */
.common-layout {
  min-height: 100vh;
}

.app-container {
  background-color: #FAFAF8;
  color: #000000;
  min-height: 100vh;
}

.main-content {
  background-color: #FAFAF8;
  padding: 20px;
  margin-top: 70px; /* 为固定导航栏留出空间 */
}

.profile-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

/* 头部信息样式 */
.profile-header {
  margin-bottom: 20px;
}

.header-content {
  display: flex;
  align-items: center;
  gap: 40px;
  padding: 20px 0;
}

/* 头像样式 */
.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.avatar-uploader {
  position: relative;
  width: 120px;
  height: 120px;
}

.avatar {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid #fff;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: all 0.3s ease;
}

.avatar:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 20px 0 rgba(0, 0, 0, 0.15);
}

.avatar-uploader-icon {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  background-color: #f5f7fa;
  border: 1px dashed #d9d9d9;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: #8c939d;
  transition: all 0.3s ease;
}

.avatar-uploader-icon:hover {
  border-color: #409eff;
  color: #409eff;
  background-color: #ecf5ff;
}

.avatar-hint {
  font-size: 12px;
  color: #909399;
}

/* 用户信息样式 */
.user-info-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.user-main-info {
  display: flex;
  align-items: center;
  gap: 15px;
  flex-wrap: wrap;
}

.nickname {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.username {
  font-size: 14px;
  color: #606266;
}

.auth-tag {
  margin-left: 10px;
}

.user-detail-info {
  display: flex;
  gap: 30px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #606266;
}

/* 信用分和余额样式 */
.credit-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
  padding: 0 30px;
  border-left: 1px solid #ebeef5;
}

.credit-score {
  position: relative;
}

.progress-content {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
}

.progress-text {
  font-size: 14px;
  color: #606266;
  margin-bottom: 5px;
}

.progress-value {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.balance-info {
  text-align: center;
}

.balance-label {
  font-size: 14px;
  color: #606266;
  margin-bottom: 5px;
}

.balance-amount {
  font-size: 28px;
  font-weight: 600;
  color: #e6a23c;
}

/* 订单快捷入口样式 */
.order-shortcuts {
  margin-bottom: 20px;
}

.section-title {
  margin: 0 0 20px 0;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  border-bottom: 2px solid #409eff;
  padding-bottom: 10px;
  display: inline-block;
}

.shortcuts-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.shortcut-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 25px 0;
  background-color: #f5f7fa;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid transparent;
}

.shortcut-item:hover {
  background-color: #ecf5ff;
  border-color: #409eff;
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
}

.shortcut-icon {
  font-size: 32px;
  color: #409eff;
  margin-bottom: 10px;
}

.shortcut-text {
  font-size: 14px;
  color: #303133;
  font-weight: 500;
}

/* 功能列表样式 */
.function-list {
  margin-bottom: 20px;
}

.function-menu {
  border: none;
  background: none;
}

.function-menu .el-menu-item {
  height: 60px;
  line-height: 60px;
  font-size: 16px;
  color: #303133;
  border-radius: 8px;
  margin-bottom: 8px;
  transition: all 0.3s ease;
}

.function-menu .el-menu-item:hover {
  background-color: #ecf5ff;
  color: #409eff;
}

.function-menu .el-menu-item.is-active {
  background-color: #ecf5ff;
  color: #409eff;
}

.function-menu .el-menu-item .el-icon {
  margin-right: 12px;
  font-size: 18px;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .header-content {
    flex-direction: column;
    text-align: center;
    gap: 20px;
  }
  
  .credit-section {
    border-left: none;
    border-top: 1px solid #ebeef5;
    padding-top: 20px;
  }
  
  .user-detail-info {
    justify-content: center;
  }
}

@media (max-width: 768px) {
  .shortcuts-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .user-main-info {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .user-detail-info {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
}
</style>