<template>
    <Dialog :open="open" @update:open="$emit('update:open', $event)">
        <DialogContent class="max-w-2xl max-h-[90vh] overflow-y-auto">
            <DialogHeader>
                <DialogTitle>Editar Orden</DialogTitle>
                <DialogDescription>
                    {{ order?.orderNumber }}
                </DialogDescription>
            </DialogHeader>

            <div class="space-y-4 py-4">
                <!-- Estado -->
                <div class="space-y-2">
                    <Label>Estado de la Orden</Label>
                    <Select v-model="localOrder.status">
                        <SelectTrigger>
                            <SelectValue placeholder="Seleccionar estado" />
                        </SelectTrigger>
                        <SelectContent>
                            <SelectItem value="pending">Pendiente</SelectItem>
                            <SelectItem value="in_progress">En Progreso</SelectItem>
                            <SelectItem value="ready">Lista para entregar</SelectItem>
                            <SelectItem value="delivered">Entregada</SelectItem>
                            <SelectItem value="cancelled">Cancelada</SelectItem>
                        </SelectContent>
                    </Select>
                </div>

                <!-- Prioridad -->
                <div class="space-y-2">
                    <Label>Prioridad</Label>
                    <Select v-model="localOrder.priority">
                        <SelectTrigger>
                            <SelectValue placeholder="Seleccionar prioridad" />
                        </SelectTrigger>
                        <SelectContent>
                            <SelectItem value="low">Baja</SelectItem>
                            <SelectItem value="normal">Normal</SelectItem>
                            <SelectItem value="high">Alta</SelectItem>
                            <SelectItem value="urgent">Urgente</SelectItem>
                        </SelectContent>
                    </Select>
                </div>

                <!-- Odontologo -->
                <div class="space-y-2">
                    <Label for="editDentist">Odontologo</Label>
                    <Input id="editDentist" v-model="localOrder.dentist" placeholder="Nombre del odontologo" />
                </div>

                <!-- Fecha de Entrega -->
                <div class="space-y-2">
                    <Label for="editDeliveryDate">Fecha de Entrega</Label>
                    <Input id="editDeliveryDate" type="date" v-model="localOrder.deliveryDate" />
                </div>

                <!-- Monto Total -->
                <div class="space-y-2">
                    <Label for="editTotalAmount">Monto Total</Label>
                    <Input id="editTotalAmount" type="number" v-model="localOrder.totalAmount" placeholder="0.00" />
                </div>

                <!-- Notas -->
                <div class="space-y-2">
                    <Label for="editNotes">Notas / Observaciones</Label>
                    <Textarea id="editNotes" v-model="localOrder.notes" placeholder="Instrucciones especiales..." rows="3" />
                </div>
            </div>

            <DialogFooter>
                <Button variant="outline" @click="$emit('update:open', false)" :disabled="saving">
                    Cancelar
                </Button>
                <Button @click="handleSave" :disabled="saving">
                    <SaveIcon v-if="!saving" class="h-4 w-4 mr-2" />
                    <div v-else class="animate-spin rounded-full h-4 w-4 border-b-2 border-white mr-2"></div>
                    {{ saving ? 'Guardando...' : 'Guardar Cambios' }}
                </Button>
            </DialogFooter>
        </DialogContent>
    </Dialog>
</template>

<script setup>
import { ref, watch } from 'vue'
import { SaveIcon } from 'lucide-vue-next'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Textarea } from '@/components/ui/textarea'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from '@/components/ui/dialog'

const props = defineProps({
    open: Boolean,
    order: Object,
    saving: Boolean
})

const emit = defineEmits(['update:open', 'save'])

const localOrder = ref({
    status: '',
    priority: '',
    dentist: '',
    deliveryDate: '',
    totalAmount: null,
    notes: ''
})

// Cargar datos cuando se abre el modal
watch(() => props.open, (isOpen) => {
    if (isOpen && props.order) {
        // Formatear fecha para input date
        let formattedDate = ''
        if (props.order.deliveryDate) {
            const date = new Date(props.order.deliveryDate)
            formattedDate = date.toISOString().split('T')[0]
        }
        
        localOrder.value = {
            status: props.order.status || 'pending',
            priority: props.order.priority || 'normal',
            dentist: props.order.dentist || '',
            deliveryDate: formattedDate,
            totalAmount: props.order.totalAmount || null,
            notes: props.order.notes || ''
        }
    }
})

const handleSave = () => {
    emit('save', {
        id: props.order.id,
        ...localOrder.value
    })
}
</script>