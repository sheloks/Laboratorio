<template>
    <div class="system-status">
        <h3 class="section-title">Estado del Sistema</h3>
        <div class="status-list">
            <div v-for="item in statusItems"
                 :key="item.id"
                 class="status-item">
                <span class="status-name">{{ item.name }}</span>
                <span :class="['status-badge', item.status]">
                    {{ item.label }}
                </span>
            </div>
        </div>
    </div>
</template>

<script setup>
    import { ref } from 'vue'

    const statusItems = ref([
        { id: 1, name: 'Inventario', status: 'ok', label: 'OK' },
        { id: 2, name: 'Facturacion AFIP', status: 'active', label: 'Activo' },
        { id: 3, name: 'Email SMTP', status: 'configured', label: 'Configurado' },
        { id: 4, name: 'WhatsApp', status: 'active', label: 'Activo' }
    ])
</script>

<style scoped>
    .system-status {
        background: white;
        border-radius: 12px;
        padding: 1.25rem;
        border: 1px solid #e5e7eb;
        box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
    }

    .section-title {
        font-size: 0.9375rem;
        font-weight: 600;
        color: #1f2937;
        margin: 0 0 1rem 0;
    }

    .status-list {
        display: flex;
        flex-direction: column;
        gap: 0.625rem;
    }

    .status-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 0.625rem 0.75rem;
        background: #f9fafb;
        border-radius: 8px;
    }

    .status-name {
        font-size: 0.8125rem;
        color: #374151;
    }

    .status-badge {
        padding: 0.25rem 0.625rem;
        border-radius: 6px;
        font-size: 0.75rem;
        font-weight: 600;
    }

        .status-badge.ok {
            background: #dcfce7;
            color: #16a34a;
        }

        .status-badge.active {
            background: #dbeafe;
            color: #2563eb;
        }

        .status-badge.configured {
            background: #e0e7ff;
            color: #4f46e5;
        }

        .status-badge.warning {
            background: #fef3c7;
            color: #d97706;
        }

        .status-badge.error {
            background: #fee2e2;
            color: #dc2626;
        }
</style>