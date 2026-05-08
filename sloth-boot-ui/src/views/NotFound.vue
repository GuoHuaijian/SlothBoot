<template>
  <div class="not-found">
    <div class="nf-orbs">
      <div class="orb orb-1"></div>
      <div class="orb orb-2"></div>
    </div>
    <div class="nf-content">
      <h1 class="nf-code">404</h1>
      <p class="nf-subtitle">页面走丢了</p>
      <p class="nf-desc">看起来这个页面不存在，或者已经被移动到其他地方</p>
      <router-link to="/" class="nf-btn">
        返回首页
        <span class="btn-arrow">&rarr;</span>
      </router-link>
    </div>
  </div>
</template>

<style scoped>
.not-found {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 70vh;
  text-align: center;
  overflow: hidden;
}

.nf-orbs {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.15;
}

.orb-1 {
  width: 300px;
  height: 300px;
  background: var(--accent);
  top: 10%;
  left: 20%;
  animation: float 8s ease-in-out infinite;
}

.orb-2 {
  width: 200px;
  height: 200px;
  background: var(--accent-light);
  bottom: 20%;
  right: 15%;
  animation: float 6s ease-in-out infinite 2s;
}

.nf-content {
  position: relative;
  z-index: 1;
  animation: fadeInUp 0.8s var(--ease-out);
}

.nf-code {
  font-family: var(--font-display);
  font-size: 120px;
  font-weight: 800;
  line-height: 1;
  margin: 0;
  background: linear-gradient(135deg, var(--accent), var(--accent-light));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.nf-subtitle {
  font-family: var(--font-display);
  font-size: 24px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 16px 0 8px;
}

.nf-desc {
  font-size: 14px;
  color: var(--text-muted);
  margin: 0 0 32px;
  max-width: 400px;
}

.nf-btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 12px 28px;
  background: linear-gradient(135deg, var(--accent), var(--accent-light));
  color: #fff;
  font-size: 14px;
  font-weight: 600;
  border-radius: var(--radius-full);
  text-decoration: none;
  transition: all var(--transition-normal);
  box-shadow: 0 4px 15px var(--accent-glow);
}

.nf-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px var(--accent-glow);
  color: #fff;
}

.btn-arrow {
  transition: transform var(--transition-fast);
}

.nf-btn:hover .btn-arrow {
  transform: translateX(4px);
}
</style>
