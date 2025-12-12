<template>
    <Dialog :open="open" @update:open="$emit('update:open', $event)">
        <DialogContent class="max-w-3xl max-h-[90vh] overflow-y-auto">
            <DialogHeader>
                <DialogTitle>Nueva Factura</DialogTitle>
                <DialogDescription>
                    Ingresa el numero de orden para generar la factura
                </DialogDescription>
            </DialogHeader>

            <div class="space-y-6 py-4">
                <!-- Sección 1: Número de Orden -->
                <div class="space-y-4">
                    <h3 class="font-medium text-gray-900 flex items-center">
                        <FileTextIcon class="h-4 w-4 mr-2" />
                        Numero de Orden
                    </h3>

                    <div class="flex gap-3">
                        <div class="flex-1">
                            <Input v-model="orderNumber"
                                   placeholder="Ej: ORD-2024-001"
                                   class="h-11 font-mono text-base"
                                   :disabled="!!selectedOrder"
                                   @keyup.enter="verifyOrder" />
                        </div>
                        <Button v-if="!selectedOrder"
                                @click="verifyOrder"
                                :disabled="verifying || !orderNumber.trim()"
                                class="px-6">
                            <SearchIcon v-if="!verifying" class="h-4 w-4 mr-2" />
                            <div v-else class="animate-spin rounded-full h-4 w-4 border-b-2 border-white mr-2"></div>
                            {{ verifying ? 'Verificando...' : 'Verificar' }}
                        </Button>
                        <Button v-else
                                variant="outline"
                                @click="clearOrder">
                            <XIcon class="h-4 w-4 mr-2" />
                            Cambiar
                        </Button>
                    </div>

                    <!-- Error -->
                    <div v-if="verifyError" class="bg-red-50 border border-red-200 rounded-lg p-4">
                        <div class="flex items-center text-red-700">
                            <AlertCircleIcon class="h-5 w-5 mr-2 flex-shrink-0" />
                            <span>{{ verifyError }}</span>
                        </div>
                    </div>

                    <!-- Orden verificada -->
                    <div v-if="selectedOrder" class="bg-green-50 border border-green-200 rounded-lg p-4">
                        <div class="flex items-center gap-2 mb-3">
                            <CheckCircle2Icon class="h-5 w-5 text-green-600" />
                            <span class="font-semibold text-green-800">Orden verificada correctamente</span>
                        </div>
                        <div class="grid grid-cols-2 gap-x-8 gap-y-2 text-sm">
                            <div class="flex justify-between">
                                <span class="text-gray-500">Numero:</span>
                                <span class="font-medium font-mono">{{ selectedOrder.orderNumber }}</span>
                            </div>
                            <div class="flex justify-between">
                                <span class="text-gray-500">Cliente:</span>
                                <span class="font-medium">{{ selectedOrder.client }}</span>
                            </div>
                            <div class="flex justify-between">
                                <span class="text-gray-500">Fecha entrega:</span>
                                <span class="font-medium">{{ formatDate(selectedOrder.deliveryDate) }}</span>
                            </div>
                            <div class="flex justify-between">
                                <span class="text-gray-500">Monto:</span>
                                <span class="font-medium text-green-700">${{ formatNumber(selectedOrder.totalAmount) }}</span>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- El resto del formulario solo se muestra si hay orden verificada -->
                <template v-if="selectedOrder">
                    <!-- Sección 2: Datos Fiscales -->
                    <div class="space-y-4 border-t pt-4">
                        <h3 class="font-medium text-gray-900 flex items-center">
                            <BuildingIcon class="h-4 w-4 mr-2" />
                            Datos Fiscales
                        </h3>
                        <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
                            <div class="space-y-2">
                                <Label for="puntoVenta">Punto de Venta *</Label>
                                <Select v-model="localForm.puntoVenta">
                                    <SelectTrigger>
                                        <SelectValue placeholder="Seleccionar" />
                                    </SelectTrigger>
                                    <SelectContent>
                                        <SelectItem value="1">0001</SelectItem>
                                        <SelectItem value="2">0002</SelectItem>
                                    </SelectContent>
                                </Select>
                            </div>
                            <div class="space-y-2">
                                <Label for="condicionIva">Condicion IVA Cliente *</Label>
                                <Select v-model="localForm.condicionIva" @update:model-value="updateInvoiceType">
                                    <SelectTrigger>
                                        <SelectValue placeholder="Seleccionar" />
                                    </SelectTrigger>
                                    <SelectContent>
                                        <SelectItem value="RI">Responsable Inscripto</SelectItem>
                                        <SelectItem value="MONO">Monotributista</SelectItem>
                                        <SelectItem value="EX">Exento</SelectItem>
                                        <SelectItem value="CF">Consumidor Final</SelectItem>
                                    </SelectContent>
                                </Select>
                            </div>
                            <div class="space-y-2">
                                <Label for="invoiceType">Tipo de Factura</Label>
                                <Input :value="localForm.invoiceType ? `Factura ${localForm.invoiceType}` : ''" disabled class="bg-gray-50" />
                            </div>
                            <div class="space-y-2">
                                <Label for="clientCuit">CUIT/CUIL *</Label>
                                <Input id="clientCuit" v-model="localForm.clientCuit" placeholder="30-12345678-9" />
                            </div>
                            <div class="space-y-2">
                                <Label for="clientAddress">Domicilio</Label>
                                <Input id="clientAddress" v-model="localForm.clientAddress" placeholder="Direccion del cliente" />
                            </div>
                            <div class="space-y-2">
                                <Label for="dueDate">Fecha de Vencimiento</Label>
                                <Input id="dueDate" type="date" v-model="localForm.dueDate" />
                            </div>
                        </div>
                    </div>

                    <!-- Sección 3: Detalle de Trabajos -->
                    <div class="space-y-4 border-t pt-4">
                        <div class="flex items-center justify-between">
                            <h3 class="font-medium text-gray-900 flex items-center">
                                <ClipboardListIcon class="h-4 w-4 mr-2" />
                                Detalle de Trabajos
                            </h3>
                            <Button variant="outline" size="sm" @click="addWorkItem">
                                <PlusIcon class="h-4 w-4 mr-1" />
                                Agregar
                            </Button>
                        </div>

                        <div v-if="localForm.workItems.length === 0" class="text-center py-4 text-gray-500 bg-gray-50 rounded-lg">
                            <p>No hay trabajos agregados</p>
                        </div>

                        <div v-else class="space-y-3">
                            <div v-for="(item, index) in localForm.workItems" :key="index" class="grid grid-cols-12 gap-2 items-end bg-gray-50 p-3 rounded-lg">
                                <div class="col-span-3 space-y-1">
                                    <Label class="text-xs">Trabajo</Label>
                                    <Select v-model="item.type">
                                        <SelectTrigger class="h-9">
                                            <SelectValue placeholder="Tipo" />
                                        </SelectTrigger>
                                        <SelectContent>
                                            <SelectItem value="corona">Corona</SelectItem>
                                            <SelectItem value="puente">Puente</SelectItem>
                                            <SelectItem value="protesis_total">Protesis Total</SelectItem>
                                            <SelectItem value="protesis_parcial">Protesis Parcial</SelectItem>
                                            <SelectItem value="incrustacion">Incrustacion</SelectItem>
                                            <SelectItem value="carilla">Carilla</SelectItem>
                                            <SelectItem value="provisional">Provisional</SelectItem>
                                            <SelectItem value="reparacion">Reparacion</SelectItem>
                                            <SelectItem value="otro">Otro</SelectItem>
                                        </SelectContent>
                                    </Select>
                                </div>
                                <div class="col-span-2 space-y-1">
                                    <Label class="text-xs">Material</Label>
                                    <Select v-model="item.material">
                                        <SelectTrigger class="h-9">
                                            <SelectValue placeholder="Material" />
                                        </SelectTrigger>
                                        <SelectContent>
                                            <SelectItem value="zirconio">Zirconio</SelectItem>
                                            <SelectItem value="disilicato">Disilicato de Litio</SelectItem>
                                            <SelectItem value="metal_porcelana">Metal Porcelana</SelectItem>
                                            <SelectItem value="acrilico">Acrilico</SelectItem>
                                            <SelectItem value="cromo_cobalto">Cromo Cobalto</SelectItem>
                                            <SelectItem value="resina">Resina</SelectItem>
                                            <SelectItem value="titanio">Titanio</SelectItem>
                                            <SelectItem value="otro">Otro</SelectItem>
                                        </SelectContent>
                                    </Select>
                                </div>
                                <div class="col-span-2 space-y-1">
                                    <Label class="text-xs">Piezas</Label>
                                    <Input v-model="item.teeth" placeholder="11,12,13" class="h-9" />
                                </div>
                                <div class="col-span-1 space-y-1">
                                    <Label class="text-xs">Cant.</Label>
                                    <Input type="number" v-model="item.quantity" min="1" class="h-9" @input="calculateTotals" />
                                </div>
                                <div class="col-span-2 space-y-1">
                                    <Label class="text-xs">Precio Unit.</Label>
                                    <Input type="number" v-model="item.unitPrice" placeholder="0" class="h-9" @input="calculateTotals" />
                                </div>
                                <div class="col-span-1 space-y-1">
                                    <Label class="text-xs">Subtotal</Label>
                                    <div class="h-9 flex items-center font-medium text-sm">
                                        ${{ formatNumber(item.quantity * item.unitPrice) }}
                                    </div>
                                </div>
                                <div class="col-span-1">
                                    <Button variant="ghost" size="sm" @click="removeWorkItem(index)" class="h-9 w-9 p-0 text-red-500 hover:text-red-700">
                                        <Trash2Icon class="h-4 w-4" />
                                    </Button>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Sección 4: Forma de Pago -->
                    <div class="space-y-4 border-t pt-4">
                        <h3 class="font-medium text-gray-900 flex items-center">
                            <CreditCardIcon class="h-4 w-4 mr-2" />
                            Forma de Pago
                        </h3>
                        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                            <div class="space-y-2">
                                <Label>Metodo de Pago</Label>
                                <Select v-model="localForm.paymentMethod">
                                    <SelectTrigger>
                                        <SelectValue placeholder="Seleccionar" />
                                    </SelectTrigger>
                                    <SelectContent>
                                        <SelectItem value="efectivo">Efectivo</SelectItem>
                                        <SelectItem value="transferencia">Transferencia</SelectItem>
                                        <SelectItem value="cheque">Cheque</SelectItem>
                                        <SelectItem value="tarjeta">Tarjeta</SelectItem>
                                        <SelectItem value="cuenta_corriente">Cuenta Corriente</SelectItem>
                                    </SelectContent>
                                </Select>
                            </div>
                            <div class="space-y-2">
                                <Label>Condicion de Pago</Label>
                                <Select v-model="localForm.paymentCondition">
                                    <SelectTrigger>
                                        <SelectValue placeholder="Seleccionar" />
                                    </SelectTrigger>
                                    <SelectContent>
                                        <SelectItem value="contado">Contado</SelectItem>
                                        <SelectItem value="30_dias">30 dias</SelectItem>
                                        <SelectItem value="60_dias">60 dias</SelectItem>
                                        <SelectItem value="90_dias">90 dias</SelectItem>
                                    </SelectContent>
                                </Select>
                            </div>
                        </div>
                    </div>

                    <!-- Sección 5: Totales -->
                    <div class="border-t pt-4">
                        <h3 class="font-medium text-gray-900 mb-3">Resumen</h3>
                        <div class="bg-gray-50 rounded-lg p-4 space-y-2">
                            <div class="flex justify-between text-sm">
                                <span>Subtotal ({{ localForm.workItems.length }} items)</span>
                                <span>${{ formatNumber(localForm.netAmount) }}</span>
                            </div>
                            <div class="flex justify-between text-sm">
                                <span>IVA 21%</span>
                                <span>${{ formatNumber(localForm.ivaAmount) }}</span>
                            </div>
                            <div class="flex justify-between font-bold text-lg border-t pt-2 mt-2">
                                <span>TOTAL</span>
                                <span class="text-green-600">${{ formatNumber(localForm.totalAmount) }}</span>
                            </div>
                        </div>
                    </div>

                    <!-- Observaciones -->
                    <div class="space-y-2">
                        <Label for="notes">Observaciones</Label>
                        <Textarea id="notes" v-model="localForm.notes" placeholder="Notas adicionales para la factura..." rows="2" />
                    </div>
                </template>
            </div>

            <DialogFooter>
                <Button variant="outline" @click="$emit('update:open', false)" :disabled="saving">
                    Cancelar
                </Button>
                <Button @click="handleSave" :disabled="saving || !canSave">
                    <SaveIcon v-if="!saving" class="h-4 w-4 mr-2" />
                    <div v-else class="animate-spin rounded-full h-4 w-4 border-b-2 border-white mr-2"></div>
                    {{ saving ? 'Guardando...' : 'Crear Factura' }}
                </Button>
            </DialogFooter>
        </DialogContent>
    </Dialog>
</template>

<script setup>
    import { ref, computed, watch } from 'vue'
    import {
        SaveIcon,
        SearchIcon,
        FileTextIcon,
        BuildingIcon,
        ClipboardListIcon,
        CreditCardIcon,
        PlusIcon,
        Trash2Icon,
        AlertCircleIcon,
        CheckCircle2Icon,
        XIcon
    } from 'lucide-vue-next'
    import { Button } from '@/components/ui/button'
    import { Input } from '@/components/ui/input'
    import { Label } from '@/components/ui/label'
    import { Textarea } from '@/components/ui/textarea'
    import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
    import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from '@/components/ui/dialog'
    import { orderService } from '@/services/orderService'

    const props = defineProps({
        open: Boolean,
        saving: Boolean
    })

    const emit = defineEmits(['update:open', 'save'])

    // Estado de verificación
    const orderNumber = ref('')
    const verifying = ref(false)
    const verifyError = ref('')
    const selectedOrder = ref(null)

    // Helper - debe estar antes de usarse
    const getDefaultDueDate = () => {
        const date = new Date()
        date.setDate(date.getDate() + 30)
        return date.toISOString().split('T')[0]
    }

    const getEmptyForm = () => ({
        orderId: '',
        orderNumber: '',
        invoiceType: '',
        puntoVenta: '1',
        condicionIva: '',
        clientCuit: '',
        clientAddress: '',
        dueDate: getDefaultDueDate(),
        paymentMethod: 'efectivo',
        paymentCondition: 'contado',
        workItems: [],
        netAmount: 0,
        ivaAmount: 0,
        totalAmount: 0,
        notes: ''
    })

    const localForm = ref(getEmptyForm())

    // Reset form when modal opens
    watch(() => props.open, (isOpen) => {
        if (isOpen) {
            localForm.value = getEmptyForm()
            selectedOrder.value = null
            orderNumber.value = ''
            verifyError.value = ''
        }
    })

    // Verificar si el número de orden existe
    const verifyOrder = async () => {
        const searchNum = orderNumber.value.trim()
        if (!searchNum) return

        verifying.value = true
        verifyError.value = ''
        selectedOrder.value = null

        try {
            // Obtener todas las órdenes y buscar coincidencia exacta
            const orders = await orderService.getOrders()

            // Buscar coincidencia exacta (case insensitive)
            const found = orders.find(o =>
                o.orderNumber?.toLowerCase() === searchNum.toLowerCase()
            )

            if (found) {
                // Orden encontrada - mapear datos
                selectedOrder.value = {
                    id: found.id,
                    orderNumber: found.orderNumber,
                    client: found.client || found.clientName || found.cliente?.name || 'Sin cliente',
                    clientId: found.clienteId || found.cliente?.id,
                    clientCuit: found.cliente?.cuit || found.clientCuit || '',
                    clientAddress: found.cliente?.address || found.clientAddress || '',
                    status: found.status,
                    totalAmount: found.totalAmount || found.price || 0,
                    deliveryDate: found.deliveryDate,
                    workItems: found.workItems || []
                }

                // Pre-llenar formulario
                localForm.value.orderId = found.id.toString()
                localForm.value.orderNumber = found.orderNumber
                localForm.value.clientCuit = selectedOrder.value.clientCuit
                localForm.value.clientAddress = selectedOrder.value.clientAddress

                // Cargar items de trabajo
                if (found.workItems && found.workItems.length > 0) {
                    localForm.value.workItems = found.workItems.map(item => ({
                        type: item.type || item.workType || 'otro',
                        material: item.material || 'otro',
                        teeth: item.teeth || item.piezas || '',
                        quantity: item.quantity || item.cantidad || 1,
                        unitPrice: item.price || item.unitPrice || item.precioUnitario || 0
                    }))
                } else {
                    // Si no hay items, crear uno con el total
                    localForm.value.workItems = [{
                        type: 'otro',
                        material: 'otro',
                        teeth: found.teeth?.join(',') || '',
                        quantity: 1,
                        unitPrice: found.totalAmount || found.price || 0
                    }]
                }

                calculateTotals()
            } else {
                verifyError.value = `El numero de orden "${searchNum}" no existe en el sistema`
            }
        } catch (error) {
            console.error('Error verificando orden:', error)
            verifyError.value = 'Error al verificar la orden. Comprueba la conexion con el servidor.'
        } finally {
            verifying.value = false
        }
    }

    // Limpiar orden y volver a empezar
    const clearOrder = () => {
        selectedOrder.value = null
        localForm.value = getEmptyForm()
        orderNumber.value = ''
        verifyError.value = ''
    }

    // Actualizar tipo de factura según condición IVA
    const updateInvoiceType = (condicion) => {
        const tipoFactura = {
            'RI': 'A',
            'MONO': 'B',
            'EX': 'B',
            'CF': 'B'
        }
        localForm.value.invoiceType = tipoFactura[condicion] || 'B'
    }

    // Agregar item de trabajo
    const addWorkItem = () => {
        localForm.value.workItems.push({
            type: '',
            material: '',
            teeth: '',
            quantity: 1,
            unitPrice: 0
        })
    }

    // Eliminar item de trabajo
    const removeWorkItem = (index) => {
        localForm.value.workItems.splice(index, 1)
        calculateTotals()
    }

    // Calcular totales
    const calculateTotals = () => {
        const subtotal = localForm.value.workItems.reduce((sum, item) => {
            return sum + (parseFloat(item.quantity) || 0) * (parseFloat(item.unitPrice) || 0)
        }, 0)

        localForm.value.netAmount = subtotal
        localForm.value.ivaAmount = subtotal * 0.21
        localForm.value.totalAmount = subtotal * 1.21
    }

    // Formatear número
    const formatNumber = (value) => {
        if (value === null || value === undefined || isNaN(value)) return '0'
        return Number(value).toLocaleString('es-AR', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
    }

    // Formatear fecha
    const formatDate = (dateStr) => {
        if (!dateStr) return '--'
        return new Date(dateStr).toLocaleDateString('es-AR')
    }

    const canSave = computed(() => {
        return selectedOrder.value &&
            localForm.value.condicionIva &&
            localForm.value.clientCuit &&
            localForm.value.workItems.length > 0 &&
            localForm.value.totalAmount > 0
    })

    const handleSave = () => {
        if (!selectedOrder.value) return

        emit('save', {
            ...localForm.value,
            client: selectedOrder.value.client
        })
    }
</script>