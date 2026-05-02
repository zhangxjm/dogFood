import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi, userApi } from '@/api'
import router from '@/router'
import type { UserInfo, MenuItem, User } from '@/types'

const TOKEN_KEY = 'admin_token'
const USER_INFO_KEY = 'admin_user_info'
const REMEMBERED_KEY = 'admin_remembered'

export const useUserStore = defineStore('user', () => {
  const token = ref<string>(localStorage.getItem(TOKEN_KEY) || '')
  const userInfo = ref<UserInfo | null>(null)
  const menus = ref<MenuItem[]>([])

  const isLoggedIn = computed(() => !!token.value)

  function setToken(newToken: string) {
    token.value = newToken
    if (newToken) {
      localStorage.setItem(TOKEN_KEY, newToken)
    } else {
      localStorage.removeItem(TOKEN_KEY)
    }
  }

  function setUserInfo(info: UserInfo | null) {
    userInfo.value = info
    if (info) {
      localStorage.setItem(USER_INFO_KEY, JSON.stringify(info))
    } else {
      localStorage.removeItem(USER_INFO_KEY)
    }
  }

  function setMenus(newMenus: MenuItem[]) {
    menus.value = newMenus
  }

  function setRememberedCredentials(username: string, password: string) {
    localStorage.setItem(REMEMBERED_KEY, JSON.stringify({ username, password }))
  }

  function clearRememberedCredentials() {
    localStorage.removeItem(REMEMBERED_KEY)
  }

  function getRememberedCredentials() {
    const data = localStorage.getItem(REMEMBERED_KEY)
    if (data) {
      try {
        return JSON.parse(data)
      } catch {
        return null
      }
    }
    return null
  }

  async function login(username: string, password: string, rememberMe: boolean) {
    const res = await authApi.login({ username, password })
    
    if (res.code === 200) {
      setToken(res.data.accessToken)
      setUserInfo(res.data.userInfo)
      
      if (rememberMe) {
        setRememberedCredentials(username, password)
      } else {
        clearRememberedCredentials()
      }
      
      await fetchMenus()
      
      return true
    }
    return false
  }

  async function fetchUserInfo() {
    const res = await authApi.getProfile()
    if (res.code === 200) {
      setUserInfo(res.data as UserInfo)
    }
    return res.data
  }

  async function fetchMenus() {
    const res = await authApi.getMenus()
    if (res.code === 200) {
      setMenus(res.data)
    }
    return res.data
  }

  function logout() {
    setToken('')
    setUserInfo(null)
    setMenus([])
    router.push('/login')
  }

  function restoreFromStorage() {
    const info = localStorage.getItem(USER_INFO_KEY)
    if (info) {
      try {
        userInfo.value = JSON.parse(info)
      } catch {
        userInfo.value = null
      }
    }
  }

  return {
    token,
    userInfo,
    menus,
    isLoggedIn,
    login,
    logout,
    fetchUserInfo,
    fetchMenus,
    setToken,
    setUserInfo,
    setMenus,
    getRememberedCredentials,
    restoreFromStorage,
  }
})
