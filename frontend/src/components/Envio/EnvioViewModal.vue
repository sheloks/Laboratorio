<template>
    <div v-if="open" class="modal-overlay" @click.self="$emit('update:open', false)">
        <div class="modal-container">
            <div class="modal-header">
                <div class="header-content">
                    <h2 class="modal-title">{{ envio?.orderNumber }}</h2>
                    <div class="badges">
                        <span :class="['status-badge', getStatusColor(envio?.status)]">
                            {{ getStatusLabel(envio?.status) }}
                        </span>
                        <span :class="['type-badge', getTypeColor(envio?.type)]">
                            {{ getTypeLabel(envio?.type) }}
                        </span>
                    </div>
                </div>
                <button class="close-btn" @click="$emit('update:open', false)">
                    <XIcon />
                </button>
            </div>

            <div class="modal-body" v-if="envio">
                <!-- Cliente -->
                <div class="info-section">
                    <h3 class="section-title">
                        <UserIcon class="section-icon" />
                        Cliente
                    </h3>
                    <div class="info-grid">
                        <div class="info-item">
                            <span class="info-label">Nombre</span>
                            <span class="info-value">{{ envio.clientName }}</span>
                        </div>
                        <div class="info-item">
                            <span class="info-label">Telefono</span>
                            <span class="info-value">{{ envio.clientPhone || '-' }}</span>
                        </div>
                        <div class="info-item full">
                            <span class="info-label">Direccion</span>
                            <span class="info-value">{{ envio.clientAddress }}</span>
                        </div>
                    </div>
                </div>

                <!-- Envio -->
                <div class="info-section">
                    <h3 class="section-title">
                        <TruckIcon class="section-icon" />
                        Datos del Envio
                    </h3>
                    <div class="info-grid">
                        <div class="info-item">
                            <span class="info-label">Empresa</span>
                            <span class="info-value">{{ envio.company }}</span>
                        </div>
                        <div class="info-item">
                            <span class="info-label">Codigo de Seguimiento</span>
                            <span class="info-value tracking">{{ envio.trackingNumber || 'Sin asignar' }}</span>
                        </div>
                        <div class="info-item">
                            <span class="info-label">Fecha de Retiro</span>
                            <span class="info-value">{{ formatDate(envio.pickupDate) }}</span>
                        </div>
                        <div class="info-item">
                            <span class="info-label">Fecha de Entrega</span>
                            <span class="info-value">{{ formatDate(envio.deliveryDate) }}</span>
                        </div>
                    </div>
                </div>

                <!-- Costo -->
                <div class="cost-section">
                    <span class="cost-label">Costo de Envio</span>
                    <span class="cost-value">{{ formatCurrency(envio.cost) }}</span>
                </div>

                <!-- Notas -->
                <div v-if="envio.notes" class="notes-section">
                    <h3 class="section-title">
                        <FileTextIcon class="section-icon" />
                        Notas
                    </h3>
                    <p class="notes-text">{{ envio.notes }}</p>
                </div>

                <!-- Timeline -->
                <div class="timeline-section">
                    <h3 class="section-title">
                        <ClockIcon class="section-icon" />
                        Historial
                    </h3>
                    <div class="timeline">
                        <div class="timeline-item">
                            <div class="timeline-dot"></div>
                            <div class="timeline-content">
                                <span class="timeline-date">{{ formatDate(envio.createdAt) }}</span>
                                <span class="timeline-text">Envio creado</span>
                            </div>
                        </div>
                        <div class="timeline-item">
                            <div class="timeline-dot active"></div>
                            <div class="timeline-content">
                                <span class="timeline-date">{{ formatDate(envio.updatedAt) }}</span>
                                <span class="timeline-text">Ultima actualizacion</span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="modal-footer">
                <button class="btn btn-secondary" @click="$emit('update:open', false)">
                    Cerrar
                </button>
                <button class="btn btn-primary" @click="$emit('edit', envio)">
                    <EditIcon class="btn-icon" />
                    Editar
                </button>
            </div>
        </div>
    </div>
</template>

<script setup>
import {
    XIcon,
    UserIcon,
    TruckIcon,
    FileTextIcon,
    ClockIcon,
    EditIcon
} from 'lucide-vue-next'
import { useEnvioHelpers } from '@/composables/envio/useEnvioHelpers'

defineProps({
    open: Boolean,
    envio: Object
})

defineEmits(['update:open', 'edit'])

const {
    getStatusLabel,
    getStatusColor,
    getTypeLabel,
    getTypeColor,
    formatCurrency,
    formatDate
} = useEnvioHelpers()
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
        max-width: 560px;
        max-height: 90vh;
        overflow: hidden;
        display: flex;
        flex-direction: column;
    }

    .modal-header {
        padding: 1.5rem;
        border-bottom: 1px solid #e5e7eb;
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
    }

    .header-content {
        flex: 1;
    }

    .modal-title {
        font-size: 1.25rem;
        font-weight: 600;
        color: #1f2937;
        margin: 0 0 0.75rem 0;
    }

    .badges {
        display: flex;
        gap: 0.5rem;
    }

    .status-badge,
    .type-badge {
        padding: 0.25rem 0.625rem;
        border-radius: 6px;
        font-size: 0.75rem;
        font-weight: 600;
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

    .info-section {
        margin-bottom: 1.5rem;
    }

    .section-title {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        font-size: 0.875rem;
        font-weight: 600;
        color: #374151;
        margin: 0 0 1rem 0;
    }

    .section-icon {
        width: 18px;
        height: 18px;
        color: #6b7280;
    }

    .info-grid {
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 1rem;
    }

    .info-item {
        display: flex;
        flex-direction: column;
        gap: 0.25rem;
    }

        .info-item.full {
            grid-column: span 2;
        }

    .info-label {
        font-size: 0.75rem;
        color: #6b7280;
        text-transform: uppercase;
        letter-spacing: 0.025em;
    }

    .info-value {
        font-size: 0.9375rem;
        color: #1f2937;
        font-weight: 500;
    }

        .info-value.tracking {
            font-family: monospace;
            background: #f3f4f6;
            padding: 0.25rem 0.5rem;
            border-radius: 4px;
            display: inline-block;
        }

    .cost-section {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 1rem;
        background: #f9fafb;
        border-radius: 8px;
        margin-bottom: 1.5rem;
    }

    .cost-label {
        font-size: 0.875rem;
        color: #6b7280;
    }

    .cost-value {
        font-size: 1.5rem;
        font-weight: 700;
        color: #1f2937;
    }

    .notes-section {
        margin-bottom: 1.5rem;
    }

    .notes-text {
        font-size: 0.875rem;
        color: #4b5563;
        line-height: 1.5;
        margin: 0;
        padding: 1rem;
        background: #f9fafb;
        border-radius: 8px;
    }

    .timeline-section {
        border-top: 1px solid #e5e7eb;
        padding-top: 1.5rem;
    }

    .timeline {
        display: flex;
        flex-direction: column;
        gap: 1rem;
    }

    .timeline-item {
        display: flex;
        gap: 1rem;
        align-items: flex-start;
    }

    .timeline-dot {
        width: 10px;
        height: 10px;
        border-radius: 50%;
        background: #d1d5db;
        margin-top: 4px;
        flex-shrink: 0;
    }

        .timeline-dot.active {
            background: #0ea5e9;
        }

    .timeline-content {
        display: flex;
        flex-direction: column;
        gap: 0.125rem;
    }

    .timeline-date {
        font-size: 0.75rem;
        color: #6b7280;
    }

    .timeline-text {
        font-size: 0.875rem;
        color: #1f2937;
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

    .btn-icon {
        width: 16px;
        height: 16px;
    }
</style>