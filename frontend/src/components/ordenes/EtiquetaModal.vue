<template>
    <Dialog :open="open" @update:open="$emit('update:open', $event)">
        <DialogContent class="max-w-md">
            <DialogHeader>
                <DialogTitle>Generar Etiqueta de Envio</DialogTitle>
                <DialogDescription>
                    Orden: {{ order?.orderNumber }}
                </DialogDescription>
            </DialogHeader>
            <div class="space-y-4 py-4">
                <div class="space-y-2">
                    <Label for="etiquetaNombre">Dr./Dra. Nombre Completo *</Label>
                    <Input 
                        id="etiquetaNombre" 
                        :model-value="form.nombre"
                        @update:model-value="updateField('nombre', $event)"
                        placeholder="Ej: Dr. Juan Perez" 
                    />
                </div>
                <div class="space-y-2">
                    <Label for="etiquetaDireccion">Direccion *</Label>
                    <Input 
                        id="etiquetaDireccion" 
                        :model-value="form.direccion"
                        @update:model-value="updateField('direccion', $event)"
                        placeholder="Ej: Av. Corrientes 1234, Piso 5" 
                    />
                </div>
                <div class="grid grid-cols-2 gap-4">
                    <div class="space-y-2">
                        <Label for="etiquetaCiudad">Ciudad *</Label>
                        <Input 
                            id="etiquetaCiudad" 
                            :model-value="form.ciudad"
                            @update:model-value="updateField('ciudad', $event)"
                            placeholder="Ej: Buenos Aires" 
                        />
                    </div>
                    <div class="space-y-2">
                        <Label for="etiquetaCp">Codigo Postal</Label>
                        <Input 
                            id="etiquetaCp" 
                            :model-value="form.codigoPostal"
                            @update:model-value="updateField('codigoPostal', $event)"
                            placeholder="Ej: 1043" 
                        />
                    </div>
                </div>
                <div class="space-y-2">
                    <Label for="etiquetaTelefono">Celular / Telefono</Label>
                    <Input 
                        id="etiquetaTelefono" 
                        :model-value="form.telefono"
                        @update:model-value="updateField('telefono', $event)"
                        placeholder="Ej: +54 9 11 1234-5678" 
                    />
                </div>
            </div>
            <DialogFooter>
                <Button variant="outline" @click="$emit('update:open', false)" :disabled="loading">
                    Cancelar
                </Button>
                <Button @click="$emit('generate')" :disabled="!canGenerate || loading">
                    <PrinterIcon v-if="!loading" class="h-4 w-4 mr-2" />
                    <div v-else class="animate-spin rounded-full h-4 w-4 border-b-2 border-white mr-2"></div>
                    {{ loading ? 'Generando...' : 'Generar Etiqueta' }}
                </Button>
            </DialogFooter>
        </DialogContent>
    </Dialog>
</template>

<script setup>
import { computed } from 'vue'
import { PrinterIcon } from 'lucide-vue-next'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from '@/components/ui/dialog'

const props = defineProps({
    open: Boolean,
    order: Object,
    form: Object,
    loading: Boolean
})

const emit = defineEmits(['update:open', 'update:form', 'generate'])

const canGenerate = computed(() => {
    return props.form?.nombre && props.form?.direccion && props.form?.ciudad
})

const updateField = (field, value) => {
    emit('update:form', { ...props.form, [field]: value })
}
</script>