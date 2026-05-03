<script setup lang="ts">
const authStore = useAuthStore()
const config = useRuntimeConfig()

const formData = ref({
  title: '',
  description: '',
  category: '插画',
  price: 0,
  tags: '',
  images: [] as string[]
})

const categories = ref<string[]>([])
const uploading = ref(false)
const selectedImages = ref<File[]>([])
const previewImages = ref<string[]>([])

useHead({
  title: '上传作品',
  meta: [
    { name: 'description', content: '上传你的创意作品，分享给更多人。' }
  ]
})

const fetchCategories = async () => {
  try {
    const { data } = await useFetch<{ name: string }[]>(
      `${config.public.apiBase}/api/categories`
    )
    if (data.value) {
      categories.value = data.value.map((c: { name: string }) => c.name)
    }
  } catch (error) {
    console.error('Failed to fetch categories:', error)
  }
}

const handleImageSelect = (event: Event) => {
  const target = event.target as HTMLInputElement
  const files = target.files
  if (!files) return

  for (let i = 0; i < files.length; i++) {
    const file = files[i]
    selectedImages.value.push(file)
    
    const reader = new FileReader()
    reader.onload = (e) => {
      if (e.target?.result) {
        previewImages.value.push(e.target.result as string)
      }
    }
    reader.readAsDataURL(file)
  }
}

const removeImage = (index: number) => {
  selectedImages.value.splice(index, 1)
  previewImages.value.splice(index, 1)
}

const uploadImages = async (): Promise<string[]> => {
  const uploadedUrls: string[] = []
  
  for (const file of selectedImages.value) {
    const formData = new FormData()
    formData.append('file', file)
    
    const { data, error } = await useFetch<{ url: string }>(
      `${config.public.apiBase}/api/upload/image`,
      {
        method: 'POST',
        headers: {
          Authorization: `Bearer ${authStore.token}`
        },
        body: formData
      }
    )
    
    if (error.value) {
      console.error('Upload error:', error.value)
      continue
    }
    
    if (data.value?.url) {
      uploadedUrls.push(data.value.url)
    }
  }
  
  return uploadedUrls
}

const handleSubmit = async () => {
  if (!formData.value.title || !formData.value.description) {
    alert('请填写标题和描述')
    return
  }

  if (previewImages.value.length === 0) {
    alert('请至少上传一张图片')
    return
  }

  uploading.value = true
  
  try {
    const uploadedUrls = await uploadImages()
    
    const tagsArray = formData.value.tags
      .split(',')
      .map(t => t.trim())
      .filter(t => t.length > 0)

    const body = {
      title: formData.value.title,
      description: formData.value.description,
      category: formData.value.category,
      price: formData.value.price,
      tags: tagsArray,
      images: uploadedUrls,
      is_premium: formData.value.price > 0
    }

    const { data, error } = await useFetch(
      `${config.public.apiBase}/api/works`,
      {
        method: 'POST',
        headers: {
          Authorization: `Bearer ${authStore.token}`,
          'Content-Type': 'application/json'
        },
        body
      }
    )

    if (error.value) {
      alert('上传失败：' + (error.value.message || '未知错误'))
      return
    }

    alert('作品上传成功！')
    navigateTo('/works')
  } catch (error) {
    console.error('Submit error:', error)
    alert('上传失败，请重试')
  } finally {
    uploading.value = false
  }
}

onMounted(() => {
  if (!authStore.isAuthenticated) {
    navigateTo('/login')
    return
  }
  if (!authStore.isDesigner) {
    alert('只有设计师可以上传作品')
    navigateTo('/')
    return
  }
  fetchCategories()
})
</script>

<template>
  <div class="container-custom py-8">
    <div class="max-w-3xl mx-auto">
      <div class="mb-8">
        <h1 class="text-2xl font-bold text-gray-900">上传作品</h1>
        <p class="text-gray-600 mt-1">分享你的创意作品</p>
      </div>

      <div class="card p-6">
        <form @submit.prevent="handleSubmit" class="space-y-6">
          <div>
            <label class="label">作品图片 *</label>
            <div
              class="border-2 border-dashed border-gray-300 rounded-lg p-8 text-center hover:border-primary-500 transition-colors cursor-pointer"
            >
              <input
                type="file"
                accept="image/*"
                multiple
                class="hidden"
                id="image-upload"
                @change="handleImageSelect"
              />
              <label for="image-upload" class="cursor-pointer">
                <svg
                  class="w-12 h-12 text-gray-400 mx-auto mb-3"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z"
                  />
                </svg>
                <p class="text-gray-600">点击或拖拽图片到这里上传</p>
                <p class="text-sm text-gray-400 mt-1">支持 JPG、PNG、GIF、WebP 格式</p>
              </label>
            </div>

            <div v-if="previewImages.length > 0" class="mt-4 flex flex-wrap gap-4">
              <div
                v-for="(image, index) in previewImages"
                :key="index"
                class="relative w-24 h-24 rounded-lg overflow-hidden"
              >
                <img :src="image" class="w-full h-full object-cover" />
                <button
                  type="button"
                  @click="removeImage(index)"
                  class="absolute top-1 right-1 w-6 h-6 bg-red-500 text-white rounded-full flex items-center justify-center hover:bg-red-600 transition-colors"
                >
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                  </svg>
                </button>
                <span v-if="index === 0" class="absolute bottom-1 left-1 bg-primary-600 text-white text-xs px-2 py-0.5 rounded">
                  封面
                </span>
              </div>
            </div>
          </div>

          <div>
            <label class="label">作品标题 *</label>
            <input
              v-model="formData.title"
              type="text"
              placeholder="为你的作品起一个响亮的标题"
              class="input"
              required
            />
          </div>

          <div>
            <label class="label">作品描述 *</label>
            <textarea
              v-model="formData.description"
              placeholder="详细描述你的作品，包括创作灵感、技巧等..."
              class="input resize-none"
              rows="5"
              required
            />
          </div>

          <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <label class="label">分类 *</label>
              <select v-model="formData.category" class="input">
                <option v-for="cat in categories" :key="cat" :value="cat">
                  {{ cat }}
                </option>
              </select>
            </div>

            <div>
              <label class="label">价格 (元)</label>
              <input
                v-model.number="formData.price"
                type="number"
                min="0"
                step="0.01"
                placeholder="0 表示免费"
                class="input"
              />
              <p class="text-sm text-gray-500 mt-1">设置为 0 表示免费作品</p>
            </div>
          </div>

          <div>
            <label class="label">标签</label>
            <input
              v-model="formData.tags"
              type="text"
              placeholder="用逗号分隔多个标签，如：插画, 设计, 艺术"
              class="input"
            />
          </div>

          <div class="flex gap-4 pt-4">
            <button
              type="button"
              @click="navigateTo('/')"
              class="btn-outline flex-1"
            >
              取消
            </button>
            <button
              type="submit"
              :disabled="uploading"
              class="btn-primary flex-1"
            >
              {{ uploading ? '上传中...' : '发布作品' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>
