<template>
  <div class="common-layout">
    <Header />
    
    <div class="main-content">
      <div class="dashboard-container">
        
        <!-- 顶部：个人信息主卡片 -->
        <div class="profile-card">
          <div class="profile-bg"></div>
          <div class="profile-body">
            <!-- 头像区域 -->
            <div class="avatar-wrapper">
              <el-upload
                class="avatar-uploader"
                :action="uploadImgUrl"
                :headers="{ 'Authorization': 'Bearer ' + appToken }"
                :show-file-list="false"
                :on-success="handleAvatarSuccess"
                :before-upload="beforeAvatarUpload"
              >
                <div class="avatar-box">
                  <img v-if="userInfo.avatar" :src="userAvatar" @error="handleAvatarError" class="avatar-img" />
                  <div v-else class="avatar-placeholder">{{ userInfo.nickname ? userInfo.nickname.charAt(0) : 'U' }}</div>
                  <!-- 悬浮遮罩 -->
                  <div class="avatar-mask">
                    <el-icon><Camera /></el-icon>
                  </div>
                </div>
              </el-upload>
            </div>

            <!-- 信息区域 -->
            <div class="info-wrapper">
              <div class="name-row">
                <h2 class="nickname">{{ userInfo.nickname || '极客用户' }}</h2>
                <el-tag effect="dark" round size="small" color="#626aef" class="level-tag">
                  <el-icon><Trophy /></el-icon> Lv.{{ userLevel }}
                </el-tag>
              </div>
              <div class="id-row">
                <span>账号 ID: {{ userInfo.username }}</span>
                <el-divider direction="vertical" />
                <span><el-icon><Iphone /></el-icon> {{ userInfo.phonenumber || '未绑定手机' }}</span>
              </div>
              <div class="join-row">
                加入平台第 <span class="days">365</span> 天 <!-- 静态展示，增加归属感 -->
              </div>
            </div>

            <!-- 数据概览 (替代余额和信用分) -->
            <div class="stats-wrapper">
              <!-- 信用/信誉展示 -->
              <div class="stat-item">
                <div class="stat-chart">
                  <el-progress 
                    type="circle" 
                    :percentage="creditPercentage" 
                    :width="70" 
                    :stroke-width="6"
                    color="#67c23a"
                  >
                    <template #default>
                      <span class="score-text">{{ userInfo.creditScore }}</span>
                    </template>
                  </el-progress>
                </div>
                <div class="stat-info">
                  <div class="label">信誉极好</div>
                  <div class="desc">极客信用分</div>
                </div>
              </div>

              <!-- 冻结押金展示 -->
              <div class="stat-item">
                <div class="stat-icon-wrapper">
                  <el-icon :size="32" color="#e6a23c"><Money /></el-icon>
                </div>
                <div class="stat-info">
                  <div class="label">{{ formattedDeposit }}</div>
                  <div class="desc">冻结押金</div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 下方内容区：左右分栏 -->
        <div class="content-grid">
          
          <!-- 左侧：订单中心 -->
          <div class="left-panel">
            <el-card class="panel-card" shadow="hover">
              <template #header>
                <div class="panel-header">
                  <span class="title">我的订单</span>
                  <el-button link type="primary" @click="goToOrder('all')">查看全部 <el-icon><ArrowRight /></el-icon></el-button>
                </div>
              </template>
              
              <div class="order-nav">
                <div class="nav-item" @click="goToOrder(0)">
                  <div class="icon-box blue"><el-icon><Wallet /></el-icon></div>
                  <span>待支付</span>
                </div>
                <div class="nav-item" @click="goToOrder(1)">
                  <div class="icon-box orange"><el-icon><Box /></el-icon></div>
                  <span>待发货</span>
                </div>
                <div class="nav-item" @click="goToOrder(2)">
                  <div class="icon-box green"><el-icon><Van /></el-icon></div>
                  <span>租赁/进行中</span>
                </div>
                <div class="nav-item" @click="goToOrder(3)">
                  <div class="icon-box purple"><el-icon><CircleCheck /></el-icon></div>
                  <span>已完成</span>
                </div>
              </div>
            </el-card>

            <!-- 可以在这里加一个“最近浏览”或“推荐”区域，丰富页面 -->
            <el-card class="panel-card mt-20" shadow="hover">
               <template #header><span class="title">专属服务</span></template>
               <div class="banner-box">
                 <div class="banner-text">
                   <h3>以旧换新</h3>
                   <p>闲置电脑高价回收，极速打款</p>
                 </div>
                 <el-button type="primary" size="small" round>立即估价</el-button>
               </div>
            </el-card>
          </div>

          <!-- 右侧：快捷设置 -->
          <div class="right-panel">
            <el-card class="panel-card" shadow="hover">
              <template #header><span class="title">账户设置</span></template>
              
              <div class="settings-list">
                <div class="setting-item" @click="openEditProfile">
                  <div class="set-icon"><el-icon><EditPen /></el-icon></div>
                  <div class="set-info">
                    <div class="set-title">编辑资料</div>
                    <div class="set-desc">修改昵称、联系方式</div>
                  </div>
                  <el-icon class="arrow"><ArrowRight /></el-icon>
                </div>

                <div class="setting-item" @click="router.push('/portal/user/address')">
                  <div class="set-icon"><el-icon><LocationInformation /></el-icon></div>
                  <div class="set-info">
                    <div class="set-title">收货地址</div>
                    <div class="set-desc">管理配送地址信息</div>
                  </div>
                  <el-icon class="arrow"><ArrowRight /></el-icon>
                </div>

                <div class="setting-item" @click="pwdDialogVisible = true">
                  <div class="set-icon"><el-icon><Lock /></el-icon></div>
                  <div class="set-info">
                    <div class="set-title">安全中心</div>
                    <div class="set-desc">修改登录密码</div>
                  </div>
                  <el-icon class="arrow"><ArrowRight /></el-icon>
                </div>

                <div class="setting-item logout" @click="handleLogout">
                  <div class="set-icon logout-icon"><el-icon><SwitchButton /></el-icon></div>
                  <div class="set-info">
                    <div class="set-title">退出登录</div>
                  </div>
                </div>
              </div>
            </el-card>
          </div>

        </div>

        <!-- 弹窗：编辑资料 -->
        <el-dialog v-model="dialogVisible" title="修改个人信息" width="450px" align-center>
          <el-form :model="editForm" label-width="80px" label-position="left">
            <el-form-item label="昵称">
              <el-input v-model="editForm.nickname" placeholder="请输入昵称" maxlength="10" show-word-limit />
            </el-form-item>
            <el-form-item label="手机号">
              <el-input v-model="editForm.phonenumber" placeholder="请输入手机号" />
            </el-form-item>
          </el-form>
          <template #footer>
            <el-button @click="dialogVisible = false">取消</el-button>
            <el-button type="primary" @click="saveProfile">保存修改</el-button>
          </template>
        </el-dialog>

        <!-- 弹窗：修改密码 -->
        <el-dialog v-model="pwdDialogVisible" title="修改登录密码" width="450px" align-center>
          <el-form :model="pwdForm" label-width="80px" :rules="pwdRules" ref="pwdFormRef" label-position="top">
            <el-form-item label="当前密码" prop="oldPassword">
              <el-input type="password" v-model="pwdForm.oldPassword" placeholder="请输入当前密码" show-password />
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input type="password" v-model="pwdForm.newPassword" placeholder="请输入新密码 (6-20位)" show-password />
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input type="password" v-model="pwdForm.confirmPassword" placeholder="请再次输入新密码" show-password />
            </el-form-item>
          </el-form>
          <template #footer>
            <el-button @click="pwdDialogVisible = false">取消</el-button>
            <el-button type="primary" @click="savePassword">确认修改</el-button>
          </template>
        </el-dialog>

      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  Camera, Trophy, Iphone, EditPen, LocationInformation, Lock, SwitchButton,
  Wallet, Box, Van, CircleCheck, ArrowRight, Money
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { updateProfile, updatePassword } from '@/api/portal/user'
import useAppUserStore from '@/store/modules/appUser'
import Header from '@/views/computerMarket/Header.vue'
import { handleAvatarError } from '@/utils/avatarUtils'

const router = useRouter()
const appUserStore = useAppUserStore()

// 用户信息
const userInfo = computed(() => appUserStore.userInfo || {
  username: '',
  nickname: '',
  avatar: '',
  phonenumber: '',
  creditScore: 500,
  frozenDeposit: 0
})

// 环境变量配置
const uploadImgUrl = ref(import.meta.env.VITE_APP_BASE_API + "/app/user/avatar")

// 弹窗控制
const dialogVisible = ref(false)
const pwdDialogVisible = ref(false)

// 编辑表单
const editForm = reactive({
  nickname: '',
  phonenumber: ''
})

// 密码表单
const pwdFormRef = ref()
const pwdForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const pwdRules = {
  oldPassword: [{ required: true, message: '请输入旧密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: (rule, value, callback) => {
        if (value !== pwdForm.newPassword) callback(new Error('两次输入密码不一致'))
        else callback()
      }, trigger: 'blur' }
  ]
}

// 计算属性
const appToken = computed(() => appUserStore.token)

const userAvatar = computed(() => appUserStore.userAvatar)

// 计算等级
const userLevel = computed(() => {
  const score = userInfo.value.creditScore || 500
  if (score >= 800) return 6
  if (score >= 700) return 5
  if (score >= 600) return 4
  if (score >= 550) return 3
  return 2
})

// 信用分百分比
const creditPercentage = computed(() => {
  const score = userInfo.value.creditScore || 500
  return Math.min(100, Math.round((score / 950) * 100))
})

// 冻结押金格式化
const formattedDeposit = computed(() => {
  const amount = userInfo.value.frozenDeposit || 0
  return '¥' + Number(amount).toFixed(2)
})

// 方法
const getUserInfo = async () => {
  if (!appUserStore.isLoggedIn) return router.push('/portal/login')
  await appUserStore.fetchUserInfo()
}

const handleAvatarSuccess = (response) => {
  if (response.code === 200) {
    const avatarUrl = response.avatar || (response.data && response.data.avatar)
    if (avatarUrl) {
      appUserStore.updateLocalUserInfo({ avatar: avatarUrl })
      ElMessage.success('头像更新成功')
    }
  } else {
    ElMessage.error('上传失败')
  }
}

const beforeAvatarUpload = (file) => {
  const isImg = ['image/jpeg', 'image/png'].includes(file.type)
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isImg || !isLt2M) ElMessage.error('请上传2MB以内的JPG/PNG图片')
  return isImg && isLt2M
}

const openEditProfile = () => {
  editForm.nickname = userInfo.value.nickname
  editForm.phonenumber = userInfo.value.phonenumber
  dialogVisible.value = true
}

const saveProfile = async () => {
  try {
    await updateProfile(editForm)
    await getUserInfo()
    dialogVisible.value = false
    ElMessage.success('资料已更新')
  } catch (e) {
    ElMessage.error('更新失败')
  }
}

const savePassword = async () => {
  if (!pwdFormRef.value) return
  await pwdFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await updatePassword(pwdForm)
        pwdDialogVisible.value = false
        // 清空表单
        Object.keys(pwdForm).forEach(k => pwdForm[k] = '')
        ElMessage.success('密码修改成功，请重新登录')
        handleLogout()
      } catch (e) {
        ElMessage.error('修改失败')
      }
    }
  })
}

const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗?', '提示', { confirmButtonText: '退出', cancelButtonText: '取消' }).then(() => {
    appUserStore.clearUserInfo()
    router.push('/portal/login')
  })
}

const goToOrder = (tab) => {
  router.push({ path: '/portal/user/order', query: { activeTab: tab } })
}

onMounted(() => getUserInfo())
</script>

<style scoped lang="scss">
/* 基础布局 */
.common-layout {
  min-height: 100vh;
  background-color: #f5f7fa; /* 浅灰底色 */
}

.main-content {
  padding-top: 80px; /* 避开 Header */
  padding-bottom: 40px;
}

.dashboard-container {
  max-width: 1100px; /* 限制内容宽度，PC端更精致 */
  margin: 0 auto;
  padding: 0 20px;
}

/* --- 1. 顶部个人信息卡片 --- */
.profile-card {
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.05);
  margin-bottom: 24px;
  position: relative;
}

.profile-bg {
  height: 160px; /* 增加高度 */
  background: linear-gradient(135deg, #a0cfff 0%, #626aef 100%);
  opacity: 0.9;
}

.profile-body {
  padding: 0 40px 30px;
  display: flex;
  align-items: flex-start; /* 改为顶部对齐，通过 padding 控制垂直位置 */
  margin-top: -60px; /* 头像上浮距离 */
  gap: 30px;
  position: relative;
}

/* 头像 */
.avatar-box {
  width: 120px; /* 稍微加大一点头像 */
  height: 120px;
  border-radius: 50%;
  background: #fff;
  border: 4px solid #fff;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  position: relative;
  overflow: hidden;
  cursor: pointer;
  z-index: 10;
}

.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-placeholder {
  width: 100%;
  height: 100%;
  background: #f0f2f5;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40px;
  color: #909399;
  font-weight: bold;
}

.avatar-mask {
  position: absolute;
  top: 0; left: 0; width: 100%; height: 100%;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 24px;
  opacity: 0;
  transition: opacity 0.3s;
}

.avatar-box:hover .avatar-mask {
  opacity: 1;
}

/* 文字信息 */
.info-wrapper {
  flex: 1;
  padding-top: 70px; /* 关键：把文字往下推 */
}

.name-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.nickname {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin: 0;
}

.id-row {
  font-size: 14px;
  color: #606266;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.join-row {
  font-size: 13px;
  color: #909399;
  background: #f4f4f5;
  padding: 4px 10px;
  border-radius: 12px;
  display: inline-block;
}
.join-row .days {
  color: #626aef;
  font-weight: bold;
  font-family: monospace;
}

/* 数据概览 (信誉分) */
.stats-wrapper {
  padding-top: 70px; /* 关键：把右侧数据也往下推 */
  display: flex;
  align-items: center;
  gap: 30px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 15px;
  background: #fbfbfb;
  padding: 10px 20px;
  border-radius: 12px;
  border: 1px solid #f0f0f0;
}

.stat-icon-wrapper {
  width: 70px;
  height: 70px;
  border-radius: 50%;
  background: #fdf6ec;
  display: flex;
  align-items: center;
  justify-content: center;
}

.score-text {
  font-weight: bold;
  color: #303133;
  font-size: 14px;
}

.stat-info .label {
  font-size: 15px;
  font-weight: bold;
  color: #303133;
}
.stat-info .desc {
  font-size: 12px;
  color: #909399;
}

/* --- 2. 下方内容网格 --- */
.content-grid {
  display: grid;
  grid-template-columns: 2fr 1fr; /* 左侧宽，右侧窄 */
  gap: 24px;
}

.panel-card {
  border-radius: 12px;
  border: none;
  box-shadow: 0 4px 12px rgba(0,0,0,0.03);
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  border-left: 4px solid #626aef;
  padding-left: 10px;
}

/* 订单导航 */
.order-nav {
  display: flex;
  justify-content: space-around;
  padding: 20px 0;
}

.nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  gap: 10px;
  transition: transform 0.2s;
}

.nav-item:hover {
  transform: translateY(-3px);
}

.icon-box {
  width: 50px;
  height: 50px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: #fff;
  box-shadow: 0 4px 10px rgba(0,0,0,0.1);
}

.nav-item span {
  font-size: 14px;
  color: #606266;
}

.blue { background: linear-gradient(135deg, #409eff, #79bbff); }
.orange { background: linear-gradient(135deg, #e6a23c, #f3d19e); }
.green { background: linear-gradient(135deg, #67c23a, #95d475); }
.purple { background: linear-gradient(135deg, #626aef, #a0cfff); }

/* Banner */
.banner-box {
  background: linear-gradient(to right, #fdfbfb, #ebedee);
  border-radius: 8px;
  padding: 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.banner-text h3 { margin: 0 0 5px 0; font-size: 16px; color: #303133; }
.banner-text p { margin: 0; font-size: 12px; color: #909399; }

.mt-20 { margin-top: 20px; }

/* 设置列表 */
.settings-list {
  display: flex;
  flex-direction: column;
}

.setting-item {
  display: flex;
  align-items: center;
  padding: 15px 10px;
  cursor: pointer;
  border-bottom: 1px solid #f5f7fa;
  transition: background 0.2s;
  border-radius: 8px;
}

.setting-item:hover {
  background: #f9faff;
}
.setting-item:last-child {
  border-bottom: none;
}

.set-icon {
  width: 36px;
  height: 36px;
  background: #f2f3f5;
  color: #606266;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  font-size: 18px;
}

.set-info {
  flex: 1;
}

.set-title {
  font-size: 14px;
  color: #303133;
  font-weight: 500;
}
.set-desc {
  font-size: 12px;
  color: #909399;
}

.arrow {
  color: #c0c4cc;
}

.logout .set-icon {
  background: #fef0f0;
  color: #f56c6c;
}
.logout .set-title {
  color: #f56c6c;
}

/* 响应式 */
@media screen and (max-width: 768px) {
  .profile-body {
    flex-direction: column;
    align-items: center;
    text-align: center;
    margin-top: -60px;
  }
  
  .content-grid {
    grid-template-columns: 1fr;
  }
  
  .stats-wrapper {
    width: 100%;
    justify-content: center;
    display: flex;
  }
}
</style>