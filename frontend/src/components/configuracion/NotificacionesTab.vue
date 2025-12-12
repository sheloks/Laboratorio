<template>
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <!-- Notificaciones por Email -->
        <Card>
            <CardHeader>
                <CardTitle class="flex items-center gap-2">
                    <MailIcon class="h-5 w-5" />
                    Notificaciones por Email
                </CardTitle>
                <CardDescription>
                    Configura cuando enviar emails automaticos
                </CardDescription>
            </CardHeader>
            <CardContent class="space-y-4">
                <div v-for="notif in notificacionesEmail" :key="notif.id"
                     class="flex items-center justify-between p-3 border rounded-lg">
                    <div class="flex items-center gap-3">
                        <div :class="['p-2 rounded-lg', notif.activo ? 'bg-blue-100' : 'bg-gray-100']">
                            <component :is="notif.icon"
                                       :class="['h-5 w-5', notif.activo ? 'text-blue-600' : 'text-gray-400']" />
                        </div>
                        <div>
                            <p class="font-medium text-sm">{{ notif.titulo }}</p>
                            <p class="text-xs text-gray-500">{{ notif.descripcion }}</p>
                        </div>
                    </div>
                    <label class="toggle-label">
                        <input type="checkbox" v-model="notif.activo" class="toggle-input" />
                        <span class="toggle-switch"></span>
                    </label>
                </div>
            </CardContent>
        </Card>

        <!-- Notificaciones por WhatsApp -->
        <Card>
            <CardHeader>
                <CardTitle class="flex items-center gap-2">
                    <MessageCircleIcon class="h-5 w-5" />
                    Notificaciones por WhatsApp
                </CardTitle>
                <CardDescription>
                    Configura mensajes automaticos de WhatsApp
                </CardDescription>
            </CardHeader>
            <CardContent class="space-y-4">
                <div v-for="notif in notificacionesWhatsapp" :key="notif.id"
                     class="flex items-center justify-between p-3 border rounded-lg">
                    <div class="flex items-center gap-3">
                        <div :class="['p-2 rounded-lg', notif.activo ? 'bg-green-100' : 'bg-gray-100']">
                            <component :is="notif.icon"
                                       :class="['h-5 w-5', notif.activo ? 'text-green-600' : 'text-gray-400']" />
                        </div>
                        <div>
                            <p class="font-medium text-sm">{{ notif.titulo }}</p>
                            <p class="text-xs text-gray-500">{{ notif.descripcion }}</p>
                        </div>
                    </div>
                    <label class="toggle-label">
                        <input type="checkbox" v-model="notif.activo" class="toggle-input" />
                        <span class="toggle-switch"></span>
                    </label>
                </div>
            </CardContent>
        </Card>

        <!-- Notificaciones del Sistema -->
        <Card>
            <CardHeader>
                <CardTitle class="flex items-center gap-2">
                    <BellIcon class="h-5 w-5" />
                    Alertas del Sistema
                </CardTitle>
                <CardDescription>
                    Notificaciones internas del sistema
                </CardDescription>
            </CardHeader>
            <CardContent class="space-y-4">
                <div v-for="alerta in alertasSistema" :key="alerta.id"
                     class="flex items-center justify-between p-3 border rounded-lg">
                    <div class="flex items-center gap-3">
                        <div :class="['p-2 rounded-lg', alerta.activo ? 'bg-orange-100' : 'bg-gray-100']">
                            <component :is="alerta.icon"
                                       :class="['h-5 w-5', alerta.activo ? 'text-orange-600' : 'text-gray-400']" />
                        </div>
                        <div>
                            <p class="font-medium text-sm">{{ alerta.titulo }}</p>
                            <p class="text-xs text-gray-500">{{ alerta.descripcion }}</p>
                        </div>
                    </div>
                    <label class="toggle-label">
                        <input type="checkbox" v-model="alerta.activo" class="toggle-input" />
                        <span class="toggle-switch"></span>
                    </label>
                </div>
            </CardContent>
        </Card>

        <!-- Plantillas de Mensajes -->
        <Card>
            <CardHeader>
                <CardTitle class="flex items-center gap-2">
                    <FileTextIcon class="h-5 w-5" />
                    Plantillas de Mensajes
                </CardTitle>
                <CardDescription>
                    Personaliza el contenido de las notificaciones
                </CardDescription>
            </CardHeader>
            <CardContent class="space-y-4">
                <div class="space-y-2">
                    <Label>Orden Lista para Retirar</Label>
                    <textarea v-model="plantillas.ordenLista"
                              rows="3"
                              class="w-full p-3 border rounded-lg text-sm resize-none focus:outline-none focus:ring-2 focus:ring-blue-500"
                              placeholder="Hola {cliente}, su orden #{numero} esta lista..."></textarea>
                    <p class="text-xs text-gray-400">Variables: {cliente}, {numero}, {fecha}, {total}</p>
                </div>

                <div class="space-y-2">
                    <Label>Recordatorio de Pago</Label>
                    <textarea v-model="plantillas.recordatorioPago"
                              rows="3"
                              class="w-full p-3 border rounded-lg text-sm resize-none focus:outline-none focus:ring-2 focus:ring-blue-500"
                              placeholder="Estimado {cliente}, le recordamos que tiene un pago pendiente..."></textarea>
                    <p class="text-xs text-gray-400">Variables: {cliente}, {monto}, {vencimiento}</p>
                </div>

                <div class="space-y-2">
                    <Label>Nueva Orden Recibida</Label>
                    <textarea v-model="plantillas.nuevaOrden"
                              rows="3"
                              class="w-full p-3 border rounded-lg text-sm resize-none focus:outline-none focus:ring-2 focus:ring-blue-500"
                              placeholder="Nueva orden #{numero} de {cliente}..."></textarea>
                    <p class="text-xs text-gray-400">Variables: {numero}, {cliente}, {trabajo}</p>
                </div>
            </CardContent>
        </Card>

        <!-- Horarios de Envio -->
        <Card class="lg:col-span-2">
            <CardHeader>
                <CardTitle class="flex items-center gap-2">
                    <ClockIcon class="h-5 w-5" />
                    Horarios de Envio
                </CardTitle>
                <CardDescription>
                    Define cuando se pueden enviar notificaciones automaticas
                </CardDescription>
            </CardHeader>
            <CardContent>
                <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
                    <div class="space-y-3">
                        <Label>Horario permitido</Label>
                        <div class="flex items-center gap-2">
                            <Input type="time" v-model="horarioEnvio.desde" class="w-28" />
                            <span class="text-gray-400">a</span>
                            <Input type="time" v-model="horarioEnvio.hasta" class="w-28" />
                        </div>
                        <p class="text-xs text-gray-500">
                            Las notificaciones fuera de este horario se enviaran al dia siguiente
                        </p>
                    </div>

                    <div class="space-y-3">
                        <Label>Dias de envio</Label>
                        <div class="flex flex-wrap gap-2">
                            <label v-for="dia in diasSemana" :key="dia.value"
                                   class="flex items-center gap-1.5 px-3 py-1.5 border rounded-full cursor-pointer text-sm"
                                   :class="{ 'bg-blue-100 border-blue-300 text-blue-700': dia.activo }">
                                <input type="checkbox" v-model="dia.activo" class="hidden" />
                                {{ dia.label }}
                            </label>
                        </div>
                    </div>

                    <div class="space-y-3">
                        <Label>Zona horaria</Label>
                        <Select v-model="horarioEnvio.zonaHoraria">
                            <SelectTrigger>
                                <SelectValue />
                            </SelectTrigger>
                            <SelectContent>
                                <SelectItem value="America/Argentina/Buenos_Aires">Argentina (GMT-3)</SelectItem>
                                <SelectItem value="America/Santiago">Chile (GMT-3)</SelectItem>
                                <SelectItem value="America/Mexico_City">Mexico (GMT-6)</SelectItem>
                            </SelectContent>
                        </Select>
                    </div>
                </div>
            </CardContent>
        </Card>

        <!-- Boton Guardar -->
        <div class="lg:col-span-2">
            <Button class="w-full" size="lg" @click="guardarNotificaciones" :disabled="guardando">
                <LoaderIcon v-if="guardando" class="h-4 w-4 mr-2 animate-spin" />
                <SaveIcon v-else class="h-4 w-4 mr-2" />
                {{ guardando ? 'Guardando...' : 'Guardar Configuracion de Notificaciones' }}
            </Button>
        </div>
    </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import {
    MailIcon,
    MessageCircleIcon,
    BellIcon,
    FileTextIcon,
    ClockIcon,
    SaveIcon,
    LoaderIcon,
    PackageIcon,
    CheckCircleIcon,
    AlertCircleIcon,
    CreditCardIcon,
    TruckIcon,
    CalendarIcon,
    UsersIcon
} from 'lucide-vue-next'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'

const guardando = ref(false)

// Notificaciones por Email
const notificacionesEmail = ref([
    { id: 1, titulo: 'Orden lista', descripcion: 'Cuando una orden esta lista para retirar', icon: CheckCircleIcon, activo: true },
    { id: 2, titulo: 'Recordatorio de pago', descripcion: 'Cuando hay facturas pendientes', icon: CreditCardIcon, activo: true },
    { id: 3, titulo: 'Orden enviada', descripcion: 'Cuando se despacha un envio', icon: TruckIcon, activo: false },
    { id: 4, titulo: 'Stock bajo', descripcion: 'Cuando un material esta por agotarse', icon: AlertCircleIcon, activo: true }
])

// Notificaciones por WhatsApp
const notificacionesWhatsapp = ref([
    { id: 1, titulo: 'Orden lista', descripcion: 'Mensaje cuando la orden esta lista', icon: CheckCircleIcon, activo: true },
    { id: 2, titulo: 'Orden recibida', descripcion: 'Confirmacion de recepcion de orden', icon: PackageIcon, activo: true },
    { id: 3, titulo: 'Seguimiento envio', descripcion: 'Actualizaciones de envio', icon: TruckIcon, activo: false }
])

// Alertas del Sistema
const alertasSistema = ref([
    { id: 1, titulo: 'Ordenes retrasadas', descripcion: 'Alertar ordenes pasadas de fecha', icon: CalendarIcon, activo: true },
    { id: 2, titulo: 'Stock critico', descripcion: 'Cuando un material llega a 0', icon: AlertCircleIcon, activo: true },
    { id: 3, titulo: 'Nuevos clientes', descripcion: 'Cuando se registra un cliente nuevo', icon: UsersIcon, activo: false },
    { id: 4, titulo: 'Backup fallido', descripcion: 'Si falla un backup automatico', icon: AlertCircleIcon, activo: true }
])

// Plantillas
const plantillas = reactive({
    ordenLista: 'Hola {cliente}, su orden #{numero} esta lista para retirar. Total: ${total}. Gracias por confiar en nosotros!',
    recordatorioPago: 'Estimado {cliente}, le recordamos que tiene un pago pendiente de ${monto} con vencimiento el {vencimiento}.',
    nuevaOrden: 'Nueva orden #{numero} recibida de {cliente}. Trabajo: {trabajo}'
})

// Horario de envio
const horarioEnvio = reactive({
    desde: '08:00',
    hasta: '20:00',
    zonaHoraria: 'America/Argentina/Buenos_Aires'
})

// Dias de la semana
const diasSemana = ref([
    { label: 'Lun', value: 1, activo: true },
    { label: 'Mar', value: 2, activo: true },
    { label: 'Mie', value: 3, activo: true },
    { label: 'Jue', value: 4, activo: true },
    { label: 'Vie', value: 5, activo: true },
    { label: 'Sab', value: 6, activo: false },
    { label: 'Dom', value: 0, activo: false }
])

// Cargar configuracion
onMounted(() => {
    const saved = localStorage.getItem('notificacionesConfig')
    if (saved) {
        const data = JSON.parse(saved)
        if (data.email) notificacionesEmail.value = data.email
        if (data.whatsapp) notificacionesWhatsapp.value = data.whatsapp
        if (data.sistema) alertasSistema.value = data.sistema
        if (data.plantillas) Object.assign(plantillas, data.plantillas)
        if (data.horario) Object.assign(horarioEnvio, data.horario)
        if (data.dias) diasSemana.value = data.dias
    }
})

// Guardar configuracion
const guardarNotificaciones = async () => {
    guardando.value = true
    try {
        await new Promise(resolve => setTimeout(resolve, 1000))

        const data = {
            email: notificacionesEmail.value,
            whatsapp: notificacionesWhatsapp.value,
            sistema: alertasSistema.value,
            plantillas: { ...plantillas },
            horario: { ...horarioEnvio },
            dias: diasSemana.value
        }

        localStorage.setItem('notificacionesConfig', JSON.stringify(data))

        // TODO: Guardar en backend
        // await fetch('/api/configuracion/notificaciones', {
        //     method: 'POST',
        //     headers: { 'Content-Type': 'application/json' },
        //     body: JSON.stringify(data)
        // })

        alert('Configuracion de notificaciones guardada')
    } catch (error) {
        alert('Error al guardar: ' + error.message)
    } finally {
        guardando.value = false
    }
}
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