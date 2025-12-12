<template>
    <div v-if="open" class="modal-overlay" @click.self="$emit('update:open', false)">
        <div class="modal-container">
            <!-- Header -->
            <div class="modal-header">
                <div class="warning-icon">
                    <AlertTriangleIcon />
                </div>
                <h2 class="modal-title">Eliminar Envio</h2>
            </div>

            <!-- Body -->
            <div class="modal-body">
                <p class="confirm-text">
                    ¿Estas seguro de que deseas eliminar este envio?
                </p>
                <div v-if="envio" class="envio-info">
                    <div class="info-row">
                        <span class="info-label">Cliente:</span>
                        <span class="info-value">{{ envio.clienteNombre }}</span>
                    </div>
                    <div class="info-row">
                        <span class="info-label">Direccion:</span>
                        <span class="info-value">{{ envio.direccion }}</span>
                    </div>
                    <div class="info-row">
                        <span class="info-label">Estado:</span>
                        <span :class="['status-badge', `badge-${envio.estado}`]">
                            {{ getStatusLabel(envio.estado) }}
                        </span>
                    </div>
                </div>
                <p class="warning-text">
                    Esta accion no se puede deshacer.
                </p>
            </div>

            <!-- Footer -->
            <div class="modal-footer">
                <button class="btn btn-secondary" @click="$emit('update:open', false)" :disabled="deleting">
                    Cancelar
                </button>
                <button class="btn btn-danger" @click="$emit('confirm')" :disabled="deleting">
                    <div v-if="deleting" class="spinner-sm"></div>
                    {{ deleting ? 'Eliminando...' : 'Eliminar' }}
                </button>
            </div>
        </div>
    </div>
</template>

<script setup>
    import { AlertTriangleIcon } from 'lucide-vue-next'

    defineProps({
        open: Boolean,
        envio: Object,
        deleting: Boolean
    })

    defineEmits(['update:open', 'confirm'])

    const getStatusLabel = (status) => {
        const labels = {
            PROGRAMADO: 'Programado',
            EN_TRANSITO: 'En Transito',
            ENTREGADO: 'Entregado',
            CANCELADO: 'Cancelado'
        }
        return labels[status] || status
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
        max-width: 420px;
        overflow: hidden;
    }

    .modal-header {
        padding: 1.5rem;
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 1rem;
    }

    .warning-icon {
        width: 56px;
        height: 56px;
        border-radius: 50%;
        background: #fef2f2;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #dc2626;
    }

        .warning-icon svg {
            width: 28px;
            height: 28px;
        }

    .modal-title {
        font-size: 1.25rem;
        font-weight: 600;
        color: #1f2937;
        margin: 0;
    }

    .modal-body {
        padding: 0 1.5rem 1.5rem;
        text-align: center;
    }

    .confirm-text {
        font-size: 0.9375rem;
        color: #374151;
        margin: 0 0 1rem 0;
    }

    .envio-info {
        background: #f9fafb;
        border-radius: 8px;
        padding: 1rem;
        text-align: left;
        margin-bottom: 1rem;
    }

    .info-row {
        display: flex;
        gap: 0.5rem;
        margin-bottom: 0.5rem;
    }

        .info-row:last-child {
            margin-bottom: 0;
        }

    .info-label {
        font-size: 0.8125rem;
        color: #6b7280;
        min-width: 80px;
    }

    .info-value {
        font-size: 0.8125rem;
        color: #1f2937;
        font-weight: 500;
    }

    .status-badge {
        padding: 0.125rem 0.5rem;
        border-radius: 4px;
        font-size: 0.75rem;
        font-weight: 500;
    }

    .badge-PROGRAMADO {
        background: #dbeafe;
        color: #1e40af;
    }

    .badge-EN_TRANSITO {
        background: #fef3c7;
        color: #92400e;
    }

    .badge-ENTREGADO {
        background: #dcfce7;
        color: #166534;
    }

    .badge-CANCELADO {
        background: #fee2e2;
        color: #991b1b;
    }

    .warning-text {
        font-size: 0.8125rem;
        color: #dc2626;
        margin: 0;
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

    .btn-danger {
        background: #dc2626;
        color: white;
    }

        .btn-danger:hover {
            background: #b91c1c;
        }

    .btn:disabled {
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

    @keyframes spin {
        to {
            transform: rotate(360deg);
        }
    }
</style>