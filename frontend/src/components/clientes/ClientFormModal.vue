
<template>
    <Dialog :open="open" @update:open="$emit('update:open', $event)">
        <DialogContent class="max-w-2xl">
            <DialogHeader>
                <DialogTitle>{{ isEditing ? 'Editar Cliente' : 'Nuevo Cliente' }}</DialogTitle>
                <DialogDescription>
                    {{ isEditing ? 'Modifica los datos del cliente' : 'Complete los datos para agregar un nuevo cliente' }}
                </DialogDescription>
            </DialogHeader>

            <div class="space-y-4 py-4">
                <div class="grid grid-cols-2 gap-4">
                    <div class="space-y-2">
                        <Label for="name">Nombre / Razon Social *</Label>
                        <Input id="name" v-model="localForm.name" placeholder="Dr. Juan Perez" />
                    </div>
                    <div class="space-y-2">
                        <Label for="tipoCliente">Tipo de Cliente *</Label>
                        <Select v-model="localForm.tipoCliente">
                            <SelectTrigger>
                                <SelectValue placeholder="Seleccionar tipo" />
                            </SelectTrigger>
                            <SelectContent>
                                <SelectItem value="ODONTOLOGO">Odontologo</SelectItem>
                                <SelectItem value="CLINICA">Clinica</SelectItem>
                            </SelectContent>
                        </Select>
                    </div>
                </div>

                <div class="grid grid-cols-2 gap-4">
                    <div class="space-y-2">
                        <Label for="email">Email</Label>
                        <Input id="email" type="email" v-model="localForm.email" placeholder="correo@ejemplo.com" />
                    </div>
                    <div class="space-y-2">
                        <Label for="phone">Telefono</Label>
                        <Input id="phone" v-model="localForm.phone" placeholder="+54 11 1234-5678" />
                    </div>
                </div>

                <div class="space-y-2">
                    <Label for="address">Direccion</Label>
                    <Input id="address" v-model="localForm.address" placeholder="Av. Corrientes 1234, CABA" />
                </div>

                <!-- Campos específicos para Odontólogo -->
                <div v-if="localForm.tipoCliente === 'ODONTOLOGO'" class="border-t pt-4 mt-4">
                    <h4 class="font-medium mb-3">Datos del Profesional</h4>
                    <div class="grid grid-cols-2 gap-4">
                        <div class="space-y-2">
                            <Label for="matricula">Matricula</Label>
                            <Input id="matricula" v-model="localForm.matricula" placeholder="MP 12345" />
                        </div>
                        <div class="space-y-2">
                            <Label for="especialidad">Especialidad</Label>
                            <Input id="especialidad" v-model="localForm.especialidad" placeholder="Ortodoncia" />
                        </div>
                    </div>
                    <div class="grid grid-cols-2 gap-4 mt-4">
                        <div class="space-y-2">
                            <Label for="universidad">Universidad</Label>
                            <Input id="universidad" v-model="localForm.universidad" placeholder="UBA" />
                        </div>
                        <div class="space-y-2">
                            <Label for="anioGraduacion">Fecha de Graduacion</Label>
                            <Input id="anioGraduacion" type="number" v-model="localForm.anioGraduacion" placeholder="2020" />
                        </div>
                    </div>
                </div>

                <!-- Campos específicos para Clínica -->
                <div v-if="localForm.tipoCliente === 'CLINICA'" class="border-t pt-4 mt-4">
                    <h4 class="font-medium mb-3">Datos de la Clinica</h4>
                    <div class="grid grid-cols-2 gap-4">
                        <div class="space-y-2">
                            <Label for="razonSocial">Razon Social</Label>
                            <Input id="razonSocial" v-model="localForm.razonSocial" placeholder="Clinica Dental S.A." />
                        </div>
                        <div class="space-y-2">
                            <Label for="cuit">CUIT</Label>
                            <Input id="cuit" v-model="localForm.cuit" placeholder="30-12345678-9" />
                        </div>
                    </div>
                    <div class="space-y-2 mt-4">
                        <Label for="responsableTecnico">Responsable Tecnico</Label>
                        <Input id="responsableTecnico" v-model="localForm.responsableTecnico" placeholder="Dr. Juan Perez" />
                    </div>
                </div>

                <!-- Error message -->
                <div v-if="error" class="bg-red-50 border border-red-200 rounded-lg p-3">
                    <p class="text-red-800 text-sm">{{ error }}</p>
                </div>
            </div>

            <DialogFooter>
                <Button variant="outline" @click="$emit('update:open', false)" :disabled="saving">
                    Cancelar
                </Button>
                <Button @click="handleSave" :disabled="saving">
                    <SaveIcon v-if="!saving" class="h-4 w-4 mr-2" />
                    <div v-else class="animate-spin rounded-full h-4 w-4 border-b-2 border-white mr-2"></div>
                    {{ saving ? 'Guardando...' : 'Guardar Cliente' }}
                </Button>
            </DialogFooter>
        </DialogContent>
    </Dialog>
</template>

<script setup>
import { ref, watch } from 'vue'
import { SaveIcon } from 'lucide-vue-next'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from '@/components/ui/dialog'

const props = defineProps({
    open: Boolean,
    client: Object,
    isEditing: Boolean,
    saving: Boolean,
    error: String
})

const emit = defineEmits(['update:open', 'save'])

const getEmptyForm = () => ({
    id: null,
    name: '',
    email: '',
    phone: '',
    address: '',
    tipoCliente: '',
    matricula: '',
    especialidad: '',
    universidad: '',
    anioGraduacion: null,
    razonSocial: '',
    cuit: '',
    responsableTecnico: ''
})

const localForm = ref(getEmptyForm())

// Cargar datos cuando se abre el modal
watch(() => props.open, (isOpen) => {
    if (isOpen) {
        if (props.isEditing && props.client) {
            localForm.value = {
                id: props.client.id,
                name: props.client.name || '',
                email: props.client.email || '',
                phone: props.client.phone || '',
                address: props.client.address || '',
                tipoCliente: props.client.tipoCliente || '',
                matricula: props.client.matricula || '',
                especialidad: props.client.especialidad || '',
                universidad: props.client.universidad || '',
                anioGraduacion: props.client.anioGraduacion || null,
                razonSocial: props.client.razonSocial || '',
                cuit: props.client.cuit || '',
                responsableTecnico: props.client.responsableTecnico || ''
            }
        } else {
            localForm.value = getEmptyForm()
        }
    }
})

const handleSave = () => {
    emit('save', { ...localForm.value })
}
</script>