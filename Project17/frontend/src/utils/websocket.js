class WebSocketClient {
  constructor() {
    this.ws = null
    this.reconnectAttempts = 0
    this.maxReconnectAttempts = 10
    this.reconnectInterval = 3000
    this.messageHandlers = new Map()
    this.isConnected = false
  }

  connect() {
    if (this.ws && this.ws.readyState === WebSocket.OPEN) {
      return
    }

    const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:'
    const host = window.location.host
    const wsUrl = `${protocol}//${host}/ws/device`

    try {
      this.ws = new WebSocket(wsUrl)

      this.ws.onopen = () => {
        console.log('WebSocket连接成功')
        this.isConnected = true
        this.reconnectAttempts = 0
        this.notifyConnectionChange(true)
      }

      this.ws.onmessage = (event) => {
        try {
          const data = JSON.parse(event.data)
          this.handleMessage(data)
        } catch (e) {
          console.error('解析WebSocket消息失败:', e)
        }
      }

      this.ws.onclose = () => {
        console.log('WebSocket连接关闭')
        this.isConnected = false
        this.notifyConnectionChange(false)
        this.reconnect()
      }

      this.ws.onerror = (error) => {
        console.error('WebSocket错误:', error)
        this.isConnected = false
      }
    } catch (e) {
      console.error('创建WebSocket连接失败:', e)
      this.reconnect()
    }
  }

  reconnect() {
    if (this.reconnectAttempts < this.maxReconnectAttempts) {
      this.reconnectAttempts++
      console.log(`尝试重新连接 (${this.reconnectAttempts}/${this.maxReconnectAttempts})...`)
      setTimeout(() => {
        this.connect()
      }, this.reconnectInterval)
    } else {
      console.error('重连次数已达上限，停止重连')
    }
  }

  notifyConnectionChange(isConnected) {
    const handlers = this.messageHandlers.get('CONNECTION_CHANGE')
    if (handlers) {
      handlers.forEach(handler => {
        try {
          handler(isConnected)
        } catch (e) {
          console.error('处理连接状态变化时出错:', e)
        }
      })
    }
  }

  handleMessage(data) {
    const type = data.type
    const handlers = this.messageHandlers.get(type)
    if (handlers) {
      handlers.forEach(handler => {
        try {
          handler(data.data)
        } catch (e) {
          console.error(`处理消息类型 ${type} 时出错:`, e)
        }
      })
    }
  }

  on(type, handler) {
    if (!this.messageHandlers.has(type)) {
      this.messageHandlers.set(type, [])
    }
    this.messageHandlers.get(type).push(handler)
  }

  off(type, handler) {
    const handlers = this.messageHandlers.get(type)
    if (handlers) {
      const index = handlers.indexOf(handler)
      if (index > -1) {
        handlers.splice(index, 1)
      }
    }
  }

  send(message) {
    if (this.ws && this.ws.readyState === WebSocket.OPEN) {
      this.ws.send(JSON.stringify(message))
    }
  }

  close() {
    if (this.ws) {
      this.ws.close()
    }
  }
}

const wsClient = new WebSocketClient()

export default wsClient
