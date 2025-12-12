import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'

export default defineConfig({
    plugins: [vue()],
    resolve: {
        alias: {
            '@': fileURLToPath(new URL('./src', import.meta.url))
        },
        extensions: ['.mjs', '.js', '.ts', '.jsx', '.tsx', '.json', '.vue']
    },
    server: {
        port: 3000,
        proxy: {
            '/api': {
                target: 'http://localhost:8081',
                changeOrigin: true
            }
        }
    },
    define: {
        'import.meta.env.VITE_API_URL': JSON.stringify('http://localhost:8081'),
        'import.meta.env.VITE_APP_NAME': JSON.stringify('Laboratorio Dental'),
        'import.meta.env.VITE_APP_VERSION': JSON.stringify('1.0.0'),
        'import.meta.env.VITE_MODE': JSON.stringify(process.env.NODE_ENV || 'development')
    }
})