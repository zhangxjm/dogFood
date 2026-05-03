<script setup lang="ts">
import type { Work } from '~/types'

interface Props {
  work: Work
}

const props = defineProps<Props>()

const imageUrl = computed(() => {
  if (props.work.images && props.work.images.length > 0) {
    const config = useRuntimeConfig()
    const firstImage = props.work.images[0]
    if (firstImage.startsWith('http')) {
      return firstImage
    }
    return `${config.public.apiBase}${firstImage}`
  }
  return 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=abstract%20art%20creative%20design&image_size=square'
})
</script>

<template>
  <NuxtLink
    :to="`/works/${work.id}`"
    class="card card-hover group"
  >
    <div class="relative aspect-video bg-gray-100 overflow-hidden">
      <img
        :src="imageUrl"
        :alt="work.title"
        class="w-full h-full object-cover transition-transform duration-300 group-hover:scale-105"
        loading="lazy"
      />
      <div class="absolute top-3 left-3">
        <span class="badge badge-primary">
          {{ work.category }}
        </span>
      </div>
      <div
        v-if="work.price > 0"
        class="absolute bottom-3 right-3 bg-black/70 text-white px-3 py-1 rounded-full text-sm font-medium"
      >
        ¥{{ work.price }}
      </div>
    </div>
    <div class="p-4">
      <h3 class="font-semibold text-gray-900 mb-2 line-clamp-1 group-hover:text-primary-600 transition-colors">
        {{ work.title }}
      </h3>
      <p class="text-sm text-gray-500 mb-3 line-clamp-2">
        {{ work.description }}
      </p>
      <div class="flex items-center justify-between">
        <div class="flex items-center space-x-2">
          <div
            class="w-6 h-6 bg-gray-200 rounded-full flex items-center justify-center overflow-hidden"
          >
            <span class="text-xs font-medium text-gray-600">
              {{ work.designer_name?.[0]?.toUpperCase() }}
            </span>
          </div>
          <span class="text-sm text-gray-600">{{ work.designer_name }}</span>
        </div>
        <div class="flex items-center space-x-3 text-xs text-gray-400">
          <span class="flex items-center space-x-1">
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"
              />
            </svg>
            <span>{{ work.likes_count }}</span>
          </span>
          <span class="flex items-center space-x-1">
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"
              />
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"
              />
            </svg>
            <span>{{ work.views_count }}</span>
          </span>
        </div>
      </div>
    </div>
  </NuxtLink>
</template>
