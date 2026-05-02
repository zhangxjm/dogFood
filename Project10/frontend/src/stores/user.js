import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login, register, getCurrentUser, updateProfile } from '@/api/auth'
import { ElMessage } from 'element-plus'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('user') || 'null'))

  const isLoggedIn = computed(() => !!token.value && !!userInfo.value)
  const isTeacher = computed(() => userInfo.value?.role === 'teacher')

  async function handleLogin(loginForm) {
    try {
      const res = await login(loginForm)
      token.value = res.data.access_token
      userInfo.value = res.data.user
      localStorage.setItem('token', res.data.access_token)
      localStorage.setItem('user', JSON.stringify(res.data.user))
      ElMessage.success('登录成功')
      return true
    } catch (error) {
      return false
    }
  }

  async function handleRegister(registerForm) {
    try {
      await register(registerForm)
      ElMessage.success('注册成功，请登录')
      return true
    } catch (error) {
      return false
    }
  }

  async function fetchUserInfo() {
    try {
      const res = await getCurrentUser()
      userInfo.value = res.data.user
      localStorage.setItem('user', JSON.stringify(res.data.user))
    } catch (error) {
      logout()
    }
  }

  async function handleUpdateProfile(data) {
    try {
      const res = await updateProfile(data)
      userInfo.value = res.data.user
      localStorage.setItem('user', JSON.stringify(res.data.user))
      ElMessage.success('更新成功')
      return true
    } catch (error) {
      return false
    }
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    isTeacher,
    handleLogin,
    handleRegister,
    fetchUserInfo,
    handleUpdateProfile,
    logout
  }
})
