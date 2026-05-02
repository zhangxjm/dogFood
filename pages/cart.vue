<template>
  <div class="container-custom py-8">
    <h1 class="text-3xl font-bold text-gray-800 mb-8">购物车</h1>

    <div v-if="cartItems.length > 0" class="grid grid-cols-1 lg:grid-cols-3 gap-8">
      <div class="lg:col-span-2">
        <div class="card">
          <div class="p-4 border-b bg-gray-50">
            <div class="flex items-center justify-between">
              <span class="font-semibold text-gray-700">商品列表 ({{ cartItems.length }})</span>
              <button 
                @click="handleClearCart"
                class="text-red-500 hover:text-red-600 text-sm"
              >
                清空购物车
              </button>
            </div>
          </div>

          <div class="divide-y">
            <div 
              v-for="item in cartItems" 
              :key="item.id"
              class="p-4 flex items-center space-x-4 hover:bg-gray-50 transition-colors"
            >
              <NuxtLink 
                :to="'/products/' + item.productId"
                class="flex-shrink-0"
              >
                <img 
                  :src="item.product.image" 
                  :alt="item.product.name"
                  class="w-20 h-20 object-cover rounded-lg"
                />
              </NuxtLink>

              <div class="flex-1 min-w-0">
                <NuxtLink 
                  :to="'/products/' + item.productId"
                  class="font-medium text-gray-800 hover:text-primary line-clamp-2"
                >
                  {{ item.product.name }}
                </NuxtLink>
                <p class="text-primary font-semibold mt-1">¥{{ item.product.price }}</p>
              </div>

              <div class="flex items-center space-x-2">
                <button 
                  @click="handleDecreaseQuantity(item)"
                  :disabled="item.quantity <= 1"
                  class="w-8 h-8 flex items-center justify-center border rounded hover:bg-gray-100 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  -
                </button>
                <span class="w-10 text-center font-medium">{{ item.quantity }}</span>
                <button 
                  @click="handleIncreaseQuantity(item)"
                  class="w-8 h-8 flex items-center justify-center border rounded hover:bg-gray-100 transition-colors"
                >
                  +
                </button>
              </div>

              <div class="text-right">
                <p class="font-semibold text-gray-800">¥{{ getItemSubtotal(item) }}</p>
                <button 
                  @click="handleRemoveItem(item.id)"
                  class="text-red-500 hover:text-red-600 text-sm mt-1"
                >
                  删除
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="lg:col-span-1">
        <div class="card sticky top-24">
          <div class="p-4 border-b bg-gray-50">
            <span class="font-semibold text-gray-700">订单摘要</span>
          </div>

          <div class="p-4 space-y-4">
            <div class="flex justify-between text-gray-600">
              <span>商品总价</span>
              <span>¥{{ formattedTotal }}</span>
            </div>
            <div class="flex justify-between text-gray-600">
              <span>运费</span>
              <span class="text-green-500">
                {{ shippingText }}
              </span>
            </div>
            <div class="flex justify-between text-gray-600">
              <span>优惠</span>
              <span class="text-green-500">-¥0.00</span>
            </div>

            <div class="border-t pt-4">
              <div class="flex justify-between items-center">
                <span class="text-lg font-semibold text-gray-800">应付总额</span>
                <span class="text-2xl font-bold text-primary">
                  ¥{{ formattedGrandTotal }}
                </span>
              </div>
              <p v-if="!isFreeShipping" class="text-sm text-gray-500 mt-2">
                再购 ¥{{ formattedRemaining }} 即可免运费
              </p>
            </div>

            <button class="w-full bg-primary text-white py-3 rounded-lg font-medium hover:bg-orange-600 transition-colors">
              去结算 ({{ cartCount }} 件)
            </button>

            <NuxtLink 
              to="/products"
              class="block w-full text-center text-primary hover:underline py-2"
            >
              继续购物 →
            </NuxtLink>
          </div>
        </div>
      </div>
    </div>

    <div v-else class="text-center py-16">
      <span class="text-6xl mb-4 block">🛒</span>
      <h2 class="text-2xl font-semibold text-gray-800 mb-2">购物车是空的</h2>
      <p class="text-gray-500 mb-6">快去挑选心仪的商品吧！</p>
      <NuxtLink 
        to="/products"
        class="inline-block bg-primary text-white px-8 py-3 rounded-lg font-medium hover:bg-orange-600 transition-colors"
      >
        去购物
      </NuxtLink>
    </div>
  </div>
</template>

<script setup>
useHead({
  title: '购物车'
})

const { cart, cartCount, fetchCart, removeFromCart, updateQuantity } = useCart()

const cartItems = computed(() => {
  return cart.value.items
})

const formattedTotal = computed(() => {
  return cart.value.total.toFixed(2)
})

const isFreeShipping = computed(() => {
  return cart.value.total >= 99
})

const shippingText = computed(() => {
  return isFreeShipping.value ? '免运费' : '¥10.00'
})

const grandTotal = computed(() => {
  return cart.value.total + (isFreeShipping.value ? 0 : 10)
})

const formattedGrandTotal = computed(() => {
  return grandTotal.value.toFixed(2)
})

const remainingForFreeShipping = computed(() => {
  return Math.max(0, 99 - cart.value.total)
})

const formattedRemaining = computed(() => {
  return remainingForFreeShipping.value.toFixed(2)
})

const getItemSubtotal = (item) => {
  return (item.product.price * item.quantity).toFixed(2)
}

const handleDecreaseQuantity = async (item) => {
  if (item.quantity <= 1) {
    return handleRemoveItem(item.id)
  }
  await updateQuantity(item.id, item.quantity - 1)
}

const handleIncreaseQuantity = async (item) => {
  await updateQuantity(item.id, item.quantity + 1)
}

const handleRemoveItem = async (itemId) => {
  if (confirm('确定要删除这件商品吗？')) {
    await removeFromCart(itemId)
  }
}

const handleClearCart = async () => {
  if (confirm('确定要清空购物车吗？')) {
    for (const item of cart.value.items) {
      await removeFromCart(item.id)
    }
  }
}

onMounted(async () => {
  await fetchCart()
})
</script>
