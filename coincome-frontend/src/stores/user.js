import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  const user = ref(null)
  const token = ref(null)

  function setUser(u, t) {
    user.value = u
    token.value = t
    localStorage.setItem('user', JSON.stringify(u))
    localStorage.setItem('token', t)
  }

  function logout() {
    user.value = null
    token.value = null
    localStorage.removeItem('user')
    localStorage.removeItem('token')
  }

  function loadFromStorage() {
    const u = localStorage.getItem('user')
    const t = localStorage.getItem('token')
    if (u && t) {
      user.value = JSON.parse(u)
      token.value = t
    }
  }

  return { user, token, setUser, logout, loadFromStorage }
})
