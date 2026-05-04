import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import api from '@/utils/api'

export const useUserStore = defineStore('user', () => {
  const userId = ref(null)
  const username = ref('')
  const realName = ref('')
  const phone = ref('')
  const role = ref('')
  const workerId = ref(null)
  const workerAuditStatus = ref('')

  const isLoggedIn = computed(() => !!localStorage.getItem('token'))
  const isAdmin = computed(() => role.value === 'ADMIN')
  const isWorker = computed(() => role.value === 'WORKER')

  async function login(usernameInput, password) {
    const res = await api.post('/api/auth/login', {
      username: usernameInput,
      password
    })
    
    if (res.data.code === 200) {
      const token = res.data.data.token
      localStorage.setItem('token', token)
      api.defaults.headers.common['Authorization'] = `Bearer ${token}`
      await fetchUserInfo()
      return { success: true }
    } else {
      return { success: false, message: res.data.message || '登录失败' }
    }
  }

  async function register(username, password, realName, phone) {
    const res = await api.post('/api/auth/register', {
      username,
      password,
      realName,
      phone
    })
    
    if (res.data.code === 200) {
      return { success: true }
    } else {
      return { success: false, message: res.data.message || '注册失败' }
    }
  }

  async function fetchUserInfo() {
    const token = localStorage.getItem('token')
    if (!token) {
      throw new Error('No token')
    }
    
    const res = await api.get('/api/auth/me')
    if (res.data.code === 200) {
      const data = res.data.data
      userId.value = data.id
      username.value = data.username
      realName.value = data.realName || ''
      phone.value = data.phone || ''
      role.value = data.role
      workerId.value = data.workerId || null
      workerAuditStatus.value = data.workerAuditStatus || ''
    } else {
      throw new Error(res.data.message)
    }
  }

  function logout() {
    userId.value = null
    username.value = ''
    realName.value = ''
    phone.value = ''
    role.value = ''
    workerId.value = null
    workerAuditStatus.value = ''
    localStorage.removeItem('token')
    delete api.defaults.headers.common['Authorization']
  }

  function updateUserInfo(info) {
    if (info.realName !== undefined) realName.value = info.realName
    if (info.phone !== undefined) phone.value = info.phone
  }

  function updateWorkerInfo(info) {
    if (info.workerId !== undefined) workerId.value = info.workerId
    if (info.workerAuditStatus !== undefined) workerAuditStatus.value = info.workerAuditStatus
  }

  return {
    userId,
    username,
    realName,
    phone,
    role,
    workerId,
    workerAuditStatus,
    isLoggedIn,
    isAdmin,
    isWorker,
    login,
    register,
    fetchUserInfo,
    logout,
    updateUserInfo,
    updateWorkerInfo
  }
})
