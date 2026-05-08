<script setup lang="ts">
import { useAppStore } from '@/stores/app'
import { watch } from 'vue'

const appStore = useAppStore()

watch(() => appStore.dark, (dark) => {
  document.documentElement.classList.toggle('dark', dark)
}, { immediate: true })
</script>

<template>
  <router-view />
</template>
