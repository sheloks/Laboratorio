<template>
    <Dialog :open="open" @update:open="$emit('update:open', $event)">
        <DialogContent class="max-w-lg">
            <DialogHeader>
                <DialogTitle class="flex items-center">
                    <ArrowLeftRightIcon class="h-5 w-5 mr-2" />
                    Movimiento de Stock
                </DialogTitle>
                <DialogDescription>
                    Registrar entrada, salida o ajuste de inventario
                </DialogDescription>
            </DialogHeader>

            <div v-if="material" class="space-y-6 py-4">
                <!-- Info del Material -->
                <div class="bg-gray-50 rounded-lg p-4">
                    <div class="flex items-center justify-between">
                        <div>
                            <p class="font-medium">{{ material.name }}</p>
                            <p class="text-sm text-gray-500">{{ material.code }}</p>
                        </div>
                        <div class="text-right">
                            <p class="text-sm text-gray-500">Stock Actual</p>
                            <p class="text-2xl font-bold" :class="getStockLevelColor(material.currentStock, material.minimumStock)">
                                {{ material.currentStock }} {{ getUnitAbbr(material.unit) }}
                            </p>
                        </div>
                    </div>
                </div>

                <!-- Tipo de Movimiento -->
                <div class="space-y-2">
                    <Label>Tipo de Movimiento *</Label>
                    <div class="grid grid-cols-4 gap-2">
                        <Button v-for="type in movementTypes"
                                :key="type.value"
                                :variant="localForm.type === type.value ? 'default' : 'outline'"
                                size="sm"
                                @click="localForm.type = type.value"
                                class="w-full">
                            <component :is="getMovementIcon(type.value)" class="h-4 w-4 mr-1" />
                            {{ type.label }}
                        </Button>
                    </div>
                </div>

                <!-- Cantidad -->
                <div class="space-y-2">
                    <Label for="quantity">
                        {{ localForm.type === 'ajuste' ? 'Nuevo Stock *' : 'Cantidad *' }}
                    </Label>
                    <div class="flex items-center space-x-2">
                        <Input id="quantity"
                               type="number"
                               v-model="localForm.quantity"
                               min="0"
                               class="flex-1" />
                        <span class="text-gray-500">{{ getUnitLabel(material.unit) }}</span>
                    </div>

                    <!-- Preview del resultado -->
                    <div v-if="localForm.quantity && localForm.type" class="bg-blue-50 rounded-lg p-3 mt-2">
                        <div class="flex items-center justify-between text-sm">
                            <span class="text-blue-700">Stock resultante:</span>
                            <span class="font-bold text-blue-900">
                                {{ calculateNewStock }} {{ getUnitAbbr(material.unit) }}
                            </span>
                        </div>
                    </div>
                </div>

                <!-- Motivo/Referencia -->
                <div class="space-y-2">
                    <Label for="reference">Referencia / Motivo</Label>
                    <Select v-model="localForm.reason">
                        <SelectTrigger>
                            <SelectValue placeholder="Seleccionar motivo" />
                        </SelectTrigger>
                        <SelectContent>
                            <template v-if="localForm.type === 'entrada'">
                                <SelectItem value="compra">Compra</SelectItem>
                                <SelectItem value="bonificacion">Bonificacion</SelectItem>
                                <SelectItem value="transferencia">Transferencia</SelectItem>
                            </template>
                            <template v-else-if="localForm.type === 'salida'">
                                <SelectItem value="uso_produccion">Uso en produccion</SelectItem>
                                <SelectItem value="descarte">Descarte/Vencido</SelectItem>
                                <SelectItem value="rotura">Rotura</SelectItem>
                                <SelectItem value="transferencia">Transferencia</SelectItem>
                            </template>
                            <template v-else-if="localForm.type === 'ajuste'">
                                <SelectItem value="inventario">Ajuste por inventario</SelectItem>
                                <SelectItem value="correccion">Correccion de error</SelectItem>
                            </template>
                            <SelectItem value="otro">Otro</SelectItem>
                        </SelectContent>
                    </Select>
                </div>

                <!-- Documento/Comprobante -->
                <div class="grid grid-cols-2 gap-4">
                    <div class="space-y-2">
                        <Label for="document">N° Comprobante</Label>
                        <Input id="document"
                               v-model="localForm.document"
                               placeholder="Ej: FC-0001-00012345" />
                    </div>
                    <div class="space-y-2">
                        <Label for="cost">Costo Total</Label>
                        <Input id="cost"
                               type="number"
                               v-model="localForm.cost"
                               min="0"
                               placeholder="0" />
                    </div>
                </div>

                <!-- Notas -->
                <div class="space-y-2">
                    <Label for="notes">Notas</Label>
                    <Textarea id="notes"
                              v-model="localForm.notes"
                              placeholder="Observaciones adicionales..."
                              rows="2" />
                </div>
            </div>

            <DialogFooter>
                <Button variant="outline" @click="$emit('update:open', false)" :disabled="loading">
                    Cancelar
                </Button>
                <Button @click="handleSave" :disabled="loading || !canSave">
                    <SaveIcon v-if="!loading" class="h-4 w-4 mr-2" />
                    <div v-else class="animate-spin rounded-full h-4 w-4 border-b-2 border-white mr-2"></div>
                    {{ loading ? 'Registrando...' : 'Registrar Movimiento' }}
                </Button>
            </DialogFooter>
        </DialogContent>
    </Dialog>
</template>

<script setup>
    import { ref, computed, watch } from 'vue'
    import {
        ArrowLeftRightIcon,
        ArrowDownIcon,
        ArrowUpIcon,
        RefreshCwIcon,
        SaveIcon
    } from 'lucide-vue-next'
    import { Button } from '@/components/ui/button'
    import { Input } from '@/components/ui/input'
    import { Label } from '@/components/ui/label'
    import { Textarea } from '@/components/ui/textarea'
    import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
    import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from '@/components/ui/dialog'
    import { useStockHelpers } from '@/composables/stock/useStockHelpers'

    const props = defineProps({
        open: Boolean,
        material: Object,
        loading: Boolean
    })

    const emit = defineEmits(['update:open', 'save'])

    const {
        movementTypes,
        getStockLevelColor,
        getUnitAbbr,
        getUnitLabel
    } = useStockHelpers()

    const getEmptyForm = () => ({
        type: 'entrada',
        quantity: null,
        reason: '',
        document: '',
        cost: null,
        notes: ''
    })

    const localForm = ref(getEmptyForm())

    // Reset form when modal opens
    watch(() => props.open, (isOpen) => {
        if (isOpen) {
            localForm.value = getEmptyForm()
        }
    })

    // Iconos por tipo de movimiento
    const getMovementIcon = (type) => {
        const icons = {
            'entrada': ArrowDownIcon,
            'salida': ArrowUpIcon,
            'ajuste': RefreshCwIcon
        }
        return icons[type] || ArrowLeftRightIcon
    }

    // Calcular nuevo stock
    const calculateNewStock = computed(() => {
        if (!props.material || !localForm.value.quantity) return props.material?.currentStock || 0

        const qty = parseFloat(localForm.value.quantity) || 0
        const current = props.material.currentStock || 0

        switch (localForm.value.type) {
            case 'entrada':
                return current + qty
            case 'salida':
                return Math.max(0, current - qty)
            case 'ajuste':
                return qty
            default:
                return current
        }
    })

    const canSave = computed(() => {
        return localForm.value.type &&
            localForm.value.quantity !== null &&
            localForm.value.quantity >= 0
    })

    const handleSave = () => {
        emit('save', { ...localForm.value })
    }
</script>