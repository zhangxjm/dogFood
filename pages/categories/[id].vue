<template>
  <div class="container-custom py-8">
    <div class="mb-8">
      <nav class="text-sm text-gray-500 mb-4">
        <NuxtLink to="/" class="hover:text-primary">首页</NuxtLink>
        <span class="mx-2">/</span>
        <NuxtLink to="/categories" class="hover:text-primary">商品分类</NuxtLink>
        <span class="mx-2">/</span>
        <span class="text-gray-800">{{ currentCategory?.name || '加载中...' }}</span>
      </nav>
      
      <div class="flex items-center">
        <span v-if="currentCategory" class="text-4xl mr-4">{{ currentCategory.icon }}</span>
        <div>
          <h1 class="text-3xl font-bold text-gray-800">{{ currentCategory?.name || '加载中...' }}</h1>
          <p class="text-gray-500 mt-1">{{ currentCategory?.description }}</p>
        </div>
      </div>
    </div>

    <div class="mb-6 flex flex-wrap items-center justify-between">
      <div class="text-gray-600">
        共 <span class="font-semibold text-primary">{{ products.length }}</span> 件商品
      </div>
      
      <div class="flex items-center space-x-4 mt-4 md:mt-0">
        <span class="text-gray-500 text-sm">排序:</span>
        <select 
          v-model="sortBy"
          class="border rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-primary"
          @change="sortProducts"
        >
          <option value="default">默认排序</option>
          <option value="price-asc">价格从低到高</option>
          <option value="price-desc">价格从高到低</option>
          <option value="sales">销量优先</option>
          <option value="rating">评分优先</option>
        </select>
      </div>
    </div>

    <div v-if="products.length > 0" class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
      <ProductCard 
        v-for="product in sortedProducts" 
        :key="product.id" 
        :product="product" 
      />
    </div>

    <div v-else class="text-center py-16">
      <span class="text-6xl mb-4 block">📭</span>
      <p class="text-gray-500 text-lg">该分类暂无商品</p>
      <NuxtLink to="/products" class="inline-block mt-4 text-primary hover:underline">
        查看全部商品 →
      </NuxtLink>
    </div>
  </div>
</template>

<script setup>
const route = useRoute()
const config = useRuntimeConfig()

const currentCategory = ref(null)
const products = ref([])
const sortBy = ref('default')
const sortedProducts = ref([])

useHead({
  title: computed(() => `${currentCategory.value?.name || '商品分类'} - 电商商城`)
})

const sortProducts = () => {
  const productsCopy = [...products.value]
  
  switch (sortBy.value) {
    case 'price-asc':
      productsCopy.sort((a, b) => a.price - b.price)
      break
    case 'price-desc':
      productsCopy.sort((a, b) => b.price - a.price)
      break
    case 'sales':
      productsCopy.sort((a, b) => b.sales - a.sales)
      break
    case 'rating':
      productsCopy.sort((a, b) => b.rating - a.rating)
      break
    default:
      break
  }
  
  sortedProducts.value = productsCopy
}

onMounted(async () => {
  const categoryId = parseInt(route.params.id)
  
  try {
    const [categoriesRes, productsRes] = await Promise.all([
      $fetch(`${config.public.apiBase}/categories`),
      $fetch(`${config.public.apiBase}/products/category/${categoryId}`)
    ])
    
    if (categoriesRes.code === 200) {
      currentCategory.value = categoriesRes.data.find(c => c.id === categoryId)
    }
    
    if (productsRes.code === 200) {
      products.value = productsRes.data
      sortedProducts.value = [...productsRes.data]
    }
  } catch (error) {
    console.error('获取数据失败:', error)
  }
})
</script>
