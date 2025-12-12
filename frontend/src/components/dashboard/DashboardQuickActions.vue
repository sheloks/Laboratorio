<template>
    <div class="quick-actions">
        <h3 class="section-title">Acciones Rapidas</h3>
        <div class="actions-list">
            <button 
                v-for="action in actions" 
                :key="action.id"
                class="action-btn"
                @click="$emit('action', action.id)"
            >
                <div class="action-icon" :style="{ background: action.bgColor }">
                    <component :is="action.icon" :style="{ color: action.iconColor }" />
                </div>
                <span class="action-label">{{ action.label }}</span>
                <ChevronRightIcon class="action-arrow" />
            </button>
        </div>
    </div>
</template>

<script setup>
import { 
    ClipboardPlusIcon, 
    UserPlusIcon, 
    TruckIcon, 
    ReceiptIcon,
    ChevronRightIcon
} from 'lucide-vue-next'

defineEmits(['action'])

const actions = [
    {
        id: 'newOrder',
        label: 'Nueva Orden',
        icon: ClipboardPlusIcon,
        bgColor: '#dbeafe',
        iconColor: '#2563eb'
    },
    {
        id: 'newClient',
        label: 'Nuevo Cliente',
        icon: UserPlusIcon,
        bgColor: '#dcfce7',
        iconColor: '#16a34a'
    },
    {
        id: 'newShipment',
        label: 'Nuevo Envio',
        icon: TruckIcon,
        bgColor: '#fef3c7',
        iconColor: '#d97706'
    },
    {
        id: 'newInvoice',
        label: 'Nueva Factura',
        icon: ReceiptIcon,
        bgColor: '#f3e8ff',
        iconColor: '#7c3aed'
    }
]
</script>

<style scoped>
.quick-actions {
    background: white;
    border-radius: 12px;
    padding: 1.25rem;
    border: 1px solid #e5e7eb;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
    height: fit-content;
}

.section-title {
    font-size: 0.9375rem;
    font-weight: 600;
    color: #1f2937;
    margin: 0 0 1rem 0;
}

.actions-list {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
}

.action-btn {
    display: flex;
    align-items: center;
    gap: 0.75rem;
    padding: 0.75rem;
    background: #f9fafb;
    border: 1px solid #f3f4f6;
    border-radius: 10px;
    cursor: pointer;
    transition: all 0.2s;
    width: 100%;
    text-align: left;
}

.action-btn:hover {
    background: #f3f4f6;
    border-color: #e5e7eb;
    transform: translateX(4px);
}

.action-icon {
    width: 40px;
    height: 40px;
    border-radius: 10px;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
}

.action-icon svg {
    width: 20px;
    height: 20px;
}

.action-label {
    flex: 1;
    font-size: 0.875rem;
    font-weight: 500;
    color: #374151;
}

.action-arrow {
    width: 16px;
    height: 16px;
    color: #9ca3af;
    flex-shrink: 0;
}

.action-btn:hover .action-arrow {
    color: #6b7280;
}
</style>