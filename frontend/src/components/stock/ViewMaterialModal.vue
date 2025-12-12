<template>
    <Dialog :open="open" @update:open="$emit('update:open', $event)">
        <DialogContent class="max-w-3xl max-h-[80vh]">
            <DialogHeader>
                <DialogTitle class="flex items-center space-x-2">
                    <PackageIcon class="h-5 w-5" />
                    <span>{{ material?.name }}</span>
                </DialogTitle>
                <DialogDescription>
                    Codigo: {{ material?.code }}
                </DialogDescription>
            </DialogHeader>

            <div v-if="material" class="py-2">
                <!-- Estado del Stock -->
                <div class="flex items-center space-x-3 mb-4">
                    <span :class="['px-3 py-1 rounded-full text-sm font-medium', stockStatusClass]">
                        {{ stockStatusLabel }}
                    </span>
                    <Badge variant="outline">
                        {{ getCategoryLabel(material.category) }}
                    </Badge>
                </div>

                <!-- Layout horizontal: 2 columnas -->
                <div class="grid grid-cols-2 gap-6">
                    <!-- Columna Izquierda -->
                    <div class="space-y-4">
                        <!-- Información General -->
                        <div class="grid grid-cols-2 gap-3">
                            <div>
                                <p class="text-xs text-gray-500">Descripcion</p>
                                <p class="font-medium text-sm">{{ material.description || '-' }}</p>
                            </div>
                            <div>
                                <p class="text-xs text-gray-500">Ubicacion</p>
                                <p class="font-medium text-sm">{{ material.location || '-' }}</p>
                            </div>
                            <div>
                                <p class="text-xs text-gray-500">Marca</p>
                                <p class="font-medium text-sm">{{ material.brand || '-' }}</p>
                            </div>
                            <div>
                                <p class="text-xs text-gray-500">Proveedor</p>
                                <p class="font-medium text-sm">{{ material.supplier || '-' }}</p>
                            </div>
                        </div>

                        <!-- Precios -->
                        <div class="border-t pt-3">
                            <h4 class="font-medium mb-2 flex items-center text-sm">
                                <DollarSignIcon class="h-4 w-4 mr-1" />
                                Informacion de Costos
                            </h4>
                            <div class="grid grid-cols-2 gap-3">
                                <div>
                                    <p class="text-xs text-gray-500">Precio de Costo</p>
                                    <p class="text-base font-bold">{{ formatPrice(material.costPrice) }}</p>
                                    <p class="text-xs text-gray-500">por {{ getUnitLabel(material.unit) }}</p>
                                </div>
                                <div>
                                    <p class="text-xs text-gray-500">Valor en Stock</p>
                                    <p class="text-base font-bold text-green-600">
                                        {{ formatPrice(material.currentStock * material.costPrice) }}
                                    </p>
                                    <p class="text-xs text-gray-500">{{ formatNumber(material.currentStock) }} x {{ formatPrice(material.costPrice) }}</p>
                                </div>
                            </div>
                        </div>

                        <!-- Fechas -->
                        <div class="border-t pt-3">
                            <div class="grid grid-cols-3 gap-2">
                                <div>
                                    <p class="text-xs text-gray-500">Ultima Compra</p>
                                    <p class="text-sm">{{ formatDateSimple(material.lastPurchaseDate) }}</p>
                                </div>
                                <div>
                                    <p class="text-xs text-gray-500">Vencimiento</p>
                                    <p :class="[isExpiringSoon(material.expirationDate) ? 'text-red-600 font-medium' : '', 'text-sm']">
                                        {{ formatDateSimple(material.expirationDate) }}
                                    </p>
                                </div>
                                <div>
                                    <p class="text-xs text-gray-500">Actualizado</p>
                                    <p class="text-sm">{{ formatDateSimple(material.updatedAt) }}</p>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Columna Derecha: Stock -->
                    <div class="space-y-3">
                        <h4 class="font-medium flex items-center text-sm">
                            <PackageIcon class="h-4 w-4 mr-1" />
                            Niveles de Stock
                        </h4>

                        <div class="grid grid-cols-3 gap-2">
                            <div class="bg-gray-50 rounded-lg p-2 text-center">
                                <p class="text-xs text-gray-500">Stock Actual</p>
                                <p class="text-lg font-bold" :class="getStockLevelColor(material.currentStock, material.minimumStock)">
                                    {{ formatNumber(material.currentStock) }}
                                </p>
                                <p class="text-xs text-gray-500">{{ getUnitLabel(material.unit) }}</p>
                            </div>
                            <div class="bg-yellow-50 rounded-lg p-2 text-center">
                                <p class="text-xs text-gray-500">Stock Minimo</p>
                                <p class="text-lg font-bold text-yellow-600">
                                    {{ formatNumber(material.minimumStock) }}
                                </p>
                                <p class="text-xs text-gray-500">{{ getUnitLabel(material.unit) }}</p>
                            </div>
                            <div class="bg-green-50 rounded-lg p-2 text-center">
                                <p class="text-xs text-gray-500">Stock Maximo</p>
                                <p class="text-lg font-bold text-green-600">
                                    {{ formatNumber(material.maximumStock) }}
                                </p>
                                <p class="text-xs text-gray-500">{{ getUnitLabel(material.unit) }}</p>
                            </div>
                        </div>

                        <!-- Barra de progreso visual -->
                        <div>
                            <div class="flex justify-between text-xs text-gray-500 mb-1">
                                <span>0</span>
                                <span>Min: {{ formatNumber(material.minimumStock) }}</span>
                                <span>Max: {{ formatNumber(material.maximumStock) }}</span>
                            </div>
                            <div class="w-full bg-gray-200 rounded-full h-2">
                                <div class="h-2 rounded-full transition-all"
                                     :class="getProgressBarColor(material.currentStock, material.minimumStock)"
                                     :style="{ width: getProgressPercentage(material.currentStock, material.maximumStock) + '%' }"></div>
                            </div>
                        </div>

                        <!-- Notas -->
                        <div v-if="material.notes" class="border-t pt-3">
                            <h4 class="font-medium mb-1 text-sm">Notas</h4>
                            <p class="text-gray-600 bg-gray-50 rounded-lg p-2 text-sm">{{ material.notes }}</p>
                        </div>
                    </div>
                </div>
            </div>

            <DialogFooter>
                <Button variant="outline" @click="$emit('update:open', false)">
                    Cerrar
                </Button>
                <Button variant="outline" @click="$emit('movement')">
                    <ArrowLeftRightIcon class="h-4 w-4 mr-2" />
                    Movimiento
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
    import { computed } from 'vue'
    import { PackageIcon, DollarSignIcon, ArrowLeftRightIcon, EditIcon } from 'lucide-vue-next'
    import { Button } from '@/components/ui/button'
    import { Badge } from '@/components/ui/badge'
    import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from '@/components/ui/dialog'
    import { useStockHelpers } from '@/composables/stock/useStockHelpers'

    const props = defineProps({
        open: Boolean,
        material: Object
    })

    defineEmits(['update:open', 'edit', 'movement'])

    const {
        getStockLevelColor,
        getCategoryLabel,
        getUnitLabel
    } = useStockHelpers()

    // Estado del stock sin simbolos raros
    const stockStatusLabel = computed(() => {
        if (!props.material) return ''
        const current = props.material.currentStock
        const minimum = props.material.minimumStock

        if (current === 0) return 'Sin Stock'
        if (current <= minimum * 0.5) return 'Critico'
        if (current <= minimum) return 'Bajo'
        return 'Optimo'
    })

    const stockStatusClass = computed(() => {
        if (!props.material) return ''
        const current = props.material.currentStock
        const minimum = props.material.minimumStock

        if (current === 0) return 'bg-red-100 text-red-800'
        if (current <= minimum * 0.5) return 'bg-red-100 text-red-800'
        if (current <= minimum) return 'bg-yellow-100 text-yellow-800'
        return 'bg-green-100 text-green-800'
    })

    // Formato de numero simple
    const formatNumber = (value) => {
        if (value === null || value === undefined) return '0'
        return Number(value).toLocaleString('es-AR')
    }

    // Formato de precio limpio
    const formatPrice = (value) => {
        if (value === null || value === undefined) return '$0'
        return '$' + Number(value).toLocaleString('es-AR', {
            minimumFractionDigits: 0,
            maximumFractionDigits: 0
        })
    }

    // Formato de fecha simple
    const formatDateSimple = (date) => {
        if (!date) return '-'
        try {
            const d = new Date(date)
            return d.toLocaleDateString('es-AR')
        } catch {
            return '-'
        }
    }

    const getProgressPercentage = (current, max) => {
        if (!max || max === 0) return 0
        return Math.min((current / max) * 100, 100)
    }

    const getProgressBarColor = (current, minimum) => {
        if (!minimum || minimum === 0) return 'bg-gray-400'
        const ratio = current / minimum
        if (current === 0) return 'bg-red-500'
        if (ratio <= 0.5) return 'bg-red-500'
        if (ratio <= 1) return 'bg-yellow-500'
        return 'bg-green-500'
    }

    const isExpiringSoon = (date) => {
        if (!date) return false
        const expDate = new Date(date)
        const today = new Date()
        const diffDays = Math.ceil((expDate - today) / (1000 * 60 * 60 * 24))
        return diffDays <= 90 && diffDays > 0
    }
</script>