import { defineStore } from 'pinia'
import { login, logout, getInfo } from '@/api/auth'
import { getToken, setToken, removeToken } from '@/utils/auth'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: getToken(),
    name: '',
    avatar: '',
    roles: [],
    permissions: []
  }),

  actions: {
    login(userInfo) {
      const { username, password } = userInfo
      return new Promise((resolve, reject) => {
        login({ username: username.trim(), password: password }).then(response => {
          const { data } = response
          this.token = data.token
          setToken(data.token)
          resolve()
        }).catch(error => {
          reject(error)
        })
      })
    },

    getInfo() {
      return new Promise((resolve, reject) => {
        getInfo().then(response => {
          const { data } = response
          
          if (!data) {
            return reject('验证失败，请重新登录。')
          }

          const { user, roles, permissions } = data
          
          this.name = user.nickName
          this.avatar = user.avatar || ''
          this.roles = roles || []
          this.permissions = permissions || []
          
          resolve(data)
        }).catch(error => {
          reject(error)
        })
      })
    },

    logout() {
      return new Promise((resolve, reject) => {
        logout().then(() => {
          this.token = ''
          this.roles = []
          this.permissions = []
          removeToken()
          resolve()
        }).catch(error => {
          reject(error)
        })
      })
    },

    resetToken() {
      return new Promise(resolve => {
        this.token = ''
        this.roles = []
        this.permissions = []
        removeToken()
        resolve()
      })
    }
  }
})
