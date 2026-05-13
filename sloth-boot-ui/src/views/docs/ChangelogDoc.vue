<script setup lang="ts">
import MarkdownRenderer from '@/components/common/MarkdownRenderer.vue'
import changelogContent from '@/docs/changelog.md?raw'
</script>

<template>
  <div class="doc-page">
    <MarkdownRenderer :content="changelogContent" />
  </div>
</template>

<style scoped>
.doc-page {
  padding: 0;
}
</style>
