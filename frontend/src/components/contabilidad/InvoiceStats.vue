<template>
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
        <Card>
            <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle class="text-sm font-medium">Ingresos Totales</CardTitle>
                <DollarSignIcon class="h-4 w-4 text-muted-foreground" />
            </CardHeader>
            <CardContent>
                <div class="text-2xl font-bold">${{ formatCurrency(stats.totalRevenue) }}</div>
                <p class="text-xs text-muted-foreground flex items-center">
                    <ArrowUpRightIcon class="h-3 w-3 mr-1 text-green-600" />
                    +{{ stats.monthlyGrowth }}% vs mes anterior
                </p>
            </CardContent>
        </Card>

        <Card>
            <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle class="text-sm font-medium">Facturas Pagadas</CardTitle>
                <CheckCircleIcon class="h-4 w-4 text-muted-foreground" />
            </CardHeader>
            <CardContent>
                <div class="text-2xl font-bold text-green-600">{{ stats.paidCount }}</div>
                <p class="text-xs text-muted-foreground">
                    ${{ formatCurrency(stats.paidAmount) }} cobrado
                </p>
            </CardContent>
        </Card>

        <Card>
            <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle class="text-sm font-medium">Facturas Pendientes</CardTitle>
                <ClockIcon class="h-4 w-4 text-muted-foreground" />
            </CardHeader>
            <CardContent>
                <div class="text-2xl font-bold text-yellow-600">{{ stats.pendingCount }}</div>
                <p class="text-xs text-muted-foreground">
                    ${{ formatCurrency(stats.pendingAmount) }} por cobrar
                </p>
            </CardContent>
        </Card>

        <Card>
            <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle class="text-sm font-medium">Facturas Vencidas</CardTitle>
                <AlertCircleIcon class="h-4 w-4 text-muted-foreground" />
            </CardHeader>
            <CardContent>
                <div class="text-2xl font-bold text-red-600">{{ stats.overdueCount }}</div>
                <p class="text-xs text-muted-foreground">
                    ${{ formatCurrency(stats.overdueAmount) }} vencido
                </p>
            </CardContent>
        </Card>
    </div>
</template>

<script setup>
import { DollarSignIcon, CheckCircleIcon, ClockIcon, AlertCircleIcon, ArrowUpRightIcon } from 'lucide-vue-next'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { useInvoiceHelpers } from '@/composables/contabilidad/useInvoiceHelpers'

defineProps({
    stats: {
        type: Object,
        required: true
    }
})

const { formatCurrency } = useInvoiceHelpers()
</script>