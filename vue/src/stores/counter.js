import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useUserStore = defineStore('user', () => {
  // 状态
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))
  
  // 计算属性
  const isLoggedIn = computed(() => !!token.value)
  const isTeacher = computed(() => userInfo.value.role === 'teacher')
  const isStudent = computed(() => userInfo.value.role === 'student')
  
  // 操作
  function setToken(newToken) {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }
  
  function setUserInfo(info) {
    userInfo.value = info
    localStorage.setItem('userInfo', JSON.stringify(info))
  }
  
  function login(loginData) {
    setToken(loginData.token)
    setUserInfo({
      userId: loginData.userId,
      username: loginData.username,
      name: loginData.name,
      role: loginData.role,
      avatar: loginData.avatar
    })
  }
  
  function logout() {
    token.value = ''
    userInfo.value = {}
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }
  
  return {
    token,
    userInfo,
    isLoggedIn,
    isTeacher,
    isStudent,
    setToken,
    setUserInfo,
    login,
    logout
  }
})
