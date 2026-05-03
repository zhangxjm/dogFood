<template>
  <div class="messages-container">
    <n-card>
      <template #header>
        <n-space justify="space-between" style="width: 100%;">
          <n-text depth="1" strong style="font-size: 18px;">我的消息</n-text>
          <n-space>
            <n-button type="primary" quaternary @click="markAllRead" :disabled="unreadCount === 0">
              全部已读
            </n-button>
          </n-space>
        </n-space>
      </template>

      <n-spin :show="loading">
        <n-empty v-if="messages.length === 0 && !loading" description="暂无消息">
          <template #extra>
            <n-button size="small" @click="loadMessages">刷新</n-button>
          </template>
        </n-empty>

        <n-list v-else>
          <n-list-item
            v-for="msg in messages"
            :key="msg.id"
            class="message-item"
            :class="{ unread: !msg.isRead }"
            @click="viewMessage(msg)"
          >
            <n-list-item-meta>
              <template #avatar>
                <n-avatar :color="msg.messageType === 'system' ? '#18a058' : '#2080f0'">
                  <n-icon>
                    <component :is="msg.messageType === 'system' ? iosSettings : iosMail" />
                  </n-icon>
                </n-avatar>
              </template>
              <template #title>
                <n-space align="center">
                  <n-text :strong="!msg.isRead">{{ msg.title }}</n-text>
                  <n-tag v-if="!msg.isRead" type="warning" size="small">
                    未读
                  </n-tag>
                </n-space>
              </template>
              <template #description>
                <n-text depth="3">{{ formatTime(msg.createTime) }}</n-text>
              </template>
            </n-list-item-meta>
          </n-list-item>
        </n-list>

        <n-space justify="center" v-if="total > 0" style="margin-top: 24px;">
          <n-pagination
            v-model:page="page"
            :page-count="Math.ceil(total / pageSize)"
            :item-count="total"
            @update:page="loadMessages"
          />
        </n-space>
      </n-spin>
    </n-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useMessage, useDialog } from 'naive-ui'
import {
  iosMail,
  iosSettings
} from '@vicons/ionicons5'
import { messageApi } from '@/api/message'
import dayjs from 'dayjs'

const messageService = useMessage()
const dialog = useDialog()

const loading = ref(false)
const messages = ref([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

const unreadCount = computed(() => {
  return messages.value.filter(m => !m.isRead).length
})

const formatTime = (time) => {
  if (!time) return ''
  return dayjs(time).format('YYYY-MM-DD HH:mm')
}

const loadMessages = async () => {
  loading.value = true
  try {
    const res = await messageApi.list({
      page: page.value,
      pageSize: pageSize.value
    })
    if (res.code === 200) {
      messages.value = res.data?.records || []
      total.value = res.data?.total || 0
    }
  } catch (e) {
    console.error('加载消息失败:', e)
  } finally {
    loading.value = false
  }
}

const viewMessage = (msg) => {
  if (!msg.isRead) {
    messageApi.markRead(msg.id).catch(() => {})
  }

  dialog.info({
    title: msg.title,
    content: msg.content,
    positiveText: '关闭',
    onPositiveClick: () => {
      if (!msg.isRead) {
        msg.isRead = true
      }
    }
  })
}

const markAllRead = async () => {
  try {
    const res = await messageApi.markAllRead()
    if (res.code === 200) {
      messages.value.forEach(m => {
        m.isRead = true
      })
      messageService.success('已全部标记为已读')
    }
  } catch (e) {
    console.error('标记已读失败:', e)
  }
}

onMounted(() => {
  loadMessages()
})
</script>

<style scoped>
.messages-container {
  max-width: 900px;
}

.message-item {
  cursor: pointer;
  transition: background-color 0.2s;
}

.message-item:hover {
  background-color: #f5f5f5;
}

.unread {
  background-color: #e6f7ff;
}

.unread:hover {
  background-color: #bae7ff;
}
</style>
