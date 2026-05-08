<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { modules } from '@/data/modules'

const router = useRouter()
const search = ref('')
const filter = ref<'all' | 'common' | 'starter'>('all')

const filtered = computed(() => {
  return modules.filter(m => {
    if (filter.value !== 'all' && m.category !== filter.value) return false
    if (search.value) {
      const q = search.value.toLowerCase()
      return m.title.toLowerCase().includes(q) || m.name.toLowerCase().includes(q) || m.description.toLowerCase().includes(q)
    }
    return true
  })
})

const moduleCount = computed(() => ({
  all: modules.length,
  common: modules.filter(m => m.category === 'common').length,
  starter: modules.filter(m => m.category === 'starter').length,
}))
</script>

<template>
  <div class="module-explorer">
    <div class="explorer-header">
      <h1 class="page-title">模块浏览</h1>
      <p class="page-desc">Sloth Boot 提供 {{ moduleCount.all }} 个模块，涵盖基础设施与业务能力</p>
    </div>

    <div class="toolbar">
      <el-input
        v-model="search"
        placeholder="搜索模块名称或描述..."
        clearable
        class="search-input"
      />
      <div class="filter-tabs">
        <button
          :class="['filter-tab', { active: filter === 'all' }]"
          @click="filter = 'all'"
        >
          全部
          <span class="tab-count">{{ moduleCount.all }}</span>
        </button>
        <button
          :class="['filter-tab', { active: filter === 'common' }]"
          @click="filter = 'common'"
        >
          Common
          <span class="tab-count">{{ moduleCount.common }}</span>
        </button>
        <button
          :class="['filter-tab', { active: filter === 'starter' }]"
          @click="filter = 'starter'"
        >
          Starter
          <span class="tab-count">{{ moduleCount.starter }}</span>
        </button>
      </div>
    </div>

    <div class="modules-grid">
      <div
        v-for="(m, i) in filtered"
        :key="m.name"
        class="module-card"
        :style="{ animationDelay: `${i * 0.04}s` }"
      >
        <div class="card-top">
          <div class="module-icon-area">
            <el-icon :size="20"><component :is="m.icon" /></el-icon>
          </div>
          <span class="module-category-tag" :class="m.category">
            {{ m.category }}
          </span>
        </div>
        <h3 class="module-name-title">{{ m.title }}</h3>
        <p class="module-artifact">{{ m.name }}</p>
        <p class="module-description">{{ m.description }}</p>
        <ul class="module-features">
          <li v-for="f in m.features" :key="f">{{ f }}</li>
        </ul>
        <div class="module-footer">
          <el-button
            v-if="m.demoRoute"
            type="primary"
            size="small"
            round
            @click="router.push(m.demoRoute!)"
          >
            体验 Demo
          </el-button>
          <el-tag size="small" type="info" effect="plain">{{ m.mavenArtifact }}</el-tag>
        </div>
      </div>
    </div>

    <el-empty v-if="filtered.length === 0" description="没有找到匹配的模块" />
  </div>
</template>

<style scoped>
.module-explorer {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 28px 80px;
}

.explorer-header {
  margin-bottom: 32px;
}

.page-title {
  font-family: var(--font-display);
  font-size: 32px;
  font-weight: 800;
  letter-spacing: -0.02em;
  margin: 0 0 8px;
  color: var(--text-primary);
}

.page-desc {
  font-size: 15px;
  color: var(--text-secondary);
  margin: 0;
}

.toolbar {
  display: flex;
  gap: 20px;
  margin-bottom: 32px;
  align-items: center;
  flex-wrap: wrap;
}

.search-input {
  width: 320px;
}

.search-input :deep(.el-input__wrapper) {
  border-radius: var(--radius-full) !important;
  padding: 4px 16px !important;
}

.filter-tabs {
  display: flex;
  gap: 4px;
  background: var(--bg-raised);
  border-radius: var(--radius-full);
  padding: 3px;
  border: 1px solid var(--border);
}

.filter-tab {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 16px;
  border: none;
  background: transparent;
  border-radius: var(--radius-full);
  font-size: 13px;
  font-weight: 500;
  color: var(--text-secondary);
  cursor: pointer;
  transition: all var(--transition-fast);
}

.filter-tab:hover {
  color: var(--text-primary);
}

.filter-tab.active {
  background: var(--accent-bg);
  color: var(--accent);
}

.tab-count {
  font-size: 11px;
  opacity: 0.6;
}

.modules-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 18px;
}

.module-card {
  background: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: var(--radius-lg);
  padding: 22px;
  display: flex;
  flex-direction: column;
  gap: 8px;
  backdrop-filter: blur(8px);
  transition: all var(--transition-normal);
  animation: fadeInUp 0.5s var(--ease-out) both;
}

.module-card:hover {
  border-color: var(--accent-border);
  box-shadow: var(--shadow-glow);
  transform: translateY(-3px);
}

.card-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.module-icon-area {
  width: 36px;
  height: 36px;
  border-radius: var(--radius-sm);
  background: var(--accent-bg);
  border: 1px solid var(--accent-border);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--accent);
}

.module-category-tag {
  font-size: 10px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  padding: 2px 8px;
  border-radius: var(--radius-full);
}

.module-category-tag.common {
  background: var(--info-bg);
  color: var(--info);
}

.module-category-tag.starter {
  background: var(--accent-bg);
  color: var(--accent);
}

.module-name-title {
  font-family: var(--font-display);
  font-size: 16px;
  font-weight: 700;
  margin: 4px 0 0;
  color: var(--text-primary);
}

.module-artifact {
  font-family: var(--font-mono);
  font-size: 12px;
  color: var(--text-muted);
  margin: 0;
}

.module-description {
  font-size: 13px;
  color: var(--text-secondary);
  margin: 0;
  line-height: 1.6;
}

.module-features {
  list-style: none;
  padding: 0;
  margin: 4px 0 0;
  flex: 1;
}

.module-features li {
  font-size: 12px;
  color: var(--text-muted);
  padding: 3px 0;
  padding-left: 14px;
  position: relative;
}

.module-features li::before {
  content: '';
  position: absolute;
  left: 0;
  top: 10px;
  width: 4px;
  height: 4px;
  border-radius: 50%;
  background: var(--accent-border);
}

.module-footer {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid var(--border);
}

@media (max-width: 1024px) {
  .modules-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .module-explorer {
    padding: 24px 16px 60px;
  }

  .modules-grid {
    grid-template-columns: 1fr;
  }

  .toolbar {
    flex-direction: column;
    align-items: stretch;
  }

  .search-input {
    width: 100%;
  }
}
</style>
