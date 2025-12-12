<template>
    <Card class="mb-6">
        <CardContent class="pt-6">
            <div class="flex flex-col sm:flex-row gap-4">
                <div class="flex-1">
                    <div class="relative">
                        <SearchIcon class="absolute left-3 top-3 h-4 w-4 text-gray-400" />
                        <Input placeholder="Buscar por numero, cliente o CUIT..."
                               :model-value="searchTerm"
                               @update:model-value="$emit('update:searchTerm', $event)"
                               class="pl-10" />
                    </div>
                </div>
                <div class="flex gap-2">
                    <Select :model-value="filterStatus"
                            @update:model-value="$emit('update:filterStatus', $event)">
                        <SelectTrigger class="w-48">
                            <FilterIcon class="h-4 w-4 mr-2" />
                            <SelectValue placeholder="Estado" />
                        </SelectTrigger>
                        <SelectContent>
                            <SelectItem value="all">Todos los estados</SelectItem>
                            <SelectItem value="pending">Pendientes</SelectItem>
                            <SelectItem value="issued">Emitidas</SelectItem>
                            <SelectItem value="paid">Pagadas</SelectItem>
                            <SelectItem value="overdue">Vencidas</SelectItem>
                            <SelectItem value="cancelled">Canceladas</SelectItem>
                        </SelectContent>
                    </Select>
                    <Select :model-value="selectedPeriod"
                            @update:model-value="$emit('update:selectedPeriod', $event)">
                        <SelectTrigger class="w-48">
                            <CalendarIcon class="h-4 w-4 mr-2" />
                            <SelectValue placeholder="Periodo" />
                        </SelectTrigger>
                        <SelectContent>
                            <SelectItem value="month">Este mes</SelectItem>
                            <SelectItem value="quarter">Este trimestre</SelectItem>
                            <SelectItem value="year">Este año</SelectItem>
                            <SelectItem value="all">Todos</SelectItem>
                        </SelectContent>
                    </Select>
                </div>
            </div>
        </CardContent>
    </Card>
</template>

<script setup>
import { SearchIcon, FilterIcon, CalendarIcon } from 'lucide-vue-next'
import { Card, CardContent } from '@/components/ui/card'
import { Input } from '@/components/ui/input'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'

defineProps({
    searchTerm: String,
    filterStatus: String,
    selectedPeriod: String
})

defineEmits(['update:searchTerm', 'update:filterStatus', 'update:selectedPeriod'])
</script>