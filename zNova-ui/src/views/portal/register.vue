<template>
  <!-- 全屏背景容器 -->
  <div class="register-wrapper">
    
    <!-- 注册卡片 -->
    <div class="register-box">
      <div class="register-header">
        <h2 class="title">创建账号</h2>
        <p class="subtitle">欢迎加入新星硬件智能评估平台</p>
      </div>

      <el-form ref="registerRef" :model="registerForm" :rules="registerRules" class="register-form">
        
        <!-- 用户名 -->
        <el-form-item prop="username">
          <el-input
            v-model="registerForm.username"
            type="text"
            size="large"
            placeholder="请输入账号 (2-20字符)"
            class="custom-input"
          >
            <template #prefix>
              <el-icon class="input-icon"><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <!-- 昵称 -->
        <el-form-item prop="nickname">
          <el-input
            v-model="registerForm.nickname"
            type="text"
            size="large"
            placeholder="请输入昵称"
            class="custom-input"
          >
            <template #prefix>
              <el-icon class="input-icon"><Postcard /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <!-- 密码 -->
        <el-form-item prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            size="large"
            placeholder="设置密码 (5-20字符)"
            show-password
            class="custom-input"
          >
            <template #prefix>
              <el-icon class="input-icon"><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <!-- 确认密码 -->
        <el-form-item prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            size="large"
            placeholder="确认密码"
            show-password
            class="custom-input"
          >
            <template #prefix>
              <el-icon class="input-icon"><CircleCheck /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <!-- 验证码 -->
        <el-form-item prop="code" v-if="captchaEnabled">
          <div class="captcha-row">
            <el-input
              v-model="registerForm.code"
              size="large"
              placeholder="验证码"
              class="custom-input captcha-input"
            >
              <template #prefix>
                <el-icon class="input-icon"><Key /></el-icon>
              </template>
            </el-input>
            <div class="captcha-img-box" @click="getCode">
              <img v-if="codeUrl" :src="codeUrl" class="code-img" title="点击切换验证码" />
              <div v-else class="img-placeholder">加载中...</div>
            </div>
          </div>
        </el-form-item>

        <!-- 按钮区域 -->
        <el-form-item class="btn-row">
          <el-button
            :loading="loading"
            size="large"
            type="primary"
            class="register-btn"
            @click.prevent="handleRegister"
            round
          >
            <span v-if="!loading">立即注册</span>
            <span v-else>注 册 中...</span>
          </el-button>
        </el-form-item>

        <!-- 底部链接 -->
        <div class="form-footer">
          <span>已有账号？</span>
          <router-link class="link-btn highlight" :to="'/portal/login'">
            去登录 <el-icon><ArrowRight /></el-icon>
          </router-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { User, Lock, Key, Postcard, CircleCheck, ArrowRight } from '@element-plus/icons-vue';
import { getCodeImg } from "@/api/login";
import { appRegister } from "@/api/appLogin";

const router = useRouter();
const registerRef = ref(null);

// 数据状态
const codeUrl = ref("");
const loading = ref(false);
const captchaEnabled = ref(true);

const registerForm = reactive({
  username: "",
  password: "",
  confirmPassword: "",
  nickname: "",
  code: "",
  uuid: ""
});

// 密码一致性校验
const equalToPassword = (rule, value, callback) => {
  if (registerForm.password !== value) {
    callback(new Error("两次输入的密码不一致"));
  } else {
    callback();
  }
};

const registerRules = {
  username: [
    { required: true, trigger: "blur", message: "请输入您的账号" },
    { min: 2, max: 20, message: "账号长度必须介于 2 和 20 之间", trigger: "blur" }
  ],
  password: [
    { required: true, trigger: "blur", message: "请输入您的密码" },
    { min: 5, max: 20, message: "密码长度必须介于 5 和 20 之间", trigger: "blur" }
  ],
  confirmPassword: [
    { required: true, trigger: "blur", message: "请再次输入您的密码" },
    { validator: equalToPassword, trigger: "blur" }
  ],
  nickname: [
    { required: true, trigger: "blur", message: "请输入您的昵称" },
    { max: 30, message: "昵称长度不能超过30个字符", trigger: "blur" }
  ],
  code: [{ required: true, trigger: "change", message: "请输入验证码" }]
};

// 获取验证码
const getCode = () => {
  getCodeImg().then(res => {
    captchaEnabled.value = res.captchaEnabled === undefined ? true : res.captchaEnabled;
    if (captchaEnabled.value) {
      codeUrl.value = "data:image/gif;base64," + res.img;
      registerForm.uuid = res.uuid;
    }
  });
};

// 注册逻辑
const handleRegister = () => {
  registerRef.value.validate(valid => {
    if (valid) {
      loading.value = true;
      appRegister({
        username: registerForm.username,
        password: registerForm.password,
        nickname: registerForm.nickname,
        code: registerForm.code,
        uuid: registerForm.uuid
      }).then(res => {
        ElMessage.success('注册成功，请登录');
        router.push('/portal/login');
      }).catch(err => {
        console.error('注册失败:', err);
        loading.value = false;
        if (captchaEnabled.value) {
          getCode();
        }
        if (err && err.message) {
          ElMessage.error(err.message);
        } else {
          ElMessage.error('注册失败，请重试');
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
.register-wrapper {
  width: 100%;
  height: 100vh;
  /* 设置背景图 */
  background-image: url("@/assets/images/computer1.png");
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  
  /* Flex布局使注册框居右垂直居中 */
  display: flex;
  align-items: center;
  justify-content: flex-end; /* 靠右对齐 */
  padding-right: 10%; /* 距离右侧的距离 */
  position: relative;
}

/* 增加淡色遮罩，提升文字可读性 */
.register-wrapper::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.05);
  z-index: 1;
}

/* 磨砂玻璃卡片 */
.register-box {
  position: relative;
  z-index: 10;
  width: 440px;
  padding: 40px;
  border-radius: 20px;
  
  /* 磨砂玻璃效果 */
  background: rgba(255, 255, 255, 0.8);
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

.register-header {
  margin-bottom: 25px;
  text-align: left;
}

.title {
  font-size: 26px;
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 8px;
}

.subtitle {
  font-size: 14px;
  color: #7f8c8d;
  margin: 0;
}

/* 输入框样式重写 */
.custom-input {
  --el-input-bg-color: rgba(255, 255, 255, 0.6);
  --el-input-border-color: transparent;
  
  height: 46px;
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

/* 验证码样式 */
.captcha-row {
  display: flex;
  gap: 15px;
}

.captcha-input {
  flex: 1;
}

.captcha-img-box {
  width: 120px;
  height: 46px;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  border: 1px solid rgba(0, 0, 0, 0.05);
  background: rgba(255, 255, 255, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
}

.captcha-img-box:hover {
  border-color: #626aef;
}

.code-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.img-placeholder {
  font-size: 12px;
  color: #909399;
}

/* 按钮样式 */
.btn-row {
  margin-top: 25px;
  margin-bottom: 15px;
}

.register-btn {
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

.register-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(98, 106, 239, 0.45);
}

.register-btn:active {
  transform: translateY(0);
}

/* 底部区域 */
.form-footer {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 5px;
  font-size: 14px;
  color: #606266;
}

.link-btn {
  display: flex;
  align-items: center;
  gap: 2px;
  text-decoration: none;
  transition: color 0.3s;
}

.link-btn.highlight {
  color: #626aef;
  font-weight: 600;
}

.link-btn.highlight:hover {
  color: #409eff;
}

@media screen and (max-width: 768px) {
  .register-wrapper {
    justify-content: center;
    padding-right: 0;
  }
  
  .register-box {
    width: 90%;
    margin: 0 20px;
  }
}
</style>