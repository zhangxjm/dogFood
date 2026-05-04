import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { userApi } from '@/api/user'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const refreshToken = ref(localStorage.getItem('refreshToken') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))
  
  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => userInfo.value?.role === 'admin' || userInfo.value?.is_staff)
  
  function setToken(accessToken, refresh) {
    token.value = accessToken
    refreshToken.value = refresh
    localStorage.setItem('token', accessToken)
    localStorage.setItem('refreshToken', refresh)
  }
  
  function setUserInfo(info) {
    userInfo.value = info
    localStorage.setItem('userInfo', JSON.stringify(info))
  }
  
  async function login(username, password) {
    try {
      const res = await userApi.login(username, password)
      if (res.code === 200) {
        setToken(res.data.access, res.data.refresh)
        setUserInfo(res.data.user)
        return true
      }
      return false
    } catch (error) {
      return false
    }
  }
  
  async function register(userData) {
    try {
      const res = await userApi.register(userData)
      if (res.code === 200) {
        setToken(res.data.access, res.data.refresh)
        setUserInfo(res.data.user)
        return true
      }
      return false
    } catch (error) {
      return false
    }
  }
  
  async function getUserInfo() {
    try {
      const res = await userApi.getUserInfo()
      if (res.code === 200) {
        setUserInfo(res.data)
        return res.data
      }
      return null
    } catch (error) {
      return null
    }
  }
  
  async function updateUserInfo(data) {
    try {
      const res = await userApi.updateInfo(data)
      if (res.code === 200) {
        setUserInfo(res.data)
        return true
      }
      return false
    } catch (error) {
      return false
    }
  }
  
  function logout() {
    token.value = ''
    refreshToken.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('refreshToken')
    localStorage.removeItem('userInfo')
  }
  
  return {
    token,
    refreshToken,
    userInfo,
    isLoggedIn,
    isAdmin,
    login,
    register,
    getUserInfo,
    updateUserInfo,
    logout,
    setToken,
    setUserInfo
  }
})
