<script setup lang="ts">
import type { Work } from '~/types'

const authStore = useAuthStore()
const config = useRuntimeConfig()

const favorites = ref<{ work_id: string; work: Work | null }[]>([])
const works = ref<Work[]>([])
const loading = ref(true)

useHead({
  title: '我的收藏',
  meta: [
    { name: 'description', content: '查看和管理我收藏的创意作品。' }
  ]
})

const fetchFavorites = async () => {
  if (!authStore.isAuthenticated) {
    navigateTo('/login')
    return
  }

  loading.value = true
  try {
    const { data } = await useFetch<{ work_id: string }[]>(
      `${config.public.apiBase}/api/users/me/favorites`,
      {
        headers: {
          Authorization: `Bearer ${authStore.token}`
        }
      }
    )
    
    if (data.value && data.value.length > 0) {
      const workIds = data.value.map(f => f.work_id)
      
      const worksPromises = workIds.map(async (workId) => {
        const { data: workData } = await useFetch<Work>(
          `${config.public.apiBase}/api/works/${workId}`
        )
        return workData.value
      })
      
      const results = await Promise.all(worksPromises)
      works.value = results.filter((w): w is Work => w !== null && w !== undefined)
    }
  } catch (error) {
    console.error('Failed to fetch favorites:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchFavorites()
})
</script>

<template>
  <div class="container-custom py-8">
    <div class="mb-8">
      <h1 class="text-2xl font-bold text-gray-900">我的收藏</h1>
      <p class="text-gray-600 mt-1">您收藏的创意作品</p>
    </div>

    <div v-if="loading" class="flex items-center justify-center py-16">
      <div class="flex flex-col items-center gap-4">
        <div class="w-12 h-12 border-4 border-primary-200 border-t-primary-600 rounded-full animate-spin" />
        <p class="text-gray-500">加载中...</p>
      </div>
    </div>

    <div v-else-if="works.length === 0" class="text-center py-16">
      <div class="text-6xl mb-4">💝</div>
      <h3 class="text-xl font-semibold text-gray-900 mb-2">暂无收藏</h3>
      <p class="text-gray-600 mb-6">您还没有收藏任何作品</p>
      <NuxtLink to="/works" class="btn-primary">
        浏览作品
      </NuxtLink>
    </div>

    <div v-else class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
      <WorkCard v-for="work in works" :key="work.id" :work="work" />
    </div>
  </div>
</template>
