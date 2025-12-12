<template>
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
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
import { UsersIcon, UserIcon, Building2Icon, CheckCircleIcon } from 'lucide-vue-next'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'

const props = defineProps({
    stats: {
        type: Object,
        required: true
    }
})

const statsCards = computed(() => [
    { 
        title: 'Total Clientes', 
        value: props.stats.total, 
        icon: UsersIcon, 
        color: '', 
        description: `+${props.stats.newThisMonth} este mes` 
    },
    { 
        title: 'Clientes Activos', 
        value: props.stats.active, 
        icon: CheckCircleIcon, 
        color: 'text-green-600', 
        description: `${props.stats.total > 0 ? Math.round((props.stats.active / props.stats.total) * 100) : 0}% del total` 
    },
    { 
        title: 'Odontologos', 
        value: props.stats.odontologos, 
        icon: UserIcon, 
        color: '', 
        description: 'Profesionales independientes' 
    },
    { 
        title: 'Clinicas', 
        value: props.stats.clinicas, 
        icon: Building2Icon, 
        color: '', 
        description: 'Centros dentales' 
    }
])
</script>