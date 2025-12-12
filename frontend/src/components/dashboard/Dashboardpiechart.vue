<template>
    <div class="chart-card">
        <div class="chart-header">
            <h3 class="chart-title">Ordenes por Estado</h3>
            <p class="chart-subtitle">Distribucion actual</p>
        </div>
        <div class="chart-container">
            <div class="pie-chart">
                <svg viewBox="0 0 200 200" class="pie-svg">
                    <circle v-for="(segment, index) in pieSegments"
                            :key="index"
                            cx="100"
                            cy="100"
                            r="70"
                            fill="none"
                            :stroke="segment.color"
                            stroke-width="35"
                            :stroke-dasharray="segment.dashArray"
                            :stroke-dashoffset="segment.dashOffset"
                            :transform="`rotate(-90 100 100)`"
                            class="pie-segment" />
                    <!-- Centro blanco -->
                    <circle cx="100" cy="100" r="45" fill="white" />
                    <text x="100" y="95" text-anchor="middle" class="pie-total-label">Total</text>
                    <text x="100" y="112" text-anchor="middle" class="pie-total-value">{{ total }}</text>
                </svg>
            </div>
            <div class="pie-legend">
                <div v-for="(item, index) in data"
                     :key="index"
                     class="legend-item">
                    <span class="legend-dot" :style="{ background: item.color }"></span>
                    <span class="legend-label">{{ item.name }}</span>
                    <span class="legend-value">{{ item.value }}</span>
                    <span class="legend-percent">{{ getPercent(item.value) }}%</span>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
    import { computed } from 'vue'

    const props = defineProps({
        data: {
            type: Array,
            default: () => [
                { name: 'Pendientes', value: 23, color: '#f59e0b' },
                { name: 'En Proceso', value: 15, color: '#3b82f6' },
                { name: 'Listas', value: 12, color: '#10b981' },
                { name: 'Entregadas', value: 106, color: '#6366f1' }
            ]
        }
    })

    const total = computed(() => {
        return props.data.reduce((sum, item) => sum + item.value, 0)
    })

    const circumference = 2 * Math.PI * 70 // r = 70

    const pieSegments = computed(() => {
        let offset = 0
        return props.data.map(item => {
            const percent = item.value / total.value
            const dashArray = `${circumference * percent} ${circumference * (1 - percent)}`
            const dashOffset = -offset
            offset += circumference * percent
            return {
                ...item,
                dashArray,
                dashOffset
            }
        })
    })

    const getPercent = (value) => {
        return Math.round((value / total.value) * 100)
    }
</script>

<style scoped>
    .chart-card {
        background: white;
        border-radius: 12px;
        padding: 1rem 1.25rem;
        border: 1px solid #e5e7eb;
        box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
    }

    .chart-header {
        margin-bottom: 0.75rem;
    }

    .chart-title {
        font-size: 0.9375rem;
        font-weight: 600;
        color: #1f2937;
        margin: 0 0 0.125rem 0;
    }

    .chart-subtitle {
        font-size: 0.75rem;
        color: #6b7280;
        margin: 0;
    }

    .chart-container {
        display: flex;
        align-items: center;
        gap: 1.25rem;
    }

    .pie-chart {
        flex-shrink: 0;
    }

    .pie-svg {
        width: 140px;
        height: 140px;
    }

    .pie-segment {
        transition: opacity 0.2s;
        cursor: pointer;
    }

        .pie-segment:hover {
            opacity: 0.8;
        }

    .pie-total-label {
        font-size: 10px;
        fill: #6b7280;
    }

    .pie-total-value {
        font-size: 20px;
        font-weight: 700;
        fill: #1f2937;
    }

    .pie-legend {
        flex: 1;
        display: flex;
        flex-direction: column;
        gap: 0.5rem;
    }

    .legend-item {
        display: flex;
        align-items: center;
        gap: 0.5rem;
    }

    .legend-dot {
        width: 10px;
        height: 10px;
        border-radius: 3px;
        flex-shrink: 0;
    }

    .legend-label {
        flex: 1;
        font-size: 0.8125rem;
        color: #374151;
    }

    .legend-value {
        font-size: 0.8125rem;
        font-weight: 600;
        color: #1f2937;
        min-width: 26px;
        text-align: right;
    }

    .legend-percent {
        font-size: 0.6875rem;
        color: #6b7280;
        min-width: 32px;
        text-align: right;
    }

    @media (max-width: 500px) {
        .chart-container {
            flex-direction: column;
        }
    }
</style>