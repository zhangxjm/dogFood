<script setup lang="ts">
useHead({
  titleTemplate: '%s - 文创作品平台',
  htmlAttrs: {
    lang: 'zh-CN'
  }
})

const authStore = useAuthStore()

onMounted(() => {
  authStore.initializeAuth()
})
</script>

<template>
  <div class="min-h-screen flex flex-col">
    <AppHeader />
    <main class="flex-1">
      <NuxtPage />
    </main>
    <AppFooter />
  </div>
</template>
