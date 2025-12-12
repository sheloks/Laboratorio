<template>
    <Dialog :open="open" @update:open="$emit('update:open', $event)">
        <DialogContent class="max-w-2xl">
            <DialogHeader>
                <DialogTitle class="flex items-center space-x-2">
                    <ReceiptIcon class="h-5 w-5" />
                    <span>{{ invoice?.invoiceNumber }}</span>
                </DialogTitle>
                <DialogDescription>
                    Detalles de la factura
                </DialogDescription>
            </DialogHeader>

            <div v-if="invoice" class="space-y-6 py-4">
                <!-- Estado -->
                <div class="flex items-center space-x-3">
                    <Badge :class="getStatusColor(invoice.status)">
                        {{ getStatusLabel(invoice.status) }}
                    </Badge>
                    <Badge v-if="invoice.afipCae" variant="outline" class="text-green-700">
                        CAE: {{ invoice.afipCae }}
                    </Badge>
                </div>

                <!-- Info Principal -->
                <div class="grid grid-cols-2 gap-4">
                    <div class="space-y-1">
                        <p class="text-sm text-gray-500">Cliente</p>
                        <p class="font-medium">{{ invoice.client }}</p>
                    </div>
                    <div class="space-y-1">
                        <p class="text-sm text-gray-500">CUIT</p>
                        <p class="font-medium">{{ invoice.clientCuit || '-' }}</p>
                    </div>
                    <div class="space-y-1">
                        <p class="text-sm text-gray-500">Orden Asociada</p>
                        <p class="font-medium">{{ invoice.orderNumber }}</p>
                    </div>
                    <div class="space-y-1">
                        <p class="text-sm text-gray-500">Fecha de Emision</p>
                        <p class="font-medium">{{ formatDate(invoice.issueDate) }}</p>
                    </div>
                </div>

                <!-- Montos -->
                <div class="border-t pt-4">
                    <h4 class="font-medium mb-3">Detalle de Importes</h4>
                    <div class="bg-gray-50 rounded-lg p-4 space-y-2">
                        <div class="flex justify-between">
                            <span class="text-gray-600">Importe Neto</span>
                            <span class="font-medium">${{ formatCurrency(invoice.netAmount) }}</span>
                        </div>
                        <div class="flex justify-between">
                            <span class="text-gray-600">IVA (21%)</span>
                            <span class="font-medium">${{ formatCurrency(invoice.ivaAmount) }}</span>
                        </div>
                        <div class="border-t pt-2 flex justify-between">
                            <span class="font-semibold">Total</span>
                            <span class="text-xl font-bold text-green-600">${{ formatCurrency(invoice.totalAmount) }}</span>
                        </div>
                    </div>
                </div>

                <!-- AFIP -->
                <div v-if="invoice.afipCae" class="border-t pt-4">
                    <h4 class="font-medium mb-3">Datos AFIP</h4>
                    <div class="grid grid-cols-2 gap-4">
                        <div class="space-y-1">
                            <p class="text-sm text-gray-500">CAE</p>
                            <p class="font-medium font-mono">{{ invoice.afipCae }}</p>
                        </div>
                        <div class="space-y-1">
                            <p class="text-sm text-gray-500">Vencimiento CAE</p>
                            <p class="font-medium">{{ formatDate(invoice.afipVencimiento) }}</p>
                        </div>
                    </div>
                </div>

                <!-- Fechas -->
                <div class="border-t pt-4">
                    <div class="grid grid-cols-2 gap-4 text-sm">
                        <div class="space-y-1">
                            <p class="text-gray-500">Fecha de Vencimiento</p>
                            <p>{{ formatDate(invoice.dueDate) }}</p>
                        </div>
                        <div class="space-y-1">
                            <p class="text-gray-500">Ultima Actualizacion</p>
                            <p>{{ formatDate(invoice.updatedAt) }}</p>
                        </div>
                    </div>
                </div>
            </div>

            <DialogFooter>
                <Button variant="outline" @click="$emit('update:open', false)">
                    Cerrar
                </Button>
                <Button variant="outline" @click="$emit('download')">
                    <DownloadIcon class="h-4 w-4 mr-2" />
                    Descargar PDF
                </Button>
                <Button v-if="invoice?.status === 'issued'" @click="$emit('markPaid')">
                    <CheckCircleIcon class="h-4 w-4 mr-2" />
                    Marcar Pagada
                </Button>
            </DialogFooter>
        </DialogContent>
    </Dialog>
</template>

<script setup>
import { ReceiptIcon, DownloadIcon, CheckCircleIcon } from 'lucide-vue-next'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from '@/components/ui/dialog'
import { useInvoiceHelpers } from '@/composables/contabilidad/useInvoiceHelpers'

defineProps({
    open: Boolean,
    invoice: Object
})

defineEmits(['update:open', 'download', 'markPaid'])

const { getStatusColor, getStatusLabel, formatCurrency, formatDate } = useInvoiceHelpers()
</script>