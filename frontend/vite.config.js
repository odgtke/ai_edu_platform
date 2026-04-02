import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 3000,
    open: true,
    proxy: {
      '/api': {
        target: 'http://localhost:8081',
        changeOrigin: true,
        secure: false
      },
      '/users': {
        target: 'http://localhost:8081',
        changeOrigin: true,
        secure: false
      },
      '/courses': {
        target: 'http://localhost:8081',
        changeOrigin: true,
        secure: false
      },
      '/auth': {
        target: 'http://localhost:8081',
        changeOrigin: true,
        secure: false
      },
      '/agent': {
        target: 'http://localhost:8081',
        changeOrigin: true,
        secure: false
      },
      '/langchain': {
        target: 'http://localhost:8081',
        changeOrigin: true,
        secure: false
      },
      '/learning': {
        target: 'http://localhost:8081',
        changeOrigin: true,
        secure: false
      }
    }
  },
  build: {
    outDir: 'dist',
    assetsDir: 'assets',
    rollupOptions: {
      input: {
        main: resolve(__dirname, 'index.html')
      }
    }
  },
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    }
  }
})