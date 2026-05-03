<script setup lang="ts">
import type { NuxtError } from '#app'

defineProps<{
  error: NuxtError
}>()

const handleClearError = () => clearError({ redirect: '/' })
</script>

<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-50">
    <div class="text-center p-8">
      <div class="text-6xl md:text-8xl font-bold text-gray-200 mb-4">
        {{ error.statusCode || 'Oops' }}
      </div>
      <h1 class="text-2xl md:text-3xl font-bold text-gray-900 mb-4">
        {{ error.statusMessage || '出错了' }}
      </h1>
      <p class="text-gray-600 mb-8 max-w-md mx-auto">
        {{ error.message || '抱歉，发生了意外错误。请稍后再试。' }}
      </p>
      <button
        @click="handleClearError"
        class="btn-primary"
      >
        返回首页
      </button>
    </div>
  </div>
</template>
