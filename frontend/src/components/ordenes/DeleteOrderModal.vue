<template>
    <Dialog :open="open" @update:open="$emit('update:open', $event)">
        <DialogContent>
            <DialogHeader>
                <DialogTitle>Eliminar Orden</DialogTitle>
                <DialogDescription>
                    Estas seguro de que deseas eliminar esta orden?
                </DialogDescription>
            </DialogHeader>
            
            <div class="py-4">
                <div class="bg-yellow-50 border border-yellow-200 rounded-lg p-4 mb-4">
                    <div class="flex items-start">
                        <AlertTriangleIcon class="h-5 w-5 text-yellow-600 mr-3 mt-0.5" />
                        <div>
                            <p class="text-sm text-yellow-800 font-medium">
                                Esta accion no se puede deshacer
                            </p>
                            <p class="text-sm text-yellow-700 mt-1">
                                Se eliminara permanentemente la orden y todos sus datos asociados.
                            </p>
                        </div>
                    </div>
                </div>
                <div v-if="order" class="bg-gray-50 rounded-lg p-4">
                    <div class="space-y-2 text-sm">
                        <div class="flex justify-between">
                            <span class="text-gray-600">Numero de Orden:</span>
                            <span class="font-medium">{{ order.orderNumber }}</span>
                        </div>
                        <div class="flex justify-between">
                            <span class="text-gray-600">Cliente:</span>
                            <span class="font-medium">{{ order.client }}</span>
                        </div>
                        <div class="flex justify-between">
                            <span class="text-gray-600">Odontologo:</span>
                            <span class="font-medium">{{ order.dentist || '-' }}</span>
                        </div>
                    </div>
                </div>
                
                <!-- Error message -->
                <div v-if="error" class="mt-4 bg-red-50 border border-red-200 rounded-lg p-3">
                    <p class="text-sm text-red-700">{{ error }}</p>
                </div>
            </div>
            
            <DialogFooter>
                <Button 
                    variant="outline" 
                    @click="$emit('update:open', false)"
                    :disabled="loading"
                >
                    Cancelar
                </Button>
                <Button 
                    variant="destructive"
                    @click="$emit('confirm')"
                    :disabled="loading"
                >
                    <Trash2Icon v-if="!loading" class="h-4 w-4 mr-2" />
                    <div v-else class="animate-spin rounded-full h-4 w-4 border-b-2 border-white mr-2"></div>
                    {{ loading ? 'Eliminando...' : 'Eliminar Orden' }}
                </Button>
            </DialogFooter>
        </DialogContent>
    </Dialog>
</template>

<script setup>
import { AlertTriangleIcon, Trash2Icon } from 'lucide-vue-next'
import { Button } from '@/components/ui/button'
import { 
    Dialog, 
    DialogContent, 
    DialogDescription, 
    DialogFooter, 
    DialogHeader, 
    DialogTitle 
} from '@/components/ui/dialog'

defineProps({
    open: {
        type: Boolean,
        default: false
    },
    order: {
        type: Object,
        default: null
    },
    loading: {
        type: Boolean,
        default: false
    },
    error: {
        type: String,
        default: null
    }
})

defineEmits(['update:open', 'confirm'])
</script>