<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import MarkdownRenderer from '@/components/common/MarkdownRenderer.vue'

import introductionContent from '@/docs/introduction.md?raw'
import gettingStartedContent from '@/docs/getting-started.md?raw'
import architectureContent from '@/docs/architecture.md?raw'
import configurationContent from '@/docs/configuration.md?raw'
import errorCodeContent from '@/docs/error-codes.md?raw'
import testingContent from '@/docs/testing.md?raw'
import faqContent from '@/docs/faq.md?raw'
import changelogContent from '@/docs/changelog.md?raw'

const contentMap: Record<string, string> = {
  introduction: introductionContent,
  'getting-started': gettingStartedContent,
  architecture: architectureContent,
  configuration: configurationContent,
  'error-codes': errorCodeContent,
  testing: testingContent,
  faq: faqContent,
  changelog: changelogContent,
}

const route = useRoute()
const content = computed(() => {
  const slug = route.path.split('/').pop() || ''
  return contentMap[slug] || '# 404\n文档未找到'
})
</script>

<template>
  <div class="doc-page">
    <MarkdownRenderer :content="content" />
  </div>
</template>

<style scoped>
.doc-page {
  padding: 0;
}
</style>
