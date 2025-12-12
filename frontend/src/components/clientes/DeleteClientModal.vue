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
                <!-- Verificando si tiene órdenes -->
                <div v-if="checking" class="flex items-center justify-center py-4 gap-2 text-gray-500">
                    <div class="animate-spin rounded-full h-5 w-5 border-b-2 border-blue-600"></div>
                    <span>Verificando ordenes asociadas...</span>
                </div>

                <!-- Tiene órdenes: mostrar opción de inactivar -->
                <div v-else-if="hasOrders" class="space-y-4">
                    <div class="bg-amber-50 border border-amber-200 rounded-lg p-4">
                        <div class="flex items-start gap-3">
                            <AlertTriangleIcon class="h-5 w-5 text-amber-600 flex-shrink-0 mt-0.5" />
                            <div>
                                <p class="text-sm font-medium text-amber-800">
                                    Este cliente tiene ordenes asociadas
                                </p>
                                <p class="text-sm text-amber-700 mt-1">
                                    No se puede eliminar. En su lugar, puedes marcarlo como <strong>inactivo</strong>.
                                </p>
                            </div>
                        </div>
                    </div>

                    <p class="text-sm text-gray-600">
                        El cliente <strong class="text-gray-900">{{ client?.name }}</strong> quedara inactivo y no aparecera en las busquedas, pero se conservaran sus ordenes.
                    </p>
                </div>

                <!-- No tiene órdenes: mostrar confirmación de eliminar -->
                <div v-else>
                    <p class="text-gray-700">
                        Estas seguro de eliminar a
                        <strong class="text-gray-900">{{ client?.name }}</strong>?
                    </p>
                    <p class="text-sm text-gray-500 mt-2">
                        Se eliminara permanentemente del sistema.
                    </p>
                </div>

                <!-- Error -->
                <div v-if="error" class="mt-4 bg-red-50 border border-red-200 rounded-lg p-3">
                    <p class="text-sm text-red-700">{{ error }}</p>
                </div>
            </div>

            <DialogFooter class="gap-2 sm:gap-2">
                <Button variant="outline" @click="$emit('update:open', false)" :disabled="loading || checking">
                    Cancelar
                </Button>

                <!-- Si tiene órdenes: botón de inactivar -->
                <Button v-if="hasOrders && !checking"
                        class="bg-amber-600 hover:bg-amber-700"
                        @click="$emit('inactivate')"
                        :disabled="loading">
                    <UserXIcon v-if="!loading" class="h-4 w-4 mr-2" />
                    <div v-else class="animate-spin rounded-full h-4 w-4 border-b-2 border-white mr-2"></div>
                    {{ loading ? 'Procesando...' : 'Marcar Inactivo' }}
                </Button>

                <!-- Si no tiene órdenes: botón de eliminar -->
                <Button v-if="!hasOrders && !checking"
                        variant="destructive"
                        @click="$emit('confirm')"
                        :disabled="loading">
                    <Trash2Icon v-if="!loading" class="h-4 w-4 mr-2" />
                    <div v-else class="animate-spin rounded-full h-4 w-4 border-b-2 border-white mr-2"></div>
                    {{ loading ? 'Eliminando...' : 'Eliminar' }}
                </Button>
            </DialogFooter>
        </DialogContent>
    </Dialog>
</template>

<script setup>
    import { AlertTriangleIcon, Trash2Icon, UserXIcon } from 'lucide-vue-next'
    import { Button } from '@/components/ui/button'
    import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from '@/components/ui/dialog'

    defineProps({
        open: Boolean,
        client: Object,
        loading: Boolean,
        checking: {
            type: Boolean,
            default: false
        },
        hasOrders: {
            type: Boolean,
            default: false
        },
        error: {
            type: String,
            default: null
        }
    })

    defineEmits(['update:open', 'confirm', 'inactivate'])
</script>