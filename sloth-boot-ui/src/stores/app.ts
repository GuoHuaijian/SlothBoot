import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAppStore = defineStore('app', () => {
  const dark = ref(localStorage.getItem('theme') === 'dark')

  function toggleDark() {
    dark.value = !dark.value
    localStorage.setItem('theme', dark.value ? 'dark' : 'light')
  }

  return { dark, toggleDark }
})
