<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { modules } from '@/data/modules'

const router = useRouter()

const featureCards = [
  { icon: '&#xe61a;', emoji: '&#x1F9E9;', title: '模块化架构', desc: '24个模块按需引入，避免臃肿依赖，精准掌控每一份依赖' },
  { icon: '&#xe624;', emoji: '&#x2699;', title: '统一配置', desc: '所有能力通过 sloth.* 命名空间统一管理，一处配置全局生效' },
  { icon: '&#xe61c;', emoji: '&#x26A1;', title: '高频场景', desc: '分布式锁、限流、幂等、缓存等开箱即用，拒绝重复造轮子' },
  { icon: '&#xe618;', emoji: '&#x1F527;', title: '自动装配', desc: '@ConditionalOnMissingBean SPI 设计，零侵入扩展机制' },
  { icon: '&#xe620;', emoji: '&#x1F916;', title: 'AI 集成', desc: 'Spring AI 一站式封装：对话、流式、向量、图像生成全覆盖' },
  { icon: '&#xe62c;', emoji: '&#x1F512;', title: '企业安全', desc: 'AES/SM4/RSA 加密、XSS 防护、数据脱敏，安全能力一站集成' },
]

const techStack = [
  { category: '语言', tech: 'Java 21' },
  { category: '框架', tech: 'Spring Boot 3.5.0' },
  { category: '微服务', tech: 'Spring Cloud 2025 / Alibaba 2025.0.0.0' },
  { category: 'AI', tech: 'Spring AI 1.1.4' },
  { category: '数据层', tech: 'MyBatis-Plus 3.5.6' },
  { category: '缓存 / MQ', tech: 'Redisson 3.27.0 / RocketMQ 2.3.0' },
  { category: '任务 / 文件', tech: 'XXL-Job 2.4.1 / EasyExcel 3.3.4' },
  { category: '网关 / RPC', tech: 'Spring Cloud Gateway / OpenFeign' },
  { category: '文档 / 监控', tech: 'Knife4j 4.5.0 / Micrometer' },
]

const quickstartSteps = [
  { title: '克隆项目', code: 'git clone https://github.com/GuoHuaijian/SlothBoot.git' },
  { title: '构建项目', code: 'mvn clean install -DskipTests' },
  { title: '添加依赖', code: `<dependency>\n    <groupId>com.sloth</groupId>\n    <artifactId>sloth-starter-redis</artifactId>\n</dependency>` },
  { title: '配置', code: `sloth:\n  web:\n    enabled: true\n  redis:\n    enabled: true\n    mode: single\n    address: 127.0.0.1:6379` },
  { title: '启动', code: `@SpringBootApplication\npublic class App {\n    public static void main(String[] args) {\n        SpringApplication.run(App.class, args);\n    }\n}` },
]

const displayedModules = computed(() => modules.slice(0, 12))

// Scroll-triggered animation
const observedSections = ref<Set<string>>(new Set())
let observer: IntersectionObserver | null = null

onMounted(() => {
  observer = new IntersectionObserver(
    (entries) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          observedSections.value.add(entry.target.id)
        }
      })
    },
    { threshold: 0.1 }
  )

  document.querySelectorAll('.lp-section[id]').forEach((el) => {
    observer!.observe(el)
  })
})

onUnmounted(() => {
  observer?.disconnect()
})

function isSectionVisible(id: string) {
  return observedSections.value.has(id)
}

function scrollToQuickstart() {
  document.getElementById('quickstart')?.scrollIntoView({ behavior: 'smooth' })
}

function openGitHub() {
  window.open('https://github.com/GuoHuaijian/SlothBoot', '_blank')
}
</script>

<template>
  <div class="landing-page">
    <!-- ===== Hero ===== -->
    <section class="hero grain">
      <div class="hero-bg">
        <div class="mesh-1"></div>
        <div class="mesh-2"></div>
        <div class="mesh-3"></div>
      </div>

      <div class="hero-content">
        <div class="hero-badge animate-fade-in-up">
          <span class="badge-dot"></span>
          企业级微服务脚手架
        </div>
        <h1 class="hero-title animate-fade-in-up stagger-1">
          <span class="sloth-emoji">&#x1F9A5;</span>
          Sloth Boot
        </h1>
        <p class="hero-subtitle animate-fade-in-up stagger-2">
          Spring Cloud Alibaba 微服务一站式解决方案
        </p>
        <p class="hero-tagline animate-fade-in-up stagger-3">
          慢即是快，稳即是远 — 不过度工程化，只交付扎实的可复用工程基础
        </p>
        <div class="hero-actions animate-fade-in-up stagger-4">
          <button class="btn-primary" @click="scrollToQuickstart">
            快速开始
            <span class="btn-shine"></span>
          </button>
          <button class="btn-ghost" @click="openGitHub">
            <svg width="16" height="16" viewBox="0 0 16 16" fill="currentColor">
              <path d="M8 0C3.58 0 0 3.58 0 8c0 3.54 2.29 6.53 5.47 7.59.4.07.55-.17.55-.38 0-.19-.01-.82-.01-1.49-2.01.37-2.53-.49-2.69-.94-.09-.23-.48-.94-.82-1.13-.28-.15-.68-.52-.01-.53.63-.01 1.08.58 1.23.82.72 1.21 1.87.87 2.33.66.07-.52.28-.87.51-1.07-1.78-.2-3.64-.89-3.64-3.95 0-.87.31-1.59.82-2.15-.08-.2-.36-1.02.08-2.12 0 0 .67-.21 2.2.82.64-.18 1.32-.27 2-.27.68 0 1.36.09 2 .27 1.53-1.04 2.2-.82 2.2-.82.44 1.1.16 1.92.08 2.12.51.56.82 1.27.82 2.15 0 3.07-1.87 3.75-3.65 3.95.29.25.54.73.54 1.48 0 1.07-.01 1.93-.01 2.2 0 .21.15.46.55.38A8.013 8.013 0 0016 8c0-4.42-3.58-8-8-8z"/>
            </svg>
            GitHub
          </button>
        </div>

        <div class="hero-stats animate-fade-in-up stagger-5">
          <div class="stat">
            <span class="stat-value">24+</span>
            <span class="stat-label">模块</span>
          </div>
          <div class="stat-divider"></div>
          <div class="stat">
            <span class="stat-value">9+</span>
            <span class="stat-label">技术栈</span>
          </div>
          <div class="stat-divider"></div>
          <div class="stat">
            <span class="stat-value">6</span>
            <span class="stat-label">在线演示</span>
          </div>
        </div>
      </div>

      <!-- Decorative grid -->
      <div class="hero-grid" aria-hidden="true"></div>
    </section>

    <!-- ===== Features ===== -->
    <section
      id="features"
      class="lp-section features"
      :class="{ visible: isSectionVisible('features') }"
    >
      <h2 class="section-heading">核心特性</h2>
      <p class="section-desc">面向生产环境的设计原则，每一项能力都经过实际项目验证</p>
      <div class="features-grid">
        <div
          v-for="(card, i) in featureCards"
          :key="i"
          class="feature-card"
          :style="{ animationDelay: `${i * 0.08}s` }"
        >
          <div class="feature-icon-wrapper">
            <span class="feature-emoji" v-html="card.emoji"></span>
          </div>
          <h3 class="feature-title">{{ card.title }}</h3>
          <p class="feature-desc">{{ card.desc }}</p>
        </div>
      </div>
    </section>

    <!-- ===== Tech Stack ===== -->
    <section
      id="tech-stack"
      class="lp-section tech-stack"
      :class="{ visible: isSectionVisible('tech-stack') }"
    >
      <h2 class="section-heading">技术选型</h2>
      <p class="section-desc">基于最新稳定版本构建，拥抱现代 Java 生态</p>
      <div class="tech-table">
        <div
          v-for="(row, i) in techStack"
          :key="i"
          class="tech-row"
          :class="{ 'tech-row-even': i % 2 === 0 }"
          :style="{ animationDelay: `${i * 0.05}s` }"
        >
          <span class="tech-category">{{ row.category }}</span>
          <span class="tech-name">{{ row.tech }}</span>
        </div>
      </div>
    </section>

    <!-- ===== Modules ===== -->
    <section
      id="modules"
      class="lp-section modules"
      :class="{ visible: isSectionVisible('modules') }"
    >
      <h2 class="section-heading">模块总览</h2>
      <p class="section-desc">按需引入，精准掌控每一份依赖</p>
      <div class="modules-grid">
        <div
          v-for="(mod, i) in displayedModules"
          :key="mod.name"
          class="module-card"
          :style="{ animationDelay: `${i * 0.06}s` }"
        >
          <div class="module-header">
            <span class="module-name-tag">{{ mod.name }}</span>
            <button
              v-if="mod.demoRoute"
              class="btn-demo"
              @click="router.push(mod.demoRoute!)"
            >Demo &rarr;</button>
          </div>
          <h3 class="module-title">{{ mod.title }}</h3>
          <p class="module-desc">{{ mod.description }}</p>
        </div>
      </div>
      <div class="modules-cta">
        <router-link to="/modules" class="btn-link">
          查看全部模块 &rarr;
        </router-link>
      </div>
    </section>

    <!-- ===== Quick Start ===== -->
    <section
      id="quickstart"
      class="lp-section quickstart"
      :class="{ visible: isSectionVisible('quickstart') }"
    >
      <h2 class="section-heading">快速开始</h2>
      <p class="section-desc">五步启动你的微服务项目</p>
      <div class="steps">
        <div
          v-for="(step, i) in quickstartSteps"
          :key="i"
          class="step"
          :style="{ animationDelay: `${i * 0.1}s` }"
        >
          <div class="step-header">
            <div class="step-number">{{ i + 1 }}</div>
            <h3 class="step-title">{{ step.title }}</h3>
          </div>
          <pre class="step-code"><code>{{ step.code }}</code></pre>
        </div>
      </div>
    </section>

    <!-- ===== Community ===== -->
    <section
      id="community"
      class="lp-section community"
      :class="{ visible: isSectionVisible('community') }"
    >
      <div class="community-card">
        <h2 class="community-title">加入开源社区</h2>
        <p class="community-desc">Sloth Boot 是一个开源项目，欢迎 Star、Fork 和贡献代码</p>
        <div class="badges">
          <a href="https://github.com/GuoHuaijian/SlothBoot" target="_blank" rel="noopener">
            <img src="https://img.shields.io/github/stars/GuoHuaijian/SlothBoot?style=flat-square&logo=github&color=d97706" alt="Stars" />
          </a>
          <a href="https://github.com/GuoHuaijian/SlothBoot" target="_blank" rel="noopener">
            <img src="https://img.shields.io/github/forks/GuoHuaijian/SlothBoot?style=flat-square&logo=github&color=d97706" alt="Forks" />
          </a>
          <a href="https://github.com/GuoHuaijian/SlothBoot/issues" target="_blank" rel="noopener">
            <img src="https://img.shields.io/github/issues/GuoHuaijian/SlothBoot?style=flat-square&logo=github&color=d97706" alt="Issues" />
          </a>
          <a href="https://github.com/GuoHuaijian/SlothBoot/blob/main/LICENSE" target="_blank" rel="noopener">
            <img src="https://img.shields.io/github/license/GuoHuaijian/SlothBoot?style=flat-square&color=d97706" alt="License" />
          </a>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped>
.landing-page {
  width: 100%;
  background: var(--bg-base);
  color: var(--text-primary);
  overflow: hidden;
}

/* Scoped animation keyframes */
@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(20px); }
  to   { opacity: 1; transform: translateY(0); }
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50%      { transform: translateY(-8px); }
}

@keyframes pulse-glow {
  0%, 100% { opacity: 0.5; }
  50%      { opacity: 1; }
}

.animate-fade-in-up {
  animation: fadeInUp 0.6s var(--ease-out) both;
}

.stagger-1 { animation-delay: 0.1s; }
.stagger-2 { animation-delay: 0.2s; }
.stagger-3 { animation-delay: 0.3s; }
.stagger-4 { animation-delay: 0.4s; }
.stagger-5 { animation-delay: 0.5s; }

/* ============================================================
   Section shared
   ============================================================ */
.lp-section {
  max-width: 1200px;
  margin: 0 auto;
  padding: 100px 28px;
}

.lp-section.visible .section-heading,
.lp-section.visible .section-desc,
.lp-section.visible .feature-card,
.lp-section.visible .tech-row,
.lp-section.visible .module-card,
.lp-section.visible .step,
.lp-section.visible .community-card {
  animation: fadeInUp 0.6s var(--ease-out) both;
}

.section-heading {
  text-align: center;
  font-family: var(--font-display);
  font-size: 32px;
  font-weight: 800;
  letter-spacing: -0.02em;
  margin: 0 0 10px;
  color: var(--text-primary);
}

.section-desc {
  text-align: center;
  font-size: 15px;
  color: var(--text-secondary);
  margin: 0 0 52px;
  max-width: 500px;
  margin-left: auto;
  margin-right: auto;
}

/* ============================================================
   Hero
   ============================================================ */
.hero {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 90vh;
  padding: 100px 28px 80px;
  overflow: hidden;
}

.hero-bg {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.mesh-1 {
  position: absolute;
  width: 600px;
  height: 600px;
  border-radius: 50%;
  background: radial-gradient(circle, var(--accent-glow), transparent 70%);
  top: -200px;
  right: -100px;
  opacity: 0.5;
  animation: float 12s ease-in-out infinite;
}

.mesh-2 {
  position: absolute;
  width: 400px;
  height: 400px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(245, 158, 11, 0.08), transparent 70%);
  bottom: -100px;
  left: -50px;
  opacity: 0.6;
  animation: float 10s ease-in-out infinite 3s;
}

.mesh-3 {
  position: absolute;
  width: 300px;
  height: 300px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(251, 191, 36, 0.06), transparent 70%);
  top: 40%;
  left: 50%;
  opacity: 0.4;
  animation: float 8s ease-in-out infinite 1.5s;
}

.hero-grid {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(var(--border) 1px, transparent 1px),
    linear-gradient(90deg, var(--border) 1px, transparent 1px);
  background-size: 60px 60px;
  opacity: 0.3;
  mask-image: radial-gradient(ellipse 60% 60% at 50% 40%, black 0%, transparent 100%);
  -webkit-mask-image: radial-gradient(ellipse 60% 60% at 50% 40%, black 0%, transparent 100%);
}

.hero-content {
  position: relative;
  z-index: 1;
  text-align: center;
  max-width: 720px;
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 6px 16px;
  background: var(--accent-bg);
  border: 1px solid var(--accent-border);
  border-radius: var(--radius-full);
  font-size: 13px;
  font-weight: 500;
  color: var(--accent);
  margin-bottom: 24px;
}

.badge-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: var(--accent);
  animation: pulse-glow 2s ease-in-out infinite;
}

.hero-title {
  font-family: var(--font-display);
  font-size: 64px;
  font-weight: 800;
  letter-spacing: -0.03em;
  margin: 0;
  line-height: 1.1;
  color: var(--text-primary);
}

.sloth-emoji {
  display: inline-block;
  animation: float 4s ease-in-out infinite;
  margin-right: 4px;
}

.hero-subtitle {
  font-size: 20px;
  font-weight: 500;
  color: var(--text-secondary);
  margin: 16px 0 0;
}

.hero-tagline {
  font-size: 15px;
  color: var(--text-muted);
  margin: 8px 0 0;
  font-style: italic;
}

.hero-actions {
  display: flex;
  gap: 14px;
  justify-content: center;
  margin-top: 40px;
}

.btn-primary {
  position: relative;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 14px 32px;
  border: none;
  background: linear-gradient(135deg, var(--accent), var(--accent-light));
  color: #fff;
  font-size: 15px;
  font-weight: 600;
  font-family: var(--font-display);
  border-radius: var(--radius-full);
  cursor: pointer;
  overflow: hidden;
  transition: all var(--transition-normal);
  box-shadow: 0 4px 20px var(--accent-glow);
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 30px var(--accent-glow);
}

.btn-shine {
  position: absolute;
  inset: 0;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.15), transparent);
  transform: translateX(-100%);
  transition: transform 0.6s var(--ease-out);
}

.btn-primary:hover .btn-shine {
  transform: translateX(100%);
}

.btn-ghost {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 14px 28px;
  border: 1px solid var(--border-strong);
  background: var(--bg-card);
  backdrop-filter: blur(8px);
  color: var(--text-primary);
  font-size: 15px;
  font-weight: 600;
  font-family: var(--font-display);
  border-radius: var(--radius-full);
  cursor: pointer;
  transition: all var(--transition-normal);
}

.btn-ghost:hover {
  border-color: var(--accent-border);
  background: var(--accent-bg);
  color: var(--accent);
  transform: translateY(-2px);
}

.hero-stats {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 32px;
  margin-top: 56px;
}

.stat {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.stat-value {
  font-family: var(--font-display);
  font-size: 28px;
  font-weight: 800;
  color: var(--accent);
}

.stat-label {
  font-size: 13px;
  color: var(--text-muted);
}

.stat-divider {
  width: 1px;
  height: 32px;
  background: var(--border);
}

/* ============================================================
   Features
   ============================================================ */
.features-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.feature-card {
  background: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: var(--radius-lg);
  padding: 28px;
  backdrop-filter: blur(8px);
  transition: all var(--transition-normal);
}

.feature-card:hover {
  border-color: var(--accent-border);
  box-shadow: var(--shadow-glow);
  transform: translateY(-4px);
}

.feature-icon-wrapper {
  width: 44px;
  height: 44px;
  border-radius: var(--radius-md);
  background: var(--accent-bg);
  border: 1px solid var(--accent-border);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 18px;
  font-size: 22px;
  line-height: 1;
  transition: all var(--transition-normal);
}

.feature-card:hover .feature-icon-wrapper {
  background: var(--accent-soft);
  transform: scale(1.1);
}

.feature-emoji {
  font-style: normal;
}

.feature-title {
  font-family: var(--font-display);
  font-size: 16px;
  font-weight: 700;
  margin: 0 0 8px;
  color: var(--text-primary);
}

.feature-desc {
  font-size: 14px;
  color: var(--text-secondary);
  margin: 0;
  line-height: 1.7;
}

/* ============================================================
   Tech Stack
   ============================================================ */
.tech-table {
  max-width: 700px;
  margin: 0 auto;
  border: 1px solid var(--border);
  border-radius: var(--radius-lg);
  overflow: hidden;
  background: var(--bg-card);
}

.tech-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 24px;
  font-size: 14px;
  border-bottom: 1px solid var(--border);
  transition: background var(--transition-fast);
}

.tech-row:last-child {
  border-bottom: none;
}

.tech-row:hover {
  background: var(--accent-bg);
}

.tech-row-even {
  background: var(--bg-raised);
}

.tech-row-even:hover {
  background: var(--accent-bg);
}

.tech-category {
  color: var(--text-muted);
  font-weight: 500;
  min-width: 120px;
}

.tech-name {
  color: var(--text-primary);
  font-family: var(--font-mono);
  font-size: 13px;
  text-align: right;
}

/* ============================================================
   Modules
   ============================================================ */
.modules-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.module-card {
  background: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: var(--radius-lg);
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 8px;
  backdrop-filter: blur(8px);
  transition: all var(--transition-normal);
}

.module-card:hover {
  border-color: var(--accent-border);
  box-shadow: var(--shadow-glow);
  transform: translateY(-3px);
}

.module-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.module-name-tag {
  font-family: var(--font-mono);
  font-size: 12px;
  color: var(--text-muted);
  background: var(--bg-raised);
  padding: 2px 8px;
  border-radius: 4px;
}

.module-title {
  font-family: var(--font-display);
  font-size: 16px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0;
}

.module-desc {
  font-size: 13px;
  color: var(--text-secondary);
  margin: 0;
  line-height: 1.7;
  flex: 1;
}

.btn-demo {
  padding: 4px 12px;
  border-radius: var(--radius-sm);
  border: 1px solid var(--accent-border);
  background: var(--accent-bg);
  color: var(--accent);
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: all var(--transition-fast);
}

.btn-demo:hover {
  background: var(--accent-soft);
  border-color: var(--accent);
}

.modules-cta {
  text-align: center;
  margin-top: 32px;
}

.btn-link {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  font-weight: 600;
  color: var(--accent);
  text-decoration: none;
  padding: 8px 0;
  border-bottom: 2px solid transparent;
  transition: all var(--transition-fast);
}

.btn-link:hover {
  border-bottom-color: var(--accent);
}

/* ============================================================
   Quick Start
   ============================================================ */
.steps {
  max-width: 680px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.step {
  background: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: var(--radius-lg);
  padding: 24px;
  backdrop-filter: blur(8px);
  transition: all var(--transition-normal);
}

.step:hover {
  border-color: var(--accent-border);
  box-shadow: var(--shadow-glow);
}

.step-header {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 16px;
}

.step-number {
  width: 32px;
  height: 32px;
  border-radius: var(--radius-full);
  background: linear-gradient(135deg, var(--accent), var(--accent-light));
  color: #fff;
  font-family: var(--font-display);
  font-size: 14px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.step-title {
  font-family: var(--font-display);
  font-size: 16px;
  font-weight: 700;
  margin: 0;
  color: var(--text-primary);
}

.step-code {
  margin: 0;
  padding: 16px;
  background: var(--bg-raised);
  border: 1px solid var(--border);
  border-radius: var(--radius-md);
  font-family: var(--font-mono);
  font-size: 13px;
  line-height: 1.7;
  color: var(--text-primary);
  overflow-x: auto;
  white-space: pre;
}

.step-code code {
  background: transparent;
  padding: 0;
  color: inherit;
  font-size: inherit;
}

/* ============================================================
   Community
   ============================================================ */
.community {
  padding-bottom: 60px;
}

.community-card {
  text-align: center;
  background: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: var(--radius-xl);
  padding: 60px 40px;
  backdrop-filter: blur(8px);
}

.community-title {
  font-family: var(--font-display);
  font-size: 28px;
  font-weight: 800;
  margin: 0 0 10px;
  color: var(--text-primary);
}

.community-desc {
  font-size: 15px;
  color: var(--text-secondary);
  margin: 0 0 32px;
}

.badges {
  display: flex;
  justify-content: center;
  gap: 12px;
  flex-wrap: wrap;
}

.badges img {
  height: 26px;
  border-radius: 4px;
  transition: transform var(--transition-fast);
}

.badges img:hover {
  transform: translateY(-2px);
}

/* ============================================================
   Responsive
   ============================================================ */
@media (max-width: 1024px) {
  .features-grid,
  .modules-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .hero-title {
    font-size: 48px;
  }
}

@media (max-width: 768px) {
  .hero {
    min-height: auto;
    padding: 80px 20px 60px;
  }

  .hero-title {
    font-size: 36px;
  }

  .hero-subtitle {
    font-size: 17px;
  }

  .hero-actions {
    flex-direction: column;
    align-items: center;
  }

  .hero-stats {
    gap: 20px;
  }

  .stat-value {
    font-size: 22px;
  }

  .lp-section {
    padding: 60px 20px;
  }

  .section-heading {
    font-size: 26px;
  }

  .features-grid,
  .modules-grid {
    grid-template-columns: 1fr;
  }

  .community-card {
    padding: 40px 24px;
  }
}
</style>
