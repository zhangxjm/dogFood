<template>
  <footer class="bg-gray-800 text-white mt-8">
    <div class="container-custom py-8">
      <div class="grid grid-cols-1 md:grid-cols-4 gap-8">
        <div>
          <h3 class="text-lg font-semibold mb-4 flex items-center">
            <span class="text-2xl mr-2">🛒</span>
            电商商城
          </h3>
          <p class="text-gray-400 text-sm">
            为您提供优质的购物体验，精选全球好物，让购物更简单。
          </p>
        </div>

        <div>
          <h4 class="text-md font-semibold mb-4">快速链接</h4>
          <ul class="space-y-2">
            <li><NuxtLink to="/" class="text-gray-400 hover:text-white transition-colors text-sm">首页</NuxtLink></li>
            <li><NuxtLink to="/categories" class="text-gray-400 hover:text-white transition-colors text-sm">商品分类</NuxtLink></li>
            <li><NuxtLink to="/products" class="text-gray-400 hover:text-white transition-colors text-sm">全部商品</NuxtLink></li>
            <li><NuxtLink to="/cart" class="text-gray-400 hover:text-white transition-colors text-sm">购物车</NuxtLink></li>
          </ul>
        </div>

        <div>
          <h4 class="text-md font-semibold mb-4">客户服务</h4>
          <ul class="space-y-2">
            <li><span class="text-gray-400 text-sm">帮助中心</span></li>
            <li><span class="text-gray-400 text-sm">退换货政策</span></li>
            <li><span class="text-gray-400 text-sm">配送说明</span></li>
            <li><span class="text-gray-400 text-sm">支付方式</span></li>
          </ul>
        </div>

        <div>
          <h4 class="text-md font-semibold mb-4">联系我们</h4>
          <ul class="space-y-2 text-gray-400 text-sm">
            <li class="flex items-center">
              <svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z" />
              </svg>
              400-123-4567
            </li>
            <li class="flex items-center">
              <svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
              </svg>
              service@shop.com
            </li>
            <li class="flex items-center">
              <svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              工作时间: 9:00-21:00
            </li>
          </ul>
        </div>
      </div>

      <div class="border-t border-gray-700 mt-8 pt-8 text-center text-gray-400 text-sm">
        <p>&copy; 2024 电商商城. 保留所有权利. | 技术栈: Nuxt3 + Vue3 + TailwindCSS + Node.js</p>
      </div>
    </div>
  </footer>
</template>
