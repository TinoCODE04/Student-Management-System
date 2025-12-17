<template>
  <div class="login-container">
    <!-- 动态背景粒子效果 -->
    <div class="particles">
      <span v-for="i in 20" :key="i" class="particle"></span>
    </div>
    
    <div class="login-box">
      <!-- Logo 和标题区域 -->
      <div class="login-header">
        <div class="logo-wrapper">
          <el-icon class="logo-icon"><School /></el-icon>
        </div>
        <h2 class="login-title">学生信息管理系统</h2>
        <p class="login-subtitle">Student Information Management System</p>
      </div>
      
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        class="login-form"
        @keyup.enter="handleLogin"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            size="large"
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
            placeholder="请输入密码"
            size="large"
            show-password
            class="custom-input"
          >
            <template #prefix>
              <el-icon class="input-icon"><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item prop="captcha">
          <div class="captcha-row">
            <el-input
              v-model="loginForm.captcha"
              placeholder="请输入验证码"
              size="large"
              class="captcha-input custom-input"
            >
              <template #prefix>
                <el-icon class="input-icon"><Picture /></el-icon>
              </template>
            </el-input>
            <div class="captcha-image" @click="refreshCaptcha" title="点击刷新验证码">
              <img v-if="captchaImage" :src="captchaImage" alt="验证码" />
              <div v-else class="captcha-loading">
                <el-icon class="loading-icon"><Loading /></el-icon>
              </div>
              <div class="captcha-refresh">
                <el-icon><Refresh /></el-icon>
              </div>
            </div>
          </div>
        </el-form-item>

        <!-- 记住我选项 -->
        <el-form-item>
          <div class="remember-me-row">
            <el-checkbox v-model="rememberMe" size="large">
              <span class="remember-me-text">一周内免登录</span>
            </el-checkbox>
            <el-tooltip
              :content="`勾选后，登录信息将保存${STORAGE_CONFIG.REMEMBER_ME_DAYS}天，即使关闭浏览器也能保持登录状态。不勾选则仅在当前标签页有效。`"
              placement="top"
              effect="light"
            >
              <el-icon class="info-icon"><QuestionFilled /></el-icon>
            </el-tooltip>
          </div>
        </el-form-item>
        
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="login-button"
            :loading="loading"
            @click="handleLogin"
          >
            <span v-if="!loading">登 录</span>
            <span v-else>登录中...</span>
          </el-button>
        </el-form-item>
      </el-form>
      
      <!-- 底部信息 -->
      <div class="login-footer">
        <p>© 2025 学生信息管理系统</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Picture, Loading, Refresh, School, QuestionFilled } from '@element-plus/icons-vue'
import { getCaptcha, login } from '@/api/auth'
import { useUserStore } from '@/stores/counter'
import { rememberMeStorage, STORAGE_CONFIG } from '@/utils/storage'

const router = useRouter()
const userStore = useUserStore()

const loginFormRef = ref(null)
const loading = ref(false)
const captchaImage = ref('')
const captchaKey = ref('')
const rememberMe = ref(rememberMeStorage.get() || false) // 从存储中恢复"记住我"状态

const loginForm = reactive({
  username: '',
  password: '',
  captcha: ''
})

const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  captcha: [
    { required: true, message: '请输入验证码', trigger: 'blur' }
  ]
}

// 刷新验证码
const refreshCaptcha = async () => {
  try {
    const res = await getCaptcha()
    captchaKey.value = res.data.captchaKey
    captchaImage.value = res.data.captchaImage
  } catch (error) {
    console.error('获取验证码失败:', error)
  }
}

// 登录
const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  await loginFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    loading.value = true
    try {
      const res = await login({
        username: loginForm.username,
        password: loginForm.password,
        captcha: loginForm.captcha,
        captchaKey: captchaKey.value
      })
      
      // 保存登录信息，传入"记住我"状态
      userStore.login(res.data, rememberMe.value)
      
      // 根据"记住我"显示不同的提示
      if (rememberMe.value) {
        ElMessage.success(`登录成功！已保存登录状态，${STORAGE_CONFIG.REMEMBER_ME_DAYS}天内免登录`)
      } else {
        ElMessage.success('登录成功！关闭标签页后需重新登录')
      }
      
      // 跳转到首页
      router.push('/dashboard')
    } catch (error) {
      console.error('登录失败:', error)
      // 刷新验证码
      refreshCaptcha()
      loginForm.captcha = ''
    } finally {
      loading.value = false
    }
  })
}

onMounted(() => {
  refreshCaptcha()
})
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-image: url('@/assets/suep.jpg');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  position: relative;
  overflow: hidden;
}

/* 半透明遮罩层 */
.login-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.1);
  z-index: 0;
}

/* 粒子动画背景 */
.particles {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
  z-index: 1;
}

.particle {
  position: absolute;
  width: 10px;
  height: 10px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  animation: float 15s infinite;
}

@keyframes float {
  0% {
    transform: translateY(100vh) scale(0);
    opacity: 0;
  }
  10% {
    opacity: 1;
  }
  90% {
    opacity: 1;
  }
  100% {
    transform: translateY(-100vh) scale(1);
    opacity: 0;
  }
}

.login-box {
  width: 420px;
  padding: 40px 45px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.3);
  position: relative;
  z-index: 2;
  backdrop-filter: blur(10px);
  animation: slideUp 0.6s ease-out;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.login-header {
  text-align: center;
  margin-bottom: 35px;
}

.logo-wrapper {
  width: 70px;
  height: 70px;
  margin: 0 auto 15px;
  background: linear-gradient(135deg, #409eff 0%, #53a8ff 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 10px 30px rgba(64, 158, 255, 0.3);
}

.logo-icon {
  font-size: 36px;
  color: #fff;
}

.login-title {
  margin: 0 0 8px 0;
  color: #303133;
  font-size: 26px;
  font-weight: 600;
  letter-spacing: 2px;
}

.login-subtitle {
  margin: 0;
  color: #909399;
  font-size: 12px;
  letter-spacing: 1px;
}

.login-form {
  width: 100%;
}

.custom-input :deep(.el-input__wrapper) {
  border-radius: 10px;
  border: 2px solid #dcdfe6;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
}

.custom-input :deep(.el-input__wrapper:hover) {
  border-color: #c0c4cc;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
}

.custom-input :deep(.el-input__wrapper.is-focus) {
  border-color: #409eff;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.25);
}

.input-icon {
  font-size: 18px;
  color: #909399;
}

.captcha-row {
  display: flex;
  width: 100%;
  gap: 12px;
}

.captcha-input {
  flex: 1;
}

.captcha-image {
  width: 130px;
  height: 40px;
  border: 1px solid #e4e7ed;
  border-radius: 10px;
  cursor: pointer;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  position: relative;
  transition: all 0.3s ease;
}

.captcha-image:hover {
  border-color: #409eff;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
}

.captcha-image:hover .captcha-refresh {
  opacity: 1;
}

.captcha-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.captcha-refresh {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
  color: #fff;
  font-size: 20px;
}

.captcha-loading {
  display: flex;
  align-items: center;
  justify-content: center;
}

.loading-icon {
  font-size: 20px;
  color: #909399;
  animation: rotate 1s linear infinite;
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

/* 记住我选项样式 */
.remember-me-row {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  gap: 5px;
  padding-left: 4px;
  margin-bottom: 8px;
}

.remember-me-text {
  color: #52504e;
  font-size: 13px;
  font-weight: 450;
  user-select: none;
}

.info-icon {
  color: #575353;
  font-size: 17px;
  cursor: help;
  transition: all 0.3s ease;
}

.info-icon:hover {
  color: #409eff;
  transform: scale(1.15);
}

/* 自定义复选框样式 */
:deep(.el-checkbox) {
  height: auto;
}

:deep(.el-checkbox__label) {
  padding-left: 8px;
  font-size: 14px;
  font-weight: 500;
  color: #303133;
}

:deep(.el-checkbox__input.is-checked .el-checkbox__inner) {
  background-color: #409eff;
  border-color: #409eff;
  border-width: 2.5px;
}

:deep(.el-checkbox__input.is-checked .el-checkbox__inner::after) {
  border-width: 2px;
  border-color: #fff;
}

:deep(.el-checkbox__inner) {
  width: 18px;
  height: 18px;
  border-radius: 4px;
  border-width: 2.5px;
  border-color: #606266;
  transition: all 0.3s ease;
}

:deep(.el-checkbox__inner:hover) {
  border-color: #409eff;
  border-width: 2.5px;
}

.login-button {
  width: 100%;
  height: 45px;
  border-radius: 10px;
  font-size: 16px;
  font-weight: 500;
  letter-spacing: 4px;
  background: linear-gradient(135deg, #409eff 0%, #53a8ff 100%);
  border: none;
  transition: all 0.3s ease;
}

.login-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(64, 158, 255, 0.4);
}

.login-button:active {
  transform: translateY(0);
}

.login-footer {
  text-align: center;
  margin-top: 25px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}

.login-footer p {
  margin: 0;
  color: #909399;
  font-size: 12px;
}

/* 响应式适配 */
@media (max-width: 480px) {
  .login-box {
    width: 90%;
    padding: 30px 25px;
  }
  
  .login-title {
    font-size: 22px;
  }
  
  .captcha-image {
    width: 100px;
  }
}
</style>
