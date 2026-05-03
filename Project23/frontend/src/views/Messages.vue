<template>
  <el-container>
    <Header />
    <el-main>
      <div class="container">
        <el-card>
          <template #header>
            <span class="card-title">我的消息</span>
          </template>
          
          <el-row :gutter="20">
            <el-col :span="8">
              <div class="chat-list">
                <div
                  v-for="chat in chatList"
                  :key="chat.key"
                  class="chat-item"
                  :class="{ active: selectedChatKey === chat.key }"
                  @click="selectChat(chat)"
                >
                  <el-avatar
                    :size="50"
                    :src="chat.avatar || 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'"
                  />
                  <div class="chat-info">
                    <div class="chat-header">
                      <span class="chat-name">{{ chat.name || '用户' }}</span>
                      <span class="chat-time">{{ formatTime(chat.lastTime) }}</span>
                    </div>
                    <div class="chat-preview">
                      <span class="preview-text">{{ chat.lastMessage || '暂无消息' }}</span>
                      <el-badge :value="chat.unread" :hidden="chat.unread === 0" class="item" />
                    </div>
                  </div>
                </div>
                
                <el-empty v-if="chatList.length === 0" description="暂无消息" :image-size="60" />
              </div>
            </el-col>
            <el-col :span="16">
              <div class="chat-panel">
                <div class="chat-header-bar" v-if="selectedChatKey">
                  <el-avatar
                    :size="40"
                    :src="selectedChat?.avatar || 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'"
                  />
                  <span class="chat-title">{{ selectedChat?.name || '用户' }}</span>
                </div>
                
                <div class="chat-content" ref="chatContentRef" v-if="selectedChatKey">
                  <div
                    v-for="msg in chatMessages"
                    :key="msg.id"
                    class="chat-message"
                    :class="{ 'my-message': isMyMessage(msg) }"
                  >
                    <el-avatar
                      :size="36"
                      :src="isMyMessage(msg) ? myAvatar : (selectedChat?.avatar || 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png')"
                    />
                    <div class="message-content">
                      <div class="message-bubble">
                        {{ msg.content }}
                      </div>
                      <div class="message-time">{{ formatTime(msg.createTime) }}</div>
                    </div>
                  </div>
                </div>
                
                <div class="chat-empty" v-else>
                  <el-icon size="60" color="#c0c4cc"><Message /></el-icon>
                  <p>选择一个对话开始聊天</p>
                </div>
                
                <div class="chat-input" v-if="selectedChatKey">
                  <el-input
                    v-model="messageInput"
                    type="textarea"
                    :rows="3"
                    placeholder="输入消息..."
                    @keyup.enter.ctrl="sendMessage"
                  />
                  <div class="input-actions">
                    <el-button type="primary" :loading="sending" @click="sendMessage">
                      发送 (Ctrl+Enter)
                    </el-button>
                  </div>
                </div>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </div>
    </el-main>
    <Footer />
  </el-container>
</template>

<script setup>
import { ref, onMounted, nextTick, computed } from 'vue'
import { getReceivedMessages, getSentMessages, sendMessage as sendMessageApi, getChatHistory, markAsRead } from '@/api/message'
import Header from '@/components/Header.vue'
import Footer from '@/components/Footer.vue'
import { ElMessage } from 'element-plus'

const chatList = ref([])
const chatMessages = ref([])
const selectedChatKey = ref(null)
const selectedChat = ref(null)
const messageInput = ref('')
const sending = ref(false)
const chatContentRef = ref(null)

const myUserId = computed(() => localStorage.getItem('userId'))
const myAvatar = computed(() => {
  const userInfo = localStorage.getItem('userInfo')
  if (userInfo) {
    return JSON.parse(userInfo).avatar
  }
  return 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
})

const isMyMessage = (msg) => {
  return String(msg.fromUserId) === myUserId.value
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
  
  return date.toLocaleString()
}

const fetchMessages = async () => {
  try {
    const [receivedRes, sentRes] = await Promise.all([
      getReceivedMessages(),
      getSentMessages()
    ])
    
    const received = receivedRes.data || []
    const sent = sentRes.data || []
    
    const chatMap = new Map()
    
    received.forEach(msg => {
      const key = `${msg.fromUserId}_${msg.productId || 'general'}`
      if (!chatMap.has(key)) {
        chatMap.set(key, {
          key,
          otherUserId: msg.fromUserId,
          productId: msg.productId,
          name: msg.fromUserNickname || '用户',
          avatar: null,
          messages: [],
          unread: 0,
          lastTime: null,
          lastMessage: ''
        })
      }
      const chat = chatMap.get(key)
      chat.messages.push(msg)
      if (!msg.isRead) chat.unread++
    })
    
    sent.forEach(msg => {
      const key = `${msg.toUserId}_${msg.productId || 'general'}`
      if (!chatMap.has(key)) {
        chatMap.set(key, {
          key,
          otherUserId: msg.toUserId,
          productId: msg.productId,
          name: msg.toUserNickname || '用户',
          avatar: null,
          messages: [],
          unread: 0,
          lastTime: null,
          lastMessage: ''
        })
      }
      chatMap.get(key).messages.push(msg)
    })
    
    chatList.value = Array.from(chatMap.values()).map(chat => {
      chat.messages.sort((a, b) => new Date(a.createTime) - new Date(b.createTime))
      const lastMsg = chat.messages[chat.messages.length - 1]
      chat.lastTime = lastMsg?.createTime
      chat.lastMessage = lastMsg?.content
      return chat
    }).sort((a, b) => {
      if (b.unread !== a.unread) return b.unread - a.unread
      return new Date(b.lastTime) - new Date(a.lastTime)
    })
  } catch (e) {
    console.error('获取消息失败', e)
  }
}

onMounted(() => {
  fetchMessages()
})

const selectChat = async (chat) => {
  selectedChatKey.value = chat.key
  selectedChat.value = chat
  
  try {
    const res = await getChatHistory(chat.otherUserId, chat.productId)
    chatMessages.value = res.data || []
    
    if (chat.unread > 0) {
      const unreadMessages = chatMessages.value.filter(m => !m.isRead && String(m.fromUserId) !== myUserId.value)
      for (const msg of unreadMessages) {
        await markAsRead(msg.id)
      }
      chat.unread = 0
    }
    
    await nextTick()
    scrollToBottom()
  } catch (e) {
    console.error('获取聊天记录失败', e)
  }
}

const scrollToBottom = () => {
  if (chatContentRef.value) {
    chatContentRef.value.scrollTop = chatContentRef.value.scrollHeight
  }
}

const sendMessage = async () => {
  if (!messageInput.value.trim()) return
  if (!selectedChat.value) return
  
  sending.value = true
  try {
    await sendMessageApi({
      productId: selectedChat.value.productId || undefined,
      toUserId: selectedChat.value.otherUserId,
      content: messageInput.value.trim()
    })
    
    ElMessage.success('发送成功')
    messageInput.value = ''
    
    await selectChat(selectedChat.value)
  } catch (e) {
    console.error('发送失败', e)
  } finally {
    sending.value = false
  }
}
</script>

<style scoped>
.container {
  max-width: 1400px;
  margin: 0 auto;
  padding-top: 80px;
  padding-bottom: 40px;
}

.card-title {
  font-size: 18px;
  font-weight: bold;
}

.chat-list {
  border-right: 1px solid #ebeef5;
  padding-right: 10px;
  min-height: 600px;
  max-height: 70vh;
  overflow-y: auto;
}

.chat-item {
  display: flex;
  padding: 15px;
  cursor: pointer;
  border-radius: 8px;
  margin-bottom: 5px;
  transition: background-color 0.2s;
}

.chat-item:hover {
  background-color: #f5f7fa;
}

.chat-item.active {
  background-color: #ecf5ff;
}

.chat-info {
  flex: 1;
  margin-left: 12px;
  min-width: 0;
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 5px;
}

.chat-name {
  font-weight: bold;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.chat-time {
  font-size: 12px;
  color: #909399;
  flex-shrink: 0;
}

.chat-preview {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.preview-text {
  font-size: 13px;
  color: #909399;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.chat-panel {
  display: flex;
  flex-direction: column;
  min-height: 600px;
  max-height: 70vh;
}

.chat-header-bar {
  display: flex;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #ebeef5;
}

.chat-title {
  margin-left: 12px;
  font-weight: bold;
  font-size: 16px;
}

.chat-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background-color: #fafafa;
}

.chat-empty {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: #909399;
  background-color: #fafafa;
}

.chat-empty p {
  margin-top: 10px;
}

.chat-message {
  display: flex;
  margin-bottom: 20px;
}

.chat-message.my-message {
  flex-direction: row-reverse;
}

.message-content {
  max-width: 60%;
  margin: 0 12px;
}

.chat-message.my-message .message-content {
  text-align: right;
}

.message-bubble {
  display: inline-block;
  padding: 12px 16px;
  border-radius: 8px;
  background-color: #fff;
  word-break: break-word;
  text-align: left;
}

.chat-message.my-message .message-bubble {
  background-color: #409eff;
  color: #fff;
}

.message-time {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}

.chat-input {
  padding: 15px;
  border-top: 1px solid #ebeef5;
}

.input-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 10px;
}
</style>
