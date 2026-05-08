import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userId = ref<number | null>(null)
  const username = ref('')

  function setAuth(data: { token: string; userId: number; username: string }) {
    token.value = data.token
    userId.value = data.userId
    username.value = data.username
    localStorage.setItem('token', data.token)
  }

  function clearAuth() {
    token.value = ''
    userId.value = null
    username.value = ''
    localStorage.removeItem('token')
  }

  return { token, userId, username, setAuth, clearAuth }
})
