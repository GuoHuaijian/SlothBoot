<script setup lang="ts">
import { computed } from 'vue'
import MarkdownIt from 'markdown-it'
import hljs from 'highlight.js'
import 'highlight.js/styles/github-dark.css'

const md = new MarkdownIt({
  html: true,
  linkify: true,
  typographer: true,
  highlight(str: string, lang: string) {
    if (lang && hljs.getLanguage(lang)) {
      return hljs.highlight(str, { language: lang }).value
    }
    return md.utils.escapeHtml(str)
  },
})

const props = defineProps<{ content: string }>()
const html = computed(() => md.render(props.content))
</script>

<template>
  <div class="md-content markdown-body" v-html="html" />
</template>

<style scoped>
.markdown-body {
  max-width: 100%;
  line-height: 1.8;
  color: var(--text-primary);
}

.markdown-body :deep(h1) {
  font-family: var(--font-display);
  font-size: 28px;
  font-weight: 700;
  margin: 0 0 20px;
  padding-bottom: 14px;
  border-bottom: 1px solid var(--border);
  color: var(--text-primary);
}

.markdown-body :deep(h2) {
  font-family: var(--font-display);
  font-size: 22px;
  font-weight: 700;
  margin: 36px 0 14px;
  color: var(--text-primary);
}

.markdown-body :deep(h3) {
  font-family: var(--font-display);
  font-size: 18px;
  font-weight: 600;
  margin: 28px 0 10px;
  color: var(--text-primary);
}

.markdown-body :deep(h4) {
  font-family: var(--font-display);
  font-size: 15px;
  font-weight: 600;
  margin: 20px 0 8px;
  color: var(--text-primary);
}

.markdown-body :deep(p) {
  margin: 0 0 14px;
  line-height: 1.8;
  color: var(--text-secondary);
}

.markdown-body :deep(code) {
  background: var(--bg-raised);
  border: 1px solid var(--border);
  padding: 2px 7px;
  border-radius: 5px;
  font-size: 0.88em;
  font-family: var(--font-mono);
  color: var(--accent);
}

.markdown-body :deep(pre) {
  background: var(--bg-raised);
  border: 1px solid var(--border);
  border-radius: var(--radius-md);
  padding: 18px;
  overflow-x: auto;
  margin: 0 0 18px;
}

.markdown-body :deep(pre code) {
  background: transparent;
  border: none;
  padding: 0;
  color: var(--text-primary);
  font-size: 13px;
  line-height: 1.7;
}

.markdown-body :deep(table) {
  width: 100%;
  border-collapse: collapse;
  margin: 0 0 18px;
  font-size: 13px;
  border-radius: var(--radius-md);
  overflow: hidden;
  border: 1px solid var(--border);
}

.markdown-body :deep(th) {
  background: var(--bg-raised);
  font-weight: 600;
  color: var(--text-secondary);
  text-align: left;
  padding: 11px 16px;
  border: 1px solid var(--border);
}

.markdown-body :deep(td) {
  padding: 11px 16px;
  border: 1px solid var(--border);
  color: var(--text-primary);
}

.markdown-body :deep(tr:hover td) {
  background: var(--accent-bg);
}

.markdown-body :deep(blockquote) {
  border-left: 3px solid var(--accent);
  padding: 10px 18px;
  margin: 0 0 16px;
  background: var(--accent-bg);
  border-radius: 0 var(--radius-sm) var(--radius-sm) 0;
  color: var(--text-secondary);
}

.markdown-body :deep(blockquote p) {
  margin: 0;
}

.markdown-body :deep(ul),
.markdown-body :deep(ol) {
  padding-left: 26px;
  margin: 0 0 14px;
  color: var(--text-secondary);
}

.markdown-body :deep(li) {
  margin: 5px 0;
  line-height: 1.7;
}

.markdown-body :deep(hr) {
  border: none;
  border-top: 1px solid var(--border);
  margin: 28px 0;
}

.markdown-body :deep(strong) {
  color: var(--text-primary);
  font-weight: 600;
}

.markdown-body :deep(a) {
  color: var(--accent);
  text-decoration: none;
  border-bottom: 1px solid transparent;
  transition: border-color var(--transition-fast);
}

.markdown-body :deep(a:hover) {
  border-bottom-color: var(--accent);
}

.markdown-body :deep(img) {
  max-width: 100%;
  border-radius: var(--radius-md);
  border: 1px solid var(--border);
}

.markdown-body :deep(.hljs) {
  background: transparent !important;
  padding: 0 !important;
}
</style>
