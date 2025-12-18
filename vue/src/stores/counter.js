import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { tokenStorage, userInfoStorage, rememberMeStorage } from '@/utils/storage'

export const useUserStore = defineStore('user', () => {
  // 状态（支持"记住我"功能，从 localStorage 或 sessionStorage 获取）
  const token = ref(tokenStorage.get() || '')
  const userInfo = ref(userInfoStorage.get() || {})
  const rememberMe = ref(rememberMeStorage.get() || false)
  
  // 计算属性
  const isLoggedIn = computed(() => !!token.value)
  const isTeacher = computed(() => userInfo.value.role === 'teacher')
  const isStudent = computed(() => userInfo.value.role === 'student')
  const isAdmin = computed(() => userInfo.value.role === 'admin')
  
  // 操作
  function setToken(newToken, remember = false) {
    token.value = newToken
    tokenStorage.set(newToken, remember)
  }
  
  function setUserInfo(info, remember = false) {
    userInfo.value = info
    userInfoStorage.set(info, remember)
  }
  
  function setRememberMe(value) {
    rememberMe.value = value
    rememberMeStorage.set(value)
  }
  
  function login(loginData, remember = false) {
    setToken(loginData.token, remember)
    setUserInfo({
      userId: loginData.userId,
      username: loginData.username,
      name: loginData.name,
      role: loginData.role,
      avatar: loginData.avatar
    }, remember)
    setRememberMe(remember)
  }
  
  function logout() {
    token.value = ''
    userInfo.value = {}
    rememberMe.value = false
    tokenStorage.remove()
    userInfoStorage.remove()
    rememberMeStorage.remove()
  }
  
  return {
    token,
    userInfo,
    rememberMe,
    isLoggedIn,
    isTeacher,
    isStudent,
    isAdmin,
    setToken,
    setUserInfo,
    setRememberMe,
    login,
    logout
  }
})
