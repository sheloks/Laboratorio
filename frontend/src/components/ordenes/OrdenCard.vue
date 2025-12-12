<template>
    <div class="orden-card" @click="$emit('click', orden)">
        <!-- Odontograma Icon -->
        <div class="orden-tooth"
             :class="`tooth-${orden.status || 'pending'}`"
             @click.stop="$emit('openOdontograma', orden)"
             title="Ver odontograma">
            <svg viewBox="0 0 36 44" fill="none" class="tooth-svg">
                <path d="M18 2C11 2 5 8 5 16C5 22 7 28 9 33C11 38 13 42 18 42C23 42 25 38 27 33C29 28 31 22 31 16C31 8 25 2 18 2Z"
                      fill="currentColor"
                      stroke="#94a3b8"
                      stroke-width="1" />
                <path d="M10 13C12 11 15 10 18 10C21 10 24 11 26 13"
                      stroke="#64748b"
                      stroke-width="0.8"
                      fill="none" />
                <line x1="18" y1="10" x2="18" y2="22" stroke="#64748b" stroke-width="0.5" opacity="0.4" />
                <line x1="10" y1="16" x2="26" y2="16" stroke="#64748b" stroke-width="0.5" opacity="0.4" />
                <text x="18"
                      y="34"
                      text-anchor="middle"
                      fill="#475569"
                      font-size="8"
                      font-weight="bold">{{ orden.toothNumber || '--' }}</text>
            </svg>
        </div>

        <!-- Order Info -->
        <div class="orden-content">
            <div class="orden-main">
                <span class="orden-id">{{ orden.orderNumber }}</span>
                <span :class="['orden-badge', `badge-${orden.priority || 'normal'}`]">
                    {{ getPriorityLabel(orden.priority) }}
                </span>
                <span :class="['orden-status', `status-${orden.status || 'pending'}`]">
                    <component :is="getStatusIcon(orden.status)" class="status-icon" />
                    {{ getStatusLabel(orden.status) }}
                </span>
            </div>
            <div class="orden-details">
                <span class="orden-client">
                    <UserIcon class="detail-icon" />
                    Cliente: {{ orden.clientName || orden.client }}
                </span>
                <span class="orden-doctor">
                    <StethoscopeIcon class="detail-icon" />
                    Odontologo: {{ orden.doctorName || 'No asignado' }}
                </span>
            </div>
            <div class="orden-meta">
                <span class="orden-date">
                    <CalendarIcon class="detail-icon" />
                    Entrega: {{ formatDate(orden.deliveryDate) }}
                </span>
                <span class="orden-price-small">
                    <DollarSignIcon class="detail-icon" />
                    {{ formatCurrency(orden.price || orden.total) }}
                </span>
            </div>
            <p v-if="orden.workType || orden.description" class="orden-work">
                {{ orden.workType || orden.description }}
            </p>
        </div>

        <!-- Order Price & Actions -->
        <div class="orden-right">
            <span class="orden-price">{{ formatCurrency(orden.price || orden.total) }}</span>
            <span class="orden-date-badge">
                <CalendarIcon class="date-icon" />
                {{ formatDate(orden.deliveryDate) }}
            </span>
        </div>
    </div>
</template>

<script setup>
    import {
        UserIcon,
        CalendarIcon,
        DollarSignIcon,
        ClockIcon,
        PlayCircleIcon,
        CheckCircleIcon,
        PackageIcon,
        StethoscopeIcon
    } from 'lucide-vue-next'

    defineProps({
        orden: {
            type: Object,
            required: true
        }
    })

    defineEmits(['click', 'openOdontograma'])

    const getPriorityLabel = (priority) => {
        const labels = {
            alta: 'Alta',
            normal: 'Normal',
            urgente: 'Urgente'
        }
        return labels[priority] || 'Normal'
    }

    const getStatusLabel = (status) => {
        const labels = {
            pending: 'Pendiente',
            in_progress: 'En Proceso',
            ready: 'Lista',
            delivered: 'Entregada'
        }
        return labels[status] || 'Pendiente'
    }

    const getStatusIcon = (status) => {
        const icons = {
            pending: ClockIcon,
            in_progress: PlayCircleIcon,
            ready: CheckCircleIcon,
            delivered: PackageIcon
        }
        return icons[status] || ClockIcon
    }

    const formatDate = (dateString) => {
        if (!dateString) return '--'
        return new Date(dateString).toLocaleDateString('es-AR')
    }

    const formatCurrency = (amount) => {
        return new Intl.NumberFormat('es-AR', {
            style: 'currency',
            currency: 'ARS',
            minimumFractionDigits: 0
        }).format(amount || 0)
    }
</script>

<style scoped>
    .orden-card {
        display: flex;
        align-items: center;
        gap: 1rem;
        padding: 1rem 1.25rem;
        background: white;
        border: 1px solid #e5e7eb;
        border-radius: 12px;
        cursor: pointer;
        transition: all 0.2s;
    }

        .orden-card:hover {
            border-color: #d1d5db;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
        }

    /* Tooth Icon */
    .orden-tooth {
        width: 52px;
        height: 60px;
        flex-shrink: 0;
        display: flex;
        align-items: center;
        justify-content: center;
        border-radius: 10px;
        transition: all 0.2s;
        cursor: pointer;
    }

        .orden-tooth:hover {
            transform: scale(1.05);
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        }

    .tooth-svg {
        width: 100%;
        height: 100%;
    }

    .tooth-pending {
        color: #fef3c7;
        background: #fffbeb;
    }

    .tooth-in_progress {
        color: #dbeafe;
        background: #eff6ff;
    }

    .tooth-ready {
        color: #dcfce7;
        background: #f0fdf4;
    }

    .tooth-delivered {
        color: #e5e7eb;
        background: #f9fafb;
    }

    /* Order Content */
    .orden-content {
        flex: 1;
        min-width: 0;
    }

    .orden-main {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        margin-bottom: 0.5rem;
        flex-wrap: wrap;
    }

    .orden-id {
        font-size: 0.9375rem;
        font-weight: 600;
        color: #1f2937;
    }

    .orden-badge {
        padding: 0.125rem 0.5rem;
        border-radius: 4px;
        font-size: 0.6875rem;
        font-weight: 600;
        text-transform: uppercase;
    }

    .badge-normal {
        background: #e5e7eb;
        color: #374151;
    }

    .badge-alta {
        background: #dbeafe;
        color: #1e40af;
    }

    .badge-urgente {
        background: #fee2e2;
        color: #dc2626;
    }

    .orden-status {
        display: flex;
        align-items: center;
        gap: 0.25rem;
        padding: 0.125rem 0.5rem;
        border-radius: 4px;
        font-size: 0.6875rem;
        font-weight: 500;
    }

    .status-icon {
        width: 12px;
        height: 12px;
    }

    .status-pending {
        background: #fef3c7;
        color: #d97706;
    }

    .status-in_progress {
        background: #dbeafe;
        color: #2563eb;
    }

    .status-ready {
        background: #dcfce7;
        color: #16a34a;
    }

    .status-delivered {
        background: #f3f4f6;
        color: #374151;
    }

    .orden-details {
        display: flex;
        gap: 1rem;
        margin-bottom: 0.375rem;
        flex-wrap: wrap;
    }

    .orden-client,
    .orden-doctor {
        display: flex;
        align-items: center;
        gap: 0.375rem;
        font-size: 0.8125rem;
        color: #374151;
    }

    .detail-icon {
        width: 14px;
        height: 14px;
        color: #9ca3af;
    }

    .orden-meta {
        display: none;
    }

    .orden-work {
        font-size: 0.8125rem;
        color: #6b7280;
        margin: 0.375rem 0 0 0;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
    }

    /* Order Right */
    .orden-right {
        text-align: right;
        flex-shrink: 0;
    }

    .orden-price {
        display: block;
        font-size: 1.125rem;
        font-weight: 700;
        color: #1f2937;
        margin-bottom: 0.375rem;
    }

    .orden-date-badge {
        display: flex;
        align-items: center;
        justify-content: flex-end;
        gap: 0.25rem;
        font-size: 0.75rem;
        color: #6b7280;
    }

    .date-icon {
        width: 12px;
        height: 12px;
    }

    /* Responsive */
    @media (max-width: 768px) {
        .orden-card {
            flex-wrap: wrap;
        }

        .orden-details {
            display: none;
        }

        .orden-meta {
            display: flex;
            gap: 1rem;
            flex-wrap: wrap;
        }

        .orden-right {
            display: none;
        }

        .orden-price-small {
            display: flex;
            align-items: center;
            gap: 0.25rem;
            font-size: 0.875rem;
            font-weight: 600;
            color: #1f2937;
        }
    }
</style>