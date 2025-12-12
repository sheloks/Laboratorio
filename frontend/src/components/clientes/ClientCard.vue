<template>
    <div class="border rounded-lg p-4 hover:bg-gray-50 transition-colors">
        <div class="flex items-center justify-between">
            <div class="flex items-center space-x-4">
                <div :class="['p-2 rounded-lg', client.tipoCliente === 'CLINICA' ? 'bg-amber-100' : 'bg-blue-100']">
                    <Building2Icon v-if="client.tipoCliente === 'CLINICA'" class="h-6 w-6 text-amber-600" />
                    <UserIcon v-else class="h-6 w-6 text-blue-600" />
                </div>
                <div>
                    <div class="flex items-center space-x-2 mb-1">
                        <h3 class="font-medium">{{ client.name }}</h3>
                        <Badge :class="client.activo ? 'bg-green-100 text-green-800' : 'bg-gray-100 text-gray-800'">
                            {{ client.activo ? 'Activo' : 'Inactivo' }}
                        </Badge>
                        <Badge variant="outline">
                            {{ client.tipoCliente === 'CLINICA' ? 'Clinica' : 'Odontologo' }}
                        </Badge>
                    </div>
                    <div class="text-sm text-gray-600 space-y-1">
                        <div class="flex items-center space-x-4">
                            <span class="flex items-center">
                                <MailIcon class="h-3 w-3 mr-1" />
                                {{ client.email || 'Sin email' }}
                            </span>
                            <span class="flex items-center">
                                <PhoneIcon class="h-3 w-3 mr-1" />
                                {{ client.phone || 'Sin telefono' }}
                            </span>
                        </div>
                        <div class="flex items-center">
                            <span class="flex items-center">
                                <MapPinIcon class="h-3 w-3 mr-1" />
                                {{ client.address || 'Sin direccion' }}
                            </span>
                        </div>
                    </div>
                </div>
            </div>

            <div class="flex items-center space-x-4">
                <div class="text-right">
                    <p class="font-medium">{{ client.totalOrders || 0 }} ordenes</p>
                    <p class="text-sm text-gray-600">${{ formatCurrency(client.totalSpent || 0) }}</p>
                    <p class="text-xs text-gray-500 flex items-center justify-end">
                        <CalendarIcon class="h-3 w-3 mr-1" />
                        {{ client.workDaysThisMonth || 0 }} dias en {{ currentMonth }}
                    </p>
                </div>
                <div class="flex space-x-2">
                    <Button variant="outline" size="sm" @click="$emit('view', client)">
                        <EyeIcon class="h-4 w-4 mr-1" />
                        Ver
                    </Button>
                    <Button variant="outline" size="sm" @click="$emit('edit', client)">
                        <EditIcon class="h-4 w-4 mr-1" />
                        Editar
                    </Button>
                    <Button variant="outline" size="sm" @click="$emit('orders', client)">
                        <FileTextIcon class="h-4 w-4 mr-1" />
                        Ordenes
                    </Button>
                    <Button variant="outline"
                            size="sm"
                            class="text-red-600 hover:text-red-700 hover:bg-red-50"
                            @click="$emit('delete', client)">
                        <Trash2Icon class="h-4 w-4" />
                    </Button>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import {
    UserIcon,
    Building2Icon,
    MailIcon,
    PhoneIcon,
    MapPinIcon,
    CalendarIcon,
    EyeIcon,
    EditIcon,
    FileTextIcon,
    Trash2Icon
} from 'lucide-vue-next'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { useClientHelpers } from '@/composables/clientes/useClientHelpers'

defineProps({
    client: {
        type: Object,
        required: true
    }
})

defineEmits(['view', 'edit', 'orders', 'delete'])

const { formatCurrency, getCurrentMonthName } = useClientHelpers()
const currentMonth = getCurrentMonthName()
</script>