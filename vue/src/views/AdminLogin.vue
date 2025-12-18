<template>
  <div class="admin-login-container">
    <!-- 动态背景粒子效果 -->
    <div class="particles">
      <span v-for="i in 20" :key="i" class="particle"></span>
    </div>
    
    <div class="admin-login-box">
      <!-- Logo 和标题区域 -->
      <div class="admin-header">
        <div class="logo-wrapper">
          <el-icon class="logo-icon"><Monitor /></el-icon>
        </div>
        <h2 class="admin-title">管理员登录</h2>
        <p class="admin-subtitle">Administrator Login</p>
      </div>
      
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        class="admin-form"
        @keyup.enter="handleLogin"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="管理员账号"
            size="large"
            class="custom-input"
          >
            <template #prefix>
              <el-icon class="input-icon"><UserFilled /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="密码"
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
              placeholder="验证码"
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
        
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="admin-login-btn"
            :loading="loading"
            @click="handleLogin"
          >
            <span v-if="!loading">登 录</span>
            <span v-else>登录中...</span>
          </el-button>
        </el-form-item>
        
        <el-form-item>
          <div class="back-link-wrapper">
            <router-link to="/login" class="back-link">
              <el-icon><Back /></el-icon>
              <span>返回普通登录</span>
            </router-link>
          </div>
        </el-form-item>
      </el-form>
      
      <!-- 底部信息 -->
      <div class="admin-footer">
        <p>© 2025 学生信息管理系统 - 管理员端</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { UserFilled, Lock, Picture, Loading, Refresh, Monitor, Back } from '@element-plus/icons-vue'
import { getCaptcha, login } from '@/api/auth'
import { tokenStorage, userInfoStorage } from '@/utils/storage'

const router = useRouter()
const loginFormRef = ref()
const loading = ref(false)
const captchaImage = ref('')

const loginForm = reactive({
  username: '',
  password: '',
  captcha: '',
  captchaKey: ''
})

const loginRules = {
  username: [
    { required: true, message: '请输入管理员账号', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  captcha: [
    { required: true, message: '请输入验证码', trigger: 'blur' }
  ]
}

// 获取验证码
const refreshCaptcha = async () => {
  try {
    const { data } = await getCaptcha()
    captchaImage.value = data.captchaImage
    loginForm.captchaKey = data.captchaKey
  } catch (error) {
    ElMessage.error('获取验证码失败')
  }
}

// 登录
const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const { data } = await login(loginForm)
        
        // 验证是否是管理员
        if (data.role !== 'admin') {
          ElMessage.error('此账号不是管理员账号，请使用普通登录')
          await refreshCaptcha()
          loginForm.captcha = ''
          return
        }
        
        // 保存token和用户信息到 storage
        tokenStorage.set(data.token)
        userInfoStorage.set({
          userId: data.userId,
          username: data.username,
          name: data.name,
          role: data.role,
          avatar: data.avatar
        })
        
        ElMessage.success('登录成功')
        router.push('/admin/dashboard')
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '登录失败')
        await refreshCaptcha()
        loginForm.captcha = ''
      } finally {
        loading.value = false
      }
    }
  })
}

onMounted(() => {
  refreshCaptcha()
})
</script>

<style scoped>
.admin-login-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-image: url('@/assets/admin-bg.jpg');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  position: relative;
  overflow: hidden;
}

/* 半透明遮罩层 */
.admin-login-container::before {
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

.admin-login-box {
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

.admin-header {
  text-align: center;
  margin-bottom: 35px;
}

.logo-wrapper {
  width: 70px;
  height: 70px;
  margin: 0 auto 15px;
  background: linear-gradient(135deg, #222326 0%, #351d4e 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 10px 30px rgba(102, 126, 234, 0.3);
}

.logo-icon {
  font-size: 36px;
  color: #fff;
}

.admin-title {
  margin: 0 0 8px 0;
  color: #303133;
  font-size: 26px;
  font-weight: 600;
  letter-spacing: 2px;
}

.admin-subtitle {
  margin: 0;
  color: #909399;
  font-size: 12px;
  letter-spacing: 1px;
}

.admin-form {
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
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
}

.custom-input :deep(.el-input__wrapper.is-focus) {
  border-color: #667eea;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.25);
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
  border-color: #667eea;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
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

.admin-login-btn {
  width: 100%;
  height: 45px;
  border-radius: 10px;
  font-size: 16px;
  font-weight: 500;
  letter-spacing: 4px;
  background: linear-gradient(135deg, #222326 0%, #351d4e 100%);
  border: none;
  transition: all 0.3s ease;
}

.admin-login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
}

.admin-login-btn:active {
  transform: translateY(0);
}

.back-link-wrapper {
  text-align: center;
}

.back-link {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: #352636;
  text-decoration: none;
  font-size: 14px;
  transition: all 0.3s;
  padding: 8px 16px;
  border-radius: 6px;
}

.back-link:hover {
  background: rgba(102, 126, 234, 0.1);
  color: #764ba2;
}

.admin-footer {
  text-align: center;
  margin-top: 25px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}

.admin-footer p {
  margin: 0;
  color: #909399;
  font-size: 12px;
}

/* 响应式适配 */
@media (max-width: 480px) {
  .admin-login-box {
    width: 90%;
    padding: 30px 25px;
  }
  
  .admin-title {
    font-size: 22px;
  }
  
  .captcha-image {
    width: 100px;
  }
}
</style>

