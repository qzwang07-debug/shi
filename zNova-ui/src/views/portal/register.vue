<template>
  <div class="common-layout">
    <el-container class="app-container">
      <!-- 导入的头部导航栏组件 -->
      <Header />
      
      <!-- 主要内容区域 -->
      <el-main class="main-content">
        <div class="portal-register">
          <div class="register-container">
            <h2 class="register-title">C端用户注册</h2>
      <el-form ref="registerForm" :model="registerForm" :rules="registerRules" class="register-form">
        <!-- 头像上传区域 -->
        <el-form-item>
          <div class="avatar-upload-section">
            <h3 class="avatar-title">上传头像</h3>
            <el-upload
              class="avatar-uploader"
              :action="uploadUrl"
              :headers="uploadHeaders"
              :show-file-list="false"
              :on-success="handleAvatarSuccess"
              :before-upload="beforeAvatarUpload"
              :auto-upload="true"
              drag
            >
              <img v-if="registerForm.avatar" :src="registerForm.avatar" class="avatar" />
              <template v-else>
                <el-icon class="avatar-uploader-icon"><Plus /></el-icon>
                <div class="avatar-hint">点击或拖拽文件到此处上传</div>
                <div class="avatar-tip">支持 JPG、PNG 格式，不超过 2MB</div>
              </template>
            </el-upload>
          </div>
        </el-form-item>
        
        <el-form-item prop="username">
          <el-input
            v-model="registerForm.username"
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
            v-model="registerForm.password"
            type="password"
            size="large"
            auto-complete="off"
            placeholder="请输入密码"
          >
            <template #prefix>
              <svg-icon icon-class="password" class="el-input__icon input-icon" />
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            size="large"
            auto-complete="off"
            placeholder="请确认密码"
          >
            <template #prefix>
              <svg-icon icon-class="password" class="el-input__icon input-icon" />
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="nickname">
          <el-input
            v-model="registerForm.nickname"
            type="text"
            size="large"
            auto-complete="off"
            placeholder="请输入昵称"
          >
            <template #prefix>
              <svg-icon icon-class="user" class="el-input__icon input-icon" />
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="code" v-if="captchaEnabled">
          <el-input
            v-model="registerForm.code"
            size="large"
            auto-complete="off"
            placeholder="验证码"
            style="width: 63%"
          >
            <template #prefix>
              <svg-icon icon-class="validCode" class="el-input__icon input-icon" />
            </template>
          </el-input>
          <div class="register-code">
            <img :src="codeUrl" @click="getCode" class="register-code-img"/>
          </div>
        </el-form-item>
        <el-form-item style="width:100%;">
          <el-button
            :loading="loading"
            size="large"
            type="primary"
            style="width:100%;"
            @click.prevent="handleRegister"
          >
            <span v-if="!loading">注 册</span>
            <span v-else>注 册 中...</span>
          </el-button>
          <div style="float: right;">
            <router-link class="link-type" :to="'/portal/login'">返回登录</router-link>
          </div>
        </el-form-item>
      </el-form>
          </div>
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script>
import { getCodeImg } from "@/api/login";
import { appRegister } from "@/api/appLogin";
import { Plus } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import Header from '@/views/computerMarket/Header.vue';
import { handleImageUrl } from '@/utils/ruoyi';

export default {
  name: "PortalRegister",
  components: {
    Plus,
    Header
  },
  data() {
    const equalToPassword = (rule, value, callback) => {
      if (this.registerForm.password !== value) {
        callback(new Error("两次输入的密码不一致"));
      } else {
        callback();
      }
    };
    return {
      codeUrl: "",
      // 头像上传配置
      uploadUrl: '/app/user/avatar', // 头像上传接口地址
      uploadHeaders: {}, // 上传请求头
      registerForm: {
        username: "",
        password: "",
        confirmPassword: "",
        nickname: "",
        avatar: "", // 头像字段
        code: "",
        uuid: ""
      },
      registerRules: {
        username: [
          { required: true, trigger: "blur", message: "请输入您的账号" },
          { min: 2, max: 20, message: "用户账号长度必须介于 2 和 20 之间", trigger: "blur" }
        ],
        password: [
          { required: true, trigger: "blur", message: "请输入您的密码" },
          { min: 5, max: 20, message: "用户密码长度必须介于 5 和 20 之间", trigger: "blur" }
        ],
        confirmPassword: [
          { required: true, trigger: "blur", message: "请再次输入您的密码" },
          { required: true, validator: equalToPassword, trigger: "blur" }
        ],
        nickname: [
          { required: true, trigger: "blur", message: "请输入您的昵称" },
          { max: 30, message: "昵称长度不能超过30个字符", trigger: "blur" }
        ],
        code: [{ required: true, trigger: "change", message: "请输入验证码" }]
      },
      loading: false,
      captchaEnabled: true
    };
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
          this.registerForm.uuid = res.uuid;
        }
      });
    },
    
    // 头像上传成功处理
    handleAvatarSuccess(response) {
      if (response.code === 200) {
        this.registerForm.avatar = response.data.avatar;
        ElMessage.success('头像上传成功');
      } else {
        ElMessage.error('头像上传失败');
      }
    },
    
    // 头像上传前验证
    beforeAvatarUpload(file) {
      const isJPG = file.type === 'image/jpeg' || file.type === 'image/png';
      const isLt2M = file.size / 1024 / 1024 < 2;

      if (!isJPG) {
        ElMessage.error('上传头像图片只能是 JPG/PNG 格式!');
      }
      if (!isLt2M) {
        ElMessage.error('上传头像图片大小不能超过 2MB!');
      }
      return isJPG && isLt2M;
    },
    
    handleRegister() {
      this.$refs.registerForm.validate(valid => {
        if (valid) {
          this.loading = true;
          appRegister({
            username: this.registerForm.username,
            password: this.registerForm.password,
            nickname: this.registerForm.nickname,
            avatar: this.registerForm.avatar, // 传递头像信息
            code: this.registerForm.code,
            uuid: this.registerForm.uuid
          }).then(res => {
            ElMessage.success('注册成功，请登录');
            this.$router.push('/portal/login');
          }).catch(err => {
            console.error('注册失败:', err);
            this.loading = false;
            if (this.captchaEnabled) {
              this.getCode();
            }
            // 显示具体的错误信息
            if (err && err.message) {
              ElMessage.error(err.message);
            } else if (typeof err === 'string') {
              ElMessage.error(err);
            } else {
              ElMessage.error('注册失败，请检查输入信息');
            }
          });
        }
      });
    }
  }
};
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
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
  display: flex;
  justify-content: center;
  align-items: center;
}

.portal-register {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background-image: url("@/assets/images/login-background.jpg");
  background-size: cover;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}
.register-container {
  width: 400px;
  padding: 30px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}
.register-title {
  margin: 0 auto 30px auto;
  text-align: center;
  color: #707070;
}

/* 头像上传区域样式 */
.avatar-upload-section {
  margin-bottom: 20px;
}

.avatar-title {
  margin: 0 0 15px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  text-align: center;
}

.avatar-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 12px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px 0;
  background-color: #fafafa;
}

.avatar-uploader:hover {
  border-color: #409eff;
  background-color: #ecf5ff;
}

.avatar-uploader-icon {
  font-size: 32px;
  color: #8c939d;
  margin-bottom: 10px;
}

.avatar {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.avatar-hint {
  font-size: 14px;
  color: #606266;
  margin-bottom: 5px;
}

.avatar-tip {
  font-size: 12px;
  color: #909399;
}

.register-form {
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
.register-code {
  width: 33%;
  height: 38px;
  float: right;
  img {
    cursor: pointer;
    vertical-align: middle;
  }
}
.register-code-img {
  height: 38px;
}
</style>