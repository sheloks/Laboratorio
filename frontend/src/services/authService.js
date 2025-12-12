// services/authService.js
// Servicio para comunicación con backend Spring Boot

const API_URL = '/api';

class AuthService {
    constructor() {
        this.baseURL = API_URL;
    }

    // Headers comunes para todas las requests
    getHeaders(includeAuth = true) {
        const headers = {
            'Content-Type': 'application/json',
        };

        if (includeAuth) {
            const token = localStorage.getItem('token');
            if (token) {
                headers['Authorization'] = `Bearer ${token}`;
            }
        }

        return headers;
    }

    // Manejo de errores centralizado
    handleError(error, fallbackMessage = 'Error en la operación') {
        console.error('Error en authService:', error);

        if (error.response) {
            const serverError = error.response.data;
            throw new Error(serverError.message || serverError.error || fallbackMessage);
        } else if (error.request) {
            throw new Error('Error de conexión con el servidor. Verifica que el backend esté ejecutándose.');
        } else {
            throw new Error(error.message || fallbackMessage);
        }
    }

    // Login
    async login(email, password) {
        try {
            console.log('🔐 Intentando login con:', { email });

            const response = await fetch(`${this.baseURL}/auth/login`, {
                method: 'POST',
                headers: this.getHeaders(false),
                body: JSON.stringify({ email, password })
            });

            const data = await response.json();

            if (!response.ok) {
                throw new Error(data.message || data.error || 'Error en el login');
            }

            console.log('✅ Login exitoso:', data);

            // Guardar token en localStorage
            if (data.token) {
                localStorage.setItem('token', data.token);
                localStorage.setItem('user', JSON.stringify(data.user));
            }

            return {
                user: data.user,
                token: data.token
            };

        } catch (error) {
            console.error('❌ Error en login:', error);
            throw error;
        }
    }

    // Register - con mapeo de campos frontend → backend
    async register(userData) {
        try {
            console.log('📝 Intentando registro con:', userData);

            // Mapear campos del frontend a los nombres que espera el backend
            const backendData = {
                email: userData.email,
                password: userData.password,
                nombre: userData.laboratoryName || userData.nombre,  // El backend espera 'nombre'
                laboratorioNombre: userData.laboratoryName || userData.laboratorioNombre,
                telefono: userData.phone || userData.telefono,
                direccion: userData.address || userData.direccion,
                plan: userData.plan,
                cuit: userData.cuit
            };

            console.log('📤 Enviando al backend:', backendData);

            const response = await fetch(`${this.baseURL}/auth/register`, {
                method: 'POST',
                headers: this.getHeaders(false),
                body: JSON.stringify(backendData)
            });

            const data = await response.json();

            if (!response.ok) {
                // Manejar errores de validación del backend
                if (data.errors) {
                    const errorMessages = Object.values(data.errors).join(', ');
                    throw new Error(errorMessages);
                }
                throw new Error(data.message || data.error || 'Error en el registro');
            }

            console.log('✅ Registro exitoso:', data);

            // Guardar token en localStorage
            if (data.token) {
                localStorage.setItem('token', data.token);
                localStorage.setItem('user', JSON.stringify(data.user));
            }

            return {
                user: data.user,
                token: data.token
            };

        } catch (error) {
            console.error('❌ Error en registro:', error);
            throw error;
        }
    }

    // Logout
    async logout() {
        try {
            const token = localStorage.getItem('token');

            if (token) {
                await fetch(`${this.baseURL}/auth/logout`, {
                    method: 'POST',
                    headers: this.getHeaders(true)
                });
            }
        } catch (error) {
            console.error('Error en logout:', error);
        } finally {
            this.clearAuthData();
        }
    }

    // Verificar token
    async verifyToken() {
        try {
            const token = localStorage.getItem('token');
            const user = localStorage.getItem('user');

            if (!token || !user) {
                return false;
            }

            const response = await fetch(`${this.baseURL}/auth/verify`, {
                method: 'GET',
                headers: this.getHeaders(true)
            });

            if (response.ok) {
                const data = await response.json();
                if (data.user) {
                    localStorage.setItem('user', JSON.stringify(data.user));
                }
                return true;
            }

            return false;

        } catch (error) {
            console.error('Error en verifyToken:', error);
            return false;
        }
    }

    // Obtener usuario actual
    getCurrentUser() {
        try {
            const userStr = localStorage.getItem('user');
            return userStr ? JSON.parse(userStr) : null;
        } catch (error) {
            console.error('Error obteniendo usuario actual:', error);
            return null;
        }
    }

    // Obtener token
    getToken() {
        return localStorage.getItem('token');
    }

    // Verificar si está autenticado
    isAuthenticated() {
        return !!this.getToken() && !!this.getCurrentUser();
    }

    // Limpiar datos de autenticación
    clearAuthData() {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
    }

    // Actualizar perfil de usuario
    async updateProfile(userData) {
        try {
            const response = await fetch(`${this.baseURL}/auth/profile`, {
                method: 'PUT',
                headers: this.getHeaders(true),
                body: JSON.stringify(userData)
            });

            const data = await response.json();

            if (!response.ok) {
                throw new Error(data.message || data.error || 'Error actualizando perfil');
            }

            if (data.user) {
                localStorage.setItem('user', JSON.stringify(data.user));
            }

            return data.user;

        } catch (error) {
            console.error('Error en updateProfile:', error);
            throw error;
        }
    }

    // Cambiar contraseña
    async changePassword(currentPassword, newPassword) {
        try {
            const response = await fetch(`${this.baseURL}/auth/change-password`, {
                method: 'POST',
                headers: this.getHeaders(true),
                body: JSON.stringify({ currentPassword, newPassword })
            });

            const data = await response.json();

            if (!response.ok) {
                throw new Error(data.message || data.error || 'Error cambiando contraseña');
            }

            return data;

        } catch (error) {
            console.error('Error en changePassword:', error);
            throw error;
        }
    }
}

const authService = new AuthService();

export default authService;