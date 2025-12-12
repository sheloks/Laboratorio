// composables/useOrders.js
import { ref, computed } from 'vue'
import { orderService } from '@/services/orderService'
import { clientService } from '@/services/clientService'

export function useOrders() {
    // State
    const orders = ref([])
    const clients = ref([])
    const isLoading = ref(false)
    const error = ref(null)
    const searchTerm = ref('')
    const filterStatus = ref('all')
    const filterClientId = ref(null)

    // Cargar ordenes
    const loadOrders = async () => {
        isLoading.value = true
        error.value = null
        try {
            const response = await orderService.getOrders()
            orders.value = response
        } catch (err) {
            console.error('Error cargando ordenes:', err)
            error.value = err.response?.data?.message || 'Error al cargar las ordenes'
        } finally {
            isLoading.value = false
        }
    }

    // Cargar clientes
    const loadClients = async () => {
        try {
            const response = await clientService.getClients()
            clients.value = response
        } catch (err) {
            console.error('Error cargando clientes:', err)
        }
    }

    // Cargar todo
    const loadAll = async () => {
        await Promise.all([loadOrders(), loadClients()])
    }

    // Crear orden
    const createOrder = async (orderData) => {
        const response = await orderService.createOrder(orderData)
        await loadOrders()
        return response
    }

    // Actualizar orden
    const updateOrder = async (orderId, orderData) => {
        const response = await orderService.updateOrder(orderId, orderData)
        await loadOrders()
        return response
    }

    // Eliminar orden
    const deleteOrder = async (orderId) => {
        await orderService.deleteOrder(orderId)
        await loadOrders()
    }

    // Exportar PDF
    const exportPdf = async (order) => {
        await orderService.exportPdf(order.id, order.orderNumber)
    }

    // Obtener QR
    const getQrCode = async (orderId) => {
        return await orderService.getQrCode(orderId)
    }

    // Descargar QR
    const downloadQrCode = async (order) => {
        await orderService.downloadQrCode(order.id, order.orderNumber)
    }

    // Descargar etiqueta
    const downloadEtiqueta = async (order, datosEnvio) => {
        await orderService.downloadEtiqueta(order.id, order.orderNumber, datosEnvio)
    }

    // Ordenes filtradas
    const filteredOrders = computed(() => {
        if (!orders.value) return []
        return orders.value.filter(order => {
            // Filtro por cliente
            if (filterClientId.value) {
                const clientIdNum = parseInt(filterClientId.value)
                // Buscar por clienteId o por cliente.id
                const orderClientId = order.clienteId || order.cliente?.id
                if (orderClientId !== clientIdNum && orderClientId !== filterClientId.value) {
                    return false
                }
            }

            // Filtro por búsqueda
            const searchLower = searchTerm.value.toLowerCase()
            const matchesSearch = !searchTerm.value ||
                order.orderNumber?.toLowerCase().includes(searchLower) ||
                order.client?.toLowerCase().includes(searchLower) ||
                order.dentist?.toLowerCase().includes(searchLower)

            // Filtro por estado
            const matchesFilter = filterStatus.value === 'all' || order.status === filterStatus.value

            return matchesSearch && matchesFilter
        })
    })

    // Limpiar filtro de cliente
    const clearClientFilter = () => {
        filterClientId.value = null
    }

    // Obtener nombre del cliente filtrado
    const filteredClientName = computed(() => {
        if (!filterClientId.value) return null
        const client = clients.value.find(c =>
            c.id === filterClientId.value || c.id === parseInt(filterClientId.value)
        )
        return client?.name || null
    })

    // Estadisticas
    const stats = computed(() => {
        const ordersList = orders.value || []
        return {
            totalOrders: ordersList.length,
            pendingOrders: ordersList.filter(o => o.status === 'pending').length,
            inProgressOrders: ordersList.filter(o => o.status === 'in_progress').length,
            readyOrders: ordersList.filter(o => o.status === 'ready').length,
            urgentOrders: ordersList.filter(o => o.priority === 'urgent').length
        }
    })

    return {
        // State
        orders,
        clients,
        isLoading,
        error,
        searchTerm,
        filterStatus,
        filterClientId,

        // Computed
        filteredOrders,
        filteredClientName,
        stats,

        // Methods
        loadOrders,
        loadClients,
        loadAll,
        createOrder,
        updateOrder,
        deleteOrder,
        exportPdf,
        getQrCode,
        downloadQrCode,
        downloadEtiqueta,
        clearClientFilter
    }
}