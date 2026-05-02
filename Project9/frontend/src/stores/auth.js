import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi } from '@/api/auth'
import router from '@/router'

export const useAuthStore = defineStore('auth', () => {
  const user = ref(null)
  const token = ref(localStorage.getItem('token') || null)
  
  const isAuthenticated = computed(() => !!token.value)
  
  async function login(credentials) {
    const response = await authApi.login(credentials)
    const { access_token, user: userData } = response.data.data
    
    token.value = access_token
    user.value = userData
    localStorage.setItem('token', access_token)
    
    return response.data
  }
  
  async function register(userData) {
    const response = await authApi.register(userData)
    return response.data
  }
  
  async function fetchUser() {
    try {
      const response = await authApi.getCurrentUser()
      user.value = response.data.data
      return true
    } catch (error) {
      console.error('Failed to fetch user:', error)
      clearAuth()
      return false
    }
  }
  
  function clearAuth() {
    user.value = null
    token.value = null
    localStorage.removeItem('token')
  }
  
  function logout() {
    clearAuth()
    router.push('/login')
  }
  
  function getToken() {
    return token.value
  }
  
  return {
    user,
    token,
    isAuthenticated,
    login,
    register,
    fetchUser,
    logout,
    getToken,
    clearAuth
  }
})
