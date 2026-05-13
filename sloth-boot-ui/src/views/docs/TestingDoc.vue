<script setup lang="ts">
import MarkdownRenderer from '@/components/common/MarkdownRenderer.vue'
import testingContent from '@/docs/testing.md?raw'
</script>

<template>
  <div class="doc-page">
    <MarkdownRenderer :content="testingContent" />
  </div>
</template>

<style scoped>
.doc-page {
  padding: 0;
}
</style>
