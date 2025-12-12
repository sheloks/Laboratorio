<template>
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6 mb-8">
        <!-- Gráfico de Ingresos Mensuales -->
        <Card class="lg:col-span-2">
            <CardHeader>
                <CardTitle class="flex items-center gap-2">
                    <BarChart3Icon class="h-5 w-5" />
                    Resumen de Ingresos
                </CardTitle>
                <CardDescription>
                    Evolucion de ingresos mensuales
                </CardDescription>
            </CardHeader>
            <CardContent>
                <div class="h-64">
                    <canvas ref="barChartRef"></canvas>
                </div>
            </CardContent>
        </Card>

        <!-- Gráfico de Distribución por Estado -->
        <Card>
            <CardHeader>
                <CardTitle class="flex items-center gap-2">
                    <PieChartIcon class="h-5 w-5" />
                    Distribucion por Estado
                </CardTitle>
                <CardDescription>
                    Estado de las facturas
                </CardDescription>
            </CardHeader>
            <CardContent>
                <div class="h-48 flex justify-center">
                    <canvas ref="pieChartRef"></canvas>
                </div>
                <!-- Leyenda -->
                <div class="mt-4 space-y-2">
                    <div class="flex items-center justify-between text-sm">
                        <div class="flex items-center">
                            <span class="w-3 h-3 rounded-full bg-green-500 mr-2"></span>
                            <span>Pagadas</span>
                        </div>
                        <span class="font-medium">{{ stats.paidCount }}</span>
                    </div>
                    <div class="flex items-center justify-between text-sm">
                        <div class="flex items-center">
                            <span class="w-3 h-3 rounded-full bg-yellow-500 mr-2"></span>
                            <span>Pendientes</span>
                        </div>
                        <span class="font-medium">{{ stats.pendingCount }}</span>
                    </div>
                    <div class="flex items-center justify-between text-sm">
                        <div class="flex items-center">
                            <span class="w-3 h-3 rounded-full bg-red-500 mr-2"></span>
                            <span>Vencidas</span>
                        </div>
                        <span class="font-medium">{{ stats.overdueCount }}</span>
                    </div>
                </div>
            </CardContent>
        </Card>
    </div>
</template>

<script setup>
    import { ref, onMounted, watch } from 'vue'
    import { BarChart3Icon, PieChartIcon } from 'lucide-vue-next'
    import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
    import Chart from 'chart.js/auto'

    const props = defineProps({
        stats: {
            type: Object,
            required: true
        },
        monthlyData: {
            type: Array,
            default: () => []
        }
    })

    const barChartRef = ref(null)
    const pieChartRef = ref(null)
    let barChart = null
    let pieChart = null

    // Datos de ejemplo para gráfico de barras (últimos 6 meses)
    const getMonthlyData = () => {
        if (props.monthlyData.length > 0) {
            return props.monthlyData
        }
        // Datos de ejemplo
        const months = ['Jul', 'Ago', 'Sep', 'Oct', 'Nov', 'Dic']
        const currentMonth = new Date().getMonth()
        const labels = []
        for (let i = 5; i >= 0; i--) {
            const monthIndex = (currentMonth - i + 12) % 12
            labels.push(['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun', 'Jul', 'Ago', 'Sep', 'Oct', 'Nov', 'Dic'][monthIndex])
        }
        return {
            labels,
            data: [185000, 220000, 195000, 280000, 310000, props.stats.totalRevenue || 250000]
        }
    }

    const initBarChart = () => {
        if (!barChartRef.value) return

        const ctx = barChartRef.value.getContext('2d')
        const monthlyData = getMonthlyData()

        if (barChart) barChart.destroy()

        barChart = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: monthlyData.labels,
                datasets: [{
                    label: 'Ingresos',
                    data: monthlyData.data,
                    backgroundColor: 'rgba(59, 130, 246, 0.8)',
                    borderColor: 'rgba(59, 130, 246, 1)',
                    borderWidth: 1,
                    borderRadius: 4
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        display: false
                    },
                    tooltip: {
                        callbacks: {
                            label: (context) => {
                                return `$${context.raw.toLocaleString('es-AR')}`
                            }
                        }
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            callback: (value) => `$${(value / 1000).toFixed(0)}k`
                        }
                    }
                }
            }
        })
    }

    const initPieChart = () => {
        if (!pieChartRef.value) return

        const ctx = pieChartRef.value.getContext('2d')

        if (pieChart) pieChart.destroy()

        pieChart = new Chart(ctx, {
            type: 'doughnut',
            data: {
                labels: ['Pagadas', 'Pendientes', 'Vencidas'],
                datasets: [{
                    data: [
                        props.stats.paidCount || 0,
                        props.stats.pendingCount || 0,
                        props.stats.overdueCount || 0
                    ],
                    backgroundColor: [
                        'rgba(34, 197, 94, 0.8)',
                        'rgba(234, 179, 8, 0.8)',
                        'rgba(239, 68, 68, 0.8)'
                    ],
                    borderColor: [
                        'rgba(34, 197, 94, 1)',
                        'rgba(234, 179, 8, 1)',
                        'rgba(239, 68, 68, 1)'
                    ],
                    borderWidth: 2
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        display: false
                    }
                },
                cutout: '60%'
            }
        })
    }

    onMounted(() => {
        initBarChart()
        initPieChart()
    })

    // Actualizar gráficos cuando cambian los stats
    watch(() => props.stats, () => {
        initBarChart()
        initPieChart()
    }, { deep: true })
</script>