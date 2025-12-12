<template>
    <div class="orders-section">
        <div class="section-header">
            <div>
                <h2 class="section-title">Ordenes Recientes</h2>
                <p class="section-subtitle">Ultimas ordenes ingresadas al sistema</p>
            </div>
        </div>

        <!-- Orders List -->
        <div class="orders-list">
            <div v-for="order in orders"
                 :key="order.id"
                 class="order-item"
                 @click="$emit('orderClick', order)">
                <!-- Odontograma Icon -->
                <div class="order-tooth" :class="`tooth-${order.status || 'pending'}`">
                    <svg viewBox="0 0 36 44" fill="none" class="tooth-svg">
                        <!-- Diente -->
                        <path d="M18 2C11 2 5 8 5 16C5 22 7 28 9 33C11 38 13 42 18 42C23 42 25 38 27 33C29 28 31 22 31 16C31 8 25 2 18 2Z"
                              fill="currentColor"
                              stroke="#94a3b8"
                              stroke-width="1" />
                        <!-- Corona del diente -->
                        <path d="M10 13C12 11 15 10 18 10C21 10 24 11 26 13"
                              stroke="#64748b"
                              stroke-width="0.8"
                              fill="none" />
                        <!-- Division -->
                        <line x1="18" y1="10" x2="18" y2="22" stroke="#64748b" stroke-width="0.5" opacity="0.4" />
                        <line x1="10" y1="16" x2="26" y2="16" stroke="#64748b" stroke-width="0.5" opacity="0.4" />
                        <!-- Numero de pieza -->
                        <text x="18"
                              y="34"
                              text-anchor="middle"
                              fill="#475569"
                              font-size="8"
                              font-weight="bold">{{ order.toothNumber || '11' }}</text>
                    </svg>
                </div>

                <!-- Order Info -->
                <div class="order-content">
                    <div class="order-main">
                        <span class="order-id">{{ order.orderNumber }}</span>
                        <span :class="['order-badge', `badge-${order.status || 'pending'}`]">
                            {{ getStatusLabel(order.status) }}
                        </span>
                    </div>
                    <p class="order-client">{{ order.client }}</p>
                    <p class="order-work">{{ order.workType }}</p>
                </div>

                <!-- Order Price & Date -->
                <div class="order-right">
                    <span class="order-price">{{ formatCurrency(order.price) }}</span>
                    <span class="order-date">
                        <CalendarIcon class="date-icon" />
                        {{ formatDate(order.deliveryDate) }}
                    </span>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
    import { CalendarIcon } from 'lucide-vue-next'

    defineProps({
        orders: {
            type: Array,
            default: () => []
        }
    })

    defineEmits(['orderClick'])

    const getStatusLabel = (status) => {
        const labels = {
            pending: 'Pendiente',
            in_progress: 'En Proceso',
            ready: 'Lista',
            delivered: 'Entregada'
        }
        return labels[status] || 'Pendiente'
    }

    const formatDate = (dateString) => {
        if (!dateString) return ''
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
    .orders-section {
        background: white;
        border-radius: 16px;
        padding: 1.5rem;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
        border: 1px solid #e5e7eb;
    }

    .section-header {
        margin-bottom: 1.25rem;
    }

    .section-title {
        font-size: 1rem;
        font-weight: 600;
        color: #1f2937;
        margin: 0 0 0.25rem 0;
    }

    .section-subtitle {
        font-size: 0.8125rem;
        color: #6b7280;
        margin: 0;
    }

    /* Orders List */
    .orders-list {
        display: flex;
        flex-direction: column;
        gap: 0.75rem;
    }

    .order-item {
        display: flex;
        align-items: center;
        gap: 1rem;
        padding: 1rem;
        background: #f9fafb;
        border: 1px solid #f3f4f6;
        border-radius: 12px;
        cursor: pointer;
        transition: all 0.2s;
    }

        .order-item:hover {
            background: #f3f4f6;
            border-color: #e5e7eb;
            transform: translateY(-1px);
        }

    /* Tooth Icon */
    .order-tooth {
        width: 48px;
        height: 56px;
        flex-shrink: 0;
        display: flex;
        align-items: center;
        justify-content: center;
    }

    .tooth-svg {
        width: 100%;
        height: 100%;
    }

    .tooth-pending {
        color: #fef3c7;
    }

    .tooth-in_progress {
        color: #dbeafe;
    }

    .tooth-ready {
        color: #dcfce7;
    }

    .tooth-delivered {
        color: #e0e7ff;
    }

    /* Order Content */
    .order-content {
        flex: 1;
        min-width: 0;
    }

    .order-main {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        margin-bottom: 0.25rem;
    }

    .order-id {
        font-size: 0.8125rem;
        font-weight: 600;
        color: #1f2937;
    }

    .order-badge {
        padding: 0.125rem 0.5rem;
        border-radius: 4px;
        font-size: 0.6875rem;
        font-weight: 600;
    }

    .badge-pending {
        background: #fef3c7;
        color: #d97706;
    }

    .badge-in_progress {
        background: #dbeafe;
        color: #2563eb;
    }

    .badge-ready {
        background: #dcfce7;
        color: #16a34a;
    }

    .badge-delivered {
        background: #e0e7ff;
        color: #4f46e5;
    }

    .order-client {
        font-size: 0.9375rem;
        font-weight: 500;
        color: #374151;
        margin: 0 0 0.125rem 0;
    }

    .order-work {
        font-size: 0.8125rem;
        color: #6b7280;
        margin: 0;
    }

    /* Order Right */
    .order-right {
        text-align: right;
        flex-shrink: 0;
    }

    .order-price {
        display: block;
        font-size: 1rem;
        font-weight: 700;
        color: #1f2937;
        margin-bottom: 0.25rem;
    }

    .order-date {
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

    @media (max-width: 640px) {
        .order-item {
            flex-wrap: wrap;
        }

        .order-right {
            width: 100%;
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding-top: 0.75rem;
            border-top: 1px solid #e5e7eb;
            margin-top: 0.5rem;
        }
    }
</style>