<template>
    <Card class="mb-6">
        <CardContent class="pt-6">
            <div class="flex flex-col sm:flex-row gap-4">
                <div class="flex-1">
                    <div class="relative">
                        <SearchIcon class="absolute left-3 top-3 h-4 w-4 text-gray-400" />
                        <Input placeholder="Buscar por nombre, codigo, marca o proveedor..."
                               :model-value="searchTerm"
                               @update:model-value="$emit('update:searchTerm', $event)"
                               class="pl-10" />
                    </div>
                </div>
                <div class="flex gap-2">
                    <Select :model-value="filterCategory"
                            @update:model-value="$emit('update:filterCategory', $event)">
                        <SelectTrigger class="w-44">
                            <TagIcon class="h-4 w-4 mr-2" />
                            <SelectValue placeholder="Categoria" />
                        </SelectTrigger>
                        <SelectContent>
                            <SelectItem value="all">Todas las categorias</SelectItem>
                            <SelectItem v-for="cat in categories"
                                        :key="cat.value"
                                        :value="cat.value">
                                {{ cat.label }}
                            </SelectItem>
                        </SelectContent>
                    </Select>
                    <Select :model-value="filterStatus"
                            @update:model-value="$emit('update:filterStatus', $event)">
                        <SelectTrigger class="w-40">
                            <FilterIcon class="h-4 w-4 mr-2" />
                            <SelectValue placeholder="Estado" />
                        </SelectTrigger>
                        <SelectContent>
                            <SelectItem value="all">Todos</SelectItem>
                            <SelectItem value="sin_stock">Sin stock</SelectItem>
                            <SelectItem value="critico">Critico</SelectItem>
                            <SelectItem value="bajo">Stock bajo</SelectItem>
                            <SelectItem value="normal">Normal</SelectItem>
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
import { SearchIcon, TagIcon, FilterIcon, DownloadIcon } from 'lucide-vue-next'
import { Card, CardContent } from '@/components/ui/card'
import { Input } from '@/components/ui/input'
import { Button } from '@/components/ui/button'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { useStockHelpers } from '@/composables/stock/useStockHelpers'

defineProps({
    searchTerm: String,
    filterCategory: String,
    filterStatus: String
})

defineEmits(['update:searchTerm', 'update:filterCategory', 'update:filterStatus', 'export'])

const { categories } = useStockHelpers()
</script>