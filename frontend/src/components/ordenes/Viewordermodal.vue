<template>
    <Dialog :open="open" @update:open="$emit('update:open', $event)">
        <DialogContent class="max-w-2xl">
            <DialogHeader>
                <DialogTitle class="flex items-center space-x-2">
                    <FileTextIcon class="h-5 w-5" />
                    <span>{{ order?.orderNumber }}</span>
                </DialogTitle>
                <DialogDescription>
                    Detalles de la orden de trabajo
                </DialogDescription>
            </DialogHeader>

            <div v-if="order" class="space-y-6 py-4">
                <!-- Estado y Prioridad -->
                <div class="flex items-center space-x-3">
                    <Badge :class="getPriorityColor(order.priority)">
                        {{ getPriorityLabel(order.priority) }}
                    </Badge>
                    <Badge :class="getStatusColor(order.status)">
                        {{ getStatusLabel(order.status) }}
                    </Badge>
                </div>

                <!-- Info Principal -->
                <div class="grid grid-cols-2 gap-4">
                    <div class="space-y-1">
                        <p class="text-sm text-gray-500">Cliente</p>
                        <p class="font-medium">{{ order.client || order.cliente?.name || '-' }}</p>
                    </div>
                    <div class="space-y-1">
                        <p class="text-sm text-gray-500">Odontologo</p>
                        <p class="font-medium">{{ order.dentist || '-' }}</p>
                    </div>
                    <div class="space-y-1">
                        <p class="text-sm text-gray-500">Paciente</p>
                        <p class="font-medium">{{ order.patient || '-' }}</p>
                    </div>
                    <div class="space-y-1">
                        <p class="text-sm text-gray-500">Fecha de Entrega</p>
                        <p class="font-medium">{{ formatDate(order.deliveryDate) }}</p>
                    </div>
                </div>

                <!-- Monto -->
                <div class="border-t pt-4">
                    <div class="flex items-center justify-between">
                        <span class="text-gray-500">Monto Total</span>
                        <span class="text-2xl font-bold text-green-600">
                            ${{ formatCurrency(order.totalAmount) }}
                        </span>
                    </div>
                </div>

                <!-- Trabajos -->
                <div v-if="order.workItems && order.workItems.length > 0" class="border-t pt-4">
                    <h4 class="font-medium mb-3 flex items-center">
                        <ClipboardListIcon class="h-4 w-4 mr-2" />
                        Trabajos
                    </h4>
                    <div class="space-y-2">
                        <div v-for="(item, index) in order.workItems" :key="index" 
                             class="bg-gray-50 rounded-lg p-3 flex justify-between items-center">
                            <div>
                                <p class="font-medium">{{ item.type }}</p>
                                <p class="text-sm text-gray-500">
                                    {{ item.material }} - Piezas: {{ item.teeth }}
                                </p>
                            </div>
                            <span class="font-medium">${{ formatCurrency(item.price) }}</span>
                        </div>
                    </div>
                </div>

                <!-- Materiales -->
                <div v-if="order.materials && order.materials.length > 0" class="border-t pt-4">
                    <h4 class="font-medium mb-3">Materiales</h4>
                    <div class="flex flex-wrap gap-2">
                        <Badge v-for="material in order.materials" :key="material" variant="outline">
                            {{ material }}
                        </Badge>
                    </div>
                </div>

                <!-- Notas -->
                <div v-if="order.notes" class="border-t pt-4">
                    <h4 class="font-medium mb-2">Notas / Observaciones</h4>
                    <p class="text-gray-600 bg-gray-50 rounded-lg p-3">{{ order.notes }}</p>
                </div>

                <!-- Fechas -->
                <div class="border-t pt-4">
                    <div class="grid grid-cols-2 gap-4 text-sm">
                        <div class="space-y-1">
                            <p class="text-gray-500">Fecha de Creacion</p>
                            <p>{{ formatDate(order.createdAt) }}</p>
                        </div>
                        <div class="space-y-1">
                            <p class="text-gray-500">Ultima Actualizacion</p>
                            <p>{{ formatDate(order.updatedAt) }}</p>
                        </div>
                    </div>
                </div>
            </div>

            <DialogFooter>
                <Button variant="outline" @click="$emit('update:open', false)">
                    Cerrar
                </Button>
                <Button @click="$emit('edit')">
                    <EditIcon class="h-4 w-4 mr-2" />
                    Editar
                </Button>
            </DialogFooter>
        </DialogContent>
    </Dialog>
</template>

<script setup>
import { FileTextIcon, ClipboardListIcon, EditIcon } from 'lucide-vue-next'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from '@/components/ui/dialog'
import { useOrderHelpers } from '@/composables/ordenes/useOrderHelpers'

defineProps({
    open: Boolean,
    order: Object
})

defineEmits(['update:open', 'edit'])

const { 
    getPriorityColor, 
    getPriorityLabel, 
    getStatusColor, 
    getStatusLabel, 
    formatDate,
    formatCurrency 
} = useOrderHelpers()
</script>