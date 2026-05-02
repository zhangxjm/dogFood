<template>
  <div>
    <div class="bg-gradient-to-r from-primary to-orange-500 text-white py-16">
      <div class="container-custom">
        <div class="text-center">
          <h1 class="text-4xl md:text-5xl font-bold mb-4">欢迎来到电商商城</h1>
          <p class="text-xl md:text-2xl mb-8 opacity-90">精选全球好物，品质生活从此开始</p>
          <NuxtLink 
            to="/products" 
            class="inline-block bg-white text-primary px-8 py-3 rounded-full font-semibold hover:bg-gray-100 transition-colors"
          >
            立即选购
          </NuxtLink>
        </div>
      </div>
    </div>

    <div class="container-custom py-12">
      <h2 class="text-2xl font-bold text-gray-800 mb-8">商品分类</h2>
      <div class="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-8 gap-4">
        <NuxtLink 
          v-for="category in categories" 
          :key="category.id"
          :to="`/categories/${category.id}`"
          class="flex flex-col items-center p-4 bg-white rounded-lg shadow hover:shadow-md transition-shadow"
        >
          <span class="text-4xl mb-2">{{ category.icon }}</span>
          <span class="text-sm text-gray-700 font-medium">{{ category.name }}</span>
        </NuxtLink>
      </div>
    </div>

    <div class="bg-gray-100 py-12">
      <div class="container-custom">
        <div class="flex items-center justify-between mb-8">
          <h2 class="text-2xl font-bold text-gray-800">
            <span class="inline-flex items-center">
              <span class="text-red-500 mr-2">🔥</span>
              热卖推荐
            </span>
          </h2>
          <NuxtLink to="/products" class="text-primary hover:underline">查看更多 →</NuxtLink>
        </div>
        
        <div class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
          <ProductCard 
            v-for="product in hotProducts" 
            :key="product.id" 
            :product="product" 
          />
        </div>
      </div>
    </div>

    <div class="container-custom py-12">
      <div class="flex items-center justify-between mb-8">
        <h2 class="text-2xl font-bold text-gray-800">
          <span class="inline-flex items-center">
            <span class="text-green-500 mr-2">✨</span>
            新品上市
          </span>
        </h2>
        <NuxtLink to="/products" class="text-primary hover:underline">查看更多 →</NuxtLink>
      </div>
      
      <div class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
        <ProductCard 
          v-for="product in newProducts" 
          :key="product.id" 
          :product="product" 
        />
      </div>
    </div>

    <div class="bg-gray-100 py-12">
      <div class="container-custom">
        <div class="grid grid-cols-1 md:grid-cols-4 gap-8">
          <div class="flex items-center p-6 bg-white rounded-lg shadow">
            <span class="text-4xl mr-4">🚚</span>
            <div>
              <h3 class="font-semibold text-gray-800">免费配送</h3>
              <p class="text-sm text-gray-500">满99元免邮</p>
            </div>
          </div>
          <div class="flex items-center p-6 bg-white rounded-lg shadow">
            <span class="text-4xl mr-4">🔄</span>
            <div>
              <h3 class="font-semibold text-gray-800">7天无理由</h3>
              <p class="text-sm text-gray-500">放心退换</p>
            </div>
          </div>
          <div class="flex items-center p-6 bg-white rounded-lg shadow">
            <span class="text-4xl mr-4">✅</span>
            <div>
              <h3 class="font-semibold text-gray-800">正品保障</h3>
              <p class="text-sm text-gray-500">100%正品</p>
            </div>
          </div>
          <div class="flex items-center p-6 bg-white rounded-lg shadow">
            <span class="text-4xl mr-4">💬</span>
            <div>
              <h3 class="font-semibold text-gray-800">客服支持</h3>
              <p class="text-sm text-gray-500">24小时在线</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
useHead({
  title: '首页'
})

const config = useRuntimeConfig()
const { fetchCart } = useCart()

const categories = ref([])
const hotProducts = ref([])
const newProducts = ref([])

onMounted(async () => {
  await fetchCart()
  
  try {
    const [categoriesRes, productsRes] = await Promise.all([
      $fetch(`${config.public.apiBase}/categories`),
      $fetch(`${config.public.apiBase}/products`)
    ])
    
    if (categoriesRes.code === 200) {
      categories.value = categoriesRes.data
    }
    
    if (productsRes.code === 200) {
      const allProducts = productsRes.data
      hotProducts.value = allProducts.filter(p => p.isHot).slice(0, 8)
      newProducts.value = allProducts.filter(p => p.isNew).slice(0, 8)
    }
  } catch (error) {
    console.error('获取数据失败:', error)
  }
})
</script>
