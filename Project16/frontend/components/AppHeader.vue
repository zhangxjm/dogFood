<script setup lang="ts">
const authStore = useAuthStore()
const route = useRoute()
const mobileMenuOpen = ref(false)
const userMenuOpen = ref(false)

const isActive = (path: string) => route.path === path

const navLinks = [
  { label: '首页', path: '/' },
  { label: '作品', path: '/works' },
  { label: '设计师', path: '/designers' }
]
</script>

<template>
  <header class="bg-white border-b border-gray-100 sticky top-0 z-50">
    <nav class="container-custom">
      <div class="flex items-center justify-between h-16">
        <NuxtLink to="/" class="flex items-center space-x-2">
          <div class="w-8 h-8 bg-gradient-to-br from-primary-500 to-accent-500 rounded-lg flex items-center justify-center">
            <span class="text-white font-bold text-lg">A</span>
          </div>
          <span class="font-bold text-xl text-gray-900 hidden sm:block">文创作品平台</span>
        </NuxtLink>

        <div class="hidden md:flex items-center space-x-8">
          <NuxtLink
            v-for="link in navLinks"
            :key="link.path"
            :to="link.path"
            :class="[
              'text-sm font-medium transition-colors duration-200',
              isActive(link.path)
                ? 'text-primary-600'
                : 'text-gray-600 hover:text-gray-900'
            ]"
          >
            {{ link.label }}
          </NuxtLink>
        </div>

        <div class="flex items-center space-x-4">
          <div class="hidden sm:block">
            <form class="relative" @submit.prevent>
              <input
                type="text"
                placeholder="搜索作品..."
                class="w-64 pl-10 pr-4 py-2 text-sm bg-gray-50 border-0 rounded-lg focus:ring-2 focus:ring-primary-500"
              />
              <svg
                class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"
                />
              </svg>
            </form>
          </div>

          <div class="flex items-center space-x-2">
            <template v-if="authStore.isAuthenticated">
              <NuxtLink
                v-if="authStore.isDesigner"
                to="/works/upload"
                class="btn-primary text-xs px-4 py-2"
              >
                上传作品
              </NuxtLink>

              <div class="relative">
                <button
                  @click="userMenuOpen = !userMenuOpen"
                  class="flex items-center space-x-2 focus:outline-none"
                >
                  <div
                    class="w-8 h-8 bg-gray-200 rounded-full flex items-center justify-center overflow-hidden"
                  >
                    <img
                      v-if="authStore.user?.avatar"
                      :src="authStore.user.avatar"
                      :alt="authStore.user.username"
                      class="w-full h-full object-cover"
                    />
                    <span v-else class="text-sm font-medium text-gray-600">
                      {{ authStore.user?.username?.[0]?.toUpperCase() }}
                    </span>
                  </div>
                  <span class="hidden md:block text-sm text-gray-700">
                    {{ authStore.user?.username }}
                  </span>
                </button>

                <div
                  v-if="userMenuOpen"
                  class="absolute right-0 mt-2 w-48 bg-white rounded-lg shadow-lg border border-gray-100 py-1"
                >
                  <NuxtLink
                    :to="`/users/${authStore.user?.id}`"
                    class="block w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-50"
                    @click="userMenuOpen = false"
                  >
                    个人主页
                  </NuxtLink>
                  <NuxtLink
                    to="/favorites"
                    class="block w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-50"
                    @click="userMenuOpen = false"
                  >
                    我的收藏
                  </NuxtLink>
                  <NuxtLink
                    to="/orders"
                    class="block w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-50"
                    @click="userMenuOpen = false"
                  >
                    我的订单
                  </NuxtLink>
                  <hr class="my-1" />
                  <button
                    @click="authStore.logout"
                    class="block w-full text-left px-4 py-2 text-sm text-red-600 hover:bg-gray-50"
                  >
                    退出登录
                  </button>
                </div>
              </div>
            </template>

            <template v-else>
              <NuxtLink
                to="/login"
                class="btn-outline text-xs px-4 py-2"
              >
                登录
              </NuxtLink>
              <NuxtLink
                to="/register"
                class="btn-primary text-xs px-4 py-2"
              >
                注册
              </NuxtLink>
            </template>

            <button
              @click="mobileMenuOpen = !mobileMenuOpen"
              class="md:hidden p-2 rounded-lg hover:bg-gray-100"
            >
              <svg
                v-if="!mobileMenuOpen"
                class="w-6 h-6"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M4 6h16M4 12h16M4 18h16"
                />
              </svg>
              <svg
                v-else
                class="w-6 h-6"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M6 18L18 6M6 6l12 12"
                />
              </svg>
            </button>
          </div>
        </div>
      </div>

      <div
        v-if="mobileMenuOpen"
        class="md:hidden py-4 border-t border-gray-100"
      >
        <div class="space-y-2">
          <NuxtLink
            v-for="link in navLinks"
            :key="link.path"
            :to="link.path"
            :class="[
              'block px-3 py-2 rounded-lg text-sm font-medium',
              isActive(link.path)
                ? 'bg-primary-50 text-primary-600'
                : 'text-gray-600 hover:bg-gray-50'
            ]"
            @click="mobileMenuOpen = false"
          >
            {{ link.label }}
          </NuxtLink>
        </div>
      </div>
    </nav>
  </header>
</template>
