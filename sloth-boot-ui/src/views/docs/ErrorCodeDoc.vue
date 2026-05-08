<script setup lang="ts">
import MarkdownRenderer from '@/components/common/MarkdownRenderer.vue'
import errorCodeContent from '@/docs/error-codes.md?raw'
</script>

<template>
  <div class="doc-page">
    <MarkdownRenderer :content="errorCodeContent" />
  </div>
</template>

<style scoped>
.doc-page {
  padding: 0;
}
</style>
