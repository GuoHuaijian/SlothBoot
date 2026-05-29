<script setup lang="ts">
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAppStore } from '@/stores/app'
import { Sunny, Moon } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const appStore = useAppStore()
const mobileMenuOpen = ref(false)

interface NavLink {
  label: string
  path: string
}

const links: NavLink[] = [
  { label: '首页', path: '/' },
  { label: '模块', path: '/modules' },
  { label: '在线体验', path: '/demo/auth' },
  { label: '文档', path: '/docs/architecture' },
]

function navigate(path: string) {
  router.push(path)
  mobileMenuOpen.value = false
}

function isActive(path: string): boolean {
  if (path === '/') return route.path === '/'
  return route.path.startsWith(path)
}
</script>

<template>
  <header class="navbar">
    <div class="navbar-inner">
      <router-link to="/" class="logo">
        <span class="logo-icon">🦥</span>
        <span class="logo-text">Sloth Boot</span>
      </router-link>

      <nav class="nav-links">
        <a
          v-for="link in links"
          :key="link.path"
          :class="['nav-link', { active: isActive(link.path) }]"
          @click.prevent="navigate(link.path)"
        >
          {{ link.label }}
          <span v-if="isActive(link.path)" class="active-dot"></span>
        </a>
      </nav>

      <div class="nav-actions">
        <button class="theme-toggle" @click="appStore.toggleDark()" :aria-label="appStore.dark ? '切换亮色' : '切换暗色'">
          <span class="theme-icon" :class="{ rotated: appStore.dark }">
            <Moon v-if="!appStore.dark" />
            <Sunny v-else />
          </span>
        </button>
        <a
          class="github-btn"
          href="https://github.com/GuoHuaijian/SlothBoot"
          target="_blank"
          rel="noopener noreferrer"
        >
          <svg width="16" height="16" viewBox="0 0 16 16" fill="currentColor">
            <path d="M8 0C3.58 0 0 3.58 0 8c0 3.54 2.29 6.53 5.47 7.59.4.07.55-.17.55-.38 0-.19-.01-.82-.01-1.49-2.01.37-2.53-.49-2.69-.94-.09-.23-.48-.94-.82-1.13-.28-.15-.68-.52-.01-.53.63-.01 1.08.58 1.23.82.72 1.21 1.87.87 2.33.66.07-.52.28-.87.51-1.07-1.78-.2-3.64-.89-3.64-3.95 0-.87.31-1.59.82-2.15-.08-.2-.36-1.02.08-2.12 0 0 .67-.21 2.2.82.64-.18 1.32-.27 2-.27.68 0 1.36.09 2 .27 1.53-1.04 2.2-.82 2.2-.82.44 1.1.16 1.92.08 2.12.51.56.82 1.27.82 2.15 0 3.07-1.87 3.75-3.65 3.95.29.25.54.73.54 1.48 0 1.07-.01 1.93-.01 2.2 0 .21.15.46.55.38A8.013 8.013 0 0016 8c0-4.42-3.58-8-8-8z"/>
          </svg>
          <span>GitHub</span>
        </a>
        <button
          class="hamburger"
          :class="{ open: mobileMenuOpen }"
          @click="mobileMenuOpen = !mobileMenuOpen"
          aria-label="菜单"
        >
          <span></span>
          <span></span>
          <span></span>
        </button>
      </div>
    </div>

    <Transition name="mobile-slide">
      <nav v-if="mobileMenuOpen" class="mobile-nav">
        <a
          v-for="link in links"
          :key="link.path"
          :class="['mobile-link', { active: isActive(link.path) }]"
          @click.prevent="navigate(link.path)"
        >
          {{ link.label }}
        </a>
      </nav>
    </Transition>
  </header>
</template>

<style scoped>
.navbar {
  position: sticky;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  background: var(--bg-card);
  backdrop-filter: blur(16px) saturate(180%);
  border-bottom: 1px solid var(--border);
}

.navbar-inner {
  display: flex;
  align-items: center;
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 28px;
  height: 60px;
}

/* Logo */
.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  text-decoration: none;
  margin-right: 40px;
  white-space: nowrap;
}

.logo-icon {
  font-size: 22px;
  line-height: 1;
}

.logo-text {
  font-family: var(--font-display);
  font-size: 17px;
  font-weight: 700;
  color: var(--text-primary);
  letter-spacing: -0.02em;
}

/* Nav links */
.nav-links {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
}

.nav-link {
  position: relative;
  font-size: 14px;
  font-weight: 500;
  color: var(--text-secondary);
  text-decoration: none;
  cursor: pointer;
  padding: 6px 14px;
  border-radius: var(--radius-sm);
  transition: color var(--transition-fast), background var(--transition-fast);
}

.nav-link:hover {
  color: var(--text-primary);
  background: var(--accent-bg);
}

.nav-link.active {
  color: var(--accent);
  background: var(--accent-bg);
}

.active-dot {
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 4px;
  height: 4px;
  border-radius: 50%;
  background: var(--accent);
}

/* Right-side actions */
.nav-actions {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-left: auto;
}

.theme-toggle {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 34px;
  height: 34px;
  border: 1px solid var(--border);
  border-radius: var(--radius-full);
  background: var(--bg-raised);
  color: var(--text-secondary);
  cursor: pointer;
  overflow: hidden;
  transition: all var(--transition-fast);
}

.theme-toggle:hover {
  border-color: var(--accent-border);
  color: var(--accent);
  background: var(--accent-bg);
}

.theme-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.4s var(--ease-out);
}

.theme-icon.rotated {
  transform: rotate(180deg);
}

.theme-toggle :deep(svg) {
  width: 16px;
  height: 16px;
}

.github-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 14px;
  border: 1px solid var(--border);
  border-radius: var(--radius-full);
  font-size: 13px;
  font-weight: 500;
  color: var(--text-secondary);
  text-decoration: none;
  background: var(--bg-raised);
  transition: all var(--transition-fast);
}

.github-btn:hover {
  border-color: var(--border-hover);
  color: var(--text-primary);
  background: var(--bg-surface);
}

.github-btn svg {
  flex-shrink: 0;
}

/* Hamburger */
.hamburger {
  display: none;
  flex-direction: column;
  justify-content: center;
  gap: 5px;
  width: 34px;
  height: 34px;
  padding: 8px;
  border: 1px solid var(--border);
  border-radius: var(--radius-sm);
  background: var(--bg-raised);
  cursor: pointer;
}

.hamburger span {
  display: block;
  width: 100%;
  height: 1.5px;
  background: var(--text-secondary);
  border-radius: 1px;
  transition: all 0.3s var(--ease-out);
}

.hamburger.open span:nth-child(1) {
  transform: translateY(6.5px) rotate(45deg);
}

.hamburger.open span:nth-child(2) {
  opacity: 0;
  transform: scaleX(0);
}

.hamburger.open span:nth-child(3) {
  transform: translateY(-6.5px) rotate(-45deg);
}

/* Mobile menu */
.mobile-slide-enter-active,
.mobile-slide-leave-active {
  transition: all 0.3s var(--ease-out);
}

.mobile-slide-enter-from,
.mobile-slide-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}

.mobile-nav {
  display: none;
  flex-direction: column;
  padding: 8px 28px 16px;
  background: var(--bg-card);
  border-bottom: 1px solid var(--border);
}

.mobile-link {
  padding: 10px 14px;
  font-size: 14px;
  font-weight: 500;
  color: var(--text-secondary);
  text-decoration: none;
  cursor: pointer;
  border-radius: var(--radius-sm);
  transition: all var(--transition-fast);
}

.mobile-link:hover {
  color: var(--text-primary);
  background: var(--accent-bg);
}

.mobile-link.active {
  color: var(--accent);
  background: var(--accent-bg);
}

/* Responsive */
@media (max-width: 768px) {
  .nav-links {
    display: none;
  }

  .github-btn {
    display: none;
  }

  .hamburger {
    display: flex;
  }

  .mobile-nav {
    display: flex;
  }
}
</style>
