// composables/clientes/useClientHelpers.js

export function useClientHelpers() {

    // Formatear moneda
    const formatCurrency = (value) => {
        if (!value) return '0'
        return new Intl.NumberFormat('es-AR').format(value)
    }

    // Formatear fecha
    const formatDate = (dateString) => {
        if (!dateString) return '-'
        return new Date(dateString).toLocaleDateString('es-AR')
    }

    // Obtener nombre del mes actual
    const getCurrentMonthName = () => {
        const months = [
            'Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio',
            'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'
        ]
        return months[new Date().getMonth()]
    }

    // Calcular dias trabajados en el mes actual
    const calculateWorkDays = (orders) => {
        if (!orders || orders.length === 0) return 0

        const now = new Date()
        const currentMonth = now.getMonth()
        const currentYear = now.getFullYear()

        // Obtener dias unicos con ordenes en el mes actual
        const uniqueDays = new Set()

        orders.forEach(order => {
            if (!order.createdAt) return

            const orderDate = new Date(order.createdAt)
            if (orderDate.getMonth() === currentMonth && orderDate.getFullYear() === currentYear) {
                // Usar solo la fecha (sin hora) como clave
                const dayKey = orderDate.toISOString().split('T')[0]
                uniqueDays.add(dayKey)
            }
        })

        return uniqueDays.size
    }

    return {
        formatCurrency,
        formatDate,
        getCurrentMonthName,
        calculateWorkDays
    }
}