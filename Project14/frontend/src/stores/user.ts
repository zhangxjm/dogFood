import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { request } from '@/utils/request'

export const useUserStore = defineStore('user', () => {
  const token = ref<string>('')
  const userInfo = ref<any>(null)

  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => userInfo.value?.role === 'admin')

  function setToken(newToken: string) {
    token.value = newToken
    uni.setStorageSync('token', newToken)
  }

  function setUserInfo(info: any) {
    userInfo.value = info
    uni.setStorageSync('userInfo', JSON.stringify(info))
  }

  async function login(username: string, password: string) {
    try {
      const res = await request.post('/api/auth/login', { username, password })
      setToken(res.access_token)
      setUserInfo(res.user)
      return res
    } catch (error) {
      throw error
    }
  }

  async function register(data: any) {
    try {
      const res = await request.post('/api/auth/register', data)
      return res
    } catch (error) {
      throw error
    }
  }

  async function getUserInfo() {
    try {
      const res = await request.get('/api/user/profile')
      setUserInfo(res)
      return res
    } catch (error) {
      throw error
    }
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    uni.removeStorageSync('token')
    uni.removeStorageSync('userInfo')
    uni.reLaunch({
      url: '/pages/login/login'
    })
  }

  function initFromStorage() {
    const storedToken = uni.getStorageSync('token')
    const storedUserInfo = uni.getStorageSync('userInfo')
    
    if (storedToken) {
      token.value = storedToken
    }
    if (storedUserInfo) {
      try {
        userInfo.value = JSON.parse(storedUserInfo)
      } catch (e) {
        console.error('解析用户信息失败', e)
      }
    }
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    isAdmin,
    setToken,
    setUserInfo,
    login,
    register,
    getUserInfo,
    logout,
    initFromStorage
  }
})
