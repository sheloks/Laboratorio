}
<template>
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <!-- Cambiar Contrasena -->
        <Card>
            <CardHeader>
                <CardTitle class="flex items-center gap-2">
                    <KeyIcon class="h-5 w-5" />
                    Cambiar Contrasena
                </CardTitle>
                <CardDescription>
                    Actualiza tu contrasena de acceso
                </CardDescription>
            </CardHeader>
            <CardContent class="space-y-4">
                <div class="space-y-2">
                    <Label>Contrasena actual</Label>
                    <div class="relative">
                        <Input :type="showCurrentPass ? 'text' : 'password'"
                               v-model="passwords.current"
                               placeholder="********" />
                        <button type="button"
                                @click="showCurrentPass = !showCurrentPass"
                                class="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600">
                            <EyeOffIcon v-if="showCurrentPass" class="h-4 w-4" />
                            <EyeIcon v-else class="h-4 w-4" />
                        </button>
                    </div>
                </div>

                <div class="space-y-2">
                    <Label>Nueva contrasena</Label>
                    <div class="relative">
                        <Input :type="showNewPass ? 'text' : 'password'"
                               v-model="passwords.new"
                               placeholder="********" />
                        <button type="button"
                                @click="showNewPass = !showNewPass"
                                class="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600">
                            <EyeOffIcon v-if="showNewPass" class="h-4 w-4" />
                            <EyeIcon v-else class="h-4 w-4" />
                        </button>
                    </div>
                    <!-- Indicador de fortaleza -->
                    <div v-if="passwords.new" class="space-y-1">
                        <div class="flex gap-1">
                            <div v-for="i in 4" :key="i"
                                 :class="['h-1 flex-1 rounded',
                                 i <= passwordStrength ? strengthColors[passwordStrength] : 'bg-gray-200']">
                            </div>
                        </div>
                        <p :class="['text-xs', strengthTextColors[passwordStrength]]">
                            {{ strengthLabels[passwordStrength] }}
                        </p>
                    </div>
                </div>

                <div class="space-y-2">
                    <Label>Confirmar nueva contrasena</Label>
                    <Input type="password"
                           v-model="passwords.confirm"
                           placeholder="********" />
                    <p v-if="passwords.confirm && passwords.new !== passwords.confirm"
                       class="text-xs text-red-500">
                        Las contrasenas no coinciden
                    </p>
                </div>

                <Button class="w-full" @click="cambiarContrasena" :disabled="!canChangePassword">
                    <KeyIcon class="h-4 w-4 mr-2" />
                    Cambiar Contrasena
                </Button>
            </CardContent>
        </Card>

        <!-- Autenticacion de 2 Factores -->
        <Card>
            <CardHeader>
                <CardTitle class="flex items-center gap-2">
                    <ShieldCheckIcon class="h-5 w-5" />
                    Autenticacion de 2 Factores
                </CardTitle>
                <CardDescription>
                    Agrega una capa extra de seguridad a tu cuenta
                </CardDescription>
            </CardHeader>
            <CardContent class="space-y-4">
                <div class="flex items-center justify-between p-4 border rounded-lg"
                     :class="{ 'bg-green-50 border-green-200': twoFactor.enabled }">
                    <div class="flex items-center gap-3">
                        <div :class="['p-2 rounded-lg', twoFactor.enabled ? 'bg-green-100' : 'bg-gray-100']">
                            <SmartphoneIcon :class="['h-5 w-5', twoFactor.enabled ? 'text-green-600' : 'text-gray-400']" />
                        </div>
                        <div>
                            <p class="font-medium">Autenticador</p>
                            <p class="text-sm text-gray-500">
                                {{ twoFactor.enabled ? 'Activado' : 'Desactivado' }}
                            </p>
                        </div>
                    </div>
                    <Button :variant="twoFactor.enabled ? 'outline' : 'default'"
                            @click="toggleTwoFactor">
                        {{ twoFactor.enabled ? 'Desactivar' : 'Activar' }}
                    </Button>
                </div>

                <div v-if="showQRSetup" class="p-4 border rounded-lg bg-gray-50">
                    <p class="text-sm font-medium mb-3">Escanea el codigo QR con tu app autenticadora</p>
                    <div class="w-48 h-48 mx-auto bg-white border rounded-lg flex items-center justify-center mb-3">
                        <QrCodeIcon class="h-32 w-32 text-gray-300" />
                    </div>
                    <div class="space-y-2">
                        <Label>Codigo de verificacion</Label>
                        <div class="flex gap-2">
                            <Input v-model="twoFactor.code" placeholder="123456" maxlength="6" />
                            <Button @click="verificar2FA">Verificar</Button>
                        </div>
                    </div>
                </div>

                <div class="p-3 bg-blue-50 border border-blue-200 rounded-lg">
                    <p class="text-sm text-blue-800">
                        <strong>Recomendado:</strong> Usa apps como Google Authenticator, Authy o Microsoft Authenticator
                    </p>
                </div>
            </CardContent>
        </Card>

        <!-- Sesiones Activas -->
        <Card>
            <CardHeader>
                <CardTitle class="flex items-center gap-2">
                    <MonitorIcon class="h-5 w-5" />
                    Sesiones Activas
                </CardTitle>
                <CardDescription>
                    Dispositivos donde tu cuenta esta conectada
                </CardDescription>
            </CardHeader>
            <CardContent class="space-y-3">
                <div v-for="sesion in sesiones" :key="sesion.id"
                     class="flex items-center justify-between p-3 border rounded-lg"
                     :class="{ 'bg-blue-50 border-blue-200': sesion.actual }">
                    <div class="flex items-center gap-3">
                        <div :class="['p-2 rounded-lg', sesion.actual ? 'bg-blue-100' : 'bg-gray-100']">
                            <component :is="sesion.icon"
                                       :class="['h-5 w-5', sesion.actual ? 'text-blue-600' : 'text-gray-500']" />
                        </div>
                        <div>
                            <p class="font-medium text-sm flex items-center gap-2">
                                {{ sesion.dispositivo }}
                                <span v-if="sesion.actual" class="px-2 py-0.5 bg-blue-600 text-white text-xs rounded-full">
                                    Actual
                                </span>
                            </p>
                            <p class="text-xs text-gray-500">
                                {{ sesion.ubicacion }} • {{ sesion.fecha }}
                            </p>
                        </div>
                    </div>
                    <Button v-if="!sesion.actual"
                            size="sm"
                            variant="ghost"
                            @click="cerrarSesion(sesion)">
                        <LogOutIcon class="h-4 w-4 text-red-500" />
                    </Button>
                </div>

                <Button variant="outline" class="w-full" @click="cerrarTodasSesiones">
                    <LogOutIcon class="h-4 w-4 mr-2" />
                    Cerrar todas las otras sesiones
                </Button>
            </CardContent>
        </Card>

        <!-- Registro de Actividad -->
        <Card>
            <CardHeader>
                <CardTitle class="flex items-center gap-2">
                    <HistoryIcon class="h-5 w-5" />
                    Registro de Actividad
                </CardTitle>
                <CardDescription>
                    Ultimas acciones de seguridad en tu cuenta
                </CardDescription>
            </CardHeader>
            <CardContent>
                <div class="space-y-3">
                    <div v-for="log in activityLogs" :key="log.id"
                         class="flex items-start gap-3 p-3 border rounded-lg">
                        <div :class="['p-1.5 rounded-full', getLogColor(log.tipo)]">
                            <component :is="getLogIcon(log.tipo)" class="h-4 w-4" />
                        </div>
                        <div class="flex-1">
                            <p class="text-sm font-medium">{{ log.accion }}</p>
                            <p class="text-xs text-gray-500">{{ log.fecha }} • {{ log.ip }}</p>
                        </div>
                    </div>
                </div>
            </CardContent>
        </Card>

        <!-- Opciones Adicionales -->
        <Card class="lg:col-span-2">
            <CardHeader>
                <CardTitle class="flex items-center gap-2">
                    <SettingsIcon class="h-5 w-5" />
                    Opciones de Seguridad
                </CardTitle>
                <CardDescription>
                    Configuraciones adicionales de seguridad
                </CardDescription>
            </CardHeader>
            <CardContent>
                <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div class="flex items-center justify-between p-3 border rounded-lg">
                        <div>
                            <p class="font-medium text-sm">Cerrar sesion automaticamente</p>
                            <p class="text-xs text-gray-500">Despues de 30 minutos de inactividad</p>
                        </div>
                        <label class="toggle-label">
                            <input type="checkbox" v-model="opciones.autoLogout" class="toggle-input" />
                            <span class="toggle-switch"></span>
                        </label>
                    </div>

                    <div class="flex items-center justify-between p-3 border rounded-lg">
                        <div>
                            <p class="font-medium text-sm">Alertas de inicio de sesion</p>
                            <p class="text-xs text-gray-500">Notificar cuando se inicie sesion en un nuevo dispositivo</p>
                        </div>
                        <label class="toggle-label">
                            <input type="checkbox" v-model="opciones.loginAlerts" class="toggle-input" />
                            <span class="toggle-switch"></span>
                        </label>
                    </div>

                    <div class="flex items-center justify-between p-3 border rounded-lg">
                        <div>
                            <p class="font-medium text-sm">Bloquear acceso no autorizado</p>
                            <p class="text-xs text-gray-500">Bloquear despues de 5 intentos fallidos</p>
                        </div>
                        <label class="toggle-label">
                            <input type="checkbox" v-model="opciones.blockUnauthorized" class="toggle-input" />
                            <span class="toggle-switch"></span>
                        </label>
                    </div>

                    <div class="flex items-center justify-between p-3 border rounded-lg">
                        <div>
                            <p class="font-medium text-sm">Requerir contrasena para acciones sensibles</p>
                            <p class="text-xs text-gray-500">Eliminar datos, cambiar configuracion critica</p>
                        </div>
                        <label class="toggle-label">
                            <input type="checkbox" v-model="opciones.confirmPassword" class="toggle-input" />
                            <span class="toggle-switch"></span>
                        </label>
                    </div>
                </div>

                <div class="mt-6">
                    <Button class="w-full" @click="guardarOpciones">
                        <SaveIcon class="h-4 w-4 mr-2" />
                        Guardar Opciones de Seguridad
                    </Button>
                </div>
            </CardContent>
        </Card>
    </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import {
    KeyIcon,
    ShieldCheckIcon,
    MonitorIcon,
    HistoryIcon,
    SettingsIcon,
    EyeIcon,
    EyeOffIcon,
    SmartphoneIcon,
    QrCodeIcon,
    LogOutIcon,
    SaveIcon,
    LaptopIcon,
    TabletIcon,
    CheckCircleIcon,
    AlertCircleIcon,
    UserIcon
} from 'lucide-vue-next'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'

// Estados de visibilidad
const showCurrentPass = ref(false)
const showNewPass = ref(false)
const showQRSetup = ref(false)

// Contrasenas
const passwords = reactive({
    current: '',
    new: '',
    confirm: ''
})

// Indicadores de fortaleza
const strengthLabels = ['', 'Muy debil', 'Debil', 'Media', 'Fuerte']
const strengthColors = ['', 'bg-red-500', 'bg-orange-500', 'bg-yellow-500', 'bg-green-500']
const strengthTextColors = ['', 'text-red-500', 'text-orange-500', 'text-yellow-500', 'text-green-500']

const passwordStrength = computed(() => {
    const pass = passwords.new
    if (!pass) return 0
    let strength = 0
    if (pass.length >= 8) strength++
    if (/[A-Z]/.test(pass)) strength++
    if (/[0-9]/.test(pass)) strength++
    if (/[^A-Za-z0-9]/.test(pass)) strength++
    return strength
})

const canChangePassword = computed(() => {
    return passwords.current &&
           passwords.new &&
           passwords.confirm &&
           passwords.new === passwords.confirm &&
           passwordStrength.value >= 2
})

// 2FA
const twoFactor = reactive({
    enabled: false,
    code: ''
})

// Sesiones
const sesiones = ref([
    { id: 1, dispositivo: 'Windows - Chrome', ubicacion: 'Reconquista, AR', fecha: 'Activa ahora', icon: LaptopIcon, actual: true },
    { id: 2, dispositivo: 'Android - App', ubicacion: 'Reconquista, AR', fecha: 'Hace 2 horas', icon: SmartphoneIcon, actual: false },
    { id: 3, dispositivo: 'iPad - Safari', ubicacion: 'Santa Fe, AR', fecha: 'Hace 1 dia', icon: TabletIcon, actual: false }
])

// Logs de actividad
const activityLogs = ref([
    { id: 1, accion: 'Inicio de sesion exitoso', fecha: '11/12/2025 13:45', ip: '190.17.xxx.xxx', tipo: 'success' },
    { id: 2, accion: 'Contrasena cambiada', fecha: '10/12/2025 09:30', ip: '190.17.xxx.xxx', tipo: 'warning' },
    { id: 3, accion: 'Intento de acceso fallido', fecha: '09/12/2025 22:15', ip: '45.67.xxx.xxx', tipo: 'error' },
    { id: 4, accion: 'Sesion cerrada', fecha: '08/12/2025 18:00', ip: '190.17.xxx.xxx', tipo: 'info' }
])

// Opciones de seguridad
const opciones = reactive({
    autoLogout: true,
    loginAlerts: true,
    blockUnauthorized: true,
    confirmPassword: false
})

// Funciones auxiliares
const getLogColor = (tipo) => {
    const colors = {
        success: 'bg-green-100 text-green-600',
        warning: 'bg-yellow-100 text-yellow-600',
        error: 'bg-red-100 text-red-600',
        info: 'bg-blue-100 text-blue-600'
    }
    return colors[tipo] || colors.info
}

const getLogIcon = (tipo) => {
    const icons = {
        success: CheckCircleIcon,
        warning: AlertCircleIcon,
        error: AlertCircleIcon,
        info: UserIcon
    }
    return icons[tipo] || UserIcon
}

// Cambiar contrasena
const cambiarContrasena = () => {
    if (!canChangePassword.value) return

    // TODO: Llamar al backend
    alert('Contrasena cambiada correctamente')
    passwords.current = ''
    passwords.new = ''
    passwords.confirm = ''
}

// Toggle 2FA
const toggleTwoFactor = () => {
    if (twoFactor.enabled) {
        if (confirm('¿Desactivar autenticacion de 2 factores?')) {
            twoFactor.enabled = false
            showQRSetup.value = false
        }
    } else {
        showQRSetup.value = true
    }
}

// Verificar 2FA
const verificar2FA = () => {
    if (twoFactor.code.length === 6) {
        twoFactor.enabled = true
        showQRSetup.value = false
        twoFactor.code = ''
        alert('Autenticacion de 2 factores activada')
    } else {
        alert('Codigo invalido')
    }
}

// Cerrar sesion
const cerrarSesion = (sesion) => {
    if (confirm(`¿Cerrar sesion en ${sesion.dispositivo}?`)) {
        sesiones.value = sesiones.value.filter(s => s.id !== sesion.id)
    }
}

// Cerrar todas las sesiones
const cerrarTodasSesiones = () => {
    if (confirm('¿Cerrar todas las otras sesiones?')) {
        sesiones.value = sesiones.value.filter(s => s.actual)
        alert('Sesiones cerradas')
    }
}

// Guardar opciones
const guardarOpciones = () => {
    localStorage.setItem('seguridadOpciones', JSON.stringify(opciones))
    alert('Opciones de seguridad guardadas')
}

// Cargar configuracion
onMounted(() => {
    const saved = localStorage.getItem('seguridadOpciones')
    if (saved) {
        Object.assign(opciones, JSON.parse(saved))
    }
})
</script>

<style scoped>
    .toggle-label {
        position: relative;
        display: inline-block;
        cursor: pointer;
    }

    .toggle-input {
        display: none;
    }

    .toggle-switch {
        width: 44px;
        height: 24px;
        background: #d1d5db;
        border-radius: 12px;
        position: relative;
        transition: background 0.2s;
        display: block;
    }

        .toggle-switch::after {
            content: '';
            position: absolute;
            width: 20px;
            height: 20px;
            background: white;
            border-radius: 50%;
            top: 2px;
            left: 2px;
            transition: transform 0.2s;
            box-shadow: 0 1px 3px rgba(0,0,0,0.2);
        }

    .toggle-input:checked + .toggle-switch {
        background: #3b82f6;
    }

        .toggle-input:checked + .toggle-switch::after {
            transform: translateX(20px);
        }
</style>