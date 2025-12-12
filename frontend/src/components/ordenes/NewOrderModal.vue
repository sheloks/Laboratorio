<template>
    <Dialog :open="open" @update:open="$emit('update:open', $event)">
        <DialogContent class="max-w-4xl max-h-[90vh] overflow-y-auto">
            <DialogHeader>
                <DialogTitle>Nueva Orden de Trabajo</DialogTitle>
                <DialogDescription>
                    Complete los datos para crear una nueva orden
                </DialogDescription>
            </DialogHeader>

            <Tabs default-value="datos" class="w-full">
                <TabsList class="grid w-full grid-cols-3">
                    <TabsTrigger value="datos">Datos Generales</TabsTrigger>
                    <TabsTrigger value="trabajos">Trabajos</TabsTrigger>
                    <TabsTrigger value="odontograma">Odontograma</TabsTrigger>
                </TabsList>

                <!-- Tab Datos Generales -->
                <TabsContent value="datos" class="space-y-4 mt-4">
                    <div class="grid grid-cols-2 gap-4">
                        <div class="space-y-2">
                            <Label for="client">Cliente / Clinica *</Label>
                            <div class="relative">
                                <Input id="client"
                                       v-model="clientSearch"
                                       @input="onClientSearch"
                                       @focus="onClientFocus"
                                       @blur="hideSuggestionsDelayed"
                                       placeholder="Escriba el nombre del cliente..."
                                       autocomplete="off" />
                                <!-- Debug info -->
                                <div class="text-xs text-gray-400 mt-1">
                                    {{ props.clients?.length || 0 }} clientes disponibles
                                </div>
                                <!-- Sugerencias -->
                                <div v-if="showClientSuggestions && filteredClients.length > 0"
                                     class="absolute z-50 w-full mt-1 bg-white border border-gray-200 rounded-lg shadow-lg max-h-48 overflow-y-auto">
                                    <div v-for="client in filteredClients"
                                         :key="client.id"
                                         @mousedown.prevent="selectClient(client)"
                                         class="px-3 py-2 cursor-pointer hover:bg-blue-50 border-b last:border-b-0 transition-colors">
                                        <div class="font-medium text-sm text-gray-800">{{ client.name }}</div>
                                        <div class="text-xs text-gray-500">{{ client.phone || client.email || '' }}</div>
                                    </div>
                                </div>
                                <!-- Indicador de cliente seleccionado -->
                                <div v-if="selectedClient" class="mt-2 flex items-center justify-between p-2 bg-green-50 border border-green-200 rounded-lg">
                                    <div class="flex items-center gap-2">
                                        <CheckCircleIcon class="h-4 w-4 text-green-600" />
                                        <span class="text-sm font-medium text-green-800">{{ selectedClient.name }}</span>
                                    </div>
                                    <button @click="clearClient" type="button" class="text-green-600 hover:text-green-800">
                                        <XIcon class="h-4 w-4" />
                                    </button>
                                </div>
                            </div>
                            <p v-if="!props.clients || props.clients.length === 0" class="text-xs text-amber-600">
                                No hay clientes. Crea uno primero en la seccion Clientes.
                            </p>
                            <p v-else-if="clientSearch && !selectedClient && filteredClients.length === 0" class="text-xs text-amber-600">
                                No se encontro ningun cliente con ese nombre.
                            </p>
                        </div>
                        <div class="space-y-2">
                            <Label for="assignedTo">Personal Asignado</Label>
                            <Select v-model="localOrder.assignedTo">
                                <SelectTrigger>
                                    <SelectValue placeholder="Seleccionar personal" />
                                </SelectTrigger>
                                <SelectContent>
                                    <SelectItem v-for="person in activeStaff" :key="person.id" :value="String(person.id)">
                                        {{ person.nombre }} - {{ person.cargo }}
                                    </SelectItem>
                                </SelectContent>
                            </Select>
                            <p v-if="activeStaff.length === 0" class="text-xs text-amber-600">
                                No hay personal activo. Agrega personal en Clientes > Personal.
                            </p>
                        </div>
                    </div>

                    <div class="grid grid-cols-2 gap-4">
                        <div class="space-y-2">
                            <Label for="patient">Paciente</Label>
                            <Input id="patient" v-model="localOrder.patient" placeholder="Nombre del paciente" />
                        </div>
                        <div class="space-y-2">
                            <Label for="deliveryDate">Fecha de Entrega</Label>
                            <Input id="deliveryDate" type="date" v-model="localOrder.deliveryDate" />
                        </div>
                    </div>

                    <div class="grid grid-cols-1 gap-4">
                        <div class="space-y-2">
                            <Label for="priority">Prioridad</Label>
                            <Select v-model="localOrder.priority">
                                <SelectTrigger>
                                    <SelectValue placeholder="Seleccionar prioridad" />
                                </SelectTrigger>
                                <SelectContent>
                                    <SelectItem value="low">Baja</SelectItem>
                                    <SelectItem value="normal">Normal</SelectItem>
                                    <SelectItem value="high">Alta</SelectItem>
                                    <SelectItem value="urgent">Urgente</SelectItem>
                                </SelectContent>
                            </Select>
                        </div>
                    </div>

                    <div class="space-y-2">
                        <Label for="notes">Notas / Observaciones</Label>
                        <Textarea id="notes" v-model="localOrder.notes" placeholder="Instrucciones especiales..." rows="3" />
                    </div>
                </TabsContent>

                <!-- Tab Trabajos -->
                <TabsContent value="trabajos" class="space-y-4 mt-4">
                    <div class="space-y-4">
                        <div class="flex justify-between items-center">
                            <Label>Trabajos a realizar</Label>
                            <Button variant="outline" size="sm" @click="addWorkItem">
                                <PlusIcon class="h-4 w-4 mr-1" />
                                Agregar trabajo
                            </Button>
                        </div>

                        <div v-for="(item, index) in localOrder.workItems" :key="index"
                             class="border rounded-lg p-4 space-y-3">
                            <div class="flex justify-between items-center">
                                <span class="font-medium">Trabajo {{ index + 1 }}</span>
                                <Button variant="ghost" size="sm" @click="removeWorkItem(index)">
                                    <XIcon class="h-4 w-4 text-red-500" />
                                </Button>
                            </div>
                            <div class="grid grid-cols-2 gap-3">
                                <div class="space-y-2">
                                    <Label>Tipo de trabajo</Label>
                                    <Select v-model="item.type">
                                        <SelectTrigger>
                                            <SelectValue placeholder="Seleccionar" />
                                        </SelectTrigger>
                                        <SelectContent>
                                            <SelectItem value="corona">Corona</SelectItem>
                                            <SelectItem value="puente">Puente</SelectItem>
                                            <SelectItem value="carilla">Carilla</SelectItem>
                                            <SelectItem value="incrustacion">Incrustacion</SelectItem>
                                            <SelectItem value="protesis">Protesis</SelectItem>
                                            <SelectItem value="ortodoncia">Ortodoncia</SelectItem>
                                            <SelectItem value="implante">Sobre implante</SelectItem>
                                            <SelectItem value="provisorio">Provisorio</SelectItem>
                                            <SelectItem value="reparacion">Reparacion</SelectItem>
                                            <SelectItem value="otro">Otro</SelectItem>
                                        </SelectContent>
                                    </Select>
                                </div>
                                <div class="space-y-2">
                                    <Label>Color / Tono</Label>
                                    <Input v-model="item.shade" placeholder="Ej: A2, B1, etc." />
                                </div>
                            </div>
                            <div class="grid grid-cols-2 gap-3">
                                <div class="space-y-2">
                                    <Label>Material</Label>
                                    <Select v-model="item.material">
                                        <SelectTrigger>
                                            <SelectValue placeholder="Seleccionar" />
                                        </SelectTrigger>
                                        <SelectContent>
                                            <SelectItem value="zirconio">Zirconio</SelectItem>
                                            <SelectItem value="disilicato">Disilicato de Litio</SelectItem>
                                            <SelectItem value="metal_porcelana">Metal Porcelana</SelectItem>
                                            <SelectItem value="acrilico">Acrilico</SelectItem>
                                            <SelectItem value="composite">Composite</SelectItem>
                                            <SelectItem value="metal">Metal</SelectItem>
                                            <SelectItem value="ceramica">Ceramica</SelectItem>
                                        </SelectContent>
                                    </Select>
                                </div>
                                <div class="space-y-2">
                                    <Label>Precio</Label>
                                    <Input type="number" v-model="item.price" placeholder="0.00" />
                                </div>
                            </div>
                            <div class="space-y-2">
                                <Label>Piezas dentales</Label>
                                <Input v-model="item.teeth" placeholder="Ej: 11, 12, 21 o seleccione en odontograma" />
                            </div>
                        </div>

                        <div v-if="localOrder.workItems.length === 0" class="text-center py-8 text-gray-500 border-2 border-dashed rounded-lg">
                            <p>No hay trabajos agregados</p>
                            <Button variant="outline" class="mt-2" @click="addWorkItem">
                                <PlusIcon class="h-4 w-4 mr-1" />
                                Agregar primer trabajo
                            </Button>
                        </div>
                    </div>

                    <!-- Resumen de precios -->
                    <div class="border-t pt-4 mt-4">
                        <div class="flex justify-between text-lg font-semibold">
                            <span>Total:</span>
                            <span>${{ calculateTotal().toLocaleString() }}</span>
                        </div>
                    </div>
                </TabsContent>

                <!-- Tab Odontograma -->
                <TabsContent value="odontograma" class="mt-4">
                    <div class="odontograma-container">
                        <!-- Arcada Superior -->
                        <div class="arcade-label">SUPERIOR</div>
                        <div class="teeth-row">
                            <div v-for="tooth in upperTeeth" :key="tooth"
                                 @click="toggleTooth(tooth)"
                                 :class="['tooth-item', { 'selected': selectedTeeth.includes(tooth), 'has-work': teethWithWork.includes(tooth) }]">
                                <div class="tooth-shape">
                                    <svg viewBox="0 0 30 40" class="tooth-svg">
                                        <path d="M15 2 C8 2 3 8 3 15 C3 22 5 30 8 36 C10 40 13 40 15 40 C17 40 20 40 22 36 C25 30 27 22 27 15 C27 8 22 2 15 2 Z"
                                              :fill="getToothFill(tooth)"
                                              :stroke="getToothStroke(tooth)"
                                              stroke-width="1.5" />
                                    </svg>
                                </div>
                                <span class="tooth-number">{{ tooth }}</span>
                            </div>
                        </div>

                        <!-- Labels laterales -->
                        <div class="side-labels">
                            <span class="side-label left">DERECHA</span>
                            <span class="side-label right">IZQUIERDA</span>
                        </div>

                        <!-- Arcada Inferior -->
                        <div class="teeth-row">
                            <div v-for="tooth in lowerTeeth" :key="tooth"
                                 @click="toggleTooth(tooth)"
                                 :class="['tooth-item', { 'selected': selectedTeeth.includes(tooth), 'has-work': teethWithWork.includes(tooth) }]">
                                <div class="tooth-shape inverted">
                                    <svg viewBox="0 0 30 40" class="tooth-svg">
                                        <path d="M15 2 C8 2 3 8 3 15 C3 22 5 30 8 36 C10 40 13 40 15 40 C17 40 20 40 22 36 C25 30 27 22 27 15 C27 8 22 2 15 2 Z"
                                              :fill="getToothFill(tooth)"
                                              :stroke="getToothStroke(tooth)"
                                              stroke-width="1.5" />
                                    </svg>
                                </div>
                                <span class="tooth-number">{{ tooth }}</span>
                            </div>
                        </div>
                        <div class="arcade-label">INFERIOR</div>

                        <!-- Leyenda -->
                        <div class="legend">
                            <div class="legend-item">
                                <span class="legend-color normal"></span>
                                <span>Normal</span>
                            </div>
                            <div class="legend-item">
                                <span class="legend-color selected"></span>
                                <span>Seleccionado</span>
                            </div>
                            <div class="legend-item">
                                <span class="legend-color has-work"></span>
                                <span>Con trabajo</span>
                            </div>
                        </div>

                        <!-- Resumen de selección -->
                        <div class="selection-summary">
                            <div class="summary-label">Piezas seleccionadas:</div>
                            <div class="summary-teeth">
                                <span v-if="selectedTeeth.length === 0" class="no-selection">Ninguna pieza seleccionada</span>
                                <span v-else class="teeth-badges">
                                    <span v-for="tooth in selectedTeeth" :key="tooth" class="tooth-badge">
                                        {{ tooth }}
                                        <button @click.stop="toggleTooth(tooth)" class="remove-tooth">&times;</button>
                                    </span>
                                </span>
                            </div>
                        </div>
                    </div>
                </TabsContent>
            </Tabs>

            <DialogFooter class="mt-6">
                <Button variant="outline" @click="$emit('update:open', false)">
                    Cancelar
                </Button>
                <Button @click="handleSave" :disabled="saving || !isValidForm">
                    <SaveIcon v-if="!saving" class="h-4 w-4 mr-2" />
                    <div v-else class="animate-spin rounded-full h-4 w-4 border-b-2 border-white mr-2"></div>
                    {{ saving ? 'Guardando...' : 'Guardar Orden' }}
                </Button>
            </DialogFooter>
        </DialogContent>
    </Dialog>
</template>

<script setup>
    import { ref, watch, computed } from 'vue'
    import { PlusIcon, XIcon, SaveIcon, CheckCircleIcon } from 'lucide-vue-next'
    import { Button } from '@/components/ui/button'
    import { Input } from '@/components/ui/input'
    import { Label } from '@/components/ui/label'
    import { Textarea } from '@/components/ui/textarea'
    import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs'
    import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
    import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from '@/components/ui/dialog'

    const props = defineProps({
        open: Boolean,
        clients: {
            type: Array,
            default: () => []
        },
        staff: {
            type: Array,
            default: () => []
        },
        materials: {
            type: Array,
            default: () => []
        },
        saving: Boolean
    })

    const emit = defineEmits(['update:open', 'save'])

    // Debug - ver qué llega
    watch(() => props.clients, (newClients) => {
        console.log('NewOrderModal - Clients recibidos:', newClients)
    }, { immediate: true })

    watch(() => props.open, (isOpen) => {
        if (isOpen) {
            console.log('NewOrderModal abierto - Clients disponibles:', props.clients)
        }
    })

    // Dientes en orden
    const upperTeeth = [18, 17, 16, 15, 14, 13, 12, 11, 21, 22, 23, 24, 25, 26, 27, 28]
    const lowerTeeth = [48, 47, 46, 45, 44, 43, 42, 41, 31, 32, 33, 34, 35, 36, 37, 38]

    const selectedTeeth = ref([])
    const teethWithWork = ref([])

    // Autocompletado de cliente
    const clientSearch = ref('')
    const showClientSuggestions = ref(false)
    const selectedClient = ref(null)

    const filteredClients = computed(() => {
        const allClients = props.clients || []
        console.log('filteredClients - Total clientes disponibles:', allClients.length, allClients)

        // Si no hay búsqueda, mostrar todos
        if (!clientSearch.value || clientSearch.value.trim() === '') {
            return allClients
        }

        // Filtrar por nombre
        const search = clientSearch.value.toLowerCase().trim()
        return allClients.filter(client =>
            client.name && client.name.toLowerCase().includes(search)
        )
    })

    const onClientSearch = () => {
        showClientSuggestions.value = true
        // Si el texto cambió y no coincide exactamente con el cliente seleccionado, limpiar selección
        if (selectedClient.value && clientSearch.value !== selectedClient.value.name) {
            selectedClient.value = null
            localOrder.value.clientId = ''
        }
    }

    const onClientFocus = () => {
        showClientSuggestions.value = true
        console.log('onClientFocus - Mostrando sugerencias, clientes:', props.clients?.length)
    }

    const selectClient = (client) => {
        console.log('selectClient:', client)
        selectedClient.value = client
        clientSearch.value = client.name
        localOrder.value.clientId = String(client.id)
        showClientSuggestions.value = false
    }

    const clearClient = () => {
        selectedClient.value = null
        clientSearch.value = ''
        localOrder.value.clientId = ''
        showClientSuggestions.value = false
    }

    const hideSuggestionsDelayed = () => {
        setTimeout(() => {
            showClientSuggestions.value = false
        }, 200)
    }

    const activeStaff = computed(() => {
        return (props.staff || []).filter(person => person.activo)
    })

    // Validación del formulario
    const isValidForm = computed(() => {
        return selectedClient.value !== null && localOrder.value.clientId !== ''
    })

    const getEmptyOrder = () => ({
        clientId: '',
        assignedTo: '',
        patient: '',
        deliveryDate: '',
        priority: 'normal',
        notes: '',
        workItems: []
    })

    const localOrder = ref(getEmptyOrder())

    watch(() => props.open, (isOpen) => {
        if (isOpen) {
            localOrder.value = getEmptyOrder()
            selectedTeeth.value = []
            teethWithWork.value = []
            // Limpiar autocompletado
            clientSearch.value = ''
            selectedClient.value = null
            showClientSuggestions.value = false
        }
    })

    const getToothFill = (tooth) => {
        if (teethWithWork.value.includes(tooth)) return '#dcfce7'
        if (selectedTeeth.value.includes(tooth)) return '#dbeafe'
        return '#f8fafc'
    }

    const getToothStroke = (tooth) => {
        if (teethWithWork.value.includes(tooth)) return '#16a34a'
        if (selectedTeeth.value.includes(tooth)) return '#3b82f6'
        return '#cbd5e1'
    }

    const addWorkItem = () => {
        localOrder.value.workItems.push({
            type: '',
            material: '',
            shade: '',
            teeth: '',
            price: 0
        })
    }

    const removeWorkItem = (index) => {
        localOrder.value.workItems.splice(index, 1)
    }

    const toggleTooth = (tooth) => {
        const index = selectedTeeth.value.indexOf(tooth)
        if (index > -1) {
            selectedTeeth.value.splice(index, 1)
        } else {
            selectedTeeth.value.push(tooth)
        }
        selectedTeeth.value.sort((a, b) => a - b)
    }

    const calculateTotal = () => {
        return localOrder.value.workItems.reduce((sum, item) => sum + (parseFloat(item.price) || 0), 0)
    }

    const handleSave = () => {
        // Validar que se haya seleccionado un cliente
        if (!selectedClient.value) {
            alert('Debe seleccionar un cliente de la lista')
            return
        }

        // Buscar el personal asignado
        const selectedStaff = props.staff?.find(s => String(s.id) === String(localOrder.value.assignedTo))

        const orderToSave = {
            // IDs como números
            clienteId: selectedClient.value.id,
            assignedToId: localOrder.value.assignedTo ? parseInt(localOrder.value.assignedTo) : null,

            // Nombres para mostrar
            client: selectedClient.value.name,
            clientName: selectedClient.value.name,
            dentist: selectedStaff ? `${selectedStaff.nombre} - ${selectedStaff.cargo}` : '',
            staffName: selectedStaff ? `${selectedStaff.nombre}` : '',

            // Otros campos
            patient: localOrder.value.patient || '',
            deliveryDate: localOrder.value.deliveryDate || null,
            priority: localOrder.value.priority || 'normal',
            notes: localOrder.value.notes || '',

            // Trabajos y dientes
            workItems: localOrder.value.workItems || [],
            workType: localOrder.value.workItems?.[0]?.type || '',
            teeth: selectedTeeth.value,

            // Totales
            totalAmount: calculateTotal(),

            // Estado inicial
            status: 'pending'
        }

        console.log('NewOrderModal - Cliente seleccionado:', selectedClient.value)
        console.log('NewOrderModal - Enviando orden:', orderToSave)

        emit('save', orderToSave)
    }
</script>

<style scoped>
    .odontograma-container {
        padding: 1.5rem;
        background: white;
        border-radius: 12px;
    }

    .arcade-label {
        text-align: center;
        font-size: 0.75rem;
        font-weight: 600;
        color: #6b7280;
        letter-spacing: 0.1em;
        margin: 0.5rem 0;
    }

    .teeth-row {
        display: flex;
        justify-content: center;
        gap: 0.25rem;
        margin: 0.5rem 0;
    }

    .side-labels {
        display: flex;
        justify-content: space-between;
        padding: 0.5rem 1rem;
        margin: 0.5rem 0;
    }

    .side-label {
        font-size: 0.625rem;
        font-weight: 600;
        color: #9ca3af;
        letter-spacing: 0.05em;
    }

    .tooth-item {
        display: flex;
        flex-direction: column;
        align-items: center;
        cursor: pointer;
        transition: transform 0.15s;
        padding: 0.25rem;
    }

        .tooth-item:hover {
            transform: scale(1.1);
        }

    .tooth-shape {
        width: 32px;
        height: 42px;
    }

        .tooth-shape.inverted {
            transform: rotate(180deg);
        }

    .tooth-svg {
        width: 100%;
        height: 100%;
    }

    .tooth-number {
        font-size: 0.625rem;
        font-weight: 600;
        color: #64748b;
        margin-top: 2px;
    }

    .tooth-item.selected .tooth-number {
        color: #3b82f6;
        font-weight: 700;
    }

    .tooth-item.has-work .tooth-number {
        color: #16a34a;
        font-weight: 700;
    }

    .legend {
        display: flex;
        justify-content: center;
        gap: 1.5rem;
        margin: 1.5rem 0 1rem;
        padding: 0.75rem;
        background: #f9fafb;
        border-radius: 8px;
    }

    .legend-item {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        font-size: 0.75rem;
        color: #6b7280;
    }

    .legend-color {
        width: 16px;
        height: 20px;
        border-radius: 3px;
        border: 1.5px solid;
    }

        .legend-color.normal {
            background: #f8fafc;
            border-color: #cbd5e1;
        }

        .legend-color.selected {
            background: #dbeafe;
            border-color: #3b82f6;
        }

        .legend-color.has-work {
            background: #dcfce7;
            border-color: #16a34a;
        }

    .selection-summary {
        margin-top: 1rem;
        padding: 1rem;
        background: #f9fafb;
        border-radius: 8px;
        border: 1px solid #e5e7eb;
    }

    .summary-label {
        font-size: 0.875rem;
        font-weight: 600;
        color: #374151;
        margin-bottom: 0.5rem;
    }

    .summary-teeth {
        display: flex;
        flex-wrap: wrap;
        gap: 0.5rem;
        min-height: 32px;
        align-items: center;
    }

    .no-selection {
        color: #9ca3af;
        font-size: 0.875rem;
        font-style: italic;
    }

    .teeth-badges {
        display: flex;
        flex-wrap: wrap;
        gap: 0.375rem;
    }

    .tooth-badge {
        display: inline-flex;
        align-items: center;
        gap: 0.25rem;
        background: #3b82f6;
        color: white;
        padding: 0.25rem 0.5rem;
        border-radius: 4px;
        font-size: 0.75rem;
        font-weight: 600;
    }

    .remove-tooth {
        background: none;
        border: none;
        color: white;
        cursor: pointer;
        font-size: 1rem;
        line-height: 1;
        padding: 0;
        margin-left: 0.25rem;
        opacity: 0.7;
    }

        .remove-tooth:hover {
            opacity: 1;
        }
</style>