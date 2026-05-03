import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login, register, getUserInfo } from '@/api/user'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))
  
  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => userInfo.value?.role === 'admin')
  const username = computed(() => userInfo.value?.username || '')
  
  async function loginAction(loginForm) {
    try {
      const res = await login(loginForm)
      token.value = res.data.access
      localStorage.setItem('token', res.data.access)
      localStorage.setItem('refreshToken', res.data.refresh)
      
      await fetchUserInfo()
      
      return { success: true, message: '登录成功' }
    } catch (error) {
      return { success: false, message: error.message || '登录失败' }
    }
  }
  
  async function registerAction(registerForm) {
    try {
      const res = await register(registerForm)
      token.value = res.data.access
      userInfo.value = res.data.user
      localStorage.setItem('token', res.data.access)
      localStorage.setItem('refreshToken', res.data.refresh)
      localStorage.setItem('userInfo', JSON.stringify(res.data.user))
      
      return { success: true, message: '注册成功' }
    } catch (error) {
      return { success: false, message: error.message || '注册失败' }
    }
  }
  
  async function fetchUserInfo() {
    try {
      const res = await getUserInfo()
      userInfo.value = res.data
      localStorage.setItem('userInfo', JSON.stringify(res.data))
      return { success: true }
    } catch (error) {
      return { success: false, message: error.message }
    }
  }
  
  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('refreshToken')
    localStorage.removeItem('userInfo')
  }
  
  function updateUserInfo(info) {
    userInfo.value = { ...userInfo.value, ...info }
    localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
  }
  
  return {
    token,
    userInfo,
    isLoggedIn,
    isAdmin,
    username,
    loginAction,
    registerAction,
    fetchUserInfo,
    logout,
    updateUserInfo,
  }
})
