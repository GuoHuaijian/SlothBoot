import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

export default defineConfig({
  base: '/SlothBoot/',
  plugins: [vue()],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src'),
    },
  },
  build: {
    outDir: 'dist',
    sourcemap: false,
    rollupOptions: {
      output: {
        manualChunks(id: string) {
          if (id.includes('element-plus')) return 'element-plus'
          if (id.includes('node_modules/vue') || id.includes('node_modules/vue-router') || id.includes('node_modules/pinia') || id.includes('node_modules/axios')) return 'vendor'
        },
      },
    },
  },
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
})
