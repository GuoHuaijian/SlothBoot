<script setup lang="ts">
import MarkdownRenderer from '@/components/common/MarkdownRenderer.vue'
import gettingStartedContent from '@/docs/getting-started.md?raw'
</script>

<template>
  <div class="doc-page">
    <MarkdownRenderer :content="gettingStartedContent" />
  </div>
</template>

<style scoped>
.doc-page {
  padding: 0;
}
</style>
