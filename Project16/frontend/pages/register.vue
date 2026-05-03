<script setup lang="ts">
const authStore = useAuthStore()
const router = useRouter()

const formData = ref({
  username: '',
  email: '',
  password: '',
  confirmPassword: '',
  isDesigner: false
})

const loading = ref(false)
const error = ref('')

useHead({
  title: '注册',
  meta: [
    { name: 'description', content: '注册文创作品平台，开始你的创意之旅。' }
  ]
})

const handleRegister = async () => {
  if (!formData.value.username || !formData.value.email || !formData.value.password) {
    error.value = '请填写所有必填字段'
    return
  }

  if (formData.value.password !== formData.value.confirmPassword) {
    error.value = '两次输入的密码不一致'
    return
  }

  if (formData.value.password.length < 6) {
    error.value = '密码至少需要6个字符'
    return
  }

  loading.value = true
  error.value = ''

  const result = await authStore.register({
    email: formData.value.email,
    password: formData.value.password,
    username: formData.value.username,
    is_designer: formData.value.isDesigner
  })
  
  if (result.success) {
    router.push('/')
  } else {
    error.value = result.error || '注册失败'
  }

  loading.value = false
}
</script>

<template>
  <div class="min-h-[calc(100vh-12rem)] flex items-center justify-center py-12">
    <div class="card w-full max-w-md p-8">
      <div class="text-center mb-8">
        <div class="flex items-center justify-center space-x-2 mb-4">
          <div class="w-12 h-12 bg-gradient-to-br from-primary-500 to-accent-500 rounded-xl flex items-center justify-center">
            <span class="text-white font-bold text-2xl">A</span>
          </div>
        </div>
        <h1 class="text-2xl font-bold text-gray-900">创建账户</h1>
        <p class="text-gray-600 mt-2">加入我们，探索无限创意可能</p>
      </div>

      <div v-if="error" class="mb-6 p-4 bg-red-50 border border-red-200 rounded-lg text-red-600 text-sm">
        {{ error }}
      </div>

      <form @submit.prevent="handleRegister" class="space-y-5">
        <div>
          <label class="label">用户名</label>
          <input
            v-model="formData.username"
            type="text"
            placeholder="你的用户名"
            class="input"
            required
          />
        </div>

        <div>
          <label class="label">邮箱</label>
          <input
            v-model="formData.email"
            type="email"
            placeholder="your@email.com"
            class="input"
            required
          />
        </div>

        <div>
          <label class="label">密码</label>
          <input
            v-model="formData.password"
            type="password"
            placeholder="至少6个字符"
            class="input"
            required
          />
        </div>

        <div>
          <label class="label">确认密码</label>
          <input
            v-model="formData.confirmPassword"
            type="password"
            placeholder="再次输入密码"
            class="input"
            required
          />
        </div>

        <div class="flex items-start gap-3 p-4 bg-gray-50 rounded-lg">
          <input
            v-model="formData.isDesigner"
            type="checkbox"
            id="isDesigner"
            class="mt-1 w-4 h-4 text-primary-600 border-gray-300 rounded focus:ring-primary-500"
          />
          <label for="isDesigner" class="text-sm">
            <span class="font-medium text-gray-900">我是设计师</span>
            <span class="block text-gray-500 mt-0.5">
              勾选后可以上传和出售你的创意作品
            </span>
          </label>
        </div>

        <button
          type="submit"
          :disabled="loading"
          class="w-full btn-primary text-lg py-3"
        >
          {{ loading ? '注册中...' : '创建账户' }}
        </button>
      </form>

      <p class="mt-6 text-xs text-gray-500 text-center">
        注册即表示你同意我们的
        <NuxtLink to="/terms" class="text-primary-600 hover:text-primary-700">
          用户协议
        </NuxtLink>
        和
        <NuxtLink to="/privacy" class="text-primary-600 hover:text-primary-700">
          隐私政策
        </NuxtLink>
      </p>

      <p class="mt-6 text-center text-sm text-gray-600">
        已有账户？
        <NuxtLink to="/login" class="text-primary-600 hover:text-primary-700 font-medium">
          立即登录
        </NuxtLink>
      </p>
    </div>
  </div>
</template>
