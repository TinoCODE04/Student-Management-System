/**
 * 存储工具类
 * 支持两种存储模式：
 * 1. sessionStorage - 多标签页独立登录（开发/测试推荐）
 * 2. localStorage - 全局单一登录（生产环境推荐）
 * 
 * 使用场景：
 * - 开发/测试时，需要同时登录多个账号 → 使用 sessionStorage
 * - 生产环境，普通用户使用 → 使用 localStorage
 * - 用户选择"记住我" → 使用 localStorage
 */

// ⚙️ 默认存储类型（当用户没有选择"记住我"时使用）
// 'sessionStorage' - 每个标签页独立登录，关闭标签页需重新登录
// 'localStorage' - 所有标签页共享登录，关闭浏览器仍保持登录
const DEFAULT_STORAGE_TYPE = 'sessionStorage' // 👈 可以根据需要修改这里

// ⚙️ "记住我"功能的有效天数配置
// 修改这个数字即可改变保存时长：
// 1 = 1天，7 = 7天，30 = 30天
const REMEMBER_ME_DAYS = 7 // 👈 修改这里！写1就是1天，写7就是7天

class Storage {
  constructor(type = DEFAULT_STORAGE_TYPE) {
    // 根据配置选择存储类型
    this.storage = type === 'localStorage' ? localStorage : sessionStorage
    this.storageType = type
  }

  /**
   * 动态切换存储类型
   * @param {string} type - 'localStorage' 或 'sessionStorage'
   */
  switchStorageType(type) {
    this.storage = type === 'localStorage' ? localStorage : sessionStorage
    this.storageType = type
  }

  /**
   * 设置存储项
   * @param {string} key - 键名
   * @param {any} value - 值（会自动JSON序列化）
   */
  set(key, value) {
    try {
      const stringValue = typeof value === 'string' ? value : JSON.stringify(value)
      this.storage.setItem(key, stringValue)
    } catch (error) {
      console.error(`存储失败 [${key}]:`, error)
    }
  }

  /**
   * 获取存储项
   * @param {string} key - 键名
   * @param {any} defaultValue - 默认值
   * @returns {any} 存储的值
   */
  get(key, defaultValue = null) {
    try {
      // 优先从当前存储中获取
      let value = this.storage.getItem(key)
      
      // 如果当前存储中没有，尝试从另一个存储中获取
      if (value === null) {
        const otherStorage = this.storageType === 'localStorage' ? sessionStorage : localStorage
        value = otherStorage.getItem(key)
      }
      
      if (value === null) return defaultValue
      
      // 尝试解析JSON，如果失败则返回原始字符串
      try {
        return JSON.parse(value)
      } catch {
        return value
      }
    } catch (error) {
      console.error(`读取失败 [${key}]:`, error)
      return defaultValue
    }
  }

  /**
   * 删除存储项（同时从两个存储中删除）
   * @param {string} key - 键名
   */
  remove(key) {
    try {
      this.storage.removeItem(key)
      // 同时从另一个存储中删除
      const otherStorage = this.storageType === 'localStorage' ? sessionStorage : localStorage
      otherStorage.removeItem(key)
    } catch (error) {
      console.error(`删除失败 [${key}]:`, error)
    }
  }

  /**
   * 清空所有存储
   */
  clear() {
    try {
      this.storage.clear()
    } catch (error) {
      console.error('清空存储失败:', error)
    }
  }

  /**
   * 检查键是否存在
   * @param {string} key - 键名
   * @returns {boolean}
   */
  has(key) {
    return this.storage.getItem(key) !== null || 
           (this.storageType === 'localStorage' ? sessionStorage : localStorage).getItem(key) !== null
  }

  /**
   * 获取所有键
   * @returns {string[]}
   */
  keys() {
    return Object.keys(this.storage)
  }
}

// 导出单例
export default new Storage()

// 命名导出，方便按需使用
export const storage = new Storage()

// Token相关的便捷方法
export const tokenStorage = {
  set(token, rememberMe = false) {
    // 根据"记住我"选择存储类型
    const storageType = rememberMe ? 'localStorage' : 'sessionStorage'
    const storageInstance = storageType === 'localStorage' ? localStorage : sessionStorage
    
    if (rememberMe) {
      // 勾选"记住我"时，保存token和过期时间
      // 计算过期时间：当前时间 + 配置的天数
      const expiryTime = Date.now() + REMEMBER_ME_DAYS * 24 * 60 * 60 * 1000
      storageInstance.setItem('token', token)
      storageInstance.setItem('tokenExpiry', expiryTime.toString())
      storageInstance.setItem('storageType', storageType)
      console.log(`✅ 记住我：已保存登录状态，${REMEMBER_ME_DAYS}天内有效`)
      console.log('📅 过期时间：', new Date(expiryTime).toLocaleString())
    } else {
      // 不勾选时，只保存token，不设置过期时间
      storageInstance.setItem('token', token)
      storageInstance.setItem('storageType', storageType)
      console.log('⏱️  会话登录：关闭标签页后需重新登录')
    }
  },
  get() {
    // 优先从 localStorage 获取
    const localToken = localStorage.getItem('token')
    if (localToken) {
      // 检查是否过期
      const expiryTime = localStorage.getItem('tokenExpiry')
      if (expiryTime) {
        const expiry = parseInt(expiryTime)
        const now = Date.now()
        if (now > expiry) {
          // 已过期，清除token
          console.log('⚠️  登录已过期（超过7天），请重新登录')
          this.remove()
          return ''
        }
        // 计算剩余天数
        const remainingDays = Math.ceil((expiry - now) / (24 * 60 * 60 * 1000))
        console.log(`✅ 登录有效，还剩 ${remainingDays} 天`)
      }
      return localToken
    }
    
    // 从 sessionStorage 获取
    return sessionStorage.getItem('token') || ''
  },
  remove() {
    localStorage.removeItem('token')
    sessionStorage.removeItem('token')
    localStorage.removeItem('tokenExpiry')
    sessionStorage.removeItem('tokenExpiry')
    localStorage.removeItem('storageType')
    sessionStorage.removeItem('storageType')
  },
  has() {
    return !!(this.get()) // 使用get方法，自动检查过期
  },
  // 获取剩余有效天数
  getRemainingDays() {
    const expiryTime = localStorage.getItem('tokenExpiry')
    if (!expiryTime) return null
    const expiry = parseInt(expiryTime)
    const now = Date.now()
    if (now > expiry) return 0
    return Math.ceil((expiry - now) / (24 * 60 * 60 * 1000))
  }
}

// 用户信息相关的便捷方法
export const userInfoStorage = {
  set(userInfo, rememberMe = false) {
    const storageType = rememberMe ? 'localStorage' : 'sessionStorage'
    const storageInstance = storageType === 'localStorage' ? localStorage : sessionStorage
    storageInstance.setItem('userInfo', JSON.stringify(userInfo))
  },
  get() {
    // 优先从 localStorage 获取，如果没有再从 sessionStorage 获取
    const localValue = localStorage.getItem('userInfo')
    const sessionValue = sessionStorage.getItem('userInfo')
    const value = localValue || sessionValue
    if (!value) return {}
    try {
      return JSON.parse(value)
    } catch {
      return {}
    }
  },
  remove() {
    localStorage.removeItem('userInfo')
    sessionStorage.removeItem('userInfo')
  },
  has() {
    return !!(localStorage.getItem('userInfo') || sessionStorage.getItem('userInfo'))
  }
}

// "记住我"状态管理
export const rememberMeStorage = {
  set(value) {
    localStorage.setItem('rememberMe', value ? 'true' : 'false')
  },
  get() {
    return localStorage.getItem('rememberMe') === 'true'
  },
  remove() {
    localStorage.removeItem('rememberMe')
  }
}

// 导出配置常量，供其他组件使用
export const STORAGE_CONFIG = {
  REMEMBER_ME_DAYS, // "记住我"的有效天数
  DEFAULT_STORAGE_TYPE // 默认存储类型
}
