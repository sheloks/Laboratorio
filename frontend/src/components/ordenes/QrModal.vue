<template>
    <Dialog :open="open" @update:open="$emit('update:open', $event)">
        <DialogContent class="max-w-sm">
            <DialogHeader>
                <DialogTitle class="text-center">Codigo QR</DialogTitle>
                <DialogDescription class="text-center">
                    {{ order?.orderNumber }}
                </DialogDescription>
            </DialogHeader>
            <div class="flex flex-col items-center py-6">
                <div v-if="loading" class="flex justify-center items-center h-64">
                    <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
                </div>
                <img v-else-if="qrUrl" :src="qrUrl" alt="QR Code" class="w-64 h-64" />
                <p class="text-xs text-gray-500 mt-4 text-center">
                    Escanea el codigo para ver los detalles de la orden
                </p>
            </div>
            <DialogFooter class="flex justify-center gap-2">
                <Button variant="outline" @click="$emit('download')">
                    <DownloadIcon class="h-4 w-4 mr-2" />
                    Descargar PNG
                </Button>
                <Button variant="outline" @click="$emit('update:open', false)">
                    Cerrar
                </Button>
            </DialogFooter>
        </DialogContent>
    </Dialog>
</template>

<script setup>
import { DownloadIcon } from 'lucide-vue-next'
import { Button } from '@/components/ui/button'
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from '@/components/ui/dialog'

defineProps({
    open: Boolean,
    order: Object,
    qrUrl: String,
    loading: Boolean
})

defineEmits(['update:open', 'download'])
</script>