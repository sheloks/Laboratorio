<template>
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <!-- Informacion de Suscripcion -->
        <Card>
            <CardHeader>
                <CardTitle class="flex items-center gap-2">
                    <CreditCardIcon class="h-5 w-5" />
                    Informacion de Suscripcion
                </CardTitle>
                <CardDescription>
                    Gestiona tu plan de suscripcion y metodos de pago
                </CardDescription>
            </CardHeader>
            <CardContent class="space-y-4">
                <!-- Plan Actual -->
                <div class="p-4 border rounded-lg bg-blue-50">
                    <div class="flex justify-between items-center mb-2">
                        <span class="font-medium">Plan Actual</span>
                        <span class="px-3 py-1 bg-blue-600 text-white text-sm font-medium rounded-full">
                            {{ config.planActual }}
                        </span>
                    </div>
                    <div class="text-sm text-gray-600">
                        <p>Proximo pago: {{ config.proximoPago }}</p>
                        <p>Monto: {{ formatCurrency(config.monto) }}</p>
                    </div>
                </div>

                <!-- Metodo de Pago -->
                <div class="space-y-2">
                    <Label>Metodo de Pago</Label>
                    <Select v-model="config.metodoPago">
                        <SelectTrigger>
                            <SelectValue placeholder="Seleccionar metodo de pago" />
                        </SelectTrigger>
                        <SelectContent>
                            <SelectItem value="mercadopago">MercadoPago</SelectItem>
                            <SelectItem value="transferencia">Transferencia Bancaria</SelectItem>
                            <SelectItem value="tarjeta">Tarjeta de Credito</SelectItem>
                        </SelectContent>
                    </Select>
                </div>

                <!-- Token MercadoPago -->
                <div class="space-y-2">
                    <Label>Token de MercadoPago</Label>
                    <Input type="password"
                           v-model="config.mpToken"
                           placeholder="TEST-123456789-abcdef" />
                </div>

                <Button class="w-full" @click="actualizarMetodoPago">
                    Actualizar Metodo de Pago
                </Button>
            </CardContent>
        </Card>

        <!-- Historial de Pagos -->
        <Card>
            <CardHeader>
                <CardTitle>Historial de Pagos</CardTitle>
                <CardDescription>
                    Ultimos movimientos de tu cuenta
                </CardDescription>
            </CardHeader>
            <CardContent>
                <div class="space-y-3">
                    <div v-for="(pago, index) in historialPagos"
                         :key="index"
                         class="flex justify-between items-center p-3 border rounded-lg">
                        <div>
                            <p class="font-medium">{{ pago.concepto }}</p>
                            <p class="text-sm text-gray-600">{{ pago.fecha }}</p>
                        </div>
                        <div class="text-right">
                            <p class="font-medium">{{ formatCurrency(pago.monto) }}</p>
                            <span :class="[
                                'px-2 py-0.5 text-xs font-medium rounded-full',
                                pago.estado === 'Pagado'
                                    ? 'bg-green-100 text-green-700'
                                    : 'bg-yellow-100 text-yellow-700'
                            ]">
                                {{ pago.estado }}
                            </span>
                        </div>
                    </div>

                    <div v-if="historialPagos.length === 0" class="text-center py-8 text-gray-500">
                        <CreditCardIcon class="h-12 w-12 mx-auto mb-2 opacity-50" />
                        <p>No hay pagos registrados</p>
                    </div>
                </div>
            </CardContent>
        </Card>
    </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { CreditCardIcon } from 'lucide-vue-next'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'

// Configuracion
const config = reactive({
    planActual: 'Plan Anual',
    proximoPago: '15 de Enero 2025',
    monto: 200000,
    metodoPago: '',
    mpToken: ''
})

// Historial de pagos
const historialPagos = ref([
    { concepto: 'Plan Anual', fecha: '15 Ene 2024', monto: 200000, estado: 'Pagado' },
    { concepto: 'Plan Anual', fecha: '15 Ene 2023', monto: 200000, estado: 'Pagado' }
])

// Formatear moneda
const formatCurrency = (amount) => {
    return new Intl.NumberFormat('es-AR', {
        style: 'currency',
        currency: 'ARS',
        minimumFractionDigits: 0
    }).format(amount)
}

// Actualizar metodo de pago
const actualizarMetodoPago = () => {
    console.log('Actualizando metodo de pago:', config.metodoPago)
    // TODO: Llamar al backend
    alert('Metodo de pago actualizado')
}
</script>