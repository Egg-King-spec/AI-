import vue from '@vitejs/plugin-vue'
import { defineConfig } from 'vite'
// 引入path模块，用于处理路径
import { resolve } from 'path'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    // 配置路径别名
    alias: {
      '@': resolve(__dirname, 'src'),
    },
  },
})
