<template>
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-6 mb-8">
        <Card v-for="stat in statsCards" :key="stat.title" class="bg-white">
            <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle class="text-sm font-medium">{{ stat.title }}</CardTitle>
                <component :is="stat.icon" class="h-4 w-4 text-muted-foreground" />
            </CardHeader>
            <CardContent>
                <div :class="['text-2xl font-bold', stat.color]">{{ stat.value }}</div>
                <p class="text-xs text-muted-foreground mt-1">{{ stat.description }}</p>
            </CardContent>
        </Card>
    </div>
</template>

<script setup>
import { computed } from 'vue'
import { FileTextIcon, ClockIcon, CheckCircleIcon, AlertCircleIcon } from 'lucide-vue-next'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'

const props = defineProps({
    stats: {
        type: Object,
        required: true
    }
})

const statsCards = computed(() => [
    { title: 'Total Ordenes', value: props.stats.totalOrders, icon: FileTextIcon, color: '', description: '' },
    { title: 'Pendientes', value: props.stats.pendingOrders, icon: AlertCircleIcon, color: 'text-gray-600', description: 'Por atender' },
    { title: 'En Progreso', value: props.stats.inProgressOrders, icon: ClockIcon, color: 'text-yellow-600', description: 'En produccion' },
    { title: 'Listas', value: props.stats.readyOrders, icon: CheckCircleIcon, color: 'text-blue-600', description: 'Para entregar' },
    { title: 'Urgentes', value: props.stats.urgentOrders, icon: AlertCircleIcon, color: 'text-red-600', description: 'Prioridad alta' }
])
</script>