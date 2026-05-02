<template>
  <header class="bg-white shadow-md sticky top-0 z-50">
    <div class="container-custom">
      <div class="flex items-center justify-between h-16">
        <NuxtLink to="/" class="flex items-center space-x-2">
          <span class="text-2xl">🛒</span>
          <span class="text-xl font-bold text-primary hidden sm:block">电商商城</span>
        </NuxtLink>

        <nav class="hidden md:flex items-center space-x-6">
          <NuxtLink to="/" class="text-gray-700 hover:text-primary transition-colors">首页</NuxtLink>
          <NuxtLink to="/categories" class="text-gray-700 hover:text-primary transition-colors">分类</NuxtLink>
          <NuxtLink to="/products" class="text-gray-700 hover:text-primary transition-colors">商品</NuxtLink>
        </nav>

        <div class="flex items-center space-x-4">
          <button 
            @click="toggleMobileMenu"
            class="md:hidden p-2 text-gray-700 hover:text-primary"
          >
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16" />
            </svg>
          </button>

          <NuxtLink to="/cart" class="relative p-2 text-gray-700 hover:text-primary transition-colors">
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z" />
            </svg>
            <span 
              v-if="cartCount > 0" 
              class="absolute -top-1 -right-1 bg-primary text-white text-xs w-5 h-5 flex items-center justify-center rounded-full"
            >
              {{ cartCount }}
            </span>
          </NuxtLink>
        </div>
      </div>

      <div v-if="showMobileMenu" class="md:hidden pb-4 border-t">
        <nav class="flex flex-col space-y-3 pt-4">
          <NuxtLink 
            to="/" 
            @click="showMobileMenu = false"
            class="text-gray-700 hover:text-primary transition-colors px-2 py-1"
          >
            首页
          </NuxtLink>
          <NuxtLink 
            to="/categories" 
            @click="showMobileMenu = false"
            class="text-gray-700 hover:text-primary transition-colors px-2 py-1"
          >
            分类
          </NuxtLink>
          <NuxtLink 
            to="/products" 
            @click="showMobileMenu = false"
            class="text-gray-700 hover:text-primary transition-colors px-2 py-1"
          >
            商品
          </NuxtLink>
          <NuxtLink 
            to="/cart" 
            @click="showMobileMenu = false"
            class="text-gray-700 hover:text-primary transition-colors px-2 py-1"
          >
            购物车
          </NuxtLink>
        </nav>
      </div>
    </div>
  </header>
</template>

<script setup>
const { cartCount } = useCart()
const showMobileMenu = ref(false)

const toggleMobileMenu = () => {
  showMobileMenu.value = !showMobileMenu.value
}
</script>
