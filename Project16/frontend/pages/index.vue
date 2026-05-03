<script setup lang="ts">
import type { Work, Category } from '~/types'

const config = useRuntimeConfig()
const works = ref<Work[]>([])
const categories = ref<Category[]>([])
const loading = ref(true)

useHead({
  title: '首页',
  meta: [
    { name: 'description', content: '文创作品平台 - 发现与分享创意艺术作品，汇聚全球优秀设计师和创意作品。' },
    { name: 'keywords', content: '文创, 设计, 艺术, 插画, 平面设计, 3D建模' }
  ]
})

const fetchData = async () => {
  loading.value = true
  try {
    const { data: worksData } = await useFetch<Work[]>(
      `${config.public.apiBase}/api/works/featured?limit=12`
    )
    if (worksData.value) {
      works.value = worksData.value
    }

    const { data: categoriesData } = await useFetch<Category[]>(
      `${config.public.apiBase}/api/categories`
    )
    if (categoriesData.value) {
      categories.value = categoriesData.value
    }
  } catch (error) {
    console.error('Failed to fetch data:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchData()
})
</script>

<template>
  <div>
    <section class="relative overflow-hidden bg-gradient-to-br from-primary-50 via-white to-accent-50">
      <div class="absolute inset-0 overflow-hidden">
        <div class="absolute -top-40 -right-40 w-80 h-80 bg-primary-200/30 rounded-full blur-3xl" />
        <div class="absolute -bottom-40 -left-40 w-80 h-80 bg-accent-200/30 rounded-full blur-3xl" />
      </div>
      
      <div class="container-custom relative py-20 md:py-32">
        <div class="max-w-3xl text-center mx-auto">
          <h1 class="text-4xl md:text-5xl lg:text-6xl font-bold text-gray-900 mb-6">
            发现创意
            <span class="text-transparent bg-clip-text bg-gradient-to-r from-primary-600 to-accent-600">
              无限可能
            </span>
          </h1>
          <p class="text-lg md:text-xl text-gray-600 mb-8 max-w-2xl mx-auto">
            汇聚全球优秀设计师，展示最具创意的艺术作品。在这里，每一个创意都值得被看见。
          </p>
          <div class="flex flex-col sm:flex-row items-center justify-center gap-4">
            <NuxtLink to="/works" class="btn-primary text-lg px-8 py-3">
              浏览作品
            </NuxtLink>
            <NuxtLink to="/register" class="btn-outline text-lg px-8 py-3">
              成为设计师
            </NuxtLink>
          </div>
        </div>
      </div>
    </section>

    <section class="py-16 bg-white">
      <div class="container-custom">
        <div class="flex items-center justify-between mb-8">
          <h2 class="section-title mb-0">热门分类</h2>
          <NuxtLink to="/works" class="text-primary-600 hover:text-primary-700 text-sm font-medium">
            查看全部 →
          </NuxtLink>
        </div>
        
        <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
          <NuxtLink
            v-for="category in categories"
            :key="category.id"
            :to="`/works?category=${category.name}`"
            class="group card p-6 text-center hover:border-primary-300"
          >
            <div class="text-4xl mb-3">{{ category.icon || '🎨' }}</div>
            <h3 class="font-semibold text-gray-900 group-hover:text-primary-600 transition-colors">
              {{ category.name }}
            </h3>
            <p class="text-sm text-gray-500 mt-1">
              {{ category.work_count }} 件作品
            </p>
          </NuxtLink>
        </div>
      </div>
    </section>

    <section class="py-16 bg-gray-50">
      <div class="container-custom">
        <div class="flex items-center justify-between mb-8">
          <h2 class="section-title mb-0">精选作品</h2>
          <NuxtLink to="/works" class="text-primary-600 hover:text-primary-700 text-sm font-medium">
            查看全部 →
          </NuxtLink>
        </div>
        
        <div v-if="loading" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
          <div v-for="i in 8" :key="i" class="card">
            <div class="aspect-video bg-gray-200 animate-pulse" />
            <div class="p-4 space-y-3">
              <div class="h-4 bg-gray-200 rounded animate-pulse" />
              <div class="h-3 bg-gray-100 rounded animate-pulse w-2/3" />
            </div>
          </div>
        </div>
        
        <div v-else-if="works.length > 0" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
          <WorkCard v-for="work in works" :key="work.id" :work="work" />
        </div>
        
        <div v-else class="text-center py-12">
          <p class="text-gray-500">暂无作品</p>
        </div>
      </div>
    </section>

    <section class="py-16 bg-gradient-to-r from-primary-600 to-accent-600">
      <div class="container-custom text-center">
        <h2 class="text-3xl md:text-4xl font-bold text-white mb-4">
          开始你的创意之旅
        </h2>
        <p class="text-primary-100 text-lg mb-8 max-w-2xl mx-auto">
          无论你是设计师还是创意爱好者，这里都是你展示和发现优秀作品的理想平台。
        </p>
        <div class="flex flex-col sm:flex-row items-center justify-center gap-4">
          <NuxtLink
            to="/register"
            class="inline-flex items-center justify-center px-8 py-3 text-lg font-medium text-primary-700 bg-white rounded-lg hover:bg-gray-50 transition-colors"
          >
            立即注册
          </NuxtLink>
          <NuxtLink
            to="/works"
            class="inline-flex items-center justify-center px-8 py-3 text-lg font-medium text-white border-2 border-white rounded-lg hover:bg-white/10 transition-colors"
          >
            探索作品
          </NuxtLink>
        </div>
      </div>
    </section>
  </div>
</template>
