<script setup lang="ts">
import type { User, Work } from '~/types'

const config = useRuntimeConfig()

const designers = ref<User[]>([])
const loading = ref(true)

useHead({
  title: '设计师',
  meta: [
    { name: 'description', content: '发现优秀的创意设计师，探索他们的作品。' }
  ]
})

const fetchDesigners = async () => {
  loading.value = true
  try {
    const { data } = await useFetch<User[]>(
      `${config.public.apiBase}/api/users/designers`
    )
    if (data.value) {
      designers.value = data.value
    }
  } catch (error) {
    console.error('Failed to fetch designers:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchDesigners()
})
</script>

<template>
  <div class="container-custom py-8">
    <div class="mb-8">
      <h1 class="text-2xl font-bold text-gray-900">设计师</h1>
      <p class="text-gray-600 mt-1">发现优秀的创意设计师</p>
    </div>

    <div v-if="loading" class="flex items-center justify-center py-16">
      <div class="flex flex-col items-center gap-4">
        <div class="w-12 h-12 border-4 border-primary-200 border-t-primary-600 rounded-full animate-spin" />
        <p class="text-gray-500">加载中...</p>
      </div>
    </div>

    <div v-else-if="designers.length === 0" class="text-center py-16">
      <div class="text-6xl mb-4">🎨</div>
      <h3 class="text-xl font-semibold text-gray-900 mb-2">暂无设计师</h3>
      <p class="text-gray-600 mb-6">正在招募优秀设计师中...</p>
      <NuxtLink to="/register" class="btn-primary">
        成为设计师
      </NuxtLink>
    </div>

    <div v-else class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
      <NuxtLink
        v-for="designer in designers"
        :key="designer.id"
        :to="`/users/${designer.id}`"
        class="card p-6 hover:border-primary-300 transition-all"
      >
        <div class="flex items-center gap-4">
          <div class="w-16 h-16 bg-gray-200 rounded-full flex items-center justify-center overflow-hidden flex-shrink-0">
            <img
              v-if="designer.avatar"
              :src="designer.avatar"
              :alt="designer.username"
              class="w-full h-full object-cover"
            />
            <span v-else class="text-2xl font-medium text-gray-600">
              {{ designer.username?.[0]?.toUpperCase() }}
            </span>
          </div>
          <div class="flex-1 min-w-0">
            <h3 class="font-semibold text-gray-900 truncate">{{ designer.username }}</h3>
            <p v-if="designer.bio" class="text-sm text-gray-500 truncate mt-1">
              {{ designer.bio }}
            </p>
            <p v-else class="text-sm text-gray-400 mt-1">
              加入于 {{ new Date(designer.created_at).toLocaleDateString('zh-CN') }}
            </p>
          </div>
        </div>
      </NuxtLink>
    </div>
  </div>
</template>
