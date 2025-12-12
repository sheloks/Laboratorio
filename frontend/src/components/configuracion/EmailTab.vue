<template>
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <!-- Configuracion SMTP -->
        <Card>
            <CardHeader>
                <CardTitle class="flex items-center gap-2">
                    <MailIcon class="h-5 w-5" />
                    Configuracion SMTP
                </CardTitle>
                <CardDescription>
                    Configura el envio de correos con Gmail
                </CardDescription>
            </CardHeader>
            <CardContent class="space-y-4">
                <div class="space-y-2">
                    <Label>Servidor SMTP</Label>
                    <Input v-model="config.smtpHost"
                           placeholder="smtp.gmail.com" />
                </div>

                <div class="space-y-2">
                    <Label>Puerto</Label>
                    <Input v-model="config.smtpPort"
                           placeholder="587" />
                </div>

                <div class="space-y-2">
                    <Label>Email Remitente</Label>
                    <Input type="email"
                           v-model="config.emailRemitente"
                           placeholder="tu-laboratorio@gmail.com" />
                </div>

                <div class="space-y-2">
                    <Label>Contrasena de Aplicacion</Label>
                    <Input type="password"
                           v-model="config.smtpPassword"
                           placeholder="contrasena-de-aplicacion" />
                    <p class="text-xs text-gray-500">
                        Genera una contrasena de aplicacion en tu cuenta de Google
                    </p>
                </div>

                <div class="flex items-center space-x-2">
                    <input type="checkbox"
                           id="ssl-enabled"
                           v-model="config.sslEnabled"
                           class="rounded border-gray-300" />
                    <Label for="ssl-enabled">Usar SSL/TLS</Label>
                </div>

                <Button class="w-full" @click="probarConfiguracion" :disabled="testing">
                    <SendIcon v-if="!testing" class="h-4 w-4 mr-2" />
                    <div v-else class="animate-spin rounded-full h-4 w-4 border-b-2 border-white mr-2"></div>
                    {{ testing ? 'Probando...' : 'Probar Configuracion' }}
                </Button>

                <div v-if="testResult" :class="[
                    'p-3 rounded-lg text-sm',
                    testResult.success ? 'bg-green-50 text-green-700' : 'bg-red-50 text-red-700'
                ]">
                    {{ testResult.message }}
                </div>
            </CardContent>
        </Card>

        <!-- Plantillas de Email -->
        <Card>
            <CardHeader>
                <CardTitle>Plantillas de Email</CardTitle>
                <CardDescription>
                    Personaliza los correos automaticos
                </CardDescription>
            </CardHeader>
            <CardContent class="space-y-4">
                <div class="space-y-2">
                    <Label>Asunto - Bienvenida</Label>
                    <Input v-model="plantillas.bienvenida"
                           placeholder="Bienvenido a nuestro laboratorio" />
                </div>

                <div class="space-y-2">
                    <Label>Asunto - Orden Lista</Label>
                    <Input v-model="plantillas.ordenLista"
                           placeholder="Tu orden esta lista para retirar" />
                </div>

                <div class="space-y-2">
                    <Label>Asunto - Factura</Label>
                    <Input v-model="plantillas.factura"
                           placeholder="Nueva factura de Laboratorio Dental" />
                </div>

                <div class="space-y-2">
                    <Label>Asunto - Recordatorio de Pago</Label>
                    <Input v-model="plantillas.recordatorioPago"
                           placeholder="Recordatorio: Factura pendiente de pago" />
                </div>

                <div class="space-y-2">
                    <Label>Firma de Email</Label>
                    <Textarea v-model="plantillas.firma"
                              rows="4"
                              placeholder="Tu firma personalizada..." />
                </div>

                <Button variant="outline" class="w-full" @click="guardarPlantillas">
                    <SaveIcon class="h-4 w-4 mr-2" />
                    Guardar Plantillas
                </Button>
            </CardContent>
        </Card>
    </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { MailIcon, SendIcon, SaveIcon } from 'lucide-vue-next'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Textarea } from '@/components/ui/textarea'

// Configuracion SMTP
const config = reactive({
    smtpHost: 'smtp.gmail.com',
    smtpPort: '587',
    emailRemitente: '',
    smtpPassword: '',
    sslEnabled: true
})

// Plantillas
const plantillas = reactive({
    bienvenida: 'Bienvenido a Laboratorio Dental',
    ordenLista: 'Tu orden esta lista para retirar',
    factura: 'Nueva factura de Laboratorio Dental',
    recordatorioPago: 'Recordatorio: Factura pendiente de pago',
    firma: 'Laboratorio Dental\nTel: +54 3482 123456\nEmail: info@laboratorio.com'
})

// Estado del test
const testing = ref(false)
const testResult = ref(null)

// Probar configuracion
const probarConfiguracion = async () => {
    testing.value = true
    testResult.value = null

    try {
        // Simular prueba
        await new Promise(resolve => setTimeout(resolve, 2000))

        // TODO: Llamar al backend para probar SMTP
        testResult.value = {
            success: true,
            message: 'Conexion exitosa! Se envio un correo de prueba.'
        }
    } catch (error) {
        testResult.value = {
            success: false,
            message: 'Error al conectar: ' + error.message
        }
    } finally {
        testing.value = false
    }
}

// Guardar plantillas
const guardarPlantillas = () => {
    console.log('Guardando plantillas:', plantillas)
    // TODO: Llamar al backend
    alert('Plantillas guardadas correctamente')
}
</script>