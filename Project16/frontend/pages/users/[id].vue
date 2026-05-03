<script setup lang="ts">
import type { User, Work } from '~/types'

const config = useRuntimeConfig()
const route = useRoute()

const user = ref<User | null>(null)
const works = ref<Work[]>([])
const loading = ref(true)

const userId = computed(() => route.params.id as string)

useHead({
  title: computed(() => user.value?.username || '用户主页'),
  meta: computed(() => [
    { name: 'description', content: user.value?.bio || `查看 ${user.value?.username} 的创意作品` }
  ])
})

const fetchUser = async () => {
  loading.value = true
  try {
    const { data: userData } = await useFetch<User>(
      `${config.public.apiBase}/api/users/${userId.value}`
    )
    if (userData.value) {
      user.value = userData.value
    }

    const { data: worksData } = await useFetch<Work[]>(
      `${config.public.apiBase}/api/works?designer_id=${userId.value}`
    )
    if (worksData.value) {
      works.value = worksData.value
    }
  } catch (error) {
    console.error('Failed to fetch user:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchUser()
})
</script>

<template>
  <div class="container-custom py-8">
    <div v-if="loading" class="flex items-center justify-center min-h-[60vh]">
      <div class="flex flex-col items-center gap-4">
        <div class="w-12 h-12 border-4 border-primary-200 border-t-primary-600 rounded-full animate-spin" />
        <p class="text-gray-500">加载中...</p>
      </div>
    </div>

    <div v-else-if="!user" class="text-center py-16">
      <div class="text-6xl mb-4">😔</div>
      <h3 class="text-xl font-semibold text-gray-900 mb-2">用户不存在</h3>
      <p class="text-gray-600 mb-6">该用户可能已被删除或不存在</p>
      <NuxtLink to="/" class="btn-primary">
        返回首页
      </NuxtLink>
    </div>

    <div v-else>
      <div class="card p-8 mb-8">
        <div class="flex flex-col md:flex-row items-center md:items-start gap-6">
          <div class="w-32 h-32 bg-gray-200 rounded-full flex items-center justify-center overflow-hidden flex-shrink-0">
            <img
              v-if="user.avatar"
              :src="user.avatar"
              :alt="user.username"
              class="w-full h-full object-cover"
            />
            <span v-else class="text-4xl font-medium text-gray-600">
              {{ user.username?.[0]?.toUpperCase() }}
            </span>
          </div>
          
          <div class="flex-1 text-center md:text-left">
            <div class="flex flex-col md:flex-row items-center gap-3 mb-3">
              <h1 class="text-2xl font-bold text-gray-900">{{ user.username }}</h1>
              <span v-if="user.is_designer" class="badge badge-primary">
                设计师
              </span>
            </div>
            
            <p v-if="user.bio" class="text-gray-600 mb-4">
              {{ user.bio }}
            </p>
            
            <div class="flex items-center justify-center md:justify-start gap-6 text-sm text-gray-500">
              <span>作品: {{ works.length }}</span>
              <span>加入于: {{ new Date(user.created_at).toLocaleDateString('zh-CN') }}</span>
            </div>
          </div>
        </div>
      </div>

      <div class="mb-6">
        <h2 class="section-title mb-0">作品</h2>
      </div>

      <div v-if="works.length > 0" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
        <WorkCard v-for="work in works" :key="work.id" :work="work" />
      </div>

      <div v-else class="text-center py-16">
        <div class="text-6xl mb-4">🎨</div>
        <h3 class="text-xl font-semibold text-gray-900 mb-2">暂无作品</h3>
        <p class="text-gray-600">该设计师还没有上传作品</p>
      </div>
    </div>
  </div>
</template>
