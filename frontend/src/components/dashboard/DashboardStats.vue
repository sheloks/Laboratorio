<template>
    <div class="stats-grid">
        <div v-for="stat in stats"
             :key="stat.id"
             class="stat-card">
            <div class="stat-content">
                <p class="stat-label">{{ stat.label }}</p>
                <div class="stat-value-row">
                    <h3 class="stat-value">{{ stat.value }}</h3>
                    <span v-if="stat.trend" class="stat-trend" :class="stat.trend">
                        <svg v-if="stat.trend === 'up'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M7 17l5-5 5 5M7 7l5 5 5-5" />
                        </svg>
                    </span>
                </div>
                <!-- Mini chart -->
                <div class="stat-chart">
                    <svg viewBox="0 0 120 40" preserveAspectRatio="none">
                        <defs>
                            <linearGradient :id="'gradient-' + stat.id" x1="0%" y1="0%" x2="0%" y2="100%">
                                <stop offset="0%" :style="{ stopColor: stat.chartColor || '#10b981', stopOpacity: 0.3 }" />
                                <stop offset="100%" :style="{ stopColor: stat.chartColor || '#10b981', stopOpacity: 0.05 }" />
                            </linearGradient>
                        </defs>
                        <path :d="stat.chartAreaPath"
                              :fill="'url(#gradient-' + stat.id + ')'" />
                        <path :d="stat.chartPath"
                              fill="none"
                              :stroke="stat.chartColor || '#10b981'"
                              stroke-width="2"
                              stroke-linecap="round" />
                    </svg>
                </div>
            </div>
            <div class="stat-illustration">
                <!-- Ilustracion segun tipo -->
                <div v-if="stat.id === 'orders'" class="illustration orders-icon">
                    <svg viewBox="0 0 64 64" fill="none">
                        <rect x="8" y="4" width="48" height="56" rx="4" fill="#e0f2fe" stroke="#0ea5e9" stroke-width="2" />
                        <path d="M16 16h32M16 28h32M16 40h20" stroke="#0ea5e9" stroke-width="2" stroke-linecap="round" />
                        <circle cx="48" cy="48" r="12" fill="#0ea5e9" />
                        <path d="M44 48h8M48 44v8" stroke="white" stroke-width="2" stroke-linecap="round" />
                    </svg>
                </div>
                <div v-else-if="stat.id === 'pending'" class="illustration pending-icon">
                    <svg viewBox="0 0 64 64" fill="none">
                        <circle cx="32" cy="32" r="28" fill="#fef3c7" stroke="#f59e0b" stroke-width="2" />
                        <circle cx="32" cy="32" r="20" fill="white" stroke="#f59e0b" stroke-width="2" />
                        <path d="M32 18v16l10 6" stroke="#f59e0b" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round" />
                        <circle cx="32" cy="32" r="3" fill="#f59e0b" />
                    </svg>
                </div>
                <div v-else-if="stat.id === 'clients'" class="illustration clients-icon">
                    <svg viewBox="0 0 64 64" fill="none">
                        <ellipse cx="32" cy="52" rx="24" ry="8" fill="#e0f2fe" />
                        <circle cx="32" cy="24" r="16" fill="#bae6fd" stroke="#0ea5e9" stroke-width="2" />
                        <circle cx="32" cy="20" r="6" fill="#0ea5e9" />
                        <path d="M24 32c0 0 4 6 8 6s8-6 8-6" stroke="#0ea5e9" stroke-width="2" stroke-linecap="round" />
                        <rect x="28" y="38" width="8" height="12" fill="#0ea5e9" rx="2" />
                    </svg>
                </div>
                <div v-else-if="stat.id === 'revenue'" class="illustration revenue-icon">
                    <svg viewBox="0 0 64 64" fill="none">
                        <rect x="4" y="20" width="56" height="36" rx="4" fill="#dcfce7" stroke="#10b981" stroke-width="2" />
                        <rect x="8" y="24" width="48" height="8" fill="#10b981" rx="2" />
                        <circle cx="32" cy="44" r="8" fill="white" stroke="#10b981" stroke-width="2" />
                        <text x="32" y="48" text-anchor="middle" fill="#10b981" font-size="10" font-weight="bold">$</text>
                        <path d="M24 8h16l4 12H20l4-12z" fill="#10b981" />
                    </svg>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
    defineProps({
        stats: {
            type: Array,
            default: () => []
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
        border-radius: 16px;
        padding: 1.25rem;
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
        border: 1px solid #e5e7eb;
        transition: all 0.2s;
        min-height: 160px;
    }

        .stat-card:hover {
            box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
            transform: translateY(-2px);
        }

    .stat-content {
        flex: 1;
        display: flex;
        flex-direction: column;
    }

    .stat-label {
        font-size: 0.875rem;
        color: #6b7280;
        margin: 0 0 0.5rem 0;
        font-weight: 500;
    }

    .stat-value-row {
        display: flex;
        align-items: center;
        gap: 0.5rem;
    }

    .stat-value {
        font-size: 2rem;
        font-weight: 700;
        color: #1f2937;
        margin: 0;
        line-height: 1.2;
    }

    .stat-trend {
        display: flex;
        align-items: center;
    }

        .stat-trend svg {
            width: 20px;
            height: 20px;
        }

        .stat-trend.up {
            color: #10b981;
        }

        .stat-trend.down {
            color: #ef4444;
        }

    .stat-chart {
        height: 50px;
        width: 100%;
        margin-top: auto;
        padding-top: 0.75rem;
    }

        .stat-chart svg {
            width: 100%;
            height: 100%;
        }

    .stat-illustration {
        width: 80px;
        height: 80px;
        flex-shrink: 0;
        display: flex;
        align-items: center;
        justify-content: center;
    }

    .illustration {
        width: 100%;
        height: 100%;
    }

        .illustration svg {
            width: 100%;
            height: 100%;
        }

    @media (max-width: 1400px) {
        .stats-grid {
            grid-template-columns: repeat(2, 1fr);
        }
    }

    @media (max-width: 768px) {
        .stats-grid {
            grid-template-columns: 1fr;
        }

        .stat-card {
            min-height: 140px;
        }
    }
</style>