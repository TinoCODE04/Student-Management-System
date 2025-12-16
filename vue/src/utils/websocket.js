/**
 * WebSocket 连接管理
 */
class WebSocketClient {
  constructor() {
    this.ws = null
    this.userId = null
    this.reconnectTimer = null
    this.heartbeatTimer = null
    this.messageHandlers = []
  }

  /**
   * 连接 WebSocket
   */
  connect(userId) {
    if (this.ws && this.ws.readyState === WebSocket.OPEN) {
      return
    }

    this.userId = userId
    const wsUrl = `ws://localhost:8080/ws/${userId}`

    try {
      this.ws = new WebSocket(wsUrl)

      this.ws.onopen = () => {
        console.log('WebSocket 连接成功')
        this.startHeartbeat()
      }

      this.ws.onmessage = (event) => {
        try {
          const data = JSON.parse(event.data)
          console.log('收到消息:', data)
          
          // 通知所有监听器
          this.messageHandlers.forEach(handler => {
            handler(data)
          })
        } catch (error) {
          console.error('解析消息失败:', error)
        }
      }

      this.ws.onerror = (error) => {
        console.error('WebSocket 错误:', error)
      }

      this.ws.onclose = () => {
        console.log('WebSocket 连接关闭')
        this.stopHeartbeat()
        this.reconnect()
      }
    } catch (error) {
      console.error('WebSocket 连接失败:', error)
      this.reconnect()
    }
  }

  /**
   * 发送消息
   */
  send(message) {
    if (this.ws && this.ws.readyState === WebSocket.OPEN) {
      this.ws.send(typeof message === 'string' ? message : JSON.stringify(message))
    } else {
      console.warn('WebSocket 未连接')
    }
  }

  /**
   * 添加消息处理器
   */
  onMessage(handler) {
    this.messageHandlers.push(handler)
  }

  /**
   * 移除消息处理器
   */
  offMessage(handler) {
    const index = this.messageHandlers.indexOf(handler)
    if (index > -1) {
      this.messageHandlers.splice(index, 1)
    }
  }

  /**
   * 心跳检测
   */
  startHeartbeat() {
    this.heartbeatTimer = setInterval(() => {
      this.send({ type: 'ping' })
    }, 30000) // 每30秒发送一次心跳
  }

  stopHeartbeat() {
    if (this.heartbeatTimer) {
      clearInterval(this.heartbeatTimer)
      this.heartbeatTimer = null
    }
  }

  /**
   * 重连
   */
  reconnect() {
    if (this.reconnectTimer) {
      return
    }

    this.reconnectTimer = setTimeout(() => {
      console.log('尝试重新连接 WebSocket...')
      this.reconnectTimer = null
      if (this.userId) {
        this.connect(this.userId)
      }
    }, 5000) // 5秒后重连
  }

  /**
   * 关闭连接
   */
  close() {
    this.stopHeartbeat()
    if (this.reconnectTimer) {
      clearTimeout(this.reconnectTimer)
      this.reconnectTimer = null
    }
    if (this.ws) {
      this.ws.close()
      this.ws = null
    }
    this.userId = null
    this.messageHandlers = []
  }
}

// 导出单例
export default new WebSocketClient()
