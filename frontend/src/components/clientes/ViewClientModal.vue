<template>
    <Dialog :open="open" @update:open="$emit('update:open', $event)">
        <DialogContent class="max-w-2xl">
            <DialogHeader>
                <DialogTitle class="flex items-center space-x-2">
                    <component :is="client?.tipoCliente === 'CLINICA' ? Building2Icon : UserIcon" class="h-5 w-5" />
                    <span>{{ client?.name }}</span>
                </DialogTitle>
                <DialogDescription>
                    Informacion completa del cliente
                </DialogDescription>
            </DialogHeader>

            <div v-if="client" class="space-y-6 py-4">
                <!-- Info General -->
                <div class="grid grid-cols-2 gap-4">
                    <div class="space-y-1">
                        <p class="text-sm text-gray-500">Tipo de Cliente</p>
                        <p class="font-medium">{{ client.tipoCliente === 'CLINICA' ? 'Clinica' : 'Odontologo' }}</p>
                    </div>
                    <div class="space-y-1">
                        <p class="text-sm text-gray-500">Estado</p>
                        <Badge :class="client.activo ? 'bg-green-100 text-green-800' : 'bg-gray-100 text-gray-800'">
                            {{ client.activo ? 'Activo' : 'Inactivo' }}
                        </Badge>
                    </div>
                </div>

                <!-- Contacto -->
                <div class="border-t pt-4">
                    <h4 class="font-medium mb-3 flex items-center">
                        <MailIcon class="h-4 w-4 mr-2" />
                        Informacion de Contacto
                    </h4>
                    <div class="grid grid-cols-2 gap-4">
                        <div class="space-y-1">
                            <p class="text-sm text-gray-500">Email</p>
                            <p class="font-medium">{{ client.email || 'No especificado' }}</p>
                        </div>
                        <div class="space-y-1">
                            <p class="text-sm text-gray-500">Telefono</p>
                            <p class="font-medium">{{ client.phone || 'No especificado' }}</p>
                        </div>
                        <div class="space-y-1 col-span-2">
                            <p class="text-sm text-gray-500">Direccion</p>
                            <p class="font-medium">{{ client.address || 'No especificada' }}</p>
                        </div>
                    </div>
                </div>

                <!-- Datos específicos Odontólogo -->
                <div v-if="client.tipoCliente === 'ODONTOLOGO'" class="border-t pt-4">
                    <h4 class="font-medium mb-3 flex items-center">
                        <UserIcon class="h-4 w-4 mr-2" />
                        Datos Profesionales
                    </h4>
                    <div class="grid grid-cols-2 gap-4">
                        <div class="space-y-1">
                            <p class="text-sm text-gray-500">Matricula</p>
                            <p class="font-medium">{{ client.matricula || 'No especificada' }}</p>
                        </div>
                        <div class="space-y-1">
                            <p class="text-sm text-gray-500">Especialidad</p>
                            <p class="font-medium">{{ client.especialidad || 'No especificada' }}</p>
                        </div>
                        <div class="space-y-1">
                            <p class="text-sm text-gray-500">Universidad</p>
                            <p class="font-medium">{{ client.universidad || 'No especificada' }}</p>
                        </div>
                        <div class="space-y-1">
                            <p class="text-sm text-gray-500">fecha de   Graduacion</p>
                            <p class="font-medium">{{ client.anioGraduacion || 'No especificado' }}</p>
                        </div>
                    </div>
                </div>

                <!-- Datos específicos Clínica -->
                <div v-if="client.tipoCliente === 'CLINICA'" class="border-t pt-4">
                    <h4 class="font-medium mb-3 flex items-center">
                        <Building2Icon class="h-4 w-4 mr-2" />
                        Datos de la Clinica
                    </h4>
                    <div class="grid grid-cols-2 gap-4">
                        <div class="space-y-1">
                            <p class="text-sm text-gray-500">Razon Social</p>
                            <p class="font-medium">{{ client.razonSocial || 'No especificada' }}</p>
                        </div>
                        <div class="space-y-1">
                            <p class="text-sm text-gray-500">CUIT</p>
                            <p class="font-medium">{{ client.cuit || 'No especificado' }}</p>
                        </div>
                        <div class="space-y-1 col-span-2">
                            <p class="text-sm text-gray-500">Responsable Tecnico</p>
                            <p class="font-medium">{{ client.responsableTecnico || 'No especificado' }}</p>
                        </div>
                    </div>
                </div>

                <!-- Estadísticas -->
                <div class="border-t pt-4">
                    <h4 class="font-medium mb-3 flex items-center">
                        <FileTextIcon class="h-4 w-4 mr-2" />
                        Estadisticas
                    </h4>
                    <div class="grid grid-cols-3 gap-4">
                        <div class="bg-gray-50 rounded-lg p-3 text-center">
                            <p class="text-2xl font-bold text-blue-600">{{ client.totalOrders || 0 }}</p>
                            <p class="text-sm text-gray-500">Ordenes</p>
                        </div>
                        <div class="bg-gray-50 rounded-lg p-3 text-center">
                            <p class="text-2xl font-bold text-green-600">${{ formatCurrency(client.totalSpent || 0) }}</p>
                            <p class="text-sm text-gray-500">Total Facturado</p>
                        </div>
                        <div class="bg-gray-50 rounded-lg p-3 text-center">
                            <p class="text-2xl font-bold text-amber-600">{{ client.workDaysThisMonth || 0 }}</p>
                            <p class="text-sm text-gray-500">Dias en {{ currentMonth }}</p>
                        </div>
                    </div>
                </div>
            </div>

            <DialogFooter>
                <Button variant="outline" @click="$emit('update:open', false)">
                    Cerrar
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
import { UserIcon, Building2Icon, MailIcon, FileTextIcon, EditIcon } from 'lucide-vue-next'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from '@/components/ui/dialog'
import { useClientHelpers } from '@/composables/clientes/useClientHelpers'

defineProps({
    open: Boolean,
    client: Object
})

defineEmits(['update:open', 'edit'])

const { formatCurrency, getCurrentMonthName } = useClientHelpers()
const currentMonth = getCurrentMonthName()
</script>