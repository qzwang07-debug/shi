<template>
  <div class="login-container">
    <!-- 登录卡片 -->
    <div class="login-box glass-card">
      <div class="title-header">
        <h3 class="title">电脑租售与硬件性能<br>智能评估平台</h3>
      </div>
      
      <el-form ref="loginRef" :model="loginForm" :rules="loginRules" class="login-form">
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            type="text"
            size="large"
            auto-complete="off"
            placeholder="请输入账号"
            class="glass-input"
          >
            <template #prefix><svg-icon icon-class="user" class="input-icon" /></template>
          </el-input>
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            size="large"
            auto-complete="off"
            placeholder="请输入密码"
            @keyup.enter="handleLogin"
            class="glass-input"
            show-password
          >
            <template #prefix><svg-icon icon-class="password" class="input-icon" /></template>
          </el-input>
        </el-form-item>
        
        <el-form-item prop="code" v-if="captchaEnabled">
          <div class="captcha-wrapper">
            <el-input
              v-model="loginForm.code"
              size="large"
              auto-complete="off"
              placeholder="验证码"
              class="glass-input code-input"
              @keyup.enter="handleLogin"
            >
              <template #prefix><svg-icon icon-class="validCode" class="input-icon" /></template>
            </el-input>
            <div class="login-code">
              <img :src="codeUrl" @click="getCode" class="login-code-img" title="点击切换验证码"/>
            </div>
          </div>
        </el-form-item>
        
        <div class="remember-me">
          <el-checkbox v-model="loginForm.rememberMe">记住密码</el-checkbox>
        </div>
        
        <el-form-item style="width:100%;">
          <el-button
            :loading="loading"
            size="large"
            type="primary"
            class="login-btn"
            @click.prevent="handleLogin"
          >
            <span v-if="!loading">立 即 登 录</span>
            <span v-else>登 录 中...</span>
          </el-button>
        </el-form-item>

        <div class="link-group">
          <router-link class="link-type" :to="'/portal/login'">买家登录</router-link>
          <span class="divider">|</span>
          <router-link class="link-type" :to="'/portal/register'">游客注册</router-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { getCodeImg } from "@/api/login";
import Cookies from "js-cookie";
import { encrypt, decrypt } from "@/utils/jsencrypt";
import useUserStore from '@/store/modules/user'
import { ref, watch, getCurrentInstance } from 'vue';
import { useRoute, useRouter } from 'vue-router';

const userStore = useUserStore()
const route = useRoute();
const router = useRouter();
const { proxy } = getCurrentInstance();

const loginForm = ref({
  username: "admin",
  password: "admin123",
  rememberMe: false,
  code: "",
  uuid: ""
});

const loginRules = {
  username: [{ required: true, trigger: "blur", message: "请输入您的账号" }],
  password: [{ required: true, trigger: "blur", message: "请输入您的密码" }],
  code: [{ required: true, trigger: "change", message: "请输入验证码" }]
};

const codeUrl = ref("");
const loading = ref(false);
// 验证码开关
const captchaEnabled = ref(true);

// 注册开关
const register = ref(true);
const redirect = ref(undefined);

watch(route, (newRoute) => {
    redirect.value = newRoute.query && newRoute.query.redirect;
}, { immediate: true });

function handleLogin() {
  proxy.$refs.loginRef.validate(valid => {
    if (valid) {
      loading.value = true;
      if (loginForm.value.rememberMe) {
        Cookies.set("username", loginForm.value.username, { expires: 30 });
        Cookies.set("password", encrypt(loginForm.value.password), { expires: 30 });
        Cookies.set("rememberMe", loginForm.value.rememberMe, { expires: 30 });
      } else {
        Cookies.remove("username");
        Cookies.remove("password");
        Cookies.remove("rememberMe");
      }
      userStore.login(loginForm.value).then(() => {
        const query = route.query;
        const otherQueryParams = Object.keys(query).reduce((acc, cur) => {
          if (cur !== "redirect") {
            acc[cur] = query[cur];
          }
          return acc;
        }, {});
        router.push({ path: redirect.value || "/", query: otherQueryParams });
      }).catch(() => {
        loading.value = false;
        if (captchaEnabled.value) {
          getCode();
        }
      });
    }
  });
}

function getCode() {
  getCodeImg().then(res => {
    captchaEnabled.value = res.captchaEnabled === undefined ? true : res.captchaEnabled;
    if (captchaEnabled.value) {
      codeUrl.value = "data:image/gif;base64," + res.img;
      loginForm.value.uuid = res.uuid;
    }
  });
}

function getCookie() {
  const username = Cookies.get("username");
  const password = Cookies.get("password");
  const rememberMe = Cookies.get("rememberMe");
  loginForm.value = {
    username: username === undefined ? loginForm.value.username : username,
    password: password === undefined ? loginForm.value.password : decrypt(password),
    rememberMe: rememberMe === undefined ? false : Boolean(rememberMe)
  };
}

getCode();
getCookie();
</script>

<style lang='scss' scoped>
// 变量定义
$primary-color: #409eff; // 科技蓝
$glass-bg: rgba(255, 255, 255, 0.45); // 磨砂玻璃背景色
$glass-border: rgba(255, 255, 255, 0.6); // 磨砂边框
$text-color: #303133;

.login-container {
  display: flex;
  justify-content: flex-end; /* 右侧显示 */
  align-items: center;
  height: 100vh;
  width: 100%;
  // 保持原有背景图片引用
  background-image: url("../assets/images/login-background1.jpg");
  background-size: cover;
  background-position: center;
  padding-right: 10vw; // 响应式右边距
}

// 磨砂玻璃卡片核心样式
.glass-card {
  width: 420px;
  padding: 40px 35px;
  border-radius: 16px;
  // 磨砂效果核心代码
  background: $glass-bg;
  backdrop-filter: blur(12px); 
  -webkit-backdrop-filter: blur(12px); // 兼容 Safari
  border: 1px solid $glass-border;
  box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.1);
  transition: transform 0.3s ease;

  &:hover {
    box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.15);
  }
}

.title-header {
  margin-bottom: 30px;
  text-align: center;

  .title {
    font-size: 24px;
    font-weight: 600;
    color: $text-color;
    margin: 0;
    line-height: 1.4;
    letter-spacing: 1px;
    text-shadow: 0 1px 2px rgba(255, 255, 255, 0.8);
  }
}

.login-form {
  // 深度选择器修改 Element Plus 输入框样式
  :deep(.el-input__wrapper) {
    background-color: rgba(255, 255, 255, 0.5); // 输入框半透明
    box-shadow: none; // 去除默认边框
    border: 1px solid rgba(255, 255, 255, 0.5);
    border-radius: 8px;
    padding-left: 10px;
    transition: all 0.3s;
    
    &:hover, &.is-focus {
      background-color: rgba(255, 255, 255, 0.8);
      box-shadow: 0 0 0 1px $primary-color inset;
    }
  }

  :deep(.el-input__inner) {
    color: #333;
    height: 42px;
  }

  .input-icon {
    width: 16px;
    height: 16px;
    margin-right: 5px;
    color: #555;
  }
}

// 验证码区域
.captcha-wrapper {
  display: flex;
  justify-content: space-between;
  align-items: center;

  .code-input {
    flex: 1;
    margin-right: 15px;
  }

  .login-code {
    width: 110px;
    height: 42px;
    border-radius: 8px;
    overflow: hidden;
    background: rgba(255, 255, 255, 0.5);
    border: 1px solid rgba(255, 255, 255, 0.5);
    cursor: pointer;
    
    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      opacity: 0.9;
      &:hover { opacity: 1; }
    }
  }
}

.remember-me {
  margin-bottom: 20px;
  :deep(.el-checkbox__label) {
    color: #555;
  }
}

// 登录按钮优化
.login-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
  letter-spacing: 4px;
  font-weight: 500;
  border-radius: 8px;
  background: linear-gradient(135deg, $primary-color 0%, darken($primary-color, 10%) 100%);
  border: none;
  box-shadow: 0 4px 15px rgba(64, 158, 255, 0.3);
  transition: all 0.3s;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 20px rgba(64, 158, 255, 0.4);
  }
  
  &:active {
    transform: translateY(0);
  }
}

// 底部链接区域
.link-group {
  margin-top: 20px;
  text-align: center;
  font-size: 14px;
  display: flex;
  justify-content: center;
  align-items: center;
  
  .divider {
    margin: 0 15px;
    color: #999;
    font-size: 12px;
  }

  .link-type {
    color: #606266;
    text-decoration: none;
    transition: color 0.3s;
    position: relative;

    &:hover {
      color: $primary-color;
      font-weight: 500;
    }
  }
}

// 响应式适配
@media (max-width: 768px) {
  .login-container {
    justify-content: center;
    padding-right: 0;
  }
  
  .glass-card {
    width: 90%;
    margin: 0 20px;
  }
}
</style>