<template>
    <div v-if="open" class="modal-overlay" @click.self="$emit('update:open', false)">
        <div class="modal-container">
            <!-- Header -->
            <div class="modal-header">
                <h2 class="modal-title">{{ isEditing ? 'Editar Envio' : 'Nuevo Envio' }}</h2>
                <button class="close-btn" @click="$emit('update:open', false)">
                    <XIcon />
                </button>
            </div>

            <!-- Body -->
            <div class="modal-body">
                <form @submit.prevent="handleSubmit" class="form">
                    <!-- Numero de Orden -->
                    <div class="form-group">
                        <label class="form-label">Numero de Orden *</label>
                        <div class="order-input-wrapper">
                            <input type="text"
                                   v-model="form.ordenNumber"
                                   class="form-input"
                                   :class="{ 'input-error': ordenError, 'input-success': ordenValidada }"
                                   placeholder="Ej: ORD-2024-001"
                                   @blur="validarOrden"
                                   required />
                            <div v-if="validandoOrden" class="input-status">
                                <div class="spinner-sm"></div>
                            </div>
                            <div v-else-if="ordenValidada" class="input-status success">
                                <CheckCircleIcon />
                            </div>
                            <div v-else-if="ordenError" class="input-status error">
                                <XCircleIcon />
                            </div>
                        </div>
                        <p v-if="ordenError" class="form-error">{{ ordenError }}</p>
                        <p v-if="ordenValidada && ordenData" class="form-success">
                            Orden encontrada: {{ ordenData.clientName }}
                        </p>
                    </div>

                    <!-- Tipo de envio: Solo Envio o Retiro -->
                    <div class="form-group">
                        <label class="form-label">Tipo *</label>
                        <div class="radio-group">
                            <label class="radio-label">
                                <input type="radio" v-model="form.tipo" value="RETIRO" />
                                <span>Retiro</span>
                            </label>
                            <label class="radio-label">
                                <input type="radio" v-model="form.tipo" value="ENVIO" />
                                <span>Envio</span>
                            </label>
                        </div>
                    </div>

                    <!-- Estado -->
                    <div class="form-group">
                        <label class="form-label">Estado</label>
                        <div class="select-wrapper">
                            <select v-model="form.estado" class="form-select">
                                <option value="PROGRAMADO">Programado</option>
                                <option value="EN_TRANSITO">En Transito</option>
                                <option value="ENTREGADO">Entregado</option>
                                <option value="CANCELADO">Cancelado</option>
                            </select>
                            <ChevronDownIcon class="select-arrow" />
                        </div>
                    </div>

                    <!-- Datos del destinatario -->
                    <div class="form-section">
                        <h3 class="section-title">Datos del Destinatario</h3>

                        <div class="form-row">
                            <div class="form-group">
                                <label class="form-label">Nombre / Cliente *</label>
                                <input type="text" v-model="form.clienteNombre" class="form-input" required />
                            </div>
                            <div class="form-group">
                                <label class="form-label">Telefono</label>
                                <input type="text" v-model="form.telefono" class="form-input" placeholder="+54 9 11..." />
                            </div>
                        </div>

                        <div class="form-group">
                            <label class="form-label">Direccion *</label>
                            <input type="text" v-model="form.direccion" class="form-input" required placeholder="Calle, numero, piso, depto..." />
                        </div>

                        <div class="form-row">
                            <div class="form-group">
                                <label class="form-label">Ciudad</label>
                                <input type="text" v-model="form.ciudad" class="form-input" />
                            </div>
                            <div class="form-group">
                                <label class="form-label">Codigo Postal</label>
                                <input type="text" v-model="form.codigoPostal" class="form-input" />
                            </div>
                        </div>
                    </div>

                    <!-- Datos del transporte -->
                    <div class="form-section">
                        <h3 class="section-title">Datos del Transporte</h3>

                        <div class="form-row">
                            <div class="form-group">
                                <label class="form-label">Transportista</label>
                                <div class="select-wrapper">
                                    <select v-model="form.transportista" class="form-select">
                                        <option value="">Seleccionar...</option>
                                        <option value="Correo Argentino">Correo Argentino</option>
                                        <option value="Andreani">Andreani</option>
                                        <option value="OCA">OCA</option>
                                        <option value="Via Cargo">Via Cargo</option>
                                        <option value="Retiro en local">Retiro en local</option>
                                        <option value="Envio propio">Envio propio</option>
                                        <option value="Otro">Otro</option>
                                    </select>
                                    <ChevronDownIcon class="select-arrow" />
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="form-label">Codigo de Seguimiento</label>
                                <input type="text" v-model="form.codigoSeguimiento" class="form-input" placeholder="Tracking number..." />
                            </div>
                        </div>

                        <div class="form-row">
                            <div class="form-group">
                                <label class="form-label">Fecha de Retiro *</label>
                                <input type="date" v-model="form.fechaRetiro" class="form-input" required />
                            </div>
                            <div class="form-group">
                                <label class="form-label">Fecha Entrega Estimada</label>
                                <input type="date" v-model="form.fechaEntregaEstimada" class="form-input" />
                            </div>
                        </div>

                        <div class="form-group">
                            <label class="form-label">Costo de Envio</label>
                            <div class="input-with-prefix">
                                <span class="input-prefix">$</span>
                                <input type="number" v-model="form.costoEnvio" class="form-input with-prefix" min="0" step="0.01" />
                            </div>
                        </div>
                    </div>

                    <!-- Notas -->
                    <div class="form-group">
                        <label class="form-label">Notas / Instrucciones</label>
                        <textarea v-model="form.notas" class="form-textarea" rows="3" placeholder="Instrucciones especiales de entrega..."></textarea>
                    </div>
                </form>
            </div>

            <!-- Footer -->
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" @click="$emit('update:open', false)">
                    Cancelar
                </button>
                <button type="submit"
                        class="btn btn-primary"
                        @click="handleSubmit"
                        :disabled="saving || !isFormValid || !ordenValidada">
                    <div v-if="saving" class="spinner-sm"></div>
                    {{ saving ? 'Guardando...' : (isEditing ? 'Guardar Cambios' : 'Crear Envio') }}
                </button>
            </div>
        </div>
    </div>
</template>

<script setup>
    import { ref, watch, computed } from 'vue'
    import { XIcon, ChevronDownIcon, CheckCircleIcon, XCircleIcon } from 'lucide-vue-next'
    import axios from '@/services/axios'

    const props = defineProps({
        open: Boolean,
        envio: Object,
        isEditing: Boolean,
        saving: Boolean
    })

    const emit = defineEmits(['update:open', 'save'])

    // Estado de validacion de orden
    const validandoOrden = ref(false)
    const ordenValidada = ref(false)
    const ordenError = ref('')
    const ordenData = ref(null)

    const getEmptyForm = () => ({
        ordenId: null,
        ordenNumber: '',
        tipo: 'ENVIO',
        estado: 'PROGRAMADO',
        clienteNombre: '',
        direccion: '',
        ciudad: '',
        codigoPostal: '',
        telefono: '',
        transportista: '',
        codigoSeguimiento: '',
        costoEnvio: 0,
        fechaRetiro: new Date().toISOString().split('T')[0],
        fechaEntregaEstimada: '',
        notas: ''
    })

    const form = ref(getEmptyForm())

    // Validar que la orden existe en el backend
    const validarOrden = async () => {
        const orderNumber = form.value.ordenNumber.trim()

        if (!orderNumber) {
            ordenError.value = ''
            ordenValidada.value = false
            ordenData.value = null
            return
        }

        validandoOrden.value = true
        ordenError.value = ''
        ordenValidada.value = false

        try {
            // Buscar la orden por numero
            const response = await axios.get('/ordenes/search', {
                params: { q: orderNumber }
            })

            const ordenes = response.data

            // Buscar coincidencia exacta
            const ordenEncontrada = ordenes.find(
                o => o.orderNumber?.toLowerCase() === orderNumber.toLowerCase()
            )

            if (ordenEncontrada) {
                ordenValidada.value = true
                ordenData.value = ordenEncontrada
                form.value.ordenId = ordenEncontrada.id

                // Autocompletar datos del cliente
                form.value.clienteNombre = ordenEncontrada.clientName || ''
                form.value.telefono = ordenEncontrada.clientPhone || ''
                form.value.direccion = ordenEncontrada.clientAddress || ''
            } else {
                ordenError.value = 'El envio no esta asociado a ninguna orden existente'
                ordenData.value = null
                form.value.ordenId = null
            }
        } catch (err) {
            console.error('Error validando orden:', err)
            ordenError.value = 'Error al validar la orden'
            ordenData.value = null
            form.value.ordenId = null
        } finally {
            validandoOrden.value = false
        }
    }

    // Watch para cargar datos cuando se abre el modal
    watch(() => props.open, (isOpen) => {
        if (isOpen) {
            // Reset validacion
            ordenError.value = ''
            ordenValidada.value = false
            ordenData.value = null

            if (props.isEditing && props.envio) {
                form.value = {
                    ordenId: props.envio.ordenId || null,
                    ordenNumber: props.envio.ordenNumber || '',
                    tipo: props.envio.tipo || 'ENVIO',
                    estado: props.envio.estado || 'PROGRAMADO',
                    clienteNombre: props.envio.clienteNombre || '',
                    direccion: props.envio.direccion || '',
                    ciudad: props.envio.ciudad || '',
                    codigoPostal: props.envio.codigoPostal || '',
                    telefono: props.envio.telefono || '',
                    transportista: props.envio.transportista || '',
                    codigoSeguimiento: props.envio.codigoSeguimiento || '',
                    costoEnvio: props.envio.costoEnvio || 0,
                    fechaRetiro: props.envio.fechaRetiro || '',
                    fechaEntregaEstimada: props.envio.fechaEntregaEstimada || '',
                    notas: props.envio.notas || ''
                }
                // Si es edicion, la orden ya esta validada
                ordenValidada.value = true
                ordenData.value = { clientName: props.envio.clienteNombre }
            } else {
                form.value = getEmptyForm()
            }
        }
    })

    const isFormValid = computed(() => {
        return form.value.ordenNumber &&
            form.value.clienteNombre &&
            form.value.direccion &&
            form.value.fechaRetiro
    })

    const handleSubmit = () => {
        if (!isFormValid.value || !ordenValidada.value) {
            if (!ordenValidada.value && form.value.ordenNumber) {
                ordenError.value = 'Debe validar el numero de orden antes de continuar'
            }
            return
        }

        emit('save', {
            ...form.value,
            costoEnvio: parseFloat(form.value.costoEnvio) || 0
        })
    }
</script>

<style scoped>
    .modal-overlay {
        position: fixed;
        inset: 0;
        background: rgba(0, 0, 0, 0.5);
        display: flex;
        align-items: center;
        justify-content: center;
        z-index: 1000;
        padding: 1rem;
    }

    .modal-container {
        background: white;
        border-radius: 16px;
        width: 100%;
        max-width: 640px;
        max-height: 90vh;
        overflow: hidden;
        display: flex;
        flex-direction: column;
    }

    .modal-header {
        padding: 1.25rem 1.5rem;
        border-bottom: 1px solid #e5e7eb;
        display: flex;
        justify-content: space-between;
        align-items: center;
    }

    .modal-title {
        font-size: 1.25rem;
        font-weight: 600;
        color: #1f2937;
        margin: 0;
    }

    .close-btn {
        width: 32px;
        height: 32px;
        border: none;
        background: transparent;
        border-radius: 8px;
        cursor: pointer;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #6b7280;
    }

        .close-btn:hover {
            background: #f3f4f6;
            color: #1f2937;
        }

        .close-btn svg {
            width: 20px;
            height: 20px;
        }

    .modal-body {
        padding: 1.5rem;
        overflow-y: auto;
        flex: 1;
    }

    .form {
        display: flex;
        flex-direction: column;
        gap: 1.25rem;
    }

    .form-section {
        padding-top: 1rem;
        border-top: 1px solid #f3f4f6;
    }

    .section-title {
        font-size: 0.875rem;
        font-weight: 600;
        color: #374151;
        margin: 0 0 1rem 0;
    }

    .form-group {
        display: flex;
        flex-direction: column;
        gap: 0.375rem;
    }

    .form-row {
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 1rem;
    }

    .form-label {
        font-size: 0.8125rem;
        font-weight: 500;
        color: #374151;
    }

    .form-input,
    .form-select,
    .form-textarea {
        padding: 0.625rem 0.75rem;
        border: 1px solid #e5e7eb;
        border-radius: 8px;
        font-size: 0.875rem;
        color: #1f2937;
        transition: all 0.2s;
    }

        .form-input:focus,
        .form-select:focus,
        .form-textarea:focus {
            outline: none;
            border-color: #0ea5e9;
            box-shadow: 0 0 0 3px rgba(14, 165, 233, 0.1);
        }

        .form-input.input-error {
            border-color: #dc2626;
        }

            .form-input.input-error:focus {
                box-shadow: 0 0 0 3px rgba(220, 38, 38, 0.1);
            }

        .form-input.input-success {
            border-color: #16a34a;
        }

            .form-input.input-success:focus {
                box-shadow: 0 0 0 3px rgba(22, 163, 74, 0.1);
            }

    .order-input-wrapper {
        position: relative;
        display: flex;
        align-items: center;
    }

        .order-input-wrapper .form-input {
            width: 100%;
            padding-right: 2.5rem;
        }

    .input-status {
        position: absolute;
        right: 10px;
        display: flex;
        align-items: center;
        justify-content: center;
    }

        .input-status svg {
            width: 18px;
            height: 18px;
        }

        .input-status.success {
            color: #16a34a;
        }

        .input-status.error {
            color: #dc2626;
        }

    .form-error {
        font-size: 0.75rem;
        color: #dc2626;
        margin: 0.25rem 0 0 0;
    }

    .form-success {
        font-size: 0.75rem;
        color: #16a34a;
        margin: 0.25rem 0 0 0;
    }

    .select-wrapper {
        position: relative;
    }

    .form-select {
        width: 100%;
        appearance: none;
        padding-right: 2.5rem;
        background: white;
        cursor: pointer;
    }

    .select-arrow {
        position: absolute;
        right: 12px;
        top: 50%;
        transform: translateY(-50%);
        width: 16px;
        height: 16px;
        color: #9ca3af;
        pointer-events: none;
    }

    .form-textarea {
        resize: vertical;
        min-height: 80px;
    }

    .input-with-prefix {
        position: relative;
        display: flex;
        align-items: center;
    }

    .input-prefix {
        position: absolute;
        left: 12px;
        color: #6b7280;
        font-size: 0.875rem;
    }

    .form-input.with-prefix {
        padding-left: 2rem;
    }

    .radio-group {
        display: flex;
        gap: 1.5rem;
    }

    .radio-label {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        font-size: 0.875rem;
        color: #374151;
        cursor: pointer;
    }

        .radio-label input {
            width: 16px;
            height: 16px;
            accent-color: #0ea5e9;
        }

    .modal-footer {
        padding: 1rem 1.5rem;
        border-top: 1px solid #e5e7eb;
        display: flex;
        justify-content: flex-end;
        gap: 0.75rem;
    }

    .btn {
        display: inline-flex;
        align-items: center;
        justify-content: center;
        gap: 0.5rem;
        padding: 0.625rem 1rem;
        border-radius: 8px;
        font-size: 0.875rem;
        font-weight: 500;
        cursor: pointer;
        transition: all 0.2s;
        border: none;
    }

    .btn-secondary {
        background: white;
        border: 1px solid #e5e7eb;
        color: #374151;
    }

        .btn-secondary:hover {
            background: #f3f4f6;
        }

    .btn-primary {
        background: #0ea5e9;
        color: white;
    }

        .btn-primary:hover {
            background: #0284c7;
        }

        .btn-primary:disabled {
            opacity: 0.6;
            cursor: not-allowed;
        }

    .spinner-sm {
        width: 16px;
        height: 16px;
        border: 2px solid rgba(255,255,255,0.3);
        border-top-color: white;
        border-radius: 50%;
        animation: spin 0.8s linear infinite;
    }

    .input-status .spinner-sm {
        border: 2px solid #e5e7eb;
        border-top-color: #0ea5e9;
    }

    @keyframes spin {
        to {
            transform: rotate(360deg);
        }
    }

    @media (max-width: 640px) {
        .form-row {
            grid-template-columns: 1fr;
        }

        .radio-group {
            flex-direction: column;
            gap: 0.75rem;
        }
    }
</style>