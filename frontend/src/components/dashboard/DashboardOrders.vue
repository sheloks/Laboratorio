<template>
    <div class="orders-section">
        <div class="section-header">
            <h2>Ordenes</h2>
        </div>

        <div class="orders-table-container">
            <table class="orders-table">
                <thead>
                    <tr>
                        <th class="col-number">#</th>
                        <th class="col-client">Cliente</th>
                        <th class="col-date">Fecha</th>
                        <th class="col-status">Estado</th>
                        <th class="col-price">Precio</th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="order in orders"
                        :key="order.id"
                        @click="$emit('orderClick', order)"
                        class="order-row">
                        <td class="order-number">{{ order.orderNumber }}</td>
                        <td class="order-client">{{ order.client }}</td>
                        <td class="order-date">{{ formatDate(order.date) }}</td>
                        <td>
                            <span :class="['status-badge', `status-${order.status}`]">
                                {{ getStatusLabel(order.status) }}
                            </span>
                        </td>
                        <td class="order-price">$ {{ formatNumber(order.price) }}</td>
                    </tr>
                </tbody>
            </table>
        </div>

        <div v-if="orders.length === 0" class="empty-state">
            <ClipboardListIcon class="empty-icon" />
            <p>No hay ordenes recientes</p>
        </div>
    </div>
</template>

<script setup>
    import { ClipboardListIcon } from 'lucide-vue-next'

    defineProps({
        orders: {
            type: Array,
            default: () => []
        }
    })

    defineEmits(['orderClick'])

    const formatDate = (dateString) => {
        const date = new Date(dateString)
        return date.toLocaleDateString('es-AR', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric'
        }) + ' ' + date.toLocaleTimeString('es-AR', {
            hour: '2-digit',
            minute: '2-digit'
        })
    }

    const formatNumber = (num) => {
        return new Intl.NumberFormat('es-AR').format(num)
    }

    const getStatusLabel = (status) => {
        const labels = {
            alta: 'Alta',
            normal: 'Normal',
            urgente: 'Urgente',
            completada: 'Completada',
            pendiente: 'Pendiente'
        }
        return labels[status] || status
    }
</script>

<style scoped>
    .orders-section {
        background: white;
        border-radius: 16px;
        padding: 1.5rem;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
        border: 1px solid #e5e7eb;
    }

    .section-header {
        margin-bottom: 1rem;
    }

        .section-header h2 {
            font-size: 1.125rem;
            font-weight: 600;
            color: #1f2937;
            margin: 0;
        }

    .orders-table-container {
        overflow-x: auto;
    }

    .orders-table {
        width: 100%;
        border-collapse: collapse;
    }

        .orders-table th {
            text-align: left;
            padding: 0.875rem 1rem;
            font-size: 0.875rem;
            font-weight: 600;
            color: #374151;
            border-bottom: 2px solid #e5e7eb;
            background: #f9fafb;
            white-space: nowrap;
        }

            .orders-table th:first-child {
                border-radius: 8px 0 0 0;
            }

            .orders-table th:last-child {
                border-radius: 0 8px 0 0;
                text-align: right;
            }

    .col-number {
        width: 60px;
    }

    .col-client {
        width: auto;
    }

    .col-date {
        width: 160px;
    }

    .col-status {
        width: 100px;
    }

    .col-price {
        width: 120px;
        text-align: right;
    }

    .order-row {
        cursor: pointer;
        transition: background 0.15s;
    }

        .order-row:hover {
            background: #f0f9ff;
        }

    .orders-table td {
        padding: 0.875rem 1rem;
        border-bottom: 1px solid #f3f4f6;
        font-size: 0.875rem;
        color: #1f2937;
        vertical-align: middle;
    }

    .order-number {
        font-weight: 600;
        color: #1f2937;
    }

    .order-client {
        font-weight: 500;
    }

    .order-date {
        color: #6b7280;
        font-size: 0.8125rem;
        font-variant-numeric: tabular-nums;
    }

    .order-price {
        font-weight: 600;
        text-align: right;
        font-variant-numeric: tabular-nums;
    }

    .status-badge {
        display: inline-flex;
        padding: 0.375rem 0.75rem;
        border-radius: 6px;
        font-size: 0.75rem;
        font-weight: 600;
        text-transform: capitalize;
    }

    .status-alta {
        background: #dbeafe;
        color: #1e40af;
    }

    .status-normal {
        background: #e5e7eb;
        color: #374151;
    }

    .status-urgente {
        background: #fee2e2;
        color: #dc2626;
    }

    .status-completada {
        background: #dcfce7;
        color: #166534;
    }

    .status-pendiente {
        background: #fef3c7;
        color: #d97706;
    }

    .empty-state {
        text-align: center;
        padding: 3rem;
        color: #9ca3af;
    }

    .empty-icon {
        width: 48px;
        height: 48px;
        margin-bottom: 1rem;
        opacity: 0.5;
    }

    .empty-state p {
        margin: 0;
    }
</style>