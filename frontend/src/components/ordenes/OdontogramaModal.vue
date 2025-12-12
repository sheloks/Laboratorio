<template>
    <div v-if="open" class="modal-overlay" @click.self="$emit('update:open', false)">
        <div class="modal-container">
            <!-- Header -->
            <div class="modal-header">
                <div>
                    <h2 class="modal-title">Odontograma</h2>
                    <p class="modal-subtitle">Orden: {{ orderNumber }}</p>
                </div>
                <button class="close-btn" @click="$emit('update:open', false)">
                    <XIcon />
                </button>
            </div>

            <!-- Body -->
            <div class="modal-body">
                <!-- Arcada Superior -->
                <div class="arcada">
                    <span class="arcada-label">Superior</span>
                    <div class="dientes-row">
                        <div v-for="tooth in upperTeeth"
                             :key="tooth.number"
                             class="diente-wrapper"
                             :class="{ selected: isSelected(tooth.number) }"
                             @click="toggleTooth(tooth.number)">
                            <svg viewBox="0 0 40 50" class="diente-svg">
                                <path d="M20 3C13 3 7 9 7 17C7 23 9 29 11 35C13 41 15 47 20 47C25 47 27 41 29 35C31 29 33 23 33 17C33 9 27 3 20 3Z"
                                      :fill="getToothFill(tooth.number)"
                                      stroke="#64748b"
                                      stroke-width="1" />
                                <path d="M11 14C13 12 17 11 20 11C23 11 27 12 29 14"
                                      stroke="#64748b"
                                      stroke-width="0.8"
                                      fill="none" />
                                <line x1="20" y1="11" x2="20" y2="24" stroke="#64748b" stroke-width="0.5" opacity="0.5" />
                                <line x1="12" y1="18" x2="28" y2="18" stroke="#64748b" stroke-width="0.5" opacity="0.5" />
                            </svg>
                            <span class="diente-numero">{{ tooth.number }}</span>
                        </div>
                    </div>
                </div>

                <!-- Linea divisoria -->
                <div class="arcada-divider">
                    <span class="divider-label">Derecha</span>
                    <div class="divider-line"></div>
                    <span class="divider-label">Izquierda</span>
                </div>

                <!-- Arcada Inferior -->
                <div class="arcada">
                    <div class="dientes-row">
                        <div v-for="tooth in lowerTeeth"
                             :key="tooth.number"
                             class="diente-wrapper"
                             :class="{ selected: isSelected(tooth.number) }"
                             @click="toggleTooth(tooth.number)">
                            <span class="diente-numero">{{ tooth.number }}</span>
                            <svg viewBox="0 0 40 50" class="diente-svg inverted">
                                <path d="M20 3C13 3 7 9 7 17C7 23 9 29 11 35C13 41 15 47 20 47C25 47 27 41 29 35C31 29 33 23 33 17C33 9 27 3 20 3Z"
                                      :fill="getToothFill(tooth.number)"
                                      stroke="#64748b"
                                      stroke-width="1" />
                                <path d="M11 14C13 12 17 11 20 11C23 11 27 12 29 14"
                                      stroke="#64748b"
                                      stroke-width="0.8"
                                      fill="none" />
                                <line x1="20" y1="11" x2="20" y2="24" stroke="#64748b" stroke-width="0.5" opacity="0.5" />
                                <line x1="12" y1="18" x2="28" y2="18" stroke="#64748b" stroke-width="0.5" opacity="0.5" />
                            </svg>
                        </div>
                    </div>
                    <span class="arcada-label">Inferior</span>
                </div>

                <!-- Leyenda -->
                <div class="legend">
                    <div class="legend-item">
                        <span class="legend-dot normal"></span>
                        <span>Normal</span>
                    </div>
                    <div class="legend-item">
                        <span class="legend-dot selected"></span>
                        <span>Seleccionado</span>
                    </div>
                    <div class="legend-item">
                        <span class="legend-dot work"></span>
                        <span>Con trabajo</span>
                    </div>
                </div>

                <!-- Piezas seleccionadas -->
                <div v-if="selectedTeethList.length > 0" class="selected-info">
                    <span class="selected-label">Piezas seleccionadas:</span>
                    <div class="selected-tags">
                        <span v-for="num in selectedTeethList"
                              :key="num"
                              class="selected-tag">
                            {{ num }}
                            <button class="tag-remove" @click="toggleTooth(num)">&times;</button>
                        </span>
                    </div>
                </div>
            </div>

            <!-- Footer -->
            <div class="modal-footer">
                <button class="btn btn-secondary" @click="$emit('update:open', false)">
                    Cerrar
                </button>
                <button class="btn btn-primary" @click="handleSave">
                    <CheckIcon class="btn-icon" />
                    Guardar Seleccion
                </button>
            </div>
        </div>
    </div>
</template>

<script setup>
    import { ref, computed, watch } from 'vue'
    import { XIcon, CheckIcon } from 'lucide-vue-next'

    const props = defineProps({
        open: Boolean,
        orderNumber: String,
        initialSelected: {
            type: Array,
            default: () => []
        },
        teethWithWork: {
            type: Array,
            default: () => []
        }
    })

    const emit = defineEmits(['update:open', 'save'])

    // Dientes superiores (18-11 derecha, 21-28 izquierda)
    const upperTeeth = [
        { number: 18 }, { number: 17 }, { number: 16 }, { number: 15 },
        { number: 14 }, { number: 13 }, { number: 12 }, { number: 11 },
        { number: 21 }, { number: 22 }, { number: 23 }, { number: 24 },
        { number: 25 }, { number: 26 }, { number: 27 }, { number: 28 }
    ]

    // Dientes inferiores (48-41 derecha, 31-38 izquierda)
    const lowerTeeth = [
        { number: 48 }, { number: 47 }, { number: 46 }, { number: 45 },
        { number: 44 }, { number: 43 }, { number: 42 }, { number: 41 },
        { number: 31 }, { number: 32 }, { number: 33 }, { number: 34 },
        { number: 35 }, { number: 36 }, { number: 37 }, { number: 38 }
    ]

    const selectedTeeth = ref(new Set())

    // Inicializar con seleccion previa
    watch(() => props.open, (isOpen) => {
        if (isOpen && props.initialSelected) {
            selectedTeeth.value = new Set(props.initialSelected)
        }
    }, { immediate: true })

    const selectedTeethList = computed(() => {
        return Array.from(selectedTeeth.value).sort((a, b) => a - b)
    })

    const isSelected = (number) => {
        return selectedTeeth.value.has(number)
    }

    const hasWork = (number) => {
        return props.teethWithWork.includes(number)
    }

    const getToothFill = (number) => {
        if (isSelected(number)) return '#dbeafe'
        if (hasWork(number)) return '#dcfce7'
        return '#f1f5f9'
    }

    const toggleTooth = (number) => {
        if (selectedTeeth.value.has(number)) {
            selectedTeeth.value.delete(number)
        } else {
            selectedTeeth.value.add(number)
        }
        // Forzar reactividad
        selectedTeeth.value = new Set(selectedTeeth.value)
    }

    const handleSave = () => {
        emit('save', selectedTeethList.value)
        emit('update:open', false)
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
        max-width: 800px;
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
        align-items: flex-start;
    }

    .modal-title {
        font-size: 1.25rem;
        font-weight: 600;
        color: #1f2937;
        margin: 0 0 0.25rem 0;
    }

    .modal-subtitle {
        font-size: 0.875rem;
        color: #6b7280;
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

    /* Arcadas */
    .arcada {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 0.5rem;
    }

    .arcada-label {
        font-size: 0.75rem;
        font-weight: 600;
        color: #6b7280;
        text-transform: uppercase;
        letter-spacing: 0.05em;
    }

    .dientes-row {
        display: flex;
        justify-content: center;
        gap: 0.25rem;
    }

    .diente-wrapper {
        display: flex;
        flex-direction: column;
        align-items: center;
        cursor: pointer;
        padding: 0.25rem;
        border-radius: 6px;
        transition: all 0.2s;
    }

        .diente-wrapper:hover {
            background: #f3f4f6;
        }

        .diente-wrapper.selected {
            background: #dbeafe;
        }

    .diente-svg {
        width: 36px;
        height: 44px;
        transition: transform 0.2s;
    }

        .diente-svg.inverted {
            transform: rotate(180deg);
        }

    .diente-wrapper:hover .diente-svg {
        transform: scale(1.1);
    }

        .diente-wrapper:hover .diente-svg.inverted {
            transform: rotate(180deg) scale(1.1);
        }

    .diente-numero {
        font-size: 0.6875rem;
        font-weight: 600;
        color: #374151;
    }

    /* Divider */
    .arcada-divider {
        display: flex;
        align-items: center;
        gap: 1rem;
        margin: 1rem 0;
        padding: 0 2rem;
    }

    .divider-label {
        font-size: 0.6875rem;
        color: #9ca3af;
        text-transform: uppercase;
    }

    .divider-line {
        flex: 1;
        height: 2px;
        background: linear-gradient(90deg, #e5e7eb 0%, #d1d5db 50%, #e5e7eb 100%);
    }

    /* Legend */
    .legend {
        display: flex;
        justify-content: center;
        gap: 1.5rem;
        margin-top: 1.5rem;
        padding-top: 1rem;
        border-top: 1px solid #f3f4f6;
    }

    .legend-item {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        font-size: 0.8125rem;
        color: #6b7280;
    }

    .legend-dot {
        width: 14px;
        height: 14px;
        border-radius: 4px;
        border: 1px solid #d1d5db;
    }

        .legend-dot.normal {
            background: #f1f5f9;
        }

        .legend-dot.selected {
            background: #dbeafe;
            border-color: #3b82f6;
        }

        .legend-dot.work {
            background: #dcfce7;
            border-color: #22c55e;
        }

    /* Selected Info */
    .selected-info {
        margin-top: 1rem;
        padding: 1rem;
        background: #f9fafb;
        border-radius: 8px;
    }

    .selected-label {
        font-size: 0.8125rem;
        font-weight: 500;
        color: #374151;
        display: block;
        margin-bottom: 0.5rem;
    }

    .selected-tags {
        display: flex;
        flex-wrap: wrap;
        gap: 0.5rem;
    }

    .selected-tag {
        display: inline-flex;
        align-items: center;
        gap: 0.25rem;
        padding: 0.25rem 0.5rem;
        background: #dbeafe;
        color: #1e40af;
        border-radius: 4px;
        font-size: 0.8125rem;
        font-weight: 500;
    }

    .tag-remove {
        background: none;
        border: none;
        color: #1e40af;
        cursor: pointer;
        font-size: 1rem;
        line-height: 1;
        padding: 0;
        margin-left: 0.125rem;
    }

        .tag-remove:hover {
            color: #dc2626;
        }

    /* Footer */
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
        background: #3b82f6;
        color: white;
    }

        .btn-primary:hover {
            background: #2563eb;
        }

    .btn-icon {
        width: 16px;
        height: 16px;
    }

    @media (max-width: 640px) {
        .diente-svg {
            width: 28px;
            height: 36px;
        }

        .dientes-row {
            gap: 0.125rem;
        }

        .diente-wrapper {
            padding: 0.125rem;
        }
    }
</style>