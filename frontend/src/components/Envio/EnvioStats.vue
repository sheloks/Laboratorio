<template>
    <div class="stats-grid">
        <div class="stat-card">
            <div class="stat-header">
                <span class="stat-title">Total Envios</span>
                <div class="stat-icon blue">
                    <TruckIcon />
                </div>
            </div>
            <div class="stat-value">{{ stats.total }}</div>
            <p class="stat-subtitle">este mes</p>
        </div>

        <div class="stat-card highlight-yellow">
            <div class="stat-header">
                <span class="stat-title">En Transito</span>
                <div class="stat-icon yellow">
                    <AlertCircleIcon />
                </div>
            </div>
            <div class="stat-value">{{ stats.inTransit }}</div>
            <p class="stat-subtitle">en camino</p>
        </div>

        <div class="stat-card">
            <div class="stat-header">
                <span class="stat-title">Programados</span>
                <div class="stat-icon blue">
                    <CalendarIcon />
                </div>
            </div>
            <div class="stat-value">{{ stats.scheduled }}</div>
            <p class="stat-subtitle">proximos dias</p>
        </div>

        <div class="stat-card">
            <div class="stat-header">
                <span class="stat-title">Entregados</span>
                <div class="stat-icon green">
                    <CheckCircleIcon />
                </div>
            </div>
            <div class="stat-value">{{ stats.delivered }}</div>
            <p class="stat-subtitle">completados</p>
        </div>
    </div>
</template>

<script setup>
import { TruckIcon, AlertCircleIcon, CalendarIcon, CheckCircleIcon } from 'lucide-vue-next'

defineProps({
    stats: {
        type: Object,
        required: true
    }
})
</script>

<style scoped>
.stats-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 1.25rem;
    margin-bottom: 1.5rem;
}

.stat-card {
    background: white;
    border-radius: 12px;
    padding: 1.25rem;
    border: 1px solid #e5e7eb;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.stat-card.highlight-yellow {
    border-color: #fbbf24;
    background: #fffbeb;
}

.stat-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 0.75rem;
}

.stat-title {
    font-size: 0.875rem;
    font-weight: 500;
    color: #6b7280;
}

.stat-icon {
    width: 36px;
    height: 36px;
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
}

.stat-icon svg {
    width: 20px;
    height: 20px;
}

.stat-icon.blue {
    background: #dbeafe;
    color: #2563eb;
}

.stat-icon.yellow {
    background: #fef3c7;
    color: #d97706;
}

.stat-icon.green {
    background: #dcfce7;
    color: #16a34a;
}

.stat-value {
    font-size: 2rem;
    font-weight: 700;
    color: #1f2937;
    line-height: 1;
    margin-bottom: 0.25rem;
}

.stat-subtitle {
    font-size: 0.75rem;
    color: #9ca3af;
    margin: 0;
}

@media (max-width: 1200px) {
    .stats-grid {
        grid-template-columns: repeat(2, 1fr);
    }
}

@media (max-width: 640px) {
    .stats-grid {
        grid-template-columns: 1fr;
    }
}
</style>