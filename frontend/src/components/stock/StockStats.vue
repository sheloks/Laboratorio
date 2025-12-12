<template>
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
        <Card>
            <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle class="text-sm font-medium">Total Materiales</CardTitle>
                <PackageIcon class="h-4 w-4 text-muted-foreground" />
            </CardHeader>
            <CardContent>
                <div class="text-2xl font-bold">{{ stats.totalMaterials }}</div>
                <p class="text-xs text-muted-foreground">
                    {{ stats.categoriesCount }} categorias
                </p>
            </CardContent>
        </Card>

        <Card>
            <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle class="text-sm font-medium">Valor del Inventario</CardTitle>
                <DollarSignIcon class="h-4 w-4 text-muted-foreground" />
            </CardHeader>
            <CardContent>
                <div class="text-2xl font-bold">${{ formatCurrency(stats.totalValue) }}</div>
                <p class="text-xs text-muted-foreground">
                    Costo total en stock
                </p>
            </CardContent>
        </Card>

        <Card>
            <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle class="text-sm font-medium">Stock Bajo</CardTitle>
                <AlertTriangleIcon class="h-4 w-4 text-yellow-500" />
            </CardHeader>
            <CardContent>
                <div class="text-2xl font-bold text-yellow-600">{{ stats.lowStockCount }}</div>
                <p class="text-xs text-muted-foreground">
                    Requieren reposicion
                </p>
            </CardContent>
        </Card>

        <Card>
            <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle class="text-sm font-medium">Sin Stock</CardTitle>
                <AlertCircleIcon class="h-4 w-4 text-red-500" />
            </CardHeader>
            <CardContent>
                <div class="text-2xl font-bold text-red-600">{{ stats.outOfStockCount }}</div>
                <p class="text-xs text-muted-foreground">
                    Agotados
                </p>
            </CardContent>
        </Card>
    </div>
</template>

<script setup>
import { PackageIcon, DollarSignIcon, AlertTriangleIcon, AlertCircleIcon } from 'lucide-vue-next'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { useStockHelpers } from '@/composables/stock/useStockHelpers'

defineProps({
    stats: {
        type: Object,
        required: true
    }
})

const { formatCurrency } = useStockHelpers()
</script>