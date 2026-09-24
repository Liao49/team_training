import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
    port: 8081,
    proxy: {                          // 生产由 Nginx 反代，开发期走代理避免跨域
      '/api': { target: 'http://localhost:8080', changeOrigin: true }
    }
  }
})
