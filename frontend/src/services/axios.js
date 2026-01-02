// services/axios.js
import axios from 'axios'

const API_URL = import.meta.env.VITE_API_URL + '/api'

const apiClient = axios.create({
    baseURL: API_URL,
    headers: {
        'Content-Type': 'application/json'
    }
})

// Interceptor de peticiones
apiClient.interceptors.request.use(
    (config) => {
        // Obtener token directamente de localStorage
        const token = localStorage.getItem('authToken') ||
            localStorage.getItem('token') ||
            sessionStorage.getItem('authToken')

        if (token) {
            config.headers.Authorization = `Bearer ${token}`
        }

        console.log('📤 Request:', config.method?.toUpperCase(), config.baseURL + config.url)

        return config
    },
    (error) => {
        return Promise.reject(error)
    }
)

// Interceptor de respuestas
apiClient.interceptors.response.use(
    (response) => response,
    async (error) => {
        console.error('❌ Response error:', error.response?.status, error.config?.url)

        if (error.response?.status === 401) {
            // Limpiar almacenamiento
            localStorage.removeItem('authToken')
            localStorage.removeItem('token')
            sessionStorage.removeItem('authToken')

            // Redirigir sin recargar (mejor UX)
            if (!window.location.pathname.includes('/login')) {
                window.location.href = '/login'
            }
        }
        return Promise.reject(error)
    }
)

export default apiClient
