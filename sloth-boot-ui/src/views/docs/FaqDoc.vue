<script setup lang="ts">
import MarkdownRenderer from '@/components/common/MarkdownRenderer.vue'
import faqContent from '@/docs/faq.md?raw'
</script>

<template>
  <div class="doc-page">
    <MarkdownRenderer :content="faqContent" />
  </div>
</template>

<style scoped>
.doc-page {
  padding: 0;
}
</style>
