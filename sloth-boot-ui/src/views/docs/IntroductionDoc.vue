<script setup lang="ts">
import MarkdownRenderer from '@/components/common/MarkdownRenderer.vue'
import introductionContent from '@/docs/introduction.md?raw'
</script>

<template>
  <div class="doc-page">
    <MarkdownRenderer :content="introductionContent" />
  </div>
</template>

<style scoped>
.doc-page {
  padding: 0;
}
</style>
