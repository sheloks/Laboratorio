<template>
    <Card class="mb-6">
        <CardContent class="pt-6">
            <div class="flex flex-col sm:flex-row gap-4">
                <div class="flex-1">
                    <div class="relative">
                        <SearchIcon class="absolute left-3 top-3 h-4 w-4 text-gray-400" />
                        <Input 
                            placeholder="Buscar por nombre, email o telefono..."
                            :model-value="searchTerm"
                            @update:model-value="$emit('update:searchTerm', $event)"
                            class="pl-10" 
                        />
                    </div>
                </div>
                <div class="flex gap-2">
                    <Select 
                        :model-value="filterType" 
                        @update:model-value="$emit('update:filterType', $event)"
                    >
                        <SelectTrigger class="w-48">
                            <FilterIcon class="h-4 w-4 mr-2" />
                            <SelectValue placeholder="Filtrar por tipo" />
                        </SelectTrigger>
                        <SelectContent>
                            <SelectItem value="all">Todos</SelectItem>
                            <SelectItem value="ODONTOLOGO">Odontologos</SelectItem>
                            <SelectItem value="CLINICA">Clinicas</SelectItem>
                            <SelectItem value="active">Activos</SelectItem>
                            <SelectItem value="inactive">Inactivos</SelectItem>
                        </SelectContent>
                    </Select>
                    <Button variant="outline" @click="$emit('export')">
                        <DownloadIcon class="h-4 w-4 mr-2" />
                        Exportar
                    </Button>
                </div>
            </div>
        </CardContent>
    </Card>
</template>

<script setup>
import { SearchIcon, FilterIcon, DownloadIcon } from 'lucide-vue-next'
import { Card, CardContent } from '@/components/ui/card'
import { Input } from '@/components/ui/input'
import { Button } from '@/components/ui/button'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'

defineProps({
    searchTerm: String,
    filterType: String
})

defineEmits(['update:searchTerm', 'update:filterType', 'export'])
</script>