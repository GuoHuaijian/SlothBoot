<script setup lang="ts">
import MarkdownRenderer from '@/components/common/MarkdownRenderer.vue'
import configContent from '@/docs/configuration.md?raw'
</script>

<template>
  <div class="doc-page">
    <MarkdownRenderer :content="configContent" />
  </div>
</template>

<style scoped>
.doc-page {
  padding: 0;
}
</style>
