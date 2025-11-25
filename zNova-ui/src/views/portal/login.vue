<template>
  <div class="portal-login">
    <div class="login-container">
      <h2 class="login-title">C端用户登录</h2>
      <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form">
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            type="text"
            size="large"
            auto-complete="off"
            placeholder="请输入用户名"
          >
            <template #prefix>
              <svg-icon icon-class="user" class="el-input__icon input-icon" />
            </template>
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
          >
            <template #prefix>
              <svg-icon icon-class="password" class="el-input__icon input-icon" />
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="code" v-if="captchaEnabled">
          <el-input
            v-model="loginForm.code"
            size="large"
            auto-complete="off"
            placeholder="验证码"
            style="width: 63%"
            @keyup.enter="handleLogin"
          >
            <template #prefix>
              <svg-icon icon-class="validCode" class="el-input__icon input-icon" />
            </template>
          </el-input>
          <div class="login-code">
            <img :src="codeUrl" @click="getCode" class="login-code-img"/>
          </div>
        </el-form-item>
        <el-form-item style="width:100%;">
          <el-button
            :loading="loading"
            size="large"
            type="primary"
            style="width:100%;"
            @click.prevent="handleLogin"
          >
            <span v-if="!loading">登 录</span>
            <span v-else>登 录 中...</span>
          </el-button>
          <div style="float: right;" v-if="register">
            <router-link class="link-type" :to="'/portal/register'">立即注册</router-link>
          </div>
          <div style="float: left;">
            <router-link class="link-type" :to="'/login'">商家登录</router-link>
          </div>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import { getCodeImg } from "@/api/login";
import { appLogin } from "@/api/appLogin";
import Cookies from "js-cookie";
import { encrypt, decrypt } from '@/utils/jsencrypt'
import { setToken } from "@/utils/auth";

export default {
  name: "PortalLogin",
  data() {
    return {
      codeUrl: "",
      loginForm: {
        username: "",
        password: "",
        rememberMe: false,
        code: "",
        uuid: ""
      },
      loginRules: {
        username: [
          { required: true, trigger: "blur", message: "请输入您的账号" }
        ],
        password: [
          { required: true, trigger: "blur", message: "请输入您的密码" }
        ],
        code: [{ required: true, trigger: "change", message: "请输入验证码" }]
      },
      loading: false,
      // 验证码开关
      captchaEnabled: true,
      // 注册开关
      register: true,
      redirect: undefined
    };
  },
  watch: {
    $route: {
      handler: function(route) {
        this.redirect = route.query && route.query.redirect;
      },
      immediate: true
    }
  },
  created() {
    this.getCode();
  },
  methods: {
    getCode() {
      getCodeImg().then(res => {
        this.captchaEnabled = res.captchaEnabled === undefined ? true : res.captchaEnabled;
        if (this.captchaEnabled) {
          this.codeUrl = "data:image/gif;base64," + res.img;
          this.loginForm.uuid = res.uuid;
        }
      });
    },
    handleLogin() {
      console.log('登录按钮被点击');
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          console.log('表单验证通过');
          this.loading = true;
          appLogin(this.loginForm.username, this.loginForm.password, this.loginForm.code, this.loginForm.uuid).then(res => {
            console.log('登录成功:', res);
            // 存储C端token
            localStorage.setItem('app_token', res.token);
            console.log("登录成功，保存token到localStorage:", res.token);
            
            // 验证token是否保存成功
            const savedToken = localStorage.getItem('app_token');
            console.log("验证保存的token:", savedToken);
            
            this.$message.success('登录成功');
            console.log('准备跳转到:', this.redirect || "/computer-market");
            this.$router.push({ path: this.redirect || "/computer-market" }).then(() => {
              console.log('跳转成功');
            }).catch(err => {
              console.error('跳转失败:', err);
            });
          }).catch(err => {
            console.error('登录失败:', err);
            this.loading = false;
            if (this.captchaEnabled) {
              this.getCode();
            }
            // 显示错误提示
            if (err && err.message) {
              this.$message.error(err.message);
            } else {
              this.$message.error('登录失败，请检查用户名和密码');
            }
          });
        } else {
          console.log('表单验证失败');
          this.$message.error('请填写完整的登录信息');
        }
      });
    }
  }
};
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
.portal-login {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-image: url("../assets/images/login-background.jpg");
  background-size: cover;
}
.login-container {
  width: 400px;
  padding: 30px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}
.login-title {
  margin: 0 auto 30px auto;
  text-align: center;
  color: #707070;
}
.login-form {
  .el-input {
    height: 38px;
    input {
      height: 38px;
    }
  }
  .input-icon {
    height: 39px;
    width: 14px;
    margin-left: 2px;
  }
}
.login-code {
  width: 33%;
  height: 38px;
  float: right;
  img {
    cursor: pointer;
    vertical-align: middle;
  }
}
.login-code-img {
  height: 38px;
}
</style>