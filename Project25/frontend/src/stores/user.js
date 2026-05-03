import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { api } from '../utils/request'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))

  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => userInfo.value?.is_admin || false)
  const username = computed(() => userInfo.value?.username || '')

  async function login(username, password) {
    const response = await api.post('/auth/login', { username, password })
    token.value = response.data.access_token
    userInfo.value = response.data.user
    
    localStorage.setItem('token', token.value)
    localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
    
    return response.data
  }

  async function register(username, password, email) {
    const response = await api.post('/auth/register', { username, password, email })
    token.value = response.data.access_token
    userInfo.value = response.data.user
    
    localStorage.setItem('token', token.value)
    localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
    
    return response.data
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  async function fetchProfile() {
    try {
      const response = await api.get('/auth/profile')
      userInfo.value = response.data
      localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
    } catch (error) {
      logout()
      throw error
    }
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    isAdmin,
    username,
    login,
    register,
    logout,
    fetchProfile
  }
})
