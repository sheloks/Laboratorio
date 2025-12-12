<template>
    <div class="chart-card">
        <div class="chart-header">
            <h3 class="chart-title">Ingresos Mensuales</h3>
            <p class="chart-subtitle">Ultimos 8 meses</p>
        </div>
        <div class="chart-container">
            <div class="bar-chart">
                <div class="chart-y-axis">
                    <span>300k</span>
                    <span>150k</span>
                    <span>0</span>
                </div>
                <div class="chart-bars">
                    <div v-for="(item, index) in data"
                         :key="index"
                         class="bar-item">
                        <div class="bar-wrapper">
                            <div class="bar"
                                 :style="{ height: getBarHeight(item.revenue) + '%' }">
                                <span class="bar-tooltip">{{ formatCurrency(item.revenue) }}</span>
                            </div>
                        </div>
                        <span class="bar-label">{{ item.month }}</span>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
    const props = defineProps({
        data: {
            type: Array,
            default: () => [
                { month: 'Ene', revenue: 185000 },
                { month: 'Feb', revenue: 210000 },
                { month: 'Mar', revenue: 195000 },
                { month: 'Abr', revenue: 240000 },
                { month: 'May', revenue: 280000 },
                { month: 'Jun', revenue: 310000 },
                { month: 'Jul', revenue: 295000 },
                { month: 'Ago', revenue: 325000 }
            ]
        }
    })

    const maxRevenue = 350000

    const getBarHeight = (revenue) => {
        return (revenue / maxRevenue) * 100
    }

    const formatCurrency = (amount) => {
        return new Intl.NumberFormat('es-AR', {
            style: 'currency',
            currency: 'ARS',
            minimumFractionDigits: 0,
            maximumFractionDigits: 0
        }).format(amount)
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
        font-size: 1rem;
        font-weight: 600;
        color: #1f2937;
        margin: 0 0 0.25rem 0;
    }

    .chart-subtitle {
        font-size: 0.8125rem;
        color: #6b7280;
        margin: 0;
    }

    .chart-container {
        height: 180px;
    }

    .bar-chart {
        display: flex;
        height: 100%;
        gap: 0.5rem;
    }

    .chart-y-axis {
        display: flex;
        flex-direction: column;
        justify-content: space-between;
        padding-bottom: 24px;
        font-size: 0.6875rem;
        color: #9ca3af;
        min-width: 36px;
    }

    .chart-bars {
        flex: 1;
        display: flex;
        align-items: flex-end;
        gap: 0.5rem;
        padding-bottom: 24px;
        border-bottom: 1px solid #e5e7eb;
    }

    .bar-item {
        flex: 1;
        display: flex;
        flex-direction: column;
        align-items: center;
        height: 100%;
    }

    .bar-wrapper {
        flex: 1;
        width: 100%;
        display: flex;
        align-items: flex-end;
        justify-content: center;
    }

    .bar {
        width: 70%;
        max-width: 40px;
        background: linear-gradient(180deg, #3b82f6 0%, #60a5fa 100%);
        border-radius: 6px 6px 0 0;
        position: relative;
        transition: height 0.5s ease;
        cursor: pointer;
    }

        .bar:hover {
            background: linear-gradient(180deg, #2563eb 0%, #3b82f6 100%);
        }

    .bar-tooltip {
        position: absolute;
        bottom: 100%;
        left: 50%;
        transform: translateX(-50%);
        background: #1f2937;
        color: white;
        padding: 0.25rem 0.5rem;
        border-radius: 4px;
        font-size: 0.6875rem;
        white-space: nowrap;
        opacity: 0;
        transition: opacity 0.2s;
        pointer-events: none;
    }

    .bar:hover .bar-tooltip {
        opacity: 1;
    }

    .bar-label {
        font-size: 0.75rem;
        color: #6b7280;
        margin-top: 0.5rem;
    }
</style>