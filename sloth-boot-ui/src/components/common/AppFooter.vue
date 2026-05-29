<template>
  <footer class="footer">
    <div class="footer-divider"></div>
    <div class="footer-inner">
      <div class="footer-brand">
        <span class="footer-logo">🦥 Sloth Boot</span>
        <p class="footer-tagline">慢即是快，稳即是远</p>
      </div>
      <div class="footer-links">
        <div class="link-group">
          <h4 class="link-title">资源</h4>
          <a href="https://github.com/GuoHuaijian/SlothBoot" target="_blank" rel="noopener noreferrer">GitHub</a>
          <router-link to="/docs/architecture">架构文档</router-link>
          <router-link to="/docs/configuration">配置参考</router-link>
        </div>
        <div class="link-group">
          <h4 class="link-title">在线体验</h4>
          <router-link to="/demo/auth">认证授权</router-link>
          <router-link to="/demo/ai">AI 助手</router-link>
          <router-link to="/demo/monitor">系统监控</router-link>
        </div>
      </div>
    </div>
    <div class="footer-bottom">
      <p class="copyright">&copy; 2025 Sloth Boot. Built with Spring Cloud Alibaba.</p>
    </div>
  </footer>
</template>

<style scoped>
.footer {
  padding: 0;
  background: var(--bg-raised);
}

.footer-divider {
  height: 1px;
  background: linear-gradient(90deg, transparent, var(--accent-border), transparent);
}

.footer-inner {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  max-width: 1280px;
  margin: 0 auto;
  padding: 48px 28px 32px;
  gap: 48px;
}

.footer-brand {
  flex-shrink: 0;
}

.footer-logo {
  font-family: var(--font-display);
  font-size: 18px;
  font-weight: 700;
  color: var(--text-primary);
}

.footer-tagline {
  margin: 6px 0 0;
  font-size: 13px;
  color: var(--text-muted);
  font-style: italic;
}

.footer-links {
  display: flex;
  gap: 64px;
}

.link-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.link-title {
  font-family: var(--font-display);
  font-size: 13px;
  font-weight: 600;
  color: var(--text-secondary);
  text-transform: uppercase;
  letter-spacing: 0.05em;
  margin-bottom: 4px;
}

.link-group a {
  font-size: 13px;
  color: var(--text-muted);
  text-decoration: none;
  transition: color var(--transition-fast);
}

.link-group a:hover {
  color: var(--accent);
}

.footer-bottom {
  border-top: 1px solid var(--border);
  text-align: center;
  padding: 16px 28px;
}

.copyright {
  color: var(--text-muted);
  font-size: 12px;
  margin: 0;
}

@media (max-width: 768px) {
  .footer-inner {
    flex-direction: column;
    padding: 32px 20px 24px;
    gap: 24px;
  }

  .footer-links {
    gap: 40px;
  }
}
</style>
