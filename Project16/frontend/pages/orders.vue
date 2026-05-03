<script setup lang="ts">
import type { Order } from '~/types'

const authStore = useAuthStore()
const config = useRuntimeConfig()

const orders = ref<Order[]>([])
const loading = ref(true)

useHead({
  title: '我的订单',
  meta: [
    { name: 'description', content: '查看和管理我的订单记录。' }
  ]
})

const fetchOrders = async () => {
  if (!authStore.isAuthenticated) {
    navigateTo('/login')
    return
  }

  loading.value = true
  try {
    const { data } = await useFetch<Order[]>(
      `${config.public.apiBase}/api/orders`,
      {
        headers: {
          Authorization: `Bearer ${authStore.token}`
        }
      }
    )
    if (data.value) {
      orders.value = data.value
    }
  } catch (error) {
    console.error('Failed to fetch orders:', error)
  } finally {
    loading.value = false
  }
}

const getStatusBadgeClass = (status: string) => {
  switch (status) {
    case 'paid':
      return 'badge-success'
    case 'pending':
      return 'badge-warning'
    case 'cancelled':
      return 'badge-danger'
    default:
      return 'badge-primary'
  }
}

const getStatusText = (status: string) => {
  switch (status) {
    case 'paid':
      return '已支付'
    case 'pending':
      return '待支付'
    case 'cancelled':
      return '已取消'
    case 'completed':
      return '已完成'
    default:
      return status
  }
}

onMounted(() => {
  fetchOrders()
})
</script>

<template>
  <div class="container-custom py-8">
    <div class="mb-8">
      <h1 class="text-2xl font-bold text-gray-900">我的订单</h1>
      <p class="text-gray-600 mt-1">查看和管理您的订单记录</p>
    </div>

    <div v-if="loading" class="flex items-center justify-center py-16">
      <div class="flex flex-col items-center gap-4">
        <div class="w-12 h-12 border-4 border-primary-200 border-t-primary-600 rounded-full animate-spin" />
        <p class="text-gray-500">加载中...</p>
      </div>
    </div>

    <div v-else-if="orders.length === 0" class="text-center py-16">
      <div class="text-6xl mb-4">📦</div>
      <h3 class="text-xl font-semibold text-gray-900 mb-2">暂无订单</h3>
      <p class="text-gray-600 mb-6">您还没有任何订单记录</p>
      <NuxtLink to="/works" class="btn-primary">
        浏览作品
      </NuxtLink>
    </div>

    <div v-else class="space-y-4">
      <div
        v-for="order in orders"
        :key="order.id"
        class="card p-6"
      >
        <div class="flex flex-col md:flex-row md:items-center justify-between gap-4 mb-4">
          <div class="flex items-center gap-4">
            <div>
              <p class="text-sm text-gray-500">订单号</p>
              <p class="font-mono font-medium text-gray-900">{{ order.order_number }}</p>
            </div>
            <span :class="['badge', getStatusBadgeClass(order.status)]">
              {{ getStatusText(order.status) }}
            </span>
          </div>
          <div class="flex items-center gap-4">
            <p class="text-sm text-gray-500">
              {{ new Date(order.created_at).toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric' }) }}
            </p>
          </div>
        </div>

        <div class="border-t border-gray-100 pt-4">
          <div class="space-y-3">
            <div
              v-for="(item, index) in order.items"
              :key="index"
              class="flex items-center gap-4 p-3 bg-gray-50 rounded-lg"
            >
              <div class="w-16 h-16 bg-gray-200 rounded-lg overflow-hidden flex-shrink-0">
                <img
                  v-if="item.image_url"
                  :src="item.image_url.startsWith('http') ? item.image_url : `${config.public.apiBase}${item.image_url}`"
                  :alt="item.title"
                  class="w-full h-full object-cover"
                />
              </div>
              <div class="flex-1 min-w-0">
                <p class="font-medium text-gray-900 truncate">{{ item.title }}</p>
              </div>
              <div class="text-right">
                <p class="font-semibold text-gray-900">¥{{ item.price }}</p>
              </div>
            </div>
          </div>

          <div class="flex items-center justify-between mt-4 pt-4 border-t border-gray-100">
            <div class="flex items-center gap-2">
              <NuxtLink
                :to="`/orders/${order.id}`"
                class="text-primary-600 hover:text-primary-700 text-sm font-medium"
              >
                查看详情
              </NuxtLink>
            </div>
            <div class="text-right">
              <p class="text-sm text-gray-500">订单总额</p>
              <p class="text-xl font-bold text-primary-600">¥{{ order.total_amount }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
