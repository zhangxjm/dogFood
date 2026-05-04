import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login, getUserInfo } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))

  const isLoggedIn = computed(() => !!token.value)

  async function loginAction(loginForm) {
    try {
      const res = await login(loginForm)
      if (res.code === 200) {
        token.value = res.data.token
        userInfo.value = {
          userId: res.data.userId,
          username: res.data.username,
          nickname: res.data.nickname,
          avatar: res.data.avatar
        }
        localStorage.setItem('token', token.value)
        localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
        return { success: true }
      } else {
        return { success: false, message: res.message }
      }
    } catch (error) {
      return { success: false, message: '登录失败，请稍后重试' }
    }
  }

  async function fetchUserInfo() {
    try {
      const res = await getUserInfo()
      if (res.code === 200) {
        userInfo.value = res.data
        localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
        return { success: true }
      }
      return { success: false }
    } catch (error) {
      return { success: false }
    }
  }

  function logout() {
    token.value = ''
    userInfo.value = {}
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    loginAction,
    fetchUserInfo,
    logout
  }
})
