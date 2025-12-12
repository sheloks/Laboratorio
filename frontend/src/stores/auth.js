// stores/auth.js
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import authService from '../services/authService'
import apiClient from '../services/axios'

export const useAuthStore = defineStore('auth', () => {
    // State
    const user = ref(null)
    const token = ref(localStorage.getItem('authToken') || null)
    const isAuthenticated = computed(() => !!token.value)

    // Actions
    const setToken = (newToken) => {
        token.value = newToken
        localStorage.setItem('authToken', newToken)
        // Configurar el token en axios
        apiClient.defaults.headers.common['Authorization'] = `Bearer ${newToken}`
    }

    const login = async (email, password) => {
        try {
            const response = await authService.login(email, password)

            if (response.token) {
                // Spring Boot debería devolver el JWT token
                setToken(response.token)
                user.value = response.user

                // Guardar usuario en localStorage
                localStorage.setItem('user', JSON.stringify(response.user))

                return { success: true }
            } else {
                return {
                    success: false,
                    error: response.message || 'Error en el login'
                }
            }
        } catch (error) {
            console.error('Error en login:', error)
            return {
                success: false,
                error: error.response?.data?.message || 'Error al iniciar sesión'
            }
        }
    }

    const register = async (registerData) => {
        try {
            const response = await authService.register(registerData)

            if (response.token) {
                setToken(response.token)
                user.value = response.user
                localStorage.setItem('user', JSON.stringify(response.user))

                return { success: true }
            } else {
                return {
                    success: false,
                    error: response.message || 'Error en el registro'
                }
            }
        } catch (error) {
            console.error('Error en registro:', error)
            return {
                success: false,
                error: error.response?.data?.message || 'Error al registrarse'
            }
        }
    }

    const logout = async () => {
        try {
            // Llamar al backend para invalidar el token (opcional)
            await authService.logout()
        } catch (error) {
            console.error('Error al hacer logout en el servidor:', error)
        } finally {
            // Limpiar estado local siempre
            user.value = null
            token.value = null
            localStorage.removeItem('authToken')
            localStorage.removeItem('user')
            delete apiClient.defaults.headers.common['Authorization']
        }
    }

    const checkAuth = () => {
        const savedToken = localStorage.getItem('authToken')
        const savedUser = localStorage.getItem('user')

        if (savedToken) {
            try {
                setToken(savedToken)
                if (savedUser) {
                    user.value = JSON.parse(savedUser)
                }
                return true
            } catch (error) {
                console.error('Error al recuperar datos del localStorage:', error)
                logout()
                return false
            }
        }
        return false
    }

    // Verificar si el token JWT ha expirado
    const isTokenExpired = () => {
        if (!token.value) return true

        try {
            // Decodificar el JWT (sin verificar la firma)
            const base64Url = token.value.split('.')[1]
            const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/')
            const payload = JSON.parse(window.atob(base64))

            // Verificar si ha expirado (exp está en segundos)
            const now = Date.now() / 1000
            return payload.exp < now
        } catch (error) {
            console.error('Error al decodificar token:', error)
            return true
        }
    }

    // Inicializar estado desde localStorage
    checkAuth()

    return {
        // State
        user,
        token,
        isAuthenticated,

        // Actions
        login,
        register,
        logout,
        checkAuth,
        isTokenExpired,
        setToken
    }
})