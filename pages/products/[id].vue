<template>
  <div class="container-custom py-8">
    <nav class="text-sm text-gray-500 mb-6">
      <NuxtLink to="/" class="hover:text-primary">首页</NuxtLink>
      <span class="mx-2">/</span>
      <NuxtLink to="/products" class="hover:text-primary">全部商品</NuxtLink>
      <span class="mx-2">/</span>
      <span class="text-gray-800">{{ product?.name || '加载中...' }}</span>
    </nav>

    <div v-if="product" class="card p-6 mb-8">
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-8">
        <div>
          <div class="mb-4">
            <img 
              :src="currentImage" 
              :alt="product.name"
              class="w-full h-96 object-contain bg-gray-50 rounded-lg"
            />
          </div>
          
          <div class="flex space-x-2">
            <button 
              v-for="(img, index) in product.images" 
              :key="index"
              @click="currentImage = img"
              :class="[
                'w-20 h-20 rounded-lg overflow-hidden border-2 transition-colors',
                currentImage === img ? 'border-primary' : 'border-gray-200 hover:border-gray-400'
              ]"
            >
              <img :src="img" :alt="`${product.name} ${index + 1}`" class="w-full h-full object-cover" />
            </button>
          </div>
        </div>

        <div>
          <div class="flex items-center space-x-2 mb-4">
            <span v-if="product.isHot" class="bg-red-500 text-white text-xs px-2 py-1 rounded">热卖</span>
            <span v-if="product.isNew" class="bg-green-500 text-white text-xs px-2 py-1 rounded">新品</span>
          </div>

          <h1 class="text-2xl font-bold text-gray-800 mb-4">{{ product.name }}</h1>

          <div class="flex items-center mb-4">
            <div class="flex items-center text-yellow-400 mr-4">
              <svg v-for="i in 5" :key="i" class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
                <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
              </svg>
              <span class="text-gray-700 ml-1">{{ product.rating }}</span>
            </div>
            <span class="text-gray-400 text-sm">{{ product.reviews }} 条评价</span>
            <span class="text-gray-400 text-sm ml-4">已售 {{ product.sales }}</span>
          </div>

          <div class="bg-gray-50 p-4 rounded-lg mb-6">
            <div class="flex items-end">
              <span class="text-primary text-3xl font-bold">¥{{ product.price }}</span>
              <span 
                v-if="product.originalPrice > product.price" 
                class="text-gray-400 text-lg line-through ml-3"
              >
                ¥{{ product.originalPrice }}
              </span>
              <span 
                v-if="product.originalPrice > product.price" 
                class="bg-red-100 text-red-600 text-sm px-2 py-1 rounded ml-3"
              >
                省 ¥{{ product.originalPrice - product.price }}
              </span>
            </div>
          </div>

          <div class="space-y-3 mb-6">
            <div class="flex items-center">
              <span class="text-gray-500 w-20">库存:</span>
              <span class="text-gray-800">{{ product.stock }} 件</span>
            </div>
          </div>

          <div class="flex items-center space-x-4 mb-6">
            <span class="text-gray-500">数量:</span>
            <div class="flex items-center border rounded-lg">
              <button 
                @click="quantity = Math.max(1, quantity - 1)"
                class="px-4 py-2 text-gray-600 hover:bg-gray-100 transition-colors"
              >
                -
              </button>
              <span class="px-4 py-2 text-gray-800 font-medium">{{ quantity }}</span>
              <button 
                @click="quantity = Math.min(product.stock, quantity + 1)"
                class="px-4 py-2 text-gray-600 hover:bg-gray-100 transition-colors"
              >
                +
              </button>
            </div>
          </div>

          <div class="flex space-x-4">
            <button 
              @click="handleAddToCart"
              :disabled="isAdding"
              class="flex-1 bg-primary text-white py-3 px-6 rounded-lg font-medium hover:bg-orange-600 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {{ isAdding ? '添加中...' : '加入购物车' }}
            </button>
            <NuxtLink 
              to="/cart"
              class="flex-1 bg-gray-100 text-gray-700 py-3 px-6 rounded-lg font-medium hover:bg-gray-200 transition-colors text-center"
            >
              去购物车
            </NuxtLink>
          </div>

          <div v-if="addMessage" class="mt-4 p-3 rounded-lg" :class="addSuccess ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'">
            {{ addMessage }}
          </div>
        </div>
      </div>
    </div>

    <div v-if="product" class="card p-6">
      <h2 class="text-xl font-bold text-gray-800 mb-6 pb-3 border-b">商品详情</h2>
      
      <div class="mb-8">
        <h3 class="text-lg font-semibold text-gray-700 mb-4">商品描述</h3>
        <p class="text-gray-600 leading-relaxed">{{ product.description }}</p>
      </div>

      <div>
        <h3 class="text-lg font-semibold text-gray-700 mb-4">商品规格</h3>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div 
            v-for="(value, key) in product.specs" 
            :key="key"
            class="flex items-center p-3 bg-gray-50 rounded-lg"
          >
            <span class="text-gray-500 w-24">{{ key }}:</span>
            <span class="text-gray-800">{{ value }}</span>
          </div>
        </div>
      </div>
    </div>

    <div v-else class="text-center py-16">
      <span class="text-6xl mb-4 block">🔍</span>
      <p class="text-gray-500 text-lg">商品不存在</p>
      <NuxtLink to="/products" class="inline-block mt-4 text-primary hover:underline">
        返回商品列表 →
      </NuxtLink>
    </div>
  </div>
</template>

<script setup>
const route = useRoute()
const config = useRuntimeConfig()
const { addToCart } = useCart()

const product = ref(null)
const currentImage = ref('')
const quantity = ref(1)
const isAdding = ref(false)
const addMessage = ref('')
const addSuccess = ref(false)

useHead({
  title: computed(() => `${product.value?.name || '商品详情'} - 电商商城`)
})

const handleAddToCart = async () => {
  if (!product.value) return
  
  isAdding.value = true
  addMessage.value = ''
  
  const result = await addToCart(product.value.id, quantity.value)
  
  addSuccess.value = result.success
  addMessage.value = result.message
  isAdding.value = false
  
  setTimeout(() => {
    addMessage.value = ''
  }, 3000)
}

onMounted(async () => {
  const productId = parseInt(route.params.id)
  
  try {
    const response = await $fetch(`${config.public.apiBase}/products/${productId}`)
    
    if (response.code === 200) {
      product.value = response.data
      currentImage.value = response.data.image
    }
  } catch (error) {
    console.error('获取商品详情失败:', error)
  }
})
</script>
