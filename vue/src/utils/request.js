import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'
import { tokenStorage, userInfoStorage } from './storage'

// 创建axios实例
const request = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    // 获取token（使用 sessionStorage）
    const token = tokenStorage.get()
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    const res = response.data
    
    // 如果返回的code不是200，说明出错了
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      
      // 401: 未登录或Token过期
      if (res.code === 401) {
        tokenStorage.remove()
        userInfoStorage.remove()
        router.push('/login')
      }
      
      // 403: 无权限
      if (res.code === 403) {
        ElMessage.error('无权限访问')
      }
      
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    
    return res
  },
  (error) => {
    console.error('响应错误:', error)
    
    if (error.response) {
      const status = error.response.status
      
      if (status === 401) {
        ElMessage.error('登录已过期，请重新登录')
        tokenStorage.remove()
        userInfoStorage.remove()
        router.push('/login')
      } else if (status === 403) {
        ElMessage.error('无权限访问')
      } else if (status === 404) {
        ElMessage.error('请求的资源不存在')
      } else if (status === 500) {
        ElMessage.error('服务器内部错误')
      } else {
        ElMessage.error(error.message || '网络错误')
      }
    } else {
      ElMessage.error('网络连接失败')
    }
    
    return Promise.reject(error)
  }
)

export default request
