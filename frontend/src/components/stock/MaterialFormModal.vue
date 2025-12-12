<template>
    <Dialog :open="open" @update:open="$emit('update:open', $event)">
        <DialogContent class="max-w-3xl max-h-[85vh] overflow-y-auto">
            <DialogHeader>
                <DialogTitle class="flex items-center gap-2">
                    <PackageIcon class="h-5 w-5 text-sky-500" />
                    {{ isEditing ? 'Editar Material' : 'Nuevo Material' }}
                </DialogTitle>
                <DialogDescription>
                    {{ isEditing ? 'Modifica los datos del material' : 'Agrega un nuevo material al inventario' }}
                </DialogDescription>
            </DialogHeader>

            <!-- Errores de validacion -->
            <div v-if="errors.length > 0" class="error-alert">
                <AlertCircleIcon class="error-icon" />
                <div>
                    <p class="error-title">Por favor corrige los siguientes errores:</p>
                    <ul class="error-list">
                        <li v-for="error in errors" :key="error">{{ error }}</li>
                    </ul>
                </div>
            </div>

            <div class="form-content">
                <!-- Layout 2 columnas -->
                <div class="form-grid">
                    <!-- Columna Izquierda -->
                    <div class="form-column">
                        <!-- Identificacion -->
                        <div class="form-section">
                            <h3 class="section-title">
                                <TagIcon class="section-icon" />
                                Identificacion
                            </h3>
                            <div class="form-row">
                                <div class="form-group">
                                    <label class="form-label">Codigo <span class="required">*</span></label>
                                    <input type="text"
                                           v-model="localForm.code"
                                           placeholder="ZIR-001"
                                           class="form-input"
                                           :class="{ 'input-error': fieldErrors.code }" />
                                    <span v-if="fieldErrors.code" class="field-error">{{ fieldErrors.code }}</span>
                                </div>
                                <div class="form-group flex-2">
                                    <label class="form-label">Nombre <span class="required">*</span></label>
                                    <input type="text"
                                           v-model="localForm.name"
                                           placeholder="Nombre del material"
                                           class="form-input"
                                           :class="{ 'input-error': fieldErrors.name }" />
                                    <span v-if="fieldErrors.name" class="field-error">{{ fieldErrors.name }}</span>
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="form-label">Descripcion</label>
                                <textarea v-model="localForm.description"
                                          placeholder="Descripcion del material"
                                          rows="2"
                                          class="form-textarea"></textarea>
                            </div>
                        </div>

                        <!-- Clasificacion -->
                        <div class="form-section">
                            <h3 class="section-title">
                                <FolderIcon class="section-icon" />
                                Clasificacion
                            </h3>
                            <div class="form-row-3">
                                <div class="form-group">
                                    <label class="form-label">Categoria <span class="required">*</span></label>
                                    <div class="select-wrapper">
                                        <select v-model="localForm.category"
                                                class="form-select"
                                                :class="{ 'input-error': fieldErrors.category }">
                                            <option value="">Seleccionar</option>
                                            <option v-for="cat in categories" :key="cat.value" :value="cat.value">
                                                {{ cat.label }}
                                            </option>
                                        </select>
                                        <ChevronDownIcon class="select-arrow" />
                                    </div>
                                    <span v-if="fieldErrors.category" class="field-error">{{ fieldErrors.category }}</span>
                                </div>
                                <div class="form-group">
                                    <label class="form-label">Marca</label>
                                    <input type="text"
                                           v-model="localForm.brand"
                                           placeholder="Ej: Ivoclar"
                                           class="form-input" />
                                </div>
                                <div class="form-group">
                                    <label class="form-label">Proveedor</label>
                                    <input type="text"
                                           v-model="localForm.supplier"
                                           placeholder="Ej: Dental Express"
                                           class="form-input" />
                                </div>
                            </div>
                        </div>

                        <!-- Ubicacion -->
                        <div class="form-section">
                            <h3 class="section-title">
                                <MapPinIcon class="section-icon" />
                                Ubicacion y Fechas
                            </h3>
                            <div class="form-row-3">
                                <div class="form-group">
                                    <label class="form-label">Ubicacion</label>
                                    <input type="text"
                                           v-model="localForm.location"
                                           placeholder="Ej: Estante A1"
                                           class="form-input" />
                                </div>
                                <div class="form-group">
                                    <label class="form-label">Ultima Compra</label>
                                    <input type="date"
                                           v-model="localForm.lastPurchaseDate"
                                           :max="today"
                                           class="form-input"
                                           :class="{ 'input-error': fieldErrors.lastPurchaseDate }" />
                                    <span v-if="fieldErrors.lastPurchaseDate" class="field-error">{{ fieldErrors.lastPurchaseDate }}</span>
                                </div>
                                <div class="form-group">
                                    <label class="form-label">Vencimiento</label>
                                    <input type="date"
                                           v-model="localForm.expirationDate"
                                           class="form-input"
                                           :class="{ 'input-error': fieldErrors.expirationDate }" />
                                    <span v-if="fieldErrors.expirationDate" class="field-error">{{ fieldErrors.expirationDate }}</span>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Columna Derecha -->
                    <div class="form-column">
                        <!-- Stock -->
                        <div class="form-section highlight">
                            <h3 class="section-title">
                                <PackageIcon class="section-icon" />
                                Niveles de Stock
                            </h3>
                            <div class="info-box">
                                <AlertTriangleIcon class="info-icon" />
                                <span>Define el stock minimo para recibir alertas de reposicion.</span>
                            </div>
                            <div class="form-row-2">
                                <div class="form-group">
                                    <label class="form-label">Unidad <span class="required">*</span></label>
                                    <div class="select-wrapper">
                                        <select v-model="localForm.unit"
                                                class="form-select"
                                                :class="{ 'input-error': fieldErrors.unit }">
                                            <option v-for="u in units" :key="u.value" :value="u.value">
                                                {{ u.label }}
                                            </option>
                                        </select>
                                        <ChevronDownIcon class="select-arrow" />
                                    </div>
                                    <span v-if="fieldErrors.unit" class="field-error">{{ fieldErrors.unit }}</span>
                                </div>
                                <div class="form-group">
                                    <label class="form-label">Stock Actual <span class="required">*</span></label>
                                    <input type="number"
                                           v-model.number="localForm.currentStock"
                                           min="0"
                                           placeholder="0"
                                           class="form-input text-right"
                                           :class="{ 'input-error': fieldErrors.currentStock }" />
                                    <span v-if="fieldErrors.currentStock" class="field-error">{{ fieldErrors.currentStock }}</span>
                                </div>
                            </div>
                            <div class="form-row-2">
                                <div class="form-group">
                                    <label class="form-label">Stock Minimo <span class="required">*</span></label>
                                    <input type="number"
                                           v-model.number="localForm.minimumStock"
                                           min="1"
                                           placeholder="1"
                                           class="form-input text-right"
                                           :class="{ 'input-error': fieldErrors.minimumStock }" />
                                    <span v-if="fieldErrors.minimumStock" class="field-error">{{ fieldErrors.minimumStock }}</span>
                                </div>
                                <div class="form-group">
                                    <label class="form-label">Stock Maximo</label>
                                    <input type="number"
                                           v-model.number="localForm.maximumStock"
                                           min="0"
                                           placeholder="0"
                                           class="form-input text-right" />
                                </div>
                            </div>

                            <!-- Preview de estado -->
                            <div class="stock-preview">
                                <span class="preview-label">Estado actual:</span>
                                <span :class="['status-badge', getStatusClass()]">{{ getStatusLabel() }}</span>
                            </div>
                        </div>

                        <!-- Precios -->
                        <div class="form-section">
                            <h3 class="section-title">
                                <DollarSignIcon class="section-icon" />
                                Precios
                            </h3>
                            <div class="form-row-2">
                                <div class="form-group">
                                    <label class="form-label">Precio de Costo <span class="required">*</span></label>
                                    <div class="input-with-prefix">
                                        <span class="input-prefix">$</span>
                                        <input type="number"
                                               v-model.number="localForm.costPrice"
                                               min="0"
                                               step="0.01"
                                               placeholder="0.00"
                                               class="form-input with-prefix text-right"
                                               :class="{ 'input-error': fieldErrors.costPrice }" />
                                    </div>
                                    <span v-if="fieldErrors.costPrice" class="field-error">{{ fieldErrors.costPrice }}</span>
                                </div>
                                <div class="form-group">
                                    <label class="form-label">Precio de Venta</label>
                                    <div class="input-with-prefix">
                                        <span class="input-prefix">$</span>
                                        <input type="number"
                                               v-model.number="localForm.salePrice"
                                               min="0"
                                               step="0.01"
                                               placeholder="0.00"
                                               class="form-input with-prefix text-right" />
                                    </div>
                                </div>
                            </div>

                            <!-- Valor total -->
                            <div class="total-value">
                                <span class="total-label">Valor en stock:</span>
                                <span class="total-amount">${{ formatNumber(localForm.currentStock * localForm.costPrice) }}</span>
                            </div>
                        </div>

                        <!-- Notas -->
                        <div class="form-section">
                            <h3 class="section-title">
                                <FileTextIcon class="section-icon" />
                                Notas
                            </h3>
                            <textarea v-model="localForm.notes"
                                      placeholder="Observaciones adicionales..."
                                      rows="3"
                                      class="form-textarea"></textarea>
                        </div>
                    </div>
                </div>
            </div>

            <DialogFooter>
                <button class="btn btn-secondary" @click="handleCancel" :disabled="saving">
                    Cancelar
                </button>
                <button class="btn btn-primary" @click="handleSave" :disabled="saving">
                    <SaveIcon v-if="!saving" class="btn-icon" />
                    <span v-else class="spinner"></span>
                    {{ saving ? 'Guardando...' : (isEditing ? 'Guardar Cambios' : 'Crear Material') }}
                </button>
            </DialogFooter>
        </DialogContent>
    </Dialog>
</template>

<script setup>
    import { ref, watch } from 'vue'
    import {
        TagIcon,
        FolderIcon,
        PackageIcon,
        DollarSignIcon,
        MapPinIcon,
        SaveIcon,
        AlertCircleIcon,
        AlertTriangleIcon,
        ChevronDownIcon,
        FileTextIcon
    } from 'lucide-vue-next'
    import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from '@/components/ui/dialog'
    import { useStockHelpers } from '@/composables/stock/useStockHelpers'

    const props = defineProps({
        open: Boolean,
        material: Object,
        isEditing: Boolean,
        saving: Boolean
    })

    const emit = defineEmits(['update:open', 'save'])

    const { categories, units } = useStockHelpers()

    const today = new Date().toISOString().split('T')[0]

    const getEmptyForm = () => ({
        code: '',
        name: '',
        description: '',
        category: '',
        brand: '',
        supplier: '',
        unit: 'unidad',
        currentStock: 0,
        minimumStock: 1,
        maximumStock: 0,
        costPrice: 0,
        salePrice: 0,
        location: '',
        lastPurchaseDate: '',
        expirationDate: '',
        notes: ''
    })

    const localForm = ref(getEmptyForm())
    const errors = ref([])
    const fieldErrors = ref({})

    // Formato de numero
    const formatNumber = (value) => {
        if (value === null || value === undefined || isNaN(value)) return '0'
        return Number(value).toLocaleString('es-AR')
    }

    // Estado del stock - SIN SIMBOLOS
    const getStatusLabel = () => {
        const current = localForm.value.currentStock || 0
        const minimum = localForm.value.minimumStock || 1

        if (current === 0) return 'Sin Stock'
        if (current <= minimum * 0.5) return 'Critico'
        if (current <= minimum) return 'Bajo'
        return 'Optimo'
    }

    const getStatusClass = () => {
        const current = localForm.value.currentStock || 0
        const minimum = localForm.value.minimumStock || 1

        if (current === 0) return 'status-danger'
        if (current <= minimum * 0.5) return 'status-danger'
        if (current <= minimum) return 'status-warning'
        return 'status-success'
    }

    watch(() => props.open, (isOpen) => {
        if (isOpen) {
            errors.value = []
            fieldErrors.value = {}
            if (props.isEditing && props.material) {
                localForm.value = {
                    ...props.material,
                    lastPurchaseDate: props.material.lastPurchaseDate || '',
                    expirationDate: props.material.expirationDate || ''
                }
            } else {
                localForm.value = getEmptyForm()
            }
        }
    })

    const validateForm = () => {
        errors.value = []
        fieldErrors.value = {}

        if (!localForm.value.code || localForm.value.code.trim() === '') {
            errors.value.push('El codigo es obligatorio')
            fieldErrors.value.code = 'Campo obligatorio'
        }

        if (!localForm.value.name || localForm.value.name.trim() === '') {
            errors.value.push('El nombre es obligatorio')
            fieldErrors.value.name = 'Campo obligatorio'
        }

        if (!localForm.value.category) {
            errors.value.push('La categoria es obligatoria')
            fieldErrors.value.category = 'Selecciona una categoria'
        }

        if (!localForm.value.unit) {
            errors.value.push('La unidad es obligatoria')
            fieldErrors.value.unit = 'Selecciona una unidad'
        }

        if (localForm.value.minimumStock === null || localForm.value.minimumStock === undefined || localForm.value.minimumStock < 1) {
            errors.value.push('El stock minimo es obligatorio y debe ser mayor a 0')
            fieldErrors.value.minimumStock = 'Debe ser mayor a 0'
        }

        if (localForm.value.currentStock < 0) {
            errors.value.push('El stock actual no puede ser negativo')
            fieldErrors.value.currentStock = 'No puede ser negativo'
        }

        if (localForm.value.costPrice === null || localForm.value.costPrice === undefined || localForm.value.costPrice < 0) {
            errors.value.push('El precio de costo es obligatorio')
            fieldErrors.value.costPrice = 'Campo obligatorio'
        }

        if (localForm.value.maximumStock > 0 && localForm.value.maximumStock < localForm.value.minimumStock) {
            errors.value.push('El stock maximo debe ser mayor o igual al stock minimo')
        }

        return errors.value.length === 0
    }

    const handleSave = () => {
        if (validateForm()) {
            const dataToSend = {
                ...localForm.value,
                lastPurchaseDate: localForm.value.lastPurchaseDate || null,
                expirationDate: localForm.value.expirationDate || null
            }
            emit('save', dataToSend)
        }
    }

    const handleCancel = () => {
        errors.value = []
        fieldErrors.value = {}
        emit('update:open', false)
    }
</script>

<style scoped>
    .form-content {
        padding: 0.5rem 0;
    }

    .form-grid {
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 1.5rem;
    }

    .form-column {
        display: flex;
        flex-direction: column;
        gap: 1rem;
    }

    .form-section {
        background: #f9fafb;
        border-radius: 10px;
        padding: 1rem;
        border: 1px solid #e5e7eb;
    }

        .form-section.highlight {
            background: linear-gradient(135deg, #eff6ff 0%, #f0fdf4 100%);
            border-color: #bfdbfe;
        }

    .section-title {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        font-size: 0.875rem;
        font-weight: 600;
        color: #1f2937;
        margin-bottom: 0.75rem;
    }

    .section-icon {
        width: 16px;
        height: 16px;
        color: #0ea5e9;
    }

    .form-row {
        display: flex;
        gap: 0.75rem;
        margin-bottom: 0.75rem;
    }

    .form-row-2 {
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 0.75rem;
        margin-bottom: 0.75rem;
    }

    .form-row-3 {
        display: grid;
        grid-template-columns: 1fr 1fr 1fr;
        gap: 0.75rem;
    }

    .form-group {
        display: flex;
        flex-direction: column;
        flex: 1;
    }

        .form-group.flex-2 {
            flex: 2;
        }

    .form-label {
        font-size: 0.75rem;
        font-weight: 500;
        color: #374151;
        margin-bottom: 0.375rem;
    }

    .required {
        color: #dc2626;
    }

    .form-input,
    .form-select,
    .form-textarea {
        width: 100%;
        padding: 0.5rem 0.75rem;
        border: 1px solid #d1d5db;
        border-radius: 6px;
        font-size: 0.875rem;
        color: #1f2937;
        background: white;
        transition: border-color 0.15s, box-shadow 0.15s;
    }

        .form-input:focus,
        .form-select:focus,
        .form-textarea:focus {
            outline: none;
            border-color: #0ea5e9;
            box-shadow: 0 0 0 3px rgba(14, 165, 233, 0.1);
        }

        .form-input.input-error,
        .form-select.input-error {
            border-color: #dc2626;
        }

        .form-input.text-right {
            text-align: right;
            font-variant-numeric: tabular-nums;
        }

    .form-textarea {
        resize: none;
    }

    .select-wrapper {
        position: relative;
    }

    .form-select {
        appearance: none;
        padding-right: 2rem;
        cursor: pointer;
    }

    .select-arrow {
        position: absolute;
        right: 0.5rem;
        top: 50%;
        transform: translateY(-50%);
        width: 16px;
        height: 16px;
        color: #9ca3af;
        pointer-events: none;
    }

    .input-with-prefix {
        position: relative;
    }

    .input-prefix {
        position: absolute;
        left: 0.75rem;
        top: 50%;
        transform: translateY(-50%);
        color: #6b7280;
        font-size: 0.875rem;
    }

    .form-input.with-prefix {
        padding-left: 1.75rem;
    }

    .field-error {
        font-size: 0.6875rem;
        color: #dc2626;
        margin-top: 0.25rem;
    }

    /* Info Box */
    .info-box {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        padding: 0.5rem 0.75rem;
        background: #fef3c7;
        border: 1px solid #fcd34d;
        border-radius: 6px;
        margin-bottom: 0.75rem;
        font-size: 0.75rem;
        color: #92400e;
    }

    .info-icon {
        width: 14px;
        height: 14px;
        flex-shrink: 0;
    }

    /* Stock Preview */
    .stock-preview {
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 0.5rem 0.75rem;
        background: white;
        border-radius: 6px;
        border: 1px solid #e5e7eb;
    }

    .preview-label {
        font-size: 0.75rem;
        color: #6b7280;
    }

    .status-badge {
        padding: 0.25rem 0.625rem;
        border-radius: 9999px;
        font-size: 0.6875rem;
        font-weight: 600;
    }

    .status-success {
        background: #dcfce7;
        color: #166534;
    }

    .status-warning {
        background: #fef3c7;
        color: #92400e;
    }

    .status-danger {
        background: #fee2e2;
        color: #dc2626;
    }

    /* Total Value */
    .total-value {
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 0.75rem;
        background: #f0fdf4;
        border-radius: 6px;
        border: 1px solid #bbf7d0;
    }

    .total-label {
        font-size: 0.75rem;
        color: #166534;
        font-weight: 500;
    }

    .total-amount {
        font-size: 1rem;
        font-weight: 700;
        color: #166534;
    }

    /* Error Alert */
    .error-alert {
        display: flex;
        align-items: flex-start;
        gap: 0.75rem;
        padding: 0.75rem 1rem;
        background: #fef2f2;
        border: 1px solid #fecaca;
        border-radius: 8px;
        margin-bottom: 0.75rem;
    }

    .error-icon {
        width: 18px;
        height: 18px;
        color: #dc2626;
        flex-shrink: 0;
        margin-top: 0.125rem;
    }

    .error-title {
        font-size: 0.8125rem;
        font-weight: 600;
        color: #dc2626;
        margin: 0 0 0.25rem 0;
    }

    .error-list {
        font-size: 0.75rem;
        color: #b91c1c;
        margin: 0;
        padding-left: 1rem;
    }

    /* Buttons */
    .btn {
        display: inline-flex;
        align-items: center;
        justify-content: center;
        gap: 0.5rem;
        padding: 0.625rem 1.25rem;
        border-radius: 8px;
        font-size: 0.875rem;
        font-weight: 500;
        cursor: pointer;
        transition: all 0.15s;
        border: none;
    }

        .btn:disabled {
            opacity: 0.6;
            cursor: not-allowed;
        }

    .btn-primary {
        background: #0ea5e9;
        color: white;
    }

        .btn-primary:hover:not(:disabled) {
            background: #0284c7;
        }

    .btn-secondary {
        background: white;
        border: 1px solid #d1d5db;
        color: #374151;
    }

        .btn-secondary:hover:not(:disabled) {
            background: #f3f4f6;
        }

    .btn-icon {
        width: 16px;
        height: 16px;
    }

    .spinner {
        width: 16px;
        height: 16px;
        border: 2px solid transparent;
        border-top-color: white;
        border-radius: 50%;
        animation: spin 0.6s linear infinite;
    }

    @keyframes spin {
        to {
            transform: rotate(360deg);
        }
    }

    /* Responsive */
    @media (max-width: 768px) {
        .form-grid {
            grid-template-columns: 1fr;
        }

        .form-row-3 {
            grid-template-columns: 1fr;
        }

        .form-row {
            flex-direction: column;
        }
    }
</style>