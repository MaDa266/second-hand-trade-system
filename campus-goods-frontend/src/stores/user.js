import { defineStore } from 'pinia'

function getStoredUser() {
  try {
    return JSON.parse(localStorage.getItem('user')) || null
  } catch (e) {
    return null
  }
}

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userInfo: getStoredUser()
  }),
  getters: {
    isLogin: (state) => !!state.token,
    isAdmin: (state) => state.userInfo?.role === 0
  },
  actions: {
    setLogin({ token, user }) {
      this.token = token
      this.userInfo = user
      localStorage.setItem('token', token)
      localStorage.setItem('user', JSON.stringify(user))
    },
    logout() {
      this.token = ''
      this.userInfo = null
      localStorage.removeItem('token')
      localStorage.removeItem('user')
    }
  }
})
