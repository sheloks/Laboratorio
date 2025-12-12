<template>
    <Dialog :open="open" @update:open="$emit('update:open', $event)">
        <DialogContent class="max-w-lg">
            <DialogHeader>
                <DialogTitle>{{ isEditing ? 'Editar Personal' : 'Nuevo Personal' }}</DialogTitle>
                <DialogDescription>
                    {{ isEditing ? 'Modifica los datos del empleado' : 'Agrega un nuevo empleado al laboratorio' }}
                </DialogDescription>
            </DialogHeader>

            <div class="space-y-4 py-4">
                <div class="space-y-2">
                    <Label for="nombre">Nombre Completo *</Label>
                    <Input id="nombre"
                           v-model="form.nombre"
                           placeholder="Ej: Juan Martinez" />
                </div>

                <div class="space-y-2">
                    <Label for="cargo">Cargo / Especialidad *</Label>
                    <Select v-model="form.cargo">
                        <SelectTrigger>
                            <SelectValue placeholder="Seleccionar cargo" />
                        </SelectTrigger>
                        <SelectContent>
                            <SelectItem value="Ceramista">Ceramista</SelectItem>
                            <SelectItem value="Protesista">Protesista</SelectItem>
                            <SelectItem value="CAD/CAM">CAD/CAM</SelectItem>
                            <SelectItem value="Ortodoncia">Ortodoncia</SelectItem>
                            <SelectItem value="Metal">Metal</SelectItem>
                            <SelectItem value="Acrilicos">Acrilicos</SelectItem>
                            <SelectItem value="Reparaciones">Reparaciones</SelectItem>
                            <SelectItem value="Administrativo">Administrativo</SelectItem>
                            <SelectItem value="Otro">Otro</SelectItem>
                        </SelectContent>
                    </Select>
                </div>

                <div class="grid grid-cols-2 gap-4">
                    <div class="space-y-2">
                        <Label for="email">Email</Label>
                        <Input id="email"
                               type="email"
                               v-model="form.email"
                               placeholder="email@ejemplo.com" />
                    </div>
                    <div class="space-y-2">
                        <Label for="telefono">Telefono</Label>
                        <Input id="telefono"
                               v-model="form.telefono"
                               placeholder="3482-123456" />
                    </div>
                </div>

                <div class="space-y-2">
                    <Label for="notas">Notas</Label>
                    <Textarea id="notas"
                              v-model="form.notas"
                              placeholder="Observaciones adicionales..."
                              rows="2" />
                </div>

                <div v-if="isEditing" class="flex items-center space-x-2 pt-2">
                    <Checkbox id="activo" v-model:checked="form.activo" />
                    <Label for="activo" class="text-sm font-normal cursor-pointer">
                        Empleado activo
                    </Label>
                </div>
            </div>

            <DialogFooter>
                <Button variant="outline" @click="$emit('update:open', false)" :disabled="saving">
                    Cancelar
                </Button>
                <Button @click="handleSave" :disabled="!canSave || saving">
                    <SaveIcon v-if="!saving" class="h-4 w-4 mr-2" />
                    <div v-else class="animate-spin rounded-full h-4 w-4 border-b-2 border-white mr-2"></div>
                    {{ saving ? 'Guardando...' : 'Guardar' }}
                </Button>
            </DialogFooter>
        </DialogContent>
    </Dialog>
</template>

<script setup>
    import { ref, watch, computed } from 'vue'
    import { SaveIcon } from 'lucide-vue-next'
    import { Button } from '@/components/ui/button'
    import { Input } from '@/components/ui/input'
    import { Label } from '@/components/ui/label'
    import { Textarea } from '@/components/ui/textarea'
    import { Checkbox } from '@/components/ui/checkbox'
    import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
    import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from '@/components/ui/dialog'

    const props = defineProps({
        open: Boolean,
        staff: Object,
        isEditing: Boolean,
        saving: Boolean
    })

    const emit = defineEmits(['update:open', 'save'])

    const getEmptyForm = () => ({
        nombre: '',
        cargo: '',
        email: '',
        telefono: '',
        notas: '',
        activo: true
    })

    const form = ref(getEmptyForm())

    watch(() => props.open, (isOpen) => {
        if (isOpen) {
            if (props.isEditing && props.staff) {
                form.value = {
                    id: props.staff.id,
                    nombre: props.staff.nombre || '',
                    cargo: props.staff.cargo || '',
                    email: props.staff.email || '',
                    telefono: props.staff.telefono || '',
                    notas: props.staff.notas || '',
                    activo: props.staff.activo ?? true
                }
            } else {
                form.value = getEmptyForm()
            }
        }
    })

    const canSave = computed(() => {
        return form.value.nombre?.trim() && form.value.cargo
    })

    const handleSave = () => {
        if (!canSave.value) return
        emit('save', { ...form.value })
    }
</script>