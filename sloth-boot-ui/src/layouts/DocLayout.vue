<script setup lang="ts">
import { useRoute } from 'vue-router'
import AppNavbar from '@/components/common/AppNavbar.vue'
import AppFooter from '@/components/common/AppFooter.vue'
import { Document, Setting, Warning } from '@element-plus/icons-vue'

const route = useRoute()
</script>

<template>
  <div class="doc-layout">
    <AppNavbar />
    <div class="doc-body">
      <aside class="doc-sidebar">
        <div class="sidebar-header">
          <span class="sidebar-label">文档中心</span>
        </div>
        <el-menu :default-active="route.path" router>
          <el-menu-item index="/docs/architecture">
            <el-icon><Document /></el-icon>
            <span>架构文档</span>
          </el-menu-item>
          <el-menu-item index="/docs/configuration">
            <el-icon><Setting /></el-icon>
            <span>配置参考</span>
          </el-menu-item>
          <el-menu-item index="/docs/error-codes">
            <el-icon><Warning /></el-icon>
            <span>错误码注册表</span>
          </el-menu-item>
        </el-menu>
      </aside>
      <main class="doc-content">
        <router-view />
      </main>
    </div>
    <AppFooter />
  </div>
</template>

<style scoped>
.doc-layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background: var(--bg-base);
}

.doc-body {
  display: flex;
  flex: 1;
}

.doc-sidebar {
  width: 250px;
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
}

.sidebar-label {
  font-family: var(--font-display);
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.06em;
  color: var(--text-muted);
}

.doc-sidebar :deep(.el-menu) {
  border-right: none;
  background: transparent;
  padding: 0 8px;
}

.doc-sidebar :deep(.el-menu-item) {
  height: 40px;
  line-height: 40px;
  font-size: 14px;
  font-weight: 500;
  margin: 1px 0;
  border-radius: var(--radius-sm);
  color: var(--text-secondary);
  transition: all var(--transition-fast);
}

.doc-sidebar :deep(.el-menu-item:hover) {
  background: var(--accent-bg);
  color: var(--text-primary);
}

.doc-sidebar :deep(.el-menu-item.is-active) {
  background: var(--accent-bg);
  color: var(--accent);
  position: relative;
}

.doc-sidebar :deep(.el-menu-item.is-active::before) {
  content: '';
  position: absolute;
  left: 0;
  top: 8px;
  bottom: 8px;
  width: 3px;
  border-radius: 2px;
  background: linear-gradient(180deg, var(--accent), var(--accent-light));
}

.doc-content {
  flex: 1;
  padding: 32px 48px;
  max-width: 900px;
  min-width: 0;
}

@media (max-width: 768px) {
  .doc-sidebar {
    display: none;
  }

  .doc-content {
    padding: 20px 16px;
  }
}
</style>
