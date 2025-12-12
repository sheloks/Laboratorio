<template>
    <Dialog :open="open" @update:open="$emit('update:open', $event)">
        <DialogContent class="max-w-md">
            <DialogHeader>
                <DialogTitle>Detalles del Personal</DialogTitle>
            </DialogHeader>

            <div v-if="staff" class="py-4">
                <!-- Avatar y nombre -->
                <div class="flex items-center gap-4 mb-6">
                    <div :class="[
                        'w-16 h-16 rounded-full flex items-center justify-center text-white font-bold text-xl',
                        staff.activo ? 'bg-blue-500' : 'bg-gray-400'
                    ]">
                        {{ getInitials(staff.nombre) }}
                    </div>
                    <div>
                        <h3 class="text-xl font-semibold text-gray-900">{{ staff.nombre }}</h3>
                        <p class="text-gray-600">{{ staff.cargo }}</p>
                        <span :class="[
                            'inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium mt-1',
                            staff.activo ? 'bg-green-100 text-green-800' : 'bg-gray-100 text-gray-600'
                        ]">
                            {{ staff.activo ? 'Activo' : 'Inactivo' }}
                        </span>
                    </div>
                </div>

                <!-- Info -->
                <div class="space-y-4">
                    <div v-if="staff.email" class="flex items-center gap-3">
                        <div class="p-2 bg-gray-100 rounded-lg">
                            <MailIcon class="h-4 w-4 text-gray-600" />
                        </div>
                        <div>
                            <p class="text-xs text-gray-500">Email</p>
                            <p class="text-sm text-gray-900">{{ staff.email }}</p>
                        </div>
                    </div>

                    <div v-if="staff.telefono" class="flex items-center gap-3">
                        <div class="p-2 bg-gray-100 rounded-lg">
                            <PhoneIcon class="h-4 w-4 text-gray-600" />
                        </div>
                        <div>
                            <p class="text-xs text-gray-500">Telefono</p>
                            <p class="text-sm text-gray-900">{{ staff.telefono }}</p>
                        </div>
                    </div>

                    <div class="flex items-center gap-3">
                        <div class="p-2 bg-purple-100 rounded-lg">
                            <ClipboardListIcon class="h-4 w-4 text-purple-600" />
                        </div>
                        <div>
                            <p class="text-xs text-gray-500">Ordenes Asignadas</p>
                            <p class="text-sm text-gray-900 font-semibold">{{ staff.ordenesAsignadas || 0 }} ordenes</p>
                        </div>
                    </div>

                    <div v-if="staff.notas" class="pt-4 border-t">
                        <p class="text-xs text-gray-500 mb-1">Notas</p>
                        <p class="text-sm text-gray-700">{{ staff.notas }}</p>
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
    import { MailIcon, PhoneIcon, ClipboardListIcon, EditIcon } from 'lucide-vue-next'
    import { Button } from '@/components/ui/button'
    import { Dialog, DialogContent, DialogFooter, DialogHeader, DialogTitle } from '@/components/ui/dialog'

    defineProps({
        open: Boolean,
        staff: Object
    })

    defineEmits(['update:open', 'edit'])

    const getInitials = (name) => {
        if (!name) return '?'
        return name.split(' ').map(n => n[0]).join('').toUpperCase().slice(0, 2)
    }
</script>