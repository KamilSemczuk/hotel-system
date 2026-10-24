import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// W trybie dev zapytania /api/* przekazujemy do Springa (bez problemów z CORS)
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': 'http://localhost:8080',
    },
  },
})
