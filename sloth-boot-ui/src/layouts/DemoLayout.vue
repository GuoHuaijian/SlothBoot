<script setup lang="ts">
import { useRoute } from 'vue-router'
import AppNavbar from '@/components/common/AppNavbar.vue'
import {
  Setting,
  Goods,
  Document,
  ChatDotRound,
  Lock,
  Monitor,
} from '@element-plus/icons-vue'

const route = useRoute()

const menuItems = [
  { index: '/demo/system', icon: Setting, label: '系统管理' },
  { index: '/demo/product', icon: Goods, label: '商品管理' },
  { index: '/demo/order', icon: Document, label: '订单管理' },
  { index: '/demo/ai', icon: ChatDotRound, label: 'AI 助手' },
  { index: '/demo/security', icon: Lock, label: '安全工具' },
  { index: '/demo/monitor', icon: Monitor, label: '系统监控' },
]
</script>

<template>
  <div class="demo-layout">
    <AppNavbar />
    <div class="demo-body">
      <aside class="demo-sidebar">
        <div class="sidebar-header">
          <span class="sidebar-label">在线体验</span>
          <span class="sidebar-count">{{ menuItems.length }} 个演示</span>
        </div>
        <el-menu :default-active="route.path" router>
          <el-menu-item
            v-for="item in menuItems"
            :key="item.index"
            :index="item.index"
          >
            <el-icon><component :is="item.icon" /></el-icon>
            <span>{{ item.label }}</span>
          </el-menu-item>
        </el-menu>
      </aside>
      <main class="demo-content">
        <div class="page-header" v-if="route.meta.title">
          <h1 class="page-title">{{ route.meta.title }}</h1>
          <div class="page-breadcrumb">
            <span class="breadcrumb-item">在线体验</span>
            <span class="breadcrumb-sep">/</span>
            <span class="breadcrumb-current">{{ route.meta.title }}</span>
          </div>
        </div>
        <router-view />
      </main>
    </div>
  </div>
</template>

<style scoped>
.demo-layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background: var(--bg-base);
}

.demo-body {
  display: flex;
  flex: 1;
}

.demo-sidebar {
  width: 230px;
  flex-shrink: 0;
  background: var(--bg-elevated);
  border-right: 1px solid var(--border);
  padding-top: 16px;
  position: sticky;
  top: 60px;
  height: calc(100vh - 60px);
  overflow-y: auto;
}

.sidebar-header {
  padding: 4px 20px 14px;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.sidebar-label {
  font-family: var(--font-display);
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.06em;
  color: var(--text-muted);
}

.sidebar-count {
  font-size: 11px;
  color: var(--text-muted);
  opacity: 0.7;
}

.demo-sidebar :deep(.el-menu) {
  border-right: none;
  background: transparent;
  padding: 0 8px;
}

.demo-sidebar :deep(.el-menu-item) {
  height: 40px;
  line-height: 40px;
  font-size: 14px;
  font-weight: 500;
  margin: 1px 0;
  border-radius: var(--radius-sm);
  color: var(--text-secondary);
  transition: all var(--transition-fast);
}

.demo-sidebar :deep(.el-menu-item:hover) {
  background: var(--accent-bg);
  color: var(--text-primary);
}

.demo-sidebar :deep(.el-menu-item.is-active) {
  background: var(--accent-bg);
  color: var(--accent);
  position: relative;
}

.demo-sidebar :deep(.el-menu-item.is-active::before) {
  content: '';
  position: absolute;
  left: 0;
  top: 8px;
  bottom: 8px;
  width: 3px;
  border-radius: 2px;
  background: linear-gradient(180deg, var(--accent), var(--accent-light));
}

.demo-content {
  flex: 1;
  padding: 28px 32px;
  overflow: auto;
  min-width: 0;
}

.page-header {
  margin-bottom: 28px;
  padding-bottom: 20px;
  border-bottom: 1px solid var(--border);
}

.page-title {
  font-family: var(--font-display);
  font-size: 24px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 6px;
  letter-spacing: -0.01em;
}

.page-breadcrumb {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
}

.breadcrumb-item {
  color: var(--text-muted);
}

.breadcrumb-sep {
  color: var(--text-muted);
  opacity: 0.5;
}

.breadcrumb-current {
  color: var(--accent);
  font-weight: 500;
}

@media (max-width: 768px) {
  .demo-sidebar {
    display: none;
  }

  .demo-content {
    padding: 20px 16px;
  }
}
</style>
