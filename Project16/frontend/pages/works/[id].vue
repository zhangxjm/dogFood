<script setup lang="ts">
import type { Work, Comment } from '~/types'

const config = useRuntimeConfig()
const route = useRoute()
const authStore = useAuthStore()

const work = ref<Work | null>(null)
const comments = ref<Comment[]>([])
const loading = ref(true)
const isLiked = ref(false)
const isFavorited = ref(false)
const commentContent = ref('')
const submittingComment = ref(false)

const workId = computed(() => route.params.id as string)

const imageUrl = computed(() => {
  if (work.value?.images && work.value.images.length > 0) {
    const firstImage = work.value.images[0]
    if (firstImage.startsWith('http')) {
      return firstImage
    }
    return `${config.public.apiBase}${firstImage}`
  }
  return 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=abstract%20art%20creative%20design&image_size=square'
})

useHead({
  title: computed(() => work.value?.title || '作品详情'),
  meta: computed(() => [
    { name: 'description', content: work.value?.description || '' },
    { property: 'og:title', content: work.value?.title || '' },
    { property: 'og:description', content: work.value?.description || '' },
    { property: 'og:image', content: imageUrl.value }
  ])
})

const fetchWork = async () => {
  loading.value = true
  try {
    const { data } = await useFetch<Work>(
      `${config.public.apiBase}/api/works/${workId.value}`
    )
    if (data.value) {
      work.value = data.value
    }
    
    const { data: commentsData } = await useFetch<Comment[]>(
      `${config.public.apiBase}/api/works/${workId.value}/comments`
    )
    if (commentsData.value) {
      comments.value = commentsData.value
    }

    if (authStore.isAuthenticated) {
      const { data: likedData } = await useFetch<{ liked: boolean }>(
        `${config.public.apiBase}/api/works/${workId.value}/liked`,
        {
          headers: {
            Authorization: `Bearer ${authStore.token}`
          }
        }
      )
      if (likedData.value) {
        isLiked.value = likedData.value.liked
      }

      const { data: favoritedData } = await useFetch<{ favorited: boolean }>(
        `${config.public.apiBase}/api/works/${workId.value}/favorited`,
        {
          headers: {
            Authorization: `Bearer ${authStore.token}`
          }
        }
      )
      if (favoritedData.value) {
        isFavorited.value = favoritedData.value.favorited
      }
    }
  } catch (error) {
    console.error('Failed to fetch work:', error)
  } finally {
    loading.value = false
  }
}

const toggleLike = async () => {
  if (!authStore.isAuthenticated) {
    navigateTo('/login')
    return
  }

  try {
    const { data } = await useFetch<{ liked: boolean }>(
      `${config.public.apiBase}/api/works/${workId.value}/like`,
      {
        method: 'POST',
        headers: {
          Authorization: `Bearer ${authStore.token}`
        }
      }
    )
    if (data.value) {
      isLiked.value = data.value.liked
      if (work.value) {
        work.value.likes_count += data.value.liked ? 1 : -1
      }
    }
  } catch (error) {
    console.error('Failed to toggle like:', error)
  }
}

const toggleFavorite = async () => {
  if (!authStore.isAuthenticated) {
    navigateTo('/login')
    return
  }

  try {
    const { data } = await useFetch<{ favorited: boolean }>(
      `${config.public.apiBase}/api/works/${workId.value}/favorite`,
      {
        method: 'POST',
        headers: {
          Authorization: `Bearer ${authStore.token}`
        }
      }
    )
    if (data.value) {
      isFavorited.value = data.value.favorited
      if (work.value) {
        work.value.favorites_count += data.value.favorited ? 1 : -1
      }
    }
  } catch (error) {
    console.error('Failed to toggle favorite:', error)
  }
}

const submitComment = async () => {
  if (!authStore.isAuthenticated) {
    navigateTo('/login')
    return
  }

  if (!commentContent.value.trim()) return

  submittingComment.value = true
  try {
    const { data } = await useFetch<Comment>(
      `${config.public.apiBase}/api/comments`,
      {
        method: 'POST',
        headers: {
          Authorization: `Bearer ${authStore.token}`
        },
        body: {
          work_id: workId.value,
          content: commentContent.value
        }
      }
    )
    if (data.value) {
      comments.value.unshift(data.value)
      commentContent.value = ''
      if (work.value) {
        work.value.comments_count++
      }
    }
  } catch (error) {
    console.error('Failed to submit comment:', error)
  } finally {
    submittingComment.value = false
  }
}

onMounted(() => {
  fetchWork()
})
</script>

<template>
  <div class="container-custom py-8">
    <div v-if="loading" class="flex items-center justify-center min-h-[60vh]">
      <div class="flex flex-col items-center gap-4">
        <div class="w-12 h-12 border-4 border-primary-200 border-t-primary-600 rounded-full animate-spin" />
        <p class="text-gray-500">加载中...</p>
      </div>
    </div>

    <div v-else-if="!work" class="text-center py-16">
      <div class="text-6xl mb-4">😔</div>
      <h3 class="text-xl font-semibold text-gray-900 mb-2">作品不存在</h3>
      <p class="text-gray-600 mb-6">该作品可能已被删除或不存在</p>
      <NuxtLink to="/works" class="btn-primary">
        返回作品列表
      </NuxtLink>
    </div>

    <div v-else class="grid grid-cols-1 lg:grid-cols-3 gap-8">
      <div class="lg:col-span-2">
        <div class="card overflow-hidden mb-6">
          <div class="aspect-video bg-gray-100">
            <img
              :src="imageUrl"
              :alt="work.title"
              class="w-full h-full object-contain"
            />
          </div>
          
          <div v-if="work.images.length > 1" class="p-4 border-t border-gray-100">
            <div class="flex gap-2 overflow-x-auto scrollbar-hide">
              <img
                v-for="(image, index) in work.images"
                :key="index"
                :src="image.startsWith('http') ? image : `${config.public.apiBase}${image}`"
                :alt="`${work.title} - ${index + 1}`"
                class="w-20 h-20 object-cover rounded-lg cursor-pointer hover:ring-2 hover:ring-primary-500 transition-all"
              />
            </div>
          </div>
        </div>

        <div class="card p-6 mb-6">
          <h1 class="text-2xl font-bold text-gray-900 mb-4">{{ work.title }}</h1>
          <div class="flex flex-wrap items-center gap-4 mb-6">
            <NuxtLink
              :to="`/users/${work.designer_id}`"
              class="flex items-center gap-2 hover:opacity-80 transition-opacity"
            >
              <div class="w-10 h-10 bg-gray-200 rounded-full flex items-center justify-center overflow-hidden">
                <img
                  v-if="work.designer_avatar"
                  :src="work.designer_avatar"
                  :alt="work.designer_name"
                  class="w-full h-full object-cover"
                />
                <span v-else class="text-sm font-medium text-gray-600">
                  {{ work.designer_name?.[0]?.toUpperCase() }}
                </span>
              </div>
              <span class="font-medium text-gray-900">{{ work.designer_name }}</span>
            </NuxtLink>
            
            <span class="badge badge-primary">{{ work.category }}</span>
            
            <div class="flex items-center gap-4 text-sm text-gray-500">
              <span class="flex items-center gap-1">
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"
                  />
                </svg>
                {{ work.likes_count }}
              </span>
              <span class="flex items-center gap-1">
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M5 5a2 2 0 012-2h10a2 2 0 012 2v16l-7-3.5L5 21V5z"
                  />
                </svg>
                {{ work.favorites_count }}
              </span>
              <span class="flex items-center gap-1">
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
                {{ work.views_count }}
              </span>
            </div>
          </div>

          <div class="prose max-w-none">
            <h3 class="text-lg font-semibold text-gray-900 mb-2">作品描述</h3>
            <p class="text-gray-600 whitespace-pre-line">{{ work.description }}</p>
          </div>

          <div v-if="work.tags.length > 0" class="mt-6">
            <h3 class="text-lg font-semibold text-gray-900 mb-3">标签</h3>
            <div class="flex flex-wrap gap-2">
              <span
                v-for="tag in work.tags"
                :key="tag"
                class="px-3 py-1 bg-gray-100 text-gray-600 rounded-full text-sm"
              >
                #{{ tag }}
              </span>
            </div>
          </div>
        </div>

        <div class="card p-6">
          <h3 class="text-lg font-semibold text-gray-900 mb-4">
            评论 ({{ work.comments_count }})
          </h3>

          <div v-if="authStore.isAuthenticated" class="mb-6">
            <div class="flex gap-4">
              <div class="w-10 h-10 bg-gray-200 rounded-full flex items-center justify-center overflow-hidden flex-shrink-0">
                <span class="text-sm font-medium text-gray-600">
                  {{ authStore.user?.username?.[0]?.toUpperCase() }}
                </span>
              </div>
              <div class="flex-1">
                <textarea
                  v-model="commentContent"
                  placeholder="写下你的评论..."
                  class="input resize-none"
                  rows="3"
                />
                <div class="flex justify-end mt-2">
                  <button
                    @click="submitComment"
                    :disabled="submittingComment || !commentContent.trim()"
                    class="btn-primary"
                  >
                    {{ submittingComment ? '发送中...' : '发送' }}
                  </button>
                </div>
              </div>
            </div>
          </div>

          <div v-else class="mb-6 p-4 bg-gray-50 rounded-lg text-center">
            <p class="text-gray-600 mb-3">登录后即可发表评论</p>
            <NuxtLink to="/login" class="btn-primary">
              立即登录
            </NuxtLink>
          </div>

          <div class="space-y-4">
            <div
              v-for="comment in comments"
              :key="comment.id"
              class="flex gap-4"
            >
              <div class="w-10 h-10 bg-gray-200 rounded-full flex items-center justify-center overflow-hidden flex-shrink-0">
                <img
                  v-if="comment.user_avatar"
                  :src="comment.user_avatar"
                  :alt="comment.user_name"
                  class="w-full h-full object-cover"
                />
                <span v-else class="text-sm font-medium text-gray-600">
                  {{ comment.user_name?.[0]?.toUpperCase() }}
                </span>
              </div>
              <div class="flex-1">
                <div class="flex items-center gap-2 mb-1">
                  <span class="font-medium text-gray-900">{{ comment.user_name }}</span>
                  <span class="text-xs text-gray-400">
                    {{ new Date(comment.created_at).toLocaleDateString('zh-CN') }}
                  </span>
                </div>
                <p class="text-gray-700">{{ comment.content }}</p>
              </div>
            </div>

            <div v-if="comments.length === 0" class="text-center py-8 text-gray-500">
              暂无评论，来发表第一条评论吧！
            </div>
          </div>
        </div>
      </div>

      <div class="lg:col-span-1">
        <div class="card p-6 sticky top-24">
          <div v-if="work.price > 0" class="mb-6">
            <div class="flex items-baseline gap-2 mb-2">
              <span class="text-3xl font-bold text-primary-600">¥{{ work.price }}</span>
              <span v-if="work.is_premium" class="badge badge-warning">付费</span>
            </div>
          </div>

          <div class="space-y-3 mb-6">
            <button
              @click="toggleLike"
              :class="[
                'w-full flex items-center justify-center gap-2 py-3 px-4 rounded-lg transition-colors',
                isLiked
                  ? 'bg-red-50 text-red-600 border border-red-200'
                  : 'bg-gray-50 text-gray-700 border border-gray-200 hover:bg-gray-100'
              ]"
            >
              <svg
                class="w-5 h-5"
                :fill="isLiked ? 'currentColor' : 'none'"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"
                />
              </svg>
              <span>{{ isLiked ? '已点赞' : '点赞' }}</span>
            </button>

            <button
              @click="toggleFavorite"
              :class="[
                'w-full flex items-center justify-center gap-2 py-3 px-4 rounded-lg transition-colors',
                isFavorited
                  ? 'bg-yellow-50 text-yellow-600 border border-yellow-200'
                  : 'bg-gray-50 text-gray-700 border border-gray-200 hover:bg-gray-100'
              ]"
            >
              <svg
                class="w-5 h-5"
                :fill="isFavorited ? 'currentColor' : 'none'"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M5 5a2 2 0 012-2h10a2 2 0 012 2v16l-7-3.5L5 21V5z"
                />
              </svg>
              <span>{{ isFavorited ? '已收藏' : '收藏' }}</span>
            </button>

            <button class="w-full flex items-center justify-center gap-2 py-3 px-4 bg-gray-50 text-gray-700 border border-gray-200 rounded-lg hover:bg-gray-100 transition-colors">
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M8.684 13.342C8.886 12.938 9 12.482 9 12c0-.482-.114-.938-.316-1.342m0 2.684a3 3 0 110-2.684m0 2.684l6.632 3.316m-6.632-6l6.632-3.316m0 0a3 3 0 105.367-2.684 3 3 0 00-5.367 2.684zm0 9.316a3 3 0 105.368 2.684 3 3 0 00-5.368-2.684z"
                />
              </svg>
              <span>分享</span>
            </button>
          </div>

          <div v-if="work.price > 0" class="border-t border-gray-100 pt-6">
            <button class="w-full btn-primary text-lg py-3">
              立即购买 ¥{{ work.price }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
