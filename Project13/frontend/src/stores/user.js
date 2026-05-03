import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useUserStore = defineStore('user', () => {
  const userInfo = ref(null)

  const isLoggedIn = computed(() => !!userInfo.value)
  const isAdmin = computed(() => userInfo.value?.role === 1)

  function setUser(user) {
    userInfo.value = user
    localStorage.setItem('user', JSON.stringify(user))
  }

  function clearUser() {
    userInfo.value = null
    localStorage.removeItem('user')
  }

  function initUser() {
    const user = localStorage.getItem('user')
    if (user) {
      userInfo.value = JSON.parse(user)
    }
  }

  return {
    userInfo,
    isLoggedIn,
    isAdmin,
    setUser,
    clearUser,
    initUser
  }
})
