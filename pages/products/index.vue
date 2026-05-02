<template>
  <div class="container-custom py-8">
    <div class="mb-8">
      <h1 class="text-3xl font-bold text-gray-800 mb-2">全部商品</h1>
      <p class="text-gray-500">浏览所有精选商品</p>
    </div>

    <div class="flex flex-col lg:flex-row gap-8">
      <aside class="lg:w-64 flex-shrink-0">
        <div class="card p-6 mb-6">
          <h3 class="font-semibold text-gray-800 mb-4">商品分类</h3>
          <ul class="space-y-2">
            <li>
              <button 
                @click="selectedCategory = null"
                :class="[
                  'w-full text-left px-3 py-2 rounded-lg transition-colors',
                  selectedCategory === null ? 'bg-primary text-white' : 'text-gray-600 hover:bg-gray-100'
                ]"
              >
                全部商品
              </button>
            </li>
            <li v-for="category in categories" :key="category.id">
              <button 
                @click="selectedCategory = category.id"
                :class="[
                  'w-full text-left px-3 py-2 rounded-lg transition-colors flex items-center',
                  selectedCategory === category.id ? 'bg-primary text-white' : 'text-gray-600 hover:bg-gray-100'
                ]"
              >
                <span class="mr-2">{{ category.icon }}</span>
                <span>{{ category.name }}</span>
              </button>
            </li>
          </ul>
        </div>

        <div class="card p-6">
          <h3 class="font-semibold text-gray-800 mb-4">价格筛选</h3>
          <div class="space-y-2">
            <button 
              @click="priceRange = null"
              :class="[
                'w-full text-left px-3 py-2 rounded-lg transition-colors text-sm',
                priceRange === null ? 'bg-primary text-white' : 'text-gray-600 hover:bg-gray-100'
              ]"
            >
              全部价格
            </button>
            <button 
              @click="priceRange = { min: 0, max: 100 }"
              :class="[
                'w-full text-left px-3 py-2 rounded-lg transition-colors text-sm',
                priceRange?.min === 0 && priceRange?.max === 100 ? 'bg-primary text-white' : 'text-gray-600 hover:bg-gray-100'
              ]"
            >
              ¥100以下
            </button>
            <button 
              @click="priceRange = { min: 100, max: 500 }"
              :class="[
                'w-full text-left px-3 py-2 rounded-lg transition-colors text-sm',
                priceRange?.min === 100 && priceRange?.max === 500 ? 'bg-primary text-white' : 'text-gray-600 hover:bg-gray-100'
              ]"
            >
              ¥100 - ¥500
            </button>
            <button 
              @click="priceRange = { min: 500, max: 2000 }"
              :class="[
                'w-full text-left px-3 py-2 rounded-lg transition-colors text-sm',
                priceRange?.min === 500 && priceRange?.max === 2000 ? 'bg-primary text-white' : 'text-gray-600 hover:bg-gray-100'
              ]"
            >
              ¥500 - ¥2000
            </button>
            <button 
              @click="priceRange = { min: 2000, max: null }"
              :class="[
                'w-full text-left px-3 py-2 rounded-lg transition-colors text-sm',
                priceRange?.min === 2000 ? 'bg-primary text-white' : 'text-gray-600 hover:bg-gray-100'
              ]"
            >
              ¥2000以上
            </button>
          </div>
        </div>
      </aside>

      <main class="flex-1">
        <div class="mb-6 flex flex-wrap items-center justify-between">
          <div class="text-gray-600">
            共 <span class="font-semibold text-primary">{{ filteredProducts.length }}</span> 件商品
          </div>
          
          <div class="flex items-center space-x-4 mt-4 md:mt-0">
            <span class="text-gray-500 text-sm">排序:</span>
            <select 
              v-model="sortBy"
              class="border rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-primary"
            >
              <option value="default">默认排序</option>
              <option value="price-asc">价格从低到高</option>
              <option value="price-desc">价格从高到低</option>
              <option value="sales">销量优先</option>
              <option value="rating">评分优先</option>
            </select>
          </div>
        </div>

        <div v-if="filteredProducts.length > 0" class="grid grid-cols-2 md:grid-cols-3 gap-6">
          <ProductCard 
            v-for="product in sortedProducts" 
            :key="product.id" 
            :product="product" 
          />
        </div>

        <div v-else class="text-center py-16">
          <span class="text-6xl mb-4 block">🔍</span>
          <p class="text-gray-500 text-lg">没有找到符合条件的商品</p>
          <button 
            @click="resetFilters"
            class="mt-4 text-primary hover:underline"
          >
            清除筛选条件
          </button>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
useHead({
  title: '全部商品'
})

const config = useRuntimeConfig()

const categories = ref([])
const allProducts = ref([])
const selectedCategory = ref(null)
const priceRange = ref(null)
const sortBy = ref('default')

const filteredProducts = computed(() => {
  let result = [...allProducts.value]
  
  if (selectedCategory.value !== null) {
    result = result.filter(p => p.categoryId === selectedCategory.value)
  }
  
  if (priceRange.value) {
    result = result.filter(p => {
      if (priceRange.value?.max === null) {
        return p.price >= priceRange.value.min
      }
      return p.price >= priceRange.value.min && p.price <= priceRange.value.max
    })
  }
  
  return result
})

const sortedProducts = computed(() => {
  const result = [...filteredProducts.value]
  
  switch (sortBy.value) {
    case 'price-asc':
      result.sort((a, b) => a.price - b.price)
      break
    case 'price-desc':
      result.sort((a, b) => b.price - a.price)
      break
    case 'sales':
      result.sort((a, b) => b.sales - a.sales)
      break
    case 'rating':
      result.sort((a, b) => b.rating - a.rating)
      break
    default:
      break
  }
  
  return result
})

const resetFilters = () => {
  selectedCategory.value = null
  priceRange.value = null
  sortBy.value = 'default'
}

onMounted(async () => {
  try {
    const [categoriesRes, productsRes] = await Promise.all([
      $fetch(`${config.public.apiBase}/categories`),
      $fetch(`${config.public.apiBase}/products`)
    ])
    
    if (categoriesRes.code === 200) {
      categories.value = categoriesRes.data
    }
    
    if (productsRes.code === 200) {
      allProducts.value = productsRes.data
    }
  } catch (error) {
    console.error('获取数据失败:', error)
  }
})
</script>
