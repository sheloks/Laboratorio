<template>
    <div class="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 flex items-center justify-center p-4">
        <div class="w-full max-w-6xl grid lg:grid-cols-2 gap-8">
            <!-- Panel izquierdo - Información -->
            <div class="flex flex-col justify-center space-y-6">
                <div class="flex items-center space-x-3 mb-8">
                    <div class="p-3 bg-blue-600 rounded-xl">
                        <HeartIcon class="h-8 w-8 text-white" />
                    </div>
                    <div>
                        <h1 class="text-3xl font-bold text-gray-900">Datasoluciones</h1>
                        <p class="text-gray-600">Sistema integral para laboratorios dentales</p>
                    </div>
                </div>

                <div class="space-y-4">
                    <div class="bg-white rounded-lg shadow-md">
                        <div class="p-6 border-b">
                            <h3 class="font-semibold text-lg flex items-center gap-2">
                                <Building2Icon class="h-5 w-5 text-blue-600" />
                                Para Laboratorios
                            </h3>
                        </div>
                        <div class="p-6">
                            <ul class="space-y-2 text-sm text-gray-600">
                                <li>• Gestión completa de órdenes de trabajo</li>
                                <li>• Control de inventario de materiales</li>
                                <li>• Facturación electrónica AFIP</li>
                                <li>• Seguimiento de transporte</li>
                                <li>• Odontograma interactivo</li>
                            </ul>
                        </div>
                    </div>

                    <div class="bg-white rounded-lg shadow-md">
                        <div class="p-6 border-b">
                            <h3 class="font-semibold text-lg">Planes de Suscripción</h3>
                        </div>
                        <div class="p-6 space-y-3">
                            <div class="flex items-center justify-between p-3 border rounded-lg">
                                <div>
                                    <p class="font-medium">Plan Mensual</p>
                                    <p class="text-sm text-gray-600">Acceso completo a todas las funciones</p>
                                </div>
                                <span class="px-3 py-1 bg-gray-100 text-gray-800 rounded-full text-sm font-medium">$20.000/mes</span>
                            </div>
                            <div class="flex items-center justify-between p-3 border rounded-lg bg-green-50 border-green-200">
                                <div>
                                    <p class="font-medium">Plan Anual</p>
                                    <p class="text-sm text-gray-600">20% de descuento (12 meses por el precio de 10)</p>
                                </div>
                                <span class="px-3 py-1 bg-green-600 text-white rounded-full text-sm font-medium">$200.000/año</span>
                            </div>
                            <div class="flex items-center justify-between p-3 border rounded-lg bg-blue-50 border-blue-200">
                                <div>
                                    <p class="font-medium">Demo Gratis</p>
                                    <p class="text-sm text-gray-600">15 días para probar todas las funciones</p>
                                </div>
                                <span class="px-3 py-1 border border-gray-300 text-gray-700 rounded-full text-sm font-medium">GRATIS</span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Panel derecho - Formularios -->
            <div class="flex items-center justify-center">
                <div class="w-full max-w-md bg-white rounded-lg shadow-lg">
                    <div class="p-6 border-b text-center">
                        <h2 class="text-2xl font-bold">Bienvenido</h2>
                        <p class="text-gray-600 mt-1">Ingresa a tu cuenta o crea una nueva</p>
                    </div>

                    <div class="p-6">
                        <!-- Alert de error -->
                        <div v-if="error" class="mb-4 p-4 border border-red-200 bg-red-50 rounded-lg flex items-start gap-2">
                            <AlertCircleIcon class="h-5 w-5 text-red-600 flex-shrink-0 mt-0.5" />
                            <p class="text-sm text-red-700">{{ error }}</p>
                        </div>

                        <!-- Tabs -->
                        <div class="space-y-4">
                            <div class="flex gap-2 p-1 bg-gray-100 rounded-lg">
                                <button @click="activeTab = 'login'"
                                        :class="[
                    'flex-1 px-4 py-2 rounded-md font-medium transition-colors',
                    activeTab === 'login'
                      ? 'bg-white text-gray-900 shadow-sm'
                      : 'text-gray-600 hover:text-gray-900'
                  ]">
                                    Iniciar Sesión
                                </button>
                                <button @click="activeTab = 'register'"
                                        :class="[
                    'flex-1 px-4 py-2 rounded-md font-medium transition-colors',
                    activeTab === 'register'
                      ? 'bg-white text-gray-900 shadow-sm'
                      : 'text-gray-600 hover:text-gray-900'
                  ]">
                                    Registrarse
                                </button>
                            </div>

                            <!-- Tab Login -->
                            <div v-show="activeTab === 'login'">
                                <form @submit.prevent="handleLogin" class="space-y-4">
                                    <div class="space-y-2">
                                        <label for="email" class="block text-sm font-medium text-gray-700">Email</label>
                                        <div class="relative">
                                            <MailIcon class="absolute left-3 top-3 h-4 w-4 text-gray-400" />
                                            <input id="email"
                                                   v-model="loginForm.email"
                                                   type="email"
                                                   placeholder="correo@ejemplo.com"
                                                   class="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                                                   required />
                                        </div>
                                    </div>
                                    <div class="space-y-2">
                                        <label for="password" class="block text-sm font-medium text-gray-700">Contraseña</label>
                                        <div class="relative">
                                            <LockIcon class="absolute left-3 top-3 h-4 w-4 text-gray-400" />
                                            <input id="password"
                                                   v-model="loginForm.password"
                                                   type="password"
                                                   placeholder="••••••••"
                                                   class="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                                                   required />
                                        </div>
                                    </div>
                                    <button type="submit"
                                            :disabled="isLoading"
                                            class="w-full bg-blue-600 text-white py-2 px-4 rounded-lg hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed font-medium transition-colors">
                                        {{ isLoading ? 'Ingresando...' : 'Iniciar Sesión' }}
                                    </button>
                                    <p class="text-xs text-gray-500 text-center">
                                        Demo: Usa cualquier email y contraseña "password123"
                                    </p>
                                </form>
                            </div>

                            <!-- Tab Register -->
                            <div v-show="activeTab === 'register'">
                                <form @submit.prevent="handleRegister" class="space-y-4">
                                    <div class="space-y-2">
                                        <label for="lab-name" class="block text-sm font-medium text-gray-700">Nombre del Laboratorio</label>
                                        <div class="relative">
                                            <Building2Icon class="absolute left-3 top-3 h-4 w-4 text-gray-400" />
                                            <input id="lab-name"
                                                   v-model="registerForm.laboratoryName"
                                                   type="text"
                                                   placeholder="Laboratorio Dental S.A."
                                                   class="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                                                   required />
                                        </div>
                                    </div>
                                    <div class="space-y-2">
                                        <label for="reg-email" class="block text-sm font-medium text-gray-700">Email</label>
                                        <div class="relative">
                                            <MailIcon class="absolute left-3 top-3 h-4 w-4 text-gray-400" />
                                            <input id="reg-email"
                                                   v-model="registerForm.email"
                                                   type="email"
                                                   placeholder="correo@ejemplo.com"
                                                   class="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                                                   required />
                                        </div>
                                    </div>
                                    <div class="space-y-2">
                                        <label for="cuit" class="block text-sm font-medium text-gray-700">CUIT</label>
                                        <input id="cuit"
                                               v-model="registerForm.cuit"
                                               type="text"
                                               placeholder="30-12345678-9"
                                               class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent" />
                                    </div>
                                    <div class="space-y-2">
                                        <label for="phone" class="block text-sm font-medium text-gray-700">Teléfono</label>
                                        <div class="relative">
                                            <PhoneIcon class="absolute left-3 top-3 h-4 w-4 text-gray-400" />
                                            <input id="phone"
                                                   v-model="registerForm.phone"
                                                   type="tel"
                                                   placeholder="+54 11 1234-5678"
                                                   class="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent" />
                                        </div>
                                    </div>
                                    <div class="space-y-2">
                                        <label for="address" class="block text-sm font-medium text-gray-700">Dirección</label>
                                        <div class="relative">
                                            <MapPinIcon class="absolute left-3 top-3 h-4 w-4 text-gray-400" />
                                            <input id="address"
                                                   v-model="registerForm.address"
                                                   type="text"
                                                   placeholder="Av. Corrientes 1234, CABA"
                                                   class="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent" />
                                        </div>
                                    </div>
                                    <div class="space-y-2">
                                        <label for="plan" class="block text-sm font-medium text-gray-700">Plan de Suscripción</label>
                                        <select id="plan"
                                                v-model="registerForm.plan"
                                                class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                                                required>
                                            <option value="" disabled>Selecciona un plan</option>
                                            <option value="trial">Demo Gratis - 15 días</option>
                                            <option value="monthly">Mensual - $20.000/mes</option>
                                            <option value="yearly">Anual - $200.000/año</option>
                                        </select>
                                    </div>
                                    <div class="space-y-2">
                                        <label for="reg-password" class="block text-sm font-medium text-gray-700">Contraseña</label>
                                        <div class="relative">
                                            <LockIcon class="absolute left-3 top-3 h-4 w-4 text-gray-400" />
                                            <input id="reg-password"
                                                   v-model="registerForm.password"
                                                   type="password"
                                                   placeholder="••••••••"
                                                   class="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                                                   required />
                                        </div>
                                    </div>
                                    <div class="space-y-2">
                                        <label class="flex items-start space-x-2">
                                            <input v-model="registerForm.acceptTerms"
                                                   type="checkbox"
                                                   required
                                                   class="mt-1 rounded border-gray-300 text-blue-600 focus:ring-blue-500" />
                                            <span class="text-sm text-gray-600">
                                                Acepto los
                                                <button type="button"
                                                        @click="showTerms"
                                                        class="text-blue-600 underline hover:text-blue-800">
                                                    términos y condiciones
                                                </button>
                                                y la
                                                <button type="button"
                                                        @click="showPrivacy"
                                                        class="text-blue-600 underline hover:text-blue-800">
                                                    política de privacidad
                                                </button>
                                            </span>
                                        </label>
                                    </div>
                                    <button type="submit"
                                            :disabled="isLoading"
                                            class="w-full bg-blue-600 text-white py-2 px-4 rounded-lg hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed font-medium transition-colors">
                                        {{ isLoading ? 'Creando cuenta...' : 'Crear Cuenta' }}
                                    </button>
                                </form>
                            </div>
                        </div>
                    </div>

                    <div class="p-6 border-t text-center">
                        <p class="text-xs text-gray-600">
                            Al registrarte en Datasoluciones, aceptas nuestros términos y condiciones y política de privacidad.
                        </p>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
    import { ref } from 'vue'
    import { useRouter } from 'vue-router'
    import { useAuthStore } from '../stores/auth'
    import {
        Building2,
        Mail,
        Lock,
        Phone,
        MapPin,
        AlertCircle,
        Heart
    } from 'lucide-vue-next'

    // Asignar íconos a variables
    const Building2Icon = Building2
    const MailIcon = Mail
    const LockIcon = Lock
    const PhoneIcon = Phone
    const MapPinIcon = MapPin
    const AlertCircleIcon = AlertCircle
    const HeartIcon = Heart

    const router = useRouter()
    const authStore = useAuthStore()

    const activeTab = ref('login')
    const isLoading = ref(false)
    const error = ref('')

    const loginForm = ref({
        email: '',
        password: ''
    })

    const registerForm = ref({
        laboratoryName: '',
        email: '',
        password: '',
        phone: '',
        address: '',
        plan: '',
        cuit: '',
        acceptTerms: false
    })

    const handleLogin = async () => {
        isLoading.value = true
        error.value = ''

        try {
            const result = await authStore.login(loginForm.value.email, loginForm.value.password)

            if (result.success) {
                router.push('/dashboard')
            } else {
                error.value = result.error || 'Error en el login'
            }
        } catch (err) {
            error.value = 'Error al conectar con el servidor'
            console.error('Error en login:', err)
        } finally {
            isLoading.value = false
        }
    }

    const handleRegister = async () => {
        isLoading.value = true
        error.value = ''

        try {
            const result = await authStore.register(registerForm.value)

            if (result.success) {
                router.push('/dashboard')
            } else {
                error.value = result.error || 'Error en el registro'
            }
        } catch (err) {
            error.value = 'Error al conectar con el servidor'
            console.error('Error en registro:', err)
        } finally {
            isLoading.value = false
        }
    }

    const showTerms = () => {
        alert('Términos y Condiciones - Al hacer clic en registrarte, aceptas nuestros términos de servicio y política de privacidad. Datasoluciones se compromete a proteger tus datos y proporcionar un servicio de calidad. El período de prueba es de 15 días sin costo alguno.')
    }

    const showPrivacy = () => {
        alert('Política de Privacidad - Nos comprometemos a proteger tu información personal y solo la utilizaremos para mejorar nuestros servicios. No compartimos tus datos con terceros sin tu consentimiento.')
    }
</script>

<style scoped>
    /* Estilos adicionales si son necesarios */
</style>