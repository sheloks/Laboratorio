<template>
    <div class="dashboard-wrapper">
        <!-- Header -->
        <AppHeader />

        <div class="dashboard-page">
            <!-- Page Header -->
            <div class="page-header">
                <div>
                    <h1 class="page-title">Dashboard</h1>
                    <p class="page-subtitle">Resumen general de tu laboratorio dental</p>
                </div>
            </div>

            <!-- Stats Cards -->
            <div class="stats-grid">
                <!-- Ordenes Totales -->
                <div class="stat-card">
                    <div class="stat-header">
                        <div class="stat-info">
                            <span class="stat-label">Ordenes Totales</span>
                            <span class="stat-value">{{ stats.totalOrders }}</span>
                            <span class="stat-detail">{{ stats.pendingOrders }} pendientes</span>
                        </div>
                        <div class="stat-icon">
                            <PackageIcon />
                        </div>
                    </div>
                </div>

                <!-- Ingresos del Mes -->
                <div class="stat-card">
                    <div class="stat-header">
                        <div class="stat-info">
                            <span class="stat-label">Ingresos del Mes</span>
                            <span class="stat-value">{{ formatCurrency(stats.totalRevenue) }}</span>
                            <span class="stat-trend up">
                                <TrendingUpIcon class="trend-icon" />
                                +{{ stats.monthlyGrowth }}% vs mes anterior
                            </span>
                        </div>
                        <div class="stat-icon green">
                            <DollarSignIcon />
                        </div>
                    </div>
                </div>

                <!-- Clientes Activos -->
                <div class="stat-card">
                    <div class="stat-header">
                        <div class="stat-info">
                            <span class="stat-label">Clientes Activos</span>
                            <span class="stat-value">{{ stats.activeClients }}</span>
                            <span class="stat-detail">odontologos registrados</span>
                        </div>
                        <div class="stat-icon blue">
                            <UsersIcon />
                        </div>
                    </div>
                </div>

                <!-- Tiempo de Entrega -->
                <div class="stat-card">
                    <div class="stat-header">
                        <div class="stat-info">
                            <span class="stat-label">Tiempo de Entrega</span>
                            <span class="stat-value">{{ stats.avgDeliveryTime }} dias</span>
                            <span class="stat-detail">promedio de fabricacion</span>
                        </div>
                        <div class="stat-icon orange">
                            <ClockIcon />
                        </div>
                    </div>
                </div>
            </div>

            <!-- Main Content with Sidebar -->
            <div class="main-layout">
                <!-- Left Column - Charts & Orders -->
                <div class="main-content">
                    <!-- Charts Row -->
                    <div class="charts-grid">
                        <DashboardRevenueChart :data="monthlyRevenue" />
                        <DashboardPieChart :data="ordersByStatus" />
                    </div>

                    <!-- Orders Section -->
                    <DashboardOrdersList :orders="recentOrders"
                                         @order-click="handleOrderClick" />
                </div>

                <!-- Right Column - Quick Actions & System Status -->
                <div class="sidebar-content">
                    <DashboardQuickActions @action="handleQuickAction" />
                    <DashboardSystemStatus />
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
    import { ref } from 'vue'
    import { useRouter } from 'vue-router'
    import {
        PackageIcon,
        DollarSignIcon,
        UsersIcon,
        ClockIcon,
        TrendingUpIcon
    } from 'lucide-vue-next'

    import AppHeader from '@/components/layout/AppHeader.vue'
    import DashboardQuickActions from '@/components/dashboard/DashboardQuickActions.vue'
    import DashboardSystemStatus from '@/components/dashboard/DashboardSystemStatus.vue'
    import DashboardRevenueChart from '@/components/dashboard/DashboardRevenueChart.vue'
    import DashboardPieChart from '@/components/dashboard/DashboardPieChart.vue'
    import DashboardOrdersList from '@/components/dashboard/DashboardOrdersList.vue'

    const router = useRouter()

    // Stats
    const stats = ref({
        totalOrders: 156,
        pendingOrders: 23,
        completedOrders: 118,
        totalRevenue: 2450000,
        monthlyGrowth: 12.5,
        activeClients: 45,
        avgDeliveryTime: 7
    })

    // Datos para grafico de barras
    const monthlyRevenue = ref([
        { month: 'Ene', revenue: 185000 },
        { month: 'Feb', revenue: 210000 },
        { month: 'Mar', revenue: 195000 },
        { month: 'Abr', revenue: 240000 },
        { month: 'May', revenue: 280000 },
        { month: 'Jun', revenue: 310000 },
        { month: 'Jul', revenue: 295000 },
        { month: 'Ago', revenue: 325000 }
    ])

    // Datos para grafico de torta
    const ordersByStatus = ref([
        { name: 'Pendientes', value: 23, color: '#f59e0b' },
        { name: 'En Proceso', value: 15, color: '#3b82f6' },
        { name: 'Listas', value: 12, color: '#10b981' },
        { name: 'Entregadas', value: 106, color: '#1f2937' }
    ])

    // Ordenes recientes
    const recentOrders = ref([
        {
            id: 1,
            orderNumber: 'ORD-2024-156',
            client: 'Dr. Carlos Rodriguez',
            workType: 'Corona de porcelana',
            toothNumber: '14',
            status: 'in_progress',
            price: 45000,
            deliveryDate: '2024-11-15'
        },
        {
            id: 2,
            orderNumber: 'ORD-2024-155',
            client: 'Dra. Maria Gonzalez',
            workType: 'Puente de 3 piezas',
            toothNumber: '21',
            status: 'pending',
            price: 32000,
            deliveryDate: '2024-11-16'
        },
        {
            id: 3,
            orderNumber: 'ORD-2024-154',
            client: 'Dr. Juan Perez',
            workType: 'Carilla estetica',
            toothNumber: '11',
            status: 'ready',
            price: 28000,
            deliveryDate: '2024-11-14'
        },
        {
            id: 4,
            orderNumber: 'ORD-2024-153',
            client: 'Dra. Ana Martinez',
            workType: 'Protesis removible',
            toothNumber: '36',
            status: 'delivered',
            price: 51000,
            deliveryDate: '2024-11-12'
        }
    ])

    // Format currency
    const formatCurrency = (amount) => {
        return new Intl.NumberFormat('es-AR', {
            style: 'currency',
            currency: 'ARS',
            minimumFractionDigits: 0
        }).format(amount)
    }

    // Quick Action Handler
    const handleQuickAction = (actionId) => {
        const routes = {
            newOrder: '/ordenes/nueva',
            newClient: '/clientes/nuevo',
            newShipment: '/transporte/nuevo',
            newInvoice: '/contabilidad/nueva-factura'
        }
        if (routes[actionId]) {
            router.push(routes[actionId])
        }
    }

    // Handlers
    const handleOrderClick = (order) => {
        router.push(`/ordenes/${order.id}`)
    }
</script>

<style scoped>
    .dashboard-wrapper {
        min-height: 100vh;
        background: #f8fafc;
    }

    .dashboard-page {
        padding: 1.5rem 2rem;
    }

    /* Page Header */
    .page-header {
        margin-bottom: 1.5rem;
    }

    .page-title {
        font-size: 1.75rem;
        font-weight: 700;
        color: #1f2937;
        margin: 0 0 0.25rem 0;
    }

    .page-subtitle {
        font-size: 0.9375rem;
        color: #6b7280;
        margin: 0;
    }

    /* Stats Grid */
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
        box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
    }

    .stat-header {
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
    }

    .stat-info {
        display: flex;
        flex-direction: column;
        gap: 0.25rem;
    }

    .stat-label {
        font-size: 0.8125rem;
        font-weight: 500;
        color: #6b7280;
    }

    .stat-value {
        font-size: 1.5rem;
        font-weight: 700;
        color: #1f2937;
    }

    .stat-detail {
        font-size: 0.75rem;
        color: #9ca3af;
    }

    .stat-trend {
        display: flex;
        align-items: center;
        gap: 0.25rem;
        font-size: 0.75rem;
    }

        .stat-trend.up {
            color: #10b981;
        }

    .trend-icon {
        width: 12px;
        height: 12px;
    }

    .stat-icon {
        width: 40px;
        height: 40px;
        border-radius: 10px;
        background: #f3f4f6;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #6b7280;
    }

        .stat-icon svg {
            width: 20px;
            height: 20px;
        }

        .stat-icon.green {
            background: #dcfce7;
            color: #16a34a;
        }

        .stat-icon.blue {
            background: #dbeafe;
            color: #2563eb;
        }

        .stat-icon.orange {
            background: #ffedd5;
            color: #ea580c;
        }

    /* Main Layout - 2 Columns */
    .main-layout {
        display: grid;
        grid-template-columns: 1fr 280px;
        gap: 1.5rem;
    }

    .main-content {
        display: flex;
        flex-direction: column;
        gap: 1.5rem;
        min-width: 0;
    }

    .sidebar-content {
        display: flex;
        flex-direction: column;
        gap: 1rem;
    }

    /* Charts Grid */
    .charts-grid {
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 1.5rem;
    }

    /* Responsive */
    @media (max-width: 1400px) {
        .main-layout {
            grid-template-columns: 1fr 260px;
        }
    }

    @media (max-width: 1200px) {
        .stats-grid {
            grid-template-columns: repeat(2, 1fr);
        }

        .main-layout {
            grid-template-columns: 1fr;
        }

        .sidebar-content {
            flex-direction: row;
        }

            .sidebar-content > * {
                flex: 1;
            }

        .charts-grid {
            grid-template-columns: 1fr;
        }
    }

    @media (max-width: 768px) {
        .dashboard-page {
            padding: 1rem;
        }

        .stats-grid {
            grid-template-columns: 1fr;
        }

        .sidebar-content {
            flex-direction: column;
        }
    }
</style>