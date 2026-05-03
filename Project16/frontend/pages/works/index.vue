<script setup lang="ts">
import type { Work, Category } from '~/types'

const config = useRuntimeConfig()
const route = useRoute()

const works = ref<Work[]>([])
const categories = ref<Category[]>([])
const loading = ref(true)

const selectedCategory = ref<string>('')
const searchQuery = ref<string>('')
const sortBy = ref<string>('created_at')
const currentPage = ref(1)
const pageSize = 12

useHead({
  title: '作品列表',
  meta: [
    { name: 'description', content: '浏览所有创意作品，支持按分类、关键词搜索和排序筛选。' }
  ]
})

const fetchCategories = async () => {
  try {
    const { data } = await useFetch<Category[]>(
      `${config.public.apiBase}/api/categories`
    )
    if (data.value) {
      categories.value = data.value
    }
  } catch (error) {
    console.error('Failed to fetch categories:', error)
  }
}

const fetchWorks = async () => {
  loading.value = true
  try {
    const params = new URLSearchParams()
    params.append('limit', String(pageSize))
    params.append('offset', String((currentPage.value - 1) * pageSize))
    params.append('sort_by', sortBy.value)

    if (selectedCategory.value) {
      params.append('category', selectedCategory.value)
    }

    if (searchQuery.value) {
      params.append('search', searchQuery.value)
    }

    const { data } = await useFetch<Work[]>(
      `${config.public.apiBase}/api/works?${params.toString()}`
    )
    if (data.value) {
      works.value = data.value
    }
  } catch (error) {
    console.error('Failed to fetch works:', error)
  } finally {
    loading.value = false
  }
}

watch(
  [selectedCategory, searchQuery, sortBy, currentPage],
  () => {
    fetchWorks()
  }
)

onMounted(() => {
  if (route.query.category) {
    selectedCategory.value = String(route.query.category)
  }
  if (route.query.search) {
    searchQuery.value = String(route.query.search)
  }
  fetchCategories()
  fetchWorks()
})
</script>

<template>
  <div class="container-custom py-8">
    <div class="mb-8">
      <h1 class="text-3xl font-bold text-gray-900 mb-2">发现作品</h1>
      <p class="text-gray-600">探索创意，发现灵感</p>
    </div>

    <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-6 mb-8">
      <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div class="md:col-span-2">
          <label class="label">搜索</label>
          <input
            v-model="searchQuery"
            type="text"
            placeholder="搜索作品..."
            class="input"
          />
        </div>
        <div>
          <label class="label">分类</label>
          <select v-model="selectedCategory" class="input">
            <option value="">全部分类</option>
            <option v-for="category in categories" :key="category.id" :value="category.name">
              {{ category.name }}
            </option>
          </select>
        </div>
        <div>
          <label class="label">排序</label>
          <select v-model="sortBy" class="input">
            <option value="created_at">最新发布</option>
            <option value="likes">最多点赞</option>
            <option value="views">最多浏览</option>
          </select>
        </div>
      </div>
    </div>

    <div v-if="loading" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
      <div v-for="i in 12" :key="i" class="card">
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

    <div v-else class="text-center py-16">
      <div class="text-6xl mb-4">🔍</div>
      <h3 class="text-xl font-semibold text-gray-900 mb-2">未找到相关作品</h3>
      <p class="text-gray-600 mb-6">尝试调整搜索条件或浏览其他分类</p>
      <NuxtLink to="/works" class="btn-primary">
        清除筛选
      </NuxtLink>
    </div>

    <div v-if="works.length === pageSize" class="flex justify-center mt-12">
      <button
        @click="currentPage++"
        class="btn-outline"
      >
        加载更多
      </button>
    </div>
  </div>
</template>
