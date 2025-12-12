<template>
    <div class="envio-card" @click="$emit('view', envio)">
        <div class="card-content">
            <!-- Header -->
            <div class="card-header">
                <div class="header-left">
                    <span class="order-number">{{ envio.orderNumber }}</span>
                    <span :class="['status-badge', getStatusColor(envio.status)]">
                        {{ getStatusLabel(envio.status) }}
                    </span>
                    <span :class="['type-badge', getTypeColor(envio.type)]">
                        {{ getTypeLabel(envio.type) }}
                    </span>
                </div>
            </div>

            <!-- Info Grid -->
            <div class="info-grid">
                <!-- Cliente -->
                <div class="info-column">
                    <div class="info-item">
                        <UserIcon class="info-icon" />
                        <span class="info-text bold">{{ envio.clientName }}</span>
                    </div>
                    <div class="info-item">
                        <MapPinIcon class="info-icon" />
                        <span class="info-text">{{ envio.clientAddress }}</span>
                    </div>
                    <div class="info-item">
                        <PhoneIcon class="info-icon" />
                        <span class="info-text">{{ envio.clientPhone }}</span>
                    </div>
                </div>

                <!-- Envio -->
                <div class="info-column">
                    <div class="info-item">
                        <Building2Icon class="info-icon" />
                        <span class="info-text bold">{{ envio.company }}</span>
                    </div>
                    <div class="info-item">
                        <PackageIcon class="info-icon" />
                        <span class="info-text">Tracking: {{ envio.trackingNumber || 'Sin asignar' }}</span>
                    </div>
                    <div class="info-item">
                        <CalendarIcon class="info-icon" />
                        <span class="info-text">
                            {{ formatDate(envio.pickupDate) }}
                            <ArrowRightIcon class="arrow-icon" />
                            {{ formatDate(envio.deliveryDate) }}
                        </span>
                    </div>
                </div>
            </div>

            <!-- Footer -->
            <div class="card-footer">
                <span class="cost-label">Costo de envio:</span>
                <span class="cost-value">{{ formatCurrency(envio.cost) }}</span>
            </div>
        </div>

        <!-- Actions -->
        <div class="card-actions" @click.stop>
            <button class="action-btn" @click="$emit('view', envio)" title="Ver detalles">
                <EyeIcon />
            </button>
            <button class="action-btn" @click="$emit('edit', envio)" title="Editar">
                <EditIcon />
            </button>
            <button class="action-btn delete" @click="$emit('delete', envio)" title="Eliminar">
                <Trash2Icon />
            </button>
        </div>
    </div>
</template>

<script setup>
import {
    UserIcon,
    MapPinIcon,
    PhoneIcon,
    Building2Icon,
    PackageIcon,
    CalendarIcon,
    ArrowRightIcon,
    EyeIcon,
    EditIcon,
    Trash2Icon
} from 'lucide-vue-next'
import { useEnvioHelpers } from '@/composables/envio/useEnvioHelpers'

defineProps({
    envio: {
        type: Object,
        required: true
    }
})

defineEmits(['view', 'edit', 'delete'])

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
    .envio-card {
        background: white;
        border-radius: 12px;
        border: 1px solid #e5e7eb;
        padding: 1.25rem;
        display: flex;
        gap: 1rem;
        transition: all 0.2s;
        cursor: pointer;
    }

        .envio-card:hover {
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
            border-color: #d1d5db;
        }

    .card-content {
        flex: 1;
        min-width: 0;
    }

    .card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 1rem;
    }

    .header-left {
        display: flex;
        align-items: center;
        gap: 0.75rem;
        flex-wrap: wrap;
    }

    .order-number {
        font-size: 1.125rem;
        font-weight: 600;
        color: #1f2937;
    }

    .status-badge,
    .type-badge {
        padding: 0.25rem 0.625rem;
        border-radius: 6px;
        font-size: 0.75rem;
        font-weight: 600;
    }

    .info-grid {
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 1.5rem;
        margin-bottom: 1rem;
    }

    .info-column {
        display: flex;
        flex-direction: column;
        gap: 0.5rem;
    }

    .info-item {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        color: #6b7280;
        font-size: 0.875rem;
    }

    .info-icon {
        width: 16px;
        height: 16px;
        flex-shrink: 0;
    }

    .info-text {
        display: flex;
        align-items: center;
        gap: 0.25rem;
    }

        .info-text.bold {
            font-weight: 500;
            color: #374151;
        }

    .arrow-icon {
        width: 12px;
        height: 12px;
        margin: 0 0.25rem;
    }

    .card-footer {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding-top: 1rem;
        border-top: 1px solid #f3f4f6;
    }

    .cost-label {
        font-size: 0.875rem;
        color: #6b7280;
    }

    .cost-value {
        font-size: 1.125rem;
        font-weight: 600;
        color: #1f2937;
    }

    .card-actions {
        display: flex;
        flex-direction: column;
        gap: 0.5rem;
    }

    .action-btn {
        width: 36px;
        height: 36px;
        border-radius: 8px;
        border: 1px solid #e5e7eb;
        background: white;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #6b7280;
        cursor: pointer;
        transition: all 0.2s;
    }

        .action-btn:hover {
            background: #f3f4f6;
            color: #1f2937;
        }

        .action-btn.delete:hover {
            background: #fef2f2;
            color: #dc2626;
            border-color: #fecaca;
        }

        .action-btn svg {
            width: 16px;
            height: 16px;
        }

    @media (max-width: 768px) {
        .info-grid {
            grid-template-columns: 1fr;
            gap: 1rem;
        }

        .card-actions {
            flex-direction: row;
        }
    }
</style>