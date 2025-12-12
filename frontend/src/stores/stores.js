// src/stores/dashboard.js
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import http from '@/api/http'

export const useDashboardStore = defineStore('dashboard', () => {
    // Estado
    const stats = ref({
        totalOrdenes: 0,
        ordenesEnProceso: 0,
        ordenesEntregadas: 0,
        clientesActivos: 0,
        ingresosMensual: 0
    })

    const recentOrders = ref([])
    const loading = ref(false)
    const error = ref(null)

    // Computadas
    const tasaEntrega = computed(() => {
        if (stats.value.totalOrdenes === 0) return 0
        return ((stats.value.ordenesEntregadas / stats.value.totalOrdenes) * 100).toFixed(1)
    })

    // Acciones
    const fetchDashboardData = async () => {
        loading.value = true
        error.value = null

        try {
            const [statsResponse, ordersResponse] = await Promise.all([
                http.get('/dashboard/stats'),
                http.get('/dashboard/recent-orders')
            ])

            stats.value = statsResponse.data
            recentOrders.value = ordersResponse.data

            console.log('✅ Dashboard data loaded')
        } catch (err) {
            error.value = err.response?.data?.message || 'Error al cargar dashboard'
            console.error('❌ Dashboard error:', err)
        } finally {
            loading.value = false
        }
    }

    const refreshStats = async () => {
        try {
            const response = await http.get('/dashboard/stats')
            stats.value = response.data
        } catch (err) {
            console.error('❌ Error refreshing stats:', err)
        }
    }

    return {
        // State
        stats,
        recentOrders,
        loading,
        error,

        // Computed
        tasaEntrega,

        // Actions
        fetchDashboardData,
        refreshStats
    }
})