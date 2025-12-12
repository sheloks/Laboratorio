<template>
    <Dialog :open="open" @update:open="$emit('update:open', $event)">
        <DialogContent class="max-w-md">
            <DialogHeader>
                <DialogTitle class="text-red-600 flex items-center gap-2">
                    <AlertTriangleIcon class="h-5 w-5" />
                    Confirmar Eliminacion
                </DialogTitle>
                <DialogDescription>
                    Esta accion no se puede deshacer
                </DialogDescription>
            </DialogHeader>
            <div class="py-4">
                <p class="text-gray-600">
                    Estas seguro de que deseas eliminar el material
                    <span class="font-semibold text-gray-900">{{ material?.name }}</span>?
                </p>
                <p v-if="material?.currentStock > 0" class="mt-2 text-amber-600 text-sm flex items-center gap-1">
                    <AlertTriangleIcon class="h-4 w-4" />
                    Este material tiene {{ material.currentStock }} {{ getUnitAbbr(material.unit) }} en stock.
                </p>

                <!-- Mostrar error si hay -->
                <div v-if="error" class="mt-3 bg-red-50 border border-red-200 rounded-lg p-3">
                    <p class="text-sm text-red-700">{{ error }}</p>
                </div>
            </div>
            <DialogFooter>
                <Button variant="outline" @click="$emit('update:open', false)" :disabled="loading">
                    Cancelar
                </Button>
                <Button variant="destructive" @click="$emit('confirm')" :disabled="loading">
                    <Trash2Icon v-if="!loading" class="h-4 w-4 mr-2" />
                    <div v-else class="animate-spin rounded-full h-4 w-4 border-b-2 border-white mr-2"></div>
                    {{ loading ? 'Eliminando...' : 'Eliminar' }}
                </Button>
            </DialogFooter>
        </DialogContent>
    </Dialog>
</template>

<script setup>
    import { AlertTriangleIcon, Trash2Icon } from 'lucide-vue-next'
    import { Button } from '@/components/ui/button'
    import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from '@/components/ui/dialog'
    import { useStockHelpers } from '@/composables/stock/useStockHelpers'

    defineProps({
        open: Boolean,
        material: Object,
        loading: Boolean,
        error: {
            type: String,
            default: null
        }
    })

    defineEmits(['update:open', 'confirm'])

    const { getUnitAbbr } = useStockHelpers()
</script>npm rn