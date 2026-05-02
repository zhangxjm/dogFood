<template>
  <div class="container-custom py-8">
    <div class="mb-8">
      <h1 class="text-3xl font-bold text-gray-800 mb-2">商品分类</h1>
      <p class="text-gray-500">选择您感兴趣的商品分类</p>
    </div>

    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
      <NuxtLink 
        v-for="category in categories" 
        :key="category.id"
        :to="`/categories/${category.id}`"
        class="card group hover:shadow-xl transition-all duration-300"
      >
        <div class="p-6">
          <div class="flex items-center mb-4">
            <span class="text-5xl mr-4">{{ category.icon }}</span>
            <div>
              <h2 class="text-xl font-bold text-gray-800 group-hover:text-primary transition-colors">
                {{ category.name }}
              </h2>
              <p class="text-sm text-gray-500">{{ category.description }}</p>
            </div>
          </div>
          
          <div class="flex items-center justify-between pt-4 border-t">
            <span class="text-sm text-gray-500">
              共 {{ getProductCount(category.id) }} 件商品
            </span>
            <span class="text-primary group-hover:translate-x-1 transition-transform">
              →
            </span>
          </div>
        </div>
      </NuxtLink>
    </div>

    <div v-if="categories.length === 0" class="text-center py-16">
      <span class="text-6xl mb-4 block">📦</span>
      <p class="text-gray-500">暂无分类数据</p>
    </div>
  </div>
</template>

<script setup>
useHead({
  title: '商品分类'
})

const config = useRuntimeConfig()

const categories = ref([])
const products = ref([])

const getProductCount = (categoryId) => {
  return products.value.filter(p => p.categoryId === categoryId).length
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
      products.value = productsRes.data
    }
  } catch (error) {
    console.error('获取数据失败:', error)
  }
})
</script>
