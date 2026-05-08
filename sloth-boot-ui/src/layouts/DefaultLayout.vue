<script setup lang="ts">
import AppNavbar from '@/components/common/AppNavbar.vue'
import AppFooter from '@/components/common/AppFooter.vue'
</script>

<template>
  <div class="layout">
    <AppNavbar />
    <main class="main-content">
      <router-view />
    </main>
    <AppFooter />
  </div>
</template>

<style scoped>
.layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background: var(--bg-base);
}

.main-content {
  flex: 1;
}
</style>
