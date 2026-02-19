<template>
  <div class="common-layout">
    
    
    <div class="main-content">
      <!-- 登录区域包裹层 -->
      <div class="login-wrapper">
        <!-- 登录卡片 -->
        <div class="login-box glass-effect">
          <div class="login-header">
            <h2 class="title">欢迎回来</h2>
            <p class="subtitle">登录您的 新星 账号</p>
          </div>

          <el-form ref="loginRef" :model="loginForm" :rules="loginRules" class="login-form">
            <el-form-item prop="username">
              <el-input
                v-model="loginForm.username"
                type="text"
                size="large"
                placeholder="请输入账号"
                class="custom-input"
              >
                <template #prefix>
                  <el-icon class="input-icon"><User /></el-icon>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item prop="password">
              <el-input
                v-model="loginForm.password"
                type="password"
                size="large"
                placeholder="请输入密码"
                show-password
                class="custom-input"
                @keyup.enter="handleLogin"
              >
                <template #prefix>
                  <el-icon class="input-icon"><Lock /></el-icon>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item prop="code" v-if="captchaEnabled">
              <div class="captcha-row">
                <el-input
                  v-model="loginForm.code"
                  size="large"
                  placeholder="验证码"
                  class="custom-input captcha-input"
                  @keyup.enter="handleLogin"
                >
                  <template #prefix>
                    <el-icon class="input-icon"><Key /></el-icon>
                  </template>
                </el-input>
                <div class="captcha-img-box" @click="getCode">
                  <img v-if="codeUrl" :src="codeUrl" class="login-code-img" title="点击切换验证码" />
                  <div v-else class="img-placeholder">加载中...</div>
                </div>
              </div>
            </el-form-item>

            <el-form-item class="btn-row">
              <el-button
                :loading="loading"
                size="large"
                type="primary"
                class="login-btn"
                @click.prevent="handleLogin"
                round
              >
                <span v-if="!loading">立即登录</span>
                <span v-else>登 录 中...</span>
              </el-button>
            </el-form-item>

            <div class="form-footer">
              <router-link class="link-btn" :to="'/computer-market'">
                <el-icon><View /></el-icon> 游客访问
              </router-link>
              <el-divider direction="vertical" />
              <router-link class="link-btn highlight" :to="'/portal/register'">
                注册新账号 <el-icon><ArrowRight /></el-icon>
              </router-link>
            </div>
          </el-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { ElMessage } from 'element-plus';
import { User, Lock, Key, Shop, ArrowRight } from '@element-plus/icons-vue';
import { getCodeImg } from "@/api/login";
import { appLogin } from "@/api/appLogin";
import { setAppToken } from "@/utils/auth";
import useAppUserStore from "@/store/modules/appUser";


const router = useRouter();
const route = useRoute();
const appUserStore = useAppUserStore();
const loginRef = ref(null);

// 数据状态
const codeUrl = ref("");
const loading = ref(false);
const captchaEnabled = ref(true);
const redirect = ref(undefined);

const loginForm = reactive({
  username: "",
  password: "",
  code: "",
  uuid: ""
});

const loginRules = {
  username: [{ required: true, trigger: "blur", message: "请输入您的账号" }],
  password: [{ required: true, trigger: "blur", message: "请输入您的密码" }],
  code: [{ required: true, trigger: "change", message: "请输入验证码" }]
};

// 监听路由变化
watch(() => route.query, (query) => {
  redirect.value = query && query.redirect;
}, { immediate: true });

// 获取验证码
const getCode = () => {
  getCodeImg().then(res => {
    captchaEnabled.value = res.captchaEnabled === undefined ? true : res.captchaEnabled;
    if (captchaEnabled.value) {
      codeUrl.value = "data:image/gif;base64," + res.img;
      loginForm.uuid = res.uuid;
    }
  });
};

// 登录逻辑
const handleLogin = () => {
  loginRef.value.validate(valid => {
    if (valid) {
      loading.value = true;
      appLogin(loginForm.username, loginForm.password, loginForm.code, loginForm.uuid)
        .then(async res => {
          appUserStore.setToken(res.token);
          setAppToken(res.token);
          // 登录成功后立即拉取用户信息
          await appUserStore.fetchUserInfo();
          ElMessage.success('登录成功，欢迎回来');
          router.push({ path: redirect.value || "/computer-market" });
        })
        .catch(err => {
          console.error(err);
          loading.value = false;
          if (captchaEnabled.value) {
            getCode();
          }
        });
    }
  });
};

onMounted(() => {
  getCode();
});
</script>

<style scoped lang="scss">
/* 基础布局 */
.common-layout, .app-container {
  min-height: 100vh;
}

.main-content {
  padding: 0;
  margin: 0;
  height: 100vh;
  overflow: hidden;
}

.login-wrapper {
  width: 100%;
  height: 100%;
  /* 设置背景图 */
  background-image: url("@/assets/images/computer1.png");
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  
  /* Flex布局使登录框居右 */
  display: flex;
  align-items: center;
  justify-content: flex-end; /* 靠右对齐 */
  padding-right: 10%; /* 距离右侧的距离 */
  position: relative;
}

/* 增加背景遮罩，提升文字可读性 */
.login-wrapper::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.1); /* 极淡的遮罩 */
  z-index: 1;
}

/* 磨砂玻璃卡片 */
.login-box {
  position: relative;
  z-index: 10;
  width: 420px;
  padding: 40px;
  border-radius: 20px;
  
  /* 磨砂玻璃核心代码 */
  background: rgba(255, 255, 255, 0.75); /* 白色半透明 */
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.6);
  box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.15);
  
  animation: slideIn 0.6s cubic-bezier(0.25, 0.46, 0.45, 0.94) both;
}

@keyframes slideIn {
  0% { transform: translateX(50px); opacity: 0; }
  100% { transform: translateX(0); opacity: 1; }
}

/* 头部文字 */
.login-header {
  margin-bottom: 30px;
  text-align: left;
}

.title {
  font-size: 28px;
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 8px;
  letter-spacing: 1px;
}

.subtitle {
  font-size: 14px;
  color: #7f8c8d;
  margin: 0;
}

/* 表单样式优化 */
.custom-input {
  --el-input-bg-color: rgba(255, 255, 255, 0.6);
  --el-input-border-color: transparent;
  --el-input-hover-border-color: transparent;
  --el-input-focus-border-color: transparent;
  
  height: 48px;
  font-size: 15px;
}

:deep(.el-input__wrapper) {
  border-radius: 12px;
  box-shadow: none !important;
  background-color: rgba(255, 255, 255, 0.5);
  border: 1px solid rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
  padding-left: 15px;
}

:deep(.el-input__wrapper:hover),
:deep(.el-input__wrapper.is-focus) {
  background-color: #fff;
  border-color: #626aef;
  box-shadow: 0 4px 12px rgba(98, 106, 239, 0.1) !important;
}

.input-icon {
  font-size: 18px;
  color: #909399;
}

/* 验证码行 */
.captcha-row {
  display: flex;
  gap: 15px;
}

.captcha-input {
  flex: 1;
}

.captcha-img-box {
  width: 120px;
  height: 48px;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  border: 1px solid rgba(0, 0, 0, 0.05);
  background: rgba(255, 255, 255, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.captcha-img-box:hover {
  transform: scale(1.02);
  border-color: #626aef;
}

.login-code-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.img-placeholder {
  font-size: 12px;
  color: #909399;
}

/* 按钮行 */
.btn-row {
  margin-top: 10px;
  margin-bottom: 20px;
}

.login-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 2px;
  background: linear-gradient(135deg, #626aef 0%, #a0cfff 100%);
  border: none;
  box-shadow: 0 4px 15px rgba(98, 106, 239, 0.35);
  transition: all 0.3s;
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(98, 106, 239, 0.45);
}

.login-btn:active {
  transform: translateY(0);
}

/* 底部链接 */
.form-footer {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 15px;
  font-size: 14px;
}

.link-btn {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #606266;
  text-decoration: none;
  transition: color 0.3s;
}

.link-btn:hover {
  color: #303133;
}

.link-btn.highlight {
  color: #626aef;
  font-weight: 500;
}

.link-btn.highlight:hover {
  color: #409eff;
}

/* 响应式调整 */
@media screen and (max-width: 768px) {
  .login-wrapper {
    justify-content: center;
    padding-right: 0;
  }
  
  .login-box {
    width: 90%;
    margin: 0 20px;
  }
}
</style>