<script setup lang="ts">
import MarkdownRenderer from '@/components/common/MarkdownRenderer.vue'
import architectureContent from '@/docs/architecture.md?raw'
</script>

<template>
  <div class="doc-page">
    <MarkdownRenderer :content="architectureContent" />
  </div>
</template>

<style scoped>
.doc-page {
  padding: 0;
}
</style>
