javascript// src/api/http.js
import axios from 'axios'

const http = axios.create({
    baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8081/api',
    timeout: 10000,
    headers: {
        'Content-Type': 'application/json'
    }
})

// ✅ INTERCEPTOR REQUEST: Agrega token automáticamente a cada petición
http.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('auth_token')
        if (token) {
            config.headers.Authorization = `Bearer ${token}`
        }
        console.log('🌐 API Request to:', config.url)
        return config
    },
    (error) => {
        return Promise.reject(error)
    }
)

// ✅ INTERCEPTOR RESPONSE: Maneja respuestas y errores globalmente
http.interceptors.response.use(
    (response) => {
        console.log('✅ API Response:', response.status, response.config.url)
        return response
    },
    (error) => {
        console.error('❌ API Error:', error.response?.status, error.config?.url)

        // Redirige al login si hay error 401 (no autorizado)
        if (error.response?.status === 401) {
            localStorage.removeItem('auth_token')
            window.location.href = '/login'
        }

        return Promise.reject(error)
    }
)

export default http